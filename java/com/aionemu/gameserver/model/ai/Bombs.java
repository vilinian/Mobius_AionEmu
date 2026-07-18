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
package com.aionemu.gameserver.model.ai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class manages the configuration and data for various types of bombs in the game.<br>
 * It serves as a model to store properties used by the {@code ai} system.
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bombs")
public class Bombs
{
	@XmlElement(name = "bomb")
	private BombTemplate bombTemplate;
	
	/**
	 * Retrieves the template for a {@link BombTemplate}.<br>
	 * This method returns the current {@code bombTemplate} object.
	 * @return The {@code BombTemplate} associated with this instance.
	 */
	public BombTemplate getBombTemplate()
	{
		return bombTemplate;
	}
}
