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
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code status} admin command.<br>
 * This class provides functionality to display information about a player or the server.<br>
 * It allows administrators to check current states using the {@link AdminCommand} system.
 * @author KID
 */
public class Status extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Status} class.<br>
	 * This command allows administrators to check system status.<br>
	 * It registers the command with the name {@code status}.
	 */
	public Status()
	{
		super("status");
	}
	
	/**
	 * Executes the command to check service statuses.<br>
	 * It sends a message based on whether {@code alliance} or {@code group} is requested.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines the status to check.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params[0].equalsIgnoreCase("alliance"))
		{
			PacketSendUtility.sendMessage(admin, PlayerAllianceService.getServiceStatus());
		}
		else if (params[0].equalsIgnoreCase("group"))
		{
			PacketSendUtility.sendMessage(admin, PlayerGroupService.getServiceStatus());
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
		PacketSendUtility.sendMessage(player, "<usage //status alliance | group");
	}
}
