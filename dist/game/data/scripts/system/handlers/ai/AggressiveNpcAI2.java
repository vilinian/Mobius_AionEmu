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
package system.handlers.ai;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.AggroEventHandler;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.ai2.handler.SimpleAbyssGuardHandler;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class defines the behavior for aggressive {@link Npc} entities.<br>
 * It handles hostile interactions and combat logic for NPCs that attack players.<br>
 * It extends {@link GeneralNpcAI2} to provide specific offensive behaviors.
 * @author ATracer
 * @author Antraxx
 */
@AIName("aggressive")
public class AggressiveNpcAI2 extends GeneralNpcAI2
{
	/**
	 * Processes the logic when a {@code Creature} is spotted.<br>
	 * This method delegates the behavior to the {@link SimpleAbyssGuardHandler}.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		CreatureEventHandler.onCreatureSee(this, creature);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
		CreatureEventHandler.onCreatureMoved(this, creature);
	}
	
	/**
	 * This method manages the behavior when a {@code Creature} becomes aggressive.<br>
	 * It checks if the AI is currently able to think.<br>
	 * If so, it triggers the {@code onAggro} logic.
	 * @param creature The {@code Creature} that has triggered the aggro state.
	 */
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		if (canThink())
		{
			AggroEventHandler.onAggro(this, creature);
		}
	}
	
	/**
	 * This method checks if the AI should defend against a specific attacker.<br>
	 * It evaluates the {@code attacker} to determine defensive actions.<br>
	 * Currently, this logic is not implemented.
	 * @param attacker The {@code Creature} that is attacking the NPC.
	 * @return {@code false} because no defense logic is currently active.
	 */
	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker)
	{
		return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
	}
	
	/**
	 * This method requests assistance from nearby NPCs.<br>
	 * It searches for living {@code Npc} objects within a specific range.<br>
	 * It triggers an aggro event on those neighbors using the current primary target.
	 * @param distance The maximum distance to search for nearby allies.
	 */
	protected void callForHelp(int distance)
	{
		final Creature firstTarget = getAggroList().getMostHated();
		for (VisibleObject object : getKnownList().getKnownObjects().values())
		{
			if ((object instanceof Npc) && isInRange(object, distance))
			{
				final Npc npc = (Npc) object;
				if (!npc.getLifeStats().isAlreadyDead())
				{
					npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, firstTarget);
				}
			}
		}
	}
	
	/**
	 * Selects a random {@link Player} from the nearby area.<br>
	 * It only considers players who are alive and within 50 units of the owner.<br>
	 * Returns {@code null} if no valid players are found.
	 * @return A random {@code Player} object or {@code null}.
	 */
	protected Player getRandomTarget()
	{
		final List<Player> players = new ArrayList<>();
		for (Player player : getKnownList().getKnownPlayers().values())
		{
			if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 50))
			{
				players.add(player);
			}
		}
		
		if (players.isEmpty())
		{
			return null;
		}
		
		return players.get(Rnd.get(players.size()));
	}
}
