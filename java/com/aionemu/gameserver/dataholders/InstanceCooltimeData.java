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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.model.gameobjects.player.Player;
// import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownItem; TODO?
import com.aionemu.gameserver.model.templates.InstanceCooltime;

/**
 * This class manages the cooldown data for various game instances.<br>
 * It stores and provides access to {@link InstanceCooltime} information used by the server.
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "instance_cooltimes")
public class InstanceCooltimeData
{
	@XmlElement(name = "instance_cooltime", required = true)
	protected List<InstanceCooltime> instanceCooltime;
	private final Map<Integer, InstanceCooltime> instanceCooltimes = new HashMap<>();
	private final HashMap<Integer, Integer> syncIdToMapId = new HashMap<>();
	private final HashMap<Integer, Integer> syncId = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the internal maps using the list of {@link InstanceCooltime} objects.<br>
	 * The {@code instanceCooltime} list is cleared after being processed.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (InstanceCooltime tmp : instanceCooltime)
		{
			instanceCooltimes.put(tmp.getWorldId(), tmp);
			syncIdToMapId.put(tmp.getId(), tmp.getWorldId());
			syncId.put(tmp.getSyncId(), tmp.getWorldId());
		}
		
		instanceCooltime.clear();
	}
	
	/**
	 * Retrieves all instance cooltime data.<br>
	 * This method returns a map of all loaded instances.
	 * @return A {@code FastMap} containing the mapping of IDs to {@link InstanceCooltime} objects.
	 */
	public Map<Integer, InstanceCooltime> getAllInstances()
	{
		return instanceCooltimes;
	}
	
	/**
	 * Retrieves the {@link InstanceCooltime} data for a specific world.<br>
	 * It uses the provided {@code worldId} to look up the value in the internal map.
	 * @param worldId The unique identifier of the world.
	 * @return The {@code InstanceCooltime} object associated with the ID, or {@code null} if not found.
	 */
	public InstanceCooltime getInstanceCooltimeByWorldId(int worldId)
	{
		return instanceCooltimes.get(worldId);
	}
	
	/**
	 * Retrieves the world ID associated with a specific synchronization ID.<br>
	 * This method looks up the value in the internal mapping.<br>
	 * It returns 0 if the provided {@code syncId} is not found.
	 * @param syncId The unique identifier for the synchronization point.
	 * @return The corresponding world ID, or 0 if no mapping exists.
	 */
	public int getWorldId(int syncId)
	{
		if (!syncIdToMapId.containsKey(syncId))
		{
			return 0;
		}
		
		return syncIdToMapId.get(syncId);
	}
	
	/**
	 * Retrieves the {@code syncId} associated with a specific ID.<br>
	 * This method checks if the provided {@code id} exists in the internal map.<br>
	 * If the ID is not found, it returns {@code 0}.
	 * @param id The unique identifier to look up.
	 * @return The corresponding {@code syncId}, or {@code 0} if no match is found.
	 */
	public int getSyncId(int id)
	{
		if (!syncId.containsKey(id))
		{
			return 0;
		}
		
		return syncId.get(id);
	}
	
	/**
	 * Gets the cooldown time for a player entering an instance.<br>
	 * This method uses the {@code syncId} to find the correct internal ID.<br>
	 * If the {@code syncId} is not found, it returns 0.
	 * @param player The {@link Player} object who is trying to enter the instance.
	 * @param syncId The unique identifier for the instance synchronization.
	 * @return The cooldown time in milliseconds as a {@code long}.
	 */
	public long getInstanceEntranceCooltimeById(Player player, int syncId)
	{
		if (!syncIdToMapId.containsKey(syncId))
		{
			return 0;
		}
		
		return getInstanceEntranceCooltime(player, syncIdToMapId.get(syncId));
	}
	
	/**
	 * Retrieves the maximum number of allowed entries for a specific world.<br>
	 * This method looks up the {@code InstanceCooltime} data using the provided {@code worldId}.<br>
	 * If no data is found, it returns {@code 0}.
	 * @param worldId The unique identifier for the world.
	 * @return The maximum entrance count as an {@code int}.
	 */
	public int getInstanceEntranceCountByWorldId(int worldId)
	{
		final InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		if (clt != null)
		{
			return clt.getMaxEntriesCount();
		}
		
		return 0;
	}
	
	/**
	 * Calculates the remaining time until a player can enter an instance.<br>
	 * This method checks the cooldown type for the specified {@code worldId}.<br>
	 * It handles daily, weekly, and relative cooldown logic.
	 * @param player The {@link Player} object requesting access.
	 * @param worldId The unique identifier of the instance world.
	 * @return The remaining cooltime in milliseconds.
	 */
	public long getInstanceEntranceCooltime(Player player, int worldId)
	{
		final InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		long cooltime = 0;
		if (clt != null)
		{
			if (clt.getCoolTimeType().isDaily())
			{
				final ZonedDateTime now = ZonedDateTime.now();
				ZonedDateTime repeatDate = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), clt.getEntCoolTime() / 100, 0, 0, 0, java.time.ZoneId.systemDefault());
				if (now.isAfter(repeatDate))
				{
					repeatDate = repeatDate.plusHours(24);
				}
				
				cooltime = repeatDate.toInstant().toEpochMilli();
			}
			else if (clt.getCoolTimeType().isWeekly())
			{
				final String[] days = clt.getTypeValue().split(",");
				cooltime = getUpdateHours(days, clt.getEntCoolTime() / 100);
			}
			else if (clt.getCoolTimeType().isRelative())
			{
				switch (worldId)
				{
					case 300020000:
					case 300290000:
					case 300480000:
					case 300560000:
					case 301160000:
					case 301200000:
					case 301320000:
					case 301330000:
					case 301340000:
					case 301400000:
					case 301520000:
					case 301570000:
					case 301580000:
					case 301690000:
					case 310010000:
					case 310020000:
					case 310030000:
					case 310040000:
					case 310060000:
					case 310070000:
					case 310080000:
					case 310120000:
					case 320010000:
					case 320020000:
					case 320030000:
					case 320040000:
					case 320050000:
					case 320060000:
					case 320070000:
					case 320090000:
					case 320120000:
					case 320140000:
					case 720010000:
					case 730010000:
					case 900120000:
					case 900130000:
					case 900140000:
					case 900150000:
					case 900190000:
					case 900200000:
					case 900210000:
					case 900230000:
						final ZonedDateTime now = ZonedDateTime.now();
						ZonedDateTime repeatDate = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 9, 0, 0, 0, java.time.ZoneId.systemDefault()); // 9 AM
						if (now.isAfter(repeatDate))
						{
							repeatDate = repeatDate.plusHours(24);
						}
						
						cooltime = repeatDate.toInstant().toEpochMilli();
						break;
					default:
						cooltime = System.currentTimeMillis() + (clt.getEntCoolTime() * 60 * 1000);
				}
			}
		}
		
		return cooltime;
	}
	
	/**
	 * Calculates the timestamp for the next update based on specific days and an hour.<br>
	 * It checks the current day against the provided {@code days} array.<br>
	 * If today is not a valid day, it finds the next occurrence in the future.
	 * @param days An array of strings representing the days of the week.
	 * @param hour The specific hour of the day for the update.
	 * @return The timestamp in milliseconds for the next scheduled update.
	 */
	private long getUpdateHours(String[] days, int hour)
	{
		final ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime repeatDate = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), hour, 0, 0, 0, java.time.ZoneId.systemDefault());
		final int curentDay = now.getDayOfWeek().getValue();
		for (String name : days)
		{
			final int day = getDay(name);
			if (day < curentDay)
			{
				continue;
			}
			
			if (day == curentDay)
			{
				if (now.isBefore(repeatDate))
				{
					return repeatDate.toInstant().toEpochMilli();
				}
			}
			else
			{
				repeatDate = repeatDate.plusDays(day - curentDay);
				return repeatDate.toInstant().toEpochMilli();
			}
		}
		
		return repeatDate.plusDays((7 - curentDay) + getDay(days[0])).toInstant().toEpochMilli();
	}
	
	/**
	 * Converts a day string into its corresponding integer value.<br>
	 * The values range from {@code 1} for Monday to {@code 7} for Sunday.
	 * @param day The name of the day as a {@code String}.
	 * @return The integer representation of the day.
	 */
	private int getDay(String day)
	{
		if (day.equals("Mon"))
		{
			return 1;
		}
		else if (day.equals("Tue"))
		{
			return 2;
		}
		else if (day.equals("Wed"))
		{
			return 3;
		}
		else if (day.equals("Thu"))
		{
			return 4;
		}
		else if (day.equals("Fri"))
		{
			return 5;
		}
		else if (day.equals("Sat"))
		{
			return 6;
		}
		else if (day.equals("Sun"))
		{
			return 7;
		}
		
		throw new IllegalArgumentException("Invalid Day: " + day);
	}
	
	/**
	 * Returns the total number of cooltime entries.<br>
	 * This method calls {@code size()} on the internal {@code instanceCooltimes} map.
	 * @return The count of items in the collection as an {@code Integer}.
	 */
	public Integer size()
	{
		return instanceCooltimes.size();
	}
}
