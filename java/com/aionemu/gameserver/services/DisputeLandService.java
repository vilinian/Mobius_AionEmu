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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DISPUTE_LAND;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneAttributes;

/**
 * This service handles the logic for land disputes within the game world.<br>
 * It manages how players interact with and claim territory.<br>
 * It coordinates actions between {@link Player} objects and {@link World} zones.
 * @author Source
 * @rework Eloann
 */
public class DisputeLandService
{
	private boolean active;
	private final List<Integer> worlds = new ArrayList<>();
	
	/**
	 * Private constructor for the {@link DisputeLandService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * Use {@code getInstance} to access the singleton instance instead.
	 */
	private DisputeLandService()
	{
	}
	
	/**
	 * Retrieves the singleton instance of the {@link DisputeLandService}.<br>
	 * Use this method to access the service from anywhere in the code.
	 * @return The active {@code DisputeLandService} instance.
	 */
	public static DisputeLandService getInstance()
	{
		return DisputeLandServiceHolder.INSTANCE;
	}
	
	/**
	 * Initializes the dispute land system settings.<br>
	 * This method checks if the feature is enabled in {@code CustomConfig}.<br>
	 * It schedules a task to deactivate the service after a set time.<br>
	 * It also populates the list of world IDs for dispute lands.
	 */
	public void initDisputeLand()
	{
		if (CustomConfig.DISPUTE_LAND_ENABLED)
		{
			CronService.getInstance().schedule(new Runnable()
			{
				@Override
				public void run()
				{
					if (isActive())
					{
						ThreadPoolManager.getInstance().schedule(new Runnable()
						{
							
							@Override
							public void run()
							{
								setActive(false);
							}
						}, CustomConfig.DISPUTE_LAND_TIME * 3600 * 1000); // 5 hours
					}
				}
			}, CustomConfig.DISPUTE_LAND_SCHEDULE);
		}
		
		worlds.add(210020000); // Eltnen.
		worlds.add(210040000); // Heiron.
		worlds.add(210050000); // Inggison.
		worlds.add(210060000); // Theobomos.
		worlds.add(220020000); // Morheim.
		worlds.add(220040000); // Beluslan.
		worlds.add(220050000); // Brusthonin.
		worlds.add(220070000); // Gelkmaros.
		
		// 4.7
		worlds.add(600090000); // Kaldor.
		worlds.add(600100000); // Levinshor.
		
		// 4.8
		worlds.add(210070000); // Cygnea.
		worlds.add(220080000); // Enshar.
		
		// 5.0
		worlds.add(210100000); // Iluma.
		worlds.add(220110000); // Norsvold.
	}
	
	/**
	 * Checks if the motion is currently active.<br>
	 * This method returns the current state of the {@code active} field.
	 * @return {@code true} if the motion is active, {@code false} otherwise.
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Sets whether the dispute land service is currently active.<br>
	 * This method updates the internal state and synchronizes it with the world.<br>
	 * It also sends a broadcast to all connected players.
	 * @param value The new status to set for the service. Use {@code true} to enable or {@code false} to disable.
	 */
	public void setActive(boolean value)
	{
		active = value;
		syncState();
		broadcast();
	}
	
	/**
	 * Updates the PVP status for all relevant worlds.<br>
	 * This method checks if {@code active} is {@code true}.<br>
	 * It then enables or disables {@code PVP_ENABLED} based on that state.
	 */
	private void syncState()
	{
		for (int world : worlds)
		{
			if ((world == 210020000) || // Eltnen.
				(world == 210040000) || // Heiron.
				(world == 210050000) || // Inggison.
				(world == 210060000) || // Theobomos.
				(world == 210070000) || // Cygnea.
				(world == 220020000) || // Morheim.
				(world == 220040000) || // Beluslan.
				(world == 220050000) || // Brusthonin.
				(world == 220070000) || // Gelkmaros.
				(world == 220080000) || // Enshar.
				(world == 210100000) || // Iluma.
				(world == 220110000) || // Norsvold.
				(world == 600090000) || // Kaldor.
				(world == 600100000))
			{
				// Levinshor.
				continue;
			}
			
			if (active)
			{
				World.getInstance().getWorldMap(world).setWorldOption(ZoneAttributes.PVP_ENABLED);
			}
			else
			{
				World.getInstance().getWorldMap(world).removeWorldOption(ZoneAttributes.PVP_ENABLED);
			}
		}
	}
	
	/**
	 * Sends the current dispute land status to a specific player.<br>
	 * This method updates the {@code Player} with the latest world list and activity state.
	 * @param player The {@link Player} who will receive the packet.
	 */
	private void broadcast(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_DISPUTE_LAND(worlds, active));
	}
	
	/**
	 * Sends a notification to all players in the world.<br>
	 * This method iterates through every {@link Player} and calls {@code broadcast}.
	 */
	private void broadcast()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				broadcast(player);
			}
		});
	}
	
	/**
	 * Sends a broadcast message to the specific {@code Player}.<br>
	 * This is called when a player successfully logs into the game.
	 * @param player The {@code Player} who is logging in.
	 */
	public void onLogin(Player player)
	{
		broadcast(player);
	}
	
	private static class DisputeLandServiceHolder
	{
		private static final DisputeLandService INSTANCE = new DisputeLandService();
	}
}
