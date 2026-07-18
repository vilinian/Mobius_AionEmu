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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * This class handles incoming socket connections dispatched by the {@link Dispatcher}.<br>
 * It creates a new {@link AConnection} using {@code create} for each accepted socket.<br>
 * The new connection is then registered with a ReadWrite {@link Dispatcher} selector for I/O read operations.
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.Dispatcher
 * @see java.nio.channels.ServerSocketChannel
 * @see java.nio.channels.SelectionKey
 * @see java.nio.channels.SocketChannel
 * @see java.nio.channels.Selector
 * @see com.aionemu.commons.network.AConnection
 * @see com.aionemu.commons.network.ConnectionFactory
 * @see com.aionemu.commons.network.NioServer
 */
public class Acceptor
{
	/**
	 * <code>ConnectionFactory</code> that will create new <code>AConnection</code>
	 * @see com.aionemu.commons.network.ConnectionFactory
	 * @see com.aionemu.commons.network.AConnection
	 */
	private final ConnectionFactory factory;
	
	/**
	 * <code>NioServer</code> that created this Acceptor.
	 * @see com.aionemu.commons.network.NioServer
	 */
	private final NioServer nioServer;
	
	/**
	 * Creates a new instance of {@link Acceptor}.<br>
	 * This constructor initializes the required components for handling connections.
	 * @param factory The {@code ConnectionFactory} used to create new connections.
	 * @param nioServer The {@code NioServer} that owns this acceptor.
	 */
	Acceptor(ConnectionFactory factory, NioServer nioServer)
	{
		this.factory = factory;
		this.nioServer = nioServer;
	}
	
	/**
	 * Processes an incoming connection from a {@link SelectionKey}.<br>
	 * This method accepts the socket and creates a new {@code AConnection}.<br>
	 * It then registers the connection with the read-write dispatcher.
	 * @param key The {@code SelectionKey} representing the server socket channel.
	 * @throws IOException If an I/O error occurs during the accept process.
	 */
	public void accept(SelectionKey key) throws IOException
	{
		/**
		 * For an accept to be pending the channel must be a server socket channel.
		 */
		final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		/** Accept the connection and make it non-blocking */
		final SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		
		final Dispatcher dispatcher = nioServer.getReadWriteDispatcher();
		final AConnection con = factory.create(socketChannel, dispatcher);
		
		if (con == null)
		{
			return;
		}
		
		// register
		dispatcher.register(socketChannel, SelectionKey.OP_READ, con);
		
		// notify initialized :)
		con.initialized();
	}
}
