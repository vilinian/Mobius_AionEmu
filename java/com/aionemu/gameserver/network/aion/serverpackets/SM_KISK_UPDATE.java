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

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet updates the state of a {@link Kisk} object for connected clients.<br>
 * It synchronizes changes to the kisk's properties and position in the game world.
 * @author Sarynth 0xB0 for 1.5.1.10 and 1.5.1.15
 */
public class SM_KISK_UPDATE extends AionServerPacket
{
	// useMask values determine who can bind to the kisk.
	// We must programmatically check for race, legion, solo, group, and alliance types.
	private final int objId;
	private final int useMask;
	private final int currentMembers;
	private final int maxMembers;
	private final int remainingRessurects;
	private final int maxRessurects;
	private final int remainingLifetime;
	
	/**
	 * Updates the client with the current status of a {@link Kisk}.<br>
	 * This method initializes the packet data from a {@code Kisk} object.
	 * @param kisk The {@code Kisk} object containing the update information.
	 */
	public SM_KISK_UPDATE(Kisk kisk)
	{
		objId = kisk.getObjectId();
		
		useMask = kisk.getUseMask();
		currentMembers = kisk.getCurrentMemberCount();
		maxMembers = kisk.getMaxMembers();
		remainingRessurects = kisk.getRemainingResurrects();
		maxRessurects = kisk.getMaxRessurects();
		remainingLifetime = kisk.getRemainingLifetime();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final Player player = con.getActivePlayer();
		writeD(objId);
		writeD(player.getObjectId());
		writeD(useMask);
		writeD(currentMembers);
		writeD(maxMembers);
		writeD(remainingRessurects);
		writeD(maxRessurects);
		writeD(remainingLifetime);
	}
}
