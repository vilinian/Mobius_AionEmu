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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the artificial intelligence for the {@code dredgionCommander} NPC.<br>
 * It extends {@link SiegeNpcAI2} to provide specific behaviors during siege events.
 * @author Luzien
 */
@AIName("dredgionCommander")
public class DredgionCommanderAI2 extends SiegeNpcAI2
{
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code scheduleOneShot()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		scheduleOneShot();
	}
	
	/**
	 * Retrieves the specific skill ID for the current NPC.<br>
	 * This method checks the {@code getNpcId()} value to determine the correct result.<br>
	 * It returns a unique integer based on the NPC's identity.
	 * @return The skill ID associated with the NPC, or 0 if no match is found.
	 */
	private int getSkill()
	{
		switch (getNpcId())
		{
			case 258236:
				return 18428;
			case 272291:
			case 272292:
			case 272293:
			case 272294:
			case 272295:
				return 21312;
			case 276649:
			case 276650:
			case 276651:
				return 17572;
			case 276871:
			case 276872:
			case 276873:
				return 18411;
			default:
				return 0;
		}
	}
	
	/**
	 * Schedules a one-time task to execute after a delay.<br>
	 * This method uses {@link ThreadPoolManager} to run logic every 45 seconds.<br>
	 * It checks if the current target is alive and belongs to a specific race before using a skill.
	 */
	private void scheduleOneShot()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (getSkill() != 0)
			{
				if (getTarget() instanceof Npc)
				{
					final Npc target = (Npc) getTarget();
					final Race race = target.getRace();
					if ((race.equals(Race.GCHIEF_DARK) || race.equals(Race.GCHIEF_LIGHT)) && !target.getLifeStats().isAlreadyDead())
					{
						AI2Actions.useSkill(DredgionCommanderAI2.this, getSkill());
						getAggroList().addHate(target, 10000);
					}
				}
				
				scheduleOneShot();
			}
		}, 45 * 1000);
	}
}
