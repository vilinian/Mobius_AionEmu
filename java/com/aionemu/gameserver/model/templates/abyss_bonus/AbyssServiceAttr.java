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
package com.aionemu.gameserver.model.templates.abyss_bonus;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * Represents the attributes and properties associated with an Abyss Service.<br>
 * This class defines how specific bonuses are applied to characters within the Abyss system.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbyssServiceAttr", propOrder =
{
	"bonusAttr"
})
public class AbyssServiceAttr
{
	@XmlElement(name = "bonus_attr")
	protected List<AbyssPenaltyAttr> bonusAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "race", required = true)
	private Race race;
	
	/**
	 * Retrieves the list of penalty attributes for this service.<br>
	 * If the list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@link AbyssPenaltyAttr} objects.
	 */
	public List<AbyssPenaltyAttr> getPenaltyAttr()
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
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
}
