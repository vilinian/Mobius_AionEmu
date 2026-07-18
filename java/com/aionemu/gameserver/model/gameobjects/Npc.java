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
package com.aionemu.gameserver.model.gameobjects;

import java.util.Iterator;
import java.util.Objects;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AITemplate;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.stats.container.HomingGameStats;
import com.aionemu.gameserver.model.stats.container.NpcGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.npc.NpcRank;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.npc.NpcUiType;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.TribeRelationService;
import com.aionemu.gameserver.spawnengine.WalkerGroup;
import com.aionemu.gameserver.spawnengine.WalkerGroupShift;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.WorldType;

/**
 * This class serves as the base class for all non-player characters in the game.<br>
 * It includes both hostile monsters and interactive citizens that players can talk to.
 * @author Luno
 */
public class Npc extends Creature
{
	private WalkerGroup walkerGroup;
	private boolean isQuestBusy = false;
	private final NpcSkillList skillList;
	private WalkerGroupShift walkerGroupShift;
	private long lastShoutedSeconds;
	private String masterName = "";
	private int creatorId = 0;
	private int townId;
	private byte oldHeading = 0;
	private final ItemAttackType attacktype = ItemAttackType.PHYSICAL;
	private final int aRange = getObjectTemplate().getAggroRange();
	
