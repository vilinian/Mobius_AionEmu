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

/**
 * This class handles the command to remove skill delays for all players.<br>
 * It allows administrators to bypass cooldowns across the entire server.
 * @author Alcapwnd
 */
public class CmdRemoveSkillDelayAll extends AbstractGMHandler
{
	/**
	 * This command removes the skill delay for all players.<br>
	 * It is intended for use by administrators only.
	 * @param admin The {@link Player} who executes this command.
	 */
	public CmdRemoveSkillDelayAll(Player admin)
	{
		super(admin, "");
		run();
	}
	
	// TODO its a little bit odd
	/**
	 * Toggles the skill delay status for a player.<br>
	 * It checks if the current {@code removeSkillDelay} is 1 or 0.<br>
	 * It updates the value and sends a confirmation message to the player.
	 */
	private void run()
	{
		/*
		 * Player t = target != null ? target : admin; if (t.getRemoveSkillDelay() == 1) { t.setRemoveSkillDelay(0); PacketSendUtility.sendMessage(t, "Now you got your normal Skill Cooldowns!"); } else if (t.getRemoveSkillDelay() == 0) { t.setRemoveSkillDelay(1); PacketSendUtility.sendMessage(t, "Now you wont have any Skill Cooldowns!"); }
		 */
	}
	
}
