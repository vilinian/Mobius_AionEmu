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

import com.aionemu.gameserver.skillengine.model.MotionTime;

import gnu.trove.map.hash.THashMap;

/**
 * This class holds the motion data for skills within the game.<br>
 * It maps specific animation identifiers to their corresponding {@link MotionTime} values.
 * @author kecimis
 */
@XmlRootElement(name = "motion_times")
@XmlAccessorType(XmlAccessType.FIELD)
public class MotionData
{
	@XmlElement(name = "motion_time")
	protected List<MotionTime> motionTimes;
	@XmlTransient
	private final THashMap<String, MotionTime> motionTimesMap = new THashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code motionTimesMap} using the list of {@link MotionTime} objects.<br>
	 * The {@code motionTimesMap} is rebuilt from the unmarshalled list.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (MotionTime motion : motionTimes)
		{
			motionTimesMap.put(motion.getName(), motion);
		}
	}
	
	/**
	 * Retrieves the list of all {@link MotionTime} objects.<br>
	 * If the internal list is {@code null}, it creates a new {@code ArrayList}.
	 * @return A {@code List} containing all motion times.
	 */
	public List<MotionTime> getMotionTimes()
	{
		if (motionTimes == null)
		{
			motionTimes = new ArrayList<>();
		}
		
		return motionTimes;
	}
	
	/**
	 * Retrieves a specific {@link MotionTime} object by its name.<br>
	 * This method looks up the value in the internal map.
	 * @param name The unique identifier for the motion time.
	 * @return The {@code MotionTime} associated with the given name, or {@code null} if not found.
	 */
	public MotionTime getMotionTime(String name)
	{
		return motionTimesMap.get(name);
	}
	
	/**
	 * Returns the total number of motion times.<br>
	 * This method checks if {@code motionTimes} is {@code null}.<br>
	 * If it is {@code null}, it returns {@code 0}.<br>
	 * Otherwise, it returns the size of the list.
	 * @return The count of elements in the collection.
	 */
	public int size()
	{
		if (motionTimes == null)
		{
			return 0;
		}
		
		return motionTimes.size();
	}
}
