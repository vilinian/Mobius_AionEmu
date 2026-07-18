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
package com.aionemu.gameserver.services.transfers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.PlayerTransferConfig;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_PTRANSFER_CONTROL;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for transferring players between different game servers.<br>
 * It manages data synchronization and communication via {@link BrokerService}.<br>
 * This service ensures that player states are correctly updated during a transfer.
 * @author KID
 */
public class PlayerTransferService
{
	private final Logger log = LoggerFactory.getLogger(PlayerTransferService.class);
	private final Logger textLog = LoggerFactory.getLogger("PLAYERTRANSFER");
	private static PlayerTransferService instance = new PlayerTransferService();
	
	/**
	 * Gets the singleton instance of the {@link PlayerTransferService}.<br>
	 * This method provides a global access point to the service.
	 * @return The single shared instance of {@code PlayerTransferService}.
	 */
	public static PlayerTransferService getInstance()
	{
		return instance;
	}
	
	private final PlayerDAO dao;
	private final Map<Integer, TransferablePlayer> transfers = new ConcurrentHashMap<>();
	private final List<Integer> rsList = new ArrayList<>();
	
	/**
	 * Initializes the {@code PlayerTransferService}.<br>
	 * This constructor sets up the required database DAOs.<br>
	 * It also parses the skill restrictions from {@link PlayerTransferConfig}.
	 */
	public PlayerTransferService()
	{
		dao = DAOManager.getDAO(PlayerDAO.class);
		if (!PlayerTransferConfig.REMOVE_SKILL_LIST.equals("*"))
		{
			for (String skillId : PlayerTransferConfig.REMOVE_SKILL_LIST.split(","))
			{
				rsList.add(Integer.parseInt(skillId));
			}
		}
		
		log.info("PlayerTransferService loaded. With " + rsList.size() + " restricted skills.");
	}
	
	private final String ptsnameitem = "ptsnameitem";
	
	/**
	 * Handles special logic for players with specific names when they enter the world.<br>
	 * It checks if the {@link Player} name ends with a predefined prefix from {@code PlayerTransferConfig}.<br>
	 * If it matches, the method sends a message and manages a special item variable.
	 * @param player The {@link Player} object that entered the world.
	 */
	public void onEnterWorld(Player player)
	{
		if (player.getName().endsWith(PlayerTransferConfig.NAME_PREFIX))
		{
			PacketSendUtility.sendMessage(player, "You can add your oldnickname-friend to your friendlist!");
			if (!player.hasVar(ptsnameitem))
			{
				final long count = ItemService.addItem(player, 169670001, 1);
				if (count == 1)
				{
					PacketSendUtility.sendMessage(player, "Please empty your inventory and relogin again. After that you'll be able to receive item that allows you to change your name.");
				}
				else
				{
					player.setVar(ptsnameitem, 1, true);
				}
			}
		}
	}
	
	/**
	 * Starts the process of transferring a player character to another account.<br>
	 * This method validates if the player is online, has active broker items, or exceeds kinah limits.<br>
	 * If all checks pass, it registers the transfer and notifies the client.
	 * @param accountId The unique identifier for the source account.
	 * @param targetAccountId The unique identifier for the destination account.
	 * @param playerId The unique identifier of the player character to be moved.
	 * @param targetServerId The ID of the server where the character will move.
	 * @param taskId The unique identifier for this specific transfer task.
	 */
	public void startTransfer(int accountId, int targetAccountId, int playerId, byte targetServerId, int taskId)
	{
		boolean exist = false;
		for (int id : DAOManager.getDAO(PlayerDAO.class).getPlayerOidsOnAccount(accountId))
		{
			if (id == playerId)
			{
				exist = true;
				break;
			}
		}
		
		if (!exist)
		{
			log.warn("transfer #" + taskId + " player " + playerId + " is not present on account " + accountId + ".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "player " + playerId + " is not present on account " + accountId));
			return;
		}
		
		if (DAOManager.getDAO(LegionMemberDAO.class).isIdUsed(playerId))
		{
			log.warn("cannot transfer #" + taskId + " player with existing legion " + playerId + ".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer player with existing legion " + playerId));
			return;
		}
		
		final PlayerCommonData common = dao.loadPlayerCommonData(playerId);
		if (common.isOnline())
		{
			log.warn("cannot transfer #" + taskId + " online players " + playerId + ".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer online players " + playerId));
			return;
		}
		
