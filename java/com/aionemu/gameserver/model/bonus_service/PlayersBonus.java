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
import com.aionemu.gameserver.model.templates.bonus_service.PlayersBonusPenaltyAttr;
import com.aionemu.gameserver.model.templates.bonus_service.PlayersBonusServiceAttr;
import com.aionemu.gameserver.skillengine.change.Func;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * This class represents the bonus statistics applied to a {@link Player}.<br>
 * It manages various stat modifiers and functions provided by the game's bonus system.<br>
 * It implements {@link StatOwner} to allow for easy calculation of player attributes.
 * @author Ace on 31/07/2016
 */
public class PlayersBonus implements StatOwner
{
	private final List<IStatFunction> functions = new ArrayList<>();
	private final PlayersBonusServiceAttr playersServiceBonusattr;
	
	/**
	 * Creates a new instance of {@code PlayersBonus}.<br>
	 * This constructor initializes the bonus data using a specific ID.<br>
	 * It retrieves the correct attributes from {@link DataManager}.
	 * @param buffId The unique identifier for the player bonus.
	 */
	public PlayersBonus(int buffId)
	{
		playersServiceBonusattr = DataManager.PLAYERS_BONUS_DATA.getInstanceBonusattr(buffId);
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
		if (playersServiceBonusattr == null)
		{
			return;
		}
		
		for (PlayersBonusPenaltyAttr playersBonusPenaltyAttr : playersServiceBonusattr.getPenaltyAttr())
		{
			if (playersBonusPenaltyAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(playersBonusPenaltyAttr.getStat(), playersBonusPenaltyAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(playersBonusPenaltyAttr.getStat(), playersBonusPenaltyAttr.getValue(), true));
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
		player.setPlayersBonusId(1);
		player.getGameStats().endEffect(this);
	}
}
