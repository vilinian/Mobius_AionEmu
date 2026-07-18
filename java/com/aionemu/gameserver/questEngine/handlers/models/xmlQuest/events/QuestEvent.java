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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions.QuestConditions;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperations;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * Represents a specific event triggered within an {@code Quest}.<br>
 * This class holds the data required to execute quest operations and check conditions based on game actions.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestEvent", propOrder =
{
	"conditions",
	"operations"
})
@XmlSeeAlso(
{
	OnKillEvent.class,
	OnTalkEvent.class
})
public abstract class QuestEvent
{
	protected QuestConditions conditions;
	protected QuestOperations operations;
	@XmlAttribute
	protected List<Integer> ids;
	
	/**
	 * Executes the logic for a monster kill event.<br>
	 * It updates quest variables and sends packets to the player.<br>
	 * This method returns {@code false} in most cases after processing.
	 * @param env The current environment containing quest and player data.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	public boolean operate(QuestEnv env)
	{
		return false;
	}
	
	/**
	 * Retrieves the list of unique identifiers for this action.<br>
	 * If no IDs exist, it returns an empty {@code List}.
	 * @return a {@code List<Integer>} containing the IDs.
	 */
	public List<Integer> getIds()
	{
		if (ids == null)
		{
			ids = new ArrayList<>();
		}
		
		return ids;
	}
}
