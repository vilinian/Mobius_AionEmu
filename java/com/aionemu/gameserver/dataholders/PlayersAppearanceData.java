package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.appearances.PlayerApp;

/**
 * This class holds the collection of appearance data for all players.<br>
 * It serves as a data holder for mapping {@link PlayerApp} objects to their respective IDs.
 * @author CoolyT
 */
@XmlRootElement(name = "players")
public class PlayersAppearanceData
{
	@XmlElement(name = "player")
	public static List<PlayerApp> players = new ArrayList<>();
	public static Map<String, PlayerApp> playerApps = new HashMap<>();
	
	/**
	 * Adds a new {@link PlayerApp} to the global list.<br>
	 * This method checks if the player is already present before adding.<br>
	 * It updates the internal data structure for appearances.
	 * @param player The {@code PlayerApp} object to be added.
	 */
	public void addPlayer(PlayerApp player)
	{
		if (players.contains(player))
		{
			players.add(player);
		}
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code playerApps} map using the list of {@link PlayerApp} objects.<br>
	 * The {@code playerApps} map is updated with each player's name in lowercase as the key.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (PlayerApp ap : players)
		{
			playerApps.put(ap.name.toLowerCase(), ap);
		}
	}
	
	/**
	 * Retrieves the list of all player appearances.<br>
	 * This method returns the {@code players} collection from the current class.
	 * @return A {@link List} containing all {@link PlayerApp} objects.
	 */
	public static List<PlayerApp> getApp()
	{
		return players;
	}
	
	/**
	 * Retrieves a {@link PlayerApp} object based on the provided name.<br>
	 * The search is case-insensitive.<br>
	 * Returns a new empty {@code PlayerApp} if no match is found.
	 * @param name The name of the appearance to look up.
	 * @return The matching {@code PlayerApp} or a new instance.
	 */
	public PlayerApp getAppearanceByName(String name)
	{
		name = name.toLowerCase();
		final PlayerApp app = new PlayerApp();
		if (playerApps.containsKey(name))
		{
			return playerApps.get(name);
		}
		
		return app;
	}
	
	/**
	 * Returns the total number of players.<br>
	 * This method returns the size of the {@code players} list.
	 * @return The count of player appearances currently loaded.
	 */
	public int size()
	{
		return players.size();
	}
}
