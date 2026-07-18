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
package com.aionemu.gameserver.model.event_window;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * This interface manages a collection of {@code T} objects associated with event windows.<br>
 * It provides methods to handle and organize creatures involved in specific game events.
 * @author Ghostfur (Aion-Unique)
 * @param <T>
 */
public interface EventWindowList<T extends Creature>
{
	
	/**
	 * add event window list
	 * @param id
	 * @param eventId
	 * @param last_stamp
	 * @param elapsed
	 * @return
	 */
	public boolean add(T id, int eventId, Timestamp last_stamp, int elapsed);
	
	/**
	 * remove event window list
	 * @param id
	 * @param eventId
	 * @return
	 */
	public boolean remove(T id, int eventId);
	
	/**
	 * size event window list
	 * @return
	 */
	public int size();
}
