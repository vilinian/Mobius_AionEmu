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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerRegisteredItemsDAO;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.HouseDecoration;
import com.aionemu.gameserver.model.gameobjects.HouseObject;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseRegistry;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.housing.PartType;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.item.HouseObjectFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

/**
 * This class provides the {@code MySQL5} database implementation for handling registered player items.<br>
 * It extends {@link PlayerRegisteredItemsDAO} to perform specific SQL queries for item persistence.
 * @author Rolandas
 */
public class MySQL5PlayerRegisteredItemsDAO extends PlayerRegisteredItemsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerRegisteredItemsDAO.class);
	public static final String CLEAN_PLAYER_QUERY = "DELETE FROM `player_registered_items` WHERE `player_id` = ?";
	public static final String SELECT_QUERY = "SELECT * FROM `player_registered_items` WHERE `player_id`=?";
	public static final String INSERT_QUERY = "INSERT INTO `player_registered_items` " + "(`expire_time`,`color`,`color_expires`,`owner_use_count`,`visitor_use_count`,`x`,`y`,`z`,`h`,`area`,`floor`,`player_id`,`item_unique_id`,`item_id`) VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_registered_items` SET " + "`expire_time`=?,`color`=?,`color_expires`=?,`owner_use_count`=?,`visitor_use_count`=?,`x`=?,`y`=?,`z`=?,`h`=?,`area`=?,`floor`=? " + "WHERE `player_id`=? AND `item_unique_id`=? AND `item_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_registered_items` WHERE `item_unique_id` = ?";
	public static final String RESET_QUERY = "UPDATE `player_registered_items` SET x=0,y=0,z=0,h=0,area='NONE' WHERE `player_id`=? AND `area` != 'DECOR'";
	private static final Predicate<HouseObject<?>> objectsToAddPredicate = (HouseObject<?> input) -> (input != null) && (input.getPersistentState() == PersistentState.NEW);
	private static final Predicate<HouseObject<?>> objectsToUpdatePredicate = (HouseObject<?> input) -> (input != null) && (input.getPersistentState() == PersistentState.UPDATE_REQUIRED);
	private static final Predicate<HouseObject<?>> objectsToDeletePredicate = (HouseObject<?> input) -> (input != null) && (PersistentState.DELETED == input.getPersistentState());
	private static final Predicate<HouseDecoration> partsToAddPredicate = (HouseDecoration input) -> (input != null) && (input.getPersistentState() == PersistentState.NEW);
	private static final Predicate<HouseDecoration> partsToUpdatePredicate = (HouseDecoration input) -> (input != null) && (input.getPersistentState() == PersistentState.UPDATE_REQUIRED);
	private static final Predicate<HouseDecoration> partsToDeletePredicate = (HouseDecoration input) -> (input != null) && (PersistentState.DELETED == input.getPersistentState());
	
	/**
	 * Retrieves all unique identifiers from the {@code player_registered_items} table.<br>
	 * This method queries the database to collect every {@code item_unique_id} that is not equal to {@code 0}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the unique item IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT item_unique_id FROM player_registered_items WHERE item_unique_id <> 0", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
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
				ids[i] = rs.getInt(1);
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from player_registered_items table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
	}
	
	/**
	 * Loads the house registry data from the database for a specific player.<br>
	 * This method retrieves all decorations and objects associated with the player's house.<br>
	 * It updates the {@link HouseRegistry} with the correct persistent states.
	 * @param playerId The unique identifier of the player to load.
	 */
	@Override
	public void loadRegistry(int playerId)
	{
		House house = HousingService.getInstance().getPlayerStudio(playerId);
		if (house == null)
		{
			final int address = HousingService.getInstance().getPlayerAddress(playerId);
			house = HousingService.getInstance().getHouseByAddress(address);
		}
		
		final HouseRegistry registry = house.getRegistry();
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			final HashMap<PartType, List<HouseDecoration>> usedParts = new HashMap<>();
			while (rset.next())
			{
				final String area = rset.getString("area");
				if ("DECOR".equals(area))
				{
					final HouseDecoration dec = createDecoration(rset);
					registry.putCustomPart(dec);
					if (dec.isUsed())
					{
						if ((house.getHouseType() != HouseType.PALACE) && (dec.getFloor() > 0))
						{
							dec.setFloor(0);
						}
						
						List<HouseDecoration> usedForType = usedParts.get(dec.getTemplate().getType());
						if (usedForType == null)
						{
							usedForType = new ArrayList<>();
							usedParts.put(dec.getTemplate().getType(), usedForType);
						}
						
						usedForType.add(dec);
					}
					
					dec.setPersistentState(PersistentState.UPDATED);
				}
				else
				{
					final HouseObject<?> obj = constructObject(registry, house, rset);
					registry.putObject(obj);
					obj.setPersistentState(PersistentState.UPDATED);
				}
			}
			
			for (PartType partType : PartType.values())
			{
				if (usedParts.containsKey(partType))
				{
					for (HouseDecoration usedDeco : usedParts.get(partType))
					{
						registry.setPartInUse(usedDeco, usedDeco.getFloor());
					}
					continue;
				}
				
				int floorCount = 1;
				if ((house.getHouseType() == HouseType.PALACE) && ((partType == PartType.INFLOOR_ANY) || (partType == PartType.INWALL_ANY)))
				{
					floorCount = 6;
				}
				
				for (int i = 0; i < floorCount; i++)
				{
					final HouseDecoration def = registry.getDefaultPartByType(partType, i);
					if (def != null)
					{
						registry.setPartInUse(def, i);
					}
				}
			}
			
			registry.setPersistentState(PersistentState.UPDATED);
			rset.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore house registry data for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
	}
	
	/**
	 * Creates or retrieves a {@link HouseObject} from the database results.<br>
	 * This method checks if the object exists in the world before creating a new one.<br>
	 * It populates the object properties with data from the {@code ResultSet}.
	 * @param registry The {@link HouseRegistry} used to look up existing objects.
	 * @param house The {@link House} associated with the object.
	 * @param rset The {@link ResultSet} containing the database row data.
	 * @return The constructed or retrieved {@link HouseObject}.
	 * @throws SQLException
	 * @throws IllegalAccessException
	 */
	private HouseObject<?> constructObject(HouseRegistry registry, House house, ResultSet rset) throws SQLException, IllegalAccessException
	{
		final int itemUniqueId = rset.getInt("item_unique_id");
		final VisibleObject visObj = World.getInstance().findVisibleObject(itemUniqueId);
		HouseObject<?> obj = null;
		if (visObj != null)
		{
			if (visObj instanceof HouseObject<?>)
			{
				obj = (HouseObject<?>) visObj;
			}
			else
			{
				throw new IllegalAccessException("Someone stole my house object id : " + itemUniqueId);
			}
		}
		else
		{
			obj = registry.getObjectByObjId(itemUniqueId);
			if (obj == null)
			{
				obj = HouseObjectFactory.createNew(house, itemUniqueId, rset.getInt("item_id"));
			}
		}
		
		obj.setOwnerUsedCount(rset.getInt("owner_use_count"));
		obj.setVisitorUsedCount(rset.getInt("visitor_use_count"));
		obj.setX(rset.getFloat("x"));
		obj.setY(rset.getFloat("y"));
		obj.setZ(rset.getFloat("z"));
		obj.setHeading((byte) rset.getInt("h"));
		obj.setColor(rset.getInt("color"));
		obj.setColorExpireEnd(rset.getInt("color_expires"));
		if (obj.getObjectTemplate().getUseDays() > 0)
		{
			obj.setExpireTime(rset.getInt("expire_time"));
		}
		
		return obj;
	}
	
	/**
	 * Converts a {@code ResultSet} into a {@link HouseDecoration} object.<br>
	 * This method extracts the unique ID, item ID, and floor from the database row.<br>
	 * It also determines if the decoration is currently in use.
	 * @param rset The {@code ResultSet} containing the decoration data.
	 * @return A new {@link HouseDecoration} instance populated with the retrieved values.
	 * @throws SQLException If a database access error occurs.
	 */
	private HouseDecoration createDecoration(ResultSet rset) throws SQLException
	{
		final int itemUniqueId = rset.getInt("item_unique_id");
		final int itemId = rset.getInt("item_Id");
		final byte floor = rset.getByte("floor");
		final HouseDecoration decor = new HouseDecoration(itemUniqueId, itemId, floor);
		decor.setUsed(rset.getInt("owner_use_count") > 0);
		return decor;
	}
	
	/**
	 * Saves the current state of a house registry to the database.<br>
	 * This method handles adding, updating, and deleting objects and decorations.<br>
	 * It updates the persistent state of all items in the {@link HouseRegistry}.
	 * @param registry The {@link HouseRegistry} containing the house data to save.
	 * @param playerId The unique identifier for the player who owns the house.
	 * @return Always returns {@code true} regardless of success or failure.
	 */
	@Override
	public boolean store(HouseRegistry registry, int playerId)
	{
		final List<HouseObject<?>> objects = registry.getObjects();
		final List<HouseDecoration> decors = registry.getAllParts();
		final Collection<HouseObject<?>> objectsToAdd = objects.stream().filter(objectsToAddPredicate).collect(Collectors.toList());
		final Collection<HouseObject<?>> objectsToUpdate = objects.stream().filter(objectsToUpdatePredicate).collect(Collectors.toList());
		final Collection<HouseObject<?>> objectsToDelete = objects.stream().filter(objectsToDeletePredicate).collect(Collectors.toList());
		final Collection<HouseDecoration> partsToAdd = decors.stream().filter(partsToAddPredicate).collect(Collectors.toList());
		final Collection<HouseDecoration> partsToUpdate = decors.stream().filter(partsToUpdatePredicate).collect(Collectors.toList());
		final Collection<HouseDecoration> partsToDelete = decors.stream().filter(partsToDeletePredicate).collect(Collectors.toList());
		
		boolean objectDeleteResult = false;
		boolean partsDeleteResult = false;
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			objectDeleteResult = deleteObjects(con, objectsToDelete);
			partsDeleteResult = deleteParts(con, partsToDelete);
			storeObjects(con, objectsToUpdate, playerId, false);
			storeParts(con, partsToUpdate, playerId, false);
			storeObjects(con, objectsToAdd, playerId, true);
			storeParts(con, partsToAdd, playerId, true);
			registry.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e)
		{
			log.error("Can't open connection to save player inventory: " + playerId);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (HouseObject<?> obj : objects)
		{
			if (obj.getPersistentState() == PersistentState.DELETED)
			{
				registry.discardObject(obj.getObjectId());
			}
			else
			{
				obj.setPersistentState(PersistentState.UPDATED);
			}
		}
		
		for (HouseDecoration decor : decors)
		{
			if (decor.getPersistentState() == PersistentState.DELETED)
			{
				registry.discardPart(decor);
			}
			else
			{
				decor.setPersistentState(PersistentState.UPDATED);
			}
		}
		
		if (!objectsToDelete.isEmpty() && objectDeleteResult)
		{
			final Collection<Integer> idIterator = objectsToDelete.stream().map(AionObject.OBJECT_TO_ID_TRANSFORMER).collect(Collectors.toList());
			IDFactory.getInstance().releaseIds(idIterator);
		}
		
		if (!partsToDelete.isEmpty() && partsDeleteResult)
		{
			for (HouseDecoration part : partsToDelete)
			{
				if (part.getObjectId() != 0)
				{
					IDFactory.getInstance().releaseId(part.getObjectId());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Saves a collection of {@link HouseObject} instances to the database.<br>
	 * It uses an {@code INSERT} query if {@code isNew} is {@code true}.<br>
	 * It uses an {@code UPDATE} query if {@code isNew} is {@code false}.
	 * @param con The active database {@link Connection}.
	 * @param objects The collection of house objects to save.
	 * @param playerId The unique ID of the player owning the objects.
	 * @param isNew A flag indicating whether to insert or update the records.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	private boolean storeObjects(Connection con, Collection<HouseObject<?>> objects, int playerId, boolean isNew)
	{
		if (GenericValidator.isBlankOrNull(objects))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(isNew ? INSERT_QUERY : UPDATE_QUERY);
			
			for (HouseObject<?> obj : objects)
			{
				if (obj.getExpireTime() > 0)
				{
					stmt.setInt(1, obj.getExpireTime());
				}
				else
				{
					stmt.setNull(1, Types.INTEGER);
				}
				
				if (obj.getColor() == null)
				{
					stmt.setNull(2, Types.INTEGER);
				}
				else
				{
					stmt.setInt(2, obj.getColor());
				}
				
				stmt.setInt(3, obj.getColorExpireEnd());
				stmt.setInt(4, obj.getOwnerUsedCount());
				stmt.setInt(5, obj.getVisitorUsedCount());
				stmt.setFloat(6, obj.getX());
				stmt.setFloat(7, obj.getY());
				stmt.setFloat(8, obj.getZ());
				stmt.setInt(9, obj.getHeading());
				if ((obj.getX() > 0) || (obj.getY() > 0) || (obj.getZ() > 0))
				{
					stmt.setString(10, obj.getPlaceArea().toString());
				}
				else
				{
					stmt.setString(10, "NONE");
				}
				
				stmt.setByte(11, (byte) 0);
				stmt.setInt(12, playerId);
				stmt.setInt(13, obj.getObjectId());
				stmt.setInt(14, obj.getObjectTemplate().getTemplateId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute house object update batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Saves a collection of {@link HouseDecoration} objects to the database.<br>
	 * It uses an insert or update query based on the {@code isNew} flag.<br>
	 * The method processes all parts in a single batch operation.
	 * @param con The active {@link Connection} to the database.
	 * @param parts The collection of decorations to be stored.
	 * @param playerId The unique identifier of the player owning the items.
	 * @param isNew Set to {@code true} to perform an insert, or {@code false} for an update.
	 * @return {@code true} if the operation succeeded or if the parts collection was empty; {@code false} otherwise.
	 */
	private boolean storeParts(Connection con, Collection<HouseDecoration> parts, int playerId, boolean isNew)
	{
		if (GenericValidator.isBlankOrNull(parts))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(isNew ? INSERT_QUERY : UPDATE_QUERY);
			for (HouseDecoration part : parts)
			{
				stmt.setNull(1, Types.INTEGER);
				stmt.setNull(2, Types.INTEGER);
				stmt.setInt(3, 0);
				stmt.setInt(4, part.isUsed() ? 1 : 0);
				stmt.setInt(5, 0);
				stmt.setFloat(6, 0);
				stmt.setFloat(7, 0);
				stmt.setFloat(8, 0);
				stmt.setInt(9, 0);
				stmt.setString(10, "DECOR");
				stmt.setByte(11, part.getFloor());
				stmt.setInt(12, playerId);
				stmt.setInt(13, part.getObjectId());
				stmt.setInt(14, part.getTemplate().getId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute house parts update batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Deletes a collection of house objects from the database.<br>
	 * This method uses a batch statement to improve performance.<br>
	 * It returns {@code true} if the operation succeeds or if the input is null.
	 * @param con The active {@link Connection} to the database.
	 * @param objects The collection of {@code HouseObject<?>} to be removed.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	private boolean deleteObjects(Connection con, Collection<HouseObject<?>> objects)
	{
		if (GenericValidator.isBlankOrNull(objects))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(DELETE_QUERY);
			for (HouseObject<?> obj : objects)
			{
				stmt.setInt(1, obj.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute delete batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Removes a collection of house decorations from the database.<br>
	 * This method uses a batch process to execute the deletion.<br>
	 * It returns {@code true} if the operation succeeds or if the input is empty.
	 * @param con The active {@link Connection} to the database.
	 * @param parts The collection of {@link HouseDecoration} objects to remove.
	 * @return {@code true} if the deletion was successful, otherwise {@code false}.
	 */
	private boolean deleteParts(Connection con, Collection<HouseDecoration> parts)
	{
		if (GenericValidator.isBlankOrNull(parts))
		{
			return true;
		}
		
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(DELETE_QUERY);
			for (HouseDecoration part : parts)
			{
				stmt.setInt(1, part.getObjectId());
				stmt.addBatch();
			}
			
			stmt.executeBatch();
			con.commit();
		}
		catch (Exception e)
		{
			log.error("Failed to execute delete batch", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(stmt);
		}
		
		return true;
	}
	
	/**
	 * Deletes all items belonging to a specific player from the database.<br>
	 * This method uses {@code CLEAN_PLAYER_QUERY} to clear the inventory.
	 * @param playerId The unique identifier of the player whose items will be removed.
	 * @return {@code true} if the deletion was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean deletePlayerItems(int playerId)
	{
		Connection con = null;
		try
		{
			log.info("Deleting player items");
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(CLEAN_PLAYER_QUERY);
			stmt.setInt(1, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error in deleting all player registered items. PlayerObjId: " + playerId, e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Clears all registered items for a specific player.<br>
	 * This method executes a database query to reset the data associated with the provided ID.
	 * @param playerId The unique identifier of the player to reset.
	 */
	@Override
	public void resetRegistry(int playerId)
	{
		Connection con = null;
		try
		{
			log.info("resetting player items: " + playerId);
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(RESET_QUERY);
			stmt.setInt(1, playerId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error in resetting  player registered items. PlayerObjId: " + playerId, e);
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
}
