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

import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the life-related statistics for a {@link Creature}.<br>
 * It handles core mechanics such as health points, recovery, and status effects.<br>
 * It serves as a base container for different types of creature life data.
 * @author ATracer
 * @param <T>
 */
public abstract class CreatureLifeStats<T extends Creature>
{
	private static final Logger log = LoggerFactory.getLogger(CreatureLifeStats.class);
	protected int currentHp;
	protected int currentMp;
	protected boolean alreadyDead = false;
	protected T owner;
	private final Lock hpLock = new ReentrantLock();
	private final Lock mpLock = new ReentrantLock();
	protected final Lock restoreLock = new ReentrantLock();
	protected volatile Future<?> lifeRestoreTask;
	
	/**
	 * Creates a new instance of {@link CreatureLifeStats}.<br>
	 * This constructor initializes the stats for a specific creature.
	 * @param owner The {@code Creature} that owns these life statistics.
	 * @param currentHp The initial health points for the creature.
	 * @param currentMp The initial mana points for the creature.
	 */
	public CreatureLifeStats(T owner, int currentHp, int currentMp)
	{
		this.owner = owner;
		this.currentHp = currentHp;
		this.currentMp = currentMp;
	}
	
	/**
	 * Retrieves the object that this controller manages.<br>
	 * This method returns the {@code owner} associated with this instance.
	 * @return The {@code VisibleObject} being controlled by this controller.
	 */
	public T getOwner()
	{
		return owner;
	}
	
	/**
	 * Retrieves the current health points of the creature.<br>
	 * This method returns the value stored in {@code currentHp}.
	 * @return The current amount of health points as an {@code int}.
	 */
	public int getCurrentHp()
	{
		return currentHp;
	}
	
	/**
	 * Retrieves the current amount of mana points.<br>
	 * This method returns the {@code currentMp} value for the creature.
	 * @return The current number of mana points as an {@code int}.
	 */
	public int getCurrentMp()
	{
		return currentMp;
	}
	
	/**
	 * Retrieves the maximum health points for the owner.<br>
	 * This method checks the {@link Creature} stats to find the current value.<br>
	 * If the value is {@code 0}, it returns {@code 1} as a fallback.
	 * @return The maximum health points as an {@code int}.
	 */
	public int getMaxHp()
	{
		int maxHp = this.getOwner().getGameStats().getMaxHp().getCurrent();
		if (maxHp == 0)
		{
			maxHp = 1;
			log.warn("CHECKPOINT: maxhp is 0 :" + this.getOwner().getGameStats());
		}
		
		return maxHp;
	}
	
	/**
	 * Retrieves the maximum MP of the owner.<br>
	 * This method calls {@code getOwner} to access the game stats.
	 * @return The current value of the maximum MP as an {@code int}.
	 */
	public int getMaxMp()
	{
		return this.getOwner().getGameStats().getMaxMp().getCurrent();
	}
	
	/**
	 * Checks if the creature is currently in a dead state.<br>
	 * This method returns the value of the {@code alreadyDead} flag.
	 * @return {@code true} if the creature is dead, {@code false} otherwise.
	 */
	public boolean isAlreadyDead()
	{
		return alreadyDead;
	}
	
	/**
	 * Reduces the current health points of this creature.<br>
	 * This method handles death logic if the value exceeds remaining health.<br>
	 * It also triggers the {@code onReduceHp} callback and notifies the owner's controller upon death.
	 * @param value The amount of health to subtract from the current HP.
	 * @param attacker The {@link Creature} that performed the attack.
	 * @return The new health point value after reduction.
	 */
	public int reduceHp(int value, Creature attacker)
	{
		if (attacker == null)
		{
			throw new NullPointerException("attacker");
		}
		
		boolean isDied = false;
		hpLock.lock();
		try
		{
			if (!alreadyDead)
			{
				int newHp = currentHp - value;
				
				if (newHp < 0)
				{
					newHp = 0;
					currentMp = 0;
					alreadyDead = true;
					isDied = true;
				}
				
				currentHp = newHp;
			}
		}
		finally
		{
			hpLock.unlock();
		}
		
		if (value != 0)
		{
			onReduceHp();
		}
		
		if (isDied)
		{
			getOwner().getController().onDie(attacker);
		}
		
		return currentHp;
	}
	
