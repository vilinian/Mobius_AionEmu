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
package com.aionemu.gameserver.model.templates.portal;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.GSConfig;

/**
 * Represents the requirements needed to activate a portal.<br>
 * This class stores the data used to validate if a player can enter a specific location.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalReq")
public class PortalReq
{
	@XmlElement(name = "quest_req")
	protected List<QuestReq> questReq;
	@XmlElement(name = "item_req")
	protected List<ItemReq> itemReq;
	@XmlAttribute(name = "min_level")
	protected int minLevel;
	@XmlAttribute(name = "max_level")
	protected int maxLevel = GSConfig.PLAYER_MAX_LEVEL;
	@XmlAttribute(name = "kinah_req")
	protected int kinahReq;
	@XmlAttribute(name = "title_id")
	protected int titleId;
	@XmlAttribute(name = "err_level")
	protected int errLevel;
	@XmlAttribute(name = "legion_req")
	protected boolean legionReq;
	
	/**
	 * Retrieves the list of quest requirements for this portal.<br>
	 * This method returns all {@code QuestReq} objects associated with the portal.
	 * @return a {@code List} of {@link QuestReq} objects.
	 */
	public List<QuestReq> getQuestReq()
	{
		return questReq;
	}
	
	/**
	 * Retrieves the list of required items.<br>
	 * This method returns all {@code ItemReq} objects associated with this portal.
	 * @return a {@code List} of {@link ItemReq} objects.
	 */
	public List<ItemReq> getItemReq()
	{
		return itemReq;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * Sets the minimum level required to use this portal.<br>
	 * This updates the {@code minLevel} field.
	 * @param value The minimum level as an {@code int}.
	 */
	public void setMinLevel(int value)
	{
		minLevel = value;
	}
	
	/**
	 * Gets the maximum level allowed for this challenge task.<br>
	 * This value is retrieved from the {@code maxLevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * Sets the maximum level required for this portal.<br>
	 * This updates the {@code maxLevel} field.
	 * @param value The new maximum level to set.
	 */
	public void setMaxLevel(int value)
	{
		maxLevel = value;
	}
	
	/**
	 * Retrieves the amount of Kinah required for this portal.<br>
	 * This value is stored in the {@code kinahReq} field.
	 * @return The required Kinah amount as an {@code int}.
	 */
	public int getKinahReq()
	{
		return kinahReq;
	}
	
	/**
	 * Sets the amount of Kinah required for this portal.<br>
	 * This updates the {@code kinahReq} field.
	 * @param value The number of Kinah required.
	 */
	public void setKinahReq(int value)
	{
		kinahReq = value;
	}
	
	/**
	 * Retrieves the unique identifier for the title.<br>
	 * This value corresponds to the {@code title_id} attribute.
	 * @return The integer ID of the title.
	 */
	public int getTitleId()
	{
		return titleId;
	}
	
	/**
	 * Retrieves the error level associated with this portal requirement.<br>
	 * This value is used to determine the severity of a failure.
	 * @return The {@code int} value representing the error level.
	 */
	public int getErrLevel()
	{
		return errLevel;
	}
	
	/**
	 * Checks if a legion is required to use this portal.<br>
	 * This method returns the value of the {@code legionReq} attribute.
	 * @return {@code true} if a legion is required, {@code false} otherwise.
	 */
	public boolean getLegionReq()
	{
		return legionReq;
	}
}
