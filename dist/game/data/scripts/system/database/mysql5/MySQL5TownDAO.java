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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.TownDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.town.Town;

/**
 * This class provides the Data Access Object (DAO) implementation for managing {@link Town} data using a {@code mysql5} database.<br>
 * It extends {@link TownDAO} to handle specific SQL queries and operations for town records.
 * @author ViAl
 */
public class MySQL5TownDAO extends TownDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5TownDAO.class);
	private static final String SELECT_QUERY = "SELECT * FROM `towns` WHERE `race` = ?";
	private static final String INSERT_QUERY = "INSERT INTO `towns`(`id`,`level`,`points`, `race`) VALUES (?,?,?,?)";
	private static final String UPDATE_QUERY = "UPDATE `towns` SET `level` = ?, `points` = ?, `level_up_date` = ? WHERE `id` = ?";
	
	/**
	 * Loads all {@link Town} objects from the database for a specific race.<br>
	 * This method queries the database using the provided {@code race}.<br>
	 * It returns a map where the keys are town IDs.
	 * @param race The {@code Race} type to filter by.
	 * @return A {@link Map} containing the loaded {@link Town} objects.
	 */
	@Override
	public Map<Integer, Town> load(Race race)
	{
		final Map<Integer, Town> towns = new HashMap<>();
		Connection conn = null;
		try
		{
			conn = DatabaseFactory.getConnection();
			final PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
			stmt.setString(1, race.toString());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int id = rset.getInt("id");
				final int level = rset.getInt("level");
				final int points = rset.getInt("points");
				final Timestamp levelUpDate = rset.getTimestamp("level_up_date");
				final Town town = new Town(id, level, points, race, levelUpDate);
				towns.put(town.getId(), town);
			}
			
			rset.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("Can't load towns info. " + e);
		}
		finally
		{
			DatabaseFactory.close(conn);
		}
		
		return towns;
	}
	
	/**
	 * Saves the state of a {@link Town} to the database.<br>
	 * This method checks the {@code PersistentState} of the provided object.<br>
	 * It calls {@code insertTown} if the town is new.<br>
	 * It calls {@code updateTown} if an update is required.
	 * @param town The {@code Town} object to be saved.
	 */
	@Override
	public void store(Town town)
	{
		switch (town.getPersistentState())
		{
			case NEW:
				insertTown(town);
				break;
			case UPDATE_REQUIRED:
				updateTown(town);
				break;
			default:
				break;
		}
	}
	
	/**
	 * This method saves a new {@code Town} object into the database.<br>
	 * It uses the {@code INSERT_QUERY} to add the town data.<br>
	 * The state of the {@code town} is updated to {@code PersistentState.UPDATED} after success.
	 * @param town The {@code Town} object to be inserted into the database.
	 */
	private void insertTown(Town town)
	{
		Connection conn = null;
		try
		{
			conn = DatabaseFactory.getConnection();
			final PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, town.getId());
			stmt.setInt(2, town.getLevel());
			stmt.setInt(3, town.getPoints());
			stmt.setString(4, town.getRace().toString());
			stmt.executeUpdate();
			stmt.close();
			town.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e)
		{
			log.error("Can insert new town into database! Town id:" + town.getId() + ". " + e);
		}
		finally
		{
			DatabaseFactory.close(conn);
		}
	}
	
	/**
	 * Updates the information for a specific {@link Town} in the database.<br>
	 * This method saves the current level, points, and update timestamp.<br>
	 * It also sets the persistent state to {@code PersistentState.UPDATED}.
	 * @param town The {@code Town} object containing the updated data.
	 */
	private void updateTown(Town town)
	{
		Connection conn = null;
		try
		{
			conn = DatabaseFactory.getConnection();
			final PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, town.getLevel());
			stmt.setInt(2, town.getPoints());
			stmt.setTimestamp(3, town.getLevelUpDate());
			stmt.setInt(4, town.getId());
			stmt.executeUpdate();
			stmt.close();
			town.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e)
		{
			log.error("Can insert new town into database! Town id:" + town.getId() + ". " + e);
		}
		finally
		{
			DatabaseFactory.close(conn);
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
