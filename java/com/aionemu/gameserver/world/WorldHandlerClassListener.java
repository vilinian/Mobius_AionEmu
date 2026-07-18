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

import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.world.handlers.WorldHandler;

/**
 * This class listens for specific events related to {@link WorldHandler} classes.<br>
 * It allows the server to react to changes or actions involving world handlers dynamically.
 * @author ATracer
 */
public class WorldHandlerClassListener implements ClassListener
{
	private static final Logger log = LoggerFactory.getLogger(WorldHandlerClassListener.class);
	
	/**
	 * This method processes the provided classes after they are loaded.<br>
	 * It checks if each class is a subclass of {@link WorldHandler}.<br>
	 * Valid classes are added to the {@link WorldEngine} instance.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Load class " + c.getName());
			}
			
			if (!isValidClass(c))
			{
				continue;
			}
			
			if (ClassUtils.isSubclass(c, WorldHandler.class))
			{
				final Class<? extends WorldHandler> tmp = (Class<? extends WorldHandler>) c;
				if (tmp != null)
				{
					WorldEngine.getInstance().addWorldHandlerClass(tmp);
				}
			}
		}
	}
	
	/**
	 * Logs the intent to unload specific classes.<br>
	 * This method iterates through the provided array and logs each class name if debug logging is enabled.
	 * @param classes The array of {@code Class<?>} objects to be logged for unloading.
	 */
	@Override
	public void preUnload(Class<?>[] classes)
	{
		if (log.isDebugEnabled())
		{
			for (Class<?> c : classes)
			{
				log.debug("Unload class " + c.getName());
			}
		}
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
