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

/**
 * @author nrg
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerCooldownsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for managing player cooldowns.<br>
 * It handles data persistence and retrieval operations specifically for the {@link PlayerCooldownsDAO} interface.
 * @author nrg
 */
public class MySQL5PlayerCooldownsDAO extends PlayerCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerCooldownsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_cooldowns` (`player_id`, `cooldown_id`, `reuse_delay`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `cooldown_id`, `reuse_delay` FROM `player_cooldowns` WHERE `player_id`=?";
	private static final Predicate<Long> cooldownPredicate = (Long input) -> (input != null) && ((input - System.currentTimeMillis()) > 28000);
	
	/**
	 * This method loads the cooldown data for a specific player from the database.<br>
	 * It updates the {@link Player} object with active skill delays.<br>
	 * Only cooldowns that have not yet expired are applied.
	 * @param player The {@code Player} object to load cooldowns into.
	 */
	@Override
	public void loadPlayerCooldowns(Player player)
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
					final int cooldownId = rset.getInt("cooldown_id");
					final long reuseDelay = rset.getLong("reuse_delay");
					
					if (reuseDelay > System.currentTimeMillis())
					{
						player.setSkillCoolDown(cooldownId, reuseDelay);
					}
				}
			}
		});
	}
	
	/**
	 * Saves the current skill cooldowns of a {@link Player} to the database.<br>
	 * This method removes old records and inserts only those that exceed the 28000ms threshold.<br>
	 * It uses batch processing to improve performance during the save operation.
	 * @param player The {@code Player} object containing the cooldown data to be stored.
	 */
	@Override
	public void storePlayerCooldowns(Player player)
	{
		deletePlayerCooldowns(player);
		
		final Map<Integer, Long> cooldowns = player.getSkillCoolDowns();
		if ((cooldowns != null) && (cooldowns.size() > 0))
		{
			final Map<Integer, Long> filteredCooldown = new HashMap<>();
			for (Map.Entry<Integer, Long> entry : cooldowns.entrySet())
			{
				if (cooldownPredicate.test(entry.getValue()))
				{
					filteredCooldown.put(entry.getKey(), entry.getValue());
				}
			}
			
			if (filteredCooldown.isEmpty())
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
				
				for (Map.Entry<Integer, Long> entry : filteredCooldown.entrySet())
				{
					st.setInt(1, player.getObjectId());
					st.setInt(2, entry.getKey());
					st.setLong(3, entry.getValue());
					st.addBatch();
				}
				
				st.executeBatch();
				con.commit();
				
			}
			catch (SQLException e)
			{
				log.error("Can't save cooldowns for player " + player.getObjectId());
			}
			finally
			{
				DatabaseFactory.close(st, con);
			}
		}
	}
	
	/**
	 * Removes all cooldown records for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear data associated with the {@code Player}.<br>
	 * It identifies the correct record using the unique object ID of the {@link Player}.
	 * @param player The {@code Player} whose cooldowns should be deleted.
	 */
	private void deletePlayerCooldowns(Player player)
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
