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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a simplified way to manage database transactions.<br>
 * It ensures that data across multiple tables remains synchronized and consistent.<br>
 * Note that this class is not thread-safe and should be synchronized externally.
 * @author SoulKeeper
 */
public class Transaction
{
	/**
	 * Logger for transactions
	 */
	private static final Logger log = LoggerFactory.getLogger(Transaction.class);
	
	/**
	 * Connection that is allocated for this transaction
	 */
	private final Connection connection;
	
	/**
	 * Creates a new {@link Transaction} instance.<br>
	 * This method initializes the connection and disables auto-commit mode.
	 * @param con The {@code Connection} to be used for this transaction.
	 * @throws SQLException If an error occurs while disabling auto-commit.
	 */
	Transaction(Connection con) throws SQLException
	{
		connection = con;
		connection.setAutoCommit(false);
	}
	
	/**
	 * Executes an {@code INSERT} or {@code UPDATE} SQL statement.<br>
	 * This method handles the database operation within the current transaction.<br>
	 * It is a convenience method that calls {@code IUStH)} with a {@code null} second argument.
	 * @param sql The SQL string to be executed by the database.
	 * @throws SQLException If a database access error occurs.
	 */
	public void insertUpdate(String sql) throws SQLException
	{
		insertUpdate(sql, null);
	}
	
	/**
	 * Executes an insert or update SQL statement.<br>
	 * It uses the provided {@code IUStH} object to handle specific logic if it is not {@code null}.<br>
	 * If {@code iusth} is {@code null}, it performs a standard execution.
	 * @param sql The SQL query string to execute.
	 * @param iusth The handler used to process the statement, or {@code null} for default behavior.
	 * @throws SQLException If a database access error occurs.
	 */
	public void insertUpdate(String sql, IUStH iusth) throws SQLException
	{
		final PreparedStatement statement = connection.prepareStatement(sql);
		if (iusth != null)
		{
			iusth.handleInsertUpdate(statement);
		}
		else
		{
			statement.executeUpdate();
		}
	}
	
	/**
	 * Creates a new {@link Savepoint} within the current transaction.<br>
	 * This allows you to roll back changes to a specific point later.
	 * @param name The unique identifier for the savepoint.
	 * @return The created {@code Savepoint} object.
	 * @throws SQLException If a database access error occurs.
	 */
	public Savepoint setSavepoint(String name) throws SQLException
	{
		return connection.setSavepoint(name);
	}
	
	/**
	 * Releases a specific {@code Savepoint} within the current transaction.<br>
	 * This tells the database that the savepoint is no longer needed.<br>
	 * It does not commit the entire transaction.
	 * @param savepoint The {@code Savepoint} to be released.
	 * @throws SQLException If a database access error occurs.
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		connection.releaseSavepoint(savepoint);
	}
	
	/**
	 * Saves all changes made during the current transaction.<br>
	 * This method finalizes the database operations.<br>
	 * It calls {@code commit} with a {@code null} value.
	 * @throws SQLException if an error occurs while committing the transaction.
	 */
	public void commit() throws SQLException
	{
		commit(null);
	}
	
	/**
	 * Finalizes the current database transaction.<br>
	 * If an error occurs during commit, it rolls back to a specific point or fully.<br>
	 * This method also resets {@code autoCommit} to {@code true} and closes the connection.
	 * @param rollBackToOnError The {@link Savepoint} to return to if the commit fails. If {@code null}, it performs a full rollback.
	 * @throws SQLException If a database access error occurs during the process.
	 */
	public void commit(Savepoint rollBackToOnError) throws SQLException
	{
		try
		{
			connection.commit();
		}
		catch (SQLException e)
		{
			log.warn("Error while commiting transaction", e);
			
			try
			{
				if (rollBackToOnError != null)
				{
					connection.rollback(rollBackToOnError);
				}
				else
				{
					connection.rollback();
				}
			}
			catch (SQLException e1)
			{
				log.error("Can't rollback transaction", e1);
			}
		}
		
		connection.setAutoCommit(true);
		connection.close();
	}
}
