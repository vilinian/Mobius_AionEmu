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
 * Handles the {@code gps} admin command to display coordinates.<br>
 * It allows administrators to view and share location data within the game world.
 * @author Sylar
 */
public class Gps extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Gps} command.<br>
	 * This class handles the GPS admin command in the game server.
	 */
	public Gps()
	{
		super("gps");
	}
	
	/**
	 * Displays the current coordinates of the administrator.<br>
	 * It sends the X, Y, Z, GeoZ, Heading, and World ID to the {@code Player}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional parameters.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		PacketSendUtility.sendMessage(admin, "== GPS Coordinates ==");
		PacketSendUtility.sendMessage(admin, "X = " + admin.getX());
		PacketSendUtility.sendMessage(admin, "Y = " + admin.getY());
		PacketSendUtility.sendMessage(admin, "Z = " + admin.getZ());
		PacketSendUtility.sendMessage(admin, "GeoZ = " + GeoService.getInstance().getZ(admin));
		PacketSendUtility.sendMessage(admin, "H = " + admin.getHeading());
		PacketSendUtility.sendMessage(admin, "World = " + admin.getWorldId());
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
