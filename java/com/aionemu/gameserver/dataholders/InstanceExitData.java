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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.portal.InstanceExit;

/**
 * This class holds the data for an instance exit point.<br>
 * It maps information from {@link InstanceExit} to be used by the game server.<br>
 * It is primarily used for handling player transitions between different areas.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"instanceExit"
})
@XmlRootElement(name = "instance_exits")
public class InstanceExitData
{
	@XmlElement(name = "instance_exit")
	protected List<InstanceExit> instanceExit;
	@XmlTransient
	protected List<InstanceExit> instanceExits = new ArrayList<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It copies elements from the {@code instanceExit} list into the internal {@code instanceExits} list.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (InstanceExit exit : instanceExit)
		{
			instanceExits.add(exit);
		}
		
		instanceExit.clear();
		instanceExit = null;
	}
	
	/**
	 * Retrieves a specific {@link InstanceExit} based on the world and race.<br>
	 * It searches through all available exits to find a match.<br>
	 * Returns {@code null} if no matching exit is found.
	 * @param worldId The unique identifier for the world.
	 * @param race The specific {@link Race} type required for the exit.
	 * @return The matching {@link InstanceExit} object or {@code null}.
	 */
	public InstanceExit getInstanceExit(int worldId, Race race)
	{
		for (InstanceExit exit : instanceExits)
		{
			if ((exit.getInstanceId() == worldId) && (race.equals(exit.getRace()) || exit.getRace().equals(Race.PC_ALL)))
			{
				return exit;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the total number of {@link InstanceExit} objects.<br>
	 * This method delegates to the internal list size.
	 * @return The count of elements in the collection.
	 */
	public int size()
	{
		return instanceExits.size();
	}
}
