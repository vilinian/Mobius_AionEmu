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
package system.handlers.ai.instance.haramel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;

import system.handlers.ai.AggressiveFirstSkillAI2;

/**
 * This class handles the artificial intelligence for NPCs located in the {@code haramel} zone.<br>
 * It extends {@link AggressiveFirstSkillAI2} to provide specific behaviors for these entities.
 * @Author Majka Ajural
 */
@AIName("Haramel_NPCs")
public class Haramel_NPCsAI2 extends AggressiveFirstSkillAI2
{
	private final AtomicBoolean isFirstAttack = new AtomicBoolean(true);
	protected List<Integer> percents = new ArrayList<>();
	
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
		switch (getNpcId())
		{
			case 216897: // Drudgelord Kakiti
				
				if (isFirstAttack.compareAndSet(true, false))
				{
					SkillEngine.getInstance().getSkill(getOwner(), 19212, 1, getOwner()).useSkill(); // Skin of Odium
				}
				break;
			case 216907: // MuMu Ham the Grey
				
				if (isFirstAttack.compareAndSet(true, false))
				{
					SkillEngine.getInstance().getSkill(getOwner(), 19211, 1, getOwner()).useSkill(); // Odella Overdose
				}
				
				checkPercentage(getLifeStats().getHpPercentage());
				break;
			case 216915: // Bossman Nukiti
				
				if (isFirstAttack.compareAndSet(true, false))
				{
					SkillEngine.getInstance().getSkill(getOwner(), 19261, 1, getOwner()).useSkill(); // False Trust
				}
				break;
		}
		
		super.handleAttack(creature);
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code addPercent()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		addPercent();
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		super.handleBackHome();
		isFirstAttack.set(true);
		addPercent();
	}
	
	/**
	 * This method is called when the AI gives up on its current target.<br>
	 * It triggers the {@code onTargetGiveup} logic.
	 */
	@Override
	protected void handleTargetGiveup()
	{
		super.handleTargetGiveup();
		
		// Walkers sometime don't trigger BackHome event.
		if (getNpcId() == 216915)
		{
			isFirstAttack.set(true);
		}
	}
	
	/**
	 * Resets the {@code percents} list.<br>
	 * It clears all existing values.<br>
	 * It adds a default value of {@code 40} to the list.
	 */
	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]
		{
			40
		});
	}
	
	/**
	 * Checks the current health percentage against a list of thresholds.<br>
	 * It triggers specific actions like spawning servants when certain levels are reached.<br>
	 * This method is synchronized to ensure thread safety during state changes.
	 * @param hpPercentage The current health percentage of the creature.
	 */
	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch (percent)
				{
					case 40:
						if (!isAlreadyDead())
						{
							SkillEngine.getInstance().getSkill(getOwner(), 19213, 1, getOwner()).useSkill(); // Play Dead
						}
						
						percents.remove(percent);
						break;
				}
			}
			break;
		}
	}
}
