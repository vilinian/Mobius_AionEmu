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
package com.aionemu.gameserver.model.house;

import java.sql.Timestamp;

/**
 * Represents a bid placed by a player on a house.<br>
 * This class stores the bidding details and allows for comparison between different bids.
 * @author Rolandas
 */
public class PlayerHouseBid implements Comparable<PlayerHouseBid>
{
	private final int playerId;
	private final int houseId;
	private final long offer;
	private final Timestamp time;
	
	/**
	 * Creates a new {@link PlayerHouseBid} object.<br>
	 * This constructor initializes all fields for a specific bid.
	 * @param playerId The unique identifier of the player making the bid.
	 * @param houseId The unique identifier of the house being bid on.
	 * @param offer The amount of currency offered by the player.
	 * @param time The exact date and time when the bid was placed.
	 */
	public PlayerHouseBid(int playerId, int houseId, long offer, Timestamp time)
	{
		this.playerId = playerId;
		this.houseId = houseId;
		this.offer = offer;
		this.time = time;
	}
	
	/**
	 * Retrieves the unique identifier for the player.<br>
	 * This value is stored in the {@code playerId} field.
	 * @return The {@code int} ID of the player.
	 */
	public int getPlayerId()
	{
		return playerId;
	}
	
	/**
	 * Retrieves the unique identifier for the house. <br>
	 * This value is assigned when a {@link PlayerHouseBid} is created.
	 * @return The {@code int} ID of the house.
	 */
	public int getHouseId()
	{
		return houseId;
	}
	
	/**
	 * Retrieves the bid amount for this house.<br>
	 * This value represents the total price offered by the player.
	 * @return The {@code long} value of the bid offer.
	 */
	public long getBidOffer()
	{
		return offer;
	}
	
	/**
	 * Retrieves the current bonus time.<br>
	 * This method returns the {@code Timestamp} value stored in this object.
	 * @return The {@code Timestamp} representing the bonus time.
	 */
	public Timestamp getTime()
	{
		return time;
	}
	
	/**
	 * Compares this bid with another {@link PlayerHouseBid} based on their timestamps.<br>
	 * This method determines which bid occurred earlier or later.
	 * @param o The other {@code PlayerHouseBid} to compare against.
	 * @return A negative integer if this bid is older, zero if they are the same, and a positive integer if this bid is newer.
	 */
	@Override
	public int compareTo(PlayerHouseBid o)
	{
		return (int) (time.getTime() - o.getTime().getTime());
	}
}
