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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the artificial intelligence for NPCs responsible for repairing gates during siege events.<br>
 * This class manages the logic for identifying damaged structures and initiating repair actions.
 * @author Source
 */
@AIName("siege_gaterepair")
public class GateRepairAI2 extends NpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		final RequestResponseHandler gaterepair = new RequestResponseHandler(player)
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				final RequestResponseHandler repairstone = new RequestResponseHandler(player)
				{
					
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						onActivate(player);
					}
					
					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// Nothing Happens
					}
				};
				if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, repairstone))
				{
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_DO_YOU_ACCEPT_REPAIR, player.getObjectId(), 5, new DescriptionId((2 * 716568) + 1)));
				}
			}
			
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				// Nothing Happens
			}
		};
		
		if (player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, gaterepair))
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_DOOR_REPAIR_POPUPDIALOG, player.getObjectId(), 5));
		}
	}
	
	/**
	 * This method handles the logic after a dialog ends.<br>
	 * It is called when a {@link Player} finishes interacting with an NPC.
	 * @param player The {@code Player} who finished the dialog.
	 */
	@Override
	protected void handleDialogFinish(Player player)
	{
	}
	
	/**
	 * Starts the gate repair process.<br>
	 * This is called when a {@link Player} interacts with the NPC.
	 * @param player The {@link Player} who triggered the activation.
	 */
	public void onActivate(Player player)
	{
		// Stert repair process
	}
}
