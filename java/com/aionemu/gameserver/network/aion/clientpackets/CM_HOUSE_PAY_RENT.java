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

import java.sql.Timestamp;

import java.time.ZonedDateTime;

import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.MaintenanceTask;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_PAY_RENT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the client request to pay for house rent.<br>
 * This packet processes the payment logic and triggers a {@link SM_HOUSE_PAY_RENT} response.<br>
 * It interacts with the {@link House} model to update maintenance status.
 * @author Rolandas
 */
public class CM_HOUSE_PAY_RENT extends AionClientPacket
{
	int weekCount;
	
	/**
	 * Handles the client request to pay house rent.<br>
	 * This packet processes the payment for a specific house.<br>
	 * It uses the {@code opcode} to identify the action type.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the player.
	 * @param restStates Additional states associated with the connection.
	 */
	public CM_HOUSE_PAY_RENT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		weekCount = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		if (!HousingConfig.ENABLE_HOUSE_PAY)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_F2P_CASH_HOUSE_FEE_FREE);
			return;
		}
		
		final House house = player.getActiveHouse();
		final long toPay = house.getLand().getMaintenanceFee() * weekCount;
		if (toPay <= 0)
		{
			return;
		}
		
		if (player.getInventory().getKinah() < toPay)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
			return;
		}
		
		long payTime = house.getNextPay() != null ? house.getNextPay().getTime() : (long) MaintenanceTask.getInstance().getRunTime() * 1000;
		int counter = weekCount;
		while ((--counter) >= 0)
		{
			payTime += MaintenanceTask.getInstance().getPeriod();
		}
		
		final ZonedDateTime nextRun = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli((long) MaintenanceTask.getInstance().getRunTime() * 1000), java.time.ZoneId.systemDefault());
		if (nextRun.plusWeeks(4).toInstant().toEpochMilli() < payTime)
		{
			// client cap
			return;
		}
		
		player.getInventory().decreaseKinah(toPay);
		house.setNextPay(new Timestamp(payTime));
		house.setFeePaid(true);
		house.save();
		PacketSendUtility.sendPacket(player, new SM_HOUSE_PAY_RENT(weekCount));
	}
}
