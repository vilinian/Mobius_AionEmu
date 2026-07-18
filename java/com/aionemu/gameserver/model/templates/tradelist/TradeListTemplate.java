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
package com.aionemu.gameserver.model.templates.tradelist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the data structure for a trade list template.<br>
 * It defines the configuration and properties used to initialize trade lists in the game world.
 * @author orz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tradelist_template")
public class TradeListTemplate
{
	/**
	 * Npc Id.
	 */
	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;
	@XmlAttribute(name = "npc_type")
	private TradeNpcType tradeNpcType = TradeNpcType.NORMAL;
	@XmlAttribute(name = "sell_price_rate")
	private int sellPriceRate = 100;
	@XmlAttribute(name = "buy_price_rate")
	private int buyPriceRate;
	@XmlElement(name = "tradelist")
	protected List<TradeTab> tradeTablist;
	
	/**
	 * Retrieves the list of {@code TradeTab} objects for this template.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@code TradeTab} objects.
	 */
	public List<TradeTab> getTradeTablist()
	{
		if (tradeTablist == null)
		{
			tradeTablist = new ArrayList<>();
		}
		
		return tradeTablist;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Gets the total number of items in the trade list.<br>
	 * This method returns the size of the {@code getTradeTablist} collection.
	 * @return The number of trade tabs as an {@code int}.
	 */
	public int getCount()
	{
		return tradeTablist.size();
	}
	
	/**
	 * Retrieves the type of NPC associated with this trade list.<br>
	 * This value determines how the NPC behaves during trading.
	 * @return The {@code TradeNpcType} of the NPC.
	 */
	public TradeNpcType getTradeNpcType()
	{
		return tradeNpcType;
	}
	
	/**
	 * Retrieves the current selling price rate.<br>
	 * This value is used to calculate how much an item sells for.
	 * @return The {@code int} value of the sell price rate.
	 */
	public int getSellPriceRate()
	{
		return sellPriceRate;
	}
	
	/**
	 * Retrieves the current purchase price rate.<br>
	 * This value determines how much players pay for items.
	 * @return The {@code int} value of the buy price rate.
	 */
	public int getBuyPriceRate()
	{
		return buyPriceRate;
	}
	
	/**
	 * <p/>
	 * Java class for anonymous complex type.
	 * <p/>
	 * The following schema fragment specifies the expected content contained within this class.
	 * <p/>
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "Tradelist")
	public static class TradeTab
	{
		@XmlAttribute
		protected int id;
		
		/**
		 * Gets the value of the id property.
		 * @return
		 */
		public int getId()
		{
			return id;
		}
	}
}
