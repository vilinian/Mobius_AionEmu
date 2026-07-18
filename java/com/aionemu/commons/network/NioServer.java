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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.options.Assertion;

/**
 * This class manages a {@code NIO} server instance to handle network connections.<br>
 * It listens for incoming traffic on specified addresses and processes them using non-blocking I/O.
 * @author -Nemesiss-
 */
public class NioServer
{
	/**
	 * Logger for NioServer
	 */
	private static final Logger log = LoggerFactory.getLogger(NioServer.class.getName());
	
	/**
	 * The channels on which we'll accept connections
	 */
	private final List<SelectionKey> serverChannelKeys = new ArrayList<>();
	
	/**
	 * Dispatcher that will accept connections
	 */
	private Dispatcher acceptDispatcher;
	/**
	 * Useful int to load balance connections between Dispatchers
	 */
	private int currentReadWriteDispatcher;
	/**
	 * Read Write Dispatchers
	 */
	private Dispatcher[] readWriteDispatchers;
	
	/**
	 * DisconnectionThreadPool that will be used to execute DisconnectionTask.
	 */
	private final Executor dcPool;
	
	/**
	 * 
	 */
	private final int readWriteThreads;
	/**
	 * 
	 */
	private final ServerCfg[] cfgs;
	
	/**
	 * Creates a new instance of {@link NioServer}.<br>
	 * This constructor initializes the server with specific thread counts and configurations.<br>
	 * It also validates if assertions are enabled for unstable builds.
	 * @param readWriteThreads The number of threads used to handle network read and write operations.
	 * @param cfgs A variable number of {@link ServerCfg} objects containing server settings.
	 */
	public NioServer(int readWriteThreads, ServerCfg... cfgs)
	{
		/**
		 * Test if this build should use assertion and enforce it. If NetworkAssertion == false javac will remove this code block
		 */
		if (Assertion.NetworkAssertion)
		{
			if (!NioServer.class.desiredAssertionStatus())
			{
				throw new RuntimeException("This is unstable build. Assertion must be enabled! Add -ea to your start script or consider using stable build instead.");
			}
		}
		
		dcPool = ThreadPoolManager.getInstance();
		this.readWriteThreads = readWriteThreads;
		this.cfgs = cfgs;
	}
	
