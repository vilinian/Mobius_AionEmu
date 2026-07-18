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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import system.handlers.ai.ActionItemNpcAI2;

/**
 * Handles the artificial intelligence logic for the {@code legendary_toy_bear} NPC.<br>
 * This class manages specific behaviors and interactions triggered by player events.
 * @author Falke_34 & FrozenKiller
 */
@AIName("legendary_toy_bear") // 833669
public class LegendaryToyBearAI2 extends ActionItemNpcAI2
{
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
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
		if (QuestEngine.getInstance().onDialog(env))
		{
			return true;
		}
		
		if (dialogId == 10000)
		{
			SkillEngine.getInstance().getSkill(getOwner(), 22788, 1, player).useWithoutPropSkill();
		}
		
		return true;
	}
}
