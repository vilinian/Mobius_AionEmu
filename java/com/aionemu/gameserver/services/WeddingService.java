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
package com.aionemu.gameserver.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.dao.WeddingDAO;
import com.aionemu.gameserver.model.Wedding;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * This service manages all wedding-related logic within the game.<br>
 * It handles creating, managing, and updating {@link Wedding} data for players.<br>
 * It interacts with {@link WeddingDAO} to persist wedding information in the database.
 * @author synchro2
 */
public class WeddingService
{
	private final Map<Integer, Wedding> weddings = new HashMap<>();
	
	/**
	 * Provides the global instance of the {@link WeddingService}.<br>
	 * This method follows the singleton pattern.<br>
	 * Use this to access wedding-related features throughout the game server.
	 * @return The single shared instance of {@code WeddingService}.
	 */
	public static WeddingService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Registers a new wedding offer in the system.<br>
	 * This method checks if both players are eligible to marry.<br>
	 * It saves the wedding details for both partners.
	 * @param partner1 The first player involved in the wedding.
	 * @param partner2 The second player involved in the wedding.
	 * @param priest The player acting as the priest for the ceremony.
	 */
	public void registerOffer(Player partner1, Player partner2, Player priest)
	{
		if (!canRegister(partner1, partner2))
		{
			PacketSendUtility.sendMessage(priest, "One of players already married.");
			return;
		}
		
		weddings.put(partner1.getObjectId(), new Wedding(partner1, partner2, priest));
		weddings.put(partner2.getObjectId(), new Wedding(partner2, partner1, priest));
	}
	
	/**
	 * Checks if two players are eligible to register for a wedding.<br>
	 * It verifies that neither player is already married.<br>
	 * It also ensures neither player currently has an active wedding record.
	 * @param partner1 The first player involved in the registration.
	 * @param partner2 The second player involved in the registration.
	 * @return {@code true} if both players can register, otherwise {@code false}.
	 */
	private boolean canRegister(Player partner1, Player partner2)
	{
		return ((getWedding(partner1) == null) && (getWedding(partner2) == null) && !partner1.isMarried() && !partner2.isMarried());
	}
	
	/**
	 * Finalizes the wedding process for a specific player.<br>
	 * This method checks if both partners have accepted the proposal.<br>
	 * It then validates the requirements and executes the wedding ceremony.
	 * @param player The {@link Player} who is accepting the wedding offer.
	 */
	public void acceptWedding(Player player)
	{
		final Player partner = getPartner(player);
		final Wedding playersWedding = getWedding(player);
		final Wedding partnersWedding = getWedding(partner);
		
		playersWedding.setAccept();
		if (partnersWedding.isAccepted())
		{
			if (!checkConditions(player, partner))
			{
				cleanWedding(player, partner);
			}
			else
			{
				doWedding(player, partner);
				if (WeddingsConfig.WEDDINGS_GIFT_ENABLE)
				{
					giveGifts(player, partner);
				}
				
				if (WeddingsConfig.WEDDINGS_ANNOUNCE)
				{
					announceWedding(player, partner);
				}
			}
		}
	}
	
	/**
	 * Finalizes the wedding process for two players.<br>
	 * This method saves the marriage to the database.<br>
	 * It updates the {@code partnerId} for both {@link Player} objects.<br>
	 * It sends success messages to the couple and the priest.
	 * @param player The first player getting married.
	 * @param partner The second player getting married.
	 */
	private void doWedding(Player player, Player partner)
	{
		DAOManager.getDAO(WeddingDAO.class).storeWedding(player, partner);
		player.setPartnerId(partner.getObjectId());
		partner.setPartnerId(player.getObjectId());
		PacketSendUtility.sendMessage(player, "You had married on " + partner.getName() + ".");
		PacketSendUtility.sendMessage(partner, "You had married on " + player.getName() + ".");
		PacketSendUtility.sendMessage(getPriest(player), "You had married" + player.getName() + " and " + partner.getName() + ".");
		cleanWedding(player, partner);
	}
	
