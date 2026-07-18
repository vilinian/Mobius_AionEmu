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
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.PlayerRankingDAO;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfCooperationRank;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfDisciplineRank;
import com.aionemu.gameserver.model.ranking.PlayerRankingResult;

/**
 * This class provides the {@code MySQL5} database implementation for handling player rankings.<br>
 * It extends {@link PlayerRankingDAO} to perform specific queries against a {@code mysql5} backend.
 */
public class MySQL5PlayerRankingDAO extends PlayerRankingDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerRankingDAO.class);
	
	public static final String SELECT_PLAYERS_RANKING = "SELECT player_ranking.rank, player_ranking.last_rank, player_ranking.points, player_ranking.player_id, players.name, players.id, players.player_class, players.race FROM player_ranking INNER JOIN players ON player_ranking.player_id = players.id WHERE player_ranking.table_id = ? AND player_ranking.points > 0 ORDER BY player_ranking.points DESC LIMIT 0, 300";
	public static final String SELECT_MY_HISTORY = "SELECT * FROM player_ranking  WHERE player_id = ? AND table_id = ?";
	public static final String INSERT_QUERY = "INSERT INTO player_ranking (player_id, table_id, rank, last_rank, points, last_points, high_points, low_points, position_match) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_QUERY = "UPDATE player_ranking SET  rank = ?, last_rank = ?, points = ?, last_points = ?, high_points = ?, low_points = ?, position_match = ? WHERE player_id = ? AND table_id = ?";
	
	/**
	 * Retrieves a list of players ranked in a specific competition table.<br>
	 * This method fetches the top 300 players with points greater than {@code 0}.<br>
	 * The results are ordered by points in descending order.
	 * @param tableId The unique identifier for the ranking table.
	 * @return An {@code ArrayList} containing {@link PlayerRankingResult} objects.
	 */
	@Override
	public ArrayList<PlayerRankingResult> getCompetitionRankingPlayers(int tableId)
	{
		Connection con = null;
		final ArrayList<PlayerRankingResult> results = new ArrayList<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_PLAYERS_RANKING);
			stmt.setInt(1, tableId);
			final ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next())
			{
				final String name = resultSet.getString("players.name");
				final int rank = resultSet.getInt("player_ranking.rank");
				final int last_rank = resultSet.getInt("player_ranking.last_rank");
				final int pc = resultSet.getInt("player_ranking.points");
				final int playerId = resultSet.getInt("players.id");
				final String playerClassStr = resultSet.getString("players.player_class");
				final PlayerClass playerClass = PlayerClass.getPlayerClassByString(playerClassStr);
				final String race = resultSet.getString("players.race");
				if (playerClass == null)
				{
					continue;
				}
				
				final PlayerRankingResult rsl = new PlayerRankingResult(name, last_rank, rank, pc, playerClass, Race.getRaceByString(race.toString()).getRaceId(), playerId);
				results.add(rsl);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("getCompetitionRankingPlayers", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return results;
	}
	
	/**
	 * Saves the discipline ranking for a specific player.<br>
	 * This method checks if the rank is new or needs an update.<br>
	 * It then calls the appropriate internal database method to save the data.
	 * @param player The {@code Player} object containing the ranking information.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean storeDisciplineRank(Player player)
	{
		final ArenaOfDisciplineRank rank = player.getDisciplineRank();
		boolean result = false;
		switch (rank.getPersistentState())
		{
			case NEW:
				result = addDisciplineRank(player.getObjectId(), rank);
				break;
			case UPDATE_REQUIRED:
				result = updateDisciplineRank(player.getObjectId(), rank);
				break;
			default:
				break;
		}
		
		rank.setPersistentState(PersistentState.UPDATED);
		return result;
	}
	
	/**
	 * Saves the cooperation ranking for a specific player.<br>
	 * This method checks the {@code PersistentState} of the rank.<br>
	 * It calls either {@code ArenaOfCooperationRank)} or {@code ArenaOfCooperationRank)} based on that state.
	 * @param player The {@code Player} object containing the ranking data to save.
	 * @return {@code true} if the database operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean storeCooperationRank(Player player)
	{
		final ArenaOfCooperationRank rank = player.getCooperationRank();
		boolean result = false;
		switch (rank.getPersistentState())
		{
			case NEW:
				result = addCooperationRank(player.getObjectId(), rank);
				break;
			case UPDATE_REQUIRED:
				result = updateCooperationRank(player.getObjectId(), rank);
				break;
			default:
				break;
		}
		
		rank.setPersistentState(PersistentState.UPDATED);
		return result;
	}
	
	/**
	 * Adds a new discipline rank record to the database.<br>
	 * This method uses the {@code INSERT_QUERY} to save the data.<br>
	 * It specifically targets table ID 541.
	 * @param objectId The unique identifier for the player object.
	 * @param rank The {@link ArenaOfDisciplineRank} object containing the ranking details.
	 * @return {@code true} if the operation succeeded, or {@code false} if a database error occurred.
	 */
	private boolean addDisciplineRank(int objectId, ArenaOfDisciplineRank rank)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, 541);
			stmt.setInt(3, rank.getRank());
			stmt.setInt(4, rank.getBestRank());
			stmt.setInt(5, rank.getPoints());
			stmt.setInt(6, rank.getLastPoints());
			stmt.setInt(7, rank.getHighPoints());
			stmt.setInt(8, rank.getLowPoints());
			stmt.setInt(9, rank.getPossitionMatch());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("addDisciplineRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Adds a new cooperation rank for a specific object to the database.<br>
	 * This method uses the {@code INSERT_QUERY} to save the ranking data.
	 * @param objectId The unique identifier of the player or object.
	 * @param rank The {@link ArenaOfCooperationRank} data to be saved.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	private boolean addCooperationRank(int objectId, ArenaOfCooperationRank rank)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, 541);
			stmt.setInt(3, rank.getRank());
			stmt.setInt(4, rank.getBestRank());
			stmt.setInt(5, rank.getPoints());
			stmt.setInt(6, rank.getLastPoints());
			stmt.setInt(7, rank.getHighPoints());
			stmt.setInt(8, rank.getLowPoints());
			stmt.setInt(9, rank.getPossitionMatch());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("addCooperationRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the discipline ranking data for a specific player in the database.<br>
	 * This method uses the {@code UPDATE_QUERY} to save new rank statistics.
	 * @param objectId The unique identifier of the player.
	 * @param rank The {@link ArenaOfDisciplineRank} object containing the new ranking data.
	 * @return {@code true} if the update was successful, or {@code false} if a database error occurred.
	 */
	private boolean updateDisciplineRank(int objectId, ArenaOfDisciplineRank rank)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, rank.getRank());
			stmt.setInt(2, rank.getBestRank());
			stmt.setInt(3, rank.getPoints());
			stmt.setInt(4, rank.getLastPoints());
			stmt.setInt(5, rank.getHighPoints());
			stmt.setInt(6, rank.getLowPoints());
			stmt.setInt(7, rank.getPossitionMatch());
			stmt.setInt(8, objectId);
			stmt.setInt(9, 541);
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("updateDisciplineRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the cooperation ranking data for a specific player in the database.<br>
	 * This method uses the {@code UPDATE_QUERY} to save new rank details.
	 * @param objectId The unique identifier of the player.
	 * @param rank The {@link ArenaOfCooperationRank} object containing the new ranking data.
	 * @return {@code true} if the update was successful, or {@code false} if a database error occurred.
	 */
	private boolean updateCooperationRank(int objectId, ArenaOfCooperationRank rank)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, rank.getRank());
			stmt.setInt(2, rank.getBestRank());
			stmt.setInt(3, rank.getPoints());
			stmt.setInt(4, rank.getLastPoints());
			stmt.setInt(5, rank.getHighPoints());
			stmt.setInt(6, rank.getLowPoints());
			stmt.setInt(7, rank.getPossitionMatch());
			stmt.setInt(8, objectId);
			stmt.setInt(9, 541);
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("updateCooperationRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the {@link ArenaOfDisciplineRank} for a specific player from the database.<br>
	 * It uses the provided {@code playerId} and {@code tableId} to find the history record.<br>
	 * If no record exists, it returns a new rank with default values.
	 * @param playerId The unique identifier of the player.
	 * @param tableId The specific ranking table ID to query.
	 * @return The loaded {@link ArenaOfDisciplineRank} object or a new one if not found.
	 */
	@Override
	public ArenaOfDisciplineRank loadArenaOfDisciplineRank(int playerId, int tableId)
	{
		ArenaOfDisciplineRank ranking = null;
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, tableId);
			final ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next())
			{
				final int rank = resultSet.getInt("rank");
				final int best_rank = resultSet.getInt("last_rank");
				final int point = resultSet.getInt("points");
				final int last_point = resultSet.getInt("last_points");
				final int high_point = resultSet.getInt("high_points");
				final int low_point = resultSet.getInt("low_points");
				final int position_match = resultSet.getInt("position_match");
				ranking = new ArenaOfDisciplineRank(rank, best_rank, point, last_point, high_point, low_point, position_match);
				ranking.setPersistentState(PersistentState.UPDATED);
			}
			else
			{
				ranking = new ArenaOfDisciplineRank(0, 0, 0, 0, 0, 0, 0);
				ranking.setPersistentState(PersistentState.NEW);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("loadArenaOfDisciplineRank", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return ranking;
	}
	
	/**
	 * Retrieves the cooperation rank for a specific player from the database.<br>
	 * It uses the {@code playerId} and {@code tableId} to find the history record.<br>
	 * If no record exists, it returns a new rank with default values.
	 * @param playerId The unique identifier of the player.
	 * @param tableId The specific ranking table ID.
	 * @return An {@link ArenaOfCooperationRank} object containing the player's data.
	 */
	@Override
	public ArenaOfCooperationRank loadArenaOfCooperationRank(int playerId, int tableId)
	{
		ArenaOfCooperationRank ranking = null;
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_MY_HISTORY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, tableId);
			final ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next())
			{
				final int rank = resultSet.getInt("rank");
				final int best_rank = resultSet.getInt("last_rank");
				final int point = resultSet.getInt("points");
				final int last_point = resultSet.getInt("last_points");
				final int high_point = resultSet.getInt("high_points");
				final int low_point = resultSet.getInt("low_points");
				final int position_match = resultSet.getInt("position_match");
				ranking = new ArenaOfCooperationRank(rank, best_rank, point, last_point, high_point, low_point, position_match);
				ranking.setPersistentState(PersistentState.UPDATED);
			}
			else
			{
				ranking = new ArenaOfCooperationRank(0, 0, 0, 0, 0, 0, 0);
				ranking.setPersistentState(PersistentState.NEW);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("loadArenaOfCooperationRank", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return ranking;
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
		return com.aionemu.gameserver.dao.MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
