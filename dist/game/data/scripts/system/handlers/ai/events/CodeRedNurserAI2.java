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
package system.handlers.ai.events;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.GeneralNpcAI2;

/**
 * Handles the specific AI behaviors for the {@code code_red_nurse} NPC.<br>
 * This class extends {@link GeneralNpcAI2} to manage unique event responses and interactions.
 * @author xTz, modified bobobear
 */
@AIName("code_red_nurse")
public class CodeRedNurserAI2 extends GeneralNpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It handles specific logic based on the current {@code getNpcId()}.<br>
	 * For certain NPCs, it calls the superclass method.<br>
	 * For others, it sends a default {@link SM_DIALOG_WINDOW} packet to the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		switch (getNpcId())
		{
			case 831435: // Jorpine (MON-THU)
			case 831436: // Yennu (MON-THU)
			case 831437: // Dalloren (FRI-SAT)
			case 831518: // Dalliea (FRI-SAT)
			case 831441: // Hylian (MON-THU)
			case 831442: // Rordah (MON-THU)
			case 831443: // Mazka (FRI-SAT)
			case 831524:
			{
				// Desha (FRI-SAT)
				super.handleDialogStart(player);
				break;
			}
			default:
			{
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			}
		}
	}
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * It checks for specific items and grants rewards or skills based on the {@code dialogId}.<br>
	 * This method is triggered by the NPC's interaction system.
	 * @param player The {@link Player} who is interacting with the NPC.
	 * @param dialogId The unique identifier for the current dialog window.
	 * @param questId The ID of the quest associated with this interaction.
	 * @param extendedRewardIndex The index used to determine specific rewards.
	 * @return Always returns {@code true} to indicate the action was processed.
	 */
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		final QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && (dialogId != DialogAction.SETPRO1.id()))
		{
			return true;
		}
		
		if (dialogId == DialogAction.SETPRO1.id())
		{
			int skillId = 0;
			int RemoveSkillId = 0;
			switch (getNpcId())
			{
				case 831435: // Jorpine (MON-THU)
				case 831441:
				{// Hylian (MON-THU)
					RemoveSkillId = 21281;
					skillId = 21280;
					break;
				}
				case 831436: // Yennu (MON-THU)
				case 831442:
				{
					// Rordah (MON-THU)
					RemoveSkillId = 21280;
					skillId = 21281;
					break;
				}
				case 831437: // Dalloren (FRI-SAT)
				case 831524:
				{
					// Desha (FRI-SAT)
					RemoveSkillId = 21283;
					skillId = 21309;
					break;
				}
				case 831518: // Dalliea (FRI-SAT)
				case 831443:
				{
					// Mazka (FRI-SAT)
					RemoveSkillId = 21309;
					skillId = 21283;
					break;
				}
			}
			
			// only one buff at the same time
			player.getEffectController().removeEffect(RemoveSkillId);
			SkillEngine.getInstance().getSkill(getOwner(), skillId, 1, player).useWithoutPropSkill();
		}
		else if ((dialogId == DialogAction.QUEST_SELECT.id()) && (questId != 0))
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
		
		return true;
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It checks the current day of the week to determine if the NPC should be active.<br>
	 * If the NPC is not active for the current day, it calls {@code onDelete()} on the controller.
	 */
	@Override
	protected void handleSpawned()
	{
		final ZonedDateTime now = ZonedDateTime.now();
		final int currentDay = now.getDayOfWeek().getValue();
		switch (getNpcId())
		{
			case 831435: // Jorpine (MON-THU)
			case 831436: // Yennu (MON-THU)
			case 831441: // Hylian (MON-THU)
			case 831442:
			{// Rordah (MON-THU)
				if ((currentDay >= 1) && (currentDay <= 4))
				{
					super.handleSpawned();
				}
				else if (!isAlreadyDead())
				{
					getOwner().getController().onDelete();
				}
				break;
			}
			case 831437: // Dalloren (FRI-SAT)
			case 831518: // Dalliea (FRI-SAT)
			case 831443: // Mazka (FRI-SAT)
			case 831524:
			{
				// Deshna (FRI-SAT)
				if ((currentDay >= 5) && (currentDay <= 7))
				{
					super.handleSpawned();
				}
				else if (!isAlreadyDead())
				{
					getOwner().getController().onDelete();
				}
				break;
			}
		}
	}
}
