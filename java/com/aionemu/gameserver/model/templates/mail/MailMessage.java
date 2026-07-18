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
package com.aionemu.gameserver.model.templates.mail;

/**
 * Represents the different types of mail messages available in the game.<br>
 * This enum is used to identify specific message templates for the {@code Mail} system.
 * @author kosyachok
 */
public enum MailMessage
{
	MAIL_SEND_SECCESS(0),
	NO_SUCH_CHARACTER_NAME(1),
	RECIPIENT_MAILBOX_FULL(2),
	MAIL_IS_ONE_RACE_ONLY(3),
	YOU_ARE_IN_RECIPIENT_IGNORE_LIST(4),
	RECIPIENT_IGNORING_MAIL_FROM_PLAYERS_LOWER_206_LVL(5), // WTF??
	MAILSPAM_WAIT_FOR_SOME_TIME(6);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link MailMessage}.<br>
	 * This constructor assigns the unique identifier to the message.
	 * @param id The numeric ID assigned to this specific mail type.
	 */
	private MailMessage(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
