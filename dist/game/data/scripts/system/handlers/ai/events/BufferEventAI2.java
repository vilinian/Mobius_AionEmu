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

import java.util.Random;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles specific AI behaviors for buffer-type NPCs.<br>
 * This class manages event responses and actions for entities with the {@code buffer} name.<br>
 * It extends {@link NpcAI2} to provide custom logic for these specific NPC types.
 * @author Master
 */
@AIName("buffer")

// 831857, 831858
public class BufferEventAI2 extends NpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * This method processes specific actions based on the {@code dialogId}.<br>
	 * It may trigger skills or update quest progress.
	 * @param player The {@link Player} who interacted with the NPC.
	 * @param dialogId The unique identifier for the current dialog option.
	 * @param questId The ID of the quest associated with this dialog.
	 * @param exp The amount of experience to grant upon selection.
	 * @return {@code true} if the action was processed successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int exp)
	{
		switch (dialogId)
		{
			case 10000:
			{
				final int[] rr =
				{
					2,
					2,
					1,
					1,
					1
				};
				final Random rand = new Random();
				
				final int skillLevel = 1;
				getOwner().setTarget(player);
				
				final int skillId1 = 20950; // Blessing of Rock I SKILLID:20950
				final int skillId2 = 20950; // Blessing of Health I
				
				if (rr[rand.nextInt(4)] == 2)
				{
					SkillEngine.getInstance().getSkill(getOwner(), skillId1, skillLevel, player).useWithoutPropSkill();
				}
				else
				{
					if (rand.nextInt(1) == 0)
					{
						SkillEngine.getInstance().getSkill(getOwner(), skillId1, skillLevel, player).useWithoutPropSkill();
					}
					else
					{
						SkillEngine.getInstance().getSkill(getOwner(), skillId2, skillLevel, player).useWithoutPropSkill();
					}
				}
				
				break;
			}
		}
		
		return true;
	}
}
