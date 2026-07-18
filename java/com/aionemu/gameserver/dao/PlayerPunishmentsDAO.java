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
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;

/**
 * This class provides data access methods for managing player punishments.<br>
 * It handles operations related to {@link CharacterBanInfo} and various {@link PunishmentType} records.
 * @author lord_rex
 */
public abstract class PlayerPunishmentsDAO implements DAO
{
	/**
	 * Returns the name of the current class.<br>
	 * This is useful for identifying the {@code DAO} type in logs or configurations.
	 * @return The full name of the class as a {@code String}.
	 */
	@Override
	public String getClassName()
	{
		return PlayerPunishmentsDAO.class.getName();
	}
	
	public abstract void loadPlayerPunishments(Player player, PunishmentType punishmentType);
	
	public abstract void storePlayerPunishments(Player player, PunishmentType punishmentType);
	
	public abstract void punishPlayer(int playerId, PunishmentType punishmentType, long expireTime, String reason);
	
	public abstract void punishPlayer(Player player, PunishmentType punishmentType, String reason);
	
	public abstract void unpunishPlayer(int playerId, PunishmentType punishmentType);
	
	public abstract CharacterBanInfo getCharBanInfo(int playerId);
}
