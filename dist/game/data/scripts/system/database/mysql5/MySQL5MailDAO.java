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
package system.database.mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.storage.StorageType;

/**
 * Provides database access methods for managing player mail and letters using {@code MySQL5}.<br>
 * This class extends {@link MailDAO} to handle specific data persistence operations.
 * @author kosyachok
 * @author Antraxx
 * @author FrozenKiller
 */
public class MySQL5MailDAO extends MailDAO
{
	private static final Logger log = LoggerFactory.getLogger(MySQL5MailDAO.class);
	
	/**
	 * Loads the mail records for a specific player from the database.<br>
	 * This method retrieves up to 100 letters and populates a {@link Mailbox} object.<br>
	 * It also handles loading any attached items associated with those letters.
	 * @param player The {@link Player} whose mailbox needs to be loaded.
	 * @return A populated {@link Mailbox} object for the given player.
	 */
	@Override
	public Mailbox loadPlayerMailbox(Player player)
	{
		final Mailbox mailbox = new Mailbox(player);
		final int playerId = player.getObjectId();
		DB.select("SELECT * FROM mail WHERE mail_recipient_id = ? ORDER BY recieved_time DESC LIMIT 100", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				final List<Item> mailboxItems = loadMailboxItems(playerId);
				while (rset.next())
				{
					final int mailUniqueId = rset.getInt("mail_unique_id");
					final int recipientId = rset.getInt("mail_recipient_id");
					final String senderName = rset.getString("sender_name");
					final String mailTitle = rset.getString("mail_title");
					final String mailMessage = rset.getString("mail_message");
					final int unread = rset.getInt("unread");
					final int attachedItemId = rset.getInt("attached_item_id");
					final long attachedKinahCount = rset.getLong("attached_kinah_count");
					final LetterType letterType = LetterType.getLetterTypeById(rset.getInt("express"));
					final Timestamp recievedTime = rset.getTimestamp("recieved_time");
					Item attachedItem = null;
					if (attachedItemId != 0)
					{
						for (Item item : mailboxItems)
						{
							if (item.getObjectId() == attachedItemId)
							{
								if (item.getItemTemplate().isArmor() || item.getItemTemplate().isWeapon())
								{
									DAOManager.getDAO(ItemStoneListDAO.class).load(Collections.singletonList(item));
								}
								
								attachedItem = item;
							}
						}
					}
					
					final Letter letter = new Letter(mailUniqueId, recipientId, attachedItem, attachedKinahCount, mailTitle, mailMessage, senderName, recievedTime, unread == 1, letterType);
					letter.setPersistState(PersistentState.UPDATED);
					mailbox.putLetterToMailbox(letter);
				}
			}
		});
		
		return mailbox;
	}
	
	/**
	 * Retrieves the total number of mail messages for a specific player.<br>
	 * This method queries the database to count both read and unread letters.<br>
	 * It returns a maximum of 100 results per query.
	 * @param playerId The unique identifier of the player.
	 * @return The total count of mails found for the given {@code playerId}.
	 */
	@Override
	public int mailCount(int playerId)
	{
		int allMailsCount = 0;
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM mail WHERE mail_recipient_id = ? ORDER BY recieved_time DESC LIMIT 100");
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				if ((rset.getInt("unread") == 1) || (rset.getInt("unread") == 0))
				{
					allMailsCount++;
				}
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not read mail for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return allMailsCount;
	}
	
	/**
	 * Calculates the number of unread messages for a specific player.<br>
	 * This method queries the database to count entries where the status is {@code 1}.
	 * @param playerId The unique identifier of the player.
	 * @return The total count of unreaded mails.
	 */
	@Override
	public int unreadedMails(int playerId)
	{
		int unreadedMails = 0;
		Connection con = null;
		try
		{
			con = DatabaseFactory.getConnection();
			final PreparedStatement stmt = con.prepareStatement("SELECT * FROM mail WHERE mail_recipient_id = ? ORDER BY recieved_time DESC LIMIT 100");
			stmt.setInt(1, playerId);
			final ResultSet rset = stmt.executeQuery();
			while (rset.next())
			{
				if (rset.getInt("unread") == 1)
				{
					unreadedMails++;
				}
			}
			
			rset.close();
			stmt.close();
		}
		catch (Exception e)
		{
			log.error("Could not read mail for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally
		{
			DatabaseFactory.close(con);
		}
		
		return unreadedMails;
	}
	
	/**
	 * Retrieves all items from the database for a specific player's mailbox.<br>
	 * It filters by the unique location ID of 127.
	 * @param playerId The unique identifier of the player.
	 * @return A {@code List} containing all {@link Item} objects found in the mailbox.
	 */
	private List<Item> loadMailboxItems(int playerId)
	{
		final List<Item> mailboxItems = new ArrayList<>();
		
		DB.select("SELECT * FROM inventory WHERE `item_owner` = ? AND `item_location` = 127", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
			}
			
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next())
				{
					final int itemUniqueId = rset.getInt("item_unique_id");
					final int itemId = rset.getInt("item_id");
					final long itemCount = rset.getLong("item_count");
					final int itemColor = rset.getInt("item_color");
					final int colorExpireTime = rset.getInt("color_expires");
					final String itemCreator = rset.getString("item_creator");
					final int expireTime = rset.getInt("expire_time");
					final int activationCount = rset.getInt("activation_count");
					final int isEquiped = rset.getInt("is_equiped");
					final int isSoulBound = rset.getInt("is_soul_bound");
					final int slot = rset.getInt("slot");
					final int enchant = rset.getInt("enchant");
					final int itemSkin = rset.getInt("item_skin");
					final int fusionedItem = rset.getInt("fusioned_item");
					final int optionalSocket = rset.getInt("optional_socket");
					final int optionalFusionSocket = rset.getInt("optional_fusion_socket");
					final int charge = rset.getInt("charge");
					final int randomBonus = rset.getInt("rnd_bonus");
					final int rndCount = rset.getInt("rnd_count");
					final int packCount = rset.getInt("pack_count");
					final int max_authorize = rset.getInt("authorize");
					final int isPacked = rset.getInt("is_packed");
					final int isAmplified = rset.getInt("is_amplified");
					final int buffSkill = rset.getInt("buff_skill");
					final int reductionLevel = rset.getInt("reduction_level");
					final int isLunaReskin = rset.getInt("luna_reskin");
					final boolean isEnhance = rset.getBoolean("isEnhance");
					final int enhanceSkillId = rset.getInt("enhanceSkillId");
					final int enhanceSkillEnchant = rset.getInt("enhanceSkillEnchant");
					final int unSeal = rset.getInt("is_seal");
					final int skinSkill = rset.getInt("skin_skill");
					final int grindSocket = rset.getInt("grind_socket");
					final int grindColor = rset.getInt("grind_color");
					final boolean contaminated = rset.getBoolean("contaminated");
					final Item item = new Item(itemUniqueId, itemId, itemCount, itemColor, colorExpireTime, itemCreator, expireTime, activationCount, isEquiped == 1, isSoulBound == 1, slot, StorageType.MAILBOX.getId(), enchant, itemSkin, fusionedItem, optionalSocket, optionalFusionSocket, charge, randomBonus, rndCount, packCount, max_authorize, isPacked == 1, isAmplified == 1, buffSkill, reductionLevel, isLunaReskin == 1, isEnhance, enhanceSkillId, enhanceSkillEnchant, unSeal, skinSkill, grindSocket, grindColor, 0, 0, contaminated);
					item.setPersistentState(PersistentState.UPDATED);
					mailboxItems.add(item);
				}
			}
		});
		
		return mailboxItems;
	}
	
	/**
	 * Saves all letters from a player's mailbox to the database.<br>
	 * This method retrieves the {@link Mailbox} from the {@code Player}.<br>
	 * It iterates through every {@link Letter} and calls {@code Letter)} for each one.
	 * @param player The {@code Player} object containing the mailbox to save.
	 */
	@Override
	public void storeMailbox(Player player)
	{
		final Mailbox mailbox = player.getMailbox();
		if (mailbox == null)
		{
			return;
		}
		
		final Collection<Letter> letters = mailbox.getLetters();
		for (Letter letter : letters)
		{
			storeLetter(letter.getTimeStamp(), letter);
		}
	}
	
	/**
	 * Saves or updates a {@link Letter} in the database based on its persistent state.<br>
	 * This method checks if the letter is new or requires an update before calling the internal save logic.<br>
	 * It then sets the persistence state of the {@code letter} to {@code UPDATED}.
	 * @param time The {@code Timestamp} to associate with the letter.
	 * @param letter The {@link Letter} object to be stored or updated.
	 * @return {@code true} if the database operation was successful, otherwise {@code false}.
	 */
	@Override
	public boolean storeLetter(Timestamp time, Letter letter)
	{
		boolean result = false;
		switch (letter.getLetterPersistentState())
		{
			case NEW:
				result = saveLetter(time, letter);
				break;
			case UPDATE_REQUIRED:
				result = updateLetter(time, letter);
				break;
			/*
			 * case DELETED: return deleteLetter(letter);
			 */
			default:
				break;
		}
		
		letter.setPersistState(PersistentState.UPDATED);
		return result;
	}
	
	/**
	 * Saves a {@link Letter} object into the database.<br>
	 * This method inserts or updates the mail record with the provided {@code Timestamp}.
	 * @param time The time at which the letter was received.
	 * @param letter The {@link Letter} object to be saved.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	private boolean saveLetter(Timestamp time, Letter letter)
	{
		int attachedItemId = 0;
		if (letter.getAttachedItem() != null)
		{
			attachedItemId = letter.getAttachedItem().getObjectId();
		}
		
		final int fAttachedItemId = attachedItemId;
		
		return DB.insertUpdate("INSERT INTO `mail` (`mail_unique_id`, `mail_recipient_id`, `sender_name`, `mail_title`, `mail_message`, `unread`, `attached_item_id`, `attached_kinah_count`, `express`, `recieved_time`) VALUES(?,?,?,?,?,?,?,?,?,?)", stmt ->
		{
			stmt.setInt(1, letter.getObjectId());
			stmt.setInt(2, letter.getRecipientId());
			stmt.setString(3, letter.getSenderName());
			stmt.setString(4, letter.getTitle());
			stmt.setString(5, letter.getMessage());
			stmt.setBoolean(6, letter.isUnread());
			stmt.setInt(7, fAttachedItemId);
			stmt.setLong(8, letter.getAttachedKinah());
			stmt.setInt(9, letter.getLetterType().getId());
			stmt.setTimestamp(10, time);
			stmt.execute();
		});
	}
	
	/**
	 * Updates the information of an existing letter in the database.<br>
	 * This method synchronizes the state of a {@code Letter} object with its record.
	 * @param time The timestamp for when the letter was received.
	 * @param letter The {@code Letter} object containing the updated data.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	private boolean updateLetter(Timestamp time, Letter letter)
	{
		int attachedItemId = 0;
		if (letter.getAttachedItem() != null)
		{
			attachedItemId = letter.getAttachedItem().getObjectId();
		}
		
		final int fAttachedItemId = attachedItemId;
		
		return DB.insertUpdate("UPDATE mail SET  unread=?, attached_item_id=?, attached_kinah_count=?, `express`=?, recieved_time=? WHERE mail_unique_id=?", stmt ->
		{
			stmt.setBoolean(1, letter.isUnread());
			stmt.setInt(2, fAttachedItemId);
			stmt.setLong(3, letter.getAttachedKinah());
			stmt.setInt(4, letter.getLetterType().getId());
			stmt.setTimestamp(5, time);
			stmt.setInt(6, letter.getObjectId());
			stmt.execute();
		});
	}
	
	/**
	 * Removes a specific letter from the database.<br>
	 * This method uses the unique identifier to locate the record.
	 * @param letterId The unique ID of the letter to delete.
	 * @return {@code true} if the operation was successful, or {@code false} otherwise.
	 */
	@Override
	public boolean deleteLetter(int letterId)
	{
		return DB.insertUpdate("DELETE FROM mail WHERE mail_unique_id=?", stmt ->
		{
			stmt.setInt(1, letterId);
			stmt.execute();
		});
	}
	
	/**
	 * Updates the total number of letters in a player's mailbox.<br>
	 * This method synchronizes the count with the database.
	 * @param recipientCommonData The {@code PlayerCommonData} object containing the name and current letter count.
	 */
	@Override
	public void updateOfflineMailCounter(PlayerCommonData recipientCommonData)
	{
		DB.insertUpdate("UPDATE players SET mailbox_letters=? WHERE name=?", stmt ->
		{
			stmt.setInt(1, recipientCommonData.getMailboxLetters());
			stmt.setString(2, recipientCommonData.getName());
			stmt.execute();
		});
	}
	
	/**
	 * Retrieves all unique identifiers from the {@code mail} table.<br>
	 * This method queries the database to collect every {@code mail_unique_id}.<br>
	 * If an error occurs, it returns an empty array.
	 * @return An array of integers containing the mail IDs.
	 */
	@Override
	public int[] getUsedIDs()
	{
		final PreparedStatement statement = DB.prepareStatement("SELECT mail_unique_id FROM mail", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		try
		{
			final ResultSet rs = statement.executeQuery();
			rs.last();
			final int count = rs.getRow();
			rs.beforeFirst();
			final int[] ids = new int[count];
			for (int i = 0; i < count; i++)
			{
				rs.next();
				ids[i] = rs.getInt("mail_unique_id");
			}
			
			return ids;
		}
		catch (SQLException e)
		{
			log.error("Can't get list of id's from mail table", e);
		}
		finally
		{
			DB.close(statement);
		}
		
		return new int[0];
	}
	
	/**
	 * Checks if the current database system supports a specific feature.<br>
	 * This method delegates the check to {@code int, int)}.
	 * @param s The name of the feature to check.
	 * @param i The first integer parameter for the feature.
	 * @param i1 The second integer parameter for the feature.
	 * @return {@code true} if the feature is supported, {@code false} otherwise.
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
