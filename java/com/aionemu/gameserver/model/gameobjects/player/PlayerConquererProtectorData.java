/**
 * 
 */
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * This class stores data related to the protector status of a {@link Player}.<br>
 * It manages information for players who have acquired conqueror protections.<br>
 * Use this model to track specific attributes associated with these special permissions.
 * @author CoolyT
 */
public class PlayerConquererProtectorData
{
	private boolean isProtector = false;
	private int ProtectorBuffId = 0;
	private int ConquerorBuffId = 0;
	private int killCountAsConquerer = 0;
	private int killCountAsProtector = 0;
	
	/**
	 * Checks if the player currently has the protector status.<br>
	 * This method returns the value of the {@code isProtector} field.
	 * @return {@code true} if the player is a protector, {@code false} otherwise.
	 */
	public boolean isProtector()
	{
		return isProtector;
	}
	
	/**
	 * Updates the protector status of the player.<br>
	 * This method sets the {@code isProtector} field to the provided value.
	 * @param isProtector The new status to set. Use {@code true} for a protector or {@code false} otherwise.
	 */
	private void setProtector(boolean isProtector)
	{
		this.isProtector = isProtector;
	}
	
	/**
	 * Retrieves the current level of the protector buff.<br>
	 * This value corresponds to the {@code ProtectorBuffId}.
	 * @return The integer level of the protector buff.
	 */
	public int getProtectorBuffLevel()
	{
		return ProtectorBuffId;
	}
	
	/**
	 * Sets the unique identifier for the protector buff.<br>
	 * This value is used to identify which specific buff is applied to the player.
	 * @param protectorBuffId The {@code int} ID of the protector buff.
	 */
	public void setProtectorBuffId(int protectorBuffId)
	{
		ProtectorBuffId = protectorBuffId;
	}
	
	/**
	 * Retrieves the current level of the conqueror buff.<br>
	 * This value is stored in the {@code ConquerorBuffId} field.
	 * @return The integer level of the conqueror buff.
	 */
	public int getConquerorBuffLevel()
	{
		return ConquerorBuffId;
	}
	
	/**
	 * Sets the unique identifier for the Conqueror buff.<br>
	 * This value is used to track specific buff effects for a player.
	 * @param conquerorBuffId The {@code int} ID of the Conqueror buff.
	 */
	public void setConquerorBuffId(int conquerorBuffId)
	{
		ConquerorBuffId = conquerorBuffId;
	}
	
	/**
	 * Retrieves the total number of kills made while in the conquerer role.<br>
	 * This value is stored in the {@code killCountAsConquerer} field.
	 * @return The current count of kills as a conquerer.
	 */
	public int getKillCountAsConquerer()
	{
		return killCountAsConquerer;
	}
	
	/**
	 * Updates the number of kills achieved while in the conquerer role.<br>
	 * This method also sets {@code isProtector} to {@code false}.
	 * @param killCountAsConquerer The new count of kills for the conquerer.
	 */
	public void setKillCountAsConquerer(int killCountAsConquerer)
	{
		setProtector(false);
		this.killCountAsConquerer = killCountAsConquerer;
	}
	
	/**
	 * Retrieves the total number of kills made while in protector mode.<br>
	 * This value is stored in the {@code killCountAsProtector} field.
	 * @return The current count of kills as a protector.
	 */
	public int getKillCountAsProtector()
	{
		return killCountAsProtector;
	}
	
	/**
	 * Sets the number of kills achieved while in protector mode.<br>
	 * This method also sets {@code isProtector} to {@code true}.
	 * @param killCountAsProtector The new count of kills as a protector.
	 */
	public void setKillCountAsProtector(int killCountAsProtector)
	{
		setProtector(true);
		this.killCountAsProtector = killCountAsProtector;
	}
	
}
