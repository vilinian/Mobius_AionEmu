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
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the specific logic and events for the Haramel instance.<br>
 * This class extends {@link GeneralInstanceHandler} to manage unique behaviors within this area.
 * @author Falke_34
 */
@InstanceID(300200000)
public class HaramelInstance extends GeneralInstanceHandler
{
	private Race spawnRace;
	
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
		
		if (spawnRace == null)
		{
			spawnRace = player.getRace();
			SpawnHaramelRace();
		}
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
		final Player player = npc.getAggroList().getMostPlayerDamage();
		if (player == null)
		{
			return;
		}
		
		switch (npc.getNpcId())
		{
			case 653196: // Drudgelord Kakiti
				sendMsgByRace(1500094, Race.PC_ALL, 0);
				break;
			case 653205: // MuMu Ham the Grey
				sendMsgByRace(1500096, Race.PC_ALL, 0);
				break;
			case 653213: // Overseer Nukiti
				sendMsgByRace(1500098, Race.PC_ALL, 0);
				break;
			case 653218: // Hamerun the Bleeder
				sendMsg(1500099);
				sendMsg(1400713); // Hamerun has dropped a treasure chest.
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 457));
				
				// TODO - Treasure Chest only tested with Priest
				spawn(700829, 224.1367f, 268.60825f, 144.89798f, (byte) 90); // Antique Treasure Chest
				spawn(749306, 224.36278f, 261.913f, 144.89798f, (byte) 30); // Haramel Exit
				break;
		}
	}
	
	/**
	 * This method handles the spawning of specific NPCs for the Haramel instance.<br>
	 * It checks the {@code spawnRace} variable to determine which models to use.<br>
	 * The logic places two different entities at predefined coordinates based on whether the race is {@code ASMODIANS}.
	 */
	private void SpawnHaramelRace()
	{
		final int Cheska_Royer1 = spawnRace == Race.ASMODIANS ? 799995 : 799994;
		final int Cheska_Royer2 = spawnRace == Race.ASMODIANS ? 806883 : 820133;
		spawn(Cheska_Royer1, 221.85893f, 351.66858f, 141.01141f, (byte) 30);
		spawn(Cheska_Royer2, 141.7932f, 22.274172f, 144.2455f, (byte) 0);
	}
	
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
