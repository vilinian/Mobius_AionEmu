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
package com.aionemu.gameserver.model.items.storage;

/**
 * Defines the different types of storage systems available in the game.<br>
 * This enum is used to categorize how items are stored and accessed by players.
 * @author kosyachok, IlBuono
 */
public enum StorageType
{
	// Cube & Warehouse
	CUBE(0, 27, 9, 162), // 4.9
	REGULAR_WAREHOUSE(1, 112, 8),
	ACCOUNT_WAREHOUSE(2, 16, 8),
	LEGION_WAREHOUSE(3, 80, 8),
	
	// Pet's Bag
	PET_BAG_6(32, 6, 6),
	PET_BAG_12(33, 12, 6),
	PET_BAG_18(34, 18, 6),
	PET_BAG_24(35, 24, 6),
	PET_BAG_28(44, 28, 6), // 5.1
	PET_BAG_30(40, 30, 6),
	
	// Cash Pet's Bag
	CASH_PET_BAG_12(36, 12, 6),
	CASH_PET_BAG_18(37, 18, 6),
	CASH_PET_BAG_30(38, 30, 6),
	CASH_PET_BAG_24(39, 24, 6),
	CASH_PET_BAG_26(41, 26, 6),
	CASH_PET_BAG_32(42, 32, 6),
	CASH_PET_BAG_34(43, 34, 6),
	
	// Housing
	HOUSE_STORAGE_01(60, 9, 9),
	HOUSE_STORAGE_02(61, 9, 9),
	HOUSE_STORAGE_03(62, 9, 9),
	HOUSE_STORAGE_04(63, 9, 9),
	HOUSE_STORAGE_05(64, 9, 9),
	HOUSE_STORAGE_06(65, 9, 9),
	HOUSE_STORAGE_07(66, 9, 9),
	HOUSE_STORAGE_08(67, 9, 9),
	HOUSE_STORAGE_09(68, 18, 9),
	HOUSE_STORAGE_10(69, 18, 9),
	HOUSE_STORAGE_11(70, 18, 9),
	HOUSE_STORAGE_12(71, 18, 9),
	HOUSE_STORAGE_14(73, 18, 9),
	HOUSE_STORAGE_16(75, 27, 9),
	HOUSE_STORAGE_18(77, 27, 9),
	HOUSE_STORAGE_20(79, 0, 0),
	
	// Other
	BROKER(126),
	MAILBOX(127);
	
	// Pet's
	public static final int PET_BAG_MIN = 32;
	public static final int PET_BAG_MAX = 44;
	
	// Housing
	public static final int HOUSE_WH_MIN = 60;
	public static final int HOUSE_WH_MAX = 79; // Custom cabinets ?? // since 3.0 to 4.0
	
	private final int id;
	private int limit;
	private int length;
	private int specialLimit;
	
	/**
	 * Initializes a new {@link StorageType} with all specific constraints.<br>
	 * This constructor sets the unique identifier and storage dimensions.<br>
	 * It also assigns a value to the {@code specialLimit} field.
	 * @param id The unique database identifier for the storage type.
	 * @param limit The maximum number of items allowed in this storage.
	 * @param length The horizontal size or length of the storage area.
	 * @param specialLimit An additional specific limit used for certain storage types.
	 */
	private StorageType(int id, int limit, int length, int specialLimit)
	{
		this(id, limit, length);
		this.specialLimit = specialLimit;
	}
	
	/**
	 * Initializes a new {@link StorageType} with specific dimensions.<br>
	 * This constructor sets the storage capacity and size limits.
	 * @param id The unique identifier for the storage type.
	 * @param limit The maximum number of items allowed in this storage.
	 * @param length The length dimension of the storage area.
	 */
	private StorageType(int id, int limit, int length)
	{
		this(id);
		this.limit = limit;
		this.length = length;
	}
	
	/**
	 * Initializes a new {@link StorageType} instance with a specific identifier.<br>
	 * This constructor is used to map the internal ID to the storage type.
	 * @param id The unique numerical identifier for the storage type.
	 */
	private StorageType(int id)
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
	
	/**
	 * Retrieves the maximum number of items allowed in this storage.<br>
	 * This value is used to determine if the storage is full.
	 * @return The current {@code int} limit.
	 */
	public int getLimit()
	{
		return limit;
	}
	
	/**
	 * Retrieves the length of the storage type.<br>
	 * This value represents the horizontal dimension of the storage area.
	 * @return The {@code int} length of the storage.
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * Retrieves the special storage limit for this {@code StorageType}.<br>
	 * This value is used to define specific capacity constraints.
	 * @return The integer value of the special limit.
	 */
	public int getSpecialLimit()
	{
		return specialLimit;
	}
	
	/**
	 * Finds a {@link StorageType} based on its unique identifier.<br>
	 * It searches through all available types to find a match for the provided {@code id}.
	 * @param id The unique integer ID of the storage type.
	 * @return The matching {@link StorageType} object, or {@code null} if no match is found.
	 */
	public static StorageType getStorageTypeById(int id)
	{
		for (StorageType st : values())
		{
			if (st.id == id)
			{
				return st;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the unique identifier for a storage type based on its dimensions.<br>
	 * It searches through all {@link StorageType} values to find a match.<br>
	 * Returns -1 if no matching storage is found.
	 * @param limit The maximum capacity of the storage.
	 * @param length The length dimension of the storage.
	 * @return The unique integer ID of the matching storage type or -1.
	 */
	public static int getStorageId(int limit, int length)
	{
		for (StorageType st : values())
		{
			if ((st.limit == limit) && (st.length == length))
			{
				return st.id;
			}
		}
		
		return -1;
	}
}
