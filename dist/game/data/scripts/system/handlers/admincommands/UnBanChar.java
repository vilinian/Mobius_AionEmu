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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the administrative command to remove a ban from a specific character.<br>
 * It interacts with {@link PunishmentService} to clear existing restrictions.
 * @author nrg
 */
public class UnBanChar extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link UnBanChar} command.<br>
	 * This class handles the logic for removing a ban from a character.
	 */
	public UnBanChar()
	{
		super("unbanchar");
	}
	
	/**
	 * Executes the command to unban a specific character.<br>
	 * It verifies if the player exists before removing the ban.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the target player name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //unbanchar <player>");
			return;
		}
		
		// Banned player must be offline
		final String name = Util.convertName(params[0]);
		final int playerId = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(name);
		if (playerId == 0)
		{
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			PacketSendUtility.sendMessage(admin, "Syntax: //unbanchar <player>");
			return;
		}
		
		PacketSendUtility.sendMessage(admin, "Character " + name + " is not longer banned!");
		
		PunishmentService.unbanChar(playerId);
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
		PacketSendUtility.sendMessage(player, "Syntax: //unban <player> [account|ip|full]");
	}
}
