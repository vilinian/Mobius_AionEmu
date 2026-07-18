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
import java.util.Iterator;

/**
 * This class provides an implementation of {@link Dispatcher} specifically for accepting new connections.<br>
 * It handles the logic required to manage incoming network requests.
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.Dispatcher
 * @see java.nio.channels.Selector
 */
public class AcceptDispatcherImpl extends Dispatcher
{
	/**
	 * Creates a new instance of {@link AcceptDispatcherImpl}.<br>
	 * This constructor initializes the dispatcher with a specific name.
	 * @param name The unique identifier for this dispatcher.
	 * @throws IOException If an error occurs during initialization.
	 */
	public AcceptDispatcherImpl(String name) throws IOException
	{
		super(name, null);
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
		if (selector.select() != 0)
		{
			final Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext())
			{
				final SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				
				if (key.isValid())
				{
					accept(key);
				}
			}
		}
	}
	
	/**
	 * Closes an active network connection.<br>
	 * This method is not intended to be used by the application.<br>
	 * Calling this method will result in an {@code UnsupportedOperationException}.
	 * @param con The {@code AConnection} object to close.
	 */
	@Override
	void closeConnection(AConnection con)
	{
		throw new UnsupportedOperationException("This method should never be called!");
	}
}
