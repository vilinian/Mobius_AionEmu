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

import java.lang.reflect.Modifier;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;

/**
 * This class is responsible for loading and registering chat commands from the server.<br>
 * It scans for specific classes to handle user input within the game's chat system.
 * @author Aquanox
 */
public class ChatCommandsLoader implements ClassListener
{
	private final ChatProcessor processor;
	
	/**
	 * Initializes a new instance of the {@link ChatCommandsLoader}.<br>
	 * This constructor sets up the required {@code ChatProcessor}.
	 * @param processor The {@code ChatProcessor} used to handle chat commands.
	 */
	public ChatCommandsLoader(ChatProcessor processor)
	{
		this.processor = processor;
	}
	
	/**
	 * This method registers the provided classes as chat commands.<br>
	 * It checks each class using {@code isValidClass} before processing.<br>
	 * If a class is valid, it creates a new instance and registers it with the {@code processor}.<br>
	 * Finally, it notifies the {@code processor} that compilation is complete.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (!isValidClass(c))
			{
				continue;
			}
			
			final Class<?> tmp = c;
			if (tmp != null)
			{
				try
				{
					processor.registerCommand((ChatCommand) tmp.getDeclaredConstructor().newInstance());
				}
				catch (ReflectiveOperationException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		processor.onCompileDone();
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
	}
	
	/**
	 * Checks if a class is valid for use as a chat command.<br>
	 * It verifies that the class is public and not abstract or an interface.<br>
	 * The class must be a subclass of {@link AdminCommand}, {@link PlayerCommand}, or {@link WeddingCommand}.
	 * @param clazz The class to validate.
	 * @return {@code true} if the class meets all requirements, otherwise {@code false}.
	 */
	public boolean isValidClass(Class<?> clazz)
	{
		final int modifiers = clazz.getModifiers();
		
		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !Modifier.isPublic(modifiers))
		{
			return false;
		}
		
		if (!ClassUtils.isSubclass(clazz, AdminCommand.class) && !ClassUtils.isSubclass(clazz, PlayerCommand.class) && !ClassUtils.isSubclass(clazz, WeddingCommand.class))
		{
			return false;
		}
		
		return true;
	}
}
