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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * This packet handles the reception of public chat messages from the client.<br>
 * It processes the message content and distributes it to other players.<br>
 * It utilizes {@link ChatProcessor} to handle the logic for these messages.
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_PUBLIC extends AionClientPacket
{
	/**
	 * Chat type
	 */
	private ChatType type;
	/**
	 * Chat message
	 */
	private String message;
	
	/**
	 * Creates a new instance of {@link CM_CHAT_MESSAGE_PUBLIC}.<br>
	 * This constructor initializes the packet with specific network states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Additional connection states if required.
	 */
	public CM_CHAT_MESSAGE_PUBLIC(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		type = ChatType.getChatTypeByInt(readC());
		
		message = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (ChatProcessor.getInstance().handleChatCommand(player, message))
		{
			return;
		}
		
		message = NameRestrictionService.filterMessage(message);
		
		if (LoggingConfig.LOG_CHAT)
		{
			PlayerChatService.chatLogging(player, type, message);
		}
		
		if (RestrictionsManager.canChat(player) && !PlayerChatService.isFlooding(player))
		{
			switch (type)
			{
				case GROUP:
					if (!player.isInTeam())
					{
						return;
					}
					
					broadcastToGroupMembers(player);
					break;
				case ALLIANCE:
					if (!player.isInAlliance2())
					{
						return;
					}
					
					broadcastToAllianceMembers(player);
					break;
				case GROUP_LEADER:
					if (!player.isInTeam())
					{
						return;
					}
					
					// Alert must go to entire group or alliance.
					if (player.isInGroup2())
					{
						broadcastToGroupMembers(player);
					}
					else
					{
						broadcastToAllianceMembers(player);
					}
					break;
				case LEGION:
					broadcastToLegionMembers(player);
					break;
				case LEAGUE:
				case LEAGUE_ALERT:
					if (!player.isInLeague())
					{
						return;
					}
					
					broadcastToLeagueMembers(player);
					break;
				case NORMAL:
				case SHOUT:
					if (player.isGM())
					{
						broadcastFromGm(player);
					}
					else
					{
						if (CustomConfig.SPEAKING_BETWEEN_FACTIONS)
						{
							broadcastToNonBlockedPlayers(player);
						}
						else
						{
							broadcastToNonBlockedRacePlayers(player);
						}
					}
					break;
				case COMMAND:
					if ((player.getAbyssRank().getRank() == AbyssRankEnum.COMMANDER) || (player.getAbyssRank().getRank() == AbyssRankEnum.SUPREME_COMMANDER))
					{
						broadcastFromCommander(player);
					}
					break;
				default:
					if (player.isGM())
					{
						broadcastFromGm(player);
					}
					else
					{
						AuditLogger.info(player, String.format("Send message type %s. Message: %s", type, message));
					}
					break;
			}
		}
	}
	
	/**
	 * Sends a chat message to all players of the same race.<br>
	 * This method also includes Game Masters in the broadcast.<br>
	 * It uses {@code broadcastPacket} to handle the delivery.
	 * @param player The {@code Player} who sent the original message.
	 */
	private void broadcastFromCommander(Player player)
	{
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return ((senderRace == object.getRace().getRaceId()) || object.isGM());
			}
		});
	}
	
	/**
	 * Sends a chat message to all players.<br>
	 * This method is used specifically for messages sent by a {@code GM}.<br>
	 * It uses {@code broadcastPacket} to deliver the {@code SM_MESSAGE}.
	 * @param player The {@code Player} object who sent the message.
	 */
	private void broadcastFromGm(Player player)
	{
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true);
	}
	
	/**
	 * Sends a chat message to all players who have not blocked the sender.<br>
	 * It uses {@code broadcastPacket} with a custom filter.<br>
	 * The filter ensures that any player in the sender's block list is excluded.
	 * @param player The {@code Player} who sent the original message.
	 */
	private void broadcastToNonBlockedPlayers(Player player)
	{
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return !object.getBlockList().contains(player.getObjectId());
			}
		});
	}
	
	/**
	 * Sends a chat message to players of the same race.<br>
	 * It filters out players who have blocked the sender.<br>
	 * It also sends an "Unknow Message" to players of different races.
	 * @param player The {@code Player} sending the message.
	 */
	private void broadcastToNonBlockedRacePlayers(Player player)
	{
		final int senderRace = player.getRace().getRaceId();
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, message, type), true, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return (((senderRace == object.getRace().getRaceId()) && !object.getBlockList().contains(player.getObjectId())) || object.isGM());
			}
		});
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(player, "Unknow Message", type), false, new ObjectFilter<Player>()
		{
			@Override
			public boolean acceptObject(Player object)
			{
				return (senderRace != object.getRace().getRaceId()) && !object.getBlockList().contains(player.getObjectId()) && !object.isGM();
			}
		});
	}
	
	/**
	 * Sends a chat message to all members of the player's current group.<br>
	 * Checks if the {@code Player} is currently in a team before sending.<br>
	 * Displays an error message if the player is not in a group.
	 * @param player The {@link Player} who sent the message.
	 */
	private void broadcastToGroupMembers(Player player)
	{
		if (player.isInTeam())
		{
			player.getCurrentGroup().sendPacket(new SM_MESSAGE(player, message, type));
		}
		else
		{
			PacketSendUtility.sendMessage(player, "You are not in an alliance or group. (Error 105)");
		}
	}
	
	/**
	 * Sends the current chat message to all members of the player's alliance.<br>
	 * This method uses {@code message} and {@code type} to create an {@code SM_MESSAGE}.<br>
	 * It targets the alliance associated with the provided {@code Player}.
	 * @param player The {@code Player} who sent the message.
	 */
	private void broadcastToAllianceMembers(Player player)
	{
		player.getPlayerAlliance2().sendPacket(new SM_MESSAGE(player, message, type));
	}
	
	/**
	 * Sends a chat message to all members of the league.<br>
	 * This method retrieves the league from the {@link Player} alliance.<br>
	 * It uses the {@code SM_MESSAGE} packet to deliver the content.
	 * @param player The {@code Player} who is sending the message.
	 */
	private void broadcastToLeagueMembers(Player player)
	{
		player.getPlayerAlliance2().getLeague().sendPacket(new SM_MESSAGE(player, message, type));
	}
	
	/**
	 * Sends a chat message to all members of the player's legion.<br>
	 * This method checks if the {@code player} is part of a legion first.<br>
	 * It uses {@code broadcastPacketToLegion} to deliver the packet.
	 * @param player The {@code Player} who sent the message.
	 */
	private void broadcastToLegionMembers(Player player)
	{
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_MESSAGE(player, message, type));
		}
	}
}
