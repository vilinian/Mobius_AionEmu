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
 * Handles the admin command for managing rings in the game.<br>
 * This class allows administrators to interact with ring-related items via {@link AdminCommand}.
 * @author xTz
 */
public class Ring extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Ring} command.<br>
	 * This constructor initializes the admin command with the name {@code ring}.
	 */
	public Ring()
	{
		super("ring");
	}
	
	/**
	 * Executes the command to display a ring area around the administrator.<br>
	 * It calculates coordinates based on the {@code Player} heading and sends them as messages.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings for additional parameters.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		double direction = Math.toRadians(admin.getHeading()) - 0.5f;
		if (direction < 0)
		{
			direction += 2f;
		}
		
		final float x1 = (float) (Math.cos(Math.PI * direction) * 6);
		final float y1 = (float) (Math.sin(Math.PI * direction) * 6);
		PacketSendUtility.sendMessage(admin, "center:" + admin.getX() + " " + admin.getY() + " " + admin.getZ());
		PacketSendUtility.sendMessage(admin, "p1:" + admin.getX() + " " + admin.getY() + " " + (admin.getZ() + 6f));
		PacketSendUtility.sendMessage(admin, "p2:" + (admin.getX() + x1) + " " + (admin.getY() + y1) + " " + admin.getZ());
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
		PacketSendUtility.sendMessage(player, "syntax; //ring");
	}
}
