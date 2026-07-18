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
package com.aionemu.commons.network;

/**
 * This class represents a task responsible for handling player disconnections.<br>
 * It is designed to be executed by the {@link com.aionemu.commons.network.DisconnectionThreadPool}.
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.DisconnectionThreadPool
 */
public class DisconnectionTask implements Runnable
{
	/**
	 * Connection that onDisconnect() method will be executed by <code>DisconnectionThreadPool</code>
	 * @see com.aionemu.commons.network.DisconnectionThreadPool
	 */
	private final AConnection connection;
	
	/**
	 * Creates a new {@link DisconnectionTask}.<br>
	 * This task handles the disconnection logic for a specific connection.<br>
	 * It is intended to be executed by the {@code DisconnectionThreadPool}.
	 * @param connection The {@link AConnection} object to be disconnected.
	 */
	public DisconnectionTask(AConnection connection)
	{
		this.connection = connection;
	}
	
	@Override
	public void run()
	{
		connection.onDisconnect();
	}
}
