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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This class handles administrative commands within the game server.<br>
 * It extends {@link AdminCommand} to provide functionality for authorized users.<br>
 * Use this class to register and manage special system-level actions.
 * @author Phantom, ATracer
 */
public class Admin extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Admin} class.<br>
	 * This constructor registers the command as {@code admin}.<br>
	 * It allows administrators to perform special actions in the game.
	 */
	public Admin()
	{
		super("admin");
	}
	
	/**
	 * Displays an HTML help page to the player.<br>
	 * It retrieves the command list from {@link HTMLCache}.<br>
	 * The message is sent using the {@link HTMLService}.
	 * @param player The player who will view the HTML content.
	 * @param params These parameters are not used for this specific action.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("commands.xhtml"));
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
		// TODO Auto-generated method stub
	}
}
