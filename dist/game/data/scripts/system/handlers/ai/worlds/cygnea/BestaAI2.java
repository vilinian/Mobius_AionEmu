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
package system.handlers.ai.worlds.cygnea;

import java.util.List;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * Handles the artificial intelligence logic for the {@code besta} NPC in the Cygnea world.<br>
 * This class extends {@link NpcAI2} to define specific behaviors and interactions for this entity.
 */
@AIName("besta") // 804843
public class BestaAI2 extends NpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It checks the {@code player} inventory for a specific item.<br>
	 * It sends different {@link SM_DIALOG_WINDOW} packets based on whether the item exists.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		if (player.getInventory().getFirstItemByItemId(185000231) != null)
		{
			// Aedra Territory Village Infiltration Rift Corridor Key
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
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
		if ((dialogId == 10000) && player.getInventory().decreaseByItemId(185000231, 1))
		{
			// Aedra Territory Village Infiltration Rift Corridor Key
			switch (getNpcId())
			{
				case 804843: // Besta
					announceLightLegionPortal();
					spawn(702721, 1214.9753f, 1564.4226f, 468.49017f, (byte) 50);
					ThreadPoolManager.getInstance().schedule(() -> despawnNpc(702721), 300000); // 5 Minutes
					break;
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
	
	/**
	 * Sends a system message to all players.<br>
	 * This notifies them that the Light Legion portal is open.<br>
	 * It uses {@code getInstance} to iterate through all active players.
	 */
	private void announceLightLegionPortal()
	{
		World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LIGHT_SIDE_LEGION_DIRECT_PORTAL_OPEN));
	}
	
	/**
	 * Removes an NPC from the game world.<br>
	 * This method finds all NPCs with the given {@code npcId}.<br>
	 * It calls {@code getController} to trigger the delete action for each one.
	 * @param npcId The unique identifier of the NPC to remove.
	 */
	private void despawnNpc(int npcId)
	{
		if (getPosition().getWorldMapInstance().getNpcs(npcId) != null)
		{
			final List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
			for (Npc npc : npcs)
			{
				npc.getController().onDelete();
			}
		}
	}
}
