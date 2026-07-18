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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.service.PlayerTeamDistributionService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.achievement.AchievementActionType;
import com.aionemu.gameserver.model.templates.npc.NpcRank;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.DialogService;
import com.aionemu.gameserver.services.MinionService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.services.player.AchievementService;
import com.aionemu.gameserver.services.player.PlayerFameService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class handles the logic and behavior for {@link Npc} entities.<br>
 * It manages how non-player characters interact with the game world and players.
 * @author -Nemesiss-, ATracer (2009-09-29), Sarynth modified by Wakizashi
 */
public class NpcController extends CreatureController<Npc>
{
	private static final Logger log = LoggerFactory.getLogger(NpcController.class);
	
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
		if (object instanceof Creature)
		{
			getOwner().getAi2().onCreatureEvent(AIEventType.CREATURE_NOT_SEE, (Creature) object);
			getOwner().getAggroList().remove((Creature) object);
		}
		
		// TODO not see player ai event
	}
	
	/**
	 * This method handles the logic when an entity is seen by this object.<br>
	 * It triggers specific AI events based on the type of {@link VisibleObject}.<br>
	 * If a dead owner sees a {@link Player}, it notifies the {@code DropService}.
	 * @param object The {@code VisibleObject} that has been detected.
	 */
	@Override
	public void see(VisibleObject object)
	{
		super.see(object);
		final Npc owner = getOwner();
		if (object instanceof Creature)
		{
			final Creature creature = (Creature) object;
			owner.getAi2().onCreatureEvent(AIEventType.CREATURE_SEE, creature);
		}
		
		if (object instanceof Player)
		{
			// TODO see player ai event
			if (owner.getLifeStats().isAlreadyDead())
			{
				DropService.getInstance().see((Player) object, owner);
			}
		}
		else if (object instanceof Summon)
		{
			// TODO see summon ai event
		}
	}
	
	/**
	 * This method is called before an {@code Npc} object is spawned.<br>
	 * It initializes the life stats and sets the initial state of the owner.<br>
	 * It also triggers a respawn event in the {@link AIEventType} system.
	 */
	@Override
	public void onBeforeSpawn()
	{
		super.onBeforeSpawn();
		final Npc owner = getOwner();
		
		// set state from npc templates
		if (owner.getObjectTemplate().getState() != 0)
		{
			owner.setState(owner.getObjectTemplate().getState());
		}
		else
		{
			owner.setState(CreatureState.NPC_IDLE);
		}
		
		owner.getLifeStats().setCurrentHpPercent(100);
		owner.getLifeStats().setCurrentMpPercent(100);
		owner.getAi2().onGeneralEvent(AIEventType.RESPAWNED);
		
		if (owner.getSpawn().canFly())
		{
			owner.setState(CreatureState.FLYING);
		}
		
		if (owner.getSpawn().getState() != 0)
		{
			owner.setState(owner.getSpawn().getState());
		}
	}
	
	/**
	 * This method is called after the object has been spawned.<br>
	 * It triggers a revalidation of zones for the owner.
	 */
	@Override
	public void onAfterSpawn()
	{
		super.onAfterSpawn();
		getOwner().getAi2().onGeneralEvent(AIEventType.SPAWNED);
	}
	
	/**
	 * Handles the logic when a creature is despawned.<br>
	 * This method cancels the {@code DECAY} task.<br>
	 * It also clears the aggro list and observation controller of the owner if they are still spawned.
	 */
	@Override
	public void onDespawn()
	{
		final Npc owner = getOwner();
		DropService.getInstance().unregisterDrop(getOwner());
		owner.getAi2().onGeneralEvent(AIEventType.DESPAWNED);
		super.onDespawn();
	}
	
	/**
	 * Sends a system message to the {@code player} when an expert NPC is defeated.<br>
	 * This method checks if the owner's rank is {@code EXPERT}.<br>
	 * It then sends a specific description based on the NPC name ID.
	 * @param player The {@link Player} who defeated the NPC.
	 */
	public void defeatNamedMsg(Player player)
	{
		final Npc owner = getOwner();
		final int npcNameId = owner.getObjectTemplate().getNameId();
		final NpcRank npcRank = owner.getObjectTemplate().getRank();
		if (npcRank == NpcRank.EXPERT)
		{
			World.getInstance().doOnAllPlayers(players -> PacketSendUtility.sendPacket(players, new SM_SYSTEM_MESSAGE(1400021, player.getName(), new DescriptionId((npcNameId * 2) + 1))));
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
		final Npc owner = getOwner();
		if (owner.getSpawn().hasPool())
		{
			owner.getSpawn().setUse(owner.getInstanceId(), false);
		}
		
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0, owner.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()));
		
		try
		{
			if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD))
			{
				doReward();
			}
			
			if (owner.getPosition().isInstanceMap())
			{
				owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
			else
			{
				owner.getPosition().getWorld().getWorldMap(owner.getWorldId()).getWorldHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
		}
		finally
		{
			// always make sure npc is schedulled to respawn
			if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY))
			{
				addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
			}
			
			if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN) && !owner.isDeleteDelayed() && !SiegeService.getInstance().isSiegeNpcInActiveSiege(owner))
			{
				final Future<?> task = scheduleRespawn();
				if (task != null)
				{
					addTask(TaskId.RESPAWN, task);
				}
			}
			else if (!hasScheduledTask(TaskId.DECAY))
			{
				onDelete();
			}
		}
		
		super.onDie(lastAttacker);
	}
	
	/**
	 * Handles the logic when a creature dies while under a silence effect.<br>
	 * This method stops all movement and cancels active casting for the owner.<br>
	 * It removes all current effects and updates the {@code CreatureState}.<br>
	 * The state is set to {@code DEAD} or {@code FLOATING_CORPSE} based on the creature type.
	 */
	@Override
	public void onDieSilence()
	{
		final Npc owner = getOwner();
		if (owner.getSpawn().hasPool())
		{
			owner.getSpawn().setUse(owner.getInstanceId(), false);
		}
		
		try
		{
			if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD))
			{
				doReward();
			}
			
			if (owner.getPosition().isInstanceMap())
			{
				owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
			else
			{
				owner.getPosition().getWorld().getWorldMap(owner.getWorldId()).getWorldHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
		}
		finally
		{
			// always make sure npc is schedulled to respawn
			if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY))
			{
				addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
			}
			
			if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN) && !owner.isDeleteDelayed() && !SiegeService.getInstance().isSiegeNpcInActiveSiege(owner))
			{
				final Future<?> task = scheduleRespawn();
				if (task != null)
				{
					addTask(TaskId.RESPAWN, task);
				}
			}
			else if (!hasScheduledTask(TaskId.DECAY))
			{
				onDelete();
			}
		}
		
		super.onDieSilence();
	}
	
	/**
	 * Processes the rewards for completing a task.<br>
	 * This method handles giving items or other benefits to the player.<br>
	 * It is called after a successful action or event.
	 */
	@Override
	public void doReward()
	{
		super.doReward();
		final AggroList list = getOwner().getAggroList();
		final Collection<AggroInfo> finalList = list.getFinalDamageList(true);
		if (getOwner() instanceof SiegeNpc)
		{
			rewardSiegeNpc();
		}
		
		final AionObject winner = list.getMostDamage();
		if (winner == null)
		{
			return;
		}
		
		float totalDmg = 0;
		for (AggroInfo info : finalList)
		{
			totalDmg += info.getDamage();
		}
		
		if (totalDmg <= 0)
		{
			log.warn("WARN total damage to " + getOwner().getName() + " is " + totalDmg + " reward process was skiped!");
			return;
		}
		
		for (AggroInfo info : finalList)
		{
			final AionObject attacker = info.getAttacker();
			
			// We are not reward Npc's
			if (attacker instanceof Npc)
			{
				continue;
			}
			
			final float percentage = info.getDamage() / totalDmg;
			if (percentage > 1)
			{
				log.warn("WARN BIG REWARD PERCENTAGE: " + percentage + " damage: " + info.getDamage() + " total damage: " + totalDmg + " name: " + info.getAttacker().getName() + " obj: " + info.getAttacker().getObjectId() + " owner: " + getOwner().getName() + " player was skiped");
				continue;
			}
			
			if (attacker instanceof TemporaryPlayerTeam<?>)
			{
				PlayerTeamDistributionService.doReward((TemporaryPlayerTeam<?>) attacker, percentage, getOwner(), winner);
			}
			else if ((attacker instanceof Player) && ((Player) attacker).isInGroup2())
			{
				PlayerTeamDistributionService.doReward(((Player) attacker).getPlayerGroup2(), percentage, getOwner(), winner);
			}
			else if (attacker instanceof Player)
			{
				final Player player = (Player) attacker;
				if (!player.getLifeStats().isAlreadyDead())
				{
					// Reward init
					long rewardXp = StatFunctions.calculateSoloExperienceReward(player, getOwner());
					int rewardDp = StatFunctions.calculateSoloDPReward(player, getOwner());
					float rewardAp = 1;
					
					// Dmg percent correction
					rewardXp *= percentage;
					rewardDp *= percentage;
					rewardAp *= percentage;
					
					QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0, 0));
					
					// When a player defeat a "Boss" all ppls on server see!!!
					defeatNamedMsg(player);
					
					AchievementService.getInstance().onUpdateAchievementAction(player, getOwner().getObjectTemplate().getTemplateId(), 1, AchievementActionType.HUNT);
					PlayerFameService.getInstance().addFameExp(player, (10 * getOwner().getLevel()) / 2);
					switch (player.getWorldId())
					{
						case 210050000:
						case 220070000:
						case 600010000:
							AbyssPointsService.addAp(player, getOwner(), Rnd.get(60, 100));
							break;
						case 600040000:
						case 800030000:
						case 800040000:
						case 800050000:
						case 800060000:
						case 800070000:
							AbyssPointsService.addGp(player, Rnd.get(10, 60));
							break;
					}
					
					player.getCommonData().addExp(rewardXp, RewardType.HUNTING, getOwner().getObjectTemplate().getNameId());
					player.getCommonData().addDp(rewardDp);
					if (getOwner().isRewardAP())
					{
						final int calculatedAp = StatFunctions.calculatePvEApGained(player, getOwner());
						rewardAp *= calculatedAp;
						if (rewardAp >= 1)
						{
							player.getCommonData().addSilverStarEnergy(1500); // 0.15%
							AbyssPointsService.addAp(player, getOwner(), (int) rewardAp);
						}
					}
					
					if (attacker.equals(winner))
					{
						DropRegistrationService.getInstance().registerDrop(getOwner(), player, player.getLevel(), null);
					}
					
					// Growth Energy + Berdin's Star
					if (getOwner().getLevel() >= 66)
					{
						if (Rnd.chance(RateConfig.GROWTH_ENERGY))
						{
							player.getCommonData().addGrowthEnergy(1060000 * 8);
							PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
						}
					}
					
					if (player.getMinion() == null)
					{
						continue;
					}
					
					MinionService.getInstance().onUpdateEnergy(player, 50);
				}
			}
		}
	}
	
	/**
	 * Retrieves the {@link Npc} object that owns this AI instance.<br>
	 * This method returns the underlying NPC entity associated with the AI.
	 * @return The {@code Npc} owner of this AI.
	 */
	@Override
	public Npc getOwner()
	{
		return (Npc) super.getOwner();
	}
	
	/**
	 * Handles the request from a {@link Player} to start a dialog.<br>
	 * This method is triggered when a player interacts with an NPC.
	 * @param player The {@code Player} object who initiated the request.
	 */
	@Override
	public void onDialogRequest(Player player)
	{
		// notify npc dialog request observer
		if (!getOwner().getObjectTemplate().canInteract())
		{
			return;
		}
		
		player.getObserveController().notifyRequestDialogObservers(getOwner());
		
		getOwner().getAi2().onCreatureEvent(AIEventType.DIALOG_START, player);
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
		final QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		if (!MathUtil.isInRange(getOwner(), player, getOwner().getObjectTemplate().getTalkDistance() + 2) && !QuestEngine.getInstance().onDialog(env))
		{
			return;
		}
		
		if (!getOwner().getAi2().onDialogSelect(player, dialogId, questId, extendedRewardIndex))
		{
			DialogService.onDialogSelect(dialogId, player, getOwner(), questId, extendedRewardIndex);
		}
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
		
		final Creature actingCreature;
		
		// summon should gain its own aggro
		if (creature instanceof Summon)
		{
			actingCreature = creature;
		}
		else
		{
			actingCreature = creature.getActingCreature();
		}
		
		super.onAttack(actingCreature, skillId, type, damage, notifyAttack, log);
		
		final Npc npc = getOwner();
		
		if (actingCreature instanceof Player)
		{
			QuestEngine.getInstance().onAttack(new QuestEnv(npc, (Player) actingCreature, 0, 0));
		}
		
		PacketSendUtility.broadcastPacket(npc, new SM_ATTACK_STATUS(npc, actingCreature, type, skillId, damage, log));
	}
	
	/**
	 * This method is called when a creature stops moving.<br>
	 * It triggers the {@code notifyAIOnMove} method to update the AI state.
	 */
	@Override
	public void onStopMove()
	{
		getOwner().getMoveController().setInMove(false);
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
		super.onStartMove();
	}
	
	/**
	 * Triggers the logic for an entity returning to its home location.<br>
	 * This method is called when a creature decides to head back to its spawn point.
	 */
	@Override
	public void onReturnHome()
	{
		if (getOwner().isDeleteDelayed())
		{
			onDelete();
		}
		
		super.onReturnHome();
	}
	
	/**
	 * This method is called when an entity enters a new zone.<br>
	 * It handles the logic required for transitioning into a {@code ZoneInstance}.
	 * @param zoneInstance The instance of the zone that was entered.
	 */
	@Override
	public void onEnterZone(ZoneInstance zoneInstance)
	{
		if (zoneInstance.getAreaTemplate().getZoneName() == null)
		{
			log.error("No name found for a Zone in the map " + zoneInstance.getAreaTemplate().getWorldId());
		}
	}
	
	/**
	 * Calculates and distributes rewards for players who attacked the siege NPC.<br>
	 * It determines the reward amount based on the percentage of total damage dealt.<br>
	 * Rewards include Abyss Points and Silver Star Energy for individual players, groups, or alliances.<br>
	 * This method also updates the {@code HUNT_SIEGE} achievement progress.
	 */
	private void rewardSiegeNpc()
	{
		final int totalDamage = getOwner().getAggroList().getTotalDamage();
		for (AggroInfo aggro : getOwner().getAggroList().getFinalDamageList(true))
		{
			final float percentage = aggro.getDamage() / totalDamage;
			List<Player> players = new ArrayList<>();
			if (aggro.getAttacker() instanceof Player)
			{
				final Player player = (Player) aggro.getAttacker();
				if (MathUtil.isIn3dRange(player, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !player.getLifeStats().isAlreadyDead())
				{
					final int apPlayerReward = Math.round(StatFunctions.calculatePvEApGained(player, getOwner()) * percentage);
					AbyssPointsService.addAp(player, getOwner(), apPlayerReward);
					AchievementService.getInstance().onUpdateAchievementAction(player, getOwner().getNpcId(), 1, AchievementActionType.HUNT_SIEGE);
				}
			}
			else if (aggro.getAttacker() instanceof PlayerGroup)
			{
				final PlayerGroup group = (PlayerGroup) aggro.getAttacker();
				for (Player member : group.getMembers())
				{
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !member.getLifeStats().isAlreadyDead())
					{
						players.add(member);
					}
				}
				
				if (!players.isEmpty())
				{
					for (Player member : players)
					{
						final int baseApReward = StatFunctions.calculatePvEApGained(member, getOwner());
						final int apRewardPerMember = Math.round((baseApReward * percentage) / players.size());
						if (apRewardPerMember > 0)
						{
							member.getCommonData().addSilverStarEnergy(1500); // 0.15%
							AbyssPointsService.addAp(member, getOwner(), apRewardPerMember);
							AchievementService.getInstance().onUpdateAchievementAction(member, getOwner().getNpcId(), 1, AchievementActionType.HUNT_SIEGE);
						}
					}
				}
			}
			else if ((aggro.getAttacker() instanceof PlayerAlliance))
			{
				final PlayerAlliance alliance = (PlayerAlliance) aggro.getAttacker();
				players = new ArrayList<>();
				for (Player member : alliance.getMembers())
				{
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !member.getLifeStats().isAlreadyDead())
					{
						players.add(member);
					}
				}
				
				if (!players.isEmpty())
				{
					for (Player member : players)
					{
						final int baseApReward = StatFunctions.calculatePvEApGained(member, getOwner());
						final int apRewardPerMember = Math.round((baseApReward * percentage) / players.size());
						if (apRewardPerMember > 0)
						{
							member.getCommonData().addSilverStarEnergy(1500); // 0.15%
							AbyssPointsService.addAp(member, getOwner(), apRewardPerMember);
							AchievementService.getInstance().onUpdateAchievementAction(member, getOwner().getNpcId(), 1, AchievementActionType.HUNT_SIEGE);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Schedules a task to respawn the owner of this object.<br>
	 * This method checks if the owner's spawn settings allow for respawning.<br>
	 * If allowed, it returns a {@code Future} from {@link RespawnService}.<br>
	 * Returns {@code null} if respawning is disabled.
	 * @return A {@code Future} representing the respawn task, or {@code null} if no task is scheduled.
	 */
	public Future<?> scheduleRespawn()
	{
		if (!getOwner().getSpawn().isNoRespawn())
		{
			return RespawnService.scheduleRespawnTask(getOwner());
		}
		
		return null;
	}
	
	/**
	 * Calculates the current attack distance to a target.<br>
	 * This method retrieves the range from the owner's game statistics.<br>
	 * The value is converted from an integer to a float by dividing by {@code 1000f}.
	 * @return The calculated attack distance as a {@code float}.
	 */
	public float getAttackDistanceToTarget()
	{
		return getOwner().getGameStats().getAttackRange().getCurrent() / 1000f;
	}
	
	/**
	 * Attempts to execute a specific skill for the owner.<br>
	 * This method retrieves the skill from {@code SkillEngine}.<br>
	 * It returns the result of the skill execution if successful.
	 * @param skillId The unique identifier for the skill to be used.
	 * @param skillLevel The level of the skill to be executed.
	 * @return {@code true} if the skill was successfully used, or {@code false} otherwise.
	 */
	@Override
	public boolean useSkill(int skillId, int skillLevel)
	{
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (!getOwner().isSkillDisabled(skillTemplate))
		{
			getOwner().getGameStats().renewLastSkillTime();
			return super.useSkill(skillId, skillLevel);
		}
		
		return false;
	}
}
