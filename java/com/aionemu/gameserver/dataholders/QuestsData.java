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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This class serves as a data holder for all quest information within the game.<br>
 * It manages the collection of {@link QuestTemplate} objects loaded from configuration files.<br>
 * It provides easy access to quest data for the {@link QuestService}.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quests")
public class QuestsData
{
	@XmlElement(name = "quest", required = true)
	protected List<QuestTemplate> questsData;
	private final TIntObjectHashMap<QuestTemplate> questData = new TIntObjectHashMap<>();
	private final TIntObjectHashMap<List<QuestTemplate>> sortedByFactionId = new TIntObjectHashMap<>();
	
	/**
	 * This method is called after the XML data has been unmarshalled.<br>
	 * It populates the {@code questData} and {@code sortedByFactionId} maps using the list of {@link QuestTemplate} objects.<br>
	 * The maps are cleared before being rebuilt.
	 * @param u The {@link Unmarshaller} used to read the data.
	 * @param parent The parent object of the current element.
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		questData.clear();
		sortedByFactionId.clear();
		for (QuestTemplate quest : questsData)
		{
			questData.put(quest.getId(), quest);
			final int npcFactionId = quest.getNpcFactionId();
			if (npcFactionId == 0)
			{
				continue;
			}
			
			if (!sortedByFactionId.containsKey(npcFactionId))
			{
				final List<QuestTemplate> factionQuests = new ArrayList<>();
				factionQuests.add(quest);
				sortedByFactionId.put(npcFactionId, factionQuests);
			}
			else
			{
				sortedByFactionId.get(npcFactionId).add(quest);
			}
		}
	}
	
	/**
	 * Retrieves a specific quest template from the data map.<br>
	 * This method uses the unique identifier provided to find the matching object.
	 * @param id The unique integer ID of the quest to retrieve.
	 * @return The {@link QuestTemplate} associated with the given {@code id}, or {@code null} if not found.
	 */
	public QuestTemplate getQuestById(int id)
	{
		return questData.get(id);
	}
	
	/**
	 * Retrieves a list of available quests for a specific NPC faction.<br>
	 * This method filters quests based on the {@code player} level and start conditions.<br>
	 * It checks if the quest has a valid handler via {@link QuestEngine}.
	 * @param npcFactionId The unique identifier for the NPC faction.
	 * @param player The {@code Player} object used to check requirements.
	 * @return A {@code List} of {@code QuestTemplate} objects that the player can start.
	 */
	public List<QuestTemplate> getQuestsByNpcFaction(int npcFactionId, Player player)
	{
		final List<QuestTemplate> factionQuests = sortedByFactionId.get(npcFactionId);
		final List<QuestTemplate> quests = new ArrayList<>();
		final QuestEnv questEnv = new QuestEnv(null, player, 0, 0);
		for (QuestTemplate questTemplate : factionQuests)
		{
			if (!QuestEngine.getInstance().isHaveHandler(questTemplate.getId()) || ((questTemplate.getMinlevelPermitted() != 0) && (player.getLevel() < questTemplate.getMinlevelPermitted())))
			{
				continue;
			}
			
			questEnv.setQuestId(questTemplate.getId());
			if (QuestService.checkStartConditions(questEnv, false))
			{
				quests.add(questTemplate);
			}
		}
		
		return quests;
	}
	
	/**
	 * Returns the total number of quests.<br>
	 * This method calls {@code size} to get the count.
	 * @return The total number of items currently stored in the collection.
	 */
	public int size()
	{
		return questData.size();
	}
	
	/**
	 * Retrieves the full list of quest templates.<br>
	 * This method returns all data loaded into {@code questsData}.
	 * @return A {@code List} containing all {@link QuestTemplate} objects.
	 */
	public List<QuestTemplate> getQuestsData()
	{
		return questsData;
	}
	
	/**
	 * Updates the internal list of quest templates.<br>
	 * This method also triggers the {@code Object)} process.
	 * @param questsData The new list of {@code QuestTemplate} objects to load.
	 */
	public void setQuestsData(List<QuestTemplate> questsData)
	{
		this.questsData = questsData;
		afterUnmarshal(null, null);
	}
}
