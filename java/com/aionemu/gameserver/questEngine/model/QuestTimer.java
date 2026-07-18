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
package com.aionemu.gameserver.questEngine.model;

import java.util.Timer;
import java.util.TimerTask;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class manages the timing logic for specific quest objectives.<br>
 * It handles the countdowns and execution of time-based events within a {@code Quest}.
 * @author Hilgert
 */
public class QuestTimer
{
	private Timer timer;
	private int Time = 0;
	@SuppressWarnings("unused")
	private final int questId;
	private boolean isTicking = false;
	private final Player player;
	
	/**
	 * Creates a new {@code QuestTimer} instance.<br>
	 * This initializes the timer for a specific quest and player.<br>
	 * It converts the seconds into milliseconds for internal use.
	 * @param questId The unique identifier for the quest.
	 * @param seconds The total duration of the timer in seconds.
	 * @param player The {@link Player} who is performing the quest.
	 */
	public QuestTimer(int questId, int seconds, Player player)
	{
		this.questId = questId;
		Time = seconds * 1000;
		this.player = player;
	}
	
	/**
	 * Starts the quest timer and notifies the player.<br>
	 * This method initializes a new {@code Timer} to track the remaining time.<br>
	 * It sets the {@code isTicking} flag to {@code true}.<br>
	 * When the time expires, it calls {@code onEnd}.
	 */
	public void Start()
	{
		PacketSendUtility.sendMessage(player, "Timer started");
		timer = new Timer();
		isTicking = true;
		
		// TODO Send Packet that timer start
		final TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				PacketSendUtility.sendMessage(player, "Timer is over");
				onEnd();
			}
		};
		
		timer.schedule(task, Time);
	}
	
	/**
	 * Stops the active timer for this quest.<br>
	 * This method cancels the {@code Timer} task.<br>
	 * It also triggers the {@code onEnd} logic to finalize the process.
	 */
	public void Stop()
	{
		timer.cancel();
		onEnd();
	}
	
	/**
	 * This method is called when the quest timer finishes.<br>
	 * It sets the {@code isTicking} flag to {@code false}.
	 */
	public void onEnd()
	{
		// TODO Send Packet that timer end
		isTicking = false;
	}
	
	/**
	 * Checks if the quest timer is currently running.<br>
	 * This method returns {@code true} if the timer has started.<br>
	 * It returns {@code false} if the timer is stopped or inactive.
	 * @return The current ticking status of the timer.
	 */
	public boolean isTicking()
	{
		return isTicking;
	}
	
	/**
	 * Gets the current elapsed time for the quest.<br>
	 * This value is converted from milliseconds to seconds.
	 * @return The total number of seconds remaining or passed.
	 */
	public int getTimeSeconds()
	{
		return Time / 1000;
	}
}
