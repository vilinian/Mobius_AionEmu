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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.item.Disposition;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.HousingBidService;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.player.PlayerMailboxState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

/**
 * This service handles all mail-related operations for players in the game.<br>
 * It manages sending, receiving, and viewing messages through the {@link Mailbox} system. The class interacts with {@link MailDAO} to persist mail data and {@link Player} objects to deliver content.
 * @author kosyachok
 */
public class MailService
{
	private static final Logger log = LoggerFactory.getLogger("MAIL_LOG");
	protected Queue<Player> newPlayers;
	
	/**
	 * Provides the global instance of the {@link MailService}.<br>
	 * This method follows the singleton pattern to ensure only one service exists.
	 * @return The active {@code MailService} instance.
	 */
	public static MailService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	/**
	 * Private constructor to prevent direct instantiation.<br>
	 * This class uses the singleton pattern via {@code getInstance}.
	 */
	private MailService()
	{
		newPlayers = new ConcurrentLinkedQueue<>();
	}
	
	/**
	 * Sends a mail letter from one player to another.<br>
	 * This method validates the recipient, checks for sufficient funds and mailbox space, and handles item attachments.<br>
	 * It calculates commissions based on item quality and updates both online and offline recipients.
	 * @param sender The {@code Player} who is sending the mail.
	 * @param recipientName The name of the player receiving the mail.
	 * @param title The subject line of the letter.
	 * @param message The main body text of the letter.
	 * @param attachedItemObjId The unique ID of the item to attach, or 0 if none.
	 * @param itemCount The quantity of the attached item.
	 * @param kinahCount The amount of {@code kinah} to include in the mail.
	 * @param letterType The {@code LetterType} category for this mail.
	 */
	public void sendMail(Player sender, String recipientName, String title, String message, int attachedItemObjId, long itemCount, long kinahCount, LetterType letterType)
	{
		if ((letterType == LetterType.BLACKCLOUD) || (recipientName.length() > 16))
		{
			return;
		}
		
		if (title.length() > 20)
		{
			title = title.substring(0, 20);
		}
		
		if (message.length() > 1000)
		{
			message = message.substring(0, 1000);
		}
		
		final PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipientName);
		
