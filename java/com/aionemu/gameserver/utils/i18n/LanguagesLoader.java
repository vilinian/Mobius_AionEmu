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
package com.aionemu.gameserver.utils.i18n;

import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.GameServer;

/**
 * This class is responsible for loading and managing internationalization (i18n) files.<br>
 * It listens for class changes to ensure that language resources are loaded correctly into the {@code GameServer}.
 * @author blakawk
 */
public class LanguagesLoader implements ClassListener
{
	private static final Logger log = LoggerFactory.getLogger(Language.class);
	private final LanguageHandler handler;
	
	/**
	 * Creates a new instance of {@code LanguagesLoader}.<br>
	 * This constructor initializes the loader with a specific {@link LanguageHandler}.
	 * @param handler The {@code LanguageHandler} used to manage language data.
	 */
	public LanguagesLoader(LanguageHandler handler)
	{
		this.handler = handler;
	}
	
	/**
	 * This method processes and registers language classes.<br>
	 * It filters the provided {@code Class<?>[]} to find subclasses of {@link Language}.<br>
	 * Valid languages are instantiated and registered via the {@code handler}.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> clazz : classes)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Loading class " + clazz.getName());
			}
			
			if (!isValidClass(clazz))
			{
				continue;
			}
			
			if (ClassUtils.isSubclass(clazz, Language.class))
			{
				final Class<? extends Language> language = (Class<? extends Language>) clazz;
				if (language != null)
				{
					try
					{
						handler.registerLanguage(language.getDeclaredConstructor().newInstance());
					}
					catch (Exception e)
					{
						log.error("Registering " + language.getName(), e);
					}
				}
			}
		}
		
		GameServer.log.info("[LanguagesLoader] Loaded " + handler.size() + " custom message handlers.");
	}
	
	/**
	 * Prepares the system to unload specific classes.<br>
	 * This method removes valid {@code DAO} classes from the {@link DAOManager}.<br>
	 * It iterates through the provided array and unregisters each valid class.
	 * @param classes The array of {@code Class<?>} objects to be processed for unloading.
	 */
	@Override
	public void preUnload(Class<?>[] classes)
	{
		if (log.isDebugEnabled())
		{
			for (Class<?> clazz : classes)
			{
				log.debug("Unload language " + clazz.getName());
			}
		}
		
		handler.clear();
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
