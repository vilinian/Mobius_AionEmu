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
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * This packet handles the upload of legion information from the client to the server.<br>
 * It is used to synchronize {@link LegionEmblemType} and other related data for a player's legion.
 * @author Simple modified cura
 */
public class CM_LEGION_UPLOAD_INFO extends AionClientPacket
{
	/**
	 * Emblem related information *
	 */
	private int totalSize;
	private int red;
	private int green;
	private int blue;
	
	/**
	 * Creates a new instance of {@link CM_LEGION_UPLOAD_INFO}.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@code State} of the packet.
	 * @param restStates A variable number of additional {@code State} objects.
	 */
	public CM_LEGION_UPLOAD_INFO(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		totalSize = readD();
		readC(); // 0xFF
		red = readC();
		green = readC();
		blue = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		
		LegionService.getInstance().uploadEmblemInfo(activePlayer, totalSize, red, green, blue, LegionEmblemType.CUSTOM);
	}
}
