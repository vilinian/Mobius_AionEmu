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
import java.util.Calendar;
import java.util.Comparator;

import com.aionemu.gameserver.model.broker.BrokerRace;

/**
 * Represents an item listed in the broker system.<br>
 * This class stores all necessary data for a trade offer, such as the {@code BrokerRace} and item details.
 * @author kosyachok
 */
public class BrokerItem implements Comparable<BrokerItem>
{
	private final Item item;
	private final int itemId;
	private final int itemUniqueId;
	private long itemCount;
	private String itemCreator;
	private long price;
	private final String seller;
	private final int sellerId;
	private final BrokerRace itemBrokerRace;
	private boolean isSold, isCanceled;
	private boolean isSettled;
	private final Timestamp expireTime;
	private Timestamp settleTime;
	PersistentState state;
	private final boolean partSale;
	
	/**
	 * Creates a new {@link BrokerItem} instance for the auction system.<br>
	 * This constructor initializes all required fields and sets the initial state to {@code NEW}.<br>
	 * The expiration time is automatically set to 8 days from the current time.
	 * @param item The {@link Item} being sold.
	 * @param price The cost of the item in currency.
	 * @param seller The name of the player selling the item.
	 * @param sellerId The unique identifier for the seller.
	 * @param itemBrokerRace The race category for the broker listing.
	 * @param partSale Indicates if this is a partial sale of an item.
	 */
	public BrokerItem(Item item, long price, String seller, int sellerId, BrokerRace itemBrokerRace, boolean partSale)
	{
		this.item = item;
		itemId = item.getItemTemplate().getTemplateId();
		itemUniqueId = item.getObjectId();
		itemCount = item.getItemCount();
		itemCreator = item.getItemCreator();
		this.price = price;
		this.seller = seller;
		this.sellerId = sellerId;
		this.itemBrokerRace = itemBrokerRace;
		isSold = false;
		isSettled = false;
		expireTime = new Timestamp(Calendar.getInstance().getTimeInMillis() + 691200000); // 8 days
		settleTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		this.partSale = partSale;
		
		state = PersistentState.NEW;
	}
	
	/**
	 * Creates a new instance of {@link BrokerItem}.<br>
	 * This constructor initializes all fields for an item listed in the broker.<br>
	 * If the {@code item} is {@code null}, it defaults {@code isSold} and {@code isSettled} to {@code true}.
	 * @param item The {@link Item} being sold.
	 * @param itemId The unique identifier for the item type.
	 * @param itemUniqueId The specific instance ID of the item.
	 * @param itemCount The quantity of the item available.
	 * @param itemCreator The name of the player who created the item.
	 * @param price The cost of the item in currency.
	 * @param seller The name of the player selling the item.
	 * @param sellerId The unique ID of the seller.
	 * @param itemBrokerRace The race category for the broker listing.
	 * @param isSold Whether the item has been sold.
	 * @param isSettled Whether the transaction has been finalized.
	 * @param expireTime The timestamp when the listing expires.
	 * @param settleTime The timestamp when the settlement occurred.
	 * @param partSale Whether this is a
	 */
	public BrokerItem(Item item, int itemId, int itemUniqueId, long itemCount, String itemCreator, long price, String seller, int sellerId, BrokerRace itemBrokerRace, boolean isSold, boolean isSettled, Timestamp expireTime, Timestamp settleTime, boolean partSale)
	{
		this.item = item;
		this.itemId = itemId;
		this.itemUniqueId = itemUniqueId;
		this.itemCount = itemCount;
		this.itemCreator = itemCreator;
		this.price = price;
		this.seller = seller;
		this.sellerId = sellerId;
		this.itemBrokerRace = itemBrokerRace;
		
		this.partSale = partSale;
		
		if (item == null)
		{
			this.isSold = true;
			this.isSettled = true;
			
		}
		else
		{
			this.isSold = isSold;
			this.isSettled = isSettled;
		}
		
		this.expireTime = expireTime;
		this.settleTime = settleTime;
		
		state = PersistentState.NOACTION;
	}
	
