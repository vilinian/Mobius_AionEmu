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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PetitionDAO;
import com.aionemu.gameserver.model.Petition;
import com.aionemu.gameserver.model.PetitionStatus;

/**
 * This class provides the database access object for handling {@link Petition} records.<br>
 * It implements specific SQL queries to manage petitions using a {@code mysql5} database.<br>
 * It extends {@link PetitionDAO} to provide specialized data persistence logic.
 * @author zdead
 */
public class MySQL5PetitionDAO extends PetitionDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PetitionDAO.class);
	
	/**
	 * Retrieves the next unique identifier for a new petition.<br>
	 * This method queries the database to find the current maximum ID.<br>
	 * It then increments that value by {@code 1}.
	 * @return The next available integer ID, or {@code 0} if an error occurs.
	 */
	@Override
	public synchronized int getNextAvailableId()
	{
		Connection con = null;
		int result = 0;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT MAX(id) as nextid FROM petitions");
			final ResultSet rset = stmt.executeQuery();
			rset.next();
			result = rset.getInt("nextid") + 1;
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot get next available petition id", e);
			return 0;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return result;
	}
	
	/**
	 * Retrieves a {@link Petition} object from the database using its unique identifier.<br>
	 * This method searches for a record matching the provided {@code petitionId}.<br>
	 * It returns {@code null} if no matching petition is found or if an error occurs.
	 * @param petitionId The unique ID of the petition to retrieve.
	 * @return The {@link Petition} object, or {@code null} if not found.
	 */
	@Override
	public Petition getPetitionById(int petitionId)
	{
		final String query = "SELECT * FROM petitions WHERE id = ?";
		Connection con = null;
		Petition result = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petitionId);
			final ResultSet rset = stmt.executeQuery();
			if (!rset.next())
			{
				return null;
			}
			
			final String statusValue = rset.getString("status");
			PetitionStatus status;
			if (statusValue.equals("PENDING"))
			{
				status = PetitionStatus.PENDING;
			}
			else if (statusValue.equals("IN_PROGRESS"))
			{
				status = PetitionStatus.IN_PROGRESS;
			}
			else
			{
				status = PetitionStatus.PENDING;
			}
			
			result = new Petition(rset.getInt("id"), rset.getInt("player_id"), rset.getInt("type"), rset.getString("title"), rset.getString("message"), rset.getString("add_data"), status.getElementId());
			
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot get petition #" + petitionId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return result;
	}
	
	/**
	 * Retrieves all active petitions from the database.<br>
	 * This includes petitions with a status of {@code PENDING} or {@code IN_PROGRESS}.<br>
	 * The results are ordered by their unique ID in ascending order.
	 * @return A {@link Set} of {@link Petition} objects, or {@code null} if an error occurs.
	 */
	@Override
	public Set<Petition> getPetitions()
	{
		final String query = "SELECT * FROM petitions WHERE status = 'PENDING' OR status = 'IN_PROGRESS' ORDER BY id ASC";
		Connection con = null;
		final Set<Petition> results = new HashSet<>();
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(query);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final String statusValue = rset.getString("status");
				PetitionStatus status;
				if (statusValue.equals("PENDING"))
				{
					status = PetitionStatus.PENDING;
				}
				else if (statusValue.equals("IN_PROGRESS"))
				{
					status = PetitionStatus.IN_PROGRESS;
				}
				else
				{
					status = PetitionStatus.PENDING;
				}
				
				final Petition p = new Petition(rset.getInt("id"), rset.getInt("player_id"), rset.getInt("type"), rset.getString("title"), rset.getString("message"), rset.getString("add_data"), status.getElementId());
				results.add(p);
			}
			
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot get next available petition id", e);
			return null;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return results;
	}
	
	/**
	 * Removes petitions from the database for a specific player.<br>
	 * It only deletes records with a status of {@code PENDING} or {@code IN_PROGRESS}.<br>
	 * This method interacts with the underlying MySQL table via {@link DatabaseFactory}.
	 * @param playerObjId The unique identifier of the player whose petitions should be removed.
	 */
	@Override
	public void deletePetition(int playerObjId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("DELETE FROM petitions WHERE player_id = ? AND (status = 'PENDING' OR status='IN_PROGRESS')");
			stmt.setInt(1, playerObjId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot delete petition", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves a new {@link Petition} object into the database.<br>
	 * This method executes an {@code INSERT} query to store all petition details.<br>
	 * It handles the connection and statement lifecycle automatically.
	 * @param petition The {@code Petition} object containing the data to save.
	 */
	@Override
	public void insertPetition(Petition petition)
	{
		Connection con = null;
		final String query = "INSERT INTO petitions (id, player_id, type, title, message, add_data, time, status) VALUES(?,?,?,?,?,?,?,?)";
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petition.getPetitionId());
			stmt.setInt(2, petition.getPlayerObjId());
			stmt.setInt(3, petition.getPetitionType().getElementId());
			stmt.setString(4, petition.getTitle());
			stmt.setString(5, petition.getContentText());
			stmt.setString(6, petition.getAdditionalData());
			stmt.setLong(7, new Date().getTime() / 1000);
			stmt.setString(8, petition.getStatus().toString());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot insert petition", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the status of a specific petition to {@code REPLIED}.<br>
	 * This method modifies the database record associated with the provided ID.
	 * @param petitionId The unique identifier of the petition to update.
	 */
	@Override
	public void setReplied(int petitionId)
	{
		Connection con = null;
		final String query = "UPDATE petitions SET status = 'REPLIED' WHERE id = ?";
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petitionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Cannot set petition replied", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
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
