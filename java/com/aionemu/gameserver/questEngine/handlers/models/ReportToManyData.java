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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.ReportToMany;

/**
 * This class holds the data required for a quest report that involves multiple targets.<br>
 * It serves as a data model for {@link ReportToMany} quest handlers.
 * @author Hilgert
 * @modified Rolandas
 * @modified FrozenKiller
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportToManyData")
public class ReportToManyData extends XMLQuest
{
	@XmlAttribute(name = "start_item_id")
	protected int startItemId;
	@XmlAttribute(name = "start_npc_ids")
	protected List<Integer> startNpcIds;
	@XmlAttribute(name = "end_npc_ids")
	protected List<Integer> endNpcIds;
	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;
	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;
	@XmlElement(name = "npc_infos", required = true)
	protected List<NpcInfos> npcInfos;
	
	/**
	 * Registers this data as a quest handler.<br>
	 * It creates a new {@link ReportToMany} instance.<br>
	 * The instance is then added to the provided {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		int maxVar = 0;
		final Map<Integer, NpcInfos> NpcInfo = new HashMap<>();
		for (NpcInfos mi : npcInfos)
		{
			NpcInfo.put(mi.getNpcId(), mi);
			if (mi.getVar() > maxVar)
			{
				maxVar = mi.getVar();
			}
		}
		
		final ReportToMany template = new ReportToMany(id, startItemId, startNpcIds, endNpcIds, NpcInfo, startDialog, endDialog, maxVar, mission);
		questEngine.addQuestHandler(template);
	}
}
