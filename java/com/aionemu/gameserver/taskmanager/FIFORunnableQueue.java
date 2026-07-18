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
package com.aionemu.gameserver.taskmanager;

import com.aionemu.commons.utils.concurrent.ExecuteWrapper;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class provides a First-In-First-Out (FIFO) queue for managing {@code Runnable} tasks.<br>
 * It ensures that tasks are executed in the exact order they were submitted to the queue.
 * @author NB4L1
 * @param <T>
 */
public abstract class FIFORunnableQueue<T extends Runnable>extends FIFOSimpleExecutableQueue<T>
{
	/**
	 * Removes the first item from the queue and runs it.<br>
	 * It uses {@code execute} to handle the task execution.<br>
	 * The task is executed with a timeout of {@code ThreadPoolManager.MAXIMUM_RUNTIME_IN_MilliSEC_WITHOUT_WARNING}.
	 */
	@Override
	protected void removeAndExecuteFirst()
	{
		ExecuteWrapper.execute(removeFirst(), ThreadPoolManager.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING);
	}
}
