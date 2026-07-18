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
package com.aionemu.gameserver.model.instance.packetfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.RunatoriumReward;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Handles the processing of network packets related to the {@link com.aionemu.gameserver.model.instance.instancereward.RunatoriumReward} system.<br>
 * It manages communication between the server and clients for instance scoring and rewards.<br>
 * This class ensures that all game actions within a Runatorium instance are correctly synchronized with the client.
 * @author GiGatR00n v4.7.5.x
 */
public class RunatoriumPacketsHandler
{
	private final List<Future<?>> idgelTask = new ArrayList<>();
	
	private final WorldMapInstance instance;
	private final RunatoriumReward rr;
	
	private final Integer mapId;
	private final int instanceId;
	
	/**
	 * Determines whether the instance is destroying? it used to avoids spawn of NPCs on InstanceDestroy Event
	 */
	private boolean isInstanceDestroyed = false;
	
	/**
	 * Initializes a new handler for Runatorium packets.<br>
	 * This constructor sets up the required map and reward data.
	 * @param mapId The unique identifier for the map.
	 * @param instanceId The unique identifier for the specific instance.
	 * @param instance The {@link WorldMapInstance} where the event occurs.
	 * @param rr The {@link RunatoriumReward} associated with this instance.
	 */
	public RunatoriumPacketsHandler(Integer mapId, int instanceId, WorldMapInstance instance, RunatoriumReward rr)
	{
		this.mapId = mapId;
		this.instanceId = instanceId;
		this.instance = instance;
		this.rr = rr;
	}
	
