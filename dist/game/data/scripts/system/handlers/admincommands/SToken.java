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
import com.aionemu.gameserver.services.player.PlayerSecurityTokenService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command for managing security tokens.<br>
 * This class allows administrators to interact with {@link PlayerSecurityTokenService} to manage player authentication.<br>
 * It provides a way to view or modify token statuses within the game world.
 * @author xXMashUpXx
 */
public class SToken extends AdminCommand
{
	/**
	 * Creates a new instance of the {@code SToken} command.<br>
	 * This class handles the security token administration for players.
	 */
	public SToken()
	{
		super("stoken");
	}
	
	/**
	 * Executes the command to manage security tokens for players.<br>
	 * It allows an admin to either generate a new token or view an existing one.<br>
	 * The method checks if the target player is online before performing any actions.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the target name and optional "show" keyword.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length < 1)
		{
			PacketSendUtility.sendMessage(player, "Syntax: //stoken <playername> || //stoken show <playername>");
			return;
		}
		
		Player receiver = null;
		
		if (params[0].equals("show"))
		{
			receiver = World.getInstance().findPlayer(Util.convertName(params[1]));
			if (receiver == null)
			{
				PacketSendUtility.sendMessage(player, "Can't find this player, maybe he's not online");
				return;
			}
			
			if (!"".equals(receiver.getPlayerAccount().getSecurityToken()))
			{
				PacketSendUtility.sendMessage(player, "The Security Token of this player is: " + receiver.getPlayerAccount().getSecurityToken());
			}
			else
			{
				PacketSendUtility.sendMessage(player, "This player haven't an Security Token!");
			}
			
		}
		else
		{
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			
			if (receiver == null)
			{
				PacketSendUtility.sendMessage(player, "Can't find this player, maybe he's not online");
				return;
			}
			
			PlayerSecurityTokenService.getInstance().generateToken(receiver);
		}
	}
	
	/**
	 * This method is called when an {@code execute} command fails.<br>
	 * It sends a failure notification to the administrator.
	 * @param admin The {@code Player} who attempted the command.
	 * @param message The error message to display.
	 */
	@Override
	public void onFail(Player admin, String message)
	{
		PacketSendUtility.sendMessage(admin, "Syntax: //stoken <playername> || //stoken show <playername>");
	}
}
