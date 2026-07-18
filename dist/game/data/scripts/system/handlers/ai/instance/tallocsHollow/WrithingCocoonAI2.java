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
package system.handlers.ai.instance.tallocsHollow;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the artificial intelligence behavior for the {@code writhingcocoon} NPC.<br>
 * This class defines how the cocoon interacts with players and other entities in the game world.
 * @author xTz
 */
@AIName("writhingcocoon")
public class WrithingCocoonAI2 extends NpcAI2
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
		if ((dialogId == 1012) && player.getInventory().decreaseByItemId(185000088, 1))
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId())
			{
				case 730232:
					final Npc npc = getPosition().getWorldMapInstance().getNpc(730233);
					if (npc != null)
					{
						npc.getController().onDelete();
					}
					
					spawn(799500, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(390510)); // Will you accompany me? Tell me if you will.
					break;
				case 730233:
					final Npc npc1 = getPosition().getWorldMapInstance().getNpc(730232);
					if (npc1 != null)
					{
						npc1.getController().onDelete();
					}
					
					spawn(799501, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(390511)); // Let me know if you need my help.
					break;
			}
			
			AI2Actions.deleteOwner(this);
		}
		else if (dialogId == 1012)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1097));
		}
		
		return true;
	}
	
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
}
