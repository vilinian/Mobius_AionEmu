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
package com.aionemu.gameserver.network.loginserver;

import java.nio.channels.SocketChannel;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.ConnectionFactory;
import com.aionemu.commons.network.Dispatcher;

/**
 * This class is the concrete implementation of {@link ConnectionFactory}.<br>
 * It is responsible for creating new {@link AConnection} instances for the login server.<br>
 * It handles the initialization of connections specifically for the Aion game environment.
 * @author -Nemesiss-
 */
public class LoginConnectionFactoryImpl implements ConnectionFactory
{
	/**
	 * Create a new {@link com.aionemu.commons.network.AConnection AConnection} instance.<br>
	 * @param socket that new {@link com.aionemu.commons.network.AConnection AConnection} instance will represent.<br>
	 * @param dispatcher to witch new connection will be registered.<br>
	 * @return a new instance of {@link com.aionemu.commons.network.AConnection AConnection}<br>
	 * @see com.aionemu.commons.network.AConnection
	 * @see com.aionemu.commons.network.Dispatcher
	 */
	
	/**
	 * Creates a new {@link AConnection} instance.<br>
	 * This method initializes the connection using the provided socket and dispatcher.
	 * @param socket The {@code SocketChannel} to be wrapped by the new connection.
	 * @param dispatcher The {@code Dispatcher} where the new connection will be registered.
	 * @return A new instance of {@link AConnection}.
	 */
	@Override
	public AConnection create(SocketChannel socket, Dispatcher dispatcher)
	{
		return new LoginServerConnection(socket, dispatcher);
	}
}
