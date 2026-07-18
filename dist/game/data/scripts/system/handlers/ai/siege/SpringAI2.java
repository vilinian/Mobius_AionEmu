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
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for {@link SiegeNpc} units of the spring type.<br>
 * This class manages specific behaviors and actions during siege events.
 * @author xTz
 */
@AIName("spring")
public class SpringAI2 extends NpcAI2
{
	/**
	 * This method is called when the AI entity is first spawned.<br>
	 * It starts a background task to send updates to all players.<br>
	 * The task checks if the player is in the same world as the owner.<br>
	 * If they are, it sends an {@code SM_FLAG_INFO} packet every 2 seconds.
	 */
	@Override
	public void handleSpawned()
	{
		startSchedule();
	}
	
	/**
	 * Starts a recurring task for the AI.<br>
	 * It uses {@link ThreadPoolManager} to run {@code checkForHeal} every 5000 milliseconds.
	 */
	private void startSchedule()
	{
		ThreadPoolManager.getInstance().schedule(() -> checkForHeal(), 5000);
	}
	
	/**
	 * Checks if any nearby allies need healing.<br>
	 * It scans for {@link Creature} objects within a range of 10.<br>
	 * If an ally is found with low health, it calls {@code doHeal}.<br>
	 * This method also ensures the schedule is started.
	 */
	private void checkForHeal()
	{
		if (!isAlreadyDead() && getPosition().isSpawned())
		{
			for (VisibleObject object : getKnownList().getKnownObjects().values())
			{
				final Creature creature = (Creature) object;
				final CreatureLifeStats<?> lifeStats = creature.getLifeStats();
				if (isInRange(creature, 10) && !creature.getEffectController().hasAbnormalEffect(19116) && !lifeStats.isAlreadyDead() && (lifeStats.getCurrentHp() < lifeStats.getMaxHp()))
				{
					if (creature instanceof SiegeNpc)
					{
						final SiegeNpc npc = (SiegeNpc) creature;
						if (getObjectTemplate().getRace() == npc.getObjectTemplate().getRace())
						{
							doHeal();
							break;
						}
					}
					else if (creature instanceof Player)
					{
						final Player player = (Player) creature;
						if ((getObjectTemplate().getRace() == player.getRace()) && player.isOnline())
						{
							doHeal();
							break;
						}
					}
				}
			}
			
			startSchedule();
		}
	}
	
	/**
	 * This method performs the healing action.<br>
	 * It targets {@code this} and uses skill {@code 19116}.<br>
	 * It is called by {@code checkForHeal}.
	 */
	private void doHeal()
	{
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, 19116);
	}
}
