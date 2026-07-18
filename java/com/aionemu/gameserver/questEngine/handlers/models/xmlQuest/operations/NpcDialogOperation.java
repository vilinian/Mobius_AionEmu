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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for displaying a dialog window to a player when interacting with an NPC.<br>
 * This operation is part of the {@link com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperation} system.<br>
 * It processes specific dialogue sequences defined in the quest XML files.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcDialogOperation")
public class NpcDialogOperation extends QuestOperation
{
	@XmlAttribute(required = true)
	protected int id;
	@XmlAttribute(name = "quest_id")
	protected Integer questId;
	
	/**
	 * Executes the logic for opening a dialog window with an {@link VisibleObject}.<br>
	 * It determines the correct quest ID to use.<br>
	 * Then it sends the {@code SM_DIALOG_WINDOW} packet to the player.
	 * @param env The quest environment containing the current context.
	 */
	@Override
	public void doOperate(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final VisibleObject obj = env.getVisibleObject();
		int qId = env.getQuestId();
		if (questId != null)
		{
			qId = questId;
		}
		
		if (qId == 0)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(obj.getObjectId(), id));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(obj.getObjectId(), id, qId));
		}
	}
}
