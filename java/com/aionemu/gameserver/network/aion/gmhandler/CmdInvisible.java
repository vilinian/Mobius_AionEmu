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
 * Handles the command to make a player invisible.<br>
 * This class processes requests to toggle the {@code CreatureVisualState} of a {@link Player}.<br>
 * It ensures that the visibility change is synchronized across the network.
 * @author Alcapwnd
 */
public class CmdInvisible extends AbstractGMHandler
{
	/**
	 * Handles the command to make a player invisible.<br>
	 * This constructor initializes the handler with the admin and parameters.<br>
	 * It then calls the {@code run} method to execute the logic.
	 * @param admin The {@code Player} who is executing the command.
	 * @param params The string containing additional arguments for the command.
	 */
	public CmdInvisible(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the logic to make the administrator invisible.<br>
	 * It updates the admin's visual state and sends a broadcast packet.<br>
	 * A confirmation message is sent to the administrator.
	 */
	private void run()
	{
		admin.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		admin.setVisualState(CreatureVisualState.HIDE20);
		PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
		PacketSendUtility.sendMessage(admin, "You are invisible.");
	}
	
}
