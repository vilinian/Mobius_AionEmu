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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.Iterator;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * This packet is sent by the server to provide the client with a list of characters.<br>
 * It contains the necessary data for displaying available characters associated with an account.
 * @author Nemesiss, AEJTester
 * @author GiGatR00n
 */
public class SM_CHARACTER_LIST extends PlayerInfo
{
	/**
	 * List Unk 0 and 2 AccountId
	 */
	private final int list;
	private final int accountId;
	
	/**
	 * Creates a new {@code SM_CHARACTER_LIST} packet.<br>
	 * This packet sends the character list to the client.<br>
	 * It uses the provided {@code list} type and {@code accountId}.
	 * @param list The list type, where {@code 0} is an empty list and {@code 2} means send characters.
	 * @param accountId The unique identifier for the account.
	 */
	public SM_CHARACTER_LIST(int list, int accountId)
	{
		this.list = list; // 0 = Empty List, 2 = SendChars
		this.accountId = accountId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(list);
		if (list == 0)
		{
			writeD(accountId);
			writeC(0x00);
		}
		else if (list == 2)
		{
			writeD(accountId);
			
			final Account account = con.getAccount();
			
			/* Checks for Deleted Characters for each client request */
			removeDeletedCharacters(account);
			
			writeC(account.size()); // characters count
			
			for (PlayerAccountData playerData : account.getSortedAccountsList())
			{
				final PlayerCommonData pcd = playerData.getPlayerCommonData();
				final CharacterBanInfo cbi = playerData.getCharBanInfo();
				final Player player = PlayerService.getPlayer(pcd.getPlayerObjId(), account);
				
				writePlayerInfo(playerData);
				writeB(new byte[40]);
				
				if ((cbi != null) && (cbi.getEnd() > (System.currentTimeMillis() / 1000)))
				{
					// client wants int so let's hope we do not reach long limit with timestamp while this server is used :P
					writeD((int) cbi.getStart()); // startPunishDate
					writeD((int) cbi.getEnd()); // endPunishDate
					writeS(cbi.getReason());
					writeB(new byte[200]); // unk 4.5.0.18
				}
				else
				{
					writeB(new byte[52]); // unk 4.9
					writeD(playerData.getDeletionTimeInSeconds()); // v4.9
					writeH(player.getPlayerSettings().getDisplay()); // display helmet 0 show, 5 dont show
					writeH(0); // unk
					writeD(0); // unk
					writeD(0); // unk
					writeD(0); // unk
					writeD(DAOManager.getDAO(MailDAO.class).mailCount(pcd.getPlayerObjId())); // All Mail Count
					writeD(DAOManager.getDAO(MailDAO.class).unreadedMails(pcd.getPlayerObjId())); // Unread Mail Count
					writeD(0); // unk
					writeD(0); // unk
					writeQ(BrokerService.getInstance().getCollectedMoney(pcd)); // collected money from broker
					writeD(0); // unk
					writeB(new byte[146]); // unk 5.0 TODO
				}
			}
		}
	}
	
	/**
	 * This method cleans up characters from an {@code Account}.<br>
	 * It identifies and removes characters that have passed their deletion time.<br>
	 * If the account becomes empty, it triggers a warehouse removal.
	 * @param account The {@code Account} object to process.
	 */
	public void removeDeletedCharacters(Account account)
	{
		/* Removes chars that should be removed */
		final Iterator<PlayerAccountData> it = account.iterator();
		while (it.hasNext())
		{
			final PlayerAccountData pad = it.next();
			final Race race = pad.getPlayerCommonData().getRace();
			final long deletionTime = (long) pad.getDeletionTimeInSeconds() * (long) 1000;
			if ((deletionTime != 0) && (deletionTime <= System.currentTimeMillis()))
			{
				it.remove();
				account.decrementCountOf(race);
				PlayerService.deletePlayerFromDB(pad.getPlayerCommonData().getPlayerObjId());
			}
		}
		
		if (account.isEmpty())
		{
			removeAccountWH(account.getId());
			account.getAccountWarehouse().clear();
		}
	}
	
	/**
	 * Removes the warehouse data for a specific account.<br>
	 * This method interacts with the {@link InventoryDAO} to delete records.
	 * @param accountId The unique identifier of the account to process.
	 */
	private static void removeAccountWH(int accountId)
	{
		DAOManager.getDAO(InventoryDAO.class).deleteAccountWH(accountId);
	}
}
