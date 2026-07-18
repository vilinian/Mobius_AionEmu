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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerBonusTimeStatus;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerUpgradeArcade;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This class provides the data access object for managing player information in a {@code mysql5} database.<br>
 * It extends {@link PlayerDAO} to handle specific SQL queries and operations related to player records.
 * @author SoulKeeper, Saelya
 * @author cura
 */
public class MySQL5PlayerDAO extends PlayerDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerDAO.class);
	private final Map<Integer, PlayerCommonData> playerCommonData = new ConcurrentHashMap<>();
	private final Map<String, PlayerCommonData> playerCommonDataByName = new ConcurrentHashMap<>();
	private MapRegion mr = null;
	
	/**
	 * Checks if a specific player name already exists in the database.<br>
	 * This method returns {@code true} if the name is taken or if an error occurs.
	 * @param name The name to check for uniqueness.
	 * @return {@code true} if the name is already used, {@code false} otherwise.
	 */
	@Override
	public boolean isNameUsed(String name)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM players WHERE ? = players.name");
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
	 * Retrieves the names of players based on a list of unique identifiers.<br>
	 * This method queries the database for each ID provided in the collection.<br>
	 * It returns an empty map if the input is {@code null} or blank.
	 * @param playerObjectIds A collection of {@code Integer} IDs to look up.
	 * @return A {@link Map} where the key is the {@code Integer} ID and the value is the player name string.
	 */
	@Override
	public Map<Integer, String> getPlayerNames(Collection<Integer> playerObjectIds)
	{
		if (GenericValidator.isBlankOrNull(playerObjectIds))
		{
			return Collections.emptyMap();
		}
		
		final Map<Integer, String> result = new HashMap<>();
		
		String sql = "SELECT id, `name` FROM players WHERE id IN(%s)";
		sql = String.format(sql, playerObjectIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
		final PreparedStatement s = DB.prepareStatement(sql);
		try
		{
			final ResultSet rs = s.executeQuery();
			while (rs.next())
			{
				final int id = rs.getInt("id");
				final String name = rs.getString("name");
				result.put(id, name);
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException("Failed to load player names", e);
		}
		finally
		{
			DB.close(s);
		}
		
		return result;
	}
	
	/**
	 * Updates the {@code account_id} for a specific player in the database.<br>
	 * This method links the provided {@link Player} object to a new {@code accountId}.<br>
	 * It uses the unique object ID from the {@code player} parameter to identify the record.
	 * @param player The {@code Player} object whose account information needs updating.
	 * @param accountId The new integer value for the {@code account_id} field.
	 */
	@Override
	public void changePlayerId(Player player, int accountId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE players SET account_id=? WHERE id=?");
			stmt.setInt(1, accountId);
			stmt.setInt(2, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error saving player: " + player.getObjectId() + " " + player.getName(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the current state of a {@link Player} to the database.<br>
	 * This method updates all character statistics and attributes in the players table.<br>
	 * It also refreshes the local cache if {@code CacheConfig.CACHE_COMMONDATA} is enabled.
	 * @param player The {@link Player} object containing the data to be stored.
	 */
	@Override
	public void storePlayer(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE players SET name=?, exp=?, recoverexp=?, x=?, y=?, z=?, heading=?, world_id=?, gender=?, race=?, player_class=?, last_online=?, cube_expands=?, advanced_stigma_slot_size=?, warehouse_size=?, note=?, title_id=?, bonus_title_id=?, dp=?, soul_sickness=?, mailbox_letters=?, reposte_energy=?, goldenstar_energy=?, silverstar_energy=?, growth_energy=?, bg_points=?, mentor_flag_time=?, initial_gamestats=?, world_owner=?, fatigue=?, fatigueRecover=?, fatigueReset=?, joinRequestLegionId=?, joinRequestState=?, frenzy_points=?, frenzy_count=?, bonus_type=?, bonus_buff_time=?, wardrobe_size=?, wardrobe_slot=?, luna_consume_count=?, muni_keys=?, luna_consume=?, toc_floor=?, minion_energy=?, last_minion=?, world_play_time=? WHERE id=?");
			
			log.debug("[DAO: MySQL5PlayerDAO] storing player " + player.getObjectId() + " " + player.getName());
			final PlayerCommonData pcd = player.getCommonData();
			stmt.setString(1, player.getName());
			stmt.setLong(2, pcd.getExp());
			stmt.setLong(3, pcd.getExpRecoverable());
			stmt.setFloat(4, player.getX());
			stmt.setFloat(5, player.getY());
			stmt.setFloat(6, player.getZ());
			stmt.setInt(7, player.getHeading());
			stmt.setInt(8, player.getWorldId());
			stmt.setString(9, player.getGender().toString());
			stmt.setString(10, player.getRace().toString());
			stmt.setString(11, pcd.getPlayerClass().toString());
			stmt.setTimestamp(12, pcd.getLastOnline());
			stmt.setInt(13, player.getCubeExpands());
			stmt.setInt(14, pcd.getAdvancedStigmaSlotSize());
			stmt.setInt(15, player.getWarehouseSize());
			stmt.setString(16, pcd.getNote());
			stmt.setInt(17, pcd.getTitleId());
			stmt.setInt(18, pcd.getBonusTitleId());
			stmt.setInt(19, pcd.getDp());
			stmt.setInt(20, pcd.getDeathCount());
			final Mailbox mailBox = player.getMailbox();
			final int mails = mailBox != null ? mailBox.size() : pcd.getMailboxLetters();
			stmt.setInt(21, mails);
			stmt.setLong(22, pcd.getCurrentReposteEnergy());
			stmt.setLong(23, pcd.getGoldenStarEnergy());
			stmt.setLong(24, pcd.getSilverStarEnergy());
			stmt.setLong(25, pcd.getGrowthEnergy());
			stmt.setInt(26, player.getCommonData().getBattleGroundPoints());
			stmt.setInt(27, pcd.getMentorFlagTime());
			stmt.setInt(28, pcd.isInitialGameStats());
			if (player.getPosition().getWorldMapInstance() == null)
			{
				log.error("Error saving player: " + player.getObjectId() + " " + player.getName() + ", world map instance is null. Setting world owner to 0. Position: " + player.getWorldId() + " " + player.getX() + " " + player.getY() + " " + player.getZ());
				stmt.setInt(29, 0);
			}
			else
			{
				stmt.setInt(29, player.getPosition().getWorldMapInstance().getOwnerId());
			}
			
			stmt.setInt(30, pcd.getFatigue());
			stmt.setInt(31, pcd.getFatigueRecover());
			stmt.setInt(32, pcd.getFatigueReset());
			stmt.setInt(33, pcd.getJoinRequestLegionId());
			stmt.setString(34, pcd.getJoinRequestState().name());
			stmt.setInt(35, player.getUpgradeArcade().getFrenzyPoints());
			stmt.setInt(36, player.getUpgradeArcade().getFrenzyCount());
			stmt.setString(37, player.getBonusTime().getStatus().toString());
			stmt.setTimestamp(38, pcd.getBonusTime().getTime());
			stmt.setInt(39, pcd.getWardrobeSize());
			stmt.setInt(40, pcd.getWardrobeSlot());
			stmt.setInt(41, pcd.getLunaConsumeCount());
			stmt.setInt(42, pcd.getMuniKeys());
			stmt.setInt(43, pcd.getLunaConsumePoint());
			stmt.setInt(44, pcd.getFloor());
			stmt.setInt(45, pcd.getMinionEnergy());
			stmt.setInt(46, pcd.getLastMinion());
			stmt.setInt(47, pcd.getWorldPlayTime());
			stmt.setInt(48, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error saving player: " + player.getObjectId() + " " + player.getName(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		if (CacheConfig.CACHE_COMMONDATA)
		{
			final PlayerCommonData cached = playerCommonData.get(player.getObjectId());
			if (cached != null)
			{
				playerCommonData.put(player.getCommonData().getPlayerObjId(), player.getCommonData());
				playerCommonDataByName.put(player.getName().toLowerCase(), player.getCommonData());
			}
		}
	}
	
	/**
	 * Saves a new player's data into the database.<br>
	 * This method inserts the character details and handles cache updates.
	 * @param pcd The {@code PlayerCommonData} object containing the player's attributes.
	 * @param accountId The unique identifier for the player's account.
	 * @param accountName The name associated with the player's account.
	 * @return {@code true} if the save was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean saveNewPlayer(PlayerCommonData pcd, int accountId, String accountName)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO players(id, `name`, account_id, account_name, x, y, z, heading, world_id, gender, race, player_class, cube_expands, warehouse_size, bonus_type, wardrobe_size, online)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)");
			
			log.debug("[DAO: MySQL5PlayerDAO] saving new player: " + pcd.getPlayerObjId() + " " + pcd.getName());
			
			preparedStatement.setInt(1, pcd.getPlayerObjId());
			preparedStatement.setString(2, pcd.getName());
			preparedStatement.setInt(3, accountId);
			preparedStatement.setString(4, accountName);
			preparedStatement.setFloat(5, pcd.getPosition().getX());
			preparedStatement.setFloat(6, pcd.getPosition().getY());
			preparedStatement.setFloat(7, pcd.getPosition().getZ());
			preparedStatement.setInt(8, pcd.getPosition().getHeading());
			preparedStatement.setInt(9, pcd.getPosition().getMapId());
			preparedStatement.setString(10, pcd.getGender().toString());
			preparedStatement.setString(11, pcd.getRace().toString());
			preparedStatement.setString(12, pcd.getPlayerClass().toString());
			preparedStatement.setInt(13, pcd.getCubeExpands());
			preparedStatement.setInt(14, pcd.getWarehouseSize());
			preparedStatement.setString(15, pcd.getBonusTime().getStatus().toString());
			preparedStatement.setInt(16, pcd.getWardrobeSize());
			preparedStatement.execute();
			preparedStatement.close();
		}
		catch (Exception e)
		{
			log.error("Error saving new player: " + pcd.getPlayerObjId() + " " + pcd.getName(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		if (CacheConfig.CACHE_COMMONDATA)
		{
			playerCommonData.put(pcd.getPlayerObjId(), pcd);
			playerCommonDataByName.put(pcd.getName().toLowerCase(), pcd);
		}
		
		return true;
	}
	
	/**
	 * Retrieves the {@code PlayerCommonData} for a player using their name.<br>
	 * This method first checks the active world and the local cache.<br>
	 * If not found, it queries the database to find the player ID.<br>
	 * Finally, it returns the data associated with that ID.
	 * @param name The unique name of the player to look up.
	 * @return The {@code PlayerCommonData} object if found, or {@code null} if no player exists with that name.
	 */
	@Override
	public PlayerCommonData loadPlayerCommonDataByName(String name)
	{
		final Player player = World.getInstance().findPlayer(name);
		if (player != null)
		{
			return player.getCommonData();
		}
		
		final PlayerCommonData pcd = playerCommonDataByName.get(name.toLowerCase());
		if (pcd != null)
		{
			return pcd;
		}
		
		int playerObjId = 0;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT id FROM players WHERE name = ?");
			stmt.setString(1, name);
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				playerObjId = rset.getInt("id");
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore playerId data for player name: " + name + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		if (playerObjId == 0)
		{
			return null;
		}
		
		return loadPlayerCommonData(playerObjId);
	}
	
	/**
	 * Retrieves the common data for a specific player.<br>
	 * This method first checks if the data exists in the cache.<br>
	 * If not found, it fetches the record from the database using the provided ID.<br>
	 * It populates a {@code PlayerCommonData} object with all relevant character stats and positions.
	 * @param playerObjId The unique identifier of the player to load.
	 * @return The populated {@code PlayerCommonData} object, or {@code null} if the data could not be loaded.
	 */
	@Override
	public PlayerCommonData loadPlayerCommonData(int playerObjId)
	{
		final PlayerCommonData cached = playerCommonData.get(playerObjId);
		if (cached != null)
		{
			log.debug("[DAO: MySQL5PlayerDAO] PlayerCommonData for id: " + playerObjId + " obtained from cache");
			return cached;
		}
		
		final PlayerCommonData cd = new PlayerCommonData(playerObjId);
		boolean success = false;
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM players WHERE id = ?");
			stmt.setInt(1, playerObjId);
			final ResultSet resultSet = stmt.executeQuery();
			log.debug("[DAO: MySQL5PlayerDAO] loading from db " + playerObjId);
			
			if (resultSet.next())
			{
				success = true;
				cd.setName(resultSet.getString("name"));
				
				// set player class before exp
				cd.setPlayerClass(PlayerClass.valueOf(resultSet.getString("player_class")));
				cd.setExp(resultSet.getLong("exp"));
				cd.setRecoverableExp(resultSet.getLong("recoverexp"));
				cd.setRace(Race.valueOf(resultSet.getString("race")));
				cd.setGender(Gender.valueOf(resultSet.getString("gender")));
				cd.setCreationDate(resultSet.getTimestamp("creation_date"));
				cd.setLastOnline(resultSet.getTimestamp("last_online"));
				cd.setNote(resultSet.getString("note"));
				cd.setCubeExpands(resultSet.getInt("cube_expands"));
				cd.setAdvancedStigmaSlotSize(resultSet.getInt("advanced_stigma_slot_size"));
				cd.setTitleId(resultSet.getInt("title_id"));
				cd.setBonusTitleId(resultSet.getInt("bonus_title_id"));
				cd.setWarehouseSize(resultSet.getInt("warehouse_size"));
				cd.setOnline(resultSet.getBoolean("online"));
				cd.setMailboxLetters(resultSet.getInt("mailbox_letters"));
				cd.setDp(resultSet.getInt("dp"));
				cd.setDeathCount(resultSet.getInt("soul_sickness"));
				cd.setCurrentReposteEnergy(resultSet.getLong("reposte_energy"));
				cd.setGoldenStarEnergy(resultSet.getLong("goldenstar_energy"));
				cd.setSilverStarEnergy(resultSet.getLong("silverstar_energy"));
				cd.setGrowthEnergy(resultSet.getLong("growth_energy"));
				cd.setBattleGroundPoints(resultSet.getInt("bg_points"));
				
				float x = resultSet.getFloat("x");
				float y = resultSet.getFloat("y");
				float z = resultSet.getFloat("z");
				byte heading = resultSet.getByte("heading");
				int worldId = resultSet.getInt("world_id");
				final PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;
				final boolean checkThis = World.getInstance().getWorldMap(worldId).isInstanceType();
				
				// This simulates a player loading error; if you have a better idea, feel free to use it.
				if (checkThis)
				{
					mr = null;
				}
				else
				{
					mr = World.getInstance().getWorldMap(worldId).getMainWorldMapInstance().getRegion(x, y, z);
				}
				
				if ((mr == null) && (playerInitialData != null))
				{
					// unstuck unlucky characters :)
					final LocationData ld = playerInitialData.getSpawnLocation(cd.getRace());
					x = ld.getX();
					y = ld.getY();
					z = ld.getZ();
					heading = ld.getHeading();
					worldId = ld.getMapId();
				}
				
				final WorldPosition position = World.getInstance().createPosition(worldId, x, y, z, heading, 0);
				cd.setPosition(position);
				cd.setWorldOwnerId(resultSet.getInt("world_owner"));
				cd.setMentorFlagTime(resultSet.getInt("mentor_flag_time"));
				cd.setInitialGameStats(resultSet.getInt("initial_gamestats"));
				cd.setLastTransferTime(resultSet.getLong("last_transfer_time"));
				cd.setFatigue(resultSet.getInt("fatigue"));
				cd.setFatigueRecover(resultSet.getInt("fatigueRecover"));
				cd.setFatigueReset(resultSet.getInt("fatigueReset"));
				cd.setJoinRequestLegionId(resultSet.getInt("joinRequestLegionId"));
				cd.setJoinRequestState(LegionJoinRequestState.valueOf(resultSet.getString("joinRequestState")));
				
				final PlayerUpgradeArcade pua = new PlayerUpgradeArcade();
				pua.setFrenzyPoints(resultSet.getInt("frenzy_points"));
				pua.setFrenzyCount(resultSet.getInt("frenzy_count"));
				cd.setBonusType(PlayerBonusTimeStatus.valueOf(resultSet.getString("bonus_type")));
				cd.setBonusTime(resultSet.getTimestamp("bonus_buff_time"));
				cd.setUpgradeArcade(pua);
				cd.setWardrobeSize(resultSet.getInt("wardrobe_size"));
				cd.setWardrobeSlot(resultSet.getInt("wardrobe_slot"));
				cd.setLunaConsumeCount(resultSet.getInt("luna_consume_count"));
				cd.setMuniKeys(resultSet.getInt("muni_keys"));
				cd.setLunaConsumePoint(resultSet.getInt("luna_consume"));
				cd.setFloor(resultSet.getInt("toc_floor"));
				cd.setMinionEnergy(resultSet.getInt("minion_energy"));
				cd.setLastMinion(resultSet.getInt("last_minion"));
				cd.setWorldPlayTime(resultSet.getInt("world_play_time"));
			}
			else
			{
				log.info("Missing PlayerCommonData from db " + playerObjId);
			}
			
			resultSet.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore PlayerCommonData data for player: " + playerObjId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		if (success)
		{
			if (CacheConfig.CACHE_COMMONDATA)
			{
				playerCommonData.put(playerObjId, cd);
				playerCommonDataByName.put(cd.getName().toLowerCase(), cd);
			}
			
			return cd;
		}
		
		return null;
	}
	
	/**
	 * Removes a player from the database.<br>
	 * This method deletes the record associated with the given {@code playerId}.<br>
	 * It also clears the player data from the cache if enabled.
	 * @param playerId The unique identifier of the player to delete.
	 */
	@Override
	public void deletePlayer(int playerId)
	{
		final PreparedStatement statement = DB.prepareStatement("DELETE FROM players WHERE id = ?");
		try
		{
			statement.setInt(1, playerId);
		}
		catch (SQLException e)
		{
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		
		if (CacheConfig.CACHE_COMMONDATA)
		{
			final PlayerCommonData pcd = playerCommonData.remove(playerId);
			if (pcd != null)
			{
				playerCommonDataByName.remove(pcd.getName().toLowerCase());
			}
		}
		
		DB.executeUpdateAndClose(statement);
	}
	
	/**
	 * Retrieves a list of player object IDs associated with a specific account.<br>
	 * This method queries the database for all records matching the provided {@code accountId}.
	 * @param accountId The unique identifier of the account to search.
	 * @return A {@code List<Integer>} containing the player IDs, or {@code null} if the query fails.
	 */
	@Override
	public List<Integer> getPlayerOidsOnAccount(int accountId)
	{
		final List<Integer> result = new ArrayList<>();
		final boolean success = DB.select("SELECT id FROM players WHERE account_id = ?", new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					result.add(resultSet.getInt("id"));
				}
			}
			
			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, accountId);
			}
		});
		
		return success ? result : null;
	}
	
	/**
	 * Updates the creation and deletion dates for a specific account.<br>
	 * This method fetches data from the database using the player object ID.<br>
	 * It populates the {@code acData} object with the retrieved timestamps.
	 * @param acData The {@link PlayerAccountData} object to update.
	 */
	@Override
	public void setCreationDeletionTime(PlayerAccountData acData)
	{
		DB.select("SELECT creation_date, deletion_date FROM players WHERE id = ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, acData.getPlayerCommonData().getPlayerObjId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				rset.next();
				
				acData.setDeletionDate(rset.getTimestamp("deletion_date"));
				acData.setCreationDate(rset.getTimestamp("creation_date"));
			}
		});
	}
	
	/**
	 * Updates the deletion date for a specific player in the database.<br>
	 * This method modifies the {@code deletion_date} column based on the provided ID.
	 * @param objectId The unique identifier of the player to update.
	 * @param deletionDate The new {@code Timestamp} to set as the deletion date.
	 */
	@Override
	public void updateDeletionTime(int objectId, Timestamp deletionDate)
	{
		DB.insertUpdate("UPDATE players set deletion_date = ? where id = ?", preparedStatement ->
		{
			preparedStatement.setTimestamp(1, deletionDate);
			preparedStatement.setInt(2, objectId);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Updates the creation date for a specific player in the database.<br>
	 * This method uses the {@code objectId} to locate the correct record.<br>
	 * It sets the new value using the provided {@code creationDate}.
	 * @param objectId The unique identifier of the player object.
	 * @param creationDate The timestamp representing when the player was created.
	 */
	@Override
	public void storeCreationTime(int objectId, Timestamp creationDate)
	{
		DB.insertUpdate("UPDATE players set creation_date = ? where id = ?", preparedStatement ->
		{
			preparedStatement.setTimestamp(1, creationDate);
			preparedStatement.setInt(2, objectId);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Updates the last online time for a specific player in the database.<br>
	 * This method uses the {@code objectId} to locate the correct record.<br>
	 * It updates the {@code last_online} column with the provided {@code Timestamp}.
	 * @param objectId The unique identifier of the player object.
	 * @param lastOnline The timestamp representing when the player was last online.
	 */
	@Override
	public void storeLastOnlineTime(int objectId, Timestamp lastOnline)
	{
		DB.insertUpdate("UPDATE players set last_online = ? where id = ?", preparedStatement ->
		{
			preparedStatement.setTimestamp(1, lastOnline);
			preparedStatement.setInt(2, objectId);
			preparedStatement.execute();
		});
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
		final PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
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
			log.error("Can't get list of id's from players table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
	}
	
	/**
	 * Updates the online status of a specific player in the database.<br>
	 * This method sets the {@code online} flag for the provided {@link Player}.
	 * @param player The {@code Player} object to update.
	 * @param online The new online status to set as {@code true} or {@code false}.
	 */
	@Override
	public void onlinePlayer(Player player, boolean online)
	{
		DB.insertUpdate("UPDATE players SET online=? WHERE id=?", stmt ->
		{
			log.debug("[DAO: MySQL5PlayerDAO] online status " + player.getObjectId() + " " + player.getName());
			
			stmt.setBoolean(1, online);
			stmt.setInt(2, player.getObjectId());
			stmt.execute();
		});
	}
	
	/**
	 * Updates the online status for all players in the database.<br>
	 * This method sets the {@code online} column to the provided value.
	 * @param online The new online status to set. Use {@code true} for online and {@code false} for offline.
	 */
	@Override
	public void setPlayersOffline(boolean online)
	{
		DB.insertUpdate("UPDATE players SET online=?", stmt ->
		{
			stmt.setBoolean(1, online);
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves the name of a player from the database.<br>
	 * This method uses the unique object ID to find the corresponding record.
	 * @param playerObjId The unique {@code int} identifier for the player.
	 * @return The {@code String} name of the player.
	 */
	@Override
	public String getPlayerNameByObjId(int playerObjId)
	{
		final String[] result = new String[1];
		DB.select("SELECT name FROM players WHERE id = ?", new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet arg0) throws SQLException
			{
				arg0.next();
				result[0] = arg0.getString("name");
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException
			{
				arg0.setInt(1, playerObjId);
			}
		});
		
		return result[0];
	}
	
	/**
	 * Retrieves the unique ID of a player based on their name.<br>
	 * This method queries the database for a matching record.
	 * @param playerName The name of the player to search for.
	 * @return The integer ID associated with the provided name.
	 */
	@Override
	public int getPlayerIdByName(String playerName)
	{
		final int[] result = new int[1];
		DB.select("SELECT id FROM players WHERE name = ?", new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet arg0) throws SQLException
			{
				arg0.next();
				result[0] = arg0.getInt("id");
			}
			
			@Override
			public void setParams(PreparedStatement arg0) throws SQLException
			{
				arg0.setString(1, playerName);
			}
		});
		
		return result[0];
	}
	
	/**
	 * Retrieves the unique account identifier for a specific character name.<br>
	 * This method queries the database to find the {@code account_id}.<br>
	 * It returns 0 if an error occurs or no record is found.
	 * @param name The character name to search for.
	 * @return The integer ID of the account, or 0 if not found.
	 */
	@Override
	public int getAccountIdByName(String name)
	{
		Connection con = null;
		int accountId = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `account_id` FROM `players` WHERE `name` = ?");
			s.setString(1, name);
			final ResultSet rs = s.executeQuery();
			rs.next();
			accountId = rs.getInt("account_id");
			rs.close();
			s.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return accountId;
	}
	
	/**
	 * Updates the player name in the database.<br>
	 * This method uses the {@code PlayerCommonData} object to identify the correct record.<br>
	 * It executes an SQL update statement for the specific {@code id}.
	 * @param recipientCommonData The data object containing the new name and unique ID.
	 */
	@Override
	public void storePlayerName(PlayerCommonData recipientCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE players SET name=? WHERE id=?");
			
			log.debug("[DAO: MySQL5PlayerDAO] storing playerName " + recipientCommonData.getPlayerObjId() + " " + recipientCommonData.getName());
			
			stmt.setString(1, recipientCommonData.getName());
			stmt.setInt(2, recipientCommonData.getPlayerObjId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error saving playerName: " + recipientCommonData.getPlayerObjId() + " " + recipientCommonData.getName(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the total number of active characters for a specific account.<br>
	 * It counts records in the {@code players} table where the deletion date is null or in the future.<br>
	 * If an error occurs during the database query, it returns 0.
	 * @param accountId The unique identifier of the account to check.
	 * @return The total count of characters associated with the provided {@code accountId}.
	 */
	@Override
	public int getCharacterCountOnAccount(int accountId)
	{
		Connection con = null;
		int cnt = 0;
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS cnt FROM `players` WHERE `account_id` = ? AND (players.deletion_date IS NULL || players.deletion_date > CURRENT_TIMESTAMP)");
			stmt.setInt(1, accountId);
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			cnt = rs.getInt("cnt");
			rs.close();
			stmt.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return cnt;
	}
	
	/**
	 * Calculates the number of unique accounts for a specific race.<br>
	 * This method filters players who have reached the minimum required level.<br>
	 * It queries the database using the {@code Race} name and experience thresholds.
	 * @param race The {@link Race} type to filter by.
	 * @return The total count of unique accounts, or 0 if an error occurs.
	 */
	@Override
	public int getCharacterCountForRace(Race race)
	{
		Connection con = null;
		int count = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT COUNT(DISTINCT(`account_name`)) AS `count` FROM `players` WHERE `race` = ? AND `exp` >= ?");
			stmt.setString(1, race.name());
			stmt.setLong(2, DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(GSConfig.RATIO_MIN_REQUIRED_LEVEL));
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			count = rs.getInt("count");
			rs.close();
			stmt.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return count;
	}
	
	/**
	 * Retrieves the total number of players currently online.<br>
	 * This method queries the database for records where the {@code online} status is {@code true}.<br>
	 * It returns 0 if an error occurs during the database operation.
	 * @return The count of online players as an {@code int}.
	 */
	@Override
	public int getOnlinePlayerCount()
	{
		Connection con = null;
		int count = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) AS `count` FROM `players` WHERE `online` = ?");
			stmt.setBoolean(1, true);
			final ResultSet rs = stmt.executeQuery();
			rs.next();
			count = rs.getInt("count");
			rs.close();
			stmt.close();
		}
		catch (Exception e)
		{
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return count;
	}
	
	/**
	 * Retrieves a set of accounts that have been inactive for a specific period.<br>
	 * It filters out accounts that still have active characters.
	 * @param daysOfInactivity The number of days since the last login to consider an account inactive.
	 * @param limitation The maximum number of results to retrieve from the database.
	 * @return A {@code Set<Integer>} containing the IDs of the inactive accounts.
	 */
	@Override
	public Set<Integer> getInactiveAccounts(int daysOfInactivity, int limitation)
	{
		final String SELECT_QUERY = "SELECT account_id FROM players WHERE UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(last_online) > ? * 24 * 60 * 60";
		
		final Map<Integer, Integer> inactiveAccounts = new HashMap<>();
		
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, daysOfInactivity);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next() && ((limitation == 0) || (limitation > inactiveAccounts.size())))
				{
					final int accountId = rset.getInt("account_id");
					
					// number of inactive chars on account
					Integer numberOfChars = 0;
					
					if ((numberOfChars = inactiveAccounts.get(accountId)) != null)
					{
						inactiveAccounts.put(accountId, numberOfChars + 1);
					}
					else
					{
						inactiveAccounts.put(accountId, 1);
					}
				}
			}
		});
		
		// filter accounts with active chars on them
		for (Iterator<Entry<Integer, Integer>> i = inactiveAccounts.entrySet().iterator(); i.hasNext();)
		{
			final Entry<Integer, Integer> entry = i.next();
			
			// atleast one active char on account
			if (entry.getValue() < getCharacterCountOnAccount(entry.getKey()))
			{
				i.remove();
			}
		}
		
		return inactiveAccounts.keySet();
	}
	
	/**
	 * Updates the last transfer timestamp for a specific player.<br>
	 * This method modifies the {@code last_transfer_time} in the database.
	 * @param playerId The unique identifier of the player.
	 * @param time The timestamp to be saved.
	 */
	@Override
	public void setPlayerLastTransferTime(int playerId, long time)
	{
		DB.insertUpdate("UPDATE players SET last_transfer_time=? WHERE id=?", stmt ->
		{
			stmt.setLong(1, time);
			stmt.setInt(2, playerId);
			stmt.execute();
		});
	}
	
	/**
	 * Updates the bonus time for a specific player.<br>
	 * This method resets the {@code bonus_type} to {@code NORMAL}.<br>
	 * It clears the {@code bonus_buff_time} if it is in the past.
	 * @param playerObjId The unique identifier of the player to update.
	 * @return {@code true} if the database operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean updateBonusTime(int playerObjId)
	{
		return DB.insertUpdate("UPDATE players SET bonus_type = 'NORMAL', bonus_buff_time = NULL WHERE `id` = ? and `bonus_buff_time` < CURRENT_TIMESTAMP", preparedStatement ->
		{
			preparedStatement.setInt(1, playerObjId);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Retrieves the character creation date from the database.<br>
	 * It uses the provided object ID to find the specific record.
	 * @param obj The unique identifier of the player object.
	 * @return A {@code Timestamp} representing the creation date, or {@code null} if an error occurs.
	 */
	@Override
	public Timestamp getCharacterCreationDateId(int obj)
	{
		Connection con = null;
		Timestamp creationDate;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement s = con.prepareStatement("SELECT `creation_date` FROM `players` WHERE `id` = ?");
			s.setInt(1, obj);
			final ResultSet rs = s.executeQuery();
			rs.next();
			creationDate = rs.getTimestamp("creation_date");
			rs.close();
			s.close();
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return creationDate;
	}
	
	/**
	 * Updates the join request status for a specific player in the database.<br>
	 * This method changes the {@code joinRequestState} value for the given {@code playerId}.
	 * @param playerId The unique identifier of the player to update.
	 * @param state The new {@link LegionJoinRequestState} to apply to the player.
	 */
	@Override
	public void updateLegionJoinRequestState(int playerId, LegionJoinRequestState state)
	{
		DB.insertUpdate("UPDATE players SET joinRequestState=? WHERE id=?", stmt ->
		{
			log.debug("[DAO: MySQL5PlayerDAO] Update joinRequestState for player " + playerId + " to : " + state.name());
			
			stmt.setString(1, state.name());
			stmt.setInt(2, playerId);
			stmt.execute();
		});
	}
	
	/**
	 * Removes the pending legion join request for a specific player.<br>
	 * This method updates the database to set the state to {@code NONE}.
	 * @param playerId The unique identifier of the player.
	 */
	@Override
	public void clearJoinRequest(int playerId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE players SET joinRequestLegionId=?, joinRequestState=? WHERE id=?");
			
			log.debug("[DAO: MySQL5PlayerDAO] Cleared LegionJoinRequest for player " + playerId);
			
			stmt.setInt(1, 0);
			stmt.setString(2, "NONE");
			stmt.setInt(3, playerId);
		}
		catch (Exception e)
		{
			
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the join request state from the database for a specific player.<br>
	 * This method updates the {@code PlayerCommonData} of the provided {@link Player}.
	 * @param player The {@link Player} object to update.
	 */
	@Override
	public void getJoinRequestState(Player player)
	{
		final String SELECT_QUERY = "SELECT * FROM players WHERE id=?";
		
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				if (rset.next())
				{
					// log.info(" State: "+LegionJoinRequestState.valueOf(rset.getString("joinRequestState")).name());
					player.getCommonData().setJoinRequestState(LegionJoinRequestState.valueOf(rset.getString("joinRequestState")));
				}
			}
		});
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
