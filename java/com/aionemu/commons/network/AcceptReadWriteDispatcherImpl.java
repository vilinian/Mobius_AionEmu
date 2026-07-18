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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * This class provides an implementation of the {@link Dispatcher} interface.<br>
 * It handles network operations including accepting new connections and reading or writing data.<br>
 * It manages these tasks using a non-blocking I/O approach.
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.Dispatcher
 * @see java.nio.channels.Selector
 */
public class AcceptReadWriteDispatcherImpl extends Dispatcher
{
	/**
	 * List of connections that should be closed by this <code>Dispatcher</code> as soon as possible.
	 */
	private final List<AConnection> pendingClose = new ArrayList<>();
	
	/**
	 * Creates a new instance of {@link AcceptReadWriteDispatcherImpl}.<br>
	 * This constructor initializes the dispatcher with a specific name and thread pool.
	 * @param name The unique identifier for this dispatcher.
	 * @param dcPool The {@code Executor} used to handle disconnections.
	 * @throws IOException If an error occurs during initialization.
	 */
	public AcceptReadWriteDispatcherImpl(String name, Executor dcPool) throws IOException
	{
		super(name, dcPool);
	}
	
	/**
	 * Processes pending network events from the {@code Selector}.<br>
	 * It iterates through all selected keys and calls {@code accept}.<br>
	 * This method is used to handle new incoming connections.
	 * @throws IOException If an error occurs during the selection or processing of keys.
	 */
	@Override
	void dispatch() throws IOException
	{
		final int selected = selector.select();
		
		processPendingClose();
		
		if (selected != 0)
		{
			final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext())
			{
				final SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				
				if (!key.isValid())
				{
					continue;
				}
				
				/** Check what event is available and deal with it */
				switch (key.readyOps())
				{
					case SelectionKey.OP_ACCEPT:
						accept(key);
						break;
					case SelectionKey.OP_READ:
						read(key);
						break;
					case SelectionKey.OP_WRITE:
						write(key);
						break;
					case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
						read(key);
						if (key.isValid())
						{
							write(key);
						}
						break;
				}
			}
		}
	}
	
	/**
	 * Adds a connection to the list of pending closures.<br>
	 * This method marks the {@code AConnection} for closing as soon as possible.<br>
	 * It ensures thread safety by synchronizing on the internal list.
	 * @param con The {@code AConnection} object to be closed.
	 */
	@Override
	void closeConnection(AConnection con)
	{
		synchronized (pendingClose)
		{
			pendingClose.add(con);
		}
	}
	
	/**
	 * This method handles the closing of all connections in the {@code pendingClose} list.<br>
	 * It iterates through each {@link AConnection} and calls {@code closeConnectionImpl}.<br>
	 * The {@code pendingClose} list is cleared after all connections are processed.
	 */
	private void processPendingClose()
	{
		synchronized (pendingClose)
		{
			for (AConnection connection : pendingClose)
			{
				closeConnectionImpl(connection);
			}
			
			pendingClose.clear();
		}
	}
}
