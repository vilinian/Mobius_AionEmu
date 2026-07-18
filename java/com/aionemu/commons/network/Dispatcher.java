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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.options.Assertion;

/**
 * This class handles the distribution of {@link SelectionKey} objects identified by a {@link Selector}.<br>
 * It processes network events and routes them to the appropriate handlers.<br>
 * It extends {@code Thread} to perform these operations asynchronously.
 * @author -Nemesiss-
 */
public abstract class Dispatcher extends Thread
{
	/**
	 * Logger for Dispatcher
	 */
	private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
	
	/**
	 * Selector thats selecting ready keys.
	 */
	Selector selector;
	
	/**
	 * Executor on witch disconnection tasks will be executed.
	 */
	private final Executor dcPool;
	/**
	 * Object on witch register vs selector.select are synchronized
	 */
	private final Object gate = new Object();
	
	/**
	 * Creates a new {@link Dispatcher} instance.<br>
	 * This constructor initializes the internal {@code selector}.<br>
	 * It sets up the thread name and the execution pool for disconnections.
	 * @param name The name to assign to the dispatcher thread.
	 * @param dcPool The {@code Executor} used for handling disconnection tasks.
	 * @throws IOException If an error occurs while opening the {@code Selector}.
	 */
	public Dispatcher(String name, Executor dcPool) throws IOException
	{
		super(name);
		selector = SelectorProvider.provider().openSelector();
		this.dcPool = dcPool;
	}
	
	/**
	 * Add connection to pendingClose list, so this connection will be closed by this <code>Dispatcher</code> as soon as possible.
	 * @param con
	 * @see com.aionemu.commons.network.Dispatcher#closeConnection(com.aionemu.commons.network.AConnection)
	 */
	abstract void closeConnection(AConnection con);
	
	/**
	 * Dispatch Selected keys and process pending close.
	 * @throws IOException
	 */
	abstract void dispatch() throws IOException;
	
	/**
	 * Retrieves the {@link Selector} used by this dispatcher.<br>
	 * This object manages the selection of ready channels.
	 * @return The current {@code Selector} instance.
	 */
	public Selector selector()
	{
		return selector;
	}
	
	@Override
	public void run()
	{
		for (;;)
		{
			try
			{
				dispatch();
				
				synchronized (gate)
				{
				}
			}
			catch (Exception e)
			{
				log.error("Dispatcher error! " + e, e);
			}
		}
	}
	
	/**
	 * Registers a {@link SelectableChannel} with the internal selector.<br>
	 * This method links the channel to an {@link AConnection} object.<br>
	 * It also wakes up the selector to ensure it processes the new registration.
	 * @param ch The channel to register.
	 * @param ops The set of operations to monitor on the channel.
	 * @param att The connection attribute associated with this channel.
	 * @throws IOException If an I/O error occurs during registration.
	 */
	public void register(SelectableChannel ch, int ops, AConnection att) throws IOException
	{
		synchronized (gate)
		{
			selector.wakeup();
			att.setKey(ch.register(selector, ops, att));
		}
	}
	
	/**
	 * Registers a {@link SelectableChannel} with the internal {@code Selector}.<br>
	 * This method ensures thread safety by synchronizing on the gate object.<br>
	 * It wakes up the selector before completing the registration.
	 * @param ch The channel to register.
	 * @param ops The set of operations to monitor.
	 * @param att The attachment associated with the key.
	 * @return The {@link SelectionKey} for the registered channel.
	 * @throws IOException If an I/O error occurs during registration.
	 */
	public SelectionKey register(SelectableChannel ch, int ops, Acceptor att) throws IOException
	{
		synchronized (gate)
		{
			selector.wakeup();
			return ch.register(selector, ops, att);
		}
	}
	
	/**
	 * Handles an incoming connection request.<br>
	 * This method retrieves the {@link Acceptor} from the {@code SelectionKey}.<br>
	 * It then calls the {@code accept} method on that object.<br>
	 * Any exceptions during this process are logged as errors.
	 * @param key The {@code SelectionKey} representing the ready channel.
	 */
	void accept(SelectionKey key)
	{
		try
		{
			((Acceptor) key.attachment()).accept(key);
		}
		catch (Exception e)
		{
			log.error("Error while accepting connection: +" + e, e);
		}
	}
	
