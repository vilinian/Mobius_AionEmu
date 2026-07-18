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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * Handles the logic for initiating a new quest.<br>
 * This operation is triggered when a player meets the requirements to begin a {@link com.aionemu.gameserver.questEngine.model.QuestEnv}.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StartQuestOperation")
public class StartQuestOperation extends QuestOperation
{
	@XmlAttribute(required = true)
	protected int id;
	
	/**
	 * Executes the logic for starting a quest.<br>
	 * This method processes the specific operation defined in the quest data.
	 * @param env The {@link QuestEnv} object containing the current quest context.
	 */
	@Override
	public void doOperate(QuestEnv env)
	{
		// TODO Auto-generated method stub
	}
}
