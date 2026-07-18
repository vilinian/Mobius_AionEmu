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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.HouseObjectCooldownsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for managing house object cooldowns.<br>
 * It handles data persistence and retrieval operations specifically for the {@link HouseObjectCooldownsDAO} interface.
 * @author Rolandas
 */
public class MySQL5HouseObjectCooldownsDAO extends HouseObjectCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5CraftCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `house_object_cooldowns` (`player_id`, `object_id`, `reuse_time`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `house_object_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `object_id`, `reuse_time` FROM `house_object_cooldowns` WHERE `player_id`=?";
	
	/**
	 * Loads the house object cooldowns from the database for a specific player.<br>
	 * This method populates the {@code Player} object with active cooldown data.<br>
	 * It filters out any cooldowns that have already expired.
	 * @param player The {@link Player} whose cooldowns need to be loaded.
	 */
	@Override
	public void loadHouseObjectCooldowns(Player player)
	{
		Connection con = null;
		final Map<Integer, Long> houseObjectCoolDowns = new HashMap<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			
			while (rset.next())
			{
				final int objectId = rset.getInt("object_id");
				final long reuseTime = rset.getLong("reuse_time");
				final int delay = (int) ((reuseTime - System.currentTimeMillis()) / 1000);
				
				if (delay > 0)
				{
					houseObjectCoolDowns.put(objectId, reuseTime);
				}
			}
			
			player.getHouseObjectCooldownList().setHouseObjectCooldowns(houseObjectCoolDowns);
			rset.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("LoadHouseObjectCooldowns", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the current house object cooldowns for a specific player to the database.<br>
	 * This method first clears existing records for the {@code Player}.<br>
	 * It then inserts new cooldown entries if the reuse time is in the future.
	 * @param player The {@link Player} whose cooldown data needs to be saved.
	 */
	@Override
	public void storeHouseObjectCooldowns(Player player)
	{
		deleteHouseObjectCoolDowns(player);
		final Map<Integer, Long> houseObjectCoolDowns = player.getHouseObjectCooldownList().getHouseObjectCooldowns();
		
		if (houseObjectCoolDowns == null)
		{
			return;
		}
		
		for (Map.Entry<Integer, Long> entry : houseObjectCoolDowns.entrySet())
		{
			final int templateId = entry.getKey();
			final long reuseTime = entry.getValue();
			
			if (reuseTime < System.currentTimeMillis())
			{
				continue;
			}
			
			Connection con = null;
			
			try
			{
				con = DatabaseFactory.getConnection();
				final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
				
				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, templateId);
				stmt.setLong(3, reuseTime);
				stmt.execute();
			}
			catch (SQLException e)
			{
				log.error("storeHouseObjectCoolDowns", e);
			}
			finally
			{
				DatabaseFactory.close(con);
			}
		}
	}
	
	/**
	 * Removes all house object cooldown records for a specific player.<br>
	 * This method clears the data from the database using the {@code DELETE_QUERY}.<br>
	 * It handles the connection and statement lifecycle internally.
	 * @param player The {@link Player} whose cooldowns should be deleted.
	 */
	private void deleteHouseObjectCoolDowns(Player player)
	{
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		}
		catch (SQLException e)
		{
			log.error("deleteHouseObjectCoolDowns", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Checks if the current database is compatible with this DAO.<br>
	 * It uses {@code int, int)} to verify the version.
	 * @param databaseName The name of the database to check.
	 * @param majorVersion The major version number of the database.
	 * @param minorVersion The minor version number of the database.
	 * @return {@code true} if the database is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion)
	{
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
