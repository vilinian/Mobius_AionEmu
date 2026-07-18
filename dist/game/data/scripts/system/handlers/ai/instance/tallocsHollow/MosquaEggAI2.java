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
package system.handlers.ai.instance.tallocsHollow;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for the {@code mosquaegg} NPC.<br>
 * This class extends {@link AggressiveNpcAI2} to define specific behaviors for this entity.
 * @author xTz
 */
@AIName("mosquaegg")
public class MosquaEggAI2 extends AggressiveNpcAI2
{
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		ThreadPoolManager.getInstance().schedule(() -> checkSpawn(), 17000);
	}
	
	/**
	 * Checks if the current position is marked as a spawn point.<br>
	 * If it is, this method triggers the {@code spawn} action.<br>
	 * It also removes the owner using {@code deleteOwner}.
	 */
	private void checkSpawn()
	{
		if (getPosition().isSpawned())
		{
			// spawn - Spawned Supraklaw
			spawn(217132, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
			AI2Actions.deleteOwner(this);
		}
	}
}
