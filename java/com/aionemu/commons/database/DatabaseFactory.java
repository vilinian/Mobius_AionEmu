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
package com.aionemu.commons.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This factory creates a connection pool for the server using {@code HikariDataSource}.<br>
 * It reads configuration from {@code database.properties} to build and manage the lifecycle of database connections.
 * @author Disturbing
 * @author SoulKeeper
 */
public class DatabaseFactory
{
	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);
	
	/**
	 * Connection Pool holds all connections - Idle or Active
	 */
	private static HikariDataSource connectionPool;
	
	/**
	 * Returns name of the database that is used For isntance, MySQL returns "MySQL"
	 */
	private static String databaseName;
	
	/**
	 * Retursn major version that is used For instance, MySQL 5.0.51 community edition returns 5
	 */
	private static int databaseMajorVersion;
	
	/**
	 * Retursn minor version that is used For instance, MySQL 5.0.51 community edition returns 0
	 */
	private static int databaseMinorVersion;
	
	/**
	 * Initializes the {@code DatabaseFactory} and sets up the connection pool.<br>
	 * This method reads configurations from {@link DatabaseConfig}.<br>
	 * It establishes a connection to retrieve database metadata.
	 */
	public synchronized static void init()
	{
		if (connectionPool != null)
		{
			return;
		}
		
		final int maxConnections = Math.max(DatabaseConfig.DATABASE_MAX_CONNECTIONS, 2);
		
		final HikariConfig config = new HikariConfig();
		config.setDriverClassName(DatabaseConfig.DATABASE_DRIVER.getName());
		config.setJdbcUrl(DatabaseConfig.DATABASE_URL);
		config.setUsername(DatabaseConfig.DATABASE_USER);
		config.setPassword(DatabaseConfig.DATABASE_PASSWORD);
		config.setMaximumPoolSize(maxConnections);
		config.setMinimumIdle(Math.max(maxConnections / 10, 2));
		config.setConnectionTimeout(60000); // 1 minute.
		config.setIdleTimeout(300000); // 5 minutes.
		config.setMaxLifetime(600000); // 10 minutes.
		config.setLeakDetectionThreshold(600000); // 10 minutes.
		config.setPoolName("AionEmuPool");
		config.setValidationTimeout(5000); // 5 seconds.
		
		try
		{
			connectionPool = new HikariDataSource(config);
		}
		catch (Exception e)
		{
			log.error("Error while creating DB Connection pool", e);
			throw new Error("DatabaseFactory not initialized!", e);
		}
		
		try
		{
			final Connection c = getConnection();
			final DatabaseMetaData dmd = c.getMetaData();
			databaseName = dmd.getDatabaseProductName();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			c.close();
		}
		catch (Exception e)
		{
			log.error("Error with connection string: " + DatabaseConfig.DATABASE_URL, e);
			throw new Error("DatabaseFactory not initialized!", e);
		}
		
		log.info("Successfully connected to database");
	}
	
	/**
	 * Retrieves a connection from the database pool.<br>
	 * This method ensures that {@code autoCommit} is set to {@code true}.<br>
	 * It uses the internal {@link HikariDataSource} to provide the link.
	 * @return A new {@code Connection} object.
	 * @throws SQLException If a database access error occurs.
	 */
	public static Connection getConnection() throws SQLException
	{
		final Connection con = connectionPool.getConnection();
		
		if (!con.getAutoCommit())
		{
			log.error("Connection Settings Error: Connection obtained from database factory should be in auto-commit" + " mode. Forsing auto-commit to true. Please check source code for connections beeing not properly" + " closed.");
			con.setAutoCommit(true);
		}
		
		return con;
	}
	
	/**
	 * Retrieves the number of connections currently in use.<br>
	 * This method queries the {@code HikariDataSource} pool metrics.
	 * @return The total count of active database connections.
	 */
	public int getActiveConnections()
	{
		return connectionPool.getHikariPoolMXBean().getActiveConnections();
	}
	
	/**
	 * Retrieves the number of connections currently sitting idle in the pool.<br>
	 * This method queries the {@code HikariDataSource} metrics.
	 * @return The total count of idle connections as an {@code int}.
	 */
	public int getIdleConnections()
	{
		return connectionPool.getHikariPoolMXBean().getIdleConnections();
	}
	
	/**
	 * Shuts down the database connection pool.<br>
	 * This method closes all active connections and sets the {@code connectionPool} to {@code null}.<br>
	 * It allows the {@code init} method to be called again later.
	 */
	public static synchronized void shutdown()
	{
		try
		{
			connectionPool.close();
		}
		catch (Exception e)
		{
			log.warn("Failed to shutdown DatabaseFactory", e);
		}
		
		// set datasource to null so we can call init() once more...
		connectionPool = null;
	}
	
	/**
	 * Closes the database resources safely.<br>
	 * This method ensures that both {@code PreparedStatement} and {@code Connection} are closed.<br>
	 * It helps prevent memory leaks in the application.
	 * @param st The {@code PreparedStatement} to be closed.
	 * @param con The {@code Connection} to be closed.
	 */
	public static void close(PreparedStatement st, Connection con)
	{
		close(st);
		close(con);
	}
	
	/**
	 * Safely closes a {@code PreparedStatement}.<br>
	 * This method checks if the statement is {@code null} or already closed.<br>
	 * It handles any {@code SQLException} internally and logs errors.
	 * @param st The {@code PreparedStatement} to be closed.
	 */
	public static void close(PreparedStatement st)
	{
		if (st == null)
		{
			return;
		}
		
		try
		{
			if (!st.isClosed())
			{
				st.close();
			}
		}
		catch (SQLException e)
		{
			log.error("Can't close Prepared Statement", e);
		}
	}
	
	/**
	 * Closes the provided {@code Connection}.<br>
	 * This method ensures that {@code autoCommit} is set to {@code true} before closing.<br>
	 * It handles {@code SQLException} internally and logs any errors.
	 * @param con The {@code Connection} object to be closed.
	 */
	public static void close(Connection con)
	{
		if (con == null)
		{
			return;
		}
		
		try
		{
			if (!con.getAutoCommit())
			{
				con.setAutoCommit(true);
			}
		}
		catch (SQLException e)
		{
			log.error("Failed to set autocommit to true while closing connection: ", e);
		}
		
		try
		{
			con.close();
		}
		catch (SQLException e)
		{
			log.error("DatabaseFactory: Failed to close database connection!", e);
		}
	}
	
	/**
	 * Retrieves the name of the current database.<br>
	 * This is used to identify the database type, such as {@code MySQL}.
	 * @return The name of the database as a {@code String}.
	 */
	public static String getDatabaseName()
	{
		return databaseName;
	}
	
	/**
	 * Retrieves the major version of the current database.<br>
	 * This value is determined during the initialization of {@link DatabaseFactory}.<br>
	 * For example, a MySQL 5.0.51 database will return {@code 5}.
	 * @return The major version number as an {@code int}.
	 */
	public static int getDatabaseMajorVersion()
	{
		return databaseMajorVersion;
	}
	
	/**
	 * Retrieves the minor version of the current database.<br>
	 * This value is used to identify specific sub-versions of the database engine.
	 * @return The {@code int} representing the minor version.
	 */
	public static int getDatabaseMinorVersion()
	{
		return databaseMinorVersion;
	}
	
	/**
	 * Private constructor for the {@link DatabaseFactory} class.<br>
	 * This prevents other classes from creating new instances of this factory.<br>
	 * Use the {@code init} method to initialize the database connection pool.
	 */
	private DatabaseFactory()
	{
		//
	}
}
