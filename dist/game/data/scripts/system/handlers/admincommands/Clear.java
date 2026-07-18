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
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to clear the chat history.<br>
 * This class allows administrators to wipe all current messages from the chat window.
 * @author KID
 */
public class Clear extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Clear} command.<br>
	 * This class handles the admin command used to clear messages.
	 */
	public Clear()
	{
		super("clear");
	}
	
	/**
	 * Executes the command to clear specific system data.<br>
	 * It handles requests for groups, allies, or finding groups.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines the action.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params[0].equalsIgnoreCase("groups"))
		{
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if (params[0].equalsIgnoreCase("allys"))
		{
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if (params[0].equalsIgnoreCase("findgroup"))
		{
			FindGroupService.getInstance().clean();
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
		PacketSendUtility.sendMessage(player, "<usage //clear groups | allys | findgroup");
	}
}
