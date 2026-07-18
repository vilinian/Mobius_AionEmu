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
package system.handlers.ai.worlds;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence behavior for {@link Creature} types identified as agrints.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific logic for these hostile NPCs.
 * @author xTz
 */
@AIName("agrint")
public class AgrintAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isSpawned = new AtomicBoolean(false);
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		int msg = 0;
		switch (getNpcId())
		{
			case 218862:
			case 218850:
				msg = 1401246;
				break;
			case 218863:
			case 218851:
				msg = 1401247;
				break;
			case 218864:
			case 218852:
				msg = 1401248;
				break;
			case 218865:
			case 218853:
				msg = 1401249;
				break;
		}
		
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, 2000);
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
		if (hpPercentage <= 50)
		{
			if (isSpawned.compareAndSet(false, true))
			{
				int npcId;
				switch (getNpcId())
				{
					case 218850:
					case 218851:
					case 218852:
					case 218853:
						npcId = getNpcId() + 320;
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						break;
					case 218862:
					case 218863:
					case 218864:
					case 218865:
						npcId = getNpcId() + 308;
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						break;
				}
			}
		}
	}
	
	/**
	 * Spawns a random NPC within a specific range.<br>
	 * This method calculates a random position based on the current location.<br>
	 * It uses {@code Rnd} to determine the spawn direction.
	 * @param npcId The unique identifier of the NPC to spawn.
	 * @param distance The maximum radius from the current position for spawning.
	 * @return The newly created {@link Npc} object.
	 */
	private Npc rndSpawnInRange(int npcId, float distance)
	{
		final float direction = Rnd.get(0, 199) / 100f;
		final float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		final float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		final WorldPosition p = getPosition();
		return (Npc) spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		isSpawned.set(false);
		super.handleBackHome();
	}
	
	/**
	 * Spawns multiple loot chests around a specific NPC.<br>
	 * This method calls {@code float)} six times.<br>
	 * It uses a random range of {@code 1} to {@code 6} for the distance.
	 * @param npcId The unique identifier of the NPC used as the spawn center.
	 */
	private void spawnChests(int npcId)
	{
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It handles specific loot spawning for certain NPC IDs.<br>
	 * It then calls {@code handleDied} to finish processing.
	 */
	@Override
	protected void handleDied()
	{
		switch (getNpcId())
		{
			case 218850:
				spawnChests(218874);
				break;
			case 218851:
				spawnChests(218876);
				break;
			case 218852:
				spawnChests(218878);
				break;
			case 218853:
				spawnChests(218880);
				break;
			case 218862:
				spawnChests(218882);
				break;
			case 218863:
				spawnChests(218884);
				break;
			case 218864:
				spawnChests(218886);
				break;
			case 218865:
				spawnChests(218888);
				break;
		}
		
		super.handleDied();
	}
	
	/**
	 * Adjusts the damage value for the owner.<br>
	 * This method overrides the default behavior of {@code modifyDamage}.<br>
	 * It currently returns a fixed value of {@code 1}.
	 * @param damage The original damage amount to be modified.
	 * @return The modified damage result.
	 */
	@Override
	public int modifyOwnerDamage(int damage)
	{
		return 1;
	}
	
	/**
	 * Adjusts the amount of damage dealt by an attack.<br>
	 * This method is used to calculate final damage values.
	 * @param damage The initial damage value to be modified.
	 * @return The multiplier applied to the damage.
	 */
	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}
}
