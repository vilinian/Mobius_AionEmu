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
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;

import java.util.HashMap;

/**
 * This class serves as a data holder for all {@link CosmeticItemTemplate} objects.<br>
 * It handles the deserialization of cosmetic item data from XML files into memory.<br>
 * Use this class to access the global registry of available cosmetics.
 * @author xTz
 */
@XmlRootElement(name = "cosmetic_items")
@XmlAccessorType(XmlAccessType.FIELD)
public class CosmeticItemsData
{
	@XmlElement(name = "cosmetic_item", type = CosmeticItemTemplate.class)
	private List<CosmeticItemTemplate> templates;
	@XmlTransient
	private final Map<String, CosmeticItemTemplate> cosmeticItemTemplates = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code cosmeticItemTemplates} map using the list of {@link CosmeticItemTemplate} templates.<br>
	 * The {@code templates} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (CosmeticItemTemplate template : templates)
		{
			cosmeticItemTemplates.put(template.getCosmeticName(), template);
		}
		
		templates.clear();
		templates = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return cosmeticItemTemplates.size();
	}
	
	/**
	 * Retrieves a specific {@link CosmeticItemTemplate} from the data map.<br>
	 * This method uses the provided unique identifier to find the item.
	 * @param str The unique name or ID of the cosmetic item.
	 * @return The corresponding {@code CosmeticItemTemplate} object, or {@code null} if not found.
	 */
	public CosmeticItemTemplate getCosmeticItemsTemplate(String str)
	{
		return cosmeticItemTemplates.get(str);
	}
}
