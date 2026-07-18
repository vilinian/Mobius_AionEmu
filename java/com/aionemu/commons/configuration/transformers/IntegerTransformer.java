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
 * This class transforms a {@code String} into an {@code Integer}.<br>
 * It supports values represented in both decimal and hex formats.
 * @author SoulKeeper
 */
public class IntegerTransformer implements PropertyTransformer<Integer>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final IntegerTransformer SHARED_INSTANCE = new IntegerTransformer();
	
	/**
	 * Converts a {@code String} into an {@code Integer}.<br>
	 * This method supports both decimal and hexadecimal formats.<br>
	 * It uses {@code decode} to perform the conversion.
	 * @param value The string value to convert.
	 * @param field The {@code Field} where the result will be assigned.
	 * @return The converted {@code Integer} object.
	 * @throws TransformationException If the string cannot be parsed into a valid number.
	 */
	@Override
	public Integer transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Integer.decode(value);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
