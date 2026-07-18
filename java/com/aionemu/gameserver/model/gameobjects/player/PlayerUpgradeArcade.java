/**
 * 
 */
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * This class manages the data and logic for a player's upgrade arcade.<br>
 * It handles specific progression mechanics related to arcade-style upgrades.
 * @author CoolyT
 */
public class PlayerUpgradeArcade
{
	private int frenzyPoints = 0;
	private int frenzyCount = 0;
	private int frenzyLevel = 1;
	private int failedLevel = 1;
	private boolean isFrenzy = false;
	private boolean reTry = false;
	private boolean failed = false;
	
	/**
	 * Retrieves the current number of {@code frenzyPoints}.<br>
	 * This value represents the player's progress in the arcade.
	 * @return The current amount of {@code frenzyPoints}.
	 */
	public int getFrenzyPoints()
	{
		return frenzyPoints;
	}
	
	/**
	 * Updates the current amount of {@code frenzyPoints}.<br>
	 * This method sets the value for the player's frenzy points.
	 * @param frenzyPoints The new number of points to set.
	 */
	public void setFrenzyPoints(int frenzyPoints)
	{
		this.frenzyPoints = frenzyPoints;
	}
	
	/**
	 * Retrieves the current number of frenzy counts.<br>
	 * This value tracks how many times the frenzy has been activated.
	 * @return The current {@code int} value of {@code frenzyCount}.
	 */
	public int getFrenzyCount()
	{
		return frenzyCount;
	}
	
	/**
	 * Updates the current {@code frenzyCount} for the player.<br>
	 * This value tracks how many times a frenzy action has occurred.
	 * @param frenzyCount The new integer value to set for the count.
	 */
	public void setFrenzyCount(int frenzyCount)
	{
		this.frenzyCount = frenzyCount;
	}
	
	/**
	 * Retrieves the current level of the player's frenzy.<br>
	 * This value is used to track progress in the arcade mode.
	 * @return The current {@code int} value of the frenzy level.
	 */
	public int getFrenzyLevel()
	{
		return frenzyLevel;
	}
	
	/**
	 * Updates the current level of the player's frenzy.<br>
	 * This method sets the {@code frenzyLevel} field to a new value.
	 * @param frenzyLevel The new integer level to assign.
	 */
	public void setFrenzyLevel(int frenzyLevel)
	{
		this.frenzyLevel = frenzyLevel;
	}
	
	/**
	 * Retrieves the current level at which the player failed.<br>
	 * This value is used to track progress in the {@link PlayerUpgradeArcade}.
	 * @return The integer value of the {@code failedLevel}.
	 */
	public int getFailedLevel()
	{
		return failedLevel;
	}
	
	/**
	 * Updates the current level at which the player failed.<br>
	 * This value is used to track progress in the {@link PlayerUpgradeArcade}.
	 * @param failedLevel The new integer value for the failed level.
	 */
	public void setFailedLevel(int failedLevel)
	{
		this.failedLevel = failedLevel;
	}
	
	/**
	 * Checks if the player is currently in a frenzy state.<br>
	 * This method returns the current value of the {@code isFrenzy} flag.
	 * @return {@code true} if the player is in frenzy, {@code false} otherwise.
	 */
	public boolean isFrenzy()
	{
		return isFrenzy;
	}
	
	/**
	 * Updates the frenzy status of the player.<br>
	 * This method sets the {@code isFrenzy} flag to either {@code true} or {@code false}.
	 * @param isFrenzy The new frenzy state to apply.
	 */
	public void setFrenzy(boolean isFrenzy)
	{
		this.isFrenzy = isFrenzy;
	}
	
	/**
	 * Checks if the player is currently in a retry state.<br>
	 * This method returns the value of the {@code reTry} field.
	 * @return {@code true} if the player is retrying, {@code false} otherwise.
	 */
	public boolean isReTry()
	{
		return reTry;
	}
	
	/**
	 * Sets whether the player can attempt the arcade again.<br>
	 * This updates the {@code reTry} status of the current session.
	 * @param reTry The boolean value to set for retry status.
	 */
	public void setReTry(boolean reTry)
	{
		this.reTry = reTry;
	}
	
	/**
	 * Checks if the arcade upgrade has failed.<br>
	 * This method returns {@code true} if the {@code failed} flag is set to {@code true}.
	 * @return {@code true} if the upgrade failed, otherwise {@code false}.
	 */
	public boolean isFailed()
	{
		return failed;
	}
	
	/**
	 * Updates the failure status of the player upgrade.<br>
	 * Sets the {@code failed} field to the provided value.
	 * @param failed The new failure status to set.
	 */
	public void setFailed(boolean failed)
	{
		this.failed = failed;
	}
	
	/**
	 * Resets the frenzy state to its default values.<br>
	 * This method sets {@code isFrenzy}, {@code failed}, {@code reTry} to {@code false}.<br>
	 * It also resets {@code frenzyLevel} and {@code failedLevel} to {@code 1}.
	 */
	public void reset()
	{
		isFrenzy = false;
		failed = false;
		frenzyLevel = 1;
		failedLevel = 1;
		reTry = false;
	}
	
}
