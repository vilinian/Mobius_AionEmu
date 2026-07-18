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
package system.handlers.playercommands;

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code help} command for players.<br>
 * It displays a list of available commands to the user.<br>
 * This class extends {@link PlayerCommand}.
 * @author Imaginary
 */
public class cmd_help extends PlayerCommand
{
	/**
	 * Displays the help menu to the player.<br>
	 * This method registers the {@code help} command.<br>
	 * It allows players to see available commands.
	 */
	public cmd_help()
	{
		super("help");
	}
	
	/**
	 * Displays the help menu for player commands.<br>
	 * It opens a specific {@code xhtml} file from the {@link HTMLCache}.<br>
	 * The {@link Player} will see the command list in their interface.
	 * @param player The player who triggered the command.
	 * @param params Additional arguments passed to the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("pcommands.xhtml"));
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
		PacketSendUtility.sendMessage(player, "syntax: .help");
	}
}
