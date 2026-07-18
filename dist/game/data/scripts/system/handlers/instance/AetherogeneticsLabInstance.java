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
package system.handlers.instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic and interactions for the {@code AetherogeneticsLab} instance.<br>
 * This class manages specific behaviors for players entering this unique area.
 * @author xTz
 */
@InstanceID(310050000)
public class AetherogeneticsLabInstance extends GeneralInstanceHandler
{
	/**
	 * Handles the logic when a {@link Player} dies in this instance.<br>
	 * It broadcasts an emotion and sends death packets to the client.
	 * @param player The {@code Player} object that has died.
	 * @param lastAttacker The {@code Creature} that dealt the final blow.
	 * @return {@code true} if the death was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onDie(Player player, Creature lastAttacker)
	{
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
	
	/**
	 * This method is called when a {@link Player} logs out of the instance.<br>
	 * It handles moving the player to the exit point.
	 * @param player The {@code Player} object who is logging out.
	 */
	@Override
	public void onPlayerLogOut(Player player)
	{
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
	
	/**
	 * This method is called when a {@link Player} leaves the instance.<br>
	 * It handles the teleportation logic to move the player to the exit point.<br>
	 * The {@code TeleportService2} class is used to perform this action.
	 * @param player The {@code Player} object that is exiting the instance.
	 */
	@Override
	public void onExitInstance(Player player)
	{
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}
