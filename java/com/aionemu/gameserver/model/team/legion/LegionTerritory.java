/**
 * 
 */
package com.aionemu.gameserver.model.team.legion;

/**
 * Represents a specific territory owned or controlled by a {@link Legion}.<br>
 * It manages the spatial data and ownership status of geographic areas in the game world.
 * @author CoolyT
 */
public class LegionTerritory
{
	int territoryId = 0;
	int legionId = 0;
	String legionName = "";
	
	/**
	 * Creates a new {@link LegionTerritory} instance.<br>
	 * This constructor initializes the territory with a specific ID.
	 * @param id The unique identifier for the territory.
	 */
	public LegionTerritory(int id)
	{
		territoryId = id;
	}
	
	/**
	 * Creates a new instance of {@link LegionTerritory}.<br>
	 * This constructor initializes the object with default values.
	 */
	public LegionTerritory()
	{
	}
	
	/**
	 * Retrieves the unique identifier for this {@link LegionTerritory}.<br>
	 * This value corresponds to the internal territory ID.
	 * @return The integer ID of the territory.
	 */
	public int getId()
	{
		return territoryId;
	}
	
	/**
	 * Sets the unique identifier for the territory.<br>
	 * This updates the {@code territoryId} field of the {@link LegionTerritory} object.
	 * @param terretoryId The new ID to assign to the territory.
	 */
	public void setTerritoryId(int terretoryId)
	{
		territoryId = terretoryId;
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
	 * Retrieves the name of the player's legion.<br>
	 * This value is stored in the {@code legionName} field.
	 * @return The name of the legion as a {@code String}.
	 */
	public String getLegionName()
	{
		return legionName;
	}
	
	/**
	 * Sets the name of the {@code Legion}.<br>
	 * This updates the internal {@code legionName} field.
	 * @param legionName The new name to assign to the legion.
	 */
	public void setLegionName(String legionName)
	{
		this.legionName = legionName;
	}
}
