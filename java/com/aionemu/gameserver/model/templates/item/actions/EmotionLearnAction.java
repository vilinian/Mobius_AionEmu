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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for a player learning a new emotion from an item.<br>
 * This action triggers when a {@link Player} uses an item that contains emotion data.<br>
 * It updates the player's available emotions and sends the appropriate feedback.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmotionLearnAction")
public class EmotionLearnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int emotionid;
	@XmlAttribute
	protected Integer minutes;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks if the emotion is valid and not already learned by the player.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if ((emotionid == 0) || (parentItem == null))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		
		if ((player.getEmotions() != null) && player.getEmotions().contains(emotionid))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_EMOTION);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Executes the action to learn an emotion.<br>
	 * This method handles the logic when a {@link Player} uses an item to gain a new emotion.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		final ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);
		
		player.getEmotions().add(emotionid, minutes == null ? 0 : (int) (System.currentTimeMillis() / 1000) + (minutes * 60), true);
		player.getInventory().delete(parentItem);
	}
}
