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
package com.aionemu.gameserver.utils.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.i18n.CustomMessageId;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;
import com.aionemu.gameserver.world.World;

/**
 * Provides administrative services and commands for Game Masters.<br>
 * This class handles special actions that are restricted to authorized users.<br>
 * It interacts with {@link Player} objects and the {@link World} to manage game state.
 * @author Waii
 */
public class GMService
{
	/**
	 * Provides access to the singleton instance of {@link GMService}.<br>
	 * Use this method to get the global service for managing Game Master actions.
	 * @return The single instance of {@code GMService}.
	 */
	public static GMService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private final Map<Integer, Player> gms = new ConcurrentHashMap<>();
	private boolean announceAny = false;
	private final List<Byte> announceList;
	
	/**
	 * Private constructor for the {@link GMService} class.<br>
	 * This prevents other classes from creating new instances of this service.<br>
	 * It ensures that only one instance is used via the {@code getInstance()} method.
	 */
	private GMService()
	{
		announceList = new ArrayList<>();
		announceAny = AdminConfig.ANNOUNCE_LEVEL_LIST.equals("*");
		if (!announceAny)
		{
			try
			{
				for (String level : AdminConfig.ANNOUNCE_LEVEL_LIST.split(","))
				{
					announceList.add(Byte.parseByte(level));
				}
			}
			catch (Exception e)
			{
				announceAny = true;
			}
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		if (player.isGM())
		{
			gms.put(player.getObjectId(), player);
		}
	}
	
	/**
	 * This method is called when a {@link Player} logs out of the game.<br>
	 * It checks if the player has GM privileges.<br>
	 * If they are a GM, it removes them from the internal list of active GMs.
	 * @param player The {@code Player} object that just logged out.
	 */
	public void onPlayerLogedOut(Player player)
	{
		if (player.isGM())
		{
			gms.remove(player.getObjectId());
		}
	}
	
	/**
	 * Retrieves a list of all Game Masters currently online.<br>
	 * This method returns the collection of {@link Player} objects with GM status.
	 * @return A {@code Collection} of {@code Player} objects who are GMs.
	 */
	public Collection<Player> getGMs()
	{
		return gms.values();
	}
	
	/**
	 * This method is called when a {@code Player} becomes available.<br>
	 * It checks if the player is a GM and updates their status in the system.<br>
	 * If they are a GM, it broadcasts an announcement to all players in the world.
	 * @param player The {@code Player} object that has become available.
	 */
	public void onPlayerAvailable(Player player)
	{
		if (player.isGM())
		{
			gms.put(player.getObjectId(), player);
			String adminTag = "%s";
			final StringBuilder sb = new StringBuilder(adminTag);
			
			if (player.getClientConnection() != null)
			{
				// * = Premium & VIP Membership
				if (MembershipConfig.PREMIUM_TAG_DISPLAY)
				{
					switch (player.getClientConnection().getAccount().getMembership())
					{
						case 1:
							adminTag = sb.insert(0, MembershipConfig.TAG_PREMIUM.substring(0, 2)).toString();
							break;
						case 2:
							adminTag = sb.insert(0, MembershipConfig.TAG_VIP.substring(0, 2)).toString();
							break;
					}
				}
				
				// * = Wedding
				if (player.isMarried())
				{
					adminTag = sb.insert(0, WeddingsConfig.TAG_WEDDING).toString();
				}
				
				if (AdminConfig.CUSTOMTAG_ENABLE)
				{
					if (player.getAccessLevel() == 1)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS1.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 2)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS2.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 3)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS3.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 4)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS4.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 5)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS5.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 6)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS6.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 7)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS7.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 8)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS8.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 9)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS9.replace("%s", sb.toString());
					}
					else if (player.getAccessLevel() == 10)
					{
						adminTag = AdminConfig.CUSTOMTAG_ACCESS10.replace("%s", sb.toString());
					}
				}
			}
			
			final Iterator<Player> iter = World.getInstance().getPlayersIterator();
			while (iter.hasNext())
			{
				PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), "Information : " + String.format(adminTag, player.getName()) + LanguageHandler.translate(CustomMessageId.ANNOUNCE_GM_CONNECTION));
			}
		}
	}
	
	/**
	 * Handles the logic when a player becomes unavailable or disconnects.<br>
	 * This method removes the player from the GM list and broadcasts a deconnection message.<br>
	 * It also determines the correct admin tag based on membership, marriage status, and access levels.
	 * @param player The {@code Player} object who is becoming unavailable.
	 */
	public void onPlayerUnavailable(Player player)
	{
		gms.remove(player.getObjectId());
		String adminTag = "%s";
		final StringBuilder sb = new StringBuilder(adminTag);
		
		if (player.getClientConnection() != null)
		{
			// * = Premium & VIP Membership
			if (MembershipConfig.PREMIUM_TAG_DISPLAY)
			{
				switch (player.getClientConnection().getAccount().getMembership())
				{
					case 1:
						adminTag = sb.insert(0, MembershipConfig.TAG_PREMIUM.substring(0, 2)).toString();
						break;
					case 2:
						adminTag = sb.insert(0, MembershipConfig.TAG_VIP.substring(0, 2)).toString();
						break;
				}
			}
			
			// * = Wedding
			if (player.isMarried())
			{
				adminTag = sb.insert(0, WeddingsConfig.TAG_WEDDING).toString();
			}
			
			if (AdminConfig.CUSTOMTAG_ENABLE)
			{
				if (player.getAccessLevel() == 1)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS1.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 2)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS2.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 3)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS3.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 4)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS4.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 5)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS5.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 6)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS6.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 7)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS7.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 8)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS8.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 9)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS9.replace("%s", sb.toString());
				}
				else if (player.getAccessLevel() == 10)
				{
					adminTag = AdminConfig.CUSTOMTAG_ACCESS10.replace("%s", sb.toString());
				}
			}
		}
		
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext())
		{
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), "Information : " + String.format(adminTag, player.getName()) + LanguageHandler.translate(CustomMessageId.ANNOUNCE_GM_DECONNECTION));
		}
	}
	
	/**
	 * Sends a message to all Game Masters.<br>
	 * The message is formatted as a {@code SM_MESSAGE}.<br>
	 * It uses the {@code ChatType.YELLOW} color for the text.
	 * @param message The content of the message to send.
	 */
	public void broadcastMesage(String message)
	{
		final SM_MESSAGE packet = new SM_MESSAGE(0, null, message, ChatType.YELLOW);
		for (Player player : gms.values())
		{
			PacketSendUtility.sendPacket(player, packet);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final GMService instance = new GMService();
	}
}
