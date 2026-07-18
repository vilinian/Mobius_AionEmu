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
package com.aionemu.gameserver.services.item;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages restrictions on items based on player permissions and categories.<br>
 * It ensures that only authorized users can access or use specific {@link Item} types.<br>
 * It handles checks for {@link LegionPermissionsMask} and other security constraints.
 * @author ATracer
 */
public class ItemRestrictionService
{
	/**
	 * Checks if a {@link Player} is prohibited from removing an {@link Item} from a specific storage.<br>
	 * This method validates permissions based on the storage type and player status.
	 * @param player The {@link Player} attempting to access the item.
	 * @param item The {@link Item} that is being moved.
	 * @param storage The unique identifier for the storage location.
	 * @return {@code true} if the action is restricted, {@code false} otherwise.
	 */
	public static boolean isItemRestrictedFrom(Player player, Item item, byte storage)
	{
		final StorageType type = StorageType.getStorageTypeById(storage);
		switch (type)
		{
			case LEGION_WAREHOUSE:
				if (!LegionService.getInstance().getLegionMember(player.getObjectId()).hasRights(LegionPermissionsMask.WH_WITHDRAWAL) || !LegionConfig.LEGION_WAREHOUSE || !player.isLegionMember())
				{
					// You do not have the authority to use the Legion warehouse.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300322));
					return true;
				}
				break;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if a {@link Player} is restricted from storing an {@link Item} in a specific storage.<br>
	 * This method validates permissions and item compatibility for various warehouse types.<br>
	 * It sends a system message to the player if the action is forbidden.
	 * @param player The {@link Player} attempting to perform the action.
	 * @param item The {@link Item} being moved.
	 * @param storage The unique identifier of the target storage.
	 * @return {@code true} if the action is restricted, {@code false} otherwise.
	 */
	public static boolean isItemRestrictedTo(Player player, Item item, byte storage)
	{
		final StorageType type = StorageType.getStorageTypeById(storage);
		switch (type)
		{
			case REGULAR_WAREHOUSE:
				if (!item.isStorableinWarehouse(player))
				{
					// You cannot store this in the warehouse.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300418));
					return true;
				}
				break;
			case ACCOUNT_WAREHOUSE:
				if (!item.isStorableinAccWarehouse(player))
				{
					// You cannot store this item in the account warehouse.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400356));
					return true;
				}
				break;
			case LEGION_WAREHOUSE:
				if (!item.isStorableinLegWarehouse(player) || !LegionConfig.LEGION_WAREHOUSE)
				{
					// You cannot store this item in the Legion warehouse.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400355));
					return true;
				}
				else if (!player.isLegionMember() || !LegionService.getInstance().getLegionMember(player.getObjectId()).hasRights(LegionPermissionsMask.WH_DEPOSIT))
				{
					// You do not have the authority to use the Legion warehouse.
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300322));
					return true;
				}
				break;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if a {@link Player} is allowed to remove a specific {@link Item}.<br>
	 * This method validates the permissions for moving an item.
	 * @param player The {@code Player} attempting the action.
	 * @param item The {@code Item} that needs to be removed.
	 * @return {@code true} if the removal is allowed, otherwise {@code false}.
	 */
	public static boolean canRemoveItem(Player player, Item item)
	{
		final ItemTemplate it = item.getItemTemplate();
		if (it.getCategory() == ItemCategory.QUEST)
		{
			// TODO: This is not removable because the quest has started and cannot be abandoned while waiting for a quest data reparse.
			return true;
		}
		
		return true;
	}
}
