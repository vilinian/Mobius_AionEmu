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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import com.aionemu.gameserver.services.mail.MailService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents a player's personal mailbox for receiving and managing letters.<br>
 * It handles the storage and retrieval of {@link Letter} objects associated with a user.
 * @author kosyachok
 * @author Atracer
 * @author Antraxx
 */
public class Mailbox
{
	private final Map<Integer, Letter> mails = new ConcurrentHashMap<>();
	private final Map<Integer, Letter> reserveMail = new ConcurrentHashMap<>();
	private final Player owner;
	public boolean isMailListUpdateRequired;
	
	// The status codes are 0x00 for closed, 0x01 for regular, and 0x02 for express.
	public byte mailBoxState = 0;
	
	/**
	 * Creates a new {@link Mailbox} instance for a specific player.<br>
	 * This method assigns the provided {@code Player} as the owner of the mailbox.
	 * @param player The {@code Player} who will own this mailbox.
	 */
	public Mailbox(Player player)
	{
		owner = player;
	}
	
	/**
	 * Adds a {@link Letter} to the mailbox.<br>
	 * This method checks if there are free slots available.<br>
	 * If space is full, it moves the letter to the reserve list.
	 * @param letter The {@code Letter} object to be stored.
	 */
	public void putLetterToMailbox(Letter letter)
	{
		if (haveFreeSlots())
		{
			mails.put(letter.getObjectId(), letter);
		}
		else
		{
			reserveMail.put(letter.getObjectId(), letter);
		}
	}
	
	/**
	 * Retrieves all letters currently in the mailbox.<br>
	 * The results are sorted by their timestamp in descending order.<br>
	 * If two letters have the same time, they are sorted by their object ID.
	 * @return A {@code Collection} of {@link Letter} objects.
	 */
	public Collection<Letter> getLetters()
	{
		final SortedSet<Letter> letters = new TreeSet<>(new Comparator<Letter>()
		{
			@Override
			public int compare(Letter o1, Letter o2)
			{
				if (o1.getTimeStamp().getTime() > o2.getTimeStamp().getTime())
				{
					return -1;
				}
				
				if (o1.getTimeStamp().getTime() < o2.getTimeStamp().getTime())
				{
					return 1;
				}
				
				return o1.getObjectId() > o2.getObjectId() ? 1 : -1;
			}
		});
		
		for (Letter letter : mails.values())
		{
			letters.add(letter);
		}
		
		return letters;
	}
	
	/**
	 * Retrieves a list of new system letters from the mailbox.<br>
	 * It filters for unread letters sent by system accounts.<br>
	 * The results are filtered based on the provided {@code substring}.
	 * @param substring The prefix to match against the sender name.
	 * @return A {@code List} of matching {@link Letter} objects.
	 */
	public List<Letter> getNewSystemLetters(String substring)
	{
		final List<Letter> letters = new ArrayList<>();
		for (Letter letter : mails.values())
		{
			if ((letter.getSenderName() == null) || !letter.isUnread() || (owner.getCommonData().getLastOnline().getTime() > letter.getTimeStamp().getTime()))
			{
				continue;
			}
			
			if (letter.getSenderName().startsWith("%") || letter.getSenderName().startsWith("$$"))
			{
				if (letter.getSenderName().startsWith(substring))
				{
					letters.add(letter);
				}
			}
		}
		
		return letters;
	}
	
	/**
	 * Retrieves a specific {@link Letter} from the mailbox.<br>
	 * It uses the unique identifier provided in the parameter.
	 * @param letterObjId The unique ID of the letter to retrieve.
	 * @return The {@code Letter} object if found, or {@code null} if it does not exist.
	 */
	public Letter getLetterFromMailbox(int letterObjId)
	{
		return mails.get(letterObjId);
	}
	
	/**
	 * Checks if there are any unread letters in the mailbox.<br>
	 * It iterates through all stored {@link Letter} objects.
	 * @return {@code true} if at least one letter is unread, otherwise {@code false}.
	 */
	public boolean haveUnread()
	{
		for (Letter letter : mails.values())
		{
			if (letter.isUnread())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the total number of unread letters in the mailbox.<br>
	 * It iterates through all stored {@link Letter} objects.
	 * @return The count of letters where {@code isUnread()} is true.
	 */
	public int getUnreadCount()
	{
		int unreadCount = 0;
		for (Letter letter : mails.values())
		{
			if (letter.isUnread())
			{
				unreadCount++;
			}
		}
		
		return unreadCount;
	}
	
	/**
	 * Checks if there are any unread letters of a specific type.<br>
	 * It searches through all letters in the mailbox.
	 * @param letterType The {@link LetterType} to check for unread messages.
	 * @return {@code true} if at least one unread letter of that type exists, otherwise {@code false}.
	 */
	public boolean haveUnreadByType(LetterType letterType)
	{
		for (Letter letter : mails.values())
		{
			if (letter.isUnread() && (letter.getLetterType() == letterType))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the number of unread letters for a specific type.<br>
	 * It iterates through all letters in the mailbox.<br>
	 * It checks if each letter is unread and matches the provided {@code letterType}.
	 * @param letterType The {@link LetterType} to filter by.
	 * @return The total count of unread letters of that type.
	 */
	public int getUnreadCountByType(LetterType letterType)
	{
		int count = 0;
		for (Letter letter : mails.values())
		{
			if (letter.isUnread() && (letter.getLetterType() == letterType))
			{
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Checks if the mailbox has any available space.<br>
	 * It returns {@code true} if there are fewer than {@code 100} letters.<br>
	 * It returns {@code false} if the mailbox is full.
	 * @return {@code true} if free slots exist, otherwise {@code false}.
	 */
	public boolean haveFreeSlots()
	{
		return mails.size() < 100;
	}
	
	/**
	 * Removes a specific letter from the mailbox.<br>
	 * This method deletes the letter associated with the provided {@code letterId}.<br>
	 * It also triggers an update for reserved letters.
	 * @param letterId The unique identifier of the letter to remove.
	 */
	public void removeLetter(int letterId)
	{
		mails.remove(letterId);
		uploadReserveLetters();
	}
	
	/**
	 * Returns the total number of letters in the mailbox.<br>
	 * This method returns the size of the internal {@code mails} map.
	 * @return The count of letters currently stored.
	 */
	public int size()
	{
		return mails.size();
	}
	
	/**
	 * This method moves letters from the reserve list to the main mailbox.<br>
	 * It checks if there are any free slots available before moving them.<br>
	 * It then triggers a mail refresh for the {@link Player} owner.
	 */
	public void uploadReserveLetters()
	{
		if ((reserveMail.size() > 0) && haveFreeSlots())
		{
			for (Letter letter : reserveMail.values())
			{
				if (haveFreeSlots())
				{
					mails.put(letter.getObjectId(), letter);
					reserveMail.remove(letter.getObjectId());
				}
				else
				{
					break;
				}
			}
			
			MailService.getInstance().refreshMail(getOwner());
		}
	}
	
	/**
	 * Sends the mail list to the player.<br>
	 * This method uses {@code sendPacket} to deliver the data.<br>
	 * It updates the UI with the current letters in the mailbox.
	 * @param expressOnly Set to {@code true} to show only express mail, or {@code false} to show all mail.
	 */
	public void sendMailList(boolean expressOnly)
	{
		PacketSendUtility.sendPacket(owner, new SM_MAIL_SERVICE(owner, getLetters(), expressOnly));
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	public Player getOwner()
	{
		return owner;
	}
}
