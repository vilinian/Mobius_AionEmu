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
package com.aionemu.gameserver.network.sequrity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class handles the periodic flushing of network buffers.<br>
 * It ensures that pending data is sent out to maintain a smooth connection.<br>
 * It uses a {@code Timer} to execute cleanup tasks automatically.
 * @author NB4L1
 */
public final class NetFlusher
{
	private static final Timer _timer = new Timer(NetFlusher.class.getName(), true);
	
	/**
	 * Schedules a task to run repeatedly at a fixed time.<br>
	 * The task will execute every {@code interval} milliseconds.<br>
	 * This method uses an internal {@code Timer}.
	 * @param runnable The task to be executed.
	 * @param interval The delay between each execution in milliseconds.
	 */
	public static void add(Runnable runnable, long interval)
	{
		_timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					runnable.run();
				}
				catch (RuntimeException e)
				{
					e.printStackTrace();
				}
			}
		}, interval, interval);
	}
}
