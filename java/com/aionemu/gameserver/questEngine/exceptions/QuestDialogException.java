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
package com.aionemu.gameserver.questEngine.exceptions;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This exception is thrown when an error occurs during a quest dialog event.<br>
 * It helps identify issues related to {@link com.aionemu.gameserver.questEngine.model.QuestEnv} processing.
 * @author vlog
 */
public class QuestDialogException extends RuntimeException
{
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -4323594385872762590L;
	
	/**
	 * Creates a new {@link QuestDialogException} for the given environment.<br>
	 * This exception captures the specific quest and dialog IDs from the {@code QuestEnv}.
	 * @param env The {@code QuestEnv} containing the current quest context.
	 */
	public QuestDialogException(QuestEnv env)
	{
		super("Info: QuestID: " + env.getQuestId() + ", DialogID: " + env.getDialogId());
	}
}
