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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.decomposable.DecomposableSelectItem;
import com.aionemu.gameserver.model.templates.decomposable.SelectItem;
import com.aionemu.gameserver.model.templates.decomposable.SelectItems;

/**
 * This class holds the data for {@link SelectItems} that can be decomposed.<br>
 * It serves as a data container for mapping {@code DecomposableSelectItem} templates.<br>
 * It is used to manage items that provide specific rewards upon being broken down.
 * @author Alcapwnd
 */
@XmlRootElement(name = "decomposable_selectitems")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecomposableSelectItemsData
{
	@XmlElement(name = "decomposable_selectitem", required = true)
	protected List<DecomposableSelectItem> selectItems;
	
	@XmlTransient
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private final HashMap<Integer, HashMap<PlayerClass, SelectItems>> selectItemData = new HashMap();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * The original list is cleared to save memory.
	 * @param u The {@link Unmarshaller} used to load the data.
	 * @param parent The parent object of this data structure.
	 */
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (Iterator i$ = selectItems.iterator(); i$.hasNext();)
		{
			DecomposableSelectItem item;
			item = (DecomposableSelectItem) i$.next();
			if (item.getItems() != null)
			{
				if (!selectItemData.containsKey(Integer.valueOf(item.getItemId())))
				{
					selectItemData.put(Integer.valueOf(item.getItemId()), new HashMap());
				}
				
				for (SelectItems its : item.getItems())
				{
					selectItemData.get(Integer.valueOf(item.getItemId())).put(its.getPlayerClass(), its);
				}
			}
		}
		
		selectItems.clear();
		selectItems = null;
	}
	
	/**
	 * Retrieves a list of selectable items based on the player's class and race.<br>
	 * This method filters the data by {@code itemid}.<br>
	 * It returns {@code null} if no matching data is found.
	 * @param playerClass The {@link PlayerClass} of the character.
	 * @param race The {@link Race} of the character.
	 * @param itemid The unique identifier for the item.
	 * @return A {@link SelectItems} object containing the filtered results.
	 */
	public SelectItems getSelectItem(PlayerClass playerClass, Race race, int itemid)
	{
		if (selectItemData.containsKey(Integer.valueOf(itemid)))
		{
			if (selectItemData.get(Integer.valueOf(itemid)).containsKey(playerClass))
			{
				final SelectItems filtered = new SelectItems();
				for (SelectItem si : selectItemData.get(itemid).get(playerClass).getItems())
				{
					if ((si.getRace() == Race.PC_ALL) || (si.getRace() == race))
					{
						filtered.addItem(si);
					}
				}
				
				return filtered;
			}
			
			if (selectItemData.get(Integer.valueOf(itemid)).containsKey(PlayerClass.ALL))
			{
				final SelectItems filtered = new SelectItems();
				for (SelectItem si : selectItemData.get(itemid).get(PlayerClass.ALL).getItems())
				{
					if ((si.getRace() == Race.PC_ALL) || (si.getRace() == race))
					{
						filtered.addItem(si);
					}
				}
				
				return filtered;
			}
			
			return null;
		}
		
		return null;
	}
	
	/**
	 * Returns the total number of decomposable items.<br>
	 * This method delegates to the internal {@code selectItemData} collection.
	 * @return The count of items in the data set.
	 */
	public int size()
	{
		return selectItemData.size();
	}
	
}
