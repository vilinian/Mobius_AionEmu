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
package com.aionemu.gameserver.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.world.handlers.WorldHandler;
import com.aionemu.gameserver.world.zone.ZoneAttributes;

/**
 * Represents a single in-game map within the game world.<br>
 * This class manages the map's data and its associated instances.
 * @author -Nemesiss-
 */
public class WorldMap
{
	private final WorldMapTemplate worldMapTemplate;
	private WorldHandler worldHandler;
	private final AtomicInteger nextInstanceId = new AtomicInteger(0);
	/**
	 * List of instances.
	 */
	private final Map<Integer, WorldMapInstance> instances = new ConcurrentHashMap<>();
	/**
	 * World to which belongs this WorldMap
	 */
	private final World world;
	private int worldOptions;
	
	/**
	 * Creates a new {@link WorldMap} object.<br>
	 * This constructor initializes the map using a template and a world.<br>
	 * It also sets up the required {@link WorldHandler} and creates initial instances.
	 * @param worldMapTemplate The template containing the map data.
	 * @param world The {@link World} that this map belongs to.
	 */
	public WorldMap(WorldMapTemplate worldMapTemplate, World world)
	{
		this.world = world;
		this.worldMapTemplate = worldMapTemplate;
		worldOptions = worldMapTemplate.getFlags();
		worldHandler = WorldEngine.getInstance().getNewInstanceHandler(worldMapTemplate.getMapId());
		worldHandler.onWorldCreate(this);
		for (int i = 1; i <= getInstanceCount(); i++)
		{
			final int nextId = getNextInstanceId();
			addInstance(nextId, WorldMapInstanceFactory.createWorldMapInstance(this, nextId));
		}
	}
	
	/**
	 * Retrieves the name of the map.<br>
	 * This method returns the {@code String} name from the {@link WorldMapTemplate}.
	 * @return The name of the map as a {@code String}.
	 */
	public String getName()
	{
		return worldMapTemplate.getName();
	}
	
	/**
	 * Retrieves the current water level of the map.<br>
	 * This value is used to determine environmental conditions.
	 * @return The {@code int} value representing the water level.
	 */
	public int getWaterLevel()
	{
		return worldMapTemplate.getWaterLevel();
	}
	
	/**
	 * Retrieves the death level for this map.<br>
	 * This value determines the difficulty or penalty associated with dying in this area.
	 * @return The current {@code int} death level.
	 */
	public int getDeathLevel()
	{
		return worldMapTemplate.getDeathLevel();
	}
	
	/**
	 * Gets the type of the world map.<br>
	 * This value is retrieved from the {@link WorldMapTemplate}.
	 * @return The {@link WorldType} associated with this map.
	 */
	public WorldType getWorldType()
	{
		return worldMapTemplate.getWorldType();
	}
	
