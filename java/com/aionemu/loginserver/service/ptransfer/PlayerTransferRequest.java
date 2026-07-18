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
package com.aionemu.loginserver.service.ptransfer;

import com.aionemu.loginserver.model.Account;

/**
 * Represents a request to transfer a player account between different servers.<br>
 * This class holds the necessary data required by the {@code PlayerTransferService} to process a move.
 * @author KID
 */
public class PlayerTransferRequest
{
	public PlayerTransferStatus status;
	public byte serverId;
	public byte targetServerId;
	public Account targetAccount;
	public byte[] db;
	public String name;
	public int targetAccountId;
	public int playerId;
	public Account account;
	public Account saccount;
	public int taskId;
	
	/**
	 * Creates a new {@code PlayerTransferRequest} object.<br>
	 * This constructor initializes the request with a specific status.
	 * @param status The {@link PlayerTransferStatus} to assign to this request.
	 */
	public PlayerTransferRequest(PlayerTransferStatus status)
	{
		this.status = status;
	}
}
