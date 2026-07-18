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
package com.aionemu.gameserver.questEngine.handlers;

import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.questEngine.QuestEngine;

/**
 * This class is responsible for loading and initializing quest handlers.<br>
 * It listens for specific classes to register them into the {@link QuestEngine}.
 * @author MrPoke
 */
public class QuestHandlerLoader implements ClassListener
{
	private static final Logger logger = LoggerFactory.getLogger(QuestHandlerLoader.class);
	
	/**
	 * Creates a new instance of the {@link QuestHandlerLoader}.<br>
	 * This constructor initializes the loader for quest handlers.
	 */
	public QuestHandlerLoader()
	{
	}
	
	/**
	 * This method registers the provided classes as quest handlers.<br>
	 * It checks each class using {@code isValidClass} before processing.<br>
	 * If a class is a subclass of {@link QuestHandler}, it creates a new instance and adds it to the {@link QuestEngine}.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Load class " + c.getName());
			}
			
			if (!isValidClass(c))
			{
				continue;
			}
			
			if (ClassUtils.isSubclass(c, QuestHandler.class))
			{
				try
				{
					final Class<? extends QuestHandler> tmp = (Class<? extends QuestHandler>) c;
					if (tmp != null)
					{
						QuestEngine.getInstance().addQuestHandler(tmp.getDeclaredConstructor().newInstance());
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException("Failed to load quest handler class: " + c.getName(), e);
				}
			}
		}
	}
	
	/**
	 * Prepares the system to unload specific classes.<br>
	 * This method clears all data from the {@link QuestEngine}.<br>
	 * It logs each class being unloaded if debug mode is enabled.
	 * @param classes The array of {@code Class<?>} objects to be processed for unloading.
	 */
	@Override
	public void preUnload(Class<?>[] classes)
	{
		if (logger.isDebugEnabled())
		{
			for (Class<?> c : classes) // debug messages
			{
				logger.debug("Unload class " + c.getName());
			}
		}
		
		QuestEngine.getInstance().clear();
	}
	
	/**
	 * Checks if a class is valid for use.<br>
	 * It verifies that the class is public.<br>
	 * The class must not be abstract or an interface.
	 * @param clazz The class to validate.
	 * @return {@code true} if the class meets the requirements, otherwise {@code false}.
	 */
	public boolean isValidClass(Class<?> clazz)
	{
		final int modifiers = clazz.getModifiers();
		
		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !Modifier.isPublic(modifiers))
		{
			return false;
		}
		
		return true;
	}
}
