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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FRIEND_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 * This class represents the collection of friends associated with a specific player.<br>
 * It manages friend data and provides methods to interact with the {@link Friend} list.
 * @author Ben
 */
public class FriendList implements Iterable<Friend>
{
	private static final Logger log = LoggerFactory.getLogger(FriendList.class);
	private Status status = Status.OFFLINE;
	private volatile byte friendListSent = 0;
	private final Queue<Friend> friends;
	private final Player player;
	
	/**
	 * Creates a new {@link FriendList} for a specific player.<br>
	 * This constructor initializes an empty list of friends.
	 * @param player The {@code Player} who will own this friend list.
	 */
	public FriendList(Player player)
	{
		this(player, new ConcurrentLinkedQueue<>());
	}
	
	/**
	 * Creates a new {@link FriendList} for a specific player.<br>
	 * This constructor initializes the list with a collection of existing friends.
	 * @param owner The {@link Player} who will own this friend list.
	 * @param newFriends A {@code Collection} of {@link Friend} objects to populate the list.
	 */
	public FriendList(Player owner, Collection<Friend> newFriends)
	{
		friends = new ConcurrentLinkedQueue<>(newFriends);
		player = owner;
	}
	
	/**
	 * Finds a specific {@link Friend} in the list.<br>
	 * It searches for a match using the provided unique identifier.
	 * @param objId The unique ID of the friend to find.
	 * @return The matching {@code Friend} object, or {@code null} if no match is found.
	 */
	public Friend getFriend(int objId)
	{
		for (Friend friend : friends)
		{
			if (friend.getOid() == objId)
			{
				return friend;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the total number of friends in the list.<br>
	 * This value corresponds to the size of the internal {@code friends} queue.
	 * @return The number of friends as an {@code int}.
	 */
	public int getSize()
	{
		return friends.size();
	}
	
	/**
	 * Adds a new {@link Friend} to the player's friend list.<br>
	 * This method updates the internal collection of friends.
	 * @param friend The {@code Friend} object to be added.
	 */
	public void addFriend(Friend friend)
	{
		friends.add(friend);
	}
	
	/**
	 * Finds a {@link Friend} by their name.<br>
	 * This method searches the list for a case-insensitive match.<br>
	 * It returns {@code null} if no friend is found.
	 * @param name The name of the friend to search for.
	 * @return The matching {@code Friend} object or {@code null}.
	 */
	public Friend getFriend(String name)
	{
		for (Friend friend : friends)
		{
			if (friend.getName().equalsIgnoreCase(name))
			{
				return friend;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes a specific friend from the list.<br>
	 * This method searches for a friend with the matching {@code friendOid}.<br>
	 * It removes that friend if they are found in the current collection.
	 * @param friendOid The unique identifier of the friend to remove.
	 */
	public void delFriend(int friendOid)
	{
		final Iterator<Friend> it = iterator();
		while (it.hasNext())
		{
			if (it.next().getOid() == friendOid)
			{
				it.remove();
			}
		}
	}
	
	/**
	 * Checks if the friend list has reached its maximum capacity.<br>
	 * It compares the current size against the allowed limit based on player permissions.
	 * @return {@code true} if the list is full, {@code false} otherwise.
	 */
	public boolean isFull()
	{
		final int MAX_FRIENDS = player.havePermission(MembershipConfig.ADVANCED_FRIENDLIST_ENABLE) ? MembershipConfig.ADVANCED_FRIENDLIST_SIZE : CustomConfig.FRIENDLIST_SIZE;
		return getSize() >= MAX_FRIENDS;
	}
	
	/**
	 * Retrieves the current online status of the friend.<br>
	 * This method checks if the player is currently connected to the server.<br>
	 * It returns {@code FriendList.Status.OFFLINE} if the player is not found or is disconnected.
	 * @return The {@code Status} of the friend.
	 */
	public Status getStatus()
	{
		return status;
	}
	
	/**
	 * Updates the online status of this friend list.<br>
	 * This method notifies all online friends about the status change.<br>
	 * It sends specific packets based on whether the player is logging in or out.
	 * @param status The new {@code Status} to apply to the friend list.
	 * @param pcd The {@code PlayerCommonData} associated with the player.
	 */
	public void setStatus(Status status, PlayerCommonData pcd)
	{
		final Status previousStatus = this.status;
		this.status = status;
		
		for (Friend friend : friends) // For all my friends
		{
			if (friend.isOnline()) // If the player is online
			{
				final Player friendPlayer = friend.getPlayer();
				if (friendPlayer == null)
				{
					continue;
				}
				
				if (friendPlayer.getClientConnection() == null)
				{
					log.warn("[AT] friendlist connection is null");
					continue;
				}
				
				friendPlayer.getFriendList().getFriend(pcd.getPlayerObjId()).setPCD(pcd);
				friendPlayer.getClientConnection().sendPacket(new SM_FRIEND_UPDATE(player.getObjectId()));
				
				if (previousStatus == Status.OFFLINE)
				{
					// Show LOGIN message
					friendPlayer.getClientConnection().sendPacket(new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.LOGIN, player.getName()));
					friendPlayer.getClientConnection().sendPacket(new SM_SYSTEM_MESSAGE(1300890, player.getName()));
				}
				else if (status == Status.OFFLINE)
				{
					// Show LOGOUT message
					friendPlayer.getClientConnection().sendPacket(new SM_FRIEND_NOTIFY(SM_FRIEND_NOTIFY.LOGOUT, player.getName()));
				}
			}
		}
		
	}
	
	/**
	 * Returns an {@link Iterator} to loop through all friends.<br>
	 * This allows you to visit each {@link Friend} in the list one by one.
	 * @return An {@code Iterator} of {@link Friend} objects.
	 */
	@Override
	public Iterator<Friend> iterator()
	{
		return friends.iterator();
	}
	
	/**
	 * Checks if the friend list has been sent to the player.<br>
	 * This method returns {@code true} if the data was successfully transmitted.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the friend list was sent, {@code false} otherwise.
	 */
	public boolean getIsFriendListSent()
	{
		return friendListSent == 1;
	}
	
	/**
	 * Updates the status of whether the friend list has been sent to the player.<br>
	 * This method sets the internal {@code friendListSent} flag.
	 * @param value The new status to set. Use {@code true} if sent and {@code false} otherwise.
	 */
	public void setIsFriendListSent(boolean value)
	{
		friendListSent = (byte) (value ? 1 : 0);
	}
	
	public enum Status
	{
		/**
		 * User is offline or invisible
		 */
		OFFLINE((byte) 0),
		/**
		 * User is online
		 */
		ONLINE((byte) 1),
		/**
		 * User is away or busy
		 */
		AWAY((byte) 3);
		
		byte value;
		
		private Status(byte value)
		{
			this.value = value;
		}
		
		public byte getId()
		{
			return value;
		}
		
		/**
		 * Gets the Status from its int value<br />
		 * Returns null if out of range
		 * @param value range 0-3
		 * @return Status
		 */
		public static Status getByValue(byte value)
		{
			for (Status stat : values())
			{
				if (stat.getId() == value)
				{
					return stat;
				}
			}
			
			return null;
		}
	}
}
