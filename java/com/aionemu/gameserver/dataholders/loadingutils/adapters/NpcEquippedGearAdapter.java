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
package com.aionemu.gameserver.dataholders.loadingutils.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aionemu.gameserver.model.items.NpcEquippedGear;

/**
 * This class provides an adapter for converting between {@code NpcEquipmentList} and {@link NpcEquippedGear}.<br>
 * It handles the data transformation required during XML serialization and deserialization.
 * @author Luno
 */
public class NpcEquippedGearAdapter extends XmlAdapter<NpcEquipmentList, NpcEquippedGear>
{
	/**
	 * Converts an {@code NpcEquippedGear} object into an {@link NpcEquipmentList}.<br>
	 * This is used for XML serialization.
	 * @param v The {@code NpcEquippedGear} object to convert.
	 * @return The resulting {@code NpcEquipmentList} object.
	 */
	@Override
	public NpcEquipmentList marshal(NpcEquippedGear v)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Converts an {@code NpcEquipmentList} object into an {@link NpcEquippedGear} object.<br>
	 * This method is used during the unmarshalling process to map data from XML.
	 * @param v The {@code NpcEquipmentList} to be converted.
	 * @return A new instance of {@code NpcEquippedGear}.
	 */
	@Override
	public NpcEquippedGear unmarshal(NpcEquipmentList v)
	{
		return new NpcEquippedGear(v);
	}
}
