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
package com.aionemu.gameserver.dataholders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.shield.ShieldTemplate;

/**
 * This class serves as a data holder for {@link ShieldTemplate} objects.<br>
 * It is used to manage and store shield information within the game server.<br>
 * It maps XML data to internal collections of shields.
 * @author Wakizashi
 */
@XmlRootElement(name = "shields")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShieldData
{
	@XmlElement(name = "shield")
	private List<ShieldTemplate> shieldTemplates;
	
	/**
	 * Returns the number of shield templates in this collection.<br>
	 * If the list is null, it initializes a new {@code ArrayList} and returns {@code 0}.
	 * @return The total count of {@link ShieldTemplate} objects.
	 */
	public int size()
	{
		if (shieldTemplates == null)
		{
			shieldTemplates = new ArrayList<>();
			return 0;
		}
		
		return shieldTemplates.size();
	}
	
	/**
	 * Retrieves the list of all available shield templates.<br>
	 * This method returns an empty {@code ArrayList} if no templates exist.
	 * @return A {@code List} of {@link ShieldTemplate} objects.
	 */
	public List<ShieldTemplate> getShieldTemplates()
	{
		if (shieldTemplates == null)
		{
			return new ArrayList<>();
		}
		
		return shieldTemplates;
	}
	
	/**
	 * Adds all templates from a collection to the current list.<br>
	 * This method initializes the internal list if it is {@code null}.
	 * @param templates The collection of {@link ShieldTemplate} objects to add.
	 */
	public void addAll(Collection<ShieldTemplate> templates)
	{
		if (shieldTemplates == null)
		{
			shieldTemplates = new ArrayList<>();
		}
		
		shieldTemplates.addAll(templates);
	}
}
