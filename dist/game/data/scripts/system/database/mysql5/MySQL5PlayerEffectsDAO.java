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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerEffectsDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * This class provides the {@code MySQL5} database implementation for handling player effects.<br>
 * It extends {@link PlayerEffectsDAO} to manage data persistence using {@code SQL} queries.
 * @author ATracer
 */
public class MySQL5PlayerEffectsDAO extends PlayerEffectsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerEffectsDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_effects` (`player_id`, `skill_id`, `skill_lvl`, `current_time`, `end_time`) VALUES (?,?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_effects` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `skill_id`, `skill_lvl`, `current_time`, `end_time` FROM `player_effects` WHERE `player_id`=?";
	private static final Predicate<Effect> insertableEffectsPredicate = (Effect input) -> (input != null) && (input.getRemainingTime() > 28000);
	
	/**
	 * Loads all active effects for a specific player from the database.<br>
	 * This method populates the {@link Player} effect controller with saved data.<br>
	 * It also triggers a broadcast of these effects to the game world.
	 * @param player The {@code Player} object whose effects need to be loaded.
	 */
	@Override
	public void loadPlayerEffects(Player player)
	{
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int skillId = rset.getInt("skill_id");
					final int skillLvl = rset.getInt("skill_lvl");
					final int remainingTime = rset.getInt("current_time");
					final long endTime = rset.getLong("end_time");
					
					if (remainingTime > 0)
					{
						player.getEffectController().addSavedEffect(skillId, skillLvl, remainingTime, endTime);
					}
				}
			}
		});
		player.getEffectController().broadCastEffects();
	}
	
	/**
	 * Saves the current active effects for a specific {@link Player}.<br>
	 * This method removes old data and inserts new valid effects into the database.<br>
	 * It uses a batch process to improve performance during the save operation.
	 * @param player The {@code Player} object whose effects need to be stored.
	 */
	@Override
	public void storePlayerEffects(Player player)
	{
		deletePlayerEffects(player);
		
		final List<Effect> effects = new ArrayList<>();
		for (Iterator<Effect> it = player.getEffectController().iterator(); it.hasNext();)
		{
			final Effect effect = it.next();
			if (insertableEffectsPredicate.test(effect))
			{
				effects.add(effect);
			}
		}
		
		if (effects.isEmpty())
		{
			return;
		}
		
		Connection con = null;
		PreparedStatement ps = null;
		try
		{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(INSERT_QUERY);
			
			for (Effect effect : effects)
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, effect.getSkillId());
				ps.setInt(3, effect.getSkillLevel());
				ps.setInt(4, effect.getRemainingTime());
				ps.setLong(5, effect.getEndTime());
				ps.addBatch();
			}
			
			ps.executeBatch();
			con.commit();
		}
		catch (SQLException e)
		{
			log.error("Exception while saving effects of player " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(ps, con);
		}
	}
	
	/**
	 * Removes all active effects for a specific player from the database.<br>
	 * This method uses the {@code DELETE_QUERY} to clear records associated with the {@code Player}.
	 * @param player The {@link Player} object whose effects need to be deleted.
	 */
	private void deletePlayerEffects(Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt ->
		{
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
	
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
}
