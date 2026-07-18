/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.cubics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.cubics.MCEntry;

/**
 * This class represents the data template for {@code Cubics} items in the game.<br>
 * It defines the base properties and attributes used to initialize cubic objects.
 * @author Phantom_KNA
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CubicsTemplate")
public class CubicsTemplate
{
	
	protected List<StatCoreList> core_list;
	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "category")
	private int category;
	@XmlAttribute(name = "maxRank")
	private int maxRank;
	@XmlAttribute(name = "quality")
	private QualityCoreType quality;
	@XmlAttribute(name = "itemId")
	private int itemId;
	
	/**
	 * Retrieves the list of core statistics for this template.<br>
	 * This method ensures that a non-null {@code List} is always returned.<br>
	 * If the internal list is empty, it returns an empty {@code ArrayList}.
	 * @return A {@code List} of {@link StatCoreList} objects.
	 */
	public List<StatCoreList> getStatLists()
	{
		if (core_list == null)
		{
			core_list = new ArrayList<>();
		}
		
		return core_list;
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
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the category of the {@link MCEntry}.<br>
	 * This value identifies which group the entry belongs to.
	 * @return The integer value representing the category.
	 */
	public int getCategory()
	{
		return category;
	}
	
	/**
	 * Retrieves the highest rank achieved by the player.<br>
	 * This value is stored in the {@code maxRank} field.
	 * @return The maximum rank as an {@code int}.
	 */
	public int getMaxRank()
	{
		return maxRank;
	}
	
	/**
	 * Retrieves the quality type of this cubic template.<br>
	 * This method returns the {@code QualityCoreType} associated with the item.
	 * @return The {@code QualityCoreType} value.
	 */
	public QualityCoreType getQuality()
	{
		return quality;
	}
	
	/**
	 * Retrieves the unique identifier for the cubic item.<br>
	 * This method returns the {@code itemId} value from the template.
	 * @return The integer ID of the cubic item.
	 */
	public int getItemIdCubic()
	{
		return itemId;
	}
}
