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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.model.team.legion.LegionHistoryType;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.model.team.legion.LegionTerritory;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;

/**
 * This class provides the database access object for handling {@link Legion} data using a {@code mysql5} backend.<br>
 * It extends {@link LegionDAO} to implement specific SQL queries for legion-related operations.
 * @author Simple
 * @modified cura
 */
public class MySQL5LegionDAO extends LegionDAO
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5LegionDAO.class);
	/**
	 * Legion Queries
	 */
	private static final String INSERT_LEGION_QUERY = "INSERT INTO legions(id, `name`) VALUES (?, ?)";
	private static final String SELECT_LEGION_QUERY1 = "SELECT * FROM legions WHERE id=?";
	private static final String SELECT_LEGION_QUERY2 = "SELECT * FROM legions WHERE name=?";
	private static final String DELETE_LEGION_QUERY = "DELETE FROM legions WHERE id = ?";
	private static final String UPDATE_LEGION_QUERY = "UPDATE legions SET name=?, level=?, contribution_points=?, deputy_permission=?, centurion_permission=?, legionary_permission=?, volunteer_permission=?, disband_time=?, description=?, joinType=?, minJoinLevel=?, territory=? WHERE id=?";
	/**
	 * Legion Description Query *
	 */
	private static final String UPDATE_LEGION_DESCRIPTION_QUERY = "UPDATE legions SET description=?, joinType=?, minJoinLevel=? WHERE id=?";
	/**
	 * Announcement Queries *
	 */
	private static final String INSERT_ANNOUNCEMENT_QUERY = "INSERT INTO legion_announcement_list(`legion_id`, `announcement`, `date`) VALUES (?, ?, ?)";
	private static final String SELECT_ANNOUNCEMENTLIST_QUERY = "SELECT * FROM legion_announcement_list WHERE legion_id=? ORDER BY date ASC LIMIT 0,7;";
	private static final String DELETE_ANNOUNCEMENT_QUERY = "DELETE FROM legion_announcement_list WHERE legion_id = ? AND date = ?";
	/**
	 * Emblem Queries *
	 */
	private static final String INSERT_EMBLEM_QUERY = "INSERT INTO legion_emblems(legion_id, emblem_id, color_r, color_g, color_b, emblem_type, emblem_data) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_EMBLEM_QUERY = "UPDATE legion_emblems SET emblem_id=?, color_r=?, color_g=?, color_b=?, emblem_type=?, emblem_data=? WHERE legion_id=?";
	private static final String SELECT_EMBLEM_QUERY = "SELECT * FROM legion_emblems WHERE legion_id=?";
	/**
	 * Storage Queries *
	 */
	private static final String SELECT_STORAGE_QUERY = "SELECT `item_unique_id`, `item_id`, `item_count`, `item_color`, `color_expires`, `item_creator`, `expire_time`, `activation_count`, `is_equiped`, `slot`, `enchant`, `item_skin`, `fusioned_item`, `optional_socket`, `optional_fusion_socket`, `charge`, `rnd_bonus`, `rnd_count`, `pack_count`, `authorize`, `is_packed`, `is_amplified`, `buff_skill`, `reduction_level`, `luna_reskin`, `isEnhance`, `enhanceSkillId`, `enhanceSkillEnchant`, `is_seal`, `skin_skill` FROM `inventory` WHERE `item_owner`=? AND `item_location`=? AND `is_equiped`=?";
	/**
	 * History Queries *
	 */
	private static final String INSERT_HISTORY_QUERY = "INSERT INTO legion_history(`legion_id`, `date`, `history_type`, `name`, `tab_id`, `description`) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_HISTORY_QUERY = "SELECT * FROM `legion_history` WHERE legion_id=? ORDER BY date ASC;";
	private static final String CLEAR_LEGION_SIEGE = "UPDATE siege_locations SET legion_id=0 WHERE legion_id=?";
	/**
	 * Announcement Queries *
	 */
	private static final String INSERT_RECRUIT_LIST_QUERY = "INSERT INTO legion_join_requests(`legionId`, `playerId`, `playerName`, `playerClassId`, `playerRaceId`, `playerLevel`, `playerGenderId`, `joinRequestMsg`, `date`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_RECRUIT_LIST_QUERY = "SELECT * FROM legion_join_requests WHERE legionId=? ORDER BY date ASC;";
	private static final String DELETE_RECRUIT_LIST_QUERY = "DELETE FROM legion_join_requests WHERE legionId = ? AND playerId = ?";
	
	/**
	 * Checks if a specific legion name already exists in the database.<br>
	 * This method returns {@code true} if the name is taken or if an error occurs.
	 * @param name The name to check for uniqueness.
	 * @return {@code true} if the name is already used, {@code false} otherwise.
	 */
	@Override
	public boolean isNameUsed(String name)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM legions WHERE ? = legions.name");
		try
		{
			s.setString(1, name);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e)
		{
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Retrieves a list of IDs for all legions that own at least one territory.<br>
	 * This method queries the database for records where the territory count is greater than {@code 0}.
	 * @return A {@code Collection<Integer>} containing the unique IDs of these legions.
	 */
	@Override
	public Collection<Integer> getLegionIdswithTerritories()
	{
		final Collection<Integer> legionIds = new ArrayList<>();
		final PreparedStatement s = DB.prepareStatement("SELECT id FROM legions WHERE territory > 0");
		try
		{
			final ResultSet rs = s.executeQuery();
			while (rs.next())
			{
				legionIds.add(rs.getInt("id"));
			}
		}
		catch (SQLException e)
		{
			log.error("Error on getting legions with territoryId... Error: ", e);
		}
		finally
		{
			DB.close(s);
		}
		
		return legionIds;
	}
	
	/**
	 * Saves a new {@link Legion} object into the database.<br>
	 * This method uses the {@code INSERT_LEGION_QUERY} to store the legion data.
	 * @param legion The {@code Legion} object containing the data to save.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean saveNewLegion(Legion legion)
	{
		final boolean success = DB.insertUpdate(INSERT_LEGION_QUERY, preparedStatement ->
		{
			log.debug("[DAO: MySQL5LegionDAO] saving new legion: " + legion.getLegionId() + " " + legion.getLegionName());
			
			preparedStatement.setInt(1, legion.getLegionId());
			preparedStatement.setString(2, legion.getLegionName());
			preparedStatement.execute();
		});
		
		return success;
	}
	
	/**
	 * Saves the provided {@link Legion} data into the database.<br>
	 * This method updates all legion attributes and handles join requests.
	 * @param legion The {@code Legion} object to be stored.
	 */
	@Override
	public void storeLegion(Legion legion)
	{
		DB.insertUpdate(UPDATE_LEGION_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5LegionDAO] storing player " + legion.getLegionId() + " " + legion.getLegionName());
			
			stmt.setString(1, legion.getLegionName());
			stmt.setInt(2, legion.getLegionLevel());
			stmt.setLong(3, legion.getContributionPoints());
			stmt.setInt(4, legion.getDeputyPermission());
			stmt.setInt(5, legion.getCenturionPermission());
			stmt.setInt(6, legion.getLegionaryPermission());
			stmt.setInt(7, legion.getVolunteerPermission());
			stmt.setInt(8, legion.getDisbandTime());
			stmt.setString(9, legion.getLegionDiscription());
			stmt.setInt(10, legion.getLegionJoinType());
			stmt.setInt(11, legion.getMinLevel());
			stmt.setInt(12, ((legion.getTerritory() != null) && (legion.getTerritory().getId() > 0)) ? legion.getTerritory().getId() : 0);
			stmt.setInt(13, legion.getLegionId());
			
			if (!legion.getJoinRequestMap().isEmpty())
			{
				for (LegionJoinRequest ljr : legion.getJoinRequestMap().values())
				{
					storeLegionJoinRequest(ljr);
				}
			}
			
			stmt.execute();
		});
	}
	
	/**
	 * Updates the description and requirements for a specific {@link Legion}.<br>
	 * This method saves the new description, join type, and minimum level to the database.
	 * @param legion The {@code Legion} object containing the updated information.
	 */
	@Override
	public void updateLegionDescription(Legion legion)
	{
		DB.insertUpdate(UPDATE_LEGION_DESCRIPTION_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5LegionDAO] Updating Legion Description " + legion.getLegionId() + " " + legion.getLegionName() + " Description : " + legion.getLegionDiscription());
			
			stmt.setString(1, legion.getLegionDiscription());
			stmt.setInt(2, legion.getLegionJoinType());
			stmt.setInt(3, legion.getMinLevel());
			stmt.setInt(4, legion.getLegionId());
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves a {@link Legion} object from the database using its name.<br>
	 * This method searches for a record matching the provided {@code legionName}.<br>
	 * It populates all legion data including permissions and territories.
	 * @param legionName The unique name of the legion to load.
	 * @return The loaded {@link Legion} object, or {@code null} if no match is found.
	 */
	@Override
	public Legion loadLegion(String legionName)
	{
		final Legion legion = new Legion();
		
		final boolean success = DB.select(SELECT_LEGION_QUERY2, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setString(1, legionName);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					legion.setLegionName(legionName);
					legion.setLegionId(resultSet.getInt("id"));
					legion.setLegionLevel(resultSet.getInt("level"));
					legion.addContributionPoints(resultSet.getLong("contribution_points"));
					
					legion.setLegionPermissions(resultSet.getShort("deputy_permission"), resultSet.getShort("centurion_permission"), resultSet.getShort("legionary_permission"), resultSet.getShort("volunteer_permission"));
					legion.setDescription(resultSet.getString("description"));
					legion.setJoinType(resultSet.getInt("joinType"));
					legion.setMinJoinLevel(resultSet.getInt("minJoinLevel"));
					
					final int terrId = resultSet.getInt("territory");
					final LegionTerritory t = new LegionTerritory(terrId);
					if (terrId > 0)
					{
						t.setLegionId(legion.getLegionId());
						t.setLegionName(legion.getLegionName());
					}
					
					legion.setTerritory(t);
					
					for (LegionJoinRequest ljr : loadLegionJoinRequests(legion.getLegionId()))
					{
						legion.addJoinRequest(ljr);
					}
					
					legion.setDisbandTime(resultSet.getInt("disband_time"));
				}
			}
		});
		
		log.debug("[MySQL5LegionDAO] Loaded " + legion.getLegionId() + " legion.");
		
		return (success && (legion.getLegionId() != 0)) ? legion : null;
	}
	
	/**
	 * Retrieves a {@link Legion} object from the database using its unique identifier.<br>
	 * This method populates all legion data including permissions, territory, and join requests.<br>
	 * It returns {@code null} if the legion is not found or has an empty name.
	 * @param legionId The unique ID of the legion to load.
	 * @return The loaded {@link Legion} object, or {@code null} if no valid data exists.
	 */
	@Override
	public Legion loadLegion(int legionId)
	{
		final Legion legion = new Legion();
		
		final boolean success = DB.select(SELECT_LEGION_QUERY1, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					legion.setLegionId(legionId);
					legion.setLegionName(resultSet.getString("name"));
					legion.setLegionLevel(resultSet.getInt("level"));
					legion.addContributionPoints(resultSet.getLong("contribution_points"));
					
					legion.setLegionPermissions(resultSet.getShort("deputy_permission"), resultSet.getShort("centurion_permission"), resultSet.getShort("legionary_permission"), resultSet.getShort("volunteer_permission"));
					legion.setDescription(resultSet.getString("description"));
					legion.setJoinType(resultSet.getInt("joinType"));
					legion.setMinJoinLevel(resultSet.getInt("minJoinLevel"));
					
					final int terrId = resultSet.getInt("territory");
					final LegionTerritory t = new LegionTerritory(terrId);
					if (terrId > 0)
					{
						t.setLegionId(legion.getLegionId());
						t.setLegionName(legion.getLegionName());
					}
					
					legion.setTerritory(t);
					
					for (LegionJoinRequest ljr : loadLegionJoinRequests(legionId))
					{
						legion.addJoinRequest(ljr);
					}
					
					legion.setDisbandTime(resultSet.getInt("disband_time"));
				}
			}
		});
		
		log.debug("[MySQL5LegionDAO] Loaded " + legion.getLegionId() + " legion.");
		
		return (success && (legion.getLegionName() != "")) ? legion : null;
	}
	
	/**
	 * Removes a legion from the database.<br>
	 * This method deletes the legion record and clears its siege data.
	 * @param legionId The unique identifier of the legion to delete.
	 */
	@Override
	public void deleteLegion(int legionId)
	{
		PreparedStatement statement = DB.prepareStatement(DELETE_LEGION_QUERY);
		try
		{
			statement.setInt(1, legionId);
		}
		catch (SQLException e)
		{
			log.error("deleteLegion #1", e);
		}
		
		DB.executeUpdateAndClose(statement);
		
		statement = DB.prepareStatement(CLEAR_LEGION_SIEGE);
		try
		{
			statement.setInt(1, legionId);
		}
		catch (SQLException e)
		{
			log.error("deleteLegion #2", e);
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Retrieves all unique identifiers from the {@code legions} table.<br>
	 * This method queries the database to collect every {@code id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the legion IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT id FROM legions", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		try
		{
			final ResultSet rs = statement.executeQuery();
			rs.last();
			final int count = rs.getRow();
			rs.beforeFirst();
			final int[] ids = new int[count];
			for (int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("id");
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from legions table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
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
	 * Retrieves the list of announcements for a specific legion.<br>
	 * The results are sorted by their timestamp.
	 * @param legionId The unique identifier of the legion to query.
	 * @return A {@code TreeMap} containing timestamps and messages, or {@code null} if the query fails.
	 */
	@Override
	public TreeMap<Timestamp, String> loadAnnouncementList(int legionId)
	{
		final TreeMap<Timestamp, String> announcementList = new TreeMap<>();
		
		final boolean success = DB.select(SELECT_ANNOUNCEMENTLIST_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					final String message = resultSet.getString("announcement");
					final Timestamp date = resultSet.getTimestamp("date");
					
					announcementList.put(date, message);
				}
			}
		});
		
		log.debug("[MySQL5LegionDAO] Loaded announcementList " + legionId + " legion.");
		
		return success ? announcementList : null;
	}
	
	/**
	 * Saves a new announcement for a specific legion.<br>
	 * This method inserts the message into the database with a timestamp.
	 * @param legionId The unique identifier of the legion.
	 * @param currentTime The time when the announcement was created.
	 * @param message The text content of the announcement.
	 * @return {@code true} if the save operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean saveNewAnnouncement(int legionId, Timestamp currentTime, String message)
	{
		final boolean success = DB.insertUpdate(INSERT_ANNOUNCEMENT_QUERY, preparedStatement ->
		{
			log.debug("[DAO: MySQL5LegionDAO] saving new announcement.");
			
			preparedStatement.setInt(1, legionId);
			preparedStatement.setString(2, message);
			preparedStatement.setTimestamp(3, currentTime);
			preparedStatement.execute();
		});
		
		return success;
	}
	
	/**
	 * Removes a specific announcement from the database.<br>
	 * This method deletes an entry based on the provided legion and time.
	 * @param legionId The unique identifier for the {@code Legion}.
	 * @param unixTime The {@code Timestamp} of the announcement to remove.
	 */
	@Override
	public void removeAnnouncement(int legionId, Timestamp unixTime)
	{
		final PreparedStatement statement = DB.prepareStatement(DELETE_ANNOUNCEMENT_QUERY);
		try
		{
			statement.setInt(1, legionId);
			statement.setTimestamp(2, unixTime);
		}
		catch (SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Saves the emblem for a specific legion to the database.<br>
	 * This method validates the {@code LegionEmblem} before saving.<br>
	 * It decides whether to create or update the record based on its current state.
	 * @param legionId The unique identifier of the legion.
	 * @param legionEmblem The emblem object to be stored.
	 */
	@Override
	public void storeLegionEmblem(int legionId, LegionEmblem legionEmblem)
	{
		if (!validEmblem(legionEmblem))
		{
			return;
		}
		
		if (!(checkEmblem(legionId)))
		{
			createLegionEmblem(legionId, legionEmblem);
		}
		else
		{
			switch (legionEmblem.getPersistentState())
			{
				case UPDATE_REQUIRED:
					updateLegionEmblem(legionId, legionEmblem);
					break;
				case NEW:
					createLegionEmblem(legionId, legionEmblem);
					break;
				default:
					break;
			}
		}
		
		legionEmblem.setPersistentState(PersistentState.UPDATED);
	}
	
	/**
	 * Checks if the provided {@code LegionEmblem} is valid.<br>
	 * It ensures that a {@code CUSTOM} type emblem contains data.<br>
	 * Returns {@code false} if the emblem is invalid.
	 * @param legionEmblem The {@code LegionEmblem} to validate.
	 * @return {@code true} if the emblem is valid, otherwise {@code false}.
	 */
	private boolean validEmblem(LegionEmblem legionEmblem)
	{
		return (legionEmblem.getEmblemType().toString().equals("CUSTOM") && (legionEmblem.getCustomEmblemData() == null)) ? false : true;
	}
	
	/**
	 * Checks if a specific legion has an emblem assigned.<br>
	 * This method queries the database for the provided {@code legionid}.<br>
	 * It returns {@code true} if an emblem exists and {@code false} otherwise.
	 * @param legionid The unique identifier of the legion to check.
	 * @return {@code true} if the emblem is found, {@code false} if not found or an error occurs.
	 */
	public boolean checkEmblem(int legionid)
	{
		final PreparedStatement st = DB.prepareStatement(SELECT_EMBLEM_QUERY);
		try
		{
			st.setInt(1, legionid);
			
			final ResultSet rs = st.executeQuery();
			
			if (rs.next())
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			log.error("Can't check " + legionid + " legion emblem: ", e);
		}
		finally
		{
			DB.close(st);
		}
		
		return false;
	}
	
	/**
	 * Creates a new emblem for a specific legion in the database.<br>
	 * This method inserts the emblem data into the records.
	 * @param legionId The unique identifier of the {@link Legion}.
	 * @param legionEmblem The {@link LegionEmblem} object containing the emblem details.
	 */
	private void createLegionEmblem(int legionId, LegionEmblem legionEmblem)
	{
		DB.insertUpdate(INSERT_EMBLEM_QUERY, preparedStatement ->
		{
			preparedStatement.setInt(1, legionId);
			preparedStatement.setInt(2, legionEmblem.getEmblemId());
			preparedStatement.setInt(3, legionEmblem.getColor_r());
			preparedStatement.setInt(4, legionEmblem.getColor_g());
			preparedStatement.setInt(5, legionEmblem.getColor_b());
			preparedStatement.setString(6, legionEmblem.getEmblemType().toString());
			preparedStatement.setBytes(7, legionEmblem.getCustomEmblemData());
			preparedStatement.execute();
		});
	}
	
	/**
	 * Updates the emblem data for a specific legion in the database.<br>
	 * This method saves the color and type details of the {@code LegionEmblem}.<br>
	 * It uses the provided {@code legionId} to identify which record to modify.
	 * @param legionId The unique identifier of the legion.
	 * @param legionEmblem The emblem object containing the new data to save.
	 */
	private void updateLegionEmblem(int legionId, LegionEmblem legionEmblem)
	{
		DB.insertUpdate(UPDATE_EMBLEM_QUERY, stmt ->
		{
			stmt.setInt(1, legionEmblem.getEmblemId());
			stmt.setInt(2, legionEmblem.getColor_r());
			stmt.setInt(3, legionEmblem.getColor_g());
			stmt.setInt(4, legionEmblem.getColor_b());
			stmt.setString(5, legionEmblem.getEmblemType().toString());
			stmt.setBytes(6, legionEmblem.getCustomEmblemData());
			stmt.setInt(7, legionId);
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves the emblem data for a specific legion from the database.<br>
	 * This method uses the {@code SELECT_EMBLEM_QUERY} to fetch details.<br>
	 * The returned {@link LegionEmblem} object is marked with a persistent state of {@code UPDATED}.
	 * @param legionId The unique identifier of the legion.
	 * @return The {@code LegionEmblem} associated with the provided ID.
	 */
	@Override
	public LegionEmblem loadLegionEmblem(int legionId)
	{
		final LegionEmblem legionEmblem = new LegionEmblem();
		
		DB.select(SELECT_EMBLEM_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					legionEmblem.setEmblem(resultSet.getInt("emblem_id"), resultSet.getInt("color_r"), resultSet.getInt("color_g"), resultSet.getInt("color_b"), LegionEmblemType.valueOf(resultSet.getString("emblem_type")), resultSet.getBytes("emblem_data"));
				}
			}
		});
		legionEmblem.setPersistentState(PersistentState.UPDATED);
		
		return legionEmblem;
	}
	
	/**
	 * Loads the warehouse items for a specific legion from the database.<br>
	 * This method retrieves all items associated with the {@code StorageType.LEGION_WAREHOUSE}.<br>
	 * It populates a new {@link LegionWarehouse} object with the retrieved data.
	 * @param legion The {@link Legion} object used to identify which warehouse to load.
	 * @return A populated {@link LegionWarehouse} containing all items for the given legion.
	 */
	@Override
	public LegionWarehouse loadLegionStorage(Legion legion)
	{
		final LegionWarehouse inventory = new LegionWarehouse(legion);
		final int legionId = legion.getLegionId();
		final int storage = StorageType.LEGION_WAREHOUSE.getId();
		final int equipped = 0;
		
		DB.select(SELECT_STORAGE_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
				stmt.setInt(2, storage);
				stmt.setInt(3, equipped);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int itemUniqueId = rset.getInt("item_unique_id");
					final int itemId = rset.getInt("item_id");
					final long itemCount = rset.getLong("item_count");
					final int itemColor = rset.getInt("item_color");
					final int colorExpireTime = rset.getInt("color_expires");
					final String itemCreator = rset.getString("item_creator");
					final int expireTime = rset.getInt("expire_time");
					final int activationCount = rset.getInt("activation_count");
					final int isEquiped = rset.getInt("is_equiped");
					final int slot = rset.getInt("slot");
					final int enchant = rset.getInt("enchant");
					final int itemSkin = rset.getInt("item_skin");
					final int fusionedItem = rset.getInt("fusioned_item");
					final int optionalSocket = rset.getInt("optional_socket");
					final int optionalFusionSocket = rset.getInt("optional_fusion_socket");
					final int charge = rset.getInt("charge");
					final int randomBonus = rset.getInt("rnd_bonus");
					final int rndCount = rset.getInt("rnd_count");
					final int packCount = rset.getInt("pack_count");
					final int max_authorize = rset.getInt("authorize");
					final int isPacked = rset.getInt("is_packed");
					final int isAmplified = rset.getInt("is_amplified");
					final int buffSkill = rset.getInt("buff_skill");
					final int reductionLevel = rset.getInt("reduction_level");
					final int isLunaReskin = rset.getInt("luna_reskin");
					final boolean isEnhance = rset.getBoolean("isEnhance");
					final int enhanceSkillId = rset.getInt("enhanceSkillId");
					final int enhanceSkillEnchant = rset.getInt("enhanceSkillEnchant");
					final int unSeal = rset.getInt("is_seal");
					final int skinSkill = rset.getInt("skin_skill");
					final int grindSocket = rset.getInt("grind_socket");
					final int grindColor = rset.getInt("grind_color");
					final boolean contaminated = rset.getBoolean("contaminated");
					final Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, colorExpireTime, itemCreator, expireTime, activationCount, isEquiped == 1, false, slot, storage, enchant, itemSkin, fusionedItem, optionalSocket, optionalFusionSocket, charge, randomBonus, rndCount, packCount, max_authorize, isPacked == 1, isAmplified == 1, buffSkill, reductionLevel, isLunaReskin == 1, isEnhance, enhanceSkillId, enhanceSkillEnchant, unSeal, skinSkill, grindSocket, grindColor, 0, 0, contaminated);
					item.setPersistentState(PersistentState.UPDATED);
					inventory.onLoadHandler(item);
				}
			}
		});
		
		return inventory;
	}
	
	/**
	 * Loads the history records from the database for a specific legion.<br>
	 * This method populates the {@code LegionHistory} collection inside the provided {@link Legion} object.
	 * @param legion The {@code Legion} object to populate with history data.
	 */
	@Override
	public void loadLegionHistory(Legion legion)
	{
		final Collection<LegionHistory> history = legion.getLegionHistory();
		
		DB.select(SELECT_HISTORY_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legion.getLegionId());
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					history.add(new LegionHistory(LegionHistoryType.valueOf(resultSet.getString("history_type")), resultSet.getString("name"), resultSet.getTimestamp("date"), resultSet.getInt("tab_id"), resultSet.getString("description")));
				}
			}
		});
	}
	
	/**
	 * Saves a new history entry for a specific legion.<br>
	 * This method inserts the data into the database table.
	 * @param legionId The unique identifier of the legion.
	 * @param legionHistory The {@code LegionHistory} object containing the data to save.
	 * @return {@code true} if the operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean saveNewLegionHistory(int legionId, LegionHistory legionHistory)
	{
		final boolean success = DB.insertUpdate(INSERT_HISTORY_QUERY, preparedStatement ->
		{
			preparedStatement.setInt(1, legionId);
			preparedStatement.setTimestamp(2, legionHistory.getTime());
			preparedStatement.setString(3, legionHistory.getLegionHistoryType().toString());
			preparedStatement.setString(4, legionHistory.getName());
			preparedStatement.setInt(5, legionHistory.getTabId());
			preparedStatement.setString(6, legionHistory.getDescription());
			preparedStatement.execute();
		});
		
		return success;
	}
	
	/**
	 * Saves a new recruitment request into the database.<br>
	 * This method uses {@code DB.insertUpdate} to store player details and messages.<br>
	 * It logs the operation for debugging purposes.
	 * @param legionJoinRequest The {@link LegionJoinRequest} object containing the join data.
	 */
	@Override
	public void storeLegionJoinRequest(LegionJoinRequest legionJoinRequest)
	{
		DB.insertUpdate(INSERT_RECRUIT_LIST_QUERY, stmt ->
		{
			log.debug("[DAO: MySQL5LegionDAO] storing Recrutit Request " + legionJoinRequest.getLegionId() + " for Player: " + legionJoinRequest.getPlayerName());
			
			stmt.setInt(1, legionJoinRequest.getLegionId());
			stmt.setInt(2, legionJoinRequest.getPlayerId());
			stmt.setString(3, legionJoinRequest.getPlayerName());
			stmt.setInt(4, legionJoinRequest.getPlayerClass());
			stmt.setInt(5, legionJoinRequest.getRace());
			stmt.setInt(6, legionJoinRequest.getLevel());
			stmt.setInt(7, legionJoinRequest.getGenderId());
			stmt.setString(8, legionJoinRequest.getMsg());
			stmt.setTimestamp(9, legionJoinRequest.getDate());
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves all join requests for a specific legion.<br>
	 * This method fetches data from the database using the provided {@code legionId}.<br>
	 * It populates a list of {@link LegionJoinRequest} objects with player details.
	 * @param legionId The unique identifier of the legion to query.
	 * @return A {@code List} containing all join requests for the specified legion.
	 */
	@Override
	public List<LegionJoinRequest> loadLegionJoinRequests(int legionId)
	{
		final List<LegionJoinRequest> requestList = new ArrayList<>();
		
		DB.select(SELECT_RECRUIT_LIST_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, legionId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					final LegionJoinRequest ljr = new LegionJoinRequest();
					ljr.setLegionId(resultSet.getInt("legionId"));
					ljr.setPlayerId(resultSet.getInt("playerId"));
					ljr.setPlayerName(resultSet.getString("playerName"));
					ljr.setPlayerClass(resultSet.getInt("playerClassId"));
					ljr.setRace(resultSet.getInt("playerRaceId"));
					ljr.setLevel(resultSet.getInt("playerLevel"));
					ljr.setGenderId(resultSet.getInt("playerGenderId"));
					ljr.setDate(resultSet.getTimestamp("date"));
					// ljr.setPersistentState(PersistentState.UPDATED);
					requestList.add(ljr);
				}
			}
		});
		
		return requestList;
	}
	
	/**
	 * Removes a specific join request from a legion.<br>
	 * This method deletes the record for a player trying to join a legion.
	 * @param legionId The unique identifier of the {@code Legion}.
	 * @param playerId The unique identifier of the player.
	 */
	@Override
	public void deleteLegionJoinRequest(int legionId, int playerId)
	{
		final PreparedStatement statement = DB.prepareStatement(DELETE_RECRUIT_LIST_QUERY);
		try
		{
			statement.setInt(1, legionId);
			statement.setInt(2, playerId);
		}
		catch (SQLException e)
		{
			log.error("deleteLegionJoinRequest #1", e);
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Removes a specific legion join request from the database.<br>
	 * This method uses the unique identifiers provided in the {@code LegionJoinRequest}.
	 * @param ljr The {@code LegionJoinRequest} object containing the IDs to delete.
	 */
	@Override
	public void deleteLegionJoinRequest(LegionJoinRequest ljr)
	{
		deleteLegionJoinRequest(ljr.getLegionId(), ljr.getPlayerId());
	}
}
