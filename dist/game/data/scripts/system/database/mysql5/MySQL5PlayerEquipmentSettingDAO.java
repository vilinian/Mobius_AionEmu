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
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerEquipmentSettingDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.equipmentsetting.EquipmentSetting;
import com.aionemu.gameserver.model.gameobjects.player.equipmentsetting.EquipmentSettingList;

/**
 * This class provides the database access layer for managing player equipment settings using {@code MySQL5}.<br>
 * It handles the persistence of {@link EquipmentSettingList} data to and from the database.<br>
 * It extends {@link PlayerEquipmentSettingDAO} to implement specific SQL queries.
 * @author Falke_34
 */
public class MySQL5PlayerEquipmentSettingDAO extends PlayerEquipmentSettingDAO
{
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(PlayerEquipmentSettingDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_equipment_setting` (`player_id`, `slot`, `display`, `m_hand`, `s_hand`, `helmet`, `torso`, `glove`, `boots`, `earrings_left`, `earrings_right`, `ring_left`, `ring_right`, `necklace`, `shoulder`, `pants`, `powershard_left`, `powershard_right`, `wings`, `waist`, `m_off_hand`, `s_off_hand`, `plume`, `bracelet`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE display = values(display), m_hand = values(m_hand), s_hand=values(s_hand), helmet=values(helmet), torso=values(torso), glove=values(glove), boots=values(boots), earrings_left=values(earrings_left), earrings_right=values(earrings_right), ring_left=values(ring_left), ring_right=values(ring_right), necklace=values(necklace), shoulder=values(shoulder), pants=values(pants), powershard_left=values(powershard_left), powershard_right=values(powershard_right), wings=values(wings), waist=values(waist), m_off_hand=values(m_off_hand), s_off_hand=values(s_off_hand),plume=values(plume), bracelet=values(bracelet)";
	public static final String SELECT_QUERY = "SELECT *  FROM `player_equipment_setting` WHERE `player_id`=?";
	
	/**
	 * Loads the equipment settings for a specific player from the database.<br>
	 * This method retrieves all slot data and updates the {@link Player} object.<br>
	 * It handles the connection to the database and populates an {@link EquipmentSettingList}.
	 * @param player The {@code Player} whose equipment settings need to be loaded.
	 */
	@Override
	public void loadEquipmentSetting(Player player)
	{
		final EquipmentSettingList equipmentSettingList = new EquipmentSettingList(player);
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				final int slot = rset.getInt("slot");
				final int display = rset.getInt("display");
				final int mHand = rset.getInt("m_hand");
				final int sHand = rset.getInt("s_hand");
				final int helmet = rset.getInt("helmet");
				final int torso = rset.getInt("torso");
				final int glove = rset.getInt("glove");
				final int boots = rset.getInt("boots");
				final int earringsLeft = rset.getInt("earrings_left");
				final int earringsRight = rset.getInt("earrings_right");
				final int ringLeft = rset.getInt("ring_left");
				final int ringRight = rset.getInt("ring_right");
				final int necklace = rset.getInt("necklace");
				final int shoulder = rset.getInt("shoulder");
				final int pants = rset.getInt("pants");
				final int powershardLeft = rset.getInt("powershard_left");
				final int powershardRight = rset.getInt("powershard_right");
				final int wings = rset.getInt("wings");
				final int waist = rset.getInt("waist");
				final int mOffHand = rset.getInt("m_off_hand");
				final int sOffHand = rset.getInt("s_off_hand");
				final int plume = rset.getInt("plume");
				final int bracelet = rset.getInt("bracelet");
				equipmentSettingList.add(slot, display, mHand, sHand, helmet, torso, glove, boots, earringsLeft, earringsRight, ringLeft, ringRight, necklace, shoulder, pants, powershardLeft, powershardRight, wings, waist, mOffHand, sOffHand, plume, bracelet, false);
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not restore equipment setting for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		player.setEquipmentSettingList(equipmentSettingList);
	}
	
	/**
	 * Saves the equipment settings for a specific player to the database.<br>
	 * This method uses {@code INSERT_QUERY} to store the data.<br>
	 * If the record already exists, it updates the display values.
	 * @param player The {@link Player} object whose ID will be used as the primary key.
	 * @param equipmentSetting The {@link EquipmentSetting} object containing the data to save.
	 */
	@Override
	public void insertEquipmentSetting(Player player, EquipmentSetting equipmentSetting)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, equipmentSetting.getSlot());
			stmt.setInt(3, equipmentSetting.getDisplay());
			stmt.setInt(4, equipmentSetting.getmHand());
			stmt.setInt(5, equipmentSetting.getsHand());
			stmt.setInt(6, equipmentSetting.getHelmet());
			stmt.setInt(7, equipmentSetting.getTorso());
			stmt.setInt(8, equipmentSetting.getGlove());
			stmt.setInt(9, equipmentSetting.getBoots());
			stmt.setInt(10, equipmentSetting.getEarringsLeft());
			stmt.setInt(11, equipmentSetting.getEarringsRight());
			stmt.setInt(12, equipmentSetting.getRingLeft());
			stmt.setInt(13, equipmentSetting.getRingRight());
			stmt.setInt(14, equipmentSetting.getNecklace());
			stmt.setInt(15, equipmentSetting.getShoulder());
			stmt.setInt(16, equipmentSetting.getPants());
			stmt.setInt(17, equipmentSetting.getPowershardLeft());
			stmt.setInt(18, equipmentSetting.getPowershardRight());
			stmt.setInt(19, equipmentSetting.getWings());
			stmt.setInt(20, equipmentSetting.getWaist());
			stmt.setInt(21, equipmentSetting.getmOffHand());
			stmt.setInt(22, equipmentSetting.getsOffHand());
			stmt.setInt(23, equipmentSetting.getPlume());
			stmt.setInt(24, equipmentSetting.getBracelet());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not store equipment setting for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
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