	/**
	 * Reads data from the channel associated with the given {@code SelectionKey}.<br>
	 * It processes the incoming bytes into messages using the {@code ByteBuffer)} method.<br>
	 * If an error occurs or the connection is closed, it calls {@code closeConnectionImpl}.
	 * @param key The {@code SelectionKey} representing the channel to read from.
	 */
	void read(SelectionKey key)
	{
		final SocketChannel socketChannel = (SocketChannel) key.channel();
		final AConnection con = (AConnection) key.attachment();
		
		final ByteBuffer rb = con.readBuffer;
		
		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if (Assertion.NetworkAssertion)
		{
			assert con.readBuffer.hasRemaining();
		}
		
		/** Attempt to read off the channel */
		int numRead;
		try
		{
			numRead = socketChannel.read(rb);
		}
		catch (IOException e)
		{
			closeConnectionImpl(con);
			return;
		}
		
		if (numRead == -1)
		{
			/**
			 * Remote entity shut the socket down cleanly. Do the same from our end and cancel the channel.
			 */
			closeConnectionImpl(con);
			return;
		}
		else if (numRead == 0)
		{
			return;
		}
		
		rb.flip();
		while ((rb.remaining() > 2) && (rb.remaining() >= rb.getShort(rb.position())))
		{
			/** got full message */
			if (!parse(con, rb))
			{
				closeConnectionImpl(con);
				return;
			}
		}
		
		if (rb.hasRemaining())
		{
			con.readBuffer.compact();
			
			/**
			 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
			 */
			if (Assertion.NetworkAssertion)
			{
				assert con.readBuffer.hasRemaining();
			}
		}
		else
		{
			rb.clear();
		}
	}
	
	/**
	 * Parses the incoming data from a buffer.<br>
	 * It extracts the message based on the specified size.<br>
	 * The method then passes the data to {@code processData}.
	 * @param con The connection object used to process the data.
	 * @param buf The buffer containing the raw bytes to be parsed.
	 * @return {@code true} if the data was processed successfully, or {@code false} otherwise.
	 */
	private boolean parse(AConnection con, ByteBuffer buf)
	{
		short sz = 0;
		try
		{
			sz = buf.getShort();
			if (sz > 1)
			{
				sz -= 2;
			}
			
			final ByteBuffer b = buf.slice().limit(sz);
			b.order(ByteOrder.LITTLE_ENDIAN);
			/** read message fully */
			buf.position(buf.position() + sz);
			
			return con.processData(b);
		}
		catch (IllegalArgumentException e)
		{
			log.warn("Error on parsing input from client - account: " + con + " packet size: " + sz + " real size:" + buf.remaining(), e);
			return false;
		}
	}
	
	/**
	 * Writes pending data from an {@link AConnection} to the underlying channel.<br>
	 * This method handles partial writes and manages the {@code OP_WRITE} interest.<br>
	 * It also closes the connection if it is marked as pending close.
	 * @param key The {@link SelectionKey} representing the channel and its associated connection.
	 */
	void write(SelectionKey key)
	{
		final SocketChannel socketChannel = (SocketChannel) key.channel();
		final AConnection con = (AConnection) key.attachment();
		
		int numWrite;
		final ByteBuffer wb = con.writeBuffer;
		/** We have not writted data */
		if (wb.hasRemaining())
		{
			try
			{
				numWrite = socketChannel.write(wb);
			}
			catch (IOException e)
			{
				closeConnectionImpl(con);
				return;
			}
			
			if (numWrite == 0)
			{
				log.info("Write " + numWrite + " ip: " + con.getIP());
				return;
			}
			
			/** Again not all data was send */
			if (wb.hasRemaining())
			{
				return;
			}
		}
		
		while (true)
		{
			wb.clear();
			final boolean writeFailed = !con.writeData(wb);
			
			if (writeFailed)
			{
				wb.limit(0);
				break;
			}
			
			/** Attempt to write to the channel */
			try
			{
				numWrite = socketChannel.write(wb);
			}
			catch (IOException e)
			{
				closeConnectionImpl(con);
				return;
			}
			
			if (numWrite == 0)
			{
				log.info("Write " + numWrite + " ip: " + con.getIP());
				return;
			}
			
			/** not all data was send */
			if (wb.hasRemaining())
			{
				return;
			}
		}
		
		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if (Assertion.NetworkAssertion)
		{
			assert !wb.hasRemaining();
		}
		
		/**
		 * We wrote away all data, so we're no longer interested in writing on this socket.
		 */
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		
		/**
		 * We wrote all data so we can close connection that is "PandingClose"
		 */
		if (con.isPendingClose())
		{
			closeConnectionImpl(con);
		}
	}
	
	/**
	 * Closes the network connection for a given {@code AConnection}.<br>
	 * This method handles the internal logic of disconnecting a client.<br>
	 * It schedules a {@link DisconnectionTask} to be executed by the pool.
	 * @param con The {@code AConnection} object that needs to be closed.
	 */
	protected void closeConnectionImpl(AConnection con)
	{
		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if (Assertion.NetworkAssertion)
		{
			assert Thread.currentThread() == this;
		}
		
		if (con.onlyClose())
		{
			// dcPool.scheduleDisconnection(new DisconnectionTask(con),
			// con.getDisconnectionDelay());
			dcPool.execute(new DisconnectionTask(con));
		}
	}
}
