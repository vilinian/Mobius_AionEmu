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
package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;

/**
 * Represents a quest condition based on specific variable values.<br>
 * It evaluates whether the current {@link QuestEnv} meets the required criteria defined in the XML configuration.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestVarCondition")
public class QuestVarCondition extends QuestCondition
{
	@XmlAttribute(required = true)
	protected int value;
	@XmlAttribute(name = "var_id", required = true)
	protected int varId;
	
	/**
	 * Checks if a specific quest variable meets the required condition.<br>
	 * This method retrieves the variable from {@link QuestEnv} and compares it to the stored {@code value}.<br>
	 * The comparison depends on the operation type defined in this class.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean doCheck(QuestEnv env)
	{
		final QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
		{
			return false;
		}
		
		final int var = qs.getQuestVars().getVarById(varId);
		switch (getOp())
		{
			case EQUAL:
				return var == value;
			case GREATER:
				return var > value;
			case GREATER_EQUAL:
				return var >= value;
			case LESSER:
				return var < value;
			case LESSER_EQUAL:
				return var <= value;
			case NOT_EQUAL:
				return var != value;
			default:
				return false;
		}
	}
}
