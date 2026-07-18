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

import com.aionemu.gameserver.skillengine.model.ChargeSkillEntry;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for skill charges within the game server.<br>
 * It acts as a data container for {@link ChargeSkillEntry} objects.<br>
 * Use this class to manage and access information regarding specific skill charges.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"chargeSkills"
})
@XmlRootElement(name = "skill_charge")
public class SkillChargeData
{
	@XmlElement(name = "charge", required = true)
	protected List<ChargeSkillEntry> chargeSkills;
	@XmlTransient
	private final TIntObjectHashMap<ChargeSkillEntry> skillChargeData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code skillChargeData} map using the list of {@link ChargeSkillEntry} objects.<br>
	 * The {@code chargeSkills} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (ChargeSkillEntry chargeSkill : chargeSkills)
		{
			skillChargeData.put(chargeSkill.getId(), chargeSkill);
		}
		
		chargeSkills.clear();
		chargeSkills = null;
	}
	
	/**
	 * Retrieves a specific {@link ChargeSkillEntry} based on its unique ID.<br>
	 * This method looks up the entry in the internal data map.
	 * @param chargeId The unique identifier for the skill charge.
	 * @return The {@code ChargeSkillEntry} associated with the provided ID, or {@code null} if not found.
	 */
	public ChargeSkillEntry getChargedSkillEntry(int chargeId)
	{
		return skillChargeData.get(chargeId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return skillChargeData.size();
	}
}
