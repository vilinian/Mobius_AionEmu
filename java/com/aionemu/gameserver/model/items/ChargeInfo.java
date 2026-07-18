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

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class represents the data and logic for an item's charge status.<br>
 * It tracks how much energy or duration a {@link Item} has remaining.<br>
 * It extends {@link ActionObserver} to monitor changes during gameplay.
 * @author ATracer
 */
public class ChargeInfo extends ActionObserver
{
	public static final int LEVEL2 = 1000000;
	public static final int LEVEL1 = 500000;
	private int chargePoints;
	private final int attackBurn;
	private final int defendBurn;
	private final Item item;
	private Player player;
	
	/**
	 * Creates a new {@link ChargeInfo} instance.<br>
	 * This constructor initializes the charge points and the associated item.<br>
	 * It also sets up burn values based on the item's improvement.
	 * @param chargePoints The initial amount of charge points.
	 * @param item The {@code Item} object used for this charge information.
	 */
	public ChargeInfo(int chargePoints, Item item)
	{
		super(ObserverType.ATTACK_DEFEND);
		this.chargePoints = chargePoints;
		this.item = item;
		if (item.getImprovement() != null)
		{
			attackBurn = item.getImprovement().getBurnAttack();
			defendBurn = item.getImprovement().getBurnDefend();
		}
		else
		{
			attackBurn = 0;
			defendBurn = 0;
		}
	}
	
	/**
	 * Retrieves the current number of charge points.<br>
	 * This value represents the remaining energy for the item.
	 * @return The total number of charge points as an {@code int}.
	 */
	public int getChargePoints()
	{
		return chargePoints;
	}
	
	/**
	 * Sets the {@link Player} associated with this task.<br>
	 * This updates the internal {@code player} field.
	 * @param player The {@code Player} object to set.
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Updates the current charge points of an item.<br>
	 * This method adds {@code addPoints} to the existing total.<br>
	 * The value is clamped between {@code 0} and {@code LEVEL2}.<br>
	 * It also marks the item state as requiring an update.
	 * @param addPoints The amount of points to add or subtract.
	 * @return The new total number of charge points.
	 */
	public int updateChargePoints(int addPoints)
	{
		int newChargePoints = chargePoints + addPoints;
		if (newChargePoints > LEVEL2)
		{
			newChargePoints = LEVEL2;
		}
		else if (newChargePoints < 0)
		{
			newChargePoints = 0;
		}
		
		if (item.isEquipped() && (player != null))
		{
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		chargePoints = newChargePoints;
		return newChargePoints;
	}
	
	/**
	 * This method is called when a creature is attacked.<br>
	 * It allows the observer to react to combat events involving a {@link Creature}.
	 * @param creature The {@code Creature} that was targeted in the attack.
	 */
	@Override
	public void attacked(Creature creature)
	{
		updateChargePoints(-defendBurn);
		final Player player = this.player;
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}
	
	/**
	 * Performs an attack on a target creature.<br>
	 * This method reduces the current charge points by {@code attackBurn}.<br>
	 * It also sends an inventory update packet to the {@link Player} if they exist.
	 * @param creature The {@code Creature} being attacked.
	 */
	@Override
	public void attack(Creature creature)
	{
		updateChargePoints(-attackBurn);
		final Player player = this.player;
		if (player != null)
		{
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}
}
