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
package com.aionemu.loginserver.network.ncrypt;

import com.aionemu.commons.utils.Rnd;

/**
 * This class serves as the core crypto engine for encrypting and decrypting network packets.<br>
 * It handles error management and verifies packet checksums to ensure data integrity.
 * @author EvilSpirit
 */
public class CryptEngine
{
	/**
	 * A key
	 */
	private byte[] key =
	{
		(byte) 0x6b,
		(byte) 0x60,
		(byte) 0xcb,
		(byte) 0x5b,
		(byte) 0x82,
		(byte) 0xce,
		(byte) 0x90,
		(byte) 0xb1,
		(byte) 0xcc,
		(byte) 0x2b,
		(byte) 0x6c,
		(byte) 0x55,
		(byte) 0x6c,
		(byte) 0x6c,
		(byte) 0x6c,
		(byte) 0x6c
	};
	/**
	 * Tells you whether the key is updated or not
	 */
	private boolean updatedKey = false;
	/**
	 * A secret blowfish cipher
	 */
	private final BlowfishCipher cipher;
	
	/**
	 * Creates a new instance of the {@link CryptEngine}.<br>
	 * This constructor initializes the {@code BlowfishCipher} using the default static key.<br>
	 * It prepares the engine to encrypt the first packet sent to the client.
	 */
	public CryptEngine()
	{
		cipher = new BlowfishCipher(key);
	}
	
	/**
	 * Updates the internal encryption key.<br>
	 * This method replaces the current {@code key} with a new value.
	 * @param newKey The new {@code byte[]} to be used for encryption.
	 */
	public void updateKey(byte[] newKey)
	{
		key = newKey;
	}
	
	/**
	 * Decrypts a specific portion of the provided byte array.<br>
	 * This method uses the {@code BlowfishCipher} to process the data.<br>
	 * It also verifies the checksum of the decrypted content.
	 * @param data The byte array containing the encrypted information.
	 * @param offset The starting position within the {@code data} array.
	 * @param length The number of bytes to decrypt.
	 * @return {@code true} if the decryption and checksum verification succeed, {@code false} otherwise.
	 */
	public boolean decrypt(byte[] data, int offset, int length)
	{
		cipher.decipher(data, offset, length);
		
		return verifyChecksum(data, offset, length);
	}
	
	/**
	 * Encrypts a specific portion of the provided byte array.<br>
	 * This method handles key updates and checksum appending automatically.
	 * @param data The {@code byte[]} containing the raw packet data.
	 * @param offset The starting position in the {@code data} array.
	 * @param length The number of bytes to encrypt.
	 * @return The new total length of the encrypted data.
	 */
	public int encrypt(byte[] data, int offset, int length)
	{
		length += 4;
		
		// Since the key is not updated, the first packet should be encrypted with the initial key.
		if (!updatedKey)
		{
			length += 4;
			length += 8 - (length % 8);
			encXORPass(data, offset, length, Rnd.nextInt());
			cipher.cipher(data, offset, length);
			cipher.updateKey(key);
			updatedKey = true;
		}
		else
		{
			length += 8 - (length % 8);
			appendChecksum(data, offset, length);
			cipher.cipher(data, offset, length);
		}
		
		return length;
	}
	
	/**
	 * Validates the integrity of a data packet using its checksum.<br>
	 * This method checks if the {@code length} is valid and matches the calculated value.
	 * @param data The byte array containing the packet information.
	 * @param offset The starting position in the {@code data} array.
	 * @param length The total length of the data to verify.
	 * @return {@code true} if the checksum is valid, otherwise {@code false}.
	 */
	private boolean verifyChecksum(byte[] data, int offset, int length)
	{
		if (((length & 3) != 0) || (length <= 4))
		{
			return false;
		}
		
		long chksum = 0;
		final int count = length - 4;
		long check;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			check = data[i] & 0xff;
			check |= (data[i + 1] << 8) & 0xff00;
			check |= (data[i + 2] << 0x10) & 0xff0000;
			check |= (data[i + 3] << 0x18) & 0xff000000;
			chksum ^= check;
		}
		
		check = data[i] & 0xff;
		check |= (data[i + 1] << 8) & 0xff00;
		check |= (data[i + 2] << 0x10) & 0xff0000;
		check |= (data[i + 3] << 0x18) & 0xff000000;
		check = data[i] & 0xff;
		check |= (data[i + 1] << 8) & 0xff00;
		check |= (data[i + 2] << 0x10) & 0xff0000;
		check |= (data[i + 3] << 0x18) & 0xff000000;
		
		return 0 == chksum;
	}
	
	/**
	 * Calculates and adds a checksum to the end of a byte array.<br>
	 * This method modifies the {@code raw} array directly.<br>
	 * It uses the last 4 bytes of the specified length to store the result.
	 * @param raw The byte array to be modified.
	 * @param offset The starting position in the array.
	 * @param length The total length of the data to process.
	 */
	private void appendChecksum(byte[] raw, int offset, int length)
	{
		long chksum = 0;
		final int count = length - 4;
		long ecx;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			ecx = raw[i] & 0xff;
			ecx |= (raw[i + 1] << 8) & 0xff00;
			ecx |= (raw[i + 2] << 0x10) & 0xff0000;
			ecx |= (raw[i + 3] << 0x18) & 0xff000000;
			chksum ^= ecx;
		}
		
		ecx = raw[i] & 0xff;
		ecx |= (raw[i + 1] << 8) & 0xff00;
		ecx |= (raw[i + 2] << 0x10) & 0xff0000;
		ecx |= (raw[i + 3] << 0x18) & 0xff000000;
		raw[i] = (byte) (chksum & 0xff);
		raw[i + 1] = (byte) ((chksum >> 0x08) & 0xff);
		raw[i + 2] = (byte) ((chksum >> 0x10) & 0xff);
		raw[i + 3] = (byte) ((chksum >> 0x18) & 0xff);
	}
	
	/**
	 * Performs an XOR operation on a specific section of the data array.<br>
	 * This method modifies the {@code data} array in place using the provided {@code key}.<br>
	 * It processes bytes starting from the specified {@code offset} for a given {@code length}.
	 * @param data The byte array to be modified.
	 * @param offset The starting position within the array.
	 * @param length The number of bytes to process.
	 * @param key The integer value used as the XOR key.
	 */
	private void encXORPass(byte[] data, int offset, int length, int key)
	{
		final int stop = length - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key;
		
		while (pos < stop)
		{
			edx = (data[pos] & 0xFF);
			edx |= (data[pos + 1] & 0xFF) << 8;
			edx |= (data[pos + 2] & 0xFF) << 16;
			edx |= (data[pos + 3] & 0xFF) << 24;
			ecx += edx;
			edx ^= ecx;
			data[pos++] = (byte) (edx & 0xFF);
			data[pos++] = (byte) ((edx >> 8) & 0xFF);
			data[pos++] = (byte) ((edx >> 16) & 0xFF);
			data[pos++] = (byte) ((edx >> 24) & 0xFF);
		}
		
		data[pos++] = (byte) (ecx & 0xFF);
		data[pos++] = (byte) ((ecx >> 8) & 0xFF);
		data[pos++] = (byte) ((ecx >> 16) & 0xFF);
		data[pos] = (byte) ((ecx >> 24) & 0xFF);
	}
}
