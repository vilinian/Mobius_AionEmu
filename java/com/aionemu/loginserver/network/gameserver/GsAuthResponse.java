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
package com.aionemu.loginserver.network.gameserver;

/**
 * This class represents the various responses that the {@code LoginServer} may send to the gameserver.<br>
 * It is primarily used to communicate results such as authentication failures or other status updates.
 * @author -Nemesiss-
 */
public enum GsAuthResponse
{
	/**
	 * Everything is OK
	 */
	AUTHED(0),
	/**
	 * Password/IP etc does not match.
	 */
	NOT_AUTHED(1),
	/**
	 * Requested id is not free
	 */
	ALREADY_REGISTERED(2);
	
	/**
	 * id of this enum that may be sent to client
	 */
	private final byte responseId;
	
	/**
	 * Creates a new instance of this enum.<br>
	 * It assigns the provided integer to the {@code responseId} field.
	 * @param responseId The unique identifier for the authentication response.
	 */
	private GsAuthResponse(int responseId)
	{
		this.responseId = (byte) responseId;
	}
	
	/**
	 * Retrieves the unique identifier for this response type.<br>
	 * This value is used when sending messages to the client.
	 * @return the {@code byte} value of the {@code responseId}.
	 */
	public byte getResponseId()
	{
		return responseId;
	}
}
