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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;

import system.handlers.ai.GeneralNpcAI2;

/**
 * Handles the AI behavior for NPCs involved in the {@code homeward_bound_event}.<br>
 * This class manages specific event logic by extending {@link GeneralNpcAI2}.
 * @author FrozenKiller
 */
@AIName("homeward_bound_event")
public class HomewardBoundEventAI2 extends GeneralNpcAI2
{
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
		if (dialogId == 10000)
		{
			switch (getNpcId())
			{
				case 833671:
				{
					if (player.getRace() == Race.ELYOS)
					{
						SkillEngine.getInstance().getSkill(player, 11047, 1, player).useWithoutPropSkill();
					}
					break;
				}
				case 833672:
				{
					if (player.getRace() == Race.ELYOS)
					{
						SkillEngine.getInstance().getSkill(player, 11049, 1, player).useWithoutPropSkill();
					}
					break;
				}
				case 833673:
				{
					if (player.getRace() == Race.ASMODIANS)
					{
						SkillEngine.getInstance().getSkill(player, 11048, 1, player).useWithoutPropSkill();
					}
					break;
				}
				case 833674:
				{
					if (player.getRace() == Race.ASMODIANS)
					{
						SkillEngine.getInstance().getSkill(player, 11050, 1, player).useWithoutPropSkill();
					}
					break;
				}
				default:
					break;
			}
		}
		else
		{
			final QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
			env.setExtendedRewardIndex(extendedRewardIndex);
			if (QuestEngine.getInstance().onDialog(env))
			{
				return true;
			}
		}
		
		return true;
	}
}
