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
package com.aionemu.commons.configuration;

/**
 * This exception is used internally during the configuration process.<br>
 * It is thrown by {@link com.aionemu.commons.configuration.PropertyTransformer} when a transformation error occurs.<br>
 * The exception is caught by {@link com.aionemu.commons.configuration.ConfigurableProcessor}.
 * @author SoulKeeper
 */
public class TransformationException extends RuntimeException
{
	/**
	 * SerialID
	 */
	private static final long serialVersionUID = -6641235751743285902L;
	
	/**
	 * Creates a new instance of {@link TransformationException}.<br>
	 * This is used when an error occurs during the configuration transformation process.
	 */
	public TransformationException()
	{
	}
	
	/**
	 * Creates a new {@link TransformationException} with a specific error message.<br>
	 * This exception is used when a configuration transformation fails.
	 * @param message The detail message explaining why the transformation failed.
	 */
	public TransformationException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new {@link TransformationException} with a specific message.<br>
	 * This constructor also accepts the underlying cause of the error.
	 * @param message The detail message explaining why the transformation failed.
	 * @param cause The original exception that triggered this error, or {@code null}.
	 */
	public TransformationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new {@link TransformationException} with the specified underlying cause.<br>
	 * This is used when an error occurs during the configuration transformation process.
	 * @param cause The original exception that triggered this error.
	 */
	public TransformationException(Throwable cause)
	{
		super(cause);
	}
}
