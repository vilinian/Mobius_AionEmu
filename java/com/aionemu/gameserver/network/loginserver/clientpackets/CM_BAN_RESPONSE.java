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
package com.aionemu.gameserver.network.loginserver.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This packet handles the response sent to the client regarding a ban request.<br>
 * It informs the user whether their account has been successfully banned or if the action failed.
 * @author Watson
 */
public class CM_BAN_RESPONSE extends LsClientPacket
{
	/**
	 * This constructor initializes a new {@code CM_BAN_RESPONSE} packet.<br>
	 * It sets the operation code for the network communication.
	 * @param opCode The unique identifier for this specific operation.
	 */
	public CM_BAN_RESPONSE(int opCode)
	{
		super(opCode);
	}
	
	private byte type;
	private int accountId;
	private String ip;
	private int time;
	private int adminObjId;
	private boolean result;
	
	@Override
	public void readImpl()
	{
		type = (byte) readC();
		accountId = readD();
		ip = readS();
		time = readD();
		adminObjId = readD();
		result = readC() == 1;
	}
	
	@Override
	public void runImpl()
	{
		final Player admin = World.getInstance().findPlayer(adminObjId);
		
		if (admin == null)
		{
			return;
		}
		
		// Some messages stuff
		String message;
		if ((type == 1) || (type == 3))
		{
			if (result)
			{
				if (time < 0)
				{
					message = "Account ID " + accountId + " was successfully unbanned";
				}
				else if (time == 0)
				{
					message = "Account ID " + accountId + " was successfully banned";
				}
				else
				{
					message = "Account ID " + accountId + " was successfully banned for " + time + " minutes";
				}
			}
			else
			{
				message = "Error occurred while banning player's account";
			}
			
			PacketSendUtility.sendMessage(admin, message);
		}
		
		if ((type == 2) || (type == 3))
		{
			if (result)
			{
				if (time < 0)
				{
					message = "IP mask " + ip + " was successfully removed from block list";
				}
				else if (time == 0)
				{
					message = "IP mask " + ip + " was successfully added to block list";
				}
				else
				{
					message = "IP mask " + ip + " was successfully added to block list for " + time + " minutes";
				}
			}
			else
			{
				message = "Error occurred while adding IP mask " + ip;
			}
			
			PacketSendUtility.sendMessage(admin, message);
		}
	}
}
