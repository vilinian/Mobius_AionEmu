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
package com.aionemu.gameserver.utils.audit;

import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PunishmentService;

/**
 * This class handles the automatic banning of players based on specific audit criteria.<br>
 * It interacts with {@link PunishmentService} to apply restrictions to a {@code Player}.
 * @author synchro2
 */
public class AutoBan
{
	/**
	 * Applies a punishment to a specific {@link Player}.<br>
	 * This method handles different ban types based on the {@code PunishmentConfig}.<br>
	 * It uses the provided message to create a reason for the action.
	 * @param player The {@code Player} object to be punished.
	 * @param message The custom message describing why the punishment is occurring.
	 */
	protected static void punishment(Player player, String message)
	{
		final String reason = "AUTO " + message;
		final String address = player.getClientConnection().getMacAddress();
		final String accountIp = player.getClientConnection().getIP();
		final int accountId = player.getClientConnection().getAccount().getId();
		final int playerId = player.getObjectId();
		final int time = PunishmentConfig.PUNISHMENT_TIME;
		final int minInDay = 1440;
		final int dayCount = (int) (Math.floor(time / minInDay));
		
		switch (PunishmentConfig.PUNISHMENT_TYPE)
		{
			case 1:
				player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
				break;
			case 2:
				PunishmentService.banChar(playerId, dayCount, reason);
				break;
			case 3:
				LoginServer.getInstance().sendBanPacket((byte) 1, accountId, accountIp, time, 0);
				break;
			case 4:
				LoginServer.getInstance().sendBanPacket((byte) 2, accountId, accountIp, time, 0);
				break;
			case 5:
				player.getClientConnection().closeNow();
				BannedMacManager.getInstance().banAddress(address, System.currentTimeMillis() + (time * 60000), reason);
				break;
		}
	}
}
