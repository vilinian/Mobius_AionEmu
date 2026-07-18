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
package com.aionemu.gameserver.model.templates.instance_bonusatrr;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the attributes associated with an instance bonus.<br>
 * This class stores data for specific bonuses granted within a game instance.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceBonusAttr", propOrder =
{
	"penaltyAttr"
})
public class InstanceBonusAttr
{
	@XmlElement(name = "penalty_attr")
	protected List<InstancePenaltyAttr> penaltyAttr;
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	/**
	 * Retrieves the list of {@link InstancePenaltyAttr} objects.<br>
	 * This method returns a live reference to the internal list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} containing all penalty attributes.
	 */
	public List<InstancePenaltyAttr> getPenaltyAttr()
	{
		if (penaltyAttr == null)
		{
			penaltyAttr = new ArrayList<>();
		}
		
		return penaltyAttr;
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
