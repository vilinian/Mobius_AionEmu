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

import java.io.File;
import java.lang.reflect.Field;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

/**
 * This class transforms a {@code String} into a {@link File} object.<br>
 * It creates a new {@code File} instance without checking if the path already exists.
 * @author SoulKeeper
 */
public class FileTransformer implements PropertyTransformer<File>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final FileTransformer SHARED_INSTANCE = new FileTransformer();
	
	/**
	 * Converts a {@code String} into a {@link File} object.<br>
	 * This method creates a new file instance based on the provided path.<br>
	 * It does not check if the file already exists on the disk.
	 * @param value The string path of the file to create.
	 * @param field The {@code Field} where the resulting file will be assigned.
	 * @return A new {@link File} object representing the provided path.
	 * @throws TransformationException If an error occurs during the transformation process.
	 */
	@Override
	public File transform(String value, Field field) throws TransformationException
	{
		return new File(value);
	}
}
