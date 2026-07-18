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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.IPRange;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.dao.GameServersDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;

/**
 * This class manages the collection of {@link com.aionemu.loginserver.network.gameserver.GsConnection} objects registered on this login server.<br>
 * It tracks whether each game server is currently online or down.
 * @author -Nemesiss-
 */
public class GameServerTable
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(GameServerTable.class);
	/**
	 * Map<Id,GameServer>
	 */
	private static Map<Byte, GameServerInfo> gameservers;
	
	/**
	 * Retrieves all registered game servers.<br>
	 * This method returns a list of {@link GameServerInfo} objects.<br>
	 * The returned collection is unmodifiable to prevent changes.
	 * @return A {@code Collection} of all available {@code GameServerInfo} instances.
	 */
	public static Collection<GameServerInfo> getGameServers()
	{
		return Collections.unmodifiableCollection(gameservers.values());
	}
	
	/**
	 * Loads all game servers from the database.<br>
	 * This method populates the {@code gameservers} map using {@code getDAO}.<br>
	 * It logs the total number of registered servers after loading.
	 */
	public static void load()
	{
		gameservers = getDAO().getAllGameServers();
		log.info("GameServerTable loaded " + gameservers.size() + " registered GameServers.");
	}
	
	/**
	 * Registers a game server with the login server.<br>
	 * This method validates the credentials and network information before completing registration.
	 * @param gsConnection The {@link GsConnection} object representing the current connection.
	 * @param requestedId The unique {@code byte} identifier for the game server.
	 * @param defaultAddress The {@byte[]} array containing the default IP address.
	 * @param ipRanges A {@link List} of {@link IPRange} objects allowed to connect.
	 * @param port The network port used by the game server.
	 * @param maxPlayers The maximum number of players allowed on this server.
	 * @param password The security password required for registration.
	 * @return A {@link GsAuthResponse} indicating if the registration was successful.
	 */
	public static GsAuthResponse registerGameServer(GsConnection gsConnection, byte requestedId, byte[] defaultAddress, List<IPRange> ipRanges, int port, int maxPlayers, String password)
	{
		final GameServerInfo gsi = gameservers.get(requestedId);
		
		/**
		 * This id is not Registered at LoginServer.
		 */
		if (gsi == null)
		{
			log.info(gsConnection + " requestedID=" + requestedId + " not aviable!");
			return GsAuthResponse.NOT_AUTHED;
		}
		
		/**
		 * Check if this GameServer is not already registered.
		 */
		if (gsi.getConnection() != null)
		{
			return GsAuthResponse.ALREADY_REGISTERED;
		}
		
		/**
		 * Check if password and ip are ok.
		 */
		if (!gsi.getPassword().equals(password) || !NetworkUtils.checkIPMatching(gsi.getIp(), gsConnection.getIP()))
		{
			log.info(gsi.getPassword() + " " + password);
			log.info(gsConnection + " wrong ip or password!");
			return GsAuthResponse.NOT_AUTHED;
		}
		
		gsi.setDefaultAddress(defaultAddress);
		gsi.setIpRanges(ipRanges);
		gsi.setPort(port);
		gsi.setMaxPlayers(maxPlayers);
		gsi.setConnection(gsConnection);
		
		gsConnection.setGameServerInfo(gsi);
		return GsAuthResponse.AUTHED;
	}
	
	/**
	 * Retrieves the information for a specific game server.<br>
	 * This method looks up the server using its unique ID.
	 * @param gameServerId The {@code byte} identifier of the server to find.
	 * @return The {@link GameServerInfo} object associated with the ID, or {@code null} if not found.
	 */
	public static GameServerInfo getGameServerInfo(byte gameServerId)
	{
		return gameservers.get(gameServerId);
	}
	
	/**
	 * Checks if a specific account is currently logged into any active game server.<br>
	 * This method iterates through all available servers to find a match.
	 * @param acc The {@link Account} object to check.
	 * @return {@code true} if the account is found on at least one server, otherwise {@code false}.
	 */
	public static boolean isAccountOnAnyGameServer(Account acc)
	{
		for (GameServerInfo gsi : getGameServers())
		{
			if (gsi.isAccountOnGameServer(acc.getId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a specific user from the game server.<br>
	 * This method finds which server the {@code account} is currently on.<br>
	 * It then sends a kick packet to that server.
	 * @param account The {@code Account} object to be disconnected.
	 */
	public static void kickAccountFromGameServer(Account account)
	{
		for (GameServerInfo gsi : getGameServers())
		{
			if (gsi.isAccountOnGameServer(account.getId()))
			{
				gsi.getConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(account.getId()));
				break;
			}
		}
	}
	
	/**
	 * Retrieves the {@link GameServersDAO} instance.<br>
	 * This method uses {@link DAOManager} to fetch the correct data access object.
	 * @return the {@code GameServersDAO} instance.
	 */
	private static GameServersDAO getDAO()
	{
		return DAOManager.getDAO(GameServersDAO.class);
	}
	
	/**
	 * Sends a heartbeat signal to a specific game server.<br>
	 * This method identifies the server by its {@code serverId}.<br>
	 * It then calls the {@code pong} method using the provided {@code pid}.
	 * @param serverId The unique identifier for the target game server.
	 * @param pid The process identifier to include in the heartbeat signal.
	 */
	public static void pong(byte serverId, int pid)
	{
		for (GameServerInfo gsi : getGameServers())
		{
			if (gsi.getId() == serverId)
			{
				gsi.getConnection().pong(pid);
				break;
			}
		}
	}
}
