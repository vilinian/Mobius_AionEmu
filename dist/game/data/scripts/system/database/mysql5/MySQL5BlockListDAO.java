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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BlockListDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.BlockList;
import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * Provides database access for managing the {@link BlockList} system using a {@code mysql5} connection.<br>
 * This class handles CRUD operations for {@link BlockedPlayer} records within the database.
 * @author Ben
 */
public class MySQL5BlockListDAO extends BlockListDAO
{
	public static final String LOAD_QUERY = "SELECT blocked_player, reason FROM blocks WHERE player=?";
	public static final String ADD_QUERY = "INSERT INTO blocks (player, blocked_player, reason) VALUES (?, ?, ?)";
	public static final String DEL_QUERY = "DELETE FROM blocks WHERE player=? AND blocked_player=?";
	public static final String SET_REASON_QUERY = "UPDATE blocks SET reason=? WHERE player=? AND blocked_player=?";
	private static Logger log = LoggerFactory.getLogger(MySQL5BlockListDAO.class);
	
	/**
	 * Adds a new entry to the block list for a specific player.<br>
	 * This method saves the blocked object ID and the reason to the database.
	 * @param playerObjId The unique identifier of the player who is doing the blocking.
	 * @param objIdToBlock The unique identifier of the object that should be blocked.
	 * @param reason A string describing why the object was blocked.
	 * @return {@code true} if the database update was successful, otherwise {@code false}.
	 */
	@Override
	public boolean addBlockedUser(int playerObjId, int objIdToBlock, String reason)
	{
		return DB.insertUpdate(ADD_QUERY, stmt ->
		{
			stmt.setInt(1, playerObjId);
			stmt.setInt(2, objIdToBlock);
			stmt.setString(3, reason);
			stmt.execute();
		});
	}
	
	/**
	 * Removes a blocked user from the database.<br>
	 * This method deletes a specific entry based on the owner and the target ID.
	 * @param playerObjId The unique identifier of the player who owns the block list.
	 * @param objIdToDelete The unique identifier of the object to be removed from the block list.
	 * @return {@code true} if the deletion was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean delBlockedUser(int playerObjId, int objIdToDelete)
	{
		return DB.insertUpdate(DEL_QUERY, stmt ->
		{
			stmt.setInt(1, playerObjId);
			stmt.setInt(2, objIdToDelete);
			stmt.execute();
		});
	}
	
	/**
	 * Loads the block list for a specific {@link Player}.<br>
	 * This method retrieves all blocked players and their reasons from the database.<br>
	 * It returns a new {@code BlockList} containing the results.
	 * @param player The {@code Player} whose block list needs to be loaded.
	 * @return A {@code BlockList} object populated with data from the database.
	 */
	@Override
	public BlockList load(Player player)
	{
		final Map<Integer, BlockedPlayer> list = new HashMap<>();
		
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				final PlayerDAO playerDao = DAOManager.getDAO(PlayerDAO.class);
				while (rset.next())
				{
					final int blockedOid = rset.getInt("blocked_player");
					final PlayerCommonData pcd = playerDao.loadPlayerCommonData(blockedOid);
					if (pcd == null)
					{
						log.error("Attempt to load block list for " + player.getName() + " tried to load a player which does not exist: " + blockedOid);
					}
					else
					{
						list.put(blockedOid, new BlockedPlayer(pcd, rset.getString("reason")));
					}
				}
				
			}
			
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
		});
		
		return new BlockList(list);
	}
	
	/**
	 * Updates the block reason for a specific player.<br>
	 * This method modifies an existing entry in the database.
	 * @param playerObjId The unique ID of the player who performed the action.
	 * @param blockedPlayerObjId The unique ID of the player who was blocked.
	 * @param reason The new text description for the block.
	 * @return {@code true} if the update was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean setReason(int playerObjId, int blockedPlayerObjId, String reason)
	{
		return DB.insertUpdate(SET_REASON_QUERY, stmt ->
		{
			stmt.setString(1, reason);
			stmt.setInt(2, playerObjId);
			stmt.setInt(3, blockedPlayerObjId);
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
