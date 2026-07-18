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
 * Transforms a {@code String} into its corresponding {@code Short} value.<br>
 * It supports both decimal and hexadecimal representations of the number.
 * @author SoulKeeper
 */
public class ShortTransformer implements PropertyTransformer<Short>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final ShortTransformer SHARED_INSTANCE = new ShortTransformer();
	
	/**
	 * Converts a {@code String} into its corresponding {@code Short} value.<br>
	 * This method supports both decimal and hexadecimal formats.<br>
	 * It uses {@code decode} to perform the conversion.
	 * @param value The string representation of the number to convert.
	 * @param field The {@code Field} where the resulting value will be assigned.
	 * @return The converted {@code Short} object.
	 * @throws TransformationException If the string is not a valid short or an error occurs.
	 */
	@Override
	public Short transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Short.decode(value);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
