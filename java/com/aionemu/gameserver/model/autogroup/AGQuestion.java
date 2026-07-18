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
package com.aionemu.gameserver.model.autogroup;

/**
 * Represents the set of predefined questions used in the {@code AGGroup} system.<br>
 * These values are used to identify specific queries during the auto-grouping process.
 * @author xTz
 */
public enum AGQuestion
{
	FAILED,
	READY,
	ADDED;
	
	/**
	 * Checks if the current state of this {@link AGQuestion} is {@code FAILED}.<br>
	 * This method returns {@code true} if the status matches the {@code FAILED} enum constant.
	 * @return {@code true} if the question failed, otherwise {@code false}.
	 */
	public boolean isFailed()
	{
		return equals(AGQuestion.FAILED);
	}
	
	/**
	 * Checks if the current state is {@code READY}.<br>
	 * This method helps determine if the question is prepared.
	 * @return {@code true} if the state is {@code READY}, otherwise {@code false}.
	 */
	public boolean isReady()
	{
		return equals(AGQuestion.READY);
	}
	
	/**
	 * Checks if the current status is {@code ADDED}.<br>
	 * This method returns {@code true} if the question has been added.
	 * @return {@code true} if the state is {@code ADDED}, otherwise {@code false}.
	 */
	public boolean isAdded()
	{
		return equals(AGQuestion.ADDED);
	}
}
