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

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.DynamicPortalService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for creating dynamic portals in the game world.<br>
 * It utilizes {@link DynamicPortalService} to manage portal placement and behavior.
 */
public class DynamicPortal extends AdminCommand
{
	private static final String COMMAND_OPEN = "open";
	private static final String COMMAND_CLOSE = "close";
	private static final String COMMAND_LIST = "list";
	
	/**
	 * Initializes a new instance of the {@code DynamicPortal} class.<br>
	 * This constructor sets up the command for managing dynamic portals.
	 */
	public DynamicPortal()
	{
		super("dynamicportal");
	}
	
	/**
	 * Executes the dynamic portal admin command.<br>
	 * It processes commands to open, close, or list portals.<br>
	 * If no parameters are provided, it displays help information.
	 * @param player The {@link Player} executing the command.
	 * @param params Variable arguments for the command actions.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_CLOSE.equalsIgnoreCase(params[0]) || COMMAND_OPEN.equalsIgnoreCase(params[0]))
		{
			handleStartStopDynamic(player, params);
		}
		else if (COMMAND_LIST.equalsIgnoreCase(params[0]))
		{
			HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("dynamicportals.xhtml"));
		}
	}
	
	/**
	 * Manages the starting and stopping of dynamic portals.<br>
	 * It checks if the provided {@code params} are valid before taking action.<br>
	 * This method interacts with {@link DynamicPortalService} to update portal states.
	 * @param player The {@code Player} who executed the command.
	 * @param params A variable list of strings containing the action and the rift ID.
	 */
	protected void handleStartStopDynamic(Player player, String... params)
	{
		if ((params.length != 2) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int dynamicRiftId = toInt(params[1]);
		if (!isValidDynamicPortalLocationId(player, dynamicRiftId))
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_OPEN.equalsIgnoreCase(params[0]))
		{
			if (DynamicPortalService.getInstance().isDynamicPortalInProgress(dynamicRiftId))
			{
				PacketSendUtility.sendMessage(player, "Dynamic Portal " + dynamicRiftId + " is already start");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Dynamic Portal " + dynamicRiftId + " started!");
				DynamicPortalService.getInstance().startDynamicPortal(dynamicRiftId);
			}
		}
		else if (COMMAND_CLOSE.equalsIgnoreCase(params[0]))
		{
			if (!DynamicPortalService.getInstance().isDynamicPortalInProgress(dynamicRiftId))
			{
				PacketSendUtility.sendMessage(player, "Dynamic Portal " + dynamicRiftId + " is not start!");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Dynamic Portal " + dynamicRiftId + " stopped!");
				DynamicPortalService.getInstance().stopDynamicPortal(dynamicRiftId);
			}
		}
	}
	
	/**
	 * Checks if a specific portal ID exists in the system.<br>
	 * It verifies the {@code dynamicRiftId} against the active locations.<br>
	 * If the ID is missing, it sends an error message to the {@link Player}.
	 * @param player The {@code Player} who initiated the command.
	 * @param dynamicRiftId The unique identifier for the portal location.
	 * @return {@code true} if the ID exists, otherwise {@code false}.
	 */
	protected boolean isValidDynamicPortalLocationId(Player player, int dynamicRiftId)
	{
		if (!DynamicPortalService.getInstance().getDynamicPortalLocations().keySet().contains(dynamicRiftId))
		{
			PacketSendUtility.sendMessage(player, "Id " + dynamicRiftId + " is invalid");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Displays the help message for the {@code //dynamicportal} command.<br>
	 * It shows the syntax for opening, closing, and listing portals.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //dynamicportal open|close <Id>\nAdminCommand //dynamicportal list (Shows an HTML with a dynamic portal list)");
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
		PacketSendUtility.sendMessage(player, "<usage //dynamicportal>");
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
