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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the synchronization of Union Points within a battlefield.<br>
 * It informs the client about the current status and distribution of points.
 * @author Falke_34
 */
public class SM_BATTLEFIELD_UNION_POINTS extends AionServerPacket
{
	
	int fortressId;
	int elyos_points;
	int asmodians_points;
	int balaur_points;
	
	/**
	 * Creates a new {@code SM_BATTLEFIELD_UNION_POINTS} packet.<br>
	 * This packet stores the points for different factions in a specific fortress.
	 * @param fortressId The unique identifier for the fortress.
	 * @param elyos_points The current points held by the Elyos faction.
	 * @param asmodians_points The current points held by the Asmodian faction.
	 * @param balaur_points The current points held by the Balaur faction.
	 */
	public SM_BATTLEFIELD_UNION_POINTS(int fortressId, int elyos_points, int asmodians_points, int balaur_points)
	{
		this.fortressId = fortressId;
		this.elyos_points = elyos_points;
		this.asmodians_points = asmodians_points;
		this.balaur_points = balaur_points;
	}
	
	@Override
	protected void writeImpl(AionConnection aionConnection)
	{
		writeD(fortressId);
		writeD(elyos_points);
		writeD(asmodians_points);
		writeD(balaur_points);
	}
}
