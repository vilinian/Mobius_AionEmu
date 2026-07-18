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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles the {@code CM_CUSTOM_SETTINGS} packet received from the client.<br>
 * It processes custom configuration data sent by the player to the server.<br>
 * The server responds using the {@link SM_CUSTOM_SETTINGS} packet.
 * @author Sweetkr
 */
public class CM_CUSTOM_SETTINGS extends AionClientPacket
{
	private int display;
	private int deny;
	
	/**
	 * Creates a new {@link CM_CUSTOM_SETTINGS} packet.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} of the packet.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_CUSTOM_SETTINGS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		/**
		 * 1 : show legion mantle 2 : priority equipment 4 : show helmet
		 */
		display = readH();
		/**
		 * 1 : view detail player 2 : trade 4 : party/force 8 : legion 16 : friend 32 : dual(pvp)
		 */
		deny = readH();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		activePlayer.getPlayerSettings().setDisplay(display);
		activePlayer.getPlayerSettings().setDeny(deny);
		
		PacketSendUtility.broadcastPacket(activePlayer, new SM_CUSTOM_SETTINGS(activePlayer), true);
	}
}
