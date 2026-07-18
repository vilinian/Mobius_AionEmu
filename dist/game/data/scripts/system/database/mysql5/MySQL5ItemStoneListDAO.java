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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.IdianStone;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ItemStone.ItemStoneType;
import com.aionemu.gameserver.model.items.ManaStone;

/**
 * This class provides the {@code MySQL5} database implementation for managing item stones.<br>
 * It handles data access operations for various stone types like {@link ManaStone} and {@link GodStone}.<br>
 * It extends the base functionality defined in {@link ItemStoneListDAO}.
 * @author ATracer
 * @rework FrozenKiller
 */
public class MySQL5ItemStoneListDAO extends ItemStoneListDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5ItemStoneListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `item_stones` (`item_unique_id`, `item_id`, `slot`, `category`, `polishNumber`, `polishCharge`) VALUES (?,?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `item_stones` SET `item_id`=?, `slot`=?, `polishNumber`=?, `polishCharge`=? where `item_unique_id`=? AND `category`=?";
	public static final String DELETE_QUERY = "DELETE FROM `item_stones` WHERE `item_unique_id`=? AND slot=? AND category=?";
	public static final String SELECT_QUERY = "SELECT `item_id`, `slot`, `category`, `polishNumber`, `polishCharge` FROM `item_stones` WHERE `item_unique_id`=?";
	private static final Predicate<ItemStone> itemStoneAddPredicate = (ItemStone itemStone) -> (itemStone != null) && (PersistentState.NEW == itemStone.getPersistentState());
	private static final Predicate<ItemStone> itemStoneDeletedPredicate = (ItemStone itemStone) -> (itemStone != null) && (PersistentState.DELETED == itemStone.getPersistentState());
	private static final Predicate<ItemStone> itemStoneUpdatePredicate = (ItemStone itemStone) -> (itemStone != null) && (PersistentState.UPDATE_REQUIRED == itemStone.getPersistentState());
	
	/**
	 * Loads item stone data from the database for a collection of items.<br>
	 * This method updates {@link ManaStone}, {@link GodStone}, and {@link IdianStone} objects.<br>
	 * It checks if the item is an armor or weapon before processing.
	 * @param items The collection of {@link Item} objects to load data for.
	 */
	@Override
	public void load(Collection<Item> items)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			for (Item item : items)
			{
				if (item.getItemTemplate().isArmor() || item.getItemTemplate().isWeapon())
				{
					stmt.setInt(1, item.getObjectId());
					final ResultSet rset = stmt.executeQuery();
					while (rset.next())
					{
						final int itemId = rset.getInt("item_id");
						final int slot = rset.getInt("slot");
						final int stoneType = rset.getInt("category");
						switch (stoneType)
						{
							case 0:
								if (item.getSockets(false) <= item.getItemStonesSize())
								{
									log.warn("Manastone slots overloaded. ObjectId: " + item.getObjectId());
									if (EnchantsConfig.CLEAN_STONE)
									{
										deleteItemStone(con, item.getObjectId(), slot, stoneType);
									}
									continue;
								}
								
								item.getItemStones().add(new ManaStone(item.getObjectId(), itemId, slot, PersistentState.UPDATED));
								break;
							case 1:
								item.setGodStone(new GodStone(item.getObjectId(), itemId, PersistentState.UPDATED));
								break;
							case 2:
								if (item.getSockets(true) <= item.getFusionStonesSize())
								{
									log.warn("Manastone slots overloaded. ObjectId: " + item.getObjectId());
									if (EnchantsConfig.CLEAN_STONE)
									{
										deleteItemStone(con, item.getObjectId(), slot, stoneType);
									}
									continue;
								}
								
								item.getFusionStones().add(new ManaStone(item.getObjectId(), itemId, slot, PersistentState.UPDATED));
								break;
							case 3:
								item.setIdianStone(new IdianStone(itemId, PersistentState.UPDATE_REQUIRED, item, rset.getInt("polishNumber"), rset.getInt("polishCharge")));
								break;
						}
					}
					
					rset.close();
				}
			}
			
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore ItemStoneList data from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves a list of {@link Item} objects to the database.<br>
	 * This method extracts and stores various types of stones from each item.<br>
	 * It handles mana stones, fusion stones, god stones, and idian stones.
	 * @param items The list of {@link Item} objects to be saved.
	 */
	@Override
	public void save(List<Item> items)
	{
		if (GenericValidator.isBlankOrNull(items))
		{
			return;
		}
		
		final Set<ManaStone> manaStones = new HashSet<>();
		final Set<ManaStone> fusionStones = new HashSet<>();
		final Set<GodStone> godStones = new HashSet<>();
		final Set<IdianStone> idianStones = new HashSet<>();
		
		for (Item item : items)
		{
			if (item.hasManaStones())
			{
				manaStones.addAll(item.getItemStones());
			}
			
			if (item.hasFusionStones())
			{
				fusionStones.addAll(item.getFusionStones());
			}
			
			final GodStone godStone = item.getGodStone();
			if (godStone != null)
			{
				godStones.add(godStone);
			}
			
			final IdianStone idianStone = item.getIdianStone();
			if (idianStone != null)
			{
				idianStones.add(idianStone);
			}
		}
		
		store(manaStones, ItemStoneType.MANASTONE);
		store(fusionStones, ItemStoneType.FUSIONSTONE);
		store(godStones, ItemStoneType.GODSTONE);
		store(idianStones, ItemStoneType.IDIANSTONE);
	}
	
	/**
	 * Saves a set of {@link ManaStone} objects to the database.<br>
	 * This method uses the internal {@code store} helper to persist the data.
	 * @param manaStones The set of stones to be stored.
	 */
	@Override
	public void storeManaStones(Set<ManaStone> manaStones)
	{
		store(manaStones, ItemStoneType.MANASTONE);
	}
	
	/**
	 * Saves a set of fusion stones to the database.<br>
	 * This method calls {@code ItemStoneType)} using the {@code FUSIONSTONE} type.
	 * @param fusionStones The set of {@code ManaStone} objects to be stored.
	 */
	@Override
	public void storeFusionStones(Set<ManaStone> fusionStones)
	{
		store(fusionStones, ItemStoneType.FUSIONSTONE);
	}
	
	/**
	 * Saves a single {@code IdianStone} object to the database.<br>
	 * This method uses the internal {@code ItemStoneType)} logic.
	 * @param idianStone The {@code IdianStone} instance to be stored.
	 */
	@Override
	public void storeIdianStones(IdianStone idianStone)
	{
		store(Collections.singleton(idianStone), ItemStoneType.IDIANSTONE);
	}
	
	/**
	 * Saves a collection of {@link ItemStone} objects to the database.<br>
	 * This method handles adding, updating, and deleting stones based on their state.<br>
	 * It also updates the persistent state of each stone to {@code UPDATED}.
	 * @param stones The set of stones to be processed.
	 * @param ist The type of item stone being stored.
	 */
	private void store(Set<? extends ItemStone> stones, ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(stones))
		{
			return;
		}
		
		final Set<? extends ItemStone> stonesToAdd = stones.stream().filter(itemStoneAddPredicate).collect(Collectors.toSet());
		final Set<? extends ItemStone> stonesToDelete = stones.stream().filter(itemStoneDeletedPredicate).collect(Collectors.toSet());
		final Set<? extends ItemStone> stonesToUpdate = stones.stream().filter(itemStoneUpdatePredicate).collect(Collectors.toSet());
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			
			deleteItemStones(con, stonesToDelete, ist);
			addItemStones(con, stonesToAdd, ist);
			updateItemStones(con, stonesToUpdate, ist);
			
		}
		catch (SQLException e)
		{
			log.error("Can't save stones", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		for (ItemStone is : stones)
		{
			is.setPersistentState(PersistentState.UPDATED);
		}
	}
	
	/**
	 * Adds a collection of {@link ItemStone} objects to the database.<br>
	 * This method uses a batch process to insert records into the table.<br>
	 * It handles specific data for {@link IdianStone} types during insertion.
	 * @param con The active {@link Connection} to the database.
	 * @param itemStones The collection of stones to be added.
	 * @param ist The type of stone used to determine the category ordinal.
	 */
	private void addItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones))
		{
			return;
		}
		
		PreparedStatement st = null;
		try
		{
			st = con.prepareStatement(INSERT_QUERY);
			
			for (ItemStone is : itemStones)
			{
				st.setInt(1, is.getItemObjId());
				st.setInt(2, is.getItemId());
				st.setInt(3, is.getSlot());
				st.setInt(4, ist.ordinal());
				if (is instanceof IdianStone)
				{
					final IdianStone stone = (IdianStone) is;
					st.setInt(5, stone.getPolishNumber());
					st.setInt(6, stone.getPolishCharge());
				}
				else
				{
					st.setInt(5, 0);
					st.setInt(6, 0);
				}
				
				st.addBatch();
			}
			
			st.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Error occured while saving item stones", e);
		}
		finally
		{
			DatabaseFactory.close(st);
		}
	}
	
	/**
	 * Updates the database records for a collection of {@link ItemStone} objects.<br>
	 * This method uses a batch update to save changes to item IDs, slots, and polish data.<br>
	 * It handles specific logic for {@link IdianStone} types during the update process.
	 * @param con The active {@link Connection} to the database.
	 * @param itemStones The collection of stones to be updated.
	 * @param ist The {@link ItemStoneType} category used for filtering the update query.
	 */
	private void updateItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones))
		{
			return;
		}
		
		PreparedStatement st = null;
		try
		{
			st = con.prepareStatement(UPDATE_QUERY);
			
			for (ItemStone is : itemStones)
			{
				st.setInt(1, is.getItemId());
				st.setInt(2, is.getSlot());
				if (is instanceof IdianStone)
				{
					final IdianStone stone = (IdianStone) is;
					st.setInt(3, stone.getPolishNumber());
					st.setInt(4, stone.getPolishCharge());
				}
				else
				{
					st.setInt(3, 0);
					st.setInt(4, 0);
				}
				
				st.setInt(5, is.getItemObjId());
				st.setInt(6, ist.ordinal());
				st.addBatch();
			}
			
			st.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Error occured while saving item stones", e);
		}
		finally
		{
			DatabaseFactory.close(st);
		}
	}
	
	/**
	 * Removes specific items from the database.<br>
	 * This method uses a batch process to delete records based on unique IDs and categories.<br>
	 * It handles the {@code Connection} and executes the {@code DELETE_QUERY}.
	 * @param con The active database connection.
	 * @param itemStones The collection of stones to be removed.
	 * @param ist The type of stone used to determine the category.
	 */
	private void deleteItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones))
		{
			return;
		}
		
		PreparedStatement st = null;
		try
		{
			st = con.prepareStatement(DELETE_QUERY);
			
			// TODO: Shouldn't we update stone slot?
			for (ItemStone is : itemStones)
			{
				st.setInt(1, is.getItemObjId());
				st.setInt(2, is.getSlot());
				st.setInt(3, ist.ordinal());
				st.execute();
				st.addBatch();
			}
			
			st.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Error occured while saving item stones", e);
		}
		finally
		{
			DatabaseFactory.close(st);
		}
	}
	
	/**
	 * Removes a specific stone from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to delete a record based on its unique identifiers.
	 * @param con The active {@link Connection} to the database.
	 * @param uid The unique identifier of the item.
	 * @param slot The specific slot position of the stone.
	 * @param category The category type of the stone.
	 */
	private void deleteItemStone(Connection con, int uid, int slot, int category)
	{
		PreparedStatement st = null;
		try
		{
			st = con.prepareStatement(DELETE_QUERY);
			st.setInt(1, uid);
			st.setInt(2, slot);
			st.setInt(3, category);
			st.execute();
		}
		catch (SQLException e)
		{
			log.error("Error occured while saving item stones", e);
		}
		finally
		{
			DatabaseFactory.close(st);
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
