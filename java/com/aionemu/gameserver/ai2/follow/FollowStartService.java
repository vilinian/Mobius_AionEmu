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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This service handles the initial logic for starting a follow behavior.<br>
 * It manages the transition when a {@link Summon} begins to track its owner.<br>
 * It ensures that the following state is initialized correctly within the AI system.
 * @author xTz
 */
public class FollowStartService
{
	/**
	 * Schedules a recurring task to check if a {@link Summon} is following a {@link Creature}.<br>
	 * This method uses the {@code ThreadPoolManager} to run the check every 1000 milliseconds.
	 * @param follower The {@code Summon} entity that should be performing the follow action.
	 * @param leading The {@code Creature} entity that is being followed.
	 * @return A {@code Future} object representing the scheduled task.
	 */
	public static Future<?> newFollowingToTargetCheckTask(Summon follower, Creature leading)
	{
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowSummonTaskAI(leading, follower), 1000, 1000);
	}
}
