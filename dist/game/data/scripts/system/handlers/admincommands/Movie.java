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
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the admin command to play a movie for players.<br>
 * It allows administrators to trigger {@link SM_PLAY_MOVIE} packets to specific targets.
 * @author d3v1an
 */
public class Movie extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Movie} class.<br>
	 * This constructor initializes the command with the name {@code movie}.
	 */
	public Movie()
	{
		super("movie");
	}
	
	/**
	 * Executes a command to play a movie for the player.<br>
	 * It parses the provided parameters to get the required IDs.<br>
	 * The method sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE} packet.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the movie identifiers.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (params.length < 1)
		{
			onFail(player, null);
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
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
		PacketSendUtility.sendMessage(player, "//movie <type> <id>");
	}
}
