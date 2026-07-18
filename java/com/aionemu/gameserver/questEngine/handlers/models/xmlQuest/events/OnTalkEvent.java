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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.QuestVar;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;

/**
 * Represents an event triggered when a player interacts with an NPC via dialogue.<br>
 * This class handles the logic for processing {@link OnTalkEvent} data defined in XML quest files.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnTalkEvent", propOrder =
{
	"var"
})
public class OnTalkEvent extends QuestEvent
{
	protected List<QuestVar> var;
	
	/**
	 * Executes the logic for a talk event.<br>
	 * It checks conditions and updates quest variables based on the environment.<br>
	 * This method returns {@code true} if any variable operation succeeds.
	 * @param env The current environment containing quest and player data.
	 * @return {@code true} if an operation succeeded, otherwise {@code false}.
	 */
	@Override
	public boolean operate(QuestEnv env)
	{
		if ((conditions == null) || conditions.checkConditionOfSet(env))
		{
			final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
			for (QuestVar questVar : var)
			{
				if (questVar.operate(env, qs))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
