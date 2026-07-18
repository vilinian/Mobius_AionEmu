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
 * Handles the admin command to toggle invulnerability for players.<br>
 * It allows administrators to make a {@link Player} immune to damage or effects.
 * @author Andy
 * @author Divinity - update
 */
public class Invul extends AdminCommand
{
	/**
	 * Registers the {@code invul} admin command.<br>
	 * This allows administrators to make a player invincible.<br>
	 * It initializes the command within the {@link AdminCommand} system.
	 */
	public Invul()
	{
		super("invul");
	}
	
	/**
	 * Toggles the invulnerability status of a {@link Player}.<br>
	 * It switches the player between mortal and immortal states.<br>
	 * A message is sent to the player to confirm the change.
	 * @param player The player whose invulnerability status will be toggled.
	 * @param params Additional arguments that are not used by this command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.isInvul())
		{
			player.setInvul(false);
			PacketSendUtility.sendMessage(player, "You are now mortal.");
		}
		else
		{
			player.setInvul(true);
			PacketSendUtility.sendMessage(player, "You are now immortal.");
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
