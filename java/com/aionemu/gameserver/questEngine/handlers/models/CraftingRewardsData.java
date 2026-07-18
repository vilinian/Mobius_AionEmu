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
import com.aionemu.gameserver.questEngine.handlers.template.CraftingRewards;

/**
 * This class represents the data structure for rewards obtained from crafting quests.<br>
 * It holds the specific items and quantities provided to a player upon completing a {@link CraftingRewards} task.
 * @author Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CraftingRewardsData")
public class CraftingRewardsData extends XMLQuest
{
	@XmlAttribute(name = "start_npc_id", required = true)
	protected int startNpcId;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpcId;
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	@XmlAttribute(name = "level_reward")
	protected int levelReward;
	
	/**
	 * Registers this data as a quest handler.<br>
	 * It creates a new {@link CraftingRewards} instance.<br>
	 * The instance is then added to the provided {@code QuestEngine}.
	 * @param questEngine The engine where the quest handler will be registered.
	 */
	@Override
	public void register(QuestEngine questEngine)
	{
		final CraftingRewards template = new CraftingRewards(id, startNpcId, skillId, levelReward, endNpcId, questMovie);
		questEngine.addQuestHandler(template);
	}
}
