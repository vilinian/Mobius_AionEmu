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
 * This class defines the artificial intelligence behavior for the {@code komad_sentry} NPC.<br>
 * It extends {@link AggressiveNpcAI2} to provide specific combat logic for this entity.
 * @author Dr.Nism
 */
@AIName("komad_sentry")
public class KomadSentryAI2 extends AggressiveNpcAI2
{
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		final int lifetime = (getNpcId() == 281514 ? 20000 : 20000);
		toDespawn(lifetime);
	}
	
	/**
	 * Schedules the removal of this NPC instance.<br>
	 * It uses {@link ThreadPoolManager} to handle the task.<br>
	 * The deletion happens after a specific time period.
	 * @param delay The amount of time to wait before despawning in milliseconds.
	 */
	private void toDespawn(int delay)
	{
		ThreadPoolManager.getInstance().schedule(() -> AI2Actions.deleteOwner(KomadSentryAI2.this), delay);
	}
}
