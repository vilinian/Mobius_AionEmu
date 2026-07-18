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
package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.WorkOrders;

/**
 * This class serves as a data model for storing work order information.<br>
 * It holds the necessary properties required by the {@link WorkOrders} template.<br>
 * Use this class to manage quest-related work order configurations.
 * @author Mr. Poke, reworked Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkOrdersData", propOrder =
{
	"giveComponent"
})
public class WorkOrdersData extends XMLQuest
{
	@XmlElement(name = "give_component", required = true)
	protected List<QuestItems> giveComponent;
	@XmlAttribute(name = "start_npc_ids", required = true)
	protected List<Integer> startNpcIds;
	@XmlAttribute(name = "recipe_id", required = true)
	protected int recipeId;
	
	/**
	 * Retrieves the list of items given to the player.<br>
	 * This method returns a live reference to the {@code giveComponent} list.<br>
	 * If the list is null, it initializes a new {@code ArrayList}.
	 * @return A {@code List} of {@link QuestItems} objects.
	 */
	public List<QuestItems> getGiveComponent()
	{
		if (giveComponent == null)
		{
			giveComponent = new ArrayList<>();
		}
		
		return giveComponent;
	}
	
	/**
	 * Retrieves the list of NPC identifiers for starting this work order.<br>
	 * These IDs correspond to the {@code start_npc_ids} attribute in the data.
	 * @return a {@code List<Integer>} containing the unique IDs of the NPCs.
	 */
	public List<Integer> getStartNpcIds()
	{
		return startNpcIds;
	}
	
	/**
	 * Retrieves the unique identifier for the crafting recipe.<br>
	 * This ID is used to identify which specific recipe this action belongs to.
	 * @return The {@code int} value of the {@code recipeid}.
	 */
	public int getRecipeId()
	{
		return recipeId;
	}
	
	/**
	 * Registers this data as a quest handler.<br>
	 * It creates a new {@link WorkOrders} instance.<br>
	 * The instance is then added to the provided {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		questEngine.addQuestHandler(new WorkOrders(this));
	}
}
