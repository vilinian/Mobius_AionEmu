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
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.SellLimit;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the maximum limits for player actions and resources.<br>
 * It ensures that players do not exceed defined constraints within the game world.
 * @author Source
 */
public class PlayerLimitService
{
	private static Map<Integer, Long> sellLimit = new ConcurrentHashMap<>();
	
	/**
	 * Updates the daily sell limit for a specific {@link Player}.<br>
	 * This method checks if the {@code reward} amount is within the allowed limit.<br>
	 * It subtracts the reward from the remaining limit if successful.
	 * @param player The {@link Player} whose sell limit is being updated.
	 * @param reward The amount to be deducted from the current limit.
	 * @return {@code true} if the update was successful, or {@code false} if the limit was exceeded.
	 */
	public static boolean updateSellLimit(Player player, long reward)
	{
		if (!CustomConfig.LIMITS_ENABLED)
		{
			return true;
		}
		
		final int accoutnId = player.getPlayerAccount().getId();
		Long limit = sellLimit.get(accoutnId);
		if (limit == null)
		{
			limit = (long) (SellLimit.getSellLimit(player.getPlayerAccount().getMaxPlayerLevel()) * player.getRates().getSellLimitRate());
			sellLimit.put(accoutnId, limit);
		}
		
		if (limit < reward)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DAY_CANNOT_SELL_NPC(limit));
			return false;
		}
		
		limit -= reward;
		sellLimit.put(accoutnId, limit);
		return true;
	}
	
	/**
	 * Schedules a periodic task to clear the {@code sellLimit} map.<br>
	 * It uses {@link CronService} to run this action based on the {@code LIMITS_UPDATE} configuration.
	 */
	public void scheduleUpdate()
	{
		CronService.getInstance().schedule(() -> sellLimit.clear(), CustomConfig.LIMITS_UPDATE, true);
	}
	
	/**
	 * Gets the single instance of the {@link PlayerLimitService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the service from anywhere in your code.
	 * @return The global instance of {@code PlayerLimitService}.
	 */
	public static PlayerLimitService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerLimitService instance = new PlayerLimitService();
	}
}
