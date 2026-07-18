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
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to remove an IP address from the ban list.<br>
 * It allows administrators to restore access for players blocked by their network identity.
 * @author Watson
 */
public class UnBanIp extends AdminCommand
{
	/**
	 * Initializes the {@link UnBanIp} admin command.<br>
	 * This constructor registers the {@code unbanip} command in the system.
	 */
	public UnBanIp()
	{
		super("unbanip");
	}
	
	/**
	 * Executes the command to remove an IP ban.<br>
	 * It takes the target IP address from the first parameter.<br>
	 * The method sends a packet to the {@link LoginServer} to lift the restriction.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments where the first element must be the IP mask.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(player, "Syntax: //unbanip <mask>");
			return;
		}
		
		LoginServer.getInstance().sendBanPacket((byte) 2, 0, params[0], -1, player.getObjectId());
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
		PacketSendUtility.sendMessage(player, "Syntax: //unbanip <mask>");
	}
}
