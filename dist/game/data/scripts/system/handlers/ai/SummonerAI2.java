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
package system.handlers.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ai.Percentage;
import com.aionemu.gameserver.model.ai.SummonGroup;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Handles the artificial intelligence logic for {@link Creature} types identified as summoners.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for summoning and managing minions.
 * @author xTz
 */
@AIName("summoner")
public class SummonerAI2 extends AggressiveNpcAI2
{
	private final List<Integer> spawnedNpc = new ArrayList<>();
	private List<Percentage> percentage = Collections.emptyList();
	private int spawnedPercent = 0;
	
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
	 * Handles the logic when an NPC is despawned.<br>
	 * It clears the list of spawned NPCs and their percentages.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
		
		synchronized (spawnedNpc)
		{
			removeHelpersSpawn();
			spawnedNpc.clear();
		}
		
		percentage.clear();
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		
		synchronized (spawnedNpc)
		{
			removeHelpersSpawn();
			spawnedNpc.clear();
		}
		
		spawnedPercent = 0;
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		percentage = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getSummons().getPercentage();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		removeHelpersSpawn();
		spawnedNpc.clear();
		percentage.clear();
	}
	
	/**
	 * This method removes helper NPCs from the game world.<br>
	 * It iterates through all IDs in the {@code spawnedNpc} list.<br>
	 * If a {@link VisibleObject} is found and is currently active, its controller is deleted.
	 */
	private void removeHelpersSpawn()
	{
		for (Integer object : spawnedNpc)
		{
			final VisibleObject npc = World.getInstance().findVisibleObject(object);
			if ((npc != null) && npc.isSpawned())
			{
				npc.getController().onDelete();
			}
		}
	}
	
	/**
	 * Adds a new helper object to the list of spawned NPCs.<br>
	 * This method ensures thread safety by synchronizing on the {@code spawnedNpc} list.
	 * @param objId The unique identifier of the object to add.
	 */
	protected void addHelpersSpawn(int objId)
	{
		synchronized (spawnedNpc)
		{
			spawnedNpc.add(objId);
		}
	}
	
	/**
	 * Checks the current health percentage to trigger specific AI actions.<br>
	 * It evaluates whether a skill should be used or summons should be spawned.<br>
	 * This method updates the {@code spawnedPercent} tracker based on the results.
	 * @param hpPercentage The current health of the creature as an integer.
	 */
	private void checkPercentage(int hpPercentage)
	{
		for (Percentage percent : percentage)
		{
			if ((spawnedPercent != 0) && (spawnedPercent <= percent.getPercent()))
			{
				continue;
			}
			
			if (hpPercentage <= percent.getPercent())
			{
				final int skill = percent.getSkillId();
				if (skill != 0)
				{
					AI2Actions.useSkill(this, skill);
				}
				
				if (percent.isIndividual())
				{
					handleIndividualSpawnedSummons(percent);
				}
				else if (percent.getSummons() != null)
				{
					handleBeforeSpawn(percent);
					for (SummonGroup summonGroup : percent.getSummons())
					{
						final SummonGroup sg = summonGroup;
						ThreadPoolManager.getInstance().schedule(() -> spawnHelpers(sg), summonGroup.getSchedule());
						
					}
				}
				
				spawnedPercent = percent.getPercent();
			}
		}
	}
	
	/**
	 * Spawns helper creatures based on the provided {@code SummonGroup}.<br>
	 * This method checks if spawning is allowed before creating new objects.<br>
	 * It determines the number of summons and their spawn locations.<br>
	 * Finally, it calls {@code handleSpawnFinished} to complete the process.
	 * @param summonGroup The group containing data for the summons to be created.
	 */
	protected void spawnHelpers(SummonGroup summonGroup)
	{
		if (!isAlreadyDead() && checkBeforeSpawn())
		{
			int count = 0;
			if (summonGroup.getCount() != 0)
			{
				count = summonGroup.getCount();
			}
			else
			{
				count = Rnd.get(summonGroup.getMinCount(), summonGroup.getMaxCount());
			}
			
			for (int i = 0; i < count; i++)
			{
				SpawnTemplate summon = null;
				if (summonGroup.getDistance() != 0)
				{
					summon = rndSpawnInRange(summonGroup.getNpcId(), summonGroup.getDistance());
				}
				else
				{
					summon = SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), summonGroup.getNpcId(), summonGroup.getX(), summonGroup.getY(), summonGroup.getZ(), summonGroup.getH());
				}
				
				final VisibleObject npc = SpawnEngine.spawnObject(summon, getPosition().getInstanceId());
				addHelpersSpawn(npc.getObjectId());
			}
			
			handleSpawnFinished(summonGroup);
		}
	}
	
	/**
	 * Calculates a random spawn location for an NPC.<br>
	 * It picks a random direction and applies the specified distance.<br>
	 * The method uses {@code addNewSingleTimeSpawn} to create the spawn.
	 * @param npcId The unique identifier of the NPC to spawn.
	 * @param distance The radius from the current position to search for a location.
	 * @return The resulting {@code SpawnTemplate} object.
	 */
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance)
	{
		final float direction = Rnd.get(0, 199) / 100f;
		final float x = (float) (Math.cos(Math.PI * direction) * distance);
		final float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	/**
	 * Checks if the conditions are met before spawning a new NPC.<br>
	 * This method is used to validate spawn requirements.
	 * @return {@code true} if the spawn can proceed, {@code false} otherwise.
	 */
	protected boolean checkBeforeSpawn()
	{
		return true;
	}
	
	/**
	 * Processes the logic required before a new NPC is spawned.<br>
	 * This method uses the provided {@code percent} to determine actions.<br>
	 * It is called by the internal spawn sequence.
	 * @param percent The current health percentage used for spawning logic.
	 */
	protected void handleBeforeSpawn(Percentage percent)
	{
	}
	
	/**
	 * This method is called when a {@link SummonGroup} has finished spawning.<br>
	 * It handles the logic required after all summons are created.
	 * @param summonGroup The group of summons that just finished spawning.
	 */
	protected void handleSpawnFinished(SummonGroup summonGroup)
	{
	}
	
	/**
	 * This method handles the logic for individual summons after they are spawned.<br>
	 * It uses the {@code percent} value to determine specific behaviors.
	 * @param percent The health percentage of the summon.
	 */
	protected void handleIndividualSpawnedSummons(Percentage percent)
	{
	}
}
