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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.BlockListDAO;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BLOCK_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_RESPONSE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.world.World;

/**
 * This service manages in-game social group activities.<br>
 * It handles features such as the buddy list and legions for {@link Player} objects.
 * @author Ben
 */
public class SocialService
{
	/**
	 * Adds a player to the block list of another player.<br>
	 * This method updates the database and sends a success packet to the client.
	 * @param player The {@code Player} who is performing the action.
	 * @param blockedPlayer The {@code Player} who will be added to the block list.
	 * @param reason A {@code String} describing why the user was blocked.
	 * @return {@code true} if the operation succeeded, otherwise {@code false}.
	 */
	public static boolean addBlockedUser(Player player, Player blockedPlayer, String reason)
	{
		if (DAOManager.getDAO(BlockListDAO.class).addBlockedUser(player.getObjectId(), blockedPlayer.getObjectId(), reason))
		{
			player.getBlockList().add(new BlockedPlayer(blockedPlayer.getCommonData(), reason));
			
			player.getClientConnection().sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.BLOCK_SUCCESSFUL, blockedPlayer.getName()));
			player.getClientConnection().sendPacket(new SM_BLOCK_LIST());
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a specific user from the block list of a {@link Player}.<br>
	 * This method updates the database and notifies the client.
	 * @param player The {@code Player} who owns the block list.
	 * @param blockedUserId The unique ID of the user to be removed.
	 * @return {@code true} if the removal was successful, otherwise {@code false}.
	 */
	public static boolean deleteBlockedUser(Player player, int blockedUserId)
	{
		if (DAOManager.getDAO(BlockListDAO.class).delBlockedUser(player.getObjectId(), blockedUserId))
		{
			player.getBlockList().remove(blockedUserId);
			player.getClientConnection().sendPacket(new SM_BLOCK_RESPONSE(SM_BLOCK_RESPONSE.UNBLOCK_SUCCESSFUL, DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(blockedUserId).getName()));
			
			player.getClientConnection().sendPacket(new SM_BLOCK_LIST());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Updates the reason for blocking a specific player.<br>
	 * This method checks if the new {@code reason} is different from the current one.<br>
	 * It updates the database and sends an {@link SM_BLOCK_LIST} packet to the {@code player}.
	 * @param player The {@link Player} who performed the action.
	 * @param target The {@link BlockedPlayer} being updated.
	 * @param reason The new string description for the block.
	 * @return {@code true} if the update was successful, otherwise {@code false}.
	 */
	public static boolean setBlockedReason(Player player, BlockedPlayer target, String reason)
	{
		if (!target.getReason().equals(reason))
		{
			if (DAOManager.getDAO(BlockListDAO.class).setReason(player.getObjectId(), target.getObjId(), reason))
			{
				target.setReason(reason);
				player.getClientConnection().sendPacket(new SM_BLOCK_LIST());
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Establishes a friendship between two {@link Player} objects.<br>
	 * This method updates the database and adds both players to each other's friend lists.<br>
	 * It also sends notification packets to both clients.
	 * @param friend1 The first player to be added as a friend.
	 * @param friend2 The second player to be added as a friend.
	 */
	public static void makeFriends(Player friend1, Player friend2)
	{
		DAOManager.getDAO(FriendListDAO.class).addFriends(friend1, friend2);
		
		friend1.getFriendList().addFriend(new Friend(friend2.getCommonData()));
		friend2.getFriendList().addFriend(new Friend(friend1.getCommonData()));
		
		friend1.getClientConnection().sendPacket(new SM_FRIEND_LIST());
		friend2.getClientConnection().sendPacket(new SM_FRIEND_LIST());
		
		friend1.getClientConnection().sendPacket(new SM_FRIEND_RESPONSE(friend2.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
		friend2.getClientConnection().sendPacket(new SM_FRIEND_RESPONSE(friend1.getName(), SM_FRIEND_RESPONSE.TARGET_ADDED));
	}
	
	/**
	 * Removes a friend from the {@code deleter} player's list.<br>
	 * This method updates the database and notifies both players.<br>
	 * It handles cases where the target player is offline or online.
	 * @param deleter The {@link Player} who is removing the friend.
	 * @param exFriend2Id The unique ID of the friend to be removed.
	 */
	public static void deleteFriend(Player deleter, int exFriend2Id)
	{
		// If the DAO is successful
		if (DAOManager.getDAO(FriendListDAO.class).delFriends(deleter.getObjectId(), exFriend2Id))
		{
			// Try to get the target player from the cache
			Player friend2Player = PlayerService.getCachedPlayer(exFriend2Id);
			
			// If the cache doesn't have this player, try to get him from the world
			if (friend2Player == null)
			{
				friend2Player = World.getInstance().findPlayer(exFriend2Id);
			}
			
			final String friend2Name = friend2Player != null ? friend2Player.getName() : DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(exFriend2Id).getName();
			
			// Delete from deleter's friend list and send packets
			deleter.getFriendList().delFriend(exFriend2Id);
			
			deleter.getClientConnection().sendPacket(new SM_FRIEND_LIST());
			deleter.getClientConnection().sendPacket(new SM_FRIEND_RESPONSE(friend2Name, SM_FRIEND_RESPONSE.TARGET_REMOVED));
			deleter.getClientConnection().sendPacket(new SM_SYSTEM_MESSAGE(1300888, friend2Name));
			
			if (friend2Player != null)
			{
				friend2Player.getFriendList().delFriend(deleter.getObjectId());
				
				if (friend2Player.isOnline())
				{
					friend2Player.getClientConnection().sendPacket(new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.DELETED, deleter.getName()));
					friend2Player.getClientConnection().sendPacket(new SM_FRIEND_LIST());
				}
			}
		}
		
	}
	
	/**
	 * Updates the personal note for a specific friend.<br>
	 * This method saves the new {@code notice} to the database.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_LIST} packet to the player.
	 * @param player The {@link Player} who owns the friend list.
	 * @param friend The {@link Friend} object to update.
	 * @param notice The new text for the friend note.
	 */
	public static void setFriendNote(Player player, Friend friend, String notice)
	{
		friend.setNote(notice);
		DAOManager.getDAO(FriendListDAO.class).setFriendNote(player.getObjectId(), friend.getOid(), notice);
		player.getClientConnection().sendPacket(new SM_FRIEND_LIST());
	}
}
