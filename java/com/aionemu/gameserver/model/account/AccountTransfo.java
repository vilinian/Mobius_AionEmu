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
package com.aionemu.gameserver.model.account;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;

/**
 * This class manages the transformation data associated with a user account.<br>
 * It handles the mapping between accounts and their available {@link TransformBookTemplate} records.
 */
public class AccountTransfo
{
	private final TransformBookTemplate template;
	private final int cardId;
	private int count;
	
	/**
	 * Creates a new {@code AccountTransfo} object.<br>
	 * This method initializes the template using the provided {@code cardId}.<br>
	 * It also sets the initial quantity for the transformation.
	 * @param cardId The unique identifier for the card.
	 * @param count The number of items to be processed.
	 */
	public AccountTransfo(int cardId, int count)
	{
		template = DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(cardId);
		this.cardId = cardId;
		this.count = count;
	}
	
	/**
	 * Retrieves the {@link TransformBookTemplate} associated with this account.<br>
	 * This method returns the base data for the transformation book.
	 * @return The {@code TransformBookTemplate} object.
	 */
	public TransformBookTemplate getTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the unique identifier for the card.<br>
	 * This value is stored in the {@code cardId} field.
	 * @return The {@code int} ID of the card.
	 */
	public int getCardId()
	{
		return cardId;
	}
	
	/**
	 * Retrieves the current count value.<br>
	 * This method returns the integer stored in the {@code count} field.
	 * @return The current count as an {@code int}.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Updates the current count value.<br>
	 * This method sets the {@code count} field to a new integer.
	 * @param count The new value to assign to the count.
	 */
	public void setCount(int count)
	{
		this.count = count;
	}
}
