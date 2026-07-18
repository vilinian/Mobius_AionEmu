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
package com.aionemu.gameserver.world;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;

/**
 * This class manages the core logic and lifecycle of a game world.<br>
 * It handles world-specific tasks such as script execution and entity management.<br>
 * It implements the {@link GameEngine} interface to coordinate high-level game operations.
 * @author ATracer
 */
public class WorldEngine implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger(WorldEngine.class);
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/worldhandlers.xml");
	public static final WorldHandler DUMMY_INSTANCE_HANDLER = new GeneralWorldHandler();
	private final Map<Integer, Class<? extends WorldHandler>> handlers = new HashMap<>();
	
	/**
	 * Starts the loading process for AI handlers.<br>
	 * This method initializes the {@code ScriptManager} and loads data from {@code INSTANCE_DESCRIPTOR_FILE}.<br>
	 * It also validates all loaded scripts to ensure they are correct.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		log.info("[Map Engine] Map engine load started");
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new WorldHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
			log.info("[Map Engine] Loaded " + handlers.size() + " World Script");
		}
		catch (Exception e)
		{
			throw new GameServerError("[Map Engine] Can't initialize map handlers.", e);
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
	 * Shuts down the map engine and cleans up resources.<br>
	 * It calls {@code shutdown()} on the {@link ScriptManager}.<br>
	 * The {@code handlers} map is cleared to release references.
	 */
	@Override
	public void shutdown()
	{
		log.info("[Map Engine] Map engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("[Map Engine] Map engine shutdown complete");
	}
	
	/**
	 * Retrieves the {@link WorldHandler} for a specific world ID.<br>
	 * It creates a new instance of the handler class associated with that ID.<br>
	 * If no handler is found, it returns the {@code DUMMY_INSTANCE_HANDLER}.
	 * @param worldId The unique identifier for the world.
	 * @return A new {@link WorldHandler} instance or a default dummy handler.
	 */
	public WorldHandler getNewInstanceHandler(int worldId)
	{
		final Class<? extends WorldHandler> instanceClass = handlers.get(worldId);
		WorldHandler worldHandler = null;
		if (instanceClass != null)
		{
			try
			{
				worldHandler = instanceClass.getDeclaredConstructor().newInstance();
			}
			catch (Exception ex)
			{
				log.warn("[Map Engine] Can't instantiate map handler " + worldId, ex);
			}
		}
		
		if (worldHandler == null)
		{
			worldHandler = DUMMY_INSTANCE_HANDLER;
		}
		
		return worldHandler;
	}
	
	/**
	 * Registers a new {@link WorldHandler} class into the engine.<br>
	 * This method looks for the {@code WorldID} annotation on the provided class.<br>
	 * It maps the ID value to the handler class for later use.
	 * @param handler The class of the world handler to register.
	 */
	void addWorldHandlerClass(Class<? extends WorldHandler> handler)
	{
		final WorldID idAnnotation = handler.getAnnotation(WorldID.class);
		if (idAnnotation != null)
		{
			handlers.put(idAnnotation.value(), handler);
		}
	}
	
	/**
	 * This method is called when a new {@link WorldMap} is created.<br>
	 * It triggers the initialization logic for the specific world handler.
	 * @param map The {@code WorldMap} object that was just initialized.
	 */
	public void onWorldCreate(WorldMap map)
	{
		map.getWorldHandler().onWorldCreate(map);
	}
	
	/**
	 * Retrieves the global instance of the {@link WorldEngine}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the main world engine from anywhere in the code.
	 * @return The single shared instance of {@code WorldEngine}.
	 */
	public static WorldEngine getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final WorldEngine instance = new WorldEngine();
	}
}
