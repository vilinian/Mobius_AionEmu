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
package com.aionemu.gameserver.model.team.legion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.model.Support;
import com.aionemu.gameserver.model.bonus_service.ServiceBuff;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ICON_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * Represents a player's legion within the game world.<br>
 * This class manages legion-specific data and properties for a {@link Player}.
 * @author Simple, CoolyT
 */
public class Legion
{
	/**
	 * Legion Information *
	 */
	private int legionId = 0;
	private String legionName = "";
	private int legionLevel = 1;
	private int legionRank = 0;
	private long contributionPoints = 0;
	private List<Integer> legionMembers = new ArrayList<>();
	private int onlineMembersCount = 0;
	private short deputyPermission = 0x1E0C;
	private short centurionPermission = 0x1C08;
	private short legionaryPermission = 0x1800;
	private short volunteerPermission = 0x800;
	private int disbandTime;
	private TreeMap<Timestamp, String> announcementList = new TreeMap<>();
	private LegionEmblem legionEmblem = new LegionEmblem();
	private LegionWarehouse legionWarehouse;
	private final SortedSet<LegionHistory> legionHistory;
	private final AtomicBoolean hasBonus = new AtomicBoolean(false);
	private final Map<Integer, LegionJoinRequest> joinRequestMap = new HashMap<>();
	private String description = "";
	private int minJoinLevel = 0;
	private int joinType = 0;
	private LegionTerritory territory;
	private ServiceBuff serviceBuff;
	
	/**
	 * Creates a new {@link Legion} instance with specific details.<br>
	 * This constructor initializes the object using the default values first.<br>
	 * It then sets the unique identifier and name for the legion.
	 * @param legionId The unique identification number for the legion.
	 * @param legionName The display name of the legion.
	 */
	public Legion(int legionId, String legionName)
	{
		this();
		this.legionId = legionId;
		this.legionName = legionName;
	}
	
	/**
	 * Creates a new instance of the {@code Legion} class.<br>
	 * This constructor initializes the default warehouse and history objects.
	 */
	public Legion()
	{
		legionWarehouse = new LegionWarehouse(this);
		legionHistory = new TreeSet<>((o1, o2) -> o1.getTime().getTime() < o2.getTime().getTime() ? 1 : -1);
	}
	
	/**
	 * Sets the unique identifier for the legion.<br>
	 * This value is used to identify which legion owns this location.
	 * @param legionId The {@code int} ID of the legion.
	 */
	public void setLegionId(int legionId)
	{
		this.legionId = legionId;
	}
	
	/**
	 * Retrieves the unique identifier for the player's legion.<br>
	 * This value is stored in the {@code legionId} field.
	 * @return The {@code int} ID of the legion.
	 */
	public int getLegionId()
	{
		return legionId;
	}
	
	/**
	 * Sets the name of the {@code Legion}.<br>
	 * This updates the internal {@code legionName} field.
	 * @param legionName The new name to assign to the legion.
	 */
	public void setLegionName(String legionName)
	{
		this.legionName = legionName;
	}
	
	/**
	 * Retrieves the name of the player's legion.<br>
	 * This value is stored in the {@code legionName} field.
	 * @return The name of the legion as a {@code String}.
	 */
	public String getLegionName()
	{
		return legionName;
	}
	
	/**
	 * Updates the list of members for this {@link Legion}.<br>
	 * This method replaces the current member list with a new one.
	 * @param legionMembers The new {@code ArrayList} of player IDs to assign.
	 */
	public void setLegionMembers(ArrayList<Integer> legionMembers)
	{
		this.legionMembers = legionMembers;
	}
	
	/**
	 * Retrieves the list of all members in this {@link Legion}.<br>
	 * Each member is represented by their unique ID.
	 * @return A {@code List<Integer>} containing the IDs of all legion members.
	 */
	public List<Integer> getLegionMembers()
	{
		return legionMembers;
	}
	
