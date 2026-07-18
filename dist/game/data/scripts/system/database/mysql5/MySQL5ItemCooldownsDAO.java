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
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.ItemCooldownsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemCooldown;

/**
 * This class provides the {@code MySQL5} database implementation for managing item cooldowns.<br>
 * It handles data persistence and retrieval for {@link com.aionemu.gameserver.model.items.ItemCooldown} objects.<br>
 * It extends the base functionality defined in {@link com.aionemu.gameserver.dao.ItemCooldownsDAO}.
 * @author ATracer
 */
public class MySQL5ItemCooldownsDAO extends ItemCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5ItemCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `item_cooldowns` (`player_id`, `delay_id`, `use_delay`, `reuse_time`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `item_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `delay_id`, `use_delay`, `reuse_time` FROM `item_cooldowns` WHERE `player_id`=?";
	private static final Predicate<ItemCooldown> itemCooldownPredicate = (ItemCooldown input) -> (input != null) && ((input.getReuseTime() - System.currentTimeMillis()) > 30000);
	
	/**
	 * Loads the item cooldown data for a specific player from the database.<br>
	 * This method populates the {@link Player} object with active cooldowns.<br>
	 * It also triggers a broadcast of effects via the effect controller.
	 * @param player The {@code Player} whose cooldowns need to be loaded.
	 */
	@Override
	public void loadItemCooldowns(Player player)
	{
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
				while (rset.next())
				{
					final int delayId = rset.getInt("delay_id");
					final int useDelay = rset.getInt("use_delay");
					final long reuseTime = rset.getLong("reuse_time");
					
					if (reuseTime > System.currentTimeMillis())
					{
						player.addItemCoolDown(delayId, reuseTime, useDelay);
					}
					
				}
			}
		});
		player.getEffectController().broadCastEffects();
	}
	
	/**
	 * Saves the current item cooldowns for a specific player to the database.<br>
	 * This method removes old records and inserts new ones using {@code INSERT_QUERY}.<br>
	 * It only stores cooldowns that are still active based on the internal predicate.
	 * @param player The {@code Player} object whose cooldown data needs to be saved.
	 */
	@Override
	public void storeItemCooldowns(Player player)
	{
		deleteItemCooldowns(player);
		final Map<Integer, ItemCooldown> itemCoolDowns = player.getItemCoolDowns();
		
		if (itemCoolDowns == null)
		{
			return;
		}
		
		final Map<Integer, ItemCooldown> map = new HashMap<>();
		for (Map.Entry<Integer, ItemCooldown> entry : itemCoolDowns.entrySet())
		{
			if (itemCooldownPredicate.test(entry.getValue()))
			{
				map.put(entry.getKey(), entry.getValue());
			}
		}
		
		final Iterator<Map.Entry<Integer, ItemCooldown>> iterator = map.entrySet().iterator();
		if (!iterator.hasNext())
		{
			return;
		}
		
		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(INSERT_QUERY);
			
			while (iterator.hasNext())
			{
				final Map.Entry<Integer, ItemCooldown> entry = iterator.next();
				st.setInt(1, player.getObjectId());
				st.setInt(2, entry.getKey());
				st.setInt(3, entry.getValue().getUseDelay());
				st.setLong(4, entry.getValue().getReuseTime());
				st.addBatch();
			}
			
			st.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Error while storing item cooldows for player " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(st, con);
		}
	}
	
	/**
	 * Removes all cooldown records for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data associated with the {@code Player}.<br>
	 * It is typically called when a player logs out or leaves the game.
	 * @param player The {@link Player} object whose cooldowns need to be removed.
	 */
	private void deleteItemCooldowns(Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt ->
		{
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
	
	/**
	 * Checks if the current database configuration supports specific requirements.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param arg0 The first requirement string.
	 * @param arg1 The first integer value.
	 * @param arg2 The second integer value.
	 * @return {@code true} if the requirements are met, otherwise {@code false}.
	 */
	@Override
	public boolean supports(String arg0, int arg1, int arg2)
	{
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}
