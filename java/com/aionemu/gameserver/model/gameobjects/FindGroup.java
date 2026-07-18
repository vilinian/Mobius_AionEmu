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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;

/**
 * Represents a request to locate or join a group within the game world.<br>
 * This class handles the logic for searching for {@link PlayerGroup} instances.
 * @author MrPoke
 * @modified teenwolf
 */
public class FindGroup
{
	private final AionObject object;
	private String message;
	private int groupType, minMembers, instanceId;
	private int lastUpdate = (int) (System.currentTimeMillis() / 1000);
	
	/**
	 * Creates a new {@code FindGroup} instance.<br>
	 * This constructor initializes the required group search parameters.
	 * @param object The {@link AionObject} associated with this request.
	 * @param message The text description for the group.
	 * @param groupType The specific category of the group to find.
	 */
	public FindGroup(AionObject object, String message, int groupType)
	{
		this.object = object;
		this.message = message;
		this.groupType = groupType;
	}
	
	/**
	 * Retrieves the current message associated with this {@code FindGroup} object.<br>
	 * This method returns the string value stored in the {@code message} field.
	 * @return The message as a {@code String}.
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Retrieves the type of the group.<br>
	 * This value is used to identify different group categories.
	 * @return The {@code int} value representing the group type.
	 */
	public int getGroupType()
	{
		return groupType;
	}
	
	/**
	 * Retrieves the unique identifier of the {@link AionObject}.<br>
	 * This method calls {@code getObjectId()} on the internal {@code object} field.
	 * @return The unique ID of the object.
	 */
	public int getObjectId()
	{
		return object.getObjectId();
	}
	
	/**
	 * Retrieves the unique identifier for this instance.<br>
	 * This ID was provided during the construction of {@link CollisionResults}.
	 * @return The {@code int} value representing the instance ID.
	 */
	public int getInstanceId()
	{
		return instanceId;
	}
	
	/**
	 * Retrieves the minimum number of members required for this group.<br>
	 * This value is used to filter search results.
	 * @return The {@code int} value representing the minimum member count.
	 */
	public int getMinMembers()
	{
		return minMembers;
	}
	
	/**
	 * Retrieves the unique identifier for the class of the associated object.<br>
	 * This method checks if the {@code object} is a {@link Player}, {@link PlayerAlliance}, or {@link PlayerGroup}.<br>
	 * It returns the ID from the leader's player class if applicable.
	 * @return The integer ID of the class, or {@code 0} if no valid class is found.
	 */
	public int getClassId()
	{
		if (object instanceof Player)
		{
			return ((Player) (object)).getPlayerClass().getClassId();
		}
		else if (object instanceof PlayerAlliance)
		{
			((PlayerAlliance) (object)).getLeaderObject().getCommonData().getPlayerClass();
		}
		else if (object instanceof PlayerGroup)
		{
			((PlayerGroup) object).getLeaderObject().getPlayerClass();
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		if (object instanceof Player)
		{
			return ((Player) (object)).getLevel();
		}
		else if (object instanceof PlayerAlliance)
		{
			int minLvl = 99;
			for (Player member : ((PlayerAlliance) (object)).getMembers())
			{
				final int memberLvl = member.getCommonData().getLevel();
				if (memberLvl < minLvl)
				{
					minLvl = memberLvl;
				}
			}
			
			return minLvl;
		}
		else if (object instanceof PlayerGroup)
		{
			return ((PlayerGroup) object).getMinExpPlayerLevel();
		}
		else if (object instanceof TemporaryPlayerTeam)
		{
			return ((TemporaryPlayerTeam<?>) object).getMinExpPlayerLevel();
		}
		
		return 1;
	}
	
	/**
	 * Retrieves the maximum level of a player or group.<br>
	 * It checks the type of the {@code object}.<br>
	 * If it is a {@link Player}, it returns their current level.<br>
	 * For groups or alliances, it finds the highest member level.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		if (object instanceof Player)
		{
			return ((Player) (object)).getLevel();
		}
		else if (object instanceof PlayerAlliance)
		{
			int maxLvl = 1;
			for (Player member : ((PlayerAlliance) (object)).getMembers())
			{
				final int memberLvl = member.getCommonData().getLevel();
				if (memberLvl > maxLvl)
				{
					maxLvl = memberLvl;
				}
			}
			
			return maxLvl;
		}
		else if (object instanceof PlayerGroup)
		{
			return ((PlayerGroup) object).getMaxExpPlayerLevel();
		}
		else if (object instanceof TemporaryPlayerTeam)
		{
			return ((TemporaryPlayerTeam<?>) object).getMaxExpPlayerLevel();
		}
		
		return 1;
	}
	
	/**
	 * Retrieves an unknown identifier for the object.<br>
	 * It returns {@code 65557} if the object is a {@link Player}.<br>
	 * Otherwise, it returns {@code 0}.
	 * @return The unique identifier as an {@code int}.
	 */
	public int getUnk()
	{
		if (object instanceof Player)
		{
			return 65557;
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the timestamp of the most recent update.<br>
	 * This value is stored in seconds since the epoch.
	 * @return The {@code int} value representing the last update time.
	 */
	public int getLastUpdate()
	{
		return lastUpdate;
	}
	
	/**
	 * Retrieves the name of the associated object.<br>
	 * This method returns the {@code String`name`} based on the type of the internal {@code object}.<br>
	 * If the object is not a recognized type, it returns an empty string.
	 * @return The name of the object as a {@code String}.
	 */
	public String getName()
	{
		if (object instanceof Player)
		{
			return ((Player) object).getName();
		}
		else if (object instanceof PlayerAlliance)
		{
			return ((PlayerAlliance) object).getLeaderObject().getCommonData().getName();
		}
		else if (object instanceof PlayerGroup)
		{
			return ((PlayerGroup) object).getLeaderObject().getName();
		}
		
		return "";
	}
	
	/**
	 * Returns the total size of the associated object.<br>
	 * It checks if the {@code object} is a {@link Player}, {@link PlayerAlliance}, or {@link PlayerGroup}.<br>
	 * If it is an alliance or group, it returns their specific size.<br>
	 * Otherwise, it returns {@code 1}.
	 * @return The size of the object as an {@code int}.
	 */
	public int getSize()
	{
		if (object instanceof Player)
		{
			return 1;
		}
		else if (object instanceof PlayerAlliance)
		{
			return ((PlayerAlliance) object).size();
		}
		else if (object instanceof PlayerGroup)
		{
			return ((PlayerGroup) object).size();
		}
		
		return 1;
	}
	
	/**
	 * Updates the display message for this group.<br>
	 * This method also refreshes the {@code lastUpdate} timestamp.
	 * @param message The new text to display.
	 */
	public void setMessage(String message)
	{
		lastUpdate = (int) (System.currentTimeMillis() / 1000);
		this.message = message;
	}
	
	/**
	 * Sets the type of the group.<br>
	 * This updates the {@code groupType} field for this object.
	 * @param groupType The new integer value for the group type.
	 */
	public void setGroupType(int groupType)
	{
		this.groupType = groupType;
	}
}
