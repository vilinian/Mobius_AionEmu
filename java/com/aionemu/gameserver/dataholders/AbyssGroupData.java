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

import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssGroupAttr;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for {@link AbyssGroupData} information.<br>
 * It stores and manages the attributes associated with specific abyss groups.<br>
 * Use this class to access group-related configurations within the game server.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
	"abyssGroupattr"
})
@XmlRootElement(name = "abyss_groupattrs")
public class AbyssGroupData
{
	@XmlElement(name = "abyss_groupattr")
	protected List<AbyssGroupAttr> abyssGroupattr;
	
	@XmlTransient
	private final TIntObjectHashMap<AbyssGroupAttr> templates = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code templates} map using the list of {@link AbyssGroupAttr} objects.<br>
	 * The {@code abyssGroupattr} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (AbyssGroupAttr template : abyssGroupattr)
		{
			templates.put(template.getBuffId(), template);
		}
		
		abyssGroupattr.clear();
		abyssGroupattr = null;
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
	 * Retrieves the attribute data for a specific instance bonus.<br>
	 * This method looks up the {@code AbyssGroupAttr} using the provided ID.
	 * @param buffId The unique identifier for the buff.
	 * @return The corresponding {@link AbyssGroupAttr} object or {@code null}.
	 */
	public AbyssGroupAttr getInstanceBonusattr(int buffId)
	{
		return templates.get(buffId);
	}
}
