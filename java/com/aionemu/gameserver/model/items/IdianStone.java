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
package com.aionemu.gameserver.model.items;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents an {@link Item} of the {@code IdianStone} type.<br>
 * This class handles the specific properties and behaviors for these stones in the game world.
 * @author xTz
 */
public class IdianStone extends ItemStone
{
	private ActionObserver actionListener;
	private int polishCharge;
	private final int polishSetId;
	private final int polishNumber;
	private final Item item;
	private final ItemTemplate template;
	private final int burnDefend;
	private final int burnAttack;
	private final RandomBonusEffect rndBonusEffect;
	
	/**
	 * Creates a new instance of an {@link IdianStone}.<br>
	 * This constructor initializes the stone with specific item data and polish properties.<br>
	 * It sets up the burn attributes and random bonus effects based on the provided template.
	 * @param itemId The unique identifier for the item type.
	 * @param persistentState The state of the object in the game world.
	 * @param item The {@link Item} object associated with this stone.
	 * @param polishNumber The specific number assigned to the polish.
	 * @param polishCharge The initial charge amount for the polish.
	 */
	public IdianStone(int itemId, PersistentState persistentState, Item item, int polishNumber, int polishCharge)
	{
		super(item.getObjectId(), itemId, 0, persistentState);
		this.item = item;
		burnDefend = item.getItemTemplate().getIdianAction().getBurnDefend();
		burnAttack = item.getItemTemplate().getIdianAction().getBurnAttack();
		this.polishCharge = polishCharge;
		template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.polishNumber = polishNumber;
		polishSetId = template.getActions().getPolishAction().getPolishSetId();
		rndBonusEffect = new RandomBonusEffect(StatBonusType.POLISH, polishSetId, polishNumber);
	}
	
	/**
	 * Handles the logic when an {@code IdianStone} is equipped by a {@link Player}.<br>
	 * It registers an action observer to manage polish charges and applies random effects.<br>
	 * The method ensures that the stone has a {@code polishCharge} greater than 0 before proceeding.
	 * @param player The {@link Player} who equipped the stone.
	 */
	public void onEquip(Player player)
	{
		if (polishCharge > 0)
		{
			actionListener = new ActionObserver(ObserverType.ALL)
			{
				@Override
				public void attacked(Creature creature)
				{
					decreasePolishCharge(player, true);
				}
				
				@Override
				public void attack(Creature creature)
				{
					decreasePolishCharge(player, false);
				}
			};
			player.getObserveController().addObserver(actionListener);
			rndBonusEffect.applyEffect(player);
		}
	}
	
	/**
	 * Reduces the {@code polishCharge} of an {@link IdianStone}.<br>
	 * This method handles the charge reduction when a player is attacked.<br>
	 * It ensures thread safety using the {@code synchronized} keyword.
	 * @param player The {@link Player} who owns the item.
	 * @param isAttacked A boolean indicating if the player was hit.
	 */
	private synchronized void decreasePolishCharge(Player player, boolean isAttacked)
	{
		decreasePolishCharge(player, isAttacked, 0);
	}
	
	/**
	 * Reduces the {@code polishCharge} of an {@link IdianStone}.<br>
	 * This method is called when a player uses a specific skill.<br>
	 * It updates the charge based on the provided {@code skillValue}.
	 * @param player The {@link Player} who owns the item.
	 * @param skillValue The amount of charge to decrease.
	 */
	public synchronized void decreasePolishCharge(Player player, int skillValue)
	{
		decreasePolishCharge(player, false, skillValue);
	}
	
	/**
	 * Reduces the {@code polishCharge} of this stone based on combat actions.<br>
	 * It handles both standard attacks and specific skill values.<br>
	 * If the charge reaches 0, it triggers an unequip action and removes the item.
	 * @param player The {@link Player} who owns the stone.
	 * @param isAttacked A boolean indicating if the player was attacked.
	 * @param skillValue The value of the skill used to determine charge reduction.
	 */
	private synchronized void decreasePolishCharge(Player player, boolean isAttacked, int skillValue)
	{
		int result = 0;
		if (polishCharge <= 0)
		{
			return;
		}
		
		if (skillValue == 0)
		{
			result = isAttacked ? burnDefend : burnAttack;
		}
		else
		{
			result = skillValue;
		}
		
		if ((polishCharge - result) < 0)
		{
			polishCharge = 0;
		}
		else
		{
			polishCharge -= result;
		}
		
		if (polishCharge == 0)
		{
			onUnEquip(player);
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401652, new DescriptionId(item.getNameId())));
			item.setIdianStone(null);
			setPersistentState(PersistentState.DELETED);
			DAOManager.getDAO(ItemStoneListDAO.class).storeIdianStones(this);
		}
	}
	
	/**
	 * Retrieves the unique identification number for this polish.<br>
	 * This value is used to identify which specific polish set it belongs to.
	 * @return The {@code int} value of the {@code polishNumber}.
	 */
	public int getPolishNumber()
	{
		return polishNumber;
	}
	
	/**
	 * Retrieves the unique identifier for the polish set.<br>
	 * This value identifies which group of items this stone belongs to.
	 * @return The {@code int} ID of the polish set.
	 */
	public int getPolishSetId()
	{
		return polishSetId;
	}
	
	/**
	 * Retrieves the current charge of the {@code IdianStone}.<br>
	 * This value represents how many uses are remaining.
	 * @return The current {@code polishCharge} as an {@code int}.
	 */
	public int getPolishCharge()
	{
		return polishCharge;
	}
	
	/**
	 * Handles the logic when a player removes this item.<br>
	 * It removes any active observers associated with the {@code IdianStone}.
	 * @param player The {@link Player} who is unequipping the item.
	 */
	public void onUnEquip(Player player)
	{
		if (actionListener != null)
		{
			rndBonusEffect.endEffect(player);
			player.getObserveController().removeObserver(actionListener);
			actionListener = null;
		}
	}
}
