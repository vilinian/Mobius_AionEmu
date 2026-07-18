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
package system.database.mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for wedding-related data.<br>
 * It extends {@link WeddingDAO} to handle specific SQL queries and operations.
 * @author synchro2
 */
public class MySQL5WeddingDAO extends WeddingDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PortalCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `weddings` (`player1`, `player2`) VALUES (?,?)";
	public static final String SELECT_QUERY = "SELECT `player1`, `player2` FROM `weddings` WHERE `player1`=? OR `player2`=?";
	public static final String DELETE_QUERY = "DELETE FROM `weddings` WHERE (`player1`=? AND `player2`=?) OR (`player2`=? AND `player1`=?)";
	
	/**
	 * Retrieves the unique ID of a player's wedding partner.<br>
	 * This method queries the database using the {@code Player} object.<br>
	 * It returns 0 if no partner is found or an error occurs.
	 * @param player The {@link Player} whose partner ID needs to be loaded.
	 * @return The unique integer ID of the partner, or 0 if not found.
	 */
	@Override
	public int loadPartnerId(Player player)
	{
		Connection con = null;
		final int playerId = player.getObjectId();
		int partnerId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, playerId);
			final ResultSet rset = stmt.executeQuery();
			int partner1Id = 0;
			int partner2Id = 0;
			if (rset.next())
			{
				partner1Id = rset.getInt("player1");
				partner2Id = rset.getInt("player2");
			}
			
			partnerId = playerId == partner1Id ? partner2Id : partner1Id;
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not get partner for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return partnerId;
	}
	
	/**
	 * Saves a new wedding record to the database.<br>
	 * This method links two {@link Player} objects together.<br>
	 * It uses the {@code INSERT_QUERY} to store their IDs.
	 * @param partner1 The first player in the marriage.
	 * @param partner2 The second player in the marriage.
	 */
	@Override
	public void storeWedding(Player partner1, Player partner2)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(INSERT_QUERY);
			
			stmt.setInt(1, partner1.getObjectId());
			stmt.setInt(2, partner2.getObjectId());
			stmt.execute();
		}
		catch (SQLException e)
		{
			log.error("storeWeddings", e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
	}
	
	/**
	 * Removes the wedding record from the database.<br>
	 * This method deletes the link between two {@link Player} objects.<br>
	 * It uses the unique object IDs of both players to find the entry.
	 * @param partner1 The first player involved in the wedding.
	 * @param partner2 The second player involved in the wedding.
	 */
	@Override
	public void deleteWedding(Player partner1, Player partner2)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY);
			
			stmt.setInt(1, partner1.getObjectId());
			stmt.setInt(2, partner2.getObjectId());
			stmt.setInt(3, partner1.getObjectId());
			stmt.setInt(4, partner2.getObjectId());
			stmt.execute();
		}
		catch (SQLException e)
		{
			log.error("deleteWedding", e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
	}
	
	/**
	 * Checks if the current database configuration supports specific requirements.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param arg0 The first requirement string.
	 * @param arg1 The first integer value.
	 * @param arg2 The second integer value.
	 * @return {@code true} if the requirements are met, otherwise {@code false}.
	 */
	@Override
	public boolean supports(String arg0, int arg1, int arg2)
	{
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}
