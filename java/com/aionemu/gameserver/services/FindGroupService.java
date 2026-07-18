/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.FindGroup;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FIND_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service handles the logic for finding and managing player groups.<br>
 * It processes {@link SM_FIND_GROUP} packets to help players locate others.<br>
 * It manages group creation, disbanding, and member additions.
 * @author cura, MrPoke, teenwolf, Mobius
 */
public class FindGroupService
{
	private final Map<Integer, FindGroup> elyosRecruitFindGroups = new ConcurrentHashMap<>();
	private final Map<Integer, FindGroup> elyosApplyFindGroups = new ConcurrentHashMap<>();
	private final Map<Integer, FindGroup> asmodianRecruitFindGroups = new ConcurrentHashMap<>();
	private final Map<Integer, FindGroup> asmodianApplyFindGroups = new ConcurrentHashMap<>();
	
	/**
	 * Private constructor for the {@link FindGroupService} class.
	 */
	private FindGroupService()
	{
	}
	
	/**
	 * Handles find-group cleanup when a player is added to a group.<br>
	 * The player's own recruit/apply entries are removed, and if the group becomes full,<br>
	 * the group's recruit entry is removed as well.
	 * @param group The {@link PlayerGroup} the player joined.
	 * @param player The {@link Player} that was added.
	 */
	public void onPlayerAddedToGroup(PlayerGroup group, Player player)
	{
		removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		if (group.isFull())
		{
			removeFindGroup(group.getRace(), 0, group.getObjectId());
		}
	}
	
	/**
	 * Handles find-group cleanup when a group is created.<br>
	 * Migrates the creator's pending find-group entry into a recruit listing for the new group.
	 * @param player The {@link Player} that created the group.
	 */
	public void onGroupCreated(Player player)
	{
		FindGroup inviterFindGroup = removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		if (inviterFindGroup == null)
		{
			inviterFindGroup = removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		}
		
		if (inviterFindGroup != null)
		{
			addFindGroupList(player, 0x02, inviterFindGroup.getMessage(), inviterFindGroup.getGroupType());
		}
	}
	
	/**
	 * Handles find-group cleanup when a group is disbanded.<br>
	 * Removes the group's recruit listing.
	 * @param group The {@link PlayerGroup} that was disbanded.
	 */
	public void onGroupDisbanded(PlayerGroup group)
	{
		removeFindGroup(group.getRace(), 0, group.getTeamId());
	}
	
	/**
	 * Handles find-group cleanup when a player is added to an alliance.<br>
	 * The player's own recruit/apply entries are removed, and if the alliance becomes full,<br>
	 * the alliance's recruit entry is removed as well.
	 * @param alliance The {@link PlayerAlliance} the player joined.
	 * @param player The {@link Player} that was added.
	 */
	public void onPlayerAddedToAlliance(PlayerAlliance alliance, Player player)
	{
		removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		if (alliance.isFull())
		{
			removeFindGroup(alliance.getRace(), 0, alliance.getObjectId());
		}
	}
	
	/**
	 * Handles find-group cleanup when an alliance is created.<br>
	 * Migrates the creator's pending find-group entry into a recruit listing for the new alliance.
	 * @param player The {@link Player} that created the alliance.
	 */
	public void onAllianceCreated(Player player)
	{
		FindGroup inviterFindGroup = removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		if (inviterFindGroup == null)
		{
			inviterFindGroup = removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		}
		
		if (inviterFindGroup != null)
		{
			addFindGroupList(player, 0x02, inviterFindGroup.getMessage(), inviterFindGroup.getGroupType());
		}
	}
	
	/**
	 * Handles find-group cleanup when an alliance is disbanded.<br>
	 * Removes the alliance's recruit listing.
	 * @param alliance The {@link PlayerAlliance} that was disbanded.
	 */
	public void onAllianceDisbanded(PlayerAlliance alliance)
	{
		removeFindGroup(alliance.getRace(), 0, alliance.getTeamId());
	}
	
