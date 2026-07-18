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
 * Manages the logic and mechanics for the Pandaemonium Battlefield instance.<br>
 * This service handles specific game rules, events, and player interactions within this zone.
 * @author Falke_34
 */
public class PandaemoniumBattlefieldService
{
	/*
	 * Used to logs information.
	 */
	private static final Logger log = LoggerFactory.getLogger(PandaemoniumBattlefieldService.class);
	
	/**
	 * Entry Level: 66-75
	 */
	private static final byte minlevel = 66, maxlevel = 76;
	
	// Determines whether users can still register for running instance?
	private boolean registerAvailable;
	
	// Determines whether the given player is already registered for instance?
	private final List<Integer> playersWithCooldown = Collections.synchronizedList(new ArrayList<>());
	
	// MaskId for Pandaemonium Battlefield Instance.
	public static final int maskId = 417;
	
	public static final int InstanceMapId = 302300000;
	
	/**
	 * instantiate class
	 */
	private static class SingletonHolder
	{
		protected static final PandaemoniumBattlefieldService instance = new PandaemoniumBattlefieldService();
	}
	
	/**
	 * Retrieves the single global instance of this service.<br>
	 * This method uses the {@code SingletonHolder} to provide access.<br>
	 * Use this to interact with the {@link PandaemoniumBattlefieldService}.
	 * @return The singleton instance of {@code PandaemoniumBattlefieldService}.
	 */
	public static PandaemoniumBattlefieldService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Starts the Pandaemonium Battlefield service.<br>
	 * This method parses the cron expressions from {@link AutoGroupConfig}.<br>
	 * It schedules the {@code startRegistration} task for each defined time.<br>
	 * Logs the scheduled times and durations to the server log.
	 */
	public void start()
	{
		final String[] times = AutoGroupConfig.PANDAEMONIUMBATTLEFIELD_TIMES.split("\\|");
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
			log.info("Scheduled Pandaemonium Battlefield Instance based on cron expression: " + cron + " Duration: " + AutoGroupConfig.PANDAEMONIUMBATTLEFIELD_TIMER + " in minutes");
		}
	}
	
	/**
	 * Initializes the registration process for the Pandaemonium Battlefield instance.<br>
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
				if (!isInInstance(player))
				{
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(maskId, SM_AUTO_GROUP.wnd_EntryIcon));
					
					// You can now participate in the Pandaemonium Battlefield battle.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDLDF5_Fortress_Re);
				}
			}
		}
	}
	
	/**
	 * Schedules the unregistration process for the Pandaemonium Battlefield instance.<br>
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
		}, AutoGroupConfig.PANDAEMONIUMBATTLEFIELD_TIMER * 60 * 1000);
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
	 * Checks if the Pandaemonium Battlefield is currently open for registration.<br>
	 * This method returns the current state of {@code registerAvailable}.
	 * @return {@code true} if players can join, or {@code false} otherwise.
	 */
	public boolean isPandaemoniumBattlefieldAvailable()
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
