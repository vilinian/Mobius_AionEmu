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
package com.aionemu.gameserver.model.templates.luna;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents the template data for a {@code Luna} bonus attribute.<br>
 * This class stores configuration details used to define specific bonuses within the game.<br>
 * It is mapped to XML data for easy modification of game balance.
 */
@XmlType(name = "luna_bonusattr")
@XmlAccessorType(XmlAccessType.NONE)
public class LunaBonusTemplate
{
	@XmlElement(name = "bonus_attr")
	protected List<LunaBonusAttr> bonusAttr;
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	/**
	 * Retrieves the list of penalty attributes for this template.<br>
	 * This method ensures that a non-null {@code List} is always returned.<br>
	 * If no attributes exist, it returns an empty {@code ArrayList}.
	 * @return A {@code List} of {@link LunaBonusAttr} objects.
	 */
	public List<LunaBonusAttr> getPenaltyAttr()
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
}
