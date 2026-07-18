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
package com.aionemu.gameserver.services.teleport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerTransformationDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.portal.InstanceExit;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.portal.PortalScroll;
import com.aionemu.gameserver.model.templates.revive_start_points.InstanceReviveStartPoints;
import com.aionemu.gameserver.model.templates.revive_start_points.WorldReviveStartPoints;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleportType;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BIND_POINT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANNEL_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FAST_TRACK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FAST_TRACK_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIDE_ROBOT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TELEPORT_MAP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.DisputeLandService;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.FastTrackService;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.LunaShopService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * This service handles teleportation logic for players and entities within the game world.<br>
 * It manages various types of movement, including portal transitions and coordinate updates.<br>
 * It interacts with {@link WorldMapInstance} to ensure valid positioning during teleportation.
 * @author xTz
 */
public class TeleportService2
{
	private static final Logger log = LoggerFactory.getLogger(TeleportService2.class);
	private static final int TELEPORT_DEFAULT_DELAY = 2200;
	
	/**
	 * Teleports a {@link Player} to a specific destination based on a template and location ID.<br>
	 * This method validates requirements such as quest completion, Kinah balance, and flypath distance.<br>
	 * It handles both standard teleportation and flight-based teleportation logic.
	 * @param template The {@link TeleporterTemplate} containing the destination data.
	 * @param locId The unique identifier for the teleport location.
	 * @param player The {@link Player} who is performing the teleport.
	 * @param npc The {@link Npc} object associated with the teleporter.
	 * @param animation The {@link TeleportAnimation} to play during the transition.
	 */
	public static void teleport(TeleporterTemplate template, int locId, Player player, Npc npc, TeleportAnimation animation)
	{
		final TribeClass tribe = npc.getTribe();
		final Race race = player.getRace();
		if ((tribe.equals(TribeClass.FIELD_OBJECT_LIGHT) && race.equals(Race.ASMODIANS)) || (tribe.equals(TribeClass.FIELD_OBJECT_DARK) && race.equals(Race.ELYOS)))
		{
			return;
		}
		
		if (template.getTeleLocIdData() == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
			{
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		
		final TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if (location == null)
		{
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
			{
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		
		final TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if (locationTemplate == null)
		{
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
			{
				PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);
			}
			return;
		}
		
		if (location.getRequiredQuest() > 0)
		{
			if (player.getRace() == Race.ELYOS)
			{
				final QuestState qs = player.getQuestStateList().getQuestState(location.getRequiredQuest());
				if ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE))
				{
					// Memories Of Eternity.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(10521));
					return;
				}
			}
			else if (player.getRace() == Race.ASMODIANS)
			{
				final QuestState qs = player.getQuestStateList().getQuestState(location.getRequiredQuest());
				if ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE))
				{
					// Recovered Destiny.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(20521));
					return;
				}
			}
		}
		
		if (!checkKinahForTransportation(location, player))
		{
			return;
		}
		
