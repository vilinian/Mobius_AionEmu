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
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to revoke permissions from a player.<br>
 * This class allows administrators to remove specific privileges or roles.
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Revoke extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Revoke} command.<br>
	 * This class handles the administrative action to remove permissions.
	 */
	public Revoke()
	{
		super("revoke");
	}
	
	/**
	 * Executes the command to revoke privileges from another player.<br>
	 * It removes either access levels or membership based on the provided parameters.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the target name and the second is the type to revoke.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length != 2)
		{
			PacketSendUtility.sendMessage(admin, "syntax //revoke <characterName> <acceslevel | membership>");
			return;
		}
		
		int type = 0;
		if (params[1].toLowerCase().equals("accesslevel"))
		{
			type = 1;
		}
		else if (params[1].toLowerCase().equals("membership"))
		{
			type = 2;
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //revoke <characterName> <acceslevel | membership>");
			return;
		}
		
		final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), 0, type);
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
		PacketSendUtility.sendMessage(player, "syntax //revoke <characterName> <acceslevel | membership>");
	}
}