	/**
	 * Retrieves the name of the person who created the item.<br>
	 * If no creator is defined, it returns an empty {@code String}.
	 * @return The name of the item creator or an empty string.
	 */
	public String getItemCreator()
	{
		if (itemCreator == null)
		{
			return "";
		}
		
		return itemCreator;
	}
	
	/**
	 * Checks if this item is listed as a partial sale.<br>
	 * This indicates whether the item can be sold in parts.
	 * @return {@code true} if it is a part sale, {@code false} otherwise.
	 */
	public boolean isPartSale()
	{
		return partSale;
	}
	
	/**
	 * Sets the name of the person who created the item.<br>
	 * This updates the {@code itemCreator} field in this {@link BrokerItem} instance.
	 * @param itemCreator The name of the creator as a {@code String}.
	 */
	public void setItemCreator(String itemCreator)
	{
		this.itemCreator = itemCreator;
	}
	
	/**
	 * Retrieves the {@code Item} associated with this broker entry.<br>
	 * This method returns the underlying object stored in the field.
	 * @return the {@link Item} object.
	 */
	public Item getItem()
	{
		return item;
	}
	
	/**
	 * Checks if the broker item has been canceled.<br>
	 * This method returns the current status of the cancellation flag.
	 * @return {@code true} if the item was canceled, {@code false} otherwise.
	 */
	public boolean isCanceled()
	{
		return isCanceled;
	}
	
	/**
	 * Updates the cancellation status of this broker item.<br>
	 * Sets the {@code isCanceled} field to the provided value.
	 * @param isCanceled The new cancellation status to set. Use {@code true} if the item is canceled and {@code false} otherwise.
	 */
	public void setIsCanceled(boolean isCanceled)
	{
		this.isCanceled = isCanceled;
	}
	
