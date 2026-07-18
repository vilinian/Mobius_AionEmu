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
package com.aionemu.gameserver.services;

import java.util.Calendar;
import java.util.concurrent.Future;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CAPTCHA;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * This service handles the logic for applying and managing various player penalties.<br>
 * It manages actions such as temporary bans, mutes, and other restrictions on {@link Player} accounts.
 * @author lord_rex, Cura, nrg
 */
public class PunishmentService
{
	/**
	 * Removes a character ban from the database.<br>
	 * This method uses {@link PlayerPunishmentsDAO} to clear the punishment.
	 * @param playerId The unique ID of the player to unban.
	 */
	public static void unbanChar(int playerId)
	{
		DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(playerId, PunishmentType.CHARBAN);
	}
	
	/**
	 * Bans a character from the game.<br>
	 * This method updates the database and kicks the player if they are online.
	 * @param playerId The unique ID of the player to ban.
	 * @param dayCount The number of days for the ban duration.
	 * @param reason The explanation for why the character was banned.
	 */
	public static void banChar(int playerId, int dayCount, String reason)
	{
		DAOManager.getDAO(PlayerPunishmentsDAO.class).punishPlayer(playerId, PunishmentType.CHARBAN, calculateDuration(dayCount), reason);
		
		// if player is online - kick him
		final Player player = World.getInstance().findPlayer(playerId);
		if (player != null)
		{
			player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
		}
	}
	
	/**
	 * Calculates the duration in seconds for a given number of days.<br>
	 * It uses {@code Calendar} to determine the future timestamp.<br>
	 * If {@code dayCount} is 0, it returns {@code Integer.MAX_VALUE}.
	 * @param dayCount The number of days to add to the current time.
	 * @return The total duration in seconds as a {@code long}.
	 */
	public static long calculateDuration(int dayCount)
	{
		if (dayCount == 0)
		{
			return Integer.MAX_VALUE; // int because client handles this with seconds timestamp in int
		}
		
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, +dayCount);
		
