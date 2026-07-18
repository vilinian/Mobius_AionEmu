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
package system.handlers.ai.siege;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for guard tower NPCs during siege events.<br>
 * This class manages how these structures behave and react to nearby players or enemies.
 * @author cheatkiller
 */
@AIName("guardtower")
public class GuardTowerAI2 extends NpcAI2
{
	private Future<?> task;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also sets the attack range of the owner to {@code 30}.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		getOwner().getObjectTemplate().setAttackRange(30);
	}
	
	/**
	 * Handles the logic when a {@code Creature} is spotted.<br>
	 * This method checks the distance between this AI and the target.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	/**
	 * This method is called when a {@link Creature} is no longer visible.<br>
	 * It cancels the current active task for this AI.
	 * @param creature The {@code Creature} that was lost from sight.
	 */
	@Override
	protected void handleCreatureNotSee(Creature creature)
	{
		if (task != null)
		{
			task = null;
		}
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		checkDistance(this, creature);
	}
	
	/**
	 * Checks if a {@link Player} is within a specific range.<br>
	 * This method determines if the AI should start an attack task.<br>
	 * It only triggers if the current {@code task} is {@code null}.
	 * @param ai The {@link NpcAI2} instance performing the check.
	 * @param creature The {@link Creature} to evaluate for distance.
	 */
	private void checkDistance(NpcAI2 ai, Creature creature)
	{
		if (creature instanceof Player)
		{
			if (task == null)
			{
				if (MathUtil.isIn3dRange(getOwner(), creature, 30))
				{
					AI2Actions.targetCreature(this, creature);
					attack();
				}
			}
		}
	}
	
	/**
	 * Stops the current task if it is still running.<br>
	 * It checks if {@code task} is not {@code null} and not already cancelled.<br>
	 * If valid, it calls {@code cancel} with {@code true}.
	 */
	private void cancelTask()
	{
		if ((task != null) && !task.isCancelled())
		{
			task.cancel(true);
		}
	}
	
	/**
	 * Initiates the attack sequence for the NPC.<br>
	 * This method schedules a recurring skill use task.<br>
	 * It uses {@link ThreadPoolManager} to run the action every 1000 milliseconds.
	 */
	private void attack()
	{
		final Player p = (Player) getOwner().getTarget();
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if ((p == null) || p.getLifeStats().isAlreadyDead())
			{
				cancelTask();
			}
			
			getOwner().getController().useSkill(getSkillList().getRandomSkill().getSkillId(), 55);
		}, 1000, 8000);
		getOwner().getController().addTask(TaskId.SKILL_USE, task);
	}
}
