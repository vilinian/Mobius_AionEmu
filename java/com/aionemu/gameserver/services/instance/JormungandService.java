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
package com.aionemu.gameserver.services.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Manages the logic and lifecycle of the Jormungand world event.<br>
 * This service handles instance-specific behaviors for players participating in the event.
 * @author Ever
 */
public class JormungandService
{
	/*
	 * Used to logs information.
	 */
	private static final Logger log = LoggerFactory.getLogger(JormungandService.class);
	
	/**
	 * Entry Level: 65-66
	 */
	public static final byte minlevel = 65, maxlevel = 66;
	
	// Determines whether users can still register for running instance?
	private boolean registerAvailable;
	
	// Determines whether the given player is already registered for instance?
	private final List<Integer> playersWithCooldown = Collections.synchronizedList(new ArrayList<>());
	
	// MaskId for Jormungand Marching Route Battlefield Instance.
	public static final int maskId = 108;
	
	public static final int InstanceMapId = 301210000;
	
	/**
	 * instantiate class
	 */
	private static class SingletonHolder
	{
		protected static final JormungandService instance = new JormungandService();
	}
	
	/**
	 * Retrieves the singleton instance of this service.<br>
	 * Use this method to access the global {@link JormungandService} object.
	 * @return The shared {@code JormungandService} instance.
	 */
	public static JormungandService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Starts the Jormungand service by scheduling registration tasks.<br>
	 * It parses the cron expressions from {@code JORMUNGAND_TIMES}.<br>
	 * Each expression schedules a task to call {@code startRegistration}.<br>
	 * The logs will display the scheduled duration and cron details.
	 */
	public void start()
	{
		final String[] times = AutoGroupConfig.JORMUNGAND_TIMES.split("\\|");
		for (String cron : times)
		{
			CronService.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					startRegistration();
				}
			}, cron);
			log.info("Scheduled Jormungand Marching Route based on cron expression: " + cron + " Duration: " + AutoGroupConfig.JORMUNGAND_TIMER + " in minutes");
		}
	}
	
	/**
	 * Initializes the registration process for the Jormungand Marching Route instance.<br>
	 * Sets {@code registerAvailable} to {@code true}.<br>
	 * Schedules the unregistration task.<br>
	 * Sends an invitation packet to all eligible players in the world.
	 */
	private void startRegistration()
	{
		registerAvailable = true;
		ScheduleUnregistration();
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext())
		{
			final Player player = iter.next();
			if ((player.getLevel() > minlevel) && (player.getLevel() < maxlevel))
			{
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(maskId, SM_AUTO_GROUP.wnd_EntryIcon));
				
				// You can now participate in the Jormungand Marching Route battle.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_OPHIDAN_WAR);
			}
		}
	}
	
	/**
	 * Schedules the unregistration process for the Jormungand Marching Route instance.<br>
	 * This method sets {@code registerAvailable} to {@code false}.<br>
	 * It clears the {@code playersWithCooldown} list and notifies the {@link AutoGroupService}.<br>
	 * It also sends a system packet to all eligible players in the world.
	 */
	private void ScheduleUnregistration()
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				registerAvailable = false;
				playersWithCooldown.clear();
				AutoGroupService.getInstance().unRegisterInstance(maskId);
				final Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext())
				{
					final Player player = iter.next();
					if (player.getLevel() > minlevel)
					{
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(maskId, SM_AUTO_GROUP.wnd_EntryIcon, true));
					}
				}
			}
		}, AutoGroupConfig.JORMUNGAND_TIMER * 60 * 1000);
	}
	
	/**
	 * Returns the minimum level required to participate.<br>
	 * This value corresponds to the {@code minlevel} constant.
	 * @return The minimum level as a {@code byte}.
	 */
	public byte getMinLevel()
	{
		return minlevel;
	}
	
	/**
	 * Retrieves the maximum level allowed for this service.<br>
	 * This value corresponds to the {@code maxlevel} constant.
	 * @return The maximum level as a {@code byte}.
	 */
	public byte getMaxLevel()
	{
		return maxlevel;
	}
	
	/**
	 * Checks if the Jormungand instance is currently open for registration.<br>
	 * This method returns the current state of {@code registerAvailable}.
	 * @return {@code true} if players can join, or {@code false} otherwise.
	 */
	public boolean isJormungandAvailable()
	{
		return registerAvailable;
	}
	
	/**
	 * Adds a {@code Player} to the cooldown list.<br>
	 * This prevents the player from joining the instance again for a period of time.<br>
	 * It uses the unique object ID of the {@link Player}.
	 * @param player The {@code Player} who needs to be put on cooldown.
	 */
	public void addCoolDown(Player player)
	{
		playersWithCooldown.add(player.getObjectId());
	}
	
	/**
	 * Checks if a specific player is currently on cooldown.<br>
	 * This method looks up the {@code Player} in the internal cooldown list.
	 * @param player The {@link Player} to check.
	 * @return {@code true} if the player has a cooldown, otherwise {@code false}.
	 */
	public boolean hasCoolDown(Player player)
	{
		return playersWithCooldown.contains(player.getObjectId());
	}
	
	/**
	 * Displays the auto-group window to a specific player.<br>
	 * This method checks if the {@code Player} is currently on cooldown.<br>
	 * If they are not, it sends an {@link SM_AUTO_GROUP} packet.
	 * @param player The {@code Player} who will receive the window.
	 * @param instanceMaskId The unique identifier for the instance mask.
	 */
	public void showWindow(Player player, byte instanceMaskId)
	{
		if (!playersWithCooldown.contains(player.getObjectId()))
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId));
		}
	}
	
	/**
	 * Checks if the specified {@link Player} is currently inside an instance.<br>
	 * This method calls the {@code isInInstance()} method on the {@code Player} object.
	 * @param player The {@code Player} to check.
	 * @return {@code true} if the player is in an instance, otherwise {@code false}.
	 */
	private boolean isInInstance(Player player)
	{
		return player.isInInstance();
	}
	
	/**
	 * Checks if a player is allowed to join the instance.<br>
	 * It verifies the registration status and level requirements.<br>
	 * It also checks for active cooldowns or current instance presence.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player can join, otherwise {@code false}.
	 */
	public boolean canPlayerJoin(Player player)
	{
		return registerAvailable && (player.getLevel() > minlevel) && (player.getLevel() < maxlevel) && !hasCoolDown(player) && !isInInstance(player);
	}
}
