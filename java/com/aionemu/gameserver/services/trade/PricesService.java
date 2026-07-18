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
package com.aionemu.gameserver.services.trade;

import com.aionemu.gameserver.configs.main.PricesConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.siege.Influence;

/**
 * This service provides the logic to calculate various in-game prices for players.<br>
 * It handles costs for items, services like {@code Godstone} sockets and teleporters, and other fees.<br>
 * It also accounts for specific configurations such as {@link PricesConfig} and faction influences.
 * @author Sarynth modified by wakizashi
 */
public class PricesService
{
	/**
	 * Retrieves the global price based on the player's race.<br>
	 * This method adjusts the base price using {@link Influence} values if siege is enabled.<br>
	 * It returns the default price from {@code PricesConfig} if no influence applies.
	 * @param playerRace The {@code Race} of the player.
	 * @return The calculated global price as an {@code int}.
	 */
	public static int getGlobalPrices(Race playerRace)
	{
		final int defaultPrices = PricesConfig.DEFAULT_PRICES;
		
		if (!SiegeConfig.SIEGE_ENABLED)
		{
			return defaultPrices;
		}
		
		float influenceValue = 0;
		switch (playerRace)
		{
			case ASMODIANS:
				influenceValue = Influence.getInstance().getGlobalAsmodiansInfluence();
				break;
			case ELYOS:
				influenceValue = Influence.getInstance().getGlobalElyosInfluence();
				break;
			default:
				influenceValue = 0.5f;
				break;
		}
		
		if (influenceValue == 0.5f)
		{
			return defaultPrices;
		}
		else if (influenceValue > 0.5f)
		{
			final float diff = influenceValue - 0.5f;
			return Math.round(defaultPrices - ((diff / 2) * 100));
		}
		else
		{
			final float diff = 0.5f - influenceValue;
			return Math.round(defaultPrices + ((diff / 2) * 100));
		}
	}
	
	/**
	 * Retrieves the default price modifier from the configuration.<br>
	 * This value is used to adjust global prices across the game.<br>
	 * It returns the {@code DEFAULT_MODIFIER} defined in {@link PricesConfig}.
	 * @return The current global price modifier as an {@code int}.
	 */
	public static int getGlobalPricesModifier()
	{
		return PricesConfig.DEFAULT_MODIFIER;
	}
	
	/**
	 * Calculates the tax rate for a specific player race.<br>
	 * This value depends on whether {@code SIEGE_ENABLED} is active.<br>
	 * It also takes into account the current influence levels of the factions.
	 * @param playerRace The {@code Race} of the player to check.
	 * @return The calculated tax as an {@code int}.
	 */
	public static int getTaxes(Race playerRace)
	{
		final int defaultTax = PricesConfig.DEFAULT_TAXES;
		
		if (!SiegeConfig.SIEGE_ENABLED)
		{
			return defaultTax;
		}
		
		float influenceValue = 0;
		switch (playerRace)
		{
			case ASMODIANS:
				influenceValue = Influence.getInstance().getGlobalAsmodiansInfluence();
				break;
			case ELYOS:
				influenceValue = Influence.getInstance().getGlobalElyosInfluence();
				break;
			default:
				influenceValue = 0.5f;
				break;
		}
		
		if (influenceValue >= 0.5f)
		{
			return defaultTax;
		}
		
		final float diff = 0.5f - influenceValue;
		return Math.round(defaultTax + ((diff / 4) * 100));
	}
	
	/**
	 * Retrieves the global modifier for vendor purchase prices.<br>
	 * This value is fetched from {@link PricesConfig}.
	 * @return The integer value of the {@code VENDOR_BUY_MODIFIER}.
	 */
	public static int getVendorBuyModifier()
	{
		return PricesConfig.VENDOR_BUY_MODIFIER;
	}
	
	/**
	 * Calculates the final modifier for selling items to a vendor.<br>
	 * This value depends on the player's {@link Race}.<br>
	 * It combines global prices, specific modifiers, and taxes.
	 * @param playerRace The race of the player performing the sale.
	 * @return The calculated sell modifier as an {@code int}.
	 */
	public static int getVendorSellModifier(Race playerRace)
	{
		return (int) (((int) (((int) ((PricesConfig.VENDOR_SELL_MODIFIER * getGlobalPrices(playerRace)) / 100F) * getGlobalPricesModifier()) / 100F) * getTaxes(playerRace)) / 100F);
	}
	
	/**
	 * Calculates the final price for a specific service.<br>
	 * This method applies global prices, modifiers, and taxes based on the player's race.<br>
	 * It rounds down the result at each step to match the client calculation.
	 * @param basePrice The initial cost of the service before any modifiers.
	 * @param playerRace The {@link Race} of the player requesting the service.
	 * @return The final calculated price as a {@code long}.
	 */
	public static long getPriceForService(long basePrice, Race playerRace)
	{
		// Multiply by Prices, Modifier, and Taxes in order, rounding down each time to match the client's calculation.
		// System.out.println("GlobalPrice: " + getGlobalPrices(playerRace) / 100D);
		// System.out.println("GlobalPriceModifier: " + getGlobalPricesModifier() / 100D);
		// System.out.println("Tax: " + getTaxes(playerRace) / 100D);
		return (long) (((long) (((long) ((basePrice * getGlobalPrices(playerRace)) / 100D) * getGlobalPricesModifier()) / 100D) * getTaxes(playerRace)) / 100D);
	}
	
	/**
	 * Calculates the final cost in Kinah for a purchase.<br>
	 * This method applies various modifiers based on the player's race.<br>
	 * It accounts for vendor bonuses, global prices, and taxes.
	 * @param requiredKinah The base price of the item.
	 * @param playerRace The {@link Race} of the player making the purchase.
	 * @return The total amount of Kinah needed to complete the buy.
	 */
	public static long getKinahForBuy(long requiredKinah, Race playerRace)
	{
		// Requires double precision for 2mil+ kinah items
		return (long) (((long) (((long) (((long) ((requiredKinah * getVendorBuyModifier()) / 100.0D) * getGlobalPrices(playerRace)) / 100.0D) * getGlobalPricesModifier()) / 100.0D) * getTaxes(playerRace)) / 100.0D);
	}
	
	/**
	 * Calculates the final amount of Kinah a player receives when selling an item.<br>
	 * This method applies the vendor sell modifier based on the player's race.
	 * @param kinahReward The base reward amount in Kinah.
	 * @param playerRace The {@link Race} of the player performing the sale.
	 * @return The final calculated Kinah amount as a {@code long}.
	 */
	public static long getKinahForSell(long kinahReward, Race playerRace)
	{
		return (long) ((kinahReward * getVendorSellModifier(playerRace)) / 100D);
	}
}
