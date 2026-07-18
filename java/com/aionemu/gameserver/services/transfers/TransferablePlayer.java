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
package com.aionemu.gameserver.services.transfers;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * Represents a {@link Player} object that is eligible for character transfers.<br>
 * This class acts as a data wrapper to facilitate moving characters between different game servers.
 * @author KID
 */
public class TransferablePlayer
{
	public int playerId;
	public int accountId;
	public int targetAccountId;
	public Player player;
	public byte targetServerId;
	public int taskId;
	
	/**
	 * Creates a new instance of {@code TransferablePlayer}.<br>
	 * This object stores the necessary IDs for moving a player.
	 * @param playerId The unique ID of the player.
	 * @param accountId The unique ID of the source account.
	 * @param targetAccountId The unique ID of the destination account.
	 */
	public TransferablePlayer(int playerId, int accountId, int targetAccountId)
	{
		this.playerId = playerId;
		this.accountId = accountId;
		this.targetAccountId = targetAccountId;
	}
}
