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

import static com.aionemu.commons.database.DatabaseFactory.getDatabaseMajorVersion;
import static com.aionemu.commons.database.DatabaseFactory.getDatabaseMinorVersion;
import static com.aionemu.commons.database.DatabaseFactory.getDatabaseName;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.DatabaseConfig;
import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;

/**
 * This class manages the lifecycle and retrieval of {@link DAO} implementations.<br>
 * It resolves the correct implementation based on the current database configuration.
 * @author SoulKeeper, Saelya
 */
public class DAOManager
{
	/**
	 * Logger for DAOManager class
	 */
	private static final Logger log = LoggerFactory.getLogger(DAOManager.class);
	
	/**
	 * Collection of registered DAOs
	 */
	private static final Map<String, DAO> daoMap = new HashMap<>();
	
	/**
	 * This script manager is responsible for loading {@link com.aionemu.commons.database.dao.DAO} implementations
	 */
	private static ScriptManager scriptManager;
	
	/**
	 * Initializes the {@link DAOManager} and its internal components.<br>
	 * This method sets up the {@code scriptManager} and registers necessary class listeners.<br>
	 * It loads the database scripts defined in {@link DatabaseConfig}.<br>
	 * If any fatal error occurs during this process, it will throw an {@code Error}.
	 */
	public static void init()
	{
		try
		{
			scriptManager = new ScriptManager();
			
			// initialize default class listeners for this ScriptManager
			final AggregatedClassListener acl = new AggregatedClassListener();
			acl.addClassListener(new OnClassLoadUnloadListener());
			acl.addClassListener(new ScheduledTaskClassListener());
			acl.addClassListener(new DAOLoader());
			scriptManager.setGlobalClassListener(acl);
			
			scriptManager.load(DatabaseConfig.DATABASE_SCRIPTCONTEXT_DESCRIPTOR);
		}
		catch (RuntimeException e)
		{
			throw new Error(e.getMessage(), e);
		}
		catch (FileNotFoundException e)
		{
			throw new Error("Can't load database script context: " + DatabaseConfig.DATABASE_SCRIPTCONTEXT_DESCRIPTOR, e);
		}
		catch (JAXBException e)
		{
			throw new Error("Can't compile database handlers - check your MySQL5 implementations", e);
		}
		catch (Exception e)
		{
			throw new Error("A fatal error occured during loading or compiling the database handlers", e);
		}
		
		log.info("Loaded " + daoMap.size() + " DAO implementations.");
	}
	
	/**
	 * Shuts down the {@link DAOManager} and cleans up resources.<br>
	 * It calls the shutdown method on the internal {@code scriptManager}.<br>
	 * It clears all registered DAOs from the {@code daoMap}.<br>
	 * The {@code scriptManager} reference is set to {@code null}.
	 */
	public static void shutdown()
	{
		scriptManager.shutdown();
		daoMap.clear();
		scriptManager = null;
	}
	
	/**
	 * Retrieves a specific {@link DAO} instance based on the provided class type.<br>
	 * This method looks up the implementation in the internal registry.<br>
	 * It throws an exception if the requested DAO is not registered.
	 * @param <T>
	 * @param clazz The class of the {@link DAO} to retrieve.
	 * @return The instance of the requested {@code DAO}.
	 * @throws DAONotFoundException If no implementation exists for the given class.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DAO> T getDAO(Class<T> clazz) throws DAONotFoundException
	{
		final DAO result = daoMap.get(clazz.getName());
		
		if (result == null)
		{
			final String s = "DAO for class " + clazz.getSimpleName() + " not implemented";
			log.error(s);
			throw new DAONotFoundException(s);
		}
		
		return (T) result;
	}
	
	/**
	 * Registers a new {@link DAO} implementation into the system.<br>
	 * This method creates an instance of the provided {@code daoClass}.<br>
	 * It checks if the {@code DAO} supports the current database version before registration.
	 * @param daoClass The class of the {@link DAO} to register.
	 * @throws DAOAlreadyRegisteredException If a {@code DAO} with the same class name is already registered.
	 * @throws IllegalAccessException If the constructor of the {@code daoClass} is inaccessible.
	 * @throws InstantiationException If the {@code daoClass} cannot be instantiated.
	 */
	public static void registerDAO(Class<? extends DAO> daoClass) throws DAOAlreadyRegisteredException, IllegalAccessException, InstantiationException
	{
		final DAO dao;
		try
		{
			dao = daoClass.getDeclaredConstructor().newInstance();
		}
		catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException e)
		{
			throw new InstantiationException(e.toString());
		}
		
		if (!dao.supports(getDatabaseName(), getDatabaseMajorVersion(), getDatabaseMinorVersion()))
		{
			return;
		}
		
		synchronized (DAOManager.class)
		{
			final DAO oldDao = daoMap.get(dao.getClassName());
			if (oldDao != null)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("DAO with className ").append(dao.getClassName()).append(" is used by ");
				sb.append(oldDao.getClass().getName()).append(". Can't override with ");
				sb.append(daoClass.getName()).append(".");
				final String s = sb.toString();
				log.error(s);
				throw new DAOAlreadyRegisteredException(s);
			}
			
			daoMap.put(dao.getClassName(), dao);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("DAO " + dao.getClassName() + " was successfuly registered.");
		}
	}
	
	/**
	 * Removes a specific {@link DAO} implementation from the manager.<br>
	 * This method searches for the class and deletes it from the internal map.
	 * @param daoClass The {@code Class} of the {@link DAO} to be removed.
	 */
	public static void unregisterDAO(Class<? extends DAO> daoClass)
	{
		synchronized (DAOManager.class)
		{
			for (DAO dao : daoMap.values())
			{
				if (dao.getClass() == daoClass)
				{
					daoMap.remove(dao.getClassName());
					
					if (log.isDebugEnabled())
					{
						log.debug("DAO " + dao.getClassName() + " was successfuly unregistered.");
					}
					
					break;
				}
			}
		}
	}
	
	/**
	 * Private constructor for the {@link DAOManager} class.<br>
	 * This prevents other classes from creating new instances of this manager.<br>
	 * The class uses static methods to manage {@link DAO} implementations.
	 */
	private DAOManager()
	{
		// empty
	}
}
