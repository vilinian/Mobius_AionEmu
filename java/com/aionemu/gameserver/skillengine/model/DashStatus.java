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
package com.aionemu.gameserver.skillengine.model;

/**
 * Represents the various states of a character's dash ability.<br>
 * This enum is used by the {@link com.aionemu.gameserver.skillengine.SkillEngine} to track movement status.
 * @author weiwei
 * @modified VladimirZ
 */
public enum DashStatus
{
	NONE(0),
	RANDOMMOVELOC(1),
	DASH(2),
	BACKDASH(3),
	MOVEBEHIND(4),
	ROBOTMOVELOC(6);
	
	private final int id;
	
	/**
	 * Creates a new instance of {@link DashStatus}.<br>
	 * This constructor maps the enum to a specific numeric identifier.
	 * @param id The unique integer value for this status.
	 */
	private DashStatus(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
}
