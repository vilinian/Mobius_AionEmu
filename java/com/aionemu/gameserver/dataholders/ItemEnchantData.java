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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.item.ItemEnchantTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link ItemEnchantTemplate} objects.<br>
 * It manages the collection of enchantment templates loaded from the game configuration.<br>
 * Use this class to access and retrieve specific enchantment data throughout the server.
 * @author Alcapwnd
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "enchant_templates")
public class ItemEnchantData
{
	@XmlElement(name = "enchant_template", required = true)
	protected List<ItemEnchantTemplate> enchantTemplates;
	
	@SuppressWarnings(
	{
		"rawtypes",
		"unchecked"
	})
	@XmlTransient
	private final TIntObjectHashMap<ItemEnchantTemplate> authorizes = new TIntObjectHashMap();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code enchantMap} using the list of {@link ItemEnchantTemplate} templates.<br>
	 * The {@code enchantMap} is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ItemEnchantTemplate it : enchantTemplates)
		{
			getEnchantMap().put(it.getId(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of enchantment templates.<br>
	 * This method converts the {@code enchantTemplates} list into a {@code TIntObjectHashMap}.<br>
	 * It is used to provide faster lookups by ID.
	 * @return A {@code TIntObjectHashMap} containing all {@link ItemEnchantTemplate} objects.
	 */
	private TIntObjectHashMap<ItemEnchantTemplate> getEnchantMap()
	{
		return authorizes;
	}
	
	/**
	 * Retrieves a specific enchantment template from the data map.<br>
	 * This method uses the provided unique identifier to find the correct object.
	 * @param id The unique integer ID of the enchantment template.
	 * @return The {@link ItemEnchantTemplate} associated with the given {@code id}.
	 */
	public ItemEnchantTemplate getEnchantTemplate(int id)
	{
		return authorizes.get(id);
	}
	
	/**
	 * Returns the total number of enchant templates.<br>
	 * This count represents all items currently stored in the list.
	 * @return The size of the {@code enchantTemplates} collection.
	 */
	public int size()
	{
		return authorizes.size();
	}
}
