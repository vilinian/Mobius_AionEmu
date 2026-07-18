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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.ConditionUnionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This class serves as a container for all possible quest conditions defined in the XML configuration.<br>
 * It allows the {@link QuestEngine} to evaluate various requirements such as player level, items, or specific locations.<br>
 * Each condition is mapped to its respective model type within the {@code QuestConditions} structure.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestConditions", propOrder =
{
	"conditions"
})
public class QuestConditions
{
	@XmlElements(
	{
		@XmlElement(name = "quest_status", type = QuestStatusCondition.class),
		@XmlElement(name = "npc_id", type = NpcIdCondition.class),
		@XmlElement(name = "pc_inventory", type = PcInventoryCondition.class),
		@XmlElement(name = "quest_var", type = QuestVarCondition.class),
		@XmlElement(name = "dialog_id", type = DialogIdCondition.class)
	})
	protected List<QuestCondition> conditions;
	@XmlAttribute(required = true)
	protected ConditionUnionType operate;
	
	/**
	 * Checks if the set of quest conditions is met.<br>
	 * It evaluates multiple {@code QuestCondition} objects based on a logical operator.<br>
	 * The result depends on whether the logic is {@code AND} or {@code OR}.
	 * @param env The current environment context for the quest.
	 * @return {@code true} if all conditions are met (for {@code AND}) or at least one is met (for {@code OR}).
	 */
	public boolean checkConditionOfSet(QuestEnv env)
	{
		boolean inCondition = (operate == ConditionUnionType.AND);
		for (QuestCondition cond : conditions)
		{
			final boolean bCond = cond.doCheck(env);
			switch (operate)
			{
				case AND:
					if (!bCond)
					{
						return false;
					}
					
					inCondition = inCondition && bCond;
					break;
				case OR:
					if (bCond)
					{
						return true;
					}
					
					inCondition = inCondition || bCond;
					break;
			}
		}
		
		return inCondition;
	}
}
