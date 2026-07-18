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

/**
 * This class handles the {@code CM_UI_SETTINGS} packet sent from the client to the server.<br>
 * It is used to synchronize or update user interface settings for a {@link Player}.
 * @author ATracer
 */
public class CM_UI_SETTINGS extends AionClientPacket
{
	int settingsType;
	byte[] data;
	int size;
	
	/**
	 * Creates a new instance of {@link CM_UI_SETTINGS}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state associated with the packet.
	 * @param restStates Additional states that may be applied to the packet.
	 */
	public CM_UI_SETTINGS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		settingsType = readC();
		readH();
		size = readH();
		data = readB(getRemainingBytes());
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (settingsType == 0)
		{
			player.getPlayerSettings().setUiSettings(data);
		}
		else if (settingsType == 1)
		{
			player.getPlayerSettings().setShortcuts(data);
		}
		else if (settingsType == 2)
		{
			player.getPlayerSettings().setHouseBuddies(data);
		}
	}
}
