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
import java.util.concurrent.locks.ReentrantLock;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FLY_TIME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_HP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the life statistics for a {@link Player}.<br>
 * It handles health and mana updates specifically for player entities.<br>
 * It extends {@link CreatureLifeStats} to provide specialized behavior for players.
 * @author ATracer, sphinx
 */
public class PlayerLifeStats extends CreatureLifeStats<Player>
{
	protected int currentFp;
	private final ReentrantLock fpLock = new ReentrantLock();
	private Future<?> flyRestoreTask;
	private Future<?> flyReduceTask;
	
	/**
	 * Creates a new {@code PlayerLifeStats} instance for a specific player.<br>
	 * This constructor initializes the life statistics using data from the {@link Player}.<br>
	 * It sets the initial health, mana, and fly point values.
	 * @param owner The {@code Player} who owns these life statistics.
	 */
	public PlayerLifeStats(Player owner)
	{
		super(owner, owner.getGameStats().getMaxHp().getCurrent(), owner.getGameStats().getMaxMp().getCurrent());
		currentFp = owner.getGameStats().getFlyTime().getCurrent();
	}
	
	/**
	 * This method is called when the {@code Player} loses health.<br>
	 * It updates the HP packet, triggers restoration tasks, and notifies the group.
	 */
	@Override
	protected void onReduceHp()
	{
		sendHpPacketUpdate();
		triggerRestoreTask();
		sendGroupPacketUpdate();
	}
	
	/**
	 * This method is called when the {@code Mp} of a player decreases.<br>
	 * It updates the mana packet, triggers restoration tasks, and notifies the group.
	 */
	@Override
	protected void onReduceMp()
	{
		sendMpPacketUpdate();
		triggerRestoreTask();
		sendGroupPacketUpdate();
	}
	
	/**
	 * Handles the logic when a {@code Player} gains MP.<br>
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
		if (value > 0)
		{
			sendMpPacketUpdate();
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			sendGroupPacketUpdate();
		}
	}
	
	/**
	 * Handles the logic when a {@code Player} gains health.<br>
	 * This method updates the status and sends packets to the client.
	 * @param type The category of the health change.
	 * @param value The amount of health gained.
	 * @param skillId The ID of the skill used to gain health.
	 * @param log The log information for the action.
	 */
	@Override
	protected void onIncreaseHp(TYPE type, int value, int skillId, LOG log)
	{
		if (isFullyRestoredHp())
		{
			// FIXME: Temp Fix: Reset aggro list when hp is full.
			owner.getAggroList().clear();
		}
		
		if (value > 0)
		{
			sendHpPacketUpdate();
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			sendGroupPacketUpdate();
		}
	}
	
	/**
	 * Updates the group status for the player.<br>
	 * This method checks if the {@code owner} is in a team.<br>
	 * If they are, it starts a task using {@link TeamEffectUpdater}.
	 */
	private void sendGroupPacketUpdate()
	{
		if (owner.isInTeam())
		{
			TeamEffectUpdater.getInstance().startTask(owner);
		}
	}
	
	/**
	 * Updates the current health, mana, and fp to match their maximum values.<br>
	 * This method ensures that {@code currentHp} equals {@code getMaxHp}.<br>
	 * It also ensures that {@code currentMp} equals {@code getMaxMp}.<br>
	 * Finally, it sets {@code currentFp} to the value of {@code getMaxFp} if the player is alive.
	 */
	@Override
	public void synchronizeWithMaxStats()
	{
		if (isAlreadyDead())
		{
			return;
		}
		
		super.synchronizeWithMaxStats();
		final int maxFp = getMaxFp();
		if (currentFp != maxFp)
		{
			currentFp = maxFp;
		}
	}
	
	/**
	 * Updates the current health and mana values.<br>
	 * This method ensures that {@code currentHp} and {@code currentMp} do not exceed their maximum limits.<br>
	 * It also triggers a restoration task if the stats are not fully restored.
	 */
	@Override
	public void updateCurrentStats()
	{
		super.updateCurrentStats();
		
		if (getMaxFp() < currentFp)
		{
			currentFp = getMaxFp();
		}
		
		if (!owner.isFlying() && !owner.isInSprintMode())
		{
			triggerFpRestore();
		}
	}
	
