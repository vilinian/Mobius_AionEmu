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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to ban a player based on their IP address.<br>
 * It prevents specific {@code Player} instances from connecting to the server.
 * @author Watson
 */
public class BanIp extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link BanIp} command.<br>
	 * This class handles the logic for banning players by their IP address.<br>
	 * It registers the command name as {@code banip}.
	 */
	public BanIp()
	{
		super("banip");
	}
	
	/**
	 * Executes the command to ban a specific IP address.<br>
	 * It parses the provided parameters for the IP mask and an optional duration in minutes.<br>
	 * If no time is provided, it defaults to a long-term ban.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the IP mask and the ban duration.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
			return;
		}
		
		final String mask = params[0];
		
		int time = 0; // Default: infinity
		if (params.length > 1)
		{
			try
			{
				time = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e)
			{
				onFail(player, e.getMessage());
				return;
			}
		}
		
		if (time == 0)
		{
			time = 60 * 24 * 365 * 10; // pseudo infinity
		}
		
		LoginServer.getInstance().sendBanPacket((byte) 2, 0, mask, time, player.getObjectId());
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
		PacketSendUtility.sendMessage(player, "Syntax: //banip <mask> [time in minutes]");
	}
}
