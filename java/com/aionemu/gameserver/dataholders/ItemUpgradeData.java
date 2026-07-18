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

import com.aionemu.gameserver.model.templates.item.upgrade.ItemUpgradeTemplate;
import com.aionemu.gameserver.model.templates.item.upgrade.UpgradeResultItem;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for item upgrade information.<br>
 * It stores and manages {@link ItemUpgradeTemplate} objects loaded from the configuration files.
 * @author Ranastic
 * @rework Navyan
 */
@XmlRootElement(name = "item_upgradess")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemUpgradeData
{
	@XmlElement(name = "item_upgrade")
	protected List<ItemUpgradeTemplate> ItemUpgradeTemplates;
	private TIntObjectHashMap<ItemUpgradeTemplate> itemUpgradeSets;
	@XmlTransient
	private Map<Integer, Map<Integer, UpgradeResultItem>> ResultItemMap;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code itemUpgradeSets} and {@code ResultItemMap} maps from the {@link ItemUpgradeTemplate} list.<br>
	 * The {@code ItemUpgradeTemplates} list is set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		itemUpgradeSets = new TIntObjectHashMap<>();
		ResultItemMap = new HashMap<>();
		
		for (ItemUpgradeTemplate set : ItemUpgradeTemplates)
		{
			itemUpgradeSets.put(set.getUpgrade_base_item_id(), set);
			
			ResultItemMap.put(set.getUpgrade_base_item_id(), new HashMap<>());
			
			if (!set.getUpgrade_result_item().isEmpty())
			{
				for (UpgradeResultItem resultItem : set.getUpgrade_result_item())
				{
					ResultItemMap.get(set.getUpgrade_base_item_id()).put(resultItem.getItem_id(), resultItem);
				}
			}
		}
		
		ItemUpgradeTemplates = null;
	}
	
	/**
	 * Retrieves a specific upgrade template from the data set.<br>
	 * This method uses the provided unique identifier to find the correct template.
	 * @param itemSetId The unique ID of the item set to look up.
	 * @return The {@link ItemUpgradeTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public ItemUpgradeTemplate getItemUpgradeTemplate(int itemSetId)
	{
		return itemUpgradeSets.get(itemSetId);
	}
	
	/**
	 * Retrieves the map of upgrade results for a specific base item.<br>
	 * This method looks up the {@code baseItemId} in the internal result map.<br>
	 * It returns the associated {@link Map} if it exists and is not empty.
	 * @param baseItemId The unique identifier of the base item to look up.
	 * @return A {@link Map} containing upgrade results, or {@code null} if no data is found.
	 */
	public Map<Integer, UpgradeResultItem> getResultItemMap(int baseItemId)
	{
		if (ResultItemMap.containsKey(baseItemId))
		{
			if (!ResultItemMap.get(baseItemId).isEmpty())
			{
				return ResultItemMap.get(baseItemId);
			}
			
			return null;
		}
		
		return null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return itemUpgradeSets.size();
	}
}
