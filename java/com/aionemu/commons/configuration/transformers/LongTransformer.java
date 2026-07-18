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
 * This class transforms a configuration string into a {@code Long} value.<br>
 * It supports values provided in both decimal and hex formats.
 */
public class LongTransformer implements PropertyTransformer<Long>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final LongTransformer SHARED_INSTANCE = new LongTransformer();
	
	/**
	 * Converts a {@code String} value into a {@code Long}.<br>
	 * This method supports both decimal and hexadecimal formats.<br>
	 * It uses {@code decode} to perform the conversion.
	 * @param value The string representation of the number to convert.
	 * @param field The {@code Field} where the resulting value will be assigned.
	 * @return The converted {@code Long} value.
	 * @throws TransformationException If the string cannot be parsed into a valid number.
	 */
	@Override
	public Long transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Long.decode(value);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
