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
package com.aionemu.gameserver.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.LookManager;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.panels.SkillPanel;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HEADING_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MINIONS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_PROTECTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIDE_ROBOT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.services.PvpService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.WorldPlayTimeService;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class handles the core logic and behaviors for {@link Player} entities.<br>
 * It manages player actions, state updates, and interactions within the game world.
 * @author -Nemesiss-, ATracer, xavier, Sarynth, RotO, xTz, KID, Sippolo
 */
public class PlayerController extends CreatureController<Player>
{
	private final Logger log = LoggerFactory.getLogger(PlayerController.class);
	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;
	@SuppressWarnings("unused")
	private Listener mListener;
	
	/**
	 * This method registers a {@link Player} as an observer.<br>
	 * It creates a new {@code FlyRingObserver} for the target.<br>
	 * The observer is added to the player's observation controller.
	 * @param object The {@code VisibleObject} representing the player to observe.
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		if (object instanceof Player)
		{
			final Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isAggroIconTo(player)));
			PacketSendUtility.sendPacket(getOwner(), new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			if (player.isUseRobot() || (player.getRobotId() != 0))
			{
				PacketSendUtility.sendPacket(getOwner(), new SM_RIDE_ROBOT(player, getRobotInfo(player).getRobotId()));
			}
			
			if (player.isTransformed())
			{
				TeleportService2.playerTransformation(getOwner());
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, player.getTransformedModelId(), true, player.getTransformedItemId(), player.getTransformedSkillId()));
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
			}
			
			if (player.isInPlayerMode(PlayerMode.RIDE))
			{
				PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION(player, EmotionType.RIDE, 0, player.ride.getNpcId()));
			}
			
			if (player.getPet() != null)
			{
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has toypet");
				PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getPet()));
			}
			else if (player.getMinion() != null)
			{
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has Minion");
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_MINIONS(6, player.getMinion().getCommonData(), 0));
			}
			
			player.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Kisk)
		{
			final Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace())
			{
				PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
			}
		}
		else if (object instanceof Npc)
		{
			final Npc npc = ((Npc) object);
			LookManager.corrigateHeading(npc, getOwner());
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION(npc, EmotionType.SELECT_TARGET));
			PacketSendUtility.sendPacket(getOwner(), new SM_HEADING_UPDATE(object.getObjectId(), object.getHeading()));
			if (!npc.getEffectController().isEmpty())
			{
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
			
			QuestEngine.getInstance().onAtDistance(new QuestEnv(object, getOwner(), 0, 0));
		}
		else if (object instanceof Summon)
		{
			final Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			if (!npc.getEffectController().isEmpty())
			{
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
		}
		else if ((object instanceof Gatherable) || (object instanceof StaticObject))
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		}
		else if (object instanceof Pet)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, (Pet) object));
		}
		else if (object instanceof Minion)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_MINIONS(5));
		}
	}
	
	/**
	 * Retrieves the robot information for a specific {@link Player}.<br>
	 * This method looks up data based on the main hand weapon skin.
	 * @param player The {@code Player} object to check.
	 * @return The {@code RobotInfo} associated with the player's equipment.
	 */
	private RobotInfo getRobotInfo(Player player)
	{
		final ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
	
	/**
	 * Updates the visibility status of a specific object.<br>
	 * This method also clears the target if the object is the current target.
	 * @param object The {@code VisibleObject} that is no longer seen.
	 * @param isOutOfRange Whether the object is outside of the visible range.
	 */
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange)
	{
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(4, (Pet) object));
		}
		else if (object instanceof Minion)
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_MINIONS(6));
		}
		else
		{
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 15));
		}
	}
	
	/**
	 * Updates and sends the list of nearby quests to the player.<br>
	 * This method checks for quests in the current map region.<br>
	 * It filters quests based on level requirements and start conditions.<br>
	 * The results are sent via a {@code SM_NEARBY_QUESTS} packet.
	 */
	public void updateNearbyQuests()
	{
		final HashMap<Integer, Integer> nearbyQuestList = new HashMap<>();
		for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds())
		{
			int diff = 0;
			if (questId <= 0xFFFF)
			{
				diff = QuestService.getLevelRequirementDiff(questId, getOwner().getCommonData().getLevel());
			}
			
			if ((diff <= 2) && QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0), false))
			{
				nearbyQuestList.put(questId, diff);
			}
		}
		
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(nearbyQuestList));
	}
	
	/**
	 * Handles logic when a player enters a new {@link ZoneInstance}.<br>
	 * This method updates the player's movement state and flight status.<br>
	 * It also triggers quest updates and notifies relevant world handlers.
	 * @param zone The {@link ZoneInstance} that the player has entered.
	 */
	@Override
	public void onEnterZone(ZoneInstance zone)
	{
		final Player player = getOwner();
		if ((!zone.canRide()) && (player.isInPlayerMode(PlayerMode.RIDE)))
		{
			player.unsetPlayerMode(PlayerMode.RIDE);
		}
		
		if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT) && (player.isInState(CreatureState.FLYING)))
		{
			switch (player.getWorldId())
			{
				case 210050000:
				case 220070000:
				case 400070000:
				case 800030000:
				case 800040000:
				case 800060000:
					player.setFlyState(0);
					player.getFlyController().endFly(true);
					player.unsetState(CreatureState.FLYING);
					
					// You cannot fly in this area.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLYING_FORBIDDEN_ZONE);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);
					break;
			}
		}
		
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		if (player.getPosition().isInstanceMap())
		{
			InstanceService.onEnterZone(player, zone);
		}
		else
		{
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onEnterZone(player, zone);
		}
		
		if (zone.getAreaTemplate().getZoneName() == null)
		{
			log.error("No name found for a Zone in the map " + zone.getAreaTemplate().getWorldId());
		}
		else
		{
			QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zone.getAreaTemplate().getZoneName());
		}
	}
	
	/**
	 * Handles the logic when a player leaves a specific area.<br>
	 * This method notifies the {@link InstanceService} or world handler about the movement.<br>
	 * It also updates the quest system based on the new zone name.
	 * @param zone The {@code ZoneInstance} that the player is exiting.
	 */
	@Override
	public void onLeaveZone(ZoneInstance zone)
	{
		final Player player = getOwner();
		if (player.getPosition().isInstanceMap())
		{
			InstanceService.onLeaveZone(player, zone);
		}
		else
		{
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onLeaveZone(player, zone);
		}
		
		final ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null)
		{
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}
	
	/**
	 * Should only be triggered from one place (life stats)
	 */
	// TODO [AT] move
	/**
	 * This method is called when a player enters the game world.<br>
	 * It initializes various services for the {@code Player}.<br>
	 * It also handles the removal of specific effects and buffs based on the current location.
	 */
	public void onEnterWorld()
	{
		final Player player = getOwner();
		InstanceService.onEnterInstance(getOwner());
		TeleportService2.playerTransformation(getOwner());
		WorldPlayTimeService.getInstance().onEnterWorld(player);
		
		if (getOwner().getPosition().getWorldMapInstance().getParent().isExceptBuff())
		{
			getOwner().getEffectController().removeAllEffects();
		}
		
		for (Effect ef : getOwner().getEffectController().getAbnormalEffects())
		{
			if (ef.isDeityAvatar())
			{
				// Remove abyss transformation if worldtype != "Abyss" && worldtype != "Balaurea" && worldtype != "Panesterra"
				if (((getOwner().getWorldType() != WorldType.ABYSS) && (getOwner().getWorldType() != WorldType.BALAUREA)) || getOwner().isInInstance())
				{
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			}
			else if (ef.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_BUFF)
			{
				ef.endEffect();
				getOwner().getEffectController().clearEffect(ef);
			}
		}
	}
	
	// TODO [AT] move
	/**
	 * Handles the logic when a player leaves the world.<br>
	 * This method triggers {@code onLeaveInstance} for the owner.
	 */
	public void onLeaveWorld()
	{
		InstanceService.onLeaveInstance(getOwner());
	}
	
	/**
	 * Checks if the player is in a valid login zone.<br>
	 * If the player has been offline for more than 600 seconds and is in a no-recall zone, they are moved to their bind point.<br>
	 * This method ensures players do not get stuck in restricted areas after long periods of inactivity.
	 */
	public void validateLoginZone()
	{
		int mapId;
		float x, y, z;
		byte h;
		boolean moveToBind = false;
		
		final BindPointPosition bind = getOwner().getBindPoint();
		
		if (bind != null)
		{
			mapId = bind.getMapId();
			x = bind.getX();
			y = bind.getY();
			z = bind.getZ();
			h = bind.getHeading();
		}
		else
		{
			final PlayerInitialData.LocationData start = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(getOwner().getRace());
			
			mapId = start.getMapId();
			x = start.getX();
			y = start.getY();
			z = start.getZ();
			h = start.getHeading();
		}
		
		final long lastOnline = getOwner().getCommonData().getLastOnline().getTime();
		final long secondsOffline = (System.currentTimeMillis() / 1000) - (lastOnline / 1000);
		if (secondsOffline > (10 * 60))
		{
			// Logout in no-recall zone sends you to bindpoint after 10 (??) minutes
			for (ZoneInstance zone : getOwner().getPosition().getMapRegion().getZones(getOwner()))
			{
				if (!zone.canRecall())
				{
					moveToBind = true;
					break;
				}
			}
			
		}
		
		if (moveToBind)
		{
			World.getInstance().setPosition(getOwner(), mapId, x, y, z, h);
		}
	}
	
	/**
	 * Handles the logic when a creature dies.<br>
	 * This method cleans up active states, summons, and pets.<br>
	 * It also processes rewards and death penalties based on game rules.
	 * @param lastAttacker The {@code Creature} that dealt the final blow.
	 * @param showPacket Determines if the death packet should be sent to the client.
	 */
	public void onDie(Creature lastAttacker, boolean showPacket)
	{
		final Player player = getOwner();
		player.getController().cancelCurrentSkill();
		player.setRebirthRevive(getOwner().haveSelfRezEffect());
		showPacket = player.hasResurrectBase() ? false : showPacket;
		final Creature master = lastAttacker.getMaster();
		
		// High ranked kill announce
		final AbyssRank ar = player.getAbyssRank();
		if (AbyssService.isOnPvpMap(player) && (ar != null))
		{
			if (ar.getRank().getId() >= 10)
			{
				AbyssService.rankedKillAnnounce(player);
			}
		}
		
		if (DuelService.getInstance().isDueling(player.getObjectId()))
		{
			if ((master != null) && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId()))
			{
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHpPercent(33);
				player.getLifeStats().setCurrentMpPercent(33);
				return;
			}
			
			DuelService.getInstance().loseDuel(player);
		}
		
		/**
		 * Release summon
		 */
		final Summon summon = player.getSummon();
		if (summon != null)
		{
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
		
		final Pet pet = player.getPet();
		if (pet != null)
		{
			PetSpawnService.dismissPet(player, true);
		}
		
		final Minion minion = player.getMinion();
		if (minion != null)
		{
			MinionService.getInstance().despawnMinion(player, player.getMinionList().getLastUsed());
		}
		
		if (player.isInState(CreatureState.FLYING))
		{
			player.setIsFlyingBeforeDeath(true);
		}
		
		// ride
		player.setPlayerMode(PlayerMode.RIDE, null);
		player.unsetState(CreatureState.RESTING);
		player.unsetState(CreatureState.FLOATING_CORPSE);
		
		// unsetflying
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);
		
		if (player.isInInstance())
		{
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker))
			{
				super.onDie(lastAttacker);
				return;
			}
		}
		
		final MapRegion mapRegion = player.getPosition().getMapRegion();
		if ((mapRegion != null) && mapRegion.onDie(lastAttacker, getOwner()))
		{
			return;
		}
		
		doReward();
		
		if ((master instanceof Npc) || (master == player))
		{
			if ((player.getLevel() > 4) && !isNoDeathPenaltyInEffect() && !isNoDeathPenaltyReduceInEffect() && !isDeathPenaltyReduceInEffect())
			{
				player.getCommonData().calculateExpLoss();
			}
		}
		
		// Effects removed with super.onDie()
		super.onDie(lastAttacker);
		
		// send sm_emotion with DIE have to be send after state is updated!
		sendDieFromCreature(lastAttacker, showPacket);
		
		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		
		if (player.isInGroup2())
		{
			player.getPlayerGroup2().sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()), new ExcludePlayerFilter(player));
		}
	}
	
	/**
	 * Handles the logic when a creature dies.<br>
	 * This method stops movement and clears active effects.<br>
	 * It updates the {@link CreatureState} based on whether the owner is a {@link Player} or an {@link Npc}.<br>
	 * Finally, it notifies all death observers about the {@code lastAttacker}.
	 * @param lastAttacker The creature that dealt the final blow.
	 */
	@Override
	public void onDie(Creature lastAttacker)
	{
		this.onDie(lastAttacker, true);
	}
	
	/**
	 * Sends the death packet to the client.<br>
	 * This method triggers {@code boolean)} using the owner of this object.
	 */
	public void sendDie()
	{
		sendDieFromCreature(getOwner(), true);
	}
	
	/**
	 * Sends the death notification for a player who was killed by a creature.<br>
	 * This method handles broadcasting emotions and sending specific death packets.<br>
	 * It also displays a system message regarding the combat death.
	 * @param lastAttacker The {@link Creature} that dealt the final blow.
	 * @param showPacket Whether to send the detailed {@code SM_DIE} packet to the player.
	 */
	private void sendDieFromCreature(Creature lastAttacker, boolean showPacket)
	{
		final Player player = getOwner();
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		
		if (showPacket)
		{
			if (player.isInInstance())
			{
				PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8, false));
			}
			else
			{
				final int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
				PacketSendUtility.sendPacket(player, new SM_DIE(player.canUseRebirthRevive(), player.haveSelfRezItem(), kiskTimeRemaining, 0, isInvader(player)));
			}
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_DEATH);
	}
	
	/**
	 * Checks if the given {@link Player} is currently in an invader zone.<br>
	 * This depends on the {@code Race} of the player and their current world ID.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is considered an invader, {@code false} otherwise.
	 */
	private boolean isInvader(Player player)
	{
		if (player.getRace().equals(Race.ASMODIANS))
		{
			return player.getWorldId() == 210060000; // Theobomos
		}
		
		return player.getWorldId() == 220050000; // Brusthonin
	}
	
	/**
	 * Processes the rewards for completing a task.<br>
	 * This method handles giving items or other benefits to the player.<br>
	 * It is called after a successful action or event.
	 */
	@Override
	public void doReward()
	{
		PvpService.getInstance().doReward(getOwner());
	}
	
	/**
	 * This method is called before a {@code Gatherable} object is spawned.<br>
	 * It resets the internal {@code gatherCount} to {@code 0}.
	 */
	@Override
	public void onBeforeSpawn()
	{
		this.onBeforeSpawn(true);
	}
	
	/**
	 * This method is called before a creature is spawned.<br>
	 * It handles the initial state of the owner and optional protection tasks.
	 * @param blink Determines if the {@code startProtectionActiveTask()} should be executed.
	 */
	public void onBeforeSpawn(boolean blink)
	{
		super.onBeforeSpawn();
		if (blink)
		{
			startProtectionActiveTask();
		}
		
		if (getOwner().getIsFlyingBeforeDeath())
		{
			getOwner().unsetState(CreatureState.FLOATING_CORPSE);
		}
		else
		{
			getOwner().unsetState(CreatureState.DEAD);
		}
		
		getOwner().setState(CreatureState.ACTIVE);
	}
	
	/**
	 * Executes an attack against a specific target.<br>
	 * This method validates the owner's state before calculating and applying damage.<br>
	 * It broadcasts the attack packet and notifies observers if the attack hits.
	 * @param target The {@code Creature} being attacked.
	 * @param attackNo The unique identifier for the current attack sequence.
	 * @param time The delay in milliseconds before the damage is applied.
	 * @param type The specific category or style of the attack.
	 */
	@Override
	public void attackTarget(Creature target, int attackNo, int time, int type)
	{
		final PlayerGameStats gameStats = getOwner().getGameStats();
		
		// Normal attack is already limited client side (ex. Press C and attacker approaches target) but need a check server side too also for Z axis issue
		if (!RestrictionsManager.canAttack(getOwner(), target) || !MathUtil.isInAttackRange(getOwner(), target, (gameStats.getAttackRange().getCurrent() / 1000) + 1))
		{
			return;
		}
		
		if (!GeoService.getInstance().canSee(getOwner(), target))
		{
			PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.STR_ATTACK_OBSTACLE_EXIST);
			return;
		}
		
		if (target instanceof Npc)
		{
			QuestEngine.getInstance().onAttack(new QuestEnv(target, getOwner(), 0, 0));
		}
		
		final int attackSpeed = gameStats.getAttackSpeed().getCurrent();
		
		final long milis = System.currentTimeMillis();
		
		// network ping..
		if (((milis - lastAttackMilis) + 300) < attackSpeed)
		{
			// hack
			return;
		}
		
		lastAttackMilis = milis;
		
		/**
		 * notify attack observers
		 */
		super.attackTarget(target, attackNo, time, type);
	}
	
	/**
	 * Handles the logic when this entity is attacked by another creature.<br>
	 * It identifies the attacker and triggers relevant quest or packet updates.
	 * @param creature The {@code Creature} performing the attack.
	 * @param skillId The unique identifier for the skill used in the attack.
	 * @param type The {@code TYPE} of the attack performed.
	 * @param damage The amount of damage dealt during this attack.
	 * @param notifyAttack A boolean indicating if the attack should be broadcasted as a notification.
	 * @param log The {@code LOG} object used for recording the action details.
	 */
	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log)
	{
		if (getOwner().getLifeStats().isAlreadyDead())
		{
			return;
		}
		
		if (getOwner().isInvul() || getOwner().isProtectionActive())
		{
			damage = 0;
		}
		
		cancelUseItem();
		cancelGathering();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), creature, type, skillId, damage, log), true);
		
		lastAttackedMilis = System.currentTimeMillis();
	}
	
	/**
	 * Executes a specific skill for the owner of this object.<br>
	 * It validates if the skill can be used before applying its effects.
	 * @param skillId The unique identifier for the skill to use.
	 * @param targetType The type of target for the skill.
	 * @param x The X coordinate of the target position.
	 * @param y The Y coordinate of the target position.
	 * @param z The Z coordinate of the target position.
	 * @param time The hit time or delay for the skill execution.
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time)
	{
		final Player player = getOwner();
		
		final Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());
		
		if (skill != null)
		{
			if (!RestrictionsManager.canUseSkill(player, skill))
			{
				return;
			}
			
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}
	
	/**
	 * Executes a specific skill for the owner of this object.<br>
	 * This method handles instance and world logic before triggering the {@code SkillEngine}.<br>
	 * It validates restrictions and updates quest progress upon successful execution.
	 * @param template The {@code SkillTemplate} containing the skill data.
	 * @param targetType The type of target for the skill.
	 * @param x The X coordinate of the target position.
	 * @param y The Y coordinate of the target position.
	 * @param z The Z coordinate of the target position.
	 * @param clientHitTime The hit time received from the client.
	 * @param skillLevel The level of the skill to be used.
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime, int skillLevel)
	{
		final Player player = getOwner();
		Skill skill = null;
		if (player.isInInstance())
		{
			player.getPosition().getWorldMapInstance().getInstanceHandler().onSkillUse(player, template);
		}
		else
		{
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onSkillUse(player, template);
		}
		
		skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());
		if ((skill == null) && player.isTransformed())
		{
			final SkillPanel panel = DataManager.PANEL_SKILL_DATA.getSkillPanel(player.getTransformModel().getPanelId());
			if ((panel != null) && panel.canUseSkill(template.getSkillId(), skillLevel))
			{
				skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget(), skillLevel);
			}
		}
		
		if (skill != null)
		{
			if (!RestrictionsManager.canUseSkill(player, skill))
			{
				return;
			}
			
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
			final QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, template.getSkillId());
		}
	}
	
	/**
	 * This method is called when the creature starts moving.<br>
	 * It notifies the {@link AI2} system and updates the current zone.
	 */
	@Override
	public void onMove()
	{
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}
	
	/**
	 * This method is called when a creature stops moving.<br>
	 * It triggers the {@code notifyAIOnMove} method to update the AI state.
	 */
	@Override
	public void onStopMove()
	{
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}
	
	/**
	 * This method is called when the entity begins to move.<br>
	 * It notifies all observers of the movement.<br>
	 * It also triggers the {@code notifyAIOnMove} logic.
	 */
	@Override
	public void onStartMove()
	{
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		super.onStartMove();
	}
	
	/**
	 * Cancels the skill currently being cast by the owner.<br>
	 * This method stops the casting process and removes any active cooldowns.<br>
	 * It also updates the AI state if the owner is an {@link NpcAI2}.
	 */
	@Override
	public void cancelCurrentSkill()
	{
		if (getOwner().getCastingSkill() == null)
		{
			return;
		}
		
		final Player player = getOwner();
		final Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if ((castingSkill.getSkillMethod() == SkillMethod.CAST) || (castingSkill.getSkillMethod() == SkillMethod.CHARGE))
		{
			PacketSendUtility.broadcastPacket(player, new SM_SKILL_CANCEL(player, castingSkill.getSkillTemplate().getSkillId()), true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED);
		}
		else if (castingSkill.getSkillMethod() == SkillMethod.ITEM)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(), castingSkill.getItemTemplate().getTemplateId(), 0, 3), true);
		}
	}
	
	/**
	 * Stops the current item usage action.<br>
	 * This method cancels any active effects from using an item.<br>
	 * It resets the state of the {@code Creature}.
	 */
	@Override
	public void cancelUseItem()
	{
		final Player player = getOwner();
		final Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE))
		{
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, usingItem == null ? 0 : usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3), true);
		}
		
		if (hasTask(TaskId.MAGIC_MORPH))
		{
			player.getController().onMove(); // Not nice but working ;)
		}
	}
	
	/**
	 * Stops the current gathering action for the owner.<br>
	 * This method checks if the {@code Player} is targeting a {@link Gatherable}.<br>
	 * If so, it calls {@code getController} to finish the process.
	 */
	public void cancelGathering()
	{
		final Player player = getOwner();
		if (player.getTarget() instanceof Gatherable)
		{
			final Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}
	
	/**
	 * Updates the passive skills for the owner.<br>
	 * This method iterates through all skills of the {@link Player}.<br>
	 * It triggers the {@code useSkill()} method for every skill marked as passive.
	 */
	public void updatePassiveStats()
	{
		final Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills())
		{
			final Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if ((skill != null) && skill.isPassive())
			{
				skill.useSkill();
			}
		}
	}
	
	/**
	 * Retrieves the {@link Player} that owns this object.<br>
	 * This method casts the result of the parent class's owner retrieval to a {@code Player}.
	 * @return The {@code Player} associated with this object.
	 */
	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}
	
	/**
	 * Handles the restoration of health or other stats.<br>
	 * This method updates the owner's data based on the {@code healType}.<br>
	 * It specifically adds to the DP value if the type is {@code DP}.
	 * @param healType The category of the healing effect.
	 * @param value The amount to restore.
	 */
	@Override
	public void onRestore(HealType healType, int value)
	{
		super.onRestore(healType, value);
		switch (healType)
		{
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @param player
	 * @return
	 */
	// TODO [AT] move to Player
	/**
	 * Checks if the specified player is currently in a duel.<br>
	 * This method verifies the status between the {@code player} and the owner of this object.
	 * @param player The {@link Player} to check for duel status.
	 * @return {@code true} if the players are dueling, {@code false} otherwise.
	 */
	public boolean isDueling(Player player)
	{
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}
	
	// TODO [AT] rename or remove
	/**
	 * Checks if the server is currently shutting down.<br>
	 * This helps determine if new actions should be blocked.
	 * @return {@code true} if a shutdown is in progress, {@code false} otherwise.
	 */
	public boolean isInShutdownProgress()
	{
		return isInShutdownProgress;
	}
	
	// TODO [AT] rename or remove
	/**
	 * Updates the status of whether the system is currently shutting down.<br>
	 * This flag helps manage operations during a server restart.
	 * @param isInShutdownProgress The new shutdown state to set. Set {@code true} if shutting down, or {@code false} otherwise.
	 */
	public void setInShutdownProgress(boolean isInShutdownProgress)
	{
		this.isInShutdownProgress = isInShutdownProgress;
	}
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * This method processes rewards and quest updates based on the selection.
	 * @param dialogId The unique identifier for the current dialog.
	 * @param player The {@link Player} who interacted with the dialog.
	 * @param questId The ID of the quest associated with this interaction.
	 * @param extendedRewardIndex The index of the specific reward to grant.
	 * @param unk An unknown parameter for future use.
	 */
	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex, int unk)
	{
		switch (dialogId)
		{
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore(), player));
				break;
		}
	}
	
	/**
	 * Updates the player's status and statistics after they reach a new level.<br>
	 * This method synchronizes life stats, broadcasts level updates, and handles quest progression.<br>
	 * It also manages skill additions, class change dialogs, and faction updates.
	 */
	public void upgradePlayer()
	{
		final Player player = getOwner();
		final byte level = player.getLevel();
		
		final PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);
		
		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();
		
		// TODO TEST
		final int effectId = player.getRace() == Race.ELYOS ? 0 : 4;
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), effectId, level), true);
		
		// Guides Html on level up
		if (HTMLConfig.ENABLE_GUIDES)
		{
			HTMLService.sendGuideHtml(player);
		}
		
		// Temporal
		ClassChangeService.showClassChangeDialog(player);
		
		if (player.getLevel() == 14)
		{
			switch (player.getRace())
			{
				case ELYOS:
				{
					if (player.getQuestStateList().hasQuest(61601))
					{
						final QuestState qs = player.getQuestStateList().getQuestState(61601);
						if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 0))
						{
							qs.setQuestVar(1);
							PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(61601, qs.getStatus(), qs.getQuestVars().getQuestVars()));
						}
					}
					break;
				}
				case ASMODIANS:
				{
					// TODO
				}
				default:
					break;
			}
		}
		
		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		player.getController().updatePassiveStats();
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		
		// add recipe for morph
		if (level == 10)
		{
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);
		}
		
		SkillLearnService.addNewSkills(player);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
		if (player.isInTeam())
		{
			TeamEffectUpdater.getInstance().startTask(player);
		}
		
		if (player.isLegionMember())
		{
			LegionService.getInstance().updateMemberInfo(player);
		}
		
		/**
		 * Mentor status now cancels automatically as soon as the lowest level group member reaches level 51
		 */
		if (player.isInGroup2() && player.isMentor())
		{
			if (level == 51)
			{
				PlayerGroupService.stopMentoring(player);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_BE_MENTEE_BY_LEVEL_LIMIT);
			}
		}
		
		if ((level >= 66) && (level <= 83))
		{
			reachedPlayerLvl(player);
		}
		
		if (level >= 76)
		{
			AchievementService.getInstance().onLeveUplPlayer(player);
		}
		
		player.getNpcFactions().onLevelUp();
	}
	
	/**
	 * Sends a system message to all players when a character reaches a new level.<br>
	 * This method broadcasts the name and level of the {@code Player}.
	 * @param player The {@link Player} who reached the new level.
	 */
	public static void reachedPlayerLvl(Player player)
	{
		World.getInstance().doOnAllPlayers(players ->
		{
			// "Player Name" has reached level %1
			final byte playerLevel = player.getLevel();
			PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1300086, player.getName(), playerLevel));
		});
	}
	
	/**
	 * Activates the protection state for the owner of this object.<br>
	 * This method triggers a visual blink and cancels active attacks.<br>
	 * It also schedules a task to stop the protection after 60 seconds.
	 */
	public void startProtectionActiveTask()
	{
		if (!getOwner().isProtectionActive())
		{
			TeleportService2.playerTransformation(getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_PROTECTION(60));
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			AttackUtil.cancelCastOn(getOwner());
			AttackUtil.removeTargetFrom(getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_PROTECTION(60000));
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
			final Future<?> task = ThreadPoolManager.getInstance().schedule(() -> stopProtectionActiveTask(), 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}
	
	/**
	 * Stops the active protection task for the player.<br>
	 * This method cancels the {@code TaskId.PROTECTION_ACTIVE} task.<br>
	 * It also updates the player's visual state and sends necessary network packets.
	 */
	public void stopProtectionActiveTask()
	{
		cancelTask(TaskId.PROTECTION_ACTIVE);
		final Player player = getOwner();
		if ((player != null) && player.isSpawned())
		{
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_PROTECTION(0));
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			notifyAIOnMove();
		}
	}
	
	/**
	 * Handles the logic when a player finishes a fly teleport.<br>
	 * It resets the {@code PlayerMode} and states for windstream users.<br>
	 * For other players, it clears flight data and validates the path used.<br>
	 * This method also updates the current zone for the player.
	 */
	public void onFlyTeleportEnd()
	{
		final Player player = getOwner();
		if (player.isInPlayerMode(PlayerMode.WINDSTREAM))
		{
			player.unsetPlayerMode(PlayerMode.WINDSTREAM);
			player.getLifeStats().triggerFpReduce();
			player.unsetState(CreatureState.FLYING);
			player.setState(CreatureState.ACTIVE);
			player.setState(CreatureState.GLIDING);
			player.getGameStats().updateStatsAndSpeedVisually();
		}
		else
		{
			player.unsetState(CreatureState.FLIGHT_TELEPORT);
			player.setFlightTeleportId(0);
			
			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR)
			{
				final long diff = (System.currentTimeMillis() - player.getFlyStartTime());
				final FlyPathEntry path = player.getCurrentFlyPath();
				
				if (player.getWorldId() != path.getEndWorldId())
				{
					AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world " + player.getWorldId() + ". expected " + path.getEndWorldId());
				}
				
				if (diff < path.getTimeInMs())
				{
					AuditLogger.info(player, "Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
					TeleportService2.moveToBindLocation(player, true);
				}
				
				player.setCurrentFlypath(null);
			}
			
			player.setFlightDistance(0);
			player.setState(CreatureState.ACTIVE);
			updateZone();
		}
	}
	
	/**
	 * Adds a specific quantity of an item to the player's quest inventory.<br>
	 * This method calls {@code addQuestItems} to update the data.
	 * @param itemId The unique identifier for the item.
	 * @param count The number of items to add.
	 * @return {@code true} if the items were added successfully, otherwise {@code false}.
	 */
	public boolean addItems(int itemId, int count)
	{
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}
	
	/**
	 * Sets the current stance of the character.<br>
	 * This updates the {@code stance} variable with the provided ID.
	 * @param skillId The unique identifier for the stance to be activated.
	 */
	public void startStance(int skillId)
	{
		stance = skillId;
	}
	
	/**
	 * Stops the current stance for the owner.<br>
	 * This method removes the active effect and updates the state to {@code 0}.<br>
	 * It also sends a {@code SM_PLAYER_STANCE} packet to the player.
	 */
	public void stopStance()
	{
		getOwner().getEffectController().removeEffect(stance);
		PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_STANCE(getOwner(), 0));
		stance = 0;
	}
	
	/**
	 * Retrieves the current stance skill identifier.<br>
	 * This value represents the ID of the active stance for the character.
	 * @return The {@code int} ID of the current stance.
	 */
	public int getStanceSkillId()
	{
		return stance;
	}
	
	/**
	 * Checks if the character is currently in a combat stance.<br>
	 * It returns {@code true} if the stance value is not {@code 0}.<br>
	 * Returns {@code false} if the character is in a neutral state.
	 * @return {@code true} if the character is in a stance, otherwise {@code false}.
	 */
	public boolean isUnderStance()
	{
		return stance != 0;
	}
	
	/**
	 * Updates the soul sickness status for the owner of this object.<br>
	 * This method checks if the player has permission to disable soul sickness.<br>
	 * It increments the death count and triggers the corresponding skill effect.
	 * @param skillId The unique identifier for the skill to be executed. Use 0 to default to skill 8291.
	 */
	public void updateSoulSickness(int skillId)
	{
		final Player player = getOwner();
		final House house = player.getActiveHouse();
		if (house != null)
		{
			switch (house.getHouseType())
			{
				case STUDIO:
				case HOUSE:
				case MANSION:
				case ESTATE:
				case PALACE:
					return;
				default:
					break;
			}
		}
		
		if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS))
		{
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10)
			{
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			}
			
			if (skillId == 0)
			{
				skillId = 8291;
			}
			
			SkillEngine.getInstance().getSkill(player, skillId, deathCount, player).useSkill();
		}
	}
	
	/**
	 * Checks if the player is currently in a state of combat.<br>
	 * It determines this by checking if an attack occurred within the last 10000 milliseconds.
	 * @return {@code true} if the player was attacked or performed an attack recently, {@code false} otherwise.
	 */
	public boolean isInCombat()
	{
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}
	
	/**
	 * Checks if the owner currently has a no death penalty effect active.<br>
	 * It iterates through all effects provided by the {@code EffectController}.
	 * @return {@code true} if any active effect is a no death penalty effect, otherwise {@code false}.
	 */
	public boolean isNoDeathPenaltyInEffect()
	{
		// Check if NoDeathPenalty is active
		final Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect.isNoDeathPenalty())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the player has an active effect that reduces death penalties.<br>
	 * This method iterates through all current effects of the owner.
	 * @return {@code true} if a no death penalty reduction effect is found, otherwise {@code false}.
	 */
	public boolean isNoDeathPenaltyReduceInEffect()
	{
		final Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect.isNoDeathPenaltyReduce())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the owner has an active death penalty reduction effect.<br>
	 * It iterates through all current effects to find a matching one.
	 * @return {@code true} if at least one reduction effect is found, otherwise {@code false}.
	 */
	public boolean isDeathPenaltyReduceInEffect()
	{
		final Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect.isDeathPenaltyReduce())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the {@code NoResurrectPenalty} effect is currently active.<br>
	 * This method iterates through all effects on the owner to find a match.
	 * @return {@code true} if the penalty is in effect, otherwise {@code false}.
	 */
	public boolean isNoResurrectPenaltyInEffect()
	{
		// Check if NoResurrectPenalty is active
		final Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect.isNoResurrectPenalty())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the {@code HiPass} effect is currently active.<br>
	 * It iterates through all effects owned by this object.
	 * @return {@code true} if any active effect is a {@code HiPass} effect, otherwise {@code false}.
	 */
	public boolean isHiPassInEffect()
	{
		// Check if HiPass is active
		final Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext())
		{
			final Effect effect = iterator.next();
			if (effect.isHiPass())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Registers a new {@code Listener} to the controller.<br>
	 * This sets the active listener for handling events.
	 * @param listener The {@code Listener} instance to register.
	 */
	public void registerListener(Listener listener)
	{
		mListener = listener;
	}
	
	/**
	 * Removes the current listener from the system.<br>
	 * This method sets the {@code mListener} field to {@code null}.<br>
	 * Use this to stop receiving updates or events.
	 */
	public void unregisterListener()
	{
		mListener = null;
	}
	
	public static abstract interface Listener
	{
		public abstract void onPlayerUsedSkill(int paramInt, Player paramPlayer);
	}
}
