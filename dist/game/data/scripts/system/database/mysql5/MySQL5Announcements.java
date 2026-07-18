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

import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.dao.AnnouncementsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.Announcement;

/**
 * This class provides the database implementation for handling {@link Announcement} objects.<br>
 * It extends {@link AnnouncementsDAO} to support operations specifically for {@code mysql5} databases.
 * @author Divinity
 */
public class MySQL5Announcements extends AnnouncementsDAO
{
	/**
	 * Retrieves all announcements from the database.<br>
	 * The results are ordered by their {@code id}.
	 * @return A {@link Set} containing all {@link Announcement} objects.
	 */
	@Override
	public Set<Announcement> getAnnouncements()
	{
		final Set<Announcement> result = new HashSet<>();
		DB.select("SELECT * FROM announcements ORDER BY id", resultSet ->
		{
			while (resultSet.next())
			{
				result.add(new Announcement(resultSet.getInt("id"), resultSet.getString("announce"), resultSet.getString("faction"), resultSet.getString("type"), resultSet.getInt("delay")));
			}
		});
		
		return result;
	}
	
	/**
	 * Adds a new announcement to the database.<br>
	 * This method saves the data provided in the {@code Announcement} object.<br>
	 * It uses the {@link DB} class to perform the insert operation.
	 * @param announce The {@code Announcement} object containing the data to save.
	 */
	@Override
	public void addAnnouncement(Announcement announce)
	{
		DB.insertUpdate("INSERT INTO announcements (announce, faction, type, delay) VALUES (?, ?, ?, ?)", preparedStatement ->
		{
			preparedStatement.setString(1, announce.getAnnounce());
			preparedStatement.setString(2, announce.getFaction());
			preparedStatement.setString(3, announce.getType());
			preparedStatement.setInt(4, announce.getDelay());
			preparedStatement.execute();
		});
	}
	
	/**
	 * Deletes an announcement from the database.<br>
	 * This method uses the provided {@code idAnnounce} to locate the record.<br>
	 * It returns {@code true} if the operation was successful.
	 * @param idAnnounce The unique identifier of the announcement to remove.
	 * @return {@code true} if the deletion succeeded, otherwise {@code false}.
	 */
	@Override
	public boolean delAnnouncement(int idAnnounce)
	{
		return DB.insertUpdate("DELETE FROM announcements WHERE id = ?", preparedStatement ->
		{
			preparedStatement.setInt(1, idAnnounce);
			preparedStatement.execute();
		});
	}
	
	/**
	 * Checks if the current database system supports a specific feature.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param s The name of the feature to check.
	 * @param i The first integer parameter for the feature.
	 * @param i1 The second integer parameter for the feature.
	 * @return {@code true} if the feature is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
