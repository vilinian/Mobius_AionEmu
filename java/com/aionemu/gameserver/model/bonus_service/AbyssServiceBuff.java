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
package com.aionemu.gameserver.model.bonus_service;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssPenaltyAttr;
import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssServiceAttr;
import com.aionemu.gameserver.skillengine.change.Func;

/**
 * This class represents a buff related to the Abyss Service system.<br>
 * It manages specific attribute bonuses and penalties for players.<br>
 * It implements {@link StatOwner} to provide access to calculated statistics.
 */
public class AbyssServiceBuff implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final AbyssServiceAttr abyssBonusAttr;
	
	/**
	 * Creates a new instance of {@code AbyssServiceBuff}.<br>
	 * It loads the bonus attributes using the provided ID.
	 * @param buffId The unique identifier for the abyss buff.
	 */
	public AbyssServiceBuff(int buffId)
	{
		abyssBonusAttr = DataManager.ABYSS_BUFF_DATA.getInstanceBonusattr(buffId);
	}
	
	/**
	 * Applies the Abyss effect to a specific player.<br>
	 * This method calculates and adds all associated penalties to the {@code Player}.<br>
	 * It updates the player's status to true for abyss bonuses.
	 * @param player The {@link Player} who will receive the effect.
	 * @param buffId The unique identifier for the Abyss buff.
	 */
	public void applyAbyssEffect(Player player, int buffId)
	{
		if (abyssBonusAttr == null)
		{
			return;
		}
		
		for (AbyssPenaltyAttr abyssPenaltyAttr : abyssBonusAttr.getPenaltyAttr())
		{
			if (abyssPenaltyAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(abyssPenaltyAttr.getStat(), abyssPenaltyAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(abyssPenaltyAttr.getStat(), abyssPenaltyAttr.getValue(), true));
			}
		}
		
		player.setAbyssBonus(true);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the active effect from a specific player.<br>
	 * This method clears all associated functions and updates the {@code Player} status.<br>
	 * It also notifies the game stats to stop processing this buff.
	 * @param player The {@link Player} who currently has the effect.
	 * @param buffId The unique identifier for the buff being removed.
	 */
	public void endEffect(Player player, int buffId)
	{
		functions.clear();
		player.setAbyssBonus(false);
		player.getGameStats().endEffect(this);
	}
}
