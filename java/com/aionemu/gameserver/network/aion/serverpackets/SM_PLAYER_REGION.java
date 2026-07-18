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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This packet is sent to the client to update the player's current region.<br>
 * It informs the game client about which {@link ZoneName} the character is currently located in.
 * @author Rolandas
 */
public class SM_PLAYER_REGION extends AionServerPacket
{
	private final ZoneName subZone;
	
	/**
	 * Creates a new {@code SM_PLAYER_REGION} packet.<br>
	 * This packet identifies the specific region for a player.<br>
	 * It uses the provided {@link ZoneName} to set the location.
	 * @param subZone The name of the sub-zone for this region.
	 */
	public SM_PLAYER_REGION(ZoneName subZone)
	{
		this.subZone = subZone;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		PacketLoggerService.getInstance().logPacketSM(getPacketName());
		writeD(con.getActivePlayer().getObjectId());
		writeC(0);
		writeC(0);
		writeC(0);
		writeD(subZone.name().hashCode());
	}
}
