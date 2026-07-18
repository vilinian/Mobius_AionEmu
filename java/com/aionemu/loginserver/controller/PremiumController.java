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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.dao.PremiumDAO;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_PREMIUM_RESPONSE;

/**
 * This class handles premium-related requests for the login server.<br>
 * It manages interactions between users and the {@link PremiumDAO} to verify subscription statuses.
 * @author KID
 */
public class PremiumController
{
	private final Logger log = LoggerFactory.getLogger("PREMIUM_CTRL");
	private static PremiumController controller = new PremiumController();
	
	/**
	 * Provides the singleton instance of the {@link PremiumController}.<br>
	 * Use this method to access the main controller for premium operations.
	 * @return The global {@code PremiumController} instance.
	 */
	public static PremiumController getController()
	{
		return controller;
	}
	
	public static byte RESULT_FAIL = 1;
	public static byte RESULT_LOW_POINTS = 2;
	public static byte RESULT_OK = 3;
	public static byte RESULT_ADD = 4;
	private final PremiumDAO dao;
	
	/**
	 * Initializes the {@code PremiumController}.<br>
	 * It sets up the required database connections.<br>
	 * This method prepares the controller to handle premium requests.
	 */
	public PremiumController()
	{
		dao = DAOManager.getDAO(PremiumDAO.class);
		log.info("PremiumController is ready for requests.");
	}
	
	/**
	 * Processes a request to purchase an item or service.<br>
	 * It checks the account balance and updates points if the transaction is valid.<br>
	 * The result is sent back to the game server via {@code SM_PREMIUM_RESPONSE}.
	 * @param accountId The unique identifier for the user account.
	 * @param requestId The specific ID of the request being processed.
	 * @param cost The amount of points to deduct from the account.
	 * @param serverId The ID of the game server where the request originated.
	 */
	public void requestBuy(int accountId, int requestId, long cost, byte serverId)
	{
		long points = dao.getPoints(accountId);
		
		final GameServerInfo server = GameServerTable.getGameServerInfo(serverId);
		if ((server == null) || (server.getConnection() == null) || !server.isAccountOnGameServer(accountId))
		{
			log.error("Account " + accountId + " requested " + requestId + " from gs #" + serverId + " and server is down.");
			return;
		}
		
		// adding new tolls
		if (cost < 0)
		{
			final long ncnt = points + (cost * -1);
			dao.updatePoints(accountId, ncnt, 0);
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_ADD, ncnt));
			return;
		}
		
		if (points < cost)
		{
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_LOW_POINTS, points));
			return;
		}
		
		if (dao.updatePoints(accountId, points, cost))
		{
			points -= cost;
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_OK, points));
			log.info("Acount " + accountId + " succed in purchasing lot #" + requestId + " for " + cost + " from server #" + serverId);
		}
		else
		{
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_FAIL, points));
			log.info("Acount " + accountId + " failed in purchasing lot #" + requestId + " for " + cost + " from server #" + serverId + ". !updatePoints");
		}
	}
}
