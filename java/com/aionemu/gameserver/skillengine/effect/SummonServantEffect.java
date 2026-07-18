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
package com.aionemu.gameserver.skillengine.effect;

import java.util.List;
import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the logic for summoning a {@link Servant} as part of a skill effect.<br>
 * This class manages the spawning process and initialization of servant entities.
 * @author ATracer
 * @Reworked Kill3r
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonServantEffect")
public class SummonServantEffect extends SummonEffect
{
	private static final Logger log = LoggerFactory.getLogger(SummonServantEffect.class);
	@XmlAttribute(name = "skill_id", required = true)
	protected int skillId;
	
	/**
	 * Spawns a servant based on the provided {@code Effect}.<br>
	 * It retrieves the coordinates from the effector of the effect.<br>
	 * This method calls {@code int, NpcObjectType, float, float, float)} to create the entity.
	 * @param effect The {@code Effect} object containing the servant data.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final Creature effector = effect.getEffector();
		final float x = effector.getX();
		final float y = effector.getY();
		final float z = effector.getZ();
		spawnServant(effect, time, NpcObjectType.SERVANT, x, y, z);
	}
	
	/**
	 * Spawns a new {@link Servant} based on the provided effect and coordinates.<br>
	 * This method handles skill ID lookups, spawning logic, and scheduling the despawn task.<br>
	 * It also triggers an initial attack event if the servant is not a totem.
	 * @param effect The {@code Effect} containing the source and target data.
	 * @param time The delay in seconds before the servant is deleted.
	 * @param npcObjectType The type of NPC to spawn, such as {@code SERVANT} or {@code TOTEM}.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @return The spawned {@link Servant} object, or {@code null} if the target is invalid.
	 */
	protected Servant spawnServant(Effect effect, int time, NpcObjectType npcObjectType, float x, float y, float z)
	{
		final Creature effector = effect.getEffector();
		final byte heading = effector.getHeading();
		final int worldId = effector.getWorldId();
		final int instanceId = effector.getInstanceId();
		
		final Creature target = (Creature) effector.getTarget();
		final Creature effected = effect.getEffected();
		
		final SkillTemplate template = effect.getSkillTemplate();
		
		if ((template.getProperties().getFirstTarget() != FirstTargetAttribute.ME) && (target == null))
		{
			log.warn("Servant trying to attack null target!!");
			return null;
		}
		
		// Just like for Traps, look for the effectSkill from NameDesc (in version 4.8, the skill_id for servant effects is not shown).
		if (skillId == 0)
		{
			final List<SkillTemplate> sT = DataManager.SKILL_DATA.getSkillTemplates();
			String descName;
			String[] splitDescName;
			String finalDescName;
			
			switch (npcObjectType)
			{
				case SERVANT:
					// PR_Light_HolyServent_G1
					descName = effect.getSkillTemplate().getNamedesc();
					splitDescName = descName.split("_");
					finalDescName = splitDescName[2] + "_" + splitDescName[3];
					
					for (SkillTemplate skillTemp : sT)
					{
						// Some effectName has HolyServent_G1 and PR_N_HolyServent_G6
						if (skillTemp.getNamedesc().equalsIgnoreCase(finalDescName) || skillTemp.getNamedesc().equalsIgnoreCase("PR_N_" + finalDescName))
						{
							skillId = skillTemp.getSkillId();
							break;
						}
					}
					break;
				case TOTEM:
					// PR_Dark_HealingServent_G6
					descName = effect.getSkillTemplate().getNamedesc();
					splitDescName = descName.split("_");
					finalDescName = "Totem_" + splitDescName[2] + "_" + splitDescName[3];
					final String finalDescName2 = splitDescName[0] + "_" + splitDescName[1] + "_" + finalDescName;
					
					for (SkillTemplate skillTemp : sT)
					{
						if (skillTemp.getNamedesc().equalsIgnoreCase(finalDescName2) || skillTemp.getNamedesc().equalsIgnoreCase("PR_N_" + finalDescName))
						{
							skillId = skillTemp.getSkillId();
							break;
						}
					}
					break;
				default:
					break;
			}
			
			if (skillId == 0)
			{
				log.warn("Couldn't Found Effect for Servant : " + effect.getSkillId());
			}
		}
		
		final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final Servant servant = VisibleObjectSpawner.spawnServant(spawn, instanceId, effector, skillId, effect.getSkillLevel(), npcObjectType);
		
		final Future<?> task = ThreadPoolManager.getInstance().schedule(() -> servant.getController().onDelete(), time * 1000);
		servant.getController().addTask(TaskId.DESPAWN, task);
		if (servant.getNpcObjectType() != NpcObjectType.TOTEM)
		{
			servant.getAi2().onCreatureEvent(AIEventType.ATTACK, (target != null ? target : effected));
		}
		
		return servant;
	}
}
