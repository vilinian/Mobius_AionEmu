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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.cubics.CubicsTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link CubicsTemplate} objects.<br>
 * It manages the collection of cubic templates loaded from XML configuration files.<br>
 * It provides quick access to template data using a {@code TIntObjectHashMap}.
 * @author Phantom_KNA
 */
@XmlRootElement(name = "cubics_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class CubicsData
{
	@XmlElement(name = "cubics_template")
	private List<CubicsTemplate> tlist;
	private final TIntObjectHashMap<CubicsTemplate> cubicsData;
	@XmlTransient
	private final Map<Integer, CubicsTemplate> cubicsDataMap;
	
	/**
	 * Creates a new instance of the {@code CubicsData} class.<br>
	 * This constructor initializes the internal data maps.
	 */
	public CubicsData()
	{
		cubicsData = new TIntObjectHashMap<>();
		cubicsDataMap = new HashMap<>(1);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code cubicsData} and {@code cubicsDataMap} using the list of {@link CubicsTemplate} objects.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (CubicsTemplate id : tlist)
		{
			cubicsData.put(id.getId(), id);
			cubicsDataMap.put(id.getId(), id);
		}
	}
	
	/**
	 * Retrieves a {@link CubicsTemplate} based on its unique identifier.<br>
	 * This method looks up the data in the internal map.
	 * @param id The unique integer ID of the cubic template.
	 * @return The corresponding {@code CubicsTemplate} object, or {@code null} if not found.
	 */
	public CubicsTemplate getCubicsId(int id)
	{
		return cubicsData.get(id);
	}
	
	/**
	 * Retrieves all available cubic templates.<br>
	 * This method returns the internal map of data.
	 * @return a {@code Map} containing all {@link CubicsTemplate} objects indexed by their ID.
	 */
	public Map<Integer, CubicsTemplate> getAll()
	{
		return cubicsDataMap;
	}
	
	/**
	 * Returns the total number of elements in this collection.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the data map.
	 */
	public int size()
	{
		return cubicsData.size();
	}
}
