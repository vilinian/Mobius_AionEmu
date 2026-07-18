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
import com.aionemu.gameserver.questEngine.handlers.template.SkillUse;

/**
 * Represents the data required for a skill to be used as part of a quest.<br>
 * This model stores specific parameters and requirements associated with a {@link SkillUse} action.
 * @author vlog, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseData")
public class SkillUseData extends XMLQuest
{
	@XmlAttribute(name = "start_npc_id")
	protected int startNpc;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpc;
	@XmlElement(name = "skill", required = true)
	protected List<QuestSkillData> skills;
	
	/**
	 * Registers this data as a quest handler.<br>
	 * It creates a new {@link SkillUse} instance.<br>
	 * The instance is then added to the provided {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		final Map<List<Integer>, QuestSkillData> questSkills = new HashMap<>();
		for (QuestSkillData qsd : skills)
		{
			questSkills.put(qsd.getSkillIds(), qsd);
		}
		
		final SkillUse questTemplate = new SkillUse(id, startNpc, endNpc, questSkills);
		questEngine.addQuestHandler(questTemplate);
	}
}