	/**
	 * Cancels an existing wedding between two players.<br>
	 * This method removes the wedding record from the database.<br>
	 * It also resets the partner IDs for both {@code Player} objects.<br>
	 * Finally, it sends a confirmation message to both players.
	 * @param player The first player involved in the wedding.
	 * @param partner The second player involved in the wedding.
	 */
	public void unDoWedding(Player player, Player partner)
	{
		DAOManager.getDAO(WeddingDAO.class).deleteWedding(player, partner);
		player.setPartnerId(0);
		partner.setPartnerId(0);
		PacketSendUtility.sendMessage(player, "Wedding canceled.");
		PacketSendUtility.sendMessage(partner, "Wedding canceled.");
	}
	
	/**
	 * Verifies if both players meet all requirements for a wedding.<br>
	 * This checks marriage status, required suits, visibility, membership, gender, race, and kinah costs.<br>
	 * If any condition fails, it sends an error message to the players and the priest.
	 * @param player The first player involved in the wedding.
	 * @param partner The second player involved in the wedding.
	 * @return {@code true} if all conditions are met, {@code false} otherwise.
	 */
	private boolean checkConditions(Player player, Player partner)
	{
		if (player.isMarried() || partner.isMarried())
		{
			PacketSendUtility.sendMessage(player, "One of players already married.");
			PacketSendUtility.sendMessage(partner, "One of players already married.");
			PacketSendUtility.sendMessage(getPriest(player), "One of players already married.");
		}
		
		if (WeddingsConfig.WEDDINGS_SUIT_ENABLE)
		{
			final String[] suits = WeddingsConfig.WEDDINGS_SUITS.split(",");
			boolean success1 = false;
			boolean success2 = false;
			try
			{
				for (String suit : suits)
				{
					final int suitId = Integer.parseInt(suit);
					if (!player.getEquipment().getEquippedItemsByItemId(suitId).isEmpty())
					{
						success1 = true;
					}
					
					if (!partner.getEquipment().getEquippedItemsByItemId(suitId).isEmpty())
					{
						success2 = true;
					}
				}
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (!success1 || !success2)
				{
					PacketSendUtility.sendMessage(player, "One of players not have required suit.");
					PacketSendUtility.sendMessage(partner, "One of players not equip required suit.");
					PacketSendUtility.sendMessage(getPriest(player), "One of players not equip required suit.");
					return false;
				}
			}
		}
		
		if (player.getKnownList().getObject(partner.getObjectId()) == null)
		{
			PacketSendUtility.sendMessage(player, "You should see spouse.");
			PacketSendUtility.sendMessage(partner, "You should see spouse.");
			PacketSendUtility.sendMessage(getPriest(player), "Players not see each other.");
			return false;
		}
		
		if (!player.havePermission(WeddingsConfig.WEDDINGS_MEMBERSHIP) || !partner.havePermission(WeddingsConfig.WEDDINGS_MEMBERSHIP))
		{
			PacketSendUtility.sendMessage(player, "One of players not have required membership.");
			PacketSendUtility.sendMessage(partner, "One of players not have required membership.");
			PacketSendUtility.sendMessage(getPriest(player), "One of players not have required membership.");
			return false;
		}
		
		if (!WeddingsConfig.WEDDINGS_SAME_SEX && player.getCommonData().getGender().equals(partner.getCommonData().getGender()))
		{
			PacketSendUtility.sendMessage(player, "Same-sex weddings prohibited.");
			PacketSendUtility.sendMessage(partner, "Same-sex weddings prohibited.");
			PacketSendUtility.sendMessage(getPriest(player), "Same-sex weddings prohibited.");
			return false;
		}
		
		if (!WeddingsConfig.WEDDINGS_DIFF_RACES && !player.getCommonData().getRace().equals(partner.getCommonData().getRace()))
		{
			PacketSendUtility.sendMessage(player, "Weddings between different races prohibited.");
			PacketSendUtility.sendMessage(partner, "Weddings between different races prohibited.");
			PacketSendUtility.sendMessage(getPriest(player), "Weddings between different races prohibited.");
			return false;
		}
		
		if (WeddingsConfig.WEDDINGS_KINAH != 0)
		{
			if (!player.getInventory().tryDecreaseKinah(WeddingsConfig.WEDDINGS_KINAH) || !partner.getInventory().tryDecreaseKinah(WeddingsConfig.WEDDINGS_KINAH))
			{
				PacketSendUtility.sendMessage(player, "One of players not have required kinah count.");
				PacketSendUtility.sendMessage(partner, "One of players not have required kinah count.");
				PacketSendUtility.sendMessage(getPriest(player), "One of players not have required kinah count.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method distributes wedding gifts to both players.<br>
	 * It uses {@link ItemService} to add the items defined in {@code WeddingsConfig}.<br>
	 * Each player receives exactly 1 gift.
	 * @param player The first player who will receive a gift.
	 * @param partner The second player who will receive a gift.
	 */
	private void giveGifts(Player player, Player partner)
	{
		ItemService.addItem(player, WeddingsConfig.WEDDINGS_GIFT, 1);
		ItemService.addItem(partner, WeddingsConfig.WEDDINGS_GIFT, 1);
	}
	
	/**
	 * Sends a marriage announcement to all players in the world.<br>
	 * It constructs a message using the names of both {@code player} and {@code partner}.<br>
	 * The message is sent as a bright yellow text via {@link PacketSendUtility}.
	 * @param player The first player getting married.
	 * @param partner The second player getting married.
	 */
	private void announceWedding(Player player, Player partner)
	{
		final String message = player.getName() + " and " + partner.getName() + " now married.";
		final Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext())
		{
			PacketSendUtility.sendBrightYellowMessage(iter.next(), message);
		}
	}
	
	/**
	 * Cancels the current wedding for the specified {@code Player}.<br>
	 * This method notifies both the partner and the priest of the cancellation.<br>
	 * It also calls {@code Player)} to remove the wedding data.
	 * @param player The {@code Player} who is initiating the cancellation.
	 */
	public void cancelWedding(Player player)
	{
		PacketSendUtility.sendMessage(player, "Wedding canceled.");
		PacketSendUtility.sendMessage(getPartner(player), "Player " + player.getName() + " declined from a wedding.");
		PacketSendUtility.sendMessage(getPriest(player), "Player " + player.getName() + " declined from a wedding.");
		cleanWedding(player, getPartner(player));
	}
	
	/**
	 * Removes the wedding record from the internal map.<br>
	 * This method cleans up data for both players involved.<br>
	 * It uses the {@code objectId} of each {@link Player}.
	 * @param player The first player in the wedding.
	 * @param partner The second player in the wedding.
	 */
	private void cleanWedding(Player player, Player partner)
	{
		weddings.remove(player.getObjectId());
		weddings.remove(partner.getObjectId());
	}
	
	/**
	 * Retrieves the {@link Wedding} associated with a specific {@link Player}.<br>
	 * It looks up the wedding using the unique object ID of the player.<br>
	 * Returns {@code null} if no wedding is found.
	 * @param player The {@link Player} to check for an active wedding.
	 * @return The {@link Wedding} object or {@code null}.
	 */
	public Wedding getWedding(Player player)
	{
		return weddings.get(player.getObjectId());
	}
	
	/**
	 * Finds the partner of a specific {@link Player}.<br>
	 * It retrieves the {@code Wedding} associated with the player's object ID.
	 * @param player The {@code Player} whose partner needs to be found.
	 * @return The {@code Player} who is married to the provided player, or {@code null} if no wedding exists.
	 */
	private Player getPartner(Player player)
	{
		final Wedding wedding = weddings.get(player.getObjectId());
		return wedding.getPartner();
	}
	
	/**
	 * Retrieves the priest associated with a specific {@link Player}.<br>
	 * It looks up the {@code Wedding} object for the given player.<br>
	 * Then it returns the priest from that wedding.
	 * @param player The {@code Player} whose wedding information is needed.
	 * @return The {@code Player} object representing the priest, or {@code null} if no wedding exists.
	 */
	private Player getPriest(Player player)
	{
		final Wedding wedding = weddings.get(player.getObjectId());
		return wedding.getPriest();
	}
	
	private static class SingletonHolder
	{
		protected static final WeddingService instance = new WeddingService();
	}
}
