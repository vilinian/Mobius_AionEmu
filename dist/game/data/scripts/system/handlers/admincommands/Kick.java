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
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to remove a player from the game server.<br>
 * It processes the request to disconnect a specific {@link Player} and sends a {@code SM_QUIT_RESPONSE}.
 * @author Elusive
 */
public class Kick extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Kick} command.<br>
	 * This class handles the logic for kicking players from the server.
	 */
	public Kick()
	{
		super("kick");
	}
	
	/**
	 * Executes the command to kick a specific player or all players from the server.<br>
	 * It checks if the target is online before closing their connection.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name or {@code All}.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //kick <character_name> | <All>");
			return;
		}
		
		if ((params[0] != null) && "All".equalsIgnoreCase(params[0]))
		{
			for (Player player : World.getInstance().getAllPlayers())
			{
				if (!player.isGM())
				{
					player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
					PacketSendUtility.sendMessage(admin, "Kicked player : " + player.getName());
				}
			}
		}
		else
		{
			final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (player == null)
			{
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}
			
			player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
			PacketSendUtility.sendMessage(admin, "Kicked player : " + player.getName());
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
		PacketSendUtility.sendMessage(player, "syntax //kick <character_name> | <All>");
	}
}
