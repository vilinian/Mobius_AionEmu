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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.MailServicePacket;
import com.aionemu.gameserver.utils.collections.ListSplitter;

/**
 * This packet handles the communication for the mail service system.<br>
 * It allows players to interact with their {@link Mailbox} and manage {@link Letter} objects.
 * @author kosyachok, Source
 */
public class SM_MAIL_SERVICE extends MailServicePacket
{
	private final int serviceId;
	private Collection<Letter> letters;
	private int totalCount;
	private int unreadCount;
	private int unreadExpressCount;
	private int unreadBlackCloudCount;
	private int mailMessage;
	private Letter letter;
	private long time;
	private int letterId;
	private int[] letterIds;
	private int attachmentType;
	private boolean isExpress;
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} instance using a specific mailbox.<br>
	 * This constructor initializes the packet with default values.
	 * @param mailbox The {@code Mailbox} object to associate with this service.
	 */
	public SM_MAIL_SERVICE(Mailbox mailbox)
	{
		super(null);
		serviceId = 0;
	}
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} instance for a specific message.<br>
	 * This constructor sets the {@code serviceId} to 1.<br>
	 * It extracts the ID from the provided {@code MailMessage}.
	 * @param mailMessage The {@code MailMessage} object containing the data.
	 */
	public SM_MAIL_SERVICE(MailMessage mailMessage)
	{
		super(null);
		serviceId = 1;
		this.mailMessage = mailMessage.getId();
	}
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} packet for a specific player.<br>
	 * This constructor initializes the service with a collection of {@link Letter} objects.<br>
	 * It sets the internal {@code serviceId} to 2.
	 * @param player The {@link Player} who is receiving the mail service.
	 * @param letters The collection of {@link Letter} items to be included in the packet.
	 */
	public SM_MAIL_SERVICE(Player player, Collection<Letter> letters)
	{
		super(player);
		serviceId = 2;
		this.letters = letters;
	}
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} packet for a specific player.<br>
	 * This constructor sets the service ID to 2.<br>
	 * It initializes the collection of letters and the express mail status.
	 * @param player The {@link Player} who is receiving the mail service.
	 * @param letters A {@code Collection} of {@link Letter} objects to be processed.
	 * @param isExpress A {@code boolean} indicating if the mail is sent via express delivery.
	 */
	public SM_MAIL_SERVICE(Player player, Collection<Letter> letters, boolean isExpress)
	{
		super(player);
		serviceId = 2;
		this.letters = letters;
		this.isExpress = isExpress;
	}
	
	/**
	 * Creates a new {@code SM_MAIL_SERVICE} packet for a specific player.<br>
	 * This constructor sets the service ID to {@code 3}.<br>
	 * It initializes the packet with a single {@link Letter} and a timestamp.
	 * @param player The {@link Player} who will receive this mail service.
	 * @param letter The {@link Letter} object being processed.
	 * @param time The timestamp associated with the mail action.
	 */
	public SM_MAIL_SERVICE(Player player, Letter letter, long time)
	{
		super(player);
		serviceId = 3;
		this.letter = letter;
		this.time = time;
	}
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} packet using specific IDs.<br>
	 * This constructor sets the {@code serviceId} to 5.<br>
	 * It initializes the letter and attachment data.
	 * @param letterId The unique identifier for the letter.
	 * @param attachmentType The type of attachment associated with the mail.
	 */
	public SM_MAIL_SERVICE(int letterId, int attachmentType)
	{
		super(null);
		serviceId = 5;
		this.letterId = letterId;
		this.attachmentType = attachmentType;
	}
	
	/**
	 * Creates a new {@link SM_MAIL_SERVICE} packet using an array of IDs.<br>
	 * This constructor sets the {@code serviceId} to {@code 6}.<br>
	 * It initializes the internal list of letters based on the provided identifiers.
	 * @param letterIds An array of integers representing the unique IDs of the letters.
	 */
	public SM_MAIL_SERVICE(int[] letterIds)
	{
		super(null);
		serviceId = 6;
		this.letterIds = letterIds;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final Mailbox mailbox = con.getActivePlayer().getMailbox();
		totalCount = mailbox.size();
		unreadCount = mailbox.getUnreadCount();
		unreadExpressCount = mailbox.getUnreadCountByType(LetterType.EXPRESS);
		unreadBlackCloudCount = mailbox.getUnreadCountByType(LetterType.BLACKCLOUD);
		writeC(serviceId);
		switch (serviceId)
		{
			case 0:
				mailbox.isMailListUpdateRequired = true;
				writeMailboxState(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
				break;
			case 1:
				writeMailMessage(mailMessage);
				break;
			case 2:
				Collection<Letter> _letters;
				if (!letters.isEmpty())
				{
					final ListSplitter<Letter> splittedLetters = new ListSplitter<>(letters, 100);
					_letters = splittedLetters.getNext();
				}
				else
				{
					_letters = letters;
				}
				
				writeLettersList(_letters, player, isExpress, unreadExpressCount + unreadBlackCloudCount);
				break;
			case 3:
				writeLetterRead(letter, time, totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
				break;
			case 5:
				writeLetterState(letterId, attachmentType);
				break;
			case 6:
				mailbox.isMailListUpdateRequired = true;
				writeLetterDelete(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount, letterIds);
				break;
		}
	}
}
