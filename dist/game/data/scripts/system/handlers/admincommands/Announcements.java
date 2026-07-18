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

import java.util.Set;

import com.aionemu.gameserver.model.Announcement;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.AnnouncementService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands for broadcasting messages to players.<br>
 * This class allows admins to send global or local announcements using the {@link AnnouncementService}.<br>
 * It processes input to create and distribute {@code Announcement} objects.
 * @author Divinity
 */
public class Announcements extends AdminCommand
{
	private final AnnouncementService announceService;
	
	/**
	 * Initializes a new instance of the {@code Announcements} class.<br>
	 * This constructor sets up the required service for handling system messages.<br>
	 * It registers the command with the name {@code announcements}.
	 */
	public Announcements()
	{
		super("announcements");
		announceService = AnnouncementService.getInstance();
	}
	
	/**
	 * Executes commands to manage server announcements.<br>
	 * Use {@code list} to view all current announcements.<br>
	 * Use {@code add} to create a new announcement with specific parameters.<br>
	 * Use {@code delete} to remove an announcement by its unique ID.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the action and required details.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params[0].equals("list"))
		{
			final Set<Announcement> announces = announceService.getAnnouncements();
			PacketSendUtility.sendMessage(player, "ID  |  FACTION  |  CHAT TYPE  |  DELAY  |  MESSAGE");
			PacketSendUtility.sendMessage(player, "-------------------------------------------------------------------");
			
			for (Announcement announce : announces)
			{
				PacketSendUtility.sendMessage(player, announce.getId() + "  |  " + announce.getFaction() + "  |  " + announce.getType() + "  |  " + announce.getDelay() + "  |  " + announce.getAnnounce());
			}
		}
		else if (params[0].equals("add"))
		{
			if ((params.length < 5))
			{
				onFail(player, null);
				return;
			}
			
			int delay;
			
			try
			{
				delay = Integer.parseInt(params[3]);
			}
			catch (NumberFormatException e)
			{
				// 15 minutes, default
				delay = 900;
			}
			
			String message = "";
			
			// Add with space
			for (int i = 4; i < (params.length - 1); i++)
			{
				message += params[i] + " ";
			}
			
			// Add the last without the end space
			message += params[params.length - 1];
			
			// Create the announce
			final Announcement announce = new Announcement(message, params[1], params[2], delay);
			
			// Add the announce in the database
			announceService.addAnnouncement(announce);
			
			// Reload all announcements
			announceService.reload();
			
			PacketSendUtility.sendMessage(player, "The announcement has been created with successful !");
		}
		else if (params[0].equals("delete"))
		{
			if ((params.length < 2))
			{
				onFail(player, null);
				return;
			}
			
			int id;
			
			try
			{
				id = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(player, "The announcement's ID is wrong !");
				onFail(player, e.getMessage());
				return;
			}
			
			// Delete the announcement from the database
			announceService.delAnnouncement(id);
			
			// Reload all announcements
			announceService.reload();
			
			PacketSendUtility.sendMessage(player, "The announcement has been deleted with successful !");
		}
		else
		{
			onFail(player, null);
			return;
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
		String syntaxCommand = "Syntax: //announcements list - Obtain all announcements in the database.\n";
		syntaxCommand += "Syntax: //announcements add <faction: ELYOS | ASMODIANS | ALL> <type: SYSTEM | WHITE | ORANGE | SHOUT | YELLOW> <delay in seconds> <message> - Add an announcements in the database.\n";
		syntaxCommand += "Syntax: //announcements delete <id (see //announcements list to find all id> - Delete an announcements from the database.";
		PacketSendUtility.sendMessage(player, syntaxCommand);
	}
}
