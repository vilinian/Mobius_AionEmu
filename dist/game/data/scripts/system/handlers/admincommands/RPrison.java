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

import java.util.NoSuchElementException;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the {@code //rprison} admin command.<br>
 * It removes a specific {@link Player} from the prison system.
 * @author lord_rex Command: //rprison <player> This command is removing player from prison.
 */
public class RPrison extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link RPrison} command.<br>
	 * This class handles the logic for releasing players from prison.
	 */
	public RPrison()
	{
		super("rprison");
	}
	
	/**
	 * Executes the command to remove a player from prison.<br>
	 * It finds the target player by name and updates their status via {@link PunishmentService}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params.length == 0) || (params.length > 2))
		{
			PacketSendUtility.sendMessage(admin, "syntax //rprison <player>");
			return;
		}
		
		try
		{
			final Player playerFromPrison = World.getInstance().findPlayer(Util.convertName(params[0]));
			
			if (playerFromPrison != null)
			{
				PunishmentService.setIsInPrison(playerFromPrison, false, 0, "");
				PacketSendUtility.sendMessage(admin, "Player " + playerFromPrison.getName() + " removed from prison.");
			}
		}
		catch (NoSuchElementException nsee)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //rprison <player>");
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //rprison <player>");
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
		PacketSendUtility.sendMessage(player, "syntax //rprison <player>");
	}
}
