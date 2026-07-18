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

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * This class stores standard RSA public key pairs where the modulus {@code n} is scrambled for network transmission.<br>
 * The public pair {@code (e, n)} must be deciphered before it can be used to encrypt data.
 * @author EvilSpirit
 */
public class EncryptedRSAKeyPair
{
	/**
	 * KeyPair
	 */
	private final KeyPair RSAKeyPair;
	/**
	 * Byte
	 */
	private final byte[] encryptedModulus;
	
	/**
	 * Creates a new instance of {@code EncryptedRSAKeyPair}.<br>
	 * This constructor stores the provided {@link KeyPair}.<br>
	 * It also encrypts the modulus from the public key.
	 * @param RSAKeyPair The standard RSA {@link KeyPair} to be processed.
	 */
	public EncryptedRSAKeyPair(KeyPair RSAKeyPair)
	{
		this.RSAKeyPair = RSAKeyPair;
		encryptedModulus = encryptModulus(((RSAPublicKey) this.RSAKeyPair.getPublic()).getModulus());
	}
	
	/**
	 * Encrypts the RSA modulus using a simple scrambling algorithm.<br>
	 * This method prepares the {@code BigInteger} for network transfer.
	 * @param modulus The {@code BigInteger} representing the RSA modulus to be encrypted.
	 * @return A {@code byte[]} containing the scrambled modulus data.
	 */
	private byte[] encryptModulus(BigInteger modulus)
	{
		byte[] encryptedModulus = modulus.toByteArray();
		
		if ((encryptedModulus.length == 0x81) && (encryptedModulus[0] == 0x00))
		{
			final byte[] temp = new byte[0x80];
			
			System.arraycopy(encryptedModulus, 1, temp, 0, 0x80);
			
			encryptedModulus = temp;
		}
		
		for (int i = 0; i < 4; i++)
		{
			final byte temp = encryptedModulus[i];
			
			encryptedModulus[i] = encryptedModulus[0x4d + i];
			encryptedModulus[0x4d + i] = temp;
		}
		
		for (int i = 0; i < 0x40; i++)
		{
			encryptedModulus[i] = (byte) (encryptedModulus[i] ^ encryptedModulus[0x40 + i]);
		}
		
		for (int i = 0; i < 4; i++)
		{
			encryptedModulus[0x0d + i] = (byte) (encryptedModulus[0x0d + i] ^ encryptedModulus[0x34 + i]);
		}
		
		for (int i = 0; i < 0x40; i++)
		{
			encryptedModulus[0x40 + i] = (byte) (encryptedModulus[0x40 + i] ^ encryptedModulus[i]);
		}
		
		return encryptedModulus;
	}
	
	/**
	 * Retrieves the standard {@code KeyPair} stored in this object.<br>
	 * This method returns the raw RSA keys used for encryption.
	 * @return the {@code KeyPair} containing the public and private keys.
	 */
	public KeyPair getRSAKeyPair()
	{
		return RSAKeyPair;
	}
	
	/**
	 * Retrieves the encrypted modulus from the RSA key pair.<br>
	 * This value is used for secure communication between the server and client.
	 * @return a {@code byte[]} containing the encrypted modulus.
	 */
	public byte[] getEncryptedModulus()
	{
		return encryptedModulus;
	}
}
