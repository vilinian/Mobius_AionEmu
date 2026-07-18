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
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.RecipeService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for players learning a new crafting recipe.<br>
 * This action checks if the {@link Player} meets the requirements to learn the skill from an {@link Item}.<br>
 * It interacts with the {@link RecipeService} to update the player's known recipes.
 * @author ATracer, MrPoke, KID
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CraftLearnAction")
public class CraftLearnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int recipeid;
	
	/**
	 * Executes the action to learn a recipe.<br>
	 * This method handles the logic when a {@link Player} uses an item to unlock a new craft.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		player.getController().cancelUseItem();
		if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
		{
			if (RecipeService.addRecipe(player, recipeid, false))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId()));
			}
		}
	}
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks if the player is allowed to learn the recipe.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		return RecipeService.validateNewRecipe(player, recipeid) != null;
	}
	
	/**
	 * Retrieves the unique identifier for the crafting recipe.<br>
	 * This ID is used to identify which specific recipe this action belongs to.
	 * @return The {@code int} value of the {@code recipeid}.
	 */
	public int getRecipeId()
	{
		return recipeid;
	}
}
