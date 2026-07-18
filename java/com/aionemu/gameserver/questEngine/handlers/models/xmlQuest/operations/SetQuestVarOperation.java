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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the operation to set a specific variable within a quest.<br>
 * It updates the {@link QuestState} by assigning a value to a predefined quest variable.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetQuestVarOperation")
public class SetQuestVarOperation extends QuestOperation
{
	@XmlAttribute(name = "var_id", required = true)
	protected int varId;
	@XmlAttribute(required = true)
	protected int value;
	
	/**
	 * Updates a specific quest variable for the player.<br>
	 * It sets the value of {@code varId} to {@code value}.<br>
	 * The method then sends an {@link SM_QUEST_ACTION} packet to the player.
	 * @param env The quest environment containing the current context.
	 */
	@Override
	public void doOperate(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final int questId = env.getQuestId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null)
		{
			qs.getQuestVars().setVarById(varId, value);
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
}
