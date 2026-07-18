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

/**
 * Represents the possible outcomes of a player transfer operation.<br>
 * This enum is used to communicate the success or failure status of a {@code PlayerTransferService} request.
 * @author KID
 */
public enum PlayerTransferResultStatus
{
	SEND_INFO(20),
	OK(21),
	ERROR(22),
	PERFORM_ACTION(23);
	
	private final int id;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Creates a new instance of {@link PlayerTransferResultStatus}.<br>
	 * This constructor assigns the unique identifier to the status.
	 * @param id The integer value representing the status code.
	 */
	PlayerTransferResultStatus(int id)
	{
		this.id = id;
	}
}
