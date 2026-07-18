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
package com.aionemu.gameserver.model.drop;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * Represents an item drop that can occur during gameplay.<br>
 * This class handles the logic for determining which {@link ItemTemplate} is awarded to a {@link Player}.<br>
 * It implements {@link DropCalculator} to manage drop rates and probabilities.
 * @author MrPoke
 */
public class Drop implements DropCalculator
{
	private int itemId;
	private int minAmount;
	private int maxAmount;
	private float chance;
	private boolean noReduce = false;
	private boolean eachMember = false;
	private ItemTemplate template;
	
	/**
	 * Creates a new {@code Drop} object with specific loot rules.<br>
	 * This constructor initializes the item data and drop probabilities.
	 * @param itemId The unique identifier for the item to be dropped.
	 * @param minAmount The minimum quantity of the item that can drop.
	 * @param maxAmount The maximum quantity of the item that can drop.
	 * @param chance The probability of the item dropping as a float value.
	 * @param noReduce Determines if the amount should remain unchanged when {@code true}.
	 */
	public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce)
	{
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
		this.noReduce = noReduce;
		template = DataManager.ITEM_DATA.getItemTemplate(itemId);
	}
	
	/**
	 * Creates a new {@link Drop} object with specific loot rules.<br>
	 * This constructor defines how an item is dropped by the game engine.
	 * @param itemId The unique identifier for the item to be dropped.
	 * @param minAmount The minimum quantity of the item that can drop.
	 * @param maxAmount The maximum quantity of the item that can drop.
	 * @param chance The probability percentage for this drop to occur.
	 * @param noReduce If {@code true}, the amount will not be reduced by modifiers.
	 * @param eachMember If {@code true}, every group member receives a separate drop.
	 */
	public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce, boolean eachMember)
	{
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
		this.noReduce = noReduce;
		this.eachMember = eachMember;
	}
	
	/**
	 * Creates a new instance of the {@code Drop} class.<br>
	 * This constructor initializes a default drop object with null values.
	 */
	public Drop()
	{
	}
	
	/**
	 * Retrieves the {@link ItemTemplate} for this drop.<br>
	 * It returns the cached {@code template} if it exists.<br>
	 * If the {@code template} is {@code null}, it fetches the data from {@link DataManager}.
	 * @return The {@link ItemTemplate} associated with this drop.
	 */
	public ItemTemplate getItemTemplate()
	{
		return template == null ? DataManager.ITEM_DATA.getItemTemplate(itemId) : template;
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the minimum quantity of an item that can be dropped.<br>
	 * This value is used by {@code int, float, com.aionemu.gameserver.model.Race, java.util.Collection)}.
	 * @return The minimum amount as an {@code int}.
	 */
	public int getMinAmount()
	{
		return minAmount;
	}
	
	/**
	 * Retrieves the maximum quantity of an item that can drop.<br>
	 * This value is used by {@code int, float, com.aionemu.gameserver.model.Race, java.util.Collection)}.
	 * @return The maximum amount as an {@code int}.
	 */
	public int getMaxAmount()
	{
		return maxAmount;
	}
	
	/**
	 * Retrieves the probability of this drop occurring.<br>
	 * The value is stored as a {@code float}.
	 * @return The drop chance value.
	 */
	public float getChance()
	{
		return chance;
	}
	
	/**
	 * Checks if the drop amount is exempt from reductions.<br>
	 * This method returns {@code true} if the {@code noReduce} flag is set.<br>
	 * It helps determine if modifiers should be applied to this specific drop.
	 * @return {@code true} if no reduction is applied, {@code false} otherwise.
	 */
	public boolean isNoReduction()
	{
		return noReduce;
	}
	
	/**
	 * Checks if the drop should be given to every member of a group.<br>
	 * Returns {@code true} if each member receives the item.<br>
	 * Returns {@code false} otherwise.
	 * @return The value of the {@code eachMember} flag.
	 */
	public Boolean isEachMember()
	{
		return eachMember;
	}
	
	/**
	 * Calculates and adds items to a drop result set based on random chance.<br>
	 * It considers modifiers, player races, and group membership rules.
	 * @param result The {@code Set<DropItem>} to store the generated drops.
	 * @param index The current position in the drop sequence.
	 * @param dropModifier A multiplier applied to the base drop chance.
	 * @param race The {@link Race} of the player triggering the drop.
	 * @param groupMembers A {@code Collection<Player>} representing the current party.
	 * @return The updated index after adding new items.
	 */
	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers)
	{
		float percent = chance;
		if (!noReduce)
		{
			percent *= dropModifier;
		}
		
		if ((Rnd.get() * 100) < percent)
		{
			if (eachMember && (groupMembers != null) && !groupMembers.isEmpty())
			{
				for (Player player : groupMembers)
				{
					final DropItem dropitem = new DropItem(this);
					dropitem.calculateCount();
					dropitem.setIndex(index++);
					dropitem.setPlayerObjId(player.getObjectId());
					dropitem.setWinningPlayer(player);
					dropitem.isDistributeItem(true);
					result.add(dropitem);
				}
			}
			else
			{
				final DropItem dropitem = new DropItem(this);
				dropitem.calculateCount();
				dropitem.setIndex(index++);
				result.add(dropitem);
			}
		}
		
		return index;
	}
	
	/**
	 * Creates a new {@link Drop} object from a raw data buffer.<br>
	 * This method reads the item properties directly from the provided {@code ByteBuffer}.<br>
	 * It is used to reconstruct drop data from binary files.
	 * @param buffer The {@code ByteBuffer} containing the serialized drop data.
	 * @return A new {@link Drop} instance populated with the data from the buffer.
	 */
	public static Drop load(ByteBuffer buffer)
	{
		final Drop drop = new Drop();
		drop.itemId = buffer.getInt();
		drop.chance = buffer.getFloat();
		drop.minAmount = buffer.getInt();
		drop.maxAmount = buffer.getInt();
		drop.noReduce = buffer.get() == 1 ? true : false;
		drop.eachMember = buffer.get() == 1 ? true : false;
		return drop;
	}
	
	/**
	 * Returns a string representation of the {@code Drop} object.<br>
	 * This method provides a summary of all drop properties.
	 * @return A formatted string containing the item ID, amounts, chance, and flags.
	 */
	@Override
	public String toString()
	{
		return "Drop [itemId=" + itemId + ", minAmount=" + minAmount + ", maxAmount=" + maxAmount + ", chance=" + chance + ", noReduce=" + noReduce + ", eachMember=" + eachMember + "]";
	}
}
