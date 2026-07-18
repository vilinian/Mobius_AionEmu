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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;

/**
 * This class manages the random statistics applied to items.<br>
 * It handles how {@link StatBonusType} values are calculated and assigned to an item.
 * @author xTz
 */
public class RandomStats
{
	private final RandomBonusEffect rndBonusEffect;
	
	/**
	 * Creates a new instance of {@code RandomStats}.<br>
	 * This constructor initializes the internal bonus effect.
	 * @param setId The unique identifier for the item set.
	 * @param setNumber The specific number within the set.
	 */
	public RandomStats(int setId, int setNumber)
	{
		rndBonusEffect = new RandomBonusEffect(StatBonusType.INVENTORY, setId, setNumber);
	}
	
	/**
	 * Applies the random bonus effects to the player.<br>
	 * This is called when the item is equipped by a {@link Player}.
	 * @param player The {@link Player} who equipped the item.
	 */
	public void onEquip(Player player)
	{
		rndBonusEffect.applyEffect(player);
	}
	
	/**
	 * Handles the logic when a player removes this item.<br>
	 * It ends any active effects associated with the {@code RandomStats}.
	 * @param player The {@link Player} who is unequipping the item.
	 */
	public void onUnEquip(Player player)
	{
		rndBonusEffect.endEffect(player);
	}
}
