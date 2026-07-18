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

import java.util.stream.Collectors;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.siegeservice.BalaurAssaultService;
import com.aionemu.gameserver.services.siegeservice.Siege;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to the {@link Siege} system.<br>
 * This class allows administrators to manage siege states, locations, and active events.
 */
@SuppressWarnings("rawtypes")
public class SiegeCommand extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_LIST_LOCATIONS = "locations";
	private static final String COMMAND_LIST_SIEGES = "sieges";
	private static final String COMMAND_CAPTURE = "capture";
	private static final String COMMAND_ASSAULT = "assault";
	
	/**
	 * Initializes a new instance of the {@link SiegeCommand} class.<br>
	 * This command allows administrators to manage siege events.<br>
	 * It registers the base command name as {@code siege}.
	 */
	public SiegeCommand()
	{
		super("siege");
	}
	
	/**
	 * Executes a siege-related admin command.<br>
	 * It identifies the action based on the first parameter provided.<br>
	 * If no parameters are given, it displays help information.
	 * @param player The {@code Player} executing the command.
	 * @param params Variable arguments containing the command type and extra data.
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
			handleStartStopSiege(player, params);
		}
		else if (COMMAND_LIST.equalsIgnoreCase(params[0]))
		{
			handleList(player, params);
		}
		else if (COMMAND_LIST_SIEGES.equals(params[0]))
		{
			listLocations(player);
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
	 * Handles the starting and stopping of sieges for a specific location.<br>
	 * It validates the input parameters before calling {@code startSiege} or {@code stopSiege}.
	 * @param player The {@code Player} who executed the command.
	 * @param params A variable list of strings where the first is the action and the second is the siege location ID.
	 */
	protected void handleStartStopSiege(Player player, String... params)
	{
		if ((params.length != 2) || !isInt(params[1]))
		{
			showHelp(player);
			return;
		}
		
		final int siegeLocId = toInt(params[1]);
		if (!isValidSiegeLocationId(player, siegeLocId))
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_START.equalsIgnoreCase(params[0]))
		{
			if (SiegeService.getInstance().isSiegeInProgress(siegeLocId))
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " is already under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " - starting siege!");
				SiegeService.getInstance().startSiege(siegeLocId);
			}
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0]))
		{
			if (!SiegeService.getInstance().isSiegeInProgress(siegeLocId))
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " is not under siege");
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocId + " - stopping siege!");
				SiegeService.getInstance().stopSiege(siegeLocId);
			}
		}
	}
	
	/**
	 * Checks if a specific fortress ID exists in the active siege locations.<br>
	 * This method validates the {@code fortressId} against the data provided by {@link SiegeService}.<br>
	 * If the ID is invalid, it sends an error message to the {@code player}.
	 * @param player The {@code Player} who initiated the check.
	 * @param fortressId The unique identifier of the fortress to validate.
	 * @return {@code true} if the location is valid, or {@code false} otherwise.
	 */
	protected boolean isValidSiegeLocationId(Player player, int fortressId)
	{
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(fortressId))
		{
			PacketSendUtility.sendMessage(player, "Id " + fortressId + " is invalid");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Processes the list command for an administrator.<br>
	 * It checks the {@code params} array to determine which sub-command to execute.<br>
	 * If the input is invalid, it calls {@code showHelp}.
	 * @param player The {@code Player} who is executing the command.
	 * @param params An array of {@code String} parameters provided by the user.
	 */
	protected void handleList(Player player, String[] params)
	{
		if (params.length != 2)
		{
			showHelp(player);
			return;
		}
		
		if (COMMAND_LIST_LOCATIONS.equalsIgnoreCase(params[1]))
		{
			listLocations(player);
		}
		else if (COMMAND_LIST_SIEGES.equalsIgnoreCase(params[1]))
		{
			listSieges(player);
		}
		else
		{
			showHelp(player);
		}
	}
	
	/**
	 * Sends a list of all siege locations to the player.<br>
	 * It displays both fortress and artifact locations.<br>
	 * Each message shows the location ID and its owner race.
	 * @param player The {@link Player} who will receive the messages.
	 */
	protected void listLocations(Player player)
	{
		for (FortressLocation f : SiegeService.getInstance().getFortresses().values())
		{
			PacketSendUtility.sendMessage(player, "Fortress: " + f.getLocationId() + " belongs to " + f.getRace());
		}
		
		for (ArtifactLocation a : SiegeService.getInstance().getStandaloneArtifacts().values())
		{
			PacketSendUtility.sendMessage(player, "Artifact: " + a.getLocationId() + " belongs to " + a.getRace());
		}
	}
	
	/**
	 * Displays a list of all active sieges to the player.<br>
	 * It shows each location ID and the remaining time for every siege.<br>
	 * The information is sent as a message to the {@code Player}.
	 * @param player The {@link Player} who will receive the messages.
	 */
	protected void listSieges(Player player)
	{
		for (Integer i : SiegeService.getInstance().getSiegeLocations().keySet())
		{
			final Siege s = SiegeService.getInstance().getSiege(i);
			if (s != null)
			{
				final int secondsLeft = SiegeService.getInstance().getRemainingSiegeTimeInSeconds(i);
				String minSec = (secondsLeft / 60) + "m ";
				minSec += (secondsLeft % 60) + "s";
				PacketSendUtility.sendMessage(player, "Location: " + i + ": " + minSec + " left.");
			}
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
		
		final int siegeLocationId = toInt(params[1]);
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId))
		{
			PacketSendUtility.sendMessage(player, "Invalid Siege Location Id: " + siegeLocationId);
			return;
		}
		
		// check if params2 is siege race
		SiegeRace sr = null;
		try
		{
			sr = SiegeRace.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			// ignore
		}
		
		// try to find legion by name
		Legion legion = null;
		if (sr == null)
		{
			try
			{
				final int legionId = Integer.valueOf(params[2]);
				legion = LegionService.getInstance().getLegion(legionId);
			}
			catch (NumberFormatException e)
			{
				String legionName = "";
				for (int i = 2; i < params.length; i++)
				{
					legionName += " " + params[i];
				}
				
				legion = LegionService.getInstance().getLegion(legionName.trim());
			}
			
			if (legion != null)
			{
				final int legionBGeneral = LegionService.getInstance().getLegionBGeneral(legion.getLegionId());
				if (legionBGeneral != 0)
				{
					final PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
					sr = SiegeRace.getByRace(BGeneral.getRace());
				}
			}
		}
		
		// check if can capture
		if ((legion == null) && (sr == null))
		{
			PacketSendUtility.sendMessage(player, params[2] + " is not valid siege race or legion name");
			return;
		}
		
		// capture
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(siegeLocationId);
		final Siege s = SiegeService.getInstance().getSiege(siegeLocationId);
		if (s != null)
		{
			s.getSiegeCounter().addRaceDamage(sr, s.getBoss().getLifeStats().getMaxHp() + 1);
			s.setBossKilled(true);
			SiegeService.getInstance().stopSiege(siegeLocationId);
			loc.setLegionId(legion != null ? legion.getLegionId() : 0);
		}
		else
		{
			SiegeService.getInstance().deSpawnNpcs(siegeLocationId);
			loc.setVulnerable(false);
			loc.setUnderShield(false);
			loc.setRace(sr);
			loc.setLegionId(legion != null ? legion.getLegionId() : 0);
			SiegeService.getInstance().spawnNpcs(siegeLocationId, sr, SiegeModType.PEACE);
			DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(loc);
		}
		
		SiegeService.getInstance().broadcastUpdate(loc);
	}
	
	/**
	 * Starts a Balaur assault at a specific location.<br>
	 * This method validates the provided {@code params} for a valid ID and delay.<br>
	 * It then calls {@code int, int)} to begin the event.
	 * @param player The {@link Player} executing the command.
	 * @param params An array containing the siege location ID and the delay time.
	 */
	protected void assault(Player player, String[] params)
	{
		if ((params.length < 2) || (!isInt(params[1]) && !isInt(params[2])))
		{
			showHelp(player);
			return;
		}
		
		final int siegeLocationId = toInt(params[1]);
		final int delay = toInt(params[2]);
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId))
		{
			PacketSendUtility.sendMessage(player, "Invalid Siege Location Id: " + siegeLocationId);
			return;
		}
		
		BalaurAssaultService.getInstance().startAssault(player, siegeLocationId, delay);
	}
	
	/**
	 * Displays the help message for the {@code //siege} command.<br>
	 * It shows available sub-commands and lists all valid fortress and artifact IDs.
	 * @param player The {@link Player} who will receive the help message.
	 */
	protected void showHelp(Player player)
	{
		PacketSendUtility.sendMessage(player, "AdminCommand //siege Help\n" + "//siege start|stop <LocationId>\n" + "//siege list locations|sieges\n" + "//siege capture <LocationId> <siegeRaceName(ELYOS,ASMODIANS,BALAUR)|legionName|legionId>\n" + "//siege assault <LocationId> <delaySec>");
		
		final java.util.Set<Integer> fortressIds = SiegeService.getInstance().getFortresses().keySet();
		final java.util.Set<Integer> artifactIds = SiegeService.getInstance().getStandaloneArtifacts().keySet();
		PacketSendUtility.sendMessage(player, "Fortress: " + fortressIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
		PacketSendUtility.sendMessage(player, "Artifacts: " + artifactIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
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
