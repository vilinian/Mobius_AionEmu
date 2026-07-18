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
package com.aionemu.gameserver.controllers.observer;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.materials.MaterialActTime;
import com.aionemu.gameserver.model.templates.materials.MaterialSkill;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class handles collision interactions with environmental materials.<br>
 * It processes logic for when a {@link Player} or {@link Creature} interacts with specific material types.<br>
 * It manages effects such as skill triggers, animations, and time-based actions based on the {@link MaterialTemplate}.
 * @author Rolandas
 */
public class CollisionMaterialActor extends AbstractCollisionObserver implements IActor
{
	private final MaterialTemplate actionTemplate;
	private final AtomicReference<MaterialSkill> currentSkill = new AtomicReference<>();
	
	/**
	 * Creates a new {@link CollisionMaterialActor} for a specific creature.<br>
	 * This actor handles interactions with material objects in the game world.
	 * @param creature The {@code Creature} that will perform the action.
	 * @param geometry The {@code Spatial} area where the collision occurs.
	 * @param actionTemplate The {@code MaterialTemplate} defining the interaction behavior.
	 */
	public CollisionMaterialActor(Creature creature, Spatial geometry, MaterialTemplate actionTemplate)
	{
		super(creature, geometry, CollisionIntention.MATERIAL.getId());
		this.actionTemplate = actionTemplate;
	}
	
	/**
	 * Determines the appropriate {@link MaterialSkill} for a specific target.<br>
	 * This method checks if the creature is valid and not protected by a player.<br>
	 * It verifies if the skill matches the target and respects weather or time constraints.
	 * @param creature The {@code Creature} to check against the action template.
	 * @return The matching {@code MaterialSkill} or {@code null} if no valid skill is found.
	 */
	private MaterialSkill getSkillForTarget(Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = (Player) creature;
			if (player.isProtectionActive())
			{
				return null;
			}
		}
		
		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : actionTemplate.getSkills())
		{
			if (skill.getTarget().isTarget(creature))
			{
				foundSkill = skill;
				break;
			}
		}
		
		if (foundSkill == null)
		{
			return null;
		}
		
		int weatherCode = -1;
		if (creature.getActiveRegion() == null)
		{
			return null;
		}
		
		final List<ZoneInstance> zones = creature.getActiveRegion().getZones(creature);
		for (ZoneInstance regionZone : zones)
		{
			if (regionZone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER)
			{
				final Vector3f center = geometry.getWorldBound().getCenter();
				if (!regionZone.getAreaTemplate().isInside3D(center.x, center.y, center.z))
				{
					continue;
				}
				
				final int weatherZoneId = DataManager.ZONE_DATA.getWeatherZoneId(regionZone.getZoneTemplate());
				weatherCode = WeatherService.getInstance().getWeatherCode(creature.getWorldId(), weatherZoneId);
				break;
			}
		}
		
		final boolean dependsOnWeather = geometry.getName().indexOf("WEATHER") != -1;
		
		// TODO: fix it
		if (dependsOnWeather && (weatherCode > 0))
		{
			return null; // not active in any weather (usually, during rain and after rain, not before)
		}
		
		if (foundSkill.getTime() == null)
		{
			return foundSkill;
		}
		
		final GameTime gameTime = (GameTime) GameTimeManager.getGameTime().clone();
		if ((foundSkill.getTime() == MaterialActTime.DAY) && (weatherCode == 0))
		{
			return foundSkill; // Sunny day, according to client data
		}
		
		if (gameTime.getDayTime() == DayTime.NIGHT)
		{
			if (foundSkill.getTime() == MaterialActTime.NIGHT)
			{
				return foundSkill;
			}
		}
		else
		{
			return foundSkill;
		}
		
		return null;
	}
	
	/**
	 * This method is called when a creature moves and triggers a collision.<br>
	 * It checks if the {@code CollisionResults} list is not empty.<br>
	 * If a GM player enters a new area, it sends a message with the geometry name.<br>
	 * Finally, it calls the {@code act} method to perform specific actions.
	 * @param collisionResults The results of the recent movement collision.
	 */
	@Override
	public void onMoved(CollisionResults collisionResults)
	{
		if (collisionResults.size() == 0)
		{
			return;
		}
		
		if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && (creature instanceof Player))
		{
			final Player player = (Player) creature;
			if (player.isGM())
			{
				final CollisionResult result = collisionResults.getClosestCollision();
				PacketSendUtility.sendMessage(player, "Entered " + result.getGeometry().getName());
			}
		}
		
		act();
	}
	
	/**
	 * Executes the material action logic for the associated creature.<br>
	 * This method determines the correct {@code MaterialSkill} to use based on the target.<br>
	 * If a new skill is identified, it schedules a repeating task to apply the skill effect.
	 */
	@Override
	public void act()
	{
		final MaterialSkill actSkill = getSkillForTarget(creature);
		if (currentSkill.getAndSet(actSkill) != actSkill)
		{
			if ((actSkill == null) || creature.getEffectController().hasAbnormalEffect(actSkill.getId()))
			{
				return;
			}
			
			final Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				if (!creature.getEffectController().hasAbnormalEffect(actSkill.getId()))
				{
					if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && (creature instanceof Player))
					{
						final Player player = (Player) creature;
						if (player.isGM())
						{
							PacketSendUtility.sendMessage(player, "Use skill=" + actSkill.getId());
						}
					}
					
					final Skill skill = SkillEngine.getInstance().getSkill(creature, actSkill.getId(), actSkill.getSkillLevel(), creature);
					skill.getEffectedList().add(creature);
					skill.useWithoutPropSkill();
				}
			}, 0, (long) (actSkill.getFrequency() * 1000));
			creature.getController().addTask(TaskId.MATERIAL_ACTION, task);
		}
	}
	
	/**
	 * Cancels the current material action for the creature.<br>
	 * This method removes the {@code MATERIAL_ACTION} task from the controller.<br>
	 * It also sets the {@code currentSkill} to {@code null}.
	 */
	@Override
	public void abort()
	{
		final Future<?> existingTask = creature.getController().getTask(TaskId.MATERIAL_ACTION);
		if (existingTask != null)
		{
			creature.getController().cancelTask(TaskId.MATERIAL_ACTION);
		}
		
		currentSkill.set(null);
	}
	
	/**
	 * This method is called when a {@link Creature} dies.<br>
	 * It handles the logic for processing death events.
	 * @param creature The {@code Creature} that has died.
	 */
	@Override
	public void died(Creature creature)
	{
		abort();
	}
	
	/**
	 * Updates the active status of this actor.<br>
	 * Use {@code true} to turn it on and {@code false} to turn it off.
	 * @param enable The new status for the actor.
	 */
	@Override
	public void setEnabled(boolean enable)
	{
		// TODO Auto-generated method stub
	}
}
