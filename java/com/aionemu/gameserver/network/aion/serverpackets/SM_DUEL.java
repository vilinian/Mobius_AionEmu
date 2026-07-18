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

import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the results of a duel between two players.<br>
 * It sends information regarding the winner and the outcome to the client.
 * @author xavier
 */
public class SM_DUEL extends AionServerPacket
{
	private String playerName;
	private DuelResult result;
	private int requesterObjId;
	private final int type;
	
	/**
	 * Initializes a new {@link SM_DUEL} packet.<br>
	 * Sets the specific duel type for this packet.
	 * @param type The integer value representing the duel category.
	 */
	private SM_DUEL(int type)
	{
		this.type = type;
	}
	
	/**
	 * Creates a new {@link SM_DUEL} packet to signal that a duel has started.<br>
	 * This method initializes the packet with a specific requester ID.
	 * @param requesterObjId The unique identifier of the object requesting the duel.
	 * @return A new instance of {@code SM_DUEL}.
	 */
	public static SM_DUEL SM_DUEL_STARTED(int requesterObjId)
	{
		final SM_DUEL packet = new SM_DUEL(0x00);
		packet.setRequesterObjId(requesterObjId);
		return packet;
	}
	
	/**
	 * Sets the unique identifier for the object that requested the duel.<br>
	 * This value is stored in the {@code requesterObjId} field.
	 * @param requesterObjId The ID of the requesting object.
	 */
	private void setRequesterObjId(int requesterObjId)
	{
		this.requesterObjId = requesterObjId;
	}
	
	/**
	 * Creates a new {@link SM_DUEL} packet to notify players of a duel outcome.<br>
	 * This method sets the result and the name of the player involved.
	 * @param result The {@code DuelResult} object containing the match details.
	 * @param playerName The name of the player associated with the result.
	 * @return A new instance of {@code SM_DUEL} with type {@code 0x01}.
	 */
	public static SM_DUEL SM_DUEL_RESULT(DuelResult result, String playerName)
	{
		final SM_DUEL packet = new SM_DUEL(0x01);
		packet.setPlayerName(playerName);
		packet.setResult(result);
		return packet;
	}
	
	/**
	 * Sets the name of the player.<br>
	 * This updates the {@code playerName} field in this {@link SM_DUEL} instance.
	 * @param playerName The name of the player to set.
	 */
	private void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
	
	/**
	 * Updates the {@code result} field of this packet.<br>
	 * This method stores the outcome of a duel.
	 * @param result The {@link DuelResult} object containing the match data.
	 */
	private void setResult(DuelResult result)
	{
		this.result = result;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(type);
		
		switch (type)
		{
			case 0x00:
				writeD(requesterObjId);
				break;
			case 0x01:
				writeC(result.getResultId()); // unknown
				writeD(result.getMsgId());
				writeS(playerName);
				break;
			case 0xE0:
				break;
			default:
				throw new IllegalArgumentException("invalid SM_DUEL packet type " + type);
		}
	}
}
