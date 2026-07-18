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

import com.aionemu.gameserver.model.templates.bonus_service.BonusServiceAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds the data for a service buff within the game server.<br>
 * It stores attributes and properties associated with specific {@link BonusServiceAttr} types.
 * @author Ace on 31/07/2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"serviceBonusattr"
})
@XmlRootElement(name = "service_bonusattrs")
public class ServiceBuffData
{
	@XmlElement(name = "service_bonusattr")
	protected List<BonusServiceAttr> serviceBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<BonusServiceAttr> templates = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link BonusServiceAttr} objects.<br>
	 * The {@code serviceBonusattr} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (BonusServiceAttr template : serviceBonusattr)
		{
			templates.put(template.getBuffId(), template);
		}
		
		serviceBonusattr.clear();
		serviceBonusattr = null;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return templates.size();
	}
	
	/**
	 * Retrieves a specific bonus attribute based on its unique ID.<br>
	 * This method looks up the data in the internal template map.
	 * @param buffId The unique identifier for the buff.
	 * @return The {@link BonusServiceAttr} object associated with the given ID, or null if not found.
	 */
	public BonusServiceAttr getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
}
