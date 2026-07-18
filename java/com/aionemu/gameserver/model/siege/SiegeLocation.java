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
package com.aionemu.gameserver.model.siege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.base.BaseTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.SiegeZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;

/**
 * Represents a specific location within a siege zone.<br>
 * This class handles the logic and data for {@link SiegeZoneInstance} areas.<br>
 * It implements {@link ZoneHandler} to manage world interactions.
 * @author Sarynth, Source, Wakizashi
 */
public class SiegeLocation implements ZoneHandler
{
	private static final Logger log = LoggerFactory.getLogger(SiegeLocation.class);
	public static final int STATE_INVULNERABLE = 0;
	public static final int STATE_VULNERABLE = 1;
	/**
	 * Unique id, defined by NCSoft
	 */
	protected SiegeLocationTemplate template;
	protected int locationId;
	protected int occupyCount;
	protected SiegeType type;
	protected int worldId;
	protected SiegeRace siegeRace = SiegeRace.BALAUR;
	protected int legionId;
	protected long lastArtifactActivation;
	private boolean vulnerable;
	private int nextState;
	protected List<SiegeZoneInstance> zone;
	private List<SiegeShield> shields;
	protected boolean isUnderShield;
	protected boolean canTeleport;
	protected int siegeDuration;
	protected int influenceValue;
	private final Map<Integer, Creature> creatures = new HashMap<>();
	private final Map<Integer, Player> players = new HashMap<>();
	protected int buffId;
	protected int buffIdA;
	protected int buffIdE;
	protected int baseId;
	
	/**
	 * Creates a new instance of {@code SiegeLocation}.<br>
	 * This constructor initializes the object with default values.
	 */
	public SiegeLocation()
	{
	}
	
	/**
	 * Creates a new {@code SiegeLocation} instance using the provided template.<br>
	 * This constructor initializes all core fields from the {@link SiegeLocationTemplate}.
	 * @param template The {@code SiegeLocationTemplate} containing the configuration data.
	 */
	public SiegeLocation(SiegeLocationTemplate template)
	{
		this.template = template;
		locationId = template.getId();
		worldId = template.getWorldId();
		type = template.getType();
		siegeDuration = template.getSiegeDuration();
		zone = new ArrayList<>();
		influenceValue = template.getInfluenceValue();
		occupyCount = template.getOccupyCount();
	}
	
