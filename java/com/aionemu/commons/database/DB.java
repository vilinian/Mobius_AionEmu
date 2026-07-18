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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>DB Documentation</b>
 * <p>
 * This class is used for making SQL query's utilizing the database connection defined in database.properties<br>
 * <br>
 * Here are the functions that one may use to utilize this class in creating an ease of access to database information.
 * </p>
 * <hr>
 * <b>SELECT (select method)</b>
 * <p>
 * Parameters:
 * <ul>
 * <li><b>Required: String query</b> - Query that will be utilized in select statement.</li>
 * <li><b>Required: ReadStH reader</b> - Interface implementation used to read output ResultSet from select statement.</li>
 * <li><i>Optional: String errMsg</i> - Custom error message that will be logged if query fails.</li>
 * </ul>
 * Returns:(<b>boolean </b>) Returns true if the query ran successfully.<br>
 * <br>
 * Purpose:<br>
 * The select function one to grab data from the database with ease. Utilizing the ReadStT, one may set up query parameters (then use ParamReadStH and set params in <code>setParams()</code>) and read the replied data from the query easily.<br>
 * <br>
 * Best practices is to create custom classes that implement ReadStT in order to throw the data around.<br>
 * <br>
 * After the function is called, it automatically closes and recycles the SQL Connection.<br>
 * <br>
 * Example:
 * 
 * <pre>
 * DB.select(&quot;SELECT name FROM test_table WHERE id=?&quot;, new ParamReadStH()
 * {
 * 	
 * 	public void setParams(PreparedStatement stmt) throws SQLException
 * 	{
 * 		stmt.setInt(1, 50);
 * 	}
 * 	
 * 	public void handleRead(ResultSet rset) throws SQLException
 * 	{
 * 		while (rset.next())
 * 		{
 * 			// Usually here in the custom class you would set it to your
 * 			// needed var.
 * 			var = rset.getString(&quot;name&quot;);
 * 		}
 * 	}
 * });
 * </pre>
 * </p>
 * <hr>
 * <b>INSERT / UPDATE (insertUpdate method)</b>
 * <p>
 * Parameters:
 * <ul>
 * <li><b>Required: String query</b> - Query that will be executed in insert/update statement.</li>
 * <li><b>Required: IUStT batch</b> - Util used to modify query parameters OR add add batches.</li>
 * <li><i>Optional: String errMsg</i> - Custom error message that will be logged if query fails.</li>
 * </ul>
 * Returns:(<b>boolean</b>) Returns true if the query ran successfully.<br>
 * <br>
 * Purpose:<br>
 * The insertUpdate function allows one to insert and update database entries. One may utilize it without needing to modify the query at all or provide a IUStH interface implementation to add parameters to statement and/or gather them in batch.<br>
 * <br>
 * <b> If the IUStH util IS provided in the function's parameters - The coder MUST call the functions stmt.executeBatch() OR stmt.executeUpdate() in order to successfully run the query.<br>
 * If IUSth util is NOT provided, the query will execute as it is. </b><br>
 * <br>
 * Best practices is to create custom classes that implement IUStT in order to modify the query in proficient manners.<br>
 * <br>
 * After the function is called, it automatically closes and recycles the SQL Connection.<br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * DB.insertUpdate(&quot;UPDATE test_table SET some_column=1&quot;);
 * </pre>
 * 
 * <br>
 * 
 * <pre>
 * DB.insertUpdate(&quot;INSERT INTO test_table VALUES (?)&quot;, new IUStH()
 * {
 * 	
 * 	public void handleInsertUpdate(PreparedStatement stmt)
 * 	{
 * 		// Usually this would be data from the custom class that implements
 * 		// IUSth
 * 		String[] batchTestVars =
 * 		{
 * 			&quot;bob&quot;,
 * 			&quot;mike&quot;,
 * 			&quot;joe&quot;
 * 		};
 * 		
 * 		for (String n : batchTestVars)
 * 		{
 * 			stmt.setString(1, n);
 * 			stmt.addBatch();
 * 		}
 * 		
 * 		// REQUIRED
 * 		stmt.executeBatch();
 * 	}
 * });
 * 
 * </pre>
 * 
 * <br>
 * 
 * <pre>
 * DB.insertUpdate(&quot;UPDATE test_table SET some_column=? WHERE other_column=?&quot;, new IUStH()
 * {
 * 	
 * 	public void handleInsertUpdate(PreparedStatement stmt)
 * 	{
 * 		stmt.setString(1, &quot;xxx&quot;);
 * 		stmt.setInt(2, 10);
 * 		stmt.executeUpdate();
 * 	}
 * });
 * 
 * </pre>
 * </p>
 * This class provides a simplified utility for executing SQL queries using the database connection defined in {@code database.properties}.<br>
 * It offers convenient methods to perform {@code select}, {@code insert}, and {@code update} operations while automatically managing the lifecycle of the {@code Connection}.<br>
 * Developers can use custom implementations of reader and updater interfaces to handle query parameters and result processing efficiently.
 * @author Disturbing
 */
