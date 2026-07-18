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
package com.aionemu.loginserver.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a collection of utility methods for handling account-related operations.<br>
 * This class simplifies common tasks such as data validation and formatting for {@code Account} objects.
 * @author SoulKeeper
 */
public class AccountUtils
{
	/**
	 * Logger :)
	 */
	private static final Logger log = LoggerFactory.getLogger(AccountUtils.class);
	
	/**
	 * Converts a plain text password into an encoded string.<br>
	 * This method uses the {@code SHA-1} algorithm to hash the input.<br>
	 * The resulting hash is then converted into a {@code Base64} format.
	 * @param password The raw password string to be encoded.
	 * @return The encoded version of the provided password as a {@code String}.
	 */
	public static String encodePassword(String password)
	{
		try
		{
			final MessageDigest messageDiegest = MessageDigest.getInstance("SHA-1");
			messageDiegest.update(password.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(messageDiegest.digest());
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Exception while encoding password");
			throw new Error(e);
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Exception while encoding password");
			throw new Error(e);
		}
	}
}
