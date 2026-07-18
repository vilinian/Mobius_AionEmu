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
package com.aionemu.gameserver.model.templates.bonus_service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the attributes associated with a {@code BonusService}.<br>
 * This class holds the configuration data for specific bonus service properties.
 * @author Ace on 31/07/2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BonusServiceAttr", propOrder =
{
	"bonusAttr"
})
public class BonusServiceAttr
{
	@XmlElement(name = "bonus_attr")
	protected List<BonusPenaltyAttr> bonusAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	/**
	 * Retrieves the list of penalty attributes associated with this service.<br>
	 * If no attributes exist, it returns an empty {@code List}.
	 * @return a {@code List} of {@link BonusPenaltyAttr} objects.
	 */
	public List<BonusPenaltyAttr> getPenaltyAttr()
	{
		if (bonusAttr == null)
		{
			bonusAttr = new ArrayList<>();
		}
		
		return bonusAttr;
	}
	
	/**
	 * Retrieves the unique buffer identifier.<br>
	 * This value is stored in the {@code buffId} field.
	 * @return The integer ID of the buff.
	 */
	public int getBuffId()
	{
		return buffId;
	}
	
	/**
	 * Sets the unique identifier for the buff.<br>
	 * This updates the {@code buffId} field in this object.
	 * @param value The new integer ID to assign to the buff.
	 */
	public void setBuffId(int value)
	{
		buffId = value;
	}
}
