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
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for managing and triggering world invasions.<br>
 * This class allows administrators to control invasion events within the game world.
 */
public class Invasion extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	
	/**
	 * Initializes a new instance of the {@link Invasion} class.<br>
	 * This constructor registers the command as {@code invasion}.
	 */
	public Invasion()
	{
		super("invasion");
	}
	
	/**
	 * Executes the invasion command for a specific player.<br>
	 * It checks if the first parameter is {@code start} or {@code stop}.<br>
	 * If no parameters are provided, it calls {@code showHelp}.
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
			handleStartStopInvasion(player, params);
		}
	}
	
	/**
	 * Processes the start and stop commands for world invasions.<br>
	 * It validates the input parameters and updates the {@link VortexService}.
	 * @param player The {@code Player} who executed the command.
	 * @param params A variable list of strings where index 0 is the action and index 1 is the vortex ID.
	 */
	protected void handleStartStopInvasion(Player player, String... params)
	{
		if ((params.length != 2) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int vortexId = toInt(params[1]);
		final String locationName = vortexId == 0 ? "Theobomos" : "Brusthonin";
		if (!isValidVortexLocationId(player, vortexId))
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_START.equalsIgnoreCase(params[0]))
		{
			if (VortexService.getInstance().isInvasionInProgress(vortexId))
			{
				PacketSendUtility.sendMessage(player, locationName + " is already under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, locationName + " invasion started!");
				VortexService.getInstance().startInvasion(vortexId);
			}
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0]))
		{
			if (!VortexService.getInstance().isInvasionInProgress(vortexId))
			{
				PacketSendUtility.sendMessage(player, locationName + " is not under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, locationName + " invasion stopped!");
				VortexService.getInstance().stopInvasion(vortexId);
			}
		}
	}
	
	/**
	 * Checks if a specific vortex ID exists in the system.<br>
	 * It verifies the {@code vortexId} against the list provided by {@link VortexService}.<br>
	 * If the ID is missing, it sends an error message to the {@code player}.
	 * @param player The {@code Player} who initiated the check.
	 * @param vortexId The unique identifier for the vortex location.
	 * @return {@code true} if the ID is valid, or {@code false} otherwise.
	 */
	protected boolean isValidVortexLocationId(Player player, int vortexId)
	{
		if (!VortexService.getInstance().getVortexLocations().keySet().contains(vortexId))
		{
			PacketSendUtility.sendMessage(player, "Id " + vortexId + " is invalid");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Displays the help message for the {@code //invasion} command.<br>
	 * It shows the user how to start or stop an invasion using a specific ID.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //invasion start|stop <Id>");
	}
	
	/**
	 * Checks whether the given string can be parsed as a base-10 integer.
	 * @param value the string to test
	 * @return {@code true} if {@code value} is a non-empty, parseable integer; {@code false} otherwise
	 */
	private static boolean isInt(String value)
	{
		if ((value == null) || value.isEmpty())
		{
			return false;
		}
		try
		{
			Integer.parseInt(value);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	/**
	 * Parses the given string as a base-10 integer.
	 * @param value the string to parse
	 * @return the parsed integer, or {@code 0} if {@code value} cannot be parsed
	 */
	private static int toInt(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
}
