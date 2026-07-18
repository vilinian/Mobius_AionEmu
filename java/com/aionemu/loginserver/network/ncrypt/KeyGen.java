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

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;

/**
 * This utility class provides functionality to generate cryptographic keys.<br>
 * It supports generating both {@code SecretKey} objects for the Blowfish algorithm and {@code KeyPair} objects for RSA encryption.
 * @author -Nemesiss-
 */
public class KeyGen
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger log = LoggerFactory.getLogger(KeyGen.class);
	/**
	 * Key generator for blowfish
	 */
	private static KeyGenerator blowfishKeyGen;
	/**
	 * Public/Static RSA KeyPairs with encrypted modulus N
	 */
	private static EncryptedRSAKeyPair[] encryptedRSAKeyPairs;
	
	/**
	 * Initializes the {@code KeyGen} class.<br>
	 * This method sets up the Blowfish and RSA key generators.<br>
	 * It also pre-generates a set of {@link EncryptedRSAKeyPair} objects.
	 * @throws GeneralSecurityException If an error occurs during security initialization.
	 */
	public static void init() throws GeneralSecurityException
	{
		log.info("Initializing Key Generator...");
		
		blowfishKeyGen = KeyGenerator.getInstance("Blowfish");
		
		final KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		
		final RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		rsaKeyPairGenerator.initialize(spec);
		encryptedRSAKeyPairs = new EncryptedRSAKeyPair[10];
		
		for (int i = 0; i < 10; i++)
		{
			encryptedRSAKeyPairs[i] = new EncryptedRSAKeyPair(rsaKeyPairGenerator.generateKeyPair());
		}
		
		// Pre-init RSA cipher.. saving about 300ms
		final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
		rsaCipher.init(Cipher.DECRYPT_MODE, encryptedRSAKeyPairs[0].getRSAKeyPair().getPrivate());
	}
	
	/**
	 * Generates a new {@code SecretKey} for the Blowfish algorithm.<br>
	 * This method uses the internal {@code KeyGenerator}.
	 * @return A newly generated {@code SecretKey}.
	 */
	public static SecretKey generateBlowfishKey()
	{
		return blowfishKeyGen.generateKey();
	}
	
	/**
	 * Retrieves a random RSA key pair from the pre-generated list.<br>
	 * This method uses {@code nextInt} to select an index.
	 * @return A randomly selected {@link EncryptedRSAKeyPair} object.
	 */
	public static EncryptedRSAKeyPair getEncryptedRSAKeyPair()
	{
		return encryptedRSAKeyPairs[Rnd.nextInt(10)];
	}
}
