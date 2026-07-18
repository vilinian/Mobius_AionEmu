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
import com.aionemu.gameserver.model.templates.housing.HousingStorage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_OBJECT_USE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents an object within a storage system, such as a warehouse or house.<br>
 * It handles the interaction between players and {@link HousingStorage} containers.
 * @author Rolandas
 */
public final class StorageObject extends HouseObject<HousingStorage>
{
	/**
	 * Creates a new instance of a {@link StorageObject}.<br>
	 * This constructor initializes the object with its owner and unique identifiers.
	 * @param owner The {@link House} that owns this storage object.
	 * @param objId The unique identifier for the specific object instance.
	 * @param templateId The ID of the template used to define the object's properties.
	 */
	public StorageObject(House owner, int objId, int templateId)
	{
		super(owner, objId, templateId);
	}
	
	/**
	 * Handles the logic when a {@link Player} interacts with this storage object.<br>
	 * This method checks if the user is the owner before sending updates.<br>
	 * It also sends warehouse information to the player.
	 * @param player The {@code Player} who used the object.
	 */
	@Override
	public void onUse(Player player)
	{
		if (player.getObjectId() != getOwnerHouse().getOwnerId())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_IS_ONLY_FOR_OWNER_VALID);
			return;
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_USE(getObjectTemplate().getNameId()));
		PacketSendUtility.sendPacket(player, new SM_OBJECT_USE_UPDATE(player.getObjectId(), 0, 0, this));
		
		for (HouseObject<?> ho : getOwnerHouse().getRegistry().getSpawnedObjects())
		{
			if (ho instanceof StorageObject)
			{
				final int warehouseId = ((HousingStorage) ho.getObjectTemplate()).getWarehouseId() + 59;
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(warehouseId).getItemsWithKinah(), warehouseId, 0, true, player));
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, warehouseId, 0, false, player));
			}
		}
	}
	
	/**
	 * Checks if the object is allowed to expire at this moment.<br>
	 * This method returns {@code false} if a player is currently using the mailbox.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		// FIXME: if player is using mailbox, should not expire immediately
		return false;
	}
}
