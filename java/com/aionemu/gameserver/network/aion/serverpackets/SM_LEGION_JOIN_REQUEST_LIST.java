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

import java.util.Collection;

import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the list of requests for joining a legion.<br>
 * It is sent to the client to provide information about pending {@link LegionJoinRequest} entries.
 * @author CoolyT
 */
public class SM_LEGION_JOIN_REQUEST_LIST extends AionServerPacket
{
	private final Collection<LegionJoinRequest> ljrList;
	
	/**
	 * Creates a new {@link SM_LEGION_JOIN_REQUEST_LIST} packet.<br>
	 * This constructor initializes the list of join requests.
	 * @param ljrList The collection of {@code LegionJoinRequest} objects to include.
	 */
	public SM_LEGION_JOIN_REQUEST_LIST(Collection<LegionJoinRequest> ljrList)
	{
		this.ljrList = ljrList;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(-ljrList.size());
		for (LegionJoinRequest ljr : ljrList)
		{
			writeD(ljr.getPlayerId());
			writeS(ljr.getPlayerName());
			writeC(ljr.getPlayerClass());
			writeC(ljr.getGenderId());
			writeH(ljr.getLevel());
			writeS(ljr.getMsg());
			writeD((int) ljr.getDate().getTime());
		}
	}
}