	/**
	 * Sends the initial score packets to all players in the instance.<br>
	 * This method updates the remaining time and player information.<br>
	 * It uses {@code sendPacket} to deliver {@code SM_INSTANCE_SCORE} data.
	 * @param player The {@code Player} object used to identify the current context.
	 */
	public void sendPreparingPacket(Player player)
	{
		instance.doOnAllPlayers(GroupMember ->
		{
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(6, rr.getRemainingTime(), rr, instance.getPlayersInside()));
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, rr.getRemainingTime(), rr, player.getObjectId(), 0, player.getRace().getRaceId()));
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, rr.getRemainingTime(), rr, instance.getPlayersInside()));
		});
	}
	
	/**
	 * Sends the score type packet to all players in the instance.<br>
	 * This method uses {@code sendPacket} to deliver information.<br>
	 * It updates both individual and group scores based on the {@code RunatoriumReward}.
	 */
	public void sendScoreTypePacket()
	{
		instance.doOnAllPlayers(player ->
		{
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(2, rr.getRemainingTime(), rr, player.getObjectId()));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, rr.getRemainingTime(), rr, instance.getPlayersInside()));
		});
	}
	
	/**
	 * Sends the reward information to all players in the instance.<br>
	 * This method uses {@link PacketSendUtility} to deliver multiple {@code SM_INSTANCE_SCORE} packets.<br>
	 * It updates the remaining time and player counts from the {@code RunatoriumReward} object.
	 */
	public void sendRewardPacket()
	{
		instance.doOnAllPlayers(player ->
		{
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, rr.getRemainingTime(), rr, instance.getPlayersInside()));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, rr.getRemainingTime(), rr, instance.getPlayersInside()));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(5, rr.getRemainingTime(), rr, player.getObjectId()));
		});
	}
	
	/**
	 * Sends the current instance score information to all players.<br>
	 * This method updates the remaining time and player count for everyone inside.<br>
	 * It uses {@code sendPacket} to deliver {@code SM_INSTANCE_SCORE} packets.
	 */
	public void sendInstanceInfoPacket()
	{
		instance.doOnAllPlayers(player ->
		{
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(6, rr.getRemainingTime(), rr, instance.getPlayersInside()));
			PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(7, rr.getRemainingTime(), rr, instance.getPlayersInside()));
		});
	}
	
	/**
	 * Sends the instance score packet to all players in the group.<br>
	 * This method uses {@code sendPacket} to deliver the data.<br>
	 * It includes information from the {@code RunatoriumReward} object.
	 * @param player The {@code Player} who triggered the action.
	 */
	public void sendNpcScorePacket(Player player)
	{
		instance.doOnAllPlayers(GroupMember -> PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(11, rr.getRemainingTime(), rr, player.getObjectId())));
	}
	
	/**
	 * Sends a notification to all players when a specific player leaves.<br>
	 * This updates the {@link SM_INSTANCE_SCORE} for everyone in the instance.
	 * @param player The {@code Player} who is leaving the instance.
	 */
	public void sendPlayerLeavePacket(Player player)
	{
		instance.doOnAllPlayers(GroupMember -> PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(8, rr.getRemainingTime(), rr, player.getObjectId())));
	}
	
	/**
	 * Sends a death notification packet to all players in the instance.<br>
	 * This method updates the {@link com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE} for everyone.<br>
	 * It uses the data from the provided {@code player} object to update scores.
	 * @param player The {@link Player} who died.
	 */
	public void sendPlayerDiePacket(Player player)
	{
		instance.doOnAllPlayers(GroupMember ->
		{
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, rr.getRemainingTime(), rr, player.getObjectId(), 60, player.getRace().getRaceId()));
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(3, rr.getRemainingTime(), rr, player.getObjectId(), 60, player.getRace().getRaceId()));
			PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(7, rr.getRemainingTime(), rr, instance.getPlayersInside()));
		});
	}
	
	/**
	 * Sends a revival notification to all players in the instance.<br>
	 * This updates the {@code SM_INSTANCE_SCORE} for everyone.
	 * @param player The {@code Player} who was revived.
	 */
	public void sendPlayerRevivedPacket(Player player)
	{
		instance.doOnAllPlayers(GroupMember -> PacketSendUtility.sendPacket(GroupMember, new SM_INSTANCE_SCORE(4, rr.getRemainingTime(), rr, player.getObjectId(), 0, player.getRace().getRaceId())));
	}
	
	/**
	 * Initializes and schedules all timed events for the instance.<br>
	 * This method sets the start time and triggers environmental spawns.<br>
	 * It uses {@link ThreadPoolManager} to schedule NPC spawns and messages.<br>
	 * It also starts the periodic spawning of Unstable Ide Energy.
	 */
	public void startInstanceTask()
	{
		rr.setInstanceStartTime();
		
		SpawnShallowWater();
		
		/* 1:37 Minutes Preparing for Battle (NA Retail v4.7.5.15) */
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			SpawnFlameVents();
			openFirstDoors();
			rr.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
			sendScoreTypePacket(); // Send 1-Time when Start_Progress and End_Progress
		}, rr.getPreparingTime()));
		
		/* after 10-Minutes Spawns 6x Intelligence Supply Box (NA Retail v4.7.5.15) */
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			sendInstanceInfoPacket();
			/* Intelligence Supply Box (6x) */
			sp(702581, 257.0131f, 265.5847f, 85.81963f, (byte) 30, 0);
			sp(702581, 253.07826f, 246.24838f, 92.942505f, (byte) 75, 0);
			sp(702582, 216.00116f, 210.73024f, 79.86218f, (byte) 105, 0);
			sp(702582, 272.0522f, 251.8284f, 85.81963f, (byte) 90, 0);
			sp(702583, 313.1777f, 307.97986f, 79.86218f, (byte) 45, 0);
			sp(702583, 276.37427f, 271.68332f, 92.942535f, (byte) 15, 0);
		}, 600000 + rr.getPreparingTime()));
		
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			sendInstanceInfoPacket();
			sendMsgByRace(1402367, Race.PC_ALL, 0); // Destroyer Kunax has appeared in the Slaying Arena.
			sp(234190, 264.4382f, 258.58527f, 85.81963f, (byte) 30, 0); // .....Destroyer Kunax.
			sp(234751, 273.76373f, 258.95422f, 85.81963f, (byte) 15, 0); // ....Sheban Elite Stalwart.
			sp(234751, 250.63719f, 244.0336f, 92.942505f, (byte) 75, 0); // ....Sheban Elite Stalwart.
			sp(234753, 256.33893f, 259.0221f, 85.81963f, (byte) 75, 0); // .....Sheban Elite Marauder.
			sp(234753, 278.2619f, 273.78964f, 92.942535f, (byte) 15, 0); // ....Sheban Elite Marauder.
			sp(234752, 264.9393f, 267.92413f, 85.81963f, (byte) 45, 0); // .....Sheban Elite Sniper.
			sp(234754, 264.97153f, 249.82945f, 85.81963f, (byte) 105, 0); // ...Sheban Elite Medic.
		}, 600000 + rr.getPreparingTime()));
		
		/* Unstable Ide Energy */
		idgelTask.add(ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> SpawnUnstableIdeEnergies(), 20000, 45000));
	}
	
	/**
	 * Spawns the shallow water visual effects in the instance.<br>
	 * This method calls {@code float, float, float, byte, int)} to place a specific object.
	 */
	protected void SpawnShallowWater()
	{
		sp(855009, 264.4382f, 258.58527f, 85.81963f, (byte) 38, 0);
	}
	
	/**
	 * Spawns the flame vent objects in the instance.<br>
	 * This method calls {@code float, float, float, byte, int)} to place specific vents.<br>
	 * It handles both Elyos and Asmodian side variants.
	 */
	protected void SpawnFlameVents()
	{
		sp(802192, 199.18739f, 191.76154f, 80.602165f, (byte) 15, 0); // ...Flame Vent Elyos
		sp(802549, 329.79938f, 326.1139f, 80.60217f, (byte) 75, 0); // .....Flame Vent Asmodian
	}
	
	/**
	 * Spawns unstable energy effects and NPCs within the instance.<br>
	 * It uses {@code float, float, float, byte, int)} to place objects at specific coordinates.<br>
	 * Some positions are randomized using {@code Rnd.get} for variety.
	 */
	protected void SpawnUnstableIdeEnergies()
	{
		/*
		 * Get Random Position (Elyos - Asmo)
		 */
		final float Rx = Rnd.get(-2, 2);
		final float Ry = Rnd.get(-2, 2);
		
		/* Searching for random player */
		sp(855008, 280.00867f, 238.88274f, 85.375f, (byte) 99, 0);
		sp(855008, 294.65027f, 274.3337f, 90.36505f, (byte) 98, 1500);
		sp(855008, 209.18584f, 256.8443f, 85.17249f, (byte) 104, 2000);
		sp(855008, 272.3378f, 292.4274f, 89.31072f, (byte) 0, 2500);
		sp(855008, 266.35608f, 278.44217f, 85.34033f, (byte) 0, 3000);
		sp(855008, 305.10504f, 311.90793f, 79.86219f, (byte) 0, 3500);
		
		/* Environment Factors - Static Points but in Random Time */
		sp(855012, 218.77754f + Rx, 210.01488f + Ry, 79.86219f, (byte) 73, Rnd.get(1000, 20000));
		sp(855012, 201.71503f + Rx, 194.91075f + Ry, 79.86219f, (byte) 74, Rnd.get(1000, 20000));
		sp(855012, 207.26093f + Rx, 228.40112f + Ry, 79.975296f, (byte) 33, Rnd.get(1000, 20000));
		sp(855012, 199.48077f + Rx, 244.93388f + Ry, 82.53585f, (byte) 30, Rnd.get(1000, 20000));
		sp(855012, 212.02956f + Rx, 245.16733f + Ry, 82.635704f, (byte) 58, Rnd.get(1000, 20000));
		sp(855012, 204.86206f + Rx, 259.98297f + Ry, 85.17249f, (byte) 119, Rnd.get(1000, 20000));
		sp(855012, 228.49147f + Rx, 259.14127f + Ry, 89.225716f, (byte) 119, Rnd.get(1000, 20000));
		sp(855012, 240.44017f + Rx, 234.15927f + Ry, 92.97302f, (byte) 74, Rnd.get(1000, 20000));
		sp(855012, 253.71873f + Rx, 247.44237f + Ry, 92.94253f, (byte) 15, Rnd.get(1000, 20000));
		sp(855012, 264.46152f + Rx, 223.30254f + Ry, 89.31f, (byte) 114, Rnd.get(1000, 20000));
		sp(855012, 290.363f + Rx, 234.34332f + Ry, 89.3f, (byte) 10, Rnd.get(1000, 20000));
		sp(855012, 299.15704f + Rx, 258.41733f + Ry, 89.27f, (byte) 6, Rnd.get(1000, 20000));
		sp(855012, 288.68448f + Rx, 283.96835f + Ry, 92.971634f, (byte) 15, Rnd.get(1000, 20000));
		sp(855012, 275.99603f + Rx, 270.95178f + Ry, 92.94253f, (byte) 72, Rnd.get(1000, 20000));
		sp(855012, 264.91425f + Rx, 294.6127f + Ry, 89.29f, (byte) 52, Rnd.get(1000, 20000));
		sp(855012, 240.26009f + Rx, 284.79178f + Ry, 89.3f, (byte) 70, Rnd.get(1000, 20000));
		sp(855012, 322.18576f + Rx, 258.4865f + Ry, 85.17249f, (byte) 119, Rnd.get(1000, 20000));
		sp(855012, 317.50922f + Rx, 274.21152f + Ry, 82.1668f, (byte) 90, Rnd.get(1000, 20000));
		sp(855012, 329.54337f + Rx, 274.05835f + Ry, 82.232315f, (byte) 90, Rnd.get(1000, 20000));
		sp(855012, 322.64337f + Rx, 290.91113f + Ry, 79.902466f, (byte) 90, Rnd.get(1000, 20000));
		sp(855012, 309.56665f + Rx, 307.76706f + Ry, 79.86219f, (byte) 15, Rnd.get(1000, 20000));
		sp(855012, 326.81918f + Rx, 323.3281f + Ry, 79.86219f, (byte) 13, Rnd.get(1000, 20000));
	}
	
	/**
	 * Opens the initial set of doors for the instance.<br>
	 * This method calls {@code openDoor} for specific door IDs.
	 */
	protected void openFirstDoors()
	{
		openDoor(1);
		openDoor(99);
	}
	
	/**
	 * Opens a specific door in the current instance.<br>
	 * This method retrieves a {@code StaticDoor} using the provided ID.<br>
	 * If the door exists, it sets its state to open.
	 * @param doorId The unique identifier of the door to be opened.
	 */
	protected void openDoor(int doorId)
	{
		final StaticDoor door = instance.getDoors().get(doorId);
		if (door != null)
		{
			door.setOpen(true);
		}
	}
	
	/**
	 * Sets the instance destruction status to {@code true}.<br>
	 * Cancels all active tasks stored in the {@code idgelTask} list.<br>
	 * This method ensures that no new NPCs are spawned during instance destruction.
	 */
	public void ClearTasks()
	{
		isInstanceDestroyed = true;
		
		for (Future<?> n : idgelTask)
		{
			if (n != null)
			{
				n.cancel(true);
			}
		}
	}
	
	/**
	 * Spawns a specific NPC at the given coordinates.<br>
	 * This method handles the positioning and timing of the entity.
	 * @param npcId The unique identifier for the {@code Npc}.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @param h The horizontal rotation value.
	 * @param time The delay before the NPC appears.
	 */
	protected void sp(int npcId, float x, float y, float z, byte h, int time)
	{
		sp(npcId, x, y, z, h, 0, time, 0, null);
	}
	
	/**
	 * Spawns an NPC at a specific location with custom properties.<br>
	 * This method handles the positioning and data for the new entity.
	 * @param npcId The unique identifier of the {@code Npc}.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @param h The height offset for the NPC.
	 * @param time The delay before the NPC appears.
	 * @param msg A specific message identifier to send.
	 * @param race The {@link Race} type of the spawned entity.
	 */
	protected void sp(int npcId, float x, float y, float z, byte h, int time, int msg, Race race)
	{
		sp(npcId, x, y, z, h, 0, time, msg, race);
	}
	
	/**
	 * Schedules the spawning of an NPC at a specific location.<br>
	 * This method checks if the instance is active before executing the task.<br>
	 * It also sends a race-specific message if one is provided.
	 * @param npcId The unique identifier for the NPC to spawn.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param h The height offset for the spawn.
	 * @param entityId The specific entity ID to use for the NPC.
	 * @param time The delay in milliseconds before spawning.
	 * @param msg The message ID to send if it is greater than 0.
	 * @param race The {@link Race} type used to determine which message to send.
	 */
	protected void sp(int npcId, float x, float y, float z, byte h, int entityId, int time, int msg, Race race)
	{
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isInstanceDestroyed)
			{
				spawn(npcId, x, y, z, h, entityId);
				if (msg > 0)
				{
					sendMsgByRace(msg, race, 0);
				}
			}
		}, time));
	}
	
	/**
	 * Schedules the spawning of an NPC at a specific location.<br>
	 * This method uses {@link ThreadPoolManager} to delay the spawn action.<br>
	 * It also assigns a walker ID and starts the walking behavior for the NPC.
	 * @param npcId The unique identifier for the NPC to be spawned.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param h The height offset for the spawn.
	 * @param time The delay in milliseconds before spawning.
	 * @param walkerId The identifier for the walking path to follow.
	 */
	protected void sp(int npcId, float x, float y, float z, byte h, int time, String walkerId)
	{
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isInstanceDestroyed)
			{
				final Npc npc = (Npc) spawn(npcId, x, y, z, h);
				npc.getSpawn().setWalkerId(walkerId);
				WalkManager.startWalking((NpcAI2) npc.getAi2());
			}
		}, time));
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
		idgelTask.add(ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isInstanceDestroyed)
			{
				instance.doOnAllPlayers(player ->
				{
					if (player.getRace().equals(race) || race.equals(Race.PC_ALL))
					{
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
					}
				});
			}
		}, time));
	}
	
	/**
	 * Sends a system message to all players in the instance.<br>
	 * This method checks if the instance is currently destroyed before sending.<br>
	 * It uses {@code String)} to deliver the text.
	 * @param str The message content to be sent to the players.
	 */
	protected void sendMsg(String str)
	{
		if (!isInstanceDestroyed)
		{
			instance.doOnAllPlayers(player -> PacketSendUtility.sendMessage(player, str));
		}
	}
	
	/**
	 * Creates a new NPC in the game world.<br>
	 * This method initializes the object at the specified coordinates and rotation.
	 * @param npcId The unique identifier for the NPC template.
	 * @param x The X coordinate of the spawn location.
	 * @param y The Y coordinate of the spawn location.
	 * @param z The Z coordinate of the spawn location.
	 * @param heading The rotation angle of the spawned object.
	 * @return The newly created {@link VisibleObject} instance.
	 */
	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading)
	{
		final SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		return SpawnEngine.spawnObject(template, instanceId);
	}
	
	/**
	 * Creates and places a new NPC into the game world.<br>
	 * This method initializes the object at the specified coordinates.
	 * @param npcId The unique identifier for the NPC type.
	 * @param x The X coordinate in the world.
	 * @param y The Y coordinate in the world.
	 * @param z The Z coordinate in the world.
	 * @param heading The direction the NPC is facing.
	 * @param staticId The unique identifier for a static object.
	 * @return The newly created {@link VisibleObject} instance.
	 */
	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading, int staticId)
	{
		final SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		template.setStaticId(staticId);
		return SpawnEngine.spawnObject(template, instanceId);
	}
	
	/**
	 * Creates a new NPC or object in the current instance.<br>
	 * This method uses {@link SpawnEngine} to generate a single-time spawn.<br>
	 * It handles complex entities that require walker data.
	 * @param npcId The unique identifier for the NPC template.
	 * @param x The X coordinate for spawning.
	 * @param y The Y coordinate for spawning.
	 * @param z The Z coordinate for spawning.
	 * @param heading The direction the object faces.
	 * @param walkerId The unique identifier for the walker pathing data.
	 * @param walkerIdx The index of the specific walker form to use.
	 * @return The {@code VisibleObject} created in the world.
	 */
	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading, String walkerId, int walkerIdx)
	{
		final SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId.intValue(), npcId, x, y, z, heading, walkerId, walkerIdx);
		return SpawnEngine.spawnObject(template, instanceId);
	}
}
