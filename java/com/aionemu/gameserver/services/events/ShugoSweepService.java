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
package com.aionemu.gameserver.services.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;
import com.aionemu.gameserver.model.templates.shugosweep.ShugoSweepReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHUGO_SWEEP;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for the Shugo Sweep event system.<br>
 * This service manages reward distribution and player participation for {@link PlayerSweep} actions.
 * @author Ghostfur
 */
public class ShugoSweepService
{
	private static final Logger log = LoggerFactory.getLogger(ShugoSweepService.class);
	private final int boardId = EventsConfig.EVENT_SHUGOSWEEP_BOARD;
	
	/**
	 * Initializes the {@link ShugoSweepService}.<br>
	 * This method sets up the necessary components for the Shugo Sweep event.<br>
	 * It logs a confirmation message to the server console.
	 */
	public void initShugoSweep()
	{
		log.info("[ShugoSweepService] is initialized...");
		
		// TODO Set the weekly cron expression to every Wednesday at 9:00 AM.
	}
	
	/**
	 * Handles the logic for checking and updating a player's passport status when they log in.<br>
	 * This method verifies if the player has an active passport or needs to be assigned one.<br>
	 * It also checks for daily stamp resets and sends the appropriate server packets to the {@code Player}.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		DAOManager.getDAO(PlayerShugoSweepDAO.class).load(player);
		if (player.getPlayerShugoSweep() == null)
		{
			final PlayerSweep ps = new PlayerSweep(0, EventsConfig.EVENT_SHUGOSWEEP_FREEDICE, boardId);
			ps.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerShugoSweep(ps);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
			DAOManager.getDAO(PlayerShugoSweepDAO.class).add(player.getObjectId(), ps.getFreeDice(), ps.getStep(), ps.getBoardId());
		}
		
		if (player.getPlayerShugoSweep().getBoardId() != boardId)
		{
			final PlayerSweep ps = new PlayerSweep(0, getPlayerSweep(player).getFreeDice(), boardId);
			ps.setPersistentState(PersistentState.UPDATE_REQUIRED);
			player.setPlayerShugoSweep(ps);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		}
		
		PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(), getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), getCommonData(player).getResetBoard(), 0));
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It saves the {@code Kisk} object if the player has one.<br>
	 * This ensures the binding is kept while the player is offline.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		DAOManager.getDAO(PlayerShugoSweepDAO.class).store(player);
		player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
	}
	
	/**
	 * Rolls a random die for the specified player.<br>
	 * This method updates the player's current step and manages their dice count.<br>
	 * It also handles reward distribution based on the new position.
	 * @param player The {@link Player} who is rolling the dice.
	 */
	public void launchDice(Player player)
	{
		final int move = Rnd.get(1, 6);
		final int step = getPlayerSweep(player).getStep();
		final int newStep = step + move;
		final int dice = getPlayerSweep(player).getFreeDice();
		final int goldDice = getCommonData(player).getGoldenDice();
		final int diff = newStep - 30;
		if (getPlayerSweep(player).getFreeDice() != 0)
		{
			getPlayerSweep(player).setFreeDice(dice - 1);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		}
		else
		{
			getCommonData(player).setGoldenDice(goldDice - 1);
			DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
		}
		
		PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(boardId, getPlayerSweep(player).getStep(), getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), 0, 0));
		
		if (newStep > 30)
		{
			System.out.println("Step > 30: " + step + " Move: " + move + " NewStep: " + newStep);
			getPlayerSweep(player).setStep(newStep);
			PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(), getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), getCommonData(player).getResetBoard(), move));
			getPlayerSweep(player).setStep(diff);
			rewardPlayer(player, getPlayerSweep(player).getStep(), diff);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		}
		else if (newStep == 30)
		{
			System.out.println("Step = 30: " + step + " Move: " + move + " NewStep: " + newStep);
			getPlayerSweep(player).setStep(newStep);
			PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(), getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), getCommonData(player).getResetBoard(), move));
			rewardPlayer(player, getPlayerSweep(player).getStep(), newStep);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
		}
		else
		{
			System.out.println("Step normal " + step + " Move: " + move + " NewStep: " + newStep);
			getPlayerSweep(player).setStep(newStep);
			player.getPlayerShugoSweep().setShugoSweepByObjId(player.getObjectId());
			PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), getPlayerSweep(player).getStep(), getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), getCommonData(player).getResetBoard(), move));
			rewardPlayer(player, getPlayerSweep(player).getStep(), move);
		}
	}
	
	/**
	 * Resets the current board progress for a specific player.<br>
	 * This method decrements the {@code resetBoard} count in the player's common data.<br>
	 * It also sets the player's current step to {@code 0}.<br>
	 * Finally, it sends an {@link SM_SHUGO_SWEEP} packet to update the client.
	 * @param player The {@link Player} whose board needs to be reset.
	 */
	public void resetBoard(Player player)
	{
		final int reset = getCommonData(player).getResetBoard();
		getCommonData(player).setResetBoard(reset - 1);
		getPlayerSweep(player).setStep(0);
		PacketSendUtility.sendPacket(player, new SM_SHUGO_SWEEP(getPlayerSweep(player).getBoardId(), 0, getPlayerSweep(player).getFreeDice(), getCommonData(player).getGoldenDice(), getCommonData(player).getResetBoard(), 0));
	}
	
	/**
	 * This method grants a reward to a {@link Player}.<br>
	 * It schedules the item delivery based on the current board step.<br>
	 * The delay is calculated using the {@code move} value.
	 * @param player The {@code Player} who will receive the items.
	 * @param step The current progress step on the board.
	 * @param move The number of moves made to determine the delay.
	 */
	private void rewardPlayer(Player player, int step, int move)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (player.isOnline())
				{
					final ShugoSweepReward reward = getRewardForBoard(boardId, step);
					ItemService.addItem(player, reward.getItemId(), reward.getCount());
				}
			}
		}, move * 1200);
	}
	
	/**
	 * Retrieves the common data for a specific player.<br>
	 * This method calls {@code getCommonData} to fetch the information.
	 * @param player The {@code Player} object to retrieve data from.
	 * @return The {@code PlayerCommonData} associated with the provided player.
	 */
	private PlayerCommonData getCommonData(Player player)
	{
		return player.getCommonData();
	}
	
	/**
	 * Retrieves the {@code PlayerSweep} data for a specific player.<br>
	 * This method fetches the current sweep progress from the {@link Player} object.
	 * @param player The {@code Player} whose sweep data is being requested.
	 * @return The {@code PlayerSweep} associated with the given player.
	 */
	private PlayerSweep getPlayerSweep(Player player)
	{
		return player.getPlayerShugoSweep();
	}
	
	/**
	 * Calculates the reward for a specific board and step.<br>
	 * This method retrieves data from {@link DataManager}.
	 * @param boardId The unique identifier for the game board.
	 * @param step The current progress step on the board.
	 * @return The {@code ShugoSweepReward} object corresponding to the input.
	 */
	private static ShugoSweepReward getRewardForBoard(int boardId, int step)
	{
		return DataManager.SHUGO_SWEEP_REWARD_DATA.getRewardBoard(boardId, step);
	}
	
	/**
	 * Provides access to the singleton instance of {@link ShugoSweepService}.<br>
	 * Use this method to get the global service for handling Shugo Sweep events.
	 * @return The single shared instance of {@code ShugoSweepService}.
	 */
	public static ShugoSweepService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ShugoSweepService instance = new ShugoSweepService();
	}
}
