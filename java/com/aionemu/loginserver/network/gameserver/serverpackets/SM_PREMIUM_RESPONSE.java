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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet handles the response sent to the client regarding their premium status.<br>
 * It informs the user whether their account has active premium privileges.
 * @author KID
 */
public class SM_PREMIUM_RESPONSE extends GsServerPacket
{
	private final int requestId;
	private final int result;
	private final long points;
	
	/**
	 * Creates a new {@code SM_PREMIUM_RESPONSE} packet.<br>
	 * This constructor initializes the response with specific data.
	 * @param requestId The unique identifier for the request.
	 * @param result The status code of the operation.
	 * @param points The number of premium points associated with the response.
	 */
	public SM_PREMIUM_RESPONSE(int requestId, int result, long points)
	{
		this.requestId = requestId;
		this.result = result;
		this.points = points;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(10);
		writeD(requestId);
		writeD(result);
		writeQ(points);
	}
}