	/**
	 * Reduces the current mana points of the creature.<br>
	 * The value will not drop below {@code 0}.<br>
	 * This method triggers {@code onReduceMp} if the reduction is non-zero.
	 * @param value The amount of mana to subtract.
	 * @return The new current mana value after reduction.
	 */
	public int reduceMp(int value)
	{
		mpLock.lock();
		try
		{
			int newMp = currentMp - value;
			
			if (newMp < 0)
			{
				newMp = 0;
			}
			
			currentMp = newMp;
		}
		finally
		{
			mpLock.unlock();
		}
		
		if (value != 0)
		{
			onReduceMp();
		}
		
		return currentMp;
	}
	
	/**
	 * Sends an attack status update packet to the owner.<br>
	 * This method uses {@code broadcastPacketAndReceive} to notify the player.<br>
	 * It creates a new {@code SM_ATTACK_STATUS} packet with the provided details.
	 * @param type The type of the attack status.
	 * @param value The numerical value for the update.
	 * @param skillId The unique identifier for the skill used.
	 * @param log The log information to include in the packet.
	 */
	protected void sendAttackStatusPacketUpdate(TYPE type, int value, int skillId, LOG log)
	{
		if (owner == null)// possible?
		{
			return;
		}
		
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_ATTACK_STATUS(owner, owner, type, skillId, value, log));
	}
	
	/**
	 * Increases the current health points of the creature.<br>
	 * This method updates the {@code currentHp} based on the provided amount.<br>
	 * It uses a default skill ID and log type for the update.
	 * @param type The category or source of the health increase.
	 * @param value The amount of health to add.
	 * @return The new health point total after the increase.
	 */
	public int increaseHp(TYPE type, int value)
	{
		return this.increaseHp(type, value, 0, LOG.REGULAR);
	}
	
	/**
	 * Increases the current health points of the owner.<br>
	 * This method checks for disease status and death before applying changes.<br>
	 * It ensures that the new health value does not exceed {@code getMaxHp}.
	 * @param type The category or source type of the health increase.
	 * @param value The amount of health to add.
	 * @param skillId The unique identifier for the skill used.
	 * @param log The log object used for recording the action.
	 * @return The updated current health points after the operation.
	 */
	public int increaseHp(TYPE type, int value, int skillId, LOG log)
	{
		boolean hpIncreased = false;
		
		if (this.getOwner().getEffectController().isAbnormalSet(AbnormalState.DISEASE))
		{
			return currentHp;
		}
		
		hpLock.lock();
		try
		{
			if (isAlreadyDead())
			{
				return 0;
			}
			
			int newHp = currentHp + value;
			if (newHp > getMaxHp())
			{
				newHp = getMaxHp();
			}
			
			if (currentHp != newHp)
			{
				currentHp = newHp;
				hpIncreased = true;
			}
		}
		finally
		{
			hpLock.unlock();
		}
		
		if (hpIncreased)
		{
			onIncreaseHp(type, value, skillId, log);
		}
		
		return currentHp;
	}
	
	/**
	 * Increases the current MP of the creature.<br>
	 * This method updates the mana points based on a specific type and amount.<br>
	 * It uses {@code LOG.REGULAR} for logging purposes.
	 * @param type The category or source of the MP increase.
	 * @param value The amount of MP to add.
	 * @return The new current MP value after the increase.
	 */
	public int increaseMp(TYPE type, int value)
	{
		return this.increaseMp(type, value, 0, LOG.REGULAR);
	}
	
	/**
	 * Increases the current MP of the creature.<br>
	 * This method ensures that the new MP does not exceed {@code getMaxMp}.<br>
	 * It returns the updated MP value after the increase.
	 * @param type The category or source type of the MP change.
	 * @param value The amount of MP to add.
	 * @param skillId The unique identifier for the skill causing the change.
	 * @param log The {@code LOG} object used for recording the action.
	 * @return The new current MP value after the increase.
	 */
	public int increaseMp(TYPE type, int value, int skillId, LOG log)
	{
		boolean mpIncreased = false;
		mpLock.lock();
		try
		{
			if (isAlreadyDead())
			{
				return 0;
			}
			
			int newMp = currentMp + value;
			if (newMp > getMaxMp())
			{
				newMp = getMaxMp();
			}
			
			if (currentMp != newMp)
			{
				currentMp = newMp;
				mpIncreased = true;
			}
		}
		finally
		{
			mpLock.unlock();
		}
		
		if (mpIncreased)
		{
			onIncreaseMp(type, value, skillId, log);
		}
		
		return currentMp;
	}
	
	/**
	 * Restores health to the owner based on their current regeneration rate.<br>
	 * This method uses {@code int)} with the {@code NATURAL_HP} type.
	 */
	public void restoreHp()
	{
		increaseHp(TYPE.NATURAL_HP, getOwner().getGameStats().getHpRegenRate().getCurrent());
	}
	
	/**
	 * Restores the current MP of the creature.<br>
	 * This method uses {@code int)} to add mana.<br>
	 * The amount added is based on the owner's current MP regeneration rate.
	 */
	public void restoreMp()
	{
		increaseMp(TYPE.NATURAL_MP, getOwner().getGameStats().getMpRegenRate().getCurrent());
	}
	
	/**
	 * Starts a background task to restore life stats.<br>
	 * This method checks if the {@code lifeRestoreTask} is {@code null}.<br>
	 * It also ensures the creature is not already dead before scheduling.<br>
	 * The task is managed by the {@link LifeStatsRestoreService} class.
	 */
	public void triggerRestoreTask()
	{
		restoreLock.lock();
		try
		{
			if ((lifeRestoreTask == null) && !alreadyDead)
			{
				lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
			}
		}
		finally
		{
			restoreLock.unlock();
		}
		
	}
	
	/**
	 * Stops the current life restoration process.<br>
	 * This method cancels the {@code lifeRestoreTask} if it is not {@code null}.<br>
	 * It also sets the task reference to {@code null}.
	 */
	public void cancelRestoreTask()
	{
		restoreLock.lock();
		try
		{
			if (lifeRestoreTask != null)
			{
				lifeRestoreTask.cancel(false);
				lifeRestoreTask = null;
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Checks if the creature has reached its maximum health and mana.<br>
	 * It compares {@code currentHp} against {@code getMaxHp} and {@code currentMp} against {@code getMaxMp}.
	 * @return {@code true} if both stats are at their maximum values, otherwise {@code false}.
	 */
	public boolean isFullyRestoredHpMp()
	{
		return (getMaxHp() == currentHp) && (getMaxMp() == currentMp);
	}
	
	/**
	 * Checks if the current health points are at maximum capacity.<br>
	 * This method compares {@code currentHp} with the result of {@code getMaxHp}.
	 * @return {@code true} if the health is full, otherwise {@code false}.
	 */
	public boolean isFullyRestoredHp()
	{
		return getMaxHp() == currentHp;
	}
	
	/**
	 * Checks if the current MP is equal to the maximum MP.<br>
	 * This indicates whether the mana pool is completely full.
	 * @return {@code true} if the current MP matches the max MP, otherwise {@code false}.
	 */
	public boolean isFullyRestoredMp()
	{
		return getMaxMp() == currentMp;
	}
	
	/**
	 * Updates the current health and mana to match their maximum values.<br>
	 * This method ensures that {@code currentHp} equals {@code getMaxHp}.<br>
	 * It also ensures that {@code currentMp} equals {@code getMaxMp}.
	 */
	public void synchronizeWithMaxStats()
	{
		final int maxHp = getMaxHp();
		if (currentHp != maxHp)
		{
			currentHp = maxHp;
		}
		
		final int maxMp = getMaxMp();
		if (currentMp != maxMp)
		{
			currentMp = maxMp;
		}
	}
	
	/**
	 * Updates the current health and mana values.<br>
	 * This method ensures that {@code currentHp} and {@code currentMp} do not exceed their maximum limits.<br>
	 * It also triggers a restoration task if the stats are not fully restored.
	 */
	public void updateCurrentStats()
	{
		final int maxHp = getMaxHp();
		if (maxHp < currentHp)
		{
			currentHp = maxHp;
		}
		
		final int maxMp = getMaxMp();
		if (maxMp < currentMp)
		{
			currentMp = maxMp;
		}
		
		if (!isFullyRestoredHpMp())
		{
			triggerRestoreTask();
		}
	}
	
	/**
	 * Calculates the current health percentage of the creature.<br>
	 * It returns a value between {@code 1} and {@code 100}.<br>
	 * This method uses {@code getCurrentHp} and {@code getMaxHp}.
	 * @return The health percentage as an {@code int}.
	 */
	public int getHpPercentage()
	{
		if (((int) ((100f * currentHp) / getMaxHp()) == 0) && (currentHp > 0))
		{
			return 1;
		}
		
		return (int) ((100f * currentHp) / getMaxHp());
	}
	
	/**
	 * Calculates the current mana percentage of the creature.<br>
	 * This method compares {@code currentMp} against {@code getMaxMp}.<br>
	 * The result is returned as an integer value.
	 * @return The current mana percentage as an integer from 0 to 100.
	 */
	public int getMpPercentage()
	{
		return (int) ((100f * currentMp) / getMaxMp());
	}
	
	protected abstract void onIncreaseMp(TYPE type, int value, int skillId, LOG log);
	
	protected abstract void onReduceMp();
	
	protected abstract void onIncreaseHp(TYPE type, int value, int skillId, LOG log);
	
	protected abstract void onReduceHp();
	
	/**
	 * Increases the FP of a creature based on the specified type and amount.<br>
	 * This method currently serves as a placeholder and returns {@code 0}.
	 * @param type The {@code TYPE} of the FP increase.
	 * @param value The amount to increase by.
	 * @return The current result of the operation, which is always {@code 0}.
	 */
	public int increaseFp(TYPE type, int value)
	{
		return 0;
	}
	
	/**
	 * Retrieves the maximum amount of {@code fp}.<br>
	 * This method currently returns a default value.
	 * @return The maximum {@code fp} value.
	 */
	public int getMaxFp()
	{
		return 0;
	}
	
	/**
	 * Retrieves the current FP value.<br>
	 * This method currently returns a default value of {@code 0}.
	 * @return The current FP as an {@code int}.
	 */
	public int getCurrentFp()
	{
		return 0;
	}
	
	/**
	 * Cancels the active life restoration task.<br>
	 * This method calls {@code cancelRestoreTask} to stop any ongoing restoration.
	 */
	public void cancelAllTasks()
	{
		cancelRestoreTask();
	}
	
	/**
	 * Updates the current health points based on a percentage.<br>
	 * This method calculates {@code currentHp} using the provided value and {@code getMaxHp}.<br>
	 * It also updates the {@code alreadyDead} status if the new health is greater than {@code 0}.
	 * @param hpPercent The percentage of maximum health to set.
	 */
	public void setCurrentHpPercent(int hpPercent)
	{
		hpLock.lock();
		try
		{
			currentHp = (int) ((hpPercent / 100f) * getMaxHp());
			
			if (currentHp > 0)
			{
				alreadyDead = false;
			}
		}
		finally
		{
			hpLock.unlock();
		}
	}
	
	/**
	 * Sets the current health points for the creature.<br>
	 * This method updates the {@code currentHp} value and manages the {@code alreadyDead} state.<br>
	 * It also triggers a status update if the new health is below the maximum allowed amount.
	 * @param hp The new health value to set.
	 */
	public void setCurrentHp(int hp)
	{
		boolean hpNotAtMaxValue = false;
		hpLock.lock();
		try
		{
			currentHp = hp;
			
			if (currentHp > 0)
			{
				alreadyDead = false;
			}
			
			if (currentHp < getMaxHp())
			{
				hpNotAtMaxValue = true;
			}
		}
		finally
		{
			hpLock.unlock();
		}
		
		if (hpNotAtMaxValue)
		{
			onReduceHp();
		}
	}
	
	/**
	 * Sets the current mana points for the creature.<br>
	 * This method ensures that the value is never less than {@code 0}.<br>
	 * It also triggers the {@code onReduceMp} logic.
	 * @param value The new amount of mana to set.
	 * @return The final value of the current mana points.
	 */
	public int setCurrentMp(int value)
	{
		mpLock.lock();
		try
		{
			int newMp = value;
			
			if (newMp < 0)
			{
				newMp = 0;
			}
			
			currentMp = newMp;
		}
		finally
		{
			mpLock.unlock();
		}
		
		onReduceMp();
		return currentMp;
	}
	
	/**
	 * Updates the current MP based on a percentage of the maximum MP.<br>
	 * This method uses {@code mpPercent} to calculate the new value.<br>
	 * It ensures thread safety by using the {@code mpLock}.
	 * @param mpPercent The percentage of max MP to set.
	 */
	public void setCurrentMpPercent(int mpPercent)
	{
		mpLock.lock();
		try
		{
			currentMp = (int) ((mpPercent / 100f) * getMaxMp());
		}
		finally
		{
			mpLock.unlock();
		}
	}
}
