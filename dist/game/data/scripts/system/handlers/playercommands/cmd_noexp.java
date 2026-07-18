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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /noexp} player command.<br>
 * This command disables experience gain for the executing {@link Player}.<br>
 * It allows administrators to prevent level progression during specific events.
 * @author Wakizashi
 */
public class cmd_noexp extends PlayerCommand
{
	/**
	 * Registers the {@code noexp} command.<br>
	 * This allows players to use the command in-game.<br>
	 * It extends the functionality of {@link PlayerCommand}.
	 */
	public cmd_noexp()
	{
		super("noexp");
	}
	
	/**
	 * Toggles the experience reward status for a player.<br>
	 * It switches the {@code noExp} flag between true and false.<br>
	 * A message is sent to the {@link Player} confirming the change.
	 * @param player The player whose experience rewards are being toggled.
	 * @param params Additional arguments that are not used by this command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.getCommonData().getNoExp())
		{
			player.getCommonData().setNoExp(false);
			PacketSendUtility.sendMessage(player, "Experience rewards are reactivated !");
		}
		else
		{
			player.getCommonData().setNoExp(true);
			PacketSendUtility.sendMessage(player, "Experience rewards are desactivated !");
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
		// TODO Auto-generated method stub
	}
}
