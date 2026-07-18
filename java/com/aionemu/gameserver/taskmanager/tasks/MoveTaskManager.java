/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.taskmanager.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;
import com.aionemu.gameserver.taskmanager.FIFOSimpleExecutableQueue;
import com.aionemu.gameserver.world.zone.ZoneUpdateService;

/**
 * This class handles the periodic processing of movement tasks for creatures in the game world.<br>
 * It manages how {@link Creature} entities update their positions over time.<br>
 * It extends {@link AbstractPeriodicTaskManager} to execute these updates at regular intervals.
 * @author ATracer
 * @reworked Rolandas, parallelized by using Fork/Join framework
 */
public class MoveTaskManager extends AbstractPeriodicTaskManager
{
	private static final Logger logger = LoggerFactory.getLogger(MoveTaskManager.class);
	
	private final Map<Integer, Creature> movingCreatures = new ConcurrentHashMap<>();
	private final TargetReachedManager targetReachedManager = new TargetReachedManager();
	private final TargetTooFarManager targetTooFarManager = new TargetTooFarManager();
	
	/**
	 * Private constructor for the {@link MoveTaskManager} class.<br>
	 * This prevents other classes from creating new instances directly.<br>
	 * Use {@code getInstance} to get the singleton instance.
	 */
	private MoveTaskManager()
	{
		super(100);
	}
	
	/**
	 * Adds a {@link Creature} to the list of moving entities.<br>
	 * This method registers the creature so it can be processed by the task manager.
	 * @param creature The {@code Creature} object to add.
	 */
	public void addCreature(Creature creature)
	{
		movingCreatures.put(creature.getObjectId(), creature);
	}
	
	/**
	 * Removes a specific {@link Creature} from the moving creatures list.<br>
	 * This method updates the internal tracking of active movements.
	 * @param creature The {@code Creature} object to be removed.
	 */
	public void removeCreature(Creature creature)
	{
		movingCreatures.remove(creature.getObjectId());
	}
	
	@Override
	public void run()
	{
		final List<Creature> arrivedCreatures = new ArrayList<>();
		final List<Creature> followingCreatures = new ArrayList<>();
		
		for (Map.Entry<Integer, Creature> e : movingCreatures.entrySet())
		{
			final Creature creature = e.getValue();
			creature.getMoveController().moveToDestination();
			if (creature.getAi2().poll(AIQuestion.DESTINATION_REACHED))
			{
				movingCreatures.remove(e.getKey());
				arrivedCreatures.add(e.getValue());
			}
			else
			{
				followingCreatures.add(e.getValue());
			}
		}
		
		targetReachedManager.executeAll(arrivedCreatures);
		targetTooFarManager.executeAll(followingCreatures);
	}
	
	/**
	 * Gets the single instance of the {@link MoveTaskManager}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the global move task manager.
	 * @return The active {@code MoveTaskManager} instance.
	 */
	public static MoveTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private final class TargetReachedManager extends FIFOSimpleExecutableQueue<Creature>
	{
		@Override
		protected void removeAndExecuteFirst()
		{
			final Creature creature = removeFirst();
			try
			{
				creature.getAi2().onGeneralEvent(AIEventType.MOVE_ARRIVED);
				ZoneUpdateService.getInstance().add(creature);
			}
			catch (RuntimeException e)
			{
				logger.warn("", e);
			}
		}
	}
	
	private final class TargetTooFarManager extends FIFOSimpleExecutableQueue<Creature>
	{
		@Override
		protected void removeAndExecuteFirst()
		{
			final Creature creature = removeFirst();
			try
			{
				creature.getAi2().onGeneralEvent(AIEventType.MOVE_VALIDATE);
			}
			catch (RuntimeException e)
			{
				logger.warn("", e);
			}
		}
	}
	
	private static final class SingletonHolder
	{
		private static final MoveTaskManager INSTANCE = new MoveTaskManager();
	}
}
