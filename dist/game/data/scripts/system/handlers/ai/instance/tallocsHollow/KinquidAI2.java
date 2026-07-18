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
package system.handlers.ai.instance.tallocsHollow;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence behavior for the {@code kinquid} NPC.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific combat logic for this entity.
 * @author xTz
 */
@AIName("kinquid")
public class KinquidAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> skillTask;
	
	/**
	 * This method manages the behavior when a {@code Creature} becomes aggressive.<br>
	 * It checks if the AI is currently able to think.<br>
	 * If so, it triggers the {@code onAggro} logic.
	 * @param creature The {@code Creature} that has triggered the aggro state.
	 */
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		super.handleCreatureAggro(creature);
		if (isHome.compareAndSet(true, false))
		{
			getPosition().getWorldMapInstance().getDoors().get(48).setOpen(false);
			check();
			cancelSkillTask();
			startSkillTask();
		}
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		cancelSkillTask();
		isHome.set(true);
		getPosition().getWorldMapInstance().getDoors().get(48).setOpen(true);
		super.handleBackHome();
		despawnDestroyer();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It cancels any active skill tasks.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		cancelSkillTask();
		super.handleDespawned();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		cancelSkillTask();
		super.handleDied();
	}
	
	/**
	 * Stops the current skill task if it is still running.<br>
	 * This method checks if {@code skillTask} is not {@code null}.<br>
	 * It then calls {@code cancel} with {@code true}.
	 */
	private void cancelSkillTask()
	{
		if ((skillTask != null) && !skillTask.isDone())
		{
			skillTask.cancel(true);
		}
	}
	
	/**
	 * Starts a recurring task to execute skills.<br>
	 * This method uses {@link ThreadPoolManager} to schedule skill actions.<br>
	 * It checks if the owner is alive before using skills from {@link SkillEngine}.
	 */
	private void startSkillTask()
	{
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (isAlreadyDead())
			{
				cancelSkillTask();
			}
			else
			{
				SkillEngine.getInstance().getSkill(getOwner(), 19233, 60, getOwner()).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(() ->
				{
					if (!isAlreadyDead() && getPosition().isSpawned())
					{
						SkillEngine.getInstance().getSkill(getOwner(), 19234, 60, getOwner()).useNoAnimationSkill();
					}
				}, 3500);
			}
		}, 35000, 35000);
	}
	
	/**
	 * Schedules a recurring task to run the {@code check} method.<br>
	 * The task is executed every 25000 milliseconds.<br>
	 * It uses the {@code ThreadPoolManager} to handle the execution.
	 */
	private void doSchedule()
	{
		ThreadPoolManager.getInstance().schedule(() -> check(), 25000);
	}
	
	/**
	 * Removes specific NPCs from the world map.<br>
	 * This method handles the destruction of armor and accessory objects.<br>
	 * It checks for {@code Npc} IDs {@code 282008} and {@code 282009}.<br>
	 * If found, it calls {@code getController} to trigger {@code onDelete()}.
	 */
	private void despawnDestroyer()
	{
		final Npc cleaveArmor = getPosition().getWorldMapInstance().getNpc(282008);
		if (cleaveArmor != null)
		{
			cleaveArmor.getController().onDelete();
		}
		
		final Npc accessoryDestruction = getPosition().getWorldMapInstance().getNpc(282009);
		if (accessoryDestruction != null)
		{
			accessoryDestruction.getController().onDelete();
		}
	}
	
	/**
	 * Performs periodic checks for the NPC state.<br>
	 * It removes destroyed objects and handles spawning logic.<br>
	 * This method is called to update the current behavior cycle.
	 */
	private void check()
	{
		despawnDestroyer();
		if (getPosition().isSpawned() && !isAlreadyDead() && !isHome.get())
		{
			int spawnId = 0;
			switch (Rnd.get(1, 2))
			{
				case 1:
					spawnId = 282008;
					break;
				case 2:
					spawnId = 282009;
					break;
			}
			
			switch (Rnd.get(1, 3))
			{
				case 1:
					spawn(spawnId, 266.70685f, 680.6733f, 1167.2369f, (byte) 0);
					break;
				case 2:
					spawn(spawnId, 292.02466f, 719.7132f, 1169.3982f, (byte) 0);
					break;
				case 3:
					spawn(spawnId, 263.4334f, 716.73004f, 1170.3693f, (byte) 0);
					break;
			}
		}
		
		doSchedule();
	}
}
