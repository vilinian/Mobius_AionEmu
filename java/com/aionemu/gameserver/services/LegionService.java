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

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.DeniedStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.model.team.legion.LegionHistoryType;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.team.legion.LegionRank;
import com.aionemu.gameserver.model.team.legion.LegionTerritory;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ICON_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_ADD_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_ANSWER_JOIN_REQUEST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_JOIN_REQUEST_FROM_PLAYER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_LEAVE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_MEMBERLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_SEARCH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_SEND_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_SEND_EMBLEM_DATA;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_TABS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_SELF_INTRO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_TITLE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_LEGION_JOIN_REQUEST_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.collections.ListSplitter;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.container.LegionContainer;
import com.aionemu.gameserver.world.container.LegionMemberContainer;

/**
 * This class handles all operations related to loading and storing {@link Legion} data and its members.<br>
 * It manages the lifecycle of legion information within the game server.
 * @author Simple modified by cura, Source, CoolyT
 */
public class LegionService
{
	private static final Logger log = LoggerFactory.getLogger(LegionService.class);
	private final LegionContainer allCachedLegions = new LegionContainer();
	private final LegionMemberContainer allCachedLegionMembers = new LegionMemberContainer();
	private final World world;
	public final static int LEGION_ACTION_KICK = 4;
	/**
	 * Legion Permission variables
	 */
	private static final int MAX_LEGION_LEVEL = 8;
	/**
	 * Legion ranking system
	 */
	private Map<Integer, Integer> legionRanking;
	/**
	 * Legion Restrictions
	 */
	private final LegionRestrictions legionRestrictions = new LegionRestrictions();
	
	/**
	 * Retrieves the singleton instance of the {@link LegionService}.<br>
	 * This method provides a global access point to the service.
	 * @return The active {@code LegionService} instance.
	 */
	public static LegionService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Initializes a new instance of the {@link LegionService}.<br>
	 * This constructor sets up the required world reference.
	 */
	public LegionService()
	{
		world = World.getInstance();
	}
	
	/**
	 * Checks if a given name follows the required format.<br>
	 * This method uses the pattern defined in {@link LegionConfig}.
	 * @param name The string to validate.
	 * @return {@code true} if the name is valid, otherwise {@code false}.
	 */
	public boolean isValidName(String name)
	{
		return LegionConfig.LEGION_NAME_PATTERN.matcher(name).matches();
	}
	
	/**
	 * Saves the {@code Legion} data to the database.<br>
	 * It handles different logic based on whether it is a new entry.<br>
	 * If {@code newLegion} is {@code true}, it adds the legion to the cache and saves it as new.<br>
	 * Otherwise, it updates the existing legion and its emblem.
	 * @param legion The {@code Legion} object to be stored.
	 * @param newLegion A boolean flag indicating if this is a new legion creation.
	 */
	private void storeLegion(Legion legion, boolean newLegion)
	{
		if (newLegion)
		{
			addCachedLegion(legion);
			DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
		}
		else
		{
			DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
			DAOManager.getDAO(LegionDAO.class).storeLegionEmblem(legion.getLegionId(), legion.getLegionEmblem());
		}
	}
	
	/**
	 * Saves the provided {@code Legion} object to the database.<br>
	 * This method updates existing data rather than creating a new entry.
	 * @param legion The {@code Legion} object to be stored.
	 */
	private void storeLegion(Legion legion)
	{
		storeLegion(legion, false);
	}
	
	/**
	 * Saves a {@code LegionMember} to the database and cache.<br>
	 * It checks if the member is new to decide which save method to use.
	 * @param legionMember The {@code LegionMember} object to store.
	 * @param newMember Set to {@code true} if this is a new member, otherwise {@code false}.
	 */
	private void storeLegionMember(LegionMember legionMember, boolean newMember)
	{
		if (newMember)
		{
			addCachedLegionMember(legionMember);
			DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(legionMember);
		}
		else
		{
			DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
		}
	}
	
	/**
	 * Saves the details of a {@link LegionMember} to the database.<br>
	 * This method updates existing records rather than creating new ones.
	 * @param legionMember The {@code LegionMember} object to be stored.
	 */
	private void storeLegionMember(LegionMember legionMember)
	{
		storeLegionMember(legionMember, false);
	}
	
	/**
	 * Updates the cache with the latest data for a {@link Player}.<br>
	 * It synchronizes information like nickname, experience, and online status.<br>
	 * If the player is not in the cache, it creates a new {@code LegionMemberEx} entry.
	 * @param player The {@code Player} object to update in the cache.
	 */
	private void storeLegionMemberExInCache(Player player)
	{
		if (allCachedLegionMembers.containsEx(player.getObjectId()))
		{
			final LegionMemberEx legionMemberEx = allCachedLegionMembers.getMemberEx(player.getObjectId());
			legionMemberEx.setNickname(player.getLegionMember().getNickname());
			legionMemberEx.setSelfIntro(player.getLegionMember().getSelfIntro());
			legionMemberEx.setPlayerClass(player.getPlayerClass());
			legionMemberEx.setExp(player.getCommonData().getExp());
			legionMemberEx.setLastOnline(player.getCommonData().getLastOnline());
			legionMemberEx.setWorldId(player.getPosition().getMapId());
			legionMemberEx.setOnline(false);
		}
		else
		{
			final LegionMemberEx legionMemberEx = new LegionMemberEx(player, player.getLegionMember(), false);
			addCachedLegionMemberEx(legionMemberEx);
		}
	}
	
	/**
	 * Retrieves a {@link Legion} object from the internal cache.<br>
	 * This method looks up the legion using its unique ID.
	 * @param legionId The unique identifier for the legion to retrieve.
	 * @return The {@code Legion} object if found, or {@code null} otherwise.
	 */
	private Legion getCachedLegion(int legionId)
	{
		return allCachedLegions.get(legionId);
	}
	
	/**
	 * Retrieves a {@code Legion} object from the internal cache.<br>
	 * This method looks up the legion using the provided name.
	 * @param legionName The unique name of the legion to find.
	 * @return The {@code Legion} associated with the name, or {@code null} if not found.
	 */
	private Legion getCachedLegion(String legionName)
	{
		return allCachedLegions.get(legionName);
	}
	
	/**
	 * Provides an iterator for all legions currently stored in the cache.<br>
	 * This method allows you to loop through every {@link Legion} object that is loaded.
	 * @return An {@code Iterator<Legion>} containing the cached legion data.
	 */
	public Iterator<Legion> getCachedLegionIterator()
	{
		return allCachedLegions.iterator();
	}
	
	/**
	 * Adds a {@code Legion} object to the internal cache.<br>
	 * This method updates the list of all cached legions.
	 * @param legion The {@code Legion} instance to be added to the cache.
	 */
	private void addCachedLegion(Legion legion)
	{
		allCachedLegions.add(legion);
	}
	
	/**
	 * Adds a {@code LegionMember} to the internal cache.<br>
	 * This method updates the list of members currently stored in memory.
	 * @param legionMember The {@code LegionMember} object to be added.
	 */
	private void addCachedLegionMember(LegionMember legionMember)
	{
		allCachedLegionMembers.addMember(legionMember);
	}
	
	/**
	 * Adds a {@code LegionMemberEx} object to the internal cache.<br>
	 * This method updates the list of cached legion members.
	 * @param legionMemberEx The {@code LegionMemberEx} instance to be added.
	 */
	private void addCachedLegionMemberEx(LegionMemberEx legionMemberEx)
	{
		allCachedLegionMembers.addMemberEx(legionMemberEx);
	}
	
