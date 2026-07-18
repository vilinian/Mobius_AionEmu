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
import com.aionemu.gameserver.dao.RealItemRndBonusDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.RealRandomBonus;
import com.aionemu.gameserver.model.items.RealRandomBonusStat;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * This class provides the {@code MySQL5} database implementation for handling real random item bonuses.<br>
 * It extends {@link RealItemRndBonusDAO} to perform specific data access operations for these items.
 */
public class MySQL5RealItemRndBonusDAO extends RealItemRndBonusDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5RealItemRndBonusDAO.class);
	public static final String SELECT_QUERY = "SELECT * FROM real_item_rnd_bonus WHERE item_obj_id = ?";
	public static final String INSERT_QUERY = "INSERT INTO real_item_rnd_bonus (`item_obj_id`, `stat_name`, `stat_val`, `is_fusion`) VALUES (?, ?, ?, ?)";
	public static final String DELETE_QUERY_ALL = "DELETE FROM real_item_rnd_bonus WHERE item_obj_id = ?";
	public static final String DELETE_QUERY_MAIN = "DELETE FROM real_item_rnd_bonus WHERE item_obj_id = ? AND is_fusion = 0";
	public static final String DELETE_QUERY_FUSION = "DELETE FROM real_item_rnd_bonus WHERE item_obj_id = ? AND is_fusion = 1";
	public static final String UPDATE_QUERY = "UPDATE real_item_rnd_bonus SET `item_obj_id` = ?, `is_fusion` = ? WHERE item_obj_id = ?";
	
	/**
	 * Loads the random bonuses for a specific item from the database.<br>
	 * This method fetches all stats associated with the {@code Item} object ID.<br>
	 * It then updates the {@link Item} with a new {@link RealRandomBonus} object.
	 * @param item The {@code Item} to load bonuses for.
	 */
	@Override
	public void loadRandomBonuses(Item item)
	{
		Connection con = null;
		final List<RealRandomBonusStat> stats = new ArrayList<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, item.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				stats.add(new RealRandomBonusStat(StatEnum.findByStringName(rset.getString("stat_name")), rset.getInt("stat_val"), rset.getInt("is_fusion") == 0 ? false : true));
				System.out.println("RANDOM-STATS Loaded " + rset.getString("stat_name"));
			}
			
			final RealRandomBonus bonus = new RealRandomBonus(item.getObjectId(), stats);
			item.setRealRndBonus(bonus);
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealItemRandBonusDAO.loadRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
	}
	
	/**
	 * Updates the random bonuses for a specific item in the database.<br>
	 * This method saves all stats contained within the {@code RealRandomBonus} object.<br>
	 * It uses the {@code INSERT_QUERY} to store each individual stat.
	 * @param bonus The {@code RealRandomBonus} object containing the data to save.
	 */
	@Override
	public void updateRandomBonuses(RealRandomBonus bonus)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			
			for (RealRandomBonusStat stat : bonus.getStats())
			{
				stmt = con.prepareStatement(INSERT_QUERY);
				stmt.setInt(1, bonus.getItemObjectId());
				stmt.setString(2, stat.getStat().toString());
				stmt.setInt(3, stat.getValue());
				stmt.setInt(4, !stat.isFusion() ? 0 : 1);
				stmt.execute();
			}
			
			if (stmt != null)
			{
				stmt.close();
			}
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealRandomBonusesDAO.updateRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
	}
	
	/**
	 * Removes all random bonuses associated with a specific item from the database.<br>
	 * This method uses the {@code DELETE_QUERY_ALL} to clear all entries for the given {@code Item}.
	 * @param item The {@link Item} object whose bonuses should be deleted.
	 */
	@Override
	public void deleteAllRandomBonuses(Item item)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY_ALL);
			stmt.setInt(1, item.getObjectId());
			stmt.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealRandomBonusesDAO.deleteRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
	}
	
	/**
	 * Updates the fusion random bonuses for two items.<br>
	 * This method sets the {@code is_fusion} flag to {@code 1}.<br>
	 * It links the first item's ID to the second item's ID in the database.
	 * @param first The primary {@link Item} being updated.
	 * @param second The secondary {@link Item} used for the fusion link.
	 */
	@Override
	public void updateFusionRandomBonuses(Item first, Item second)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, first.getObjectId());
			stmt.setInt(2, 1);
			stmt.setInt(3, second.getObjectId());
			stmt.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealRandomBonusesDAO.deleteRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
	}
	
	/**
	 * Removes the main random bonuses for a specific item.<br>
	 * This method deletes records where {@code is_fusion} is {@code 0}.<br>
	 * It uses the {@code DELETE_QUERY_MAIN} query to perform the operation.
	 * @param item The {@link Item} object whose bonuses should be deleted.
	 */
	@Override
	public void deleteMainRandomBonuses(Item item)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY_MAIN);
			stmt.setInt(1, item.getObjectId());
			stmt.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealRandomBonusesDAO.deleteRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
	}
	
	/**
	 * Removes all fusion random bonuses for a specific item.<br>
	 * This method updates the database by executing {@code DELETE_QUERY_FUSION}.<br>
	 * It targets records where the {@code is_fusion} flag is set to {@code 1}.
	 * @param item The {@link Item} object whose fusion bonuses should be deleted.
	 */
	@Override
	public void deleteFusionRandomBonuses(Item item)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY_FUSION);
			stmt.setInt(1, item.getObjectId());
			stmt.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("Error in MySQL5RealRandomBonusesDAO.deleteRandomBonuses", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		return;
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
