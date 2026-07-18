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
package com.aionemu.gameserver.model.team2.alliance.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.function.Predicate;

/**
 * This event handles the modification of loot rules for a specific alliance.<br>
 * It allows the system to update {@link LootGroupRules} when changes occur.<br>
 * It also provides a way to check if a {@link Player} meets certain criteria.
 * @author ATracer
 */
public class ChangeAllianceLootRulesEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
	private final PlayerAlliance alliance;
	private final LootGroupRules lootGroupRules;
	
	/**
	 * Creates a new event to update the loot rules for a specific alliance.<br>
	 * This event links a {@link PlayerAlliance} with its new {@link LootGroupRules}.
	 * @param alliance The {@code PlayerAlliance} that will receive the updated rules.
	 * @param lootGroupRules The new {@code LootGroupRules} to be applied to the alliance.
	 */
	public ChangeAllianceLootRulesEvent(PlayerAlliance alliance, LootGroupRules lootGroupRules)
	{
		this.alliance = alliance;
		this.lootGroupRules = lootGroupRules;
	}
	
	/**
	 * Updates the loot rules for an alliance.<br>
	 * This method sets the new {@code LootGroupRules} for the {@link PlayerAlliance}.<br>
	 * It then applies this change to all members of the alliance.
	 */
	@Override
	public void handleEvent()
	{
		alliance.setLootGroupRules(lootGroupRules);
		alliance.applyOnMembers(this);
	}
	
	/**
	 * Applies the alliance loot rules to a specific player.<br>
	 * This method sends an {@link SM_ALLIANCE_INFO} packet to the member.
	 * @param member The {@code Player} who will receive the update.
	 * @return Always returns {@code true}.
	 */
	@Override
	public boolean test(Player member)
	{
		PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance));
		return true;
	}
}
