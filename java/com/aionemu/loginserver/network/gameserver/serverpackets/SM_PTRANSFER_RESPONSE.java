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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferRequest;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferResultStatus;
import com.aionemu.loginserver.service.ptransfer.PlayerTransferTask;

/**
 * This packet handles the response for a player transfer request.<br>
 * It informs the client of the result status from the {@link PlayerTransferTask}.
 * @author KID
 */
public class SM_PTRANSFER_RESPONSE extends GsServerPacket
{
	private final PlayerTransferResultStatus result;
	private Account account;
	private PlayerTransferRequest request;
	private int taskId;
	private String reason;
	private PlayerTransferTask task;
	
	/**
	 * Creates a new response for a player transfer request.<br>
	 * This constructor initializes the packet with a specific status and task ID.
	 * @param result The {@code PlayerTransferResultStatus} of the operation.
	 * @param taskId The unique identifier for the transfer task.
	 */
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId)
	{
		this.result = result;
		this.taskId = taskId;
	}
	
	/**
	 * Creates a new response packet for a player transfer operation.<br>
	 * This constructor initializes the status and links the request data.
	 * @param result The {@code PlayerTransferResultStatus} of the operation.
	 * @param request The {@code PlayerTransferRequest} containing the original details.
	 */
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferRequest request)
	{
		this.result = result;
		this.request = request;
		account = request.targetAccount;
		taskId = request.taskId;
	}
	
	/**
	 * Creates a new response packet for a player transfer request.<br>
	 * This constructor initializes the status, task ID, and failure reason.
	 * @param result The {@code PlayerTransferResultStatus} of the operation.
	 * @param taskId The unique identifier for the transfer task.
	 * @param reason A description of why the transfer succeeded or failed.
	 */
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId, String reason)
	{
		this.result = result;
		this.taskId = taskId;
		this.reason = reason;
	}
	
	/**
	 * Creates a new response packet for a player transfer operation.<br>
	 * This constructor links the final status with the specific task performed.
	 * @param result The {@link PlayerTransferResultStatus} of the operation.
	 * @param task The {@link PlayerTransferTask} associated with this request.
	 */
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferTask task)
	{
		this.result = result;
		this.task = task;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(12);
		writeD(result.getId());
		switch (result)
		{
			case SEND_INFO:
				writeD(request.targetAccountId);
				writeD(taskId);
				writeS(request.name);
				writeS(account.getName());
				writeD(request.db.length);
				writeB(request.db);
				break;
			case OK:
				writeD(taskId);
				break;
			case ERROR:
				writeD(taskId);
				writeS(reason);
				break;
			case PERFORM_ACTION:
				writeC(task.sourceServerId);
				writeC(task.targetServerId);
				writeD(task.sourceAccountId);
				writeD(task.targetAccountId);
				writeD(task.playerId);
				writeD(task.id);
				break;
		}
	}
}
