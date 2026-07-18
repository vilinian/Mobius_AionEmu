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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * Handles the client request to upload a legion emblem.<br>
 * This packet allows players to submit their custom emblems to the server for display.
 * @author Simple
 */
public class CM_LEGION_UPLOAD_EMBLEM extends AionClientPacket
{
	/**
	 * Emblem related information *
	 */
	private int size;
	private byte[] data;
	
	/**
	 * This method creates a new {@code CM_LEGION_UPLOAD_EMBLEM} packet.<br>
	 * It initializes the packet with the required network states.<br>
	 * Use this to handle emblem uploads from the client.
	 * @param opcode The unique identifier for the packet type.
	 * @param state The primary {@link State} of the connection.
	 * @param restStates Additional {@link State} values for the packet.
	 */
	public CM_LEGION_UPLOAD_EMBLEM(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		size = readD();
		data = new byte[size];
		data = readB(size);
	}
	
	@Override
	protected void runImpl()
	{
		if ((data != null) && (data.length > 0))
		{
			LegionService.getInstance().uploadEmblemData(getConnection().getActivePlayer(), size, data);
		}
	}
}
