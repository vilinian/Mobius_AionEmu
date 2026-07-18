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
package com.aionemu.gameserver.model.broker;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;

/**
 * This class manages a local cache of player data related to the broker system.<br>
 * It allows for efficient retrieval of {@link BrokerItem} information associated with players.
 * @author ATracer
 */
public class BrokerPlayerCache
{
	private BrokerItem[] brokerListCache = new BrokerItem[0];
	private int brokerMaskCache;
	private int brokerSoftTypeCache;
	private int brokerStartPageCache;
	private List<Integer> itemList = new ArrayList<>();
	
	/**
	 * Retrieves the current list of cached broker items.<br>
	 * This method returns the internal {@code BrokerItem[]} array.
	 * @return an array of {@link BrokerItem} objects.
	 */
	public BrokerItem[] getBrokerListCache()
	{
		return brokerListCache;
	}
	
	/**
	 * Updates the internal cache of {@code BrokerItem} objects.<br>
	 * This method stores the provided array into the local cache.
	 * @param brokerListCache The array of {@code BrokerItem} to store.
	 */
	public void setBrokerListCache(BrokerItem[] brokerListCache)
	{
		this.brokerListCache = brokerListCache;
	}
	
	/**
	 * Retrieves the current value of the {@code brokerMaskCache}.<br>
	 * This value is used to filter items in the broker list.
	 * @return the integer value of the {@code brokerMaskCache}.
	 */
	public int getBrokerMaskCache()
	{
		return brokerMaskCache;
	}
	
	/**
	 * Updates the internal cache for the broker mask.<br>
	 * This value is used to filter items in the broker list.
	 * @param brokerMaskCache The new integer value for the mask cache.
	 */
	public void setBrokerMaskCache(int brokerMaskCache)
	{
		this.brokerMaskCache = brokerMaskCache;
	}
	
	/**
	 * Retrieves the cached value for the broker sort type.<br>
	 * This value is used to determine how items are ordered in the list.
	 * @return the current {@code int} value of the {@code brokerSoftTypeCache}.
	 */
	public int getBrokerSortTypeCache()
	{
		return brokerSoftTypeCache;
	}
	
	/**
	 * Updates the cached value for the broker soft type.<br>
	 * This method sets the internal {@code brokerSoftTypeCache} field.
	 * @param brokerSoftTypeCache The new integer value to store in the cache.
	 */
	public void setBrokerSortTypeCache(int brokerSoftTypeCache)
	{
		this.brokerSoftTypeCache = brokerSoftTypeCache;
	}
	
	/**
	 * Retrieves the current starting page for the broker list.<br>
	 * This value is used to determine which page of items to display first.
	 * @return the {@code int} value representing the start page.
	 */
	public int getBrokerStartPageCache()
	{
		return brokerStartPageCache;
	}
	
	/**
	 * Retrieves the list of items currently being searched.<br>
	 * This method returns the {@code itemList} if it is not {@code null}.
	 * @return a {@code List<Integer>} containing the search item IDs, or {@code null} if the list is empty.
	 */
	public List<Integer> getSearchItemList()
	{
		if (itemList == null)
		{
			return null;
		}
		
		return itemList;
	}
	
	/**
	 * Sets the starting page for the broker list cache.<br>
	 * This value determines which page is loaded first when a user views items.
	 * @param brokerStartPageCache The initial page number to store in {@code brokerStartPageCache}.
	 */
	public void setBrokerStartPageCache(int brokerStartPageCache)
	{
		this.brokerStartPageCache = brokerStartPageCache;
	}
	
	/**
	 * Updates the list of items used for searching.<br>
	 * This method sets the {@code itemList} field to the provided value.
	 * @param itemList The new {@code List<Integer>} to store.
	 */
	public void setSearchItemsList(List<Integer> itemList)
	{
		this.itemList = itemList;
	}
}
