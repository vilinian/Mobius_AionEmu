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
package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperations;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Represents an event triggered when a monster is killed during a quest.<br>
 * This class handles the logic for updating {@link QuestState} based on the kill action.<br>
 * It provides data necessary for the {@link QuestOperations} to process the completion of a quest objective.
 * @author Mr. Poke, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnKillEvent", propOrder =
{
	"monster",
	"complite"
})
public class OnKillEvent extends QuestEvent
{
	@XmlElement(name = "monster")
	protected List<Monster> monster;
	protected QuestOperations complite;
	
	/**
	 * Retrieves the list of {@link Monster} objects associated with this event.<br>
	 * If the list is {@code null}, it returns a new empty {@code ArrayList}.
	 * @return A {@code List} of {@link Monster} objects.
	 */
	public List<Monster> getMonsters()
	{
		if (monster == null)
		{
			monster = new ArrayList<>();
		}
		
		return monster;
	}
	
	/**
	 * Executes the logic for a monster kill event.<br>
	 * It updates quest variables and sends packets to the player.<br>
	 * This method returns {@code false} in most cases after processing.
	 * @param env The current environment containing quest and player data.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	@Override
	public boolean operate(QuestEnv env)
	{
		if ((monster == null) || !(env.getVisibleObject() instanceof Npc))
		{
			return false;
		}
		
		final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
		{
			return false;
		}
		
		final Npc npc = (Npc) env.getVisibleObject();
		for (Monster m : monster)
		{
			if (m.getNpcIds().contains(npc.getNpcId()))
			{
				final int var = qs.getQuestVarById(m.getVar());
				if ((var >= (m.getStartVar() == null ? 0 : m.getStartVar())) && (var < m.getEndVar()))
				{
					qs.setQuestVarById(m.getVar(), var + 1);
					PacketSendUtility.sendPacket(env.getPlayer(), new SM_QUEST_ACTION(env.getQuestId(), qs.getStatus(), qs.getQuestVars().getQuestVars()));
				}
			}
		}
		
		if (complite != null)
		{
			for (Monster m : monster)
			{
				if (qs.getQuestVarById(m.getVar()) != qs.getQuestVarById(m.getVar()))
				{
					return false;
				}
			}
			
			complite.operate(env);
		}
		
		return false;
	}
}
