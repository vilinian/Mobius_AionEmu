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
 * Handles the admin command to promote a player's permissions.<br>
 * This class allows administrators to grant higher privileges to other users.
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Promote extends AdminCommand
{
	/**
	 * Initializes the {@link Promote} command.<br>
	 * This constructor registers the command with the system.
	 */
	public Promote()
	{
		super("promote");
	}
	
	/**
	 * Executes the command to promote a player's access level or membership.<br>
	 * It validates the input parameters and updates the target player's permissions.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the character name, the second is the type (accesslevel or membership), and the third is the mask value.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length != 3)
		{
			PacketSendUtility.sendMessage(admin, "syntax //promote <characterName> <accesslevel | membership> <mask> ");
			return;
		}
		
		int mask = 0;
		try
		{
			mask = Integer.parseInt(params[2]);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Only number!");
			return;
		}
		
		int type = 0;
		if (params[1].toLowerCase().equals("accesslevel"))
		{
			type = 1;
			if ((mask > 10) || (mask < 0))
			{
				PacketSendUtility.sendMessage(admin, "accesslevel can be 0 - 10");
				return;
			}
		}
		else if (params[1].toLowerCase().equals("membership"))
		{
			type = 2;
			if ((mask > 3) || (mask < 0))
			{
				PacketSendUtility.sendMessage(admin, "membership can be 0 - 3");
				return;
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //promote <characterName> <accesslevel | membership> <mask>");
			return;
		}
		
		final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), mask, type);
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
		PacketSendUtility.sendMessage(player, "syntax //promote <characterName> <accesslevel | membership> <mask> ");
	}
}
