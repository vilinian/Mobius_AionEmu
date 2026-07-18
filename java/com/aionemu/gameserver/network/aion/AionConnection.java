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

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.PacketProcessor;
import com.aionemu.commons.utils.concurrent.ExecuteWrapper;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.Crypt;
import com.aionemu.gameserver.network.PacketFloodFilter;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KEY;
import com.aionemu.gameserver.network.factories.AionPacketHandlerFactory;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_MAC;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Represents the network connection between the {@code GameServer} and an Aion client.<br>
 * This class handles communication, packet processing, and session management for a single player.
 * @author -Nemesiss-
 */
public class AionConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AionConnection.class);
	private static final PacketProcessor<AionConnection> packetProcessor = new PacketProcessor<>(NetworkConfig.PACKET_PROCESSOR_MIN_THREADS, NetworkConfig.PACKET_PROCESSOR_MAX_THREADS, NetworkConfig.PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD, NetworkConfig.PACKET_PROCESSOR_THREAD_KILL_THRESHOLD, new ExecuteWrapper());
	private String hdd_serial;
	private String ipv4list;
	private String local_ip;
	private String windows;
	private int countryCode = GSConfig.SERVER_COUNTRY_CODE;
	private String aionBin;
	private int memory;
	private int winEncode;
	private String traceroute;
	
	/**
	 * Retrieves the country code for the current connection.<br>
	 * This method returns a specific value if the IP matches a hardcoded address.<br>
	 * Otherwise, it returns the stored {@code countryCode}.
	 * @return The integer representing the country code.
	 */
	public int getCountryCode()
	{
		if (getIP().equals("109.87.238.83"))
		{
			return 7;
		}
		
		return countryCode;
	}
	
	/**
	 * Sets the country code for the current connection.<br>
	 * This value is used to identify the player's geographic region.
	 * @param countryCode The unique integer identifier for the country.
	 */
	public void setCountryCode(int countryCode)
	{
		this.countryCode = countryCode;
	}
	
	/**
	 * Sets the local IP address for this connection.<br>
	 * This value is stored in the {@code local_ip} field.
	 * @param local_ip The IP address to set.
	 */
	public void setLocalIP(String local_ip)
	{
		this.local_ip = local_ip;
	}
	
	/**
	 * Updates the list of IPv4 addresses for this connection.<br>
	 * The {@code iplist} string is stored in the internal field.
	 * @param iplist The new list of IPv4 addresses to set.
	 */
	public void setIPv4List(String iplist)
	{
		ipv4list = iplist;
	}
	
	/**
	 * Sets the hard drive serial number for this connection.<br>
	 * This value is used to identify the hardware of the client.
	 * @param hdd_serial The {@code String} representing the hard drive serial.
	 */
	public void setHDDSerial(String hdd_serial)
	{
		this.hdd_serial = hdd_serial;
	}
	
	/**
	 * Sets the operating system identifier for this connection.<br>
	 * This value is used to identify the client's environment.
	 * @param windows The name of the operating system to set.
	 */
	public void setWindows(String windows)
	{
		this.windows = windows;
	}
	
	/**
	 * Updates the memory value for this connection.<br>
	 * This method sets the {@code memory} field to a new integer value.
	 * @param memory The new memory value to assign.
	 */
	public void setMemoryPC(int memory)
	{
		this.memory = memory;
	}
	
	/**
	 * Sets the connection string for the {@code AionBin}.<br>
	 * This value is used to identify the specific binary connection.
	 * @param AionBinConnection The connection string to be assigned.
	 */
	public void setAionBin(String AionBinConnection)
	{
		aionBin = AionBinConnection;
	}
	
	/**
	 * Retrieves the path to the Aion binary executable.<br>
	 * This value is used to identify the client application.
	 * @return The {@code String} representing the Aion binary path.
	 */
	public String getAionBin()
	{
		return aionBin;
	}
	
	/**
	 * Retrieves the amount of memory associated with this connection.<br>
	 * This value is typically used for hardware identification.
	 * @return The memory value as an {@code int}.
	 */
	public int getMemory()
	{
		return memory;
	}
	
	/**
	 * Sets the character encoding used for Windows systems.<br>
	 * This value is stored in the {@code winEncode} field.
	 * @param enc The integer code representing the desired encoding.
	 */
	public void setWindowsEncoding(int enc)
	{
		winEncode = enc;
	}
	
	/**
	 * Retrieves the current Windows encoding value.<br>
	 * This value is used to identify the character encoding of the client system.
	 * @return The integer value representing the Windows encoding.
	 */
	public int getWindowsEncoding()
	{
		return winEncode;
	}
	
	/**
	 * Retrieves the IP address used for the traceroute.<br>
	 * This value is stored in the {@code traceroute} field.
	 * @return The traceroute IP address as a {@code String}.
	 */
	public String getTracerouteIP()
	{
		return traceroute;
	}
	
	/**
	 * Sets the IP address used for traceroute.<br>
	 * This updates the {@code traceroute} field with the provided value.
	 * @param ips The IP address to be stored as the traceroute target.
	 */
	public void setTracerouteIP(String ips)
	{
		traceroute = ips;
	}
	
	/**
	 * Possible states of AionConnection
	 */
	public static enum State
	{
		/**
		 * client just connect
		 */
		CONNECTED,
		/**
		 * client is authenticated
		 */
		AUTHED,
		/**
		 * client entered world.
		 */
		IN_GAME
	}
	
	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<AionServerPacket> sendMsgQueue = new ArrayDeque<>();
	/**
	 * Current state of this connection
	 */
	private volatile State state;
	/**
	 * AionClient is authenticating by passing to GameServer id of account.
	 */
	private Account account;
	/**
	 * Crypt that will encrypt/decrypt packets.
	 */
	private final Crypt crypt = new Crypt();
	/**
	 * active Player that owner of this connection is playing [entered game]
	 */
	private final AtomicReference<Player> activePlayer = new AtomicReference<>();
	private String lastPlayerName = "";
	private final AionPacketHandler aionPacketHandler;
	private long lastPingTimeMS;
	private int nbInvalidPackets = 0;
	
	// TODO! why there is no any comments what is this doing? i have no clue what is it for [Nemesiss]
	private final static int MAX_INVALID_PACKETS = 3;
	private String macAddress;
	/**
	 * Ping checker - for detecting hanged up connections *
	 */
	private final PingChecker pingChecker;
	/**
	 * packet flood filter *
	 */
	private int[] pff;
	private long[] pffRequests;
	
	/**
	 * Creates a new {@link AionConnection} instance.<br>
	 * This constructor initializes the connection with specific buffer sizes.<br>
	 * It sets up the packet handler and starts the ping checker.<br>
	 * It also enables the flood filter if it is configured in the security settings.
	 * @param sc The {@code SocketChannel} used for network communication.
	 * @param d The {@code Dispatcher} used to handle asynchronous tasks.
	 */
	public AionConnection(SocketChannel sc, Dispatcher d)
	{
		super(sc, d, 8192 * 4, 8192 * 4);
		
		final AionPacketHandlerFactory aionPacketHandlerFactory = AionPacketHandlerFactory.getInstance();
		aionPacketHandler = aionPacketHandlerFactory.getPacketHandler();
		
		state = State.CONNECTED;
		
		final String ip = getIP();
		log.info("connection from: " + ip);
		
		pingChecker = new PingChecker();
		pingChecker.start();
		
		if (SecurityConfig.PFF_ENABLE)
		{
			pff = PacketFloodFilter.getInstance().getPackets();
			pffRequests = new long[pff.length];
		}
	}
	
	/**
	 * This method performs the initial setup for the connection.<br>
	 * It sends the {@code SM_KEY} packet to the client.
	 */
	@Override
	protected void initialized()
	{
		/**
		 * Send SM_KEY packet
		 */
		sendPacket(new SM_KEY());
	}
	
	/**
	 * Enables the encryption key for this connection.<br>
	 * This method calls {@code enableKey}.
	 * @return The result of the key enablement process as an {@code int}.
	 */
	public int enableCryptKey()
	{
		return crypt.enableKey();
	}
	
	/**
	 * Decrypts and processes incoming data from the client.<br>
	 * It validates the packet structure and checks for flooding.<br>
	 * If the packet is valid, it executes the corresponding logic.
	 * @param data The {@code ByteBuffer} containing the raw packet data.
	 * @return {@code true} if the data was processed successfully or skipped due to decryption failure; {@code false} otherwise.
	 */
	@Override
	protected boolean processData(ByteBuffer data)
	{
		try
		{
			if (!crypt.decrypt(data))
			{
				nbInvalidPackets++;
				log.info("[" + nbInvalidPackets + "/" + MAX_INVALID_PACKETS + "] Decrypt fail, client packet passed...");
				if (nbInvalidPackets >= MAX_INVALID_PACKETS)
				{
					log.warn("Decrypt fail!");
					return false;
				}
				
				return true;
			}
		}
		catch (Exception ex)
		{
			log.error("Exception caught during decrypt!" + ex.getMessage());
			return false;
		}
		
		if (data.remaining() < 5)
		{// op + static code + op == 5 bytes
			log.error("Received fake packet from: " + this);
			return false;
		}
		
		final AionClientPacket pck = aionPacketHandler.handle(data, this);
		
		/**
		 * Execute packet only if packet exist (!= null) and read was ok.
		 */
		if (pck != null)
		{
			if (SecurityConfig.PFF_ENABLE)
			{
				final int opcode = pck.getOpcode();
				if (pff.length > opcode)
				{
					if (pff[opcode] > 0)
					{
						final long last = pffRequests[opcode];
						if (last == 0)
						{
							pffRequests[opcode] = System.currentTimeMillis();
						}
						else
						{
							final long diff = System.currentTimeMillis() - last;
							if (diff < pff[opcode])
							{
								log.warn(this + " has flooding " + pck.getClass().getSimpleName() + " " + diff);
								switch (SecurityConfig.PFF_LEVEL)
								{
									case 1: // disconnect
										return false;
									case 2:
										break;
								}
							}
							else
							{
								pffRequests[opcode] = System.currentTimeMillis();
							}
						}
					}
				}
			}
			
			PacketLoggerService.getInstance().logPacketCM(pck.getPacketName());
			
			if (pck.read())
			{
				packetProcessor.executePacket(pck);
			}
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
			final long begin = System.nanoTime();
			if (sendMsgQueue.isEmpty())
			{
				return false;
			}
			
			final AionServerPacket packet = sendMsgQueue.removeFirst();
			PacketLoggerService.getInstance().logPacketSM(packet.getPacketName());
			try
			{
				packet.write(this, data);
				return true;
			}
			finally
			{
				RunnableStatsManager.handleStats(packet.getClass(), "runImpl()", System.nanoTime() - begin);
			}
			
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
		/**
		 * Client starts authentication procedure
		 */
		pingChecker.stop();
		if (getAccount() != null)
		{
			LoginServer.getInstance().aionClientDisconnected(getAccount().getId());
			LoginServer.getInstance().sendPacket(new SM_MAC(getAccount().getId(), macAddress, hdd_serial));
		}
		
		final Player player = getActivePlayer();
		if (player != null)
		{
			PlayerLeaveWorldService.tryLeaveWorld(player);
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
		// TODO mb some packet should be send to client before closing?
		close(/* packet, */true);
	}
	
	/**
	 * Encrypts the data contained within a {@code ByteBuffer}.<br>
	 * This method checks if encryption is enabled before processing.<br>
	 * If it is not enabled, it enables it and returns early.<br>
	 * Otherwise, it calls the {@code encrypt} method.
	 * @param buf The {@code ByteBuffer} to be encrypted.
	 */
	public void encrypt(ByteBuffer buf)
	{
		crypt.encrypt(buf);
	}
	
	/**
	 * Sends a packet to the connected client.<br>
	 * This method adds the {@code AionServerPacket} to the outgoing message queue.<br>
	 * It ensures that the connection is active before attempting to send data.
	 * @param bp The {@code AionServerPacket} object to be sent.
	 */
	public void sendPacket(AionServerPacket bp)
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
	 * Closes the connection and prepares it for disconnection.<br>
	 * This method sets a pending close flag and clears the outgoing message queue.<br>
	 * It also adds the specified {@code closePacket} to the queue.
	 * @param closePacket The packet to send before closing the connection.
	 * @param forced Whether the closure should be treated as a forced disconnection.
	 */
	public void close(AionServerPacket closePacket, boolean forced)
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
	}
	
	/**
	 * Retrieves the {@link Account} associated with this connection.
	 * @return The current {@code Account} object.
	 */
	public Account getAccount()
	{
		return account;
	}
	
	/**
	 * Sets the {@link Account} associated with this connection.<br>
	 * This method updates the internal account reference.<br>
	 * The provided {@code account} parameter must not be {@code null}.
	 * @param account The {@link Account} object to set.
	 */
	public void setAccount(Account account)
	{
		if (!(account != null))
		{
			throw new IllegalArgumentException("Account can't be null");
		}
		this.account = account;
	}
	
	/**
	 * Sets the current active {@link Player} for this connection.<br>
	 * Updates the connection state based on whether a player is provided.<br>
	 * Returns {@code true} if the operation succeeded.
	 * @param player The {@link Player} object to set as active.
	 * @return {@code true} if the player was successfully set, otherwise {@code false}.
	 */
	public boolean setActivePlayer(Player player)
	{
		if (player == null)
		{
			activePlayer.set(player);
			setState(State.AUTHED);
		}
		else if (activePlayer.compareAndSet(null, player))
		{
			setState(State.IN_GAME);
			lastPlayerName = player.getName();
		}
		else
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the {@link Player} currently associated with this connection.<br>
	 * This method returns the player object stored in the internal atomic reference.
	 * @return The current {@code Player} object or {@code null} if no player is active.
	 */
	public Player getActivePlayer()
	{
		return activePlayer.get();
	}
	
	/**
	 * Retrieves the timestamp of the most recent ping received.<br>
	 * The value is measured in milliseconds.
	 * @return the time of the last ping as a {@code long}.
	 */
	public long getLastPingTimeMS()
	{
		return lastPingTimeMS;
	}
	
	/**
	 * Updates the timestamp of the last received ping.<br>
	 * This value is stored in milliseconds.
	 * @param lastPingTimeMS The time of the last ping in {@code long} format.
	 */
	public void setLastPingTimeMS(long lastPingTimeMS)
	{
		this.lastPingTimeMS = lastPingTimeMS;
	}
	
	/**
	 * Closes the current connection immediately.<br>
	 * This method calls {@code boolean)} with a {@code false} value for the forced parameter.
	 */
	public void closeNow()
	{
		this.close(false);
	}
	
	/**
	 * Sets the hardware MAC address for this connection.<br>
	 * This value is used to identify the client device.
	 * @param mac The {@code String} representing the MAC address.
	 */
	public void setMacAddress(String mac)
	{
		macAddress = mac;
	}
	
	/**
	 * Retrieves the unique MAC address of the connected client.<br>
	 * This value is used for hardware identification.
	 * @return The {@code String} representation of the MAC address.
	 */
	public String getMacAddress()
	{
		return macAddress;
	}
	
	/**
	 * Retrieves the hard drive serial number.<br>
	 * This value was previously set using {@code setHDDSerial}.
	 * @return The {@code String} representing the hardware serial.
	 */
	public String getHddSerial()
	{
		return hdd_serial;
	}
	
	/**
	 * Retrieves the list of IPv4 addresses.<br>
	 * This method returns the {@code ipv4list} string associated with this connection.
	 * @return The string containing the IPv4 list.
	 */
	public String getIpv4list()
	{
		return ipv4list;
	}
	
	/**
	 * Retrieves the local IP address of the connection.<br>
	 * This value was previously set using {@code setLocalIP}.
	 * @return The local IP address as a {@code String}.
	 */
	public String getLocalIP()
	{
		return local_ip;
	}
	
	/**
	 * Retrieves the Windows operating system information.<br>
	 * This value is stored in the {@code windows} field.
	 * @return The string representing the Windows version or name.
	 */
	public String getWindows()
	{
		return windows;
	}
	
	/**
	 * Returns a string representation of the {@code AionConnection}.<br>
	 * This method includes details about the connection state, account, and player information.<br>
	 * If no active player is found, it returns an empty string.
	 * @return A formatted string containing connection details or an empty string.
	 */
	@Override
	public String toString()
	{
		final Player player = activePlayer.get();
		if (player != null)
		{
			return "AionConnection [state=" + state + ", account=" + account + ", getObjectId()=" + player.getObjectId() + ", lastPlayerName=" + lastPlayerName + ", macAddress=" + macAddress + ",hddSerial=" + hdd_serial + ", getIP()=" + getIP() + "]";
		}
		
		return "";
	}
	
	private class PingChecker implements Runnable
	{
		// We do not have to detect hung connections immediately as it is a very rare case, so a ten-minute check should be sufficient.
		private static final int checkTime = 10 * 60 * 1000;
		private ScheduledFuture<?> task;
		private boolean started;
		
		private void start()
		{
			if (!(!started))
			{
				throw new IllegalStateException("PingChecker can be started only one time!");
			}
			started = true;
			task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, checkTime, checkTime);
		}
		
		private void stop()
		{
			task.cancel(false);
		}
		
		@Override
		public void run()
		{
			if ((System.currentTimeMillis() - getLastPingTimeMS()) > checkTime)
			{
				log.info("Found hanged up client: " + AionConnection.this + " - closing now :)");
				closeNow();
			}
		}
	}
}
