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
import com.aionemu.gameserver.dao.MotionDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerEmotionListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.model.gameobjects.player.motion.MotionList;

/**
 * This class provides the database access layer for handling {@link Motion} data using a {@code mysql5} backend.<br>
 * It extends {@link MotionDAO} to implement specific queries and operations required by the game server.
 * @author MrPoke
 */
public class MySQL5MotionDAO extends MotionDAO
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(PlayerEmotionListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_motions` (`player_id`, `motion_id`, `active`,  `time`) VALUES (?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT `motion_id`, `active`, `time` FROM `player_motions` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_motions` WHERE `player_id`=? AND `motion_id`=?";
	public static final String UPDATE_QUERY = "UPDATE `player_motions` SET `active`=? WHERE `player_id`=? AND `motion_id`=?";
	
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
	
	/**
	 * Loads the motion list for a specific player from the database.<br>
	 * This method populates the {@link MotionList} for the provided {@code Player}.<br>
	 * It retrieves all active and inactive motions associated with the player's ID.
	 * @param player The {@code Player} object whose motions need to be loaded.
	 */
	@Override
	public void loadMotionList(Player player)
	{
		Connection con = null;
		final MotionList motions = new MotionList(player);
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int motionId = rset.getInt("motion_id");
				final int time = rset.getInt("time");
				final boolean isActive = rset.getBoolean("active");
				motions.add(new Motion(motionId, time, isActive), false);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore motions for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		player.setMotions(motions);
	}
	
	/**
	 * Saves a new motion record to the database.<br>
	 * This method uses the {@code INSERT_QUERY} to store data.<br>
	 * It returns {@code true} if the operation succeeds.<br>
	 * It returns {@code false} if an error occurs during the process.
	 * @param objectId The unique identifier for the player or object.
	 * @param motion The {@link Motion} object containing the data to save.
	 * @return {@code true} if saved successfully, otherwise {@code false}.
	 */
	@Override
	public boolean storeMotion(int objectId, Motion motion)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, motion.getId());
			stmt.setBoolean(3, motion.isActive());
			stmt.setInt(4, motion.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Removes a specific motion from the database for a given object.<br>
	 * This method uses {@code DELETE_QUERY} to perform the operation.
	 * @param objectId The unique identifier of the player or object.
	 * @param motionId The unique identifier of the motion to remove.
	 * @return {@code true} if the deletion was successful, {@code false} otherwise.
	 */
	@Override
	public boolean deleteMotion(int objectId, int motionId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, motionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not delete motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
	
	/**
	 * Updates the active status of a specific motion for an object.<br>
	 * This method modifies the record in the database using the provided {@code Motion} data.
	 * @param objectId The unique identifier of the player or object.
	 * @param motion The {@link Motion} object containing the new state to save.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean updateMotion(int objectId, Motion motion)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setBoolean(1, motion.isActive());
			stmt.setInt(2, objectId);
			stmt.setInt(3, motion.getId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
	}
}
