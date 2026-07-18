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
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This class handles the {@code //fsc} admin command used for development purposes.<br>
 * It allows administrators to manually create and send custom packets from the server to the client using a specific format string.<br>
 * The command takes a packet ID, a format definition (e.g., {@code d}, {@code h}, {@code s}), and a list of corresponding data values.
 * @author Luno
 */
public class Fsc extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Fsc} class.<br>
	 * This constructor sets up the command for sending custom packets.
	 */
	public Fsc()
	{
		super("fsc");
	}
	
	/**
	 * Executes the custom packet sending command.<br>
	 * It parses the provided parameters to create a {@code SM_CUSTOM_PACKET}.<br>
	 * The method sends the constructed packet to the specified {@link Player}.
	 * @param player The admin player who executes the command.
	 * @param params Variable arguments containing the packet ID, format string, and data values.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length < 3)
		{
			PacketSendUtility.sendMessage(player, "Incorrent number of params in //fsc command");
			return;
		}
		
		final int id = Integer.decode(params[0]);
		String format = "";
		
		if (params.length > 1)
		{
			format = params[1];
		}
		
		final SM_CUSTOM_PACKET packet = new SM_CUSTOM_PACKET(id);
		
		int i = 0;
		for (char c : format.toCharArray())
		{
			packet.addElement(PacketElementType.getByCode(c), params[i + 2]);
			i++;
		}
		
		PacketSendUtility.sendPacket(player, packet);
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
		PacketSendUtility.sendMessage(player, "Incorrent number of params in //fsc command");
	}
}
