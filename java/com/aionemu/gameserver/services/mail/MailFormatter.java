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
package com.aionemu.gameserver.services.mail;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.templates.mail.MailPart;
import com.aionemu.gameserver.model.templates.mail.MailTemplate;

/**
 * This class provides utility methods to format mail content for players.<br>
 * It processes {@link MailTemplate} objects and their associated {@link MailPart} components.<br>
 * Use this class to convert raw data into human-readable strings for the in-game mail system.
 * @author Rolandas
 */
public final class MailFormatter
{
	/**
	 * Sends a special mail to a player for cash items.<br>
	 * This method uses the {@code $$CASH_ITEM_MAIL} template.<br>
	 * It formats the item details and sends it via {@link SystemMailService}.
	 * @param recipientName The name of the player receiving the mail.
	 * @param itemObjectId The unique ID of the item being sent.
	 * @param itemCount The total number of items to include in the mail.
	 */
	public static void sendBlackCloudMail(String recipientName, int itemObjectId, int itemCount)
	{
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$CASH_ITEM_MAIL", "", Race.PC_ALL);
		
		final MailPart formatter = new MailPart()
		{
			@Override
			public String getParamValue(String name)
			{
				if ("itemid".equals(name))
				{
					return Integer.toString(itemObjectId);
				}
				else if ("count".equals(name))
				{
					return Integer.toString(itemCount);
				}
				else if ("unk1".equals(name))
				{
					return "0";
				}
				else if ("purchasedate".equals(name))
				{
					return Long.toString(System.currentTimeMillis() / 1000);
				}
				
				return "";
			}
		};
		
		final String title = template.getFormattedTitle(formatter);
		final String body = template.getFormattedMessage(formatter);
		
		SystemMailService.getInstance().sendMail("$$CASH_ITEM_MAIL", recipientName, title, body, itemObjectId, itemCount, 0, LetterType.BLACKCLOUD);
	}
	
	/**
	 * Sends a maintenance warning mail to the owner of a house.<br>
	 * The mail content depends on the number of warnings already issued.<br>
	 * It uses {@link House} data to identify the recipient and location details.
	 * @param ownedHouse The {@code House} object that requires maintenance.
	 * @param warnCount The current count of warnings sent to the owner.
	 * @param impoundTime The timestamp when the house will be impounded.
	 */
	public static void sendHouseMaintenanceMail(House ownedHouse, int warnCount, long impoundTime)
	{
		String templateName = "";
		switch (warnCount)
		{
			case 1:
				templateName = "$$HS_OVERDUE_FIRST";
				break;
			case 2:
				templateName = "$$HS_OVERDUE_SECOND";
				break;
			case 3:
				templateName = "$$HS_OVERDUE_FINAL";
				break;
			default:
				return;
		}
		
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate(templateName, "", ownedHouse.getPlayerRace());
		
		final MailPart formatter = new MailPart()
		{
			@Override
			public String getParamValue(String name)
			{
				if ("address".equals(name))
				{
					return Integer.toString(ownedHouse.getAddress().getId());
				}
				else if ("datetime".equals(name))
				{
					return Long.toString(impoundTime / 1000);
				}
				
				return "";
			}
		};
		
		final String title = template.getFormattedTitle(null);
		final String message = template.getFormattedMessage(formatter);
		
		SystemMailService.getInstance().sendMail(templateName, ownedHouse.getButler().getMasterName(), title, message, 0, 0, 0, LetterType.NORMAL);
	}
	
	/**
	 * Sends a notification mail regarding a house auction result.<br>
	 * This method uses the {@code $$HS_AUCTION_MAIL} template to format the message.<br>
	 * It handles null checks for all required input objects.
	 * @param ownedHouse The {@code House} object involved in the auction.
	 * @param playerData The {@code PlayerCommonData} of the player receiving the mail.
	 * @param result The {@code AuctionResult} containing the outcome details.
	 * @param time The timestamp used to calculate the date and time for the mail.
	 * @param returnKinah The amount of kinah to be returned in the mail.
	 */
	public static void sendHouseAuctionMail(House ownedHouse, PlayerCommonData playerData, AuctionResult result, long time, long returnKinah)
	{
		if ((ownedHouse == null) || (playerData == null) || (result == null))
		{
			return;
		}
		
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$HS_AUCTION_MAIL", "", playerData.getRace());
		
		final MailPart formatter = new MailPart()
		{
			@Override
			public String getParamValue(String name)
			{
				if ("address".equals(name))
				{
					return Integer.toString(ownedHouse.getAddress().getId());
				}
				else if ("datetime".equals(name))
				{
					return Long.toString(time / 1000);
				}
				else if ("resultid".equals(name))
				{
					return Integer.toString(result.getId());
				}
				else if ("raceid".equals(name))
				{
					return Integer.toString(playerData.getRace().getRaceId());
				}
				
				return "";
			}
		};
		
		final String title = template.getFormattedTitle(formatter);
		final String message = template.getFormattedMessage(formatter);
		
		SystemMailService.getInstance().sendMail("$$HS_AUCTION_MAIL", playerData.getName(), title, message, 0, 0, returnKinah, LetterType.NORMAL);
	}
	
	/**
	 * Sends a reward mail to a player after an Abyss siege.<br>
	 * This method formats the mail using the {@code $$ABYSS_REWARD_MAIL} template.<br>
	 * It includes details about the location, rank, and results.
	 * @param siegeLocation The {@link SiegeLocation} where the event occurred.
	 * @param playerData The {@link PlayerCommonData} of the recipient player.
	 * @param level The {@link AbyssSiegeLevel} achieved by the player.
	 * @param result The {@link SiegeResult} of the match.
	 * @param time The timestamp of the event in milliseconds.
	 * @param attachedItemObjId The ID of the item to attach to the mail.
	 * @param attachedItemCount The quantity of the attached item.
	 * @param attachedKinahCount The amount of Kinah to attach to the mail.
	 */
	public static void sendAbyssRewardMail(SiegeLocation siegeLocation, PlayerCommonData playerData, AbyssSiegeLevel level, SiegeResult result, long time, int attachedItemObjId, long attachedItemCount, long attachedKinahCount)
	{
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$ABYSS_REWARD_MAIL", "", playerData.getRace());
		
		final MailPart formatter = new MailPart()
		{
			@Override
			public String getParamValue(String name)
			{
				if ("siegelocid".equals(name))
				{
					return Integer.toString(siegeLocation.getTemplate().getId());
				}
				else if ("datetime".equals(name))
				{
					return Long.toString(time / 1000);
				}
				else if ("rankid".equals(name))
				{
					return Integer.toString(level.getId());
				}
				else if ("raceid".equals(name))
				{
					return Integer.toString(playerData.getRace().getRaceId());
				}
				else if ("resultid".equals(name))
				{
					return Integer.toString(result.getId());
				}
				
				return "";
			}
		};
		
		final String title = template.getFormattedTitle(formatter);
		final String message = template.getFormattedMessage(formatter);
		
		SystemMailService.getInstance().sendMail("$$ABYSS_REWARD_MAIL", playerData.getName(), title, message, attachedItemObjId, attachedItemCount, attachedKinahCount, LetterType.NORMAL);
	}
}
