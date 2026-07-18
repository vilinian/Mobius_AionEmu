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
package com.aionemu.gameserver.skillengine.periodicaction;

import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * Represents an action that repeats over a specific duration.<br>
 * This class serves as a base for skills that trigger effects at regular intervals.<br>
 * It handles the logic for periodic execution within the {@link com.aionemu.gameserver.skillengine.model.Effect} system.
 * @author antness
 */
public abstract class PeriodicAction
{
	public abstract void act(Effect effect);
}
