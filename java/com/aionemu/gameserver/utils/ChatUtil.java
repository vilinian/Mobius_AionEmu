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
package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Provides utility methods for handling in-game chat messages.<br>
 * This class simplifies formatting and broadcasting text to players.<br>
 * It interacts with {@link AdminConfig} and other configuration files to manage permissions.
 * @author antness
 */
public class ChatUtil
{
	/**
	 * Converts a {@link WorldPosition} object into a formatted string.<br>
	 * This method uses the provided {@code label} to prefix the coordinates.<br>
	 * It is a convenient way to display locations in chat messages.
	 * @param label The text to display before the coordinates.
	 * @param pos The {@code WorldPosition} containing the map ID and coordinates.
	 * @return A formatted string representing the position.
	 */
	public static String position(String label, WorldPosition pos)
	{
		return position(label, pos.getMapId(), pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Formats a coordinate string for display.<br>
	 * This method combines a label with specific world coordinates.
	 * @param label The text prefix to show before the position.
	 * @param worldId The unique identifier of the world.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @return A formatted string containing the label and coordinates.
	 */
	public static String position(String label, long worldId, float x, float y, float z)
	{
		// TODO: need rework for abyss map
		return String.format("[pos:%s;%d %f %f %f -1]", label, worldId, x, y, z);
	}
	
	/**
	 * This method creates a formatted string for an item.<br>
	 * It takes an {@code itemId} and wraps it in brackets.<br>
	 * The result is used to display item information in chat.
	 * @param itemId The unique identifier of the item.
	 * @return A formatted string containing the item ID.
	 */
	public static String item(long itemId)
	{
		return String.format("[item: %d]", itemId);
	}
	
	/**
	 * This method formats a recipe identifier into a readable string.<br>
	 * It is used to display the {@code recipeId} in a specific format.
	 * @param recipeId The unique ID of the recipe to format.
	 * @return A formatted string containing the recipe ID.
	 */
	public static String recipe(long recipeId)
	{
		return String.format("[recipe: %d]", recipeId);
	}
	
	/**
	 * This method formats a quest identifier into a readable string.<br>
	 * It is used to display quest information in the chat.
	 * @param questId The unique ID of the quest.
	 * @return A formatted string containing the {@code questId}.
	 */
	public static String quest(int questId)
	{
		return String.format("[quest: %d]", questId);
	}
	
	/**
	 * This method removes a specific part of a {@code PlayerName}.<br>
	 * It looks for a {@code Pattern} containing the {@code %s} placeholder.<br>
	 * The text before and after that placeholder is removed from the result.
	 * @param PlayerName The original name of the player.
	 * @param Pattern The pattern used to identify which part of the name to remove.
	 * @return The modified string with the pattern parts removed.
	 */
	public static String removePattern(String PlayerName, String Pattern)
	{
		final int index = Pattern.indexOf("%s");
		if (index == -1)
		{
			return PlayerName;
		}
		
		String RealName = "";
		RealName = PlayerName.replace(Pattern.substring(0, index), "");
		RealName = RealName.replace(Pattern.substring(index + 2), "");
		
		return RealName;
	}
	
	/**
	 * This method retrieves the clean name of an administrator.<br>
	 * It removes all decorative tags from the provided {@code PlayerName}.<br>
	 * It uses {@code String)} to strip various prefixes.
	 * @param PlayerName The original name of the player including any tags.
	 * @return The cleaned name without any administrative or membership tags.
	 */
	public static String getRealAdminName(String PlayerName)
	{
		String RealAdminName = "";
		RealAdminName = removePattern(PlayerName, MembershipConfig.TAG_VIP);
		RealAdminName = removePattern(RealAdminName, MembershipConfig.TAG_PREMIUM);
		RealAdminName = removePattern(RealAdminName, WeddingsConfig.TAG_WEDDING);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS1);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS2);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS3);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS4);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS5);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS6);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS7);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS8);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS9);
		RealAdminName = removePattern(RealAdminName, AdminConfig.CUSTOMTAG_ACCESS10);
		return RealAdminName;
	}
}
