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
package com.aionemu.gameserver.services.player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SECURITY_TOKEN;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This service manages the generation and validation of security tokens for {@link Player} accounts.<br>
 * It ensures that client connections are authenticated using secure cryptographic methods.
 * @author xXMashUpXx
 */
public class PlayerSecurityTokenService
{
	private final Logger log = LoggerFactory.getLogger(PlayerSecurityTokenService.class);
	String token;
	
	/**
	 * Creates a new security token for a specific {@link Player}.<br>
	 * This method checks if the {@code player} is valid and does not already have a token.<br>
	 * It generates the token using the player's name and race information.<br>
	 * Finally, it saves the token to the account and sends it to the client.
	 * @param player The {@code Player} object for which the token will be generated.
	 */
	public void generateToken(Player player)
	{
		if (player == null)
		{
			log.warn("[SecurityToken] Player don't exist O.o");
			return;
		}
		
		if (!"".equals(player.getPlayerAccount().getSecurityToken()))
		{
			log.warn("[SecurityToken] Player with already exist token should'nt get another one!");
			return;
		}
		
		MD5(player.getName() + "GH58" + player.getRace().toString() + "8HHGZTU");
		
		player.getPlayerAccount().setSecurityToken(token);
		sendToken(player, player.getPlayerAccount().getSecurityToken());
	}
	
	/**
	 * Sends a security token to a specific player.<br>
	 * This method uses {@code sendPacket} to deliver the data.<br>
	 * It checks if the {@code player} object is {@code null} before sending.
	 * @param player The {@link Player} who will receive the token.
	 * @param token The {@code String} value of the security token to send.
	 */
	public void sendToken(Player player, String token)
	{
		if (player == null)
		{
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_SECURITY_TOKEN(token));
	}
	
	/**
	 * This method generates an MD5 hash from a given input string.<br>
	 * It converts the raw bytes into a hexadecimal format.<br>
	 * The resulting value is stored in the class {@code token} field.
	 * @param md5 The input string to be hashed.
	 * @return The generated hex string or {@code null} if an error occurs.
	 */
	public String MD5(String md5)
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] array = md.digest(md5.getBytes());
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i)
			{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			
			return token = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			log.warn("[SecurityToken] Error to generate token for player!");
		}
		
		return null;
	}
	
	/**
	 * Provides the global instance of the {@link PlayerSecurityTokenService}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code PlayerSecurityTokenService}.
	 */
	public static PlayerSecurityTokenService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PlayerSecurityTokenService instance = new PlayerSecurityTokenService();
	}
}
