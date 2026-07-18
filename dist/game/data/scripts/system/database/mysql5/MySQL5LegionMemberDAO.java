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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.model.team.legion.LegionRank;
import com.aionemu.gameserver.services.LegionService;

/**
 * This class provides the database access object for managing {@link LegionMember} data.<br>
 * It implements specific SQL queries to interact with the {@code mysql5} database schema.<br>
 * It extends {@link LegionMemberDAO} to handle all legion member related operations.
 * @author Simple
 */
public class MySQL5LegionMemberDAO extends LegionMemberDAO
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5LegionMemberDAO.class);
	/**
	 * LegionMember Queries
	 */
	private static final String INSERT_LEGIONMEMBER_QUERY = "INSERT INTO legion_members(`legion_id`, `player_id`, `rank`) VALUES (?, ?, ?)";
	private static final String UPDATE_LEGIONMEMBER_QUERY = "UPDATE legion_members SET nickname=?, rank=?, selfintro=?, challenge_score=? WHERE player_id=?";
	private static final String SELECT_LEGIONMEMBER_QUERY = "SELECT * FROM legion_members WHERE player_id = ?";
	private static final String DELETE_LEGIONMEMBER_QUERY = "DELETE FROM legion_members WHERE player_id = ?";
	private static final String SELECT_LEGIONMEMBERS_QUERY = "SELECT player_id FROM legion_members WHERE legion_id = ?";
	/**
	 * LegionMemberEx Queries *
	 */
	private static final String SELECT_LEGIONMEMBEREX_QUERY = "SELECT players.name, players.exp, players.player_class, players.last_online, players.world_id, legion_members.* FROM players, legion_members WHERE id = ? AND players.id=legion_members.player_id";
	private static final String SELECT_LEGIONMEMBEREX2_QUERY = "SELECT players.id, players.exp, players.player_class, players.last_online, players.world_id, legion_members.* FROM players, legion_members WHERE name = ? AND players.id=legion_members.player_id";
	
	/**
	 * Checks if a specific player ID is already assigned to a legion member.<br>
	 * This method queries the database to see if any record exists for the given ID.
	 * @param playerObjId The unique identifier of the player to check.
	 * @return {@code true} if the ID is currently in use, or {@code false} otherwise.
	 */
	@Override
	public boolean isIdUsed(int playerObjId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT count(player_id) as cnt FROM legion_members WHERE ? = legion_members.player_id");
		try
		{
			s.setInt(1, playerObjId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e)
		{
			log.error("Can't check if name " + playerObjId + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Saves a new member into the database.<br>
	 * This method uses {@code INSERT_LEGIONMEMBER_QUERY} to store the data.
	 * @param legionMember The {@link LegionMember} object containing the data to save.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean saveNewLegionMember(LegionMember legionMember)
	{
		final boolean success = DB.insertUpdate(INSERT_LEGIONMEMBER_QUERY, preparedStatement ->
		{
			preparedStatement.setInt(1, legionMember.getLegion().getLegionId());
			preparedStatement.setInt(2, legionMember.getObjectId());
			preparedStatement.setString(3, legionMember.getRank().toString());
			preparedStatement.execute();
		});
		
		return success;
	}
	
	/**
	 * Saves the details of a {@link LegionMember} to the database.<br>
	 * This method updates the record for a specific player.
	 * @param playerId The unique ID of the player.
	 * @param legionMember The {@code LegionMember} object containing the data to store.
	 */
	@Override
	public void storeLegionMember(int playerId, LegionMember legionMember)
	{
		DB.insertUpdate(UPDATE_LEGIONMEMBER_QUERY, stmt ->
		{
			stmt.setString(1, legionMember.getNickname());
			stmt.setString(2, legionMember.getRank().toString());
			stmt.setString(3, legionMember.getSelfIntro());
			stmt.setInt(4, legionMember.getChallengeScore());
			stmt.setInt(5, playerId);
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves a {@link LegionMember} from the database using a player ID.<br>
	 * This method returns {@code null} if the ID is {@code 0} or if the player is not in a legion.
	 * @param playerObjId The unique identifier of the player to load.
	 * @return The loaded {@link LegionMember} object, or {@code null} if no member was found.
	 */
	@Override
	public LegionMember loadLegionMember(int playerObjId)
	{
		if (playerObjId == 0)
		{
			return null;
		}
		
		final LegionMember legionMember = new LegionMember(playerObjId);
		
		final boolean success = DB.select(SELECT_LEGIONMEMBER_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerObjId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					resultSet.next();
					final int legionId = resultSet.getInt("legion_id");
					legionMember.setRank(LegionRank.valueOf(resultSet.getString("rank")));
					legionMember.setNickname(resultSet.getString("nickname"));
					legionMember.setSelfIntro(resultSet.getString("selfintro"));
					legionMember.setChallengeScore(resultSet.getInt("challenge_score"));
					legionMember.setLegion(LegionService.getInstance().getLegion(legionId));
				}
				catch (SQLException sqlE)
				{
					log.debug("[DAO: MySQL5LegionMemberDAO] Player is not in a Legion");
				}
			}
		});
		
		if (success && (legionMember.getLegion() != null))
		{
			return legionMember;
		}
		
		return null;
	}
	
	/**
	 * Loads a {@link LegionMemberEx} object from the database using a player ID.<br>
	 * This method retrieves detailed information including player stats and legion details.<br>
	 * It returns {@code null} if the player is not found or is not in a legion.
	 * @param playerObjId The unique identifier of the player to load.
	 * @return A populated {@link LegionMemberEx} object or {@code null}.
	 */
	@Override
	public LegionMemberEx loadLegionMemberEx(int playerObjId)
	{
		final LegionMemberEx legionMemberEx = new LegionMemberEx(playerObjId);
		
		final boolean success = DB.select(SELECT_LEGIONMEMBEREX_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerObjId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					resultSet.next();
					legionMemberEx.setName(resultSet.getString("players.name"));
					legionMemberEx.setExp(resultSet.getLong("players.exp"));
					legionMemberEx.setPlayerClass(PlayerClass.valueOf(resultSet.getString("players.player_class")));
					legionMemberEx.setLastOnline(resultSet.getTimestamp("players.last_online"));
					legionMemberEx.setWorldId(resultSet.getInt("players.world_id"));
					
					final int legionId = resultSet.getInt("legion_members.legion_id");
					legionMemberEx.setRank(LegionRank.valueOf(resultSet.getString("legion_members.rank")));
					legionMemberEx.setNickname(resultSet.getString("legion_members.nickname"));
					legionMemberEx.setSelfIntro(resultSet.getString("legion_members.selfintro"));
					
					legionMemberEx.setLegion(LegionService.getInstance().getLegion(legionId));
				}
				catch (SQLException sqlE)
				{
					log.debug("[DAO: MySQL5LegionMemberDAO] Player is not in a Legion");
				}
			}
		});
		
		if (success && (legionMemberEx.getLegion() != null))
		{
			return legionMemberEx;
		}
		
		return null;
	}
	
	/**
	 * Loads a {@link LegionMemberEx} object using the player's name.<br>
	 * This method queries the database to retrieve member details.<br>
	 * It returns {@code null} if the player is not found or not in a legion.
	 * @param playerName The unique name of the player to load.
	 * @return The populated {@link LegionMemberEx} object or {@code null}.
	 */
	@Override
	public LegionMemberEx loadLegionMemberEx(String playerName)
	{
		final LegionMemberEx legionMember = new LegionMemberEx(playerName);
		
		final boolean success = DB.select(SELECT_LEGIONMEMBEREX2_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setString(1, playerName);
			}
			
			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					resultSet.next();
					legionMember.setObjectId(resultSet.getInt("id"));
					legionMember.setExp(resultSet.getLong("exp"));
					legionMember.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
					legionMember.setLastOnline(resultSet.getTimestamp("last_online"));
					legionMember.setWorldId(resultSet.getInt("world_id"));
					
					final int legionId = resultSet.getInt("legion_id");
					legionMember.setRank(LegionRank.valueOf(resultSet.getString("rank")));
					legionMember.setNickname(resultSet.getString("nickname"));
					legionMember.setSelfIntro(resultSet.getString("selfintro"));
					
					legionMember.setLegion(LegionService.getInstance().getLegion(legionId));
				}
				catch (SQLException sqlE)
				{
					log.debug("[DAO: MySQL5LegionMemberDAO] Player is not in a Legion");
				}
			}
		});
		
		if (success && (legionMember.getLegion() != null))
		{
			return legionMember;
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of player IDs belonging to a specific legion.<br>
	 * This method queries the database using the provided {@code legionId}.<br>
	 * It returns an {@code ArrayList<Integer>} containing all member IDs.<br>
	 * If no members are found or the query fails, it returns {@code null}.
	 * @param legionId The unique identifier of the legion to search.
	 * @return An {@code ArrayList<Integer>} of player IDs, or {@code null} if empty.
	 */
	@Override
	public ArrayList<Integer> loadLegionMembers(int legionId)
	{
		final ArrayList<Integer> legionMembers = new ArrayList<>();
		
		final boolean success = DB.select(SELECT_LEGIONMEMBERS_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet)
			{
				try
				{
					while (resultSet.next())
					{
						final int playerObjId = resultSet.getInt("player_id");
						legionMembers.add(playerObjId);
					}
				}
				catch (SQLException sqlE)
				{
					log.error("[DAO: MySQL5LegionMemberDAO] No players in Legion. DELETE Legion Id: " + legionId);
				}
			}
		});
		
		if (success && (legionMembers.size() > 0))
		{
			return legionMembers;
		}
		
		return null;
	}
	
	/**
	 * Checks if the current database system supports a specific feature.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param s The name of the feature to check.
	 * @param i The first integer parameter for the feature.
	 * @param i1 The second integer parameter for the feature.
	 * @return {@code true} if the feature is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
	
	/**
	 * Removes a member from the legion database.<br>
	 * This method uses the {@code playerObjId} to identify which record to delete.<br>
	 * It executes a SQL delete query via the {@link DB} class.
	 * @param playerObjId The unique identifier of the player to remove.
	 */
	@Override
	public void deleteLegionMember(int playerObjId)
	{
		final PreparedStatement statement = DB.prepareStatement(DELETE_LEGIONMEMBER_QUERY);
		try
		{
			statement.setInt(1, playerObjId);
		}
		catch (SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Retrieves all unique identifiers from the {@code players} table.<br>
	 * This method queries the database to collect every {@code id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the player IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		// TODO: Auto-generated method stub
		return null;
	}
}
