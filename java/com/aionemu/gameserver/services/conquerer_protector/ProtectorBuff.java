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
package com.aionemu.gameserver.services.conquerer_protector;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class manages the buff data for the Conquerer Protector system.<br>
 * It allows the server to track and apply specific statistics to players who have this status.
 * @author CoolyT
 */
public class ProtectorBuff implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	
	/**
	 * Applies a specific buff effect to a {@link Player}.<br>
	 * This method calculates the stat increase based on the provided {@code buffLevel}.<br>
	 * It removes any existing effects before adding the new one.
	 * @param player The {@code Player} who will receive the buff.
	 * @param buffLevel The level of the buff to apply.
	 */
	public void applyEffect(Player player, int buffLevel)
	{
		final int addvalue = buffLevel * 20; // BuffLevel 1 = 20; BuffLevel 2 = 40; Bufflevel 3 = 60; regarding client Xml ......
		
		if (buffLevel == 0)
		{
			if (hasBuff())
			{
				player.getGameStats().endEffect(this);
			}
			return;
		}
		
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
