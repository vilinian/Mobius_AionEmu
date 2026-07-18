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
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESURRECT;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the GM command to resurrect a player.<br>
 * It processes the request and sends an {@link SM_RESURRECT} packet to the target.<br>
 * This allows administrators to bring dead players back to life.
 * @author Alcapwnd
 */
public class CmdResurrect extends AbstractGMHandler
{
	/**
	 * Creates a new instance of the {@code CmdResurrect} handler.<br>
	 * This constructor initializes the admin and parameters.<br>
	 * It then immediately executes the {@code run} method.
	 * @param admin The {@code Player} who is performing the action.
	 * @param params The string containing additional arguments for the command.
	 */
	public CmdResurrect(Player admin, String params)
	{
		super(admin, params);
		run();
	}
	
	/**
	 * Executes the command to resurrect a player.<br>
	 * It checks if the target is currently dead.<br>
	 * If so, it activates the resurrection and sends a {@link SM_RESURRECT} packet.
	 */
	public void run()
	{
		final Player t = target != null ? target : admin;
		if (!t.getLifeStats().isAlreadyDead())
		{
			return;
		}
		
		t.setPlayerResActivate(true);
		PacketSendUtility.sendPacket(t, new SM_RESURRECT(admin));
	}
	
}
