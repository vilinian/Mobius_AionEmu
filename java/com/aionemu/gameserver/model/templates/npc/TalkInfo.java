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
package com.aionemu.gameserver.model.templates.npc;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the dialogue information for an {@link com.aionemu.gameserver.model.gameobjects.Npc}.<br>
 * It stores the text and data required to display conversations between players and non-player characters.
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TalkInfo")
public class TalkInfo
{
	@XmlAttribute(name = "distance")
	private int talkDistance = 2;
	@XmlAttribute(name = "delay")
	private int talkDelay;
	@XmlAttribute(name = "is_dialog")
	private boolean hasDialog;
	@XmlAttribute(name = "func_dialogs")
	private List<Integer> funcDialogIds;
	@XmlAttribute(name = "subdialog_type")
	private String subDialogType;
	
	/**
	 * Retrieves the distance required for a conversation.<br>
	 * This value is stored in the {@code talkDistance} field.
	 * @return the current talk distance as an {@code int}.
	 */
	public int getDistance()
	{
		return talkDistance;
	}
	
	/**
	 * Retrieves the time delay for this announcement.<br>
	 * This value is stored as an {@code int}.
	 * @return The current delay value.
	 */
	public int getDelay()
	{
		return talkDelay;
	}
	
	/**
	 * Checks if this NPC is configured to have a dialog.<br>
	 * It returns {@code true} if the NPC has valid dialog information.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the NPC is a dialog NPC, {@code false} otherwise.
	 */
	public boolean isDialogNpc()
	{
		return hasDialog;
	}
	
	/**
	 * Retrieves the list of function dialog IDs for this NPC.<br>
	 * This method returns {@code null} if no talk information is available.
	 * @return a {@code List<Integer>} containing the dialog IDs, or {@code null}.
	 */
	public List<Integer> getFuncDialogIds()
	{
		return funcDialogIds;
	}
	
	/**
	 * Retrieves the specific sub-dialog type for this NPC.<br>
	 * This method checks if {@code talkInfo} exists before accessing the value.
	 * @return The sub-dialog type as a {@code String}, or {@code null} if no information is available.
	 */
	public String getSubDialogType()
	{
		return subDialogType;
	}
}
