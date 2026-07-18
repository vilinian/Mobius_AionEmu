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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Handles the specific logic and behaviors for the {@code UdasTempleLowerInstance}.<br>
 * This class manages instance-specific events such as NPC interactions and player movements.<br>
 * It extends {@link GeneralInstanceHandler} to provide specialized functionality for this area.
 * @author Falke_34
 */
@InstanceID(300160000)
public class UdasTempleLowerInstance extends GeneralInstanceHandler
{
	/**
	 * This method is called when a new {@link WorldMapInstance} is created.<br>
	 * It initializes the local doors map from the provided instance.<br>
	 * It calls the superclass implementation of {@code onInstanceCreate}.
	 * @param instance The {@code WorldMapInstance} being created.
	 */
	@Override
	public void onInstanceCreate(WorldMapInstance instance)
	{
		super.onInstanceCreate(instance);
	}
	
	/**
	 * Handles the logic that occurs when an {@link Npc} dies.<br>
	 * This method identifies the player who dealt the most damage to the NPC.<br>
	 * It triggers specific messages or spawns based on the unique ID of the dead NPC.
	 * @param npc The {@link Npc} object that has died.
	 */
	@Override
	public void onDie(Npc npc)
	{
		switch (npc.getNpcId())
		{
			case 653581: // Zhanim the Librarian - Quest Instance
				spawn(730229, 744.0f, 885.0f, 153.0f, (byte) 0); // Traveller's Bag
				break;
			case 653459: // Chura Twinblade
				spawn(837099, 1255.0344f, 994.6545f, 103.74671f, (byte) 0); // Tusnian
				break;
			case 653470: // Debilkarim the Maker
				spawn(836792, 565.91876f, 1316.0468f, 187.98033f, (byte) 0); // Exit
				break;
		}
	}
	
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
}
