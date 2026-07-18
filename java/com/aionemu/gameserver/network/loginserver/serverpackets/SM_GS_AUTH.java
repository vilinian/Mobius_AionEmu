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
package com.aionemu.gameserver.network.loginserver.serverpackets;

import java.util.List;

import com.aionemu.commons.network.IPRange;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

/**
 * This packet handles the authentication process between the Game Server and the Login Server.<br>
 * It is used specifically for player registration requests.
 * @author -Nemesiss-
 */
public class SM_GS_AUTH extends LsServerPacket
{
	/**
	 * Creates a new instance of the {@code SM_GS_AUTH} packet.<br>
	 * This packet is used for authentication between the game server and the login server.
	 */
	public SM_GS_AUTH()
	{
		super(0x00);
	}
	
	@Override
	protected void writeImpl(LoginServerConnection con)
	{
		writeC(NetworkConfig.GAMESERVER_ID);
		writeC(IPConfig.getDefaultAddress().length);
		writeB(IPConfig.getDefaultAddress());
		
		final List<IPRange> ranges = IPConfig.getRanges();
		final int size = ranges.size();
		writeD(size);
		for (int i = 0; i < size; i++)
		{
			final IPRange ipRange = ranges.get(i);
			final byte[] min = ipRange.getMinAsByteArray();
			final byte[] max = ipRange.getMaxAsByteArray();
			writeC(min.length);
			writeB(min);
			writeC(max.length);
			writeB(max);
			writeC(ipRange.getAddress().length);
			writeB(ipRange.getAddress());
		}
		
		writeH(NetworkConfig.GAME_PORT);
		writeD(NetworkConfig.MAX_ONLINE_PLAYERS);
		writeS(NetworkConfig.LOGIN_PASSWORD);
	}
}
