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
package com.aionemu.loginserver.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.dao.PremiumDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.model.ReconnectingAccount;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.LoginConnection.State;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.SM_SERVER_LIST;
import com.aionemu.loginserver.network.aion.serverpackets.SM_UPDATE_SESSION;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_AUTH_RESPONSE;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_GS_CHARACTER_RESPONSE;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;
import com.aionemu.loginserver.utils.AccountUtils;

/**
 * This class handles all account-related operations within the login server.<br>
 * It manages core logic for authenticating users and processing {@code Account} actions.
 * @author KID
 * @author SoulKeeper
 */
public class AccountController
{
	/**
	 * Map with accounts that are active on LoginServer or joined GameServer and are not authenticated yet.
	 */
	private static final Map<Integer, LoginConnection> accountsOnLS = new HashMap<>();
	/**
	 * Map with accounts that are reconnecting to LoginServer ie was joined GameServer.
	 */
	private static final Map<Integer, ReconnectingAccount> reconnectingAccounts = new HashMap<>();
	/**
	 * Map with characters count on each gameserver and accounts
	 */
	private static final Map<Integer, Map<Integer, Integer>> accountsGSCharacterCounts = new HashMap<>();
	
	/**
	 * Removes an {@code Account} from the active login server list.<br>
	 * This method updates the internal map by using the unique ID of the provided account.
	 * @param account The {@code Account} object to be removed.
	 */
	public static synchronized void removeAccountOnLS(Account account)
	{
		accountsOnLS.remove(account.getId());
	}
	
	/**
	 * Verifies the authentication of an account using a session key.<br>
	 * If successful, it moves the account from the login server to the game server.<br>
	 * It also updates the last known server and sends the final response packet.
	 * @param key The {@code SessionKey} used to validate the current connection.
	 * @param gsConnection The {@code GsConnection} object representing the game server link.
	 */
	public static synchronized void checkAuth(SessionKey key, GsConnection gsConnection)
	{
		final LoginConnection con = accountsOnLS.get(key.accountId);
		
		if ((con != null) && con.getSessionKey().checkSessionKey(key))
		{
			/**
			 * account is successful logged in on gs remove it from here
			 */
			accountsOnLS.remove(key.accountId);
			
			final GameServerInfo gsi = gsConnection.getGameServerInfo();
			final Account acc = con.getAccount();
			
			/**
			 * Add account to accounts on GameServer list and update accounts last server
			 */
			gsi.addAccountToGameServer(acc);
			
			acc.setLastServer(gsi.getId());
			getAccountDAO().updateLastServer(acc.getId(), acc.getLastServer());
			
			final long toll = DAOManager.getDAO(PremiumDAO.class).getPoints(acc.getId());
			final long luna = DAOManager.getDAO(PremiumDAO.class).getLuna(acc.getId());
			/**
			 * Send response to GameServer
			 */
			gsConnection.sendPacket(new SM_ACCOUNT_AUTH_RESPONSE(key.accountId, true, acc.getName(), acc.getAccessLevel(), acc.getMembership(), toll, luna, acc.getReturn()));
		}
		else
		{
			gsConnection.sendPacket(new SM_ACCOUNT_AUTH_RESPONSE(key.accountId, false, null, (byte) 0, (byte) 0, 0, 0, (byte) 0));
		}
	}
	
	/**
	 * Adds a new {@code ReconnectingAccount} to the internal tracking map.<br>
	 * This method ensures that the account is registered for reconnection handling.
	 * @param acc The {@code ReconnectingAccount} object to be added.
	 */
	public static synchronized void addReconnectingAccount(ReconnectingAccount acc)
	{
		reconnectingAccounts.put(acc.getAccount().getId(), acc);
	}
	
	/**
	 * Authenticates an account that is currently in the reconnection queue.<br>
	 * This method verifies the {@code reconnectKey} before completing the login.<br>
	 * If the key is invalid or the account is missing, the {@link LoginConnection} is closed.
	 * @param accountId The unique identifier of the account to authenticate.
	 * @param loginOk A status flag indicating if the login was successful.
	 * @param reconnectKey The security key provided by the client for reconnection.
	 * @param client The {@link LoginConnection} object representing the current client session.
	 */
	public static synchronized void authReconnectingAccount(int accountId, int loginOk, int reconnectKey, LoginConnection client)
	{
		final ReconnectingAccount reconnectingAccount = reconnectingAccounts.remove(accountId);
		
		if ((reconnectingAccount != null) && (reconnectingAccount.getReconnectionKey() == reconnectKey))
		{
			final Account acc = reconnectingAccount.getAccount();
			
			client.setAccount(acc);
			accountsOnLS.put(acc.getId(), client);
			client.setState(State.AUTHED_LOGIN);
			client.setSessionKey(new SessionKey(client.getAccount()));
			client.sendPacket(new SM_UPDATE_SESSION(client.getSessionKey()));
		}
		else
		{
			client.closeNow();
		}
	}
	
