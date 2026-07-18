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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles the {@code neutral} admin command.<br>
 * This class allows administrators to set a player's status to neutral.<br>
 * It interacts with {@link Player} objects to update their state.
 * @author Sarynth, (edited by Pan)
 */
public class Neutral extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link Neutral} command.<br>
	 * This class handles the neutral admin command.
	 */
	public Neutral()
	{
		super("neutral");
	}
	
	/**
	 * Sets the neutrality status of the administrator.<br>
	 * This method updates how players and NPCs interact with the admin based on the provided parameter.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines the target group (e.g., "players", "npcs", "all", or "cancel").
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final String help = "Syntax: //neutral < players | npcs | all | cancel >\n" + "Players - You're neutral to Players of both factions.\n" + "Npcs - You're neutral to all Npcs and Monsters.\n" + "All - You're neutral to Players of both factions and all Npcs.\n" + "Cancel - Cancel all. Players and Npcs have default enmity to you.";
		
		if (params.length != 1)
		{
			onFail(admin, null);
			return;
		}
		
		String output = "You now appear neutral to " + params[0] + ".";
		
		final int enemyType = admin.getAdminEnmity();
		
		if (params[0].equals("all"))
		{
			admin.setAdminNeutral(3);
			admin.setAdminEnmity(0);
		}
		else if (params[0].equals("players"))
		{
			admin.setAdminNeutral(2);
			if (enemyType > 1)
			{
				admin.setAdminEnmity(0);
			}
		}
		else if (params[0].equals("npcs"))
		{
			admin.setAdminNeutral(1);
			if ((enemyType == 1) || (enemyType == 3))
			{
				admin.setAdminEnmity(0);
			}
		}
		else if (params[0].equals("cancel"))
		{
			admin.setAdminNeutral(0);
			output = "You appear regular to both Players and Npcs.";
		}
		else if (params[0].equals("help"))
		{
			PacketSendUtility.sendMessage(admin, help);
			return;
		}
		else
		{
			onFail(admin, null);
			return;
		}
		
		PacketSendUtility.sendMessage(admin, output);
		
		if (admin.getAdminNeutral() != 0)
		{
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_INFO(admin, true));
		}
		else
		{
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_INFO(admin, false));
		}
		
		PacketSendUtility.broadcastPacket(admin, new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
		admin.clearKnownlist();
		admin.updateKnownlist();
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
		final String syntax = "Syntax: //neutral < players | npcs | all | cancel >\n" + "If you're unsure about what you want to do, type //neutral help";
		PacketSendUtility.sendMessage(player, syntax);
	}
}
