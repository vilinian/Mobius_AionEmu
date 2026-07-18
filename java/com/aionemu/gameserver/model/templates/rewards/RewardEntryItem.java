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
package com.aionemu.gameserver.model.templates.rewards;

/**
 * Represents an individual item entry within a reward template.<br>
 * This class stores the specific properties of an item granted to a player.
 * @author KID
 */
public class RewardEntryItem
{
	/**
	 * Creates a new instance of {@link RewardEntryItem}.<br>
	 * This constructor initializes the reward with specific properties.
	 * @param unique The uniqueness status of the item.
	 * @param item_id The unique identifier for the item.
	 * @param count The total quantity of the item.
	 */
	public RewardEntryItem(int unique, int item_id, long count)
	{
		this.unique = unique;
		id = item_id;
		this.count = count;
	}
	
	public int id, unique;
	public long count;
}
