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
package system.handlers.weddingcommands;

import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.chathandlers.WeddingCommand;

/**
 * Handles the {@code missyou} command for married players.<br>
 * It allows users to send a special message or effect to their spouse.
 * @author synchro2
 * @rework Eloann
 */
public class missyou extends WeddingCommand
{
	/**
	 * Initializes the {@code missyou} command.<br>
	 * This method sets up the command within the {@link WeddingCommand} system.
	 */
	public missyou()
	{
		super("missyou");
	}
	
	/**
	 * Teleports the player to their wedding partner.<br>
	 * It checks if both players are online and not in restricted areas or combat.<br>
	 * The command can only be used once every hour.
	 * @param player The {@code Player} executing the command.
	 * @param params Additional parameters for the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final Player partner = player.findPartner();
		
		if (partner == null)
		{
			PacketSendUtility.sendMessage(player, "Not online.");
			return;
		}
		
		if (player.isInPrison() || player.isInPvPArena() || partner.isInPrison() || partner.isInPvPArena())
		{
			PacketSendUtility.sendMessage(player, "You cannot use this command in your location.");
			return;
		}
		
		if (player.isInInstance() || partner.isInInstance())
		{
			PacketSendUtility.sendMessage(player, "You can't teleported to " + partner.getName() + ", your partner is in Instance.");
			return;
		}
		
		if (player.isAttackMode() || partner.isAttackMode())
		{
			PacketSendUtility.sendMessage(player, "You can't use this command in combat mode!");
			return;
		}
		
		if (!player.isCommandInUse())
		{
			TeleportService2.teleportTo(player, partner.getWorldId(), partner.getInstanceId(), partner.getX(), partner.getY(), partner.getZ(), partner.getHeading(), TeleportAnimation.BEAM_ANIMATION);
			PacketSendUtility.sendMessage(player, "Teleported to player " + partner.getName() + ".");
			player.setCommandUsed(true);
			
			ThreadPoolManager.getInstance().schedule(() -> player.setCommandUsed(false), 60 * 60 * 1000);
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Only 1 TP per hour.");
		}
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "Failed");
	}
}
