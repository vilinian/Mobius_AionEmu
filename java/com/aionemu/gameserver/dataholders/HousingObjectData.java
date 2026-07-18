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

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.housing.HousingChair;
import com.aionemu.gameserver.model.templates.housing.HousingEmblem;
import com.aionemu.gameserver.model.templates.housing.HousingJukeBox;
import com.aionemu.gameserver.model.templates.housing.HousingMoveableItem;
import com.aionemu.gameserver.model.templates.housing.HousingMovieJukeBox;
import com.aionemu.gameserver.model.templates.housing.HousingNpc;
import com.aionemu.gameserver.model.templates.housing.HousingPassiveItem;
import com.aionemu.gameserver.model.templates.housing.HousingPicture;
import com.aionemu.gameserver.model.templates.housing.HousingPostbox;
import com.aionemu.gameserver.model.templates.housing.HousingStorage;
import com.aionemu.gameserver.model.templates.housing.HousingUseableItem;
import com.aionemu.gameserver.model.templates.housing.PlaceableHouseObject;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for objects placed within a player's house.<br>
 * It stores the necessary properties and state for various {@link PlaceableHouseObject} types.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	/**
	 * This class represents a collection of housing objects.<br>
	 * It maps XML elements to various types of house items.<br>
	 * These include furniture, NPCs, and interactive objects.
	 */
	"housingObjects"
})
@XmlRootElement(name = "housing_objects")
public class HousingObjectData
{
	@XmlElements(
	{
		@XmlElement(name = "postbox", type = HousingPostbox.class),
		@XmlElement(name = "use_item", type = HousingUseableItem.class),
		@XmlElement(name = "move_item", type = HousingMoveableItem.class),
		@XmlElement(name = "chair", type = HousingChair.class),
		@XmlElement(name = "picture", type = HousingPicture.class),
		@XmlElement(name = "passive", type = HousingPassiveItem.class),
		@XmlElement(name = "npc", type = HousingNpc.class),
		@XmlElement(name = "storage", type = HousingStorage.class),
		@XmlElement(name = "jukebox", type = HousingJukeBox.class),
		@XmlElement(name = "moviejukebox", type = HousingMovieJukeBox.class),
		@XmlElement(name = "emblem", type = HousingEmblem.class)
	})
	protected List<PlaceableHouseObject> housingObjects;
	@XmlTransient
	protected TIntObjectHashMap<PlaceableHouseObject> objectTemplatesById = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code objectTemplatesById} map using the list of {@link PlaceableHouseObject} objects.<br>
	 * The {@code housingObjects} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		if (housingObjects == null)
		{
			return;
		}
		
		for (PlaceableHouseObject obj : housingObjects)
		{
			objectTemplatesById.put(obj.getTemplateId(), obj);
		}
		
		housingObjects.clear();
		housingObjects = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return objectTemplatesById.size();
	}
	
	/**
	 * Retrieves a house object template based on its unique ID.<br>
	 * This method looks up the data in the internal map.
	 * @param templateId The unique identifier for the house object.
	 * @return The {@link PlaceableHouseObject} associated with the given ID, or {@code null} if not found.
	 */
	public PlaceableHouseObject getTemplateById(int templateId)
	{
		return objectTemplatesById.get(templateId);
	}
}
