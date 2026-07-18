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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class defines the usage restrictions for specific items in the game.<br>
 * It manages conditions such as cooldowns and limitations based on {@link Gender} or other attributes.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UseLimits")
public class ItemUseLimits
{
	@XmlAttribute(name = "usedelay")
	private int useDelay;
	@XmlAttribute(name = "usedelayid")
	private int useDelayId;
	@XmlAttribute(name = "ownership_world")
	private int ownershipWorldId;
	@XmlAttribute
	private String usearea;
	@XmlAttribute(name = "gender")
	private Gender genderPermitted;
	@XmlAttribute(name = "ride_usable")
	private Boolean rideUsable;
	@XmlAttribute(name = "rank_min")
	private int minRank;
	@XmlAttribute(name = "rank_max")
	private int maxRank = AbyssRankEnum.SUPREME_COMMANDER.getId();
	@XmlAttribute(name = "guild_level")
	private int guildLevel;
	
	/**
	 * Retrieves the unique identifier for the item's use delay.<br>
	 * This value is used to determine specific cooldown behaviors.
	 * @return the {@code int} value of the {@code useDelayId}.
	 */
	public int getDelayId()
	{
		return useDelayId;
	}
	
	/**
	 * Sets the unique identifier for the item use delay.<br>
	 * This value is used to determine specific delay behaviors.
	 * @param delayId The {@code int} ID of the delay.
	 */
	public void setDelayId(int delayId)
	{
		useDelayId = delayId;
	}
	
	/**
	 * Retrieves the delay time for using this item.<br>
	 * This value is stored in the {@code useDelay} field.
	 * @return The current delay time as an {@code int}.
	 */
	public int getDelayTime()
	{
		return useDelay;
	}
	
	/**
	 * Sets the delay time for using an item.<br>
	 * This value is stored in the {@code useDelay} field.
	 * @param useDelay The amount of delay to apply.
	 */
	public void setDelayTime(int useDelay)
	{
		this.useDelay = useDelay;
	}
	
	/**
	 * Retrieves the area where this item can be used.<br>
	 * This method returns a {@link ZoneName} object.
	 * @return the {@code ZoneName} of the allowed use area.
	 */
	public ZoneName getUseArea()
	{
		if (usearea == null)
		{
			return null;
		}
		
		try
		{
			return ZoneName.get(usearea);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Retrieves the ownership world limit for this item.<br>
	 * This value determines which world owns the item limits.
	 * @return The integer representing the ownership world.
	 */
	public int getOwnershipWorld()
	{
		return ownershipWorldId;
	}
	
	/**
	 * Retrieves the allowed {@link Gender} for this quest.<br>
	 * This method returns the value stored in the {@code genderPermitted} field.
	 * @return The permitted {@code Gender} for the quest.
	 */
	public Gender getGenderPermitted()
	{
		return genderPermitted;
	}
	
	/**
	 * Checks if the item can be used for riding.<br>
	 * It returns {@code false} if the value is {@code null}.
	 * @return {@code true} if riding is allowed, otherwise {@code false}.
	 */
	public boolean isRideUsable()
	{
		if (rideUsable == null)
		{
			return false;
		}
		
		return rideUsable;
	}
	
	/**
	 * Retrieves the minimum rank required to use this item.<br>
	 * This value is used by {@code verifyRank} to check permissions.
	 * @return The minimum rank as an {@code int}.
	 */
	public int getMinRank()
	{
		return minRank;
	}
	
	/**
	 * Retrieves the highest rank achieved by the player.<br>
	 * This value is stored in the {@code maxRank} field.
	 * @return The maximum rank as an {@code int}.
	 */
	public int getMaxRank()
	{
		return maxRank;
	}
	
	/**
	 * Checks if a given rank is allowed for this item.<br>
	 * It compares the {@code rank} against the minimum and maximum limits.
	 * @param rank The rank value to check.
	 * @return {@code true} if the rank is within the valid range, otherwise {@code false}.
	 */
	public boolean verifyRank(int rank)
	{
		return (minRank <= rank) && (maxRank >= rank);
	}
	
	/**
	 * Retrieves the minimum guild level required to use this item.<br>
	 * This value is stored in the {@code guildLevel} field.
	 * @return The permitted guild level as an {@code int}.
	 */
	public int getGuildLevelPermitted()
	{
		return guildLevel;
	}
}
