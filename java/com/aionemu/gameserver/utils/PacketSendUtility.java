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
package com.aionemu.gameserver.utils;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.SiegeZoneInstance;

/**
 * Provides static utility methods for sending network packets to players and other entities.<br>
 * These methods interact only with objects passed as parameters to keep the {@link Player} class as a pure data holder.
 * @author Luno
 */
public class PacketSendUtility
{
	/**
	 * Sends a yellow message to a specific {@link Player}.<br>
	 * This method uses the {@code SM_MESSAGE} packet.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The text content of the message to be sent.
	 */
	public static void sendMessage(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW));
	}
	
	/**
	 * Sends a white colored message to a specific player.<br>
	 * This method uses the {@code AionServerPacket)} utility.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content of the message to display.
	 */
	public static void sendWhiteMessage(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE));
	}
	
	/**
	 * Sends a white message to the center of the screen for a specific player.<br>
	 * This method uses the {@code ChatType#WHITE_CENTER} type.
	 * @param player The {@link Player} who will receive the message.
	 * @param msg The text content to be displayed.
	 */
	public static void sendWhiteMessageOnCenter(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE_CENTER));
	}
	
	/**
	 * Sends a message to a specific {@link Player} in yellow text.<br>
	 * This method uses the {@code ChatType.YELLOW} style.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content of the message to send.
	 */
	public static void sendYellowMessage(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW));
	}
	
	/**
	 * Sends a yellow message to the center of the screen for a specific player.<br>
	 * This method uses the {@code YELLOW_CENTER} type.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content to be displayed.
	 */
	public static void sendYellowMessageOnCenter(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW_CENTER));
	}
	
	/**
	 * Sends a message to a specific player in bright yellow text.<br>
	 * This method uses the {@code AionServerPacket)} utility.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content of the message to display.
	 */
	public static void sendBrightYellowMessage(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW));
	}
	
	/**
	 * Sends a bright yellow message to the center of a player's screen.<br>
	 * This method uses {@code AionServerPacket)} internally.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content to display.
	 */
	public static void sendBrightYellowMessageOnCenter(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW_CENTER));
	}
	
	/**
	 * Sends a system message to a specific {@link Player}.<br>
	 * This method uses the {@code GROUP_LEADER} chat type.
	 * @param player The {@code Player} who will receive the message.
	 * @param sender The name of the person sending the message.
	 * @param msg The content of the message to be sent.
	 */
	public static void sendSys1Message(Player player, String sender, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, sender, msg, ChatType.GROUP_LEADER));
	}
	
	/**
	 * Sends a system message to a specific {@link Player}.<br>
	 * This method uses the {@code SM_MESSAGE} packet type.<br>
	 * The message is displayed in white text.
	 * @param player The {@code Player} who will receive the message.
	 * @param sender The name of the entity sending the message.
	 * @param msg The content of the message to be sent.
	 */
	public static void sendSys2Message(Player player, String sender, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE));
	}
	
	/**
	 * Sends a system message to a specific {@link Player}.<br>
	 * This method uses the {@code SM_MESSAGE} packet with the {@code COMMAND} chat type.
	 * @param player The {@code Player} who will receive the message.
	 * @param sender The name of the entity sending the message.
	 * @param msg The content of the message to be displayed.
	 */
	public static void sendSys3Message(Player player, String sender, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, sender, msg, ChatType.COMMAND));
	}
	
	/**
	 * Sends a warning message to the center of the screen for a specific player.<br>
	 * This method uses the {@code LEAGUE_ALERT} type.
	 * @param player The {@code Player} who will receive the message.
	 * @param msg The {@code String} content of the warning message.
	 */
	public static void sendWarnMessageOnCenter(Player player, String msg)
	{
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.LEAGUE_ALERT));
	}
	
	/**
	 * Sends a specific packet to a player.<br>
	 * This method checks if the {@code Player} has an active connection before sending.<br>
	 * It uses the {@link com.aionemu.gameserver.network.aion.AionServerPacket} object provided.
	 * @param player The {@code Player} who will receive the packet.
	 * @param packet The {@code AionServerPacket} to be sent to the client.
	 */
	public static void sendPacket(Player player, AionServerPacket packet)
	{
		if (player.getClientConnection() != null)
		{
			player.getClientConnection().sendPacket(packet);
		}
	}
	
	/**
	 * Sends a packet to a specific player after a delay.<br>
	 * The method uses {@link ThreadPoolManager} to schedule the task.<br>
	 * It checks if the {@code player} connection is active before sending.
	 * @param player The {@link Player} who will receive the packet.
	 * @param packet The {@link AionServerPacket} to be sent.
	 * @param time The delay in milliseconds before sending the packet.
	 */
	public static void playerSendPacketTime(Player player, AionServerPacket packet, int time)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (player.getClientConnection() != null)
			{
				player.getClientConnection().sendPacket(packet);
			}
		}, time);
	}
	
	/**
	 * Sends a specific packet to all online players who know the {@link Npc}.<br>
	 * This action is scheduled to occur after a delay.
	 * @param npc The {@code Npc} object that owns the known list of players.
	 * @param packet The {@code AionServerPacket} to be sent to the players.
	 * @param time The delay in milliseconds before sending the packet.
	 */
	public static void npcSendPacketTime(Npc npc, AionServerPacket packet, int time)
	{
		ThreadPoolManager.getInstance().schedule(() -> npc.getKnownList().doOnAllPlayers(player ->
		{
			if (player.isOnline())
			{
				sendPacket(player, packet);
			}
			
		}), time);
	}
	
	/**
	 * Sends a network packet to other players.<br>
	 * This method handles the broadcasting logic for {@link AionServerPacket}.<br>
	 * It determines who receives the packet based on the {@code toSelf} flag.
	 * @param player The {@code Player} object used as the source of the broadcast.
	 * @param packet The {@code AionServerPacket} to be sent to other players.
	 * @param toSelf If set to {@code true}, the packet is also sent back to the original player.
	 */
	public static void broadcastPacket(Player player, AionServerPacket packet, boolean toSelf)
	{
		if (toSelf)
		{
			sendPacket(player, packet);
		}
		
		broadcastPacket(player, packet);
	}
	
	/**
	 * Sends a specific packet to a {@link VisibleObject}.<br>
	 * It checks if the object is a {@link Player} and sends it directly.<br>
	 * It then broadcasts the packet to all nearby entities.
	 * @param visibleObject The target object to receive the packet.
	 * @param packet The {@code AionServerPacket} to be sent.
	 */
	public static void broadcastPacketAndReceive(VisibleObject visibleObject, AionServerPacket packet)
	{
		if (visibleObject instanceof Player)
		{
			sendPacket((Player) visibleObject, packet);
		}
		
		broadcastPacket(visibleObject, packet);
	}
	
	/**
	 * Sends a specific packet to all players who can see the given object.<br>
	 * This method checks if each player is online before sending the {@code AionServerPacket}.
	 * @param visibleObject The {@link VisibleObject} used to determine which players receive the packet.
	 * @param packet The {@link AionServerPacket} to be sent to the players.
	 */
	public static void broadcastPacket(VisibleObject visibleObject, AionServerPacket packet)
	{
		visibleObject.getKnownList().doOnAllPlayers(player ->
		{
			if (player.isOnline())
			{
				sendPacket(player, packet);
			}
		});
	}
	
	/**
	 * Sends a network packet to a specific player and others nearby.<br>
	 * It checks if the recipient is known by the source {@code player}.<br>
	 * The method uses an {@link ObjectFilter} to decide who receives it.
	 * @param player The player who initiates the broadcast.
	 * @param packet The {@code AionServerPacket} to be sent.
	 * @param toSelf If {@code true}, the source player also receives the packet.
	 * @param filter The filter used to determine which players should receive the packet.
	 */
	public static void broadcastPacket(Player player, AionServerPacket packet, boolean toSelf, ObjectFilter<Player> filter)
	{
		if (toSelf)
		{
			sendPacket(player, packet);
		}
		
		player.getKnownList().doOnAllPlayers(object ->
		{
			if (filter.acceptObject(object))
			{
				sendPacket(object, packet);
			}
		});
	}
	
	/**
	 * Sends a specific packet to all players near a visible object.<br>
	 * It checks if the player is within the specified {@code distance}.
	 * @param visibleObject The {@link VisibleObject} used as the center point for the broadcast.
	 * @param packet The {@link AionServerPacket} to be sent to nearby players.
	 * @param distance The maximum range in units for the broadcast.
	 */
	public static void broadcastPacket(VisibleObject visibleObject, AionServerPacket packet, int distance)
	{
		visibleObject.getKnownList().doOnAllPlayers(p ->
		{
			if (MathUtil.isIn3dRange(visibleObject, p, distance))
			{
				sendPacket(p, packet);
			}
		});
	}
	
	/**
	 * Sends a specific packet to all players that meet certain criteria.<br>
	 * It uses the {@code World} instance to iterate through every player.<br>
	 * Only players who pass the provided {@link ObjectFilter} will receive the {@code AionServerPacket}.
	 * @param packet The {@code AionServerPacket} to be sent to the players.
	 * @param filter The {@code ObjectFilter} used to determine which players should receive the packet.
	 */
	public static void broadcastFilteredPacket(AionServerPacket packet, ObjectFilter<Player> filter)
	{
		World.getInstance().doOnAllPlayers(object ->
		{
			if (filter.acceptObject(object))
			{
				sendPacket(object, packet);
			}
		});
	}
	
	/**
	 * Sends a specific network packet to all online members of a {@link Legion}.<br>
	 * This method iterates through the legion and calls {@code AionServerPacket)} for each member.
	 * @param legion The {@code Legion} object containing the members to receive the packet.
	 * @param packet The {@code AionServerPacket} to be sent to every online member.
	 */
	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			sendPacket(onlineLegionMember, packet);
		}
	}
	
	/**
	 * Sends a specific packet to all members of a legion.<br>
	 * It excludes the player with the provided {@code playerObjId}.<br>
	 * This method uses {@code AionServerPacket)} for each member.
	 * @param legion The {@code Legion} object containing the members to receive the packet.
	 * @param packet The {@code AionServerPacket} to be sent.
	 * @param playerObjId The unique ID of the player who should not receive the packet.
	 */
	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet, int playerObjId)
	{
		for (Player onlineLegionMember : legion.getOnlineLegionMembers())
		{
			if (onlineLegionMember.getObjectId() != playerObjId)
			{
				sendPacket(onlineLegionMember, packet);
			}
		}
	}
	
	/**
	 * Sends a specific network packet to every player located within a siege zone.<br>
	 * This method uses the {@code doOnAllPlayers} logic to iterate through players.<br>
	 * It ensures that all active participants in the area receive the same data.
	 * @param zone The {@code SiegeZoneInstance} where the packet will be broadcast.
	 * @param packet The {@code AionServerPacket} object containing the data to send.
	 */
	public static void broadcastPacketToZone(SiegeZoneInstance zone, AionServerPacket packet)
	{
		zone.doOnAllPlayers(player -> sendPacket(player, packet));
	}
	
	/**
	 * Sends a specific packet to all members of a player's group.<br>
	 * It checks if the {@code PlayerGroup2} exists before sending.
	 * @param player The {@link Player} whose group will receive the packet.
	 * @param packet The {@link AionServerPacket} to be sent to the group members.
	 * @param toSelf If set to {@code false}, the original {@code player} will not receive their own packet.
	 */
	public static void broadcastPacketToGroup(Player player, AionServerPacket packet, boolean toSelf)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group == null)
		{
			return;
		}
		
		for (Player member : group.getMembers())
		{
			if ((member == player) && !toSelf)
			{
				continue;
			}
			
			sendPacket(member, packet);
		}
	}
	
	/**
	 * Sends a network packet to all members of a player's group.<br>
	 * This method iterates through the {@link PlayerGroup} associated with the {@code player}.<br>
	 * It skips the original sender if {@code toSelf} is set to {@code false}.
	 * @param player The {@code Player} whose group will receive the packet.
	 * @param packet The {@code AionServerPacket} to be sent to the members.
	 * @param toSelf If {@code true}, the packet is also sent to the original sender.
	 */
	public static void broadcastPacketToAlliance(Player player, AionServerPacket packet, boolean toSelf)
	{
		final PlayerGroup group = player.getPlayerGroup2();
		if (group == null)
		{
			return;
		}
		
		for (Player member : group.getMembers())
		{
			if ((member == player) && !toSelf)
			{
				continue;
			}
			
			sendPacket(member, packet);
		}
	}
	
	/**
	 * Special function for Aion Absolute Announce :)
	 */
	/**
	 * Sends a global event message to all players.<br>
	 * This method prefixes the message with {@code [EVENT ALL ON A DAEVA]}.<br>
	 * It uses {@code String)} for each player.
	 * @param msg The content of the event message to display.
	 */
	public static void event(String msg)
	{
		World.getInstance().doOnAllPlayers(sender -> sendBrightYellowMessageOnCenter(sender, "[EVENT ALL ON A DAEVA]: " + msg));
	}
	
	/**
	 * Broadcasts a specific event message to all players.<br>
	 * The message is prefixed with {@code [EVENT]: }.<br>
	 * It uses the {@code getInstance} to find all active players.
	 * @param msg The content of the message to display.
	 */
	public static void event2(String msg)
	{
		World.getInstance().doOnAllPlayers(sender -> broadcastPacket(sender, new SM_MESSAGE(sender, "[EVENT]: " + msg, ChatType.GROUP_LEADER), true));
	}
}
