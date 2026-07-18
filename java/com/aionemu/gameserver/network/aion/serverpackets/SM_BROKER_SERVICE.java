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

import java.sql.Timestamp;
import java.util.Calendar;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This packet handles the service requests for the broker system.<br>
 * It manages interactions between players and the broker's item services.
 * @author IlBuono, kosyachok
 */
public class SM_BROKER_SERVICE extends AionServerPacket
{
	private enum BrokerPacketType
	{
		SEARCHED_ITEMS(0),
		REGISTERED_ITEMS(1),
		REGISTER_ITEM(3),
		SHOW_SETTLED_ICON(5),
		SETTLED_ITEMS(5),
		REMOVE_SETTLED_ICON(6),
		ADD_ITEM_WINDOW(7);
		
		private final int id;
		
		private BrokerPacketType(int id)
		{
			this.id = id;
		}
		
		private int getId()
		{
			return id;
		}
	}
	
	private final BrokerPacketType type;
	private BrokerItem[] brokerItems;
	private int itemsCount;
	private int startPage;
	private int message;
	private long settled_kinah;
	private long averagePrice;
	private long maxPrice;
	private long minPrice;
	private int itemObjectId;
	private boolean lowHighSame;
	
	/**
	 * Creates a packet to register a single item in the broker.<br>
	 * This constructor sets the {@code type} to {@code REGISTER_ITEM}.
	 * @param brokerItem The {@link BrokerItem} object to be registered.
	 * @param message A specific message identifier for this action.
	 * @param itemsCount The total count of items involved in this operation.
	 */
	public SM_BROKER_SERVICE(BrokerItem brokerItem, int message, int itemsCount)
	{
		type = BrokerPacketType.REGISTER_ITEM;
		brokerItems = new BrokerItem[]
		{
			brokerItem
		};
		this.message = message;
		this.itemsCount = itemsCount;
	}
	
	/**
	 * Initializes a new packet for registering an item.<br>
	 * This constructor sets the {@code type} to {@code REGISTER_ITEM}.<br>
	 * It also stores the provided message value.
	 * @param message The message content associated with this registration.
	 */
	public SM_BROKER_SERVICE(int message)
	{
		type = BrokerPacketType.REGISTER_ITEM;
		this.message = message;
	}
	
