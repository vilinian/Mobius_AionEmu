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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /gp} player command.<br>
 * This class allows players to check their current Abyss Points.<br>
 * It interacts with the {@link com.aionemu.gameserver.services.abyss.AbyssPointsService} to retrieve and display point values.
 * @author Waii
 */
public class cmd_gp extends PlayerCommand
{
	/**
	 * Registers the {@code gp} command.<br>
	 * This method initializes the command handler for players.<br>
	 * It calls the constructor of the {@link PlayerCommand} class.
	 */
	public cmd_gp()
	{
		super("gp");
	}
	
	/**
	 * Executes the command to grant GP and deduct AP from a player.<br>
	 * It checks if the {@code player} has enough Abyss Points before proceeding.<br>
	 * If successful, it adds 200 GP and removes 150,000 AP.
	 * @param player The player who will receive the points.
	 * @param params Additional arguments for the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		final int ap = 150000;
		final int gp = 200;
		if (player.getAbyssRank().getAp() < ap)
		{
			PacketSendUtility.sendMessage(player, "You don't have enough Abyss Points, required: " + ap);
			return;
		}
		
		AbyssPointsService.addGp(player, gp);
		AbyssPointsService.addAp(player, -ap);
	}
}
