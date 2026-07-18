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
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.minion.MinionTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for all {@link MinionTemplate} objects.<br>
 * It manages the collection of minion data loaded from configuration files.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "minions")
public class MinionData
{
	@XmlElement(name = "minion")
	private List<MinionTemplate> minionTemplates;
	@XmlTransient
	private final TIntObjectHashMap<MinionTemplate> minionData = new TIntObjectHashMap<>();
	@XmlTransient
	private final List<Integer> minionDataList = new ArrayList<>();
	
	/**
	 * This method is called after the object is unmarshalled from XML.<br>
	 * It populates the {@code minionData} map and {@code minionDataList} using data from {@code minionTemplates}.<br>
	 * Finally, it clears and nullifies the original list to save memory.
	 * @param unmarshaller The {@link Unmarshaller} used to read the XML.
	 * @param o The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object o)
	{
		for (MinionTemplate minion : minionTemplates)
		{
			minionData.put(minion.getId(), minion);
			minionDataList.add(minion.getId());
		}
		
		minionTemplates.clear();
		minionTemplates = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return minionData.size();
	}
	
	/**
	 * Retrieves a specific {@link MinionTemplate} based on its unique identifier.<br>
	 * This method looks up the data in the internal map.
	 * @param Id The unique integer ID of the minion to find.
	 * @return The {@code MinionTemplate} associated with the provided {@code Id}, or {@code null} if not found.
	 */
	public MinionTemplate getMinionTemplate(int Id)
	{
		return minionData.get(Id);
	}
	
	/**
	 * Retrieves all available minion IDs.<br>
	 * This method returns the internal list of {@code Integer} values.
	 * @return a {@code List<Integer>} containing all minion IDs.
	 */
	public List<Integer> getAll()
	{
		return minionDataList;
	}
}
