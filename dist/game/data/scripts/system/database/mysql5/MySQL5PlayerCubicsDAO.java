package system.database.mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerCubicsDAO;
import com.aionemu.gameserver.model.cubics.PlayerMCEntry;
import com.aionemu.gameserver.model.cubics.PlayerMCList;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the database access layer for managing player cubics in a {@code mysql5} environment.<br>
 * It extends {@link PlayerCubicsDAO} to handle specific SQL queries for saving and loading cubic data.
 * @author Phantom_KNA
 */
public class MySQL5PlayerCubicsDAO extends PlayerCubicsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerCubicsDAO.class);
	
	public static final String INSERT_QUERY = "INSERT INTO `player_cubic` (`player_id`, `cubic_id`, `rank`, `level`, `stat_value`, `category`) VALUES (?,?,?,?,?,?)";
	public static final String INSERT_OR_UPDATE = "INSERT INTO `player_cubic` (`player_id`, `cubic_id`, `rank`, `level`, `stat_value`,`category`) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `cubic_id` = VALUES(`cubic_id`), `rank` = VALUES(`rank`), `level` = VALUES(`level`), `stat_value` = VALUES(`stat_value`), `category` = VALUES(`category`)";
	public static final String SELECT_QUERY = "SELECT `cubic_id`,`rank`,`level`,`stat_value`,`category` FROM `player_cubic` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_cubic` WHERE `player_id`=? AND `cubic_id`=?";
	
	/**
	 * Loads the cubic data for a specific player from the database.<br>
	 * This method retrieves all entries associated with the {@code player}.<br>
	 * It returns a new {@link PlayerMCList} containing the loaded data.
	 * @param player The {@link Player} object whose data needs to be loaded.
	 * @return A {@link PlayerMCList} containing the player's cubic information.
	 */
	@Override
	public PlayerMCList load(Player player)
	{
		final List<PlayerMCEntry> mc = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int cubic_id = rset.getInt("cubic_id");
				final int rank = rset.getInt("rank");
				final int level = rset.getInt("level");
				final int stat_value = rset.getInt("stat_value");
				final int category = rset.getInt("category");
				mc.add(new PlayerMCEntry(cubic_id, rank, level, stat_value, category, PersistentState.UPDATED));
			}
			
			rset.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("Could not restore PlayerCubic for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new PlayerMCList(mc);
	}
	
	/**
	 * Saves a player's cubic data to the database.<br>
	 * This method inserts new records or updates existing ones.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if a database error occurs.
	 * @param objectId The unique identifier for the player.
	 * @param cubic_id The unique identifier for the cubic item.
	 * @param rank The current rank of the cubic.
	 * @param level The current level of the cubic.
	 * @param stat_value The specific statistic value assigned to the cubic.
	 * @param category The category classification of the cubic.
	 * @return {@code true} if the data was stored successfully, otherwise {@code false}.
	 */
	@Override
	public boolean store(int objectId, int cubic_id, int rank, int level, int stat_value, int category)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_OR_UPDATE);
			stmt.setInt(1, objectId);
			stmt.setInt(2, cubic_id);
			stmt.setInt(3, rank);
			stmt.setInt(4, level);
			stmt.setInt(5, stat_value);
			stmt.setInt(6, category);
			stmt.execute();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("Could not store PlayerCubic for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes a specific cubic item from the database for a player.<br>
	 * This method uses the {@code DELETE_QUERY} to find the record.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if a {@code SQLException} occurs.
	 * @param playerObjId The unique identifier of the player.
	 * @param cubic_id The unique identifier of the cubic item to remove.
	 * @return A boolean indicating whether the deletion was successful.
	 */
	@Override
	public boolean delete(int playerObjId, int cubic_id)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerObjId);
			stmt.setInt(2, cubic_id);
			stmt.execute();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("Could not delete PlayerCubic for player " + playerObjId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Retrieves the rank of a specific cubic for a player.<br>
	 * This method queries the database using the provided IDs.<br>
	 * It returns 0 if an error occurs during the database operation.
	 * @param playerObjId The unique identifier of the player.
	 * @param cubic_id The unique identifier of the cubic.
	 * @return The rank value as an {@code int}.
	 */
	@Override
	public int getRankById(int playerObjId, int cubic_id)
	{
		Connection con = null;
		int rank = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `rank` FROM `player_cubic` WHERE `player_id`=? AND `cubic_id`=?");
			s.setInt(1, playerObjId);
			s.setInt(2, cubic_id);
			final ResultSet rs = s.executeQuery();
			rs.next();
			rank = rs.getInt("rank");
			rs.close();
			s.close();
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return rank;
	}
	
	/**
	 * Retrieves the current level of a specific cubic for a player.<br>
	 * This method queries the database using the provided identifiers.<br>
	 * It returns 0 if an error occurs or no record is found.
	 * @param playerObjId The unique identifier for the player.
	 * @param cubic_id The unique identifier for the cubic item.
	 * @return The level of the cubic as an {@code int}.
	 */
	@Override
	public int getLevelById(int playerObjId, int cubic_id)
	{
		Connection con = null;
		int level = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `level` FROM `player_cubic` WHERE `player_id`=? AND `cubic_id`=?");
			s.setInt(1, playerObjId);
			s.setInt(2, cubic_id);
			final ResultSet rs = s.executeQuery();
			rs.next();
			level = rs.getInt("level");
			rs.close();
			s.close();
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return level;
	}
	
	/**
	 * Retrieves the {@code stat_value} for a specific player and cubic.<br>
	 * This method queries the database using the provided IDs.<br>
	 * It returns 0 if an error occurs during the database operation.
	 * @param playerObjId The unique identifier of the player.
	 * @param cubic_id The unique identifier of the cubic.
	 * @return The integer value of the stat or 0 if not found.
	 */
	@Override
	public int getStatValueById(int playerObjId, int cubic_id)
	{
		Connection con = null;
		int stat_value = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `stat_value` FROM `player_cubic` WHERE `player_id`=? AND `cubic_id`=?");
			s.setInt(1, playerObjId);
			s.setInt(2, cubic_id);
			final ResultSet rs = s.executeQuery();
			rs.next();
			stat_value = rs.getInt("stat_value");
			rs.close();
			s.close();
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return stat_value;
	}
	
	/**
	 * This method counts the number of items in a specific category for a player.<br>
	 * It queries the {@code player_cubic} table using the provided IDs.
	 * @param playerObjId The unique identifier for the player.
	 * @param category The specific category ID to filter by.
	 * @return The total count of items found, or 0 if an error occurs.
	 */
	@Override
	public int getCategoryById(int playerObjId, int category)
	{
		Connection con = null;
		int categorys = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT count(category) FROM `player_cubic` WHERE `player_id`=? AND `category`=?");
			s.setInt(1, playerObjId);
			s.setInt(2, category);
			final ResultSet rs = s.executeQuery();
			rs.next();
			categorys = rs.getInt("count(category)");
			rs.close();
			s.close();
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return categorys;
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
