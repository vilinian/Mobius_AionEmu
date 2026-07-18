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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class represents an extended version of a {@link LegionMember}.<br>
 * It provides additional data and functionality for members within a legion.<br>
 * Use this class when the standard {@code LegionMember} properties are insufficient.
 * @author Simple
 */
public class LegionMemberEx extends LegionMember
{
	private static Logger log = LoggerFactory.getLogger(LegionMemberEx.class);
	private String name;
	private PlayerClass playerClass;
	private int level;
	private Timestamp lastOnline;
	private int worldId;
	private boolean online = false;
	
	/**
	 * Creates a new {@link LegionMemberEx} instance from an existing {@link Player}.<br>
	 * This constructor copies data from the provided {@link LegionMember} and {@code player}.<br>
	 * It sets the initial online status based on the {@code online} boolean.
	 * @param player The {@link Player} object providing character details.
	 * @param legionMember The base {@link LegionMember} data to copy from.
	 * @param online A boolean indicating if the member is currently online.
	 */
	public LegionMemberEx(Player player, LegionMember legionMember, boolean online)
	{
		super(player.getObjectId(), legionMember.getLegion(), legionMember.getRank());
		nickname = legionMember.getNickname();
		selfIntro = legionMember.getSelfIntro();
		name = player.getName();
		playerClass = player.getPlayerClass();
		level = player.getLevel();
		lastOnline = player.getCommonData().getLastOnline();
		worldId = player.getPosition().getMapId();
		this.online = online;
	}
	
	/**
	 * Creates a new {@link LegionMemberEx} instance.<br>
	 * This constructor initializes the member using a specific player ID.<br>
	 * It calls the parent constructor to set up the base data.
	 * @param playerObjId The unique identifier for the player object.
	 */
	public LegionMemberEx(int playerObjId)
	{
		super(playerObjId);
	}
	
	/**
	 * Creates a new {@link LegionMemberEx} instance.<br>
	 * This constructor initializes the member with a specific name.
	 * @param name The name of the legion member.
	 */
	public LegionMemberEx(String name)
	{
		super();
		this.name = name;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name.
	 * @param name The new name to assign.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	/**
	 * Sets the character class for the player.<br>
	 * This updates the {@code playerClass} field in this object.
	 * @param playerClass The new {@link PlayerClass} to assign.
	 */
	public void setPlayerClass(PlayerClass playerClass)
	{
		this.playerClass = playerClass;
	}
	
	/**
	 * Retrieves the last time the player was online.<br>
	 * This method returns a Unix timestamp in seconds.<br>
	 * It returns {@code 0} if the player is currently online or has no recorded login history.
	 * @return The last online time as an {@code int} representing seconds, or {@code 0}.
	 */
	public int getLastOnline()
	{
		if ((lastOnline == null) || isOnline())
		{
			return 0;
		}
		
		return (int) (lastOnline.getTime() / 1000);
	}
	
	/**
	 * Updates the last online time for the player.<br>
	 * This method sets the {@code lastOnline} field to the provided value.
	 * @param timestamp The {@code Timestamp} representing when the player was last online.
	 */
	public void setLastOnline(Timestamp timestamp)
	{
		lastOnline = timestamp;
	}
	
	/**
	 * Retrieves the current level of the {@code LegionMemberEx}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Sets the experience points for the player.<br>
	 * This method updates the {@code level} based on the new value.<br>
	 * It also caps the experience at the maximum allowed amount.<br>
	 * If the player is online, it sends a status update packet.
	 * @param exp The new experience value to set.
	 */
	public void setExp(long exp)
	{
		// maxLevel is 51 but in game 50 should be shown with full XP bar
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();
		
		if ((getPlayerClass() != null) && getPlayerClass().isStartingClass())
		{
			maxLevel = 10;
		}
		
		final long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);
		int level = 1;
		
		if (exp > maxExp)
		{
			exp = maxExp;
		}
		
		// make sure level is never larger than maxLevel-1
		while (((level + 1) != maxLevel) && (exp >= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level + 1)))
		{
			level++;
		}
		
		this.level = level;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
	
	/**
	 * Sets the unique identifier for the world.<br>
	 * This updates the {@code worldId} field of the current object.
	 * @param worldId The new ID to assign to the world.
	 */
	public void setWorldId(int worldId)
	{
		this.worldId = worldId;
	}
	
	/**
	 * Updates the online status of the player.<br>
	 * Sets the {@code online} field to the provided value.
	 * @param online The new online status to set.
	 */
	public void setOnline(boolean online)
	{
		this.online = online;
	}
	
	/**
	 * Checks if the player is currently online.<br>
	 * This method returns the current status of the {@code isOnline} flag.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return online;
	}
	
	/**
	 * Checks if the current object's ID matches a given value.<br>
	 * This method compares the internal {@code objectId} with the provided parameter.
	 * @param objectId The unique identifier to compare against.
	 * @return {@code true} if the IDs match, otherwise {@code false}.
	 */
	public boolean sameObjectId(int objectId)
	{
		return getObjectId() == objectId;
	}
	
	/**
	 * Checks if the {@code LegionMemberEx} object contains all required data.<br>
	 * It validates fields like name, level, and world ID.<br>
	 * Returns {@code true} if all fields are valid.<br>
	 * Returns {@code false} if any mandatory field is missing or invalid.
	 * @return {@code true} if the member data is complete; {@code false} otherwise.
	 */
	public boolean isValidLegionMemberEx()
	{
		if (getObjectId() < 1)
		{
			log.error("[LegionMemberEx] Player Object ID is empty.");
		}
		else if (getName() == null)
		{
			log.error("[LegionMemberEx] Player Name is empty." + getObjectId());
		}
		else if (getPlayerClass() == null)
		{
			log.error("[LegionMemberEx] Player Class is empty." + getObjectId());
		}
		else if (getLevel() < 1)
		{
			log.error("[LegionMemberEx] Player Level is empty." + getObjectId());
		}
		else if (getLastOnline() == 0)
		{
			log.error("[LegionMemberEx] Last Online is empty." + getObjectId());
		}
		else if (getWorldId() < 1)
		{
			log.error("[LegionMemberEx] World Id is empty." + getObjectId());
		}
		else if (getLegion() == null)
		{
			log.error("[LegionMemberEx] Legion is empty." + getObjectId());
		}
		else if (getRank() == null)
		{
			log.error("[LegionMemberEx] Rank is empty." + getObjectId());
		}
		else if (getNickname() == null)
		{
			log.error("[LegionMemberEx] Nickname is empty." + getObjectId());
		}
		else if (getSelfIntro() == null)
		{
			log.error("[LegionMemberEx] Self Intro is empty." + getObjectId());
		}
		else
		{
			return true;
		}
		
		return false;
	}
}
