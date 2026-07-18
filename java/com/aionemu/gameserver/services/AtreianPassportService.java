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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.AtreianPassportDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.atreianpassport.AtreianPassportRewards;
import com.aionemu.gameserver.model.templates.atreianpassport.AtreianPassportTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATREIAN_PASSPORT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic for the {@code AtreianPassport} system.<br>
 * This service handles reward distribution and passport interactions for players.<br>
 * It interacts with {@link AtreianPassportDAO} to persist data and {@link ItemService} to grant items.
 * @author Falke_34
 */
public class AtreianPassportService
{
	private static final Logger log = LoggerFactory.getLogger(AtreianPassportService.class);
	private final Map<Integer, AtreianPassportTemplate> pc_basic = new HashMap<>(1);
	public Map<Integer, AtreianPassportTemplate> data = new HashMap<>(1);
	
	/**
	 * Retrieves all passports associated with a specific account.<br>
	 * This method fetches IDs from the {@link AtreianPassportDAO} and maps them to their templates.
	 * @param accountId The unique identifier for the player account.
	 * @return A map where keys are passport IDs and values are {@link AtreianPassportTemplate} objects.
	 */
	public Map<Integer, AtreianPassportTemplate> getPlayerPassports(int accountId)
	{
		final Map<Integer, AtreianPassportTemplate> passports = new HashMap<>();
		final List<Integer> ids = DAOManager.getDAO(AtreianPassportDAO.class).getPassports(accountId);
		for (Integer i : ids)
		{
			passports.put(i, data.get(i));
		}
		
		return passports;
	}
	
	/**
	 * Handles the logic for checking and updating a player's passport status when they log in.<br>
	 * This method verifies if the player has an active passport or needs to be assigned one.<br>
	 * It also checks for daily stamp resets and sends the appropriate server packets to the {@code Player}.
	 * @param player The {@code Player} object who is currently logging into the game.
	 */
	public void onLogin(Player player)
	{
		if (player == null)
		{
			return;
		}
		
		final int passportId = EventsConfig.ATREIAN_PASSPORT_ID;
		final int accountId = player.getPlayerAccount().getId();
		final AtreianPassportDAO dao = DAOManager.getDAO(AtreianPassportDAO.class);
		final Map<Integer, AtreianPassportTemplate> playerPassports = getPlayerPassports(accountId);
		
		// Added reset if all Stamps are received
		if (dao.getStamps(accountId, passportId) == 7)
		{
			dao.updatePassport(accountId, passportId, 0, true, new Timestamp(System.currentTimeMillis() - 86400000L));
		}
		
		if (!playerPassports.containsKey(passportId))
		{
			final Timestamp now = new Timestamp(System.currentTimeMillis() - 86400000L);
			dao.insertPassport(accountId, passportId, 0, now);
			PacketSendUtility.sendPacket(player, new SM_ATREIAN_PASSPORT(passportId, 0, 1, false));
		}
		else
		{
			final int stamps = dao.getStamps(accountId, passportId);
			final Timestamp now2 = new Timestamp(System.currentTimeMillis());
			final Timestamp lastStamp = dao.getLastStamp(accountId, passportId);
			if ((now2.getTime() - lastStamp.getTime()) >= 86400000L)
			{
				DAOManager.getDAO(AtreianPassportDAO.class).updatePassport(accountId, passportId, stamps, false, lastStamp);
				PacketSendUtility.sendPacket(player, new SM_ATREIAN_PASSPORT(passportId, 0, 1, false));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NEW_PASSPORT_AVAIBLE);
			}
			else
			{
				PacketSendUtility.sendPacket(player, new SM_ATREIAN_PASSPORT(passportId, 0, 1, true));
			}
		}
	}
	
	/**
	 * Initializes the {@code AtreianPassportService}.<br>
	 * This method loads all passport templates from the static data.<br>
	 * It logs a message to confirm that the service has started.
	 */
	public void onStart()
	{
		final Map<Integer, AtreianPassportTemplate> raw = DataManager.ATREIAN_PASSPORT_DATA.getAll();
		if (raw.size() != 0)
		{
			getPassports(raw);
		}
		else
		{
			log.warn("[AtreianPassportService] Passports from static data = 0");
		}
		
		log.info("[AtreianPassportService] is initialized...");
	}
	
	/**
	 * Grants rewards to a {@link Player} based on their passport progress.<br>
	 * This method checks if the player is eligible for the next reward.<br>
	 * It updates the database and sends a confirmation packet if successful.
	 * @param player The {@link Player} receiving the reward.
	 * @param passportId The unique identifier of the passport to check.
	 */
	public void getReward(Player player, int passportId)
	{
		final AtreianPassportTemplate atreianPassportRewards = DataManager.ATREIAN_PASSPORT_DATA.getAtreianPassportId(passportId);
		final int accountId = player.getPlayerAccount().getId();
		final AtreianPassportDAO dao = DAOManager.getDAO(AtreianPassportDAO.class);
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(player.getCreationDate());
		final int stamps = dao.getStamps(accountId, passportId);
		for (AtreianPassportRewards component : atreianPassportRewards.getRewards())
		{
			final Timestamp now = new Timestamp(System.currentTimeMillis());
			final Timestamp lastStamp = dao.getLastStamp(accountId, passportId);
			if ((now.getTime() - lastStamp.getTime()) >= 86400000L)
			{
				if (component.getRewardItemNum() == (stamps + 1))
				{
					ItemService.addItem(player, component.getRewardItemId(), component.getRewardItemCount());
					PacketSendUtility.sendPacket(player, new SM_ATREIAN_PASSPORT(passportId, stamps + 1, 1, true));
					DAOManager.getDAO(AtreianPassportDAO.class).updatePassport(accountId, passportId, stamps + 1, true, now);
				}
			}
		}
	}
	
	/**
	 * This method loads passport data into the service.<br>
	 * It populates the {@code data} map with entries from the provided collection.<br>
	 * It then processes each {@link AtreianPassportTemplate} to identify basic passports.
	 * @param raw A map containing the initial passport templates to be loaded.
	 */
	public void getPassports(Map<Integer, AtreianPassportTemplate> raw)
	{
		data.putAll(raw);
		for (AtreianPassportTemplate atp : data.values())
		{
			switch (atp.getAttendType())
			{
				case PC_BASIC:
				{
					getBasicPassports(atp.getId(), atp);
					break;
				}
			}
		}
		
		log.info("[AtreianPassportService] Loaded " + pc_basic.size() + " Basic Passports");
	}
	
	/**
	 * This method saves a basic passport to the internal cache.<br>
	 * It checks if the {@code id} already exists in the map before adding it.<br>
	 * If the {@code id} is missing, it stores the provided {@link AtreianPassportTemplate}.
	 * @param id The unique identifier for the passport.
	 * @param atp The {@link AtreianPassportTemplate} object to store.
	 */
	public void getBasicPassports(int id, AtreianPassportTemplate atp)
	{
		if (pc_basic.containsKey(id))
		{
			return;
		}
		
		pc_basic.put(id, atp);
	}
	
	/**
	 * Provides access to the singleton instance of this service.<br>
	 * Use this method to get the global {@link AtreianPassportService} object.
	 * @return The single shared instance of {@code AtreianPassportService}.
	 */
	public static AtreianPassportService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AtreianPassportService instance = new AtreianPassportService();
	}
}
