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
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.aionemu.commons.options.Assertion;

/**
 * Represents a network connection associated with a server socket.<br>
 * This class is created by {@code ConnectionFactory} and attached to a {@link SelectionKey}.<br>
 * The key is registered with a {@code Selector} to handle I/O read and write operations.
 * @author -Nemesiss-
 */
public abstract class AConnection
{
	/**
	 * SocketChannel representing this connection
	 */
	private final SocketChannel socketChannel;
	/**
	 * Dispatcher [AcceptReadWriteDispatcherImpl] to witch this connection SelectionKey is registered.
	 */
	private final Dispatcher dispatcher;
	/**
	 * SelectionKey representing this connection.
	 */
	private SelectionKey key;
	/**
	 * True if this connection should be closed after sending last server packet.
	 */
	protected boolean pendingClose;
	/**
	 * True if OnDisconnect() method should be called immediately after this connection was closed.
	 */
	protected boolean isForcedClosing;
	/**
	 * True if this connection is already closed.
	 */
	protected boolean closed;
	/**
	 * Object on witch some methods are synchronized
	 */
	protected final Object guard = new Object();
	/**
	 * ByteBuffer for io write.
	 */
	public final ByteBuffer writeBuffer;
	/**
	 * ByteBuffer for io read.
	 */
	public final ByteBuffer readBuffer;
	
	/**
	 * Caching ip address to make sure that {@code getIP} method works even after disconnection
	 */
	private final String ip;
	
	/**
	 * Used only for PacketProcessor synchronization purpose
	 */
	private boolean locked = false;
	
	/**
	 * Creates a new instance of an {@link AConnection}.<br>
	 * This constructor initializes the buffers and captures the remote IP address.
	 * @param sc The {@code SocketChannel} used for this connection.
	 * @param d The {@code Dispatcher} that will handle I/O operations.
	 * @param rbSize The size of the read buffer in bytes.
	 * @param wbSize The size of the write buffer in bytes.
	 */
	public AConnection(SocketChannel sc, Dispatcher d, int rbSize, int wbSize)
	{
		socketChannel = sc;
		dispatcher = d;
		writeBuffer = ByteBuffer.allocate(wbSize);
		writeBuffer.flip();
		writeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		readBuffer = ByteBuffer.allocate(rbSize);
		readBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		ip = socketChannel.socket().getInetAddress().getHostAddress();
	}
	
	/**
	 * Sets the {@link SelectionKey} for this connection.<br>
	 * This links the connection to a specific selector key.
	 * @param key The {@code SelectionKey} to assign to this connection.
	 */
	void setKey(SelectionKey key)
	{
		this.key = key;
	}
	
	/**
	 * Enables the write interest for this connection.<br>
	 * This method updates the {@link SelectionKey} to include {@code OP_WRITE}.<br>
	 * It also calls {@code wakeup()} on the associated selector.
	 */
	protected void enableWriteInterest()
	{
		if (key.isValid())
		{
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			key.selector().wakeup();
		}
	}
	
	/**
	 * Retrieves the {@link Dispatcher} associated with this connection.<br>
	 * This dispatcher handles the I/O operations for the socket.
	 * @return the {@code Dispatcher} instance.
	 */
	Dispatcher getDispatcher()
	{
		return dispatcher;
	}
	
	/**
	 * Retrieves the {@code SocketChannel} associated with this connection.<br>
	 * This channel is used for network communication.
	 * @return The current {@code SocketChannel}.
	 */
	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}
	
	/**
	 * Closes the current connection.<br>
	 * This method updates the {@code isForcedClosing} state.<br>
	 * It then notifies the dispatcher to handle the closure.
	 * @param forced Set to {@code true} if the connection should be closed immediately.
	 */
	public void close(boolean forced)
	{
		synchronized (guard)
		{
			if (isWriteDisabled())
			{
				return;
			}
			
			isForcedClosing = forced;
			getDispatcher().closeConnection(this);
		}
	}
	
	/**
	 * Closes the underlying {@code SocketChannel} and cancels the associated {@code SelectionKey}.<br>
	 * This method updates the {@code closed} state to {@code true}.<br>
	 * It returns {@code false} if the connection is already closed.<br>
	 * Otherwise, it returns {@code true} after successfully closing the resources.
	 * @return {@code true} if the connection was successfully closed, or {@code false} if it was already closed.
	 */
	boolean onlyClose()
	{
		/**
		 * Test if this build should use assertion. If NetworkAssertion == false javac will remove this code block
		 */
		if (Assertion.NetworkAssertion)
		{
			assert Thread.currentThread() == dispatcher;
		}
		
		synchronized (guard)
		{
			if (closed)
			{
				return false;
			}
			
			try
			{
				if (socketChannel.isOpen())
				{
					socketChannel.close();
					key.attach(null);
					key.cancel();
				}
				
				closed = true;
			}
			catch (IOException ignored)
			{
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the connection is waiting to be closed.<br>
	 * It returns {@code true} only if {@code pendingClose} is set and the connection is not yet {@code closed}.
	 * @return {@code true} if the connection is pending closure, {@code false} otherwise.
	 */
	boolean isPendingClose()
	{
		return pendingClose && !closed;
	}
	
	/**
	 * Checks if the connection is currently unable to perform write operations.<br>
	 * This happens if the connection is {@code closed} or has a {@code pendingClose} status.
	 * @return {@code true} if writing is disabled, {@code false} otherwise.
	 */
	protected boolean isWriteDisabled()
	{
		return pendingClose || closed;
	}
	
	/**
	 * Retrieves the IP address of this connection.<br>
	 * This value is cached to remain available after a disconnection.
	 * @return The {@code String} representation of the IP address.
	 */
	public String getIP()
	{
		return ip;
	}
	
	/**
	 * Attempts to lock the connection for thread-safe operations.<br>
	 * This method checks if the {@code locked} flag is currently {@code false}.<br>
	 * If it can be locked, it sets the flag to {@code true}.
	 * @return {@code true} if the lock was successfully acquired, or {@code false} if it was already held.
	 */
	boolean tryLockConnection()
	{
		if (locked)
		{
			return false;
		}
		
		return locked = true;
	}
	
	/**
	 * Releases the lock on this connection.<br>
	 * This method sets the {@code locked} flag to {@code false}.<br>
	 * Use this after finishing a synchronized operation.
	 */
	void unlockConnection()
	{
		locked = false;
	}
	
	/**
	 * @param data
	 * @return True if data was processed correctly, False if some error occurred and connection should be closed NOW.
	 */
	abstract protected boolean processData(ByteBuffer data);
	
	/**
	 * This method will be called by Dispatcher, and will be repeated till return false.
	 * @param data
	 * @return True if data was written to buffer, False indicating that there are not any more data to write.
	 */
	abstract protected boolean writeData(ByteBuffer data);
	
	/**
	 * Called when AConnection object is fully initialized and ready to process and send packets. It may be used as hook for sending first packet etc.
	 */
	abstract protected void initialized();
	
	/**
	 * This method is called by Dispatcher when connection is ready to be closed.
	 * @return time in ms after witch onDisconnect() method will be called.
	 */
	abstract protected long getDisconnectionDelay();
	
	/**
	 * This method is called by Dispatcher to inform that this connection was closed and should be cleared. This method is called only once.
	 */
	abstract protected void onDisconnect();
	
	/**
	 * This method is called by NioServer to inform that NioServer is shouting down. This method is called only once.
	 */
	abstract protected void onServerClose();
}
