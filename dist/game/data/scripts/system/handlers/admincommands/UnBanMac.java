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
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to remove a MAC address from the ban list.<br>
 * It interacts with {@link BannedMacManager} to lift restrictions for specific hardware.
 * @author KID
 */
public class UnBanMac extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link UnBanMac} command.<br>
	 * This class handles the logic for unbanning a MAC address.<br>
	 * It registers the command name as {@code unbanmac}.
	 */
	public UnBanMac()
	{
		super("unbanmac");
	}
	
	/**
	 * Executes the command to unban a specific MAC address.<br>
	 * It takes the first parameter as the target address to remove from the ban list.<br>
	 * The method notifies the admin of whether the operation was successful.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments where the first element is the MAC address.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			onFail(player, null);
			return;
		}
		
		final String address = params[0];
		final boolean result = BannedMacManager.getInstance().unbanAddress(address, "uban;mac=" + address + ", " + player.getObjectId() + "; admin=" + player.getName());
		if (result)
		{
			PacketSendUtility.sendMessage(player, "mac " + address + " has unbanned");
		}
		else
		{
			PacketSendUtility.sendMessage(player, "mac " + address + " is not banned");
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
		PacketSendUtility.sendMessage(player, "Syntax: //unbanmac <mac>");
	}
}
