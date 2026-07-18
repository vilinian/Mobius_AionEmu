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
package com.aionemu.gameserver.model.templates.windstreams;

/**
 * Represents a specific path or route within the Windstream system.<br>
 * This class stores the configuration data required to define movement along these streams.
 * @author xTz
 */
public class WindstreamPath
{
	public int teleportId;
	public int distance;
	
	/**
	 * Creates a new {@link WindstreamPath} object.<br>
	 * This constructor initializes the path with specific coordinates.
	 * @param teleportId The unique identifier for the teleport point.
	 * @param distance The length of the windstream path.
	 */
	public WindstreamPath(int teleportId, int distance)
	{
		this.teleportId = teleportId;
		this.distance = distance;
	}
}
