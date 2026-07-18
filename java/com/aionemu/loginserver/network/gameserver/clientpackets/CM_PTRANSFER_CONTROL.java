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
package com.aionemu.loginserver.network.gameserver.clientpackets;

import com.aionemu.loginserver.network.gameserver.GsClientPacket;
import com.aionemu.loginserver.service.PlayerTransferService;

/**
 * Handles the control request for player transfers between game servers.<br>
 * This packet allows the server to manage and validate transfer requests initiated by the client.
 * @author KID
 */
public class CM_PTRANSFER_CONTROL extends GsClientPacket
{
	private byte actionId;
	
	@Override
	protected void readImpl()
	{
		actionId = readSC();
		switch (actionId)
		{
			case 1: // request transfer
			{
				final int taskId = readD();
				final String name = readS();
				final int bytes = getRemainingBytes();
				final byte[] db = this.readB(bytes);
				PlayerTransferService.getInstance().requestTransfer(taskId, name, db);
			}
				break;
			case 2: // ERROR
			{
				final int taskId = readD();
				final String reason = readS();
				PlayerTransferService.getInstance().onError(taskId, reason);
			}
				break;
			case 3: // ok
			{
				final int taskId = readD();
				final int playerId = readD();
				PlayerTransferService.getInstance().onOk(taskId, playerId);
			}
				break;
			case 4: // Task stop
			{
				final int taskId = readD();
				final String reason = readS();
				PlayerTransferService.getInstance().onTaskStop(taskId, reason);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		// no actions required
	}
}
