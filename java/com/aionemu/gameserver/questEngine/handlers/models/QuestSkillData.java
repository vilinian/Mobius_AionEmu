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
package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the data structure for skills associated with a specific quest.<br>
 * It holds information required by the {@link com.aionemu.gameserver.questEngine.handlers.QuestHandler} to manage skill-related quest requirements or rewards.
 * @author vlog, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestSkillData")
public class QuestSkillData
{
	@XmlAttribute(name = "ids", required = true)
	protected List<Integer> skillIds;
	@XmlAttribute(name = "start_var")
	protected int startVar = 0;
	@XmlAttribute(name = "end_var", required = true)
	protected int endVar;
	@XmlAttribute(name = "var_num")
	protected int varNum = 0;
	
	/**
	 * Retrieves the list of required skill identifiers.<br>
	 * This method ensures a non-null {@code List<Integer>} is returned.
	 * @return a {@code List<Integer>} containing all skill IDs.
	 */
	public List<Integer> getSkillIds()
	{
		return skillIds;
	}
	
	/**
	 * Retrieves the number of variables associated with this quest skill data.<br>
	 * This value is stored in the {@code varNum} field.
	 * @return The current value of {@code varNum}.
	 */
	public int getVarNum()
	{
		return varNum;
	}
	
	/**
	 * Retrieves the starting variable value.<br>
	 * This value is used to initialize quest progress.
	 * @return The current {@code int} value of {@code startVar}.
	 */
	public int getStartVar()
	{
		return startVar;
	}
	
	/**
	 * Retrieves the ending variable value for this {@link Monster}.<br>
	 * This value is used to determine the completion state.
	 * @return The integer value of the {@code endVar} attribute.
	 */
	public int getEndVar()
	{
		return endVar;
	}
}
