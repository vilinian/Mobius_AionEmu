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
package com.aionemu.gameserver.ai2.follow;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class handles the artificial intelligence for a {@link Summon} following its owner.<br>
 * It manages the movement logic to ensure the summon stays near the {@link Player}.<br>
 * The task is executed as a {@code Runnable} to update the summon's position periodically.
 * @author xTz
 */
public class FollowSummonTaskAI implements Runnable
{
	private final Creature target;
	private final Summon summon;
	private final Player master;
	private float targetX;
	private float targetY;
	private float targetZ;
	private final Future<?> task;
	
	/**
	 * Creates a new task for a {@link Summon} to follow a specific target.<br>
	 * This constructor initializes the required coordinates and links the task.
	 * @param target The {@code Creature} that the summon should follow.
	 * @param summon The {@code Summon} instance performing the action.
	 */
	public FollowSummonTaskAI(Creature target, Summon summon)
	{
		this.target = target;
		this.summon = summon;
		master = summon.getMaster();
		task = summon.getMaster().getController().getTask(TaskId.SUMMON_FOLLOW);
		setLeadingCoordinates();
	}
	
	/**
	 * Updates the internal coordinates of the {@code target}.<br>
	 * It retrieves the current X, Y, and Z values from the {@link Creature} object.<br>
	 * These values are used to track the destination for movement.
	 */
	private void setLeadingCoordinates()
	{
		targetX = target.getX();
		targetY = target.getY();
		targetZ = target.getZ();
	}
	
	@Override
	public void run()
	{
		if ((target == null) || (summon == null) || (master == null))
		{
			if (task != null)
			{
				task.cancel(true);
			}
			return;
		}
		
		if (!isInMasterRange())
		{
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.DISTANCE);
			return;
		}
		
		if (!isInTargetRange())
		{
			if ((targetX != target.getX()) || (targetY != target.getY()) || (targetZ != target.getZ()))
			{
				setLeadingCoordinates();
				onOutOfTargetRange();
			}
		}
		else if (!master.equals(target))
		{
			onDestination();
		}
	}
	
	/**
	 * Checks if the {@code target} is within a specific distance of the {@code summon}.<br>
	 * It uses a range value of {@code 2}.
	 * @return {@code true} if the target is in range, {@code false} otherwise.
	 */
	private boolean isInTargetRange()
	{
		return MathUtil.isIn3dRange(target, summon, 2);
	}
	
	/**
	 * Checks if the {@link Summon} is within a specific distance of its master.<br>
	 * It uses a range of {@code 50} units to perform this check.
	 * @return {@code true} if the summon is close enough to the master, {@code false} otherwise.
	 */
	private boolean isInMasterRange()
	{
		return MathUtil.isIn3dRange(master, summon, 50);
	}
	
	/**
	 * This method is called when the summon reaches its destination.<br>
	 * It triggers an {@code ATTACK} event on the {@link Creature} target.
	 */
	protected void onDestination()
	{
		summon.getAi2().onCreatureEvent(AIEventType.ATTACK, target);
	}
	
	/**
	 * Handles the logic when the target is outside of the allowed range.<br>
	 * This method triggers a {@code MOVE_VALIDATE} event for the {@link Summon}.
	 */
	private void onOutOfTargetRange()
	{
		summon.getAi2().onGeneralEvent(AIEventType.MOVE_VALIDATE);
	}
}
