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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;

/**
 * Handles the logic for quest operations that require a player to collect specific items.<br>
 * This class manages the requirements and validation for {@link com.aionemu.gameserver.questEngine.model.QuestEnv} tasks involving item gathering.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollectItemQuestOperation", propOrder =
{
	"_true",
	"_false"
})
public class CollectItemQuestOperation extends QuestOperation
{
	@XmlElement(name = "true", required = true)
	protected QuestOperations _true;
	@XmlElement(name = "false", required = true)
	protected QuestOperations _false;
	@XmlAttribute
	protected Boolean removeItems;
	
	/**
	 * Executes the logic for collecting an item.<br>
	 * It checks if the collection requirements are met based on the {@code removeItems} flag.<br>
	 * Depending on the result, it runs either the {@code _true} or {@code _false} operation.
	 * @param env The quest environment containing the current context.
	 */
	@Override
	public void doOperate(QuestEnv env)
	{
		if (QuestService.collectItemCheck(env, removeItems == null ? true : false))
		{
			_true.operate(env);
		}
		else
		{
			_false.operate(env);
		}
	}
}
