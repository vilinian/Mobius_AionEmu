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

import com.aionemu.gameserver.model.templates.revive_start_points.InstanceReviveStartPoints;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for revive start points within an instance.<br>
 * It maps {@code InstanceReviveStartPoints} templates to their respective IDs.<br>
 * Use this class to manage and retrieve spawn locations for player revivals.
 * @author Falke_34
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"InstanceStartPoints"
})
@XmlRootElement(name = "instance_revive_start_points")
public class ReviveInstanceStartPointsData
{
	@XmlElement(name = "instance_revive_start_point")
	protected List<InstanceReviveStartPoints> InstanceStartPoints;
	
	@XmlTransient
	private final TIntObjectHashMap<InstanceReviveStartPoints> custom = new TIntObjectHashMap<>();
	
	/**
	 * Retrieves the revive start point for a specific world.<br>
	 * This method looks up the data using the provided {@code worldId}.<br>
	 * It returns {@code null} if no custom point is found.
	 * @param worldId The unique identifier of the world to look up.
	 * @return The {@link InstanceReviveStartPoints} object for the given world, or {@code null}.
	 */
	public InstanceReviveStartPoints getReviveStartPoint(int worldId)
	{
		return custom.get(worldId);
	}
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code custom} map using the list of {@link InstanceReviveStartPoints}.<br>
	 * The {@code custom} map is rebuilt from the loaded data.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (InstanceReviveStartPoints it : InstanceStartPoints)
		{
			getCustomMap().put(it.getReviveWorld(), it);
		}
	}
	
	/**
	 * Retrieves the internal map of custom revive start points.<br>
	 * This method provides access to the {@code TIntObjectHashMap} used for lookups.
	 * @return a {@code TIntObjectHashMap} containing the instance data.
	 */
	private TIntObjectHashMap<InstanceReviveStartPoints> getCustomMap()
	{
		return custom;
	}
	
	/**
	 * Returns the number of elements in the custom map.<br>
	 * This method calls {@code getCustomMap} to retrieve the internal collection.
	 * @return The total count of custom achievement action templates.
	 */
	public int size()
	{
		return custom.size();
	}
}
