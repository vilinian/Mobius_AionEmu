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
package com.aionemu.gameserver.network.aion.gmhandler;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * Handles the command to grant a specific title to a player.<br>
 * This class processes requests from Game Masters to modify player attributes.<br>
 * It interacts with {@link Player} objects to apply the new title.
 * @author Alcapwnd
 */
public class CmdGiveTitle extends AbstractGMHandler
{
	/**
	 * This constructor initializes the {@code CmdGiveTitle} handler.<br>
	 * It sets up the required data for giving a title to a player.<br>
	 * The method then executes the logic via {@code run}.
	 * @param admin The {@code Player} who is executing the command.
	 * @param params The string containing the arguments for the command.
	 */
	public CmdGiveTitle(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the command to give a specific title to a player.<br>
	 * It validates that the provided {@code titleId} is between 1 and 301.<br>
	 * If successful, it adds the title to the target player and sends notifications.
	 */
	public void run()
	{
		Player t = admin;
		
		if ((admin.getTarget() != null) && (admin.getTarget() instanceof Player))
		{
			t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));
		}
		
		final Integer titleId = Integer.parseInt(params);
		
		if ((titleId > 301) || (titleId < 1))
		{
			PacketSendUtility.sendMessage(admin, "title id " + titleId + " is invalid (must be between 1 and 301)");
		}
		else
		{
			if (t != null)
			{
				if (!t.getTitleList().addTitle(titleId, false, 0))
				{
					PacketSendUtility.sendMessage(admin, "you can't add title #" + titleId + " to " + (t.equals(admin) ? "yourself" : t.getName()));
				}
				else
				{
					PacketSendUtility.sendMessage(admin, "you added to " + t.getName() + " title #" + titleId);
					PacketSendUtility.sendMessage(t, admin.getName() + " gave you title #" + titleId);
				}
			}
		}
	}
}
