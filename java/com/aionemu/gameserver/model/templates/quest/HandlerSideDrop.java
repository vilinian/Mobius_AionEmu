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
package com.aionemu.gameserver.model.templates.quest;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.QuestTemplate;

/**
 * Represents a drop that occurs on the side of a quest objective.<br>
 * This class handles specific loot logic associated with {@link QuestTemplate} requirements.
 * @author vlog
 * @modified Rolandas
 */
public class HandlerSideDrop extends QuestDrop
{
	private final int neededAmount;
	
	/**
	 * Creates a new {@link HandlerSideDrop} instance.<br>
	 * This method initializes the drop data for a specific quest and NPC.<br>
	 * It also determines if the drop applies to each member of a group.
	 * @param questId The unique identifier for the quest.
	 * @param npcId The unique identifier for the NPC.
	 * @param itemId The unique identifier for the item being dropped.
	 * @param amount The quantity of the item to drop.
	 * @param chance The probability percentage for the drop to occur.
	 */
	public HandlerSideDrop(int questId, int npcId, int itemId, int amount, int chance)
	{
		this.questId = questId;
		this.npcId = npcId;
		this.itemId = itemId;
		this.chance = chance;
		
		final QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		for (QuestDrop drop : template.getQuestDrop())
		{
			if ((drop.npcId == npcId) && (drop.itemId == itemId))
			{
				dropEachMember = drop.dropEachMember;
				break;
			}
		}
		
		neededAmount = amount;
	}
	
	/**
	 * Creates a new {@link HandlerSideDrop} instance.<br>
	 * This constructor sets up the drop requirements for a specific quest step.
	 * @param questId The unique identifier for the quest.
	 * @param npcId The unique identifier for the NPC involved.
	 * @param itemId The unique identifier for the item to be dropped.
	 * @param amount The quantity of the item to drop.
	 * @param chance The probability percentage for the drop to occur.
	 * @param step The specific quest step associated with this drop.
	 */
	public HandlerSideDrop(int questId, int npcId, int itemId, int amount, int chance, int step)
	{
		this(questId, npcId, itemId, amount, chance);
		collecting_step = step;
	}
	
	/**
	 * Retrieves the total quantity required for this drop.<br>
	 * This value is stored in the {@code neededAmount} field.
	 * @return The integer amount needed.
	 */
	public int getNeededAmount()
	{
		return neededAmount;
	}
}
