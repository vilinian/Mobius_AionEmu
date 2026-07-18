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

import java.util.Iterator;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the {@code /announce} admin command.<br>
 * It allows administrators to send global messages to all players in the world.<br>
 * This class manages message formatting and determines if the sender should be hidden.
 * @author Ben, Ritsu Smart Matching Enabled //announce anon This will work. as well as //announce a This will work. Both will match the "a" or "anon" to the "anonymous" flag.
 */
public class Announce extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Announce} command.<br>
	 * This class handles the system-wide announcement feature for administrators.
	 */
	public Announce()
	{
		super("announce");
	}
	
	/**
	 * Executes the announce command to send a global message.<br>
	 * It determines if the message should be sent as anonymous or from a specific player.<br>
	 * The method sends the final message in bright yellow to all players in the world.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the sender type and the message content.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		String message;
		
		if (("anonymous").startsWith(params[0].toLowerCase()))
		{
			message = "Announce: ";
		}
		else if (("name").startsWith(params[0].toLowerCase()))
		{
			message = player.getName() + " : ";
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Syntax: //announce <anonymous|name> <message>");
			return;
		}
		
		// Add with space
		for (int i = 1; i < (params.length - 1); i++)
		{
			message += params[i] + " ";
		}
		
		// Add the last without the end space
		message += params[params.length - 1];
		
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		
		while (iter.hasNext())
		{
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), message);
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
		PacketSendUtility.sendMessage(player, "Syntax: //announce <anonymous|name> <message>");
	}
}
