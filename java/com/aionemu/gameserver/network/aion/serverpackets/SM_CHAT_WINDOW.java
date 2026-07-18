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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the updates for the player's chat window.<br>
 * It synchronizes chat messages and other relevant information to the client.
 * @author ginho1
 * @edit Cheatkiller
 */
public class SM_CHAT_WINDOW extends AionServerPacket
{
	private final Player target;
	private final boolean isGroup;
	
	/**
	 * Creates a new chat window packet.<br>
	 * This packet handles messages sent to specific players or groups.
	 * @param target The {@link Player} who will receive the message.
	 * @param isGroup Set to {@code true} if the message is for a group, otherwise {@code false}.
	 */
	public SM_CHAT_WINDOW(Player target, boolean isGroup)
	{
		this.target = target;
		this.isGroup = isGroup;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (target == null)
		{
			return;
		}
		
		if (isGroup)
		{
			if (target.isInGroup2())
			{
				writeC(2); // group
				writeS(target.getName());
				
				final PlayerGroup group = target.getPlayerGroup2();
				
				writeD(group.getTeamId());
				writeS(group.getLeader().getName());
				
				final Collection<Player> members = group.getMembers();
				for (Player groupMember : members)
				{
					writeC(groupMember.getLevel());
				}
				
				for (int i = group.size(); i < 6; i++)
				{
					writeC(0);
				}
				
				for (Player groupMember : members)
				{
					writeC(groupMember.getPlayerClass().getClassId());
				}
				
				for (int i = group.size(); i < 6; i++)
				{
					writeC(0);
				}
			}
			else if (target.isInAlliance2())
			{
				writeC(2); // alliance
				writeS(target.getName());
				
				final PlayerAlliance alliance = target.getPlayerAlliance2();
				
				writeD(alliance.getTeamId());
				writeS(alliance.getLeader().getName());
				
				final Collection<Player> members = alliance.getMembers();
				for (Player groupMember : members)
				{
					writeC(groupMember.getLevel());
				}
				
				for (int i = alliance.size(); i < 24; i++)
				{
					writeC(0);
				}
				
				for (Player groupMember : members)
				{
					writeC(groupMember.getPlayerClass().getClassId());
				}
				
				for (int i = alliance.size(); i < 24; i++)
				{
					writeC(0);
				}
			}
			else
			{
				writeC(4); // no group
				writeS(target.getName());
				writeD(0); // no group yet
				writeC(target.getPlayerClass().getClassId());
				writeC(target.getLevel());
				writeC(0); // unk
			}
		}
		else
		{
			writeC(1);
			writeS(target.getName());
			writeS(target.getLegion() != null ? target.getLegion().getLegionName() : "");
			writeC(target.getLevel());
			writeH(target.getPlayerClass().getClassId());
			writeS(target.getCommonData().getNote());
			writeD(1);
		}
	}
}
