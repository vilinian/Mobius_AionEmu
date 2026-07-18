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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an item drop associated with a specific quest.<br>
 * This class defines the rewards or items granted to players upon completing a {@code Quest}.
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestDrop")
public class QuestDrop
{
	@XmlAttribute(name = "npc_id")
	protected Integer npcId;
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	@XmlAttribute
	protected Integer chance;
	@XmlAttribute(name = "drop_each_member")
	protected int dropEachMember = 0;
	@XmlAttribute(name = "collecting_step")
	protected int collecting_step = 0;
	@XmlTransient
	protected Integer questId;
	
	/**
	 * Retrieves the unique identifier for the NPC. <br>
	 * This value is used to identify which NPC provides this drop.
	 * @return The {@code Integer} ID of the NPC, or {@code null} if not set.
	 */
	public Integer getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the unique identifier for the item.<br>
	 * This value is used to identify which item is being rewarded.
	 * @return The {@code Integer} ID of the item, or {@code null} if not set.
	 */
	public Integer getItemId()
	{
		return itemId;
	}
	
	/**
	 * Retrieves the success rate for a quest drop.<br>
	 * This value represents the percentage chance of an item dropping.
	 * @return The current {@code chance} value as an {@code int}. If {@code null}, it returns {@code 100}.
	 */
	public int getChance()
	{
		if (chance == null)
		{
			return 100;
		}
		
		return chance;
	}
	
	/**
	 * Checks if the item drops for every member of a group.<br>
	 * This method returns {@code true} if the {@code dropEachMember} value is equal to {@code 1}.
	 * @return {@code true} if it drops for each member, otherwise {@code false}
	 */
	public boolean isDropEachMemberGroup()
	{
		return dropEachMember == 1;
	}
	
	/**
	 * Checks if the item drops for every member of an alliance.<br>
	 * This method returns {@code true} if the {@code dropEachMember} value is equal to {@code 2}.
	 * @return {@code true} if it drops for each alliance member, otherwise {@code false}
	 */
	public boolean isDropEachMemberAlliance()
	{
		return dropEachMember == 2;
	}
	
	/**
	 * Retrieves the unique identifier for the quest.<br>
	 * This value is linked to a specific {@code Quest}.
	 * @return The {@code Integer} ID of the quest, or {@code null} if not set.
	 */
	public Integer getQuestId()
	{
		return questId;
	}
	
	/**
	 * Retrieves the current step of the collection process.<br>
	 * This value is used to track progress within a quest.
	 * @return The {@code int} value representing the current step.
	 */
	public int getCollectingStep()
	{
		return collecting_step;
	}
	
	/**
	 * Sets the unique identifier for the quest.<br>
	 * This updates the {@code questId} field of this {@link QuestDrop}.
	 * @param questId The {@code Integer} ID of the quest to assign.
	 */
	public void setQuestId(Integer questId)
	{
		this.questId = questId;
	}
}
