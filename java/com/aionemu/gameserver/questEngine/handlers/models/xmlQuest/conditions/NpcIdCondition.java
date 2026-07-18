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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * Represents a quest condition that checks for the presence of a specific {@link Npc} ID.<br>
 * This class is used by the {@link com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions.QuestCondition} system to validate NPC requirements.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcIdCondition")
public class NpcIdCondition extends QuestCondition
{
	@XmlAttribute(required = true)
	protected int values;
	
	/**
	 * Checks if the NPC ID matches the required condition.<br>
	 * This method retrieves the {@link Npc} from the {@link QuestEnv} and compares its ID against the stored {@code values}.<br>
	 * It supports various comparison operations like equality, greater than, and less than.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean doCheck(QuestEnv env)
	{
		int id = 0;
		final VisibleObject visibleObject = env.getVisibleObject();
		if ((visibleObject != null) && (visibleObject instanceof Npc))
		{
			id = ((Npc) visibleObject).getNpcId();
		}
		
		switch (getOp())
		{
			case EQUAL:
				return id == values;
			case GREATER:
				return id > values;
			case GREATER_EQUAL:
				return id >= values;
			case LESSER:
				return id < values;
			case LESSER_EQUAL:
				return id <= values;
			case NOT_EQUAL:
				return id != values;
			default:
				return false;
		}
	}
}
