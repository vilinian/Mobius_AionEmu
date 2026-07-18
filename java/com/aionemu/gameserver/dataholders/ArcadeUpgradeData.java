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

import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTabItem;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the configuration data for arcade upgrades.<br>
 * It serves as a data container for {@link ArcadeTab} information loaded from XML files.
 * @author Raziel
 */
@XmlRootElement(name = "arcadelist")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArcadeUpgradeData
{
	@XmlElement(name = "tab")
	private List<ArcadeTab> arcadeTabTemplate;
	private final TIntObjectHashMap<List<ArcadeTabItem>> arcadeItemList = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code arcadeItemList} map using the list of {@link ArcadeTab} templates.<br>
	 * The {@code arcadeItemList} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		arcadeItemList.clear();
		for (ArcadeTab template : arcadeTabTemplate)
		{
			arcadeItemList.put(template.getId(), template.getArcadeTabItems());
		}
	}
	
	/**
	 * Returns the number of items in the arcade list.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of arcade items currently stored.
	 */
	public int size()
	{
		return arcadeItemList.size();
	}
	
	/**
	 * Retrieves a list of items belonging to a specific arcade tab.<br>
	 * This method looks up the data using the provided {@code id}.
	 * @param id The unique identifier for the arcade tab.
	 * @return A {@code List} of {@link ArcadeTabItem} objects, or {@code null} if not found.
	 */
	public List<ArcadeTabItem> getArcadeTabById(int id)
	{
		return arcadeItemList.get(id);
	}
	
	/**
	 * Retrieves the list of all arcade tabs.<br>
	 * This method returns the {@code arcadeTabTemplate} collection.
	 * @return a {@code List} of {@link ArcadeTab} objects.
	 */
	public List<ArcadeTab> getArcadeTabs()
	{
		return arcadeTabTemplate;
	}
}
