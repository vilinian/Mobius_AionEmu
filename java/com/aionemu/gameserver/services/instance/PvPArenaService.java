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
package com.aionemu.gameserver.services.instance;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Manages the logic and lifecycle of the PvP Arena instances.<br>
 * It handles player entry, arena rules, and system messages for participants.
 * @author xTz
 */
public class PvPArenaService
{
	/**
	 * Checks if a player can enter the PvP Arena for a specific group type.<br>
	 * It validates time restrictions and required items.
	 * @param player The {@link Player} object to check.
	 * @param agt The {@link AutoGroupType} of the arena.
	 * @return {@code true} if the player can enter, otherwise {@code false}.
	 */
	public static boolean isPvPArenaAvailable(Player player, AutoGroupType agt)
	{
		if (AutoGroupConfig.START_TIME_ENABLE && !checkTime(agt) && (player.getAccessLevel() >= 1))
		{
			return true;
		}
		
		if (AutoGroupConfig.START_TIME_ENABLE && !checkTime(agt))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401306, agt.getInstanceMapId()));
			return false;
		}
		
		if (!checkItem(player, agt))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
			return false;
		}
		
		// todo check cool down
		return true;
	}
	
	/**
	 * Checks if a {@link Player} has the required items for an {@link AutoGroupType}.<br>
	 * This method verifies inventory counts based on the specific arena type.
	 * @param player The {@code Player} object to check.
	 * @param agt The {@code AutoGroupType} defining the requirements.
	 * @return {@code true} if the player meets the requirements, otherwise {@code false}.
	 */
	public static boolean checkItem(Player player, AutoGroupType agt)
	{
		final Storage inventory = player.getInventory();
		if (agt.isPvPFFAArena() || agt.isPvPSoloArena())
		{
			return inventory.getItemCountByItemId(186000135) > 0;
		}
		else if (agt.isHarmonyArena())
		{
			return inventory.getItemCountByItemId(186000184) > 0;
		}
		else if (agt.isGloryArena())
		{
			return inventory.getItemCountByItemId(186000185) >= 3;
		}
		
		return true;
	}
	
	/**
	 * Checks if the current time allows for specific arena activities.<br>
	 * This method determines availability based on the {@code AutoGroupType}.<br>
	 * It calls internal checks like {@code isPvPArenaAvailable} or {@code isHarmonyArenaAvailable}.
	 * @param agt The type of auto group to check.
	 * @return {@code true} if the activity is allowed, otherwise {@code false}.
	 */
	private static boolean checkTime(AutoGroupType agt)
	{
		if (agt.isPvPFFAArena() || agt.isPvPSoloArena())
		{
			return isPvPArenaAvailable();
		}
		else if (agt.isHarmonyArena())
		{
			return isHarmonyArenaAvailable();
		}
		else if (agt.isGloryArena())
		{
			return isGloryArenaAvailable();
		}
		
		return true;
	}
	
	/**
	 * Checks if the {@code PvPArena} is currently open based on the system time.<br>
	 * This method validates specific hours for both weekdays and weekends.
	 * @return {@code true} if the arena is available, {@code false} otherwise.
	 */
	private static boolean isPvPArenaAvailable()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final int hour = now.getHour();
		final int day = now.getDayOfWeek().getValue();
		if ((day == 6) || (day == 7))
		{
			return (hour == 0) || (hour == 1) || ((hour >= 10) && (hour <= 23));
		}
		
		return (hour == 0) || (hour == 1) || (hour == 12) || (hour == 13) || ((hour >= 18) && (hour <= 23));
	}
	
	/**
	 * Checks if the Harmony Arena is currently open.<br>
	 * This method validates the current time and day of the week.<br>
	 * It returns {@code true} if the arena is active based on the schedule.
	 * @return {@code true} if available, {@code false} otherwise.
	 */
	private static boolean isHarmonyArenaAvailable()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final int hour = now.getHour();
		final int day = now.getDayOfWeek().getValue();
		if (day == 6)
		{
			return (hour >= 10) || (hour == 1) || (hour == 2);
		}
		else if (day == 7)
		{
			return (hour == 0) || (hour == 1) || (hour >= 10);
		}
		else
		{
			return ((hour >= 10) && (hour < 14)) || ((hour >= 18) && (hour <= 23));
		}
	}
	
	/**
	 * Checks if the Glory Arena is currently open.<br>
	 * This method verifies the current time and day of the week.<br>
	 * It returns {@code true} only during specific weekend hours.
	 * @return {@code true} if the arena is available, {@code false} otherwise.
	 */
	private static boolean isGloryArenaAvailable()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final int hour = now.getHour();
		final int day = now.getDayOfWeek().getValue();
		return ((day == 6) || (day == 7)) && (hour >= 20) && (hour < 22);
	}
}