public final class DB
{
	/** Logger */
	protected static final Logger log = LoggerFactory.getLogger(DB.class);
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class is intended to be used with static methods only.
	 */
	private DB()
	{
	}
	
	/**
	 * Executes a SQL SELECT query and processes the results.<br>
	 * This method uses the provided {@code ReadStH} to handle the output.<br>
	 * It automatically manages the database connection lifecycle.
	 * @param query The SQL string to be executed.
	 * @param reader The implementation used to read the result set.
	 * @return {@code true} if the query was successful, or {@code false} otherwise.
	 */
	public static boolean select(String query, ReadStH reader)
	{
		return select(query, reader, null);
	}
	
	/**
	 * Executes a SQL select query and processes the results using a reader.<br>
	 * This method handles opening and closing the database connection automatically.<br>
	 * It returns {@code true} if the operation succeeds or {@code false} if an error occurs.
	 * @param query The SQL string to be executed.
	 * @param reader An implementation of {@link ReadStH} to handle the result set.
	 * @param errMsg A custom message to log if the query fails; can be {@code null}.
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	public static boolean select(String query, ReadStH reader, String errMsg)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rset;
		
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(query);
			if (reader instanceof ParamReadStH)
			{
				((ParamReadStH) reader).setParams(stmt);
			}
			
			rset = stmt.executeQuery();
			reader.handleRead(rset);
		}
		catch (Exception e)
		{
			if (errMsg == null)
			{
				log.warn("Error executing select query " + e, e);
			}
			else
			{
				log.warn(errMsg + " " + e, e);
			}
			
			return false;
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
				
				if (stmt != null)
				{
					stmt.close();
				}
			}
			catch (Exception e)
			{
				log.warn("Failed to close DB connection " + e, e);
			}
		}
		
		return true;
	}
	
	/**
	 * Executes a stored procedure or a complex SQL query.<br>
	 * This method uses the provided {@code ReadStH} to handle the results.<br>
	 * It returns {@code true} if the execution succeeds.
	 * @param query The SQL string to be executed.
	 * @param reader The handler used to process the result set.
	 * @return {@code true} if successful, {@code false} otherwise.
	 */
	public static boolean call(String query, ReadStH reader)
	{
		return call(query, reader, null);
	}
	
