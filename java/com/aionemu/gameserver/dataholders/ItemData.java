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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.restriction.ItemCleanupTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data container for all {@link ItemTemplate} objects.<br>
 * It handles the loading and management of item definitions from XML files.<br>
 * Use this class to access global item properties throughout the game server.
 * @author Luno
 * @Reworked GiGatR00n (Aion-Core)
 * @Reworked Kill3r (Aion-Core)
 * @Rework FrozenKiller
 */
@XmlRootElement(name = "item_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData extends ReloadableData
{
	@XmlElement(name = "item_template")
	private List<ItemTemplate> its;
	@XmlTransient
	private TIntObjectHashMap<ItemTemplate> items;
	@XmlTransient
	Map<Integer, List<ItemTemplate>> manastones = new HashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code items} map using the list of {@link ItemTemplate} objects.<br>
	 * The {@code its} list is set to {@code null} after the map is built.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		items = new TIntObjectHashMap<>();
		for (ItemTemplate it : its)
		{
			items.put(it.getTemplateId(), it);
		}
		
		its = null;
	}
	
	/**
	 * Cleans up item properties based on the defined rules.<br>
	 * This method iterates through all {@link ItemCleanupTemplate} entries.<br>
	 * It updates trade, sell, and warehouse permissions for each template.
	 */
	public void cleanup()
	{
		for (ItemCleanupTemplate ict : DataManager.ITEM_CLEAN_UP.getList())
		{
			final ItemTemplate template = items.get(ict.getId());
			applyCleanup(template, ict.resultTrade(), ItemMask.TRADEABLE);
			applyCleanup(template, ict.resultSell(), ItemMask.SELLABLE);
			applyCleanup(template, ict.resultWH(), ItemMask.STORABLE_IN_WH);
			applyCleanup(template, ict.resultAccountWH(), ItemMask.STORABLE_IN_AWH);
			applyCleanup(template, ict.resultLegionWH(), ItemMask.STORABLE_IN_LWH);
		}
	}
	
	/**
	 * This method updates the item mask based on a specific result.<br>
	 * It modifies the {@code ItemTemplate} using the provided {@code mask}.<br>
	 * The operation only occurs if {@code result} is not -1.
	 * @param item The {@link ItemTemplate} to be modified.
	 * @param result The status code determining the modification type.
	 * @param mask The bitmask used for the update.
	 */
	private void applyCleanup(ItemTemplate item, byte result, int mask)
	{
		if (result != -1)
		{
			switch (result)
			{
				case 1:
					item.modifyMask(true, mask);
					break;
				case 0:
					item.modifyMask(false, mask);
					break;
			}
		}
	}
	
	/**
	 * Retrieves an {@link ItemTemplate} based on its unique ID.<br>
	 * This method looks up the item in the internal data map.
	 * @param itemId The unique identifier for the item to find.
	 * @return The {@code ItemTemplate} associated with the given ID, or {@code null} if not found.
	 */
	public ItemTemplate getItemTemplate(int itemId)
	{
		return items.get(itemId);
	}
	
	/**
	 * Returns the number of elements in this set.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return items.size();
	}
	
	/**
	 * Retrieves the collection of manastones from the data. <br>
	 * Each entry maps an ID to a list of {@link ItemTemplate} objects.
	 * @return A {@code Map} where keys are IDs and values are lists of {@link ItemTemplate}.
	 */
	public Map<Integer, List<ItemTemplate>> getManastones()
	{
		return manastones;
	}
	
	// public ItemTemplate getPetEggTemplate(int petId) {
	// return petEggs.get(petId);
	// }
	
	/**
	 * Reloads the item template data from the XML file.<br>
	 * This method updates {@code ITEM_DATA} with new values.<br>
	 * It sends a success or failure message to the administrator.
	 * @param admin The {@code Player} who triggered the reload.
	 */
	@Override
	public void reload(Player admin)
	{
		try
		{
			final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			final Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema("./data/static_data/static_data.xsd"));
			final List<ItemTemplate> newTemplates = new ArrayList<>();
			final ItemData data = (ItemData) un.unmarshal(new File("./data/static_data/items/item_templates.xml"));
			if ((data != null) && (data.getData() != null))
			{
				newTemplates.addAll(data.getData());
			}
			
			DataManager.ITEM_DATA.setData(newTemplates);
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(admin, "Item templates reload failed!");
			log.error("Item templates reload failed!", e);
		}
		finally
		{
			PacketSendUtility.sendMessage(admin, "Item templates reload Success! Total loaded: " + DataManager.ITEM_DATA.size());
		}
	}
	
	/**
	 * Retrieves the list of all loaded item templates.<br>
	 * This method returns the internal {@code its} collection.
	 * @return a {@code List} containing all {@link ItemTemplate} objects.
	 */
	@Override
	protected List<ItemTemplate> getData()
	{
		return its;
	}
	
	/**
	 * Updates the internal list of {@code ItemTemplate} objects.<br>
	 * This method sets the {@code its} field using the provided {@code data}.<br>
	 * It also triggers the {@code Object)} method.
	 * @param data The list of templates to be stored in this object.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setData(List<?> data)
	{
		its = (List<ItemTemplate>) data;
		afterUnmarshal(null, null);
	}
	
	/**
	 * Retrieves the internal map of all item templates.<br>
	 * This map uses {@code int} IDs as keys to store {@link ItemTemplate} objects.
	 * @return a {@code TIntObjectHashMap} containing all loaded items.
	 */
	public TIntObjectHashMap<ItemTemplate> getItemData()
	{
		return items;
	}
}
