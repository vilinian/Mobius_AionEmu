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
package com.aionemu.gameserver.world.zone.handler;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.CollisionMaterialActor;
import com.aionemu.gameserver.controllers.observer.IActor;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.materials.MaterialSkill;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the logic for material-related interactions within a {@link ZoneInstance}.<br>
 * It manages how players and creatures interact with {@link MaterialTemplate} objects.<br>
 * This class ensures that material skills and spatial data are processed correctly.
 * @author Rolandas
 */
public class MaterialZoneHandler implements ZoneHandler
{
	Map<Integer, IActor> observed = new HashMap<>();
	private final Spatial geometry;
	private final MaterialTemplate template;
	private boolean actOnEnter = false;
	private Race ownerRace = Race.NONE;
	
	/**
	 * Creates a new {@link MaterialZoneHandler} instance.<br>
	 * This constructor initializes the geometry and material template.<br>
	 * It also determines if actions occur on entry based on the spatial name.<br>
	 * It sets the owner race based on specific naming patterns.
	 * @param geometry The {@code Spatial} object defining the zone area.
	 * @param template The {@code MaterialTemplate} used for this zone.
	 */
	public MaterialZoneHandler(Spatial geometry, MaterialTemplate template)
	{
		this.geometry = geometry;
		this.template = template;
		final String name = geometry.getName();
		if ((name.indexOf("FIRE_BOX") != -1) || (name.indexOf("FIRE_SEMISPHERE") != -1) || (name.indexOf("FIREPOT") != -1) || (name.indexOf("FIRE_CYLINDER") != -1) || (name.indexOf("FIRE_CONE") != -1) || name.startsWith("BU_H_CENTERHALL"))
		{
			actOnEnter = true;
		}
		
		if (name.startsWith("BU_AB_DARKSP"))
		{
			ownerRace = Race.ASMODIANS;
		}
		else if (name.startsWith("BU_AB_LIGHTSP"))
		{
			ownerRace = Race.ELYOS;
		}
	}
	
	/**
	 * Handles logic when a {@code Creature} enters a specific {@code ZoneInstance}.<br>
	 * This method checks if the creature is a non-GM {@link Player}.<br>
	 * It kills players who enter the wrong faction base.
	 * @param creature The {@code Creature} entering the zone.
	 * @param zone The {@code ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone)
	{
		if (ownerRace == creature.getRace())
		{
			return;
		}
		
		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : template.getSkills())
		{
			if (skill.getTarget().isTarget(creature))
			{
				foundSkill = skill;
				break;
			}
		}
		
		if (foundSkill == null)
		{
			return;
		}
		
		final CollisionMaterialActor actor = new CollisionMaterialActor(creature, geometry, template);
		creature.getObserveController().addObserver(actor);
		observed.put(creature.getObjectId(), actor);
		if (actOnEnter)
		{
			actor.act();
		}
	}
	
	/**
	 * This method is called when a {@link Creature} leaves a {@link ZoneInstance}.<br>
	 * It removes the observer from the creature if it is a non-GM player.<br>
	 * The logic ensures that observation effects are cleared correctly.
	 * @param creature The {@link Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} being exited.
	 */
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone)
	{
		final IActor actor = observed.get(creature.getObjectId());
		if (actor != null)
		{
			creature.getObserveController().removeObserver((ActionObserver) actor);
			observed.remove(creature.getObjectId());
			actor.abort();
		}
	}
}
