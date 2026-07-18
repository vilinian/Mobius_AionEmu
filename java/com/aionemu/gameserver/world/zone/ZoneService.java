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
package com.aionemu.gameserver.world.zone;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.ZoneData;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.geometry.Area;
import com.aionemu.gameserver.model.geometry.CylinderArea;
import com.aionemu.gameserver.model.geometry.PolyArea;
import com.aionemu.gameserver.model.geometry.SemisphereArea;
import com.aionemu.gameserver.model.geometry.SphereArea;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeShield;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.model.templates.zone.MaterialZoneTemplate;
import com.aionemu.gameserver.model.templates.zone.WorldZoneTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.services.ShieldService;
import com.aionemu.gameserver.world.zone.handler.GeneralZoneHandler;
import com.aionemu.gameserver.world.zone.handler.MaterialZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandlerClassListener;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Manages the lifecycle and logic of world zones within the game server.<br>
 * It handles zone loading, spatial queries, and coordinates interactions between {@link ZoneHandler} instances.
 * @author ATracer modified by antness
 */
public final class ZoneService implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger(ZoneService.class);
	private final TIntObjectHashMap<List<ZoneInfo>> zoneByMapIdMap;
	private final Map<ZoneName, Class<? extends ZoneHandler>> handlers = new HashMap<>();
	private final Map<ZoneName, ZoneHandler> collidableHandlers = new HashMap<>();
	public static final ZoneHandler DUMMY_ZONE_HANDLER = new GeneralZoneHandler();
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File ZONE_DESCRIPTOR_FILE = new File("./data/scripts/system/zonehandlers.xml");
	
	/**
	 * Private constructor for the {@link ZoneService} class.<br>
	 * This constructor initializes the internal zone data map from {@code DataManager}.<br>
	 * It should not be called directly by other classes.
	 */
	private ZoneService()
	{
		zoneByMapIdMap = DataManager.ZONE_DATA.getZones();
	}
	
	/**
	 * Retrieves the singleton instance of the {@link ZoneService}.<br>
	 * This provides a global access point to the zone management system.
	 * @return The active {@code ZoneService} instance.
	 */
	public static ZoneService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneService instance = new ZoneService();
	}
	
	/**
	 * Retrieves the {@code ZoneHandler} associated with a specific {@link ZoneName}.<br>
	 * This method first checks for an existing instance in the cache.<br>
	 * If no instance exists, it attempts to create a new one using the registered class.<br>
	 * It returns a {@code DUMMY_ZONE_HANDLER} if no valid handler can be found.
	 * @param zoneName The name of the zone to retrieve the handler for.
	 * @return The {@code ZoneHandler} instance for the given zone.
	 */
	public ZoneHandler getNewZoneHandler(ZoneName zoneName)
	{
		ZoneHandler zoneHandler = collidableHandlers.get(zoneName);
		if (zoneHandler != null)
		{
			return zoneHandler;
		}
		
		final Class<? extends ZoneHandler> zoneClass = handlers.get(zoneName);
		if (zoneClass != null)
		{
			try
			{
				zoneHandler = zoneClass.getDeclaredConstructor().newInstance();
			}
			catch (IllegalAccessException ex)
			{
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
			catch (Exception ex)
			{
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
		}
		
		if (zoneHandler == null)
		{
			zoneHandler = DUMMY_ZONE_HANDLER;
		}
		
		return zoneHandler;
	}
	
	/**
	 * Registers a new {@link ZoneHandler} class into the system.<br>
	 * This method reads the {@code ZoneNameAnnotation} from the provided class.<br>
	 * It maps each zone name found in the annotation to this handler.
	 * @param handler The class of the {@link ZoneHandler} to register.
	 */
	public void addZoneHandlerClass(Class<? extends ZoneHandler> handler)
	{
		final ZoneNameAnnotation idAnnotation = handler.getAnnotation(ZoneNameAnnotation.class);
		if (idAnnotation != null)
		{
			final String[] zoneNames = idAnnotation.value().split(" ");
			for (String zoneNameString : zoneNames)
			{
				try
				{
					final ZoneName zoneName = ZoneName.get(zoneNameString.trim());
					if (zoneName == ZoneName.get("NONE"))
					{
						throw new RuntimeException();
					}
					
					handlers.put(zoneName, handler);
				}
				catch (Exception e)
				{
					log.warn("Missing ZoneName: " + idAnnotation.value());
				}
			}
		}
	}
	
	/**
	 * Registers a specific {@link ZoneHandler} class for a given zone.<br>
	 * This method maps the {@code zoneName} to its corresponding handler.
	 * @param zoneName The name of the zone to associate with the handler.
	 * @param handler The class of the handler that will manage this zone.
	 */
	public void addZoneHandlerClass(ZoneName zoneName, Class<? extends ZoneHandler> handler)
	{
		handlers.put(zoneName, handler);
	}
	
	/**
	 * Starts the loading process for AI handlers.<br>
	 * This method initializes the {@code ScriptManager} and loads data from {@code INSTANCE_DESCRIPTOR_FILE}.<br>
	 * It also validates all loaded scripts to ensure they are correct.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		log.info("Loading Zone Engine...");
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ZoneHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			scriptManager.load(ZONE_DESCRIPTOR_FILE);
			log.info("Loaded " + handlers.size() + " zone handlers.");
		}
		catch (IllegalStateException e)
		{
			log.warn("Can't initialize instance handlers.", e.getMessage());
		}
		catch (Exception e)
		{
			throw new GameServerError("Can't initialize instance handlers.", e);
		}
		finally
		{
			if (progressLatch != null)
			{
				progressLatch.countDown();
			}
		}
	}
	
	/**
	 * Shuts down the zone engine and cleans up its resources.<br>
	 * This method shuts down the {@code ScriptManager}.<br>
	 * It also clears all registered handlers from the internal list.
	 */
	@Override
	public void shutdown()
	{
		log.info("Zone engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("Zone engine shutdown complete");
	}
	
	/**
	 * Retrieves all zone instances associated with a specific world map.<br>
	 * This method creates the full map instance and populates it with specific area types like fly, fort, or pvp.<br>
	 * It also initializes necessary services such as shields for siege locations.
	 * @param mapId The unique identifier of the world map to load.
	 * @return A {@code Map} containing {@link ZoneName} keys and their corresponding {@link ZoneInstance} values.
	 */
	public Map<ZoneName, ZoneInstance> getZoneInstancesByWorldId(int mapId)
	{
		final Map<ZoneName, ZoneInstance> zones = new HashMap<>();
		final int worldSize = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getWorldSize();
		final WorldZoneTemplate zone = new WorldZoneTemplate(worldSize, mapId);
		final PolyArea fullArea = new PolyArea(zone.getName(), mapId, zone.getPoints().getPoint(), zone.getPoints().getBottom(), zone.getPoints().getTop());
		final ZoneInstance fullMap = new ZoneInstance(mapId, new ZoneInfo(fullArea, zone));
		fullMap.addHandler(getNewZoneHandler(zone.getName()));
		zones.put(zone.getName(), fullMap);
		
		final Collection<ZoneInfo> areas = zoneByMapIdMap.get(mapId);
		if (areas == null)
		{
			return zones;
		}
		
		ShieldService.getInstance().load(mapId);
		
		for (ZoneInfo area : areas)
		{
			ZoneInstance instance = null;
			switch (area.getZoneTemplate().getZoneType())
			{
				case FLY:
					instance = new FlyZoneInstance(mapId, area);
					break;
				case FORT:
					instance = new SiegeZoneInstance(mapId, area);
					final SiegeLocation siege = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations().get(area.getZoneTemplate().getSiegeId().get(0));
					if (siege != null)
					{
						siege.addZone((SiegeZoneInstance) instance);
						if (GeoDataConfig.GEO_SHIELDS_ENABLE)
						{
							ShieldService.getInstance().attachShield(siege);
						}
					}
					break;
				case ARTIFACT:
					instance = new SiegeZoneInstance(mapId, area);
					for (int artifactId : area.getZoneTemplate().getSiegeId())
					{
						final SiegeLocation artifact = DataManager.SIEGE_LOCATION_DATA.getArtifacts().get(artifactId);
						if (artifact == null)
						{
							log.warn("Missing siege location data for zone " + area.getZoneTemplate().getName().name());
						}
						else
						{
							artifact.addZone((SiegeZoneInstance) instance);
						}
					}
					break;
				case PVP:
					instance = new PvPZoneInstance(mapId, area);
					break;
				default:
					final InvasionZoneInstance invasionZone = getIZI(area);
					if (invasionZone != null)
					{
						instance = invasionZone;
					}
					else
					{
						instance = new ZoneInstance(mapId, area);
					}
			}
			
			instance.addHandler(getNewZoneHandler(area.getZoneTemplate().getName()));
			zones.put(area.getZoneTemplate().getName(), instance);
		}
		
		return zones;
	}
	
	/**
	 * Retrieves the {@link InvasionZoneInstance} for a specific area.<br>
	 * This method checks if the zone name matches a predefined list of invasion zones.<br>
	 * If a match is found, it calls {@code validateZone} to return the instance.<br>
	 * It returns {@code null} if the area does not belong to any known invasion zone.
	 * @param area The {@code ZoneInfo} object representing the area to check.
	 * @return The validated {@code InvasionZoneInstance} or {@code null}.
	 */
	private InvasionZoneInstance getIZI(ZoneInfo area)
	{
		if (area.getZoneTemplate().getName().name().equals("WAILING_CLIFFS_220050000") || area.getZoneTemplate().getName().name().equals("BALTASAR_CEMETERY_220050000") || area.getZoneTemplate().getName().name().equals("THE_LEGEND_SHRINE_220050000") || area.getZoneTemplate().getName().name().equals("SUDORVILLE_220050000") || area.getZoneTemplate().getName().name().equals("BALTASAR_HILL_VILLAGE_220050000") || area.getZoneTemplate().getName().name().equals("BRUSTHONIN_MITHRIL_MINE_220050000"))
		{
			return validateZone(area);
		}
		else if (area.getZoneTemplate().getName().name().equals("JAMANOK_INN_210060000") || area.getZoneTemplate().getName().name().equals("THE_STALKING_GROUNDS_210060000") || area.getZoneTemplate().getName().name().equals("BLACK_ROCK_HOT_SPRING_210060000") || area.getZoneTemplate().getName().name().equals("FREGIONS_FLAME_210060000"))
		{
			return validateZone(area);
		}
		
		return null;
	}
	
	/**
	 * Validates a zone and creates an {@link InvasionZoneInstance} if it belongs to a vortex.<br>
	 * This method checks the map ID of the provided {@code area}.<br>
	 * It returns an instance linked to the vortex or {@code null} if no vortex exists.
	 * @param area The {@code ZoneInfo} containing the zone data to validate.
	 * @return A new {@link InvasionZoneInstance} or {@code null}.
	 */
	private InvasionZoneInstance validateZone(ZoneInfo area)
	{
		final int mapId = area.getZoneTemplate().getMapid();
		final VortexLocation vortex = DataManager.VORTEX_DATA.getVortexLocation(mapId);
		if (vortex != null)
		{
			final InvasionZoneInstance instance = new InvasionZoneInstance(mapId, area);
			vortex.addZone(instance);
			return instance;
		}
		
		return null;
	}
	
	/**
	 * Creates a new material zone template based on the provided geometry.<br>
	 * This method registers a {@code MaterialZoneHandler} or a {@link SiegeShield} for the specified world.<br>
	 * It also adds the new zone to the map data if it does not already exist.
	 * @param geometry The spatial area defining the shape of the zone.
	 * @param worldId The unique identifier for the world where the zone is located.
	 * @param materialId The ID of the material template to apply to this zone.
	 * @param failOnMissing If {@code true}, the method will return if the zone name already exists.
	 */
	public void createMaterialZoneTemplate(Spatial geometry, int worldId, int materialId, boolean failOnMissing)
	{
		ZoneName zoneName = null;
		if (failOnMissing)
		{
			zoneName = ZoneName.get(geometry.getName() + "_" + worldId);
		}
		else
		{
			zoneName = ZoneName.createOrGet(geometry.getName() + "_" + worldId);
		}
		
		if (zoneName.name().equals(ZoneName.NONE))
		{
			return;
		}
		
		ZoneHandler handler = collidableHandlers.get(zoneName);
		if (handler == null)
		{
			if (materialId == 11)
			{
				if (GeoDataConfig.GEO_SHIELDS_ENABLE)
				{
					handler = new SiegeShield(geometry);
					ShieldService.getInstance().registerShield(worldId, (SiegeShield) handler);
				}
				else
				{
					return;
				}
			}
			else
			{
				final MaterialTemplate template = DataManager.MATERIAL_DATA.getTemplate(materialId);
				if (template == null)
				{
					return;
				}
				
				handler = new MaterialZoneHandler(geometry, template);
			}
			
			collidableHandlers.put(zoneName, handler);
		}
		else
		{
			// log.warn("Duplicate material mesh: " + zoneName.toString());
		}
		
		Collection<ZoneInfo> areas = zoneByMapIdMap.get(worldId);
		if (areas == null)
		{
			zoneByMapIdMap.put(worldId, new ArrayList<>());
			areas = zoneByMapIdMap.get(worldId);
		}
		
		ZoneInfo zoneInfo = null;
		for (ZoneInfo area : areas)
		{
			if (area.getZoneTemplate().getName().equals(zoneName))
			{
				zoneInfo = area;
				break;
			}
		}
		
		if (zoneInfo == null)
		{
			final MaterialZoneTemplate zoneTemplate = new MaterialZoneTemplate(geometry, worldId);
			
			// maybe add to zone data if needed search ?
			Area zoneInfoArea = null;
			if (zoneTemplate.getSphere() != null)
			{
				zoneInfoArea = new SphereArea(zoneName, worldId, zoneTemplate.getSphere().getX(), zoneTemplate.getSphere().getY(), zoneTemplate.getSphere().getZ(), zoneTemplate.getSphere().getR());
			}
			else if (zoneTemplate.getCylinder() != null)
			{
				zoneInfoArea = new CylinderArea(zoneName, worldId, zoneTemplate.getCylinder().getX(), zoneTemplate.getCylinder().getY(), zoneTemplate.getCylinder().getR(), zoneTemplate.getCylinder().getBottom(), zoneTemplate.getCylinder().getTop());
			}
			else if (zoneTemplate.getSemisphere() != null)
			{
				zoneInfoArea = new SemisphereArea(zoneName, worldId, zoneTemplate.getSemisphere().getX(), zoneTemplate.getSemisphere().getY(), zoneTemplate.getSemisphere().getZ(), zoneTemplate.getSemisphere().getR());
			}
			
			if (zoneInfoArea != null)
			{
				zoneInfo = new ZoneInfo(zoneInfoArea, zoneTemplate);
				areas.add(zoneInfo);
			}
		}
	}
	
	/**
	 * Creates a new {@link MaterialZoneTemplate} for a specific region.<br>
	 * This method automatically handles the naming of the geometry based on the provided {@code regionId}.<br>
	 * It uses a default failure policy when creating the template.
	 * @param geometry The {@code Spatial} object defining the area shape.
	 * @param regionId The unique identifier for the region.
	 * @param worldId The ID of the world map where the zone is located.
	 * @param materialId The ID of the material associated with this zone.
	 */
	public void createMaterialZoneTemplate(Spatial geometry, int regionId, int worldId, int materialId)
	{
		geometry.setName(geometry.getName() + "_" + regionId);
		createMaterialZoneTemplate(geometry, worldId, materialId, false);
	}
	
	/**
	 * Saves all active material zones to the data files.<br>
	 * This method iterates through all world maps and collects valid zone templates.<br>
	 * It then sorts these templates by map ID before persisting them via {@link ZoneData}.
	 */
	public void saveMaterialZones()
	{
		final List<ZoneTemplate> templates = new ArrayList<>();
		for (WorldMapTemplate map : DataManager.WORLD_MAPS_DATA)
		{
			final Collection<ZoneInfo> areas = zoneByMapIdMap.get(map.getMapId());
			if (areas == null)
			{
				continue;
			}
			
			for (ZoneInfo zone : areas)
			{
				if (collidableHandlers.containsKey(zone.getArea().getZoneName()))
				{
					templates.add(zone.getZoneTemplate());
				}
			}
		}
		
		Collections.sort(templates, (o1, o2) -> o1.getMapid() - o2.getMapid());
		
		final ZoneData zoneData = new ZoneData();
		zoneData.zoneList = templates;
		zoneData.saveData();
	}
}
