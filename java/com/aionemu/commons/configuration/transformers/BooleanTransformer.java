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
 * This class provides basic transformation for boolean values.<br>
 * It accepts {@code true}/{@code false} (case-insensitive) or {@code 1}/{@code 0} as valid inputs.<br>
 * Any other input will result in a {@link TransformationException}.
 * @author SoulKeeper
 */
public class BooleanTransformer implements PropertyTransformer<Boolean>
{
	/**
	 * Shared instance of this transformer, it's thread safe so no need to create multiple instances
	 */
	public static final BooleanTransformer SHARED_INSTANCE = new BooleanTransformer();
	
	/**
	 * Converts a {@code String} into a {@code Boolean} value.<br>
	 * This method supports {@code true}, {@code false}, {@code 1}, and {@code 0}.<br>
	 * It throws an exception if the input is not a valid boolean representation.
	 * @param value The string to be converted.
	 * @param field The target {@code Field} where the result will be applied.
	 * @return The resulting {@code Boolean} value.
	 * @throws TransformationException If the provided {@code value} cannot be parsed as a boolean.
	 */
	@Override
	public Boolean transform(String value, Field field) throws TransformationException
	{
		// We should throw an error if the value is incorrect because Boolean.parseBoolean defaults to false for any string other than true.
		if ("true".equalsIgnoreCase(value) || "1".equals(value))
		{
			return true;
		}
		else if ("false".equalsIgnoreCase(value) || "0".equals(value))
		{
			return false;
		}
		else
		{
			throw new TransformationException("Invalid boolean string: " + value);
		}
	}
}
