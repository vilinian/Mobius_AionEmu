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
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles administrative commands related to the {@link GeoService}.<br>
 * This class allows administrators to manage and interact with world geometry data.
 * @author MrPoke
 */
public class Geo extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Geo} command.<br>
	 * This class handles administrative commands related to world geometry.
	 */
	public Geo()
	{
		super("geo");
	}
	
	/**
	 * Executes the command to display coordinate information.<br>
	 * It checks if the first parameter starts with {@code z}.<br>
	 * If valid, it sends a message showing the current and target Z coordinates.
	 * @param player The admin player executing the command.
	 * @param params Variable arguments containing the command flags or targets.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if ("z".startsWith(params[0]))
		{
			PacketSendUtility.sendMessage(player, "GeoZ: " + GeoService.getInstance().getZ(player) + " current Z: " + player.getZ());
		}
	}
}
