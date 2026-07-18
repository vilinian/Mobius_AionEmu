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
package com.aionemu.gameserver.model.team2;

import java.util.Collection;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.team2.group.PlayerFilters;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.function.Predicate;

/**
 * Represents a temporary team structure for {@link Player} objects.<br>
 * This class handles short-lived group dynamics that do not persist permanently in the game world.
 * @author ATracer
 * @param <TM>
 */
public abstract class TemporaryPlayerTeam<TM extends TeamMember<Player>>extends GeneralTeam<Player, TM>
{
	private LootGroupRules lootGroupRules = new LootGroupRules();
	
	/**
	 * Creates a new instance of a {@link TemporaryPlayerTeam}.<br>
	 * This constructor initializes the team using a specific object identifier.
	 * @param objId The unique identifier for the team object.
	 */
	public TemporaryPlayerTeam(Integer objId)
	{
		super(objId);
	}
	
	/**
	 * Level of the player with lowest exp
	 * @return
	 */
	public abstract int getMinExpPlayerLevel();
	
	/**
	 * Level of the player with highest exp
	 * @return
	 */
	public abstract int getMaxExpPlayerLevel();
	
	/**
	 * Retrieves the {@code Race} of the team leader.<br>
	 * This method calls {@code getLeader} to find the leader and returns their race.
	 * @return The {@link Race} of the team leader.
	 */
	@Override
	public Race getRace()
	{
		return getLeader().getObject().getRace();
	}
	
	/**
	 * Sends a network packet to all members of this team.<br>
	 * This method uses {@code applyOnMembers} to distribute the data.
	 * @param packet The {@code AionServerPacket} to be sent.
	 */
	@Override
	public void sendPacket(AionServerPacket packet)
	{
		applyOnMembers(new TeamMessageSender(packet, (Player p) -> true));
	}
	
	/**
	 * Sends a specific packet to all members of the team who meet a condition.<br>
	 * This method uses {@code applyOnMembers} to filter recipients.
	 * @param packet The {@code AionServerPacket} to be sent.
	 * @param predicate A {@code Predicate} used to check if a {@link Player} should receive the packet.
	 */
	@Override
	public void sendPacket(AionServerPacket packet, Predicate<Player> predicate)
	{
		applyOnMembers(new TeamMessageSender(packet, predicate));
	}
	
	/**
	 * Gets the count of members currently online.<br>
	 * This method calls {@code getOnlineMembers} and returns the size of the resulting collection.
	 * @return The number of online members as an {@code int}.
	 */
	@Override
	public int onlineMembers()
	{
		return getOnlineMembers().size();
	}
	
	/**
	 * Retrieves a list of all players currently online.<br>
	 * This method uses {@code ONLINE} to filter the members.
	 * @return A {@code Collection} of {@link Player} objects who are currently online.
	 */
	@Override
	public Collection<Player> getOnlineMembers()
	{
		return filterMembers(PlayerFilters.ONLINE);
	}
	
	/**
	 * Sets up the initial state of the team.<br>
	 * This method assigns the provided {@code leader} to the team.<br>
	 * It calls {@code setLeader} internally.
	 * @param leader The member who will lead the team.
	 */
	protected void initializeTeam(TM leader)
	{
		setLeader(leader);
	}
	
	/**
	 * Retrieves the current {@link LootGroupRules} for this team.<br>
	 * This provides the rules used to determine how loot is distributed.
	 * @return the {@code LootGroupRules} object.
	 */
	public LootGroupRules getLootGroupRules()
	{
		return lootGroupRules;
	}
	
	/**
	 * Sets the loot rules for this team.<br>
	 * This method updates the {@code lootGroupRules} field.<br>
	 * It also sends a system message if the rule is set to {@code FREEFORALL}.
	 * @param lootGroupRules The new {@code LootGroupRules} object to apply.
	 */
	public void setLootGroupRules(LootGroupRules lootGroupRules)
	{
		this.lootGroupRules = lootGroupRules;
		if ((lootGroupRules != null) && (lootGroupRules.getLootRule() == LootRuleType.FREEFORALL))
		{
			applyOnMembers(new TeamPacketGroupSender(PlayerFilters.HAS_LOOT_PET, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE03, new SM_PET(13, false)));
		}
	}
	
	public static final class TeamPacketGroupSender implements Predicate<Player>
	{
		private final AionServerPacket[] packets;
		private final Predicate<Player> predicate;
		
		public TeamPacketGroupSender(Predicate<Player> predicate, AionServerPacket... packets)
		{
			this.packets = packets;
			this.predicate = predicate;
		}
		
		@Override
		public boolean test(Player player)
		{
			if (predicate.test(player))
			{
				for (AionServerPacket packet : packets)
				{
					PacketSendUtility.sendPacket(player, packet);
				}
			}
			
			return true;
		}
	}
	
	public static final class TeamMessageSender implements Predicate<Player>
	{
		private final AionServerPacket packet;
		private final Predicate<Player> predicate;
		
		public TeamMessageSender(AionServerPacket packet, Predicate<Player> predicate)
		{
			this.packet = packet;
			this.predicate = predicate;
		}
		
		@Override
		public boolean test(Player player)
		{
			if (predicate.test(player))
			{
				PacketSendUtility.sendPacket(player, packet);
			}
			
			return true;
		}
	}
}
