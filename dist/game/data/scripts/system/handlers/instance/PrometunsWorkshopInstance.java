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

import java.util.Map;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Handles the logic for the Prometun's Workshop instance.<br>
 * This class manages specific behaviors and interactions within this unique map area.
 */
@InstanceID(302430000)
public class PrometunsWorkshopInstance extends GeneralInstanceHandler
{
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	private int startRimOreGrinder;
	
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
		doors = instance.getDoors();
	}
	
	/**
	 * This method is called when a {@link Player} enters the instance.<br>
	 * It initializes the race data if it is currently {@code null}.<br>
	 * It triggers the {@code SpawnHaramelRace} method to set up the environment.
	 * @param player The {@link Player} object who entered the instance.
	 */
	@Override
	public void onEnterInstance(Player player)
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
		switch (npc.getObjectTemplate().getTemplateId())
		{
			case 654728: // Drakan Legate of the 39th Legion
				doors.get(179).setOpen(true);
				sendMsgByRace(1404585, Race.PC_ALL, 0);
				break;
			case 650016: // Yarkhan
				doors.get(206).setOpen(true);
				break;
			case 655210: // Drakan Prefect of the 39th Legion
				doors.get(177).setOpen(true);
				break;
			case 655277: // Rim Ore Grinder
			case 655279: // Rim Ore Grinder
				startRimOreGrinder++;
				if (startRimOreGrinder == 2)
				{
					doors.get(235).setOpen(true);
					sendMsgByRace(1404592, Race.PC_ALL, 0);
				}
				break;
			case 650018: // Suffering Prometun
				doors.get(92).setOpen(true);
				spawn(655269, 797.97754f, 872.6895f, 694.6929f, (byte) 90); // Workshop Treasure Chest
				
				ThreadPoolManager.getInstance().schedule(() ->
				{
					deleteNpc(650042); // TODO
				}, 5000);
				break;
			case 655211: // Drakan Strategist of the 39th Legion
				doors.get(183).setOpen(true);
				sendMsgByRace(1404589, Race.PC_ALL, 0);
				break;
			case 650021: // Tarukkan
				ThreadPoolManager.getInstance().schedule(() ->
				{
					deleteNpc(837097); // TODO
				}, 5000);
				break;
			case 650025: // Prigga
				doors.get(381).setOpen(true);
				break;
		}
	}
	
	// When message 1404590 is received for door ID 233, a 3-minute timer starts; after this time, message 1404732 destroys the rim ore grinder, door ID 235 opens, and treasure chest message 1404591 causes the chest to vanish.
	
	// TODO: Jotun and the Bridge and the Rim Furnace.
	
	/**
	 * Sends a system message to players based on their race.<br>
	 * This method uses {@link ThreadPoolManager} to delay the delivery of the message.<br>
	 * It checks if the player's race matches the provided {@code Race} or is set to {@code Race.PC_ALL}.
	 * @param msg The unique identifier for the system message to send.
	 * @param race The specific {@code Race} that should receive the message.
	 * @param time The delay in milliseconds before sending the message.
	 */
	protected void sendMsgByRace(int msg, Race race, int time)
	{
		ThreadPoolManager.getInstance().schedule(() -> instance.doOnAllPlayers(player ->
		{
			if (player.getRace().equals(race) || race.equals(Race.PC_ALL))
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
			}
			
		}), time);
	}
	
	/**
	 * Removes an {@link Npc} from the instance.<br>
	 * This method checks if the NPC exists before calling its delete logic.
	 * @param npcId The unique identifier of the NPC to remove.
	 */
	private void deleteNpc(int npcId)
	{
		if (getNpc(npcId) != null)
		{
			getNpc(npcId).getController().onDelete();
		}
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
	 * Handles the logic when a {@link Player} leaves this instance.<br>
	 * This method is called to clean up any specific data for the player.
	 * @param player The {@code Player} object who is leaving the instance.
	 */
	@Override
	public void onLeaveInstance(Player player)
	{
	}
	
	/**
	 * This method is called when the instance is being destroyed.<br>
	 * It sets the {@code isInstanceDestroyed} flag to {@code true}.<br>
	 * It also clears all entries from the {@code doors} map.
	 */
	@Override
	public void onInstanceDestroy()
	{
		isInstanceDestroyed = true;
		doors.clear();
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
}