	/**
	 * Updates the player's health point status.<br>
	 * This method adds a broadcast mask to the {@link Player} object.<br>
	 * It ensures that the client receives the latest HP information.
	 */
	public void sendHpPacketUpdate()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_HP_STAT);
	}
	
	/**
	 * Sends the current health points to the player.<br>
	 * This method uses {@code sendPacket} to transmit an {@code SM_STATUPDATE_HP} packet.<br>
	 * It provides the owner with their {@code currentHp} and {@code getMaxHp} values.
	 */
	public void sendHpPacketUpdateImpl()
	{
		PacketSendUtility.sendPacket(owner, new SM_STATUPDATE_HP(currentHp, getMaxHp()));
	}
	
	/**
	 * Updates the MP status for the player.<br>
	 * This method adds a broadcast mask to the {@link Player} object.<br>
	 * It ensures that other clients receive the updated MP information.
	 */
	public void sendMpPacketUpdate()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_MP_STAT);
	}
	
	/**
	 * Sends the current MP status to the player.<br>
	 * This method uses {@code sendPacket} to deliver an {@code SM_STATUPDATE_MP} packet.<br>
	 * It provides the player with their updated {@code currentMp} and {@code getMaxMp} values.
	 */
	public void sendMpPacketUpdateImpl()
	{
		PacketSendUtility.sendPacket(owner, new SM_STATUPDATE_MP(currentMp, getMaxMp()));
	}
	
	/**
	 * Retrieves the current FP value.<br>
	 * This method currently returns a default value of {@code 0}.
	 * @return The current FP as an {@code int}.
	 */
	@Override
	public int getCurrentFp()
	{
		return currentFp;
	}
	
	/**
	 * Retrieves the maximum amount of {@code fp}.<br>
	 * This method currently returns a default value.
	 * @return The maximum {@code fp} value.
	 */
	@Override
	public int getMaxFp()
	{
		return owner.getGameStats().getFlyTime().getCurrent();
	}
	
	/**
	 * Calculates the current Fly Point percentage.<br>
	 * It compares {@code currentFp} against the value from {@code getMaxFp}.
	 * @return The percentage of remaining Fly Points as an integer.
	 */
	public int getFpPercentage()
	{
		return (100 * currentFp) / getMaxFp();
	}
	
	/**
	 * Increases the FP of a creature based on the specified type and amount.<br>
	 * This method currently serves as a placeholder and returns {@code 0}.
	 * @param type The {@code TYPE} of the FP increase.
	 * @param value The amount to increase by.
	 * @return The current result of the operation, which is always {@code 0}.
	 */
	@Override
	public int increaseFp(TYPE type, int value)
	{
		return this.increaseFp(type, value, 0, LOG.REGULAR);
	}
	
	/**
	 * Increases the current FP value of the player.<br>
	 * This method checks if the player is alive before applying changes.<br>
	 * It ensures the new FP does not exceed the maximum allowed limit.
	 * @param type The {@code TYPE} of the increase.
	 * @param value The amount to add to the current FP.
	 * @param skillId The ID of the skill used for this increase.
	 * @param log The {@code LOG} object for recording the action.
	 * @return The updated FP value after the increase.
	 */
	public int increaseFp(TYPE type, int value, int skillId, LOG log)
	{
		fpLock.lock();
		
		try
		{
			if (isAlreadyDead())
			{
				return 0;
			}
			
			int newFp = currentFp + value;
			if (newFp > getMaxFp())
			{
				newFp = getMaxFp();
			}
			
			if (currentFp != newFp)
			{
				onIncreaseFp(type, newFp - currentFp, skillId, log);
				currentFp = newFp;
			}
		}
		finally
		{
			fpLock.unlock();
		}
		
		return currentFp;
		
	}
	
	/**
	 * Reduces the current FP value of the player.<br>
	 * This method ensures that the new value does not drop below {@code 0}.<br>
	 * It also triggers the {@code onReduceFp} logic.
	 * @param value The amount of FP to subtract.
	 * @return The updated current FP value.
	 */
	public int reduceFp(int value)
	{
		fpLock.lock();
		try
		{
			int newFp = currentFp - value;
			
			if (newFp < 0)
			{
				newFp = 0;
			}
			
			currentFp = newFp;
		}
		finally
		{
			fpLock.unlock();
		}
		
		onReduceFp();
		
		return currentFp;
	}
	
	/**
	 * Sets the current FP value for the player.<br>
	 * This method ensures that the value is never less than {@code 0}.<br>
	 * It also triggers the {@code onReduceFp} logic.
	 * @param value The new FP value to set.
	 * @return The updated FP value.
	 */
	public int setCurrentFp(int value)
	{
		fpLock.lock();
		try
		{
			int newFp = value;
			
			if (newFp < 0)
			{
				newFp = 0;
			}
			
			currentFp = newFp;
		}
		finally
		{
			fpLock.unlock();
		}
		
		onReduceFp();
		
		return currentFp;
	}
	
	/**
	 * Handles the logic when a player's FP increases.<br>
	 * This method updates the attack status and manages fly time broadcasts.
	 * @param type The {@code TYPE} of the update.
	 * @param value The amount to increase by.
	 * @param skillId The ID of the skill used.
	 * @param log The {@code LOG} information for the action.
	 */
	protected void onIncreaseFp(TYPE type, int value, int skillId, LOG log)
	{
		if (value > 0)
		{
			sendAttackStatusPacketUpdate(type, value, skillId, log);
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_FLY_TIME);
		}
	}
	
	/**
	 * Handles the logic when a player's FP is reduced.<br>
	 * This method updates the broadcast mask to notify others of changes in fly time.
	 */
	protected void onReduceFp()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_FLY_TIME);
	}
	
	/**
	 * Sends the current flight point update to the player.<br>
	 * This method uses {@code sendPacket} to deliver an {@code SM_FLY_TIME} packet.<br>
	 * It checks if the {@code owner} is {@code null} before sending.
	 */
	public void sendFpPacketUpdateImpl()
	{
		if (owner == null)
		{
			return;
		}
		
		PacketSendUtility.sendPacket(owner, new SM_FLY_TIME(currentFp, getMaxFp()));
	}
	
	/**
	 * Restores the player's flight points over time.<br>
	 * This method calls {@code int)} with a value of {@code 1}.<br>
	 * It uses the {@code NATURAL_FP} type to update the stats.
	 */
	public void restoreFp()
	{
		// how much fly time restoring per 1 second.
		increaseFp(TYPE.NATURAL_FP, 1);
	}
	
	/**
	 * Restores {@code currentFp} based on the player's natural regeneration rate.<br>
	 * This method checks if the {@code REGEN_FP} value is non-zero.<br>
	 * It then increases the FP by one-third of that value using {@code int)}.
	 */
	public void specialrestoreFp()
	{
		if (owner.getGameStats().getStat(StatEnum.REGEN_FP, 0).getCurrent() != 0)
		{
			increaseFp(TYPE.NATURAL_FP, owner.getGameStats().getStat(StatEnum.REGEN_FP, 0).getCurrent() / 3);
		}
	}
	
	/**
	 * Starts the process to restore {@code currentFp} for the player.<br>
	 * This method cancels any active reduction tasks and schedules a new restoration task.<br>
	 * It uses {@link LifeStatsRestoreService} to manage the background task.
	 */
	public void triggerFpRestore()
	{
		cancelFpReduce();
		
		restoreLock.lock();
		try
		{
			if ((flyRestoreTask == null) && !alreadyDead && !isFlyTimeFullyRestored())
			{
				flyRestoreTask = LifeStatsRestoreService.getInstance().scheduleFpRestoreTask(this);
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Stops the current flight point restoration process.<br>
	 * This method cancels the {@code flyRestoreTask} if it is currently running.<br>
	 * It sets the task reference to {@code null} after cancellation.
	 */
	public void cancelFpRestore()
	{
		restoreLock.lock();
		try
		{
			if ((flyRestoreTask != null) && !flyRestoreTask.isCancelled())
			{
				flyRestoreTask.cancel(false);
				flyRestoreTask = null;
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Triggers a reduction in the player's FP based on a specific cost.<br>
	 * This method calls {@code triggerFpReduce} to update the stats.
	 * @param costFp The amount of FP to be reduced.
	 */
	public void triggerFpReduceByCost(Integer costFp)
	{
		triggerFpReduce(costFp);
	}
	
	/**
	 * Starts the process to reduce the player's FP over time.<br>
	 * This method initiates a background task for FP reduction.<br>
	 * It is used to handle the gradual decrease of flight points.
	 */
	public void triggerFpReduce()
	{
		triggerFpReduce(null);
	}
	
	/**
	 * Starts a task to reduce the player's FP over time.<br>
	 * This method cancels any existing restore tasks before scheduling a new reduction.<br>
	 * It only triggers if the player is not dead and does not have unlimited flight permissions.
	 * @param costFp The amount of FP to be reduced by the task.
	 */
	private void triggerFpReduce(Integer costFp)
	{
		cancelFpRestore();
		restoreLock.lock();
		try
		{
			if ((flyReduceTask == null) && !alreadyDead && (owner.getAccessLevel() < AdminConfig.GM_FLIGHT_UNLIMITED) && !owner.isUnderNoFPConsum())
			{
				flyReduceTask = LifeStatsRestoreService.getInstance().scheduleFpReduceTask(this, costFp);
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Cancels the active flight point reduction task.<br>
	 * This method checks if {@code flyReduceTask} is running and stops it.<br>
	 * It sets the task reference to {@code null} after cancellation.
	 */
	public void cancelFpReduce()
	{
		restoreLock.lock();
		try
		{
			if ((flyReduceTask != null) && !flyReduceTask.isCancelled())
			{
				flyReduceTask.cancel(false);
				flyReduceTask = null;
			}
		}
		finally
		{
			restoreLock.unlock();
		}
	}
	
	/**
	 * Checks if the player's fly points are fully restored.<br>
	 * It compares the {@code currentFp} value against the maximum allowed amount.
	 * @return {@code true} if {@code currentFp} equals the result of {@code getMaxFp}, otherwise {@code false}.
	 */
	public boolean isFlyTimeFullyRestored()
	{
		return getMaxFp() == currentFp;
	}
	
	/**
	 * Stops all active tasks for this player.<br>
	 * This method calls the parent {@code cancelAllTasks()} method.<br>
	 * It also cancels specific FP restore and reduction tasks.
	 */
	@Override
	public void cancelAllTasks()
	{
		super.cancelAllTasks();
		cancelFpReduce();
		cancelFpRestore();
	}
	
	/**
	 * This method initiates the restoration process when a player is revived.<br>
	 * It calls {@code triggerRestoreTask} and {@code triggerFpRestore}.
	 */
	public void triggerRestoreOnRevive()
	{
		triggerRestoreTask();
		triggerFpRestore();
	}
}
