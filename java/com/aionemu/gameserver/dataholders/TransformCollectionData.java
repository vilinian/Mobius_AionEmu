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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;

/**
 * This class serves as a data holder for {@link TransformCollectionTemplate} objects.<br>
 * It acts as a container to manage and access collections of transform templates from the game data.<br>
 * It is used primarily for deserializing XML configuration files into usable Java objects.
 */
@XmlRootElement(name = "transform_collection_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransformCollectionData
{
	@XmlElement(name = "transform_collection_template")
	private List<TransformCollectionTemplate> tlist;
	@XmlTransient
	private final Map<Integer, TransformCollectionTemplate> transCollectionData = new HashMap<>();
	
	/**
	 * This method is called after the XML data is unmarshalled.<br>
	 * It populates internal maps using the list of {@link TransformCollectionTemplate} objects.
	 * @param paramUnmarshaller The {@code Unmarshaller} used to read the data.
	 * @param paramObject The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject)
	{
		for (TransformCollectionTemplate book : tlist)
		{
			transCollectionData.put(book.getId(), book);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return transCollectionData.size();
	}
	
	/**
	 * Retrieves all collection templates from the data holder.<br>
	 * This method returns a {@code Map} where the key is the unique ID.
	 * @return A {@code Map} containing all {@link TransformCollectionTemplate} objects.
	 */
	public Map<Integer, TransformCollectionTemplate> getAllCollection()
	{
		return transCollectionData;
	}
	
	/**
	 * Retrieves a specific collection template using its unique identifier.<br>
	 * This method looks up the data in the internal map.
	 * @param id The unique integer ID of the collection to find.
	 * @return The {@link TransformCollectionTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public TransformCollectionTemplate getTransformCollectionById(int id)
	{
		return transCollectionData.get(id);
	}
}
