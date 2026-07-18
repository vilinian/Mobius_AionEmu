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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a container for all {@link NpcTemplate} instances.<br>
 * It stores the base data for NPC classes, which are uniquely identified by an npc id.<br>
 * Each {@link Npc} instance uses these templates to define shared properties like name, items, and statistics.
 * @author Luno
 */
@XmlRootElement(name = "npc_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcData extends ReloadableData
{
	@XmlElement(name = "npc_template")
	private List<NpcTemplate> npcs;
	/**
	 * A map containing all npc templates
	 */
	private final TIntObjectHashMap<NpcTemplate> npcData = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code npcData} map using the list of {@link NpcTemplate} objects.<br>
	 * The {@code npcs} list is cleared and set to {@code null} after processing.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for (NpcTemplate npc : npcs)
		{
			npcData.put(npc.getTemplateId(), npc);
		}
		
		npcs.clear();
		npcs = null;
	}
	
	/**
	 * Returns the total number of NPC templates stored in this container.<br>
	 * This method calls {@code size} to retrieve the count.
	 * @return The number of {@link NpcTemplate} objects currently available.
	 */
	public int size()
	{
		return npcData.size();
	}
	
	/**
	 * Retrieves a specific {@link NpcTemplate} from the data map.<br>
	 * It uses the provided unique identifier to find the correct template.
	 * @param id The unique integer ID of the NPC template.
	 * @return The {@link NpcTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public NpcTemplate getNpcTemplate(int id)
	{
		return npcData.get(id);
	}
	
	/**
	 * Retrieves the internal map of all NPC templates.<br>
	 * This map uses an integer ID as the key to look up {@link NpcTemplate} objects.
	 * @return A {@code TIntObjectHashMap} containing all loaded {@link NpcTemplate} data.
	 */
	public TIntObjectHashMap<NpcTemplate> getNpcData()
	{
		return npcData;
	}
	
	/**
	 * Reloads the NPC template data from the XML files.<br>
	 * This method updates {@code NPC_DATA} with new values.<br>
	 * It sends a success or failure message to the administrator.
	 * @param admin The {@code Player} who triggered the reload.
	 */
	@Override
	public void reload(Player admin)
	{
		final File dir = new File("./data/static_data/npcs");
		try
		{
			final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			final Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema("./data/static_data/static_data.xsd"));
			final List<NpcTemplate> newTemplates = new ArrayList<>();
			for (File file : listFiles(dir, true))
			{
				final NpcData data = (NpcData) un.unmarshal(file);
				if ((data != null) && (data.getData() != null))
				{
					newTemplates.addAll(data.getData());
				}
			}
			
			DataManager.NPC_DATA.setData(newTemplates);
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(admin, "Npc reload failed!");
			log.error("Npc reload failed!", e);
		}
		finally
		{
			PacketSendUtility.sendMessage(admin, "Npc reload Success! Total loaded: " + DataManager.NPC_DATA.size());
		}
	}
	
	/**
	 * Retrieves the list of all NPC templates.<br>
	 * This method returns the internal {@code npcs} collection.
	 * @return a {@code List} containing all {@link NpcTemplate} objects.
	 */
	@Override
	protected List<NpcTemplate> getData()
	{
		return npcs;
	}
	
	/**
	 * Updates the internal list of NPC templates.<br>
	 * This method sets the {@code npcs} field with the provided data.<br>
	 * It also triggers the {@code Object)} method to refresh the state.
	 * @param templates The list of templates to be loaded into the system.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void setData(List<?> templates)
	{
		npcs = (List<NpcTemplate>) templates;
		afterUnmarshal(null, null);
	}
}
