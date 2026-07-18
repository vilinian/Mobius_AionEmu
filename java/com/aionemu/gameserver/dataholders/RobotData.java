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

import com.aionemu.gameserver.model.templates.robot.RobotInfo;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for robot information within the game server.<br>
 * It stores and manages {@link RobotInfo} objects to facilitate easy access to robot-related data.
 * @author Ever'
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"robots"
})
@XmlRootElement(name = "robots")
public class RobotData
{
	@XmlElement(name = "robot_info")
	private List<RobotInfo> robots;
	@XmlTransient
	private TIntObjectHashMap<RobotInfo> robotInfos;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code robotInfos} map using the list of {@link RobotInfo} objects.<br>
	 * The {@code robots} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		robotInfos = new TIntObjectHashMap<>();
		for (RobotInfo info : robots)
		{
			robotInfos.put(info.getRobotId(), info);
		}
		
		robots.clear();
		robots = null;
	}
	
	/**
	 * Retrieves the information for a specific robot.<br>
	 * This method looks up the data using the provided {@code npcId}.
	 * @param npcId The unique identifier of the robot to find.
	 * @return The {@link RobotInfo} object associated with the ID, or {@code null} if not found.
	 */
	public RobotInfo getRobotInfo(int npcId)
	{
		return robotInfos.get(npcId);
	}
	
	/**
	 * Returns the total number of robots.<br>
	 * This method calls {@code size} to get the count.
	 * @return The number of items currently stored in the collection.
	 */
	public int size()
	{
		return robotInfos.size();
	}
}
