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
package system.handlers.ai.classNpc;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.world.knownlist.Visitor;

import system.handlers.ai.AggressiveFirstSkillAI2;

/**
 * Handles the artificial intelligence for the {@code drakanmedic} NPC.<br>
 * This class extends {@link AggressiveFirstSkillAI2} to provide specific behaviors for this unit.
 * @author Cheatkiller
 */
@AIName("drakanmedic")
public class DrakanMedicAI2 extends AggressiveFirstSkillAI2
{
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It calls the base behavior from {@code handleAttack}.<br>
	 * There is a 3% chance to trigger the {@code spawnServant} method.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		if (Rnd.get(1, 100) < 3)
		{
			spawnServant();
		}
	}
	
	/**
	 * Creates a servant NPC for the owner.<br>
	 * It checks if a {@code holyServant} already exists at the current position.<br>
	 * If no servant is found, it calls {@code rndSpawn} to create one.<br>
	 * It also sends a message via {@code getInstance}.
	 */
	private void spawnServant()
	{
		final int servant = getOwner().getObjectTemplate().getRating() == NpcRating.NORMAL ? 281621 : 281839;
		final Npc holyServant = getPosition().getWorldMapInstance().getNpc(servant);
		if (holyServant == null)
		{
			rndSpawn(servant);
			NpcShoutsService.getInstance().sendMsg(getOwner(), 341784, getObjectId(), 0, 0);
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
		super.handleBackHome();
		despawnServant();
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
		despawnServant();
	}
	
	/**
	 * Removes the servant NPC from the game world.<br>
	 * This method identifies the correct servant ID based on the owner's rating.<br>
	 * It then calls {@code onDelete()} on the servant if it exists.
	 */
	private void despawnServant()
	{
		getOwner().getKnownList().doOnAllNpcs(new Visitor<Npc>()
		{
			@Override
			public void visit(Npc object)
			{
				final int servant = getOwner().getObjectTemplate().getRating() == NpcRating.NORMAL ? 281621 : 281839;
				final Npc holyServant = getPosition().getWorldMapInstance().getNpc(servant);
				if (holyServant != null)
				{
					holyServant.getController().onDelete();
				}
			}
		});
	}
	
	/**
	 * Spawns a random servant for the owner.<br>
	 * It uses {@code rndSpawnInRange} to find a template.<br>
	 * Then it calls {@code int, Npc, int)} to create the entity.
	 * @param npcId The unique identifier for the NPC type to spawn.
	 */
	private void rndSpawn(int npcId)
	{
		final SpawnTemplate template = rndSpawnInRange(npcId);
		VisibleObjectSpawner.spawnEnemyServant(template, getOwner().getInstanceId(), getOwner(), getOwner().getLevel());
	}
	
	/**
	 * Calculates a random spawn point near the current position.<br>
	 * It uses {@code addNewSingleTimeSpawn} to create the new entity.
	 * @param npcId The unique identifier for the NPC to be spawned.
	 * @return A new {@code SpawnTemplate} object representing the calculated location.
	 */
	private SpawnTemplate rndSpawnInRange(int npcId)
	{
		final float direction = Rnd.get(0, 199) / 100f;
		final float x1 = (float) (Math.cos(Math.PI * direction) * 5);
		final float y1 = (float) (Math.sin(Math.PI * direction) * 5);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), getPosition().getHeading());
	}
}
