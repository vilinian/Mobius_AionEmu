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
package com.aionemu.gameserver.model.templates.minion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class defines the set of actions that a {@code Minion} can perform.<br>
 * It serves as a template for managing behaviors and automated tasks for minion entities.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MinionActions")
public class MinionActions
{
	@XmlElement(name = "skill")
	protected ArrayList<MinionSkill> skillActions;
	
	/**
	 * Retrieves the list of skills associated with this minion.<br>
	 * It returns an empty {@code Collection} if no skills are defined.
	 * @return a {@code Collection} of {@link MinionSkill} objects.
	 */
	public Collection<MinionSkill> getSkillsCollections()
	{
		return (skillActions != null ? skillActions : Collections.emptyList());
	}
}
