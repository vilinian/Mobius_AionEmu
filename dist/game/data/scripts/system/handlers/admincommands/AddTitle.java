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

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to assign a specific title to a player.<br>
 * It allows administrators to modify player metadata via the {@link AdminCommand} system.
 * @author Eloann
 * @modified GiGatR00n
 */
public class AddTitle extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link AddTitle} class.<br>
	 * This command allows administrators to assign titles to players.<br>
	 * It registers the command with the name {@code addtitle}.
	 */
	public AddTitle()
	{
		super("addtitle");
	}
	
	/**
	 * Executes the command to give a specific title to a targeted player.<br>
	 * It validates the title ID and checks if the target is a {@code Player}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the title ID and the second is an optional expiration time in minutes.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params.length < 1) || (params.length > 2))
		{
			onFail(admin, null);
			return;
		}
		
		final int titleId = Integer.parseInt(params[0]);
		if ((titleId > 369) || (titleId < 1))
		{
			PacketSendUtility.sendMessage(admin, "title id " + titleId + " is invalid (must be between 1 and 369)");
			return;
		}
		
		final VisibleObject target = admin.getTarget();
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "No target selected");
			return;
		}
		
		if (target instanceof Player)
		{
			final Player player = (Player) target;
			
			boolean sucess = false;
			
			try
			{
				if (params.length == 2)
				{
					final int expireMinutes = Integer.parseInt(params[1]);
					sucess = player.getTitleList().addTitle(titleId, true, expireMinutes);
				}
				else
				{
					sucess = player.getTitleList().addTitle(titleId, true, 0);
				}
			}
			catch (NumberFormatException ex)
			{
				PacketSendUtility.sendMessage(admin, "Missing integer");
				return;
			}
			
			if (sucess)
			{
				PacketSendUtility.sendMessage(admin, "Title added!");
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "You can't add this title");
			}
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
		PacketSendUtility.sendMessage(player, "syntax //addtitle <title_id> [expire time]");
	}
}
