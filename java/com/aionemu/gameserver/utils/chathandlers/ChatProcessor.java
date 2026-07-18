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
package com.aionemu.gameserver.utils.chathandlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles the processing of chat messages sent by players.<br>
 * It parses incoming text and routes it to the appropriate {@link Player} or system action. It ensures that chat commands are executed correctly within the game environment.
 * @author KID
 * @Modified Rolandas
 */
public class ChatProcessor implements GameEngine
{
	private static final Logger log = LoggerFactory.getLogger("ADMINAUDIT_LOG");
	private static ChatProcessor instance = new ChatProcessor();
	private Map<String, ChatCommand> commands = new HashMap<>();
	private final Map<String, Byte> accessLevel = new HashMap<>();
	private ScriptManager sm = new ScriptManager();
	private Exception loadException = null;
	
	/**
	 * Gets the single shared instance of the {@link ChatProcessor}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access the chat system from other parts of the code.
	 * @return The global {@code ChatProcessor} instance.
	 */
	public static ChatProcessor getInstance()
	{
		return instance;
	}
	
	/**
	 * Starts the loading process for chat handlers.<br>
	 * This method initializes the {@code ScriptManager} and calls the {@code ChatProcessor)} method.<br>
	 * It ensures that the {@code progressLatch} is decremented regardless of whether the operation succeeds.
	 * @param progressLatch A {@code CountDownLatch} used to track the loading progress. If it is {@code null}, no action is taken.
	 */
	@Override
	public void load(CountDownLatch progressLatch)
	{
		try
		{
			log.info("Chat processor load started");
			init(sm, this);
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
	 * Shuts down the {@code ChatProcessor}.<br>
	 * This method stops all active chat processing tasks.<br>
	 * It ensures that resources are released properly before the application exits.
	 */
	@Override
	public void shutdown()
	{
	}
	
	/**
	 * Private constructor for the {@link ChatProcessor} class.<br>
	 * This prevents other classes from creating new instances of this class.<br>
	 * It ensures that only one instance is used throughout the application.
	 */
	private ChatProcessor()
	{
	}
	
	/**
	 * Creates a new instance of {@link ChatProcessor}.<br>
	 * This constructor initializes the processor using a provided {@code ScriptManager}.
	 * @param scriptManager The manager used to handle scripts.
	 */
	private ChatProcessor(ScriptManager scriptManager)
	{
		init(scriptManager, this);
	}
	
	/**
	 * Initializes the chat processing system.<br>
	 * This method loads levels and registers necessary script listeners.<br>
	 * It also loads various XML handler files using multiple threads.
	 * @param scriptManager The {@code ScriptManager} used to load scripts.
	 * @param processor The current instance of the {@link ChatProcessor}.
	 */
	private void init(ScriptManager scriptManager, ChatProcessor processor)
	{
		loadLevels();
		
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ChatCommandsLoader(processor));
		scriptManager.setGlobalClassListener(acl);
		
		final File[] files = new File[]
		{
			new File("./data/scripts/system/adminhandlers.xml"),
			new File("./data/scripts/system/playerhandlers.xml"),
			new File("./data/scripts/system/weddinghandlers.xml")
		};
		final CountDownLatch loadLatch = new CountDownLatch(files.length);
		
		for (int i = 0; i < files.length; i++)
		{
			final int index = i;
			ThreadPoolManager.getInstance().execute(() ->
			{
				try
				{
					scriptManager.load(files[index]);
				}
				catch (Exception e)
				{
					loadException = e;
				}
				finally
				{
					loadLatch.countDown();
				}
			});
		}
		
		try
		{
			loadLatch.await();
		}
		catch (InterruptedException e1)
		{
		}
		
		if (loadException != null)
		{
			throw new GameServerError("Can't initialize chat handlers.", loadException);
		}
	}
	
	/**
	 * Adds a new command to the chat system.<br>
	 * This method checks if the alias is already in use.<br>
	 * It also ensures the command has a valid access level.
	 * @param cmd The {@code ChatCommand} object to register.
	 */
	public void registerCommand(ChatCommand cmd)
	{
		if (commands.containsKey(cmd.getAlias()))
		{
			log.warn("Command " + cmd.getAlias() + " is already registered. Fail");
			return;
		}
		
		if (!accessLevel.containsKey(cmd.getAlias()))
		{
			log.warn("Command " + cmd.getAlias() + " do not have access level. Fail");
			return;
		}
		
		cmd.setAccessLevel(accessLevel.get(cmd.getAlias()));
		commands.put(cmd.getAlias(), cmd);
	}
	
	/**
	 * Reloads the chat commands and script manager.<br>
	 * This method creates a new {@link ChatProcessor} instance to refresh the configuration.<br>
	 * It clears the current {@code commands} map and replaces the old {@link ScriptManager}.<br>
	 * If an error occurs during reloading, it restores the previous command set.
	 */
	public void reload()
	{
		ScriptManager tmpSM;
		final ChatProcessor adminCP;
		final Map<String, ChatCommand> backupCommands = new HashMap<>(commands);
		commands.clear();
		loadException = null;
		
		try
		{
			tmpSM = new ScriptManager();
			adminCP = new ChatProcessor(tmpSM);
		}
		catch (Throwable e)
		{
			commands = backupCommands;
			throw new GameServerError("Can't reload chat handlers.", e);
		}
		
		backupCommands.clear();
		sm.shutdown();
		sm = null;
		sm = tmpSM;
		instance = adminCP;
	}
	
	/**
	 * This method loads the access levels for chat commands.<br>
	 * It reads data from the {@code config/administration/commands.properties} file.<br>
	 * The results are stored in the {@code accessLevel} map.
	 */
	private void loadLevels()
	{
		accessLevel.clear();
		try
		{
			final java.util.Properties props = PropertiesUtils.load("config/administration/commands.properties");
			
			for (Object key : props.keySet())
			{
				final String str = (String) key;
				accessLevel.put(str, Byte.valueOf(props.getProperty(str).trim()));
			}
		}
		catch (IOException e)
		{
			log.error("Can't read commands.properties", e);
		}
	}
	
	/**
	 * Processes a chat message sent by a {@link Player}.<br>
	 * It checks if the {@code text} matches any registered commands.<br>
	 * The method identifies prefixes like {@code //}, {@code ..}, or {@code .} to determine the command type.
	 * @param player The {@link Player} who sent the message.
	 * @param text The raw chat string provided by the user.
	 * @return {@code true} if the command was successfully processed, otherwise {@code false}.
	 */
	public boolean handleChatCommand(Player player, String text)
	{
		if (text.split(" ").length == 0)
		{
			return false;
		}
		
		if ((text.startsWith("//") && (getCommand(text.substring(2)) instanceof AdminCommand)) || (text.startsWith("..") && (getCommand(text.substring(2)) instanceof WeddingCommand)))
		{
			return (getCommand(text.substring(2))).process(player, text.substring(2));
		}
		else if (text.startsWith(".") && ((getCommand(text.substring(1)) instanceof PlayerCommand) || (CustomConfig.ENABLE_ADMIN_DOT_COMMANDS && (getCommand(text.substring(1)) instanceof AdminCommand))))
		{
			return (getCommand(text.substring(1))).process(player, text.substring(1));
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * This method finds a {@link ChatCommand} based on the input text.<br>
	 * It extracts the first word from the string to use as an alias.<br>
	 * If no matching command is found, it returns {@code null}.
	 * @param text The raw chat message sent by a player.
	 * @return The corresponding {@code ChatCommand} object or {@code null}.
	 */
	private ChatCommand getCommand(String text)
	{
		final String alias = text.split(" ")[0];
		final ChatCommand cmd = commands.get(alias);
		return cmd;
	}
	
	/**
	 * This method is called when the script compilation process finishes.<br>
	 * It logs the total number of loaded {@code ChatCommand} objects to the console.
	 */
	public void onCompileDone()
	{
		log.info("Loaded " + commands.size() + " commands.");
	}
}