	/**
	 * Authenticates a user based on their name and password.<br>
	 * This method checks for IP bans, account existence, and active penalties.<br>
	 * It also verifies if the account is already logged in elsewhere.
	 * @param name The username of the account to log in.
	 * @param password The plain text password provided by the user.
	 * @param connection The {@link LoginConnection} object for the current session.
	 * @return An {@link AionAuthResponse} indicating the result of the login attempt.
	 */
	public static AionAuthResponse login(String name, String password, LoginConnection connection)
	{
		// if ip is banned
		if (BannedIpController.isBanned(connection.getIP()))
		{
			return AionAuthResponse.BAN_IP;
		}
		
		Account account = loadAccount(name);
		
		// Try to create new account
		if ((account == null) && Config.ACCOUNT_AUTO_CREATION)
		{
			account = createAccount(name, password);
		}
		
		// If account not found and not created
		if (account == null)
		{
			return AionAuthResponse.INVALID_PASSWORD;
		}
		
		if ((account.getAccessLevel() < Config.MAINTENANCE_MOD_GMLEVEL) && Config.MAINTENANCE_MOD)
		{
			return AionAuthResponse.GM_ONLY;
		}
		
		// Check if the passwords are equal.
		if (!account.getPasswordHash().equals(AccountUtils.encodePassword(password)) || (account.getActivated() != 1))
		{
			return AionAuthResponse.INVALID_PASSWORD;
		}
		
		// If account expired
		if (AccountTimeController.isAccountExpired(account))
		{
			return AionAuthResponse.TIME_EXPIRED;
		}
		
		// if account is banned
		if (AccountTimeController.isAccountPenaltyActive(account))
		{
			return AionAuthResponse.BAN_IP;
		}
		
		// if account is restricted to some ip or mask
		if (account.getIpForce() != null)
		{
			if (!NetworkUtils.checkIPMatching(account.getIpForce(), connection.getIP()))
			{
				return AionAuthResponse.BAN_IP;
			}
		}
		
		// Do not allow to login two times with same account
		synchronized (AccountController.class)
		{
			if (GameServerTable.isAccountOnAnyGameServer(account))
			{
				GameServerTable.kickAccountFromGameServer(account);
				return AionAuthResponse.ALREADY_LOGGED_IN;
			}
			
			// If someone is at loginserver, he should be disconnected
			if (accountsOnLS.containsKey(account.getId()))
			{
				final LoginConnection aionConnection = accountsOnLS.remove(account.getId());
				
				aionConnection.closeNow();
				return AionAuthResponse.ALREADY_LOGGED_IN;
			}
			
			connection.setAccount(account);
			accountsOnLS.put(account.getId(), connection);
		}
		
		AccountTimeController.updateOnLogin(account);
		
		// if everything was OK
		getAccountDAO().updateLastIp(account.getId(), connection.getIP());
		
		// last mac is updated after receiving packet from gameserver
		getAccountDAO().updateMembership(account.getId());
		
		return AionAuthResponse.AUTHED;
	}
	
