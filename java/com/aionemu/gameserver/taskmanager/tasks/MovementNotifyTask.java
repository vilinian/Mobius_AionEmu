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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import com.aionemu.gameserver.world.knownlist.VisitorWithOwner;

/**
 * This task handles the notification of movement for {@link Creature} objects.<br>
 * It ensures that nearby entities are updated about position changes in the game world.
 * @author ATracer
 */
public class MovementNotifyTask extends AbstractFIFOPeriodicTaskManager<Creature>
{
	private static Map<Integer, int[]> moveBroadcastCounts = new HashMap<>();
	static
	{
		final Iterator<WorldMapTemplate> iter = DataManager.WORLD_MAPS_DATA.iterator();
		while (iter.hasNext())
		{
			moveBroadcastCounts.put(iter.next().getMapId(), new int[2]);
		}
	}
	
	private static final class SingletonHolder
	{
		private static final MovementNotifyTask INSTANCE = new MovementNotifyTask();
	}
	
	/**
	 * Retrieves the single shared instance of {@link MovementNotifyTask}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code MovementNotifyTask} instance.
	 */
	public static MovementNotifyTask getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private final MoveNotifier MOVE_NOTIFIER = new MoveNotifier();
	
	/**
	 * Creates a new instance of the {@link MovementNotifyTask}.<br>
	 * This task handles movement notifications for creatures.<br>
	 * It initializes with a default period of {@code 500} milliseconds.
	 */
	public MovementNotifyTask()
	{
		super(500);
	}
	
	/**
	 * Executes the movement notification logic for a specific {@link Creature}.<br>
	 * This method checks if the creature is alive before processing.<br>
	 * It updates broadcast statistics based on the number of NPCs notified.
	 * @param creature The {@code Creature} object to process.
	 */
	@Override
	protected void callTask(Creature creature)
	{
		if (creature.getLifeStats().isAlreadyDead())
		{
			return;
		}
		
		// In Reshanta:
		// The max_move_broadcast_count is 200 and the min_move_broadcast_range is 75, as specified in the client WorldId.xml.
		final int limit = creature.getWorldId() == 400010000 ? 200 : Integer.MAX_VALUE;
		final int iterations = creature.getKnownList().doOnAllNpcsWithOwner(MOVE_NOTIFIER, limit);
		
		if (!(creature instanceof Player))
		{
			final int[] maxCounts = moveBroadcastCounts.get(creature.getWorldId());
			synchronized (maxCounts)
			{
				if (iterations > maxCounts[0])
				{
					maxCounts[0] = iterations;
					maxCounts[1] = creature.getObjectTemplate().getTemplateId();
				}
			}
		}
	}
	
	/**
	 * Retrieves the current movement broadcast statistics.<br>
	 * This method collects data for all world maps.<br>
	 * It returns a list of strings formatted for logging or display.
	 * @return An array of {@code String} objects containing the broadcast counts.
	 */
	public String[] dumpBroadcastStats()
	{
		final List<String> lines = new ArrayList<>();
		lines.add("------- Movement broadcast counts -------");
		for (Entry<Integer, int[]> entry : moveBroadcastCounts.entrySet())
		{
			lines.add("WorldId=" + entry.getKey() + ": " + entry.getValue()[0] + " (NpcId " + entry.getValue()[1] + ")");
		}
		
		lines.add("-----------------------------------------");
		return lines.toArray(new String[0]);
	}
	
	/**
	 * Returns the name of the method that was called by this task.<br>
	 * This is used for internal tracking and logging purposes.
	 * @return The {@code String} name of the executed method.
	 */
	@Override
	protected String getCalledMethodName()
	{
		return "notifyOnMove()";
	}
	
	private class MoveNotifier implements VisitorWithOwner<Npc, VisibleObject>
	{
		@Override
		public void visit(Npc object, VisibleObject owner)
		{
			if ((object.getAi2().getState() == AIState.DIED) || object.getLifeStats().isAlreadyDead())
			{
				if (object.getAi2().isLogging())
				{
					AI2Logger.moveinfo(object, "WARN: NPC died but still in knownlist");
				}
				return;
			}
			
			object.getAi2().onCreatureEvent(AIEventType.CREATURE_MOVED, (Creature) owner);
		}
	}
}
