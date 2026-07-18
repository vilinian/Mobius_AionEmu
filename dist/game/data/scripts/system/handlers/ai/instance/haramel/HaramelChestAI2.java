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
package system.handlers.ai.instance.haramel;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.world.WorldPosition;

import system.handlers.ai.ChestAI2;

/**
 * Handles the specific AI behavior for chests located in the Haramel region.<br>
 * This class extends {@link ChestAI2} to provide specialized logic for these instances.
 * @author xTz
 */
@AIName("haramelchest")
public class HaramelChestAI2 extends ChestAI2
{
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It spawns a new entity at a specific location if the current position is valid.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		final WorldPosition p = getPosition();
		if ((p != null) && (p.getWorldMapInstance() != null))
		{
			spawn(700852, 224.598f, 331.143f, 141.892f, (byte) 90);
		}
		
		super.handleDespawned();
	}
}
