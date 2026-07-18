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
package system.handlers.ai.quests;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the specific AI behavior for the NPC associated with quest {@code Q20060}.<br>
 * This class manages the logic for the character named Garnon within this quest.<br>
 * It extends {@link NpcAI2} to provide custom actions and state transitions.
 * @author Cheatkiller
 */
@AIName("Q20060")
public class GarnonQ20060AI2 extends NpcAI2
{
	private Future<?> task;
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It cancels any active {@code task} if it is still running.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		super.handleDespawned();
		if ((task != null) && !task.isDone())
		{
			task.cancel(true);
		}
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code despawn()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		despawn();
	}
	
	/**
	 * Schedules the removal of this NPC.<br>
	 * It waits for a delay before calling {@code float, float, float, byte)}.<br>
	 * This method also triggers the {@code onDelete()} action on the owner's controller.
	 */
	private void despawn()
	{
		task = ThreadPoolManager.getInstance().schedule(() ->
		{
			spawn(800020, 442.279f, 464.349f, 341.520f, (byte) 20);
			getOwner().getController().onDelete();
		}, 60000 * 3);
	}
}
