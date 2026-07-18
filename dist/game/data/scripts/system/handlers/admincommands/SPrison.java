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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the {@code //sprison} admin command.<br>
 * It sends a specified {@link Player} to the prison for a set duration in minutes.<br>
 * This class manages the logic for issuing punishments via the {@link PunishmentService}.
 * @author lord_rex Command: //sprison <player> <delay>(minutes) This command is sending player to prison.
 */
public class SPrison extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link SPrison} command.<br>
	 * This class handles the logic for sending players to prison.<br>
	 * It registers the command name as {@code sprison}.
	 */
	public SPrison()
	{
		super("sprison");
	}
	
	/**
	 * Executes the command to send a player to prison.<br>
	 * It takes the target name, delay time, and an optional reason as parameters.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the target name, the second is the delay in minutes, and the rest is the reason.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length < 2)
		{
			sendInfo(admin);
			return;
		}
		
		try
		{
			final Player playerToPrison = World.getInstance().findPlayer(Util.convertName(params[0]));
			final int delay = Integer.parseInt(params[1]);
			
			String reason = Util.convertName(params[2]);
			for (int itr = 3; itr < params.length; itr++)
			{
				reason += " " + params[itr];
			}
			
			if (playerToPrison != null)
			{
				PunishmentService.setIsInPrison(playerToPrison, true, delay, reason);
				PacketSendUtility.sendMessage(admin, "Player " + playerToPrison.getName() + " sent to prison for " + delay + " minute(s) because " + reason + ".");
			}
		}
		catch (Exception e)
		{
			sendInfo(admin);
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
		sendInfo(player);
	}
	
	/**
	 * Sends a syntax help message to the specified {@code Player}.<br>
	 * This informs the user of the correct command format.
	 * @param player The {@link Player} who will receive the message.
	 */
	private void sendInfo(Player player)
	{
		PacketSendUtility.sendMessage(player, "syntax //sprison <player> <delay> <reason>");
	}
}
