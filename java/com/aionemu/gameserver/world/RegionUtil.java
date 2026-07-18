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
package com.aionemu.gameserver.world;

import com.aionemu.gameserver.configs.main.WorldConfig;

/**
 * Provides utility methods for managing and interacting with world regions.<br>
 * This class simplifies common operations related to {@code Region}.
 * @author ATracer
 */
public class RegionUtil
{
	public static final int X_3D_OFFSET = 1000000;
	public static final int Y_3D_OFFSET = 1000;
	public static final int X_2D_OFFSET = 1000;
	
	/**
	 * Calculates the unique identifier for a 2D region.<br>
	 * This method uses the provided coordinates and size to determine the ID.<br>
	 * It is useful for mapping world positions to specific grid areas.
	 * @param regionSize The width and height of a single region square.
	 * @param x The horizontal coordinate in the game world.
	 * @param y The vertical coordinate in the game world.
	 * @return The calculated integer ID for the 2D region.
	 */
	public static int get2DRegionId(int regionSize, float x, float y)
	{
		return (((int) x / regionSize) * X_2D_OFFSET) + ((int) y / regionSize);
	}
	
	/**
	 * Calculates a unique identifier for a 3D region.<br>
	 * This method uses the provided coordinates to determine which grid cell a point belongs to.<br>
	 * It is similar to {@code float, float)} but includes height data.
	 * @param regionSize The size of one side of a cubic region.
	 * @param x The x-coordinate in the 3D world.
	 * @param y The y-coordinate in the 3D world.
	 * @param z The z-coordinate in the 3D world.
	 * @return The calculated unique integer ID for the 3D region.
	 */
	public static int get3DRegionId(int regionSize, float x, float y, float z)
	{
		return (((int) x / regionSize) * X_3D_OFFSET) + (((int) y / regionSize) * Y_3D_OFFSET) + ((int) z / regionSize);
	}
	
	/**
	 * Calculates the unique ID for a 2D region based on coordinates.<br>
	 * This method uses the default size from {@link WorldConfig}.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @return The calculated integer ID of the 2D region.
	 */
	public static int get2dRegionId(float x, float y)
	{
		return get2DRegionId(WorldConfig.WORLD_REGION_SIZE, x, y);
	}
	
	/**
	 * Calculates the unique identifier for a 3D region.<br>
	 * This method uses the global world size to determine the ID.<br>
	 * It maps coordinates into a specific grid cell.
	 * @param x The horizontal coordinate.
	 * @param y The vertical coordinate.
	 * @param z The depth coordinate.
	 * @return The calculated integer ID of the 3D region.
	 */
	public static int get3dRegionId(float x, float y, float z)
	{
		return get3DRegionId(WorldConfig.WORLD_REGION_SIZE, x, y, z);
	}
	
	/**
	 * This method calculates the {@code x} coordinate from a 2D region identifier.<br>
	 * It uses the {@code X_2D_OFFSET} and {@code WORLD_REGION_SIZE} to perform the calculation.
	 * @param regionId The unique identifier of the 2D region.
	 * @return The calculated {@code x} coordinate as an {@code int}.
	 */
	public static int getXFrom2dRegionId(int regionId)
	{
		return (regionId / X_2D_OFFSET) * WorldConfig.WORLD_REGION_SIZE;
	}
	
	/**
	 * This method retrieves the {@code y} coordinate from a 2D region identifier.<br>
	 * It uses the {@code X_2D_OFFSET} and {@code WORLD_REGION_SIZE} to calculate the value.
	 * @param regionId The unique identifier of the 2D region.
	 * @return The calculated {@code y} coordinate as an {@code int}.
	 */
	public static int getYFrom2dRegionId(int regionId)
	{
		return (regionId % X_2D_OFFSET) * WorldConfig.WORLD_REGION_SIZE;
	}
	
	/**
	 * This method extracts the {@code x} coordinate from a 3D region identifier.<br>
	 * It uses the {@code X_3D_OFFSET} constant to perform the calculation.<br>
	 * The result is based on the {@code WorldConfig.WORLD_REGION_SIZE}.
	 * @param regionId The unique identifier of the 3D region.
	 * @return The calculated {@code x} coordinate as an {@code int}.
	 */
	public static int getXFrom3dRegionId(int regionId)
	{
		return (regionId / X_3D_OFFSET) * WorldConfig.WORLD_REGION_SIZE;
	}
	
	/**
	 * This method retrieves the {@code y} coordinate from a 3D region identifier.<br>
	 * It uses the {@code X_3D_OFFSET} and {@code Y_3D_OFFSET} constants to calculate the value.<br>
	 * The result is scaled by the {@code WORLD_REGION_SIZE}.
	 * @param regionId The unique 3D region identifier to process.
	 * @return The calculated {@code y} coordinate as an {@code int}.
	 */
	public static int getYFrom3dRegionId(int regionId)
	{
		return ((regionId % X_3D_OFFSET) / Y_3D_OFFSET) * WorldConfig.WORLD_REGION_SIZE;
	}
	
	/**
	 * Extracts the {@code z} coordinate from a 3D region identifier.<br>
	 * This method uses {@code X_3D_OFFSET} and {@code Y_3D_OFFSET} to calculate the value.<br>
	 * It returns the result based on the {@code WorldConfig.WORLD_REGION_SIZE}.
	 * @param regionId The unique identifier of the 3D region.
	 * @return The calculated {@code z} coordinate as an {@code int}.
	 */
	public static int getZFrom3dRegionId(int regionId)
	{
		return (regionId % X_3D_OFFSET % Y_3D_OFFSET) * WorldConfig.WORLD_REGION_SIZE;
	}
}
