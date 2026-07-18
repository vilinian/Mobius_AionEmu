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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.world.WeatherEntry;
import com.aionemu.gameserver.model.templates.world.WeatherTable;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WEATHER;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;

/**
 * Manages the global weather system for the game world.<br>
 * It handles weather transitions and updates {@link Player} objects with current conditions.<br>
 * This service uses data from {@link WeatherTable} to determine environmental effects.
 * @author ATracer
 * @author Kwazar
 * @reworked Rolandas
 */
public class WeatherService
{
	private final Map<WeatherKey, WeatherEntry[]> worldZoneWeathers;
	
	/**
	 * Provides access to the singleton instance of {@link WeatherService}.<br>
	 * Use this method to get the global weather manager.
	 * @return The single instance of {@code WeatherService}.
	 */
	public static WeatherService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link WeatherService} class.<br>
	 * It initializes the internal weather data structures.<br>
	 * This method is not intended to be called from outside this class.
	 */
	private WeatherService()
	{
		worldZoneWeathers = new HashMap<>();
		final GameTime gameTime = (GameTime) GameTimeManager.getGameTime().clone();
		for (Iterator<WorldMapTemplate> mapIterator = DataManager.WORLD_MAPS_DATA.iterator(); mapIterator.hasNext();)
		{
			final int mapId = mapIterator.next().getMapId();
			final WeatherTable table = DataManager.MAP_WEATHER_DATA.getWeather(mapId);
			if (table != null)
			{
				final WeatherKey key = new WeatherKey(gameTime, mapId);
				worldZoneWeathers.put(key, new WeatherEntry[table.getZoneCount()]);
				setNextWeather(key);
			}
		}
	}
	
	/**
	 * Key class used to store date of key creation (for rolling weather usage)
	 * @author Kwazar, Rolandas
	 */
	private class WeatherKey
	{
		private GameTime created;
		private final int mapId;
		
		public WeatherKey(GameTime createdTime, int mapId)
		{
			created = createdTime;
			this.mapId = mapId;
		}
		
		public int getMapId()
		{
			return mapId;
		}
		
		public GameTime getCreatedTime()
		{
			return created;
		}
		
		@Override
		public boolean equals(Object o)
		{
			final WeatherKey other = (WeatherKey) o;
			return mapId == other.mapId;
		}
		
		@Override
		public int hashCode()
		{
			return Integer.valueOf(mapId).hashCode();
		}
	}
	
	/**
	 * This method updates the weather for all world zones.<br>
	 * It schedules a task to run on a background thread.<br>
	 * Each zone will have its next weather state determined.
	 */
	public void checkWeathersTime()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			for (WeatherKey key : worldZoneWeathers.keySet())
			{
				setNextWeather(key);
				onWeatherChange(key.getMapId(), null);
			}
		}, 0);
	}
	
	/**
	 * Updates the weather for a specific map area.<br>
	 * This method calculates the next {@link WeatherEntry} based on current data.<br>
	 * It updates the {@code created} timestamp using {@link GameTimeManager}.
	 * @param key The unique identifier for the map zone.
	 */
	private synchronized void setNextWeather(WeatherKey key)
	{
		final WeatherEntry[] weatherEntries = getWeatherEntries(key.getMapId());
		final WeatherTable table = DataManager.MAP_WEATHER_DATA.getWeather(key.getMapId());
		key.created = (GameTime) GameTimeManager.getGameTime().clone();
		for (int zoneIndex = 0; zoneIndex < weatherEntries.length; zoneIndex++)
		{
			final WeatherEntry oldEntry = weatherEntries[zoneIndex];
			WeatherEntry newEntry = null;
			if (oldEntry == null)
			{
				newEntry = getRandomWeather(key.getCreatedTime(), table, zoneIndex + 1);
			}
			else
			{
				newEntry = table.getWeatherAfter(oldEntry);
				if (newEntry == null)
				{
					newEntry = getRandomWeather(key.getCreatedTime(), table, zoneIndex + 1);
				}
			}
			
			weatherEntries[zoneIndex] = newEntry;
		}
	}
	
	/**
	 * Selects a random weather entry based on the provided table and zone.<br>
	 * It uses weighted ranks and time-based logic to determine the result.
	 * @param createdTime The current {@code GameTime} used for day-time adjustments.
	 * @param table The {@link WeatherTable} containing available weather data.
	 * @param zoneId The unique identifier for the map zone.
	 * @return A {@link WeatherEntry} object representing the chosen weather.
	 */
	private WeatherEntry getRandomWeather(GameTime createdTime, WeatherTable table, int zoneId)
	{
		final List<WeatherEntry> weathers = table.getWeathersForZone(zoneId);
		
		int chance = Rnd.get(0, 700);
		
		// Rank 2 occurs twice as often as rank 1, which occurs twice as often as rank 0.
		int rank = 2;
		if (chance > 600)
		{
			rank = 0;
		}
		else if (chance > 400)
		{
			rank = 1;
		}
		
		final List<WeatherEntry> chosenWeather = new ArrayList<>();
		while (rank >= 0)
		{
			for (WeatherEntry entry : weathers)
			{
				if (entry.getRank() == -1)
				{
					return entry; // constant weather, maybe completely random ?
				}
				
				if (entry.getRank() == rank)
				{
					chosenWeather.add(entry);
				}
			}
			
			if (chosenWeather.size() > 0)
			{
				rank = -1;
				break;
			}
			
			rank--;
		}
		
		WeatherEntry newWeather = null;
		if (chosenWeather.size() == 0)
		{
			// no weather, code = 0
			newWeather = new WeatherEntry();
		}
		else
		{
			// Since almost all weather types have preceding and following conditions, the chances of selection are nearly equal.
			newWeather = chosenWeather.get(Rnd.get(chosenWeather.size()));
			
			// now find "before" weather if such exists
			if (!newWeather.isBefore())
			{
				for (WeatherEntry entry : weathers)
				{
					if (newWeather.getWeatherName().equals(entry.getWeatherName()) && entry.isBefore())
					{
						newWeather = entry;
						break;
					}
				}
			}
			
			// We do not want weather present every time; rank 2 is the strongest to appear and rank 0 is the weakest.
			int dayTimeCorrection = 1;
			if (createdTime.getDayTime() == DayTime.AFTERNOON)
			{
				dayTimeCorrection *= 2; // sunny days more often :)
			}
			
			chance = Rnd.get(0, 100);
			if (((newWeather.getRank() == 0) && (chance > (33 / dayTimeCorrection))) || ((newWeather.getRank() == 1) && (chance > (50 / dayTimeCorrection))) || ((newWeather.getRank() == 2) && (chance > (66 / dayTimeCorrection))))
			{
				newWeather = new WeatherEntry();
			}
			
			// TODO: check snow to not fall in summers
		}
		
		return newWeather;
	}
	
	/**
	 * Updates the current weather for a specific {@link Player}.<br>
	 * This method triggers the weather change logic based on the player's world ID.
	 * @param player The {@code Player} object to update.
	 */
	public void loadWeather(Player player)
	{
		onWeatherChange(player.getWorldId(), player);
	}
	
	/**
	 * Finds the {@code WeatherKey} associated with a specific map ID.<br>
	 * It searches through the internal weather data map.
	 * @param mapId The unique identifier for the map to look up.
	 * @return The matching {@code WeatherKey} or {@code null} if no match is found.
	 */
	private WeatherKey getWeatherKeyByMapId(int mapId)
	{
		for (WeatherKey key : worldZoneWeathers.keySet())
		{
			if (key.getMapId() == mapId)
			{
				return key;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the weather entries for a specific map.<br>
	 * This method looks up the {@code WeatherKey} based on the provided {@code mapId}.<br>
	 * It returns the associated array from the internal storage.
	 * @param mapId The unique identifier of the map to check.
	 * @return An array of {@link WeatherEntry} objects, or {@code null} if no key is found.
	 */
	private WeatherEntry[] getWeatherEntries(int mapId)
	{
		final WeatherKey key = getWeatherKeyByMapId(mapId);
		if (key == null)
		{
			return null;
		}
		
		return worldZoneWeathers.get(key);
	}
	
	/**
	 * Updates the weather for a specific map region.<br>
	 * This method sets the new {@code weatherCode} for all zones in the given {@code mapId}.<br>
	 * It then triggers the internal {@code Player)} logic.
	 * @param mapId The unique identifier of the map to update.
	 * @param weatherCode The new weather code to apply to the region.
	 */
	public synchronized void changeRegionWeather(int mapId, int weatherCode)
	{
		final WeatherKey key = new WeatherKey(null, mapId);
		final WeatherEntry[] weatherEntries = worldZoneWeathers.get(key);
		if (weatherEntries == null)
		{
			return; // do nothing
		}
		
		for (int i = 0; i < weatherEntries.length; i++)
		{
			final WeatherEntry oldEntry = weatherEntries[i];
			if (oldEntry == null)
			{
				weatherEntries[i] = new WeatherEntry(0, weatherCode);
			}
			else
			{
				weatherEntries[i] = new WeatherEntry(oldEntry.getZoneId(), weatherCode);
			}
		}
		
		onWeatherChange(mapId, null);
	}
	
	/**
	 * Resets all current weather entries to their default state.<br>
	 * This method clears the active weather codes for every loaded zone.<br>
	 * It then triggers a notification via {@code Player)}.
	 */
	public synchronized void resetWeather()
	{
		final Set<WeatherKey> loadedWeathers = new HashSet<>(worldZoneWeathers.keySet());
		for (WeatherKey key : loadedWeathers)
		{
			final WeatherEntry[] oldEntries = worldZoneWeathers.get(key);
			for (int i = 0; i < oldEntries.length; i++)
			{
				oldEntries[i] = new WeatherEntry(oldEntries[i].getZoneId(), 0);
			}
			
			onWeatherChange(key.getMapId(), null);
		}
	}
	
	/**
	 * Retrieves the specific weather code for a given map and zone.<br>
	 * This method searches through all {@code WeatherEntry} objects associated with the {@code mapId}.<br>
	 * It returns the code of the first entry that matches the provided {@code weatherZoneId}.<br>
	 * If no matching zone is found, it returns {@code 0}.
	 * @param mapId The unique identifier for the world map.
	 * @param weatherZoneId The specific zone ID within the map to check.
	 * @return The integer weather code or {@code 0} if not found.
	 */
	public int getWeatherCode(int mapId, int weatherZoneId)
	{
		final WeatherEntry[] weatherEntries = getWeatherEntries(mapId);
		for (WeatherEntry entry : weatherEntries)
		{
			if ((entry != null) && (entry.getZoneId() == weatherZoneId))
			{
				return entry.getCode();
			}
		}
		
		return 0;
	}
	
	/**
	 * Updates the weather for a specific map.<br>
	 * This method sends an {@code SM_WEATHER} packet to players.<br>
	 * It handles both single and multiple player updates.
	 * @param mapId The unique identifier of the map.
	 * @param player The {@link Player} instance to receive the update or {@code null} to update all players on the map.
	 */
	private void onWeatherChange(int mapId, Player player)
	{
		final WeatherEntry[] weatherEntries = getWeatherEntries(mapId);
		
		if (weatherEntries == null)
		{
			return;
		}
		
		if (player == null)
		{
			for (Iterator<Player> playerIterator = World.getInstance().getPlayersIterator(); playerIterator.hasNext();)
			{
				final Player currentPlayer = playerIterator.next();
				if (!currentPlayer.isSpawned())
				{
					continue;
				}
				
				if (currentPlayer.getWorldId() == mapId)
				{
					PacketSendUtility.sendPacket(currentPlayer, new SM_WEATHER(weatherEntries));
				}
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_WEATHER(weatherEntries));
		}
	}
	
	private static class SingletonHolder
	{
		protected static final WeatherService instance = new WeatherService();
	}
}
