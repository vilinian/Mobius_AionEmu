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
package com.aionemu.gameserver.model.gameobjects.siege;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;

/**
 * Represents a non-player character specifically designed for siege events.<br>
 * This class extends {@link Npc} to provide specialized behavior for siege mechanics.
 * @author ViAl
 */
public class SiegeNpc extends Npc
{
	private final int siegeId;
	private final SiegeRace siegeRace;
	
	/**
	 * Creates a new instance of a {@link SiegeNpc}.<br>
	 * This constructor initializes the NPC with specific siege data.<br>
	 * It sets the internal siege ID and race from the provided template.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@code NpcController} that manages this NPC's logic.
	 * @param spawnTemplate The {@link SiegeSpawnTemplate} containing siege-specific data.
	 * @param objectTemplate The base {@link NpcTemplate} for the NPC model.
	 */
	public SiegeNpc(int objId, NpcController controller, SiegeSpawnTemplate spawnTemplate, NpcTemplate objectTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate);
		siegeId = spawnTemplate.getSiegeId();
		siegeRace = spawnTemplate.getSiegeRace();
	}
	
	/**
	 * Retrieves the race associated with this {@link SiegeNpc}.<br>
	 * This identifies which faction the NPC belongs to during a siege.
	 * @return the {@code SiegeRace} of the current NPC.
	 */
	public SiegeRace getSiegeRace()
	{
		return siegeRace;
	}
	
	/**
	 * Retrieves the unique identifier for this siege.<br>
	 * This ID identifies which specific siege event the {@code SiegeNpc} belongs to.
	 * @return The {@code int} value of the siege ID.
	 */
	public int getSiegeId()
	{
		return siegeId;
	}
	
	/**
	 * Retrieves the spawn template for this siege NPC.<br>
	 * This method casts the base {@link Npc} spawn to a {@code SiegeSpawnTemplate}.
	 * @return The {@code SiegeSpawnTemplate} associated with this object.
	 */
	@Override
	public SiegeSpawnTemplate getSpawn()
	{
		return (SiegeSpawnTemplate) super.getSpawn();
	}
	
	/**
	 * Determines if the given {@link Creature} is an enemy.<br>
	 * It checks if the creature belongs to a different siege race.<br>
	 * If not, it falls back to the default behavior of the parent class.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Creature creature)
	{
		if ((creature instanceof SiegeNpc) && (getSiegeRace() != ((SiegeNpc) creature).getSiegeRace()))
		{
			return true;
		}
		
		return super.isEnemyFrom(creature);
	}
}
