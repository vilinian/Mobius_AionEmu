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
 * This class serves as a placeholder implementation of {@link PropertyTransformer}.<br>
 * It is used to simplify the codebase by reducing conditional logic.<br>
 * This class performs no actual transformations.
 * @author SoulKeeper
 */
public class StringTransformer implements PropertyTransformer<String>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final StringTransformer SHARED_INSTANCE = new StringTransformer();
	
	/**
	 * This method returns the input string without any changes.<br>
	 * It is used to satisfy the {@link PropertyTransformer} interface requirements.
	 * @param value The original {@code String} to be processed.
	 * @param field The {@code Field} where the result will be assigned.
	 * @return The same {@code String} provided in the {@code value} parameter.
	 * @throws TransformationException If an error occurs during transformation.
	 */
	@Override
	public String transform(String value, Field field) throws TransformationException
	{
		return value;
	}
}
