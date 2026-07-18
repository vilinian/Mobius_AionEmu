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
package com.aionemu.loginserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.network.IPRange;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsConnection.State;

/**
 * Represents the information of a {@code GameServer} as seen by the login server.<br>
 * This class stores essential details such as the unique identifier and IP address.
 * @author -Nemesiss-
 */
public class GameServerInfo
{
	/**
	 * Id of this GameServer
	 */
	private final byte id;
	/**
	 * Allowed IP for this GameServer if gs will connect from another ip wont be registered.
	 */
	private final String ip;
	/**
	 * Password
	 */
	private final String password;
	/**
	 * Default server address, usually internet address
	 */
	private byte[] defaultAddress;
	/**
	 * Mapping of ip ranges, usually used for local area connections
	 */
	private List<IPRange> ipRanges;
	/**
	 * Port on with this GameServer is accepting clients.
	 */
	private int port;
	/**
	 * gsConnection - if GameServer is connected to LoginServer.
	 */
	private GsConnection gscHandler;
	/**
	 * Max players count that may play on this GameServer.
	 */
	private int maxPlayers;
	/**
	 * Map<AccId,Account> of accounts logged in on this GameServer.
	 */
	private final Map<Integer, Account> accountsOnGameServer = new HashMap<>();
	
	/**
	 * Creates a new instance of {@link GameServerInfo}.<br>
	 * This constructor initializes the server with its unique identity and credentials.
	 * @param id The unique identifier for the game server.
	 * @param ip The allowed IP address for this server.
	 * @param password The security password for the server.
	 */
	public GameServerInfo(byte id, String ip, String password)
	{
		this.id = id;
		this.ip = ip;
		this.password = password;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link CollisionIntention}.<br>
	 * This value is used to represent the collision type as a byte.
	 * @return The {@code byte} ID of the current enum constant.
	 */
	public byte getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the password for this {@code GameServerInfo}.<br>
	 * This value is used to authenticate the connection.
	 * @return The {@code String} password.
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Retrieves the IP address of this game server.<br>
	 * This value is used to identify where the server is hosted.
	 * @return The {@code String} representing the server IP address.
	 */
	public String getIp()
	{
		return ip;
	}
	
	/**
	 * Retrieves the network port for this game server.<br>
	 * This value indicates where the server accepts client connections.
	 * @return The port number as an {@code int}.
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Sets the network port for this game server.<br>
	 * This value is used to identify where clients connect.
	 * @param port The port number to assign to the server.
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	
	/**
	 * Retrieves the default address for this game server.<br>
	 * This is typically used as the internet address.
	 * @return a {@code byte[]} containing the default address.
	 */
	public byte[] getDefaultAddress()
	{
		return defaultAddress;
	}
	
	/**
	 * Sets the default address for this game server.<br>
	 * This is typically used to store the internet address.
	 * @param defaultAddress The {@code byte[]} array representing the new address.
	 */
	public void setDefaultAddress(byte[] defaultAddress)
	{
		this.defaultAddress = defaultAddress;
	}
	
	/**
	 * Retrieves the list of allowed IP ranges for this game server.<br>
	 * These are typically used for local area connections.
	 * @return a {@code List} of {@link IPRange} objects.
	 */
	public List<IPRange> getIpRanges()
	{
		return ipRanges;
	}
	
	/**
	 * Updates the list of allowed IP ranges for this game server.<br>
	 * This is typically used to manage local area connections.
	 * @param ipRanges The {@code List} of {@link IPRange} objects to set.
	 */
	public void setIpRanges(List<IPRange> ipRanges)
	{
		this.ipRanges = ipRanges;
	}
	
	/**
	 * Retrieves the current connection handler for this game server.<br>
	 * This method returns the {@code GsConnection} object associated with the server.<br>
	 * It may return {@code null} if no connection exists.
	 * @return the {@code GsConnection} instance or {@code null}.
	 */
	public GsConnection getConnection()
	{
		return gscHandler;
	}
	
