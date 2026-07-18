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

import com.aionemu.gameserver.model.autogroup.AutoGroup;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AutoGroup} information.<br>
 * It manages the collection of auto-groups within the game server.<br>
 * It provides easy access to group data using unique identifiers.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"autoGroup"
})
@XmlRootElement(name = "auto_groups")
public class AutoGroupData
{
	@XmlElement(name = "auto_group")
	protected List<AutoGroup> autoGroup;
	@XmlTransient
	private final TIntObjectHashMap<AutoGroup> autoGroupByInstanceId = new TIntObjectHashMap<>();
	@XmlTransient
	private final TIntObjectHashMap<AutoGroup> autoGroupByNpcId = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data is loaded.<br>
	 * It populates internal maps with {@code AutoGroup} data based on instance and NPC IDs.<br>
	 * The original {@code autoGroup} list is cleared to save memory.
	 * @param unmarshaller The {@link Unmarshaller} used to read the data.
	 * @param parent The object that contains this data.
	 */
	void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		for (AutoGroup ag : autoGroup)
		{
			autoGroupByInstanceId.put(ag.getId(), ag);
			
			if (!ag.getNpcIds().isEmpty())
			{
				for (int npcId : ag.getNpcIds())
				{
					autoGroupByNpcId.put(npcId, ag);
				}
			}
		}
		
		autoGroup.clear();
		autoGroup = null;
	}
	
	/**
	 * Retrieves an {@link AutoGroup} based on a specific instance mask ID.<br>
	 * This method looks up the data in the internal map using the provided {@code maskId}.
	 * @param maskId The unique identifier for the instance mask.
	 * @return The corresponding {@code AutoGroup} object, or {@code null} if not found.
	 */
	public AutoGroup getTemplateByInstaceMaskId(int maskId)
	{
		return autoGroupByInstanceId.get(maskId);
	}
	
	/**
	 * Returns the number of elements in the instance ID map.<br>
	 * This method calls {@code autoGroupByInstanceId.size()} to get the count.
	 * @return The total number of items stored by instance ID.
	 */
	public int size()
	{
		return autoGroupByInstanceId.size();
	}
}
