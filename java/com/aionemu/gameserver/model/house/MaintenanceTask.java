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
package com.aionemu.gameserver.model.house;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.HousingBidService;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.taskmanager.AbstractCronTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Represents a scheduled maintenance task for the housing system.<br>
 * This class handles periodic background operations related to house management.<br>
 * It extends {@link AbstractCronTask} to execute automated logic at specific intervals.
 * @author Rolandas
 */
public class MaintenanceTask extends AbstractCronTask
{
	private static final Logger log = LoggerFactory.getLogger(MaintenanceTask.class);
	private static final List<House> maintainedHouses;
	private static MaintenanceTask instance;
	
	static
	{
		maintainedHouses = new ArrayList<>();
		try
		{
			instance = new MaintenanceTask(HousingConfig.HOUSE_MAINTENANCE_TIME);
		}
		catch (ParseException pe)
		{
		}
	}
	
	/**
	 * Retrieves the singleton instance of the {@link MaintenanceTask}.<br>
	 * This method provides a global access point to the maintenance task.
	 * @return The single {@code MaintenanceTask} instance.
	 */
	public static MaintenanceTask getInstance()
	{
		return instance;
	}
	
	/**
	 * Creates a new instance of {@link MaintenanceTask}.<br>
	 * It initializes the task with a specific maintenance time.
	 * @param maintainTime The string representing the scheduled maintenance time.
	 * @throws ParseException If the provided time string is invalid.
	 */
	private MaintenanceTask(String maintainTime) throws ParseException
	{
		super(maintainTime);
	}
	
	/**
	 * Calculates the time remaining until the next task execution.<br>
	 * This method returns the delay in milliseconds.<br>
	 * If the run time has already passed, it returns {@code 0}.
	 * @return The number of milliseconds until the task should run.
	 */
	@Override
	protected long getRunDelay()
	{
		final int left = (int) (getRunTime() - (System.currentTimeMillis() / 1000));
		if (left < 0)
		{
			return 0;
		}
		
		return left * 1000;
	}
	
	/**
	 * Returns the configuration key for the server time.<br>
	 * This value is used to identify the maintenance period.
	 * @return a {@code String} representing the variable name.
	 */
	@Override
	protected String getServerTimeVariable()
	{
		return "houseMaintainTime";
	}
	
	/**
	 * Checks if this task is allowed to run during the initial server startup.<br>
	 * This method always returns {@code false}.
	 * @return {@code true} if the task can run on init, otherwise {@code false}.
	 */
	@Override
	protected boolean canRunOnInit()
	{
		return false;
	}
	
	/**
	 * Checks if the current server time is within the maintenance window.<br>
	 * It compares the scheduled run time with the current system time.
	 * @return {@code true} if it is currently maintenance time, {@code false} otherwise.
	 */
	public boolean isMaintainTime()
	{
		return (getRunTime() - (System.currentTimeMillis() / 1000)) <= 0;
	}
	
	/**
	 * Performs initial setup for the house maintenance task.<br>
	 * This method logs the start of the initialization process.
	 */
	@Override
	protected void preInit()
	{
		GameServer.log.info("[HouseService] Initializing House maintenance task...");
	}
	
	/**
	 * Prepares the task before it starts running.<br>
	 * This method calls {@code updateMaintainedHouses} to refresh the house list.<br>
	 * It also logs the number of houses being maintained to the server log.
	 */
	@Override
	protected void preRun()
	{
		updateMaintainedHouses();
		GameServer.log.info("[HouseService] Executing House maintenance. Maintained Houses: " + maintainedHouses.size());
	}
	
	/**
	 * Updates the list of houses that require maintenance.<br>
	 * This method checks all custom houses for unpaid fees.<br>
	 * It resets the fee status and updates the next payment time if necessary.<br>
	 * The process only runs if {@code HousingConfig.ENABLE_HOUSE_PAY} is {@code true}.
	 */
	private void updateMaintainedHouses()
	{
		maintainedHouses.clear();
		
		if (!HousingConfig.ENABLE_HOUSE_PAY)
		{
			return;
		}
		
		final Date now = new Date();
		final List<House> houses = HousingService.getInstance().getCustomHouses();
		for (House house : houses)
		{
			if ((house.getStatus() == HouseStatus.INACTIVE) || (house.getOwnerId() == 0))
			{
				continue;
			}
			
			if (house.isFeePaid())
			{
				if ((house.getNextPay() == null) || house.getNextPay().before(now))
				{
					house.setFeePaid(false);
					
					// if never paid, just set time to the next period
					if (house.getNextPay() == null)
					{
						house.setNextPay(new Timestamp((long) getRunTime() * 1000));
					}
					
					house.save();
				}
				else
				{
					continue;
				}
			}
			
			maintainedHouses.add(house);
		}
	}
	
