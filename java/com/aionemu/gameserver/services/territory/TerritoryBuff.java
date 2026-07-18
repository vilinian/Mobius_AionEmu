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
package com.aionemu.gameserver.services.territory;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * Represents a buff applied to a territory that affects the statistics of players within it.<br>
 * This class implements {@link StatOwner} to manage and provide specific stat bonuses.
 * @author CoolyT
 */
public class TerritoryBuff implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Applies the bonus effects to a specific {@link Player}.<br>
	 * This method checks if the {@code lunaBonusTemplate} is valid.<br>
	 * It adds new stat functions based on the template attributes.<br>
	 * Finally, it registers these effects to the player's game stats.
	 * @param player The {@code Player} who will receive the effect.
	 */
	public void applyEffect(Player player)
	{
		final int addvalue = 60; // there is only one BuffLevel = 60; regarding client Xml ......
		
		if (hasBuff())
		{
			endEffect(player);
		}
		
		functions.add(new StatAddFunction(StatEnum.PVP_DEFEND_RATIO, addvalue, true));
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Checks if the current object has any active buffs.<br>
	 * It looks at the internal list of {@code IStatFunction} objects.
	 * @return {@code true} if there is at least one buff, otherwise {@code false}.
	 */
	public boolean hasBuff()
	{
		return !functions.isEmpty();
	}
	
	/**
	 * Removes the effect from the specified {@link Player}.<br>
	 * This method clears all internal functions.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} who currently has this effect applied.
	 */
	public void endEffect(Player player)
	{
		functions.clear();
		player.getGameStats().endEffect(this);
	}
	
}
