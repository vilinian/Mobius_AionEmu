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
import java.util.Set;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.house.PlayerHouseBid;

/**
 * This class provides Data Access Object (DAO) operations for managing house bids.<br>
 * It handles the persistence and retrieval of {@link PlayerHouseBid} records in the database.
 * @author Rolandas
 */
public abstract class HouseBidsDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return HouseBidsDAO.class.getName();
	}
	
	public abstract Set<PlayerHouseBid> loadBids();
	
	public abstract boolean addBid(int playerId, int houseId, long bidOffer, Timestamp time);
	
	public abstract void changeBid(int playerId, int houseId, long newBidOffer, Timestamp time);
	
	public abstract void deleteHouseBids(int houseId);
}
