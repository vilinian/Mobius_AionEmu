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

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.questEngine.QuestEngine;

/**
 * This class represents the environment context for a quest.<br>
 * It stores relevant game objects and data needed during quest execution.<br>
 * It provides easy access to entities like {@link Player}, {@link Npc}, and {@link StaticObject}.
 * @author MrPoke
 */
public class QuestEnv
{
	private VisibleObject visibleObject;
	private Player player;
	private int questId;
	private int dialogId;
	private int extendedRewardIndex;
	
	/**
	 * Creates a new instance of {@code QuestEnv}.<br>
	 * This class stores the environment data for a specific quest interaction.
	 * @param visibleObject The {@link VisibleObject} involved in the quest.
	 * @param player The {@link Player} who is currently performing the quest.
	 * @param questId The unique identifier for the quest.
	 * @param dialogId The specific ID of the dialog being displayed.
	 */
	public QuestEnv(VisibleObject visibleObject, Player player, Integer questId, Integer dialogId)
	{
		super();
		this.visibleObject = visibleObject;
		this.player = player;
		this.questId = questId;
		this.dialogId = dialogId;
	}
	
	/**
	 * Retrieves the {@link VisibleObject} associated with this environment.<br>
	 * This method returns the object currently being interacted with.
	 * @return The {@code VisibleObject} instance.
	 */
	public VisibleObject getVisibleObject()
	{
		return visibleObject;
	}
	
	/**
	 * Sets the {@link VisibleObject} for this environment.<br>
	 * This updates the internal reference to a specific object.
	 * @param visibleObject The {@code VisibleObject} to set.
	 */
	public void setVisibleObject(VisibleObject visibleObject)
	{
		this.visibleObject = visibleObject;
	}
	
	/**
	 * Retrieves the {@link Player} who initiated this wedding.<br>
	 * This method returns the primary participant of the ceremony.
	 * @return The {@code Player} object representing the main character.
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Sets the {@link Player} associated with this task.<br>
	 * This updates the internal {@code player} field.
	 * @param player The {@code Player} object to set.
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Retrieves the unique identifier for the quest.<br>
	 * This value is linked to a specific {@code Quest}.
	 * @return The {@code Integer} ID of the quest, or {@code null} if not set.
	 */
	public Integer getQuestId()
	{
		return questId;
	}
	
	/**
	 * Sets the unique identifier for the quest.<br>
	 * This updates the {@code questId} field of this {@link QuestDrop}.
	 * @param questId The {@code Integer} ID of the quest to assign.
	 */
	public void setQuestId(Integer questId)
	{
		this.questId = questId;
	}
	
	/**
	 * Retrieves the unique identifier for the current dialog.<br>
	 * This value is used to identify which specific conversation is active.
	 * @return The {@code Integer} ID of the dialog.
	 */
	public Integer getDialogId()
	{
		return dialogId;
	}
	
	/**
	 * Retrieves the {@link DialogAction} associated with the current quest environment.<br>
	 * It uses the internal {@code dialogId} to fetch data from the {@link QuestEngine}.<br>
	 * If no dialog is found, it returns {@code DialogAction.NULL}.
	 * @return The {@code DialogAction} object or {@code DialogAction.NULL} if not found.
	 */
	public DialogAction getDialog()
	{
		final DialogAction dialog = QuestEngine.getInstance().getDialog(dialogId);
		if (dialog == null)
		{
			return DialogAction.NULL;
		}
		
		return dialog;
	}
	
	/**
	 * Sets the unique identifier for the current dialog.<br>
	 * This value is used to track which conversation step is active.
	 * @param dialogId The {@code Integer} ID of the dialog to set.
	 */
	public void setDialogId(Integer dialogId)
	{
		this.dialogId = dialogId;
	}
	
	/**
	 * Retrieves the unique identifier of the current target object.<br>
	 * This method checks the type of {@code getVisibleObject}.<br>
	 * It returns the specific ID based on whether the object is an {@code Npc}, {@code Gatherable}, or {@code StaticObject}.<br>
	 * If no valid object is found, it returns {@code 0}.
	 * @return The unique identifier of the target object as an {@code int}.
	 */
	public int getTargetId()
	{
		if (visibleObject == null)
		{
			return 0;
		}
		else if (visibleObject instanceof Npc)
		{
			return ((Npc) visibleObject).getNpcId();
		}
		else if (visibleObject instanceof Gatherable)
		{
			return ((Gatherable) visibleObject).getObjectTemplate().getTemplateId();
		}
		else if (visibleObject instanceof StaticObject)
		{
			return visibleObject.getObjectTemplate().getTemplateId();
		}
		
		return 0;
	}
	
	/**
	 * Sets the index for an extended reward.<br>
	 * This value is used to identify specific rewards in a quest.
	 * @param index The {@code int} value to set as the new index.
	 */
	public void setExtendedRewardIndex(int index)
	{
		extendedRewardIndex = index;
	}
	
	/**
	 * Retrieves the current index for an extended reward.<br>
	 * This value is used to identify specific rewards in a quest sequence.
	 * @return The {@code int} value of the extended reward index.
	 */
	public int getExtendedRewardIndex()
	{
		return extendedRewardIndex;
	}
}
