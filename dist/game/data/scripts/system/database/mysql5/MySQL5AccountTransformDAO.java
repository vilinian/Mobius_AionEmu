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
import com.aionemu.gameserver.dao.AccountTransformDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;

/**
 * This class provides the {@code MySQL5} database implementation for handling account transformations.<br>
 * It extends {@link AccountTransformDAO} to perform specific data operations on {@link AccountTransfo} objects.
 */
public class MySQL5AccountTransformDAO extends AccountTransformDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5AccountTransformDAO.class);
	private static final String LOAD_QUERY = "SELECT * FROM `account_transform` WHERE `account_id`=?";
	private static final String INSERT_QUERY = "INSERT INTO `account_transform`(`account_id`,`card_id`, `count`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE account_transform set `count`=? WHERE `account_id`=? AND `card_id`=?";
	
	/**
	 * Retrieves all transformations associated with a specific account.<br>
	 * This method queries the database using the {@code account_id}.<br>
	 * It returns a map where the key is the card ID and the value is the transformation object.
	 * @param account The {@link Account} object used to identify which records to load.
	 * @return A {@link Map} containing all {@link AccountTransfo} objects for the given account.
	 */
	@Override
	public Map<Integer, AccountTransfo> loadAccountTransfo(Account account)
	{
		final Map<Integer, AccountTransfo> tl = new HashMap<>();
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, account.getId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int id = rset.getInt("card_id");
					final int count = rset.getInt("count");
					tl.put(id, new AccountTransfo(id, count));
				}
			}
		});
		
		return tl;
	}
	
	/**
	 * Adds a new transform record to the database for a specific account.<br>
	 * This method saves the {@code AccountTransfo} data using an insert query.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the database operation.
	 * @param account The {@link Account} object that owns the transform.
	 * @param trans The {@link AccountTransfo} object containing the transform details to save.
	 * @return {@code true} if the record was added successfully, otherwise {@code false}.
	 */
	@Override
	public boolean addTransfo(Account account, AccountTransfo trans)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, account.getId());
			stmt.setInt(2, trans.getCardId());
			stmt.setInt(3, trans.getCount());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store transform book for account " + account.getName() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Updates the count of a specific transform card for an account.<br>
	 * This method modifies the data in the database using {@code UPDATE_QUERY}.
	 * @param account The {@code Account} object containing the unique ID.
	 * @param transfo The {@code AccountTransfo} object containing the new count and card ID.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateTransfo(Account account, AccountTransfo transfo)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, transfo.getCount());
			stmt.setInt(2, account.getId());
			stmt.setInt(3, transfo.getCardId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not update Transform Card data for Account " + account.getName() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes a specific transformation from an account.<br>
	 * This method deletes the record matching the provided {@code id} for the given {@link Account}.
	 * @param account The {@code Account} object that owns the transformation.
	 * @param id The unique identifier of the card or transformation to delete.
	 */
	@Override
	public void deleteTransfo(Account account, int id)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("DELETE FROM account_transform WHERE account_id = ? AND card_id = ?");
			stmt.setInt(1, account.getId());
			stmt.setInt(2, id);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error removing transformation #" + id, e);
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
