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
 * Handles the admin command to teleport a player to another player's location.<br>
 * This class allows administrators to move characters across the game world instantly.
 * @author Tanelorn
 */
public class MoveToPlayer extends AdminCommand
{
	/**
	 * Initializes the {@link MoveToPlayer} command.<br>
	 * This class handles moving an administrator to a specific player's location.
	 */
	public MoveToPlayer()
	{
		super("movetoplayer");
	}
	
	/**
	 * Teleports the administrator to a specific player.<br>
	 * It validates that the target player is online and not the administrator themselves.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "syntax //movetoplayer characterName");
			return;
		}
		
		final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		if (player == admin)
		{
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}
		
		TeleportService2.teleportTo(admin, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
		PacketSendUtility.sendMessage(admin, "Teleported to player " + player.getName() + ".");
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
		PacketSendUtility.sendMessage(player, "syntax //movetoplayer characterName");
	}
}
