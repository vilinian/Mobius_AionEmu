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
package com.aionemu.gameserver.model.house;

import com.aionemu.commons.taskmanager.AbstractLockManager;

/**
 * This class manages the script data associated with a player's house.<br>
 * It provides thread-safe access to these properties by extending {@link AbstractLockManager}.
 * @author Rolandas Use readLock + readUnlock to read both fields, because they may change !!!
 */
public final class PlayerScript extends AbstractLockManager
{
	/**
	 * Creates a new instance of {@link PlayerScript}.<br>
	 * The default values for bytes are set to {@code null}.<br>
	 * The default value for uncompressed size is {@code -1}.
	 */
	public PlayerScript()
	{
	}
	
	/**
	 * Creates a new {@link PlayerScript} instance.<br>
	 * This constructor initializes the script with specific data.
	 * @param compressedBytes The {@code byte[]} containing the compressed script data.
	 * @param uncompressedSize The size of the data after it is decompressed as an {@code int}.
	 */
	public PlayerScript(byte[] compressedBytes, int uncompressedSize)
	{
		this.compressedBytes = compressedBytes;
		this.uncompressedSize = uncompressedSize;
	}
	
	private int uncompressedSize = -1;
	private byte[] compressedBytes = null;
	
	/**
	 * Returns the size of the data before it was compressed.<br>
	 * This value is stored in bytes.
	 * @return The total number of bytes for the uncompressed data.
	 */
	public int getUncompressedSize()
	{
		return uncompressedSize;
	}
	
	/**
	 * Retrieves the compressed data of this script.<br>
	 * This method returns the {@code byte[]} array stored in the object.
	 * @return The {@code byte[]} array containing the compressed bytes.
	 */
	public byte[] getCompressedBytes()
	{
		return compressedBytes;
	}
	
	/**
	 * Updates the internal data of this {@link PlayerScript}.<br>
	 * It sets both the compressed byte array and the size.<br>
	 * This method uses a write lock to ensure thread safety.
	 * @param compressedBytes The {@code byte[]} containing the compressed data.
	 * @param uncompressedSize The {@code int} representing the size of the data before compression.
	 */
	public void setData(byte[] compressedBytes, int uncompressedSize)
	{
		writeLock();
		this.compressedBytes = compressedBytes;
		this.uncompressedSize = uncompressedSize;
		writeUnlock();
	}
}
