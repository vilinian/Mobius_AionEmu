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
package system.handlers.ai.worlds;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.handler.SimpleAbyssGuardHandler;
import com.aionemu.gameserver.controllers.observer.GaleCycloneObserver;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 * This class handles the AI logic for NPCs that provide a movement speed buff.<br>
 * It manages how these entities interact with {@link Player} objects to apply effects.
 * @author xTz
 */
@AIName("f2p_movespeedup")
public class MovespeedUpAI2 extends NpcAI2
{
	private final Map<Integer, GaleCycloneObserver> observed = new ConcurrentHashMap<>();
	private boolean blocked;
	
	/**
	 * Processes the logic when a {@code Creature} is spotted.<br>
	 * This method delegates the behavior to the {@link SimpleAbyssGuardHandler}.
	 * @param creature The {@code Creature} that was seen.
	 */
	@Override
	protected void handleCreatureSee(Creature creature)
	{
		if (blocked)
		{
			return;
		}
		
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			final GaleCycloneObserver observer = new GaleCycloneObserver(player, getOwner())
			{
				@Override
				public void onMove()
				{
					if (!blocked)
					{
						SkillEngine.getInstance().getSkill(getOwner(), 22883, 540, player).useNoAnimationSkill();
					}
				}
			};
			player.getObserveController().addObserver(observer);
			observed.put(player.getObjectId(), observer);
		}
	}
	
	/**
	 * This method is called when a {@link Creature} is no longer visible.<br>
	 * It cancels the current active task for this AI.
	 * @param creature The {@code Creature} that was lost from sight.
	 */
	@Override
	protected void handleCreatureNotSee(Creature creature)
	{
		if (blocked)
		{
			return;
		}
		
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			final Integer obj = player.getObjectId();
			final GaleCycloneObserver observer = observed.remove(obj);
			if (observer != null)
			{
				player.getObserveController().removeObserver(observer);
			}
		}
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It triggers the {@code onDie} logic.<br>
	 * This ensures all death-related actions are processed correctly.
	 */
	@Override
	protected void handleDied()
	{
		clear();
		super.handleDied();
	}
	
	/**
	 * Handles the logic when an NPC is despawned.<br>
	 * It clears the internal state of the AI.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		clear();
		super.handleDespawned();
	}
	
	/**
	 * Resets the internal state of this AI instance.<br>
	 * It sets the {@code blocked} flag to {@code true}.<br>
	 * It removes all entries from the {@code observed} map.<br>
	 * It unregisters observers from all associated {@link Player} objects.
	 */
	private void clear()
	{
		blocked = true;
		for (Integer obj : observed.keySet())
		{
			final Player player = getKnownList().getKnownPlayers().get(obj);
			final GaleCycloneObserver observer = observed.remove(obj);
			if (player != null)
			{
				player.getObserveController().removeObserver(observer);
			}
		}
	}
}
