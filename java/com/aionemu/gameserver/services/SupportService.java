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

import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.Support;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * This service manages the support system for players within the game world.<br>
 * It handles requests and interactions related to {@link Support} objects.<br>
 * Use this class to coordinate support-related logic across the server.
 * @author ThunderBolt
 */
public class SupportService
{
	private static final Logger log = LoggerFactory.getLogger(SupportService.class);
	Queue<Support> supports = new ConcurrentLinkedQueue<>();
	Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	
	/**
	 * Private constructor for the {@link SupportService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It ensures that only one instance is used via the {@code getInstance()} method.
	 */
	private SupportService()
	{
		log.info("SupportService initialized");
	}
	
	/**
	 * Adds a new {@link Support} ticket to the queue.<br>
	 * This method registers the owner and notifies all online GM players.<br>
	 * It ensures that duplicate owners are not processed.
	 * @param support The {@code Support} object to be added.
	 */
	public void addTicket(Support support)
	{
		if (!players.add(support.getOwner()))
		{
			return;
		}
		
		supports.add(support);
		
		for (Player player : World.getInstance().getAllPlayers())
		{
			if (!player.isGM())
			{
				continue;
			}
			
			if (player.getFriendList().getStatus() == Status.ONLINE)
			{
				PacketSendUtility.sendSys2Message(player, "Ticket", "New support from " + support.getOwner().getName() + "!");
			}
		}
	}
	
	/**
	 * Retrieves and removes the next available {@link Support} ticket from the queue.<br>
	 * This method ensures that the owner of the ticket is currently online.<br>
	 * If no valid tickets are found, it returns {@code null}.
	 * @return The next available {@code Support} object or {@code null} if none exist.
	 */
	public Support getTicket()
	{
		Support support = supports.poll();
		if (support == null)
		{
			return null;
		}
		
		players.remove(support.getOwner());
		
		while (!support.getOwner().isOnline())
		{
			support = supports.poll();
			players.remove(support.getOwner());
		}
		
		return support;
	}
	
	/**
	 * Retrieves the first {@code Support} object from the queue.<br>
	 * This method does not remove the item from the list.<br>
	 * It returns {@code null} if the queue is empty.
	 * @return The first {@code Support} object, or {@code null} if none exist.
	 */
	public Support peek()
	{
		return supports.peek();
	}
	
	/**
	 * Checks if a specific {@link Player} currently has an active ticket.<br>
	 * This method looks up the player in the internal collection.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player has a ticket, otherwise {@code false}.
	 */
	public boolean hasTicket(Player player)
	{
		return players.contains(player);
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out of the game.<br>
	 * This method schedules a background task to clean up resources.<br>
	 * It ensures that logout processing does not block the main thread.
	 * @param player The {@code Player} object who is logging out.
	 */
	public void onPlayerLogout(Player player)
	{
		// We just need to cleanup our own mess, so we can run this parallel to the logout thread
		ThreadPoolManager.getInstance().schedule(new LogoutWorker(player), 0);
	}
	
	private static class SingletonHolder
	{
		protected static final SupportService instance = new SupportService();
	}
	
	/**
	 * Provides access to the global instance of {@link SupportService}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code SupportService}.
	 */
	public static SupportService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private class LogoutWorker implements Runnable
	{
		private final Player player;
		
		public LogoutWorker(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			players.remove(player);
			final Iterator<Support> it = supports.iterator();
			
			while (it.hasNext())
			{
				final Support support = it.next();
				
				if (support.getOwner() == player)
				{
					it.remove();
				}
			}
		}
	}
}
