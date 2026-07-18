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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.services.LifeStatsRestoreService;

/**
 * This class represents the life statistics for {@link Npc} entities.<br>
 * It extends {@link CreatureLifeStats} to provide specific behavior for non-player characters.
 * @author ATracer
 */
public class NpcLifeStats extends CreatureLifeStats<Npc>
{
	/**
	 * Creates a new {@code NpcLifeStats} instance for a specific NPC.<br>
	 * This constructor initializes the life stats using the current HP and MP of the {@link Npc}.
	 * @param owner The {@code Npc} that owns these life statistics.
	 */
	public NpcLifeStats(Npc owner)
	{
		super(owner, owner.getGameStats().getMaxHp().getCurrent(), owner.getGameStats().getMaxMp().getCurrent());
	}
	
	/**
	 * Handles the logic when an {@code Npc} gains health.<br>
	 * This method updates the status and sends a packet to the client.
	 * @param type The category of the health change.
	 * @param value The amount of health gained.
	 * @param skillId The ID of the skill used to gain health.
	 * @param log The log information for the action.
	 */
	@Override
	protected void onIncreaseHp(TYPE type, int value, int skillId, LOG log)
	{
		sendAttackStatusPacketUpdate(type, value, skillId, log);
	}
	
	/**
	 * Handles the logic when an {@code Npc} gains MP.<br>
	 * This method is called during mana restoration events.<br>
	 * It allows for custom behavior based on the skill used.
	 * @param type The category of the mana increase.
	 * @param value The amount of MP gained.
	 * @param skillId The unique identifier of the skill used.
	 * @param log The {@link LOG} object used to record the action.
	 */
	@Override
	protected void onIncreaseMp(TYPE type, int value, int skillId, LOG log)
	{
		// nothing todo
	}
	
	/**
	 * This method is called when the {@code Npc} loses health.<br>
	 * It serves as a placeholder for future logic related to damage.
	 */
	@Override
	protected void onReduceHp()
	{
		// nothing todo
	}
	
	/**
	 * This method is called when the {@code Mp} of an NPC decreases.<br>
	 * It serves as a placeholder for any logic needed during mana reduction.
	 */
	@Override
	protected void onReduceMp()
	{
		// nothing todo
	}
	
	/**
	 * Starts a background task to restore life stats.<br>
	 * This method checks if the {@code lifeRestoreTask} is {@code null}.<br>
	 * It also ensures the creature is not already dead before scheduling.<br>
	 * The task is managed by the {@link LifeStatsRestoreService} class.
	 */
	@Override
	public void triggerRestoreTask()
	{
		restoreLock.lock();
		try
		{
			if ((lifeRestoreTask == null) && !alreadyDead)
			{
				lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleHpRestoreTask(this);
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Starts the resting process for the {@code Npc}.<br>
	 * This method calls {@code triggerRestoreTask} to begin recovery.
	 */
	public void startResting()
	{
		triggerRestoreTask();
	}
}
