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
package system.handlers.ai.instance.azoturanFortress;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import system.handlers.ai.AggressiveNpcAI2;

/**
 * Handles the artificial intelligence for the {@code betrayericaronix} NPC.<br>
 * This class extends {@link AggressiveNpcAI2} to define specific behaviors within the Azoturan Fortress instance.
 * @author Antraxx
 */
@AIName("betrayericaronix")
public class BetrayerIcaronixAI2 extends AggressiveNpcAI2
{
	private final AtomicBoolean isStartEvent = new AtomicBoolean(false);
	
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
	 * Checks the current health percentage to trigger specific AI actions.<br>
	 * It evaluates whether a skill should be used or summons should be spawned.<br>
	 * This method updates the {@code spawnedPercent} tracker based on the results.
	 * @param hpPercentage The current health of the creature as an integer.
	 */
	private void checkPercentage(int hpPercentage)
	{
		if (hpPercentage <= 50)
		{
			if (isStartEvent.compareAndSet(false, true))
			{
				scheduleSpawnIcaronixTheBetrayer(getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				AI2Actions.deleteOwner(this);
			}
		}
	}
	
	/**
	 * Schedules the spawning of Icaronix The Betrayer.<br>
	 * This method uses {@link ThreadPoolManager} to delay the spawn by {@code 5000} milliseconds.
	 * @param x The X coordinate for the spawn location.
	 * @param y The Y coordinate for the spawn location.
	 * @param z The Z coordinate for the spawn location.
	 * @param h The horizontal rotation value for the spawned creature.
	 */
	private void scheduleSpawnIcaronixTheBetrayer(float x, float y, float z, byte h)
	{
		ThreadPoolManager.getInstance().schedule(() -> spawn(214599, x, y, z, h), 5000);
	}
}
