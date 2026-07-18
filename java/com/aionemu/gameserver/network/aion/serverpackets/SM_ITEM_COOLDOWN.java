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

import java.util.Map;

import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is sent to the client to notify it about an item's cooldown status.<br>
 * It provides information regarding the remaining time for a specific {@link com.aionemu.gameserver.model.items.ItemCooldown}.
 * @author ATracer
 */
public class SM_ITEM_COOLDOWN extends AionServerPacket
{
	private final Map<Integer, ItemCooldown> cooldowns;
	
	/**
	 * Creates a new {@link SM_ITEM_COOLDOWN} packet.<br>
	 * This method initializes the packet with a map of item cooldowns.
	 * @param cooldowns A {@code Map} containing {@code Integer} IDs and their corresponding {@link ItemCooldown} objects.
	 */
	public SM_ITEM_COOLDOWN(Map<Integer, ItemCooldown> cooldowns)
	{
		this.cooldowns = cooldowns;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(cooldowns.size());
		final long currentTime = System.currentTimeMillis();
		for (Map.Entry<Integer, ItemCooldown> entry : cooldowns.entrySet())
		{
			writeH(entry.getKey());
			final int left = (int) ((entry.getValue().getReuseTime() - currentTime) / 1000);
			writeD(left > 0 ? left : 0);
			writeD(entry.getValue().getUseDelay());
		}
	}
}
