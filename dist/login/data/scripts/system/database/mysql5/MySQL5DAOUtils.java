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



package mysql5;

/**
 * This class provides utility methods for {@code MySQL5} Data Access Object (DAO) operations.<br>
 * It simplifies common database interactions for the {@code mysql5} system.
 * @author SoulKeeper
 */
public class MySQL5DAOUtils {

	/**
	 * Constant for MySQL name ;)
	 */
	public static final String MYSQL_DB_NAME = "MySQL";

	/**
	 * Checks if the provided database is compatible with MySQL 5.<br>
	 * It compares the database name and the major version number.
	 * @param db The name of the database to check.
	 * @param majorVersion The major version number of the database.
	 * @param minorVersion The minor version number of the database.
	 * @return {@code true} if the database is MySQL 5, otherwise {@code false}.
	 */
	public static boolean supports(String db, int majorVersion, int minorVersion) {
		return MYSQL_DB_NAME.equals(db) && majorVersion == 5;
	}
}
