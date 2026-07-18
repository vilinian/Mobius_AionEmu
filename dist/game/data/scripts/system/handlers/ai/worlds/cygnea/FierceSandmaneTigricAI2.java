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
package system.handlers.ai.worlds.cygnea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for the {@code fierce_sandmane_tigric} creature.<br>
 * This class extends {@link AggressiveNpcAI2} to provide specific behaviors for this NPC type.
 * @author Falke_34
 * @rework FrozenKiller
 */
@AIName("fierce_sandmane_tigric") // 235973
public class FierceSandmaneTigricAI2 extends AggressiveNpcAI2
{
	protected List<Integer> percents = new ArrayList<>();
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleSpawned();
	}
	
	/**
	 * Handles the logic when this AI is attacked by a {@code Creature}.<br>
	 * It updates the current health percentage of the NPC.<br>
	 * It then calls the superclass method to process the attack.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		checkPercentage(getLifeStats().getHpPercentage());
		super.handleAttack(creature);
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		addPercent();
		super.handleBackHome();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It clears the {@code percents} list.<br>
	 * It handles specific logic for NPCs with IDs {@code 235973} and {@code 235974}.<br>
	 * Finally, it calls the superclass {@code handleDied} method.
	 */
	@Override
	protected void handleDied()
	{
		final Npc npc = getPosition().getWorldMapInstance().getNpc(235973);
		final Npc add = getPosition().getWorldMapInstance().getNpc(235974);
		if (npc != null)
		{
			add.getController().onDelete();
			percents.clear();
			super.handleDied();
		}
		else
		{
			percents.clear();
			super.handleDied();
		}
	}
	
	/**
	 * Resets the {@code percents} list.<br>
	 * It clears all existing values.<br>
	 * It adds a default value of {@code 50} to the list.
	 */
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			50
		});
	}
	
	/**
	 * This method checks the current health percentage of a creature.<br>
	 * It triggers specific events based on predefined thresholds.<br>
	 * Once an event is triggered, that threshold is removed from the list.
	 * @param hpPercent The current health percentage to check.
	 */
	private synchronized void checkPercentage(int hpPercent)
	{
		for (Integer percent : percents)
		{
			if (hpPercent <= percent)
			{
				switch (percent)
				{
					case 50:
						spawn(235974, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Cloned Seagric
						break;
				}
				
				percents.remove(percent);
				break;
			}
		}
	}
}
