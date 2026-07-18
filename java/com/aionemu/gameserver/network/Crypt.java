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
package com.aionemu.gameserver.network;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;

/**
 * This class handles the encryption of outgoing server packets.<br>
 * It also manages the decryption of incoming client packets.<br>
 * Use this class to ensure secure communication between the client and the server.
 * @author hack99
 * @author kao
 * @author -Nemesiss-
 */
public class Crypt
{
	private final static Logger log = LoggerFactory.getLogger(Crypt.class);
	/**
	 * Second byte of server packet must be equal to this
	 */
	public final static byte staticServerPacketCode = 0x56; // 7.5
	/**
	 * Crypt is enabled after first server packet was send.
	 */
	private boolean isEnabled;
	private EncryptionKeyPair packetKey = null;
	
	/**
	 * Enables the encryption key for server and client communication.<br>
	 * This method generates a random {@code int} to create a new {@link EncryptionKeyPair}.<br>
	 * It ensures that only one key is set during the session.
	 * @return The calculated "false key" used by the client to encrypt and decrypt packets.
	 */
	public int enableKey()
	{
		if (packetKey != null)
		{
			throw new KeyAlreadySetException();
		}
		
		/**
		 * rnd key - this will be used to encrypt/decrypt packet
		 */
		final int key = Rnd.nextInt();
		
		packetKey = new EncryptionKeyPair(key);
		
		log.debug("new encrypt key: " + packetKey);
		
		/**
		 * false key that will be sent to aion client in SM_KEY packet
		 */
		return (key ^ 0xCD92E4D9) + 0x3FF2CCDF; // 7.x
	}
	
	/**
	 * Decrypts the data contained in a {@code ByteBuffer}.<br>
	 * This method checks if encryption is enabled before processing.<br>
	 * If it is disabled, the method returns {@code true} without changes.
	 * @param buf The {@code ByteBuffer} containing the encrypted data to be decrypted.
	 * @return {@code true} if the packet was processed or skipped successfully, and {@code false} otherwise.
	 */
	public boolean decrypt(ByteBuffer buf)
	{
		if (!isEnabled)
		{
			log.debug("if encryption wasn't enabled, then maybe it's client reconnection, so skip packet");
			return true;
		}
		
		return packetKey.decrypt(buf);
	}
	
	/**
	 * Encrypts the data contained within a {@code ByteBuffer}.<br>
	 * This method checks if encryption is enabled before processing.<br>
	 * If it is not enabled, it enables it and returns early.<br>
	 * Otherwise, it calls the {@code encrypt} method.
	 * @param buf The {@code ByteBuffer} to be encrypted.
	 */
	public void encrypt(ByteBuffer buf)
	{
		if (!isEnabled)
		{
			/**
			 * first packet is not encrypted
			 */
			isEnabled = true;
			log.debug("packet is not encrypted... send in SM_KEY");
			return;
		}
		
		packetKey.encrypt(buf);
	}
	
	/**
	 * Converts an operation code into its encoded format.<br>
	 * This method applies a specific bitwise transformation for version {@code 7.5}.
	 * @param op The original operation code to encode.
	 * @return The resulting encoded integer value.
	 */
	public static int encodeOpcodec(int op)
	{
		return (op + 0xD8) ^ 0xD9; // 7.5
	}
}