	/**
	 * Adds a new group to the find group list for a specific player.<br>
	 * This method handles different actions based on the player's race.<br>
	 * It also sends a system message and updates the client with the new list.
	 * @param player The {@link Player} object who is creating the group.
	 * @param action The type of action being performed.
	 * @param message The text message to display for the group.
	 * @param groupType The category or type of the group.
	 */
	public void addFindGroupList(Player player, int action, String message, int groupType)
	{
		AionObject object = null;
		if (player.isInTeam())
		{
			object = player.getCurrentTeam();
		}
		else
		{
			object = player;
		}
		
		final FindGroup findGroup = new FindGroup(object, message, groupType);
		final int objectId = object.getObjectId();
		switch (player.getRace())
		{
			case ELYOS:
				switch (action)
				{
					case 0x02:
						elyosRecruitFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400392));
						break;
					case 0x06:
						elyosApplyFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400393));
						break;
				}
				break;
			case ASMODIANS:
				switch (action)
				{
					case 0x02:
						asmodianRecruitFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400392));
						break;
					case 0x06:
						asmodianApplyFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400393));
						break;
				}
				break;
			default:
				break;
		}
		
		final Collection<FindGroup> findGroupList = new ArrayList<>();
		findGroupList.add(findGroup);
		
		PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(action, ((int) (System.currentTimeMillis() / 1000)), findGroupList));
	}
	
	/**
	 * Updates the details of an existing find group for a specific player.<br>
	 * This method modifies the message and type based on the provided action.
	 * @param player The {@link Player} who is interacting with the group.
	 * @param message The new text message to display for the group.
	 * @param action The specific action code being performed.
	 * @param groupType The category or type of the find group.
	 * @param objectId The unique identifier of the {@link FindGroup} to update.
	 */
	public void updateFindGroupList(Player player, String message, int action, int groupType, int objectId)
	{
		FindGroup findGroup = null;
		
		switch (player.getRace())
		{
			case ELYOS:
				switch (action)
				{
					case 0x03:
						findGroup = elyosRecruitFindGroups.get(objectId);
						findGroup.setMessage(message);
						findGroup.setGroupType(groupType);
						break;
					case 0x07:
						findGroup = elyosApplyFindGroups.get(objectId);
						findGroup.setMessage(message);
						findGroup.setGroupType(groupType);
						break;
				}
				break;
			case ASMODIANS:
				switch (action)
				{
					case 0x03:
						findGroup = asmodianRecruitFindGroups.get(objectId);
						findGroup.setMessage(message);
						findGroup.setGroupType(groupType);
						break;
					case 0x07:
						findGroup = asmodianApplyFindGroups.get(objectId);
						findGroup.setMessage(message);
						findGroup.setGroupType(groupType);
						break;
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * Retrieves a list of find groups based on the specified race and action.<br>
	 * This method filters groups by {@code Race} type and specific action codes.
	 * @param race The {@link Race} category to filter by.
	 * @param action The specific action identifier for the group search.
	 * @return A {@code Collection} of {@link FindGroup} objects or {@code null}.
	 */
	public Collection<FindGroup> getFindGroups(Race race, int action)
	{
		switch (race)
		{
			case ELYOS:
				switch (action)
				{
					case 0x00:
						return elyosRecruitFindGroups.values();
					case 0x04:
						return elyosApplyFindGroups.values();
					case 0xA:
						return Collections.emptyList();
				}
				break;
			case ASMODIANS:
				switch (action)
				{
					case 0x00:
						return asmodianRecruitFindGroups.values();
					case 0x04:
						return asmodianApplyFindGroups.values();
					case 0xA:
						return Collections.emptyList();
				}
				break;
			default:
				break;
		}
		
		return null;
	}
	
	/**
	 * Registers a group instance for a specific player.<br>
	 * This method handles the logic for auto-grouping based on an {@code instanceId}.<br>
	 * It sends a notification packet to the {@link Player} if a valid type is found.
	 * @param player The {@link Player} receiving the registration.
	 * @param action The specific action being performed.
	 * @param instanceId The unique identifier for the group instance.
	 * @param message A descriptive message to be sent.
	 * @param minMembers The minimum number of members required for the group.
	 * @param groupType The category or type of the group.
	 */
	public void registerInstanceGroup(Player player, int action, int instanceId, String message, int minMembers, int groupType)
	{
		final AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceId);
		if (agt != null)
		{
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceId, 1, 0, player.getName()));
		}
	}
	
	/**
	 * Sends the list of find groups to a specific player.<br>
	 * This method uses {@code sendPacket} to deliver an {@code SM_FIND_GROUP} packet.<br>
	 * It retrieves group data based on the player's race and the provided action.
	 * @param player The {@code Player} who will receive the packet.
	 * @param action The specific action type for filtering groups.
	 */
	public void sendFindGroups(Player player, int action)
	{
		PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(action, (int) (System.currentTimeMillis() / 1000), getFindGroups(player.getRace(), action)));
	}
	
	/**
	 * Removes a {@link FindGroup} from the active list based on the provided criteria.<br>
	 * This method identifies the group using the {@code race}, {@code action}, and {@code playerObjId}.<br>
	 * If the group is found, it broadcasts a removal packet to other players of the same race.
	 * @param race The character race associated with the find group.
	 * @param action The specific action type for the find group.
	 * @param playerObjId The unique identifier of the player object.
	 * @return The {@link FindGroup} object that was removed, or {@code null} if no group was found.
	 */
	public FindGroup removeFindGroup(Race race, int action, int playerObjId)
	{
		FindGroup findGroup = null;
		switch (race)
		{
			case ELYOS:
				switch (action)
				{
					case 0x00:
						findGroup = elyosRecruitFindGroups.remove(playerObjId);
						break;
					case 0x04:
						findGroup = elyosApplyFindGroups.remove(playerObjId);
						break;
				}
				break;
			case ASMODIANS:
				switch (action)
				{
					case 0x00:
						findGroup = asmodianRecruitFindGroups.remove(playerObjId);
						break;
					case 0x04:
						findGroup = asmodianApplyFindGroups.remove(playerObjId);
						break;
				}
				break;
			default:
				break;
		}
		
		if (findGroup != null)
		{
			PacketSendUtility.broadcastFilteredPacket(new SM_FIND_GROUP(action + 1, playerObjId, findGroup.getUnk()), object -> race == object.getRace());
		}
		
		return findGroup;
	}
	
	/**
	 * Cleans up the internal find group maps.<br>
	 * This method removes expired or invalid entries from all race categories.<br>
	 * It calls {@code Race, int)} for each specific group type.
	 */
	public void clean()
	{
		cleanMap(elyosRecruitFindGroups, Race.ELYOS, 0x00);
		cleanMap(elyosApplyFindGroups, Race.ELYOS, 0x04);
		cleanMap(asmodianRecruitFindGroups, Race.ASMODIANS, 0x00);
		cleanMap(asmodianApplyFindGroups, Race.ASMODIANS, 0x04);
	}
	
	/**
	 * Removes expired entries from the provided map.<br>
	 * It checks if a {@link FindGroup} has not been updated for over 3600 seconds.<br>
	 * Expired groups are removed using the {@code int, int)} method.
	 * @param map The {@code Map} containing the group data to be cleaned.
	 * @param race The {@code Race} associated with the groups.
	 * @param action The specific {@code action} type for filtering.
	 */
	private void cleanMap(Map<Integer, FindGroup> map, Race race, int action)
	{
		for (FindGroup group : map.values())
		{
			if ((group.getLastUpdate() + (60 * 60)) < (System.currentTimeMillis() / 1000))
			{
				removeFindGroup(race, action, group.getObjectId());
			}
		}
	}
	
	/**
	 * Provides the global instance of the {@link FindGroupService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The single shared instance of {@code FindGroupService}.
	 */
	public static FindGroupService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final FindGroupService instance = new FindGroupService();
	}
}
