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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.geometry.AbstractArea;
import com.aionemu.gameserver.model.geometry.RectangleArea;

/**
 * Represents a specific geographical region within the game world.<br>
 * This class extends {@link RectangleArea} to define bounded zones for gameplay logic.
 * @author ATracer
 */
public class RegionZone extends RectangleArea
{
	/**
	 * Creates a new {@link RegionZone} based on specific coordinates.<br>
	 * This constructor uses the {@code WORLD_REGION_SIZE} from {@link WorldConfig} to determine the width and height.
	 * @param startX The starting X coordinate of the region.
	 * @param startY The starting Y coordinate of the region.
	 * @param minZ The minimum Z coordinate (minimum height) of the region.
	 * @param maxZ The maximum Z coordinate (maximum height) of the region.
	 */
	public RegionZone(float startX, float startY, float minZ, float maxZ)
	{
		super(null, 0, startX, startY, startX + WorldConfig.WORLD_REGION_SIZE, startY + WorldConfig.WORLD_REGION_SIZE, minZ, maxZ);
	}
	
	/**
	 * Checks if a specific area is contained within this region.<br>
	 * This method currently always returns {@code true}.
	 * @param area The {@link AbstractArea} to check.
	 * @return {@code true} if the area is inside, otherwise {@code false}.
	 */
	public boolean isInside(AbstractArea area)
	{
		return true;
	}
}
