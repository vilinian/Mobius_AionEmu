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

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.handlers.template.XmlQuest;

/**
 * This class represents the data structure for a quest loaded from an {@code XML} file.<br>
 * It serves as a model to hold all configuration details required by the {@link XmlQuest} template.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlQuest", propOrder =
{
	"onTalkEvent",
	"onKillEvent"
})
public class XmlQuestData extends XMLQuest
{
	@XmlElement(name = "on_talk_event")
	protected List<OnTalkEvent> onTalkEvent;
	@XmlElement(name = "on_kill_event")
	protected List<OnKillEvent> onKillEvent;
	@XmlAttribute(name = "start_npc_id")
	protected Integer startNpcId;
	@XmlAttribute(name = "end_npc_id")
	protected Integer endNpcId;
	
	/**
	 * Retrieves the list of {@link OnTalkEvent} objects associated with this quest.<br>
	 * If the list is null, a new {@code ArrayList} is created and returned.<br>
	 * Modifications to the returned list will affect the internal state of this object.
	 * @return A {@code List} of {@link OnTalkEvent} objects.
	 */
	public List<OnTalkEvent> getOnTalkEvent()
	{
		if (onTalkEvent == null)
		{
			onTalkEvent = new ArrayList<>();
		}
		
		return onTalkEvent;
	}
	
	/**
	 * Retrieves the list of kill events associated with this quest.<br>
	 * If the list is {@code null}, a new {@code ArrayList} is created and returned.
	 * @return A {@code List} of {@link OnKillEvent} objects.
	 */
	public List<OnKillEvent> getOnKillEvent()
	{
		if (onKillEvent == null)
		{
			onKillEvent = new ArrayList<>();
		}
		
		return onKillEvent;
	}
	
	/**
	 * Retrieves the unique identifier for the starting NPC.<br>
	 * This ID is used to determine which NPC initiates the quest.
	 * @return The {@code Integer} ID of the starting NPC or {@code null}.
	 */
	public Integer getStartNpcId()
	{
		return startNpcId;
	}
	
	/**
	 * Retrieves the unique identifier for the ending NPC.<br>
	 * This value is used to determine which NPC concludes the quest.
	 * @return The {@code Integer} ID of the end NPC or {@code null}.
	 */
	public Integer getEndNpcId()
	{
		return endNpcId;
	}
	
	/**
	 * Registers this quest data with the provided engine.<br>
	 * It creates a new {@link XmlQuest} instance using this data.<br>
	 * This instance is then added to the {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		questEngine.addQuestHandler(new XmlQuest(this));
	}
}
