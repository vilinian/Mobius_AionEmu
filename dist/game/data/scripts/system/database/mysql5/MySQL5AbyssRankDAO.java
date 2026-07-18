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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * Provides database access for {@link AbyssRank} data using the {@code mysql5} driver.<br>
 * This class handles SQL queries to manage and retrieve abyss rankings from the database.
 * @author ATracer, Divinity, nrg
 * @rework Phantom_KNA
 */
public class MySQL5AbyssRankDAO extends AbyssRankDAO
{
	private static String LOGIN_DATABASE = GSConfig.LOGINSERVER_NAME;
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5AbyssRankDAO.class);
	public static final String SELECT_QUERY = "SELECT daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp, last_update FROM abyss_rank WHERE player_id = ?";
	public static final String INSERT_QUERY = "INSERT INTO abyss_rank (player_id, daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp, last_update) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_QUERY = "UPDATE abyss_rank SET  daily_ap = ?, daily_gp = ?, weekly_ap = ?, weekly_gp = ?, ap = ?, gp = ?, rank = ?, top_ranking = ?, daily_kill = ?, weekly_kill = ?, all_kill = ?, max_rank = ?, last_kill = ?, last_ap = ?, last_gp = ?, last_update = ? WHERE player_id = ?";
	public static final String SELECT_PLAYERS_RANKING = "SELECT abyss_rank.rank, abyss_rank.ap, abyss_rank.gp, abyss_rank.old_rank_pos, abyss_rank.rank_pos, players.name, legions.name, players.id, players.title_id, players.player_class, players.gender, players.exp FROM abyss_rank INNER JOIN players INNER JOIN " + LOGIN_DATABASE + ".account_data ON abyss_rank.player_id = players.id AND " + LOGIN_DATABASE + ".account_data.id = players.account_id AND " + LOGIN_DATABASE + ".account_data.access_level = 0 LEFT JOIN legion_members ON legion_members.player_id = players.id LEFT JOIN legions ON legions.id = legion_members.legion_id WHERE players.race = ? AND abyss_rank.gp > 0 ORDER BY abyss_rank.gp DESC LIMIT 0, 300";
	public static final String SELECT_PLAYERS_RANKING_ACTIVE_ONLY = "SELECT abyss_rank.rank, abyss_rank.ap, abyss_rank.gp, abyss_rank.old_rank_pos, abyss_rank.rank_pos, players.name, legions.name, players.id, players.title_id, players.player_class, players.gender, players.exp FROM abyss_rank INNER JOIN players INNER JOIN " + LOGIN_DATABASE + ".account_data ON abyss_rank.player_id = players.id AND " + LOGIN_DATABASE + ".account_data.id = players.account_id AND " + LOGIN_DATABASE + ".account_data.access_level = 0 LEFT JOIN legion_members ON legion_members.player_id = players.id LEFT JOIN legions ON legions.id = legion_members.legion_id WHERE players.race = ? AND abyss_rank.gp > 0 AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY abyss_rank.gp DESC LIMIT 0, 300";
	public static final String SELECT_LEGIONS_RANKING = "SELECT legions.id, legions.name, legions.contribution_points, legions.level as lvl, legions.old_rank_pos, legions.rank_pos FROM legions,legion_members,players WHERE players.race = ? AND legion_members.rank = 'BRIGADE_GENERAL' AND legion_members.player_id = players.id AND legion_members.legion_id = legions.id AND legions.contribution_points > 0 GROUP BY id ORDER BY legions.contribution_points DESC LIMIT 0,50";
	public static final String SELECT_AP_PLAYER = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND ap > ? ORDER by ap DESC";
	public static final String SELECT_AP_PLAYER_ACTIVE_ONLY = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND ap > ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY ap DESC";
	public static final String SELECT_GP_PLAYER = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND gp > ? ORDER by gp DESC";
	public static final String SELECT_GP_PLAYER_ACTIVE_ONLY = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND gp > ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY gp DESC";
	public static final String UPDATE_RANK = "UPDATE abyss_rank SET  rank = ?, top_ranking = ? WHERE player_id = ?";
	public static final String SELECT_LEGION_COUNT = "SELECT COUNT(player_id) as players FROM legion_members WHERE legion_id = ?";
	public static final String UPDATE_PLAYER_RANK_LIST = "UPDATE abyss_rank SET abyss_rank.old_rank_pos = abyss_rank.rank_pos, abyss_rank.rank_pos = @a:=@a+1 where player_id in (SELECT id FROM players where race = ?) order by gp desc" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 500" : ""); // only
																																																																											// Three hundred positions are relevant later.
																																																																											// Update them.
																																																																											// +
																																																																											// Some extra positions can enter the top rankings.
	public static final String UPDATE_PLAYER_RANK_LIST_ACTIVE_ONLY = "UPDATE abyss_rank SET abyss_rank.old_rank_pos = abyss_rank.rank_pos, abyss_rank.rank_pos = @a:=@a+1 where player_id in (SELECT id FROM players where race = ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60) order by gp desc" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 500" : ""); // only
																																																																																																			// Three hundred positions are relevant later.
																																																																																																			// Update them.
																																																																																																			// +
																																																																																																			// Some extra positions can enter the top rankings.
	public static final String UPDATE_LEGION_RANK_LIST = "UPDATE legions SET legions.old_rank_pos = legions.rank_pos, legions.rank_pos = @a:=@a+1 where id in (SELECT legion_id FROM legion_members, players where rank = 'BRIGADE_GENERAL' AND players.id = legion_members.player_id and players.race = ?) order by legions.contribution_points DESC" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 75" : ""); // only
																																																																																																						// Fifty positions are relevant later.
																																																																																																						// Update them.
																																																																																																						// +
																																																																																																						// Some extra positions can enter the top rankings.
	public static final String SELECT_ALL_GPRANK = "SELECT player_id FROM abyss_rank WHERE rank > ?";
	public static final String UPDATE_GLORY_POINTS = "UPDATE `abyss_rank` SET `gp` = ? WHERE `player_id` = ?";
	
	/**
	 * Retrieves a list of player IDs for a specific rank.<br>
	 * This method queries the database using the provided {@code rank}.<br>
	 * It ensures that each unique {@code player_id} is only added once.
	 * @param rank The numerical rank to filter by.
	 * @return A {@code List<Integer>} containing the IDs of players at that rank.
	 */
	@Override
	public List<Integer> RankPlayers(int rank)
	{
		final List<Integer> players = new ArrayList<>();
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_ALL_GPRANK);
			stmt.setInt(1, rank);
			final ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next())
			{
				final int playerId = resultSet.getInt("player_id");
				if (!players.contains(playerId))
				{
					players.add(playerId);
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		
		return players;
	}
	
	/**
	 * Updates the glory points for a specific player in the database.<br>
	 * This method uses the {@code UPDATE_GLORY_POINTS} query to save changes.
	 * @param playerId The unique identifier of the player.
	 * @param gp The new amount of glory points to set.
	 */
	@Override
	public void updateGloryPoints(int playerId, int gp)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_GLORY_POINTS);
			stmt.setInt(1, gp);
			stmt.setInt(2, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the {@code AbyssRank} data for a specific player from the database.<br>
	 * If no record exists, it creates a new default rank object.
	 * @param playerId The unique identifier of the player to load.
	 * @return The {@code AbyssRank} object containing the player's statistics.
	 */
	@Override
	public AbyssRank loadAbyssRank(int playerId)
	{
		AbyssRank abyssRank = null;
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			
			stmt.setInt(1, playerId);
			
			final ResultSet resultSet = stmt.executeQuery();
			
			if (resultSet.next())
			{
				final int daily_ap = resultSet.getInt("daily_ap");
				final int daily_gp = resultSet.getInt("daily_gp");
				final int weekly_ap = resultSet.getInt("weekly_ap");
				final int weekly_gp = resultSet.getInt("weekly_gp");
				final int ap = resultSet.getInt("ap");
				final int gp = resultSet.getInt("gp");
				final int rank = resultSet.getInt("rank");
				final int top_ranking = resultSet.getInt("top_ranking");
				final int daily_kill = resultSet.getInt("daily_kill");
				final int weekly_kill = resultSet.getInt("weekly_kill");
				final int all_kill = resultSet.getInt("all_kill");
				final int max_rank = resultSet.getInt("max_rank");
				final int last_kill = resultSet.getInt("last_kill");
				final int last_ap = resultSet.getInt("last_ap");
				final int last_gp = resultSet.getInt("last_gp");
				final long last_update = resultSet.getLong("last_update");
				
				abyssRank = new AbyssRank(daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp, last_update);
				abyssRank.setPersistentState(PersistentState.UPDATED);
			}
			else
			{
				abyssRank = new AbyssRank(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, System.currentTimeMillis());
				abyssRank.setPersistentState(PersistentState.NEW);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("loadAbyssRank", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return abyssRank;
	}
	
	/**
	 * Loads the {@code AbyssRank} data for a specific player.<br>
	 * This method retrieves the rank from the database and assigns it to the {@link Player}.
	 * @param player The {@link Player} object whose rank needs to be loaded.
	 */
	@Override
	public void loadAbyssRank(Player player)
	{
		final AbyssRank rank = loadAbyssRank(player.getObjectId());
		player.setAbyssRank(rank);
	}
	
	/**
	 * Saves the {@link AbyssRank} data for a specific player to the database.<br>
	 * It checks the persistent state of the rank to decide whether to add or update it.<br>
	 * The method updates the rank state to {@code UPDATED} after processing.
	 * @param player The {@link Player} object containing the rank information to save.
	 * @return {@code true} if the database operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean storeAbyssRank(Player player)
	{
		final AbyssRank rank = player.getAbyssRank();
		boolean result = false;
		switch (rank.getPersistentState())
		{
			case NEW:
				result = addRank(player.getObjectId(), rank);
				break;
			case UPDATE_REQUIRED:
				result = updateRank(player.getObjectId(), rank);
				break;
			default:
				break;
		}
		
		rank.setPersistentState(PersistentState.UPDATED);
		return result;
	}
	
	/**
	 * Saves a new {@link AbyssRank} record to the database for a specific object ID.<br>
	 * This method uses the {@code INSERT_QUERY} to store all rank statistics.
	 * @param objectId The unique identifier of the player or object.
	 * @param rank The {@link AbyssRank} data to be saved.
	 * @return {@code true} if the operation succeeded, or {@code false} if a database error occurred.
	 */
	private boolean addRank(int objectId, AbyssRank rank)
	{
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			
			stmt.setInt(1, objectId);
			stmt.setInt(2, rank.getDailyAP());
			stmt.setInt(3, rank.getDailyGP());
			stmt.setInt(4, rank.getWeeklyAP());
			stmt.setInt(5, rank.getWeeklyGP());
			stmt.setInt(6, rank.getAp());
			stmt.setInt(7, rank.getGp());
			stmt.setInt(8, rank.getRank().getId());
			stmt.setInt(9, rank.getTopRanking());
			stmt.setInt(10, rank.getDailyKill());
			stmt.setInt(11, rank.getWeeklyKill());
			stmt.setInt(12, rank.getAllKill());
			stmt.setInt(13, rank.getMaxRank());
			stmt.setInt(14, rank.getLastKill());
			stmt.setInt(15, rank.getLastAP());
			stmt.setInt(16, rank.getLastGP());
			stmt.setLong(17, rank.getLastUpdate());
			stmt.execute();
			stmt.close();
			
			return true;
		}
		catch (SQLException e)
		{
			log.error("addRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the ranking data for a specific player in the database.<br>
	 * This method saves all statistics from an {@link AbyssRank} object.
	 * @param objectId The unique identifier of the player to update.
	 * @param rank The {@link AbyssRank} object containing the new data.
	 * @return {@code true} if the update was successful, or {@code false} if a database error occurred.
	 */
	private boolean updateRank(int objectId, AbyssRank rank)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			
			stmt.setInt(1, rank.getDailyAP());
			stmt.setInt(2, rank.getDailyGP());
			stmt.setInt(3, rank.getWeeklyAP());
			stmt.setInt(4, rank.getWeeklyGP());
			stmt.setInt(5, rank.getAp());
			stmt.setInt(6, rank.getGp());
			stmt.setInt(7, rank.getRank().getId());
			stmt.setInt(8, rank.getTopRanking());
			stmt.setInt(9, rank.getDailyKill());
			stmt.setInt(10, rank.getWeeklyKill());
			stmt.setInt(11, rank.getAllKill());
			stmt.setInt(12, rank.getMaxRank());
			stmt.setInt(13, rank.getLastKill());
			stmt.setInt(14, rank.getLastAP());
			stmt.setInt(15, rank.getLastGP());
			stmt.setLong(16, rank.getLastUpdate());
			stmt.setInt(17, objectId);
			stmt.execute();
			stmt.close();
			
			return true;
		}
		catch (SQLException e)
		{
			log.error("updateRank", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves a list of players ranked in the Abyss for a specific race.<br>
	 * This method filters results based on how many days a player has been offline.<br>
	 * If {@code maxOfflineDays} is 0, it returns all rankings regardless of activity.
	 * @param race The {@link Race} type to filter by.
	 * @param maxOfflineDays The maximum number of days a player can be offline to be included in the list.
	 * @return An {@code ArrayList} containing {@link AbyssRankingResult} objects for the matching players.
	 */
	@Override
	public ArrayList<AbyssRankingResult> getAbyssRankingPlayers(Race race, int maxOfflineDays)
	{
		Connection con = null;
		final ArrayList<AbyssRankingResult> results = new ArrayList<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(maxOfflineDays > 0 ? SELECT_PLAYERS_RANKING_ACTIVE_ONLY : SELECT_PLAYERS_RANKING);
			
			stmt.setString(1, race.toString());
			
			if (maxOfflineDays > 0)
			{
				stmt.setInt(2, maxOfflineDays);
			}
			
			final ResultSet resultSet = stmt.executeQuery();
			
			while (resultSet.next())
			{
				final String name = resultSet.getString("players.name");
				final int playerAbyssRank = resultSet.getInt("abyss_rank.rank");
				final int ap = resultSet.getInt("abyss_rank.ap");
				final int gp = resultSet.getInt("abyss_rank.gp");
				final int playerTitle = resultSet.getInt("players.title_id");
				final int playerId = resultSet.getInt("players.id");
				final String playerClassStr = resultSet.getString("players.player_class");
				final String playerGenderStr = resultSet.getString("players.gender");
				final int playerLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getLevelForExp(resultSet.getLong("players.exp"));
				final String playerLegion = resultSet.getString("legions.name");
				final int oldRankPos = resultSet.getInt("old_rank_pos");
				final int rankPos = resultSet.getInt("rank_pos");
				final PlayerClass playerClass = PlayerClass.getPlayerClassByString(playerClassStr);
				final Gender playerGender = Gender.getGenderByString(playerGenderStr);
				if (playerClass == null)
				{
					continue;
				}
				
				final AbyssRankingResult rsl = new AbyssRankingResult(name, playerAbyssRank, playerId, ap, gp, playerTitle, playerClass, playerGender, playerLevel, playerLegion, oldRankPos, rankPos);
				results.add(rsl);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("getAbyssRankingPlayers", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return results;
	}
	
	/**
	 * Retrieves the ranking of legions for a specific race.<br>
	 * This method fetches data from the database and populates an {@code ArrayList}.<br>
	 * It includes details such as contribution points, legion level, and member counts.
	 * @param race The {@link Race} type to filter the rankings by.
	 * @return An {@code ArrayList} containing {@link AbyssRankingResult} objects for all matching legions.
	 */
	@Override
	public ArrayList<AbyssRankingResult> getAbyssRankingLegions(Race race)
	{
		final ArrayList<AbyssRankingResult> results = new ArrayList<>();
		DB.select(SELECT_LEGIONS_RANKING, new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet arg0) throws SQLException
			{
				while (arg0.next())
				{
					final String name = arg0.getString("legions.name");
					final long cp = arg0.getLong("legions.contribution_points");
					final int legionId = arg0.getInt("legions.id");
					final int legionLevel = arg0.getInt("lvl");
					final int legionMembers = getLegionMembersCount(legionId);
					final int oldRankPos = arg0.getInt("old_rank_pos");
					final int rankPos = arg0.getInt("rank_pos");
					final AbyssRankingResult rsl = new AbyssRankingResult(cp, name, legionId, legionLevel, legionMembers, oldRankPos, rankPos);
					results.add(rsl);
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException
			{
				arg0.setString(1, race.toString());
			}
		});
		
		return results;
	}
	
	/**
	 * Calculates the total number of members in a specific legion.<br>
	 * This method queries the database to sum up all players belonging to the given {@code legionId}.
	 * @param legionId The unique identifier for the legion.
	 * @return The total count of members as an {@code int}.
	 */
	private int getLegionMembersCount(int legionId)
	{
		final int[] result = new int[1];
		DB.select(SELECT_LEGION_COUNT, new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet arg0) throws SQLException
			{
				while (arg0.next())
				{
					result[0] += arg0.getInt("players");
				}
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException
			{
				arg0.setInt(1, legionId);
			}
		});
		
		return result[0];
	}
	
	/**
	 * Loads the AP values for players of a specific race.<br>
	 * This method filters players based on an AP limit and offline status.
	 * @param race The {@link Race} type to filter by.
	 * @param lowerApLimit The minimum AP value required to include a player.
	 * @param maxOfflineDays The maximum number of days a player can be offline. Use 0 for all players.
	 * @return A {@code Map<Integer, Integer>} where the key is the player ID and the value is their AP.
	 */
	@Override
	public Map<Integer, Integer> loadPlayersAp(Race race, int lowerApLimit, int maxOfflineDays)
	{
		final Map<Integer, Integer> results = new HashMap<>();
		DB.select(maxOfflineDays > 0 ? SELECT_AP_PLAYER_ACTIVE_ONLY : SELECT_AP_PLAYER, new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					final int playerId = rs.getInt("player_id");
					final int ap = rs.getInt("ap");
					final int gp = rs.getInt("gp");
					results.put(playerId, ap);
					results.put(playerId, gp);
				}
			}
			
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setString(1, race.toString());
				ps.setInt(2, lowerApLimit);
				
				if (maxOfflineDays > 0)
				{
					ps.setInt(3, maxOfflineDays);
				}
			}
		});
		
		return results;
	}
	
	/**
	 * Loads the Glory Points for players of a specific race.<br>
	 * It filters players based on a minimum GP limit and maximum offline days.
	 * @param race The {@code Race} type to filter by.
	 * @param lowerGpLimit The minimum amount of GP required to be included.
	 * @param maxOfflineDays The maximum number of days a player can be offline. Use 0 for no limit.
	 * @return A {@code Map<Integer, Integer>} where the key is the player ID and the value is their GP.
	 */
	@Override
	public Map<Integer, Integer> loadPlayersGp(Race race, int lowerGpLimit, int maxOfflineDays)
	{
		final Map<Integer, Integer> results = new HashMap<>();
		DB.select(maxOfflineDays > 0 ? SELECT_GP_PLAYER_ACTIVE_ONLY : SELECT_GP_PLAYER, new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					final int playerId = rs.getInt("player_id");
					final int gp = rs.getInt("gp");
					final int ap = rs.getInt("ap");
					results.put(playerId, ap);
					results.put(playerId, gp);
				}
			}
			
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setString(1, race.toString());
				ps.setInt(2, lowerGpLimit);
				
				if (maxOfflineDays > 0)
				{
					ps.setInt(3, maxOfflineDays);
				}
			}
		});
		
		return results;
	}
	
	/**
	 * Updates the abyss rank for a specific player in the database.<br>
	 * This method uses the {@code AbyssRankEnum} to set the new rank and quota.
	 * @param playerId The unique identifier of the player.
	 * @param rankEnum The enum containing the new rank details.
	 */
	@Override
	public void updateAbyssRank(int playerId, AbyssRankEnum rankEnum)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_RANK);
			
			stmt.setInt(1, rankEnum.getId());
			stmt.setInt(2, rankEnum.getQuota());
			stmt.setInt(3, playerId);
			
			stmt.execute();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("updateAbyssRank", e);
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
	
	/**
	 * Updates the ranking lists for both players and legions.<br>
	 * This method refreshes the data in the database.<br>
	 * It handles different logic based on whether active players are filtered.
	 * @param maxOfflineDays The number of days to exclude offline players. Use 0 to include all players.
	 */
	@Override
	public void updateRankList(int maxOfflineDays)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(maxOfflineDays > 0 ? UPDATE_PLAYER_RANK_LIST_ACTIVE_ONLY : UPDATE_PLAYER_RANK_LIST);
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ELYOS");
			if (maxOfflineDays > 0)
			{
				stmt.setInt(2, maxOfflineDays);
			}
			
			stmt.addBatch();
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ASMODIANS");
			if (maxOfflineDays > 0)
			{
				stmt.setInt(2, maxOfflineDays);
			}
			
			stmt.addBatch();
			stmt.executeBatch();
			stmt.close();
			stmt = con.prepareStatement(UPDATE_LEGION_RANK_LIST);
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ELYOS");
			stmt.addBatch();
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ASMODIANS");
			stmt.addBatch();
			stmt.executeBatch();
		}
		catch (SQLException e)
		{
			log.error("updateRank", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
}
