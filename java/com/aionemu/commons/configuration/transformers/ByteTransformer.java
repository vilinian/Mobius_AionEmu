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
 * This class transforms a {@code String} into a {@code Byte}.<br>
 * It supports input strings in both decimal and hex formats.<br>
 * A {@link com.aionemu.commons.configuration.TransformationException} is thrown if the transformation fails.
 * @author SoulKeeper
 */
public class ByteTransformer implements PropertyTransformer<Byte>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final ByteTransformer SHARED_INSTANCE = new ByteTransformer();
	
	/**
	 * Converts a {@code String} value into a {@code Byte}.<br>
	 * The input string can be in decimal or hex format.<br>
	 * This method is used by the configuration system to map values to fields.
	 * @param value The string value to convert.
	 * @param field The {@code Field} where the result will be assigned.
	 * @return The converted {@code Byte} object.
	 * @throws TransformationException If the conversion fails for any reason.
	 */
	@Override
	public Byte transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Byte.decode(value);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
