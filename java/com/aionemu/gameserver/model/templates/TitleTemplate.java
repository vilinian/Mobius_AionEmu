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
package com.aionemu.gameserver.model.templates;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

/**
 * Represents a template for character titles in the game.<br>
 * This class defines the properties and {@link com.aionemu.gameserver.model.stats.calc.functions.StatFunction} modifiers associated with a specific title.<br>
 * It serves as a data model for loading title information from XML configuration files.
 * @author xavier
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "title_templates")
public class TitleTemplate implements StatOwner
{
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;
	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;
	@XmlAttribute(name = "race", required = true)
	private Race race;
	private int titleId;
	@XmlAttribute(name = "nameId")
	private int nameId;
	@XmlAttribute(name = "desc")
	private String description;
	
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
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the descriptive text for this title.<br>
	 * This corresponds to the {@code desc} attribute in the XML configuration.
	 * @return The description string or {@code null} if no description is provided.
	 */
	public String getDesc()
	{
		return description;
	}
	
	/**
	 * Retrieves the list of stat modifiers for this {@link TitleTemplate}.<br>
	 * This method returns all active effects applied to the title.
	 * @return a {@code List} of {@link StatFunction} objects or {@code null} if no modifiers exist.
	 */
	public List<StatFunction> getModifiers()
	{
		if (modifiers != null)
		{
			return modifiers.getModifiers();
		}
		
		return null;
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It converts the {@code id} string into an integer for the {@code titleId} field.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		titleId = Integer.parseInt(id);
	}
}
