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
import com.aionemu.gameserver.network.NetworkBannedManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to remove a network ban from a player.<br>
 * It interacts with {@link NetworkBannedManager} to lift restrictions for specific accounts.
 * @author Alex
 */
public class UnBanNet extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link UnBanNet} command.<br>
	 * This class handles the logic for unbanning players from the network.
	 */
	public UnBanNet()
	{
		super("unbannet");
	}
	
	/**
	 * Executes the command to unban a network address.<br>
	 * It takes an IP address from the first parameter and removes it from the ban list.<br>
	 * The method sends a confirmation message to the {@code player} based on the result.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments where the first element must be the IP address to unban.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			onFail(player, null);
			return;
		}
		
		final String ip = params[0];
		final boolean result = NetworkBannedManager.getInstance().unbanAddress(ip, "uban;net=" + ip + ", " + player.getObjectId() + "; admin=" + player.getName());
		if (result)
		{
			PacketSendUtility.sendMessage(player, "network address " + ip + " has unbanned");
		}
		else
		{
			PacketSendUtility.sendMessage(player, "network address " + ip + " is not banned");
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
		PacketSendUtility.sendMessage(player, "Syntax: //unbannet <network address>");
	}
}
