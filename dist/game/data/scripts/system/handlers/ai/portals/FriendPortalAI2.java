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
package system.handlers.ai.portals;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.gameobjects.SummonedHouseNpc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the artificial intelligence for friend portal NPCs.<br>
 * This class manages interactions when players use these portals to visit friends.
 * @author Rolandas
 */
@AIName("friendportal")

// 810003, 810031
public class FriendPortalAI2 extends NpcAI2
{
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It checks if the {@code player} has permission to access the housing friend list.<br>
	 * If allowed, it sends the appropriate {@link DialogPage} packet.<br>
	 * Otherwise, it sends a system message indicating they cannot use the portal.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		final SummonedHouseNpc me = (SummonedHouseNpc) getOwner();
		final int playerOwner = me.getCreator().getOwnerId();
		
		final boolean allowed = (player.getObjectId() == playerOwner) || (player.getFriendList().getFriend(playerOwner) != null) || ((player.getLegion() != null) && player.getLegion().isMember(playerOwner));
		
		if (allowed)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), DialogPage.HOUSING_FRIENDLIST.id()));
		}
		else
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_HOUSING_TELEPORT_CANT_USE);
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
}
