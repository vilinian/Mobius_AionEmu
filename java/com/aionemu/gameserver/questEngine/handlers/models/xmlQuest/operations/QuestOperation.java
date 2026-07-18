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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * Represents a single operation within an {@link com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperation} sequence.<br>
 * This class defines the logic and data required to execute specific steps in an XML-based quest.<br>
 * It serves as a model for the quest engine to process player actions and progress.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestOperation")
@XmlSeeAlso(
{
	TakeItemOperation.class,
	StartQuestOperation.class,
	SetQuestVarOperation.class,
	NpcDialogOperation.class,
	GiveItemOperation.class,
	SetQuestStatusOperation.class
})
public abstract class QuestOperation
{
	public abstract void doOperate(QuestEnv env);
}