	/**
	 * Disconnects a specific account from the game servers.<br>
	 * This method sends a kick packet to the active game server.<br>
	 * It also removes the account from the login server connection map.
	 * @param accountId The unique identifier of the account to be kicked.
	 */
	public static void kickAccount(int accountId)
	{
		synchronized (AccountController.class)
		{
			for (GameServerInfo gsi : GameServerTable.getGameServers())
			{
				if (gsi.isAccountOnGameServer(accountId))
				{
					gsi.getConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(accountId));
					break;
				}
			}
			
			if (accountsOnLS.containsKey(accountId))
			{
				final LoginConnection conn = accountsOnLS.remove(accountId);
				conn.closeNow();
			}
		}
	}
	
	/**
	 * Updates the last known MAC address for a specific account.<br>
	 * This method calls {@code String)} to save the new data.
	 * @param accountId The unique identifier of the account.
	 * @param address The new MAC address string to store.
	 * @return {@code true} if the update was successful, {@code false} otherwise.
	 */
	public static boolean refreshAccountsLastMac(int accountId, String address)
	{
		return getAccountDAO().updateLastMac(accountId, address);
	}
	
	/**
	 * Retrieves an {@link Account} from the database using a name.<br>
	 * This method also updates the account time if the account exists.
	 * @param name The unique name of the account to find.
	 * @return The found {@code Account} object or {@code null} if no match is found.
	 */
	public static Account loadAccount(String name)
	{
		final Account account = getAccountDAO().getAccount(name);
		if (account != null)
		{
			account.setAccountTime(getAccountTimeDAO().getAccountTime(account.getId()));
		}
		
		return account;
	}
	
	/**
	 * Retrieves an {@link Account} object from the database using its unique identifier.<br>
	 * This method also updates the account's time information if the account exists.
	 * @param id The unique integer ID of the account to load.
	 * @return The loaded {@link Account} object, or {@code null} if no account is found with that ID.
	 */
	public static Account loadAccount(int id)
	{
		final Account account = getAccountDAO().getAccount(id);
		if (account != null)
		{
			account.setAccountTime(getAccountTimeDAO().getAccountTime(id));
		}
		
		return account;
	}
	
	/**
	 * Creates a new {@link Account} in the database.<br>
	 * This method hashes the password and sets default values for the new user.<br>
	 * It returns the created account or {@code null} if the insertion fails.
	 * @param name The unique username for the new account.
	 * @param password The plain text password to be encoded.
	 * @return The newly created {@link Account} object, or {@code null} if creation failed.
	 */
	public static Account createAccount(String name, String password)
	{
		final String passwordHash = AccountUtils.encodePassword(password);
		final Account account = new Account();
		
		account.setName(name);
		account.setPasswordHash(passwordHash);
		account.setAccessLevel((byte) 0);
		account.setMembership((byte) 0);
		account.setActivated((byte) 1);
		account.setReturn((byte) 0);
		account.setReturnEnd(new Timestamp(System.currentTimeMillis()));
		
		if (getAccountDAO().insertAccount(account))
		{
			return account;
		}
		
		return null;
	}
	
	/**
	 * Retrieves the {@link AccountDAO} instance from the manager.<br>
	 * This method provides access to account database operations.
	 * @return The {@code AccountDAO} instance.
	 */
	private static AccountDAO getAccountDAO()
	{
		return DAOManager.getDAO(AccountDAO.class);
	}
	
	/**
	 * Retrieves the {@link AccountTimeDAO} instance from the manager.<br>
	 * This method provides access to database operations for account time data.
	 * @return The {@code AccountTimeDAO} object.
	 */
	private static AccountTimeDAO getAccountTimeDAO()
	{
		return DAOManager.getDAO(AccountTimeDAO.class);
	}
	
	/**
	 * Loads the character counts for a specific account across all game servers.<br>
	 * This method updates the internal cache and sends the necessary packets to each server.<br>
	 * It also triggers a server list update if all character counts are successfully retrieved.
	 * @param accountId The unique identifier of the account to process.
	 */
	public static synchronized void loadGSCharactersCount(int accountId)
	{
		GsConnection gsc = null;
		Map<Integer, Integer> accountCharacterCount = null;
		
		if (accountsGSCharacterCounts.containsKey(accountId))
		{
			accountsGSCharacterCounts.remove(accountId);
		}
		
		accountsGSCharacterCounts.put(accountId, new HashMap<>());
		
		accountCharacterCount = accountsGSCharacterCounts.get(accountId);
		
		for (GameServerInfo gsi : GameServerTable.getGameServers())
		{
			gsc = gsi.getConnection();
			
			if (gsc != null)
			{
				gsc.sendPacket(new SM_GS_CHARACTER_RESPONSE(accountId));
			}
			else
			{
				accountCharacterCount.put((int) gsi.getId(), 0);
			}
		}
		
		if (hasAllGSCharacterCounts(accountId))
		{
			sendServerListFor(accountId);
		}
	}
	
	/**
	 * Checks if an account has characters on all available game servers.<br>
	 * It compares the character count for the given {@code accountId} against the total number of game servers.
	 * @param accountId The unique identifier of the account to check.
	 * @return {@code true} if the account has characters on every server, otherwise {@code false}.
	 */
	public static synchronized boolean hasAllGSCharacterCounts(int accountId)
	{
		final Map<Integer, Integer> characterCount = accountsGSCharacterCounts.get(accountId);
		
		if (characterCount != null)
		{
			if (characterCount.size() == GameServerTable.getGameServers().size())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sends the server list packet to a specific account.<br>
	 * This method checks if the {@code accountId} exists in the active connections.<br>
	 * If found, it sends an {@link com.aionemu.loginserver.network.aion.serverpackets.SM_SERVER_LIST} packet.
	 * @param accountId The unique identifier of the account to receive the packet.
	 */
	public static void sendServerListFor(int accountId)
	{
		if (accountsOnLS.containsKey(accountId))
		{
			accountsOnLS.get(accountId).sendPacket(new SM_SERVER_LIST());
		}
	}
	
	/**
	 * Retrieves the count of game server characters for a specific account.<br>
	 * This method looks up the data using the provided {@code accountId}.
	 * @param accountId The unique identifier of the account to check.
	 * @return A {@code Map<Integer, Integer>} containing the character counts.
	 */
	public static Map<Integer, Integer> getGSCharacterCountsFor(int accountId)
	{
		return accountsGSCharacterCounts.get(accountId);
	}
	
	/**
	 * Updates the number of characters for a specific game server.<br>
	 * This method stores the count in the internal tracking map.
	 * @param accountId The unique identifier for the user account.
	 * @param gsid The unique identifier for the game server.
	 * @param characterCount The number of characters to record for this server.
	 */
	public static synchronized void addGSCharacterCountFor(int accountId, int gsid, int characterCount)
	{
		if (!accountsGSCharacterCounts.containsKey(accountId))
		{
			accountsGSCharacterCounts.put(accountId, new HashMap<>());
		}
		
		accountsGSCharacterCounts.get(accountId).put(gsid, characterCount);
	}
}