	/**
	 * Performs the periodic house maintenance check.<br>
	 * This method identifies houses with overdue payments and sends warnings to owners.<br>
	 * It also handles auctioning houses that have missed multiple payment periods.
	 */
	@Override
	protected void executeTask()
	{
		if (!HousingConfig.ENABLE_HOUSE_PAY)
		{
			return;
		}
		
		// Get times based on configuration values
		final ZonedDateTime now = ZonedDateTime.now();
		final ZonedDateTime previousRun = now.minus(java.time.Duration.ofMillis(getPeriod())); // usually week ago
		final ZonedDateTime beforePreviousRun = previousRun.minus(java.time.Duration.ofMillis(getPeriod())); // usually two weeks ago
		
		for (House house : maintainedHouses)
		{
			if (house.isFeePaid())
			{
				continue; // player already paid, don't check
			}
			
			final long payTime = house.getNextPay().getTime();
			long impoundTime = 0;
			int warnCount = 0;
			
			PlayerCommonData pcd = null;
			final Player player = World.getInstance().findPlayer(house.getOwnerId());
			if (player == null)
			{
				pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(house.getOwnerId());
			}
			else
			{
				pcd = player.getCommonData();
			}
			
			if (pcd == null)
			{
				// player doesn't exist already for some reasons
				log.warn("[HouseService] House " + house.getAddress().getId() + " had player assigned but no player exists. Auctioned.");
				putHouseToAuction(house, null);
				continue;
			}
			
			if (payTime <= beforePreviousRun.toInstant().toEpochMilli())
			{
				final ZonedDateTime plusDay = beforePreviousRun.minusDays(1);
				if (payTime <= plusDay.toInstant().toEpochMilli())
				{
					// player didn't pay after the second warning and one day passed
					impoundTime = now.toInstant().toEpochMilli();
					warnCount = 3;
					putHouseToAuction(house, pcd);
				}
				else
				{
					impoundTime = now.plusDays(1).toInstant().toEpochMilli();
					warnCount = 2;
				}
			}
			else if (payTime <= previousRun.toInstant().toEpochMilli())
			{
				// player did't pay 1 period
				impoundTime = now.plus(java.time.Duration.ofMillis(getPeriod())).plusDays(1).toInstant().toEpochMilli();
				warnCount = 1;
			}
			else
			{
				continue; // should not happen
			}
			
			if (pcd.isOnline())
			{
				if (warnCount == 3)
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_SEQUESTRATE);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OVERDUE);
				}
			}
			
			MailFormatter.sendHouseMaintenanceMail(house, warnCount, impoundTime);
		}
	}
	
	/**
	 * Removes the owner from a {@code House} and adds it to the auction.<br>
	 * Updates the player's house registry if they are currently online.<br>
	 * Sends relevant packets to notify the player of the change.
	 * @param house The {@code House} object that is being put into auction.
	 * @param playerCommonData The data for the player who previously owned the house.
	 */
	private void putHouseToAuction(House house, PlayerCommonData playerCommonData)
	{
		house.revokeOwner();
		HousingBidService.getInstance().addHouseToAuction(house);
		house.save();
		log.info("[HouseService] House " + house.getAddress().getId() + " overdued and put to auction.");
		if (playerCommonData == null)
		{
			return;
		}
		
		if (playerCommonData.isOnline())
		{
			final Player player = playerCommonData.getPlayer();
			player.getHouses().remove(house);
			player.setHouseRegistry(null);
			
			// TODO: check this
			PacketSendUtility.sendPacket(player, new SM_HOUSE_ACQUIRE(player.getObjectId(), house.getAddress().getId(), false));
			PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, null));
		}
	}
}
