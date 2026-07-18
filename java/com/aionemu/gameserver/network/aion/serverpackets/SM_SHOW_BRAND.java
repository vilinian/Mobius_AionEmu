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
 * This packet is used to display brand information to the client.<br>
 * It handles the synchronization of branding data between the server and the player.
 * @author Sweetkr
 */
public class SM_SHOW_BRAND extends AionServerPacket
{
	private final int brandId;
	private final int targetObjectId;
	
	/**
	 * This method creates a new {@code SM_SHOW_BRAND} packet.<br>
	 * It identifies which brand to display on a specific object.
	 * @param brandId The unique identifier for the brand.
	 * @param targetObjectId The ID of the object that will show the brand.
	 */
	public SM_SHOW_BRAND(int brandId, int targetObjectId)
	{
		this.brandId = brandId;
		this.targetObjectId = targetObjectId;
	}
	
	@Override // TODO GRP SIZE + FOR ?
	protected void writeImpl(AionConnection con)
	{
		writeH(0x01);
		writeD(0x01); // unk
		writeD(brandId);
		writeD(targetObjectId);
	}
}
