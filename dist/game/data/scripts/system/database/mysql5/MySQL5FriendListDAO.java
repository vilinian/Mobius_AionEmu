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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * This class provides the database access object for managing friend lists using a {@code mysql5} database.<br>
 * It handles all CRUD operations related to {@link Friend} and {@link FriendList} entities.
 * @author Ben
 */
public class MySQL5FriendListDAO extends FriendListDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5FriendListDAO.class);
	public static final String LOAD_QUERY = "SELECT * FROM `friends` WHERE `player`=?";
	public static final String ADD_QUERY = "INSERT INTO `friends` (`player`,`friend`) VALUES (?, ?)";
	public static final String DEL_QUERY = "DELETE FROM `friends` WHERE `player` = ? AND `friend` = ?";
	public static final String SET_NOTE = "UPDATE `friends` SET `note` = ? WHERE `player` = ? AND `friend` = ?";
	
	/**
	 * Retrieves the list of friends for a specific player from the database.<br>
	 * This method populates a {@link FriendList} object with all associated friend data.
	 * @param player The {@code Player} whose friend list needs to be loaded.
	 * @return A new {@link FriendList} containing the retrieved friends.
	 */
	@Override
	public FriendList load(Player player)
	{
		final List<Friend> friends = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(LOAD_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			final PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
			while (rset.next())
			{
				final int objId = rset.getInt("friend");
				
				final PlayerCommonData pcd = dao.loadPlayerCommonData(objId);
				if (pcd != null)
				{
					final Friend friend = new Friend(pcd);
					friends.add(friend);
				}
			}
		}
		catch (Exception e)
		{
			log.error("Could not restore QuestStateList data for player: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return new FriendList(player, friends);
	}
	
	/**
	 * Adds a mutual friendship between two players.<br>
	 * This method creates entries for both users in the database.<br>
	 * It returns {@code true} if the operation succeeds.
	 * @param player The first {@link Player} to add a friend.
	 * @param friend The second {@link Player} being added as a friend.
	 * @return {@code true} if the friendship was successfully saved, otherwise {@code false}.
	 */
	@Override
	public boolean addFriends(Player player, Player friend)
	{
		return DB.insertUpdate(ADD_QUERY, ps ->
		{
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, friend.getObjectId());
			ps.addBatch();
			
			ps.setInt(1, friend.getObjectId());
			ps.setInt(2, player.getObjectId());
			ps.addBatch();
			
			ps.executeBatch();
		});
	}
	
	/**
	 * Removes a friendship between two players from the database.<br>
	 * This method deletes both entries for the pair to ensure mutual removal.
	 * @param playerOid The unique identifier of the first player.
	 * @param friendOid The unique identifier of the second player.
	 * @return {@code true} if the operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean delFriends(int playerOid, int friendOid)
	{
		return DB.insertUpdate(DEL_QUERY, ps ->
		{
			ps.setInt(1, playerOid);
			ps.setInt(2, friendOid);
			ps.addBatch();
			
			ps.setInt(1, friendOid);
			ps.setInt(2, playerOid);
			ps.addBatch();
			
			ps.executeBatch();
		});
	}
	
	/**
	 * Updates the personal note for a specific friend.<br>
	 * This method saves the {@code note} string to the database.<br>
	 * It identifies the correct record using {@code playerId} and {@code friendId}.
	 * @param playerId The unique identifier of the player.
	 * @param friendId The unique identifier of the friend.
	 * @param note The text content to save as a note.
	 */
	@Override
	public void setFriendNote(int playerId, int friendId, String note)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SET_NOTE);
			stmt.setString(1, note);
			stmt.setInt(2, playerId);
			stmt.setInt(3, friendId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
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
