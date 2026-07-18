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
import com.aionemu.gameserver.services.DisputeLandService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for managing land disputes.<br>
 * This class allows administrators to resolve or manage {@link com.aionemu.gameserver.services.DisputeLandService} actions.
 */
public class Dispute extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	
	/**
	 * Initializes a new instance of the {@link Dispute} class.<br>
	 * This constructor sets up the admin command for managing disputes.
	 */
	public Dispute()
	{
		super("dispute");
	}
	
	/**
	 * Executes the dispute command for an admin.<br>
	 * It checks if the first parameter is {@code start} or {@code stop}.<br>
	 * If valid, it calls the {@code String...)} method.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the action and additional details.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0]))
		{
			handleRift(player, params);
		}
	}
	
	/**
	 * Processes the rift command for a specific player.<br>
	 * This method toggles the active state of the {@link DisputeLandService}.<br>
	 * It requires exactly one argument to function correctly.
	 * @param player The {@code Player} who executed the command.
	 * @param params A variable number of {@code String} arguments used to start or stop the rift.
	 */
	protected void handleRift(Player player, String... params)
	{
		if (params.length != 1)
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_START.equalsIgnoreCase(params[0]))
		{
			DisputeLandService.getInstance().setActive(true);
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0]))
		{
			DisputeLandService.getInstance().setActive(false);
		}
	}
	
	/**
	 * Displays the help message for the {@code //dispute} command.<br>
	 * It informs the user about how to start and stop disputes.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //dispute start|stop");
	}
}
