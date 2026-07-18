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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerCollectionDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollection;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionEntry;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionInfos;
import com.aionemu.gameserver.model.templates.collection.CollectionType;

/**
 * This class provides the {@code MySQL5} database implementation for handling player collection data.<br>
 * It extends {@link PlayerCollectionDAO} to perform specific CRUD operations on {@link PlayerCollection} records.
 */
public class MySQL5PlayerCollectionDAO extends PlayerCollectionDAO
{
	private final Logger log = LoggerFactory.getLogger(MySQL5PlayerCollectionDAO.class);
	
	public static final String INSERT_QUERY = "INSERT INTO player_collection_infos (player_id, type, level, exp) VALUES (?, ?, ?, ?)";
	public static final String LOAD_QUERY = "SELECT * FROM `player_collection_infos` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_collection_infos set level=?, exp=? WHERE `player_id`=? AND `type`=?";
	
	public static final String LOAD_COLLECTION_QUERY = "SELECT * FROM `player_collections` WHERE `player_id`=?";
	public static final String INSERT_COLLECTION = "INSERT INTO player_collections (player_id, collection_id) VALUES (?, ?)";
	public static final String UPDATE_COLLECTION = "UPDATE player_collections set complete=?, step=?, item1=?, item2=?, item3=?, item4=?, item5=?, item6=?, item7=?, item8=?, item9=?, item10=?, item11=?, item12=?, item13=?, item14=? WHERE `player_id`=? AND `collection_id`=?";
	
	/**
	 * Retrieves all collection information for a specific player from the database.<br>
	 * This method maps each {@link CollectionType} to its corresponding {@link PlayerCollectionInfos}.
	 * @param player The {@code Player} object whose collections need to be loaded.
	 * @return A {@code Map} containing the collection types and their associated information.
	 */
	@Override
	public Map<CollectionType, PlayerCollectionInfos> loadPlayerCollection(Player player)
	{
		@SuppressWarnings("unused")
		final PlayerCollection collection = new PlayerCollection();
		final Map<CollectionType, PlayerCollectionInfos> infos = new HashMap<>();
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final PlayerCollectionInfos info = new PlayerCollectionInfos(CollectionType.valueOf(rset.getString("type")), rset.getInt("level"), rset.getInt("exp"));
					infos.put(info.getType(), info);
				}
			}
		});
		
		return infos;
	}
	
	/**
	 * Saves a new player collection record to the database.<br>
	 * This method uses {@code INSERT_QUERY} to store the information.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if a {@code SQLException} occurs.
	 * @param player The {@link Player} object containing the unique ID.
	 * @param infos The {@link PlayerCollectionInfos} data to be saved.
	 * @return A boolean indicating whether the insertion was successful.
	 */
	@Override
	public boolean insertPlayerCollection(Player player, PlayerCollectionInfos infos)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setString(2, infos.getType().toString());
			stmt.setInt(3, infos.getLevel());
			stmt.setInt(4, infos.getExp());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("add collection error", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the collection data for a specific player in the database.<br>
	 * This method saves the level and experience values from {@code PlayerCollectionInfos}.
	 * @param player The {@link Player} whose collection is being updated.
	 * @param infos The {@link PlayerCollectionInfos} containing the new data to save.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updatePlayerCollection(Player player, PlayerCollectionInfos infos)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, infos.getLevel());
			stmt.setInt(2, infos.getExp());
			stmt.setInt(3, player.getObjectId());
			stmt.setString(4, infos.getType().toString());
			
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update Player collection data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Loads the collection entries for a specific player from the database.<br>
	 * This method populates the {@link Player} object with its collection data.<br>
	 * It distinguishes between completed and incomplete entries during the process.
	 * @param player The {@code Player} whose collection data needs to be loaded.
	 */
	@Override
	public void loadCollectionEntry(Player player)
	{
		@SuppressWarnings("unused")
		final Map<Integer, PlayerCollection> collection = new HashMap<>();
		DB.select(LOAD_COLLECTION_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final boolean complete = rset.getBoolean("complete");
					
					final PlayerCollectionEntry entry = new PlayerCollectionEntry(rset.getInt("collection_id"), rset.getBoolean("item1"), rset.getBoolean("item2"), rset.getBoolean("item3"), rset.getBoolean("item4"), rset.getBoolean("item5"), rset.getBoolean("item6"), rset.getBoolean("item7"), rset.getBoolean("item8"), rset.getBoolean("item9"), rset.getBoolean("item10"), rset.getBoolean("item11"), rset.getBoolean("item12"), rset.getBoolean("item13"), rset.getBoolean("item14"), rset.getInt("step"));
					entry.setComplete(complete);
					if (complete)
					{
						player.getPlayerCollection().getCompleteCollection().add(entry);
					}
					else
					{
						player.getPlayerCollection().getPlayerCollectionEntry().put(entry.getId(), entry);
					}
				}
			}
		});
	}
	
	/**
	 * Saves a new collection entry into the database for a specific player.<br>
	 * This method uses the {@code INSERT_COLLECTION} query to store the data.<br>
	 * It returns {@code true} if the operation succeeds and {@code false} otherwise.
	 * @param player The {@link Player} object who owns the collection.
	 * @param entry The {@link PlayerCollectionEntry} object containing the new data.
	 * @return {@code true} if the insertion was successful, or {@code false} if a database error occurred.
	 */
	@Override
	public boolean insertCollection(Player player, PlayerCollectionEntry entry)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_COLLECTION);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, entry.getId());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("add collection error", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the progress of a specific collection entry for a player in the database.<br>
	 * This method saves the completion status and item requirements.
	 * @param player The {@link Player} whose collection is being updated.
	 * @param entry The {@link PlayerCollectionEntry} containing the new data to save.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateCollection(Player player, PlayerCollectionEntry entry)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_COLLECTION);
			stmt.setBoolean(1, entry.isComplete());
			stmt.setInt(2, entry.getStep());
			stmt.setBoolean(3, entry.isItem1());
			stmt.setBoolean(4, entry.isItem2());
			stmt.setBoolean(5, entry.isItem3());
			stmt.setBoolean(6, entry.isItem4());
			stmt.setBoolean(7, entry.isItem5());
			stmt.setBoolean(8, entry.isItem6());
			stmt.setBoolean(9, entry.isItem7());
			stmt.setBoolean(10, entry.isItem8());
			stmt.setBoolean(11, entry.isItem9());
			stmt.setBoolean(12, entry.isItem10());
			stmt.setBoolean(13, entry.isItem11());
			stmt.setBoolean(14, entry.isItem12());
			stmt.setBoolean(15, entry.isItem13());
			stmt.setBoolean(16, entry.isItem14());
			stmt.setInt(17, player.getObjectId());
			stmt.setInt(18, entry.getId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update player collection entry data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