	/**
	 * Retrieves a list of all members currently logged into the game.<br>
	 * This method checks every member in the {@code legionMembers} list.<br>
	 * It uses {@code getInstance} to find active players.
	 * @return An {@code ArrayList} containing the {@link Player} objects that are online.
	 */
	public ArrayList<Player> getOnlineLegionMembers()
	{
		final ArrayList<Player> onlineLegionMembers = new ArrayList<>();
		for (int legionMemberObjId : legionMembers)
		{
			final Player onlineLegionMember = World.getInstance().findPlayer(legionMemberObjId);
			if (onlineLegionMember != null)
			{
				onlineLegionMembers.add(onlineLegionMember);
			}
		}
		
		return onlineLegionMembers;
	}
	
	/**
	 * Adds a new member to the legion.<br>
	 * This method checks if the player can join before adding them.
	 * @param playerObjId The unique identifier of the player to add.
	 * @return {@code true} if the member was added successfully, {@code false} otherwise.
	 */
	public boolean addLegionMember(int playerObjId)
	{
		if (canAddMember())
		{
			legionMembers.add(playerObjId);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a member from the legion list.<br>
	 * This method uses the {@code playerObjId} to identify which member to remove.<br>
	 * It updates the internal {@code legionMembers} collection.
	 * @param playerObjId The unique identifier of the player to remove.
	 */
	public void deleteLegionMember(int playerObjId)
	{
		legionMembers.remove(Integer.valueOf(playerObjId));
	}
	
	/**
	 * Retrieves the current number of members who are currently online.<br>
	 * This value is updated by {@code increaseOnlineMembersCount} and {@code decreaseOnlineMembersCount}.
	 * @return The total count of online legion members as an {@code int}.
	 */
	public int getOnlineMembersCount()
	{
		return onlineMembersCount;
	}
	
	/**
	 * Reduces the count of online members by 1.<br>
	 * This method updates the {@code onlineMembersCount} field.
	 */
	public void decreaseOnlineMembersCount()
	{
		onlineMembersCount--;
	}
	
	/**
	 * Increases the count of members currently online in the {@code Legion}.<br>
	 * This method updates the {@code onlineMembersCount} variable by adding 1.
	 */
	public void increaseOnlineMembersCount()
	{
		onlineMembersCount++;
	}
	
	/**
	 * Updates the permission levels for different ranks within the legion.<br>
	 * This method sets the permissions for deputies, centurions, legionaries, and volunteers.
	 * @param deputyPermission The new permission value for the deputy rank.
	 * @param centurionPermission The new permission value for the centurion rank.
	 * @param legionaryPermission The new permission value for the legionary rank.
	 * @param volunteerPermission The new permission value for the volunteer rank.
	 * @return Always returns {@code true} after successfully updating the permissions.
	 */
	public boolean setLegionPermissions(short deputyPermission, short centurionPermission, short legionaryPermission, short volunteerPermission)
	{
		this.deputyPermission = deputyPermission;
		this.centurionPermission = centurionPermission;
		this.legionaryPermission = legionaryPermission;
		this.volunteerPermission = volunteerPermission;
		return true;
	}
	
	/**
	 * Retrieves the permission level for deputies within the {@link Legion}.<br>
	 * This value is used to determine what actions a deputy can perform.
	 * @return the current {@code short} value of the deputy permission.
	 */
	public short getDeputyPermission()
	{
		return deputyPermission;
	}
	
	/**
	 * Retrieves the permission level for a Centurion.<br>
	 * This value is used to determine what actions a Centurion can perform within the legion.
	 * @return The {@code short} value representing the centurion permissions.
	 */
	public short getCenturionPermission()
	{
		return centurionPermission;
	}
	
	/**
	 * Retrieves the permission level for a {@code legionary}.<br>
	 * This value is used to determine what actions a member can perform.
	 * @return The current {@code short} permission value for legionaries.
	 */
	public short getLegionaryPermission()
	{
		return legionaryPermission;
	}
	
	/**
	 * Retrieves the permission level for volunteers.<br>
	 * This value is used to determine what actions a volunteer can perform within the {@link Legion}.
	 * @return The current {@code short} value of the volunteer permission.
	 */
	public short getVolunteerPermission()
	{
		return volunteerPermission;
	}
	
	/**
	 * Retrieves the current level of the player's legion.<br>
	 * This value is stored in the {@code legionLevel} field.
	 * @return The integer level of the legion.
	 */
	public int getLegionLevel()
	{
		return legionLevel;
	}
	
	/**
	 * Updates the current level of the {@code Legion}.<br>
	 * This method sets the internal {@code legionLevel} field.
	 * @param legionLevel The new level to assign to the legion.
	 */
	public void setLegionLevel(int legionLevel)
	{
		this.legionLevel = legionLevel;
	}
	
	/**
	 * Updates the rank of the {@code Legion}.<br>
	 * This method sets the internal {@code legionRank} value.
	 * @param legionRank The new rank to assign to the legion.
	 */
	public void setLegionRank(int legionRank)
	{
		this.legionRank = legionRank;
	}
	
	/**
	 * Retrieves the current rank of the {@code Legion}.
	 * @return The integer value representing the rank.
	 */
	public int getLegionRank()
	{
		return legionRank;
	}
	
	/**
	 * Adds a specific amount of points to the total legion contribution.<br>
	 * This method updates the {@code contributionPoints} field.
	 * @param contributionPoints The number of points to add.
	 */
	public void addContributionPoints(long contributionPoints)
	{
		this.contributionPoints += contributionPoints;
	}
	
	/**
	 * Updates the total contribution points for this {@link Legion}.<br>
	 * This method sets the internal value of {@code contributionPoints}.
	 * @param contributionPoints The new number of points to assign.
	 */
	public void setContributionPoints(long contributionPoints)
	{
		this.contributionPoints = contributionPoints;
	}
	
	/**
	 * Retrieves the total number of contribution points for this {@link Legion}.
	 * @return The current amount of {@code contributionPoints}.
	 */
	public long getContributionPoints()
	{
		return contributionPoints;
	}
	
	/**
	 * Checks if the legion has enough members to reach the next level.<br>
	 * This method compares the current member count against requirements defined in {@link LegionConfig}.
	 * @return {@code true} if the required number of members is met, {@code false} otherwise.
	 */
	public boolean hasRequiredMembers()
	{
		final int memberSize = getLegionMembers().size();
		switch (getLegionLevel())
		{
			case 1:
				return memberSize >= LegionConfig.LEGION_LEVEL2_REQUIRED_MEMBERS;
			case 2:
				return memberSize >= LegionConfig.LEGION_LEVEL3_REQUIRED_MEMBERS;
			case 3:
				return memberSize >= LegionConfig.LEGION_LEVEL4_REQUIRED_MEMBERS;
			case 4:
				return memberSize >= LegionConfig.LEGION_LEVEL5_REQUIRED_MEMBERS;
			case 5:
				return memberSize >= LegionConfig.LEGION_LEVEL6_REQUIRED_MEMBERS;
			case 6:
				return memberSize >= LegionConfig.LEGION_LEVEL7_REQUIRED_MEMBERS;
			case 7:
				return memberSize >= LegionConfig.LEGION_LEVEL8_REQUIRED_MEMBERS;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the amount of {@code Kinah} required to upgrade the legion.<br>
	 * This value is determined by the current level returned by {@code getLegionLevel}.<br>
	 * It pulls data from the {@code LegionConfig} class.
	 * @return The required {@code Kinah} amount as an {@code int}.
	 */
	public int getKinahPrice()
	{
		switch (getLegionLevel())
		{
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_KINAH;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_KINAH;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_KINAH;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_KINAH;
			case 5:
				return LegionConfig.LEGION_LEVEL6_REQUIRED_KINAH;
			case 6:
				return LegionConfig.LEGION_LEVEL7_REQUIRED_KINAH;
			case 7:
				return LegionConfig.LEGION_LEVEL8_REQUIRED_KINAH;
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the contribution points required to reach the next level.<br>
	 * This value is determined based on the current {@code legionLevel}.<br>
	 * It pulls data from the {@link LegionConfig} class.
	 * @return The amount of contribution points needed for the next level as an {@code int}.
	 */
	public int getContributionPrice()
	{
		switch (getLegionLevel())
		{
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_CONTRIBUTION;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_CONTRIBUTION;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_CONTRIBUTION;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_CONTRIBUTION;
			case 5:
				return LegionConfig.LEGION_LEVEL6_REQUIRED_CONTRIBUTION;
			case 6:
				return LegionConfig.LEGION_LEVEL7_REQUIRED_CONTRIBUTION;
			case 7:
				return LegionConfig.LEGION_LEVEL8_REQUIRED_CONTRIBUTION;
		}
		
		return 0;
	}
	
	/**
	 * Checks if the legion has enough space to accept a new member.<br>
	 * This method compares the current member count against limits defined in {@link LegionConfig}.<br>
	 * It returns {@code true} if there is room available based on the current {@code legionLevel}.<br>
	 * It returns {@code false} if the limit is reached or if the level is not supported.
	 * @return {@code true} if a new member can be added, {@code false} otherwise.
	 */
	private boolean canAddMember()
	{
		final int memberSize = getLegionMembers().size();
		switch (getLegionLevel())
		{
			case 1:
				return memberSize < LegionConfig.LEGION_LEVEL1_MAX_MEMBERS;
			case 2:
				return memberSize < LegionConfig.LEGION_LEVEL2_MAX_MEMBERS;
			case 3:
				return memberSize < LegionConfig.LEGION_LEVEL3_MAX_MEMBERS;
			case 4:
				return memberSize < LegionConfig.LEGION_LEVEL4_MAX_MEMBERS;
			case 5:
				return memberSize < LegionConfig.LEGION_LEVEL5_MAX_MEMBERS;
			case 6:
				return memberSize < LegionConfig.LEGION_LEVEL6_MAX_MEMBERS;
			case 7:
				return memberSize < LegionConfig.LEGION_LEVEL7_MAX_MEMBERS;
			case 8:
				return memberSize < LegionConfig.LEGION_LEVEL8_MAX_MEMBERS;
		}
		
		return false;
	}
	
	/**
	 * Updates the list of announcements for this {@link Legion}.<br>
	 * The list uses a {@code TreeMap} to keep messages sorted by time.
	 * @param announcementList The new map of {@code Timestamp} and message strings.
	 */
	public void setAnnouncementList(TreeMap<Timestamp, String> announcementList)
	{
		this.announcementList = announcementList;
	}
	
	/**
	 * Adds a new message to the legion's announcement list.<br>
	 * The message is stored using its timestamp as the key.
	 * @param unixTime The {@code Timestamp} representing when the announcement was made.
	 * @param announcement The {@code String} content of the announcement.
	 */
	public void addAnnouncementToList(Timestamp unixTime, String announcement)
	{
		announcementList.put(unixTime, announcement);
	}
	
	/**
	 * Removes the oldest announcement from the list.<br>
	 * This method deletes the first entry in the {@code announcementList}.
	 */
	public void removeFirstEntry()
	{
		announcementList.remove(announcementList.firstEntry().getKey());
	}
	
	/**
	 * Retrieves the list of announcements for the legion.<br>
	 * The results are sorted by their {@code Timestamp}.
	 * @return A {@code TreeMap} containing the announcement timestamps and messages.
	 */
	public TreeMap<Timestamp, String> getAnnouncementList()
	{
		return announcementList;
	}
	
	/**
	 * Retrieves the most recent announcement from the legion.<br>
	 * It looks for the last entry in the {@code announcementList}.<br>
	 * Returns {@code null} if no announcements exist.
	 * @return An {@link Entry} containing the {@link Timestamp} and message, or {@code null}.
	 */
	public Entry<Timestamp, String> getCurrentAnnouncement()
	{
		if (announcementList.size() > 0)
		{
			return announcementList.lastEntry();
		}
		
		return null;
	}
	
	/**
	 * Sets the time when the legion will be disbanded.<br>
	 * This updates the {@code disbandTime} field of the {@link Legion}.
	 * @param disbandTime The new time value for disbanding.
	 */
	public void setDisbandTime(int disbandTime)
	{
		this.disbandTime = disbandTime;
	}
	
	/**
	 * Retrieves the time when the legion is scheduled to be disbanded.
	 * @return The {@code int} value representing the disband time.
	 */
	public int getDisbandTime()
	{
		return disbandTime;
	}
	
	/**
	 * Checks if the legion is currently in the process of being disbanded.<br>
	 * It returns {@code true} if the {@code disbandTime} is greater than {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the legion is disbanding, {@code false} otherwise.
	 */
	public boolean isDisbanding()
	{
		return disbandTime > 0;
	}
	
	/**
	 * Checks if a specific player is part of this legion.<br>
	 * It searches the {@code legionMembers} list for the provided ID.
	 * @param playerObjId The unique identifier of the player to check.
	 * @return {@code true} if the player is a member, {@code false} otherwise.
	 */
	public boolean isMember(int playerObjId)
	{
		return legionMembers.contains(playerObjId);
	}
	
	/**
	 * Sets the emblem for this {@link Legion}.<br>
	 * This updates the internal {@code legionEmblem} field.
	 * @param legionEmblem The new {@code LegionEmblem} to assign.
	 */
	public void setLegionEmblem(LegionEmblem legionEmblem)
	{
		this.legionEmblem = legionEmblem;
	}
	
	/**
	 * Retrieves the emblem associated with this {@link Legion}.
	 * @return the {@code LegionEmblem} object.
	 */
	public LegionEmblem getLegionEmblem()
	{
		return legionEmblem;
	}
	
	/**
	 * Sets the warehouse for this {@link Legion}.<br>
	 * This method updates the internal {@code legionWarehouse} field.
	 * @param legionWarehouse The {@code LegionWarehouse} object to assign.
	 */
	public void setLegionWarehouse(LegionWarehouse legionWarehouse)
	{
		this.legionWarehouse = legionWarehouse;
	}
	
	/**
	 * Retrieves the warehouse associated with this {@link Legion}.
	 * @return the {@code LegionWarehouse} object.
	 */
	public LegionWarehouse getLegionWarehouse()
	{
		return legionWarehouse;
	}
	
	/**
	 * Retrieves the total number of warehouse slots available for this legion.<br>
	 * The value is determined based on the current {@code legionLevel}.<br>
	 * It uses values defined in the {@link LegionConfig} class.
	 * @return The integer count of available warehouse slots.
	 */
	public int getWarehouseSlots()
	{
		switch (getLegionLevel())
		{
			case 1:
				return LegionConfig.LWH_LEVEL1_SLOTS;
			case 2:
				return LegionConfig.LWH_LEVEL2_SLOTS;
			case 3:
				return LegionConfig.LWH_LEVEL3_SLOTS;
			case 4:
				return LegionConfig.LWH_LEVEL4_SLOTS;
			case 5:
				return LegionConfig.LWH_LEVEL5_SLOTS;
			case 6:
				return LegionConfig.LWH_LEVEL6_SLOTS;
			case 7:
				return LegionConfig.LWH_LEVEL7_SLOTS;
			case 8:
				return LegionConfig.LWH_LEVEL8_SLOTS;
		}
		
		return LegionConfig.LWH_LEVEL1_SLOTS;
	}
	
	/**
	 * Retrieves the current level of the warehouse.<br>
	 * This value is calculated based on the {@code getLegionLevel} minus 1.
	 * @return The integer level of the warehouse.
	 */
	public int getWarehouseLevel()
	{
		return getLegionLevel() - 1;
	}
	
	/**
	 * Retrieves the history of the {@code Legion}.<br>
	 * This method returns all recorded historical data.
	 * @return a {@code Collection} containing {@link LegionHistory} objects.
	 */
	public Collection<LegionHistory> getLegionHistory()
	{
		return legionHistory;
	}
	
	/**
	 * Retrieves the history records that match a specific tab type.<br>
	 * This method filters the {@code legionHistory} collection based on the provided ID.
	 * @param tabType The unique identifier for the history category to filter by.
	 * @return A {@code Collection} of {@link LegionHistory} objects matching the requested type.
	 */
	public Collection<LegionHistory> getLegionHistoryByTabId(int tabType)
	{
		if (legionHistory.isEmpty())
		{
			return legionHistory;
		}
		
		return legionHistory.stream().filter(history -> history.getTabId() == tabType).collect(Collectors.toList());
	}
	
	/**
	 * Adds a new history record to the {@code Legion}.<br>
	 * This method stores the provided {@link LegionHistory} object in the internal collection.
	 * @param history The {@code LegionHistory} object to be added.
	 */
	public void addHistory(LegionHistory history)
	{
		legionHistory.add(history);
	}
	
	/**
	 * Applies a bonus to all online legion members.<br>
	 * This method checks the current member count against {@link LegionConfig}.<br>
	 * It grants specific buffs based on the number of active players.<br>
	 * It also sends an announcement message to every member.
	 */
	public void addBonus()
	{
		final ArrayList<Player> members = getOnlineLegionMembers();
		if ((members.size() >= LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS) && (members.size() <= 9))
		{
			if (hasBonus.compareAndSet(false, true))
			{
				for (Player member : members)
				{
					serviceBuff = new ServiceBuff(1);
					serviceBuff.applyEffect(member, 1);
				}
			}
		}
		else if ((members.size() >= LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS) && (members.size() <= 240))
		{
			if (hasBonus.compareAndSet(false, true))
			{
				for (Player member : members)
				{
					serviceBuff = new ServiceBuff(5);
					serviceBuff.applyEffect(member, 5);
					serviceBuff.endEffect(member, 1);
					PacketSendUtility.sendPacket(member, new SM_ICON_INFO(1, true));
				}
			}
		}
		
		for (Player member : members)
		{
			PacketSendUtility.sendMessage(member, "Legion: Bonus + 10 % XP , Craft , Gather");
		}
	}
	
	/**
	 * Removes the active bonus from all online legion members.<br>
	 * This method checks if the number of online members is below the required threshold.<br>
	 * If the condition is met, it ends the current effect for each {@link Player}.
	 */
	public void removeBonus()
	{
		final ArrayList<Player> members = getOnlineLegionMembers();
		if (members.size() < LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS)
		{
			if (hasBonus.compareAndSet(true, false))
			{
				for (Player member : members)
				{
					serviceBuff = new ServiceBuff(1);
					serviceBuff.endEffect(member, 1);
				}
			}
		}
		else if (members.size() < LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS)
		{
			if (hasBonus.compareAndSet(true, false))
			{
				for (Player member : members)
				{
					serviceBuff = new ServiceBuff(5);
					serviceBuff.endEffect(member, 5);
					serviceBuff.applyEffect(member, 1);
					PacketSendUtility.sendPacket(member, new SM_ICON_INFO(1, false));
				}
			}
		}
	}
	
	// Legion buff remove message
	/**
	 * Removes the bonus message from online legion members.<br>
	 * This method checks if the number of online members is below {@code LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS}.<br>
	 * If the condition is met, it sends a notification to all online members regarding the lack of bonuses.
	 */
	public void removeBonusMassage()
	{
		final ArrayList<Player> members = getOnlineLegionMembers();
		if (members.size() < LegionConfig.LEGION_BUFF_REQUIRED_MEMBERS)
		{
			for (Player member : members)
			{
				PacketSendUtility.sendMessage(member, "Legion : Bonus + 0 % XP , Craft , Gather");
			}
		}
		
	}
	
	/**
	 * Checks if the legion currently has an active bonus.<br>
	 * This method retrieves the current state of the {@code hasBonus} flag.
	 * @return {@code true} if a bonus is active, {@code false} otherwise.
	 */
	public boolean hasBonus()
	{
		return hasBonus.get();
	}
	
	/**
	 * Compares this {@link Legion} object with another object for equality.<br>
	 * It checks if both objects have the same {@code legionId}.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}
		
		final Legion legion = (Legion) o;
		return legionId == legion.legionId;
	}
	
	/**
	 * Returns a hash code value for this {@link Legion} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code legionId} field.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return legionId;
	}
	
	/**
	 * Retrieves the text description of the {@code Legion}.<br>
	 * This string provides details about the legion's purpose or rules.
	 * @return The {@code String} containing the legion description.
	 */
	public String getLegionDiscription()
	{
		return description;
	}
	
	/**
	 * Retrieves the current join type of the {@link Legion}.<br>
	 * This value determines how players can join this specific group.
	 * @return The integer value representing the join type.
	 */
	public int getLegionJoinType()
	{
		return joinType;
	}
	
	/**
	 * Retrieves the minimum level required to join this legion.<br>
	 * This value is stored in the {@code minJoinLevel} field.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minJoinLevel;
	}
	
	/**
	 * Updates the description of the {@link Support} object.<br>
	 * This method sets the internal {@code description} field to a new value.
	 * @param description The new text to set as the description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Sets the minimum level required for a player to join this legion.<br>
	 * This value is used to filter eligible members during the joining process.
	 * @param minJoinLevel The minimum character level required to join.
	 */
	public void setMinJoinLevel(int minJoinLevel)
	{
		this.minJoinLevel = minJoinLevel;
	}
	
	/**
	 * Sets the type of join for the {@code Legion}.<br>
	 * This value determines how members can join this specific legion.
	 * @param joinType The integer value representing the join type.
	 */
	public void setJoinType(int joinType)
	{
		this.joinType = joinType;
	}
	
	/**
	 * Retrieves the map of pending join requests.<br>
	 * The keys are player IDs and the values are {@link LegionJoinRequest} objects.
	 * @return a {@code Map} containing all current join requests.
	 */
	public Map<Integer, LegionJoinRequest> getJoinRequestMap()
	{
		return joinRequestMap;
	}
	
	/**
	 * Retrieves a {@link LegionJoinRequest} for a specific player.<br>
	 * This method looks up the request using the provided {@code playerId}.<br>
	 * It returns {@code null} if no request exists.
	 * @param playerId The unique ID of the player to look up.
	 * @return The {@code LegionJoinRequest} object or {@code null}.
	 */
	public LegionJoinRequest getJoinRequestByPlayerId(int playerId)
	{
		return joinRequestMap.get(playerId);
	}
	
	/**
	 * Removes a pending join request from the internal map.<br>
	 * This method uses the {@code playerId} to identify which request to delete.
	 * @param playerId The unique ID of the player whose request is being removed.
	 */
	public void deleteJoinRequest(int playerId)
	{
		joinRequestMap.remove(playerId);
	}
	
	/**
	 * Adds a new join request to the legion's internal map.<br>
	 * This method ensures that the request is only added if it does not already exist for the given player ID.
	 * @param joinRequest The {@code LegionJoinRequest} object containing the player's join details.
	 */
	public void addJoinRequest(LegionJoinRequest joinRequest)
	{
		if (!joinRequestMap.containsKey(joinRequest.getPlayerId()))
		{
			joinRequestMap.put(joinRequest.getPlayerId(), joinRequest);
		}
	}
	
	/**
	 * Resets the current territory of the {@code Legion}.<br>
	 * This method initializes a new {@link com.aionemu.gameserver.model.team.legion.LegionTerritory} with an ID of {@code 0}.
	 */
	public void clearTerritory()
	{
		setTerritory(new LegionTerritory(0));
	}
	
	/**
	 * Checks if the legion currently owns a territory.<br>
	 * It returns {@code true} if the territory ID is greater than {@code 0}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if a territory is owned, {@code false} otherwise.
	 */
	public boolean ownsTerretory()
	{
		return getTerritory().getId() > 0;
	}
	
	/**
	 * Retrieves the current territory of the {@code Legion}.
	 * @return the {@link LegionTerritory} object associated with this legion.
	 */
	public LegionTerritory getTerritory()
	{
		return territory;
	}
	
	/**
	 * Sets the current territory for this {@link Legion}.<br>
	 * This updates the internal territory field with the provided value.
	 * @param territory The {@code LegionTerritory} object to assign.
	 */
	public void setTerritory(LegionTerritory territory)
	{
		this.territory = territory;
	}
}
