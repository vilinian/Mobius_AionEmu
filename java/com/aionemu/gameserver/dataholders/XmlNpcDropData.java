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
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.npcdrops.XmlDrop;
import com.aionemu.gameserver.model.npcdrops.XmlDropGroup;
import com.aionemu.gameserver.model.npcdrops.XmlNpcDrops;

/**
 * This class holds the data for NPC drops parsed from XML files.<br>
 * It serves as a container for {@link XmlNpcDrops} objects within the game server.<br>
 * Use this class to manage and access drop tables defined in the configuration.
 * @author Falke_34
 */
@XmlRootElement(name = "npc_drops")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlNpcDropData
{
	static Logger log = LoggerFactory.getLogger(XmlNpcDropData.class);
	@XmlElement(name = "npc_drop")
	private List<XmlNpcDrops> nds;
	@XmlTransient
	private HashMap<Integer, ArrayList<DropGroup>> drops;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code drops} map using the list of {@link XmlNpcDrops}.<br>
	 * The {@code drops} map is cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		drops = new HashMap<>();
		for (XmlNpcDrops nd : nds)
		{
			final List<DropGroup> newDg = new ArrayList<>();
			for (XmlDropGroup dg : nd.getDropGroup())
			{
				final List<Drop> dr = new ArrayList<>();
				for (XmlDrop xd : dg.getDrop())
				{
					final Drop datDg = new Drop(xd.getItemId(), xd.getMinAmount(), xd.getMaxAmount(), xd.getChance(), xd.isNoReduction(), xd.isEachMember());
					dr.add(datDg);
				}
				
				final DropGroup datDg = new DropGroup(dr, dg.getRace(), dg.isUseCategory(), dg.getGroupName());
				newDg.add(datDg);
			}
			
			if (drops.containsKey(Integer.valueOf(nd.getNpcId())))
			{
				log.warn("Drop NPC duplicate List ID: " + nd.getNpcId());
			}
			else
			{
				drops.put(nd.getNpcId(), new ArrayList<>());
			}
			
			drops.get(nd.getNpcId()).addAll(newDg);
		}
	}
	
	/**
	 * Returns the total number of NPC drop entries.<br>
	 * This method calls {@code size()} on the internal {@code nds} list.
	 * @return The count of elements in the collection.
	 */
	public int size()
	{
		return nds.size();
	}
	
	/**
	 * Retrieves the collection of drop groups. <br>
	 * The map uses an {@code Integer} ID as the key.
	 * @return a {@link HashMap} containing the drop data.
	 */
	public HashMap<Integer, ArrayList<DropGroup>> getDrops()
	{
		return drops;
	}
	
	/**
	 * Removes all drop data from this object.<br>
	 * The {@code drops} map is cleared and set to {@code null}.<br>
	 * After calling this, the {@code size} method will return 0.
	 */
	public void clear()
	{
		drops.clear();
		drops = null;
	}
}