		if (location.getType().equals(TeleportType.FLIGHT))
		{
			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR)
			{
				final FlyPathEntry flypath = DataManager.FLY_PATH.getPathTemplate((short) location.getLocId());
				if (flypath == null)
				{
					AuditLogger.info(player, "Try to use null flyPath #" + location.getLocId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}
				
				final double dist = MathUtil.getDistance(player, flypath.getStartX(), flypath.getStartY(), flypath.getStartZ());
				if (dist > 7)
				{
					AuditLogger.info(player, "Try to use flyPath #" + location.getLocId() + " but hes too far " + dist);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}
				
				if (player.getWorldId() != flypath.getStartWorldId())
				{
					AuditLogger.info(player, "Try to use flyPath #" + location.getLocId() + " from not native start world " + player.getWorldId() + ". expected " + flypath.getStartWorldId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}
				
				player.setCurrentFlypath(flypath);
			}
			
			player.unsetPlayerMode(PlayerMode.RIDE);
			player.setState(CreatureState.FLIGHT_TELEPORT);
			player.unsetState(CreatureState.ACTIVE);
			player.setFlightTeleportId(location.getTeleportId());
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, location.getTeleportId(), 0), true);
			playerTransformation(player);
		}
		else
		{
			int instanceId = 1;
			final int mapId = locationTemplate.getMapId();
			if (player.getWorldId() == mapId)
			{
				instanceId = player.getInstanceId();
			}
			
			sendLoc(player, mapId, instanceId, locationTemplate.getX(), locationTemplate.getY(), locationTemplate.getZ(), (byte) locationTemplate.getHeading(), animation);
			playerTransformation(player);
		}
	}
	
	/**
	 * Checks if the {@link Player} has enough Kinah to use a transportation service.<br>
	 * It calculates the price based on the {@link TeleportLocation} and player race.<br>
	 * If a HiPass effect is active, the cost is set to 1.<br>
	 * Returns {@code false} if the payment fails or the player has insufficient funds.
	 * @param location The destination teleport data containing the base price.
	 * @param player The player attempting to use the transportation service.
	 * @return {@code true} if the Kinah were successfully deducted, {@code false} otherwise.
	 */
	private static boolean checkKinahForTransportation(TeleportLocation location, Player player)
	{
		final Storage inventory = player.getInventory();
		
		// TODO: Price vary depending on the influence ratio
		final int basePrice = location.getPrice();
		// TODO check for location.getPricePvp()
		
		long transportationPrice = PricesService.getPriceForService(basePrice, player.getRace());
		
		// If HiPassEffect is active, then all flight/teleport prices are 1 kinah
		if (player.getController().isHiPassInEffect())
		{
			transportationPrice = 1;
		}
		
		if (!inventory.tryDecreaseKinah(transportationPrice, ItemUpdateType.DEC_KINAH_FLY))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(transportationPrice));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sends a teleport location packet to the player.<br>
	 * This method handles the visual animation and updates the player's position after a delay.<br>
	 * It also ensures the player is moved correctly between maps or instances.
	 * @param player The {@link Player} object to move.
	 * @param mapId The unique identifier for the destination map.
	 * @param instanceId The specific instance ID if the map is an instance.
	 * @param x The X coordinate of the destination.
	 * @param y The Y coordinate of the destination.
	 * @param z The Z coordinate of the destination.
	 * @param h The heading value for the destination.
	 * @param animation The {@link TeleportAnimation} to play during the move.
	 */
	private static void sendLoc(Player player, int mapId, int instanceId, float x, float y, float z, byte h, TeleportAnimation animation)
	{
		final boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).isInstance();
		final int delay = TELEPORT_DEFAULT_DELAY;
		
		if (animation.equals(TeleportAnimation.BEAM_ANIMATION))
		{
			player.setPortAnimation(2);
		}
		else if (animation.equals(TeleportAnimation.JUMP_ANIMATION))
		{
			player.setPortAnimation(11);
		}
		
		if (player.getLevel() >= 66)
		{
			PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(isInstance, instanceId, mapId, x, y, z, h, 3)); // 3 = Archdaeva Animation
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(isInstance, instanceId, mapId, x, y, z, h, animation.getStartAnimationId()));
		}
		
		player.unsetPlayerMode(PlayerMode.RIDE);
		playerTransformation(player);
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (player.getLifeStats().isAlreadyDead())
			{
				return;
			}
			
			if (animation.equals(TeleportAnimation.BEAM_ANIMATION))
			{
				PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 2), 50);
			}
			else if (animation.equals(TeleportAnimation.JUMP_ANIMATION))
			{
				PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 11), 50);
			}
			
			if (!player.isSpawned())
			{
				final WorldPosition pos = World.getInstance().createPosition(mapId, x, y, z, h, instanceId);
				player.setPosition(pos);
				DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
				return;
			}
			
			changePosition(player, mapId, instanceId, x, y, z, h, animation);
		}, delay);
	}
	
	/**
	 * Moves a {@link Player} to a specific {@code WorldPosition}.<br>
	 * This method handles moving the player, their pet, and their summon.<br>
	 * It also updates the player's zone, quests, and effects.<br>
	 * If the player is dead, it uses {@code int, int, float, float, float, byte)} instead.
	 * @param player The {@link Player} to be moved.
	 * @param pos The target {@code WorldPosition}.
	 */
	public static void teleportTo(Player player, WorldPosition pos)
	{
		if (player.getWorldId() == pos.getMapId())
		{
			player.getPosition().setXYZH(pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			
			// Pet
			final Pet pet = player.getPet();
			if (pet != null)
			{
				World.getInstance().setPosition(pet, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			
			// Summon
			final Summon summon = player.getSummon();
			if (summon != null)
			{
				World.getInstance().setPosition(summon, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			
			MinionService.getInstance().onTeleportPlayer(player);
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
			player.setPortAnimation(4); // Beam exit animation
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			
			// Pet
			if (pet != null)
			{
				World.getInstance().spawn(pet);
			}
			
			// Summon
			if (summon != null)
			{
				World.getInstance().spawn(summon);
			}
			
			MinionService.getInstance().onTeleportPlayer(player);
			player.getKnownList().clear();
			player.updateKnownlist();
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			DisputeLandService.getInstance().onLogin(player);
			player.getEffectController().updatePlayerEffectIcons();
			playerTransformation(player);
		}
		else if (player.getLifeStats().isAlreadyDead())
		{
			teleportDeadTo(player, pos.getMapId(), 1, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		}
		else
		{
			teleportTo(player, pos.getMapId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		}
	}
	
	/**
	 * Teleports a dead {@link Player} to a specific location.<br>
	 * This method handles the transition between worlds and updates the player's position.<br>
	 * It also sends necessary packets to synchronize the client state.
	 * @param player The {@code Player} object to teleport.
	 * @param worldId The ID of the destination world.
	 * @param instanceId The ID of the destination instance.
	 * @param x The destination X coordinate.
	 * @param y The destination Y coordinate.
	 * @param z The destination Z coordinate.
	 * @param heading The rotation angle at the destination.
	 */
	public static void teleportDeadTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading)
	{
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method updates the player's position based on the provided coordinates.
	 * @param player The {@code Player} object to move.
	 * @param worldId The unique identifier for the target world.
	 * @param x The horizontal coordinate of the destination.
	 * @param y The vertical coordinate of the destination.
	 * @param z The depth coordinate of the destination.
	 * @return {@code true} if the teleport was successful, otherwise {@code false}.
	 */
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z)
	{
		return teleportTo(player, worldId, x, y, z, player.getHeading());
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method handles the teleportation logic including animation and instance checking.
	 * @param player The {@code Player} object to move.
	 * @param worldId The ID of the destination world.
	 * @param x The X coordinate of the destination.
	 * @param y The Y coordinate of the destination.
	 * @param z The Z coordinate of the destination.
	 * @param h The heading value for the player's orientation.
	 * @return {@code true} if the teleportation was successful, {@code false} otherwise.
	 */
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h)
	{
		int instanceId = 1;
		if (player.getWorldId() == worldId)
		{
			instanceId = player.getInstanceId();
		}
		
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method handles coordinate updates and plays a visual effect.
	 * @param player The {@link Player} object to move.
	 * @param worldId The ID of the destination world.
	 * @param x The X coordinate of the destination.
	 * @param y The Y coordinate of the destination.
	 * @param z The Z coordinate of the destination.
	 * @param h The heading value for the new position.
	 * @param animation The {@link TeleportAnimation} to play during movement.
	 * @return {@code true} if the teleport was successful, {@code false} otherwise.
	 */
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h, TeleportAnimation animation)
	{
		int instanceId = 1;
		if (player.getWorldId() == worldId)
		{
			instanceId = player.getInstanceId();
		}
		
		return teleportTo(player, worldId, instanceId, x, y, z, h, animation);
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method handles moving the player to a designated map and instance.<br>
	 * It uses the default beam animation for the teleportation effect.
	 * @param player The {@link Player} object to move.
	 * @param worldId The unique identifier of the world or map.
	 * @param instanceId The specific instance ID for the location.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param h The heading value for the player's orientation.
	 * @return {@code true} if the teleportation was successful, {@code false} otherwise.
	 */
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte h)
	{
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method handles the teleportation logic including coordinates and instance IDs.<br>
	 * It uses the default beam animation for the transition.
	 * @param player The {@link Player} object to move.
	 * @param worldId The unique identifier of the target world.
	 * @param instanceId The specific instance ID of the location.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @return {@code true} if the teleportation was successful, {@code false} otherwise.
	 */
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z)
	{
		return teleportTo(player, worldId, instanceId, x, y, z, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method checks if the player is alive before moving them.<br>
	 * It handles world transitions and plays the specified {@link TeleportAnimation}.
	 * @param player The {@link Player} object to move.
	 * @param worldId The ID of the destination world.
	 * @param instanceId The ID of the destination instance.
	 * @param x The destination X coordinate.
	 * @param y The destination Y coordinate.
	 * @param z The destination Z coordinate.
	 * @param heading The rotation angle at the destination.
	 * @param animation The {@link TeleportAnimation} to play during movement.
	 * @return {@code true} if the teleport was successful, or {@code false} if the player is dead.
	 */
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation)
	{
		if (player.getLifeStats().isAlreadyDead())
		{
			return false;
		}
		else if (DuelService.getInstance().isDueling(player.getObjectId()))
		{
			DuelService.getInstance().loseDuel(player);
		}
		
		if (player.getWorldId() != worldId)
		{
			player.getController().onLeaveWorld();
		}
		
		if (animation.isNoAnimation())
		{
			playerTransformation(player);
			player.unsetPlayerMode(PlayerMode.RIDE);
			changePosition(player, worldId, instanceId, x, y, z, heading, animation);
		}
		else
		{
			sendLoc(player, worldId, instanceId, x, y, z, heading, animation);
		}
		
		return true;
	}
	
	/**
	 * Moves a {@link Player} to a specific location in the game world.<br>
	 * This method handles closing stores, canceling skills, and updating pet or summon positions.<br>
	 * It also manages world transitions and triggers necessary network packets for the new location.
	 * @param player The {@link Player} object to move.
	 * @param worldId The ID of the destination world.
	 * @param instanceId The ID of the destination instance.
	 * @param x The destination X coordinate.
	 * @param y The destination Y coordinate.
	 * @param z The destination Z coordinate.
	 * @param heading The rotation angle at the destination.
	 * @param animation The {@link TeleportAnimation} to play during the move.
	 */
	private static void changePosition(Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation)
	{
		if (player.hasStore())
		{
			PrivateStoreService.closePrivateStore(player);
		}
		
		player.getController().cancelCurrentSkill();
		if (player.getWorldId() != worldId)
		{
			player.getController().onLeaveWorld();
		}
		
		player.getFlyController().endFly(true);
		World.getInstance().despawn(player);
		
		// Send 2x, is normal !!!
		playerTransformation(player);
		player.getController().cancelCurrentSkill();
		final int currentWorldId = player.getWorldId();
		final WorldPosition pos = World.getInstance().createPosition(worldId, x, y, z, heading, instanceId);
		player.setPosition(pos);
		final boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).isInstance();
		
		// Pet
		final Pet pet = player.getPet();
		if (pet != null)
		{
			World.getInstance().setPosition(pet, worldId, instanceId, x, y, z, heading);
		}
		
		// Summon
		final Summon summon = player.getSummon();
		if (summon != null)
		{
			World.getInstance().setPosition(summon, worldId, instanceId, x, y, z, heading);
		}
		
		player.setPortAnimation(animation.getEndAnimationId());
		player.getController().startProtectionActiveTask();
		if (currentWorldId == worldId)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			World.getInstance().spawn(player);
			player.getEffectController().updatePlayerEffectIcons();
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			DisputeLandService.getInstance().onLogin(player);
			
			// Send 2x, is normal !!!
			playerTransformation(player);
			
			// Pet
			if (pet != null)
			{
				World.getInstance().spawn(pet);
				player.setPortAnimation(4);
			}
			
			// Summon
			if (summon != null)
			{
				World.getInstance().spawn(summon);
				player.setPortAnimation(4);
			}
			
			player.getKnownList().clear();
			player.updateKnownlist();
			if (player.isUseRobot() || (player.getRobotId() != 0))
			{
				PacketSendUtility.sendPacket(player, new SM_RIDE_ROBOT(player, getRobotInfo(player).getRobotId()));
			}
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			playerTransformation(player);
			if (player.isUseRobot() || (player.getRobotId() != 0))
			{
				ThreadPoolManager.getInstance().schedule(() -> PacketSendUtility.sendPacket(player, new SM_RIDE_ROBOT(player, getRobotInfo(player).getRobotId())), 3000);
			}
		}
		
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
		
		sendWorldSwitchMessage(player, currentWorldId, worldId, isInstance);
		AchievementService.getInstance().onUpdateAchievementAction(player, worldId, 1, AchievementActionType.ENTER_WORLD);
	}
	
	/**
	 * Retrieves the robot information for a specific {@link Player}.<br>
	 * This method looks up data based on the main hand weapon skin.
	 * @param player The {@code Player} object to check.
	 * @return The {@code RobotInfo} associated with the player's equipment.
	 */
	public static RobotInfo getRobotInfo(Player player)
	{
		final ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
	
	/**
	 * Sends a message to the player when they change worlds.<br>
	 * This method handles the transition between different world IDs.<br>
	 * It triggers the logic for entering an instance if required.
	 * @param player The {@code Player} object receiving the message.
	 * @param oldWorld The ID of the previous world.
	 * @param newWorld The ID of the destination world.
	 * @param enteredInstance A boolean indicating if the player is entering an instance.
	 */
	private static void sendWorldSwitchMessage(Player player, int oldWorld, int newWorld, boolean enteredInstance)
	{
		onEnterInstance(player, oldWorld, newWorld, enteredInstance);
	}
	
	/**
	 * Handles logic when a {@code Player} enters an instance.<br>
	 * It updates achievements and manages portal cooldowns.<br>
	 * This method also triggers the player transformation update.
	 * @param player The {@code Player} object involved in the teleport.
	 * @param oldWorld The ID of the previous world.
	 * @param newWorld The ID of the destination world.
	 * @param enteredInstance A boolean indicating if the destination is an instance.
	 */
	private static void onEnterInstance(Player player, int oldWorld, int newWorld, boolean enteredInstance)
	{
		if (enteredInstance && (oldWorld != newWorld) && !WorldMapType.getWorld(newWorld).isPersonal())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_DUNGEON_OPENED_FOR_SELF(newWorld));
			LunaShopService.getInstance().sendLunaInstanceBuff(player, player.getLevel());
			AchievementService.getInstance().onUpdateAchievementAction(player, newWorld, 1, AchievementActionType.ENTER_WORLD);
			if (player.getPortalCooldownList().getPortalCooldownItem(newWorld) == null)
			{
				player.getPortalCooldownList().addPortalCooldown(newWorld, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, newWorld));
			}
			else
			{
				player.getPortalCooldownList().addEntry(newWorld);
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE, 20000);
			}
		}
		
		playerTransformation(player);
	}
	
	/**
	 * Displays the map information for a specific teleportation point.<br>
	 * This method checks if the {@code player} is flying or targeting an enemy before sending the packet.<br>
	 * It uses the {@code targetObjectId} to find the object and {@code npcId} to get the template.
	 * @param player The {@link Player} who will receive the map information.
	 * @param targetObjectId The unique ID of the visible object in the world.
	 * @param npcId The ID used to retrieve the teleportation template.
	 */
	public static void showMap(Player player, int targetObjectId, int npcId)
	{
		if (player.isInFlyingState())
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_AIRPORT_WHEN_FLYING);
			return;
		}
		
		final Npc object = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		if (player.isEnemyFrom(object))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_WRONG_NPC); // TODO retail message
			return;
		}
		
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_MAP(player, targetObjectId, getTeleporterTemplate(npcId)));
	}
	
	/**
	 * Retrieves the {@link TeleporterTemplate} associated with a specific NPC.<br>
	 * This method looks up data using the provided {@code npcId}.
	 * @param npcId The unique identifier of the NPC.
	 * @return The {@code TeleporterTemplate} for the given ID, or {@code null} if not found.
	 */
	public static TeleporterTemplate getTeleporterTemplate(int npcId)
	{
		return DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npcId);
	}
	
	/**
	 * Moves a player to the specified kiosk location.<br>
	 * This method uses {@code int, float, float, float, byte)} to update the position.
	 * @param player The {@code Player} object to move.
	 * @param kisk The {@code WorldPosition} of the destination kiosk.
	 */
	public static void moveToKiskLocation(Player player, WorldPosition kisk)
	{
		teleportTo(player, kisk.getMapId(), kisk.getX(), kisk.getY(), kisk.getZ(), kisk.getHeading());
	}
	
	/**
	 * Moves a player to the correct prison based on their race.<br>
	 * It checks if the {@code Player} is an {@code ELYOS} or {@code ASMODIANS}.<br>
	 * The method uses {@code int, float, float, float)} to move them.
	 * @param player The {@code Player} object to teleport.
	 */
	public static void teleportToPrison(Player player)
	{
		if (player.getRace() == Race.ELYOS)
		{
			teleportTo(player, WorldMapType.LF_PRISON.getId(), 275, 239, 49);
		}
		else if (player.getRace() == Race.ASMODIANS)
		{
			teleportTo(player, WorldMapType.DF_PRISON.getId(), 275, 239, 49);
		}
	}
	
	/**
	 * Moves a {@link Player} to the location of a specific NPC.<br>
	 * This method finds the first available spawn point for the given {@code npcId}.<br>
	 * It handles both standard world maps and instance transitions automatically.
	 * @param player The {@link Player} object that will be moved.
	 * @param npcId The unique identifier of the NPC to teleport to.
	 */
	public static void teleportToNpc(Player player, int npcId)
	{
		final int worldId = player.getWorldId();
		final SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(worldId, npcId);
		
		if (searchResult == null)
		{
			log.warn("No npc spawn found for : " + npcId);
			return;
		}
		
		final SpawnSpotTemplate spot = searchResult.getSpot();
		final WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(searchResult.getWorldId());
		WorldMapInstance newInstance = null;
		
		if (worldTemplate.isInstance())
		{
			newInstance = InstanceService.getNextAvailableInstance(searchResult.getWorldId());
		}
		
		if (newInstance != null)
		{
			InstanceService.registerPlayerWithInstance(newInstance, player);
			teleportTo(player, searchResult.getWorldId(), newInstance.getInstanceId(), spot.getX(), spot.getY(), spot.getZ());
		}
		else
		{
			teleportTo(player, searchResult.getWorldId(), spot.getX(), spot.getY(), spot.getZ());
		}
	}
	
	/**
	 * Sends the bind point information to a specific {@link Player}.<br>
	 * It retrieves coordinates from the player's saved bind point.<br>
	 * If no bind point exists, it uses the default spawn location for the player's race.<br>
	 * The final data is sent using the {@code SM_BIND_POINT_INFO} packet.
	 * @param player The {@link Player} who will receive the bind point information.
	 */
	public static void sendSetBindPoint(Player player)
	{
		int worldId;
		float x, y, z;
		if (player.getBindPoint() != null)
		{
			final BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else
		{
			final PlayerInitialData.LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		
		PacketSendUtility.sendPacket(player, new SM_BIND_POINT_INFO(worldId, x, y, z, player));
	}
	
	/**
	 * Moves a {@link Player} to their designated bind location.<br>
	 * This method handles the positioning logic for the character.
	 * @param player The {@code Player} object to move.
	 * @param useTeleport A boolean indicating if the movement should use a teleport effect.
	 */
	public static void moveToBindLocation(Player player, boolean useTeleport)
	{
		moveToBindLocation(player, useTeleport, 0);
	}
	
	/**
	 * Moves a {@link Player} to their designated bind location.<br>
	 * If no bind point exists, it uses the default spawn for their race.<br>
	 * This method handles instance exit logic before moving the player.
	 * @param player The {@code Player} object to move.
	 * @param useTeleport Set to {@code true} to use the teleport system, or {@code false} to set position directly.
	 * @param delay The time in milliseconds to wait before moving.
	 */
	public static void moveToBindLocation(Player player, boolean useTeleport, int delay)
	{
		byte h = 0;
		int worldId;
		float x;
		float y;
		float z;
		if (player.getBindPoint() != null)
		{
			final BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
			h = bplist.getHeading();
		}
		else
		{
			final PlayerInitialData.LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		
		InstanceService.onLeaveInstance(player);
		
		if (useTeleport)
		{
			teleportTo(player, worldId, x, y, z, h);
		}
		else
		{
			World.getInstance().setPosition(player, worldId, 1, x, y, z, h);
		}
	}
	
	/**
	 * Moves a {@link Player} to a position relative to a {@link VisibleObject}.<br>
	 * The target position is calculated based on the object's heading and direction.
	 * @param object The reference {@link VisibleObject} used for the starting point.
	 * @param player The {@link Player} who will be moved.
	 * @param direction The numerical direction to move in.
	 * @param distance The number of units to move from the object.
	 * @return {@code true} if the teleport was successful, {@code false} otherwise.
	 */
	public static boolean moveToTargetWithDistance(VisibleObject object, Player player, int direction, int distance)
	{
		final double radian = Math.toRadians(object.getHeading() * 3);
		final float x0 = object.getX();
		final float y0 = object.getY();
		final float x1 = (float) (Math.cos((Math.PI * direction) + radian) * distance);
		final float y1 = (float) (Math.sin((Math.PI * direction) + radian) * distance);
		return teleportTo(player, object.getWorldId(), x0 + x1, y0 + y1, object.getZ());
	}
	
	/**
	 * Moves a {@link Player} to the designated exit of an instance.<br>
	 * This method cancels any active skills before attempting the move.<br>
	 * If no valid exit is found or the destination world is missing, it moves the player to their bind location.
	 * @param player The {@link Player} object to be moved.
	 * @param worldId The ID of the current world.
	 * @param race The {@link Race} of the player used to determine the correct exit point.
	 */
	public static void moveToInstanceExit(Player player, int worldId, Race race)
	{
		player.getController().cancelCurrentSkill();
		final InstanceExit instanceExit = getInstanceExit(worldId, race);
		if (instanceExit == null)
		{
			log.warn("No instance exit found for race: " + race + " " + worldId);
			moveToBindLocation(player, true);
			return;
		}
		
		if (InstanceService.isInstanceExist(instanceExit.getExitWorld(), 1))
		{
			teleportTo(player, instanceExit.getExitWorld(), instanceExit.getX(), instanceExit.getY(), instanceExit.getZ(), instanceExit.getH());
		}
		else
		{
			moveToBindLocation(player, true);
		}
	}
	
	/**
	 * Handles the automatic teleportation of players between opposite maps.<br>
	 * This method checks if a player is in Iluma or Norsvold and moves them to the other side based on their race.<br>
	 * It uses {@code int, float, float, float, byte)} to perform the move.
	 * @param player The {@code Player} object to check for teleportation logic.
	 */
	public static void onLogOutOppositeMap(Player player)
	{
		switch (player.getWorldId())
		{
			case 210100000: // Iluma
				if (player.getCommonData().getRace() == Race.ASMODIANS)
				{
					TeleportService2.teleportTo(player, 220110000, 1813.9795f, 1982.6705f, 199.1976f, (byte) 52);
				}
				break;
			case 220110000: // Norsvold
				if (player.getCommonData().getRace() == Race.ELYOS)
				{
					TeleportService2.teleportTo(player, 210100000, 1417.6694f, 1282.3623f, 336.125f, (byte) 8);
				}
				break;
		}
	}
	
	/**
	 * Retrieves the {@link InstanceExit} data for a specific world and race.<br>
	 * This method fetches information from the {@code DataManager}.
	 * @param worldId The unique identifier of the world.
	 * @param race The {@link Race} type of the character.
	 * @return The corresponding {@link InstanceExit} object.
	 */
	public static InstanceExit getInstanceExit(int worldId, Race race)
	{
		return DataManager.INSTANCE_EXIT_DATA.getInstanceExit(worldId, race);
	}
	
	/**
	 * Retrieves the starting points for instance revives based on a specific world.<br>
	 * This method uses {@link DataManager} to fetch the correct coordinates.
	 * @param worldId The unique identifier of the world.
	 * @return The {@code InstanceReviveStartPoints} for the given world.
	 */
	public static InstanceReviveStartPoints getReviveInstanceStartPoints(int worldId)
	{
		return DataManager.REVIVE_INSTANCE_START_POINTS.getReviveStartPoint(worldId);
	}
	
	/**
	 * Retrieves the starting point for a character's revival.<br>
	 * This method uses {@link DataManager} to find the correct location based on world and player stats.
	 * @param worldId The unique identifier of the world.
	 * @param race The {@code Race} type of the character.
	 * @param level The current level of the character.
	 * @return A {@code WorldReviveStartPoints} object containing the revival coordinates.
	 */
	public static WorldReviveStartPoints getReviveWorldStartPoints(int worldId, Race race, int level)
	{
		return DataManager.REVIVE_WORLD_START_POINTS.getReviveStartPoint(worldId, race, level);
	}
	
	/**
	 * Moves a {@link Player} to a specific location using a portal scroll.<br>
	 * This method validates the scroll template and path before teleporting.<br>
	 * It also updates achievements for entering the world.
	 * @param player The {@link Player} who will be moved.
	 * @param portalName The name of the scroll used to find the destination.
	 * @param worldId The ID of the target world.
	 */
	public static void useTeleportScroll(Player player, String portalName, int worldId)
	{
		final PortalScroll template = DataManager.PORTAL2_DATA.getPortalScroll(portalName);
		if (template == null)
		{
			log.warn("No portal template found for : " + portalName + " " + worldId);
			return;
		}
		
		final Race playerRace = player.getRace();
		final PortalPath portalPath = template.getPortalPath();
		if (portalPath == null)
		{
			log.warn("No portal scroll for " + playerRace + " on " + portalName + " " + worldId);
			return;
		}
		
		final PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portalPath.getLocId());
		if (loc == null)
		{
			log.warn("No portal loc for locId" + portalPath.getLocId());
			return;
		}
		
		teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
		AchievementService.getInstance().onUpdateAchievementAction(player, worldId, 1, AchievementActionType.ENTER_WORLD);
	}
	
	/**
	 * Moves a {@link Player} to the starting point of a specific world.<br>
	 * This method handles despawning the player and finding the correct revive location.<br>
	 * If no start point is found, it moves the player to their bind location.
	 * @param player The {@link Player} object to teleport.
	 * @param worldId The unique identifier for the target world.
	 */
	public static void teleportWorldStartPoint(Player player, int worldId)
	{
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		final WorldReviveStartPoints startPoint = getReviveWorldStartPoints(worldId, player.getRace(), player.getLevel());
		if (startPoint != null)
		{
			World.getInstance().setPosition(player, startPoint.getReviveWorld(), 0, startPoint.getX(), startPoint.getY(), startPoint.getZ(), startPoint.getH());
		}
		else
		{
			moveToBindLocation(player, false);
		}
		
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	/**
	 * Moves a {@link Player} to the starting point of an instance.<br>
	 * This method handles world transition and spawning logic.<br>
	 * It uses {@code int, int, float, float, float, byte)} if a revive point exists.<br>
	 * Otherwise, it moves the player to their bind location.
	 * @param player The {@link Player} object to teleport.
	 * @param worldId The ID of the target world.
	 */
	public static void teleportInstanceStartPoint(Player player, int worldId)
	{
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		final InstanceReviveStartPoints revivePoint = getReviveInstanceStartPoints(worldId);
		if (revivePoint != null)
		{
			TeleportService2.teleportTo(player, worldId, worldId, revivePoint.getX(), revivePoint.getY(), revivePoint.getY(), (byte) revivePoint.getY());
		}
		else
		{
			moveToBindLocation(player, false);
		}
		
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		if (player.isLegionMember())
		{
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
		}
	}
	
	/**
	 * Moves a {@link Player} to a different channel.<br>
	 * This method handles the despawn and repositioning of the player.<br>
	 * It also updates the player's protection status and sends necessary network packets.
	 * @param player The {@code Player} object to move.
	 * @param channel The target channel ID for the movement.
	 */
	public static void changeChannel(Player player, int channel)
	{
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(), player.getZ(), player.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		playerTransformation(player);
	}
	
	/**
	 * Moves a {@link Player} to a new location using the fast track system.<br>
	 * This method handles position updates and sends necessary network packets.<br>
	 * It adjusts the player state based on whether they are moving forward or backward.
	 * @param player The {@link Player} object to move.
	 * @param serverId The ID of the target server for the fast track movement.
	 * @param back A boolean indicating if the movement is in a backward direction.
	 */
	public static void moveFastTrack(Player player, int serverId, boolean back)
	{
		if (back)
		{
			playerTransformation(player);
			World.getInstance().despawn(player);
			World.getInstance().setPosition(player, player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
			player.getController().startProtectionActiveTask();
			player.FAST_TRACK_TYPE = 0;
			PacketSendUtility.sendPacket(player, new SM_FAST_TRACK_MOVE(NetworkConfig.GAMESERVER_ID, serverId, player.getWorldId()));
			PacketSendUtility.sendPacket(player, new SM_FAST_TRACK(NetworkConfig.GAMESERVER_ID, serverId, false));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			FastTrackService.getInstance().checkFastTrackMove(player, player.getPlayerAccount().getId(), true);
		}
		else
		{
			World.getInstance().despawn(player);
			World.getInstance().setPosition(player, player.getWorldId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
			player.getController().startProtectionActiveTask();
			player.FAST_TRACK_TYPE = 1;
			PacketSendUtility.sendPacket(player, new SM_FAST_TRACK_MOVE(serverId, NetworkConfig.GAMESERVER_ID, player.getWorldId()));
			PacketSendUtility.sendPacket(player, new SM_FAST_TRACK(serverId, NetworkConfig.GAMESERVER_ID, false));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
			playerTransformation(player);
			FastTrackService.getInstance().checkFastTrackMove(player, player.getPlayerAccount().getId(), false);
		}
	}
	
	/**
	 * Loads and applies the transformation data for a specific player.<br>
	 * This method updates the {@code Player} model using {@link PlayerTransformationDAO}.<br>
	 * It also sends a {@code SM_TRANSFORM} packet to the client to update the visual appearance.
	 * @param player The {@code Player} object to transform.
	 */
	public static void playerTransformation(Player player)
	{
		DAOManager.getDAO(PlayerTransformationDAO.class).loadPlTransfo(player);
		PacketSendUtility.sendPacket(player, new SM_TRANSFORM(player, player.getTransformModel().getPanelId(), true, player.getTransformModel().getItemId()));
	}
}
