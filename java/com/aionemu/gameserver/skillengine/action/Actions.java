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
package com.aionemu.gameserver.skillengine.action;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a container for various skill actions.<br>
 * It defines the collection of {@code Action} objects that can be executed by the skill engine.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Actions", propOrder =
{
	"actions"
})
public class Actions
{
	@XmlElements(
	{
		@XmlElement(name = "itemuse", type = ItemUseAction.class),
		@XmlElement(name = "mpuse", type = MpUseAction.class),
		@XmlElement(name = "hpuse", type = HpUseAction.class),
		@XmlElement(name = "dpuse", type = DpUseAction.class)
	})
	protected List<Action> actions;
	
	/**
	 * Retrieves the list of {@link Action} objects.<br>
	 * If the internal list is {@code null}, a new empty {@code ArrayList} is created.
	 * @return A {@code List} containing all available {@code Action} items.
	 */
	public List<Action> getActions()
	{
		if (actions == null)
		{
			actions = new ArrayList<>();
		}
		
		return actions;
	}
}
