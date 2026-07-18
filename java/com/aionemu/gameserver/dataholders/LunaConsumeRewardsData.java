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

import com.aionemu.gameserver.model.templates.luna.LunaConsumeRewardsTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for rewards obtained from consuming {@link LunaConsumeRewardsTemplate} items.<br>
 * It serves as a data container to map specific reward types to their corresponding values.
 */
@XmlRootElement(name = "luna_consume_rewards")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaConsumeRewardsData
{
	@XmlElement(name = "luna_consume_reward")
	private List<LunaConsumeRewardsTemplate> lunaList;
	
	@XmlTransient
	private final TIntObjectHashMap<LunaConsumeRewardsTemplate> lunaData = new TIntObjectHashMap<>();
	
	@XmlTransient
	private final TIntObjectHashMap<LunaConsumeRewardsTemplate> lunaConsumeCountData = new TIntObjectHashMap<>();
	
	@XmlTransient
	private final Map<Integer, LunaConsumeRewardsTemplate> lunaDataMap = new HashMap<>(1);
	
	/**
	 * This method is called after the XML data is unmarshalled.<br>
	 * It populates internal maps using the list of {@link LunaConsumeRewardsTemplate} objects.
	 * @param paramUnmarshaller The {@code Unmarshaller} used to read the data.
	 * @param paramObject The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject)
	{
		for (LunaConsumeRewardsTemplate lunaConsume : lunaList)
		{
			lunaData.put(lunaConsume.getId(), lunaConsume);
			lunaConsumeCountData.put(lunaConsume.getSumCount(), lunaConsume);
			lunaDataMap.put(lunaConsume.getId(), lunaConsume);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return lunaData.size();
	}
	
	/**
	 * Retrieves a specific reward template based on its unique ID.<br>
	 * This method looks up the data in the internal {@code lunaData} map.
	 * @param id The unique identifier of the reward to find.
	 * @return The corresponding {@link LunaConsumeRewardsTemplate} object, or {@code null} if not found.
	 */
	public LunaConsumeRewardsTemplate getLunaConsumeRewardsId(int id)
	{
		return lunaData.get(id);
	}
	
	/**
	 * Retrieves a reward template based on a specific point value.<br>
	 * This method looks up the data in the {@code lunaConsumeCountData} map.
	 * @param point The point value used to identify the reward.
	 * @return The corresponding {@link LunaConsumeRewardsTemplate} or {@code null}.
	 */
	public LunaConsumeRewardsTemplate getLunaConsumeRewardsBypoint(int point)
	{
		return lunaConsumeCountData.get(point);
	}
	
	/**
	 * Retrieves all reward templates from the data map.<br>
	 * This method returns a {@code Map} containing every {@link LunaConsumeRewardsTemplate}.
	 * @return A {@code Map} where the key is an {@code Integer} ID and the value is the template.
	 */
	public Map<Integer, LunaConsumeRewardsTemplate> getAll()
	{
		return lunaDataMap;
	}
}
