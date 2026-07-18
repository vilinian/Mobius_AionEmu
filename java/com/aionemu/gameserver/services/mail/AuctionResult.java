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
 * Represents the possible outcomes of an auction process.<br>
 * This enum is used by {@link com.aionemu.gameserver.services.mail.MailService} to determine the status of a completed auction.<br>
 * It helps identify if an auction was successful, failed, or resulted in a specific state like {@code EXPIRED}.
 * @author Rolandas
 */
public enum AuctionResult
{
	FAILED_BID(0),
	CANCELED_BID(1),
	FAILED_SALE(2),
	SUCCESS_SALE(3),
	WIN_BID(4),
	GRACE_START(5),
	GRACE_FAIL(6),
	GRACE_SUCCESS(7);
	
	private final int value;
	
	/**
	 * Creates a new {@link AuctionResult} with a specific numeric code.<br>
	 * This constructor maps the internal integer to an enum constant.
	 * @param value The unique identifier for the auction result.
	 */
	AuctionResult(int value)
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
	
	/**
	 * Retrieves an {@link AuctionResult} based on its unique ID.<br>
	 * This method searches through all available values.<br>
	 * It returns {@code null} if no matching ID is found.
	 * @param resultId The integer ID of the auction result to find.
	 * @return The corresponding {@code AuctionResult} or {@code null}.
	 */
	public static AuctionResult getResultFromId(int resultId)
	{
		for (AuctionResult result : AuctionResult.values())
		{
			if (result.getId() == resultId)
			{
				return result;
			}
		}
		
		return null;
	}
}
