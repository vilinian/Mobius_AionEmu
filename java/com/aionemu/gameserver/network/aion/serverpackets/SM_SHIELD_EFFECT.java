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

import java.util.ArrayList;
import java.util.Collection;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.SiegeService;

/**
 * This packet handles the visual effects associated with shields in the game.<br>
 * It is sent to clients to trigger specific shield-related animations or graphics.
 * @author xTz, Source
 */
public class SM_SHIELD_EFFECT extends AionServerPacket
{
	private final Collection<SiegeLocation> locations;
	
	/**
	 * Creates a new {@link SM_SHIELD_EFFECT} packet.<br>
	 * This constructor initializes the shield effect with specific coordinates.
	 * @param locations A collection of {@code SiegeLocation} objects to be included in the effect.
	 */
	public SM_SHIELD_EFFECT(Collection<SiegeLocation> locations)
	{
		this.locations = locations;
	}
	
	/**
	 * Creates a shield effect at a specific location.<br>
	 * This method uses the {@link SiegeService} to find the correct coordinates.
	 * @param location The unique identifier for the siege area.
	 */
	public SM_SHIELD_EFFECT(int location)
	{
		locations = new ArrayList<>();
		locations.add(SiegeService.getInstance().getSiegeLocation(location));
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(locations.size());
		for (SiegeLocation loc : locations)
		{
			writeD(loc.getLocationId());
			writeC(loc.isUnderShield() ? 1 : 0);
		}
	}
}
