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

import java.util.List;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * Handles the client request to change a player's current subzone.<br>
 * This packet is used to transition characters between different areas within a zone.<br>
 * It interacts with {@link ZoneInstance} to manage spatial movement.
 * @author Rolandas
 */
public class CM_SUBZONE_CHANGE extends AionClientPacket
{
	private int unk;
	
	/**
	 * Handles the transition of a player between subzones.<br>
	 * This packet updates the current zone status for the client.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} associated with the change.
	 * @param restStates A variable number of additional {@link State} objects.
	 */
	public CM_SUBZONE_CHANGE(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		// Always 1, maybe for neutral zones 0 ?
		unk = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		player.revalidateZones();
		if ((player.getAccessLevel() >= 3) && WorldConfig.ENABLE_SHOW_ZONEENTER)
		{
			final List<ZoneInstance> zones = player.getPosition().getMapRegion().getZones(player);
			int foundZones = 0;
			for (ZoneInstance zone : zones)
			{
				if ((zone.getZoneTemplate().getZoneType() == ZoneClassName.DUMMY) || (zone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER))
				{
					continue;
				}
				
				foundZones++;
				PacketSendUtility.sendMessage(player, "Passed zone: unk=" + unk + "; " + zone.getZoneTemplate().getZoneType() + " " + zone.getAreaTemplate().getZoneName().name());
			}
			
			if (foundZones == 0)
			{
				PacketSendUtility.sendMessage(player, "Passed unknown zone, unk=" + unk);
			}
		}
	}
}
