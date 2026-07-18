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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;

/**
 * This class serves as a base for all crafting-related tasks in the skill engine.<br>
 * It provides common functionality for handling item creation logic within {@link AbstractInteractionTask}.
 * @author ATracer
 * @author synchro2
 * @author Antraxx
 * @author Kamikaze
 */
public abstract class AbstractCraftTask extends AbstractInteractionTask
{
	protected int maxSuccessValue = 40;
	protected int maxFailureValue = 60;
	protected int currentSuccessValue;
	protected int currentFailureValue;
	protected int skillLvlDiff;
	protected CraftCritType critType = CraftCritType.NONE;
	protected ItemQuality itemQuality;
	
	protected enum CraftCritType
	{
		NONE(0),
		INSTANT(1), // Junk - Gray
		BLUE(2), // Common - White
		PURPLE(3); // Superior - Green
		
		private final int critId;
		
		private CraftCritType(int critId)
		{
			this.critId = critId;
		}
		
		public int getCritId()
		{
			return critId;
		}
		
		public int getPacketId()
		{
			return critId > 0 ? critId : 1;
		}
	}
	
	/**
	 * Initializes a new crafting task.<br>
	 * This constructor sets up the basic requirements for a craft action.<br>
	 * It links the player who starts the task with the object they are interacting with.
	 * @param requestor The {@link Player} who is performing the craft.
	 * @param responder The {@link VisibleObject} being interacted with.
	 * @param skillLvlDiff The difference in skill levels between the players involved.
	 */
	public AbstractCraftTask(Player requestor, VisibleObject responder, int skillLvlDiff)
	{
		super(requestor, responder);
		this.skillLvlDiff = skillLvlDiff;
	}
	
	/**
	 * Handles the logic for a single step in a crafting interaction.<br>
	 * It checks if the task has reached a success or failure state.<br>
	 * If neither is met, it calls {@code analyzeInteraction} and updates the UI.
	 * @return {@code true} if the task failed, {@code false} if it is still in progress or succeeded.
	 */
	@Override
	protected boolean onInteraction()
	{
		if (currentSuccessValue == maxSuccessValue)
		{
			return onSuccessFinish();
		}
		
		if (currentFailureValue == maxFailureValue)
		{
			onFailureFinish();
			return true;
		}
		
		analyzeInteraction();
		
		sendInteractionUpdate();
		return false;
	}
	
	/**
	 * Calculates the success or failure progress for a crafting task.<br>
	 * This method uses {@code skillLvlDiff} to determine the difficulty.<br>
	 * It updates either {@code currentSuccessValue} or {@code currentFailureValue}.<br>
	 * The values are capped at {@code maxFailureValue}.
	 */
	protected void analyzeInteraction()
	{
		final int multi = Math.max(0, 33 - (skillLvlDiff * 5));
		if (Rnd.get(100) > multi)
		{
			currentSuccessValue += Rnd.get(maxSuccessValue / (multi + 1) / 2, maxSuccessValue);
		}
		else
		{
			currentFailureValue += Rnd.get(maxFailureValue / (multi + 1) / 2, maxFailureValue);
		}
		
		if (currentSuccessValue >= maxFailureValue)
		{
			currentSuccessValue = maxFailureValue;
		}
		else if (currentFailureValue >= maxFailureValue)
		{
			currentFailureValue = maxFailureValue;
		}
	}
	
	protected abstract void sendInteractionUpdate();
	
	protected abstract boolean onSuccessFinish();
	
	protected abstract void onFailureFinish();
}
