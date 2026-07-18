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
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.utils.i18n.CustomMessageId;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;

/**
 * Handles the {@code /cube} player command.<br>
 * This class allows players to interact with the {@link CubeExpandService}.<br>
 * It processes user input to manage cube expansion and related actions.
 * @author Kamui, Maestros
 */
public class cmd_cube extends PlayerCommand
{
	/**
	 * Initializes the {@code cube} command handler.<br>
	 * This constructor sets up the command for player interaction.<br>
	 * It calls the superclass constructor to register the name.
	 */
	public cmd_cube()
	{
		super("cube");
	}
	
	/**
	 * Expands the player's cube to its maximum capacity.<br>
	 * It checks if the player already has 9 expansions.<br>
	 * If not, it uses {@code boolean)} to add them.
	 * @param player The {@code Player} who is receiving the expansion.
	 * @param params Additional arguments that are not used by this command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.getCubeExpands() >= 9)
		{
			PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.CUBE_ALLREADY_EXPANDED));
			return;
		}
		while (player.getCubeExpands() < 9)
		{
			CubeExpandService.expand(player, true);
		}
		
		PacketSendUtility.sendMessage(player, LanguageHandler.translate(CustomMessageId.CUBE_SUCCESS_EXPAND));
	}
	
	/**
	 * This method is called when an {@code execute} command fails.<br>
	 * It sends a failure notification to the administrator.
	 * @param admin The {@code Player} who attempted the command.
	 * @param message The error message to display.
	 */
	@Override
	public void onFail(Player admin, String message)
	{
		PacketSendUtility.sendMessage(admin, "Syntax : .cube");
	}
}
