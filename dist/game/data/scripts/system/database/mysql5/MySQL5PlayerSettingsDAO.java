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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerSettingsDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSettings;

/**
 * This class provides the database access layer for managing {@link PlayerSettings} using a {@code MySQL5} database.<br>
 * It handles all CRUD operations to persist player-specific configuration data.
 * @author ATracer
 */
public class MySQL5PlayerSettingsDAO extends PlayerSettingsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerSettingsDAO.class);
	
	/**
	 * Loads the settings for a specific {@link Player} from the database.<br>
	 * This method retrieves all saved data types and updates the player object.<br>
	 * If an error occurs, it logs the exception to the system logger.
	 * @param player The {@code Player} whose settings need to be loaded.
	 */
	@Override
	public void loadSettings(Player player)
	{
		final int playerId = player.getObjectId();
		final PlayerSettings playerSettings = new PlayerSettings();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM player_settings WHERE player_id = ?");
			statement.setInt(1, playerId);
			final ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
			{
				final int type = resultSet.getInt("settings_type");
				switch (type)
				{
					case 0:
						playerSettings.setUiSettings(resultSet.getBytes("settings"));
						break;
					case 1:
						playerSettings.setShortcuts(resultSet.getBytes("settings"));
						break;
					case 2:
						playerSettings.setHouseBuddies(resultSet.getBytes("settings"));
						break;
					case -1:
						playerSettings.setDisplay(resultSet.getInt("settings"));
						break;
					case -2:
						playerSettings.setDeny(resultSet.getInt("settings"));
						break;
				}
			}
			
			resultSet.close();
			statement.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore PlayerSettings data for player " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		playerSettings.setPersistentState(PersistentState.UPDATED);
		player.setPlayerSettings(playerSettings);
	}
	
	/**
	 * Saves the configuration data for a specific player to the database.<br>
	 * This method checks if the {@code PlayerSettings} have been modified before saving.<br>
	 * It updates various settings including UI, shortcuts, and display options.
	 * @param player The {@link Player} object containing the settings to be saved.
	 */
	@Override
	public void saveSettings(Player player)
	{
		final int playerId = player.getObjectId();
		
		final PlayerSettings playerSettings = player.getPlayerSettings();
		if (playerSettings.getPersistentState() == PersistentState.UPDATED)
		{
			return;
		}
		
		final byte[] uiSettings = playerSettings.getUiSettings();
		final byte[] shortcuts = playerSettings.getShortcuts();
		final byte[] houseBuddies = playerSettings.getHouseBuddies();
		final int display = playerSettings.getDisplay();
		final int deny = playerSettings.getDeny();
		
		if (uiSettings != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", stmt ->
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, 0);
				stmt.setBytes(3, uiSettings);
				stmt.execute();
			});
		}
		
		if (shortcuts != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", stmt ->
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, 1);
				stmt.setBytes(3, shortcuts);
				stmt.execute();
			});
		}
		
		if (houseBuddies != null)
		{
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", stmt ->
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, 2);
				stmt.setBytes(3, houseBuddies);
				stmt.execute();
			});
		}
		
		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", stmt ->
		{
			stmt.setInt(1, playerId);
			stmt.setInt(2, -1);
			stmt.setInt(3, display);
			stmt.execute();
		});
		
		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", stmt ->
		{
			stmt.setInt(1, playerId);
			stmt.setInt(2, -2);
			stmt.setInt(3, deny);
			stmt.execute();
		});
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
