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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerTitleListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;

/**
 * This class provides the database access layer for managing {@link TitleList} objects in a MySQL 5 environment.<br>
 * It handles all SQL queries related to player titles and extends the functionality of {@link PlayerTitleListDAO}.
 * @author xavier
 */
public class MySQL5PlayerTitleListDAO extends PlayerTitleListDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerTitleListDAO.class);
	private static final String LOAD_QUERY = "SELECT `title_id`, `remaining` FROM `player_titles` WHERE `player_id`=?";
	private static final String INSERT_QUERY = "INSERT INTO `player_titles`(`player_id`,`title_id`, `remaining`) VALUES (?,?,?)";
	private static final String DELETE_QUERY = "DELETE FROM `player_titles` WHERE `player_id`=? AND `title_id` =?;";
	
	/**
	 * Retrieves the list of titles for a specific player.<br>
	 * This method fetches data from the database using the {@code playerId}.<br>
	 * It populates a new {@link TitleList} object with all found entries.
	 * @param playerId The unique identifier of the player to load.
	 * @return A {@link TitleList} containing the player's titles.
	 */
	@Override
	public TitleList loadTitleList(int playerId)
	{
		final TitleList tl = new TitleList();
		
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int id = rset.getInt("title_id");
					final int remaining = rset.getInt("remaining");
					tl.addEntry(id, remaining);
				}
			}
		});
		
		return tl;
	}
	
	/**
	 * Saves a new {@link Title} entry to the database for a specific {@link Player}.<br>
	 * This method handles the SQL insertion and manages the database connection.
	 * @param player The {@link Player} object who will own the title.
	 * @param entry The {@link Title} data to be saved.
	 * @return {@code true} if the save was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean storeTitles(Player player, Title entry)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, entry.getId());
			stmt.setInt(3, entry.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store emotionId for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
	
	/**
	 * Removes a specific title from a player's list in the database.<br>
	 * This method uses {@code DELETE_QUERY} to update the records.
	 * @param playerId The unique identifier of the player.
	 * @param titleId The unique identifier of the title to remove.
	 * @return {@code true} if the operation succeeded, or {@code false} if an error occurred.
	 */
	@Override
	public boolean removeTitle(int playerId, int titleId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, titleId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete title for player " + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
}