	/**
	 * Removes a {@link Legion} from the database and the cache.<br>
	 * This method calls {@code deleteLegion} via the {@code LegionDAO}.
	 * @param legion The {@code Legion} object to be deleted.
	 */
	private void deleteLegionFromDB(Legion legion)
	{
		allCachedLegions.remove(legion);
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legion.getLegionId());
	}
	
	/**
	 * Removes a member from the database and the cache.<br>
	 * This method also updates the {@link Legion} object and records a kick history.
	 * @param legionMember The {@code LegionMemberEx} object to be removed.
	 */
	private void deleteLegionMemberFromDB(LegionMemberEx legionMember)
	{
		allCachedLegionMembers.remove(legionMember);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(legionMember.getObjectId());
		final Legion legion = legionMember.getLegion();
		legion.deleteLegionMember(legionMember.getObjectId());
		addHistory(legion, legionMember.getName(), LegionHistoryType.KICK);
	}
	
	/**
	 * Retrieves a {@link Legion} object based on its name.<br>
	 * This method checks the cache before loading from the database.<br>
	 * It also populates additional information for the retrieved legion.
	 * @param legionName The unique name of the legion to find.
	 * @return The {@code Legion} object if found, or {@code null} otherwise.
	 */
	public Legion getLegion(String legionName)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if (allCachedLegions.contains(legionName))
		{
			return getCachedLegion(legionName);
		}
		
		/**
		 * Else load the legion information from the database
		 */
		final Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionName);
		
		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);
		
		/**
		 * Return the legion
		 */
		return legion;
	}
	
	/**
	 * Retrieves a {@link Legion} object using its unique identifier.<br>
	 * This method checks the cache before loading data from the database.<br>
	 * It also populates additional information for the retrieved legion.
	 * @param legionId The unique ID of the legion to find.
	 * @return The {@code Legion} object associated with the provided ID.
	 */
	public Legion getLegion(int legionId)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if (allCachedLegions.contains(legionId))
		{
			return getCachedLegion(legionId);
		}
		
		/**
		 * Else load the legion information from the database
		 */
		final Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionId);
		
		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);
		
		/**
		 * Return the legion
		 */
		return legion;
	}
	
	/**
	 * This method populates the data for a specific {@link Legion}.<br>
	 * It retrieves members, announcements, emblems, and storage from the database.<br>
	 * It also updates the legion rank and history information.
	 * @param legion The {@code Legion} object to be populated with data.
	 */
	private void loadLegionInfo(Legion legion)
	{
		/**
		 * Check if legion is not null
		 */
		if (legion == null)
		{
			return;
		}
		
		/**
		 * Load and add the legion members to legion
		 */
		legion.setLegionMembers(DAOManager.getDAO(LegionMemberDAO.class).loadLegionMembers(legion.getLegionId()));
		
		/**
		 * Load and set the announcement list
		 */
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion.getLegionId()));
		
		/**
		 * Set legion emblem
		 */
		legion.setLegionEmblem(DAOManager.getDAO(LegionDAO.class).loadLegionEmblem(legion.getLegionId()));
		
		/**
		 * Load Legion Warehouse
		 */
		legion.setLegionWarehouse(DAOManager.getDAO(LegionDAO.class).loadLegionStorage(legion));
		
		if (legionRanking.containsKey(legion.getLegionId()))
		{
			legion.setLegionRank(legionRanking.get(legion.getLegionId()));
		}
		
		/**
		 * Load Legion History
		 */
		DAOManager.getDAO(LegionDAO.class).loadLegionHistory(legion);
	}
	
	/**
	 * Retrieves the unique ID of the Brigade General for a specific legion.<br>
	 * This method searches through all members of the legion to find the one with the {@code BRIGADE_GENERAL} rank.<br>
	 * It returns 0 if no such member is found.
	 * @param legionId The unique identifier of the legion to search.
	 * @return The unique ID of the Brigade General or 0 if not found.
	 */
	public int getLegionBGeneral(int legionId)
	{
		final Legion legion = LegionService.getInstance().getLegion(legionId);
		int legionBG = 0;
		
		for (int memberObjId : legion.getLegionMembers())
		{
			final LegionMember legionMember = LegionService.getInstance().getLegionMember(memberObjId);
			if (legionMember.getRank() == LegionRank.BRIGADE_GENERAL)
			{
				legionBG = memberObjId;
			}
		}
		
		return legionBG;
	}
	
	/**
	 * Retrieves a {@link LegionMember} based on the provided player object ID.<br>
	 * This method checks the cache before loading from the database.<br>
	 * It returns {@code null} if the member is not found or belongs to a disbanded legion.
	 * @param playerObjId The unique identifier of the player.
	 * @return The {@link LegionMember} object, or {@code null} if no valid member exists.
	 */
	public LegionMember getLegionMember(int playerObjId)
	{
		LegionMember legionMember = null;
		if (allCachedLegionMembers.contains(playerObjId))
		{
			legionMember = allCachedLegionMembers.getMember(playerObjId);
		}
		else
		{
			legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(playerObjId);
			if (legionMember != null)
			{
				addCachedLegionMember(legionMember);
			}
		}
		
		if (legionMember != null)
		{
			if (checkDisband(legionMember.getLegion()))
			{
				return null;
			}
		}
		
		return legionMember;
	}
	
	/**
	 * Checks if a {@link Legion} is ready to be disbanded.<br>
	 * It verifies if the disband status is active and if the current time has passed the scheduled disband time.<br>
	 * If both conditions are met, it calls {@code disbandLegion(legion)}.
	 * @param legion The {@code Legion} object to check.
	 * @return {@code true} if the legion was successfully disbanded, otherwise {@code false}.
	 */
	private boolean checkDisband(Legion legion)
	{
		if (legion.isDisbanding())
		{
			if ((System.currentTimeMillis() / 1000) > legion.getDisbandTime())
			{
				disbandLegion(legion);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Disbands a specific legion and removes it from the system.<br>
	 * This method clears all members from the cache.<br>
	 * It also cleans up siege data and deletes the record from the database.
	 * @param legion The {@code Legion} object to be disbanded.
	 */
	public void disbandLegion(Legion legion)
	{
		for (Integer memberObjId : legion.getLegionMembers())
		{
			allCachedLegionMembers.remove(getLegionMemberEx(memberObjId));
		}
		
		SiegeService.getInstance().cleanLegionId(legion.getLegionId());
		updateAfterDisbandLegion(legion);
		deleteLegionFromDB(legion);
	}
	
	/**
	 * Retrieves the {@link LegionMemberEx} object for a specific player.<br>
	 * This method checks the cache before loading from the database.<br>
	 * If not found in the cache, it loads the data and adds it to the cache.
	 * @param playerObjId The unique identifier of the player.
	 * @return The {@code LegionMemberEx} object associated with the ID.
	 */
	private LegionMemberEx getLegionMemberEx(int playerObjId)
	{
		if (allCachedLegionMembers.containsEx(playerObjId))
		{
			return allCachedLegionMembers.getMemberEx(playerObjId);
		}
		
		final LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerObjId);
		addCachedLegionMemberEx(legionMember);
		return legionMember;
	}
	
	/**
	 * Retrieves the {@code LegionMemberEx} object for a specific player.<br>
	 * This method checks the cache before loading from the database.<br>
	 * If not found in the cache, it loads the data and updates the cache.
	 * @param playerName The name of the player to look up.
	 * @return The {@code LegionMemberEx} object associated with the player.
	 */
	private LegionMemberEx getLegionMemberEx(String playerName)
	{
		if (allCachedLegionMembers.containsEx(playerName))
		{
			return allCachedLegionMembers.getMemberEx(playerName);
		}
		
		final LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerName);
		addCachedLegionMemberEx(legionMember);
		return legionMember;
	}
	
	/**
	 * Sends a request to disband the legion of the active player.<br>
	 * This method checks if the {@link Player} is allowed to disband their legion.<br>
	 * If allowed, it opens a question window for the user to confirm the action.
	 * @param npc The {@code Creature} object representing the NPC who initiated the request.
	 * @param activePlayer The {@link Player} who owns the legion being disbanded.
	 */
	public void requestDisbandLegion(Creature npc, Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if (legionRestrictions.canDisbandLegion(activePlayer, legion))
		{
			final RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					final int unixTime = (int) ((System.currentTimeMillis() / 1000) + LegionConfig.LEGION_DISBAND_TIME);
					legion.setDisbandTime(unixTime);
					updateMembersOfDisbandLegion(legion, unixTime);
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};
			
			final boolean disbandResult = activePlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE, disbandResponseHandler);
			if (disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE, 0, 0));
			}
		}
	}
	
	/**
	 * Creates a new {@link Legion} for the specified player.<br>
	 * This method checks restrictions and deducts the required Kinah cost.<br>
	 * It initializes the legion with the creator as the first member.
	 * @param activePlayer The {@code Player} who is creating the legion.
	 * @param legionName The name of the new legion to be created.
	 */
	public void createLegion(Player activePlayer, String legionName)
	{
		if (legionRestrictions.canCreateLegion(activePlayer, legionName))
		{
			/**
			 * Create new legion and put originator as first member
			 */
			final Legion legion = new Legion(IDFactory.getInstance().nextId(), legionName);
			legion.addLegionMember(activePlayer.getObjectId());
			
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);
			
			/**
			 * Create a LegionMember, add it to the legion and bind it to a Player
			 */
			storeLegion(legion, true);
			final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			storeNewAnnouncement(legion.getLegionId(), currentTime, "");
			legion.addAnnouncementToList(currentTime, "");
			legion.setTerritory(new LegionTerritory());
			addLegionMember(legion, activePlayer, LegionRank.BRIGADE_GENERAL);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System.currentTimeMillis() / 1000), ""));
			/**
			 * Add create and joined legion history and save it
			 */
			addHistory(legion, "", LegionHistoryType.CREATE);
			addHistory(legion, activePlayer.getName(), LegionHistoryType.JOIN);
			
			/**
			 * Send required packets
			 */
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATED(legion.getLegionName()));
		}
	}
	
	/**
	 * Adds a {@code Player} directly to a specific legion.<br>
	 * This method checks if the legion exists before attempting the addition.
	 * @param legionId The unique identifier of the target legion.
	 * @param player The {@code Player} object to be added.
	 * @return {@code true} if the player was successfully added, or {@code false} otherwise.
	 */
	public boolean directAddPlayer(int legionId, Player player)
	{
		final Legion legion = getLegion(legionId);
		
		if (legion == null)
		{
			return false;
		}
		
		return directAddPlayer(legion, player);
	}
	
	/**
	 * Adds a {@link Player} directly to a specific {@link Legion}.<br>
	 * This method updates the legion membership and displays the current announcement.<br>
	 * It also records the join event in the legion history.
	 * @param legion The {@link Legion} object to add the player to.
	 * @param player The {@link Player} object to be added as a member.
	 * @return {@code true} if the player was successfully added, otherwise {@code false}.
	 */
	public boolean directAddPlayer(Legion legion, Player player)
	{
		final int playerObjId = player.getObjectId();
		if (legion.addLegionMember(playerObjId))
		{
			// Bind LegionMember to Player
			addLegionMember(legion, player);
			
			// Display current announcement
			displayLegionMessage(player, legion.getCurrentAnnouncement());
			
			// Add to history of legion
			addHistory(legion, player.getName(), LegionHistoryType.JOIN);
			return true;
		}
		
		player.resetLegionMember();
		return false;
	}
	
	/**
	 * Sends a join invitation to another player for the current player's legion.<br>
	 * This method checks restrictions before sending a request packet.<br>
	 * It handles the response logic for accepting or denying the invite.
	 * @param activePlayer The player who is sending the invitation.
	 * @param targetPlayer The player who will receive the invitation.
	 */
	private void invitePlayerToLegion(Player activePlayer, Player targetPlayer)
	{
		if (legionRestrictions.canInvitePlayer(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();
			
			final RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(targetPlayer.getName()));
					}
					else
					{
						final int playerObjId = targetPlayer.getObjectId();
						if (legion.addLegionMember(playerObjId))
						{
							// Bind LegionMember to Player
							addLegionMember(legion, targetPlayer);
							
							// Display current announcement
							displayLegionMessage(targetPlayer, legion.getCurrentAnnouncement());
							
							// Add to history of legion
							addHistory(legion, targetPlayer.getName(), LegionHistoryType.JOIN);
						}
						else
						{
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CAN_NOT_ADD_MEMBER_ANY_MORE);
							targetPlayer.resetLegionMember();
						}
					}
					
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_REJECTED_INVITATION(targetPlayer.getName()));
				}
			};
			
			final boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_INVITE_I_JOINED_MSGBOX, responseHandler);
			
			// If the player is busy and could not be asked
			if (!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_OTHER_IS_BUSY);
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_SENT_INVITE_MSG_TO_HIM(targetPlayer.getName()));
				
				// Send question packet to buddy
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_INVITE_I_JOINED_MSGBOX, 0, 0, legion.getLegionName(), legion.getLegionLevel() + "", activePlayer.getName()));
			}
		}
	}
	
	/**
	 * Sends a system message to a specific player.<br>
	 * This method uses the provided announcement data to create a guild notice.<br>
	 * It checks if the {@code currentAnnouncement} is not {@code null} before sending.
	 * @param targetPlayer The {@link Player} who will receive the message.
	 * @param currentAnnouncement An {@link Entry} containing the timestamp and the message text.
	 */
	private void displayLegionMessage(Player targetPlayer, Entry<Timestamp, String> currentAnnouncement)
	{
		if (currentAnnouncement != null)
		{
			PacketSendUtility.sendPacket(targetPlayer, SM_SYSTEM_MESSAGE.STR_GUILD_NOTICE(currentAnnouncement.getValue(), (int) (currentAnnouncement.getKey().getTime() / 1000)));
		}
	}
	
	/**
	 * This method handles the process of appointing a new Brigade General.<br>
	 * It checks if the {@code activePlayer} has permission to make the appointment.<br>
	 * If allowed, it sends an invitation request to the {@code targetPlayer}.<br>
	 * The system manages rank updates and history logging upon acceptance.
	 * @param activePlayer The player initiating the appointment request.
	 * @param targetPlayer The player being nominated for the Brigade General position.
	 */
	private void appointBrigadeGeneral(Player activePlayer, Player targetPlayer)
	{
		if (legionRestrictions.canAppointBrigadeGeneral(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();
			final RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if (!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_NO_SUCH_USER);
					}
					else
					{
						final LegionMember legionMember = targetPlayer.getLegionMember();
						if (legionMember.getRank().getRankId() > LegionRank.BRIGADE_GENERAL.getRankId())
						{
							// Demote Brigade General to Centurion
							activePlayer.getLegionMember().setRank(LegionRank.CENTURION);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer, 0, ""));
							
							// Promote member to Brigade General
							legionMember.setRank(LegionRank.BRIGADE_GENERAL);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer, 1300273, targetPlayer.getName()));
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x08));
							addHistory(legion, targetPlayer.getName(), LegionHistoryType.APPOINTED);
						}
					}
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_HE_DECLINE_YOUR_OFFER(targetPlayer.getName()));
				}
			};
			
			final boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_CHANGE_MASTER_DO_YOU_ACCEPT_OFFER, responseHandler);
			
			// If the player is busy and could not be asked
			if (!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_SENT_CANT_OFFER_WHEN_HE_IS_QUESTION_ASKED);
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_SENT_OFFER_MSG_TO_HIM(targetPlayer.getName()));
				
				// Send the question packet to the buddy; note that the character name parameter needs to be added as it currently does not work.
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_CHANGE_MASTER_DO_YOU_ACCEPT_OFFER, activePlayer.getObjectId(), 0, activePlayer.getName()));
			}
		}
	}
	
	/**
	 * Assigns a specific rank to a legion member.<br>
	 * This method checks if the {@code activePlayer} has permission to perform the action.<br>
	 * It updates the database and broadcasts the change to all members of the legion.
	 * @param activePlayer The player performing the appointment action.
	 * @param charName The name of the character receiving the new rank.
	 * @param rankId The integer ID representing the {@code LegionRank} to assign.
	 */
	private void appointRank(Player activePlayer, String charName, int rankId)
	{
		final LegionMemberEx LM = getLegionMemberEx(charName);
		if (LM == null)
		{
			log.error("Char name does not exist in legion member table: " + charName);
			return;
		}
		
		if (legionRestrictions.canAppointRank(activePlayer, LM.getObjectId()))
		{
			final Legion legion = activePlayer.getLegion();
			final LegionRank rank = LegionRank.values()[rankId];
			int msgId = 0;
			switch (rank)
			{
				case DEPUTY:
					msgId = 1400902;
					break;
				case LEGIONARY:
					msgId = 1300268;
					break;
				case CENTURION:
					msgId = 1300267;
					break;
				case VOLUNTEER:
					msgId = 1400903;
				default:
					break;
			}
			
			final LegionMember legionMember = getLegionMember(LM.getObjectId());
			legionMember.setRank(rank);
			DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
			LM.setRank(rank);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(LM, msgId, LM.getName()));
		}
	}
	
	/**
	 * Assigns a specific rank to a player within the legion.<br>
	 * This method checks if the {@code activePlayer} has permission to perform the action.<br>
	 * It updates the {@code targetPlayer} rank and broadcasts the update to all members.
	 * @param activePlayer The player performing the appointment.
	 * @param targetPlayer The player receiving the new rank.
	 * @param rankId The unique identifier for the rank to be assigned.
	 */
	private void appointRank(Player activePlayer, Player targetPlayer, int rankId)
	{
		if (legionRestrictions.canAppointRank(activePlayer, targetPlayer.getObjectId()))
		{
			final Legion legion = activePlayer.getLegion();
			int msgId = 0;
			final LegionRank rank = LegionRank.values()[rankId];
			final LegionMember legionMember = targetPlayer.getLegionMember();
			switch (rank)
			{
				case DEPUTY:
					msgId = 1400902;
					break;
				case LEGIONARY:
					msgId = 1300268;
					break;
				case CENTURION:
					msgId = 1300267;
					break;
				case VOLUNTEER:
					msgId = 1400903;
				default:
					break;
			}
			
			legionMember.setRank(rank);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer, msgId, targetPlayer.getName()));
		}
	}
	
	/**
	 * Updates the self-introduction text for a player.<br>
	 * This method checks if the change is allowed by {@code legionRestrictions}.<br>
	 * It updates the {@link LegionMember} data and broadcasts the update to the entire legion.
	 * @param activePlayer The {@code Player} object of the user making the request.
	 * @param newSelfIntro The new string value for the self-introduction.
	 */
	private void changeSelfIntro(Player activePlayer, String newSelfIntro)
	{
		if (legionRestrictions.canChangeSelfIntro(activePlayer, newSelfIntro))
		{
			final LegionMember legionMember = activePlayer.getLegionMember();
			legionMember.setSelfIntro(newSelfIntro);
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_UPDATE_SELF_INTRO(activePlayer.getObjectId(), newSelfIntro));
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WRITE_INTRO_DONE);
		}
	}
	
	/**
	 * Updates the permission levels for different ranks within a specific {@link Legion}.<br>
	 * This method applies the new permissions and broadcasts an update to all members if successful.
	 * @param legion The {@link Legion} object to modify.
	 * @param deputyPermission The new permission level for deputies.
	 * @param centurionPermission The new permission level for centurions.
	 * @param legionarPermission The new permission level for legionars.
	 * @param volunteerPermission The new permission level for volunteers.
	 */
	public void changePermissions(Legion legion, short deputyPermission, short centurionPermission, short legionarPermission, short volunteerPermission)
	{
		if (legion.setLegionPermissions(deputyPermission, centurionPermission, legionarPermission, volunteerPermission))
		{
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x02, legion));
		}
	}
	
	/**
	 * Checks if the player is allowed to upgrade their legion level.<br>
	 * Deducts the required Kinah from the player's inventory.<br>
	 * Increases the legion level and records the history.
	 * @param activePlayer The {@code Player} object requesting the change.
	 */
	private void requestChangeLevel(Player activePlayer)
	{
		if (legionRestrictions.canChangeLevel(activePlayer))
		{
			final Legion legion = activePlayer.getLegion();
			activePlayer.getInventory().decreaseKinah(legion.getKinahPrice());
			changeLevel(legion, legion.getLegionLevel() + 1, false);
			addHistory(legion, legion.getLegionLevel() + "", LegionHistoryType.LEVEL_UP);
		}
	}
	
	/**
	 * Updates the level of a specific {@link Legion}.<br>
	 * This method broadcasts the change to all members.<br>
	 * It optionally saves the new data to the database.
	 * @param legion The {@code Legion} object to modify.
	 * @param newLevel The integer value for the new level.
	 * @param save Set to {@code true} to persist changes to the database.
	 */
	public void changeLevel(Legion legion, int newLevel, boolean save)
	{
		legion.setLegionLevel(newLevel);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x00, legion));
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.STR_GUILD_EVENT_LEVELUP(newLevel));
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * Updates the nickname of a specific member within the active player's legion.<br>
	 * This method checks if the target is in the same legion and verifies permissions.<br>
	 * It broadcasts the update to all members if the change is successful.
	 * @param activePlayer The {@code Player} object who is initiating the request.
	 * @param charName The character name of the member whose nickname will be changed.
	 * @param newNickname The new {@code String} value for the nickname.
	 */
	private void changeNickname(Player activePlayer, String charName, String newNickname)
	{
		final Legion legion = activePlayer.getLegion();
		LegionMember legionMember;
		Player targetPlayer;
		if ((targetPlayer = World.getInstance().findPlayer(charName)) != null)
		{
			legionMember = targetPlayer.getLegionMember();
			if (targetPlayer.getLegion() != legion)
			{
				return;
			}
		}
		else
		{
			final LegionMemberEx LM = getLegionMemberEx(charName);
			if ((LM == null) || (LM.getLegion() != legion))
			{
				return;
			}
			
			legionMember = getLegionMember(LM.getObjectId());
		}
		
		if (legionRestrictions.canChangeNickname(legion, legionMember.getObjectId(), newNickname))
		{
			legionMember.setNickname(newNickname);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_NICKNAME(legionMember.getObjectId(), newNickname));
			if (targetPlayer == null)
			{
				DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
			}
		}
	}
	
	/**
	 * Updates the status of all online members after a {@link Legion} is disbanded.<br>
	 * This method broadcasts a title update and sends a leave member packet to each player.<br>
	 * It also resets the legion member data for every affected {@link Player}.
	 * @param legion The {@code Legion} object that was recently disbanded.
	 */
	private void updateAfterDisbandLegion(Legion legion)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_TITLE(onlineLegionMember.getObjectId(), 0, "", 0), true);
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_LEAVE_MEMBER(1300302, 0, legion.getLegionName()));
			onlineLegionMember.resetLegionMember();
		}
	}
	
	/**
	 * Updates the emblem information for all online members of a specific legion.<br>
	 * This method broadcasts the new emblem data to every active player in the {@link Legion}.<br>
	 * It also sends additional custom data if the emblem type is set to {@code CUSTOM}.
	 * @param legion The {@link Legion} object containing the member list and emblem details.
	 * @param emblemType The {@link LegionEmblemType} used to identify the emblem category.
	 */
	private void updateMembersEmblem(Legion legion, LegionEmblemType emblemType)
	{
		final LegionEmblem legionEmblem = legion.getLegionEmblem();
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), emblemType), true);
			if (legionEmblem.getEmblemType() == LegionEmblemType.CUSTOM)
			{
				sendEmblemData(onlineLegionMember, legionEmblem, legion.getLegionId(), legion.getLegionName());
			}
		}
	}
	
	/**
	 * Updates the status of all online members for a disbanded legion.<br>
	 * This method sends an update packet to each individual member.<br>
	 * It also broadcasts a disband notification to the entire {@code Legion}.
	 * @param legion The {@code Legion} object being processed.
	 * @param unixTime The current timestamp used in the update packets.
	 */
	private void updateMembersOfDisbandLegion(Legion legion, int unixTime)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300303, unixTime + ""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x06, unixTime));
		}
	}
	
	/**
	 * Updates the online members of a specific legion.<br>
	 * This method sends an update packet to each member and broadcasts a change notification to the entire legion.
	 * @param legion The {@code Legion} object containing the members to be updated.
	 */
	private void updateMembersOfRecreateLegion(Legion legion)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300307, ""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07));
		}
	}
	
	/**
	 * Saves a new emblem for the player's legion.<br>
	 * This method updates the legion data and notifies all members.<br>
	 * It also records the change in the legion history.
	 * @param activePlayer The {@link Player} who is performing the action.
	 * @param customEmblem The {@link LegionEmblem} to be applied to the legion.
	 */
	public void storeLegionEmblem(Player activePlayer, LegionEmblem customEmblem)
	{
		addHistory(activePlayer.getLegion(), "", LegionHistoryType.EMBLEM_MODIFIED);
		activePlayer.getLegion().setLegionEmblem(customEmblem);
		updateMembersEmblem(activePlayer.getLegion(), customEmblem.getEmblemType());
		PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_EMBLEM);
	}
	
	/**
	 * Saves a new legion emblem for the player's current legion.<br>
	 * This method checks restrictions before updating the emblem data.<br>
	 * It also deducts the required Kinah from the player's inventory.
	 * @param activePlayer The {@code Player} performing the action.
	 * @param legionId The unique identifier for the {@code Legion}.
	 * @param emblemId The ID of the emblem to be used.
	 * @param color_r The red component of the emblem color.
	 * @param color_g The green component of the emblem color.
	 * @param color_b The blue component of the emblem color.
	 * @param emblemType The {@code LegionEmblemType} to apply.
	 */
	public void storeLegionEmblem(Player activePlayer, int legionId, int emblemId, int color_r, int color_g, int color_b, LegionEmblemType emblemType)
	{
		if (legionRestrictions.canStoreLegionEmblem(activePlayer, legionId, emblemId))
		{
			final Legion legion = activePlayer.getLegion();
			if (legion.getLegionEmblem().isDefaultEmblem())
			{
				addHistory(legion, "", LegionHistoryType.EMBLEM_REGISTER);
			}
			else
			{
				addHistory(legion, "", LegionHistoryType.EMBLEM_MODIFIED);
			}
			
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH);
			legion.getLegionEmblem().setEmblem(emblemId, color_r, color_g, color_b, emblemType, null);
			updateMembersEmblem(legion, emblemType);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_EMBLEM);
		}
	}
	
	/**
	 * Retrieves a list of {@link LegionMemberEx} objects for a specific {@link Legion}.<br>
	 * It filters out members if their ID matches the provided {@code objExcluded} value.<br>
	 * The method finds active players in the world or loads them from the database.
	 * @param legion The {@link Legion} object to retrieve members from.
	 * @param objExcluded The unique identifier of a member to exclude from the list, or {@code null} to include all.
	 * @return An {@link ArrayList} containing the loaded {@link LegionMemberEx} objects.
	 */
	public ArrayList<LegionMemberEx> loadLegionMemberExList(Legion legion, Integer objExcluded)
	{
		final ArrayList<LegionMemberEx> legionMembers = new ArrayList<>();
		for (Integer memberObjId : legion.getLegionMembers())
		{
			LegionMemberEx legionMemberEx;
			if ((objExcluded != null) && objExcluded.equals(memberObjId))
			{
				continue;
			}
			
			final Player memberPlayer = world.findPlayer(memberObjId);
			if (memberPlayer != null)
			{
				legionMemberEx = new LegionMemberEx(memberPlayer, memberPlayer.getLegionMember(), true);
			}
			else
			{
				legionMemberEx = getLegionMemberEx(memberObjId);
			}
			
			legionMembers.add(legionMemberEx);
		}
		
		return legionMembers;
	}
	
	/**
	 * Retrieves the name of the brigade general for a specific {@link Legion}.<br>
	 * It searches through all members to find the one with the general status.<br>
	 * Returns a default error string if no general is found.
	 * @param legion The {@code Legion} object to search within.
	 * @return The name of the brigade general as a {@code String}.
	 */
	public String getBrigadeGeneralName(Legion legion)
	{
		for (LegionMemberEx member : loadLegionMemberExList(legion, null))
		{
			if (member.isBrigadeGeneral())
			{
				return member.getName();
			}
		}
		
		log.debug("can't get Name of BrigadeGeneral for Legion: " + legion.getLegionName() + " (id:" + legion.getLegionId() + ")");
		return "ERROR Name..";
	}
	
	/**
	 * Retrieves the {@link Player} who holds the Brigade General rank for a specific legion.<br>
	 * This method searches through all members of the provided {@code Legion}.<br>
	 * It returns {@code null} if no Brigade General is found or if the player is offline.
	 * @param legion The {@code Legion} to search for its leader.
	 * @return The {@link Player} object representing the Brigade General, or {@code null}.
	 */
	public Player getBrigadeGeneral(Legion legion)
	{
		Player player = null;
		for (LegionMemberEx member : loadLegionMemberExList(legion, null))
		{
			if (member.isBrigadeGeneral())
			{
				player = World.getInstance().findPlayer(member.getObjectId());
			}
		}
		
		if (player == null)
		{
			log.debug("LegionService.getBrigadeGeneral - Player is NULL ! - Legion: " + legion.getLegionName() + " (id:" + legion.getLegionId() + ")");
		}
		
		return player;
	}
	
	/**
	 * Retrieves the {@link PlayerCommonData} for the brigade general of a specific legion.<br>
	 * This method searches through all members to find the one with the brigade general status.<br>
	 * It returns {@code null} if no such member is found.
	 * @param legion The {@link Legion} object to search within.
	 * @return The {@link PlayerCommonData} of the brigade general, or {@code null}.
	 */
	public PlayerCommonData getBrigadeGeneralRace(Legion legion)
	{
		PlayerCommonData player = null;
		for (LegionMemberEx member : loadLegionMemberExList(legion, null))
		{
			if (member.isBrigadeGeneral())
			{
				player = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(member.getObjectId());
			}
		}
		
		return player;
	}
	
	/**
	 * Opens the legion warehouse for a specific {@link Player}.<br>
	 * This method checks permissions and sends the necessary packets to display items.<br>
	 * It also opens the dialog window of the interacting {@link Npc}.
	 * @param player The {@link Player} who is trying to access the warehouse.
	 * @param npc The {@link Npc} object used to open the dialog window.
	 */
	public void openLegionWarehouse(Player player, Npc npc)
	{
		if (legionRestrictions.canOpenWarehouse(player))
		{
			LegionWhUpdate(player);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion())); // kinah
			final int whLvl = player.getLegion().getWarehouseLevel();
			final List<Item> items = player.getLegion().getLegionWarehouse().getItems();
			final int storageId = StorageType.LEGION_WAREHOUSE.getId();
			final boolean isEmpty = items.isEmpty();
			if (!isEmpty)
			{
				boolean isFirst = true;
				final ListSplitter<Item> splitter = new ListSplitter<>(items, 10);
				while (!splitter.isLast())
				{
					PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(splitter.getNext(), storageId, whLvl, isFirst, player));
					isFirst = false;
				}
			}
			
			PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, storageId, whLvl, isEmpty, player));
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 25));
		}
	}
	
	/**
	 * Recreates the legion for a specific player.<br>
	 * This method checks if the {@link Player} is allowed to recreate their legion.<br>
	 * It then opens a request window via an {@link Npc} to confirm the action.
	 * @param npc The {@link Npc} used to handle the interaction and display the request window.
	 * @param activePlayer The {@link Player} who will be recreating the legion.
	 */
	public void recreateLegion(Npc npc, Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if (legionRestrictions.canRecreateLegion(activePlayer, legion))
		{
			final RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc)
			{
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					legion.setDisbandTime(0);
					PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07));
					updateMembersOfRecreateLegion(legion);
				}
				
				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};
			
			final boolean disbandResult = activePlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE_CANCEL, disbandResponseHandler);
			if (disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_GUILD_DISPERSE_STAYMODE_CANCEL, 0, 0));
			}
		}
	}
	
	/**
	 * Updates the rank of each legion based on the provided data.<br>
	 * This method iterates through all cached legions and applies new ranks.<br>
	 * It also broadcasts a packet to notify members of any changes.
	 * @param legionRanking A {@code Map<Integer, Integer>} where the key is the legion ID and the value is the new rank.
	 */
	public void performRankingUpdate(Map<Integer, Integer> legionRanking)
	{
		GameServer.log.debug("[LegionService] Legion ranking update task started");
		final long startTime = System.currentTimeMillis();
		
		final Iterator<Legion> legionsIterator = allCachedLegions.iterator();
		int legionsUpdated = 0;
		
		this.legionRanking = legionRanking;
		
		while (legionsIterator.hasNext())
		{
			final Legion legion = legionsIterator.next();
			if (legionRanking.containsKey(legion.getLegionId()))
			{
				legion.setLegionRank(legionRanking.get(legion.getLegionId()));
				PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x01, legion));
			}
			
			legionsUpdated++;
		}
		
		final long workTime = System.currentTimeMillis() - startTime;
		log.debug("[LegionService] Legion ranking update: " + workTime + " ms, legions: " + legionsUpdated);
	}
	
	/**
	 * Updates the legion warehouse data for a specific player.<br>
	 * This method saves all items and item stones to the database.<br>
	 * It handles errors internally by logging any exceptions during the save process.
	 * @param player The {@link Player} object whose legion warehouse needs updating.
	 */
	public void LegionWhUpdate(Player player)
	{
		final Legion legion = player.getLegion();
		
		if (legion == null)
		{
			return;
		}
		
		final List<Item> allItems = legion.getLegionWarehouse().getItemsWithKinah();
		allItems.addAll(legion.getLegionWarehouse().getDeletedItems());
		try
		{
			/**
			 * 1. save items first
			 */
			DAOManager.getDAO(InventoryDAO.class).store(allItems, player.getObjectId(), player.getPlayerAccount().getId(), legion.getLegionId());
			
			/**
			 * 2. save item stones
			 */
			DAOManager.getDAO(ItemStoneListDAO.class).save(allItems);
		}
		catch (Exception ex)
		{
			log.error("[LegionService] Exception during periodic saving of legion WH", ex);
		}
	}
	
	/**
	 * Updates the member information for a specific player.<br>
	 * This method broadcasts the update to all members of the player's {@link Legion}.
	 * @param player The {@code Player} object whose information needs to be updated.
	 */
	public void updateMemberInfo(Player player)
	{
		PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
	}
	
	/**
	 * Updates the contribution points for a specific {@link Legion}.<br>
	 * This method broadcasts the update to all members.<br>
	 * It optionally saves the changes to the database.
	 * @param legion The {@code Legion} object to modify.
	 * @param newPoints The new value for the contribution points.
	 * @param save Set to {@code true} to persist the change in the database, or {@code false} to only update it in memory.
	 */
	public void setContributionPoints(Legion legion, long newPoints, boolean save)
	{
		legion.setContributionPoints(newPoints);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x03, legion));
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * Updates the legion emblem information for a specific player.<br>
	 * This method checks if the {@link Player} is allowed to upload new data.<br>
	 * It resets existing settings and applies the new visual properties.
	 * @param activePlayer The {@link Player} who owns the legion.
	 * @param totalSize The size of the emblem image.
	 * @param color_r The red component of the emblem color.
	 * @param color_g The green component of the emblem color.
	 * @param color_b The blue component of the emblem color.
	 * @param emblemType The {@link LegionEmblemType} to be assigned.
	 */
	public void uploadEmblemInfo(Player activePlayer, int totalSize, int color_r, int color_g, int color_b, LegionEmblemType emblemType)
	{
		if (legionRestrictions.canUploadEmblemInfo(activePlayer))
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.resetUploadSettings();
			
			final int emblemId = legionEmblem.getEmblemId() + 1;
			legionEmblem.setEmblem(emblemId, color_r, color_g, color_b, emblemType, null);
			legionEmblem.setUploadSize(totalSize);
			legionEmblem.setUploading(true);
		}
	}
	
	/**
	 * Uploads custom data for a legion emblem.<br>
	 * This method checks if the {@code activePlayer} has permission to upload.<br>
	 * It updates the emblem size and data in the system.<br>
	 * If the upload is complete, it deducts Kinah from the player's inventory.
	 * @param activePlayer The {@link Player} who is performing the upload.
	 * @param size The amount of data being uploaded.
	 * @param data The raw byte array containing the emblem information.
	 */
	public void uploadEmblemData(Player activePlayer, int size, byte[] data)
	{
		if (legionRestrictions.canUploadEmblem(activePlayer))
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.addUploadedSize(size);
			legionEmblem.addUploadData(data);
			
			if (legionEmblem.getUploadSize() == legionEmblem.getUploadedSize())
			{
				if ((legionEmblem.getUploadedSize() == 0) || (legionEmblem.getUploadSize() == 0))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WARN_CORRUPT_EMBLEM_FILE);
					return;
				}
				
				if (!activePlayer.getInventory().tryDecreaseKinah(1130000))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_MONEY);
					return;
				}
				
				// Finished
				legionEmblem.setCustomEmblemData(legionEmblem.getUploadData());
				DAOManager.getDAO(LegionDAO.class).storeLegionEmblem(activePlayer.getLegion().getLegionId(), legionEmblem);
				final LegionEmblem emblem = DAOManager.getDAO(LegionDAO.class).loadLegionEmblem(activePlayer.getLegion().getLegionId());
				LegionService.getInstance().storeLegionEmblem(activePlayer, emblem);
			}
		}
	}
	
	/**
	 * Sends the legion emblem information to a specific player.<br>
	 * This method transmits both basic emblem details and custom data packets.<br>
	 * It handles large custom data by splitting it into multiple chunks if necessary.
	 * @param player The {@link Player} who will receive the emblem data.
	 * @param legionEmblem The {@link LegionEmblem} object containing the visual data.
	 * @param legionId The unique identifier for the legion.
	 * @param legionName The display name of the legion.
	 */
	public void sendEmblemData(Player player, LegionEmblem legionEmblem, int legionId, String legionName)
	{
		PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM(legionId, legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), legionName, legionEmblem.getEmblemType(), legionEmblem.getCustomEmblemData().length));
		final ByteBuffer buf = ByteBuffer.allocate(legionEmblem.getCustomEmblemData().length);
		buf.put(legionEmblem.getCustomEmblemData()).position(0);
		log.debug("[LegionService] legionEmblem size: " + buf.capacity() + " bytes");
		final int maxSize = 7993;
		int currentSize;
		byte[] bytes;
		do
		{
			log.debug("[LegionService] legionEmblem data position: " + buf.position());
			currentSize = buf.capacity() - buf.position();
			log.debug("[LegionService] legionEmblem data remaining capacity: " + currentSize + " bytes");
			
			if (currentSize >= maxSize)
			{
				bytes = new byte[maxSize];
				for (int i = 0; i < maxSize; i++)
				{
					bytes[i] = buf.get();
				}
				
				log.debug("[LegionService] legionEmblem data send size: " + (bytes.length) + " bytes");
				PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM_DATA(maxSize, bytes));
			}
			else
			{
				bytes = new byte[currentSize];
				for (int i = 0; i < currentSize; i++)
				{
					bytes[i] = buf.get();
				}
				
				log.debug("[LegionService] legionEmblem data send size: " + (bytes.length) + " bytes");
				PacketSendUtility.sendPacket(player, new SM_LEGION_SEND_EMBLEM_DATA(currentSize, bytes));
			}
		}
		while (buf.capacity() != buf.position());
	}
	
	/**
	 * Updates the name of a specific {@link Legion}.<br>
	 * This method broadcasts the new name to all online members.<br>
	 * It optionally saves the change to the database.
	 * @param legion The {@code Legion} object to modify.
	 * @param newLegionName The new name to assign to the legion.
	 * @param save A boolean indicating whether to persist the change to the database.
	 */
	public void setLegionName(Legion legion, String newLegionName, boolean save)
	{
		legion.setLegionName(newLegionName);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_INFO(legion));
		
		for (Player legionMember : legion.getOnlineLegionMembers())
		{
			PacketSendUtility.broadcastPacket(legionMember, new SM_LEGION_UPDATE_TITLE(legionMember.getObjectId(), legion.getLegionId(), legion.getLegionName(), legionMember.getLegionMember().getRank().getRankId()), true);
		}
		
		if (save)
		{
			storeLegion(legion);
		}
	}
	
	/**
	 * Updates the announcement for a player's legion.<br>
	 * This method checks if the {@code activePlayer} has permission to change the text.<br>
	 * It saves the new message and broadcasts it to all members of the legion.
	 * @param activePlayer The {@link Player} who is attempting to make the change.
	 * @param announcement The new {@code String} message to be displayed.
	 */
	private void changeAnnouncement(Player activePlayer, String announcement)
	{
		if (legionRestrictions.canChangeAnnouncement(activePlayer.getLegionMember(), announcement))
		{
			final Legion legion = activePlayer.getLegion();
			
			final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			storeNewAnnouncement(legion.getLegionId(), currentTime, announcement);
			legion.addAnnouncementToList(currentTime, announcement);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System.currentTimeMillis() / 1000), announcement));
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_WRITE_NOTICE_DONE);
		}
	}
	
	/**
	 * Saves the announcement list for a specific {@link Legion}.<br>
	 * This method keeps only the most recent 7 announcements.<br>
	 * It removes older entries from the database and the {@code Legion} object.
	 * @param legion The {@code Legion} object containing the announcements to store.
	 */
	private void storeLegionAnnouncements(Legion legion)
	{
		for (int i = 0; i < (legion.getAnnouncementList().size() - 7); i++)
		{
			removeAnnouncement(legion.getLegionId(), legion.getAnnouncementList().firstEntry().getKey());
			legion.removeFirstEntry();
		}
	}
	
	/**
	 * Saves a new announcement for a specific legion to the database.<br>
	 * This method uses {@link LegionDAO} to persist the data.
	 * @param legionId The unique identifier of the legion.
	 * @param currentTime The timestamp when the announcement was created.
	 * @param message The text content of the announcement.
	 * @return {@code true} if the save operation succeeded, {@code false} otherwise.
	 */
	private boolean storeNewAnnouncement(int legionId, Timestamp currentTime, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, currentTime, message);
	}
	
	/**
	 * Removes a specific announcement from the database.<br>
	 * This method uses {@link LegionDAO} to delete the entry.
	 * @param legionId The unique identifier for the legion.
	 * @param key The timestamp used as the primary key for the announcement.
	 */
	private void removeAnnouncement(int legionId, Timestamp key)
	{
		DAOManager.getDAO(LegionDAO.class).removeAnnouncement(legionId, key);
	}
	
	/**
	 * Adds a new entry to the history of a specific {@link Legion}.<br>
	 * This method records an action or message for the legion.
	 * @param legion The {@code Legion} object that owns the history.
	 * @param text The description or content of the history entry.
	 * @param legionHistoryType The category of the history event.
	 */
	private void addHistory(Legion legion, String text, LegionHistoryType legionHistoryType)
	{
		addHistory(legion, text, legionHistoryType, 0, "");
	}
	
	/**
	 * Adds a new history entry to a specific {@link Legion}.<br>
	 * This method saves the history to the database and broadcasts the update to all members.
	 * @param legion The {@code Legion} object where the history will be added.
	 * @param text The main content or message of the history entry.
	 * @param legionHistoryType The category type of the history record.
	 * @param tabId The specific tab identifier for organizing history entries.
	 * @param description A brief summary or extra detail about the history event.
	 */
	public void addHistory(Legion legion, String text, LegionHistoryType legionHistoryType, int tabId, String description)
	{
		final LegionHistory legionHistory = new LegionHistory(legionHistoryType, text, new Timestamp(System.currentTimeMillis()), tabId, description);
		
		legion.addHistory(legionHistory);
		DAOManager.getDAO(LegionDAO.class).saveNewLegionHistory(legion.getLegionId(), legionHistory);
		
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_TABS(legion.getLegionHistoryByTabId(tabId), tabId));
	}
	
	/**
	 * Adds a {@link Player} to the specified {@link Legion}.<br>
	 * This method updates the membership records for the provided entities.
	 * @param legion The {@code Legion} object that will receive the new member.
	 * @param player The {@code Player} object being added to the group.
	 */
	private void addLegionMember(Legion legion, Player player)
	{
		addLegionMember(legion, player, LegionRank.VOLUNTEER);
	}
	
	/**
	 * Adds a {@link Player} to a specific {@link Legion} with a given {@link LegionRank}.<br>
	 * This method updates the database and sends all necessary network packets to the player.<br>
	 * It also broadcasts the new member information to other members of the legion.<br>
	 * Finally, it updates the visual appearance of the player in the game world.
	 * @param legion The {@link Legion} object that the player is joining.
	 * @param player The {@link Player} object being added to the legion.
	 * @param rank The {@link LegionRank} assigned to the new member.
	 */
	private void addLegionMember(Legion legion, Player player, LegionRank rank)
	{
		// Set legion member of player and save in the database
		player.setLegionMember(new LegionMember(player.getObjectId(), legion, rank));
		storeLegionMember(player.getLegionMember(), true);
		
		// Send the new legion member the required legion packets
		PacketSendUtility.sendPacket(player, new SM_LEGION_INFO(legion));
		final ArrayList<LegionMemberEx> totalMembers = loadLegionMemberExList(legion, player.getObjectId());
		final ListSplitter<LegionMemberEx> splits = new ListSplitter<>(totalMembers, 128);
		
		// Send the member list to the new legion member
		boolean isFirst = true;
		while (!splits.isLast())
		{
			boolean result = false;
			final List<LegionMemberEx> curentMembers = splits.getNext();
			if (isFirst && (curentMembers.size() < totalMembers.size()))
			{
				result = true;
			}
			
			PacketSendUtility.sendPacket(player, new SM_LEGION_MEMBERLIST(curentMembers, result, isFirst));
			isFirst = false;
		}
		
		// Send legion member info to the members
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(player, false, 1300260, player.getName()), player.getObjectId());
		PacketSendUtility.sendPacket(player, new SM_LEGION_ADD_MEMBER(player, false, 0, ""));
		
		// Send legion emblem information
		final LegionEmblem legionEmblem = legion.getLegionEmblem();
		PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b(), legionEmblem.getEmblemType()), true);
		
		// Send legion edit
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x08));
		
		// Update legion member's appearance in game
		PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(), legion.getLegionId(), legion.getLegionName(), player.getLegionMember().getRank().getRankId()), true);
		legion.addBonus();
	}
	
	/**
	 * Removes a member from the legion and updates the database.<br>
	 * This method handles cache removal, packet broadcasting, and bonus resets.
	 * @param charName The unique name of the character to remove.
	 * @param kick If {@code true}, sends a kick notification; otherwise, sends a leave notification.
	 * @param playerName The name displayed in the notification message.
	 * @return {@code true} if the member was successfully removed, or {@code false} if the character was not found.
	 */
	private boolean removeLegionMember(String charName, boolean kick, String playerName)
	{
		/**
		 * Get LegionMemberEx from cache or database if offline
		 */
		final LegionMemberEx legionMember = getLegionMemberEx(charName);
		if (legionMember == null)
		{
			log.error("[LegionService] Char name does not exist in legion member table: " + charName);
			return false;
		}
		
		/**
		 * Delete legion member from database and cache
		 */
		deleteLegionMemberFromDB(legionMember);
		
		/**
		 * If player is online send packet and reset legion member
		 */
		final Player player = world.findPlayer(charName);
		if (player != null)
		{
			PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(), 0, "", 2), true);
		}
		
		final Legion legion = legionMember.getLegion();
		/**
		 * Send packets to legion members
		 */
		if (kick)
		{
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_LEAVE_MEMBER(1300247, legionMember.getObjectId(), playerName, legionMember.getName()));
		}
		else
		{
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_LEAVE_MEMBER(900699, legionMember.getObjectId(), charName));
		}
		
		legion.removeBonus();
		return true;
	}
	
	/**
	 * Processes various requests related to character names and legion actions.<br>
	 * This method handles invites, kicks, rank appointments, and nickname changes.<br>
	 * It identifies the target player based on the provided name.
	 * @param exOpcode The operation code determining which action to perform.
	 * @param activePlayer The {@link Player} who is initiating the request.
	 * @param charName The name of the character being targeted or modified.
	 * @param newNickname The new nickname to assign if the opcode is for changing names.
	 * @param rank The rank value used when appointing a new legion member.
	 */
	public void handleCharNameRequest(int exOpcode, Player activePlayer, String charName, String newNickname, int rank)
	{
		final Legion legion = activePlayer.getLegion();
		
		charName = Util.convertName(charName);
		final Player targetPlayer = world.findPlayer(charName);
		
		switch (exOpcode)
		{
			/**
			 * Invite to legion *
			 */
			case 0x01:
				if (targetPlayer != null)
				{
					if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.GUILD))
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_INVITE_GUILD(charName));
						return;
					}
					
					invitePlayerToLegion(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_NO_USER_TO_INVITE);
				}
				break;
			/**
			 * Kick member from legion *
			 */
			case LEGION_ACTION_KICK:
				/**
				 * Check if player can be kicked
				 */
				if (legionRestrictions.canKickPlayer(activePlayer, charName))
				{
					if (removeLegionMember(charName, true, activePlayer.getName()))
					{
						// send packet to members?
						if (targetPlayer != null)
						{
							PacketSendUtility.sendPacket(targetPlayer, new SM_LEGION_LEAVE_MEMBER(1300246, 0, legion.getLegionName()));
							targetPlayer.resetLegionMember();
						}
					}
				}
				
				if (legion.hasBonus())
				{
					PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, false));
				}
				break;
			/**
			 * Appoint a new Brigade General *
			 */
			case 0x05:
				if (targetPlayer != null)
				{
					appointBrigadeGeneral(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_NO_USER_TO_INVITE);
				}
				break;
			/**
			 * Appoint Centurion/Legionairy *
			 */
			case 0x06:
				if (targetPlayer != null)
				{
					appointRank(activePlayer, targetPlayer, rank);
				}
				else
				{
					appointRank(activePlayer, charName, rank);
				}
				break;
			/**
			 * Set nickname *
			 */
			case 0x0F:
				changeNickname(activePlayer, charName, newNickname);
				break;
		}
	}
	
	/**
	 * Processes incoming requests related to legion management.<br>
	 * This method handles specific actions based on the provided {@code exOpcode}.<br>
	 * It updates information like announcements or self-introductions.
	 * @param exOpcode The operation code identifying the type of request.
	 * @param activePlayer The {@link Player} who sent the request.
	 * @param text The string content provided by the player for the update.
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer, String text)
	{
		switch (exOpcode)
		{
			/**
			 * Edit announcements *
			 */
			case 0x09:
				changeAnnouncement(activePlayer, text);
				break;
			/**
			 * Change self introduction *
			 */
			case 0x0A:
				changeSelfIntro(activePlayer, text);
				break;
		}
	}
	
	/**
	 * Processes incoming requests related to the {@link Legion} system.<br>
	 * This method handles specific actions like leaving a legion or leveling it up.<br>
	 * It checks permissions and updates the game state based on the provided opcode.
	 * @param exOpcode The operation code identifying the type of request.
	 * @param activePlayer The {@link Player} who is sending the request.
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer)
	{
		switch (exOpcode)
		{
			/**
			 * Leave legion *
			 */
			case 0x02:
				if (legionRestrictions.canLeave(activePlayer))
				{
					if (removeLegionMember(activePlayer.getName(), false, ""))
					{
						final Legion legion = activePlayer.getLegion();
						PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion.getLegionName()));
						activePlayer.resetLegionMember();
						if (legion.hasBonus())
						{
							PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, false));
						}
					}
				}
				break;
			/**
			 * Level legion up *
			 */
			case 0x0E:
				requestChangeLevel(activePlayer);
				break;
		}
	}
	
	/**
	 * Removes a {@link Player} from their current legion.<br>
	 * This method handles the removal logic and sends the necessary packets to the client.<br>
	 * It also resets the player's legion member data.
	 * @param player The {@link Player} object to be removed from the legion.
	 * @return {@code true} if the player was successfully removed, otherwise {@code false}.
	 */
	public boolean removePlayerFromLegionAsItself(Player player)
	{
		if (removeLegionMember(player.getName(), false, ""))
		{
			final Legion legion = player.getLegion();
			PacketSendUtility.sendPacket(player, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion.getLegionName()));
			player.resetLegionMember();
			if (legion.hasBonus())
			{
				PacketSendUtility.sendPacket(player, new SM_ICON_INFO(1, false));
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Handles the logic for a player logging into the game.<br>
	 * This method updates the legion status and sends necessary packets to the player.<br>
	 * It also notifies other members of the player's online status.
	 * @param activePlayer The {@code Player} object representing the user who just logged in.
	 */
	public void onLogin(Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		
		// Tell all legion members player has come online
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer, 0, ""), activePlayer.getObjectId());
		
		// Notify legion members player has logged in
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.STR_MSG_NOTIFY_LOGIN_GUILD(activePlayer.getName()), activePlayer.getObjectId());
		
		// Send member add to player
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(activePlayer, true, 0, ""));
		
		// Send legion info packets
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_INFO(legion));
		final ArrayList<LegionMemberEx> totalMembers = loadLegionMemberExList(legion, null);
		
		// Send member list to player
		final ListSplitter<LegionMemberEx> splits = new ListSplitter<>(totalMembers, 128);
		
		// Send the member list to the new legion member
		boolean isFirst = true;
		while (!splits.isLast())
		{
			boolean result = false;
			final List<LegionMemberEx> curentMembers = splits.getNext();
			if (isFirst && (curentMembers.size() < totalMembers.size()))
			{
				result = true;
			}
			
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_MEMBERLIST(curentMembers, result, isFirst));
			isFirst = false;
		}
		
		// Send current announcement to player
		displayLegionMessage(activePlayer, legion.getCurrentAnnouncement());
		
		if (legion.isDisbanding())
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_EDIT(0x06, legion.getDisbandTime()));
		}
		
		legion.increaseOnlineMembersCount();
		if (legion.hasBonus())
		{
			PacketSendUtility.sendPacket(activePlayer, new SM_ICON_INFO(1, true));
		}
		else
		{
			legion.addBonus();
		}
		
		// show message to player on login "have buff"
		if (legion.getOnlineMembersCount() >= LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS)
		{
			PacketSendUtility.sendYellowMessageOnCenter(activePlayer, "[LegionService] : Legion Bonus aviable.");
		}
		
		// show message to all when yeach player log in "no bonus"
		legion.removeBonusMassage();
		
		// show message to player on login "no bonus"
		if (legion.getOnlineMembersCount() < LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS)
		{
			PacketSendUtility.sendYellowMessageOnCenter(activePlayer, "[LegionService] : Invite more Members to get Legion Bonus.");
		}
	}
	
	/**
	 * Handles the logic when a {@link Player} logs out.<br>
	 * It updates the legion status and saves member data.<br>
	 * This ensures that online counts and bonuses are correctly adjusted.
	 * @param player The {@code Player} who is logging out.
	 */
	public void onLogout(Player player)
	{
		final Legion legion = player.getLegion();
		final LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
		if (lwh.getWhUser() == player.getObjectId())
		{
			lwh.setWhUser(0);
		}
		
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(player));
		storeLegion(legion);
		storeLegionMember(player.getLegionMember());
		storeLegionMemberExInCache(player);
		storeLegionAnnouncements(legion);
		legion.decreaseOnlineMembersCount();
		legion.removeBonus();
	}
	
	/**
	 * Clears all data from the internal memory caches.<br>
	 * This removes all entries from {@code allCachedLegions}.<br>
	 * This also clears all entries from {@code allCachedLegionMembers}.
	 */
	public void clearCaches()
	{
		allCachedLegions.clear();
		allCachedLegionMembers.clear();
	}
	
	/**
	 * This class contains all restrictions for legion features
	 * @author Simple
	 */
	private class LegionRestrictions
	{
		/**
		 * Static Emblem information *
		 */
		private static final int MIN_EMBLEM_ID = 0;
		private static final int MAX_EMBLEM_ID = 49;
		
		/**
		 * This method checks all restrictions for legion creation
		 * @param activePlayer
		 * @param legionName
		 * @return true if allow to create a legion
		 */
		private boolean canCreateLegion(Player activePlayer, String legionName)
		{
			/* Some reasons why legions can' be created */
			if (!isValidName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_INVALID_GUILD_NAME);
				return false;
			} // STR_GUILD_CREATE_TOO_FAR_FROM_CREATOR_NPC TODO
			else if (!isFreeName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_SAME_GUILD_EXIST);
				return false;
			}
			else if (activePlayer.isLegionMember())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_ALREADY_BELONGS_TO_GUILD);
				return false;
			}
			else if (activePlayer.getInventory().getKinah() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CREATE_NOT_ENOUGH_MONEY);
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for invite player to legion
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can invite player
		 */
		private boolean canInvitePlayer(Player activePlayer, Player targetPlayer)
		{
			final Legion legion = activePlayer.getLegion();
			if (activePlayer.getLifeStats().isAlreadyDead())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CANT_INVITE_WHEN_DEAD);
				return false;
			}
			
			if (isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_CAN_NOT_INVITE_SELF);
				return false;
			}
			else if (targetPlayer.isLegionMember())
			{
				if (legion.isMember(targetPlayer.getObjectId()))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_IS_MY_GUILD_MEMBER(targetPlayer.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_INVITE_HE_IS_OTHER_GUILD_MEMBER(targetPlayer.getName()));
				}
				
				return false;
			}
			else if (!activePlayer.getLegionMember().hasRights(LegionPermissionsMask.INVITE))
			{
				// No rights to invite
				return false;
			}
			else if ((activePlayer.getRace() != targetPlayer.getRace()) && !LegionConfig.LEGION_INVITEOTHERFACTION)
			{
				// Not Same Race
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for kicking a player from a legion
		 * @param activePlayer
		 * @param charName
		 * @return true if can kick player
		 */
		private boolean canKickPlayer(Player activePlayer, String charName)
		{
			/**
			 * Get LegionMemberEx from cache or database if offline
			 */
			final LegionMemberEx legionMember = getLegionMemberEx(charName);
			if (legionMember == null)
			{
				log.error("[LegionService] Char name does not exist in legion member table: " + charName);
				return false;
			}
			
			// TODO: Can not kick during a war!!
			// STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH
			final Legion legion = activePlayer.getLegion();
			
			if (isSelf(activePlayer, legionMember.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_CANT_BANISH_SELF);
				return false;
			}
			else if (legionMember.isBrigadeGeneral())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_CAN_BANISH_MASTER);
				return false;
			}
			else if (legionMember.getRank() == activePlayer.getLegionMember().getRank())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			else if (!legion.isMember(legionMember.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			else if (!activePlayer.getLegionMember().hasRights(LegionPermissionsMask.KICK))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_BANISH_DONT_HAVE_RIGHT_TO_BANISH);
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for appointing brigade general
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can appoint brigade general
		 */
		private boolean canAppointBrigadeGeneral(Player activePlayer, Player targetPlayer)
		{
			final Legion legion = activePlayer.getLegion();
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT);
				return false;
			}
			
			if (isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_ERROR_SELF);
				return false;
			}
			else if (!legion.isMember(targetPlayer.getObjectId())) // not in same legion
			{
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for appointing rank
		 * @param activePlayer
		 * @param targetObjId
		 * @return true if can appoint rank
		 */
		private boolean canAppointRank(Player activePlayer, int targetObjId)
		{
			final Legion legion = activePlayer.getLegion();
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT);
				return false;
			}
			
			if (isSelf(activePlayer, targetObjId))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_MASTER_ERROR_SELF);
				return false;
			}
			else if (!legion.isMember(targetObjId))
			{
				// not in same legion
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for changing self intro
		 * @param activePlayer
		 * @param newSelfIntro
		 * @return true if allowed to change self intro
		 */
		private boolean canChangeSelfIntro(Player activePlayer, String newSelfIntro)
		{
			return isValidSelfIntro(newSelfIntro);
		}
		
		/**
		 * This method checks all restrictions for changing legion level
		 * @param activePlayer
		 * @return true if allowed to change legion level
		 */
		private boolean canChangeLevel(Player activePlayer)
		{
			final Legion legion = activePlayer.getLegion();
			final int levelContributionPrice = legion.getContributionPrice();
			
			if (legion.getLegionLevel() == MAX_LEGION_LEVEL)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_CANT_LEVEL_UP);
				return false;
			}
			else if (LegionConfig.ENABLE_GUILD_TASK_REQ && (legion.getLegionLevel() >= 5))
			{
				if (!ChallengeTaskService.getInstance().canRaiseLegionLevel(legion.getLegionId(), legion.getLegionLevel()))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_LEVEL_UP_CHALLENGE_TASK(legion.getLegionLevel()));
					return false;
				}
			}
			else if (activePlayer.getInventory().getKinah() < legion.getKinahPrice())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_MONEY);
				return false;
			}
			else if (!legion.hasRequiredMembers())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_MEMBER);
				return false;
			}
			else if (legion.getContributionPoints() < levelContributionPrice)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_CHANGE_LEVEL_NOT_ENOUGH_POINT);
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method will check all restrictions for changing nickname
		 * @param legion
		 * @param targetObjectId
		 * @param newNickname
		 * @return true if allowed to change nickname of target player
		 */
		private boolean canChangeNickname(Legion legion, int targetObjectId, String newNickname)
		{
			if (!isValidNickname(newNickname))
			{
				// invalid nickname
				return false;
			}
			else if (!legion.isMember(targetObjectId))
			{
				// not in same legion
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for changing announcements
		 * @param legionMember
		 * @param announcement
		 * @return true if can change announcement
		 */
		private boolean canChangeAnnouncement(LegionMember legionMember, String announcement)
		{
			return legionMember.hasRights(LegionPermissionsMask.EDIT) && (announcement.isEmpty() || isValidAnnouncement(announcement));
		}
		
		/**
		 * This method checks all restrictions for disband legion
		 * @param activePlayer
		 * @param legion
		 * @return true if can disband legion
		 */
		private boolean canDisbandLegion(Player activePlayer, Legion legion)
		{
			// TODO: Can't disband during a war!!
			// TODO: Can't disband legion with fortress or hideout!!
			if (legion == null)
			{
				return false;
			}
			
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ONLY_MASTER_CAN_DISPERSE);
				return false;
			}
			else if (legion.getLegionWarehouse().size() > 0)
			{
				// TODO: Can't disband during using legion warehouse!!
				return false;
			}
			else if (legion.isDisbanding())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ALREADY_REQUESTED);
				return false;
			}
			else if (legion.getLegionWarehouse().size() > 0)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_CANT_DISPERSE_GUILD_STORE_ITEM_IN_WAREHOUSE);
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for leaving
		 * @param activePlayer
		 * @return true if allowed to leave
		 */
		private boolean canLeave(Player activePlayer)
		{
			if (isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_LEAVE_MASTER_CANT_LEAVE_BEFORE_CHANGE_MASTER);
				return false;
			}
			
			return true;
		}
		
		public boolean canChangeLegionJoinSetting(Player activePlayer)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for recreate legion
		 * @param activePlayer
		 * @param legion
		 * @return true if allowed to recreate legion
		 */
		private boolean canRecreateLegion(Player activePlayer, Legion legion)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_GUILD_DISPERSE_ONLY_MASTER_CAN_DISPERSE);
				return false;
			}
			else if (!legion.isDisbanding())
			{
				// Legion is not disbanding
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for upload emblem info
		 * @param activePlayer
		 * @return true if allowed to upload emblem info
		 */
		private boolean canUploadEmblemInfo(Player activePlayer)
		{
			// TODO: System Messages
			if (!isBrigadeGeneral(activePlayer)) // Not legion leader
			{
				return false;
			}
			else if (activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if (activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Already uploading emblem, reset uploading
				activePlayer.getLegion().getLegionEmblem().setUploading(false);
				return false;
			}
			
			return true;
		}
		
		/**
		 * This method checks all restrictions for uploading emblem
		 * @param activePlayer
		 * @return true if allowed to upload emblem
		 */
		private boolean canUploadEmblem(Player activePlayer)
		{
			if (!isBrigadeGeneral(activePlayer))
			{
				// Not legion leader
				return false;
			}
			else if (activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if (!activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Not uploading emblem
				return false;
			}
			
			return true;
		}
		
		/**
		 * @param player
		 * @return
		 */
		public boolean canOpenWarehouse(Player player)
		{
			if (!player.isLegionMember())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NO_GUILD_TO_DEPOSIT);
				return false;
			}
			
			final Legion legion = player.getLegion();
			final LegionWarehouse legWh = legion.getLegionWarehouse();
			final int whUser = legWh.getWhUser();
			final int playerId = player.getObjectId();
			if (legion.isDisbanding())
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_CANT_USE_WHILE_DISPERSE);
				return false;
			}
			else if (!LegionConfig.LEGION_WAREHOUSE)
			{
				// Legion Warehouse not enabled
				return false;
			}
			else if ((whUser != playerId) && (legWh.getWhUser() != 0))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GUILD_WAREHOUSE_IN_USE);
				return false;
			}
			
			legWh.setWhUser(player.getObjectId());
			return true;
		}
		
		/**
		 * @param activePlayer
		 * @param legionId
		 * @param emblemId
		 * @return
		 */
		public boolean canStoreLegionEmblem(Player activePlayer, int legionId, int emblemId)
		{
			final Legion legion = activePlayer.getLegion();
			if ((emblemId < MIN_EMBLEM_ID) || (emblemId > MAX_EMBLEM_ID))
			{
				// Not a valid emblemId
				return false;
			}
			else if (legionId != legion.getLegionId())
			{
				// legion id not equal
				return false;
			}
			else if (legion.getLegionLevel() < 2)
			{
				// legion level not high enough
				return false;
			}
			else if (activePlayer.getInventory().getKinah() < LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH));
				return false;
			}
			
			return true;
		}
		
		/**
		 * Checks if player is brigade general and returns message if not
		 * @param player
		 * @return
		 */
		private boolean isBrigadeGeneral(Player player)
		{
			return player.getLegionMember().isBrigadeGeneral();
		}
		
		/**
		 * Checks if target is same as current player
		 * @param player
		 * @param targetObjId
		 * @return
		 */
		private boolean isSelf(Player player, int targetObjId)
		{
			return player.sameObjectId(targetObjId);
		}
		
		/**
		 * Checks if name is already taken or not
		 * @param name character name
		 * @return true if is free, false in other case
		 */
		private boolean isFreeName(String name)
		{
			return !DAOManager.getDAO(LegionDAO.class).isNameUsed(name);
		}
		
		/**
		 * Checks if a self intro is valid. It should contain only english letters
		 * @param name character name
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidSelfIntro(String name)
		{
			return LegionConfig.SELF_INTRO_PATTERN.matcher(name).matches();
		}
		
		/**
		 * Checks if a nickname is valid. It should contain only english letters
		 * @param name character name
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidNickname(String name)
		{
			return LegionConfig.NICKNAME_PATTERN.matcher(name).matches();
		}
		
		/**
		 * Checks if a announcement is valid. It should contain only english letters
		 * @param name announcement
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidAnnouncement(String name)
		{
			return LegionConfig.ANNOUNCEMENT_PATTERN.matcher(name.replaceAll("\\r\\n", "")).matches();
		}
	}
	
	/**
	 * Records the history of an item movement for a {@link Player}.<br>
	 * This method updates the legion history based on the source and destination storage types.
	 * @param player The {@link Player} performing the action.
	 * @param itemId The unique identifier of the item.
	 * @param count The quantity of the item being moved.
	 * @param sourceStorage The {@link IStorage} where the item is coming from.
	 * @param destStorage The {@link IStorage} where the item is going to.
	 */
	public void addWHItemHistory(Player player, int itemId, long count, IStorage sourceStorage, IStorage destStorage)
	{
		final Legion legion = player.getLegion();
		if (legion != null)
		{
			final String description = Integer.toString(itemId) + ":" + Long.toString(count);
			if (sourceStorage.getStorageType() == StorageType.LEGION_WAREHOUSE)
			{
				LegionService.getInstance().addHistory(legion, player.getName(), LegionHistoryType.ITEM_WITHDRAW, 2, description);
			}
			else if (destStorage.getStorageType() == StorageType.LEGION_WAREHOUSE)
			{
				LegionService.getInstance().addHistory(legion, player.getName(), LegionHistoryType.ITEM_DEPOSIT, 2, description);
			}
		}
	}
	
	/**
	 * Processes a request from a {@code Player} to search for legions.<br>
	 * It filters the cached legions based on the provided type and name.<br>
	 * The results are sent back to the player via a packet.
	 * @param player The {@code Player} who is performing the search.
	 * @param type The search category used to filter the results.
	 * @param legionName The string used to filter legions by name.
	 */
	public void handleLegionSearch(Player player, int type, String legionName)
	{
		final List<Legion> matchingLegions = new ArrayList<>();
		switch (type)
		{
			case 0:
				for (Legion legion : allCachedLegions.getAllLegions())
				{
					if (getBrigadeGeneralRace(legion).getRace() == player.getRace())
					{
						matchingLegions.add(legion);
					}
				}
				break;
			case 1:
				for (Legion legion : allCachedLegions.getAllLegions())
				{
					if ((getBrigadeGeneralRace(legion).getRace() == player.getRace()) && legion.getLegionName().toLowerCase().contains(legionName.toLowerCase()))
					{
						matchingLegions.add(legion);
					}
				}
				break;
		}
		/*
		 * log.info("Sending LegionSearch Size is :"+matchingLegions.size()+" Legions :"); for (Legion legion : matchingLegions) { log.info("* LegionName : "+legion.getLegionName()+"(id:"+legion.getLegionId()+") BrigadeGeneral: "+getBrigadeGeneralName(legion)+ " Description: "+legion.getLegionDiscription()+" joinType: "+legion.getLegionJoinType()+" Min.Lv: "+legion.getMinLevel()); }
		 */
		PacketSendUtility.sendPacket(player, new SM_LEGION_SEARCH(matchingLegions));
	}
	
	/**
	 * Updates the description for the {@link Legion} of a specific player.<br>
	 * This method checks if the {@code Player} has permission to change settings.<br>
	 * It updates the database and sends a packet to the client.
	 * @param player The {@code Player} whose legion description will be updated.
	 * @param description The new {@code String} description to set for the legion.
	 */
	public void setJoinDescription(Player player, String description)
	{
		final Legion legion = player.getLegion();
		
		if (legion == null)
		{
			return;
		}
		
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setDescription(description);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0C, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	/**
	 * Updates the join type for a player's legion.<br>
	 * This method checks if the {@code Player} is allowed to change settings.<br>
	 * It then updates the {@link Legion} and sends a packet to the client.
	 * @param player The {@code Player} object whose legion will be updated.
	 * @param joinType The new {@code int} value for the join type.
	 */
	public void setJoinType(Player player, int joinType)
	{
		final Legion legion = player.getLegion();
		
		if (legion == null)
		{
			return;
		}
		
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setJoinType(joinType);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0D, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	/**
	 * Sets the minimum level required to join a legion.<br>
	 * This method updates the requirements for the {@link Player}'s current legion.<br>
	 * It also sends a packet to the player and updates the database.
	 * @param player The {@code Player} object who owns the legion.
	 * @param minLevel The new minimum level required to join.
	 */
	public void setJoinMinLevel(Player player, int minLevel)
	{
		final Legion legion = player.getLegion();
		
		if (legion == null)
		{
			return;
		}
		
		if (legionRestrictions.canChangeLegionJoinSetting(player))
		{
			legion.setMinJoinLevel(minLevel);
			PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x0E, legion));
			DAOManager.getDAO(LegionDAO.class).updateLegionDescription(legion);
		}
	}
	
	/**
	 * Sends a packet to the player containing information about joining a specific legion.<br>
	 * If the {@code legionId} is invalid, it sends an empty response.<br>
	 * Otherwise, it retrieves the {@link Legion} and sends its details.
	 * @param player The {@code Player} who will receive the packet.
	 * @param legionId The unique identifier of the legion to request information for.
	 */
	public void sendLegionJoinRequestPacket(Player player, int legionId)
	{
		if (legionId <= 0)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAYER_LEGION_JOIN_REQUEST_INFO(0, ""));
		}
		else
		{
			final Legion legion = getLegion(legionId);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_LEGION_JOIN_REQUEST_INFO(legion.getLegionId(), legion.getLegionName()));
		}
	}
	
	/**
	 * Sends a packet to the player regarding their legion join request.<br>
	 * This method is called when a {@link Player} enters the world.<br>
	 * It checks if the player has a pending request and sends the corresponding info.
	 * @param player The {@code Player} object receiving the packet.
	 */
	public void sendLegionJoinRequestPacketonEnterWorld(Player player)
	{
		final int legionId = player.getCommonData().getJoinRequestLegionId();
		if (legionId <= 0)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAYER_LEGION_JOIN_REQUEST_INFO(0, ""));
		}
		else
		{
			final Legion legion = getLegion(legionId);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_LEGION_JOIN_REQUEST_INFO(legion.getLegionId(), legion.getLegionName()));
		}
	}
	
	/**
	 * Processes a request from a {@code Player} to join a specific legion.<br>
	 * This method handles different types of join actions based on the {@code joinType}.<br>
	 * It interacts with the {@link Legion} object and notifies relevant members.
	 * @param player The {@code Player} who is attempting to join the legion.
	 * @param legionId The unique identifier for the target {@code Legion}.
	 * @param joinType The type of join action being performed.
	 * @param joinRequestMsg A custom message sent along with the join request.
	 */
	public void handleLegionJoinRequest(Player player, int legionId, int joinType, String joinRequestMsg)
	{
		final Legion legion = getLegion(legionId);
		
		if (legion == null)
		{
			return;
		}
		
		// log.info("jointype :" + joinType);
		switch (joinType)
		{
			case 0: // send Request
				player.getCommonData().setJoinRequestLegionId(legionId);
				sendLegionJoinRequestPacket(player, legionId);
				
				final LegionJoinRequest ljr = new LegionJoinRequest(legionId, player, joinRequestMsg);
				legion.addJoinRequest(ljr);
				
				final Player brigadeGeneral = getBrigadeGeneral(legion);
				if (brigadeGeneral != null)
				{
					PacketSendUtility.sendPacket(brigadeGeneral, new SM_LEGION_JOIN_REQUEST_FROM_PLAYER(ljr));
				}
				break;
			case 1:
				directAddPlayer(legion, player);
				break;
			default:
				PacketSendUtility.sendMessage(player, "This Legion isn't recruiting new members..");
				break;
		}
	}
	
	/**
	 * Cancels a pending join request for a player in a specific legion.<br>
	 * This method clears the request from the {@code Player} object.<br>
	 * It also removes the player from the legion's internal request map.<br>
	 * Finally, it notifies the brigade general of the cancellation.
	 * @param player The {@code Player} who is canceling their join request.
	 * @param legionId The unique identifier for the {@code Legion}.
	 */
	public void handleJoinRequestCancel(Player player, int legionId)
	{
		final Legion legion = getLegion(legionId);
		player.clearJoinRequest();
		sendLegionJoinRequestPacket(player, 0);
		
		if (legion.getJoinRequestMap().containsKey(player.getObjectId()))
		{
			legion.getJoinRequestMap().remove(player.getObjectId());
		}
		
		final Player bg = getBrigadeGeneral(legion);
		if (bg != null)
		{
			PacketSendUtility.sendPacket(bg, new SM_LEGION_ANSWER_JOIN_REQUEST(player.getObjectId(), false));
		}
		
	}
	
	/**
	 * Processes the response to a legion join request for a specific player.<br>
	 * It checks the current {@code JoinRequestState} of the player's common data.<br>
	 * If accepted, it adds the player to the legion or sends a server change message.<br>
	 * If denied, it notifies the player and clears the request state.
	 * @param player The {@link Player} who is receiving the join request response.
	 */
	public void handleJoinRequestGetAnswer(Player player)
	{
		final PlayerCommonData pcd = player.getCommonData();
		switch (pcd.getJoinRequestState())
		{
			case ACCEPTED:
				if (!player.isOnFastTrack())
				{
					directAddPlayer(pcd.getJoinRequestLegionId(), player);
					handleJoinRequestCancel(player, player.getCommonData().getJoinRequestLegionId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEGION_APPLICATION_ACCEPTED);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEGION_JOIN_SERVER_CHANGE);
				}
				break;
			case DENIED:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEGION_APPLICATION_DENIED);
				handleJoinRequestCancel(player, player.getCommonData().getJoinRequestLegionId());
				break;
			default:
				break;
		}
	}
	
	/**
	 * Processes the response to a legion join request.<br>
	 * This method updates the state for both the requester and the general.<br>
	 * It handles cases where the player is offline or online.
	 * @param brigadeGeneral The {@code Player} who owns the legion and sent the answer.
	 * @param playerId The unique identifier of the player requesting to join.
	 * @param accept A boolean indicating if the request was granted as {@code true} or denied as {@code false}.
	 */
	public void handleJoinRequestGiveAnswer(Player brigadeGeneral, int playerId, boolean accept)
	{
		boolean playerOnline = true;
		final LegionJoinRequestState state = accept ? LegionJoinRequestState.ACCEPTED : LegionJoinRequestState.DENIED;
		
		final Legion legion = brigadeGeneral.getLegion();
		if (legion == null)
		{
			return;
		}
		
		final Player player = World.getInstance().findPlayer(playerId);
		if (player == null)
		{
			playerOnline = false;
			DAOManager.getDAO(PlayerDAO.class).updateLegionJoinRequestState(playerId, state);
			
			if (legion.getJoinRequestMap().containsKey(playerId))
			{
				legion.getJoinRequestMap().remove(playerId);
			}
		}
		
		// TODO Buggy
		PacketSendUtility.sendPacket(brigadeGeneral, new SM_LEGION_ANSWER_JOIN_REQUEST(playerId, accept));
		
		if (playerOnline && (player != null))
		{
			player.getCommonData().setJoinRequestState(state);
			handleJoinRequestGetAnswer(player);
		}
		
	}
	
	private static class SingletonHolder
	{
		protected static final LegionService instance = new LegionService();
	}
}
