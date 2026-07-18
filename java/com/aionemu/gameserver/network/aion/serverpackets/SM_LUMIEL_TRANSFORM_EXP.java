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
 * This packet handles the transformation experience for the character Lumiel.<br>
 * It is used to synchronize specific visual or state changes related to this entity.
 */
public class SM_LUMIEL_TRANSFORM_EXP extends AionServerPacket
{
	private final int lumielId;
	private final long exp;
	
	/**
	 * Creates a new {@code SM_LUMIEL_TRANSFORM_EXP} packet.<br>
	 * This packet handles experience updates for a specific Lumiel entity.
	 * @param lumielId The unique identifier for the Lumiel.
	 * @param exp The amount of experience to be granted.
	 */
	public SM_LUMIEL_TRANSFORM_EXP(int lumielId, long exp)
	{
		this.lumielId = lumielId;
		this.exp = exp;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(lumielId);
		writeD((int) exp);
	}
}