	/**
	 * Retrieves the size of the world map.<br>
	 * This value corresponds to the {@code world_size} attribute.
	 * @return The integer size of the world.
	 */
	public int getWorldSize()
	{
		return worldMapTemplate.getWorldSize();
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value corresponds to the {@code mapId} field.
	 * @return The {@code Integer} ID of the map.
	 */
	public Integer getMapId()
	{
		return worldMapTemplate.getMapId();
	}
	
	/**
	 * Checks if flying is enabled on this map.<br>
	 * It looks at the current {@code worldOptions}.
	 * @return {@code true} if flying is allowed, {@code false} otherwise.
	 */
	public boolean isPossibleFly()
	{
		return (worldOptions & ZoneAttributes.FLY.getId()) != 0;
	}
	
	/**
	 * Checks if the map has a special buff exception.<br>
	 * This returns the value of the {@code exceptBuff} attribute.
	 * @return {@code true} if the buff is excepted, {@code false} otherwise.
	 */
	public boolean isExceptBuff()
	{
		return worldMapTemplate.isExceptBuff();
	}
	
	/**
	 * Checks if gliding is enabled on this map.<br>
	 * This method looks at the {@code flags} property to see if the glide attribute is set.
	 * @return {@code true} if players can glide, otherwise {@code false}.
	 */
	public boolean canGlide()
	{
		return (worldOptions & ZoneAttributes.GLIDE.getId()) != 0;
	}
	
	/**
	 * Checks if the map allows placing a kisk.<br>
	 * This method evaluates the {@code flags} property against the {@code BIND} ID.
	 * @return {@code true} if the bind flag is set, {@code false} otherwise.
	 */
	public boolean canPutKisk()
	{
		return (worldOptions & ZoneAttributes.BIND.getId()) != 0;
	}
	
	/**
	 * Checks if players are allowed to use the recall feature on this map.<br>
	 * It verifies if the {@code RECALL} attribute is active in the current world options.
	 * @return {@code true} if recall is enabled, {@code false} otherwise.
	 */
	public boolean canRecall()
	{
		return (worldOptions & ZoneAttributes.RECALL.getId()) != 0;
	}
	
	/**
	 * Checks if riding is allowed on this map.<br>
	 * This method evaluates the {@code flags} property.<br>
	 * It returns {@code true} if the ride attribute is enabled.
	 * @return {@code true} if riding is permitted, {@code false} otherwise.
	 */
	public boolean canRide()
	{
		return (worldOptions & ZoneAttributes.RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if flying while riding is allowed on this map.<br>
	 * This method evaluates the {@code flags} property against the {@code FLY_RIDE} ID.
	 * @return {@code true} if flying while riding is enabled, {@code false} otherwise.
	 */
	public boolean canFlyRide()
	{
		return (worldOptions & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}
	
	/**
	 * Checks if Player vs Player combat is enabled on this map.<br>
	 * This method evaluates the {@code flags} attribute against the {@code PVP_ENABLED} ID.
	 * @return {@code true} if PVP is allowed, {@code false} otherwise.
	 */
	public boolean isPvpAllowed()
	{
		return (worldOptions & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between players of the same race are permitted.<br>
	 * This method evaluates the current {@code flags} for this map.
	 * @return {@code true} if same-race duels are allowed, {@code false} otherwise.
	 */
	public boolean isSameRaceDuelsAllowed()
	{
		return (worldOptions & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Checks if duels between different races are permitted on this map.<br>
	 * This method evaluates the current {@code flags} against the {@code DUEL_OTHER_RACE_ENABLED} bitmask.
	 * @return {@code true} if other race duels are allowed, {@code false} otherwise.
	 */
	public boolean isOtherRaceDuelsAllowed()
	{
		return (worldOptions & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}
	
	/**
	 * Updates the world options by adding a specific attribute.<br>
	 * This method uses a bitwise OR operation to apply the {@code ZoneAttributes}.<br>
	 * It modifies the internal state of the current {@link WorldMap}.
	 * @param option The {@code ZoneAttributes} object to apply.
	 */
	public void setWorldOption(ZoneAttributes option)
	{
		worldOptions |= option.getId();
	}
	
	/**
	 * Removes a specific option from the world configuration.<br>
	 * This method updates the {@code worldOptions} bitmask by clearing the bits associated with the provided {@link ZoneAttributes}.
	 * @param option The {@code ZoneAttributes} object containing the ID of the option to remove.
	 */
	public void removeWorldOption(ZoneAttributes option)
	{
		worldOptions &= ~option.getId();
	}
	
	/**
	 * Checks if a specific zone attribute is overridden for this map.<br>
	 * It compares the template flags against the current world options.
	 * @param option The {@code ZoneAttributes} to check.
	 * @return {@code true} if the option is currently active and overridden, {@code false} otherwise.
	 */
	public boolean hasOverridenOption(ZoneAttributes option)
	{
		if ((worldMapTemplate.getFlags() & option.getId()) == 0)
		{
			return (worldOptions & option.getId()) != 0;
		}
		
		return (worldOptions & option.getId()) == 0;
	}
	
	/**
	 * Returns the total number of instances for this map.<br>
	 * This count includes the base twins and beginner twins defined in the {@link WorldMapTemplate}.
	 * @return The total instance count as an {@code int}.
	 */
	public int getInstanceCount()
	{
		int twinCount = worldMapTemplate.getTwinCount();
		if ((twinCount == 0) && !worldMapTemplate.isInstance())
		{
			twinCount = 1;
		}
		
		twinCount += worldMapTemplate.getBeginnerTwinCount();
		return twinCount;
	}
	
	/**
	 * Retrieves the primary instance of this {@link WorldMap}.<br>
	 * This method returns the instance with an ID of {@code 1}.
	 * @return The main {@link WorldMapInstance} for this map.
	 */
	public WorldMapInstance getMainWorldMapInstance()
	{
		// TODO Balance players into instances.
		return getWorldMapInstance(1);
	}
	
	/**
	 * Retrieves a specific {@link WorldMapInstance} using its unique identifier.<br>
	 * If the provided {@code instanceId} is 0, it defaults to 1.<br>
	 * This method validates that the ID exists within the current map limits.
	 * @param instanceId The unique ID of the map instance to find.
	 * @return The requested {@link WorldMapInstance} object.
	 */
	public WorldMapInstance getWorldMapInstanceById(int instanceId)
	{
		// instanceId is a count, some code still uses 0 for the default instance
		if (instanceId == 0)
		{
			instanceId = 1;
		}
		
		if (!isInstanceType())
		{
			if (instanceId > getInstanceCount())
			{
				throw new IllegalArgumentException("WorldMapInstance " + getMapId() + " has lower instances count than " + instanceId);
			}
		}
		
		return getWorldMapInstance(instanceId);
	}
	
	/**
	 * Retrieves a specific {@link WorldMapInstance} using its unique ID.<br>
	 * If the provided {@code instanceId} is {@code 0}, it defaults to {@code 1}.
	 * @param instanceId The unique identifier for the map instance.
	 * @return The {@link WorldMapInstance} associated with the ID, or {@code null} if not found.
	 */
	private WorldMapInstance getWorldMapInstance(int instanceId)
	{
		// instanceId is a count, some code still uses 0 for the default instance
		if (instanceId == 0)
		{
			instanceId = 1;
		}
		
		return instances.get(instanceId);
	}
	
	/**
	 * Removes a specific map instance from the current world map.<br>
	 * If {@code instanceId} is {@code 0}, it will be treated as {@code 1}.<br>
	 * This method updates the internal collection of instances.
	 * @param instanceId The unique identifier of the instance to remove.
	 */
	public void removeWorldMapInstance(int instanceId)
	{
		// instanceId is a count, some code still uses 0 for the default instance
		if (instanceId == 0)
		{
			instanceId = 1;
		}
		
		instances.remove(instanceId);
	}
	
	/**
	 * Adds a new {@link WorldMapInstance} to the map.<br>
	 * If the {@code instanceId} is {@code 0}, it will be automatically changed to {@code 1}.<br>
	 * This method stores the instance in the internal collection.
	 * @param instanceId The unique identifier for the instance.
	 * @param instance The {@link WorldMapInstance} object to add.
	 */
	public void addInstance(int instanceId, WorldMapInstance instance)
	{
		// instanceId is a count, some code still uses 0 for the default instance
		if (instanceId == 0)
		{
			instanceId = 1;
		}
		
		instances.put(instanceId, instance);
	}
	
	/**
	 * Retrieves the {@link World} associated with this map region.<br>
	 * This method gets the world by accessing the parent {@link WorldMapInstance}.
	 * @return The {@code World} object.
	 */
	public World getWorld()
	{
		return world;
	}
	
	/**
	 * Retrieves the template for this map.<br>
	 * This provides access to the base configuration of the {@link WorldMap}.
	 * @return The {@code WorldMapTemplate} associated with this map.
	 */
	public WorldMapTemplate getTemplate()
	{
		return worldMapTemplate;
	}
	
	/**
	 * Generates a unique identifier for a new map instance.<br>
	 * This method increments the internal counter and returns the new value.
	 * @return The next available {@code int} ID for an instance.
	 */
	public int getNextInstanceId()
	{
		return nextInstanceId.incrementAndGet();
	}
	
	/**
	 * Checks if this map is an instance type.<br>
	 * This method queries the {@link WorldMapTemplate} to determine the map category.
	 * @return {@code true} if the map is an instance, {@code false} otherwise.
	 */
	public boolean isInstanceType()
	{
		return worldMapTemplate.isInstance();
	}
	
	/**
	 * Returns an {@link Iterator} for all active map instances.<br>
	 * This allows you to loop through every {@link WorldMapInstance} in this map.
	 * @return An {@code Iterator} containing the collection of {@link WorldMapInstance} objects.
	 */
	public Iterator<WorldMapInstance> iterator()
	{
		return instances.values().iterator();
	}
	
	/**
	 * Retrieves all unique IDs for the current map's instances.<br>
	 * This method returns a collection of keys from the internal instance map.
	 * @return A {@code Collection<Integer>} containing all active instance IDs.
	 */
	public Collection<Integer> getAvailableInstanceIds()
	{
		return instances.keySet();
	}
	
	/**
	 * Retrieves all active instances for this {@link WorldMap}.<br>
	 * This method returns a collection of every {@code WorldMapInstance} currently loaded.
	 * @return A {@code Collection} containing all {@code WorldMapInstance} objects.
	 */
	public Collection<WorldMapInstance> getInstances()
	{
		return instances.values();
	}
	
	/**
	 * Retrieves the {@link WorldHandler} for this map.<br>
	 * This handler manages the logic and events of the world.
	 * @return the current {@code WorldHandler} instance.
	 */
	public WorldHandler getWorldHandler()
	{
		return worldHandler;
	}
	
	/**
	 * Sets the {@link WorldHandler} for this map.<br>
	 * This defines how the game handles logic within this specific world.
	 * @param worldHandler The new {@code WorldHandler} to assign.
	 */
	public void setWorldHandler(WorldHandler worldHandler)
	{
		this.worldHandler = worldHandler;
	}
}