	/**
	 * Creates a new instance of an {@link Npc}.<br>
	 * This constructor initializes the NPC using its base template level.
	 * @param objId The unique identifier for this specific object.
	 * @param controller The {@link NpcController} that handles the logic for this NPC.
	 * @param spawnTemplate The data containing information about where the NPC spawns.
	 * @param objectTemplate The base template defining the NPC's properties and stats.
	 */
	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate)
	{
		this(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}
	
	/**
	 * Creates a new instance of an {@link Npc} with a specific level.<br>
	 * This constructor initializes the movement and skill systems.<br>
	 * It also sets up the AI engine based on the provided templates.
	 * @param objId The unique identifier for this object.
	 * @param controller The {@link NpcController} that manages this NPC.
	 * @param spawnTemplate The template containing spawn information.
	 * @param objectTemplate The base template defining the NPC's properties.
	 * @param level The character level of the NPC.
	 */
	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		Objects.requireNonNull(objectTemplate, "Npcs should be based on template");
		controller.setOwner(this);
		moveController = new NpcMoveController(this);
		skillList = new NpcSkillList(this);
		setupStatContainers(level);
		
		boolean aiOverride = false;
		if (spawnTemplate.getModel() != null)
		{
			if (spawnTemplate.getModel().getAi() != null)
			{
				aiOverride = true;
				AI2Engine.getInstance().setupAI(spawnTemplate.getModel().getAi(), this);
			}
		}
		
		if (!aiOverride)
		{
			AI2Engine.getInstance().setupAI(objectTemplate.getAi(), this);
		}
		
		lastShoutedSeconds = System.currentTimeMillis() / 1000;
	}
	
	/**
	 * Retrieves the movement controller for this NPC.<br>
	 * This method returns an instance of {@link NpcMoveController}.
	 * @return the {@code NpcMoveController} associated with this object.
	 */
	@Override
	public NpcMoveController getMoveController()
	{
		return (NpcMoveController) super.getMoveController();
	}
	
	/**
	 * Initializes the statistics containers for this object.<br>
	 * It sets up both {@link HomingGameStats} and {@link NpcLifeStats}.
	 * @param level The level of the object.
	 */
	protected void setupStatContainers(byte level)
	{
		setGameStats(new NpcGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}
	
	/**
	 * Retrieves the {@code NpcTemplate} associated with this NPC.<br>
	 * This provides access to the base data for the NPC object.
	 * @return The {@link NpcTemplate} of the NPC.
	 */
	@Override
	public NpcTemplate getObjectTemplate()
	{
		return (NpcTemplate) objectTemplate;
	}
	
	/**
	 * Retrieves the name of the NPC.<br>
	 * This method returns the {@code String} name from the associated {@link NpcTemplate}.
	 * @return The name of the NPC as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return getObjectTemplate().getName();
	}
	
	/**
	 * Retrieves the unique identifier for the NPC template.<br>
	 * This method fetches the ID from the {@link NpcTemplate}.
	 * @return The integer ID of the NPC template.
	 */
	public int getNpcId()
	{
		return getObjectTemplate().getTemplateId();
	}
	
	/**
	 * Retrieves the level of this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return The level as a {@code byte}.
	 */
	@Override
	public byte getLevel()
	{
		return getObjectTemplate().getLevel();
	}
	
	/**
	 * Retrieves the life statistics for this NPC.<br>
	 * This method returns an {@link NpcLifeStats} object containing health data.
	 * @return The {@code NpcLifeStats} container for the current NPC.
	 */
	@Override
	public NpcLifeStats getLifeStats()
	{
		return (NpcLifeStats) super.getLifeStats();
	}
	
	/**
	 * Retrieves the game statistics for this NPC.<br>
	 * This method returns a {@link NpcGameStats} object containing relevant combat and gameplay data.
	 * @return The {@code NpcGameStats} container for this NPC.
	 */
	@Override
	public NpcGameStats getGameStats()
	{
		return (NpcGameStats) super.getGameStats();
	}
	
	/**
	 * Retrieves the {@link NpcController} for this NPC.<br>
	 * This method casts the base controller to its specific type.
	 * @return The {@code NpcController} associated with this object.
	 */
	@Override
	public NpcController getController()
	{
		return (NpcController) super.getController();
	}
	
	/**
	 * Retrieves the attack type for this item.<br>
	 * This method returns a constant value of {@code PHYSICAL}.
	 * @return the {@link ItemAttackType} of the attack.
	 */
	@Override
	public ItemAttackType getAttackType()
	{
		return ai2.modifyAttackType(attacktype);
	}
	
	/**
	 * Retrieves the list of skills for this {@link Npc}.<br>
	 * This method returns the internal {@code skillList} object.
	 * @return The {@code NpcSkillList} containing all skills for this NPC.
	 */
	public NpcSkillList getSkillList()
	{
		return skillList;
	}
	
	/**
	 * Checks if the NPC is capable of performing walking movements.<br>
	 * This returns {@code true} if a walker ID exists or random walk is enabled.<br>
	 * It also checks if {@code ACTIVE_NPC_MOVEMENT} is active.
	 * @return {@code true} if the NPC can move, {@code false} otherwise.
	 */
	public boolean hasWalkRoutes()
	{
		return (getSpawn().getWalkerId() != null) || (getSpawn().hasRandomWalk() && AIConfig.ACTIVE_NPC_MOVEMENT);
	}
	
	/**
	 * Retrieves the {@link TribeClass} of this creature.<br>
	 * This method returns the specific class type assigned to the object.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	@Override
	public TribeClass getTribe()
	{
		final TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null)
		{
			return transformTribe;
		}
		
		return getObjectTemplate().getTribe();
	}
	
	/**
	 * Retrieves the base tribe for this object.<br>
	 * This method looks up the primary tribe based on the current {@link TribeClass}.
	 * @return The base {@link TribeClass} of the creature.
	 */
	@Override
	public TribeClass getBaseTribe()
	{
		return DataManager.TRIBE_RELATIONS_DATA.getBaseTribe(getTribe());
	}
	
	/**
	 * Gets the current aggression range for this NPC.<br>
	 * This value is modified by the {@link AI2Engine}.
	 * @return The calculated aggro range as an {@code int}.
	 */
	public int getAggroRange()
	{
		return ai2.modifyARange(aRange);
	}
	
	/**
	 * Retrieves the rating of this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return the {@code NpcRating} associated with this object.
	 */
	public NpcRating getRating()
	{
		return getObjectTemplate().getRating();
	}
	
	/**
	 * Retrieves the rank of this NPC.<br>
	 * This method fetches the rank from the associated {@link NpcTemplate}.
	 * @return the {@code NpcRank} of the NPC.
	 */
	public NpcRank getRank()
	{
		return getObjectTemplate().getRank();
	}
	
	/**
	 * Retrieves the {@link AbyssNpcType} for this NPC.<br>
	 * This value is fetched from the underlying {@link NpcTemplate}.
	 * @return The {@code AbyssNpcType} associated with this object.
	 */
	public AbyssNpcType getAbyssNpcType()
	{
		return getObjectTemplate().getAbyssNpcType();
	}
	
	/**
	 * Retrieves the health gauge value for this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return The integer value of the HP gauge.
	 */
	public int getHpGauge()
	{
		return getObjectTemplate().getHpGauge();
	}
	
	/**
	 * Checks if the NPC is currently at its designated spawn point.<br>
	 * It returns {@code true} if the distance to the spawn location is less than 3.
	 * @return {@code true} if the NPC is near its spawn point, {@code false} otherwise.
	 */
	public boolean isAtSpawnLocation()
	{
		return getDistanceToSpawnLocation() < 3;
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method checks both directions of hostility between the two creatures.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if either creature considers the other an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return creature.isEnemyFrom(this) || this.isEnemyFrom(creature);
	}
	
	/**
	 * Checks if the specified {@link Creature} is an enemy based on tribe relations.<br>
	 * This method determines if the creature is aggressive or hostile toward this object.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Creature creature)
	{
		return TribeRelationService.isAggressive(creature, this) || TribeRelationService.isHostile(creature, this);
	}
	
	/**
	 * Checks if the provided {@link Npc} is considered an enemy.<br>
	 * This method checks for aggressive or hostile relations between entities.
	 * @param npc The {@code Npc} object to check.
	 * @return {@code true} if the NPC is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Npc npc)
	{
		return TribeRelationService.isAggressive(this, npc) || TribeRelationService.isHostile(this, npc);
	}
	
	/**
	 * Checks if the specified {@link Player} considers this object an enemy.<br>
	 * This method delegates the check to the {@code Player} object.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player considers this object an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Player player)
	{
		return player.isEnemyFrom(this);
	}
	
	/**
	 * Determines the relationship type between this entity and a {@link Creature}.<br>
	 * It checks various conditions like hostility, friendship, and invulnerability.<br>
	 * The result is returned as an integer ID from the {@code CreatureType} enum.
	 * @param creature The {@code Creature} to check against.
	 * @return The integer ID of the determined {@code CreatureType}.
	 */
	@Override
	public int getType(Creature creature)
	{
		int typeForPlayer = -1;
		if (TribeRelationService.isInvulnerable(this, creature))
		{
			typeForPlayer = CreatureType.INVULNERABLE.getId();
		}
		else if (TribeRelationService.isNone(this, creature))
		{
			typeForPlayer = CreatureType.PEACE.getId();
		}
		else if (TribeRelationService.isAggressive(this, creature))
		{
			typeForPlayer = CreatureType.AGGRESSIVE.getId();
		}
		else if (TribeRelationService.isHostile(this, creature))
		{
			typeForPlayer = CreatureType.ATTACKABLE.getId();
		}
		else if (TribeRelationService.isFriend(this, creature) || TribeRelationService.isNeutral(this, creature))
		{
			typeForPlayer = CreatureType.FRIEND.getId();
		}
		else if (TribeRelationService.isSupport(this, creature))
		{
			typeForPlayer = CreatureType.SUPPORT.getId();
		}
		
		if ((typeForPlayer == CreatureType.PEACE.getId()) || (typeForPlayer == CreatureType.SUPPORT.getId()))
		{
			if (getObjectTemplate().isDialogNpc())
			{
				typeForPlayer = CreatureType.FRIEND.getId();
			}
		}
		
		return typeForPlayer;
	}
	
	/**
	 * Calculates the distance between this object and its spawn point.<br>
	 * It uses the coordinates from {@code getSpawn} to find the starting position.<br>
	 * The result is calculated using the {@code MathUtil} utility class.
	 * @return The distance as a {@code double}.
	 */
	public double getDistanceToSpawnLocation()
	{
		return MathUtil.getDistance(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), getX(), getY(), getZ());
	}
	
	/**
	 * Retrieves the current visibility state of the creature.<br>
	 * This value represents how other entities perceive this object.
	 * @return the {@code int} value representing the current {@link CreatureSeeState}.
	 */
	@Override
	public int getSeeState()
	{
		final int skillSeeState = super.getSeeState();
		final int congenitalSeeState = getObjectTemplate().getRating().getCongenitalSeeState().getId();
		return Math.max(skillSeeState, congenitalSeeState);
	}
	
	/**
	 * Checks if the NPC is currently busy with a quest.<br>
	 * This method returns {@code true} if the NPC is occupied.
	 * @return {@code true} if the NPC is busy, {@code false} otherwise.
	 */
	public boolean getIsQuestBusy()
	{
		return isQuestBusy;
	}
	
	/**
	 * Updates the quest status of the NPC.<br>
	 * Sets whether the NPC is currently occupied with a quest.
	 * @param busy The new status to set for the quest activity.
	 */
	public void setIsQuestBusy(boolean busy)
	{
		isQuestBusy = busy;
	}
	
	/**
	 * Retrieves the name of the master associated with this homing object.<br>
	 * This method returns an empty string if no master is found.
	 * @return The name of the master as a {@code String}.
	 */
	public String getMasterName()
	{
		return masterName;
	}
	
	/**
	 * Sets the name of the master for this object.<br>
	 * This updates the {@code masterName} field with the provided value.
	 * @param masterName The name to be assigned as the master.
	 */
	public void setMasterName(String masterName)
	{
		this.masterName = masterName;
	}
	
	/**
	 * Retrieves the unique identifier of the creator.<br>
	 * This value identifies which entity created this object.
	 * @return the {@code int} ID of the creator.
	 */
	public int getCreatorId()
	{
		return creatorId;
	}
	
	/**
	 * Sets the unique identifier for the creator of this object.<br>
	 * This value is stored in the {@code creatorId} field.
	 * @param creatorId The unique ID of the creator.
	 */
	public void setCreatorId(int creatorId)
	{
		this.creatorId = creatorId;
	}
	
	/**
	 * Retrieves the unique identifier for the town associated with this object.<br>
	 * This value is used to determine which town area the entity belongs to.
	 * @return the {@code int} ID of the town.
	 */
	public int getTownId()
	{
		return townId;
	}
	
	/**
	 * Sets the unique identifier for the town.<br>
	 * This updates the {@code townId} field of this object.
	 * @param townId The unique ID of the town to assign.
	 */
	public void setTownId(int townId)
	{
		this.townId = townId;
	}
	
	/**
	 * Retrieves the object that created this instance.<br>
	 * This method currently returns {@code null}.
	 * @return the creator of this object, or {@code null} if no creator exists.
	 */
	public VisibleObject getCreator()
	{
		return null;
	}
	
	/**
	 * Sets the current target for this object.<br>
	 * This method updates the target and resets related statistics.<br>
	 * It also triggers a broadcast to notify other clients if the object is alive.
	 * @param creature The {@code VisibleObject} to set as the new target.
	 */
	@Override
	public void setTarget(VisibleObject creature)
	{
		if (getTarget() != creature)
		{
			super.setTarget(creature);
			super.clearAttackedCount();
			getGameStats().renewLastChangeTargetTime();
			if (!getLifeStats().isAlreadyDead())
			{
				PacketSendUtility.broadcastPacket(this, new SM_LOOKATOBJECT(this));
			}
		}
	}
	
	/**
	 * Sets the {@code WalkerGroup} for this object.<br>
	 * This method assigns a specific group to manage walking behavior.
	 * @param wg The {@link WalkerGroup} to assign.
	 */
	public void setWalkerGroup(WalkerGroup wg)
	{
		walkerGroup = wg;
	}
	
	/**
	 * Retrieves the {@code WalkerGroup} associated with this NPC.<br>
	 * This group defines how the NPC moves and navigates through the world.
	 * @return The current {@link WalkerGroup} object.
	 */
	public WalkerGroup getWalkerGroup()
	{
		return walkerGroup;
	}
	
	/**
	 * Sets the group shift for this walker.<br>
	 * This updates the {@code walkerGroupShift} field with a new value.
	 * @param shift The new {@link WalkerGroupShift} to apply.
	 */
	public void setWalkerGroupShift(WalkerGroupShift shift)
	{
		walkerGroupShift = shift;
	}
	
	/**
	 * Retrieves the current shift of the walker group.<br>
	 * This value determines which movement pattern the NPC follows.
	 * @return the {@code WalkerGroupShift} object.
	 */
	public WalkerGroupShift getWalkerGroupShift()
	{
		return walkerGroupShift;
	}
	
	/**
	 * Checks if the current object is marked as a flag.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the object is a flag, otherwise {@code false}.
	 */
	@Override
	public boolean isFlag()
	{
		return (getObjectTemplate().getNpcTemplateType() == NpcTemplateType.FLAG) && (getObjectTemplate().getNpcTemplateType() == NpcTemplateType.MONSTER) && (getObjectTemplate().getNpcTemplateType() == NpcTemplateType.RAID_MONSTER);
	}
	
	/**
	 * Checks if the NPC is a boss monster.<br>
	 * This method returns {@code true} if the rating is {@code HERO} or {@code LEGENDARY}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the NPC is a boss, {@code false} otherwise.
	 */
	public boolean isBoss()
	{
		return (getObjectTemplate().getRating() == NpcRating.HERO) || (getObjectTemplate().getRating() == NpcRating.LEGENDARY);
	}
	
	/**
	 * Checks if the NPC has a static ID.<br>
	 * This method returns {@code true} if the spawn location has a non-zero static ID.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the NPC is static, {@code false} otherwise.
	 */
	public boolean hasStatic()
	{
		return getSpawn().getStaticId() != 0;
	}
	
	/**
	 * Retrieves the {@code Race} of the NPC.<br>
	 * This method returns the race defined in the object template.
	 * @return The {@link Race} of the NPC.
	 */
	@Override
	public Race getRace()
	{
		return getObjectTemplate().getRace();
	}
	
	/**
	 * Retrieves the UI type for this NPC.<br>
	 * This value is fetched from the {@link NpcTemplate}.
	 * @return the {@code NpcUiType} associated with this NPC.
	 */
	public NpcUiType getUiType()
	{
		return getObjectTemplate().getNpcUiType();
	}
	
	/**
	 * Retrieves the drop information for this NPC.<br>
	 * This method fetches data from the {@link NpcTemplate}.
	 * @return the {@code NpcDrop} object associated with this NPC.
	 */
	public NpcDrop getNpcDrop()
	{
		return getObjectTemplate().getNpcDrop();
	}
	
	/**
	 * Updates the {@code type} of this NPC.<br>
	 * This method changes the internal category identifier for the object.
	 * @param newType The new integer value to assign to the NPC type.
	 */
	public void setNpcType(int newType)
	{
		type = newType;
	}
	
	/**
	 * Checks if the NPC is eligible to reward AP.<br>
	 * This method evaluates conditions based on the NPC type, world type, and AI settings.<br>
	 * It returns {@code true} for siege NPCs or specific Abyss and Balaurea creatures.
	 * @return {@code true} if the NPC can reward AP, {@code false} otherwise.
	 */
	public boolean isRewardAP()
	{
		if (this instanceof SiegeNpc)
		{
			return true;
		}
		else if (getWorldType() == WorldType.ABYSS)
		{
			return true;
		}
		else if (getAi2().ask(AIQuestion.SHOULD_REWARD_AP).isPositive())
		{
			return true;
		}
		else if (getWorldType() == WorldType.BALAUREA)
		{
			return (getRace() == Race.DRAKAN) || (getRace() == Race.LIZARDMAN);
		}
		
		return false;
	}
	
	/**
	 * Checks if the NPC is allowed to perform a shout action.<br>
	 * It verifies that shout data exists for this NPC and map.<br>
	 * It also ensures enough time has passed since the last shout.
	 * @param delaySeconds The minimum number of seconds required between shouts.
	 * @return {@code true} if the NPC can shout now, {@code false} otherwise.
	 */
	public boolean mayShout(int delaySeconds)
	{
		if (!DataManager.NPC_SHOUT_DATA.hasAnyShout(getPosition().getMapId(), getNpcId()))
		{
			return false;
		}
		
		return ((System.currentTimeMillis() - lastShoutedSeconds) / 1000) >= delaySeconds;
	}
	
	/**
	 * Triggers a specific shout for an {@link Npc}.<br>
	 * This method checks if the conditions for the shout are met.<br>
	 * It then schedules a system message to be sent after a delay.
	 * @param shout The {@link NpcShout} template containing the shout data.
	 * @param target The {@link Creature} that should receive the shout or be checked for range.
	 * @param param An optional object used as an additional parameter for the message.
	 * @param delaySeconds The time in seconds to wait before sending the message.
	 */
	public void shout(NpcShout shout, Creature target, Object param, int delaySeconds)
	{
		if (((shout.getWhen() != ShoutEventType.DIED) && (shout.getWhen() != ShoutEventType.BEFORE_DESPAWN) && getLifeStats().isAlreadyDead()) || !mayShout(delaySeconds))
		{
			return;
		}
		
		if ((shout.getPattern() != null) && !((AITemplate) getAi2()).onPatternShout(shout.getWhen(), shout.getPattern(), shout.getSkillNo()))
		{
			return;
		}
		
		final int shoutRange = getObjectTemplate().getMinimumShoutRange();
		if (((shout.getShoutType() == ShoutType.SAY) && !(target instanceof Player)) || ((target != null) && !MathUtil.isIn3dRange(target, this, shoutRange)))
		{
			return;
		}
		
		final Npc thisNpc = this;
		final SM_SYSTEM_MESSAGE message = new SM_SYSTEM_MESSAGE(true, shout.getStringId(), getObjectId(), 1, param);
		lastShoutedSeconds = System.currentTimeMillis() / 1000;
		
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (thisNpc.getLifeStats().isAlreadyDead() && (shout.getWhen() != ShoutEventType.DIED) && (shout.getWhen() != ShoutEventType.BEFORE_DESPAWN))
			{
				return;
			}
			
			// message for the specific player (when IDLE we are already broadcasting!!!)
			if ((shout.getShoutType() == ShoutType.SAY) || (shout.getWhen() == ShoutEventType.IDLE))
			{
				// [RR] Should we have lastShoutedSeconds separated from broadcasts (??)
				PacketSendUtility.sendPacket((Player) target, message);
			}
			else
			{
				final Iterator<Player> iter = thisNpc.getKnownList().getKnownPlayers().values().iterator();
				while (iter.hasNext())
				{
					final Player kObj = iter.next();
					if (kObj.getLifeStats().isAlreadyDead() || !kObj.isOnline())
					{
						continue;
					}
					
					if (MathUtil.isIn3dRange(kObj, thisNpc, shoutRange))
					{
						PacketSendUtility.sendPacket(kObj, message);
					}
				}
			}
		}, delaySeconds * 1000);
	}
	
	/**
	 * Retrieves the previous heading of the NPC.<br>
	 * This value represents the direction before the most recent update.
	 * @return the {@code byte} representing the old heading.
	 */
	public byte getOldHeading()
	{
		return oldHeading;
	}
	
	/**
	 * Updates the previous heading of the NPC.<br>
	 * This value is used to track where the entity was facing before a movement change.
	 * @param oldHeading The {@code byte} value representing the previous direction.
	 */
	public void setOldHeading(byte oldHeading)
	{
		this.oldHeading = oldHeading;
	}
}
