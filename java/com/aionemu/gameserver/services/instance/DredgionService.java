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
 * Manages the logic and mechanics for the Dredgion world instance.<br>
 * This service handles specific events and interactions unique to this game area.
 * @author xTz
 * @author GiGatR00n v4.7.5.x
 */
public class DredgionService
{
	private static final Logger log = LoggerFactory.getLogger(DredgionService.class);
	private boolean registerAvailable;
	private final List<Integer> playersWithCooldown = Collections.synchronizedList(new ArrayList<>());
	private final SM_AUTO_GROUP[] autoGroupUnreg, autoGroupReg;
	private final byte maskLvlGradeC = 1, maskLvlGradeB = 2, maskLvlGradeA = 3;
	public static final byte minlevel = 45, maxlevel = 61;
	
	/*
	 * instantiate class
	 */
	private static class SingletonHolder
	{
		protected static final DredgionService instance = new DredgionService();
	}
	
	/**
	 * Retrieves the singleton instance of {@link DredgionService}.<br>
	 * This method provides a global access point to the service.<br>
	 * Use this instead of creating a new object manually.
	 * @return The single shared instance of {@code DredgionService}.
	 */
	public static DredgionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Initializes a new instance of the {@link DredgionService}.<br>
	 * This constructor sets up the internal arrays for auto group registration.<br>
	 * It prepares the data structures needed for managing dungeon instances.
	 */
	public DredgionService()
	{
		autoGroupUnreg = new SM_AUTO_GROUP[maskLvlGradeA + 1];
		autoGroupReg = new SM_AUTO_GROUP[autoGroupUnreg.length];
		for (byte i = maskLvlGradeC; i <= maskLvlGradeA; i++)
		{
			autoGroupUnreg[i] = new SM_AUTO_GROUP(i, SM_AUTO_GROUP.wnd_EntryIcon, true);
			autoGroupReg[i] = new SM_AUTO_GROUP(i, SM_AUTO_GROUP.wnd_EntryIcon);
		}
	}
	
	/**
	 * Starts the Dredgion service tasks.<br>
	 * This method parses the cron expressions from {@link AutoGroupConfig}.<br>
	 * It schedules the registration task using {@link CronService}.<br>
	 * Each scheduled task will trigger {@code startDredgionRegistration}.
	 */
	public void start()
	{
		final String[] times = AutoGroupConfig.DREDGION_TIMES.split("\\|");
		for (String cron : times)
		{
			CronService.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					startDredgionRegistration();
				}
			}, cron);
			log.info("Scheduled Dredgion: based on cron expression: " + cron + " Duration: " + AutoGroupConfig.DREDGION_TIMER + " in minutes");
		}
	}
	
	/**
	 * Schedules a task to handle the unregistration of Dredgion instances.<br>
	 * This method resets {@code registerAvailable} and clears player cooldowns.<br>
	 * It also sends an unregister packet to all eligible players in the world.
	 */
	private void startUregisterDredgionTask()
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				registerAvailable = false;
				playersWithCooldown.clear();
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeA);
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeB);
				AutoGroupService.getInstance().unRegisterInstance(maskLvlGradeC);
				final Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext())
				{
					final Player player = iter.next();
					if (player.getLevel() > minlevel)
					{
						final int instanceMaskId = getInstanceMaskId(player);
						if (instanceMaskId > 0)
						{
							PacketSendUtility.sendPacket(player, autoGroupUnreg[instanceMaskId]);
						}
					}
				}
			}
		}, AutoGroupConfig.DREDGION_TIMER * 60 * 1000);
	}
	
	/**
	 * Initializes the registration process for Dredgion.<br>
	 * Sets {@code registerAvailable} to {@code true}.<br>
	 * Starts the unregistration task.<br>
	 * Sends registration packets to eligible players in the world.
	 */
	private void startDredgionRegistration()
	{
		registerAvailable = true;
		startUregisterDredgionTask();
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext())
		{
			final Player player = iter.next();
			if ((player.getLevel() > minlevel) && (player.getLevel() < maxlevel))
			{
				final int instanceMaskId = getInstanceMaskId(player);
				if (instanceMaskId > 0)
				{
					PacketSendUtility.sendPacket(player, autoGroupReg[instanceMaskId]);
					switch (instanceMaskId)
					{
						case maskLvlGradeC:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
							break;
						case maskLvlGradeB:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDDREADGION_02);
							break;
						case maskLvlGradeA:
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDDREADGION_03);
							break;
					}
				}
			}
		}
	}
	
	/**
	 * Checks if the Dredgion registration is currently active.<br>
	 * This method returns the current state of {@code registerAvailable}.
	 * @return {@code true} if players can join, or {@code false} otherwise.
	 */
	public boolean isDredgionAvailable()
	{
		return registerAvailable;
	}
	
	/**
	 * Gets the instance mask ID for a specific {@link Player}.<br>
	 * This value is determined based on the player's current level.<br>
	 * It returns {@code 0} if the level is outside the allowed range.
	 * @param player The {@code Player} object to check.
	 * @return The corresponding mask ID as a {@code byte}.
	 */
	public byte getInstanceMaskId(Player player)
	{
		final int level = player.getLevel();
		if ((level < minlevel) || (level >= maxlevel))
		{
			return 0;
		}
		
		if (level < 51)
		{
			return maskLvlGradeC;
		}
		else if (level < 56)
		{
			return maskLvlGradeB;
		}
		else
		{
			return maskLvlGradeA;
		}
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
	 * This method checks if the {@code instanceMaskId} matches the player's current instance.<br>
	 * It sends an {@link SM_AUTO_GROUP} packet only if the player is not on cooldown.
	 * @param player The {@link Player} who will receive the window.
	 * @param instanceMaskId The unique identifier for the instance mask to display.
	 */
	public void showWindow(Player player, int instanceMaskId)
	{
		if (getInstanceMaskId(player) != instanceMaskId)
		{
			return;
		}
		
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
