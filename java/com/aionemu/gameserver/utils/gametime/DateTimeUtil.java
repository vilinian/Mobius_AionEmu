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
package com.aionemu.gameserver.utils.gametime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GSConfig;

/**
 * Provides utility methods for handling and converting date and time objects.<br>
 * This class simplifies operations involving {@code GregorianCalendar} and {@code ZonedDateTime}.
 * @author Mobius
 */
public final class DateTimeUtil
{
	static Logger log = LoggerFactory.getLogger(DateTimeUtil.class);
	private static boolean canApplyZoneChange = false;
	
	/**
	 * Initializes the time zone settings.<br>
	 * This method validates the {@code TIME_ZONE_ID} from {@link GSConfig}.<br>
	 * If valid, it sets {@code canApplyZoneChange} to {@code true}.
	 */
	public static void init()
	{
		try
		{
			if (!GSConfig.TIME_ZONE_ID.isEmpty())
			{
				// Just check validity on start (throws if the configured zone id is invalid).
				ZoneId.of(GSConfig.TIME_ZONE_ID);
				canApplyZoneChange = true;
			}
		}
		catch (Throwable e)
		{
			log.error("Invalid or not supported timezone specified!!!\nAdd a valid value for GSConfig.TIME_ZONE_ID");
		}
	}
	
	/**
	 * Converts a {@link GregorianCalendar} object into a {@link ZonedDateTime} object.<br>
	 * This method handles optional time zone adjustments based on the server configuration.
	 * @param calendar The {@code GregorianCalendar} instance to convert.
	 * @return The resulting {@code ZonedDateTime} object.
	 */
	public static ZonedDateTime getDateTime(GregorianCalendar calendar)
	{
		final ZonedDateTime dt = calendar.toZonedDateTime();
		if (canApplyZoneChange)
		{
			return dt.withZoneSameLocal(ZoneId.of(GSConfig.TIME_ZONE_ID));
		}
		
		return dt;
	}
}
