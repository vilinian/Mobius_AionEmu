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

import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.templates.housing.HouseType;

/**
 * Represents an individual entry in a house auction system.<br>
 * It stores the bidding details for a specific {@link HouseType}.
 * @author Rolandas
 */
public class HouseBidEntry implements Cloneable
{
	private int entryIndex;
	private int landId;
	private int address;
	private int buildingId;
	private HouseType houseType;
	private long bidPrice;
	private final long unk2 = 100000;
	private int bidCount;
	private int mapId;
	private int lastBiddingPlayer;
	private long lastBidTime;
	
	/**
	 * Creates a new {@link HouseBidEntry} for a specific house.<br>
	 * This constructor initializes the bid details using data from a {@code House}.<br>
	 * It sets the starting price and maps the location information.
	 * @param house The {@code House} object containing the property details.
	 * @param index The unique position of this entry in the bidding list.
	 * @param initialBid The starting amount for the bid.
	 */
	public HouseBidEntry(House house, int index, long initialBid)
	{
		entryIndex = index;
		landId = house.getLand().getId();
		address = house.getAddress().getId();
		mapId = house.getAddress().getMapId();
		buildingId = house.getBuilding().getId();
		houseType = house.getHouseType();
		bidPrice = initialBid;
		lastBiddingPlayer = 0;
		lastBidTime = 0;
	}
	
	/**
	 * Private constructor for the {@link HouseBidEntry} class.<br>
	 * This prevents direct instantiation of this object from outside the class.
	 */
	private HouseBidEntry()
	{
	}
	
	/**
	 * Retrieves the unique index of this bid entry.<br>
	 * This value identifies the position within a specific bidding list.
	 * @return The {@code int} representing the entry index.
	 */
	public int getEntryIndex()
	{
		return entryIndex;
	}
	
	/**
	 * Updates the unique index of this bid entry.<br>
	 * This value is used to identify the position in a list of bids.
	 * @param entryIndex The new {@code int} value for the entry index.
	 */
	public void setEntryIndex(int entryIndex)
	{
		this.entryIndex = entryIndex;
	}
	
	/**
	 * Retrieves the unique identifier for the land.<br>
	 * This value is used to identify which specific plot of land this entry belongs to.
	 * @return The {@code int} ID of the land.
	 */
	public int getLandId()
	{
		return landId;
	}
	
	/**
	 * Retrieves the unique address of the house bid entry.<br>
	 * This value is used to identify the specific location in the game world.
	 * @return The {@code int} representing the house address.
	 */
	public int getAddress()
	{
		return address;
	}
	
	/**
	 * Retrieves the unique identifier for the building.<br>
	 * This value is used to identify a specific structure in the game world.
	 * @return The {@code int} ID of the building.
	 */
	public int getBuildingId()
	{
		return buildingId;
	}
	
	/**
	 * Sets the unique identifier for the building.<br>
	 * This updates the {@code buildingId} field of the current object.
	 * @param buildingId The new ID to assign to the building.
	 */
	public void setBuildingId(int buildingId)
	{
		this.buildingId = buildingId;
	}
	
	/**
	 * Retrieves the current price of the bid.<br>
	 * This value represents the amount offered for a house.
	 * @return The current {@code bidPrice}.
	 */
	public long getBidPrice()
	{
		return bidPrice;
	}
	
	/**
	 * Updates the current bidding price for this house entry.<br>
	 * This method sets the {@code bidPrice} field to a new value.
	 * @param bidPrice The new price to set for the bid.
	 */
	public void setBidPrice(long bidPrice)
	{
		this.bidPrice = bidPrice;
	}
	
	/**
	 * Retrieves the total number of bids placed on this house.<br>
	 * This value is updated whenever a new bid is made.
	 * @return The current count of bids as an {@code int}.
	 */
	public int getBidCount()
	{
		return bidCount;
	}
	
	/**
	 * Increases the total number of bids for this entry.<br>
	 * This method updates the {@code bidCount} field by adding 1.
	 */
	public void incrementBidCount()
	{
		bidCount++;
	}
	
	/**
	 * Retrieves the constant value for {@code unk2}.<br>
	 * This value is used internally by the house bidding system.
	 * @return The long value of {@code unk2}.
	 */
	public long getUnk2()
	{
		return unk2;
	}
	
	/**
	 * Retrieves the {@link HouseType} of this bid entry.<br>
	 * This returns the specific type assigned to the house.
	 * @return The {@code HouseType} associated with this entry.
	 */
	public HouseType getHouseType()
	{
		return houseType;
	}
	
	/**
	 * Retrieves the unique identifier for the map.<br>
	 * This value is assigned during the creation of the {@link AssembledNpc}.
	 * @return The {@code int} representing the map ID.
	 */
	public int getMapId()
	{
		return mapId;
	}
	
	/**
	 * Retrieves the ID of the player who placed the most recent bid.<br>
	 * This value is stored in the {@code lastBiddingPlayer} field.
	 * @return The unique identifier of the last bidding player.
	 */
	public int getLastBiddingPlayer()
	{
		return lastBiddingPlayer;
	}
	
	/**
	 * Updates the ID of the player who made the most recent bid.<br>
	 * This value is stored in the {@code lastBiddingPlayer} field.
	 * @param lastBiddingPlayer The unique identifier of the bidding player.
	 */
	public void setLastBiddingPlayer(int lastBiddingPlayer)
	{
		this.lastBiddingPlayer = lastBiddingPlayer;
	}
	
	/**
	 * Calculates the amount of Kinah to refund for a bid.<br>
	 * This value is based on the {@code bidPrice} and the percentage defined in {@link HousingConfig}.
	 * @return The calculated refund amount as a {@code long}.
	 */
	public long getRefundKinah()
	{
		return (long) (bidPrice * HousingConfig.BID_REFUND_PERCENT);
	}
	
	/**
	 * Retrieves the timestamp of the most recent bid.<br>
	 * This value is stored in the {@code lastBidTime} field.
	 * @return The time of the last bid as a {@code long}.
	 */
	public long getLastBidTime()
	{
		return lastBidTime;
	}
	
	/**
	 * Updates the timestamp of the most recent bid.<br>
	 * This value is stored in {@code lastBidTime}.
	 * @param lastBidTime The new time for the last bid.
	 */
	public void setLastBidTime(long lastBidTime)
	{
		this.lastBidTime = lastBidTime;
	}
	
	/**
	 * Creates a copy of the current {@code HouseBidEntry} object.<br>
	 * This method returns a new instance with the same field values.
	 * @return A new {@code HouseBidEntry} object.
	 */
	public Object Clone()
	{
		final HouseBidEntry cloned = new HouseBidEntry();
		cloned.address = address;
		cloned.bidCount = bidCount;
		cloned.bidPrice = bidPrice;
		cloned.buildingId = buildingId;
		cloned.entryIndex = entryIndex;
		cloned.houseType = houseType;
		cloned.landId = landId;
		cloned.mapId = mapId;
		cloned.lastBiddingPlayer = lastBiddingPlayer;
		return cloned;
	}
}
