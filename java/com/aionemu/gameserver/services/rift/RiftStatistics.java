package com.aionemu.gameserver.services.rift;

/**
 * This class manages and tracks statistics related to the {@code Rift} system.<br>
 * It provides data for monitoring game events and player activities within rifts.
 * @author CoolyT
 */
public class RiftStatistics
{
	int worldMap = 0;
	int spawnedRifts = 0;
	int spawnedNpcs = 0;
	boolean isSpawned = false;
	boolean isVortex = false;
	
	/**
	 * Retrieves the current value of the {@code worldMap}.<br>
	 * This method returns the map identifier associated with the rift.
	 * @return the current {@code worldMap} value.
	 */
	public int getWorldMap()
	{
		return worldMap;
	}
	
	/**
	 * Updates the current {@code worldMap} value.<br>
	 * This method sets the map ID for the rift statistics.
	 * @param worldMap The new integer ID for the world map.
	 */
	public void setWorldMap(int worldMap)
	{
		this.worldMap = worldMap;
	}
	
	/**
	 * Retrieves the total number of rifts that have been spawned.<br>
	 * This value is stored in the {@code spawnedRifts} field.
	 * @return The count of spawned rifts as an {@code int}.
	 */
	public int getSpawnedRifts()
	{
		return spawnedRifts;
	}
	
	/**
	 * Updates the total number of rifts that have been spawned.<br>
	 * This method sets the {@code spawnedRifts} field to a new value.
	 * @param spawnedRifts The new count of spawned rifts.
	 */
	public void setSpawnedRifts(int spawnedRifts)
	{
		this.spawnedRifts = spawnedRifts;
	}
	
	/**
	 * Increases the total count of {@code spawnedRifts}.<br>
	 * This method adds the provided value to the current total.
	 * @param spawnedRifts The number of rifts to add.
	 */
	public void addSpawnedRifts(int spawnedRifts)
	{
		this.spawnedRifts += spawnedRifts;
	}
	
	/**
	 * Retrieves the total number of NPCs that have been spawned.<br>
	 * This value is stored in the {@code spawnedNpcs} field.
	 * @return The count of spawned NPCs as an {@code int}.
	 */
	public int getSpawnedNpcs()
	{
		return spawnedNpcs;
	}
	
	/**
	 * Updates the total number of NPCs that have been spawned.<br>
	 * This method sets the {@code spawnedNpcs} field to a new value.
	 * @param spawnedNpcs The new count of spawned NPCs.
	 */
	public void setSpawnedNpcs(int spawnedNpcs)
	{
		this.spawnedNpcs = spawnedNpcs;
	}
	
	/**
	 * Updates the total count of NPCs that have been spawned.<br>
	 * This method adds the provided value to the current {@code spawnedNpcs} count.
	 * @param spawnedNpcs The number of NPCs to add to the total.
	 */
	public void addSpawnedNpcs(int spawnedNpcs)
	{
		this.spawnedNpcs += spawnedNpcs;
	}
	
	/**
	 * Checks if the current position of this object is a valid spawn point.<br>
	 * This method delegates the check to the {@code isSpawned} method.
	 * @return {@code true} if the position is spawned, {@code false} otherwise.
	 */
	public boolean isSpawned()
	{
		return isSpawned;
	}
	
	/**
	 * Updates the spawn status of the rift.<br>
	 * This method sets the {@code isSpawned} field to the provided value.
	 * @param isSpawned The new spawn state to set. Use {@code true} if spawned and {@code false} otherwise.
	 */
	public void setSpawned(boolean isSpawned)
	{
		this.isSpawned = isSpawned;
	}
	
	/**
	 * Checks if this controller represents a vortex.
	 * @return {@code true} if the object is a vortex, {@code false} otherwise.
	 */
	public boolean isVortex()
	{
		return isVortex;
	}
	
	/**
	 * Updates the vortex status of the rift.<br>
	 * This method sets the {@code isVortex} field to the provided value.
	 * @param isVortex The new boolean state for the vortex.
	 */
	public void setVortex(boolean isVortex)
	{
		this.isVortex = isVortex;
	}
	
}
