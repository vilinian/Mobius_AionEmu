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
package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.configs.main.DualBoxConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.world.World;

/**
 * This class provides mechanisms to prevent players from logging into multiple accounts simultaneously.<br>
 * It checks for active sessions and handles {@link SM_QUIT_RESPONSE} logic to enforce account limits.
 */
public class DualBoxProtection
{
	static int count = 0;
	
	/**
	 * Verifies if a {@link Player} is allowed to stay connected.<br>
	 * This method checks for multiple connections from the same IP address.<br>
	 * It disconnects the player if they exceed the limit set in {@code DualBoxConfig}.<br>
	 * Players on the whitelist are exempt from this check.
	 * @param player The {@code Player} object to validate.
	 */
	public static void checkConnection(Player player)
	{
		count = 0;
		for (Player p : World.getInstance().getAllPlayers())
		{
			if (p.getClientConnection().getIP().equals(player.getClientConnection().getIP()))
			{
				count++;
			}
		}
		
		if ((count > DualBoxConfig.DUALBOX_ALLOWED_CHARS) && !isInWhiteList(player.getClientConnection().getIP()))
		{
			player.getClientConnection().close(new SM_QUIT_RESPONSE(), true);
		}
	}
	
	/**
	 * Checks if a specific IP address is allowed in the whitelist.<br>
	 * This method looks up the {@code address} against the configuration list.
	 * @param address The IP address to check.
	 * @return {@code true} if the address is found, otherwise {@code false}.
	 */
	private static boolean isInWhiteList(String address)
	{
		final String[] list = DualBoxConfig.DUALBOX_WHITELIST.split(";");
		for (String ip : list)
		{
			if (ip.equals(address))
			{
				return true;
			}
		}
		
		return false;
	}
}
