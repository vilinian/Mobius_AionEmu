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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to display the current number of online players.<br>
 * It retrieves the count from the {@link PlayerDAO} and sends it back to the administrator.
 * @author VladimirZ
 */
public class Online extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Online} command.<br>
	 * This constructor registers the command with the name {@code online}.<br>
	 * It allows administrators to check player status.
	 */
	public Online()
	{
		super("online");
	}
	
	/**
	 * Displays the current number of players online.<br>
	 * It sends a message to the {@code admin} with the total count.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional arguments.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final int playerCount = DAOManager.getDAO(PlayerDAO.class).getOnlinePlayerCount();
		
		if (playerCount == 1)
		{
			PacketSendUtility.sendMessage(admin, "There is " + (playerCount) + " player online !");
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "There are " + (playerCount) + " players online !");
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
		PacketSendUtility.sendMessage(player, "Syntax: //online");
	}
}
