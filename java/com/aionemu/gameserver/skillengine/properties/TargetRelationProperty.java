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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.CreatureType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * Defines the relationship between a skill and its potential targets.<br>
 * It determines which {@link Creature} types can be affected by a specific {@link Skill}.
 * @author ATracer
 * @Reworked GiGatR00n (Aion-Core)
 */
public class TargetRelationProperty
{
	/**
	 * Checks if an {@link Npc} is invulnerable to a specific {@link Player}.<br>
	 * This method evaluates the relationship between two {@link Creature} objects.<br>
	 * It returns {@code true} only if the NPC type is marked as {@code INVULNERABLE}.
	 * @param player The {@link Player} performing the action.
	 * @param npc The {@link Npc} being targeted.
	 * @return {@code true} if the NPC is invulnerable, otherwise {@code false}.
	 */
	public static boolean isNpcInvul(Creature player, Creature npc)
	{
		if ((player instanceof Player) && (npc instanceof Npc))
		{
			switch (CreatureType.getCreatureType(npc.getType(player)))
			{
				case INVULNERABLE:
					return true;
				default:
					break;
			}
		}
		
		return false;
	}
	
	/**
	 * Configures the first target behavior for a specific {@link Skill}.<br>
	 * This method updates the skill's target attributes based on the provided {@code Properties}.<br>
	 * It handles various logic types such as self-targeting, pet targeting, and party checks.
	 * @param skill The {@link Skill} object to be updated.
	 * @param properties The {@code Properties} containing the configuration data.
	 * @return {@code true} if the target was successfully set or processed, otherwise {@code false}.
	 */
	public static boolean set(Skill skill, Properties properties)
	{
		final TargetRelationAttribute value = properties.getTargetRelation();
		
		final List<Creature> effectedList = skill.getEffectedList();
		final boolean isMaterialSkill = DataManager.MATERIAL_DATA.isMaterialSkill(skill.getSkillId());
		Creature effector = skill.getEffector();
		
		switch (value)
		{
			case ALL:
				break;
			case ENEMY:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					final Creature nextEffected = iter.next();
					
					if (!isNpcInvul(effector, nextEffected))
					{
						if (effector.isEnemy(nextEffected) || isMaterialSkill)
						{
							continue;
						}
					}
					
					iter.remove();
				}
				break;
			case FRIEND:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					final Creature nextEffected = iter.next();
					
					if ((!effector.isEnemy(nextEffected) && isBuffAllowed(nextEffected)) || isMaterialSkill)
					{
						continue;
					}
					
					iter.remove();
				}
				
				if (effectedList.isEmpty())
				{
					skill.setFirstTarget(skill.getEffector());
					effectedList.add(skill.getEffector());
				}
				else
				{
					skill.setFirstTarget(effectedList.get(0));
				}
				break;
			case MYPARTY:
				for (Iterator<Creature> iter = effectedList.iterator(); iter.hasNext();)
				{
					final Creature nextEffected = iter.next();
					
					Player player = null;
					if (nextEffected instanceof Player)
					{
						player = (Player) nextEffected;
					}
					else if (nextEffected instanceof Summon)
					{
						final Summon playerSummon = (Summon) nextEffected;
						if (playerSummon.getMaster() != null)
						{
							player = playerSummon.getMaster();
						}
					}
					
					if (player != null)
					{
						if (effector instanceof Servant)
						{
							effector = ((Servant) effector).getMaster();
						}
						
						final Player playerEffector = (Player) effector;
						if (playerEffector.isInAlliance2() && player.isInAlliance2())
						{
							if (playerEffector.getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId()))
							{
								continue;
							}
						}
						else if (playerEffector.isInGroup2() && player.isInGroup2())
						{
							if (playerEffector.getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId()))
							{
								continue;
							}
						}
					}
					
					iter.remove();
				}
				
				if (effectedList.isEmpty())
				{
					skill.setFirstTarget(effector);
					effectedList.add(effector);
				}
				else
				{
					skill.setFirstTarget(effectedList.get(0));
				}
				break;
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Checks if a {@link Creature} is allowed to receive buffs.<br>
	 * This method validates specific restrictions for certain siege NPCs.<br>
	 * It returns {@code false} for restricted types like artifacts or doors.
	 * @param effected The {@link Creature} being checked.
	 * @return {@code true} if the creature can be buffed, otherwise {@code false}.
	 */
	public static boolean isBuffAllowed(Creature effected)
	{
		if (effected instanceof SiegeNpc)
		{
			switch (((SiegeNpc) effected).getObjectTemplate().getAbyssNpcType())
			{
				case ARTIFACT:
				case ARTIFACT_EFFECT_CORE:
				case DOOR:
				case DOORREPAIR:
					return false;
				default:
					break;
			}
		}
		
		return true;
	}
}
