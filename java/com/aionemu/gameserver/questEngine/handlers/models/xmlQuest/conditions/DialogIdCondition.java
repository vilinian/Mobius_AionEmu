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
package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * This class represents a quest condition based on a specific dialog identifier.<br>
 * It checks if the current {@link QuestEnv} matches the required {@code DialogId}.
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DialogIdCondition")
public class DialogIdCondition extends QuestCondition
{
	@XmlAttribute(required = true)
	protected int value;
	
	/**
	 * Retrieves the current numerical value.<br>
	 * This method returns the {@code int} stored in the internal variable.
	 * @return The current value.
	 */
	public int getValue()
	{
		return value;
	}
	
	/**
	 * Checks if the current dialog ID matches the required condition.<br>
	 * This method compares the ID from {@link QuestEnv} against the stored {@code value}.<br>
	 * It supports both equality and inequality operations.
	 * @param env The environment containing the current quest data.
	 * @return {@code true} if the condition is met, otherwise {@code false}.
	 */
	@Override
	public boolean doCheck(QuestEnv env)
	{
		final int data = env.getDialogId();
		switch (getOp())
		{
			case EQUAL:
				return data == value;
			case NOT_EQUAL:
				return data != value;
			default:
				return false;
		}
	}
}