		if ((PlayerTransferConfig.REUSE_HOURS > 0) && ((common.getLastTransferTime() + (PlayerTransferConfig.REUSE_HOURS * 3600000)) > System.currentTimeMillis()))
		{
			log.warn("cannot transfer #" + taskId + " that player so often " + playerId + ".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer that player so often " + playerId));
			return;
		}
		
		final Player player = PlayerService.getPlayer(playerId, AccountService.loadAccount(accountId));
		final long kinah = player.getInventory().getKinah() + player.getWarehouse().getKinah();
		if ((PlayerTransferConfig.MAX_KINAH > 0) && (kinah >= PlayerTransferConfig.MAX_KINAH))
		{
			log.warn("cannot transfer #" + taskId + " players with " + kinah + " kinah in inventory/wh.");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer players with " + kinah + " kinah in inventory/wh."));
			return;
		}
		
		if (BrokerService.getInstance().hasRegisteredItems(player))
		{
			log.warn("cannot transfer #" + taskId + " player while he own some items in broker.");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer player while he own some items in broker."));
			return;
		}
		
		final TransferablePlayer tp = new TransferablePlayer(playerId, accountId, targetAccountId);
		tp.player = player;
		tp.targetServerId = targetServerId;
		tp.accountId = accountId;
		tp.targetAccountId = targetAccountId;
		tp.taskId = taskId;
		transfers.put(taskId, tp);
		
		textLog.info("taskId:" + taskId + "; [StartTransfer]");
		LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.CHARACTER_INFORMATION, tp));
	}
	
	/**
	 * Clones a character to a target account.<br>
	 * This method validates the character name and checks for available slots.<br>
	 * It then creates the new character using the provided database information.
	 * @param taskId The unique identifier for the current transfer task.
	 * @param targetAccountId The ID of the account receiving the cloned character.
	 * @param name The desired name for the new character.
	 * @param account The account name associated with the character.
	 * @param db The raw database bytes required to create the character.
	 */
	public void cloneCharacter(int taskId, int targetAccountId, String name, String account, byte[] db)
	{
		if (!PlayerService.isFreeName(name))
		{
			if (PlayerTransferConfig.BLOCK_SAMENAME)
			{
				LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "Name is already in use"));
				return;
			}
			
			log.info("Name is already in use `" + name + "`");
			textLog.info("taskId:" + taskId + "; [CloneCharacter:!isFreeName]");
			String newName = name + PlayerTransferConfig.NAME_PREFIX;
			
			final int i = 0;
			while (!PlayerService.isFreeName(newName))
			{
				newName = name + PlayerTransferConfig.NAME_PREFIX + i;
			}
			
			name = newName;
		}
		
		if (AccountService.loadAccount(targetAccountId).size() >= GSConfig.CHARACTER_LIMIT_COUNT)
		{
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "No free character slots"));
			return;
		}
		
		final CMT_CHARACTER_INFORMATION acp = new CMT_CHARACTER_INFORMATION(0, State.CONNECTED);
		acp.setBuffer(ByteBuffer.wrap(db).order(ByteOrder.LITTLE_ENDIAN));
		final Player cha = acp.readInfo(name, targetAccountId, account, rsList, textLog);
		
		if (cha == null)
		{
			// something went wrong!
			log.error("clone failed #" + taskId + " `" + name + "`");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "unexpected sql error while creating a clone"));
		}
		else
		{
			DAOManager.getDAO(PlayerDAO.class).setPlayerLastTransferTime(cha.getObjectId(), System.currentTimeMillis());
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.OK, taskId));
			log.info("clone successful #" + taskId + " `" + name + "`");
			textLog.info("taskId:" + taskId + "; [CloneCharacter:Done]");
		}
	}
	
	/**
	 * This method is called when a player transfer is completed successfully.<br>
	 * It removes the task from the active transfers list.<br>
	 * It also deletes the original player data from the database.
	 * @param taskId The unique identifier for the transfer task.
	 */
	public void onOk(int taskId)
	{
		final TransferablePlayer tplayer = transfers.remove(taskId);
		textLog.info("taskId:" + taskId + "; [TransferComplete]");
		PlayerService.deletePlayerFromDB(tplayer.playerId);
	}
	
	/**
	 * Handles an error that occurs during a player transfer.<br>
	 * This method removes the task from the active list.<br>
	 * It also logs the failure details to the text log.
	 * @param taskId The unique identifier for the current transfer task.
	 * @param reason A description of why the transfer failed.
	 */
	public void onError(int taskId, String reason)
	{
		transfers.remove(taskId);
		textLog.info("taskId:" + taskId + "; [Error. Transfer failed] " + reason);
	}
}
