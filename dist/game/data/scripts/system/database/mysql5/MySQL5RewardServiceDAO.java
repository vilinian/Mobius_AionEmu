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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.RewardServiceDAO;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;

/**
 * This class provides the database access layer for handling rewards using a {@code mysql5} connection.<br>
 * It extends {@link RewardServiceDAO} to implement specific queries for reward data.
 * @author KID
 */
public class MySQL5RewardServiceDAO extends RewardServiceDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5RewardServiceDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `web_reward` SET `rewarded`=?, received=NOW() WHERE `unique`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `web_reward` WHERE `item_owner`=? AND `rewarded`=?";
	
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
	
	/**
	 * Retrieves all rewards that a specific player is eligible to claim.<br>
	 * It fetches items from the database where the owner matches the {@code playerId}.<br>
	 * The method returns a {@link List} containing {@link RewardEntryItem} objects.
	 * @param playerId The unique identifier of the player.
	 * @return A list of available rewards for the given player.
	 */
	@Override
	public List<RewardEntryItem> getAvailable(int playerId)
	{
		final List<RewardEntryItem> list = new ArrayList<>();
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, 0);
			
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int unique = rset.getInt("unique");
				final int item_id = rset.getInt("item_id");
				final long count = rset.getLong("item_count");
				list.add(new RewardEntryItem(unique, item_id, count));
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.warn("getAvailable() for " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return list;
	}
	
	/**
	 * This method marks specific rewards as no longer available.<br>
	 * It updates the database for each ID provided in the {@code ids} list.<br>
	 * The status is updated using the {@code UPDATE_QUERY}.
	 * @param ids A {@code List} of unique reward identifiers to update.
	 */
	@Override
	public void uncheckAvailable(List<Integer> ids)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			for (int uniqid : ids)
			{
				stmt = con.prepareStatement(UPDATE_QUERY);
				stmt.setInt(1, 1);
				stmt.setInt(2, uniqid);
				stmt.execute();
				stmt.close();
			}
		}
		catch (Exception e)
		{
			log.error("uncheckAvailable", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
}
