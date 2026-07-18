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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

/**
 * This class manages the loot tables for non-player characters.<br>
 * It maps {@link NpcTemplate} objects to their corresponding {@link NpcDrop} data.<br>
 * Use this class to retrieve drop information when an NPC is defeated.
 * @author MrPoke
 */
public class NpcDropData
{
	private static Logger log = LoggerFactory.getLogger(DataManager.class);
	private List<NpcDrop> npcDrop;
	
	/**
	 * Retrieves the list of all NPC drops.<br>
	 * This method returns the internal {@code npcDrop} collection.
	 * @return a {@code List} containing {@link NpcDrop} objects.
	 */
	public List<NpcDrop> getNpcDrop()
	{
		return npcDrop;
	}
	
	/**
	 * Sets the list of drops for NPCs.<br>
	 * This method updates the {@code npcDrop} field with a new {@code List} of {@link NpcDrop} objects.
	 * @param npcDrop The list of NPC drops to assign.
	 */
	public void setNpcDrop(List<NpcDrop> npcDrop)
	{
		this.npcDrop = npcDrop;
	}
	
	/**
	 * Returns the number of {@code NpcDrop} elements in the list.<br>
	 * This method calls {@code getNpcDrop} to determine the count.
	 * @return The total number of drops associated with this data object.
	 */
	public int size()
	{
		return npcDrop.size();
	}
	
	/**
	 * Loads the NPC drop data from the binary file located at {@code data/static_data/npc_drops/npc_drop.dat}.<br>
	 * This method parses drops, names, and groups to populate the {@link NpcDropData} object.<br>
	 * It also links these drops to their corresponding templates in {@code NPC_DATA}.
	 * @return a new {@code NpcDropData} instance containing all loaded NPC drop information.
	 */
	@SuppressWarnings("resource")
	public static NpcDropData load()
	{
		List<Drop> drops = new ArrayList<>();
		List<String> names = new ArrayList<>();
		final List<NpcDrop> npcDrops = new ArrayList<>();
		FileChannel roChannel = null;
		HashMap<Integer, ArrayList<DropGroup>> xmlGroup = DataManager.XML_NPC_DROP_DATA.getDrops();
		try
		{
			final RandomAccessFile channel = new RandomAccessFile("data/static_data/npc_drops/npc_drop.dat", "r");
			roChannel = channel.getChannel();
			final int size = (int) roChannel.size();
			final MappedByteBuffer buffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0L, size).load();
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			for (int count = buffer.getInt(), i = 0; i < count; ++i)
			{
				drops.add(Drop.load(buffer));
			}
			
			for (int count = buffer.getInt(), i = 0; i < count; ++i)
			{
				final int lenght = buffer.get();
				final byte[] byteString = new byte[lenght];
				buffer.get(byteString);
				final String name = new String(byteString);
				names.add(name);
			}
			
			for (int count = buffer.getInt(), i = 0; i < count; ++i)
			{
				final int npcId = buffer.getInt();
				final int groupCount = buffer.getInt();
				final List<DropGroup> dropGroupList = new ArrayList<>(groupCount);
				for (int groupIndex = 0; groupIndex < groupCount; ++groupIndex)
				{
					final byte raceId = buffer.get();
					Race race = null;
					switch (raceId)
					{
						case 0:
						{
							race = Race.ELYOS;
							break;
						}
						case 1:
						{
							race = Race.ASMODIANS;
							break;
						}
						default:
						{
							race = Race.PC_ALL;
							break;
						}
					}
					
					final boolean useCategory = buffer.get() == 1;
					final String groupName = names.get(buffer.getShort());
					final int dropCount = buffer.getInt();
					final List<Drop> dropList = new ArrayList<>();
					for (int dropIndex = 0; dropIndex < dropCount; ++dropIndex)
					{
						dropList.add(drops.get(buffer.getInt()));
					}
					
					final DropGroup dropGroup = new DropGroup(dropList, race, useCategory, groupName);
					dropGroupList.add(dropGroup);
				}
				
				if (xmlGroup.get(npcId) != null)
				{
					dropGroupList.addAll(xmlGroup.get(npcId));
					xmlGroup.remove(npcId);
				}
				
				final NpcDrop npcDrop = new NpcDrop(dropGroupList, npcId);
				npcDrops.add(npcDrop);
				final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
				if (npcTemplate != null)
				{
					npcTemplate.setNpcDrop(npcDrop);
				}
			}
			
			if (!xmlGroup.isEmpty())
			{
				for (Map.Entry<Integer, ArrayList<DropGroup>> entry : xmlGroup.entrySet())
				{
					final NpcDrop npcDrop2 = new NpcDrop(entry.getValue(), entry.getKey());
					npcDrops.add(npcDrop2);
					final NpcTemplate npcTemplate2 = DataManager.NPC_DATA.getNpcTemplate(entry.getKey());
					if (npcTemplate2 != null)
					{
						npcTemplate2.setNpcDrop(npcDrop2);
					}
				}
			}
			
			drops.clear();
			drops = null;
			names.clear();
			names = null;
			xmlGroup.clear();
			xmlGroup = null;
			DataManager.XML_NPC_DROP_DATA.clear();
		}
		catch (FileNotFoundException e)
		{
			NpcDropData.log.error("Drop loader: Missing npc_drop.dat!!!");
		}
		catch (IOException e2)
		{
			NpcDropData.log.error("Drop loader: IO error in drop Loading.");
		}
		finally
		{
			try
			{
				if (roChannel != null)
				{
					roChannel.close();
				}
			}
			catch (IOException e3)
			{
				NpcDropData.log.error("Drop loader: IO error in drop Loading.");
			}
		}
		
		final NpcDropData dropData = new NpcDropData();
		
		// NpcDropData.log.info("Drop loader: Npc drops loading done.");
		dropData.setNpcDrop(npcDrops);
		return dropData;
	}
	
	/**
	 * Reloads the NPC drop data from the configuration files.<br>
	 * This method clears existing drops and calls {@code load} to refresh them.
	 */
	public static void reload()
	{
		final TIntObjectHashMap<NpcTemplate> npcData = DataManager.NPC_DATA.getNpcData();
		npcData.forEachValue(new TObjectProcedure<NpcTemplate>()
		{
			@Override
			public boolean execute(NpcTemplate npcTemplate)
			{
				npcTemplate.setNpcDrop(null);
				return false;
			}
		});
		load();
	}
}
