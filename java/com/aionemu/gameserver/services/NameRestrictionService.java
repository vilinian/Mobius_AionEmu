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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.NameConfig;

/**
 * This service handles the validation of character names based on predefined rules.<br>
 * It checks if a name is allowed by comparing it against {@link NameConfig} settings.<br>
 * Use this class to ensure that players choose appropriate and valid identifiers.
 * @author nrg
 */
public class NameRestrictionService
{
	private static final String ENCODED_BAD_WORD = "----";
	private static String[] forbiddenSequences;
	private static String[] forbiddenByClient;
	
	/**
	 * Checks if the provided {@code name} is valid.<br>
	 * It compares the input against the pattern defined in {@link NameConfig}.
	 * @param name The string to validate.
	 * @return {@code true} if the name matches the required pattern, {@code false} otherwise.
	 */
	public static boolean isValidName(String name)
	{
		return NameConfig.CHAR_NAME_PATTERN.matcher(name).matches();
	}
	
	/**
	 * Checks if the provided {@code name} contains any restricted words.<br>
	 * This method validates the input against both client and sequence rules.
	 * @param name The string to check for forbidden content.
	 * @return {@code true} if the name is forbidden, {@code false} otherwise.
	 */
	public static boolean isForbiddenWord(String name)
	{
		return isForbiddenByClient(name) || isForbiddenBySequence(name);
	}
	
	/**
	 * Checks if a name is restricted by the client configuration.<br>
	 * It compares the input against a list of forbidden strings.
	 * @param name The name to check.
	 * @return {@code true} if the name is forbidden, {@code false} otherwise.
	 */
	private static boolean isForbiddenByClient(String name)
	{
		if (!NameConfig.NAME_FORBIDDEN_ENABLE || NameConfig.NAME_FORBIDDEN_CLIENT.equals(""))
		{
			return false;
		}
		
		if ((forbiddenByClient == null) || (forbiddenByClient.length == 0))
		{
			forbiddenByClient = NameConfig.NAME_FORBIDDEN_CLIENT.split(",");
		}
		
		for (String s : forbiddenByClient)
		{
			if (name.equalsIgnoreCase(s))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the provided name contains any forbidden sequences.<br>
	 * It compares the input against a list of restricted strings defined in {@link NameConfig}.<br>
	 * The check is case-insensitive.
	 * @param name The name to validate.
	 * @return {@code true} if a forbidden sequence is found, {@code false} otherwise.
	 */
	private static boolean isForbiddenBySequence(String name)
	{
		if (NameConfig.NAME_SEQUENCE_FORBIDDEN.equals(""))
		{
			return false;
		}
		
		if ((forbiddenSequences == null) || (forbiddenSequences.length == 0))
		{
			forbiddenSequences = NameConfig.NAME_SEQUENCE_FORBIDDEN.toLowerCase().split(",");
		}
		
		for (String s : forbiddenSequences)
		{
			if (name.toLowerCase().contains(s))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method cleans a chat message by removing bad words.<br>
	 * It checks every word in the {@code message}.<br>
	 * Any forbidden words are replaced with a placeholder.
	 * @param message The original text to be filtered.
	 * @return The cleaned version of the string.
	 */
	public static String filterMessage(String message)
	{
		for (String word : message.split(" "))
		{
			if (isForbiddenWord(word))
			{
				message = message.replace(word, ENCODED_BAD_WORD);
			}
		}
		
		return message;
	}
}