	/**
	 * Retrieves the {@link SiegeLocationTemplate} for this location.<br>
	 * This provides access to the base configuration data.
	 * @return The {@code SiegeLocationTemplate} associated with this instance.
	 */
	public SiegeLocationTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the unique identifier for this siege location.<br>
	 * This ID is defined by NCSoft.
	 * @return The {@code int} value of the {@code locationId}.
	 */
	public int getLocationId()
	{
		return locationId;
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
	 * Retrieves the {@link SiegeType} of this location.<br>
	 * This identifies what kind of siege is occurring here.
	 * @return the current {@code SiegeType}
	 */
	public SiegeType getType()
	{
		return type;
	}
	
	/**
	 * Retrieves the total duration of the current siege.<br>
	 * This value is stored in the {@code siegeDuration} field.
	 * @return The length of the siege as an {@code int}.
	 */
	public int getSiegeDuration()
	{
		return siegeDuration;
	}
	
	/**
	 * Retrieves the current race of the siege.<br>
	 * This method returns the {@code SiegeRace} assigned to this location.
	 * @return The {@code SiegeRace} of the siege.
	 */
	public SiegeRace getRace()
	{
		return siegeRace;
	}
	
	/**
	 * Sets the race type for this {@link SiegeLocation}.<br>
	 * This updates the internal {@code siegeRace} field.
	 * @param siegeRace The new {@code SiegeRace} to assign.
	 */
	public void setRace(SiegeRace siegeRace)
	{
		this.siegeRace = siegeRace;
	}
	
	/**
	 * Retrieves the unique identifier for the player's legion.<br>
	 * This value is stored in the {@code legionId} field.
	 * @return The {@code int} ID of the legion.
	 */
	public int getLegionId()
	{
		return legionId;
	}
	
	/**
	 * Sets the unique identifier for the legion.<br>
	 * This value is used to identify which legion owns this location.
	 * @param legionId The {@code int} ID of the legion.
	 */
	public void setLegionId(int legionId)
	{
		this.legionId = legionId;
	}
	
	/**
	 * Retrieves the current number of occupants in this location.<br>
	 * This value represents how many entities are currently occupying the area.
	 * @return The total count of occupants as an {@code int}.
	 */
	public int getOccupyCount()
	{
		return occupyCount;
	}
	
	/**
	 * Updates the number of entities currently occupying this location.<br>
	 * This value is used to track how many players or creatures are present.
	 * @param occupyCount The new count to set for the location.
	 */
	public void setOccupyCount(int occupyCount)
	{
		this.occupyCount = occupyCount;
	}
	
	/**
	 * Retrieves the current value of the {@code nextState} variable.<br>
	 * This represents the state transition for the siege location.
	 * @return The integer value of the next state.
	 */
	public int getNextState()
	{
		return nextState;
	}
	
	/**
	 * Updates the value of the {@code nextState} field.<br>
	 * This method determines what state the siege location will transition to next.
	 * @param nextState The new integer value for the next state.
	 */
	public void setNextState(int nextState)
	{
		this.nextState = nextState;
	}
	
	/**
	 * Checks if the current siege location is in a vulnerable state.<br>
	 * This method returns the value of the {@code vulnerable} field.
	 * @return {@code true} if the location is vulnerable, or {@code false} otherwise.
	 */
	public boolean isVulnerable()
	{
		return vulnerable;
	}
	
	/**
	 * Checks if the owner of this controller is currently under a shield.
	 * @return {@code true} if the owner is shielded, otherwise {@code false}.
	 */
	public boolean isUnderShield()
	{
		return isUnderShield;
	}
	
	/**
	 * Sets whether the location is currently protected by a shield.<br>
	 * This method also enables or disables all active {@code SiegeShield} objects.
	 * @param value The new shield status to apply. Use {@code true} to enable and {@code false} to disable.
	 */
	public void setUnderShield(boolean value)
	{
		isUnderShield = value;
		if (shields != null)
		{
			for (SiegeShield shield : shields)
			{
				shield.setEnabled(value);
			}
		}
	}
	
	/**
	 * Updates the list of active shields for this siege location.<br>
	 * This method replaces the current {@code shields} collection with a new one.<br>
	 * It logs each shield added to the location for debugging purposes.
	 * @param shields The list of {@link SiegeShield} objects to assign to this location.
	 */
	public void setShields(List<SiegeShield> shields)
	{
		this.shields = shields;
		log.debug("Attached shields for locId: " + locationId);
		for (SiegeShield shield : shields)
		{
			log.debug(shield.toString());
		}
	}
	
	/**
	 * Checks if teleportation is enabled for this location.<br>
	 * This method returns the current state of the {@code canTeleport} flag.
	 * @param player The {@link Player} object being checked.
	 * @return {@code true} if teleporting is allowed, otherwise {@code false}.
	 */
	public boolean isCanTeleport(Player player)
	{
		return canTeleport;
	}
	
	/**
	 * Sets whether teleportation is allowed for this location.<br>
	 * This updates the {@code canTeleport} field.
	 * @param canTeleport The boolean value to set for teleport permission.
	 */
	public void setCanTeleport(boolean canTeleport)
	{
		this.canTeleport = canTeleport;
	}
	
	/**
	 * Updates the vulnerability status of the siege location.<br>
	 * This method sets the {@code vulnerable} flag to the provided value.
	 * @param value The new vulnerability state to set.
	 */
	public void setVulnerable(boolean value)
	{
		vulnerable = value;
	}
	
	/**
	 * Retrieves the current influence value of this siege location.<br>
	 * This value represents the amount of control or power held at the site.
	 * @return The current {@code int} influence value.
	 */
	public int getInfluenceValue()
	{
		return influenceValue;
	}
	
	/**
	 * Retrieves the list of {@link SiegeZoneInstance} objects associated with this location.
	 * @return a {@code List} containing all {@code SiegeZoneInstance} objects.
	 */
	public List<SiegeZoneInstance> getZone()
	{
		return zone;
	}
	
	/**
	 * Adds a new {@code SiegeZoneInstance} to the internal list.<br>
	 * This method also registers this {@link SiegeLocation} as a handler for the zone.
	 * @param zone The {@code SiegeZoneInstance} to be added.
	 */
	public void addZone(SiegeZoneInstance zone)
	{
		this.zone.add(zone);
		zone.addHandler(this);
	}
	
	/**
	 * Checks if a specific creature is located within this siege area.<br>
	 * It iterates through all associated {@link SiegeZoneInstance} objects to verify the position.
	 * @param creature The {@code Creature} object to check.
	 * @return {@code true} if the creature is inside any zone, otherwise {@code false}.
	 */
	public boolean isInsideLocation(Creature creature)
	{
		if (zone.isEmpty())
		{
			return false;
		}
		
		for (int i = 0; i < zone.size(); i++)
		{
			if (zone.get(i).isInsideCreature(creature))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a player is currently inside an active siege zone.<br>
	 * The zone must be in a vulnerable state for this to return {@code true}.
	 * @param player The {@link Player} object to check.
	 * @return {@code true} if the player is in a vulnerable siege location, otherwise {@code false}.
	 */
	public boolean isInActiveSiegeZone(Player player)
	{
		if (isVulnerable() && isInsideLocation(player))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Clears all enemy entities from the current location.<br>
	 * It kills any {@code Kisk} objects found in the area.<br>
	 * It teleports all enemy {@link Player} instances to their bind locations.
	 */
	public void clearLocation()
	{
	}
	
	/**
	 * Tracks a {@code Creature} when it enters a specific {@code ZoneInstance}.<br>
	 * This method adds the creature to the internal tracking maps.<br>
	 * If the creature is a {@link Player}, it is also added to the player map.
	 * @param creature The {@code Creature} entering the zone.
	 * @param zone The {@code ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		if (!creatures.containsKey(creature.getObjectId()))
		{
			creatures.put(creature.getObjectId(), creature);
			if (creature instanceof Player)
			{
				players.put(creature.getObjectId(), (Player) creature);
			}
		}
	}
	
	/**
	 * This method is called when a {@link Creature} leaves a {@link ZoneInstance}.<br>
	 * It removes the observer from the creature if it is a non-GM player.<br>
	 * The logic ensures that observation effects are cleared correctly.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} being exited.
	 */
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone)
	{
		if (!isInsideLocation(creature))
		{
			creatures.remove(creature.getObjectId());
			players.remove(creature.getObjectId());
		}
	}
	
	/**
	 * Iterates through all {@link Player} objects in the current location.<br>
	 * Applies the provided {@code Visitor} to each non-null player found.<br>
	 * Logs an error if any exception occurs during the process.
	 * @param visitor The {@code Visitor} to apply to every player.
	 */
	public void doOnAllPlayers(Visitor<Player> visitor)
	{
		try
		{
			for (Map.Entry<Integer, Player> e : players.entrySet())
			{
				final Player player = e.getValue();
				if (player != null)
				{
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex)
		{
			log.error("Exception when running visitor on all players" + ex);
		}
	}
	
	/**
	 * Retrieves all creatures currently in this siege location.<br>
	 * The results are stored in a {@code Map}.
	 * @return A {@code Map} containing the unique IDs and {@link Creature} objects.
	 */
	public Map<Integer, Creature> getCreatures()
	{
		return creatures;
	}
	
	/**
	 * Retrieves the list of players currently at this location.<br>
	 * The map uses {@code Integer} IDs as keys to identify each {@link Player}.
	 * @return A {@code Map} containing all current {@code Player} objects.
	 */
	public Map<Integer, Player> getPlayers()
	{
		return players;
	}
	
	/**
	 * Retrieves the unique buffer identifier from the {@link SiegeLocationTemplate}.<br>
	 * This method updates the local {@code buffId} field with the value from the template.
	 * @return The integer ID of the buff associated with this location.
	 */
	public int getBuffId()
	{
		return buffId = template.getBuffId();
	}
	
	/**
	 * Retrieves the unique identifier for buff A from the template.<br>
	 * This method updates the local {@code buffIdA} field with the value from {@code getBuffIdA}.
	 * @return The integer ID of buff A.
	 */
	public int getBuffIdA()
	{
		return buffIdA = template.getBuffIdA();
	}
	
	/**
	 * Retrieves the special buff ID from the {@link SiegeLocationTemplate}.<br>
	 * This method updates the local {@code buffIdE} field with the value.
	 * @return The integer value of the buff ID.
	 */
	public int getBuffIdE()
	{
		return buffIdE = template.getBuffIdE();
	}
	
	/**
	 * Retrieves the unique identifier for this siege location.<br>
	 * This value is fetched from the {@link SiegeLocationTemplate}.<br>
	 * It also updates the internal {@code baseId} field.
	 * @return The unique integer ID of the siege location.
	 */
	public int getBaseId()
	{
		return baseId = template.getBaseId();
	}
}
