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
 * This class represents the attributes for a player's bonus service.<br>
 * It holds configuration data used by {@code PlayersBonusService}.
 * @author Ace on 31/07/2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlayersBonusServiceAttr", propOrder =
{
	"playersBonusAttr"
})
public class PlayersBonusServiceAttr
{
	@XmlElement(name = "apply_bonus")
	protected List<PlayersBonusPenaltyAttr> playersBonusAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	/**
	 * Retrieves the list of penalty attributes for the player bonus.<br>
	 * This method ensures that a non-null {@code List} is always returned.
	 * @return A {@code List} of {@link PlayersBonusPenaltyAttr} objects.
	 */
	public List<PlayersBonusPenaltyAttr> getPenaltyAttr()
	{
		if (playersBonusAttr == null)
		{
			playersBonusAttr = new ArrayList<>();
		}
		
		return playersBonusAttr;
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
