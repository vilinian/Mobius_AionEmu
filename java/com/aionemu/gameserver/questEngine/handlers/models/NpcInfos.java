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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a data model for storing information related to {@code NPC} entities.<br>
 * It is used by the quest engine to manage various properties of non-player characters.
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcInfos")
public class NpcInfos
{
	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	@XmlAttribute(name = "var", required = true)
	protected int var;
	@XmlAttribute(name = "quest_dialog", required = true)
	protected int DialogAction;
	@XmlAttribute(name = "close_dialog")
	protected int closeDialog;
	@XmlAttribute(name = "movie")
	protected int movie;
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is stored as an {@code int}.
	 * @return The unique integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return npcId;
	}
	
	/**
	 * Retrieves the current variable value for this {@link Monster}.<br>
	 * This value is stored in the {@code var} field.
	 * @return The integer value of the variable.
	 */
	public int getVar()
	{
		return var;
	}
	
	/**
	 * Retrieves the quest dialog action value.<br>
	 * This value is used to determine how a dialogue should behave.
	 * @return The {@code int} value of the {@code DialogAction}.
	 */
	public int getQuestDialog()
	{
		return DialogAction;
	}
	
	/**
	 * Retrieves the value of the {@code close_dialog} attribute.<br>
	 * This value determines if a dialog should be closed.
	 * @return The integer value of the close dialog property.
	 */
	public int getCloseDialog()
	{
		return closeDialog;
	}
	
	/**
	 * Retrieves the current value of the {@code movie} property.<br>
	 * This value is used to identify which movie should be played.
	 * @return The integer value of the movie.
	 */
	public int getMovie()
	{
		return movie;
	}
	
	/**
	 * Sets the movie ID for this NPC.<br>
	 * This updates the {@code movie} field.
	 * @param movie The new movie ID to assign.
	 */
	public void setMovie(int movie)
	{
		this.movie = movie;
	}
}
