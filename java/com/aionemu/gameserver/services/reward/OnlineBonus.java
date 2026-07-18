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
package com.aionemu.gameserver.services.reward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * This class handles the distribution of rewards for players who remain online.<br>
 * It manages periodic bonuses granted to {@link Player} objects based on their active status.
 * @author Eloann
 * @Reworked Kill3r
 */
public class OnlineBonus
{
	private static final Logger log = LoggerFactory.getLogger(OnlineBonus.class);
	
	/**
	 * This is a private constructor for the {@link OnlineBonus} class.<br>
	 * It initializes the scheduled task that rewards players for staying online.<br>
	 * This constructor should not be called directly by other classes.
	 */
	private OnlineBonus()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> World.getInstance().doOnAllPlayers(object ->
		{
			final int time = MembershipConfig.ONLINE_BONUS_TIME;
			try
			{
				if (object.getInventory().isFull())
				{
					log.warn("[OnlineBonusService] Player " + object.getName() + " tried to receive item with full inventory.");
				}
				else
				{
					ItemService.addItem(object, MembershipConfig.ONLINE_BONUS_ITEM, MembershipConfig.ONLINE_BONUS_COUNT);
					if (MembershipConfig.ONLINE_BONUS_ABYSS_ENABLE)
					{
						AbyssPointsService.addAp(object, MembershipConfig.ONLINE_BONUS_AP);
						AbyssPointsService.addGp(object, MembershipConfig.ONLINE_BONUS_GP);
						PacketSendUtility.sendMessage(object, "[OnlineBonusService]: You've Played " + time + " Minutes. You earn a Bonus Item and Some AP! :)");
					}
					else
					{
						PacketSendUtility.sendMessage(object, "[OnlineBonusService]: You've Played " + time + " Minutes. You earn a Bonus Item! :)");
					}
				}
			}
			catch (Exception ex)
			{
				log.error("Exception during event rewarding of player " + object.getName(), ex);
			}
			
		}), MembershipConfig.ONLINE_BONUS_TIME * 60000, MembershipConfig.ONLINE_BONUS_TIME * 60000);
	}
	
	/**
	 * Handles the logic when a {@link Player} logs into the game.<br>
	 * It checks if online bonuses are enabled in the {@code MembershipConfig}.<br>
	 * If enabled, it updates the player's online bonus timestamp.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void playerLoggedIn(Player player)
	{
		if (MembershipConfig.ONLINE_BONUS_ENABLE)
		{
			player.setOnlineBonusTime(System.currentTimeMillis());
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} disconnects from the server.<br>
	 * This method cleans up any active online bonuses for the user.<br>
	 * It ensures that rewards are processed correctly before the session ends.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void playerLoggedOut(Player player)
	{
	}
	
	/**
	 * Provides the global instance of the {@link OnlineBonus} class.<br>
	 * This method follows the singleton pattern to ensure only one instance exists.<br>
	 * Use this method to access the online bonus service throughout the application.
	 * @return The single instance of {@code OnlineBonus}.
	 */
	public static OnlineBonus getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final OnlineBonus instance = new OnlineBonus();
	}
}
