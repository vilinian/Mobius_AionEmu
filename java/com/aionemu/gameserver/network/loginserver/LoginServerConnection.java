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

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.network.factories.LsPacketHandlerFactory;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_GS_AUTH;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Represents a network connection between the {@link LoginServer} and the {@link GameServer}.<br>
 * It handles communication and data exchange between these two server components.
 * @author -Nemesiss-
 */
public class LoginServerConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServerConnection.class);
	
	/**
	 * Possible states of GsConnection
	 */
	public static enum State
	{
		/**
		 * game server just connect
		 */
		CONNECTED,
		/**
		 * game server is authenticated
		 */
		AUTHED
	}
	
	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<LsServerPacket> sendMsgQueue = new ArrayDeque<>();
	/**
	 * Current state of this connection
	 */
	private State state;
	private final LsPacketHandler lsPacketHandler;
	
	/**
	 * Creates a new connection between the GameServer and the LoginServer.<br>
	 * This constructor initializes the {@code lsPacketHandler} and sets the initial state.<br>
	 * It logs a success message to the console upon creation.
	 * @param sc The {@link SocketChannel} used for network communication.
	 * @param d The {@link Dispatcher} used to handle asynchronous I/O operations.
	 */
	public LoginServerConnection(SocketChannel sc, Dispatcher d)
	{
		super(sc, d, 8192 * 8, 8192 * 8);
		final LsPacketHandlerFactory lsPacketHandlerFactory = LsPacketHandlerFactory.getInstance();
		lsPacketHandler = lsPacketHandlerFactory.getPacketHandler();
		
		state = State.CONNECTED;
		GameServer.log.info("Connected to LoginServer!");
		System.out.println("");
	}
	
	/**
	 * Performs the initial setup for the connection.<br>
	 * It sends the {@code SM_GS_AUTH} packet to the client.
	 */
	@Override
	protected void initialized()
	{
		/**
		 * send first packet - authentication.
		 */
		sendPacket(new SM_GS_AUTH());
	}
	
	/**
	 * Processes incoming data from the network.<br>
	 * It uses {@link LsPacketHandler} to parse the {@code ByteBuffer}.<br>
	 * If a valid packet is found, it is sent to the {@code ThreadPoolManager}.
	 * @param data The raw bytes received from the connection.
	 * @return {@code true} if the data was processed successfully.
	 */
	@Override
	public boolean processData(ByteBuffer data)
	{
		final LsClientPacket pck = lsPacketHandler.handle(data, this);
		log.debug("[LoginServer] recived packet: " + pck);
		
		/**
		 * Execute packet only if packet exist (!= null) and read was ok.
		 */
		if ((pck != null) && pck.read())
		{
			ThreadPoolManager.getInstance().executeLsPacket(pck);
		}
		
		return true;
	}
	
	/**
	 * Sends the provided data to the client.<br>
	 * This method retrieves a packet from the internal queue and writes it.<br>
	 * It returns {@code true} if the write was successful.<br>
	 * It returns {@code false} if there are no packets left in the queue.
	 * @param data The {@code ByteBuffer} containing the information to be sent.
	 * @return {@code true} if a packet was successfully written, otherwise {@code false}.
	 */
	@Override
	protected boolean writeData(ByteBuffer data)
	{
		synchronized (guard)
		{
			final LsServerPacket packet = sendMsgQueue.pollFirst();
			if (packet == null)
			{
				return false;
			}
			
			packet.write(this, data);
			return true;
		}
	}
	
	/**
	 * Retrieves the delay before a disconnection occurs.<br>
	 * This method returns the current timeout value in milliseconds.
	 * @return The disconnection delay as a {@code long}.
	 */
	@Override
	protected long getDisconnectionDelay()
	{
		return 0;
	}
	
	/**
	 * Handles the cleanup logic when a client disconnects.<br>
	 * Stops the {@code pingChecker}.<br>
	 * Notifies the {@link LoginServer} if an account is logged in.<br>
	 * Triggers the world leave process for the active player.
	 */
	@Override
	protected void onDisconnect()
	{
		LoginServer.getInstance().loginServerDown();
	}
	
	/**
	 * Handles the logic when the server is shutting down.<br>
	 * This method forces the connection to close immediately.<br>
	 * It calls {@code boolean)} with a {@code true} flag.
	 */
	@Override
	protected void onServerClose()
	{
		// TODO mb some packet should be send to loginserver before closing?
		close(/* packet, */true);
	}
	
	/**
	 * Sends a server packet to the connected game server.<br>
	 * This method adds the {@code LsServerPacket} to the outgoing queue.<br>
	 * It ensures that the connection is active before sending.
	 * @param bp The {@code LsServerPacket} to be sent.
	 */
	public void sendPacket(LsServerPacket bp)
	{
		synchronized (guard)
		{
			/**
			 * Connection is already closed or waiting for last (close packet) to be sent
			 */
			if (isWriteDisabled())
			{
				return;
			}
			
			log.debug("[LoginServer] sending packet: " + bp);
			
			sendMsgQueue.addLast(bp);
			enableWriteInterest();
		}
	}
	
	/**
	 * Closes the connection to the server.<br>
	 * It sends a final packet before disconnecting.<br>
	 * The queue of pending messages is cleared during this process.
	 * @param closePacket The {@code LsServerPacket} to send before closing.
	 * @param forced Set to {@code true} if the connection should be closed immediately without waiting.
	 */
	public void close(LsServerPacket closePacket, boolean forced)
	{
		synchronized (guard)
		{
			if (isWriteDisabled())
			{
				return;
			}
			
			log.debug("[LoginServer] sending packet: " + closePacket + " and closing connection after that.");
			
			pendingClose = true;
			isForcedClosing = forced;
			sendMsgQueue.clear();
			sendMsgQueue.addLast(closePacket);
			enableWriteInterest();
		}
	}
	
	/**
	 * Retrieves the current connection status.<br>
	 * This method returns the {@code State} of the current connection.
	 * @return The current {@code State} of this connection.
	 */
	public State getState()
	{
		return state;
	}
	
	/**
	 * Updates the current connection state.<br>
	 * This method sets the {@code state} field to the provided value.
	 * @param state The new {@code State} to apply to this connection.
	 */
	public void setState(State state)
	{
		this.state = state;
	}
	
	/**
	 * Returns a string representation of this connection.<br>
	 * It combines the prefix {@code LoginServer} with the IP address.
	 * @return A string containing the server name and IP.
	 */
	@Override
	public String toString()
	{
		return "LoginServer " + getIP();
	}
}