	/**
	 * Initializes the server and starts listening for incoming connections.<br>
	 * This method sets up the internal dispatchers and binds to the configured addresses.<br>
	 * It will throw an {@code Error} if any part of the initialization fails.
	 */
	public void connect()
	{
		try
		{
			initDispatchers(readWriteThreads, dcPool);
			
			/** Create a new non-blocking server socket channel for clients */
			for (ServerCfg cfg : cfgs)
			{
				final ServerSocketChannel serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);
				
				/** Bind the server socket to the specified address and port */
				InetSocketAddress isa;
				if ("*".equals(cfg.hostName))
				{
					isa = new InetSocketAddress(cfg.port);
					log.info("Server listening on all available IPs on Port " + cfg.port + " for " + cfg.connectionName);
				}
				else
				{
					isa = new InetSocketAddress(cfg.hostName, cfg.port);
					log.info("Server listening on IP: " + cfg.hostName + " Port " + cfg.port + " for " + cfg.connectionName);
				}
				
				serverChannel.socket().bind(isa);
				
				/**
				 * Register the server socket channel, indicating an interest in accepting new connections
				 */
				final SelectionKey acceptKey = getAcceptDispatcher().register(serverChannel, SelectionKey.OP_ACCEPT, new Acceptor(cfg.factory, this));
				serverChannelKeys.add(acceptKey);
			}
		}
		catch (Exception e)
		{
			log.error("NioServer Initialization Error: " + e, e);
			throw new Error("NioServer Initialization Error!");
		}
	}
	
	/**
	 * Retrieves the dispatcher responsible for accepting new connections.<br>
	 * This is used to manage incoming network requests.
	 * @return the {@code Dispatcher} instance that handles connection acceptance.
	 */
	public Dispatcher getAcceptDispatcher()
	{
		return acceptDispatcher;
	}
	
	/**
	 * Retrieves the next available {@link Dispatcher} for handling read and write operations.<br>
	 * This method uses a round-robin approach to balance the load between multiple dispatchers.<br>
	 * If no specific read-write dispatchers are configured, it returns the {@code acceptDispatcher}.
	 * @return The next {@code Dispatcher} in the rotation.
	 */
	public Dispatcher getReadWriteDispatcher()
	{
		if (readWriteDispatchers == null)
		{
			return acceptDispatcher;
		}
		
		if (readWriteDispatchers.length == 1)
		{
			return readWriteDispatchers[0];
		}
		
		if (currentReadWriteDispatcher >= readWriteDispatchers.length)
		{
			currentReadWriteDispatcher = 0;
		}
		
		return readWriteDispatchers[currentReadWriteDispatcher++];
	}
	
	/**
	 * Initializes the network dispatchers for the server.<br>
	 * It sets up either a single or multiple {@code Dispatcher} instances based on the thread count.
	 * @param readWriteThreads The number of threads to use for handling read and write operations.
	 * @param dcPool The {@code Executor} used for disconnection tasks.
	 * @throws IOException If an error occurs during dispatcher initialization.
	 */
	private void initDispatchers(int readWriteThreads, Executor dcPool) throws IOException
	{
		if (readWriteThreads < 1)
		{
			acceptDispatcher = new AcceptReadWriteDispatcherImpl("AcceptReadWrite Dispatcher", dcPool);
			acceptDispatcher.start();
		}
		else
		{
			acceptDispatcher = new AcceptDispatcherImpl("Accept Dispatcher");
			acceptDispatcher.start();
			
			readWriteDispatchers = new Dispatcher[readWriteThreads];
			for (int i = 0; i < readWriteDispatchers.length; i++)
			{
				readWriteDispatchers[i] = new AcceptReadWriteDispatcherImpl("ReadWrite-" + i + " Dispatcher", dcPool);
				readWriteDispatchers[i].start();
			}
		}
	}
	
	/**
	 * Returns the total number of active network connections.<br>
	 * This method calculates the sum of keys from all {@link Dispatcher} selectors.
	 * @return The current count of active connections.
	 */
	public int getActiveConnections()
	{
		int count = 0;
		if (readWriteDispatchers != null)
		{
			for (Dispatcher d : readWriteDispatchers)
			{
				count += d.selector().keys().size();
			}
		}
		else
		{
			count = acceptDispatcher.selector().keys().size() - serverChannelKeys.size();
		}
		
		return count;
	}
	
	/**
	 * Shuts down the server and closes all active connections.<br>
	 * This method cancels all {@code SelectionKey} objects in the {@code serverChannelKeys} list.<br>
	 * It also calls {@code closeAll} to force disconnect all remaining users.
	 */
	public void shutdown()
	{
		log.info("Closing ServerChannels...");
		try
		{
			for (SelectionKey key : serverChannelKeys)
			{
				key.cancel();
			}
			
			log.info("ServerChannel closed.");
		}
		catch (Exception e)
		{
			log.error("Error during closing ServerChannel, " + e, e);
		}
		
		notifyServerClose();
		/** Wait 5s */
		try
		{
			Thread.sleep(1000);
		}
		catch (Throwable t)
		{
			log.warn("Nio thread was interrupted during shutdown", t);
		}
		
		log.info(" Active connections: " + getActiveConnections());
		
		/** DC all */
		log.info("Forced Disconnecting all connections...");
		closeAll();
		log.info(" Active connections: " + getActiveConnections());
		
		// dcPool.waitForDisconnectionTasks();
		
		/** Wait 5s */
		try
		{
			Thread.sleep(1000);
		}
		catch (Throwable t)
		{
			log.warn("Nio thread was interrupted during shutdown", t);
		}
	}
	
	/**
	 * Sends a close notification to all active connections.<br>
	 * It iterates through the {@code readWriteDispatchers} or the {@code acceptDispatcher}.<br>
	 * Each {@link AConnection} found in the selection keys is notified via its {@code onServerClose()} method.
	 */
	private void notifyServerClose()
	{
		if (readWriteDispatchers != null)
		{
			for (Dispatcher d : readWriteDispatchers)
			{
				for (SelectionKey key : d.selector().keys())
				{
					if (key.attachment() instanceof AConnection)
					{
						((AConnection) key.attachment()).onServerClose();
					}
				}
			}
		}
		else
		{
			for (SelectionKey key : acceptDispatcher.selector().keys())
			{
				if (key.attachment() instanceof AConnection)
				{
					((AConnection) key.attachment()).onServerClose();
				}
			}
		}
	}
	
	/**
	 * Closes all active connections.<br>
	 * This method iterates through the {@code readWriteDispatchers} or the {@code acceptDispatcher}.<br>
	 * It identifies every {@code AConnection} and calls its {@code close(true)} method.
	 */
	private void closeAll()
	{
		if (readWriteDispatchers != null)
		{
			for (Dispatcher d : readWriteDispatchers)
			{
				for (SelectionKey key : d.selector().keys())
				{
					if (key.attachment() instanceof AConnection)
					{
						((AConnection) key.attachment()).close(true);
					}
				}
			}
		}
		else
		{
			for (SelectionKey key : acceptDispatcher.selector().keys())
			{
				if (key.attachment() instanceof AConnection)
				{
					((AConnection) key.attachment()).close(true);
				}
			}
		}
	}
}
