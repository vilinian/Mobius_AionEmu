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
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the command to toggle the visibility of a {@link Player}.<br>
 * It allows Game Masters to make a player visible or invisible in the game world.
 * @author Alcapwnd
 */
public class CmdVisible extends AbstractGMHandler
{
	/**
	 * This constructor initializes a new {@link CmdVisible} command handler.<br>
	 * It sets up the required admin and parameters for execution.<br>
	 * It then calls the {@code run()} method to process the command.
	 * @param admin The {@code Player} object representing the administrator who sent the command.
	 * @param params The string containing the arguments for the command.
	 */
	public CmdVisible(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the logic to make the administrator visible.<br>
	 * It removes the {@code HIDE} abnormal state and the {@code HIDE20} visual state.<br>
	 * Finally, it broadcasts the updated state and sends a confirmation message.
	 */
	private void run()
	{
		admin.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
		admin.unsetVisualState(CreatureVisualState.HIDE20);
		PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
		PacketSendUtility.sendMessage(admin, "You are invisible.");
	}
	
}
