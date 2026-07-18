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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.CollisionDieActor;
import com.aionemu.gameserver.controllers.observer.ShieldObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.shield.Shield;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeShield;
import com.aionemu.gameserver.model.templates.shield.ShieldTemplate;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Manages the logic and lifecycle of {@link Shield} objects within the game world.<br>
 * This service handles shield placement, interactions, and updates for players and creatures.
 * @author xavier
 * @modified Rolandas
 */
public class ShieldService
{
	Logger log = LoggerFactory.getLogger(ShieldService.class);
	
	private static class SingletonHolder
	{
		protected static final ShieldService instance = new ShieldService();
	}
	
	private final Map<Integer, Shield> sphereShields = new ConcurrentHashMap<>();
	private final Map<Integer, List<SiegeShield>> registeredShields = new ConcurrentHashMap<>();
	
	/**
	 * Provides access to the global instance of {@link ShieldService}.<br>
	 * This method uses the singleton pattern.
	 * @return The single shared instance of {@code ShieldService}.
	 */
	public static ShieldService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor for the {@link ShieldService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It ensures that only one instance is used throughout the application.
	 */
	private ShieldService()
	{
	}
	
	/**
	 * Loads all shield templates for a specific map.<br>
	 * This method populates the {@code sphereShields} collection.<br>
	 * It filters data based on the provided {@code mapId}.
	 * @param mapId The unique identifier of the map to load.
	 */
	public void load(int mapId)
	{
		for (ShieldTemplate template : DataManager.SHIELD_DATA.getShieldTemplates())
		{
			if (template.getMap() != mapId)
			{
				continue;
			}
			
			final Shield f = new Shield(template);
			sphereShields.put(f.getId(), f);
		}
	}
	
	/**
	 * Spawns all active shields in the game world.<br>
	 * This method iterates through {@code sphereShields} and calls {@code spawn}.<br>
	 * It also logs information about any registered but unbound shields.
	 */
	public void spawnAll()
	{
		for (Shield shield : sphereShields.values())
		{
			shield.spawn();
			log.debug("Added " + shield.getName() + " at m=" + shield.getWorldId() + ",x=" + shield.getX() + ",y=" + shield.getY() + ",z=" + shield.getZ());
		}
		
		// TODO: check this list of not bound meshes (would remain inactive)
		for (List<SiegeShield> otherShields : registeredShields.values())
		{
			for (SiegeShield shield : otherShields)
			{
				log.debug("Not bound shield " + shield.getGeometry().getName());
			}
		}
		
		GameServer.log.info("[ShieldService] Loaded " + sphereShields.size() + " FortressShields");
	}
	
	/**
	 * Creates a new {@link ActionObserver} for a specific shield.<br>
	 * This method checks if a shield exists at the given {@code locationId}.<br>
	 * It returns a {@code ShieldObserver} if the shield is found.
	 * @param locationId The unique identifier for the shield location.
	 * @param observed The {@link Creature} that will be monitored by the observer.
	 * @return A new {@link ActionObserver} instance, or {@code null} if no shield exists at the location.
	 */
	public ActionObserver createShieldObserver(int locationId, Creature observed)
	{
		if (sphereShields.containsKey(locationId))
		{
			return new ShieldObserver(sphereShields.get(locationId), observed);
		}
		
		return null;
	}
	
	/**
	 * Creates a new {@link ActionObserver} for a specific creature and shield.<br>
	 * This method checks if geo shields are enabled in the configuration.<br>
	 * It returns a {@code CollisionDieActor} if they are active.<br>
	 * If disabled, it returns {@code null}.
	 * @param geoShield The {@link SiegeShield} to use for collision geometry.
	 * @param observed The {@link Creature} that will be monitored by the observer.
	 * @return An {@link ActionObserver} instance or {@code null}.
	 */
	public ActionObserver createShieldObserver(SiegeShield geoShield, Creature observed)
	{
		ActionObserver observer = null;
		if (GeoDataConfig.GEO_SHIELDS_ENABLE)
		{
			observer = new CollisionDieActor(observed, geoShield.getGeometry());
			((CollisionDieActor) observer).setEnabled(true);
		}
		
		return observer;
	}
	
	/**
	 * Registers a {@link SiegeShield} to a specific world.<br>
	 * This method adds the shield to the list of registered shields for the given {@code worldId}.<br>
	 * If no list exists for that ID, it creates a new one.
	 * @param worldId The unique identifier for the world.
	 * @param shield The {@link SiegeShield} object to register.
	 */
	public void registerShield(int worldId, SiegeShield shield)
	{
		List<SiegeShield> mapShields = registeredShields.get(worldId);
		if (mapShields == null)
		{
			mapShields = new ArrayList<>();
			registeredShields.put(worldId, mapShields);
		}
		
		mapShields.add(shield);
	}
	
	/**
	 * Links siege shields to a specific {@link SiegeLocation}.<br>
	 * This method checks the current zone for valid shields.<br>
	 * It removes found shields from the global registry and assigns them to the location.
	 * @param location The {@code SiegeLocation} where the shield should be attached.
	 */
	public void attachShield(SiegeLocation location)
	{
		final List<SiegeShield> mapShields = registeredShields.get(location.getTemplate().getWorldId());
		if (mapShields == null)
		{
			return;
		}
		
		final ZoneInstance zone = location.getZone().get(0);
		final List<SiegeShield> shields = new ArrayList<>();
		
		for (int index = mapShields.size() - 1; index >= 0; index--)
		{
			final SiegeShield shield = mapShields.get(index);
			final Vector3f center = shield.getGeometry().getWorldBound().getCenter();
			if (zone.getAreaTemplate().isInside3D(center.x, center.y, center.z))
			{
				shields.add(shield);
				mapShields.remove(index);
				final Shield sphereShield = sphereShields.get(location.getLocationId());
				if (sphereShield != null)
				{
					sphereShields.remove(location.getLocationId());
				}
				
				shield.setSiegeLocationId(location.getLocationId());
			}
		}
		
		if (shields.size() == 0)
		{
			// log.warn("Could not find a shield for locId: " + location.getLocationId());
		}
		else
		{
			location.setShields(shields);
		}
	}
}
