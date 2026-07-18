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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownList;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.templates.InstanceCooltime;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Map;

/**
 * This packet handles the transmission of instance information to the client.<br>
 * It provides details about specific game instances, such as their status and cooldowns.
 * @author nrg
 */
public class SM_INSTANCE_INFO extends AionServerPacket
{
	private final Player player;
	private final boolean isAnswer;
	private final int cooldownId;
	private final int worldId;
	private final TemporaryPlayerTeam<?> playerTeam;
	
	/**
	 * Creates a new {@code SM_INSTANCE_INFO} packet for a specific player.<br>
	 * This constructor initializes the basic data needed for instance information.
	 * @param player The {@link Player} who will receive this packet.
	 * @param isAnswer A boolean flag indicating if this is an answer to a request.
	 * @param playerTeam The {@link TemporaryPlayerTeam} associated with the player.
	 */
	public SM_INSTANCE_INFO(Player player, boolean isAnswer, TemporaryPlayerTeam<?> playerTeam)
	{
		this.player = player;
		this.isAnswer = isAnswer;
		this.playerTeam = playerTeam;
		worldId = 0;
		cooldownId = 0;
	}
	
	/**
	 * Creates a new {@code SM_INSTANCE_INFO} packet for a specific player.<br>
	 * This constructor initializes the packet with information about a game instance.<br>
	 * It automatically sets the {@code isAnswer} flag to {@code false}.
	 * @param player The {@link Player} who will receive this packet.
	 * @param instanceId The unique identifier for the game instance.
	 */
	public SM_INSTANCE_INFO(Player player, int instanceId)
	{
		this.player = player;
		isAnswer = false;
		playerTeam = null;
		worldId = instanceId;
		cooldownId = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(instanceId) != null ? DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(instanceId).getId() : 0;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final boolean hasTeam = playerTeam != null;
		writeC(!isAnswer ? 2 : hasTeam ? 1 : 0);
		writeC(cooldownId);
		writeD(0);
		writeC(1);
		writeH(1);
		if (cooldownId == 0)
		{
			writeD(player.getObjectId());
			writeH(DataManager.INSTANCE_COOLTIME_DATA.size());
			final PortalCooldownList cooldownList = player.getPortalCooldownList();
			for (Map.Entry<Integer, InstanceCooltime> e : DataManager.INSTANCE_COOLTIME_DATA.getAllInstances().entrySet())
			{
				writeD(e.getValue().getId());
				writeD(0x0);
				if (cooldownList.getPortalCooldown(e.getValue().getWorldId()) == 0)
				{
					writeD(0x0);
				}
				else
				{
					writeD((int) (cooldownList.getPortalCooldown(e.getValue().getWorldId()) - System.currentTimeMillis()) / 1000);
				}
				
				writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(e.getKey()));
				writeD(cooldownList.getPortalCooldownItem(e.getValue().getWorldId()) != null ? cooldownList.getPortalCooldownItem(e.getValue().getWorldId()).getEntryCount() * -1 : 0);
				writeD(0); // 4.9
				writeD(0); // 4.9
				writeD(0); // Unk 5.1
				writeD(0); // Unk 6.x
				writeC(1); // activated
			}
			
			writeS(player.getName());
		}
		else
		{
			writeD(player.getObjectId());
			writeH(player.getPortalCooldownList().size()); // TEST
			for (int i = 0; i < player.getPortalCooldownList().size(); i++)
			{
				writeD(cooldownId); // InstanceID
				writeD(0);
				writeD(0);
				writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCountByWorldId(worldId)); // Max Entry's
				writeD(player.getPortalCooldownList().getPortalCooldownItem(worldId) != null ? player.getPortalCooldownList().getPortalCooldownItem(worldId).getEntryCount() * -1 : 0); // Used Entry's (negative -)
				writeD(0); // 4.9
				writeD(0); // 4.9
				writeD(0); // Unk 5.1
				writeD(0); // Unk 6.x
				writeC(1); // activated
			}
			
			writeS(player.getName());
		}
	}
}
