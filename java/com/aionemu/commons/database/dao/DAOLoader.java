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
package com.aionemu.commons.database.dao;

import java.lang.reflect.Modifier;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;

/**
 * This utility class is responsible for loading all {@code DAO} instances after the script context has been initialized.<br>
 * Each {@code DAO} must be a public, non-abstract class with a default public no-arg constructor.
 * @author SoulKeeper, Aquanox
 */
public class DAOLoader implements ClassListener
{
	/**
	 * This method registers the provided classes as DAOs.<br>
	 * It checks each class using {@code isValidDAO} before registration.<br>
	 * If a class is valid, it calls {@code registerDAO}.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes)
	{
		// Register DAOs
		for (Class<?> clazz : classes)
		{
			if (!isValidDAO(clazz))
			{
				continue;
			}
			
			try
			{
				DAOManager.registerDAO((Class<? extends DAO>) clazz);
			}
			catch (Exception e)
			{
				throw new Error("Can't register DAO class", e);
			}
		}
	}
	
	/**
	 * Prepares the system to unload specific classes.<br>
	 * This method removes valid {@code DAO} classes from the {@link DAOManager}.<br>
	 * It iterates through the provided array and unregisters each valid class.
	 * @param classes The array of {@code Class<?>} objects to be processed for unloading.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void preUnload(Class<?>[] classes)
	{
		// Unregister DAO's
		for (Class<?> clazz : classes)
		{
			if (!isValidDAO(clazz))
			{
				continue;
			}
			
			try
			{
				DAOManager.unregisterDAO((Class<? extends DAO>) clazz);
			}
			catch (Exception e)
			{
				throw new Error("Can't unregister DAO class", e);
			}
		}
	}
	
	/**
	 * Checks if a given class is a valid DAO.<br>
	 * It verifies that the class follows all required rules.<br>
	 * The class must be a subclass of {@link DAO}.<br>
	 * It cannot be abstract, an interface, or private.
	 * @param clazz The class to check.
	 * @return {@code true} if the class is valid, otherwise {@code false}.
	 */
	public boolean isValidDAO(Class<?> clazz)
	{
		if (!ClassUtils.isSubclass(clazz, DAO.class))
		{
			return false;
		}
		
		final int modifiers = clazz.getModifiers();
		
		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !Modifier.isPublic(modifiers) || clazz.isAnnotationPresent(DisabledDAO.class))
		{
			return false;
		}
		
		return true;
	}
}
