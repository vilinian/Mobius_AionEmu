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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.goods.GoodsList;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for the collection of {@link com.aionemu.gameserver.model.templates.goods.GoodsList} objects.<br>
 * It is used to manage and store global goods data loaded from XML configuration files.
 * @author ATracer
 */
@XmlRootElement(name = "goodslists")
@XmlAccessorType(XmlAccessType.FIELD)
public class GoodsListData
{
	@XmlElement(required = true)
	protected List<GoodsList> list;
	@XmlElement(name = "in_list")
	protected List<GoodsList> inList;
	@XmlElement(name = "purchase_list")
	protected List<GoodsList> purchaseList;
	/**
	 * A map containing all goodslist templates
	 */
	private TIntObjectHashMap<GoodsList> goodsListData;
	private TIntObjectHashMap<GoodsList> goodsInListData;
	private TIntObjectHashMap<GoodsList> goodsPurchaseListData;
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the goods list maps using the provided lists.<br>
	 * The original lists are set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		goodsListData = new TIntObjectHashMap<>();
		for (GoodsList it : list)
		{
			goodsListData.put(it.getId(), it);
		}
		
		goodsInListData = new TIntObjectHashMap<>();
		for (GoodsList it : inList)
		{
			goodsInListData.put(it.getId(), it);
		}
		
		goodsPurchaseListData = new TIntObjectHashMap<>();
		for (GoodsList it : purchaseList)
		{
			goodsPurchaseListData.put(it.getId(), it);
		}
		
		list = null;
		inList = null;
		purchaseList = null;
	}
	
	/**
	 * Retrieves a specific {@link GoodsList} from the data map.<br>
	 * This method uses the provided unique identifier to find the correct entry.
	 * @param id The unique integer ID of the goods list to retrieve.
	 * @return The {@code GoodsList} associated with the given ID, or {@code null} if not found.
	 */
	public GoodsList getGoodsListById(int id)
	{
		return goodsListData.get(id);
	}
	
	/**
	 * Retrieves a specific {@link GoodsList} from the in-list collection.<br>
	 * This method uses the provided unique identifier to find the correct entry.
	 * @param id The unique integer ID of the goods list to retrieve.
	 * @return The {@code GoodsList} associated with the given ID, or {@code null} if not found.
	 */
	public GoodsList getGoodsInListById(int id)
	{
		return goodsInListData.get(id);
	}
	
	/**
	 * Retrieves a specific purchase list from the data map.<br>
	 * It uses the provided unique identifier to find the correct entry.
	 * @param id The unique ID of the purchase list to retrieve.
	 * @return The {@code GoodsList} associated with the given {@code id}, or {@code null} if not found.
	 */
	public GoodsList getGoodsPurchaseListById(int id)
	{
		return goodsPurchaseListData.get(id);
	}
	
	/**
	 * Returns the total number of items across all lists.<br>
	 * This method sums the sizes of {@code goodsListData}, {@code goodsInListData}, and {@code goodsPurchaseListData}.
	 * @return The combined count of all goods list templates.
	 */
	public int size()
	{
		return goodsListData.size() + goodsInListData.size() + goodsPurchaseListData.size();
	}
}
