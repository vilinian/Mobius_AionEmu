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
package com.aionemu.gameserver.instance;

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
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * This class manages the lifecycle and execution of game instances.<br>
 * It handles the creation, updates, and removal of {@link InstanceHandler} objects within the world.<br>
 * It serves as the primary engine for processing instance-specific logic.
 * @author ATracer
 */
public class InstanceEngine implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger(InstanceEngine.class);
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/instancehandlers.xml");
	public static final InstanceHandler DUMMY_INSTANCE_HANDLER = new GeneralInstanceHandler();
	private final Map<Integer, Class<? extends InstanceHandler>> handlers = new HashMap<>();
	
	/**
	 * Starts the loading process for AI handlers.<br>
	 * This method initializes the {@code ScriptManager} and loads data from {@code INSTANCE_DESCRIPTOR_FILE}.<br>
	 * It also validates all loaded scripts to ensure they are correct.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		log.info("[InstanceEngine] Instance engine load started");
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new InstanceHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
			log.info("[InstanceEngine] Loaded " + handlers.size() + " instance handlers.");
		}
		catch (Exception e)
		{
			throw new GameServerError("[InstanceEngine] Can't initialize instance handlers.", e);
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
	 * Shuts down the instance engine and cleans up resources.<br>
	 * This method calls {@code shutdown} to stop all scripts.<br>
	 * It also clears the internal {@code handlers} map.
	 */
	@Override
	public void shutdown()
	{
		log.info("[InstanceEngine] Instance engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("[InstanceEngine] Instance engine shutdown complete");
	}
	
	/**
	 * Reloads the instance engine and its associated scripts.<br>
	 * This method creates a new {@code ScriptManager} and loads data from {@code INSTANCE_DESCRIPTOR_FILE}.<br>
	 * It then calls {@code shutdown} to clear old resources and {@code load} to restart the system.
	 */
	public void reload()
	{
		Util.printSection("Instances");
		log.info("[InstanceEngine] Reloading instances");
		ScriptManager tmpSM;
		try
		{
			tmpSM = new ScriptManager();
			final AggregatedClassListener acl = new AggregatedClassListener();
			acl.addClassListener(new OnClassLoadUnloadListener());
			acl.addClassListener(new ScheduledTaskClassListener());
			acl.addClassListener(new InstanceHandlerClassListener());
			tmpSM.setGlobalClassListener(acl);
			try
			{
				tmpSM.load(INSTANCE_DESCRIPTOR_FILE);
			}
			catch (Exception e)
			{
				throw new GameServerError("[InstanceEngine] Error", e);
			}
		}
		catch (Exception e)
		{
			throw new GameServerError("[InstanceEngine] Error", e);
		}
		
		shutdown();
		load(null);
	}
	
	/**
	 * Creates a new {@link InstanceHandler} for a specific world.<br>
	 * It looks up the handler class associated with the provided {@code worldId}.<br>
	 * If no handler is found, it returns the {@code DUMMY_INSTANCE_HANDLER}.
	 * @param worldId The unique identifier of the world.
	 * @return A new instance of a handler or a default dummy handler.
	 */
	public InstanceHandler getNewInstanceHandler(int worldId)
	{
		final Class<? extends InstanceHandler> instanceClass = handlers.get(worldId);
		InstanceHandler instanceHandler = null;
		if (instanceClass != null)
		{
			try
			{
				instanceHandler = instanceClass.getDeclaredConstructor().newInstance();
			}
			catch (Exception ex)
			{
				log.warn("[InstanceEngine] Can't instantiate instance handler " + worldId, ex);
			}
		}
		
		if (instanceHandler == null)
		{
			instanceHandler = DUMMY_INSTANCE_HANDLER;
		}
		
		return instanceHandler;
	}
	
	/**
	 * Registers a new {@link InstanceHandler} class into the engine.<br>
	 * This method looks for an {@link InstanceID} annotation on the provided class.<br>
	 * It maps the ID value to the handler class for later use.
	 * @param handler The class of the instance handler to register.
	 */
	void addInstanceHandlerClass(Class<? extends InstanceHandler> handler)
	{
		final InstanceID idAnnotation = handler.getAnnotation(InstanceID.class);
		if (idAnnotation != null)
		{
			handlers.put(idAnnotation.value(), handler);
		}
	}
	
	/**
	 * This method is called when a new {@link WorldMapInstance} is created.<br>
	 * It initializes the local doors map from the provided instance.<br>
	 * It calls the superclass implementation of {@code onInstanceCreate}.
	 * @param instance The {@code WorldMapInstance} being created.
	 */
	public void onInstanceCreate(WorldMapInstance instance)
	{
		instance.getInstanceHandler().onInstanceCreate(instance);
	}
	
	/**
	 * Provides the global access point for the {@link InstanceEngine}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to get the main engine instance throughout your code.
	 * @return The single shared instance of {@code InstanceEngine}.
	 */
	public static InstanceEngine getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final InstanceEngine instance = new InstanceEngine();
	}
}
