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
package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.gameserver.model.templates.base.BaseTemplate;

/**
 * This class represents the result of a search operation for spawn templates.<br>
 * It holds the data retrieved from the {@link com.aionemu.gameserver.model.templates.spawns.SpawnTemplate} database queries.
 * @author Rolandas
 */
public final class SpawnSearchResult
{
	private final SpawnSpotTemplate spot;
	private final int worldId;
	
	/**
	 * Creates a new instance of {@link SpawnSearchResult}.<br>
	 * This object stores the location and world information for a spawn.
	 * @param worldId The unique identifier for the world.
	 * @param spot The {@code SpawnSpotTemplate} containing the specific coordinates.
	 */
	public SpawnSearchResult(int worldId, SpawnSpotTemplate spot)
	{
		this.worldId = worldId;
		this.spot = spot;
	}
	
	/**
	 * Retrieves the {@link SpawnSpotTemplate} associated with this search result.<br>
	 * This method returns the specific location data for a spawn point.
	 * @return The {@code SpawnSpotTemplate} object.
	 */
	public SpawnSpotTemplate getSpot()
	{
		return spot;
	}
	
	/**
	 * Retrieves the unique identifier for the world.<br>
	 * This value is fetched from the associated {@link BaseTemplate}.
	 * @return The {@code int} ID of the world.
	 */
	public int getWorldId()
	{
		return worldId;
	}
}
