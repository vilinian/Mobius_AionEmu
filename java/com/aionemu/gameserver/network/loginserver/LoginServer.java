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

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.NioServer;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_L2AUTH_LOGIN_CHECK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RECONNECT_KEY;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_AUTH;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_DISCONNECTED;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_BAN;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_LS_CONTROL;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * This class provides utility methods for establishing a connection between the {@code GameServer} and the {@link LoginServer}.<br>
 * It handles communication protocols to synchronize account data and manage player authentication.<br>
 * Use this class to facilitate network interaction between these two core components.
 * @author -Nemesiss-
 */
public class LoginServer
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServer.class);
	/**
	 * Map<accountId,Connection> for waiting request. This request is send to LoginServer and GameServer is waiting for response.
	 */
	private final Map<Integer, AionConnection> loginRequests = new HashMap<>();
	/**
	 * Map<accountId,Connection> for all logged in accounts.
	 */
	private final Map<Integer, AionConnection> loggedInAccounts = new HashMap<>();
	/**
	 * Connection to LoginServer.
	 */
	private LoginServerConnection loginServer;
	private NioServer nioServer;
	private boolean serverShutdown = false;
	
	/**
	 * Retrieves the singleton instance of the {@link LoginServer}.<br>
	 * This method provides a global access point to the login server manager.
	 * @return The active {@code LoginServer} instance.
	 */
	public static LoginServer getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor to prevent direct instantiation.<br>
	 * This class uses the {@code getInstance} method for access.
	 */
	private LoginServer()
	{
	}
	
	/**
	 * Sets the {@link NioServer} instance for this server.<br>
	 * This method assigns the provided {@code nioServer} to the internal field.
	 * @param nioServer The {@code NioServer} object to be used by the login server.
	 */
	public void setNioServer(NioServer nioServer)
	{
		this.nioServer = nioServer;
	}
	
	/**
	 * Establishes a connection to the LoginServer.<br>
	 * This method attempts to open a {@code SocketChannel} using the address from {@link NetworkConfig}.<br>
	 * It will retry every 10 seconds if the connection fails.
	 * @return A new {@code LoginServerConnection} instance once successfully connected.
	 */
	public LoginServerConnection connect()
	{
		SocketChannel sc;
		for (;;)
		{
			loginServer = null;
			log.info("Connecting to LoginServer: " + NetworkConfig.LOGIN_ADDRESS);
			try
			{
				sc = SocketChannel.open(NetworkConfig.LOGIN_ADDRESS);
				sc.configureBlocking(false);
				final Dispatcher d = nioServer.getReadWriteDispatcher();
				loginServer = new LoginServerConnection(sc, d);
				
				// register
				d.register(sc, SelectionKey.OP_READ, loginServer);
				
				// initialized
				loginServer.initialized();
				
				return loginServer;
			}
			catch (Exception e)
			{
				log.info("Cant connect to LoginServer: " + e.getMessage());
				System.out.println("");
			}
			
			try
			{
				/**
				 * 10s sleep
				 */
				Thread.sleep(10 * 1000);
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/**
	 * Handles the situation where the connection to the login server is lost.<br>
	 * It clears all pending authentication requests and disconnects those clients.<br>
	 * If the server is not shutting down, it schedules a reconnection attempt after 5000 milliseconds.
	 */
	public void loginServerDown()
	{
		log.warn("Connection with LoginServer lost...");
		
		loginServer = null;
		synchronized (this)
		{
			/**
			 * We lost connection for LoginServer so client pending authentication should be disconnected [cuz authentication will never ends]
			 */
			for (AionConnection client : loginRequests.values())
			{
				// TODO! somme error packet!
				client.close(/* closePacket, */true);
			}
			
			loginRequests.clear();
		}
		
		/**
		 * Reconnect after 5s if not server shutdown sequence
		 */
		if (!serverShutdown)
		{
			ThreadPoolManager.getInstance().schedule(() -> connect(), 5000);
		}
	}
	
	/**
	 * Handles the disconnection of an Aion client from the login server.<br>
	 * This method removes the account from active tracking and notifies other systems.
	 * @param accountId The unique identifier for the disconnected account.
	 */
	public void aionClientDisconnected(int accountId)
	{
		synchronized (this)
		{
			loginRequests.remove(accountId);
			loggedInAccounts.remove(accountId);
		}
		
		sendAccountDisconnected(accountId);
	}
	
	/**
	 * Sends a disconnection packet to the login server.<br>
	 * This method checks if the {@code loginServer} is in the {@code State.AUTHED} state before sending.<br>
	 * It notifies the login server that the account with the given ID has disconnected.
	 * @param accountId The unique identifier of the account to disconnect.
	 */
	private void sendAccountDisconnected(int accountId)
	{
		log.info("Sending account disconnected " + accountId);
		if ((loginServer != null) && (loginServer.getState() == State.AUTHED))
		{
			loginServer.sendPacket(new SM_ACCOUNT_DISCONNECTED(accountId));
		}
	}
	
	/**
	 * Requests authentication for a specific client from the Login Server.<br>
	 * This method checks if the {@code loginServer} is active before proceeding.<br>
	 * If the server is unavailable, it closes the {@code client} connection.<br>
	 * Otherwise, it registers the request and sends an {@link com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_AUTH} packet.
	 * @param accountId The unique identifier for the account.
	 * @param client The {@code AionConnection} object representing the connected client.
	 * @param loginOk Status flag indicating if the login was successful.
	 * @param playOk1 Status flag for the first play check.
	 * @param playOk2 Status flag for the second play check.
	 */
	public void requestAuthenticationOfClient(int accountId, AionConnection client, int loginOk, int playOk1, int playOk2)
	{
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not possible.
		 */
		if ((loginServer == null) || (loginServer.getState() != State.AUTHED))
		{
			log.debug("LS !!! " + (loginServer == null ? "NULL" : loginServer.getState()));
			
			// TODO! some error packet!
			client.close(/* closePacket, */true);
			return;
		}
		
		synchronized (this)
		{
			if (loginRequests.containsKey(accountId))
			{
				return;
			}
			
			loginRequests.put(accountId, client);
		}
		
		loginServer.sendPacket(new SM_ACCOUNT_AUTH(accountId, loginOk, playOk1, playOk2));
	}
	
	/**
	 * Processes the authentication response from the login server.<br>
	 * This method validates the account data and updates the client state.<br>
	 * If validation fails, it closes the connection with an error packet.<br>
	 * If successful, it marks the account as authenticated and sends a success packet.
	 * @param accountId The unique identifier for the account.
	 * @param accountName The name of the account being authenticated.
	 * @param result A boolean indicating if the authentication was successful.
	 * @param accountTime The time associated with the account.
	 * @param accessLevel The permission level assigned to the account.
	 * @param membership The membership status of the account.
	 * @param toll The toll value for the account.
	 * @param luna The luna value for the account.
	 * @param isReturn A flag indicating if this is a return request.
	 */
	public void accountAuthenticationResponse(int accountId, String accountName, boolean result, AccountTime accountTime, byte accessLevel, byte membership, long toll, long luna, byte isReturn)
	{
		final AionConnection client = loginRequests.remove(accountId);
		
		if (client == null)
		{
			return;
		}
		
		final Account account = AccountService.getAccount(accountId, accountName, accountTime, accessLevel, membership, toll, luna, isReturn);
		if (!validateAccount(account))
		{
			log.info("[LoginServer] Illegal account auth detected: " + accountId);
			client.close(new SM_L2AUTH_LOGIN_CHECK(false, accountName), true);
			return;
		}
		
		if (result)
		{
			client.setAccount(account);
			client.setState(AionConnection.State.AUTHED);
			loggedInAccounts.put(accountId, client);
			log.info("[LoginServer] Account authed: " + accountId + " = " + accountName);
			client.sendPacket(new SM_L2AUTH_LOGIN_CHECK(true, accountName));
		}
		else
		{
			log.info("[LoginServer] Account not authed: " + accountId);
			client.close(new SM_L2AUTH_LOGIN_CHECK(false, accountName), true);
		}
	}
	
	/**
	 * Checks if the {@code Account} is valid for login.<br>
	 * This method ensures that no characters from this account are currently online.<br>
	 * If a duplicate login is detected, it kicks the existing player and returns {@code false}.
	 * @param account The {@code Account} object to validate.
	 * @return {@code true} if the account can be logged in, or {@code false} otherwise.
	 */
	private boolean validateAccount(Account account)
	{
		for (PlayerAccountData accountData : account)
		{
			if (accountData.getPlayerCommonData().isOnline())
			{
				log.warn("[LoginServer] [AUDIT] Possible dupe hack account: " + account.getId());
				final Player player = World.getInstance().findPlayer(accountData.getPlayerCommonData().getPlayerObjId());
				if (player != null)
				{
					// kick
					PlayerLeaveWorldService.startLeaveWorld(player);
				}
				else
				{
					// db update offline
					DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false);
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Requests a reconnection for an authenticated account.<br>
	 * This method checks if the {@link LoginServer} is currently in the {@code AUTHED} state.<br>
	 * If the server is unavailable, it closes the provided {@code client}.<br>
	 * Otherwise, it registers the request and sends a reconnect key to the client.
	 * @param client The {@code AionConnection} object representing the player's connection.
	 */
	public void requestAuthReconnection(AionConnection client)
	{
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not possible.
		 */
		if ((loginServer == null) || (loginServer.getState() != State.AUTHED))
		{
			// TODO! somme error packet!
			client.close(/* closePacket, */false);
			return;
		}
		
		synchronized (this)
		{
			if (loginRequests.containsKey(client.getAccount().getId()))
			{
				return;
			}
			
			loginRequests.put(client.getAccount().getId(), client);
			
		}
		
		loginServer.sendPacket(new SM_ACCOUNT_RECONNECT_KEY(client.getAccount().getId()));
	}
	
	/**
	 * Handles the response for a client attempting to reconnect.<br>
	 * It retrieves the connection for the specified {@code accountId}.<br>
	 * If found, it sends a {@code SM_RECONNECT_KEY} packet to the client.
	 * @param accountId The unique identifier of the account trying to reconnect.
	 * @param reconnectKey The key provided by the client to verify the reconnection.
	 */
	public void authReconnectionResponse(int accountId, int reconnectKey)
	{
		final AionConnection client = loginRequests.remove(accountId);
		
		if (client == null)
		{
			return;
		}
		
		log.info("[LoginServer] Account reconnecting: " + accountId + " = " + client.getAccount().getName());
		client.close(new SM_RECONNECT_KEY(reconnectKey), false);
	}
	
	/**
	 * Disconnects a specific user from the server.<br>
	 * This method checks if the account is currently active on this {@code GameServer}.<br>
	 * If found, it closes the connection using {@code int)}.<br>
	 * If not found, it notifies the login server that the account is disconnected.
	 * @param accountId The unique identifier of the account to kick.
	 */
	public void kickAccount(int accountId)
	{
		synchronized (this)
		{
			final AionConnection client = loggedInAccounts.get(accountId);
			if (client != null)
			{
				closeClientWithCheck(client, accountId);
			} // This account is not logged in on this GameServer but LS thinks different...
			else
			{
				sendAccountDisconnected(accountId);
			}
		}
	}
	
	/**
	 * Closes the connection for a specific client and removes it from the server.<br>
	 * This method logs the action and schedules a cleanup task to run after 5000 milliseconds.<br>
	 * If the client is still active after the delay, it is forced closed and removed from {@code loggedInAccounts}.
	 * @param client The {@link AionConnection} object representing the client to close.
	 * @param accountId The unique identifier for the account being disconnected.
	 */
	private void closeClientWithCheck(AionConnection client, int accountId)
	{
		log.info("[LoginServer] Closing client connection " + accountId);
		client.close(/* closePacket, */false);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			final AionConnection client1 = loggedInAccounts.get(accountId);
			if (client1 != null)
			{
				log.warn("[LoginServer] Removing client from server because of stalled connection");
				client1.close(false);
				loggedInAccounts.remove(accountId);
				sendAccountDisconnected(accountId);
			}
		}, 5000);
	}
	
	/**
	 * Retrieves a list of all currently logged-in accounts.<br>
	 * The map uses the account ID as the key.
	 * @return An unmodifiable {@link Map} containing account IDs and their corresponding {@link AionConnection} objects.
	 */
	public Map<Integer, AionConnection> getLoggedInAccounts()
	{
		return Collections.unmodifiableMap(loggedInAccounts);
	}
	
	/**
	 * Handles the disconnection between the game server and the login server.<br>
	 * This method sets the {@code serverShutdown} flag to {@code true}.<br>
	 * It closes all active connections in the {@code loginRequests} map.<br>
	 * Finally, it closes the {@link LoginServer} instance if it exists.
	 */
	public void gameServerDisconnected()
	{
		synchronized (this)
		{
			serverShutdown = true;
			/**
			 * GameServer shutting down, must close all pending login requests
			 */
			for (AionConnection client : loginRequests.values())
			{
				// TODO! some error packet!
				client.close(/* closePacket, */true);
			}
			
			loginRequests.clear();
			
			if (loginServer != null)
			{
				loginServer.close(false);
			}
		}
		
		log.info("[LoginServer] GameServer disconnected from the Login Server...");
	}
	
	/**
	 * Sends a control packet to the login server.<br>
	 * This method checks if the {@code loginServer} is in the {@code AUTHED} state before sending.<br>
	 * It uses the {@link SM_LS_CONTROL} packet class to transmit the data.
	 * @param accountName The name of the account.
	 * @param playerName The name of the player.
	 * @param adminName The name of the administrator.
	 * @param param A specific integer parameter for the control packet.
	 * @param type The type of control packet to send.
	 */
	public void sendLsControlPacket(String accountName, String playerName, String adminName, int param, int type)
	{
		if ((loginServer != null) && (loginServer.getState() == State.AUTHED))
		{
			loginServer.sendPacket(new SM_LS_CONTROL(accountName, playerName, adminName, param, type));
		}
	}
	
	/**
	 * Updates specific account properties based on the provided type.<br>
	 * This method modifies either the access level or membership status.
	 * @param accountId The unique identifier of the account to update.
	 * @param param The new value to set for the account property.
	 * @param type The type of update to perform, where {@code 1} is access level and {@code 2} is membership.
	 */
	public void accountUpdate(int accountId, byte param, int type)
	{
		synchronized (this)
		{
			final AionConnection client = loggedInAccounts.get(accountId);
			if (client != null)
			{
				final Account account = client.getAccount();
				if (type == 1)
				{
					account.setAccessLevel(param);
				}
				
				if (type == 2)
				{
					account.setMembership(param);
				}
			}
		}
	}
	
	/**
	 * Sends a ban packet to the login server.<br>
	 * This method notifies the {@code AUTHED} state of the login server about an account ban.
	 * @param type The type of the ban packet.
	 * @param accountId The unique identifier of the account being banned.
	 * @param ip The IP address associated with the account.
	 * @param time The duration of the ban in seconds.
	 * @param adminObjId The ID of the administrator who issued the ban.
	 */
	public void sendBanPacket(byte type, int accountId, String ip, int time, int adminObjId)
	{
		if ((loginServer != null) && (loginServer.getState() == State.AUTHED))
		{
			loginServer.sendPacket(new SM_BAN(type, accountId, ip, time, adminObjId));
		}
	}
	
	/**
	 * Sends a packet to the login server.<br>
	 * This method checks if the {@code loginServer} is active and in the {@code AUTHED} state.<br>
	 * It returns {@code true} if the packet was sent successfully.<br>
	 * It returns {@code false} if the connection is not available.
	 * @param pk The {@code LsServerPacket} to be sent.
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	public boolean sendPacket(LsServerPacket pk)
	{
		if ((loginServer != null) && (loginServer.getState() == State.AUTHED))
		{
			loginServer.sendPacket(pk);
			return true;
		}
		
		return false;
	}
	
	private static class SingletonHolder
	{
		protected static final LoginServer instance = new LoginServer();
	}
}
