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
package com.aionemu.gameserver.model.templates.spawns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;

/**
 * Represents a spawn point that exists only for a limited duration.<br>
 * This class is used to manage temporary entities in the game world.<br>
 * It provides data for {@link com.aionemu.gameserver.model.templates.spawns.Spawn} objects with expiration logic.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "TemporarySpawn")
public class TemporarySpawn
{
	@XmlAttribute(name = "spawn_time") // *.*.* hour.day.month (* == all)
	private String spawnTime;
	@XmlAttribute(name = "despawn_time") // *.*.* hour.day.month (* == all)
	private String despawnTime;
	
	/**
	 * Retrieves the scheduled time for this spawn.<br>
	 * The format follows the pattern {@code hour.day.month}.<br>
	 * Use {@code getSpawnHour} to get specific parts of this value.
	 * @return The {@code String} representing the spawn time.
	 */
	public String getSpawnTime()
	{
		return spawnTime;
	}
	
	/**
	 * Retrieves the hour component from the {@code spawnTime}.<br>
	 * This value is extracted using the internal {@code int)} method.
	 * @return The hour as an {@code Integer}, or {@code null} if not set.
	 */
	public Integer geSpawnHour()
	{
		return getTime(spawnTime, 0);
	}
	
	/**
	 * Retrieves the day of the month for the spawn time.<br>
	 * This method calls {@code int)} with a type value of {@code 1}.
	 * @return The day as an {@code Integer} or {@code null} if not set.
	 */
	public Integer geSpawnDay()
	{
		return getTime(spawnTime, 1);
	}
	
	/**
	 * Retrieves the month associated with the spawn time.<br>
	 * This method parses the {@code spawnTime} string to extract the month value.
	 * @return The month as an {@code Integer}, or {@code null} if not available.
	 */
	public Integer getSpawnMonth()
	{
		return getTime(spawnTime, 2);
	}
	
	/**
	 * Retrieves the hour when this temporary spawn should disappear.<br>
	 * It uses the {@code despawn_time} attribute to calculate the value.
	 * @return The hour as an {@code Integer}.
	 */
	public Integer geDespawnHour()
	{
		return getTime(despawnTime, 0);
	}
	
	/**
	 * Retrieves the day of the month for the despawn time.<br>
	 * This method uses the {@code despawnTime} attribute to calculate the value.
	 * @return The day as an {@code Integer}, or {@code null} if not set.
	 */
	public Integer geDespawnDay()
	{
		return getTime(despawnTime, 1);
	}
	
	/**
	 * Retrieves the month when this temporary spawn should disappear.<br>
	 * It parses the {@code despawn_time} attribute to find the month value.
	 * @return The month as an {@code Integer}, or {@code null} if not set.
	 */
	public Integer getDespawnMonth()
	{
		return getTime(despawnTime, 2);
	}
	
	/**
	 * Converts a time string into an integer based on the specified type.<br>
	 * It splits the {@code time} string by dots and parses the part at index {@code type}.<br>
	 * If the value is {@code *}, it returns {@code null}.
	 * @param time The time string to parse, such as "hour.day.month".
	 * @param type The index of the part to retrieve (e.g., 0 for hour).
	 * @return The parsed integer value or {@code null} if the value is a wildcard.
	 */
	private Integer getTime(String time, int type)
	{
		final String result = time.split("\\.")[type];
		if (result.equals("*"))
		{
			return null;
		}
		
		return Integer.parseInt(result);
	}
	
	/**
	 * Retrieves the scheduled time when a temporary spawn should disappear.<br>
	 * This value is stored as a {@code String}.
	 * @return The {@code despawn_time} string.
	 */
	public String getDespawnTime()
	{
		return despawnTime;
	}
	
	/**
	 * Checks if the current game time matches the provided values.<br>
	 * It compares the hour, day, and month against {@link GameTimeManager}.<br>
	 * A value of {@code null} acts as a wildcard for that specific field.
	 * @param hour The required hour of the day.
	 * @param day The required day of the month.
	 * @param month The required month of the year.
	 * @return {@code true} if the current time matches all non-null parameters, otherwise {@code false}.
	 */
	private boolean isTime(Integer hour, Integer day, Integer month)
	{
		final GameTime gameTime = GameTimeManager.getGameTime();
		if ((hour != null) && (hour == gameTime.getHour()))
		{
			if (day == null)
			{
				return true;
			}
			
			if (day == gameTime.getDay())
			{
				return (month == null) || (month == gameTime.getMonth());
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the current time allows for a new spawn.<br>
	 * This method compares the current game time against the configured spawn window.
	 * @return {@code true} if spawning is currently allowed, {@code false} otherwise.
	 */
	public boolean canSpawn()
	{
		return isTime(geSpawnHour(), geSpawnDay(), getSpawnMonth());
	}
	
	/**
	 * Checks if the current time matches the scheduled despawn time.<br>
	 * This method uses {@code geDespawnHour}, {@code geDespawnDay}, and {@code getDespawnMonth} to verify the status.
	 * @return {@code true} if the entity is allowed to despawn, otherwise {@code false}.
	 */
	public boolean canDespawn()
	{
		return isTime(geDespawnHour(), geDespawnDay(), getDespawnMonth());
	}
	
	/**
	 * Checks if the current game time falls within the allowed spawning window.<br>
	 * This method compares the current hour, day, and month against the configured limits.<br>
	 * It returns {@code true} if the entity is allowed to spawn right now.
	 * @return {@code true} if the current time is valid for spawning, otherwise {@code false}.
	 */
	public boolean isInSpawnTime()
	{
		final GameTime gameTime = GameTimeManager.getGameTime();
		final Integer spawnHour = geSpawnHour();
		final Integer spawnDay = geSpawnDay();
		final Integer spawnMonth = getSpawnMonth();
		final Integer despawnHour = geDespawnHour();
		final Integer despawnDay = geDespawnDay();
		final Integer despawnMonth = getDespawnMonth();
		final int curentHour = gameTime.getHour();
		final int curentDay = gameTime.getDay();
		final int curentMonth = gameTime.getMonth();
		
		if (spawnMonth != null)
		{
			if (!checkTime(curentMonth, spawnMonth, despawnMonth))
			{
				return false;
			}
		}
		
		if (spawnDay != null)
		{
			if (!checkTime(curentDay, spawnDay, despawnDay))
			{
				return false;
			}
		}
		
		if ((spawnMonth == null) && (spawnDay == null) && !checkHour(curentHour, spawnHour, despawnHour))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if the current time falls within a specific window.<br>
	 * This method handles both standard and overnight time ranges.
	 * @param curentTime The current game time value.
	 * @param spawnTime The start time of the period.
	 * @param despawnTime The end time of the period.
	 * @return {@code true} if the time is valid, otherwise {@code false}.
	 */
	private boolean checkTime(int curentTime, int spawnTime, int despawnTime)
	{
		if (spawnTime < despawnTime)
		{
			if (!((curentTime >= spawnTime) && (curentTime <= despawnTime)))
			{
				return false;
			}
		}
		else if (spawnTime > despawnTime)
		{
			if (!((curentTime >= spawnTime) || (curentTime <= despawnTime)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the current hour falls within a specific time range.<br>
	 * This method handles both standard and overnight time windows.<br>
	 * It returns {@code true} if the time is valid for spawning.
	 * @param curentTime The current hour of the game day.
	 * @param spawnTime The starting hour for the spawn window.
	 * @param despawnTime The ending hour for the spawn window.
	 * @return {@code true} if the time is within the range, otherwise {@code false}.
	 */
	private boolean checkHour(int curentTime, int spawnTime, int despawnTime)
	{
		if (spawnTime < despawnTime)
		{
			if (!((curentTime >= spawnTime) && (curentTime < despawnTime)))
			{
				return false;
			}
		}
		else if (spawnTime > despawnTime)
		{
			if (!((curentTime >= spawnTime) || (curentTime < despawnTime)))
			{
				return false;
			}
		}
		
		return true;
	}
}
