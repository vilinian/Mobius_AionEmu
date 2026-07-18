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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerPunishmentsDAO;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;

/**
 * This class provides the database access layer for managing player punishments in a {@code mysql5} environment.<br>
 * It handles CRUD operations for character bans and other restrictions by extending {@link PlayerPunishmentsDAO}.
 * @author lord_rex, Cura, nrg
 */
public class MySQL5PlayerPunishmentsDAO extends PlayerPunishmentsDAO
{
	public static final String SELECT_QUERY = "SELECT `player_id`, `start_time`, `duration`, `reason` FROM `player_punishments` WHERE `player_id`=? AND `punishment_type`=?";
	public static final String UPDATE_QUERY = "UPDATE `player_punishments` SET `duration`=? WHERE `player_id`=? AND `punishment_type`=?";
	public static final String REPLACE_QUERY = "REPLACE INTO `player_punishments` VALUES (?,?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_punishments` WHERE `player_id`=? AND `punishment_type`=?";
	
	/**
	 * This method loads punishment data from the database for a specific player.<br>
	 * It updates the {@code Player} object with timers based on the {@code PunishmentType}.
	 * @param player The {@link Player} whose punishments need to be loaded.
	 * @param punishmentType The type of punishment to check in the database.
	 */
	@Override
	public void loadPlayerPunishments(Player player, PunishmentType punishmentType)
	{
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, player.getObjectId());
				ps.setString(2, punishmentType.toString());
			}
			
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					if (punishmentType == PunishmentType.PRISON)
					{
						player.setPrisonTimer(rs.getLong("duration") * 1000);
					}
					else if (punishmentType == PunishmentType.GATHER)
					{
						player.setGatherableTimer(rs.getLong("duration") * 1000);
					}
				}
			}
		});
	}
	
	/**
	 * Saves the current punishment data for a specific player to the database.<br>
	 * This method updates the duration based on the {@code PunishmentType}.
	 * @param player The {@link Player} object whose data needs to be saved.
	 * @param punishmentType The type of {@link PunishmentType} being updated.
	 */
	@Override
	public void storePlayerPunishments(Player player, PunishmentType punishmentType)
	{
		DB.insertUpdate(UPDATE_QUERY, ps ->
		{
			if (punishmentType == PunishmentType.PRISON)
			{
				ps.setLong(1, player.getPrisonTimer() / 1000);
			}
			else if (punishmentType == PunishmentType.GATHER)
			{
				ps.setLong(1, (player.getGatherableTimer() - (System.currentTimeMillis() - player.getStopGatherable())) / 1000);
			}
			
			ps.setInt(2, player.getObjectId());
			ps.setString(3, punishmentType.toString());
			ps.execute();
		});
	}
	
	/**
	 * Applies a punishment to a specific player in the database.<br>
	 * This method updates or inserts the punishment record using {@code REPLACE_QUERY}.
	 * @param playerId The unique identifier of the player to punish.
	 * @param punishmentType The type of punishment to apply from {@link PunishmentType}.
	 * @param duration The length of time the punishment should last in seconds.
	 * @param reason A text description explaining why the punishment was issued.
	 */
	@Override
	public void punishPlayer(int playerId, PunishmentType punishmentType, long duration, String reason)
	{
		DB.insertUpdate(REPLACE_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setString(2, punishmentType.toString());
			ps.setLong(3, System.currentTimeMillis() / 1000);
			ps.setLong(4, duration);
			ps.setString(5, reason);
			ps.execute();
		});
	}
	
	/**
	 * Applies a specific punishment to a {@link Player}.<br>
	 * This method handles the logic for different {@link PunishmentType} values.<br>
	 * It updates the database with the provided reason and duration.
	 * @param player The {@code Player} object to be punished.
	 * @param punishmentType The type of {@link PunishmentType} to apply.
	 * @param reason A string describing why the player is being punished.
	 */
	@Override
	public void punishPlayer(Player player, PunishmentType punishmentType, String reason)
	{
		if (punishmentType == PunishmentType.PRISON)
		{
			punishPlayer(player.getObjectId(), punishmentType, player.getPrisonTimer() / 1000, reason);
		}
		else if (punishmentType == PunishmentType.GATHER)
		{
			punishPlayer(player.getObjectId(), punishmentType, player.getGatherableTimer() / 1000, reason);
		}
	}
	
	/**
	 * Removes a specific punishment from a player's record.<br>
	 * This method uses the {@code DELETE_QUERY} to clear the data.
	 * @param playerId The unique ID of the player to unpunish.
	 * @param punishmentType The type of punishment to remove.
	 */
	@Override
	public void unpunishPlayer(int playerId, PunishmentType punishmentType)
	{
		DB.insertUpdate(DELETE_QUERY, ps ->
		{
			ps.setInt(1, playerId);
			ps.setString(2, punishmentType.toString());
			ps.execute();
		});
	}
	
	/**
	 * Retrieves the ban information for a specific character.<br>
	 * This method looks up data in the database using the {@code playerId}.<br>
	 * It specifically checks for the {@code CHARBAN} type.
	 * @param playerId The unique identifier of the player to check.
	 * @return A {@code CharacterBanInfo} object containing the ban details, or {@code null} if no ban exists.
	 */
	@Override
	public CharacterBanInfo getCharBanInfo(int playerId)
	{
		final CharacterBanInfo[] charBan = new CharacterBanInfo[1];
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, playerId);
				ps.setString(2, PunishmentType.CHARBAN.toString());
			}
			
			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next())
				{
					charBan[0] = new CharacterBanInfo(playerId, rs.getLong("start_time"), rs.getLong("duration"), rs.getString("reason"));
				}
			}
		});
		
		return charBan[0];
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
