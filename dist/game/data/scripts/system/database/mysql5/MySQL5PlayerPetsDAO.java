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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.pet.PetDopingBag;
import com.aionemu.gameserver.services.toypet.PetHungryLevel;

/**
 * This class provides the database access layer for managing player pets using {@code MySQL5}.<br>
 * It handles all CRUD operations related to pet data in the database.<br>
 * It extends {@link PlayerPetsDAO} to implement specific queries for the game server.
 * @author M@xx, xTz, Rolandas
 */
public class MySQL5PlayerPetsDAO extends PlayerPetsDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerPetsDAO.class);
	
	/**
	 * Updates the feeding status of a specific pet in the database.<br>
	 * This method saves the current hunger level and progress for a {@link Player}.
	 * @param player The {@code Player} object who owns the pet.
	 * @param petId The unique identifier for the pet.
	 * @param hungryLevel The current hunger level of the pet.
	 * @param feedProgress The progress made during the feeding action.
	 * @param reuseTime The time remaining until the pet can be fed again.
	 */
	@Override
	public void saveFeedStatus(Player player, int petId, int hungryLevel, int feedProgress, long reuseTime)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET hungry_level = ?, feed_progress = ?, reuse_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setInt(1, hungryLevel);
			stmt.setInt(2, feedProgress);
			stmt.setLong(3, reuseTime);
			stmt.setInt(4, player.getObjectId());
			stmt.setInt(5, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update pet #" + petId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the {@code PetDopingBag} data to the database.<br>
	 * This method updates the doping information for a specific pet owned by a player.<br>
	 * It combines food, drink, and scroll IDs into a single string for storage.
	 * @param player The {@link Player} object who owns the pet.
	 * @param petId The unique identifier of the pet to update.
	 * @param bag The {@link PetDopingBag} containing the data to be saved.
	 */
	@Override
	public void saveDopingBag(Player player, int petId, PetDopingBag bag)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET dopings = ? WHERE player_id = ? AND pet_id = ?");
			String itemIds = bag.getFoodItem() + "," + bag.getDrinkItem();
			for (int itemId : bag.getScrollsUsed())
			{
				itemIds += "," + Integer.toString(itemId);
			}
			
			stmt.setString(1, itemIds);
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update doping for pet #" + petId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Updates the reuse time for a specific pet.<br>
	 * This method modifies the database record for the player's pet.
	 * @param player The {@link Player} object who owns the pet.
	 * @param petId The unique identifier of the pet.
	 * @param time The new value to set for the reuse time.
	 */
	@Override
	public void setTime(Player player, int petId, long time)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET reuse_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setLong(1, time);
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update pet #" + petId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves a new pet record into the database.<br>
	 * This method uses {@code PetCommonData} to populate the {@code player_pets} table.<br>
	 * It handles the SQL execution and closes the connection automatically.
	 * @param petCommonData The data object containing the pet details to be saved.
	 */
	@Override
	public void insertPlayerPet(PetCommonData petCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("INSERT INTO player_pets(player_id, pet_id, decoration, name, despawn_time, expire_time) VALUES(?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, petCommonData.getMasterObjectId());
			stmt.setInt(2, petCommonData.getPetId());
			stmt.setInt(3, petCommonData.getDecoration());
			stmt.setString(4, petCommonData.getName());
			stmt.setTimestamp(5, petCommonData.getDespawnTime());
			stmt.setInt(6, petCommonData.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error inserting new pet #" + petCommonData.getPetId() + "[" + petCommonData.getName() + "]", e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Removes a specific pet from a player's database record.<br>
	 * This method deletes the entry matching both the {@code Player} ID and the {@code petId}.<br>
	 * It logs an error if the deletion fails during execution.
	 * @param player The {@link Player} object who owns the pet.
	 * @param petId The unique identifier of the pet to be removed.
	 */
	@Override
	public void removePlayerPet(Player player, int petId)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("DELETE FROM player_pets WHERE player_id = ? AND pet_id = ?");
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error removing pet #" + petId, e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Retrieves all pets belonging to a specific {@link Player}.<br>
	 * This method queries the database for pet records associated with the player's unique ID.<br>
	 * It populates a list of {@code PetCommonData} objects with their current status and attributes.
	 * @param player The {@link Player} whose pets need to be loaded.
	 * @return A {@code List} containing all {@code PetCommonData} for the specified player, or an empty list if none are found.
	 */
	@Override
	public List<PetCommonData> getPlayerPets(Player player)
	{
		final List<PetCommonData> pets = new ArrayList<>();
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM player_pets WHERE player_id = ?");
			stmt.setInt(1, player.getObjectId());
			final ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				final PetCommonData petCommonData = new PetCommonData(rs.getInt("pet_id"), player.getObjectId(), rs.getInt("expire_time"));
				petCommonData.setName(rs.getString("name"));
				petCommonData.setDecoration(rs.getInt("decoration"));
				if (petCommonData.getFeedProgress() != null)
				{
					petCommonData.getFeedProgress().setHungryLevel(PetHungryLevel.fromId(rs.getInt("hungry_level")));
					petCommonData.getFeedProgress().setData(rs.getInt("feed_progress"));
					petCommonData.setRefeedTime(rs.getLong("reuse_time"));
				}
				
				if (petCommonData.getDopingBag() != null)
				{
					final String dopings = rs.getString("dopings");
					if (dopings != null)
					{
						final String[] ids = dopings.split(",");
						for (int i = 0; i < ids.length; i++)
						{
							petCommonData.getDopingBag().setItem(Integer.parseInt(ids[i]), i);
						}
					}
				}
				
				petCommonData.setBirthday(rs.getTimestamp("birthday"));
				if (petCommonData.getRefeedDelay() > 0)
				{
					petCommonData.setIsFeedingTime(false);
					petCommonData.scheduleRefeed(petCommonData.getRefeedDelay());
				}
				else if (petCommonData.getFeedProgress() != null)
				{
					petCommonData.getFeedProgress().setHungryLevel(PetHungryLevel.HUNGRY);
				}
				
				petCommonData.setStartMoodTime(rs.getLong("mood_started"));
				petCommonData.setShuggleCounter(rs.getInt("counter"));
				petCommonData.setMoodCdStarted(rs.getLong("mood_cd_started"));
				petCommonData.setGiftCdStarted(rs.getLong("gift_cd_started"));
				Timestamp ts = null;
				try
				{
					ts = rs.getTimestamp("despawn_time");
				}
				catch (Exception e)
				{
				}
				
				if (ts == null)
				{
					ts = new Timestamp(System.currentTimeMillis());
				}
				
				petCommonData.setDespawnTime(ts);
				pets.add(petCommonData);
			}
			
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error getting pets for " + player.getObjectId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return pets;
	}
	
	/**
	 * Updates the name of a specific pet in the database.<br>
	 * This method uses the {@code PetCommonData} object to identify the correct record.<br>
	 * It updates the entry where the {@code player_id} and {@code pet_id} match.
	 * @param petCommonData The data object containing the new name and unique identifiers.
	 */
	@Override
	public void updatePetName(PetCommonData petCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET name = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setString(1, petCommonData.getName());
			stmt.setInt(2, petCommonData.getMasterObjectId());
			stmt.setInt(3, petCommonData.getPetId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error update pet #" + petCommonData.getPetId(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Saves the mood and cooldown data for a specific pet to the database.<br>
	 * This method updates the {@code player_pets} table with current status values.
	 * @param petCommonData The {@link PetCommonData} object containing the updated pet information.
	 * @return {@code true} if the update was successful, or {@code false} if an error occurred.
	 */
	@Override
	public boolean savePetMoodData(PetCommonData petCommonData)
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("UPDATE player_pets SET mood_started = ?, counter = ?, mood_cd_started = ?, gift_cd_started = ?, despawn_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setLong(1, petCommonData.getMoodStartTime());
			stmt.setInt(2, petCommonData.getShuggleCounter());
			stmt.setLong(3, petCommonData.getMoodCdStarted());
			stmt.setLong(4, petCommonData.getGiftCdStarted());
			stmt.setTimestamp(5, petCommonData.getDespawnTime());
			stmt.setInt(6, petCommonData.getMasterObjectId());
			stmt.setInt(7, petCommonData.getPetId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Error updating mood for pet #" + petCommonData.getPetId(), e);
			return false;
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return true;
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
