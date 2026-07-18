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
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for dyeing a {@link HouseObject} using an item.<br>
 * This action updates the appearance of the object and sends the corresponding packets to the player.
 * @author IceReaper
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DyeAction")
public class DyeAction extends AbstractItemAction implements IHouseObjectDyeAction
{
	@XmlAttribute(name = "color")
	protected String color;
	@XmlAttribute
	private Integer minutes;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It ensures that the {@code targetItem} is not {@code null}.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if (targetItem == null)
		{
			// no item selected.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Converts the hex color string into a {@code BGRA} integer format.<br>
	 * It checks if the {@code color} field is set to {@code "no"}.<br>
	 * If it is not {@code "no"}, it parses the hex value and rearranges the bytes.
	 * @return The resulting {@code int} representing the color in {@code BGRA} format.
	 */
	private int getColorBGRA()
	{
		if (color.equals("no"))
		{
			return 0;
		}
		
		final int rgb = Integer.parseInt(color, 16);
		return 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
	}
	
	/**
	 * Executes the action for dyeing an item.<br>
	 * This method handles the logic when a {@link Player} uses a dye to change the color of a {@code Item}.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
		{
			return;
		}
		
		if (targetItem.getItemSkinTemplate().isItemDyePermitted())
		{
			if (getColorBGRA() == 0)
			{
				targetItem.setItemColor(0);
				targetItem.setColorExpireTime(0);
			}
			else
			{
				targetItem.setItemColor(parentItem.getItemTemplate().getTemplateId());
				if (minutes != null)
				{
					targetItem.setColorExpireTime((int) ((System.currentTimeMillis() / 1000) + (minutes * 60)));
				}
			}
			
			// item is equipped, so need broadcast packet
			if (player.getEquipment().getEquippedItemByObjId(targetItem.getObjectId()) != null)
			{
				PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedForApparence()), true);
				player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
			} // item is not equipped
			else
			{
				player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
			}
			
			ItemPacketService.updateItemAfterInfoChange(player, targetItem);
		}
	}
	
	/**
	 * Retrieves the color value of the action.<br>
	 * This method calls {@code getColorBGRA} to get the result.
	 * @return The integer representation of the color.
	 */
	public int getColor()
	{
		return getColorBGRA();
	}
	
	/**
	 * Checks if a {@link Player} is allowed to perform the dye action.<br>
	 * It validates the target house object and its paint properties.
	 * @param player The player attempting the action.
	 * @param parentItem The item used to perform the action.
	 * @param targetHouseObject The house object being targeted for dyeing.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, HouseObject<?> targetHouseObject)
	{
		if (targetHouseObject == null)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		if (color.equals("no") && (targetHouseObject.getColor() == null))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_ERROR_CANNOTREMOVE);
			return false;
		}
		
		final boolean canPaint = targetHouseObject.getObjectTemplate().getCanDye();
		if (!canPaint)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_ERROR_CANNOTPAINT);
		}
		
		return canPaint;
	}
	
	/**
	 * Executes the action to dye a house object using an item.<br>
	 * This method removes one {@code parentItem} from the player's inventory.<br>
	 * It updates the color of the {@code targetHouseObject} based on the defined color value.<br>
	 * Finally, it sends the necessary packets and spawns the updated object.
	 * @param player The {@link Player} performing the action.
	 * @param parentItem The {@link Item} used as the dye source.
	 * @param targetHouseObject The {@link HouseObject} being modified.
	 */
	@Override
	public void act(Player player, Item parentItem, HouseObject<?> targetHouseObject)
	{
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
		{
			return;
		}
		
		if (color.equals("no"))
		{
			targetHouseObject.setColor(null);
		}
		else
		{
			targetHouseObject.setColor(Integer.parseInt(color, 16));
		}
		
		final float x = targetHouseObject.getX();
		final float y = targetHouseObject.getY();
		final float z = targetHouseObject.getZ();
		final int rotation = targetHouseObject.getRotation();
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(7, 0, targetHouseObject.getObjectId()));
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(5, targetHouseObject.getObjectId(), x, y, z, rotation));
		targetHouseObject.spawn();
		final int objectName = targetHouseObject.getObjectTemplate().getNameId();
		if (color.equals("no"))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_REMOVE_SUCCEED(objectName));
		}
		else
		{
			final int paintName = parentItem.getItemTemplate().getNameId();
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_SUCCEED(objectName, paintName));
		}
	}
}
