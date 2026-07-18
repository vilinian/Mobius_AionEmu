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
 * This class transforms a {@code String} representation into its corresponding {@link Enum} type.<br>
 * The input string must exactly match the case definition of the enum constants.<br>
 * For example, it will successfully parse {@code "FILE"} but will fail to parse {@code "file"}.
 * @author SoulKeeper
 */
public class EnumTransformer implements PropertyTransformer<Enum<?>>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final EnumTransformer SHARED_INSTANCE = new EnumTransformer();
	
	/**
	 * Converts a {@code String} into its corresponding {@link Enum} type.<br>
	 * The string must match the exact case of the enum constant.<br>
	 * This method uses the type information from the provided {@code Field}.
	 * @param value The string representation of the enum to convert.
	 * @param field The reflection field used to determine the target Enum class.
	 * @return The converted {@link Enum} object.
	 * @throws TransformationException If the string does not match any valid enum constant.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Enum<?> transform(String value, Field field) throws TransformationException
	{
		@SuppressWarnings("rawtypes")
		final Class<? extends Enum> clazz = (Class<? extends Enum>) field.getType();
		
		try
		{
			return Enum.valueOf(clazz, value);
		}
		catch (Exception e)
		{
			throw new TransformationException(e);
		}
	}
}
