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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the {@code CM_FILE_VERIFY} packet sent from the client to the server.<br>
 * This packet is used to verify the integrity of the game files on the user's machine.
 * @author Alex
 */
public class CM_FILE_VERIFY extends AionClientPacket
{
	private String result;
	private int unk;
	
	/**
	 * This method initializes a new {@code CM_FILE_VERIFY} packet.<br>
	 * It sets the required network states for the connection.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the {@link AionConnection}.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_FILE_VERIFY(int opcode, AionConnection.State state, AionConnection.State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		unk = readC();
		result = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		PacketSendUtility.sendMessage(player, "result: " + result + " unk: " + unk);
	}
	
	/**
	 * Creates and returns a copy of this {@code CM_FILE_VERIFY} object.<br>
	 * This method uses the default cloning mechanism from the parent class.
	 * @return A new object that is a copy of this instance.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
