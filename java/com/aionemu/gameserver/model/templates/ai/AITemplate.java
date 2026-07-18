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
package com.aionemu.gameserver.model.templates.ai;

import com.aionemu.gameserver.model.ai.Ai;
import com.aionemu.gameserver.model.ai.Bombs;
import com.aionemu.gameserver.model.ai.Summons;

/**
 * This class serves as a data template for defining the behavior of {@link Ai} entities.<br>
 * It stores configuration settings used to initialize AI logic and behaviors.
 * @author xTz
 */
public class AITemplate
{
	private int npcId;
	private Summons summons;
	private Bombs bombs;
	
	/**
	 * Creates a new instance of the {@code AITemplate} class.<br>
	 * This constructor initializes a default template for AI behavior.
	 */
	public AITemplate()
	{
	}
	
	/**
	 * Creates a new {@code AITemplate} using an existing {@link Ai} object.<br>
	 * This method copies the data from the provided {@code template}.
	 * @param template The {@code Ai} object to copy data from.
	 */
	public AITemplate(Ai template)
	{
		summons = template.getSummons();
		bombs = template.getBombs();
		npcId = template.getNpcId();
	}
	
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
	 * Retrieves the {@link Summons} object associated with this AI.<br>
	 * This method returns the current summon data.
	 * @return the {@code Summons} object or {@code null} if no summons exist.
	 */
	public Summons getSummons()
	{
		return summons;
	}
	
	/**
	 * Retrieves the {@code Bombs} object associated with this AI.<br>
	 * This method returns the current bomb data for the NPC.
	 * @return The {@link Bombs} instance.
	 */
	public Bombs getBombs()
	{
		return bombs;
	}
}
