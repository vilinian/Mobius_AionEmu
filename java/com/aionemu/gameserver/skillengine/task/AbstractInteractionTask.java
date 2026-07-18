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
package com.aionemu.gameserver.skillengine.task;

import java.util.concurrent.Future;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class serves as a base for tasks involving interactions between game entities.<br>
 * It provides the foundation for executing asynchronous actions within the {@code task} system.
 * @author ATracer
 * @author Antraxx
 */
public abstract class AbstractInteractionTask
{
	protected Future<?> task;
	protected Player requestor;
	protected VisibleObject responder;
	
	/**
	 * Creates a new interaction task between two entities.<br>
	 * This constructor initializes the {@code requestor} and the {@code responder}.<br>
	 * If the {@code responder} is {@code null}, it defaults to the {@code requestor}.
	 * @param requestor The {@link Player} who initiates the action.
	 * @param responder The {@link VisibleObject} that reacts to the action.
	 */
	public AbstractInteractionTask(Player requestor, VisibleObject responder)
	{
		this.requestor = requestor;
		if (responder == null)
		{
			this.responder = requestor;
		}
		else
		{
			this.responder = responder;
		}
	}
	
	/**
	 * Called on each interaction
	 * @return
	 */
	protected abstract boolean onInteraction();
	
	/**
	 * Called when interaction is complete
	 */
	protected abstract void onInteractionFinish();
	
	/**
	 * Called before interaction is started
	 */
	protected abstract void onInteractionStart();
	
	/**
	 * Called when interaction is not complete and need to be aborted
	 */
	protected abstract void onInteractionAbort();
	
	/**
	 * Starts the execution of the interaction task.<br>
	 * It schedules a recurring task using {@link ThreadPoolManager}.<br>
	 * The task validates participants and executes the interaction logic based on {@link CraftConfig}.
	 */
	public void start()
	{
		onInteractionStart();
		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			if (!validateParticipants())
			{
				stop(true);
			}
			
			final boolean stopTask = onInteraction();
			if (stopTask)
			{
				stop(false);
			}
		}, CraftConfig.CRAFT_TIMER_DELAY, CraftConfig.CRAFT_TIMER_PERIOD);
	}
	
	/**
	 * Stops the current interaction task.<br>
	 * It cancels the running {@code Future} if it is still active.<br>
	 * If {@code participantNull} is {@code false}, it triggers the completion logic.
	 * @param participantNull Indicates whether a participant is null.
	 */
	public void stop(boolean participantNull)
	{
		if (!participantNull)
		{
			onInteractionFinish();
		}
		
		if ((task != null) && !task.isCancelled())
		{
			task.cancel(false);
			task = null;
		}
	}
	
	/**
	 * Cancels the current task immediately.<br>
	 * It triggers the {@code onInteractionAbort} logic.<br>
	 * This method also calls {@code stop} with a value of {@code false}.
	 */
	public void abort()
	{
		onInteractionAbort();
		stop(false);
	}
	
	/**
	 * Checks if the task is currently running.<br>
	 * It returns {@code true} if the {@code task} is not {@code null} and has not been cancelled.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the task is still active, {@code false} otherwise.
	 */
	public boolean isInProgress()
	{
		return (task != null) && !task.isCancelled();
	}
	
	/**
	 * Checks if the participants for this task are valid.<br>
	 * It verifies that the {@code requestor} is not {@code null}.
	 * @return {@code true} if the {@code requestor} exists, otherwise {@code false}.
	 */
	public boolean validateParticipants()
	{
		return requestor != null;
	}
}
