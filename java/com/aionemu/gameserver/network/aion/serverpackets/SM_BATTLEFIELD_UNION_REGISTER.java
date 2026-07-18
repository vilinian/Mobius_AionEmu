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
 * This packet handles the registration of a union in a battlefield.<br>
 * It is used to notify the client about the current state of the battlefield union.
 * @author Falke_34
 */
public class SM_BATTLEFIELD_UNION_REGISTER extends AionServerPacket
{
	
	int requestId;
	boolean isRegister;
	
	/**
	 * Creates a new instance of the {@code SM_BATTLEFIELD_UNION_REGISTER} packet.<br>
	 * This packet handles registration for battlefield unions.
	 * @param requestId The unique identifier for the request.
	 * @param isRegister A boolean flag indicating if the action is a registration.
	 */
	public SM_BATTLEFIELD_UNION_REGISTER(int requestId, boolean isRegister)
	{
		this.requestId = requestId;
		this.isRegister = isRegister;
	}
	
	@Override
	protected void writeImpl(AionConnection aionConnection)
	{
		writeD(requestId);
		writeC(isRegister ? 0 : 1);
	}
}
