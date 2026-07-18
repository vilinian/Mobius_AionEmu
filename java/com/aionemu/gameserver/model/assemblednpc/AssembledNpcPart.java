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
package com.aionemu.gameserver.model.assemblednpc;

import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate.AssembledNpcPartTemplate;

/**
 * Represents an individual component of an {@link com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate}.<br>
 * This class holds the data for a specific part used to construct an assembled NPC.
 * @author xTz
 */
public class AssembledNpcPart
{
	private final Integer object;
	private final AssembledNpcPartTemplate template;
	
	/**
	 * Creates a new instance of {@link AssembledNpcPart}.<br>
	 * This constructor initializes the part with an object ID and its corresponding template.
	 * @param object The unique identifier for the NPC object.
	 * @param template The {@code AssembledNpcPartTemplate} used to define this part.
	 */
	public AssembledNpcPart(Integer object, AssembledNpcPartTemplate template)
	{
		this.object = object;
		this.template = template;
	}
	
	/**
	 * Retrieves the unique identifier for this part.<br>
	 * This value is stored in the {@code object} field.
	 * @return The {@code Integer} ID of the object.
	 */
	public Integer getObject()
	{
		return object;
	}
	
	/**
	 * Retrieves the {@link AssembledNpcPartTemplate} associated with this part.<br>
	 * This method returns the configuration data for the NPC component.
	 * @return The {@code AssembledNpcPartTemplate} object.
	 */
	public AssembledNpcPartTemplate getAssembledNpcPartTemplate()
	{
		return template;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC.<br>
	 * This value is obtained from the {@link AssembledNpcPartTemplate}.
	 * @return The integer ID of the NPC.
	 */
	public int getNpcId()
	{
		return template.getNpcId();
	}
	
	/**
	 * Retrieves the unique static identifier for this part.<br>
	 * This value is fetched from the {@link AssembledNpcPartTemplate}.
	 * @return The {@code int} representing the static ID.
	 */
	public int getStaticId()
	{
		return template.getStaticId();
	}
}
