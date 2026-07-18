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

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerLifeStatsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;

/**
 * This class provides the database access layer for handling {@link PlayerLifeStats} data.<br>
 * It implements specific SQL queries to interact with a {@code mysql5} database.<br>
 * It extends {@link PlayerLifeStatsDAO} to provide specialized functionality for player life statistics.
 * @author Mr. Poke
 */
public class MySQL5PlayerLifeStatsDAO extends PlayerLifeStatsDAO
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerLifeStatsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_life_stats` (`player_id`, `hp`, `mp`, `fp`) VALUES (?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT `hp`, `mp`, `fp` FROM `player_life_stats` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_life_stats set `hp`=?, `mp`=?, `fp`=? WHERE `player_id`=?";
	
	/**
	 * Loads the life statistics for a specific {@link Player}.<br>
	 * It retrieves HP, MP, and FP values from the database.<br>
	 * If no data exists, it calls {@code insertPlayerLifeStat} to create a new record.
	 * @param player The {@code Player} object whose stats need to be loaded.
	 */
	@Override
	public void loadPlayerLifeStat(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			if (rset.next())
			{
				final PlayerLifeStats lifeStats = player.getLifeStats();
				lifeStats.setCurrentHp(rset.getInt("hp"));
				lifeStats.setCurrentMp(rset.getInt("mp"));
				lifeStats.setCurrentFp(rset.getInt("fp"));
			}
			else
			{
				insertPlayerLifeStat(player);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore PlayerLifeStat data for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the current life statistics of a {@link Player} to the database.<br>
	 * This method records the HP, MP, and FP values for the given player.<br>
	 * It uses the {@code INSERT_QUERY} to create a new record.
	 * @param player The {@code Player} object containing the stats to save.
	 */
	@Override
	public void insertPlayerLifeStat(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, player.getLifeStats().getCurrentHp());
			stmt.setInt(3, player.getLifeStats().getCurrentMp());
			stmt.setInt(4, player.getLifeStats().getCurrentFp());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store PlayerLifeStat data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the life statistics in the database for a specific player.<br>
	 * This method saves the current {@code hp}, {@code mp}, and {@code fp} values.<br>
	 * It uses the {@code UPDATE_QUERY} to modify the records.
	 * @param player The {@link Player} object containing the updated stats.
	 */
	@Override
	public void updatePlayerLifeStat(Player player)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, player.getLifeStats().getCurrentHp());
			stmt.setInt(2, player.getLifeStats().getCurrentMp());
			stmt.setInt(3, player.getLifeStats().getCurrentFp());
			stmt.setInt(4, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update PlayerLifeStat data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
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
