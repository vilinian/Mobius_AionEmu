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
import java.sql.Types;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class provides the database access layer for managing player quest lists using {@code MySQL5}.<br>
 * It handles the persistence of {@link QuestState} data for {@link Player} objects.<br>
 * It extends {@link PlayerQuestListDAO} to implement specific SQL queries.
 * @author MrPoke
 * @modified vlog, Rolandas
 */
public class MySQL5PlayerQuestListDAO extends PlayerQuestListDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerQuestListDAO.class);
	public static final String SELECT_QUERY = "SELECT `quest_id`, `status`, `quest_vars`, `complete_count`, `next_repeat_time`, `reward`, `complete_time` FROM `player_quests` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE `player_quests` SET `status`=?, `quest_vars`=?, `complete_count`=?, `next_repeat_time`=?, `reward`=?, `complete_time`=? WHERE `player_id`=? AND `quest_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_quests` WHERE `player_id`=? AND `quest_id`=?";
	public static final String INSERT_QUERY = "INSERT INTO `player_quests` (`player_id`, `quest_id`, `status`, `quest_vars`, `complete_count`, `next_repeat_time`, `reward`, `complete_time`) VALUES (?,?,?,?,?,?,?,?)";
	private static final Predicate<QuestState> questsToAddPredicate = (QuestState input) -> (input != null) && (PersistentState.NEW == input.getPersistentState());
	private static final Predicate<QuestState> questsToUpdatePredicate = (QuestState input) -> (input != null) && (PersistentState.UPDATE_REQUIRED == input.getPersistentState());
	private static final Predicate<QuestState> questsToDeletePredicate = (QuestState input) -> (input != null) && (PersistentState.DELETED == input.getPersistentState());
	
	/**
	 * Loads the quest progress for a specific player from the database.<br>
	 * This method retrieves all quest records associated with the {@code Player}.<br>
	 * It returns a new {@link QuestStateList} containing the loaded data.
	 * @param player The {@link Player} whose quest data needs to be loaded.
	 * @return A {@link QuestStateList} populated with the player's quests.
	 */
	@Override
	public QuestStateList load(Player player)
	{
		final QuestStateList questStateList = new QuestStateList();
		
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int questId = rset.getInt("quest_id");
				final int questVars = rset.getInt("quest_vars");
				final int completeCount = rset.getInt("complete_count");
				final Timestamp nextRepeatTime = rset.getTimestamp("next_repeat_time");
				Integer reward = rset.getInt("reward");
				if (rset.wasNull())
				{
					reward = 0;
				}
				
				final Timestamp completeTime = rset.getTimestamp("complete_time");
				final QuestStatus status = QuestStatus.valueOf(rset.getString("status"));
				final QuestState questState = new QuestState(questId, status, questVars, completeCount, nextRepeatTime, reward, completeTime);
				questState.setPersistentState(PersistentState.UPDATED);
				questStateList.addQuest(questId, questState);
			}
			
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore QuestStateList data for player: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
		
		return questStateList;
	}
	
	/**
	 * Saves the quest progress of a {@link Player} to the database.<br>
	 * This method removes old data and inserts the current quest states.<br>
	 * It updates the persistent state of each {@code QuestState} to {@code UPDATED}.
	 * @param player The {@code Player} object containing the quest data to save.
	 */
	@Override
	public void store(Player player)
	{
		final Collection<QuestState> qsList = player.getQuestStateList().getAllQuestState();
		if (GenericValidator.isBlankOrNull(qsList))
		{
			return;
		}
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			
			deleteQuest(con, player.getObjectId(), qsList);
			
			addQuests(con, player.getObjectId(), qsList);
			updateQuests(con, player.getObjectId(), qsList);
		}
		catch (SQLException e)
		{
			log.error("Can't save quests for player " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (QuestState qs : qsList)
		{
			qs.setPersistentState(PersistentState.UPDATED);
		}
	}
	
	/**
	 * Saves a collection of new quest states into the database.<br>
	 * This method filters the provided {@code Collection<QuestState>} and uses a batch insert to save them for a specific player.<br>
	 * It handles null checks for timestamps and rewards before committing the transaction.
	 * @param con The active {@link Connection} used to execute the SQL queries.
	 * @param playerId The unique identifier of the player owning these quests.
	 * @param states A collection of {@link QuestState} objects to be added to the database.
	 */
	private void addQuests(Connection con, int playerId, Collection<QuestState> states)
	{
		states = states.stream().filter(questsToAddPredicate).collect(Collectors.toList());
		
		if (GenericValidator.isBlankOrNull(states))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(INSERT_QUERY);
			
			for (QuestState qs : states)
			{
				ps.setInt(1, playerId);
				ps.setInt(2, qs.getQuestId());
				ps.setString(3, qs.getStatus().toString());
				ps.setInt(4, qs.getQuestVars().getQuestVars());
				ps.setInt(5, qs.getCompleteCount());
				if (qs.getNextRepeatTime() != null)
				{
					ps.setTimestamp(6, qs.getNextRepeatTime());
				}
				else
				{
					ps.setNull(6, Types.TIMESTAMP);
				}
				
				if (qs.getReward() == null)
				{
					ps.setNull(7, Types.INTEGER);
				}
				else
				{
					ps.setInt(7, qs.getReward());
				}
				
				if (qs.getCompleteTime() == null)
				{
					ps.setNull(8, Types.TIMESTAMP);
				}
				else
				{
					ps.setTimestamp(8, qs.getCompleteTime());
				}
				
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Failed to insert new quests for player " + playerId);
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
	}
	
	/**
	 * Updates the quest records in the database for a specific player.<br>
	 * This method filters the provided {@code Collection<QuestState>} and executes a batch update.<br>
	 * It handles null checks for timestamps and rewards before committing the changes.
	 * @param con The active {@link Connection} to the database.
	 * @param playerId The unique ID of the player whose quests are being updated.
	 * @param states A collection of {@link QuestState} objects containing the new data.
	 */
	private void updateQuests(Connection con, int playerId, Collection<QuestState> states)
	{
		states = states.stream().filter(questsToUpdatePredicate).collect(Collectors.toList());
		
		if (GenericValidator.isBlankOrNull(states))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(UPDATE_QUERY);
			
			for (QuestState qs : states)
			{
				ps.setString(1, qs.getStatus().toString());
				ps.setInt(2, qs.getQuestVars().getQuestVars());
				ps.setInt(3, qs.getCompleteCount());
				if (qs.getNextRepeatTime() != null)
				{
					ps.setTimestamp(4, qs.getNextRepeatTime());
				}
				else
				{
					ps.setNull(4, Types.TIMESTAMP);
				}
				
				if (qs.getReward() == null)
				{
					ps.setNull(5, Types.SMALLINT);
				}
				else
				{
					ps.setInt(5, qs.getReward());
				}
				
				if (qs.getCompleteTime() == null)
				{
					ps.setNull(6, Types.TIMESTAMP);
				}
				else
				{
					ps.setTimestamp(6, qs.getCompleteTime());
				}
				
				ps.setInt(7, playerId);
				ps.setInt(8, qs.getQuestId());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Failed to update existing quests for player " + playerId);
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
	}
	
	/**
	 * Removes specific quest records from the database.<br>
	 * This method filters the provided collection and executes a batch delete for the specified player.
	 * @param con The active {@code Connection} to the database.
	 * @param playerId The unique ID of the player whose quests are being modified.
	 * @param states A {@code Collection} of {@link QuestState} objects to be removed.
	 */
	private void deleteQuest(Connection con, int playerId, Collection<QuestState> states)
	{
		states = states.stream().filter(questsToDeletePredicate).collect(Collectors.toList());
		
		if (GenericValidator.isBlankOrNull(states))
		{
			return;
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = con.prepareStatement(DELETE_QUERY);
			
			for (QuestState qs : states)
			{
				ps.setInt(1, playerId);
				ps.setInt(2, qs.getQuestId());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Failed to delete existing quests for player " + playerId);
		}
		finally
		{
			DatabaseFactory.close(ps);
		}
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
}
