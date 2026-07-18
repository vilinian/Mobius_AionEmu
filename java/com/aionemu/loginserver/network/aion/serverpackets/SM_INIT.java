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
package com.aionemu.loginserver.network.aion.serverpackets;

import javax.crypto.SecretKey;

import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * This packet handles the initial session setup between the client and the server.<br>
 * It contains the {@code session id}, {@code protocol revision}, and the scrambled RSA public key.<br>
 * The packet is also used to transmit the {@code blowfish key} for secure communication.
 */
public final class SM_INIT extends AionServerPacket
{
	/**
	 * Session Id of this connection
	 */
	private final int sessionId;
	/**
	 * public Rsa key that client will use to encrypt login and password that will be send in RequestAuthLogin client packet.
	 */
	private final byte[] publicRsaKey;
	/**
	 * blowfish key for packet encryption/decryption.
	 */
	private final byte[] blowfishKey;
	
	/**
	 * Initializes a new {@code SM_INIT} packet for the given connection.<br>
	 * This method extracts the necessary keys and session data from the provided objects.
	 * @param client The {@link LoginConnection} used to retrieve the encrypted modulus and session ID.
	 * @param blowfishKey The {@code SecretKey} used to generate the encryption key for this session.
	 */
	public SM_INIT(LoginConnection client, SecretKey blowfishKey)
	{
		this(client.getEncryptedModulus(), blowfishKey.getEncoded(), client.getSessionId());
	}
	
	/**
	 * Initializes a new {@code SM_INIT} packet.<br>
	 * This method sets the session ID, RSA key, and Blowfish key.<br>
	 * It is used to establish secure communication with the client.
	 * @param publicRsaKey The scrambled RSA public key for encryption.
	 * @param blowfishKey The secret key used for packet encryption/decryption.
	 * @param sessionId The unique identifier for the current connection.
	 */
	private SM_INIT(byte[] publicRsaKey, byte[] blowfishKey, int sessionId)
	{
		super(0x00);
		this.sessionId = sessionId;
		this.publicRsaKey = publicRsaKey;
		this.blowfishKey = blowfishKey;
	}
	
	@Override
	protected void writeImpl(LoginConnection con)
	{
		writeD(sessionId); // session id
		writeD(0x0000c621); // protocol revision
		writeB(publicRsaKey); // RSA Public Key
		
		// unk
		writeB(new byte[16]);
		writeB(blowfishKey); // BlowFish key
		writeD(197635); // unk
		writeD(2097152); // unk
	}
}
