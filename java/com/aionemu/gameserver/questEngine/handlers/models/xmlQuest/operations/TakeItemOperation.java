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

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This class handles the logic for removing an item from a player's inventory as part of a quest.<br>
 * It is used by the {@link QuestEngine} to process specific requirements defined in XML quest files.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TakeItemOperation")
public class TakeItemOperation extends QuestOperation
{
	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	@XmlAttribute(required = true)
	protected int count;
	
	/**
	 * Removes a specific amount of an item from the player's inventory.<br>
	 * It uses the {@code itemId} and {@code count} defined in this operation.
	 * @param env The quest environment containing the current context.
	 */
	@Override
	public void doOperate(QuestEnv env)
	{
		env.getPlayer().getInventory().decreaseByItemId(itemId, count);
	}
}
