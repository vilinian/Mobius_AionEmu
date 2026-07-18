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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.PlayerShugoSweepDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;

/**
 * This class provides the {@code MySQL5} database implementation for handling player Shugo Sweep data.<br>
 * It extends {@link PlayerShugoSweepDAO} to perform specific SQL queries for this game system.
 * @author Ghostfur
 */
public class MySQL5PlayerShugoSweepDAO extends PlayerShugoSweepDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerLunaShopDAO.class);
	
	public static final String ADD_QUERY = "INSERT INTO `player_shugo_sweep` (`player_id`, `free_dice`, `sweep_step`, `board_id`) VALUES (?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT * FROM `player_shugo_sweep` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_shugo_sweep`";
	public static final String UPDATE_QUERY = "UPDATE player_shugo_sweep set `free_dice`=?, `sweep_step`=?, `board_id`=? WHERE `player_id`=?";
	
	/**
	 * Loads the {@link PlayerSweep} data for a specific player from the database.<br>
	 * This method retrieves sweep settings and attaches them to the provided {@code Player}.<br>
	 * If no record is found, the player will not have a sweep assigned.
	 * @param player The {@code Player} object whose sweep data needs to be loaded.
	 */
	@Override
	public void load(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				final int dice = rset.getInt("free_dice");
				final int step = rset.getInt("sweep_step");
				final int boardId = rset.getInt("board_id");
				final PlayerSweep ps = new PlayerSweep(step, dice, boardId);
				ps.setPersistentState(PersistentState.UPDATED);
				player.setPlayerShugoSweep(ps);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore PlayerSweep data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Adds a new record to the player Shugo Sweep table.<br>
	 * This method inserts data using the {@code ADD_QUERY}.<br>
	 * It returns {@code true} if the insertion is successful.
	 * @param playerId The unique identifier for the player.
	 * @param dice The number of free dice assigned to the player.
	 * @param step The current sweep step progress.
	 * @param boardId The specific ID of the game board.
	 * @return {@code true} if the record was added successfully, otherwise {@code false}.
	 */
	@Override
	public boolean add(int playerId, int dice, int step, int boardId)
	{
		return DB.insertUpdate(ADD_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setInt(2, dice);
			ps.setInt(3, step);
			ps.setInt(4, boardId);
			ps.execute();
			ps.close();
		});
	}
	
	/**
	 * Removes all records from the player luna shop table.<br>
	 * This method executes the {@code DELETE_QUERY}.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	@Override
	public boolean delete()
	{
		return DB.insertUpdate(DELETE_QUERY, ps ->
		{
			ps.execute();
			ps.close();
		});
	}
	
	/**
	 * Saves the modified items of a {@link Player} to the database.<br>
	 * This method identifies all dirty items and persists them using the player's unique IDs.
	 * @param player The {@code Player} object containing the items to be saved.
	 * @return {@code true} if the save operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean store(Player player)
	{
		Connection con = null;
		boolean insert = false;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			final PlayerSweep bind = player.getPlayerShugoSweep();
			switch (bind.getPersistentState())
			{
				case UPDATE_REQUIRED:
				case NEW:
					insert = updatePlayerSweep(con, player);
					log.info("DB updated.");
					break;
				default:
					break;
			}
			
			bind.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e)
		{
			log.error("Can't open connection to save player updateSweep: " + player.getObjectId());
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return insert;
	}
	
	/**
	 * Updates the Shugo Sweep data for a specific player in the database.<br>
	 * This method uses the {@code UPDATE_QUERY} to save current progress.<br>
	 * It commits the transaction if the batch execution succeeds.
	 * @param con The active {@link Connection} to the database.
	 * @param player The {@link Player} object containing the sweep data to update.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	private boolean updatePlayerSweep(Connection con, Player player)
	{
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(UPDATE_QUERY);
			final PlayerSweep lr = player.getPlayerShugoSweep();
			stmt.setInt(1, lr.getFreeDice());
			stmt.setInt(2, lr.getStep());
			stmt.setInt(3, lr.getBoardId());
			stmt.setInt(4, player.getObjectId());
			stmt.addBatch();
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Could not update PlayerSweep data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Updates the Shugo Sweep data for a specific object ID.<br>
	 * This method uses the {@code UPDATE_QUERY} to save new values.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the database update.
	 * @param obj The unique identifier for the object.
	 * @param freeDice The number of free dice available.
	 * @param step The current sweep step.
	 * @param boardId The ID of the board being used.
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	@Override
	public boolean setShugoSweepByObjId(int obj, int freeDice, int step, int boardId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, freeDice);
			stmt.setInt(2, step);
			stmt.setInt(3, boardId);
			stmt.setInt(4, obj);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
