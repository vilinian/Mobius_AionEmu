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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code admincommand} for displaying HTML content to players.<br>
 * It utilizes {@link HTMLCache} and {@link HTMLService} to process and send web-based messages.
 * @author lord_rex
 */
public class Html extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Html} command.<br>
	 * This constructor registers the {@code html} command with the system.
	 */
	public Html()
	{
		super("html");
	}
	
	/**
	 * Executes the HTML management command.<br>
	 * It allows reloading the {@code HTMLCache} or showing a specific file.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing either "reload" or "show" followed by a filename.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "Usage: //html <reload|show>");
			return;
		}
		
		if (params[0].equals("reload"))
		{
			HTMLCache.getInstance().reload(true);
			PacketSendUtility.sendMessage(player, HTMLCache.getInstance().toString());
		}
		else if (params[0].equals("show"))
		{
			if (params.length >= 2)
			{
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML(params[1] + ".xhtml"));
			}
			else
			{
				PacketSendUtility.sendMessage(player, "Usage: //html show <filename>");
			}
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
		PacketSendUtility.sendMessage(player, "Usage: //html <reload|show>");
	}
}
