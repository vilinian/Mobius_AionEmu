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
package com.aionemu.gameserver.services.mail;

/**
 * Represents the possible outcomes of a siege event.<br>
 * This enum is used to determine which rewards or messages are sent via {@link com.aionemu.gameserver.services.mail.MailService}.
 * @author Rolandas
 */
public enum SiegeResult
{
	DEFENCE(0),
	OCCUPY(1),
	PROTECT(2),
	DEFENDER(3),
	EMPTY(4),
	FAIL(5);
	
	private final int value;
	
	/**
	 * Creates a new {@link SiegeResult} instance.<br>
	 * This constructor assigns an internal integer value to the enum constant.
	 * @param value The unique identifier for the siege result.
	 */
	SiegeResult(int value)
	{
		this.value = value;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link AbyssSiegeLevel}.<br>
	 * This value corresponds to the internal type code.
	 * @return The integer ID of the level.
	 */
	public int getId()
	{
		return value;
	}
}
