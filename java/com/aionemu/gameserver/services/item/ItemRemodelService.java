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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service handles the logic for changing the visual appearance of items.<br>
 * It manages how {@link Item} objects are remodeled and updates the player's appearance accordingly.
 * @author Sarynth modified by Wakizashi
 */
public class ItemRemodelService
{
	/**
	 * Remodels an item by applying a skin from another item.<br>
	 * This method checks for level requirements and sufficient Kinah costs.<br>
	 * It validates that both items are compatible before processing the change.
	 * @param player The {@link Player} performing the remodel action.
	 * @param keepItemObjId The unique ID of the item to be modified.
	 * @param extractItemObjId The unique ID of the skin item to extract from.
	 */
	public static void remodelItem(Player player, int keepItemObjId, int extractItemObjId)
	{
		final Storage inventory = player.getInventory();
		final Item keepItem = inventory.getItemByObjId(keepItemObjId);
		final Item extractItem = inventory.getItemByObjId(extractItemObjId);
		
		if (keepItem == null)
		{
			// NPE check.
			return;
		}
		
		final long remodelCost = PricesService.getPriceForService(10000, player.getRace());
		
		if (!keepItem.isSkinnedItem() && (extractItem == null))
		{
			return;
		}
		
		// Check Player Level
		if (player.getLevel() < 10)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_PC_LEVEL_LIMIT);
			return;
		}
		
		// Check Kinah
		if (player.getInventory().getKinah() < remodelCost)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_ENOUGH_GOLD(new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}
		
		// Remove Skin
		if (keepItem.isSkinnedItem() && (extractItem == null))
		{
			// Remove Money
			if (!player.getInventory().tryDecreaseKinah(remodelCost))
			{
				return;
			}
			
			// Revert item to ORIGINAL SKIN
			keepItem.setItemSkinTemplate(keepItem.getItemTemplate());
			
			// Remove dye color if item can not be dyed.
			if (!keepItem.getItemTemplate().isItemDyePermitted())
			{
				keepItem.setItemColor(0);
			}
			
			// Notify Player
			ItemPacketService.updateItemAfterInfoChange(player, keepItem);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_SUCCEED(new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}
		
		// Check that types match.
		if ((keepItem.getItemTemplate().getWeaponType() != extractItem.getItemSkinTemplate().getWeaponType()) || ((extractItem.getItemSkinTemplate().getArmorType() != ArmorType.CLOTHES) && (keepItem.getItemTemplate().getArmorType() != extractItem.getItemSkinTemplate().getArmorType())) || (keepItem.getItemTemplate().getArmorType() == ArmorType.CLOTHES) || (keepItem.getItemTemplate().getItemSlot() != extractItem.getItemSkinTemplate().getItemSlot()))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_COMPATIBLE(new DescriptionId(keepItem.getItemTemplate().getNameId()), new DescriptionId(extractItem.getItemSkinTemplate().getNameId())));
			return;
		}
		
		if (!keepItem.isRemodelable(player))
		{
			// "1300478" = The appearance of %0 cannot be modified.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300478, new DescriptionId(keepItem.getItemTemplate().getNameId())));
			return;
		}
		
		if (!extractItem.isRemodelable(player))
		{
			// "1300482" = You have failed to modify the appearance of the item as you could not remove the skin item %0.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300482, new DescriptionId(extractItem.getItemTemplate().getNameId())));
			return;
		}
		
		final ItemTemplate skin = extractItem.getItemSkinTemplate();
		final ItemActions actions = skin.getActions();
		if (extractItem.isSkinnedItem() && (actions != null) && (actions.getRemodelAction() != null) && (actions.getRemodelAction().getExtractType() == 2))
		{
			// "1300482" = You have failed to modify the appearance of the item as you could not remove the skin item %0.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300482, new DescriptionId(extractItem.getItemTemplate().getNameId())));
			return;
		}
		
		// -- SUCCESS --
		
		// Remove Money
		player.getInventory().decreaseKinah(remodelCost);
		
		// Remove Item
		player.getInventory().decreaseItemCount(extractItem, 1);
		
		// REMODEL ITEM
		keepItem.setItemSkinTemplate(skin);
		
		// Transfer Dye
		keepItem.setItemColor(extractItem.getItemColor());
		
		// Skin Skill
		keepItem.setItemSkinSkill(extractItem.getItemSkinSkill());
		
		// Notify Player
		ItemPacketService.updateItemAfterInfoChange(player, keepItem);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300483, new DescriptionId(keepItem.getItemTemplate().getNameId())));
	}
	
	/**
	 * This method allows a player to preview an item remodel via a command.<br>
	 * It checks if the player has compatible equipment to display the new skin.<br>
	 * If a match is found, it triggers a temporary visual update for the specified duration.
	 * @param player The {@link Player} who is initiating the preview.
	 * @param itemId The unique ID of the item template to preview.
	 * @param duration The amount of time in seconds to keep the preview active.
	 * @return {@code true} if a compatible item was found and updated, otherwise {@code false}.
	 */
	public static boolean commandPreviewRemodelItem(Player player, int itemId, int duration)
	{
		final ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (template == null)
		{
			return false;
		}
		
		final Equipment equip = player.getEquipment();
		if (equip == null)
		{
			return false;
		}
		
		for (Item item : equip.getEquippedItemsWithoutStigma())
		{
			if ((item.getEquipmentSlot() == ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) || (item.getEquipmentSlot() == ItemSlot.SUB_OFF_HAND.getSlotIdMask()))
			{
				continue;
			}
			
			if (item.getItemTemplate().isWeapon())
			{
				if ((item.getItemTemplate().getWeaponType() == template.getWeaponType()) && (item.getItemSkinTemplate().getTemplateId() != itemId))
				{
					systemPreviewRemodelItem(player, item, template, duration);
					return true;
				}
			}
			else if (item.getItemTemplate().isArmor())
			{
				if ((item.getItemTemplate().getItemSlot() == template.getItemSlot()) && (item.getItemSkinTemplate().getTemplateId() != itemId))
				{
					systemPreviewRemodelItem(player, item, template, duration);
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Shows a temporary preview of an item remodel for a specific player.<br>
	 * This method updates the appearance and sends a system message to the user.<br>
	 * The change is automatically reverted after the specified duration.
	 * @param player The {@link Player} who will view the preview.
	 * @param item The {@link Item} currently being modified.
	 * @param template The {@link ItemTemplate} used for the new look.
	 * @param duration The time in milliseconds to display the preview.
	 */
	public static void systemPreviewRemodelItem(Player player, Item item, ItemTemplate template, int duration)
	{
		final ItemTemplate oldTemplate = item.getItemSkinTemplate();
		item.setItemSkinTemplate(template);
		
		PacketSendUtility.sendPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence()));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300483, new DescriptionId(item.getItemTemplate().getNameId())));
		
		PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				item.setItemSkinTemplate(oldTemplate);
			}
		}, 50);
		
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				PacketSendUtility.sendPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence()));
				PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
			}
		}, duration * 1000);
	}
	
	/**
	 * Remodels an item for a specific player.<br>
	 * This method updates the skin of the {@code keepItem} using the provided {@code template}.<br>
	 * It also handles skill removal and re-addition as needed.
	 * @param player The {@link Player} who is performing the remodel action.
	 * @param keepItem The {@link Item} that will be modified by the remodel process.
	 * @param template The {@link ItemTemplate} containing the new skin data.
	 */
	public static void systemRemodelItem(Player player, Item keepItem, ItemTemplate template)
	{
		if (keepItem.getItemSkinSkill() > 0)
		{
			SkillLearnService.removeSkill(player, keepItem.getItemSkinSkill());
		}
		
		keepItem.setItemSkinTemplate(template);
		keepItem.setItemSkinSkill(template.getSkinSkill());
		ItemPacketService.updateItemAfterInfoChange(player, keepItem);
		PacketSendUtility.sendPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence()));
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300483, new DescriptionId(keepItem.getItemTemplate().getNameId())));
		if (keepItem.getItemSkinSkill() > 0)
		{
			player.getSkillList().addSkill(player, keepItem.getItemSkinSkill(), 1);
		}
	}
}
