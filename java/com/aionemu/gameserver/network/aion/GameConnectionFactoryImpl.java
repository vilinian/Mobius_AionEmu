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
package com.aionemu.gameserver.network.aion;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.ConnectionFactory;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.sequrity.FloodManager;
import com.aionemu.gameserver.network.sequrity.FloodManager.Result;

/**
 * This class is the concrete implementation of {@link ConnectionFactory}.<br>
 * It is responsible for creating new {@link AConnection} instances for game clients.<br>
 * Use this factory to initialize and manage incoming network connections.
 * @author -Nemesiss-
 */
public class GameConnectionFactoryImpl implements ConnectionFactory
{
	private final Logger log = LoggerFactory.getLogger(GameConnectionFactoryImpl.class);
	private FloodManager floodAcceptor;
	
	/**
	 * Constructs a new instance of {@link GameConnectionFactoryImpl}.<br>
	 * This constructor initializes the {@code floodAcceptor} if enabled in the configuration.
	 */
	public GameConnectionFactoryImpl()
	{
		if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			floodAcceptor = new FloodManager(NetworkConfig.Flood_Tick, new FloodManager.FloodFilter(NetworkConfig.Flood_SWARN, NetworkConfig.Flood_SReject, NetworkConfig.Flood_STick), // short period
				new FloodManager.FloodFilter(NetworkConfig.Flood_LWARN, NetworkConfig.Flood_LReject, NetworkConfig.Flood_LTick)); // long period
		}
	}
	
	/**
	 * Creates a new {@link AConnection} instance from a provided socket.<br>
	 * This method also checks for potential flooding before establishing the connection.
	 * @param socket The {@code SocketChannel} used to connect to the client.
	 * @param dispatcher The {@code Dispatcher} used to handle network events.
	 * @return A new {@code AConnection} object, or {@code null} if the connection was rejected.
	 * @throws IOException If an error occurs during the connection process.
	 */
	@Override
	public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException
	{
		if (NetworkConfig.ENABLE_FLOOD_CONNECTIONS)
		{
			final String host = socket.socket().getInetAddress().getHostAddress();
			final Result isFlooding = floodAcceptor.isFlooding(host, true);
			switch (isFlooding)
			{
				case REJECTED:
				{
					log.warn("Rejected connection from " + host);
					socket.close();
					return null;
				}
				case WARNED:
				{
					log.warn("Connection over warn limit from " + host);
					break;
				}
				default:
					break;
			}
		}
		
		return new AionConnection(socket, dispatcher);
	}
}
