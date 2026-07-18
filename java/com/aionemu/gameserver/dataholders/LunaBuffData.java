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

import com.aionemu.gameserver.model.templates.luna.LunaBonusTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for {@link LunaBonusTemplate} attributes.<br>
 * It serves as a data holder for processing luna-related buffs in the game server.
 */
@XmlRootElement(name = "luna_bonusattrs")
@XmlAccessorType(XmlAccessType.FIELD)
public class LunaBuffData
{
	@XmlElement(name = "luna_bonusattr")
	private List<LunaBonusTemplate> tlist;
	private final TIntObjectHashMap<LunaBonusTemplate> mcData = new TIntObjectHashMap<>();
	@XmlTransient
	private final Map<Integer, LunaBonusTemplate> mcDataMap = new HashMap<>(1);
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code mcData} and {@code mcDataMap} using the list of {@link LunaBonusTemplate} objects.<br>
	 * The maps are updated with the IDs from the loaded templates.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (LunaBonusTemplate id : tlist)
		{
			mcData.put(id.getBuffId(), id);
			mcDataMap.put(id.getBuffId(), id);
		}
	}
	
	/**
	 * Retrieves a specific {@link LunaBonusTemplate} based on its unique ID.<br>
	 * This method looks up the template in the internal data map.
	 * @param id The unique identifier for the luna buff.
	 * @return The corresponding {@code LunaBonusTemplate} or {@code null} if not found.
	 */
	public LunaBonusTemplate getLunaBuffId(int id)
	{
		return mcData.get(id);
	}
	
	/**
	 * Retrieves all available bonus templates.<br>
	 * This method returns the internal map of data.
	 * @return a {@code Map} containing all {@link LunaBonusTemplate} objects.
	 */
	public Map<Integer, LunaBonusTemplate> getAll()
	{
		return mcDataMap;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return mcData.size();
	}
}
