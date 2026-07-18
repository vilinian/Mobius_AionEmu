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

import com.aionemu.gameserver.model.templates.bonus_service.F2pBonusAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class holds data related to Free-to-Play (F2P) bonuses.<br>
 * It stores the attributes and configurations for various F2P rewards.<br>
 * Use this class to manage how {@link com.aionemu.gameserver.model.templates.bonus_service.F2pBonusAttr} data is handled.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"f2pBonusattr"
})
@XmlRootElement(name = "f2p_bonus")
public class F2PBonusData
{
	@XmlElement(name = "f2p")
	protected List<F2pBonusAttr> f2pBonusattr;
	
	@XmlTransient
	private final TIntObjectHashMap<F2pBonusAttr> templates = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link F2pBonusAttr} objects.<br>
	 * The {@code f2pBonusattr} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (F2pBonusAttr template : f2pBonusattr)
		{
			templates.put(template.getBuffId(), template);
		}
		
		f2pBonusattr.clear();
		f2pBonusattr = null;
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
	 * This method looks up the data in the internal {@code templates} map.
	 * @param buffId The unique identifier for the buff.
	 * @return The corresponding {@link F2pBonusAttr} object or {@code null} if not found.
	 */
	public F2pBonusAttr getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
}