		return ((cal.getTimeInMillis() - System.currentTimeMillis()) / 1000);
	}
	
	/**
	 * Sets the prison status for a specific {@link Player}.<br>
	 * This method handles teleporting, timer scheduling, and database updates.<br>
	 * Use {@code true} to imprison the player and {@code false} to release them.
	 * @param player The {@link Player} object to modify.
	 * @param state The new prison status to apply.
	 * @param delayInMinutes The duration of the prison stay in minutes.
	 * @param reason The reason for the punishment.
	 */
	public static void setIsInPrison(Player player, boolean state, long delayInMinutes, String reason)
	{
		stopPrisonTask(player, false);
		if (state)
		{
			long prisonTimer = player.getPrisonTimer();
			if (delayInMinutes > 0)
			{
				prisonTimer = delayInMinutes * 60000L;
				schedulePrisonTask(player, prisonTimer);
				PacketSendUtility.sendMessage(player, "You have been teleported to prison for a time of " + delayInMinutes + " minutes.\n If you disconnect the time stops and the timer of the prison'll see at your next login.");
			}
			
			player.setStartPrison(System.currentTimeMillis());
			TeleportService2.teleportToPrison(player);
			DAOManager.getDAO(PlayerPunishmentsDAO.class).punishPlayer(player, PunishmentType.PRISON, reason);
		}
		else
		{
			PacketSendUtility.sendMessage(player, "You come out of prison.");
			
			player.setPrisonTimer(0);
			
			TeleportService2.moveToBindLocation(player, true);
			
			DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(player.getObjectId(), PunishmentType.PRISON);
		}
	}
	
	/**
	 * Stops the prison task for a specific {@link Player}.<br>
	 * If {@code save} is {@code true}, it preserves the current timer.<br>
	 * It cancels the {@code TaskId.PRISON} if it exists.
	 * @param player The {@link Player} whose prison task should be stopped.
	 * @param save Whether to save the remaining prison time before stopping.
	 */
	public static void stopPrisonTask(Player player, boolean save)
	{
		final Future<?> prisonTask = player.getController().getTask(TaskId.PRISON);
		if (prisonTask != null)
		{
			if (save)
			{
				long delay = player.getPrisonTimer();
				if (delay < 0)
				{
					delay = 0;
				}
				
				player.setPrisonTimer(delay);
			}
			
			player.getController().cancelTask(TaskId.PRISON);
		}
	}
	
	/**
	 * Updates the current status of a player who is in prison.<br>
	 * This method checks if the {@code Player} is currently imprisoned.<br>
	 * It sends a message to the player regarding their remaining time.<br>
	 * If the player is not in the correct map, it schedules a teleport to the prison area.
	 * @param player The {@code Player} object to update.
	 */
	public static void updatePrisonStatus(Player player)
	{
		if (player.isInPrison())
		{
			final long prisonTimer = player.getPrisonTimer();
			if (prisonTimer > 0)
			{
				schedulePrisonTask(player, prisonTimer);
				int timeInPrison = (int) (prisonTimer / 60000);
				
				if (timeInPrison <= 0)
				{
					timeInPrison = 1;
				}
				
				PacketSendUtility.sendMessage(player, "You are still in prison for " + timeInPrison + " minute" + (timeInPrison > 1 ? "s" : "") + ".");
				
				player.setStartPrison(System.currentTimeMillis());
			}
			
			if ((player.getWorldId() != WorldMapType.DF_PRISON.getId()) && (player.getWorldId() != WorldMapType.LF_PRISON.getId()))
			{
				PacketSendUtility.sendMessage(player, "You will be teleported to prison in one minute!");
				ThreadPoolManager.getInstance().schedule(() -> TeleportService2.teleportToPrison(player), 60000);
			}
		}
	}
	
	/**
	 * Schedules a task to release a player from prison.<br>
	 * It sets the {@code prisonTimer} on the {@link Player}.<br>
	 * The task calls {@code boolean, long, String)} after the delay.
	 * @param player The {@link Player} who is currently in prison.
	 * @param prisonTimer The amount of time in milliseconds until the release occurs.
	 */
	private static void schedulePrisonTask(Player player, long prisonTimer)
	{
		player.setPrisonTimer(prisonTimer);
		player.getController().addTask(TaskId.PRISON, ThreadPoolManager.getInstance().schedule(() -> setIsInPrison(player, false, 0, ""), prisonTimer));
	}
	
	/**
	 * Updates the gatherable status for a specific {@link Player}.<br>
	 * This method handles both enabling and disabling gathering restrictions.<br>
	 * It manages captcha requirements and interacts with {@code stopGatherableTask}.
	 * @param player The {@code Player} object to modify.
	 * @param captchaCount The current number of captchas completed by the player.
	 * @param state The new status to set for the gatherable flag.
	 * @param delay The time in milliseconds to set for the gathering timer.
	 */
	public static void setIsNotGatherable(Player player, int captchaCount, boolean state, long delay)
	{
		stopGatherableTask(player, false);
		
		if (state)
		{
			if (captchaCount < 3)
			{
				PacketSendUtility.sendPacket(player, new SM_CAPTCHA(captchaCount + 1, player.getCaptchaImage()));
			}
			else
			{
				player.setCaptchaWord(null);
				player.setCaptchaImage(null);
			}
			
			player.setGatherableTimer(delay);
			player.setStopGatherable(System.currentTimeMillis());
			scheduleGatherableTask(player, delay);
			DAOManager.getDAO(PlayerPunishmentsDAO.class).punishPlayer(player, PunishmentType.GATHER, "Possible gatherbot");
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400269));
			player.setCaptchaWord(null);
			player.setCaptchaImage(null);
			player.setGatherableTimer(0);
			player.setStopGatherable(0);
			DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(player.getObjectId(), PunishmentType.GATHER);
		}
	}
	
	/**
	 * Stops the gatherable task for a specific {@link Player}.<br>
	 * This method cancels the active task associated with {@code TaskId.GATHERABLE}.<br>
	 * If {@code save} is {@code true}, it preserves the current timer value.
	 * @param player The {@link Player} whose task needs to be stopped.
	 * @param save A boolean indicating whether to save the current timer before canceling.
	 */
	public static void stopGatherableTask(Player player, boolean save)
	{
		final Future<?> gatherableTask = player.getController().getTask(TaskId.GATHERABLE);
		
		if (gatherableTask != null)
		{
			if (save)
			{
				long delay = player.getGatherableTimer();
				if (delay < 0)
				{
					delay = 0;
				}
				
				player.setGatherableTimer(delay);
			}
			
			player.getController().cancelTask(TaskId.GATHERABLE);
		}
	}
	
	/**
	 * Updates the gatherable status for a specific player.<br>
	 * This method checks if the {@link Player} is currently not gatherable.<br>
	 * It schedules a task if a valid timer exists.
	 * @param player The {@code Player} object to update.
	 */
	public static void updateGatherableStatus(Player player)
	{
		if (player.isNotGatherable())
		{
			final long gatherableTimer = player.getGatherableTimer();
			
			if (gatherableTimer > 0)
			{
				scheduleGatherableTask(player, gatherableTimer);
				player.setStopGatherable(System.currentTimeMillis());
			}
		}
	}
	
	/**
	 * Schedules a task to update the player's gatherable status.<br>
	 * This method sets the {@code gatherableTimer} on the {@link Player}.<br>
	 * It uses {@link ThreadPoolManager} to run {@code setIsNotGatherable} after the delay.
	 * @param player The {@link Player} who will have their task scheduled.
	 * @param gatherableTimer The time in milliseconds to wait before executing the task.
	 */
	private static void scheduleGatherableTask(Player player, long gatherableTimer)
	{
		player.setGatherableTimer(gatherableTimer);
		player.getController().addTask(TaskId.GATHERABLE, ThreadPoolManager.getInstance().schedule(() -> setIsNotGatherable(player, 0, false, 0), gatherableTimer));
	}
	
	/**
	 * PunishmentType
	 * @author Cura
	 */
	public enum PunishmentType
	{
		PRISON,
		GATHER,
		CHARBAN
	}
}
