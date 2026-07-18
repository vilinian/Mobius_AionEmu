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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.AtreianPassportDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;

/**
 * This class provides the {@code MySQL5} specific implementation for the {@link AtreianPassportDAO} class.<br>
 * It handles database operations related to atreian passports using {@code MySQL5} syntax.
 * @author FrozenKiller
 */
public class MySQL5AtreianPassportDAO extends AtreianPassportDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5AtreianPassportDAO.class);
	
	/**
	 * Adds a new passport record to the database.<br>
	 * This method saves the passport details for a specific account.
	 * @param accountId The unique identifier for the user account.
	 * @param passportId The unique identifier for the passport.
	 * @param stamps The number of stamps collected on this passport.
	 * @param last_stamp The timestamp of the most recent stamp received.
	 */
	@Override
	public void insertPassport(int accountId, int passportId, int stamps, Timestamp last_stamp)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("INSERT INTO `atreian_passports` (`account_id`, `passport_id`, `stamps`, `last_stamp`) VALUES (?,?,?,?)");
			stmt.setInt(1, accountId);
			stmt.setInt(2, passportId);
			stmt.setInt(3, stamps);
			stmt.setTimestamp(4, last_stamp);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Can't insert into passports: " + e.getMessage());
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the passport information for a specific account.<br>
	 * This method modifies the stamp count, reward status, and timestamp in the database.
	 * @param accountId The unique identifier for the user account.
	 * @param passportId The unique identifier for the specific passport.
	 * @param stamps The new number of stamps to assign.
	 * @param rewarded A {@code true} or {@code false} value indicating if a reward was given.
	 * @param last_stamp The timestamp of the most recent stamp activity.
	 */
	@Override
	public void updatePassport(int accountId, int passportId, int stamps, boolean rewarded, Timestamp last_stamp)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE atreian_passports SET stamps = ?, rewarded = ?, last_stamp = ? WHERE account_id = ? AND passport_id = ?");
			
			stmt.setInt(1, stamps);
			stmt.setInt(2, rewarded ? 1 : 0);
			stmt.setTimestamp(3, last_stamp);
			stmt.setInt(4, accountId);
			stmt.setInt(5, passportId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error updating passports ", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves the number of stamps for a specific passport.<br>
	 * This method looks up the record using the provided account and passport IDs.<br>
	 * It returns 0 if no record is found or an error occurs.
	 * @param accountId The unique identifier for the user account.
	 * @param passportId The unique identifier for the specific passport.
	 * @return The total number of stamps as an {@code int}.
	 */
	@Override
	public int getStamps(int accountId, int passportId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT * FROM atreian_passports WHERE account_id = ? AND passport_id = ?");
		try
		{
			s.setInt(1, accountId);
			s.setInt(2, passportId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("stamps");
		}
		catch (SQLException e)
		{
			return 0;
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Retrieves the most recent stamp time for a specific passport.<br>
	 * This method queries the database using the provided account and passport identifiers.<br>
	 * If an error occurs, it returns the current system time.
	 * @param accountId The unique identifier for the user account.
	 * @param passportId The unique identifier for the specific passport.
	 * @return A {@code Timestamp} representing the last stamp recorded.
	 */
	@Override
	public Timestamp getLastStamp(int accountId, int passportId)
	{
		final PreparedStatement s = DB.prepareStatement("SELECT last_stamp FROM atreian_passports WHERE account_id = ? AND passport_id = ?");
		try
		{
			s.setInt(1, accountId);
			s.setInt(2, passportId);
			final ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getTimestamp("last_stamp");
		}
		catch (SQLException e)
		{
			log.error("Can't get last Passport Stamp!" + e);
			return new Timestamp(System.currentTimeMillis());
		}
		finally
		{
			DB.close(s);
		}
	}
	
	/**
	 * Retrieves all passport IDs for a specific account.<br>
	 * This method queries the database using the provided {@code accountId}.<br>
	 * It returns a list of integers representing the unique identifiers.
	 * @param accountId The unique ID of the user account to search.
	 * @return A {@code List<Integer>} containing all passport IDs found for the account.
	 */
	@Override
	public List<Integer> getPassports(int accountId)
	{
		final List<Integer> ids = new ArrayList<>();
		
		DB.select("SELECT passport_id FROM atreian_passports WHERE account_id = ?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setInt(1, accountId);
			}
			
			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				while (resultSet.next())
				{
					ids.add(resultSet.getInt("passport_id"));
				}
			}
		});
		
		return ids;
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
