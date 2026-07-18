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
package com.aionemu.gameserver.model.templates.factions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * This class defines the template data for NPC factions within the game.<br>
 * It stores configuration details used to determine how different NPCs interact with each other and players.
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcFaction")
public class NpcFactionTemplate
{
	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "nameId")
	protected int nameId;
	@XmlAttribute(name = "category")
	protected FactionCategory category;
	@XmlAttribute(name = "minlevel")
	protected Integer minlevel;
	@XmlAttribute(name = "maxlevel")
	protected int maxlevel = 99;
	@XmlAttribute(name = "race")
	protected Race race;
	@XmlAttribute(name = "npcid")
	protected int npcId;
	@XmlAttribute(name = "skill_points")
	protected int skillPoints;
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Retrieves the name of the bookmark.<br>
	 * This method returns the {@code String`name`} associated with this object.
	 * @return The name of the bookmark as a {@code String}.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retrieves the unique identifier for the group name.<br>
	 * This value corresponds to the {@code name_id} attribute.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return nameId;
	}
	
	/**
	 * Retrieves the faction category for this template.<br>
	 * This method returns the {@code FactionCategory} associated with the NPC faction.
	 * @return The {@code FactionCategory} of the current template.
	 */
	public FactionCategory getCategory()
	{
		return category;
	}
	
	/**
	 * Retrieves the minimum level required for this auto group.<br>
	 * This value is fetched from the underlying template.
	 * @return The minimum level as an {@code int}.
	 */
	public int getMinLevel()
	{
		return minlevel;
	}
	
	/**
	 * Retrieves the maximum level allowed for this faction.<br>
	 * This value is stored in the {@code maxlevel} field.
	 * @return The maximum level as an {@code int}.
	 */
	public int getMaxLevel()
	{
		return maxlevel;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	public Race getRace()
	{
		return race;
	}
	
	/**
	 * Checks if this faction is categorized as a mentor.<br>
	 * This compares the {@code category} field against {@code MENTOR}.
	 * @return {@code true} if the category is a mentor, {@code false} otherwise.
	 */
	public boolean isMentor()
	{
		return category == FactionCategory.MENTOR;
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
	 * Retrieves the total number of skill points for this faction.<br>
	 * This value is used to determine how many points are available for skills.
	 * @return The current amount of {@code skillPoints}.
	 */
	public int getSkillPoints()
	{
		return skillPoints;
	}
}
