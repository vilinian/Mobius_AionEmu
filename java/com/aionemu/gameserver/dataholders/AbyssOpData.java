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

import com.aionemu.gameserver.model.templates.abyss_op.AbyssOp;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AbyssOp} information.<br>
 * It manages the collection of abyss operations loaded from configuration files.<br>
 * It provides easy access to operation data throughout the game server.
 */
@XmlRootElement(name = "abyss_ops")
@XmlAccessorType(XmlAccessType.FIELD)
public class AbyssOpData
{
	@XmlElement(name = "abyss_op")
	private List<AbyssOp> aolist;
	
	@XmlTransient
	private final TIntObjectHashMap<AbyssOp> opData = new TIntObjectHashMap<>();
	
	@XmlTransient
	private final Map<Integer, AbyssOp> opDataMap = new HashMap<>(1);
	
	/**
	 * This method is called after the XML data is unmarshalled.<br>
	 * It populates internal maps using the list of {@link AbyssOp} objects.
	 * @param paramUnmarshaller The {@code Unmarshaller} used to read the data.
	 * @param paramObject The object that was just unmarshalled.
	 */
	void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject)
	{
		for (AbyssOp abyssOp : aolist)
		{
			opData.put(abyssOp.getId(), abyssOp);
			opDataMap.put(abyssOp.getId(), abyssOp);
		}
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return opData.size();
	}
	
	/**
	 * Retrieves an {@link AbyssOp} object based on its unique identifier.<br>
	 * This method looks up the ID in the internal data map.
	 * @param id The unique integer ID of the abyss operation.
	 * @return The {@code AbyssOp} associated with the given ID, or {@code null} if not found.
	 */
	public AbyssOp getAbyssOpId(int id)
	{
		return opData.get(id);
	}
	
	/**
	 * Retrieves all abyss operations from the data holder.<br>
	 * This method returns a map where the keys are IDs and values are {@link AbyssOp} objects.
	 * @return A {@code Map<Integer, AbyssOp>} containing all loaded operations.
	 */
	public Map<Integer, AbyssOp> getAll()
	{
		return opDataMap;
	}
}
