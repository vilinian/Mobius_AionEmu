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
package com.aionemu.gameserver.model.templates.item.bonuses;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents a random bonus that can be applied to an item.<br>
 * It contains a list of {@link ModifiersTemplate} that are selected randomly when the item is generated.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RandomBonus", propOrder =
{
	"modifiers"
})
public class RandomBonus
{
	@XmlElement(required = true)
	protected List<ModifiersTemplate> modifiers;
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "type", required = true)
	private StatBonusType bonusType;
	
	/**
	 * Retrieves the list of {@link ModifiersTemplate} associated with this bonus.<br>
	 * If the list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@code ModifiersTemplate} objects.
	 */
	public List<ModifiersTemplate> getModifiers()
	{
		if (modifiers == null)
		{
			modifiers = new ArrayList<>();
		}
		
		return modifiers;
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
	 * Retrieves the type of the random bonus.<br>
	 * This method returns the {@code StatBonusType} associated with this object.
	 * @return The {@code StatBonusType} of the bonus.
	 */
	public StatBonusType getBonusType()
	{
		return bonusType;
	}
}
