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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HousingNpc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;

/**
 * Represents a non-player character (NPC) object within the game world.<br>
 * This class serves as a base model for various types of NPCs, including those related to {@link House} systems.
 * @author Rolandas
 */
public class NpcObject extends HouseObject<HousingNpc>
{
	Npc npc = null;
	
	/**
	 * Creates a new instance of an {@link NpcObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this object.
	 * @param objId The unique identifier for the specific object instance.
	 * @param templateId The ID of the template used to define the object's properties.
	 */
	public NpcObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this object.<br>
	 * This method is triggered by the use action.
	 * @param player The {@code Player} who used the object.
	 */
	@Override
	public void onUse(Player player)
	{
		// TODO: Talk ?
	}
	
	/**
	 * This method initializes the {@code Npc} object for this entity.<br>
	 * It calls the superclass {@code spawn} method first.<br>
	 * If no {@code Npc} exists, it creates a new one using the {@link SpawnEngine}.
	 */
	@Override
	public synchronized void spawn()
	{
		super.spawn();
		if (npc == null)
		{
			final HousingNpc template = getObjectTemplate();
			final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(getOwnerHouse().getWorldId(), template.getNpcId(), getX(), getY(), getZ(), getHeading());
			npc = (Npc) SpawnEngine.spawnObject(spawn, getOwnerHouse().getInstanceId());
		}
	}
	
	/**
	 * Handles the cleanup logic when this object is removed from the world.<br>
	 * It calls {@code onDespawn} on the superclass.<br>
	 * If an {@code npc} exists, it deletes its controller and sets the reference to {@code null}.
	 */
	@Override
	public synchronized void onDespawn()
	{
		super.onDespawn();
		if (npc != null)
		{
			npc.getController().onDelete();
			npc = null;
		}
	}
	
	/**
	 * Checks if the {@code NpcObject} is allowed to expire at this moment.<br>
	 * It returns {@code true} if there is no associated {@code Npc}.<br>
	 * It also returns {@code true} if the {@code Npc} has no current target.
	 * @return {@code true} if the object can expire, otherwise {@code false}.
	 */
	@Override
	public synchronized boolean canExpireNow()
	{
		if (npc == null)
		{
			return true;
		}
		
		return npc.getTarget() == null;
	}
	
	/**
	 * Retrieves the unique identifier for the {@link Npc} associated with this object.<br>
	 * It checks if the {@code npc} field is {@code null}.<br>
	 * If no NPC exists, it returns {@code 0}.
	 * @return The unique ID of the NPC or {@code 0} if none is assigned.
	 */
	public int getNpcObjectId()
	{
		return npc == null ? 0 : npc.getObjectId();
	}
}
