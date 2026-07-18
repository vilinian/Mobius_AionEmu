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

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet serves as the response to a {@code CM_GS_AUTH} request.<br>
 * It notifies the GameServer whether the registration was successful or provides details on what went wrong.
 * @author -Nemesiss-
 */
public class SM_GS_AUTH_RESPONSE extends GsServerPacket
{
	/**
	 * Response for Gameserver authentication
	 */
	private final GsAuthResponse response;
	
	/**
	 * Creates a new instance of {@code SM_GS_AUTH_RESPONSE}.<br>
	 * This constructor initializes the packet with the provided authentication data.
	 * @param response The {@link GsAuthResponse} object containing the auth results.
	 */
	public SM_GS_AUTH_RESPONSE(GsAuthResponse response)
	{
		this.response = response;
	}
	
	@Override
	protected void writeImpl(GsConnection con)
	{
		writeC(0);
		writeC(response.getResponseId());
		if (response.getResponseId() == 0)
		{
			writeC(GameServerTable.getGameServers().size());
		}
	}
}
