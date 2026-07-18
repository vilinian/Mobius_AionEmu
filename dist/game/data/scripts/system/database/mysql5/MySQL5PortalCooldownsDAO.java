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
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PortalCooldownsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownItem;

/**
 * This class provides the {@code MySQL5} database implementation for managing portal cooldowns.<br>
 * It handles data persistence and retrieval for {@link PortalCooldownItem} objects using a {@code MySQL5} backend.
 */
public class MySQL5PortalCooldownsDAO extends PortalCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PortalCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `portal_cooldowns` (`player_id`, `world_id`, `reuse_time`, `entry_count`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `portal_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `world_id`, `reuse_time`, `entry_count` FROM `portal_cooldowns` WHERE `player_id`=?";
	
	/**
	 * Loads the portal cooldown data from the database for a specific player.<br>
	 * This method populates the {@code getPortalCooldownList} with active cooldowns.<br>
	 * It filters out any items where the reuse time has already passed.
	 * @param player The {@code Player} object whose cooldowns need to be loaded.
	 */
	@Override
	public void loadPortalCooldowns(Player player)
	{
		Connection con = null;
		final Map<Integer, PortalCooldownItem> portalCoolDowns = new HashMap<>();
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			
			while (rset.next())
			{
				final int worldId = rset.getInt("world_id");
				final long reuseTime = rset.getLong("reuse_time");
				final int entryCount = rset.getInt("entry_count");
				if (reuseTime > System.currentTimeMillis())
				{
					portalCoolDowns.put(worldId, new PortalCooldownItem(worldId, entryCount, reuseTime));
				}
			}
			
			player.getPortalCooldownList().setPortalCoolDowns(portalCoolDowns);
			rset.close();
		}
		catch (SQLException e)
		{
			log.error("LoadPortalCooldowns", e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
		}
	}
	
	/**
	 * Saves the portal cooldown data for a specific player to the database.<br>
	 * This method removes old records and inserts new ones from the {@link Player} object.<br>
	 * It only stores cooldowns where the reuse time is in the future.
	 * @param player The {@code Player} whose cooldown information needs to be saved.
	 */
	@Override
	public void storePortalCooldowns(Player player)
	{
		deletePortalCooldowns(player);
		final Map<Integer, PortalCooldownItem> portalCoolDowns = player.getPortalCooldownList().getPortalCoolDowns();
		
		if (portalCoolDowns == null)
		{
			return;
		}
		
		for (Map.Entry<Integer, PortalCooldownItem> entry : portalCoolDowns.entrySet())
		{
			final int worldId = entry.getKey();
			final long reuseTime = entry.getValue().getCooldown();
			final int entryCount = entry.getValue().getEntryCount();
			
			if (reuseTime < System.currentTimeMillis())
			{
				continue;
			}
			
			Connection con = null;
			
			PreparedStatement stmt = null;
			try
			{
				con = DatabaseFactory.getConnection();
				stmt = con.prepareStatement(INSERT_QUERY);
				
				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, worldId);
				stmt.setLong(3, reuseTime);
				stmt.setInt(4, entryCount);
				stmt.execute();
			}
			catch (SQLException e)
			{
				log.error("storePortalCooldowns", e);
			}
			finally
			{
				DatabaseFactory.close(stmt, con);
			}
		}
	}
	
	/**
	 * Removes all portal cooldown records for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data associated with the {@code Player}.<br>
	 * It handles any {@code SQLException} by logging an error message.
	 * @param player The {@link Player} object whose cooldowns need to be deleted.
	 */
	private void deletePortalCooldowns(Player player)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY);
			
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		}
		catch (SQLException e)
		{
			log.error("deletePortalCooldowns", e);
		}
		finally
		{
			DatabaseFactory.close(stmt, con);
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
