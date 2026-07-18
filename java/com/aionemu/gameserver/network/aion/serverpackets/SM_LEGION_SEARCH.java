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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.LegionService;

/**
 * This packet handles the request to search for a specific {@link Legion}.<br>
 * It allows players to find legion information based on provided criteria.
 * @author CoolyT
 */
public class SM_LEGION_SEARCH extends AionServerPacket
{
	private final List<Legion> legions;
	
	/**
	 * Creates a new {@link SM_LEGION_SEARCH} packet.<br>
	 * This method initializes the list of {@link Legion} objects to be sent.
	 * @param legions The list of {@code Legion} data to include in the search.
	 */
	public SM_LEGION_SEARCH(List<Legion> legions)
	{
		this.legions = legions;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(-legions.size());
		
		for (Legion legion : legions)
		{
			writeD(legion.getLegionId());
			writeS(legion.getLegionName());
			writeS(LegionService.getInstance().getBrigadeGeneralName(legion));
			writeC(legion.getLegionLevel());
			writeD(legion.getLegionMembers().size()); // or maybe this are 2 H's ...
			writeS(legion.getLegionDiscription());
			writeC(legion.getLegionJoinType());
			writeH(legion.getMinLevel());
		}
	}
}
