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
package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.ItemOrders;

/**
 * This class represents the data structure for item orders within a quest.<br>
 * It holds the specific requirements and details for items that players must interact with during a {@link com.aionemu.gameserver.questEngine.handlers.template.ItemOrders} task.
 * @author Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemOrdersData")
public class ItemOrdersData extends XMLQuest
{
	@XmlAttribute(name = "start_item_id", required = true)
	protected int startItemId;
	@XmlAttribute(name = "talk_npc_id1")
	protected int talkNpc1;
	@XmlAttribute(name = "talk_npc_id2")
	protected int talkNpc2;
	@XmlAttribute(name = "end_npc_id", required = true)
	protected int endNpcId;
	
	/**
	 * Registers this data as a quest handler.<br>
	 * It creates a new {@link ItemOrders} instance.<br>
	 * The instance is then added to the provided {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		final ItemOrders template = new ItemOrders(id, startItemId, talkNpc1, talkNpc2, endNpcId);
		questEngine.addQuestHandler(template);
	}
}
