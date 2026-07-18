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
package com.aionemu.gameserver.network.aion;

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;

/**
 * This class represents the base packet for all mail-related network communications.<br>
 * It handles data exchange between the server and the client regarding {@link Letter} objects.
 * @author kosyachok, Source
 */
public abstract class MailServicePacket extends AionServerPacket
{
	// private static final Logger log = LoggerFactory.getLogger(MailServicePacket.class);
	protected Player player;
	
	/**
	 * Creates a new {@code MailServicePacket} for a specific user.<br>
	 * This constructor initializes the packet with the provided {@link Player} object.
	 * @param player The {@code Player} who will receive this mail service packet.
	 */
	public MailServicePacket(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Writes the list of letters to the network packet.<br>
	 * This method handles filtering based on whether the player is a postman.<br>
	 * It iterates through the provided {@code Collection<Letter>} and writes their details.
	 * @param letters The collection of {@link Letter} objects to be sent.
	 * @param player The {@link Player} object associated with this action.
	 * @param isPostman A boolean flag indicating if the player has postman privileges.
	 * @param showCount The number of items to display if the player is a postman, otherwise it uses the collection size.
	 */
	protected void writeLettersList(Collection<Letter> letters, Player player, boolean isPostman, int showCount)
	{
		writeD(player.getObjectId());
		writeC(0);
		writeH(isPostman ? -showCount : -letters.size()); // -loop cnt [stupid nc shit!]
		for (Letter letter : letters)
		{
			if (isPostman)
			{
				if (!letter.isExpress())
				{
					continue;
				}
				else if (!letter.isUnread())
				{
					continue;
				}
			}
			
			writeD(letter.getObjectId());
			writeS(letter.getSenderName());
			writeS(letter.getTitle());
			writeC(letter.isUnread() ? 0 : 1);
			if (letter.getAttachedItem() != null)
			{
				writeD((int) (letter.getTimeStamp().getTime() / 1000));
				writeD(letter.getAttachedItem().getObjectId());
			}
			else
			{
				writeD(0);
				writeD(0);
			}
			
			writeQ(letter.getAttachedKinah());
			writeQ(0);
			writeC(letter.getLetterType().getId());
		}
	}
	
	/**
	 * Sends a specific mail message to the client.<br>
	 * This method writes the {@code messageId} to the packet buffer.
	 * @param messageId The unique identifier for the mail message.
	 */
	protected void writeMailMessage(int messageId)
	{
		writeC(messageId);
	}
	
	/**
	 * This method writes the current state of the player's mailbox to the network packet.<br>
	 * It updates various counts for letters and special items.
	 * @param totalCount The total number of letters in the mailbox.
	 * @param unreadCount The number of unread letters.
	 * @param expressCount The count of express mail items.
	 * @param blackCloudCount The count of black cloud items.
	 */
	protected void writeMailboxState(int totalCount, int unreadCount, int expressCount, int blackCloudCount)
	{
		writeH(totalCount);
		writeH(unreadCount);
		writeH(expressCount);
		writeH(blackCloudCount);
	}
	
	/**
	 * Writes the data for a specific read letter to the network buffer.<br>
	 * This method includes details about the sender, title, message, and any attached items.<br>
	 * It also updates the mailbox status counts.
	 * @param letter The {@code Letter} object containing the mail content.
	 * @param time The timestamp associated with the letter.
	 * @param totalCount The total number of letters in the mailbox.
	 * @param unreadCount The number of unread letters.
	 * @param expressCount The count of express letters.
	 * @param blackCloudCount The count of black cloud letters.
	 */
	protected void writeLetterRead(Letter letter, long time, int totalCount, int unreadCount, int expressCount, int blackCloudCount)
	{
		writeD(letter.getRecipientId());
		writeD(totalCount + (unreadCount * 0x10000)); // total count + unread hex
		writeD(expressCount + blackCloudCount); // unread express + BC letters count
		writeD(letter.getObjectId());
		writeD(letter.getRecipientId());
		writeS(letter.getSenderName());
		writeS(letter.getTitle());
		writeS(letter.getMessage());
		
		final Item item = letter.getAttachedItem();
		if (item != null)
		{
			final ItemTemplate itemTemplate = item.getItemTemplate();
			
			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeQ((int) letter.getAttachedItem().getItemCount());
			writeH(0x24); // unk
			writeNameId(itemTemplate.getNameId());
			
			final ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());
			
		}
		else
		{
			writeD(0);
			writeD(0);
			writeQ(0);
			writeD(0);
		}
		
		writeD((int) letter.getAttachedKinah());
		writeD(0); // AP reward for castle assault/defense (in future)
		writeH(0);
		writeB(new byte[7]);
		writeD((int) (time / 1000));
		writeC(letter.getLetterType().getId()); // mail type
	}
	
	/**
	 * This method writes the state of a specific letter to the network packet.<br>
	 * It sends the {@code letterId} and the {@code attachmentType}.<br>
	 * It also sets a constant value of {@code 1} for the status.
	 * @param letterId The unique identifier of the letter.
	 * @param attachmentType The type of attachment associated with the letter.
	 */
	protected void writeLetterState(int letterId, int attachmentType)
	{
		writeD(letterId);
		writeC(attachmentType);
		writeC(1);
	}
	
	/**
	 * Sends a packet to delete specific letters from the mailbox.<br>
	 * This method updates the counts for total, unread, express, and black cloud letters.<br>
	 * It also includes an array of IDs for the letters being removed.
	 * @param totalCount The total number of letters in the mailbox.
	 * @param unreadCount The number of unread letters.
	 * @param expressCount The number of express letters.
	 * @param blackCloudCount The number of black cloud letters.
	 * @param letterIds An array of IDs for the letters to be deleted.
	 */
	protected void writeLetterDelete(int totalCount, int unreadCount, int expressCount, int blackCloudCount, int... letterIds)
	{
		writeD(totalCount + (unreadCount * 0x10000)); // total count + unread hex
		writeD(expressCount + blackCloudCount); // unread express + BC letters count
		writeH(letterIds.length);
		for (int letterId : letterIds)
		{
			writeD(letterId);
		}
	}
}
