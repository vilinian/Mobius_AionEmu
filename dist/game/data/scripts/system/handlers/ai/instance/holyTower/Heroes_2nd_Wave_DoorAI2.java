/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY); without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package system.handlers.ai.instance.holyTower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for the door in the heroes second wave.<br>
 * This class manages specific behaviors for this instance within the {@code holyTower} area.
 * @author Falke_34
 */
@AIName("heroes_2nd_wave_door") // 248441
public class Heroes_2nd_Wave_DoorAI2 extends AggressiveNpcAI2
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
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
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
	 * Handles the logic when an NPC is despawned.<br>
	 * It clears the {@code percents} list.<br>
	 * This method then calls the superclass implementation of {@code handleDespawned}.
	 */
	@Override
	protected void handleDespawned()
	{
		percents.clear();
		super.handleDespawned();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It clears the {@code percents} list.<br>
	 * It then calls the superclass {@code handleDied} method.
	 */
	@Override
	protected void handleDied()
	{
		percents.clear();
		super.handleDied();
	}
	
	/**
	 * Resets the {@code percents} list.<br>
	 * It clears all existing values.<br>
	 * It adds a default set of values to the list.
	 */
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			80,
			60,
			40,
			20
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
					case 80:
						spawn(248020, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Legatus
						spawn(248015, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Warrior
						break;
					case 60:
						spawn(248016, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Assassin
						break;
					case 40:
						spawn(248015, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Warrior
						spawn(248018, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Medicus
						break;
					case 20:
						spawn(248015, getOwner().getX() - 2, getOwner().getY() - 2, getOwner().getZ(), getOwner().getHeading()); // Warrior
						break;
				}
				
				percents.remove(percent);
				break;
			}
		}
	}
}
