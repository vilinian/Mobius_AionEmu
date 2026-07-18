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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.grind.GrindCombine;

/**
 * This class holds the data for grind combinations within the game server.<br>
 * It serves as a data container for {@link com.aionemu.gameserver.model.templates.item.grind.GrindCombine} objects.
 */
@XmlRootElement(name = "grind_combines")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class GrindCombineData
{
	@XmlElement(name = "grind_combine")
	private List<GrindCombine> tlist;
	
	/**
	 * Retrieves a {@link GrindCombine} based on player and color criteria.<br>
	 * It searches the internal list for a matching entry.<br>
	 * Returns {@code null} if no match is found.
	 * @param player The {@link Player} object used to check the character class.
	 * @param color1 The first required color ID.
	 * @param color2 The second required color ID.
	 * @return The matching {@link GrindCombine} object or {@code null}.
	 */
	public GrindCombine getCombine(Player player, int color1, int color2)
	{
		GrindCombine result = null;
		for (GrindCombine cmb : tlist)
		{
			if ((cmb.getPlayerClass() != player.getPlayerClass()) || (cmb.getColor1() != color1) || (cmb.getColor2() != color2))
			{
				continue;
			}
			
			result = cmb;
		}
		
		return result;
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return tlist.size();
	}
}
