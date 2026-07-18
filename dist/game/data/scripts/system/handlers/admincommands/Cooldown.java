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
 * This class handles the {@code admincommand} for managing action cooldowns.<br>
 * It allows administrators to set or modify wait times for specific game actions.
 * @author Cura
 */
public class Cooldown extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Cooldown} class.<br>
	 * This constructor registers the command as {@code cooldown}.
	 */
	public Cooldown()
	{
		super("cooldown");
	}
	
	/**
	 * Toggles the cooldown status for all skills.<br>
	 * It resets the cooldown if it is currently active.<br>
	 * If no cooldown exists, it sets the state to true and sends a recovery message.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing additional parameters for the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.isCoolDownZero())
		{
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills has been recovered.");
			player.setCoolDownZero(false);
		}
		else
		{
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills is set to 0.");
			player.setCoolDownZero(true);
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