		if (recipientCommonData == null)
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.NO_SUCH_CHARACTER_NAME));
			return;
		}
		
		if ((recipientCommonData.getRace() != sender.getRace()) && (sender.getAccessLevel() < AdminConfig.GM_LEVEL))
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.MAIL_IS_ONE_RACE_ONLY));
			return;
		}
		
		final Player recipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		if (recipient != null)
		{
			if (!recipient.getMailbox().haveFreeSlots())
			{
				PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.RECIPIENT_MAILBOX_FULL));
				return;
			}
		}
		else if (recipientCommonData.getMailboxLetters() > 99)
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.RECIPIENT_MAILBOX_FULL));
			return;
		}
		
		if (!validateMailSendPrice(sender, kinahCount, attachedItemObjId, itemCount))
		{
			return;
		}
		
		Item attachedItem = null;
		long finalAttachedKinahCount = 0;
		
		int kinahMailCommission = 0;
		int itemMailCommission = 0;
		
		final Storage senderInventory = sender.getInventory();
		
		if ((attachedItemObjId != 0) && (itemCount > 0))
		{
			final Item senderItem = senderInventory.getItemByObjId(attachedItemObjId);
			
			if ((senderItem == null) || !AdminService.getInstance().canOperate(sender, null, senderItem, "mail"))
			{
				return;
			}
			
			float qualityPriceRate;
			switch (senderItem.getItemTemplate().getItemQuality())
			{
				case JUNK:
				case COMMON:
					qualityPriceRate = 0.02f;
					break;
				
				case RARE:
					qualityPriceRate = 0.03f;
					break;
				
				case LEGEND:
				case UNIQUE:
					qualityPriceRate = 0.04f;
					break;
				
				case MYTHIC:
				case EPIC:
					qualityPriceRate = 0.05f;
					break;
				
				case ANCIENT:
				case RELIC:
				case FINALITY:
					qualityPriceRate = 0.06f;
					break;
				
				default:
					qualityPriceRate = 0.02f;
					break;
			}
			
			if (senderItem.getItemCount() < itemCount)
			{
				return; // Client hack
			}
			
			// Check Mailing untradables with Cash items (Special courier passes)
			if (!senderItem.isTradeable(sender))
			{
				final Disposition dispo = senderItem.getItemTemplate().getDisposition();
				if ((dispo == null) || (dispo.getId() == 0) || (dispo.getCount() == 0)) // can not be traded, hack
				{
					return;
				}
				
				if (!senderItem.isPacked())
				{
					if (senderInventory.getItemCountByItemId(dispo.getId()) >= dispo.getCount())
					{
						senderInventory.decreaseByItemId(dispo.getId(), dispo.getCount());
					}
					else
					{
						PacketSendUtility.sendPacket(sender, new SM_SYSTEM_MESSAGE(1401514, new DescriptionId(dispo.getId())));
						return;
					}
				}
				else
				{
					if (senderItem.getPackCount() > senderItem.getItemTemplate().getPackCount())
					{
						return;
					}
				}
			}
			
			// reuse item in case of full decrease of count
			if (senderItem.getItemCount() == itemCount)
			{
				senderInventory.remove(senderItem);
				PacketSendUtility.sendPacket(sender, new SM_DELETE_ITEM(attachedItemObjId));
				attachedItem = senderItem;
			}
			else if (senderItem.getItemCount() > itemCount)
			{
				attachedItem = ItemFactory.newItem(senderItem.getItemTemplate().getTemplateId(), itemCount);
				senderInventory.decreaseItemCount(senderItem, itemCount);
			}
			
			if (attachedItem == null)
			{
				return;
			}
			
			attachedItem.setEquipped(false);
			attachedItem.setEquipmentSlot(0);
			attachedItem.setItemLocation(StorageType.MAILBOX.getId());
			itemMailCommission = Math.round((attachedItem.getItemTemplate().getPrice() * attachedItem.getItemCount()) * qualityPriceRate);
		}
		
		/**
		 * Calculate kinah
		 */
		if (kinahCount > 0)
		{
			if ((senderInventory.getKinah() - kinahCount) >= 0)
			{
				finalAttachedKinahCount = kinahCount;
				kinahMailCommission = Math.round(kinahCount * 0.01f);
			}
		}
		
		final long finalMailKinah = 10 + kinahMailCommission + itemMailCommission + finalAttachedKinahCount;
		
		if (senderInventory.getKinah() > finalMailKinah)
		{
			senderInventory.decreaseKinah(finalMailKinah);
		}
		else
		{
			AuditLogger.info(sender, "Mail kinah exploit.");
			return;
		}
		
		final Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
		final Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem, finalAttachedKinahCount, title, message, sender.getName(), time, true, letterType);
		
		// first save attached item for FK consistency
		if (attachedItem != null)
		{
			if (!DAOManager.getDAO(InventoryDAO.class).store(attachedItem, recipientCommonData.getPlayerObjId()))
			{
				DAOManager.getDAO(ItemStoneListDAO.class).save(recipientCommonData.getPlayer());
				return;
			}
		}
		
		// save letter
		if (!DAOManager.getDAO(MailDAO.class).storeLetter(time, newLetter))
		{
			return;
		}
		
		/**
		 * Send mail update packets
		 */
		if (recipient != null)
		{
			final Mailbox recipientMailbox = recipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);
			
			// packets for sender
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.MAIL_SEND_SECCESS));
			
			// packets for recipient
			PacketSendUtility.sendPacket(recipient, new SM_MAIL_SERVICE(recipientMailbox));
			recipientMailbox.isMailListUpdateRequired = true;
			
			// if recipient have opened mail list we should update it
			if (recipientMailbox.mailBoxState != 0)
			{
				final boolean isPostman = (recipientMailbox.mailBoxState & PlayerMailboxState.EXPRESS) == PlayerMailboxState.EXPRESS;
				PacketSendUtility.sendPacket(recipient, new SM_MAIL_SERVICE(recipient, recipientMailbox.getLetters(), isPostman));
			}
			
			if (letterType == LetterType.EXPRESS)
			{
				PacketSendUtility.sendPacket(recipient, SM_SYSTEM_MESSAGE.STR_POSTMAN_NOTIFY);
			}
		}
		
		if (attachedItem != null)
		{
			if (LoggingConfig.LOG_MAIL)
			{
				log.info("[MAILSERVICE] [Player: " + sender.getName() + "] send [Item: " + attachedItem.getItemId() + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "] [Item Name: " + attachedItem.getItemName() + "]" : "]") + " [Count: " + attachedItem.getItemCount() + "] to [Reciever: " + recipientName + "]");
			}
		}
		
		/**
		 * Update loaded common data and db if player is offline
		 */
		if (!recipientCommonData.isOnline())
		{
			PacketSendUtility.sendPacket(sender, new SM_MAIL_SERVICE(MailMessage.MAIL_SEND_SECCESS));
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			DAOManager.getDAO(MailDAO.class).updateOfflineMailCounter(recipientCommonData);
		}
	}
	
	/**
	 * Reads a specific mail from the player's mailbox.<br>
	 * This method sends the mail content to the {@link Player}.<br>
	 * It also marks the letter as read in the system.
	 * @param player The {@link Player} who is reading the mail.
	 * @param letterId The unique identifier of the mail to be read.
	 */
	public void readMail(Player player, int letterId)
	{
		final Letter letter = player.getMailbox().getLetterFromMailbox(letterId);
		if (letter == null)
		{
			log.warn("Cannot read mail " + player.getObjectId() + " " + letterId);
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player, letter, letter.getTimeStamp().getTime()));
		letter.setReadLetter();
	}
	
	/**
	 * Retrieves and processes attachments from a specific mail letter.<br>
	 * This method handles items or kinah based on the provided {@code attachmentType}.<br>
	 * It updates the player inventory and removes the attachment from the letter.
	 * @param player The {@link Player} who is receiving the mail.
	 * @param letterId The unique identifier of the mail letter.
	 * @param attachmentType The type of attachment to retrieve, such as an item or kinah.
	 */
	public void getAttachments(Player player, int letterId, int attachmentType)
	{
		final Letter letter = player.getMailbox().getLetterFromMailbox(letterId);
		
		if (letter == null)
		{
			return;
		}
		
		switch (attachmentType)
		{
			case 0:
			{
				final Item attachedItem = letter.getAttachedItem();
				if (attachedItem == null)
				{
					return;
				}
				
				if (player.getInventory().isFull())
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
					return;
				}
				
				if (attachedItem.isPacked())
				{
					attachedItem.setPacked(false);
				}
				
				player.getInventory().add(attachedItem);
				if (!DAOManager.getDAO(InventoryDAO.class).store(attachedItem, player.getObjectId()))
				{
					return;
				}
				
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(letterId, attachmentType));
				letter.removeAttachedItem();
				break;
			}
			case 1:
			{
				player.getInventory().increaseKinah(letter.getAttachedKinah());
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(letterId, attachmentType));
				letter.removeAttachedKinah();
				break;
			}
		}
	}
	
	/**
	 * Deletes specific mail items from a player's mailbox.<br>
	 * This method removes the letters from the {@link Player} object and the database.<br>
	 * It also sends a synchronization packet to the client.
	 * @param player The {@code Player} who owns the mailbox.
	 * @param mailObjId An array of {@code int} IDs representing the mail objects to delete.
	 */
	public void deleteMail(Player player, int[] mailObjId)
	{
		final Mailbox mailbox = player.getMailbox();
		
		for (int letterId : mailObjId)
		{
			mailbox.removeLetter(letterId);
			DAOManager.getDAO(MailDAO.class).deleteLetter(letterId);
		}
		
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(mailObjId));
	}
	
	/**
	 * Checks if the {@code sender} has enough currency to pay for sending a mail.<br>
	 * It calculates the total cost based on kinah amount and item quality.<br>
	 * The method returns {@code true} if the balance is sufficient.
	 * @param sender The {@link Player} who is trying to send the mail.
	 * @param kinahCount The amount of kinah being sent in the mail.
	 * @param attachedItemObjId The unique ID of the item attached to the mail.
	 * @param itemCount The quantity of the attached item.
	 * @return {@code true} if the sender can afford the fee, otherwise {@code false}.
	 */
	private boolean validateMailSendPrice(Player sender, long kinahCount, int attachedItemObjId, long itemCount)
	{
		int itemMailCommission = 0;
		final int kinahMailCommission = Math.round(kinahCount * 0.01f);
		if (attachedItemObjId != 0)
		{
			final Item senderItem = sender.getInventory().getItemByObjId(attachedItemObjId);
			if ((senderItem == null) || (senderItem.getItemTemplate() == null))
			{
				return false;
			}
			
			float qualityPriceRate;
			switch (senderItem.getItemTemplate().getItemQuality())
			{
				case JUNK:
				case COMMON:
					qualityPriceRate = 0.02f;
					break;
				
				case RARE:
					qualityPriceRate = 0.03f;
					break;
				
				case LEGEND:
				case UNIQUE:
					qualityPriceRate = 0.04f;
					break;
				
				case MYTHIC:
				case EPIC:
					qualityPriceRate = 0.05f;
					break;
				
				case ANCIENT:
				case RELIC:
				case FINALITY:
					qualityPriceRate = 0.06f;
					break;
				
				default:
					qualityPriceRate = 0.02f;
					break;
			}
			
			itemMailCommission = Math.round((senderItem.getItemTemplate().getPrice() * itemCount) * qualityPriceRate);
		}
		
		final int finalMailPrice = 10 + itemMailCommission + kinahMailCommission;
		
		return sender.getInventory().getKinah() >= finalMailPrice;
		
	}
	
	/**
	 * This method is called when a {@link Player} logs into the instance.<br>
	 * It handles any initialization logic required for the player's session.
	 * @param player The {@code Player} object that just logged in.
	 */
	public void onPlayerLogin(Player player)
	{
		ThreadPoolManager.getInstance().schedule(new MailLoadTask(player), 5000);
	}
	
	/**
	 * Updates the mail information for a specific player.<br>
	 * This method sends the latest mailbox data to the client.<br>
	 * It ensures the {@link Player} sees their current letters.
	 * @param player The {@code Player} whose mail needs to be refreshed.
	 */
	public void refreshMail(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player.getMailbox()));
		PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player, player.getMailbox().getLetters(), false));
	}
	
	/**
	 * Task to load all player mail items
	 * @author ATracer
	 */
	private class MailLoadTask implements Runnable
	{
		private final Player player;
		
		private MailLoadTask(Player player)
		{
			this.player = player;
		}
		
		@Override
		public void run()
		{
			player.setMailbox(DAOManager.getDAO(MailDAO.class).loadPlayerMailbox(player));
			PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player.getMailbox()));
			HousingBidService.getInstance().onPlayerLogin(player);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final MailService instance = new MailService();
	}
}
