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

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This packet sends information about the members of a group to a client.<br>
 * It contains details for each {@link Player} within the {@link PlayerGroup}.
 * @author Lyahim, ATracer
 */
public class SM_GROUP_MEMBER_INFO extends AionServerPacket
{
	private final int groupId;
	private final Player player;
	private GroupEvent event;
	
	/**
	 * Creates a new {@code SM_GROUP_MEMBER_INFO} packet.<br>
	 * This packet contains information about a specific group member.<br>
	 * It is used to sync group data between the server and the client.
	 * @param group The {@link PlayerGroup} containing the members.
	 * @param player The {@link Player} who triggered or is involved in the event.
	 * @param event The {@link GroupEvent} that occurred within the group.
	 */
	public SM_GROUP_MEMBER_INFO(PlayerGroup group, Player player, GroupEvent event)
	{
		groupId = group.getTeamId();
		this.player = player;
		this.event = event;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final PlayerLifeStats pls = player.getLifeStats();
		final PlayerCommonData pcd = player.getCommonData();
		final WorldPosition wp = pcd.getPosition();
		
		if ((event == GroupEvent.ENTER) && !player.isOnline())
		{
			event = GroupEvent.ENTER_OFFLINE;
		}
		
		writeD(groupId);
		writeD(player.getObjectId());
		if (player.isOnline())
		{
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp()); // maxflighttime
			writeD(pls.getCurrentFp()); // currentflighttime
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		
		writeD(0); // unk 3.5
		writeD(wp.getMapId());
		writeD(wp.getMapId()); // TODO looks like some ObjId
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId()); // class id
		writeC(pcd.getGender().getGenderId()); // gender id
		writeC(pcd.getLevel()); // level
		
		writeC(event.getId()); // something events
		writeH(0); // TODO Old (player.isOnline() ? 1 : 0 / always 0 in 5.6
		writeH(player.isMentor() ? 0x01 : 0x00);
		
		switch (event)
		{
			case MOVEMENT: // 0x01
			case DISCONNECTED: // 0x03
				break;
			case LEAVE: // 0x00
				writeH(0x00); // unk
				writeC(0x00); // unk
				break;
			case ENTER_OFFLINE: // 0x07
			case JOIN: // 0x05
				writeS(pcd.getName()); // name
				break;
			case ENTER: // 0x13
			case UPDATE: // 0x13
				writeS(pcd.getName());
				writeD(0x00);
				writeD(0x00);
				writeC(0x7F); // = 127
				final List<Effect> abnormalEffects1 = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects1.size());
				if (abnormalEffects1.size() > 0)
				{
					for (Effect effect : abnormalEffects1)
					{
						writeD(effect.getEffectorId());
						writeH(effect.getSkillId());
						writeC(effect.getSkillLevel());
						writeC(effect.getTargetSlot());
						writeD(effect.getRemainingTime());
						writeH(0);
					}
				}
				
				writeB(new byte[32]);
				break;
			default: // 0x41, Todo find 0x09
				writeD(0x00); // unk
				writeD(0x00); // unk
				writeC(0x7F); // slots
				final List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects.size()); // Abnormal effects
				if (abnormalEffects.size() > 0)
				{
					for (Effect effect : abnormalEffects)
					{
						writeD(effect.getEffectorId()); // casterid
						writeH(effect.getSkillId()); // spellid
						writeC(effect.getSkillLevel()); // spell level
						writeC(effect.getTargetSlot()); // unk ?
						writeD(effect.getRemainingTime()); // estimatedtime
						writeH(0);
					}
				}
				
				writeB(new byte[32]);
				break;
		}
	}
}
