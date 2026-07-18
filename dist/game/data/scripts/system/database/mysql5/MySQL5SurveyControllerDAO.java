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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.SurveyControllerDAO;
import com.aionemu.gameserver.model.templates.survey.SurveyItem;

/**
 * This class provides the {@code MySQL5} database implementation for the {@link SurveyControllerDAO}.<br>
 * It handles all data access operations related to survey controllers in a {@code MySQL5} environment.
 * @author KID
 */
public class MySQL5SurveyControllerDAO extends SurveyControllerDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5SurveyControllerDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `surveys` SET `used`=?, used_time=NOW() WHERE `unique_id`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `surveys` WHERE `used`=?";
	
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
	
	/**
	 * Retrieves all survey items that have not been used yet.<br>
	 * This method queries the database for records where the {@code used} status is {@code 0}.<br>
	 * It returns a {@link List} containing the results.
	 * @return A {@code List} of {@link SurveyItem} objects.
	 */
	@Override
	public List<SurveyItem> getAllNew()
	{
		final List<SurveyItem> list = new ArrayList<>();
		
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, 0);
			
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final SurveyItem item = new SurveyItem();
				item.uniqueId = rset.getInt("unique_id");
				item.ownerId = rset.getInt("owner_id");
				item.itemId = rset.getInt("item_id");
				item.count = rset.getLong("item_count");
				item.html = rset.getString("html_text");
				item.radio = rset.getString("html_radio");
				list.add(item);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.warn("getAllNew() from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return list;
	}
	
	/**
	 * Marks a specific survey item as used in the database.<br>
	 * This method updates the {@code used} status to {@code 1}.<br>
	 * It returns {@code true} if the update succeeds.<br>
	 * It returns {@code false} if an error occurs during the process.
	 * @param id The unique identifier of the survey item to use.
	 * @return {@code true} if successful, otherwise {@code false}.
	 */
	@Override
	public boolean useItem(int id)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, 1);
			stmt.setInt(2, id);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("useItem", e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
}
