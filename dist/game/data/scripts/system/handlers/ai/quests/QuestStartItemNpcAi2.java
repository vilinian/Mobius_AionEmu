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
package system.handlers.ai.quests;

import java.util.List;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Actions.SelectDialogResult;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.ActionItemNpcAI2;

/**
 * Handles the AI logic for NPCs that trigger quest starts when a player uses an item.<br>
 * This class extends {@link ActionItemNpcAI2} to manage specific quest-related interactions.
 * @author Cheatkiller
 */
@AIName("quest_start_use_item")
public class QuestStartItemNpcAi2 extends ActionItemNpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		super.handleDialogStart(player);
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		final List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(getOwner().getNpcId()).getOnQuestStart();
		final int dialogId = relatedQuests.isEmpty() ? -1 : 26;
		final SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, dialogId);
		if (!dialogResult.isSuccess())
		{
			if (isDialogNpc())
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), DialogAction.SELECT_ACTION_1011.id()));
			}
			return;
		}
	}
	
	/**
	 * Checks if the current NPC is configured as a dialog NPC.<br>
	 * This method retrieves the status from the {@code ObjectTemplate}.
	 * @return {@code true} if the NPC can initiate dialogs, {@code false} otherwise.
	 */
	private boolean isDialogNpc()
	{
		return getObjectTemplate().isDialogNpc();
	}
}
