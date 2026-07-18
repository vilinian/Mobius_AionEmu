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
import com.aionemu.gameserver.model.templates.bonus_service.F2pBonusAttr;
import com.aionemu.gameserver.model.templates.bonus_service.F2pPenalityAttr;
import com.aionemu.gameserver.skillengine.change.Func;

/**
 * This class manages the statistics bonuses and penalties applied to Free-to-Play (F2P) players.<br>
 * It implements {@link StatOwner} to provide specific attribute modifications for these accounts.
 */
public class F2pBonus implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final F2pBonusAttr f2pBonusattr;
	
	/**
	 * Creates a new {@code F2pBonus} instance for a specific buff.<br>
	 * This constructor loads the bonus attributes from the data manager.
	 * @param buffId The unique identifier of the buff to load.
	 */
	public F2pBonus(int buffId)
	{
		f2pBonusattr = DataManager.F2P_BONUS_DATA.getInstanceBonusattr(buffId);
	}
	
	/**
	 * Applies a specific bonus effect to a player.<br>
	 * This method adds the necessary stat functions based on the {@code buffId}.<br>
	 * It updates the player's game stats using the internal list of functions.
	 * @param player The {@link Player} who will receive the effect.
	 * @param buffId The unique identifier for the buff to apply.
	 */
	public void applyEffect(Player player, int buffId)
	{
		if (f2pBonusattr == null)
		{
			return;
		}
		
		for (F2pPenalityAttr f2pBonusPenaltyAttr : f2pBonusattr.getPenaltyAttr())
		{
			if (f2pBonusPenaltyAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(f2pBonusPenaltyAttr.getStat(), f2pBonusPenaltyAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(f2pBonusPenaltyAttr.getStat(), f2pBonusPenaltyAttr.getValue(), true));
			}
		}
		
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
		player.getGameStats().endEffect(this);
	}
}
