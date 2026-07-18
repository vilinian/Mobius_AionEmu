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
package com.aionemu.gameserver.model.templates.spawns.siegespawns;

import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.siege.SiegeSpawnType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents the configuration template for monster spawns specifically used during siege events.<br>
 * This class extends {@link SpawnTemplate} to include siege-specific data such as race and spawn types.
 * @author xTz
 */
public class SiegeSpawnTemplate extends SpawnTemplate
{
	private int siegeId;
	private SiegeRace siegeRace;
	private SiegeSpawnType siegeSpawnType;
	private SiegeModType siegeModType;
	
	/**
	 * Creates a new {@link SiegeSpawnTemplate} using existing group and spot data.<br>
	 * This constructor initializes the template with shared spawn properties.
	 * @param spawnGroup The {@code SpawnGroup2} that this spawn belongs to.
	 * @param spot The {@code SpawnSpotTemplate} defining the location for this spawn.
	 */
	public SiegeSpawnTemplate(SpawnGroup2 spawnGroup, SpawnSpotTemplate spot)
	{
		super(spawnGroup, spot);
	}
	
	/**
	 * Creates a new {@link SiegeSpawnTemplate} using specific coordinates and movement settings.<br>
	 * This constructor initializes the spawn data for siege-related entities.
	 * @param spawnGroup The {@link SpawnGroup2} group this spawn belongs to.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The initial rotation direction in degrees.
	 * @param randWalk The range for random movement walking.
	 * @param walkerId The unique identifier for the walker animation.
	 * @param staticId The ID used to identify a static object.
	 * @param fly A flag indicating if the entity can fly.
	 */
	public SiegeSpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int staticId, int fly)
	{
		super(spawnGroup, x, y, z, heading, randWalk, walkerId, staticId, fly);
	}
	
	/**
	 * Retrieves the unique identifier for this siege.<br>
	 * This ID identifies which specific siege event the {@code SiegeNpc} belongs to.
	 * @return The {@code int} value of the siege ID.
	 */
	public int getSiegeId()
	{
		return siegeId;
	}
	
	/**
	 * Retrieves the race associated with this {@link SiegeNpc}.<br>
	 * This identifies which faction the NPC belongs to during a siege.
	 * @return the {@code SiegeRace} of the current NPC.
	 */
	public SiegeRace getSiegeRace()
	{
		return siegeRace;
	}
	
	/**
	 * Retrieves the type of the siege spawn.<br>
	 * This method returns the {@code SiegeSpawnType} associated with this template.
	 * @return the {@code SiegeSpawnType} of the siege spawn.
	 */
	public SiegeSpawnType getSiegeSpawnType()
	{
		return siegeSpawnType;
	}
	
	/**
	 * Retrieves the modification type for this siege spawn.<br>
	 * This value determines how the siege behavior is modified.
	 * @return the {@code SiegeModType} associated with this template.
	 */
	public SiegeModType getSiegeModType()
	{
		return siegeModType;
	}
	
	/**
	 * Sets the unique identifier for a siege.<br>
	 * This value is used to link the spawn to a specific siege event.
	 * @param siegeId The {@code int} ID of the siege.
	 */
	public void setSiegeId(int siegeId)
	{
		this.siegeId = siegeId;
	}
	
	/**
	 * Sets the race for this {@link SiegeSpawnTemplate}.<br>
	 * This defines which faction or race the spawn belongs to.
	 * @param siegeRace The {@code SiegeRace} to assign.
	 */
	public void setSiegeRace(SiegeRace siegeRace)
	{
		this.siegeRace = siegeRace;
	}
	
	/**
	 * Sets the spawn type for this {@link SiegeSpawnTemplate}.<br>
	 * This defines how the siege entities appear in the world.
	 * @param siegeSpawnType The new {@code SiegeSpawnType} to assign.
	 */
	public void setSiegeSpawnType(SiegeSpawnType siegeSpawnType)
	{
		this.siegeSpawnType = siegeSpawnType;
	}
	
	/**
	 * Sets the modification type for this siege spawn.<br>
	 * This updates the {@code siegeModType} field of the template.
	 * @param siegeModType The new {@link SiegeModType} to assign.
	 */
	public void setSiegeModType(SiegeModType siegeModType)
	{
		this.siegeModType = siegeModType;
	}
	
	/**
	 * Checks if the current spawn template is set to peace mode.<br>
	 * This method compares the {@code siegeModType} against {@code PEACE}.
	 * @return {@code true} if the mode is peace, otherwise {@code false}.
	 */
	public boolean isPeace()
	{
		return siegeModType.equals(SiegeModType.PEACE);
	}
	
	/**
	 * Checks if the spawn template belongs to a {@code SiegeModType}.<br>
	 * It compares the current {@code siegeModType} against {@code SIEGE}.
	 * @return {@code true} if it is a siege type, otherwise {@code false}.
	 */
	public boolean isSiege()
	{
		return siegeModType.equals(SiegeModType.SIEGE);
	}
	
	/**
	 * Checks if the spawn template is configured as an assault.<br>
	 * This method compares the {@code siegeModType} to {@code ASSAULT}.
	 * @return {@code true} if it is an assault, {@code false} otherwise.
	 */
	public boolean isAssault()
	{
		return siegeModType.equals(SiegeModType.ASSAULT);
	}
}
