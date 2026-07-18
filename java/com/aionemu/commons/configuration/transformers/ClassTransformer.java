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
 * This class provides functionality to retrieve a {@code Class} object based on its string name.<br>
 * It ensures that the class is not initialized during the retrieval process.
 * @see Class#forName(String)
 * @see Class#forName(String, boolean, ClassLoader)
 * @author Aquanox
 */
public class ClassTransformer implements PropertyTransformer<Class<?>>
{
	/** Shared instance. */
	public static final ClassTransformer SHARED_INSTANCE = new ClassTransformer();
	
	/**
	 * Converts a string into its corresponding {@code Class} object.<br>
	 * This method uses the current class loader to find the class.<br>
	 * It throws a {@link TransformationException} if the class is not found.
	 * @param value The name of the class as a string.
	 * @param field The {@code Field} associated with this property.
	 * @return The {@code Class} object corresponding to the provided string.
	 * @throws TransformationException If the class cannot be located.
	 */
	@Override
	public Class<?> transform(String value, Field field) throws TransformationException
	{
		try
		{
			return Class.forName(value, false, getClass().getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			throw new TransformationException("Cannot find class with name '" + value + "'");
		}
	}
}
