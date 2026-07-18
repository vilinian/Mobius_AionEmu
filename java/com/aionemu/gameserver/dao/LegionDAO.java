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
package com.aionemu.gameserver.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;

/**
 * This class is responsible for loading and storing {@link Legion} data in the database.<br>
 * It provides the Data Access Object (DAO) layer for managing all legion-related records.
 * @author Simple, CoolyT
 */
public abstract class LegionDAO implements IDFactoryAwareDAO
{
	/**
	 * Returns true if name is used, false in other case
	 * @param name name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isNameUsed(String name);
	
	/**
	 * Creates legion in DB
	 * @param legion
	 * @return
	 */
	public abstract boolean saveNewLegion(Legion legion);
	
	/**
	 * Stores legion to DB
	 * @param legion
	 */
	public abstract void storeLegion(Legion legion);
	
	/**
	 * Loads a legion
	 * @param legionName
	 * @return
	 */
	public abstract Legion loadLegion(String legionName);
	
	/**
	 * Loads a legion
	 * @param legionId
	 * @return Legion
	 */
	public abstract Legion loadLegion(int legionId);
	
	/**
	 * Removes legion and all related data (Done by CASCADE DELETION)
	 * @param legionId legion to delete
	 */
	public abstract void deleteLegion(int legionId);
	
	/**
	 * Returns the announcement list of a legion
	 * @param legionId
	 * @return announcementList
	 */
	public abstract TreeMap<Timestamp, String> loadAnnouncementList(int legionId);
	
	/**
	 * Creates announcement in DB
	 * @param legionId
	 * @param currentTime
	 * @param message
	 * @return true or false
	 */
	public abstract boolean saveNewAnnouncement(int legionId, Timestamp currentTime, String message);
	
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return LegionDAO.class.getName();
	}
	
	/**
	 * Stores a legion emblem in the database
	 * @param legionId
	 * @param legionEmblem
	 */
	public abstract void storeLegionEmblem(int legionId, LegionEmblem legionEmblem);
	
	/**
	 * @param legionId
	 * @param key
	 */
	public abstract void removeAnnouncement(int legionId, Timestamp key);
	
	/**
	 * Loads a legion emblem
	 * @param legionId
	 * @return LegionEmblem
	 */
	public abstract LegionEmblem loadLegionEmblem(int legionId);
	
	/**
	 * Loads the warehouse of legions
	 * @param legion
	 * @return Storage
	 */
	public abstract LegionWarehouse loadLegionStorage(Legion legion);
	
	/**
	 * @param legion
	 */
	public abstract void loadLegionHistory(Legion legion);
	
	/**
	 * @param legionId
	 * @param legionHistory
	 * @return true if query successful
	 */
	public abstract boolean saveNewLegionHistory(int legionId, LegionHistory legionHistory);
	
	/**
	 * @param legion
	 */
	public abstract void updateLegionDescription(Legion legion);
	
	public abstract void storeLegionJoinRequest(LegionJoinRequest legionJoinRequest);
	
	public abstract List<LegionJoinRequest> loadLegionJoinRequests(int legionId);
	
	public abstract void deleteLegionJoinRequest(int legionId, int playerId);
	
	public abstract void deleteLegionJoinRequest(LegionJoinRequest legionJoinRequest);
	
	public abstract Collection<Integer> getLegionIdswithTerritories();
}
