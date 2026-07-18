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
package system.handlers.ai.worlds.heiron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

import system.handlers.ai.AggressiveFirstSkillAI2;

/**
 * Handles the artificial intelligence behavior for the {@code bollvig} NPC in Heiron.<br>
 * This class extends {@link AggressiveFirstSkillAI2} to manage combat actions and movement.
 * @author Ritsu
 */
@AIName("bollvig") // 212314
public class BollvigAI2 extends AggressiveFirstSkillAI2
{
	protected List<Integer> percents = new ArrayList<>();
	private Future<?> firstTask;
	private Future<?> secondTask;
	private Future<?> thirdTask;
	private Future<?> lastTask;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleSpawned();
		final Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	/**
	 * This method is called when the NPC has respawned.<br>
	 * It updates the internal percentage list and calls {@code handleRespawned} from the parent class.<br>
	 * It also removes a specific NPC controller if it exists in the current world instance.
	 */
	@Override
	protected void handleRespawned()
	{
		addPercent();
		super.handleRespawned();
		final Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	/**
	 * Checks the current health percentage against a list of thresholds.<br>
	 * It triggers specific actions like spawning servants when certain levels are reached.<br>
	 * This method is synchronized to ensure thread safety during state changes.
	 * @param hpPercentage The current health percentage of the creature.
	 */
	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch (percent)
				{
					case 75:
					case 50:
						cancelTask();
						useFirstSkillTree();
						break;
					case 25:
						cancelTask();
						firstSkill();
						break;
				}
				
				percents.remove(percent);
				break;
			}
		}
	}
	
	/**
	 * Executes the initial sequence of skills and actions.<br>
	 * This method triggers {@code useSkill} for skill ID {@code 17861}.<br>
	 * It also calls {@code rndSpawnInRange} multiple times.<br>
	 * Finally, it invokes {@code firstSkill}.
	 */
	private void useFirstSkillTree()
	{
		useSkill(17861); // Sleep of Death
		rndSpawnInRange(280802);
		rndSpawnInRange(280802);
		rndSpawnInRange(280803);
		rndSpawnInRange(280803);
		firstSkill();
	}
	
	/**
	 * Executes the initial skill logic based on current health.<br>
	 * It checks if {@code hpPercent} is between {@code 25} and {@code 50}.<br>
	 * If true, it schedules a task to use skill {@code 18034} and spawn an NPC.<br>
	 * Otherwise, it uses skill {@code 18037} if health is low.
	 */
	private void firstSkill()
	{
		final int hpPercent = getLifeStats().getHpPercentage();
		if ((50 >= hpPercent) && (hpPercent > 25))
		{
			firstTask = ThreadPoolManager.getInstance().schedule(() ->
			{
				useSkill(18034); // Nerve Absorption
				rndSpawnInRange(280804);
			}, 10000);
		}
		else if (hpPercent <= 25)
		{
			useSkill(18037); // Blood Cell Destruction
		}
		
		secondTask = ThreadPoolManager.getInstance().schedule(() -> skillThree(), 31000);
	}
	
	/**
	 * Executes the third skill sequence for the NPC.<br>
	 * This method is triggered as part of a scheduled task.<br>
	 * It handles specific skill logic based on current health percentages.
	 */
	private void skillThree()
	{
		useSkill(17899); // Charming Attraction
		thirdTask = ThreadPoolManager.getInstance().schedule(() ->
		{
			final int hpPercent = getLifeStats().getHpPercentage();
			if ((75 >= hpPercent) && (hpPercent > 50))
			{
				useSkill(18025); // Curse of Soul
				firstSkill();
			}
			else if (50 >= hpPercent)
			{
				useSkill(18025); // Curse of Soul
				firstSkill();
			}
			else if (25 >= hpPercent)
			{
				useSkill(18027); // Mortal Cutting
				lastTask = ThreadPoolManager.getInstance().schedule(() -> skillThree(), 11000);
			}
		}, 5000);
	}
	
	/**
	 * Stops the currently running AI task.<br>
	 * It checks each {@code Future} object in order to see if it is still active.<br>
	 * If a task is found, it calls {@code cancel} with {@code true}.
	 */
	private void cancelTask()
	{
		if ((firstTask != null) && !firstTask.isDone())
		{
			firstTask.cancel(true);
		}
		else if ((secondTask != null) && !secondTask.isDone())
		{
			secondTask.cancel(true);
		}
		else if ((thirdTask != null) && !thirdTask.isDone())
		{
			thirdTask.cancel(true);
		}
		else if ((lastTask != null) && !lastTask.isDone())
		{
			lastTask.cancel(true);
		}
	}
	
	/**
	 * Spawns a random NPC within a specific range.<br>
	 * This method calculates a random position around a fixed point.<br>
	 * It uses {@code int)} to determine the direction.
	 * @param npcId The unique identifier of the NPC to spawn.
	 */
	private void rndSpawnInRange(int npcId)
	{
		final float direction = Rnd.get(0, 199) / 100f;
		final float x = (float) (Math.cos(Math.PI * direction) * 10);
		final float y = (float) (Math.sin(Math.PI * direction) * 10);
		spawn(npcId, 1001 + x, 2828 + y, 235.66f, (byte) 0);
	}
	
	/**
	 * Executes a specific skill for the owner.<br>
	 * This method interacts with the {@link SkillEngine}.
	 * @param skillId The unique identifier of the skill to use.
	 */
	private void useSkill(int skillId)
	{
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}
	
	/**
	 * Resets the {@code percents} list.<br>
	 * It clears all existing values.<br>
	 * It adds a default value of {@code 75}, {@code 50}, and {@code 25} to the list.
	 */
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			75,
			50,
			25
		});
	}
	
	/**
	 * Handles the logic when an NPC returns home.<br>
	 * It updates the internal percentage and cancels current tasks.<br>
	 * It then calls {@code handleBackHome} from the parent class.
	 */
	@Override
	protected void handleBackHome()
	{
		addPercent();
		cancelTask();
		super.handleBackHome();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It clears the {@code percents} list and cancels active tasks.<br>
	 * It removes specific summons and calls the superclass method.<br>
	 * Finally, it may spawn a new entity if the NPC check passes.
	 */
	@Override
	protected void handleDespawned()
	{
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDespawned();
		if (checkNpc())
		{
			spawn(204655, 1001f, 2828f, 235.66f, (byte) 0);
		}
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDied();
		if (checkNpc())
		{
			spawn(204655, 1001f, 2828f, 235.66f, (byte) 0);
		}
	}
	
	/**
	 * Removes all active summons for a specific NPC.<br>
	 * This method finds NPCs by their {@code npcId}.<br>
	 * It calls the {@code onDelete()} method on each found NPC controller.
	 * @param npcId The unique identifier of the NPC to remove.
	 */
	private void deleteSummons(int npcId)
	{
		if (getPosition().getWorldMapInstance().getNpcs(npcId) != null)
		{
			final List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
			for (Npc npc : npcs)
			{
				npc.getController().onDelete();
			}
		}
	}
	
	/**
	 * Checks if specific NPCs are missing or dead on the current map.<br>
	 * This method verifies the status of NPC IDs {@code 204655} and {@code 212314}.<br>
	 * It returns {@code true} if the conditions for spawning are met.
	 * @return {@code true} if the NPCs are missing or dead, otherwise {@code false}.
	 */
	private boolean checkNpc()
	{
		final WorldMapInstance map = getPosition().getWorldMapInstance();
		if ((map.getNpc(204655) == null) && ((map.getNpc(212314) == null) || map.getNpc(212314).getLifeStats().isAlreadyDead()))
		{
			return true;
		}
		
		return false;
	}
}
