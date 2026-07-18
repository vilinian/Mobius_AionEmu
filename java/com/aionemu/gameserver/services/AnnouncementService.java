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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AnnouncementsDAO;
import com.aionemu.gameserver.model.Announcement;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the automatic announcement system for the game server.<br>
 * This service handles the scheduling and broadcasting of {@link Announcement} messages to players.<br>
 * It ensures that notifications are sent correctly across the {@link World}.
 * @author Divinity
 */
public class AnnouncementService
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);
	private Collection<Announcement> announcements;
	private final List<Future<?>> delays = new ArrayList<>();
	
	/**
	 * Private constructor for the {@link AnnouncementService} class.<br>
	 * This constructor is not intended to be called directly.<br>
	 * It initializes the service by calling the {@code load} method.
	 */
	private AnnouncementService()
	{
		load();
	}
	
	/**
	 * Retrieves the single instance of the {@link AnnouncementService}.<br>
	 * This method follows the singleton pattern.
	 * @return The global {@code AnnouncementService} instance.
	 */
	public static AnnouncementService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Reloads the announcement data.<br>
	 * This method cancels all pending tasks in {@code delays}.<br>
	 * It clears the current {@code announcements} list.<br>
	 * Finally, it calls {@code load} to refresh the data.
	 */
	public void reload()
	{
		// Cancel all tasks
		if ((delays != null) && (delays.size() > 0))
		{
			for (Future<?> delay : delays)
			{
				delay.cancel(false);
			}
		}
		
		// Clear all announcements
		announcements.clear();
		
		// And load again all announcements
		load();
	}
	
	/**
	 * Loads all announcements from the database into memory.<br>
	 * Schedules each announcement to be sent to players based on its delay.<br>
	 * Uses {@link ThreadPoolManager} to handle the repeating tasks.
	 */
	private void load()
	{
		announcements = ConcurrentHashMap.newKeySet();
		announcements.addAll(getDAO().getAnnouncements());
		
		for (Announcement announce : announcements)
		{
			delays.add(ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				final Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext())
				{
					final Player player = iter.next();
					
					if (announce.getFaction().equalsIgnoreCase("ALL"))
					{
						if ((announce.getChatType() == ChatType.SHOUT) || (announce.getChatType() == ChatType.GROUP_LEADER))
						{
							PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, "Announcement", announce.getAnnounce(), announce.getChatType()));
						}
						else
						{
							PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, "Announcement", "Announcement: " + announce.getAnnounce(), announce.getChatType()));
						}
					}
					else if (announce.getFactionEnum() == player.getRace())
					{
						if ((announce.getChatType() == ChatType.SHOUT) || (announce.getChatType() == ChatType.GROUP_LEADER))
						{
							PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, (announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement", announce.getAnnounce(), announce.getChatType()));
						}
						else
						{
							PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, (announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement", (announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement: " + announce.getAnnounce(), announce.getChatType()));
						}
					}
				}
			}, announce.getDelay() * 1000, announce.getDelay() * 1000));
		}
		
		log.info("[AnnouncementService] Loaded " + announcements.size() + " announcements");
	}
	
	/**
	 * Adds a new announcement to the database.<br>
	 * This method saves the data provided in the {@code Announcement} object.<br>
	 * It uses the {@link DB} class to perform the insert operation.
	 * @param announce The {@code Announcement} object containing the data to save.
	 */
	public void addAnnouncement(Announcement announce)
	{
		getDAO().addAnnouncement(announce);
	}
	
	/**
	 * Deletes an announcement from the database.<br>
	 * This method uses the provided {@code idAnnounce} to locate the record.<br>
	 * It returns {@code true} if the operation was successful.
	 * @param idAnnounce The unique identifier of the announcement to remove.
	 * @return {@code true} if the deletion succeeded, otherwise {@code false}.
	 */
	public boolean delAnnouncement(int idAnnounce)
	{
		return getDAO().delAnnouncement(idAnnounce);
	}
	
	/**
	 * Retrieves all announcements from the database.<br>
	 * The results are ordered by their {@code id}.
	 * @return A {@link Set} containing all {@link Announcement} objects.
	 */
	public Set<Announcement> getAnnouncements()
	{
		return getDAO().getAnnouncements();
	}
	
	/**
	 * Retrieves the database access object for announcements.<br>
	 * This method uses {@link DAOManager} to fetch the correct instance.
	 * @return The {@code AnnouncementsDAO} instance used for database operations.
	 */
	private AnnouncementsDAO getDAO()
	{
		return DAOManager.getDAO(AnnouncementsDAO.class);
	}
	
	private static class SingletonHolder
	{
		protected static final AnnouncementService instance = new AnnouncementService();
	}
}
