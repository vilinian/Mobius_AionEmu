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
package com.aionemu.gameserver.ai2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

/**
 * This class serves as the core engine for managing and executing {@link Npc} artificial intelligence.<br>
 * It handles the processing of AI behaviors and coordinates tasks within the game world.
 * @author ATracer
 */
public class AI2Engine implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger(AI2Engine.class);
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/aihandlers.xml");
	private final Map<String, Class<? extends AbstractAI>> aiMap = new HashMap<>();
	
	/**
	 * Starts the loading process for AI handlers.<br>
	 * This method initializes the {@code ScriptManager} and loads data from {@code INSTANCE_DESCRIPTOR_FILE}.<br>
	 * It also validates all loaded scripts to ensure they are correct.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		log.info("[AIEngine] engine load started");
		scriptManager = new ScriptManager();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new AI2HandlerClassListener());
		scriptManager.setGlobalClassListener(acl);
		
		try
		{
			scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
			GameServer.log.info("[AIEngine] Loaded " + aiMap.size() + " ai handlers.");
			validateScripts();
		}
		catch (Exception e)
		{
			throw new GameServerError("[AIEngine] Can't initialize ai handlers.", e);
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
	 * Shuts down the {@code AI2Engine} and cleans up its resources.<br>
	 * This method calls {@code shutdown} to stop all scripts.<br>
	 * It also clears the internal {@code aiMap}.
	 */
	@Override
	public void shutdown()
	{
		log.info("[AIEngine] engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		aiMap.clear();
		log.info("[AIEngine] engine shutdown complete");
	}
	
	/**
	 * Registers a new AI class into the system.<br>
	 * This method looks for the {@code AIName} annotation on the provided class.<br>
	 * It adds the class to the internal map using the name defined in that annotation.
	 * @param class1 The {@link AbstractAI} class to register.
	 */
	public void registerAI(Class<? extends AbstractAI> class1)
	{
		final AIName nameAnnotation = class1.getAnnotation(AIName.class);
		if (nameAnnotation != null)
		{
			aiMap.put(nameAnnotation.value(), class1);
		}
	}
	
	/**
	 * This method initializes a new {@link AbstractAI} instance for a specific creature.<br>
	 * It retrieves the AI class from the internal map using the provided {@code name}.<br>
	 * The method links the AI to its {@code owner} and returns the created object.
	 * @param name The unique identifier used to look up the AI class in the registry.
	 * @param owner The {@link Creature} that will be controlled by this AI.
	 * @return The initialized {@link AI2} instance, or {@code null} if an error occurs during creation.
	 */
	public AI2 setupAI(String name, Creature owner)
	{
		AbstractAI aiInstance = null;
		try
		{
			aiInstance = aiMap.get(name).getDeclaredConstructor().newInstance();
			aiInstance.setOwner(owner);
			owner.setAi2(aiInstance);
			if (AIConfig.ONCREATE_DEBUG)
			{
				aiInstance.setLogging(true);
			}
		}
		catch (Exception e)
		{
			log.error("[AIEngine] AI factory error: " + name, e);
		}
		
		return aiInstance;
	}
	
	/**
	 * Initializes the AI behavior for a specific NPC.<br>
	 * This method links an {@link AiNames} type to an {@code Npc} object.<br>
	 * It prepares the necessary scripts and logic for the entity.
	 * @param aiName The name of the AI behavior to apply.
	 * @param owner The NPC that will use this AI behavior.
	 */
	public void setupAI(AiNames aiName, Npc owner)
	{
		setupAI(aiName.getName(), owner);
	}
	
	/**
	 * Checks for invalid AI names in the NPC data.<br>
	 * It compares registered AI names against those found in {@link DataManager}.<br>
	 * Any missing AI names are logged as a warning.
	 */
	private void validateScripts()
	{
		final Collection<String> npcAINames = DataManager.NPC_DATA.getNpcData().valueCollection().stream().map(NpcTemplate::getAi).distinct().collect(Collectors.toCollection(ArrayList::new));
		npcAINames.removeAll(aiMap.keySet());
		if (npcAINames.size() > 0)
		{
			log.warn("[AIEngine] Bad AI names: " + npcAINames.stream().map(String::valueOf).collect(Collectors.joining(", ")));
		}
	}
	
	/**
	 * Provides access to the singleton instance of {@link AI2Engine}.<br>
	 * Use this method to get the main engine for AI operations.
	 * @return The global {@code AI2Engine} instance.
	 */
	public static AI2Engine getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AI2Engine instance = new AI2Engine();
	}
}
