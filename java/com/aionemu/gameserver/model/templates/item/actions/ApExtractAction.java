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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.Acquisition;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * Handles the logic for extracting Abyss Points from an item.<br>
 * This action allows a {@link Player} to process an {@link Item} to gain points.<br>
 * It interacts with the {@link AbyssPointsService} to update the player's balance.
 * @author Rolandas, Luzien
 */
public class ApExtractAction extends AbstractItemAction
{
	@XmlAttribute
	protected ApExtractTarget target;
	@XmlAttribute
	protected float rate;
	
	/**
	 * Checks if a {@link Player} can perform this action.<br>
	 * This method validates the requirements for interacting with items.<br>
	 * It checks item levels, qualities, and categories to determine eligibility.
	 * @param player The {@link Player} attempting the action.
	 * @param parentItem The item that triggers the action.
	 * @param targetItem The item being acted upon.
	 * @return {@code true} if the action is allowed, otherwise {@code false}.
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem)
	{
		if ((targetItem == null) || !targetItem.canApExtract() || (parentItem.getItemTemplate().getLevel() < targetItem.getItemTemplate().getLevel()) || (parentItem.getItemTemplate().getItemQuality() != targetItem.getItemTemplate().getItemQuality()))
		{
			return false;
		}
		
		// TODO: ApExtractTarget.OTHER, ApExtractTarget.ALL. Find out what should go there
		ApExtractTarget type = null;
		switch (targetItem.getItemTemplate().getCategory())
		{
			case SWORD:
			case DAGGER:
			case MACE:
			case ORB:
			case SPELLBOOK:
			case BOW:
			case GREATSWORD:
			case POLEARM:
			case STAFF:
			case SHIELD:
			case HARP:
			case GUN:
			case CANNON:
				type = ApExtractTarget.WEAPON;
				break;
			case JACKET:
			case PANTS:
			case SHOES:
			case GLOVES:
			case SHOULDERS:
				type = ApExtractTarget.ARMOR;
				break;
			case NECKLACE:
			case EARRINGS:
			case RINGS:
			case HELMET:
			case BELT:
				type = ApExtractTarget.ACCESSORY;
				break;
			case NONE:
				if (targetItem.getItemTemplate().getArmorType() == ArmorType.WING)
				{
					type = ApExtractTarget.WING;
					break;
				}
				
				return false;
			default:
				return false;
		}
		
		return ((target == ApExtractTarget.EQUIPMENT) || (target == type));
	}
	
	/**
	 * Executes the action to extract Abyss Points from an item.<br>
	 * This method handles the logic when a {@link Player} uses a {@code parentItem} on a {@code targetItem}.<br>
	 * It removes the {@code targetItem} and grants the player Abyss Points based on the extraction rate.
	 * @param player The {@code Player} who is performing the action.
	 * @param parentItem The {@code Item} that triggers this action.
	 * @param targetItem The {@code Item} being acted upon.
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		final Acquisition acquisition = targetItem.getItemTemplate().getAcquisition();
		if ((acquisition == null) || (acquisition.getRequiredAp() == 0))
		{
			return;
		}
		
		final int ap = (int) (acquisition.getRequiredAp() * rate);
		final Storage inventory = player.getInventory();
		
		if (inventory.delete(targetItem) != null)
		{
			if (inventory.decreaseByObjectId(parentItem.getObjectId(), 1))
			{
				AbyssPointsService.addAp(player, ap);
			}
		}
		else
		{
			AuditLogger.info(player, "Possible extract item hack, do not remove item.");
		}
	}
	
	/**
	 * Retrieves the current extraction target.<br>
	 * This method returns the {@code ApExtractTarget} associated with this action.
	 * @return The {@link ApExtractTarget} object.
	 */
	public ApExtractTarget getTarget()
	{
		return target;
	}
	
	/**
	 * Retrieves the extraction rate for this action.<br>
	 * This value is used to determine how many items are produced.
	 * @return The current {@code float} rate.
	 */
	public float getRate()
	{
		return rate;
	}
}
