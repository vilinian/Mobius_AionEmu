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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerFameDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.fame.FameExp;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_FAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the fame system for {@link Player} objects.<br>
 * It handles experience calculations and updates {@link PlayerFame} data.<br>
 * It also coordinates sending relevant packets like {@code SM_PLAYER_FAME} to the client.
 */
public class PlayerFameService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerFameService.class);
	public int MAX_LEVEL = 9;
	
	/**
	 * Initializes the {@link PlayerFameService}.<br>
	 * This method logs a message to indicate that the service has finished loading.
	 */
	public void init()
	{
		log.info("[PlayerFameService] loaded ...");
	}
	
	/**
	 * Resets the weekly fame progress for all players.<br>
	 * This method iterates through all {@link PlayerFame} records.<br>
	 * It reduces experience points and updates levels based on weekly rules.
	 */
	public void onResetWeekly()
	{
		final List<PlayerFame> famesReduce = getDao().weeklyFame();
		for (PlayerFame fame : famesReduce)
		{
			final long reduce = Math.round(fame.getExp() * 0);
			if ((fame.getExp() - reduce) <= 0)
			{
				fame.setLevel(fame.getLevel() - 1);
				final long changeExp = getExpForLevel(fame.getLevel()) - Math.round(fame.getExp() * 0.015);
				fame.setExp(changeExp);
				getDao().reduceWeekly(fame);
			}
			else
			{
				fame.setExp(fame.getExp() - reduce);
				getDao().reduceWeekly(fame);
			}
		}
		
		PlayerFameService.log.info("[PlayerFameService] : Weekly Reduce Finish");
	}
	
	/**
	 * Restores lost experience points to a player's fame.<br>
	 * This method checks the current world for any pending {@code expLoss}.<br>
	 * It adds that amount back to the {@link Player} and updates the database.
	 * @param player The {@code Player} object whose fame experience needs to be recovered.
	 */
	public void recoverExpFame(Player player)
	{
		final PlayerFame fame = fameLevelByWorld(player, player.getWorldId());
		if (fame != null)
		{
			addFameExp(player, fame.getExpLoss());
			fame.setExpLoss(0);
			getDao().updatePlayerFame(player, fame);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		player.setPlayerFame(getDao().loadPlayerFame(player));
		for (int i = 1; i < 8; ++i)
		{
			if (!player.getPlayerFame().containsKey(i))
			{
				final PlayerFame fame = new PlayerFame(i, 1, 0, 0, player.getObjectId());
				player.getPlayerFame().put(fame.getId(), fame);
				getDao().addPlayerFame(player, fame);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
	}
	
	/**
	 * Adds experience points to the {@link Player}'s fame.<br>
	 * This method updates the player's level if they reach the required threshold.<br>
	 * It also handles capping the experience at the maximum level.
	 * @param player The {@code Player} object receiving the experience.
	 * @param points The amount of experience to add.
	 */
	public void addFameExp(Player player, long points)
	{
		for (PlayerFame playerFame : player.getPlayerFame().values())
		{
			if (player.getWorldId() == playerFame.getFameEnum().getWorldId())
			{
				final long exp = playerFame.getExp();
				if ((playerFame.getLevel() == MAX_LEVEL) && ((exp + points) > getExpForLevel(playerFame.getLevel())))
				{
					playerFame.setExp(getExpForLevel(9));
					getDao().updatePlayerFame(player, playerFame);
				}
				else if ((exp + points) >= getExpForLevel(playerFame.getLevel()))
				{
					final long diff = (exp + points) - getExpForLevel(playerFame.getLevel());
					playerFame.setLevel(playerFame.getLevel() + 1);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FAME_CHANGE_LEVEL_DONE(playerFame.getFameEnum().getDescriptionId(), playerFame.getLevel()));
					playerFame.setExp(diff);
					getDao().updatePlayerFame(player, playerFame);
				}
				else
				{
					playerFame.setExp(exp + points);
					getDao().updatePlayerFame(player, playerFame);
				}
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
	}
	
	/**
	 * Handles the logic when a {@link Player} dies.<br>
	 * This method updates the player's fame experience based on death rules.<br>
	 * It also sends a {@code SM_PLAYER_FAME} packet to the client.
	 * @param player The {@code Player} object that has died.
	 */
	public void onPlayerDie(Player player)
	{
		for (PlayerFame playerFame : player.getPlayerFame().values())
		{
			if ((player.getWorldId() == playerFame.getFameEnum().getWorldId()) && (playerFame.getExp() != 0))
			{
				final int loss = Math.round(playerFame.getExp() * 0);
				final int unrecoverable = (int) (loss * 0.22222222);
				final int recoverable = loss - unrecoverable;
				if ((playerFame.getLevel() - loss) < 0)
				{
					playerFame.setExp(0);
				}
				else
				{
					playerFame.setExp(playerFame.getExp() - loss);
				}
				
				playerFame.setExpLoss(playerFame.getExpLoss() + recoverable);
				getDao().updatePlayerFame(player, playerFame);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_PLAYER_FAME(player));
	}
	
	/**
	 * Retrieves the {@link PlayerFame} for a specific world.<br>
	 * It searches through all of the {@code player} fame records.<br>
	 * The method returns the record that matches the provided {@code worldId}.
	 * @param player The {@code Player} object to check.
	 * @param worldId The unique identifier for the world.
	 * @return The matching {@link PlayerFame} object or {@code null} if not found.
	 */
	public PlayerFame fameLevelByWorld(Player player, int worldId)
	{
		PlayerFame playerFame = null;
		for (PlayerFame fame : player.getPlayerFame().values())
		{
			if (fame.getFameEnum().getWorldId() == worldId)
			{
				playerFame = fame;
			}
		}
		
		return playerFame;
	}
	
	/**
	 * Retrieves the required experience points for a specific fame level.<br>
	 * This method uses {@link FameExp} to look up the value.
	 * @param level The target fame level to check.
	 * @return The amount of experience needed for the given {@code level}.
	 */
	public long getExpForLevel(int level)
	{
		return FameExp.getFameExp(level).getExp();
	}
	
	/**
	 * Retrieves the {@link PlayerFameDAO} instance.<br>
	 * This method uses {@code DAOManager} to fetch the database access object.
	 * @return The {@code PlayerFameDAO} used for fame data operations.
	 */
	public PlayerFameDAO getDao()
	{
		return DAOManager.getDAO(PlayerFameDAO.class);
	}
	
	/**
	 * Gets the singleton instance of this service.<br>
	 * Use this method to access the global {@link PlayerFameService}.<br>
	 * This ensures only one instance exists throughout the application.
	 * @return The singleton instance of {@code PlayerFameService}.
	 */
	public static PlayerFameService getInstance()
	{
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder
	{
		private static final PlayerFameService INSTANCE = new PlayerFameService();
	}
}
