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

import java.nio.charset.Charset;

import com.aionemu.gameserver.controllers.HouseController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HousePermissions;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to modify house settings.<br>
 * This packet allows a {@link Player} to update configurations for their {@link House}.<br>
 * It interacts with the {@link HousingService} to process these changes.
 * @author Rolandas
 */
public class CM_HOUSE_SETTINGS extends AionClientPacket
{
	int doorState;
	int displayOwner;
	String signNotice;
	
	/**
	 * Creates a new {@link CM_HOUSE_SETTINGS} packet.<br>
	 * This constructor initializes the packet with specific states.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_HOUSE_SETTINGS(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		doorState = readC();
		displayOwner = readC();
		signNotice = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		House house = HousingService.getInstance().getPlayerStudio(player.getObjectId());
		if (house == null)
		{
			final int address = HousingService.getInstance().getPlayerAddress(player.getObjectId());
			house = HousingService.getInstance().getHouseByAddress(address);
		}
		
		final HousePermissions doorPermission = HousePermissions.getPacketDoorState(doorState);
		house.setDoorState(doorPermission);
		house.setNoticeState(HousePermissions.getNoticeState(displayOwner));
		house.setSignNotice(signNotice.getBytes(Charset.forName("UTF-16LE")));
		final HouseController controller = house.getController();
		controller.updateAppearance();
		controller.broadcastAppearance();
		
		if (doorPermission == HousePermissions.DOOR_OPENED_ALL)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OPEN_DOOR);
		}
		else if (doorPermission == HousePermissions.DOOR_OPENED_FRIENDS)
		{
			house.getController().kickVisitors(player, false, true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_CLOSE_DOOR_WITHOUT_FRIENDS);
		}
		else if (doorPermission == HousePermissions.DOOR_CLOSED)
		{
			house.getController().kickVisitors(player, true, true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_CLOSE_DOOR_ALL);
		}
	}
}
