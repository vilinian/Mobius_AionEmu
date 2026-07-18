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
package system.handlers.admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin {@code movetome} command.<br>
 * This allows an administrator to teleport a target player to their current location.
 * @author Cyrakuse
 */
public class MoveToMe extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link MoveToMe} command.<br>
	 * This class handles the admin command to teleport players to an administrator.
	 */
	public MoveToMe()
	{
		super("movetome");
	}
	
	/**
	 * Teleports a specified player to the admin's current location.<br>
	 * It takes the character name from the {@code params} array as input.<br>
	 * The method validates if the target is online and not the admin themselves.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the name of the target player to teleport.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "syntax //movetome <characterName>");
			return;
		}
		
		final Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null)
		{
			PacketSendUtility.sendMessage(player, "The specified player is not online.");
			return;
		}
		
		if (playerToMove == player)
		{
			PacketSendUtility.sendMessage(player, "Cannot use this command on yourself.");
			return;
		}
		
		TeleportService2.teleportTo(playerToMove, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
		PacketSendUtility.sendMessage(player, "Teleported player " + playerToMove.getName() + " to your location.");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by " + player.getName() + ".");
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
		PacketSendUtility.sendMessage(player, "syntax //movetome <characterName>");
	}
}
