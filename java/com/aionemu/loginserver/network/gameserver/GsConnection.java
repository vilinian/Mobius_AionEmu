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
package com.aionemu.loginserver.network.gameserver;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.PingPongThread;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.network.factories.GsPacketHandlerFactory;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * Represents a network connection between the {@link com.aionemu.loginserver.GameServerInfo} and the LoginServer.<br>
 * This class manages the communication channel for handling game server specific data packets.
 * @author -Nemesiss-
 */
public class GsConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(GsConnection.class);
	
	/**
	 * Possible states of GsConnection
	 */
	public static enum State
	{
		/**
		 * Means that GameServer just connect, but is not authenticated yet
		 */
		CONNECTED,
		/**
		 * GameServer is authenticated
		 */
		AUTHED
	}
	
	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<GsServerPacket> sendMsgQueue = new ArrayDeque<>();
	/**
	 * Current state of this connection
	 */
	private State state;
	/**
	 * GameServerInfo for this GsConnection.
	 */
	private GameServerInfo gameServerInfo = null;
	private PingPongThread pingThread;
	
	/**
	 * Creates a new {@link GsConnection} instance.<br>
	 * This initializes the connection using the provided network components.
	 * @param sc The {@code SocketChannel} used for communication.
	 * @param d The {@code Dispatcher} used to handle network events.
	 */
	public GsConnection(SocketChannel sc, Dispatcher d)
	{
		super(sc, d, 8192 * 8, 8192 * 8);
	}
	
	/**
	 * Processes incoming data from the network.<br>
	 * It uses {@link GsPacketHandlerFactory} to parse the {@code ByteBuffer}.<br>
	 * If a valid packet is found, it is sent to the {@code ThreadPoolManager}.
	 * @param data The raw bytes received from the connection.
	 * @return {@code true} if the data was processed successfully.
	 */
	@Override
	public boolean processData(ByteBuffer data)
	{
		final GsClientPacket pck = GsPacketHandlerFactory.handle(data, this);
		
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
			final GsServerPacket packet = sendMsgQueue.pollFirst();
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
	 * Handles the cleanup logic when a connection is lost.<br>
	 * Stops the {@code pingThread} if it is active.<br>
	 * Clears the associated {@link GameServerInfo} data.
	 */
	@Override
	protected void onDisconnect()
	{
		if (Config.ENABLE_PINGPONG)
		{
			pingThread.closeMe();
		}
		
		log.info(this + " disconnected");
		if (gameServerInfo != null)
		{
			gameServerInfo.setConnection(null);
			gameServerInfo.clearAccountsOnGameServer();
			gameServerInfo = null;
		}
	}
	
	/**
	 * Handles the logic when the server is shutting down.<br>
	 * This method forces the connection to close immediately.<br>
	 * It calls {@code boolean)} with a {@code true} flag.
	 */
	@Override
	protected void onServerClose()
	{
		// TODO mb some packet should be send to gameserver before closing?
		close(/* packet, */true);
	}
	
	/**
	 * Sends a packet to the connected game server.<br>
	 * This method adds the {@code GsServerPacket} to the outgoing queue.<br>
	 * It enables write interest if the connection is active.
	 * @param bp The {@code GsServerPacket} to be sent.
	 */
	public void sendPacket(GsServerPacket bp)
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
			
			sendMsgQueue.addLast(bp);
			enableWriteInterest();
		}
	}
	
	/**
	 * Closes the connection to the game server.<br>
	 * This method prepares the final packet and clears the message queue.
	 * @param closePacket The {@code GsServerPacket} to send during closure.
	 * @param forced Set to {@code true} if the connection should be closed immediately.
	 */
	public void close(GsServerPacket closePacket, boolean forced)
	{
		synchronized (guard)
		{
			if (isWriteDisabled())
			{
				return;
			}
			
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
		if (state == State.AUTHED)
		{
			if (Config.ENABLE_PINGPONG)
			{
				ThreadPoolManager.getInstance().schedule(pingThread, 5000);
			}
		}
	}
	
	/**
	 * Retrieves the information for the connected game server.<br>
	 * This method returns the {@code GameServerInfo} object associated with this connection.<br>
	 * It may return {@code null} if no information has been set yet.
	 * @return The {@code GameServerInfo} of the current connection.
	 */
	public GameServerInfo getGameServerInfo()
	{
		return gameServerInfo;
	}
	
	/**
	 * Updates the {@code GameServerInfo} for this connection.<br>
	 * This method stores the provided server details in the local field.
	 * @param gameServerInfo The {@link GameServerInfo} object to set.
	 */
	public void setGameServerInfo(GameServerInfo gameServerInfo)
	{
		this.gameServerInfo = gameServerInfo;
	}
	
	/**
	 * Returns a string representation of the {@code GsConnection}.<br>
	 * This includes the GameServer ID and the IP address.
	 * @return A formatted string describing this connection.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("GameServer [ID:");
		if (gameServerInfo != null)
		{
			sb.append(gameServerInfo.getId());
		}
		else
		{
			sb.append("null");
		}
		sb.append("] ").append(getIP());
		return sb.toString();
	}
	
	/**
	 * Handles the response for a ping request.<br>
	 * This method is called when a {@code pong} packet is received.<br>
	 * It updates the status of the corresponding process ID.
	 * @param pid The unique identifier for the process.
	 */
	public void pong(int pid)
	{
		if (Config.ENABLE_PINGPONG)
		{
			pingThread.onResponse(pid);
		}
	}
	
	/**
	 * Performs the initial setup for the {@code GsConnection}.<br>
	 * Sets the state to {@code State.CONNECTED}.<br>
	 * Starts a new {@link PingPongThread} if enabled in the configuration.<br>
	 * Logs the connection attempt from the server IP address.
	 */
	@Override
	protected void initialized()
	{
		// TODO Auto-generated method stub
		state = State.CONNECTED;
		final String ip = getIP();
		
		if (Config.ENABLE_PINGPONG)
		{
			pingThread = new PingPongThread(this);
		}
		
		log.info("Gameserver connection attemp from: " + ip);
	}
}