	/**
	 * Removes the item from the active broker list.<br>
	 * This method marks the item as sold and settled.<br>
	 * It also updates the {@code settleTime} to the current time.
	 */
	public void removeItem()
	{
		// this.item = null;
		isSold = true;
		isSettled = true;
		settleTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	/**
	 * Retrieves the unique identifier for this wardrobe item.<br>
	 * This value corresponds to the {@code itemId} assigned during object creation.
	 * @return The unique {@code int} ID of the item.
	 */
	public int getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the unique identifier for this specific item.<br>
	 * This ID distinguishes this item from others of the same type.
	 * @return The {@code int} value representing the unique ID.
	 */
	public int getItemUniqueId()
	{
		return itemUniqueId;
	}
	
	/**
	 * Retrieves the current price of the item.<br>
	 * This value represents the cost in the game currency.
	 * @return the {@code long} price of the item.
	 */
	public long getPrice()
	{
		return price;
	}
	
	/**
	 * Updates the price of this {@link BrokerItem}.<br>
	 * This method sets the new value for the item's cost.
	 * @param price The new price to assign to the item.
	 */
	public void setPrice(long price)
	{
		this.price = price;
	}
	
	/**
	 * Retrieves the name of the seller.<br>
	 * This value was provided when the {@link BrokerItem} was created.
	 * @return The {@code String} name of the seller.
	 */
	public String getSeller()
	{
		return seller;
	}
	
	/**
	 * Retrieves the unique identifier of the seller.<br>
	 * This ID is used to identify which player listed the item.
	 * @return the {@code int} value representing the seller's ID.
	 */
	public int getSellerId()
	{
		return sellerId;
	}
	
	/**
	 * Retrieves the {@code BrokerRace} associated with this item.<br>
	 * This represents the specific race category for the broker listing.
	 * @return the {@code BrokerRace} of the item.
	 */
	public BrokerRace getItemBrokerRace()
	{
		return itemBrokerRace;
	}
	
	/**
	 * Checks if the item has been sold.<br>
	 * Returns {@code true} if the sale is complete.<br>
	 * Returns {@code false} otherwise.
	 * @return The current sold status of the item.
	 */
	public boolean isSold()
	{
		return isSold;
	}
	
	/**
	 * Updates the {@code persistentState} of this item.<br>
	 * This method prevents changing from {@code PersistentState.NEW} to {@code PersistentState.UPDATE_REQUIRED}.
	 * @param persistentState The new {@link PersistentState} to assign.
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch (persistentState)
		{
			case DELETED:
				if (state == PersistentState.NEW)
				{
					state = PersistentState.NOACTION;
				}
				else
				{
					state = PersistentState.DELETED;
				}
				break;
			case UPDATE_REQUIRED:
				if (state == PersistentState.NEW)
				{
					break;
				}
			default:
				state = persistentState;
		}
		
	}
	
	/**
	 * Retrieves the current persistent state of this item.<br>
	 * This information is saved between game sessions.
	 * @return the {@link PersistentState} object.
	 */
	public PersistentState getPersistentState()
	{
		return state;
	}
	
	/**
	 * Checks if the item has been settled.<br>
	 * This indicates whether the transaction process is complete.
	 * @return {@code true} if the item is settled, {@code false} otherwise.
	 */
	public boolean isSettled()
	{
		return isSettled;
	}
	
	/**
	 * Marks this item as settled.<br>
	 * Sets the {@code isSettled} flag to {@code true}.<br>
	 * Records the current system time into the {@code settleTime} field.
	 */
	public void setSettled()
	{
		isSettled = true;
		settleTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	/**
	 * Retrieves the expiration time of the broker item.<br>
	 * This value indicates when the listing will no longer be active.
	 * @return the {@code Timestamp} representing the expiration date and time.
	 */
	public Timestamp getExpireTime()
	{
		return expireTime;
	}
	
	/**
	 * Retrieves the time when the item was settled.<br>
	 * This value represents the completion timestamp of the transaction.
	 * @return the {@code Timestamp} of the settlement, or {@code null} if not set.
	 */
	public Timestamp getSettleTime()
	{
		return settleTime;
	}
	
	/**
	 * Retrieves the total number of items for this broker entry.<br>
	 * This value represents the quantity of the {@link Item}.
	 * @return The current count of items as a {@code long}.
	 */
	public long getItemCount()
	{
		return itemCount;
	}
	
	/**
	 * Updates the total number of items for this {@link BrokerItem}.<br>
	 * This method sets the internal {@code itemCount} field.
	 * @param itemCount The new quantity of items to set.
	 */
	public void setItemCount(long itemCount)
	{
		this.itemCount = itemCount;
	}
	
	/**
	 * Retrieves the level of the current item.<br>
	 * This method calls {@code getItemTemplate} to find the base data.<br>
	 * It then returns the level value from that template.
	 * @return The integer level of the item.
	 */
	private int getItemLevel()
	{
		return item.getItemTemplate().getLevel();
	}
	
	/**
	 * Calculates the price for a single unit of the item.<br>
	 * This method divides the total {@code price} by the {@code itemCount}.
	 * @return The calculated price per piece as a {@code long}.
	 */
	private long getPiecePrice()
	{
		return getPrice() / getItemCount();
	}
	
	/**
	 * Retrieves the display name of the item.<br>
	 * This method calls {@code getItemName} to get the value.
	 * @return The name of the item as a {@code String}.
	 */
	private String getItemName()
	{
		return item.getItemName();
	}
	
	/**
	 * Compares this {@code BrokerItem} with another {@code BrokerItem}.<br>
	 * It determines the order based on the unique identifier.
	 * @param o The other item to compare against.
	 * @return A positive integer if this item has a higher ID, or -1 otherwise.
	 */
	@Override
	public int compareTo(BrokerItem o)
	{
		return itemUniqueId > o.getItemUniqueId() ? 1 : -1;
	}
	
	/**
	 * Sorting using price of item
	 */
	static Comparator<BrokerItem> NAME_SORT_ASC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		return o1.getItemName().compareTo(o2.getItemName());
	};
	static Comparator<BrokerItem> NAME_SORT_DESC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		return o1.getItemName().compareTo(o2.getItemName());
	};
	/**
	 * Sorting using price of item
	 */
	static Comparator<BrokerItem> PRICE_SORT_ASC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getPrice() == o2.getPrice())
		{
			return 0;
		}
		
		return o1.getPrice() > o2.getPrice() ? 1 : -1;
	};
	static Comparator<BrokerItem> PRICE_SORT_DESC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getPrice() == o2.getPrice())
		{
			return 0;
		}
		
		return o1.getPrice() > o2.getPrice() ? -1 : 1;
	};
	/**
	 * Sorting using piece price of item
	 */
	static Comparator<BrokerItem> PIECE_PRICE_SORT_ASC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getPiecePrice() == o2.getPiecePrice())
		{
			return 0;
		}
		
		return o1.getPiecePrice() > o2.getPiecePrice() ? 1 : -1;
	};
	static Comparator<BrokerItem> PIECE_PRICE_SORT_DESC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getPiecePrice() == o2.getPiecePrice())
		{
			return 0;
		}
		
		return o1.getPiecePrice() > o2.getPiecePrice() ? -1 : 1;
	};
	/**
	 * Sorting using level of item
	 */
	static Comparator<BrokerItem> LEVEL_SORT_ASC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getItemLevel() == o2.getItemLevel())
		{
			return 0;
		}
		
		return o1.getItemLevel() > o2.getItemLevel() ? 1 : -1;
	};
	static Comparator<BrokerItem> LEVEL_SORT_DESC = (o1, o2) ->
	{
		if ((o1 == null) || (o2 == null))
		{
			return comparePossiblyNull(o1, o2);
		}
		
		if (o1.getItemLevel() == o2.getItemLevel())
		{
			return 0;
		}
		
		return o1.getItemLevel() > o2.getItemLevel() ? -1 : 1;
	};
	
	/**
	 * Compares two objects while handling {@code null} values.<br>
	 * This method treats {@code null} as less than any non-null object.<br>
	 * It returns a value based on the presence of {@code null}.
	 * @param <T> The type of objects being compared.
	 * @param aThis The first object to compare.
	 * @param aThat The second object to compare.
	 * @return A negative integer if {@code aThis} is {@code null}, a positive integer if {@code aThat} is {@code null}, or zero otherwise.
	 */
	private static <T extends Comparable<T>> int comparePossiblyNull(T aThis, T aThat)
	{
		int result = 0;
		if ((aThis == null) && (aThat != null))
		{
			result = -1;
		}
		else if ((aThis != null) && (aThat == null))
		{
			result = 1;
		}
		
		return result;
	}
	
	/**
	 * Creates a {@link Comparator} to sort {@link BrokerItem} objects.<br>
	 * The sorting logic depends on the provided integer value.
	 * @param sortType The numeric ID representing the desired sort order.
	 * @return A {@code Comparator} based on the specified {@code sortType}.
	 */
	public static Comparator<BrokerItem> getComparatoryByType(int sortType)
	{
		switch (sortType)
		{
			case 0:
				return NAME_SORT_ASC;
			case 1:
				return NAME_SORT_DESC;
			case 2:
				return LEVEL_SORT_ASC;
			case 3:
				return LEVEL_SORT_DESC;
			case 4:
				return PRICE_SORT_ASC;
			case 5:
				return PRICE_SORT_DESC;
			case 6:
				return PIECE_PRICE_SORT_ASC;
			case 7:
				return PIECE_PRICE_SORT_DESC;
			default:
				throw new IllegalArgumentException("Illegal sort type for broker items");
		}
	}
}
