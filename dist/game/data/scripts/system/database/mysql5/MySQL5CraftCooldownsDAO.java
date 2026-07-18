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

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.CraftCooldownsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for managing craft cooldowns.<br>
 * It extends {@link CraftCooldownsDAO} to handle specific SQL queries for player crafting data.
 * @author synchro2
 */
public class MySQL5CraftCooldownsDAO extends CraftCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5CraftCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `craft_cooldowns` (`player_id`, `delay_id`, `reuse_time`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `craft_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `delay_id`, `reuse_time` FROM `craft_cooldowns` WHERE `player_id`=?";
	
	/**
	 * Loads the craft cooldown data for a specific player from the database.<br>
	 * This method populates the {@link Player} object with active cooldowns.<br>
	 * It filters out any cooldowns that have already expired.
	 * @param player The {@code Player} whose cooldowns need to be loaded.
	 */
	@Override
	public void loadCraftCooldowns(Player player)
	{
		Connection con = null;
		final Map<Integer, Long> craftCoolDowns = new HashMap<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			
			while (rset.next())
			{
				final int delayId = rset.getInt("delay_id");
				final long reuseTime = rset.getLong("reuse_time");
				final int delay = (int) ((reuseTime - System.currentTimeMillis()) / 1000);
				
				if (delay > 0)
				{
					craftCoolDowns.put(delayId, reuseTime);
				}
			}
			
			player.getCraftCooldownList().setCraftCoolDowns(craftCoolDowns);
			rset.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			log.error("LoadcraftCoolDowns", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the current craft cooldowns for a specific player to the database.<br>
	 * This method removes old data before inserting new valid entries.<br>
	 * It checks if the {@code reuseTime} is greater than the current system time.
	 * @param player The {@link Player} object containing the cooldown data to save.
	 */
	@Override
	public void storeCraftCooldowns(Player player)
	{
		deleteCraftCoolDowns(player);
		final Map<Integer, Long> craftCoolDowns = player.getCraftCooldownList().getCraftCoolDowns();
		
		if (craftCoolDowns == null)
		{
			return;
		}
		
		for (Map.Entry<Integer, Long> entry : craftCoolDowns.entrySet())
		{
			final int delayId = entry.getKey();
			final long reuseTime = entry.getValue();
			
			if (reuseTime < System.currentTimeMillis())
			{
				continue;
			}
			
			Connection con = null;
			
			try
			{
				con = DatabaseFactory.getConnection();
				final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
				
				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, delayId);
				stmt.setLong(3, reuseTime);
				stmt.execute();
			}
			catch (SQLException e)
			{
				log.error("storecraftCoolDowns", e);
			}
			finally
			{
				DatabaseFactory.close(con);
			}
		}
	}
	
	/**
	 * Removes all craft cooldown records for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data associated with the {@code Player}.<br>
	 * It handles {@code SQLException} by logging an error message.
	 * @param player The {@link Player} object whose cooldowns need to be deleted.
	 */
	private void deleteCraftCoolDowns(Player player)
	{
		Connection con = null;
		
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		}
		catch (SQLException e)
		{
			log.error("deletecraftCoolDowns", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
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
