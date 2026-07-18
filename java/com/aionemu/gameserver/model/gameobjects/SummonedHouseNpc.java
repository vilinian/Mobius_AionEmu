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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents an NPC that functions as a summoned house.<br>
 * This class extends {@link SummonedObject} to handle house-specific logic for summoned entities.
 * @author Rolandas
 */
public class SummonedHouseNpc extends SummonedObject<House>
{
	String masterName;
	
	/**
	 * Creates a new instance of a summoned NPC associated with a house.<br>
	 * This constructor initializes the object with all required template and owner data.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@link NpcController} that handles this NPC's logic.
	 * @param spawnTemplate The {@link SpawnTemplate} defining where the NPC appears.
	 * @param npcTemplate The {@link NpcTemplate} containing the base stats and data.
	 * @param house The {@link House} object that owns or created this NPC.
	 * @param masterName The name of the owner associated with this summoned entity.
	 */
	public SummonedHouseNpc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate, House house, String masterName)
	{
		super(objId, controller, spawnTemplate, npcTemplate, npcTemplate.getLevel());
		setCreator(house);
		this.masterName = masterName;
	}
	
	/**
	 * Retrieves the unique identifier of the creator.<br>
	 * This value identifies which entity created this object.
	 * @return the {@code int} ID of the creator.
	 */
	@Override
	public int getCreatorId()
	{
		return getCreator().getAddress().getId();
	}
	
	/**
	 * Retrieves the name of the master associated with this homing object.<br>
	 * This method returns an empty string if no master is found.
	 * @return The name of the master as a {@code String}.
	 */
	@Override
	public String getMasterName()
	{
		return masterName;
	}
	
	/**
	 * Determines the type of the provided {@link Creature}.<br>
	 * This method checks if a creature is considered a friend.
	 * @param creature The {@code Creature} to evaluate.
	 * @return The integer ID for the {@code FRIEND} type.
	 */
	@Override
	public int getType(Creature creature)
	{
		return CreatureType.FRIEND.getId();
	}
	
	/**
	 * Retrieves the master {@link Creature} associated with this object.<br>
	 * This method returns {@code null} if the master is not currently online.
	 * @return the {@code Creature} object or {@code null}.
	 */
	@Override
	public Creature getMaster()
	{
		// Not interesting, player may be offline
		return null;
	}
}