	/**
	 * Initializes a packet to display registered items in the broker.<br>
	 * This constructor sets the {@code type} to {@code REGISTERED_ITEMS}.<br>
	 * It stores the provided array of {@link BrokerItem} objects.
	 * @param brokerItems The array of items to be displayed in the broker service.
	 */
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems)
	{
		type = BrokerPacketType.REGISTERED_ITEMS;
		this.brokerItems = brokerItems;
	}
	
	/**
	 * This constructor initializes a packet for showing settled items.<br>
	 * It sets the {@code BrokerPacketType} to {@code SETTLED_ITEMS}.
	 * @param brokerItems An array of {@link BrokerItem} objects to be displayed.
	 * @param settled_kinah The total amount of kinah settled for these items.
	 */
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, long settled_kinah)
	{
		type = BrokerPacketType.SETTLED_ITEMS;
		this.brokerItems = brokerItems;
		this.settled_kinah = settled_kinah;
	}
	
	/**
	 * Initializes a packet for displaying searched items in the broker.<br>
	 * This constructor sets the {@code BrokerPacketType} to {@code SEARCHED_ITEMS}.
	 * @param brokerItems The array of {@link BrokerItem} objects to be sent.
	 * @param itemsCount The total number of items found.
	 * @param startPage The page index where the list begins.
	 */
	public SM_BROKER_SERVICE(BrokerItem[] brokerItems, int itemsCount, int startPage)
	{
		type = BrokerPacketType.SEARCHED_ITEMS;
		this.brokerItems = brokerItems;
		this.itemsCount = itemsCount;
		this.startPage = startPage;
	}
	
	/**
	 * Initializes a packet to show or remove the settled icon.<br>
	 * This constructor sets the {@code BrokerPacketType} based on the visibility flag.<br>
	 * It also stores the amount of settled kinah.
	 * @param showSettledIcon Set to {@code true} to show the icon, or {@code false} to remove it.
	 * @param settled_kinah The amount of kinah that has been settled.
	 */
	public SM_BROKER_SERVICE(boolean showSettledIcon, long settled_kinah)
	{
		type = showSettledIcon ? BrokerPacketType.SHOW_SETTLED_ICON : BrokerPacketType.REMOVE_SETTLED_ICON;
		this.settled_kinah = settled_kinah;
	}
	
	/**
	 * Initializes a new {@code SM_BROKER_SERVICE} packet for adding an item window.<br>
	 * This constructor sets the price details and status for a specific item object.
	 * @param itemObjectId The unique identifier of the item object.
	 * @param averagePrice The calculated average price of the item.
	 * @param maxPrice The highest recorded price for the item.
	 * @param minPrice The lowest recorded price for the item.
	 * @param lowHighSame A boolean flag indicating if the minimum and maximum prices are equal.
	 */
	public SM_BROKER_SERVICE(int itemObjectId, long averagePrice, long maxPrice, long minPrice, boolean lowHighSame)
	{
		type = BrokerPacketType.ADD_ITEM_WINDOW;
		this.itemObjectId = itemObjectId;
		this.averagePrice = averagePrice;
		this.maxPrice = maxPrice;
		this.minPrice = minPrice;
		this.lowHighSame = lowHighSame;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		switch (type)
		{
			case SEARCHED_ITEMS:
				writeSearchedItems();
				break;
			case REGISTERED_ITEMS:
				writeRegisteredItems();
				break;
			case REGISTER_ITEM:
				writeRegisterItem();
				break;
			case SHOW_SETTLED_ICON:
				writeShowSettledIcon();
				break;
			case REMOVE_SETTLED_ICON:
				writeRemoveSettledIcon();
				break;
			case SETTLED_ITEMS:
				writeShowSettledItems();
				break;
			case ADD_ITEM_WINDOW:
				writeAddItemWindow();
				break;
		}
		
	}
	
	/**
	 * Writes the data for the add item window to the packet.<br>
	 * This method handles the serialization of price details and object IDs.
	 */
	private void writeAddItemWindow()
	{
		writeC(type.getId());
		writeC(0); // unk
		writeD(itemObjectId);
		writeQ(averagePrice);
		writeC(lowHighSame ? 2 : 0);
		writeQ(minPrice);
		writeQ(maxPrice);
	}
	
	/**
	 * Writes the searched items data to the network connection.<br>
	 * This method sends the {@code type}, {@code itemsCount}, and {@code startPage}.<br>
	 * It then loops through the {@code brokerItems} array and calls {@code writeItemInfo} for each one.
	 */
	private void writeSearchedItems()
	{
		writeC(type.getId());
		writeD(itemsCount);
		writeC(0);
		writeH(startPage);
		writeH(brokerItems.length);
		for (BrokerItem item : brokerItems)
		{
			writeItemInfo(item);
		}
	}
	
	/**
	 * Writes the registered items to the network packet.<br>
	 * This method iterates through the {@code brokerItems} array.<br>
	 * It calls {@code writeRegisteredItemInfo} for each item.
	 */
	private void writeRegisteredItems()
	{
		writeC(type.getId());
		writeD(0x00);
		writeH(brokerItems.length); // you can register a max of 15 items, so 0x0F
		for (BrokerItem brokerItem : brokerItems)
		{
			writeRegisteredItemInfo(brokerItem);
		}
	}
	
	/**
	 * Writes the data for a single registered item to the network stream.<br>
	 * This method handles different logic based on the {@code message} value.<br>
	 * It uses {@code writeRegisteredItemInfo} when {@code message} is 0.
	 */
	private void writeRegisterItem()
	{
		writeC(type.getId());
		writeC(message);
		if (message == 0)
		{
			writeC(itemsCount + 1);
			final BrokerItem itemForRegistration = brokerItems[0];
			writeRegisteredItemInfo(itemForRegistration);
		}
		else
		{
			writeB(new byte[107]);
		}
	}
	
	/**
	 * Writes the packet data for showing a settled icon.<br>
	 * This method handles the specific byte sequence for the {@code SHOW_SETTLED_ICON} type.<br>
	 * It uses the {@code settled_kinah} value from the class fields.
	 */
	private void writeShowSettledIcon()
	{
		writeC(type.getId());
		writeQ(settled_kinah);
		writeD(0x00);
		writeH(0x00);
		writeH(0x01);
		writeC(0x00);
	}
	
	/**
	 * This method writes the packet data to remove a settled icon.<br>
	 * It sends the {@code BrokerPacketType#REMOVE_SETTLED_ICON} ID.<br>
	 * It also writes a default value of {@code 0}.
	 */
	private void writeRemoveSettledIcon()
	{
		writeC(type.getId());
		writeC(0);
	}
	
	/**
	 * Writes the settled items data to the network buffer.<br>
	 * This method iterates through the {@code brokerItems} array.<br>
	 * It includes details like item IDs, prices, and counts.<br>
	 * It also handles special blob entries for mana sockets.
	 */
	private void writeShowSettledItems()
	{
		writeC(type.getId());
		writeQ(settled_kinah);
		writeH(brokerItems.length);
		writeD(0x00);
		writeC(0x00);
		writeH(brokerItems.length);
		for (BrokerItem settledItem : brokerItems)
		{
			writeD(settledItem.getItemId());
			if (settledItem.isSold())
			{
				writeQ(settledItem.getPrice());
			}
			else
			{
				writeQ(0);
			}
			
			writeQ(settledItem.getItemCount());
			writeQ(settledItem.getItemCount());
			writeD((int) ((settledItem.getSettleTime().getTime() / 1000) / 60));
			
			// TODO! thats really odd - looks like getItem() may return null...
			final Item item = settledItem.getItem();
			if (item != null)
			{
				ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
			}
			else
			{
				writeB(new byte[231]);
			}
			
			writeS(settledItem.getItemCreator());
			
			// long test = (brokerItems.length <= 9 ? ((long) Math.round(settledItem.getPrice() * 0.02f)) : ((long) Math.round(settledItem.getPrice() * 0.04f)));
			writeQ(0); // TODO Kinah - fee
		}
	}
	
	/**
	 * Writes the details of a specific {@link BrokerItem} to the packet buffer.<br>
	 * This method handles item IDs, prices, counts, and expiration times.<br>
	 * It also includes special data like mana sockets and premium options.
	 * @param brokerItem The {@code BrokerItem} object containing the information to write.
	 */
	private void writeRegisteredItemInfo(BrokerItem brokerItem)
	{
		final Item item = brokerItem.getItem();
		
		writeD(brokerItem.getItemUniqueId());
		writeD(brokerItem.getItemId());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());
		writeQ(item.getItemCount());
		final Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
		final int daysLeft = (int) ((brokerItem.getExpireTime().getTime() - currentTime.getTime()) / 86400000);
		writeC(daysLeft);
		
		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
		
		writeS(brokerItem.getItemCreator());
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.POLISH_INFO, null, item).writeThisBlob(getBuf());
		writeC(0);
		writeC(brokerItem.isPartSale() ? 1 : 0);
		writeQ(brokerItem.getItemCount() <= 9 ? ((long) Math.round(brokerItem.getPrice() * 0.022f)) : ((long) Math.round(brokerItem.getPrice() * 0.044f)));
	}
	
	/**
	 * Writes the details of a specific {@code BrokerItem} to the packet buffer.<br>
	 * This method handles the object ID, price, count, and various item blobs.<br>
	 * It also records seller information and part sale status.
	 * @param brokerItem The {@code BrokerItem} object containing the data to be written.
	 */
	private void writeItemInfo(BrokerItem brokerItem)
	{
		final Item item = brokerItem.getItem();
		
		writeD(item.getObjectId());
		writeD(item.getItemTemplate().getTemplateId());
		writeQ(brokerItem.getPrice());
		writeQ(brokerItem.getPrice());
		writeQ(item.getItemCount());
		
		ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
		
		writeS(brokerItem.getSeller());
		writeS(brokerItem.getItemCreator()); // creator
		ItemInfoBlob.newBlobEntry(ItemBlobType.PREMIUM_OPTION, null, item).writeThisBlob(getBuf());
		ItemInfoBlob.newBlobEntry(ItemBlobType.POLISH_INFO, null, item).writeThisBlob(getBuf());
		writeC(0);
		writeC(brokerItem.isPartSale() ? 1 : 0);
	}
}
