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
package com.aionemu.gameserver.utils.xml;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Provides utility methods for compressing and decompressing data.<br>
 * It simplifies the handling of {@code byte[]} arrays using standard compression algorithms.
 * @author Rolandas
 */
public final class CompressUtil
{
	/**
	 * Converts a compressed byte array back into a readable string.<br>
	 * This method uses the {@code Inflater} class to handle decompression.<br>
	 * It returns the result as a {@code UTF-16LE} encoded string.
	 * @param bytes The compressed data to be processed.
	 * @return The decompressed string value.
	 * @throws Exception If an error occurs during the decompression process.
	 */
	public static String Decompress(byte[] bytes) throws Exception
	{
		try (final Inflater decompressor = new Inflater())
		{
			decompressor.setInput(bytes);
			
			// Create an expandable byte array to hold the decompressed data
			final ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
			
			final byte[] buffer = new byte[1024];
			while (true)
			{
				final int count = decompressor.inflate(buffer);
				if (count > 0)
				{
					bos.write(buffer, 0, count);
				}
				else if ((count == 0) && decompressor.finished())
				{
					break;
				}
				else
				{
					throw new RuntimeException("Bad zip data, size: " + bytes.length);
				}
			}
			
			bos.close();
			return bos.toString("UTF-16LE");
		}
	}
	
	/**
	 * Compresses a given {@code String} into a byte array.<br>
	 * This method uses the {@code Deflater} class to reduce data size.<br>
	 * It converts the input text using the {@code UTF-16LE} encoding.
	 * @param text The {@code String} content to be compressed.
	 * @return A {@code byte[]} containing the compressed data.
	 * @throws Exception If an error occurs during the compression process.
	 */
	public static byte[] Compress(String text) throws Exception
	{
		try (final Deflater compressor = new Deflater())
		{
			final byte[] bytes = text.getBytes("UTF-16LE");
			compressor.setInput(bytes);
			
			// Create an expandable byte array to hold the compressed data
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			compressor.finish();
			
			final byte[] buffer = new byte[1024];
			while (!compressor.finished())
			{
				final int count = compressor.deflate(buffer);
				bos.write(buffer, 0, count);
			}
			
			bos.close();
			return bos.toByteArray();
		}
	}
}
