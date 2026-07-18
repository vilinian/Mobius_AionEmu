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
import java.util.regex.Pattern;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

/**
 * This class provides automatic transformation for resolving {@code java.util.regex.Pattern} objects.<br>
 * It allows configuration values to be automatically converted into {@link Pattern} instances during property loading.
 * @author SoulKeeper
 */
public class PatternTransformer implements PropertyTransformer<Pattern>
{
	/**
	 * Shared instance of this transformer
	 */
	public static final PatternTransformer SHARED_INSTANCE = new PatternTransformer();
	
	/**
	 * Converts a {@code String} into a {@link Pattern} object.<br>
	 * This method compiles the provided string as a regular expression.<br>
	 * It uses the metadata from the {@code Field} to perform the transformation.
	 * @param value The raw string value to be compiled into a pattern.
	 * @param field The reflection field where the result will be assigned.
	 * @return A compiled {@link Pattern} object.
	 * @throws TransformationException If the provided string is not a valid regular expression.
	 */
	@Override
	public Pattern transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Pattern.compile(value);
		}
		catch (Exception e)
		{
			throw new TransformationException("Not valid RegExp: " + value, e);
		}
	}
}
