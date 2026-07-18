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
package com.aionemu.gameserver.model.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.instance_bonusatrr.InstanceBonusAttr;
import com.aionemu.gameserver.model.templates.instance_bonusatrr.InstancePenaltyAttr;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Represents a buff applied to an instance that modifies character statistics.<br>
 * It manages the application of various {@link IStatFunction} types to affect the game state.
 * @author xTz
 */
public class InstanceBuff implements StatOwner
{
	private Future<?> task;
	private final List<IStatFunction> functions = new ArrayList<>();
	private final InstanceBonusAttr instanceBonusAttr;
	private long startTime;
	
	/**
	 * Creates a new {@link InstanceBuff} object.<br>
	 * This constructor initializes the buff using data from {@code DataManager}.
	 * @param buffId The unique identifier for the instance buff.
	 */
	public InstanceBuff(int buffId)
	{
		instanceBonusAttr = DataManager.INSTANCE_BUFF_DATA.getInstanceBonusattr(buffId);
	}
	
	/**
	 * Applies the instance buff effects to a specific {@link Player}.<br>
	 * This method calculates penalties and adds them to the player's stats.<br>
	 * It also schedules a task if the provided {@code time} is not {@code 0}.
	 * @param player The {@link Player} who will receive the buff effect.
	 * @param time The duration in milliseconds for the scheduled task.
	 */
	public void applyEffect(Player player, int time)
	{
		if (hasInstanceBuff() || (instanceBonusAttr == null))
		{
			return;
		}
		
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new InstanceBuffTask(player), time);
		}
		
		startTime = System.currentTimeMillis();
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr())
		{
			final StatEnum stat = instancePenaltyAttr.getStat();
			final int statToModified = player.getGameStats().getStat(stat, 0).getBase();
			final int value = instancePenaltyAttr.getValue();
			final int valueModified = instancePenaltyAttr.getFunc().equals(Func.PERCENT) ? ((statToModified * value) / 100) : (value);
			functions.add(new StatAddFunction(stat, valueModified, true));
		}
		
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the effect from the specified {@link Player}.<br>
	 * This method clears all internal functions.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} who currently has this effect applied.
	 */
	public void endEffect(Player player)
	{
		functions.clear();
		if (hasInstanceBuff())
		{
			task.cancel(true);
		}
		
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Applies a specific pledge effect to a player.<br>
	 * This method adds new stat functions based on the instance bonus attributes.<br>
	 * It also updates the {@code buffId} on the {@link Player} object.
	 * @param player The {@link Player} who will receive the pledge.
	 * @param buffId The unique identifier for the pledge to apply.
	 */
	public void applyPledge(Player player, int buffId)
	{
		if (instanceBonusAttr == null)
		{
			return;
		}
		
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr())
		{
			if (instancePenaltyAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
		}
		
		player.setBonusId(buffId);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Removes the active pledge from a {@link Player}.<br>
	 * This method clears all associated functions and resets the bonus ID.<br>
	 * It also notifies the player's game stats to stop the effect.
	 * @param player The {@code Player} object whose pledge needs to be ended.
	 */
	public void endPledge(Player player)
	{
		functions.clear();
		player.setBonusId(0);
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Applies a pledge effect to a specific player.<br>
	 * This method sets the bonus ID and adds relevant stat functions.<br>
	 * It also schedules a task if the provided time is not {@code 0}.
	 * @param player The {@link Player} who will receive the buff.
	 * @param buffId The unique identifier for the pledge effect.
	 * @param time The duration in milliseconds to schedule the task.
	 */
	public void applyPledgeDuration(Player player, int buffId, int time)
	{
		if (hasInstanceBuff() || (instanceBonusAttr == null))
		{
			return;
		}
		
		if (time != 0)
		{
			task = ThreadPoolManager.getInstance().schedule(new InstanceBuffTask(player), time);
		}
		
		startTime = System.currentTimeMillis();
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr())
		{
			if (instancePenaltyAttr.getFunc().equals(Func.PERCENT))
			{
				functions.add(new StatRateFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
			else
			{
				functions.add(new StatAddFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
		}
		
		player.setBonusId(buffId);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * This method removes the pledge duration from a {@link Player}.<br>
	 * It clears all active functions and cancels any running tasks.<br>
	 * The player's bonus ID is reset to {@code 0}.<br>
	 * Finally, it calls {@code endEffect} on the player's game stats.
	 * @param player The {@link Player} whose pledge duration will be ended.
	 */
	public void endPledgeDuration(Player player)
	{
		functions.clear();
		if (hasInstanceBuff())
		{
			task.cancel(true);
			player.setBonusId(0);
		}
		
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Calculates the time left for this buff.<br>
	 * It returns the difference between the current time and the start time.<br>
	 * The result is provided in seconds.
	 * @return The remaining time as an {@code int}.
	 */
	public int getRemaningTime()
	{
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	private class InstanceBuffTask implements Runnable
	{
		private final Player player;
		
		public InstanceBuffTask(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			endEffect(player);
			if (player.getBonusId() > 0)
			{
				endPledgeDuration(player);
			}
		}
	}
	
	/**
	 * Checks if the instance buff is currently active.<br>
	 * It verifies that the internal task is not {@code null}.<br>
	 * It also ensures that the task has not finished yet.
	 * @return {@code true} if the buff is still running, otherwise {@code false}.
	 */
	public boolean hasInstanceBuff()
	{
		return (task != null) && !task.isDone();
	}
}
