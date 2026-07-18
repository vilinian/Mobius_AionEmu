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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.bonus_service.PlayersBonus;
import com.aionemu.gameserver.model.bonus_service.ServiceBuff;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerBonusTimeStatus;

/**
 * This service manages the application and lifecycle of buffs for {@link Player} objects.<br>
 * It handles the logic for granting, removing, and tracking active {@code ServiceBuff} effects.
 * @author Ace on 31/07/2016
 */
public class PlayerBuffService
{
	private static ServiceBuff serviceBuff;
	private static PlayersBonus playersBonus;
	private static final Logger log = LoggerFactory.getLogger("GAMECONNECTION_LOG");
	
	/**
	 * This method initializes the buffs for a {@link Player} entering the game world.<br>
	 * It updates the player's bonus time and applies various status effects.<br>
	 * The method calls internal logic to handle security, new player, and returner buffs.
	 * @param player The {@code Player} object that is entering the world.
	 */
	public void enterWorld(Player player)
	{
		player.setBonusTime(player.getCommonData().getBonusTime());
		player.setBonusTimeStatus();
		securityBuff(player);
		newPlayerBuff(player);
		returnPlayerBuff(player);
	}
	
	/**
	 * Applies a security buff to the player.<br>
	 * This check ensures that non-members receive a {@code ServiceBuff}.<br>
	 * It verifies if the {@code Player} membership status is equal to {@code 0}.
	 * @param player The {@link Player} object to receive the buff.
	 */
	private void securityBuff(Player player)
	{
		// Service Security Buff.
		if (player.getMembership() == 0)
		{
			serviceBuff = new ServiceBuff(2);
			serviceBuff.applyEffect(player, 2);
		}
	}
	
	/**
	 * Applies the appropriate bonus to a {@link Player}.<br>
	 * It checks if the player is a new user.<br>
	 * New players receive an Ascension Boost.<br>
	 * Existing players have their effects ended.
	 * @param player The {@code Player} object to process.
	 */
	private void newPlayerBuff(Player player)
	{
		if (player.isNewPlayer())
		{
			playersBonus = new PlayersBonus(2);
			playersBonus.applyEffect(player, 2);
			log.info("Player " + player.getName() + " Received Ascension Boost");
		}
		else
		{
			playersBonus = new PlayersBonus(1);
			playersBonus.endEffect(player, 1);
		}
	}
	
	/**
	 * This method handles the logic for returning a player's buff.<br>
	 * It checks if the {@link Player} has a {@code RETURN} status.<br>
	 * If true, it applies a new boost to the player.<br>
	 * Otherwise, it ends the current effect.
	 * @param player The {@link Player} object to process.
	 */
	private void returnPlayerBuff(Player player)
	{
		if (player.getBonusTime().getStatus() == PlayerBonusTimeStatus.RETURN)
		{
			playersBonus = new PlayersBonus(3);
			playersBonus.applyEffect(player, 3);
			player.setPlayersBonusId(3);
			log.info("Player " + player.getName() + " Received Return Boost");
		}
		else
		{
			playersBonus = new PlayersBonus(1);
			playersBonus.endEffect(player, 1);
		}
	}
	
	/**
	 * Provides the global instance of the {@link PlayerBuffService}.<br>
	 * This method follows the singleton pattern.
	 * @return The single instance of {@code PlayerBuffService}.
	 */
	public static PlayerBuffService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerBuffService instance = new PlayerBuffService();
	}
}
