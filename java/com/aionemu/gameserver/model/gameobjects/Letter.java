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
package com.aionemu.gameserver.model.gameobjects;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.Petition;

/**
 * Represents a mail or letter object within the game world.<br>
 * This class handles the data and properties associated with player correspondence.<br>
 * It extends {@link AionObject} to integrate with the core game object system.
 * @author kosyachok
 */
public class Letter extends AionObject
{
	private final int recipientId;
	private Item attachedItem;
	private long attachedKinahCount;
	private final String senderName;
	private final String title;
	private final String message;
	private boolean unread;
	private boolean express;
	private final Timestamp timeStamp;
	private PersistentState persistentState;
	private LetterType letterType;
	
	/**
	 * Creates a new {@link Letter} object.<br>
	 * This constructor initializes all fields including the sender, recipient, and content.<br>
	 * It automatically determines if the letter is express based on the {@code letterType}.
	 * @param objId The unique identifier for the object.
	 * @param recipientId The ID of the player receiving the letter.
	 * @param attachedItem The {@link Item} included with this letter, or {@code null}.
	 * @param attachedKinahCount The amount of Kinah currency attached to the letter.
	 * @param title The subject line of the letter.
	 * @param message The main body text of the letter.
	 * @param senderName The name of the player who sent the letter.
	 * @param timeStamp The date and time when the letter was created.
	 * @param unread A boolean indicating if the letter has been read, where {@code true} means it is new.
	 * @param letterType The category of the letter, such as express or blackcloud.
	 */
	public Letter(int objId, int recipientId, Item attachedItem, long attachedKinahCount, String title, String message, String senderName, Timestamp timeStamp, boolean unread, LetterType letterType)
	{
		super(objId);
		
		if ((letterType == LetterType.EXPRESS) || (letterType == LetterType.BLACKCLOUD))
		{
			express = true;
		}
		else
		{
			express = false;
		}
		
		this.recipientId = recipientId;
		this.attachedItem = attachedItem;
		this.attachedKinahCount = attachedKinahCount;
		this.title = title;
		this.message = message;
		this.senderName = senderName;
		this.timeStamp = timeStamp;
		this.unread = unread;
		persistentState = PersistentState.NEW;
		this.letterType = letterType;
	}
	
	/**
	 * Gets the name of the item attached to this letter.<br>
	 * This method retrieves the {@code NameId} from the {@link Item} object.
	 * @return The name of the attached item as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return String.valueOf(attachedItem.getItemTemplate().getNameId());
	}
	
	/**
	 * Retrieves the unique identifier for the letter recipient.<br>
	 * This value is stored in the {@code recipientId} field.
	 * @return The {@code int} ID of the recipient.
	 */
	public int getRecipientId()
	{
		return recipientId;
	}
	
	/**
	 * Retrieves the {@code Item} associated with this letter.<br>
	 * Returns {@code null} if no item is attached.
	 * @return The {@code Item} object attached to the letter.
	 */
	public Item getAttachedItem()
	{
		return attachedItem;
	}
	
	/**
	 * Retrieves the amount of {@code Kinah} attached to this letter.<br>
	 * This value represents the currency amount sent with the message.
	 * @return The number of {@code Kinah} attached to the letter as a {@code long}.
	 */
	public long getAttachedKinah()
	{
		return attachedKinahCount;
	}
	
	/**
	 * Retrieves the title of the {@link Petition}.<br>
	 * This returns the name given to the petition.
	 * @return The {@code String} representing the title.
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Retrieves the current message associated with this {@code FindGroup} object.<br>
	 * This method returns the string value stored in the {@code message} field.
	 * @return The message as a {@code String}.
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Retrieves the name of the person who sent this {@link Letter}.<br>
	 * This method returns the value stored in the {@code senderName} field.
	 * @return The name of the sender as a {@code String}.
	 */
	public String getSenderName()
	{
		return senderName;
	}
	
	/**
	 * Retrieves the type of the current {@link Letter}.<br>
	 * This helps identify what category this specific letter belongs to.
	 * @return the {@code LetterType} of this object.
	 */
	public LetterType getLetterType()
	{
		return letterType;
	}
	
	/**
	 * Checks if the letter has been read.<br>
	 * Returns {@code true} if it is still new.<br>
	 * Returns {@code false} if it has already been opened.
	 * @return The status of the unread flag.
	 */
	public boolean isUnread()
	{
		return unread;
	}
	
	/**
	 * Marks the letter as read.<br>
	 * This method sets the {@code unread} status to {@code false}.<br>
	 * It also updates the {@link PersistentState} to {@code UPDATE_REQUIRED}.
	 */
	public void setReadLetter()
	{
		unread = false;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Checks if the letter is an express delivery.<br>
	 * This method returns {@code true} if it is marked as express.<br>
	 * It returns {@code false} otherwise.
	 * @return The status of the express delivery.
	 */
	public boolean isExpress()
	{
		return express;
	}
	
	/**
	 * Sets whether this letter is an express delivery.<br>
	 * This method updates the {@code express} status.<br>
	 * It also marks the {@link PersistentState} as {@code UPDATE_REQUIRED}.
	 * @param express The new express status to set.
	 */
	public void setExpress(boolean express)
	{
		this.express = express;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Sets the {@code letterType} for this letter.<br>
	 * This method also updates the express status based on the type.
	 * @param letterType The new {@link LetterType} to assign.
	 */
	public void setLetterType(LetterType letterType)
	{
		this.letterType = letterType;
		if ((letterType == LetterType.EXPRESS) || (letterType == LetterType.BLACKCLOUD))
		{
			express = true;
		}
		else
		{
			express = false;
		}
	}
	
	/**
	 * Retrieves the current persistence state of the letter.<br>
	 * This is used to determine how the letter should be saved in the database.
	 * @return the {@code PersistentState} associated with this {@link Letter}.
	 */
	public PersistentState getLetterPersistentState()
	{
		return persistentState;
	}
	
	/**
	 * Removes the {@code Item} currently attached to this letter.<br>
	 * Sets the {@code persistentState} to {@code UPDATE_REQUIRED}.<br>
	 * This method clears the item reference but does not delete the item from the game.
	 */
	public void removeAttachedItem()
	{
		attachedItem = null;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Removes the amount of Kinah attached to this {@link Letter}.<br>
	 * It sets the {@code attachedKinahCount} to {@code 0}.<br>
	 * The {@code persistentState} is updated to {@code UPDATE_REQUIRED}.
	 */
	public void removeAttachedKinah()
	{
		attachedKinahCount = 0;
		persistentState = PersistentState.UPDATE_REQUIRED;
	}
	
	/**
	 * Marks this letter as deleted.<br>
	 * This updates the {@code persistentState} to {@code PersistentState.DELETED}.
	 */
	public void delete()
	{
		persistentState = PersistentState.DELETED;
	}
	
	/**
	 * Updates the persistent state of this {@link Letter}.<br>
	 * This method stores the provided {@code PersistentState} object.
	 * @param state The new {@code PersistentState} to assign.
	 */
	public void setPersistState(PersistentState state)
	{
		persistentState = state;
	}
	
	/**
	 * Retrieves the creation date of the letter.<br>
	 * This returns the {@code Timestamp} value stored in the object.
	 * @return The {@code Timestamp} of the letter.
	 */
	public Timestamp getTimeStamp()
	{
		return timeStamp;
	}
}
