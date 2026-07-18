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
package com.aionemu.gameserver.model.ai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a collection of summoned entities managed as a single unit.<br>
 * This class handles the grouping logic for multiple summons in the game world.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonGroup")
public class SummonGroup
{
	@XmlAttribute(name = "npcId")
	protected int npcId;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;
	@XmlAttribute(name = "count")
	protected int count;
	@XmlAttribute(name = "minCount")
	protected int minCount;
	@XmlAttribute(name = "maxCount")
	protected int maxCount;
	@XmlAttribute(name = "distance")
	protected float distance;
	@XmlAttribute(name = "schedule")
	protected int schedule;
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Retrieves the horizontal rotation value.<br>
	 * This value represents the orientation of the {@code SummonGroup}.
	 * @return The current {@code byte} value for {@code h}.
	 */
	public byte getH()
	{
		return h;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Retrieves the minimum allowed count for this summon group.<br>
	 * This value is used to limit how many NPCs can exist at once.
	 * @return The {@code int} value of the minimum count.
	 */
	public int getMinCount()
	{
		return minCount;
	}
	
	/**
	 * Retrieves the maximum number of NPCs allowed in this group.<br>
	 * This value is stored in the {@code maxCount} field.
	 * @return The maximum count as an {@code int}.
	 */
	public int getMaxCount()
	{
		return maxCount;
	}
	
	/**
	 * Returns the distance value of the collision.<br>
	 * This value is retrieved from the {@code distance} field.
	 * @return The distance as a {@code float}.
	 */
	public float getDistance()
	{
		return distance;
	}
	
	/**
	 * Retrieves the current schedule value.<br>
	 * This value is used to determine the timing of the group.
	 * @return The {@code int} value of the schedule.
	 */
	public int getSchedule()
	{
		return schedule;
	}
}
