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
 * Handles the admin command to grant experience points to a player.<br>
 * It allows administrators to modify a {@link Player}'s level or progress manually.
 * @author Wakizashi
 */
public class AddExp extends AdminCommand
{
	/**
	 * Initializes the {@link AddExp} command.<br>
	 * This class handles adding experience to a player.<br>
	 * It registers the command name as {@code addexp}.
	 */
	public AddExp()
	{
		super("addexp");
	}
	
	/**
	 * Executes the command to add experience points to a target player.<br>
	 * It parses the first parameter as a long value to determine the amount of {@code exp} to add.<br>
	 * The method verifies that the admin has a valid {@link Player} target selected.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments where the first element must be the number of experience points to add.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length != 1)
		{
			onFail(player, null);
			return;
		}
		
		Player target = null;
		
		if (player.getTarget() == null)
		{
			onFail(player, null);
		}
		else if (!(player.getTarget() instanceof Player))
		{
			onFail(player, null);
		}
		else
		{
			target = (Player) player.getTarget();
		}
		
		if (target == null)
		{
			return;
		}
		
		final String paramValue = params[0];
		long exp;
		try
		{
			exp = Long.parseLong(paramValue);
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(player, "<exp> must be an Integer");
			return;
		}
		
		exp += target.getCommonData().getExp();
		target.getCommonData().setExp(exp);
		PacketSendUtility.sendMessage(player, "You added " + params[0] + " exp points to the target.");
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
		PacketSendUtility.sendMessage(player, "Select a target and use command this way:");
		PacketSendUtility.sendMessage(player, "syntax //addexp <exp>");
	}
}
