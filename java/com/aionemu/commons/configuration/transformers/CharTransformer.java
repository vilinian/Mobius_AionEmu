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
package com.aionemu.commons.configuration.transformers;

import java.lang.reflect.Field;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

/**
 * This class transforms a {@code String} representation into a {@code Character}.<br>
 * It is used when a character value is stored as a {@code String} in the configuration.<br>
 * It implements the {@link PropertyTransformer} interface for {@code Character} types.
 */
public class CharTransformer implements PropertyTransformer<Character>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final CharTransformer SHARED_INSTANCE = new CharTransformer();
	
	/**
	 * Converts a {@code String} into a single {@code Character}.<br>
	 * This method ensures the input contains only one character.<br>
	 * It is used to map configuration values to character fields.
	 * @param value The string value to be converted.
	 * @param field The {@link Field} where the result will be assigned.
	 * @return The resulting {@code Character}.
	 * @throws TransformationException If the input has more than one character or an error occurs.
	 */
	@Override
	public Character transform(String value, Field field) throws TransformationException
	{
		try
		{
			final char[] chars = value.toCharArray();
			if (chars.length > 1)
			{
				throw new TransformationException("To many characters in the value");
			}
			
			return chars[0];
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
