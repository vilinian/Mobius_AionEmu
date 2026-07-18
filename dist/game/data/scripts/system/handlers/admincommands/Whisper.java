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
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command for sending private whisper messages to players.<br>
 * This class allows administrators to communicate directly with a specific {@link Player}.
 */
public class Whisper extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Whisper} class.<br>
	 * This constructor registers the command as {@code whisper}.<br>
	 * It allows administrators to send private messages.
	 */
	public Whisper()
	{
		super("whisper");
	}
	
	/**
	 * Toggles the ability for a player to receive whispers.<br>
	 * Use {@code on} or {@code off} as the first parameter.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be {@code on} or {@code off}.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params[0].equalsIgnoreCase("off"))
		{
			admin.setUnWispable();
			PacketSendUtility.sendMessage(admin, "Accepting Whisper : OFF");
		}
		else if (params[0].equalsIgnoreCase("on"))
		{
			admin.setWispable();
			PacketSendUtility.sendMessage(admin, "Accepting Whisper : ON");
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
		PacketSendUtility.sendMessage(player, "syntax //whisper [on for wispable / off for unwispable]");
	}
}
