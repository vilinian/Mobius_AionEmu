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

import java.util.concurrent.Future;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the admin command to remove a chat restriction from a player.<br>
 * It identifies the target player and restores their ability to communicate in the game world.
 * @author Watson
 */
public class UnGag extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link UnGag} command.<br>
	 * This command allows administrators to remove a gag from a player.
	 */
	public UnGag()
	{
		super("ungag");
	}
	
	/**
	 * Removes the gag from a specific player.<br>
	 * It finds the target player by their name and cancels any active gag tasks.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //ungag <player>");
			return;
		}
		
		final String name = Util.convertName(params[0]);
		final Player player = World.getInstance().findPlayer(name);
		if (player == null)
		{
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //ungag <player>");
			return;
		}
		
		player.setGagged(false);
		final Future<?> task = player.getController().getTask(TaskId.GAG);
		if (task != null)
		{
			player.getController().cancelTask(TaskId.GAG);
		}
		
		PacketSendUtility.sendMessage(player, "You have been ungagged");
		
		PacketSendUtility.sendMessage(admin, "Player " + name + " ungagged");
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
		PacketSendUtility.sendMessage(player, "Syntax: //ungag <player>");
	}
}
