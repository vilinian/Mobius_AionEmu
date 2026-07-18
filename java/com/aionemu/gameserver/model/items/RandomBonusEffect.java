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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.templates.item.bonuses.StatBonusType;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents a random bonus effect applied to an item.<br>
 * This class manages how specific stats are modified based on randomized values.<br>
 * It implements {@link StatOwner} to provide access to these calculated bonuses.
 * @author xTz
 */
public class RandomBonusEffect implements StatOwner
{
	private final ModifiersTemplate template;
	
	/**
	 * Creates a new {@code RandomBonusEffect} instance.<br>
	 * This constructor initializes the effect using data from {@link DataManager}.<br>
	 * It links the specific bonus type to its corresponding polish set and number.
	 * @param type The category of the stat bonus.
	 * @param polishSetId The unique identifier for the polish set.
	 * @param polishNumber The specific index within that polish set.
	 */
	public RandomBonusEffect(StatBonusType type, int polishSetId, int polishNumber)
	{
		template = DataManager.ITEM_RANDOM_BONUSES.getTemplate(type, polishSetId, polishNumber);
	}
	
	/**
	 * Applies the bonus effects to a specific {@link Player}.<br>
	 * This method checks if the {@code lunaBonusTemplate} is valid.<br>
	 * It adds new stat functions based on the template attributes.<br>
	 * Finally, it registers these effects to the player's game stats.
	 * @param player The {@code Player} who will receive the effect.
	 */
	public void applyEffect(Player player)
	{
		player.getGameStats().addEffect(this, template.getModifiers());
	}
	
	/**
	 * Removes the effect from the specified {@link Player}.<br>
	 * This method clears all internal functions.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} who currently has this effect applied.
	 */
	public void endEffect(Player player)
	{
		player.getGameStats().endEffect(this);
	}
}
