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
import com.aionemu.gameserver.dao.PlayerLumielDAO;
import com.aionemu.gameserver.model.gameobjects.player.LumielTransform;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * This class provides the {@code MySQL5} database implementation for {@link PlayerLumielDAO}.<br>
 * It handles data access operations specifically for Lumiel player records using a {@code MySQL5} backend.
 */
public class MySQL5PlayerLumielDAO extends PlayerLumielDAO
{
	private final Logger log = LoggerFactory.getLogger(MySQL5PlayerLumielDAO.class);
	
	public static final String INSERT_ACHIEVEMENT = "INSERT INTO lumiel_transform (player_id, lumiel_id, points) VALUES (?, ?, ?)";
	public static final String LOAD_QUERY = "SELECT * FROM `lumiel_transform` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE lumiel_transform set points=? WHERE `player_id`=? AND `lumiel_id`=?";
	
	/**
	 * Loads all {@link LumielTransform} data for a specific player from the database.<br>
	 * It retrieves records based on the unique object ID of the {@code player}.
	 * @param player The {@code Player} object whose data needs to be loaded.
	 * @return A {@code Map} where keys are lumiel IDs and values are {@link LumielTransform} objects.
	 */
	@Override
	public Map<Integer, LumielTransform> loadPlayerLumiel(Player player)
	{
		final Map<Integer, LumielTransform> playerLumiels = new HashMap<>();
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
					final LumielTransform lumiel = new LumielTransform(rset.getInt("lumiel_id"), rset.getInt("points"));
					playerLumiels.put(lumiel.getId(), lumiel);
				}
			}
		});
		
		return playerLumiels;
	}
	
	/**
	 * Adds a new {@link LumielTransform} record for a specific {@link Player}.<br>
	 * This method saves the transform data into the database.
	 * @param player The {@code Player} object to associate with the transform.
	 * @param lumielTransform The {@code LumielTransform} data to be saved.
	 * @return {@code true} if the operation succeeded, or {@code false} if a database error occurred.
	 */
	@Override
	public boolean addPlayerLumiel(Player player, LumielTransform lumielTransform)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_ACHIEVEMENT);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, lumielTransform.getId());
			stmt.setLong(3, lumielTransform.getPoints());
			stmt.execute();
			stmt.close();
			return true;
		}
		catch (SQLException e)
		{
			log.error("addLumiel error", e);
			
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the points for a specific {@link LumielTransform} in the database.<br>
	 * This method links the data to a specific {@link Player}.
	 * @param player The {@code Player} object used to identify the owner.
	 * @param lumielTransform The {@code LumielTransform} object containing the new points.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateLumielTransform(Player player, LumielTransform lumielTransform)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setLong(1, lumielTransform.getPoints());
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, lumielTransform.getId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update PlayerLumiel data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
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
}
