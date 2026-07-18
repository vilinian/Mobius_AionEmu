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
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the logic for toggling and managing Game Master (GM) modes.<br>
 * This class allows administrators to enable special privileges for {@link Player} objects.<br>
 * It extends {@link AdminCommand} to provide command-based access to these features.
 * @author Eloann
 */
public class GMMode extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link GMMode} class.<br>
	 * This constructor sets up the default command name for the admin system.
	 */
	public GMMode()
	{
		super("gm");
	}
	
	/**
	 * Toggles the GM mode for the administrator.<br>
	 * Use {@code on} to enable it or {@code off} to disable it.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be either {@code on} or {@code off}.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (admin.getAccessLevel() < 1)
		{
			PacketSendUtility.sendMessage(admin, "You cannot use this command.");
			return;
		}
		
		if (params.length != 1)
		{
			onFail(admin, null);
			return;
		}
		
		if (params[0].toLowerCase().equals("on"))
		{
			if (!admin.isGmMode())
			{
				admin.setGmMode(true);
				admin.setWispable();
				
				GMService.getInstance().onPlayerLogin(admin); // put gm into
				
				// gmlist
				GMService.getInstance().onPlayerAvailable(admin); // send
				
				// The message is available.
				admin.clearKnownlist();
				PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
				PacketSendUtility.sendPacket(admin, new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
				admin.updateKnownlist();
				PacketSendUtility.sendMessage(admin, "you are now Available and Wispable by players");
				
			}
		}
		
		if (params[0].toLowerCase().equals("off"))
		{
			if (admin.isGmMode())
			{
				admin.setGmMode(false);
				admin.setUnWispable();
				
				GMService.getInstance().onPlayerLogedOut(admin); // remove gm
				
				// Add to the GMList.
				GMService.getInstance().onPlayerUnavailable(admin); // send
				
				// The message is unavailable.
				admin.clearKnownlist();
				PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
				PacketSendUtility.sendPacket(admin, new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
				admin.updateKnownlist();
				PacketSendUtility.sendMessage(admin, "you are now Unavailable and Unwispable by players");
			}
		}
	}
	
	/**
	 * This method is called when an {@code execute} command fails.<br>
	 * It sends a failure notification to the administrator.
	 * @param admin The {@code Player} who attempted the command.
	 * @param message The error message to display.
	 */
	@Override
	public void onFail(Player admin, String message)
	{
		final String syntax = "syntax //gm <on|off>";
		PacketSendUtility.sendMessage(admin, syntax);
	}
}
