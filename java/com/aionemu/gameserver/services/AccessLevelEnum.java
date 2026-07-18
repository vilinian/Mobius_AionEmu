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

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Defines the different permission levels available for users within the game server.<br>
 * This enumeration is used to control access to specific administrative features and commands.
 */
public enum AccessLevelEnum
{
	AccessLevel1(1, AdminConfig.CUSTOMTAG_ACCESS1, "\ue042Supporter\ue043", new int[]
	{
		240,
		241,
		277
	}),
	AccessLevel2(2, AdminConfig.CUSTOMTAG_ACCESS2, "\ue042Junior-GM\ue043", new int[]
	{
		240,
		241,
		277
	}),
	AccessLevel3(3, AdminConfig.CUSTOMTAG_ACCESS3, "\ue042Senior-GM\ue043", new int[]
	{
		240,
		241,
		277
	}),
	AccessLevel4(4, AdminConfig.CUSTOMTAG_ACCESS4, "\ue042Head-GM\ue043", new int[]
	{
		240,
		241,
		277
	}),
	AccessLevel5(5, AdminConfig.CUSTOMTAG_ACCESS5, "\ue042Admin\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	}),
	AccessLevel6(6, AdminConfig.CUSTOMTAG_ACCESS6, "\ue042Developer\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	}),
	AccessLevel7(7, AdminConfig.CUSTOMTAG_ACCESS7, "\ue042S-Admin L1\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	}),
	AccessLevel8(8, AdminConfig.CUSTOMTAG_ACCESS8, "\ue042S-Admin L2\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	}),
	AccessLevel9(9, AdminConfig.CUSTOMTAG_ACCESS9, "\ue042Co-Owner\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	}),
	AccessLevel10(10, AdminConfig.CUSTOMTAG_ACCESS10, "\ue042S-Owner\ue043", new int[]
	{
		240,
		241,
		277,
		282,
		376,
		377,
		378,
		379,
		380,
		381,
		382,
		383,
		384,
		385,
		386,
		387,
		388,
		389,
		390,
		391,
		392,
		393,
		394,
		395,
		395,
		396
	});
	
	private final int level;
	private final String nameLevel;
	private final String status;
	private final int[] skills;
	
	/**
	 * Creates a new instance of {@link AccessLevelEnum}.<br>
	 * This constructor initializes the access level properties.
	 * @param id The unique identifier for the access level.
	 * @param name The display name of the access level.
	 * @param status The status string associated with this level.
	 * @param skills An array of skill IDs granted to this level.
	 */
	AccessLevelEnum(int id, String name, String status, int[] skills)
	{
		level = id;
		nameLevel = name;
		this.status = status;
		this.skills = skills;
	}
	
	/**
	 * Retrieves the display name of the access level.<br>
	 * This method returns the {@code String`nameLevel`} associated with this enum constant.
	 * @return The name of the access level as a {@code String}.
	 */
	public String getName()
	{
		return nameLevel;
	}
	
	/**
	 * Retrieves the current level of the {@code MCEntry}.<br>
	 * This value represents the progression stage.
	 * @return The integer value of the level.
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the display name of the access level.<br>
	 * This value is used to identify the rank in the game.
	 * @return The {@code String} representing the status name.
	 */
	public String getStatusName()
	{
		return status;
	}
	
	/**
	 * Retrieves the list of skill IDs associated with this access level.<br>
	 * These values are used to determine specific permissions.
	 * @return an {@code int[]} array containing the skill identifiers.
	 */
	public int[] getSkills()
	{
		return skills;
	}
	
	/**
	 * Retrieves an {@link AccessLevelEnum} based on a numeric level.<br>
	 * This method searches through all available levels to find a match.<br>
	 * It returns {@code null} if the provided level does not exist.
	 * @param level The integer value representing the access level to find.
	 * @return The matching {@link AccessLevelEnum} object or {@code null}.
	 */
	public static AccessLevelEnum getAlType(int level)
	{
		for (AccessLevelEnum al : AccessLevelEnum.values())
		{
			if (level == al.getLevel())
			{
				return al;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the name associated with a specific access level.<br>
	 * This method searches through all {@link AccessLevelEnum} values.<br>
	 * It returns the name if the {@code level} matches an existing entry.
	 * @param level The integer ID of the access level to look up.
	 * @return The name of the access level or a placeholder string if not found.
	 */
	public static String getAlName(int level)
	{
		for (AccessLevelEnum al : AccessLevelEnum.values())
		{
			if (level == al.getLevel())
			{
				return al.getName();
			}
		}
		
		return "%s";
	}
	
	/**
	 * Retrieves the display name for a {@link Player}.<br>
	 * It returns the status name if the player has an access level greater than {@code 0}.<br>
	 * Otherwise, it returns the name of the player's legion.
	 * @param player The {@code Player} object to check.
	 * @return A {@code String} representing the player's status or legion name.
	 */
	public static String getStatusName(Player player)
	{
		return player.getAccessLevel() > 0 ? AccessLevelEnum.getAlType(player.getAccessLevel()).getStatusName() : player.getLegion().getLegionName();
	}
}
