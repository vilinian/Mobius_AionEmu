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
package com.aionemu.gameserver.network;

/**
 * This class manages the core network communication for the game server.<br>
 * It handles incoming and outgoing data packets between the client and the {@code NetworkManager}.<br>
 * It serves as a central hub for coordinating all network-related activities.
 * @author KID
 */
public class NetworkController
{
	private static NetworkController instance = new NetworkController();
	
	/**
	 * Provides access to the singleton instance of {@link NetworkController}.<br>
	 * Use this method to get the global network controller.
	 * @return The single instance of {@code NetworkController}.
	 */
	public static NetworkController getInstance()
	{
		return instance;
	}
	
	private byte serverCount = 1;
	
	/**
	 * Retrieves the current number of active servers.<br>
	 * This value is managed by {@code setServerCount}.
	 * @return The total count of servers as a {@code byte}.
	 */
	public byte getServerCount()
	{
		return serverCount;
	}
	
	/**
	 * Updates the total number of servers in the system.<br>
	 * This method sets the value used by {@code getServerCount}.
	 * @param count The new server count to set.
	 */
	public void setServerCount(byte count)
	{
		serverCount = count;
	}
}
