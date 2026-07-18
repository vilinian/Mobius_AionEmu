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
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to the {@link RiftService}.<br>
 * This class allows administrators to manage and interact with rift instances.<br>
 * It extends {@link AdminCommand} to provide specific functionality for these actions.
 */
public class Rift extends AdminCommand
{
	private static final String COMMAND_OPEN = "open";
	private static final String COMMAND_CLOSE = "close";
	private static final String COMMAND_LIST = "list";
	
	/**
	 * Initializes a new instance of the {@code Rift} command.<br>
	 * This class handles administrative commands for managing rifts.<br>
	 * It extends the base {@link AdminCommand} class.
	 */
	public Rift()
	{
		super("rift");
	}
	
	/**
	 * Executes the rift management command.<br>
	 * It processes commands to open, close, or list rifts.<br>
	 * If no parameters are provided, it displays help information.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the specific action and any required IDs.
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
			handleRift(player, params);
		}
		else if (COMMAND_LIST.equalsIgnoreCase(params[0]))
		{
			HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("rifts.xhtml"));
		}
	}
	
	/**
	 * Processes the rift command for a specific player.<br>
	 * This method toggles the active state of the {@link RiftService}.<br>
	 * It requires exactly two arguments to function correctly.
	 * @param player The {@code Player} who executed the command.
	 * @param params A variable number of {@code String} arguments used to start or stop the rift.
	 */
	protected void handleRift(Player player, String... params)
	{
		if ((params.length < 2) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int id = toInt(params[1]);
		boolean result;
		if (!isValidId(player, id))
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_OPEN.equalsIgnoreCase(params[0]))
		{
			final boolean guards = Boolean.parseBoolean(params[2]);
			result = RiftService.getInstance().openRifts(id, guards);
			PacketSendUtility.sendMessage(player, result ? "Rifts is opened!" : "Rifts was already opened");
		}
		else if (COMMAND_CLOSE.equalsIgnoreCase(params[0]))
		{
			result = RiftService.getInstance().closeRifts(id);
			PacketSendUtility.sendMessage(player, result ? "Rifts is closed!" : "Rifts was already closed");
		}
	}
	
	/**
	 * Checks if the provided ID is valid for a rift.<br>
	 * This method calls {@code isValidId} to verify the input.<br>
	 * If the ID is invalid, it sends an error message to the {@code player}.
	 * @param player The {@code Player} who sent the command.
	 * @param id The unique identifier to check.
	 * @return {@code true} if the ID is valid, otherwise {@code false}.
	 */
	protected boolean isValidId(Player player, int id)
	{
		if (!RiftService.getInstance().isValidId(id))
		{
			PacketSendUtility.sendMessage(player, "Id " + id + " is invalid");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Displays the help message for the {@code //rift} command.<br>
	 * It shows the available options for opening, closing, and listing rifts.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //rift open|close <Id|worldId> (open with boolean for guards)\nAdminCommand //rift list (Shows an HTML with a rift list)");
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