	/**
	 * Executes a stored procedure or a callable SQL statement.<br>
	 * This method handles the database connection and statement lifecycle automatically.<br>
	 * It uses the provided {@code ReadStH} to handle parameters and results.
	 * @param query The SQL string containing the call statement.
	 * @param reader The implementation used to set parameters and handle the result set.
	 * @param errMsg A custom message to log if the execution fails; can be {@code null}.
	 * @return {@code true} if the procedure executed successfully, otherwise {@code false}.
	 */
	public static boolean call(String query, ReadStH reader, String errMsg)
	{
		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rset;
		
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareCall(query);
			if (reader instanceof CallReadStH)
			{
				((CallReadStH) reader).setParams(stmt);
			}
			
			rset = stmt.executeQuery();
			reader.handleRead(rset);
		}
		catch (Exception e)
		{
			if (errMsg == null)
			{
				log.warn("Error calling stored procedure " + e, e);
			}
			else
			{
				log.warn(errMsg + " " + e, e);
			}
			
			return false;
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
				
				if (stmt != null)
				{
					stmt.close();
				}
			}
			catch (Exception e)
			{
				log.warn("Failed to close DB connection " + e, e);
			}
		}
		
		return true;
	}
	
	/**
	 * Executes an SQL insert or update query.<br>
	 * This method handles the database operation and automatically manages the connection.
	 * @param query The SQL string to be executed.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	public static boolean insertUpdate(String query)
	{
		return insertUpdate(query, null, null);
	}
	
	/**
	 * Executes an SQL query that inserts or updates data.<br>
	 * This method handles the database operation and logs a custom message if it fails.
	 * @param query The SQL string to be executed.
	 * @param errMsg A custom error message to log in case of failure.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	public static boolean insertUpdate(String query, String errMsg)
	{
		return insertUpdate(query, null, errMsg);
	}
	
	/**
	 * Executes an SQL query that performs an insert or update operation.<br>
	 * This method uses the provided {@code IUStH} batch to handle data updates.<br>
	 * It automatically manages the database connection and closes it after completion.
	 * @param query The SQL string to be executed by the database.
	 * @param batch The {@link IUStH} object containing the batch data.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	public static boolean insertUpdate(String query, IUStH batch)
	{
		return insertUpdate(query, batch, null);
	}
	
	/**
	 * Executes an insert or update SQL query.<br>
	 * This method handles batch processing if a {@code IUStH} object is provided.<br>
	 * It automatically manages the database connection and statement lifecycle.
	 * @param query The SQL string to be executed.
	 * @param batch The {@link IUStH} handler for managing batch parameters; can be {@code null}.
	 * @param errMsg A custom message to log if the execution fails; can be {@code null}.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	public static boolean insertUpdate(String query, IUStH batch, String errMsg)
	{
		Connection con = null;
		PreparedStatement stmt = null;
		
		try
		{
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(query);
			if (batch != null)
			{
				batch.handleInsertUpdate(stmt);
			}
			else
			{
				stmt.executeUpdate();
			}
			
		}
		catch (Exception e)
		{
			if (errMsg == null)
			{
				log.warn("Failed to execute IU query " + e, e);
			}
			else
			{
				log.warn(errMsg + " " + e, e);
			}
			
			return false;
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
				
				if (stmt != null)
				{
					stmt.close();
				}
			}
			catch (Exception e)
			{
				log.warn("Failed to close DB connection " + e, e);
			}
		}
		
		return true;
	}
	
	/**
	 * Starts a new database transaction.<br>
	 * This method retrieves a connection from {@link DatabaseFactory}.<br>
	 * It returns a new {@code Transaction} object to manage the session.
	 * @return A new {@code Transaction} instance for the current database connection.
	 * @throws SQLException
	 */
	public static Transaction beginTransaction() throws SQLException
	{
		final Connection con = DatabaseFactory.getConnection();
		return new Transaction(con);
	}
	
	/**
	 * Creates a {@link PreparedStatement} for the given SQL string.<br>
	 * This method uses default settings for result set type and concurrency.
	 * @param sql The SQL query to be prepared.
	 * @return A new {@code PreparedStatement} object.
	 */
	public static PreparedStatement prepareStatement(String sql)
	{
		return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}
	
	/**
	 * Creates a {@link PreparedStatement} using the SQL string provided.<br>
	 * This method handles obtaining the database connection automatically.<br>
	 * It logs an error if the statement cannot be created.
	 * @param sql The SQL query string to prepare.
	 * @param resultSetType The type of result set returned by the query.
	 * @param resultSetConcurrency The concurrency level for the result set.
	 * @return The prepared {@link PreparedStatement} object or {@code null} if an error occurs.
	 */
	public static PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
	{
		Connection c = null;
		PreparedStatement ps = null;
		try
		{
			c = DatabaseFactory.getConnection();
			ps = c.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}
		catch (Exception e)
		{
			log.error("Can't create PreparedStatement for querry: " + sql, e);
			if (c != null)
			{
				try
				{
					c.close();
				}
				catch (SQLException e1)
				{
					log.error("Can't close connection after exception", e1);
				}
			}
		}
		
		return ps;
	}
	
	/**
	 * Executes an update operation using the provided {@code PreparedStatement}.<br>
	 * This method handles potential exceptions and logs any errors that occur.
	 * @param statement The {@code PreparedStatement} to be executed.
	 * @return The number of rows affected by the update, or -1 if an error occurs.
	 */
	public static int executeUpdate(PreparedStatement statement)
	{
		try
		{
			return statement.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("Can't execute update for PreparedStatement", e);
		}
		
		return -1;
	}
	
	/**
	 * Executes an update query and closes the statement.<br>
	 * This method calls {@code executeUpdate} first.<br>
	 * It then automatically calls {@code close} to free resources.
	 * @param statement The {@code PreparedStatement} to execute and close.
	 */
	public static void executeUpdateAndClose(PreparedStatement statement)
	{
		executeUpdate(statement);
		close(statement);
	}
	
	/**
	 * Executes a SQL query using the provided {@code PreparedStatement}.<br>
	 * This method handles the execution and catches any potential exceptions.
	 * @param statement The {@code PreparedStatement} to be executed.
	 * @return A {@code ResultSet} containing the query results or {@code null} if an error occurs.
	 */
	public static ResultSet executeQuerry(PreparedStatement statement)
	{
		ResultSet rs = null;
		try
		{
			rs = statement.executeQuery();
		}
		catch (Exception e)
		{
			log.error("Error while executing querry", e);
		}
		
		return rs;
	}
	
	/**
	 * Closes the provided {@code PreparedStatement}.<br>
	 * This method also closes the underlying {@code Connection}.<br>
	 * It checks if the statement is already closed before attempting to close it.<br>
	 * Any exceptions caught during this process are logged as errors.
	 * @param statement The {@code PreparedStatement} to be closed.
	 */
	public static void close(PreparedStatement statement)
	{
		try
		{
			if (statement.isClosed())
			{
				// noinspection ThrowableInstanceNeverThrown
				log.warn("Attempt to close PreparedStatement that is closes already", new Exception());
				return;
			}
			
			final Connection c = statement.getConnection();
			statement.close();
			c.close();
		}
		catch (Exception e)
		{
			log.error("Error while closing PreparedStatement", e);
		}
	}
}
