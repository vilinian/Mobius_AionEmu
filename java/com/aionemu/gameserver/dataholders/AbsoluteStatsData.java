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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.stats.AbsoluteStatsTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for absolute statistics.<br>
 * It stores and manages {@link AbsoluteStatsTemplate} information within the game server.<br>
 * It is used to map specific stats to their corresponding values.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"absoluteStats"
})
@XmlRootElement(name = "absolute_stats")
public class AbsoluteStatsData
{
	@XmlElement(name = "stats_set", required = true)
	protected List<AbsoluteStatsTemplate> absoluteStats;
	@XmlTransient
	private final TIntObjectHashMap<ModifiersTemplate> absoluteStatsData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code absoluteStatsData} map using the list of {@link AbsoluteStatsTemplate} objects.<br>
	 * The {@code absoluteStats} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (AbsoluteStatsTemplate stats : absoluteStats)
		{
			absoluteStatsData.put(stats.getId(), stats.getModifiers());
		}
		
		absoluteStats.clear();
		absoluteStats = null;
	}
	
	/**
	 * Retrieves a specific modifier template from the data map.<br>
	 * This method uses the provided ID to look up the corresponding entry.
	 * @param statSetId The unique identifier for the stats set.
	 * @return The {@link ModifiersTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public ModifiersTemplate getTemplate(int statSetId)
	{
		return absoluteStatsData.get(statSetId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return absoluteStatsData.size();
	}
}
