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
 * Handles the admin notice command.<br>
 * This class allows administrators to broadcast messages to all players in the world.
 * @author Jenose Updated By Darkwolf
 */
public class Notice extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Notice} class.<br>
	 * This constructor initializes the command with the name {@code notice}.
	 */
	public Notice()
	{
		super("notice");
	}
	
	/**
	 * Executes the notice command to send a global message.<br>
	 * It combines all provided parameters into a single string.<br>
	 * The message is displayed in bright yellow to all players in the world.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the text of the notice.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		String message = "";
		
		try
		{
			for (int i = 0; i < params.length; i++)
			{
				message += " " + params[i];
			}
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "Parameters should be text or number !");
			return;
		}
		
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		
		while (iter.hasNext())
		{
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), "Information: " + message);
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
		PacketSendUtility.sendMessage(player, "Syntax: //notice <message>");
	}
}
