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
package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This class handles the operational logic for quests defined in {@code xml} files.<br>
 * It manages specific actions and requirements associated with quest progression.<br>
 * It serves as a data model for processing {@link com.aionemu.gameserver.questEngine.model.QuestEnv} operations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestOperations", propOrder =
{
	/**
	 * This class represents the list of operations for a quest.<br>
	 * It contains various actions like giving items or starting quests.<br>
	 * These operations are mapped from XML elements to specific operation types.
	 */
	"operations"
})
public class QuestOperations
{
	@XmlElements(
	{
		@XmlElement(name = "take_item", type = TakeItemOperation.class),
		@XmlElement(name = "npc_dialog", type = NpcDialogOperation.class),
		@XmlElement(name = "set_quest_status", type = SetQuestStatusOperation.class),
		@XmlElement(name = "give_item", type = GiveItemOperation.class),
		@XmlElement(name = "start_quest", type = StartQuestOperation.class),
		@XmlElement(name = "npc_use", type = ActionItemUseOperation.class),
		@XmlElement(name = "set_quest_var", type = SetQuestVarOperation.class),
		@XmlElement(name = "collect_items", type = CollectItemQuestOperation.class)
	})
	protected List<QuestOperation> operations;
	@XmlAttribute
	protected Boolean override;
	
	/**
	 * Checks if the operation should override existing values.<br>
	 * It returns {@code true} if the {@code override} field is {@code null}.<br>
	 * Otherwise, it returns the value of the {@code override} field.
	 * @return {@code true} if overriding is enabled, {@code false} otherwise.
	 */
	public boolean isOverride()
	{
		if (override == null)
		{
			return true;
		}
		
		return override;
	}
	
	/**
	 * Executes all quest operations defined in the current context.<br>
	 * It iterates through the {@code operations} list and calls {@code doOperate} for each one.<br>
	 * The method returns the value of {@code isOverride}.
	 * @param env The current environment containing quest and player data.
	 * @return {@code true} if override is enabled, otherwise {@code false}.
	 */
	public boolean operate(QuestEnv env)
	{
		if (operations != null)
		{
			for (QuestOperation oper : operations)
			{
				oper.doOperate(env);
			}
		}
		
		return isOverride();
	}
}