	/**
	 * Sets the {@link GsConnection} handler for this game server.<br>
	 * This method updates the internal connection object.
	 * @param gscHandler The {@code GsConnection} instance to assign.
	 */
	public void setConnection(GsConnection gscHandler)
	{
		this.gscHandler = gscHandler;
	}
	
	/**
	 * Retrieves the maximum number of players allowed on this game server.<br>
	 * This value is stored in the {@code maxPlayers} field.
	 * @return The maximum player capacity as an {@code int}.
	 */
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	/**
	 * Sets the maximum number of players allowed on this game server.<br>
	 * This value is used to limit capacity and check if the server is full.
	 * @param maxPlayers The maximum player count for the server.
	 */
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
	
	/**
	 * Checks if the game server is currently online.<br>
	 * This method verifies that a connection exists and is in the {@code AUTHED} state.
	 * @return {@code true} if the server is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return (gscHandler != null) && (gscHandler.getState() == State.AUTHED);
	}
	
	/**
	 * Checks if a specific account is currently logged into this game server.<br>
	 * It looks for the {@code accountId} in the internal tracking map.
	 * @param accountId The unique identifier of the account to check.
	 * @return {@code true} if the account exists on this server, otherwise {@code false}.
	 */
	public boolean isAccountOnGameServer(int accountId)
	{
		return accountsOnGameServer.containsKey(accountId);
	}
	
	/**
	 * Removes an account from the current game server.<br>
	 * This method updates the internal map of active players.
	 * @param accountId The unique identifier of the {@code Account} to remove.
	 * @return The {@code Account} object that was removed, or {@code null} if it did not exist.
	 */
	public Account removeAccountFromGameServer(int accountId)
	{
		return accountsOnGameServer.remove(accountId);
	}
	
	/**
	 * Adds a new {@code Account} to the list of active accounts on this game server.<br>
	 * This method updates the internal mapping using the account ID as the key.
	 * @param acc The {@code Account} object to be added.
	 */
	public void addAccountToGameServer(Account acc)
	{
		accountsOnGameServer.put(acc.getId(), acc);
	}
	
	/**
	 * Retrieves an {@link Account} from the game server using its unique ID.<br>
	 * This method looks up the account in the internal map of active players.
	 * @param accountId The unique identifier for the account to find.
	 * @return The {@code Account} object if found, or {@code null} if it does not exist.
	 */
	public Account getAccountFromGameServer(int accountId)
	{
		return accountsOnGameServer.get(accountId);
	}
	
	/**
	 * Removes all accounts from the current game server.<br>
	 * This method clears the internal {@code accountsOnGameServer} map.<br>
	 * Use this when a server is restarting or shutting down.
	 */
	public void clearAccountsOnGameServer()
	{
		accountsOnGameServer.clear();
	}
	
	/**
	 * Retrieves the number of players currently logged into this game server.<br>
	 * It counts the total size of the {@code accountsOnGameServer} map.
	 * @return The current count of active players as an {@code int}.
	 */
	public int getCurrentPlayers()
	{
		return accountsOnGameServer.size();
	}
	
	/**
	 * Checks if the game server has reached its maximum player capacity.<br>
	 * It compares {@code getCurrentPlayers} against {@code getMaxPlayers}.
	 * @return {@code true} if the server is full, {@code false} otherwise.
	 */
	public boolean isFull()
	{
		return getCurrentPlayers() >= getMaxPlayers();
	}
	
	/**
	 * Retrieves the correct IP address for a specific player.<br>
	 * It checks if the {@code playerIp} falls within any known {@link IPRange}.<br>
	 * If no range matches, it returns the {@code defaultAddress}.<br>
	 * If the server is offline, it returns the local loopback address.
	 * @param playerIp The IP address string of the player to check.
	 * @return A {@code byte[]} containing the resolved IP address.
	 */
	public byte[] getIPAddressForPlayer(String playerIp)
	{
		if (!isOnline())
		{
			return new byte[]
			{
				127,
				0,
				0,
				1
			};
		}
		
		for (IPRange ipr : ipRanges)
		{
			if (ipr.isInRange(playerIp))
			{
				return ipr.getAddress();
			}
		}
		
		return defaultAddress;
	}
}
