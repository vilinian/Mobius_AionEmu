package com.aionemu.gameserver.model.skinskill;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillSkinTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a visual skin applied to a specific skill used by a {@link Player}.<br>
 * This class manages the properties and expiration of the skin based on its {@link SkillSkinTemplate}.
 * @author Ghostfur (Aion-Unique)
 */
public class SkillSkin implements IExpirable
{
	private final SkillSkinTemplate template;
	private final int id;
	private int dispearTime = 0;
	private final int isActive;
	
	/**
	 * Creates a new instance of {@link SkillSkin}.<br>
	 * This constructor initializes the skin with its template and status.
	 * @param template The {@code SkillSkinTemplate} used for this skin.
	 * @param id The unique identifier for the skill skin.
	 * @param dispearTime The time in seconds until the skin disappears.
	 * @param isActive The current active state of the skin, represented as an integer.
	 */
	public SkillSkin(SkillSkinTemplate template, int id, int dispearTime, int isActive)
	{
		this.template = template;
		this.id = id;
		this.dispearTime = dispearTime;
		this.isActive = isActive;
	}
	
	/**
	 * Retrieves the {@link SkillSkinTemplate} associated with this skill skin.<br>
	 * This method returns the base configuration for the skin.
	 * @return The {@code SkillSkinTemplate} object.
	 */
	public SkillSkinTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Calculates the time left before the skill skin expires.<br>
	 * It subtracts the current Unix timestamp from the {@code dispearTime}.<br>
	 * If {@code dispearTime} is 0, it returns 0.
	 * @return The remaining time in seconds as an {@code int}.
	 */
	public int getRemainingTime()
	{
		if (dispearTime == 0)
		{
			return 0;
		}
		
		return dispearTime - (int) (System.currentTimeMillis() / 1000L);
	}
	
	/**
	 * Checks if the skill skin is currently active.<br>
	 * Returns the status of the {@code isActive} field.
	 * @return The integer value representing the active state.
	 */
	public int getIsActive()
	{
		return isActive;
	}
	
	/**
	 * Retrieves the expiration time of this object.<br>
	 * This value represents when the object will expire.
	 * @return The expiration time as an {@code int}.
	 */
	@Override
	public int getExpireTime()
	{
		return dispearTime;
	}
	
	/**
	 * Removes the expired skill skin from the player's list.<br>
	 * This method updates the {@link Player} object by removing the specific skin.
	 * @param player The {@link Player} who owns the skill skin.
	 */
	@Override
	public void expireEnd(Player player)
	{
		player.getSkillSkinList().removeSkillSkin(id);
	}
	
	/**
	 * Sends a notification message to the player.<br>
	 * This method informs the {@link Player} that the skill animation has ended.<br>
	 * It displays a specific message in bright yellow text.
	 * @param player The {@code Player} who will receive the message.
	 * @param time The remaining time value, which is currently unused.
	 */
	@Override
	public void expireMessage(Player player, int time)
	{
		PacketSendUtility.sendBrightYellowMessageOnCenter(player, "Skill Animation Expired"); // For testing should be removed later if all works 100%
	}
	
	/**
	 * Checks if the chair object is allowed to expire at this moment.<br>
	 * This method currently always returns {@code true}.
	 * @return {@code true} if the object can expire now, otherwise {@code false}.
	 */
	@Override
	public boolean canExpireNow()
	{
		return true;
	}
}
