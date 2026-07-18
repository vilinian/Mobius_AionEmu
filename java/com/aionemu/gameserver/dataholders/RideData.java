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

import com.aionemu.gameserver.model.templates.ride.RideInfo;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for ride information.<br>
 * It stores and manages the properties of various mounts used in the game.<br>
 * It is primarily used to map {@code RideInfo} templates to their respective IDs.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"rides"
})
@XmlRootElement(name = "rides")
public class RideData
{
	@XmlElement(name = "ride_info")
	private List<RideInfo> rides;
	@XmlTransient
	private TIntObjectHashMap<RideInfo> rideInfos;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code rideInfos} map using the list of {@link RideInfo} objects.<br>
	 * The {@code rides} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		rideInfos = new TIntObjectHashMap<>();
		
		for (RideInfo info : rides)
		{
			rideInfos.put(info.getNpcId(), info);
		}
		
		rides.clear();
		rides = null;
	}
	
	/**
	 * Retrieves the {@link RideInfo} for a specific NPC.<br>
	 * This method looks up the data using the provided {@code npcId}.<br>
	 * It returns {@code null} if no ride is found.
	 * @param npcId The unique identifier of the NPC.
	 * @return The {@code RideInfo} object associated with the ID.
	 */
	public RideInfo getRideInfo(int npcId)
	{
		return rideInfos.get(npcId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return rideInfos.size();
	}
}
