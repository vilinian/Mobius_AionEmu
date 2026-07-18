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

import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a container for all {@link TradeListTemplate} instances.<br>
 * It manages the data used to define trade lists within the game world.
 * @author Luno
 */
@XmlRootElement(name = "npc_trade_list")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeListData
{
	@XmlElement(name = "tradelist_template")
	private List<TradeListTemplate> tlist;
	@XmlElement(name = "trade_in_list_template")
	private List<TradeListTemplate> tInlist;
	@XmlElement(name = "purchase_template")
	private List<TradeListTemplate> plist;
	/**
	 * A map containing all trade list templates
	 */
	private final TIntObjectHashMap<TradeListTemplate> npctlistData = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<TradeListTemplate> npcTradeInlistData = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<TradeListTemplate> npcPurchaseTemplateData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the internal maps using the lists of {@link TradeListTemplate}.<br>
	 * The maps are updated with data from {@code tlist}, {@code tInlist}, and {@code plist}.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (TradeListTemplate npc : tlist)
		{
			npctlistData.put(npc.getNpcId(), npc);
		}
		
		for (TradeListTemplate npc : tInlist)
		{
			npcTradeInlistData.put(npc.getNpcId(), npc);
		}
		
		for (TradeListTemplate npc : plist)
		{
			npcPurchaseTemplateData.put(npc.getNpcId(), npc);
		}
	}
	
	/**
	 * Returns the total number of teleporter templates stored in this container.<br>
	 * This method calls {@code size} to retrieve the count.
	 * @return The number of items currently held in the data map.
	 */
	public int size()
	{
		return npctlistData.size();
	}
	
	/**
	 * Retrieves a specific trade list template from the data map.<br>
	 * This method looks up the template using the provided unique identifier.
	 * @param id The unique integer ID of the trade list template to find.
	 * @return The {@link TradeListTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public TradeListTemplate getTradeListTemplate(int id)
	{
		return npctlistData.get(id);
	}
	
	/**
	 * Retrieves a specific trade-in list template from the data map.<br>
	 * This method looks up the template using the provided unique identifier.
	 * @param id The unique integer ID of the trade-in list.
	 * @return The {@link TradeListTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public TradeListTemplate getTradeInListTemplate(int id)
	{
		return npcTradeInlistData.get(id);
	}
	
	/**
	 * Retrieves a specific purchase template based on its unique identifier.<br>
	 * This method looks up the data in the {@code npcPurchaseTemplateData} map.
	 * @param id The unique integer ID of the purchase template to find.
	 * @return The {@link TradeListTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public TradeListTemplate getPurchaseTemplate(int id)
	{
		return npcPurchaseTemplateData.get(id);
	}
	
	/**
	 * Retrieves the map of all trade list templates.<br>
	 * This map uses {@code int} IDs as keys to store {@link TradeListTemplate} objects.
	 * @return a {@code TIntObjectHashMap} containing all trade list templates.
	 */
	public TIntObjectHashMap<TradeListTemplate> getTradeListTemplate()
	{
		return npctlistData;
	}
}
