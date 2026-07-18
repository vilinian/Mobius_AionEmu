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
package com.aionemu.commons.configs;

import java.io.File;

import com.aionemu.commons.configuration.Property;

/**
 * This class holds all the configuration settings for the database.<br>
 * It provides a centralized way to manage connection details and properties.
 * @author SoulKeeper
 */
public class DatabaseConfig
{
	/**
	 * Default database url.
	 */
	@Property(key = "database.url", defaultValue = "jdbc:mysql://localhost:3306/aion_uni")
	public static String DATABASE_URL;
	
	/**
	 * Name of database Driver
	 */
	@Property(key = "database.driver", defaultValue = "com.mysql.cj.jdbc.Driver")
	public static Class<?> DATABASE_DRIVER;
	
	/**
	 * Default database user
	 */
	@Property(key = "database.user", defaultValue = "root")
	public static String DATABASE_USER;
	
	/**
	 * Default database password
	 */
	@Property(key = "database.password", defaultValue = "")
	public static String DATABASE_PASSWORD;
	
	/**
	 * Maximum amount of connections in the HikariCP pool
	 */
	@Property(key = "database.connections.max", defaultValue = "5")
	public static int DATABASE_MAX_CONNECTIONS;
	
	/**
	 * Location of database script context descriptor
	 */
	@Property(key = "database.scriptcontext.descriptor", defaultValue = "./data/scripts/system/database/database.xml")
	public static File DATABASE_SCRIPTCONTEXT_DESCRIPTOR;
}
