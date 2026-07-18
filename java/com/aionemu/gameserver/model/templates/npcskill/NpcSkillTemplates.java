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
package com.aionemu.gameserver.model.templates.npcskill;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class serves as a container for all {@link NpcSkillTemplate} templates.<br>
 * It manages the data loaded from XML configuration files to define NPC abilities.
 * @author AionChs Master
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcskills")
public class NpcSkillTemplates
{
	@XmlAttribute(name = "npcid")
	protected int npcId;
	@XmlElement(name = "npcskill")
	protected List<NpcSkillTemplate> npcSkills;
	
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
	 * Retrieves the list of skills for a specific NPC.<br>
	 * This method returns all {@link NpcSkillTemplate} objects associated with this template.
	 * @return A {@code List} containing all {@code NpcSkillTemplate} objects.
	 */
	public List<NpcSkillTemplate> getNpcSkills()
	{
		return npcSkills;
	}
}
