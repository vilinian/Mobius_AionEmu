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
package com.aionemu.gameserver.model;

/**
 * Represents the possible outcomes of a duel between two players.<br>
 * This enum is used by {@code Duel} to determine the winner and state.
 * @author xavier
 */
public enum DuelResult
{
	DUEL_WON(1300098, (byte) 2),
	DUEL_LOST(1300099, (byte) 0),
	DUEL_DRAW(1300100, (byte) 1);
	
	private final int msgId;
	private final byte resultId;
	
	/**
	 * Creates a new instance of the {@link DuelResult} enum.<br>
	 * This constructor initializes the internal message and result identifiers.
	 * @param msgId The unique identifier for the message.
	 * @param resultId The numerical code representing the duel outcome.
	 */
	private DuelResult(int msgId, byte resultId)
	{
		this.msgId = msgId;
		this.resultId = resultId;
	}
	
	/**
	 * Retrieves the unique message identifier for this duel result.<br>
	 * This ID is used to display the correct notification to the player.
	 * @return The {@code int} value of the message ID.
	 */
	public int getMsgId()
	{
		return msgId;
	}
	
	/**
	 * Retrieves the unique identifier for the duel outcome.<br>
	 * This value is used to determine the specific type of result.
	 * @return the {@code byte} value representing the result ID.
	 */
	public byte getResultId()
	{
		return resultId;
	}
}
