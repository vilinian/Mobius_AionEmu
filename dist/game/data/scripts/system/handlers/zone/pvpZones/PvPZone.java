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
package system.handlers.zone.pvpZones;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.SiegeZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.AdvencedZoneHandler;

/**
 * This class handles the logic for Player vs Player (PvP) zones within the game world.<br>
 * It provides a base implementation for managing zone-specific behaviors and rules.
 * @author MrPoke
 */
public abstract class PvPZone implements AdvencedZoneHandler
{
	/**
	 * This method is called when a {@code Creature} enters a specific {@link ZoneInstance}.<br>
	 * It handles any logic required for entering the area.
	 * @param player The {@code Creature} that entered the zone.
	 * @param zone The {@link ZoneInstance} being entered.
	 */
	@Override
	public void onEnterZone(Creature player, ZoneInstance zone)
	{
	}
	
	/**
	 * This method is called when a {@code Creature} leaves a specific {@link ZoneInstance}.<br>
	 * It handles any logic required to update the game state after the player exits.
	 * @param player The {@code Creature} that is leaving the zone.
	 * @param zone The {@link ZoneInstance} that was just exited.
	 */
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone)
	{
	}
	
	/**
	 * Handles the logic that occurs when a creature dies in a PvP zone.<br>
	 * This method checks if the target is a {@link Player}.<br>
	 * It triggers specific animations and messages based on the zone type.
	 * @param lastAttacker The creature that dealt the final blow.
	 * @param target The creature that died.
	 * @param zone The current {@link ZoneInstance} where the death occurred.
	 * @return {@code true} if the death was handled for a player, otherwise {@code false}.
	 */
	@Override
	public boolean onDie(Creature lastAttacker, Creature target, ZoneInstance zone)
	{
		if (!(target instanceof Player))
		{
			return false;
		}
		
		final Player player = (Player) target;
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		if (zone instanceof SiegeZoneInstance)
		{
			((SiegeZoneInstance) zone).doOnAllPlayers(p -> PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_PvPZONE_OUT_MESSAGE(player.getName())));
			
			ThreadPoolManager.getInstance().schedule(() ->
			{
				PlayerReviveService.duelRevive(player);
				doTeleport(player, zone.getZoneTemplate().getName());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PvPZONE_MY_DEATH_TO_B(lastAttacker.getName()));
			}, 5000);
		}
		
		return true;
	}
	
	protected abstract void doTeleport(Player player, ZoneName zoneName);
}
