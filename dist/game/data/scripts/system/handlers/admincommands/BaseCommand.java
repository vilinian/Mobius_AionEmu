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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.base.Base;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Serves as the base class for all administrative commands.<br>
 * It provides common functionality and shared logic for {@link AdminCommand} implementations.
 */
@SuppressWarnings("rawtypes")
public class BaseCommand extends AdminCommand
{
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CAPTURE = "capture";
	private static final String COMMAND_ASSAULT = "assault";
	
	/**
	 * Initializes a new instance of the {@link BaseCommand} class.<br>
	 * This constructor sets up the base command name as {@code base}.
	 */
	public BaseCommand()
	{
		super("base");
	}
	
	/**
	 * Executes a base-related admin command.<br>
	 * It checks the first parameter to determine which action to perform.<br>
	 * If no parameters are provided, it calls {@code showHelp}.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the command name and extra data.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length == 0)
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_LIST.equalsIgnoreCase(params[0]))
		{
			handleList(player, params);
		}
		else if (COMMAND_CAPTURE.equals(params[0]))
		{
			capture(player, params);
		}
		else if (COMMAND_ASSAULT.equals(params[0]))
		{
			assault(player, params);
		}
	}
	
	/**
	 * Checks if a specific base ID exists in the game world.<br>
	 * It verifies the {@code baseId} against the list provided by {@link BaseService}.<br>
	 * If the ID is not found, it sends an error message to the {@code player}.
	 * @param player The {@code Player} object receiving the feedback.
	 * @param baseId The unique identifier of the base to check.
	 * @return {@code true} if the ID exists, otherwise {@code false}.
	 */
	protected boolean isValidBaseLocationId(Player player, int baseId)
	{
		if (!BaseService.getInstance().getBaseLocations().keySet().contains(baseId))
		{
			PacketSendUtility.sendMessage(player, "Id " + baseId + " is invalid");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Processes the list command for an administrator.<br>
	 * It sends a message to the {@code player} for every base location found in {@link BaseService}.<br>
	 * If the {@code params} array does not contain exactly one element, it calls {@code showHelp}.
	 * @param player The {@code Player} who is executing the command.
	 * @param params An array of {@code String} parameters provided by the user.
	 */
	protected void handleList(Player player, String[] params)
	{
		if (params.length != 1)
		{
			showHelp(player);
			return;
		}
		
		for (BaseLocation base : BaseService.getInstance().getBaseLocations().values())
		{
			PacketSendUtility.sendMessage(player, "Base:" + base.getId() + " belongs to " + base.getRace());
		}
	}
	
	/**
	 * Captures a specific base for a chosen race.<br>
	 * This method validates the input parameters and checks if the player has permission.<br>
	 * It uses {@link BaseService} to perform the capture action.
	 * @param player The {@code Player} object who is executing the command.
	 * @param params An array of strings containing the base ID and the race name.
	 */
	protected void capture(Player player, String[] params)
	{
		if ((params.length < 3) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int baseId = toInt(params[1]);
		if (!isValidBaseLocationId(player, baseId))
		{
			return;
		}
		
		// check if params2 is race
		Race race = null;
		try
		{
			race = Race.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			// ignore
		}
		
		// check if can capture
		if (race == null)
		{
			PacketSendUtility.sendMessage(player, params[2] + " is not valid race");
			showHelp(player);
			return;
		}
		
		// capture
		final Base base = BaseService.getInstance().getActiveBase(baseId);
		if (base != null)
		{
			BaseService.getInstance().capture(baseId, race);
		}
	}
	
	/**
	 * Initiates an assault on a specific base.<br>
	 * This method spawns attackers of a chosen {@code Race}.<br>
	 * It validates the base ID and the race name provided in {@code params}.
	 * @param player The {@link Player} executing the command.
	 * @param params An array containing the base ID and the target race.
	 */
	protected void assault(Player player, String[] params)
	{
		if ((params.length < 3) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int baseId = toInt(params[1]);
		if (!isValidBaseLocationId(player, baseId))
		{
			return;
		}
		
		// check if params2 is race
		Race race = null;
		try
		{
			race = Race.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			// ignore
		}
		
		// check if race is valid
		if (race == null)
		{
			PacketSendUtility.sendMessage(player, params[2] + " is not valid race");
			showHelp(player);
			return;
		}
		
		// assault
		final Base base = BaseService.getInstance().getActiveBase(baseId);
		if (base != null)
		{
			if (base.isAttacked())
			{
				PacketSendUtility.sendMessage(player, "Assault already started!");
			}
			else
			{
				base.spawnAttackers(race);
			}
		}
		else
		{
			PacketSendUtility.sendMessage(player, "This Base doesn't exists!");
		}
	}
	
	/**
	 * Displays the help message for the {@code //base} command.<br>
	 * It shows the available sub-commands and their required parameters.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //base Help\n" + "//base list\n" + "//base capture <Id> <Race (ELYOS,ASMODIANS,NPC)>\n" + "//base assault <Id> <Race (ELYOS,ASMODIANS,NPC)> <delaySec>");
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
