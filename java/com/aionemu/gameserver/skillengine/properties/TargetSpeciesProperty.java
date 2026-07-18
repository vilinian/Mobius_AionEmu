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
package com.aionemu.gameserver.skillengine.properties;

import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * This class defines properties related to target species for skills.<br>
 * It helps determine which types of {@link Creature} entities can be targeted by a {@link Skill}.
 * @author Luzien
 */
public class TargetSpeciesProperty
{
	/**
	 * Filters the list of affected creatures for a {@link Skill} based on species.<br>
	 * This method removes targets that do not match the specified {@code TargetSpeciesAttribute}.<br>
	 * It checks if targets are instances of {@link Npc} or {@link Player} accordingly.
	 * @param skill The {@link Skill} object to be updated.
	 * @param properties The {@code Properties} containing the configuration data.
	 * @return {@code true} if the operation completed successfully, otherwise {@code false}.
	 */
	public static boolean set(Skill skill, Properties properties)
	{
		final TargetSpeciesAttribute value = properties.getTargetSpecies();
		
		final List<Creature> effectedList = skill.getEffectedList();
		
		switch (value)
		{
			case NPC:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					final Creature nextEffected = iter.next();
					
					if (nextEffected instanceof Npc)
					{
						continue;
					}
					
					iter.remove();
				}
				break;
			case PC:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					final Creature nextEffected = iter.next();
					
					if (nextEffected instanceof Player)
					{
						continue;
					}
					
					iter.remove();
				}
				break;
			default:
				break;
		}
		
		return true;
	}
}
