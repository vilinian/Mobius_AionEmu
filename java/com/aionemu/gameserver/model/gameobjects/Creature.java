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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.ObserveController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.controllers.movement.MoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a movable object within the game world.<br>
 * This serves as the base class for all entities capable of movement, such as {@link Player} and NPCs.
 * @author -Nemesiss-
 */
public abstract class Creature extends VisibleObject
{
	private static final Logger log = LoggerFactory.getLogger(Creature.class);
	protected AI2 ai2;
	private boolean isDespawnDelayed = false;
	private CreatureLifeStats<? extends Creature> lifeStats;
	private CreatureGameStats<? extends Creature> gameStats;
	private EffectController effectController;
	protected MoveController moveController;
	private int state = CreatureState.ACTIVE.getId();
	private int visualState = CreatureVisualState.VISIBLE.getId();
	private int seeState = CreatureSeeState.NORMAL.getId();
	private Skill castingSkill;
	private Map<Integer, Long> skillCoolDowns;
	private Map<Integer, Long> skillCoolDownsBase;
	private final ObserveController observeController;
	private TransformModel transformModel;
	private final AggroList aggroList;
	private byte adminFlags = 0;
	private Item usingItem;
	private final transient byte[] zoneTypes = new byte[ZoneType.values().length];
	private int skillNumber;
	private int attackedCount;
	private final long spawnTime = System.currentTimeMillis();
	protected int type = CreatureType.NULL.getId();
	private TribeClass tribe = TribeClass.GENERAL;
	
	/**
	 * Creates a new instance of a {@link Creature}.<br>
	 * This constructor initializes the basic properties and internal controllers.<br>
	 * It also sets up the visual model based on the provided templates.
	 * @param objId The unique identifier for the creature.
	 * @param controller The {@link CreatureController} that handles creature logic.
	 * @param spawnTemplate The template containing data about how the creature spawns.
	 * @param objectTemplate The visual and physical properties of the creature.
	 * @param position The initial coordinates of the creature in the world.
	 */
	public Creature(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, WorldPosition position)
	{
		super(objId, controller, spawnTemplate, objectTemplate, position);
		observeController = new ObserveController();
		setTransformModel(new TransformModel(this));
		if ((spawnTemplate != null) && (spawnTemplate.getModel() != null))
		{
			if (spawnTemplate.getModel().getTribe() != null)
			{
				getTransformModel().setTribe(spawnTemplate.getModel().getTribe(), true);
			}
		}
		
		aggroList = createAggroList();
	}
	
	/**
	 * Retrieves the {@link MoveController} for this creature.<br>
	 * This controller handles all movement logic and pathfinding.
	 * @return the {@code MoveController} instance.
	 */
	public MoveController getMoveController()
	{
		return moveController;
	}
	
	/**
	 * Creates a new {@link AggroList} for this creature.<br>
	 * This method initializes the list using the current object instance.
	 * @return A new {@code AggroList} associated with this creature.
	 */
	protected AggroList createAggroList()
	{
		return new AggroList(this);
	}
	
	/**
	 * Retrieves the controller associated with this creature.<br>
	 * This method returns a {@link CreatureController} instance.
	 * @return The {@code CreatureController} for this object.
	 */
	@Override
	public CreatureController<? extends Creature> getController()
	{
		return (CreatureController<?>) super.getController();
	}
	
	/**
	 * Retrieves the current life statistics for this {@link Creature}.<br>
	 * This provides access to health and related vital data.
	 * @return the {@code CreatureLifeStats} object associated with this creature.
	 */
	public CreatureLifeStats<? extends Creature> getLifeStats()
	{
		return lifeStats;
	}
	
	/**
	 * Updates the {@code lifeStats} of this creature.<br>
	 * This method replaces the current statistics with a new {@link CreatureLifeStats} object.
	 * @param lifeStats The new life statistics to assign.
	 */
	public void setLifeStats(CreatureLifeStats<? extends Creature> lifeStats)
	{
		this.lifeStats = lifeStats;
	}
	
	/**
	 * Retrieves the current game statistics for this creature.<br>
	 * This method returns a {@link CreatureGameStats} object containing relevant data.
	 * @return The {@code CreatureGameStats} associated with this creature.
	 */
	public CreatureGameStats<? extends Creature> getGameStats()
	{
		return gameStats;
	}
	
	/**
	 * Updates the {@code gameStats} for this creature.<br>
	 * This method assigns a new {@link CreatureGameStats} object to the entity.
	 * @param gameStats The new stats container to apply.
	 */
	public void setGameStats(CreatureGameStats<? extends Creature> gameStats)
	{
		this.gameStats = gameStats;
	}
	
	public abstract byte getLevel();
	
	/**
	 * Retrieves the {@link EffectController} for this object.<br>
	 * This controller manages all visual and status effects.
	 * @return the current {@code EffectController} instance.
	 */
	public EffectController getEffectController()
	{
		return effectController;
	}
	
	/**
	 * Sets the {@link EffectController} for this object.<br>
	 * This method updates the controller responsible for managing effects.
	 * @param effectController The new {@code EffectController} to assign.
	 */
	public void setEffectController(EffectController effectController)
	{
		this.effectController = effectController;
	}
	
	/**
	 * Retrieves the {@link AI2} instance for this creature.<br>
	 * If no {@code AI2} is assigned, it creates a new one using {@link AI2Engine}.
	 * @return The current {@code AI2} object.
	 */
	public AI2 getAi2()
	{
		return ai2 != null ? ai2 : AI2Engine.getInstance().setupAI("dummy", this);
	}
	
	/**
	 * Sets the {@link AI2} instance for this creature.<br>
	 * This method updates the internal {@code ai2} field.
	 * @param ai2 The new {@code AI2} object to assign.
	 */
	public void setAi2(AI2 ai2)
	{
		this.ai2 = ai2;
	}
	
	/**
	 * Checks if the deletion of this object is delayed.<br>
	 * This method returns {@code true} if the despawn process is postponed.
	 * @return {@code true} if the delete action is delayed, {@code false} otherwise.
	 */
	public boolean isDeleteDelayed()
	{
		return isDespawnDelayed;
	}
	
	/**
	 * Sets whether the despawn process should be delayed.<br>
	 * This determines if the object is removed immediately or after a delay.
	 * @param delayed The boolean value to set for the despawn delay.
	 */
	public void setDespawnDelayed(boolean delayed)
	{
		isDespawnDelayed = delayed;
	}
	
	/**
	 * Checks if the current object is marked as a flag.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the object is a flag, otherwise {@code false}.
	 */
	public boolean isFlag()
	{
		return false;
	}
	
	/**
	 * Checks if the creature is currently performing a skill cast.<br>
	 * It returns {@code true} if a skill is being cast.<br>
	 * It returns {@code false} if no skill is being cast.
	 * @return {@code true} if casting, {@code false} otherwise.
	 */
	public boolean isCasting()
	{
		return castingSkill != null;
	}
	
	/**
	 * Sets the current skill being cast by this creature.<br>
	 * If the provided {@code Skill} is not {@code null}, it increments the internal skill counter.
	 * @param castingSkill The {@link Skill} object to set as the active casting skill.
	 */
	public void setCasting(Skill castingSkill)
	{
		if (castingSkill != null)
		{
			skillNumber++;
		}
		
		this.castingSkill = castingSkill;
	}
	
	/**
	 * Retrieves the unique identifier for the skill currently being cast.<br>
	 * It checks if a {@code Skill} is active before fetching the ID.<br>
	 * Returns {@code 0} if no skill is being cast.
	 * @return The {@code int} ID of the casting skill or {@code 0}.
	 */
	public int getCastingSkillId()
	{
		return castingSkill != null ? castingSkill.getSkillTemplate().getSkillId() : 0;
	}
	
	/**
	 * Retrieves the {@link Skill} currently being cast by this creature.<br>
	 * Returns {@code null} if the creature is not currently casting a skill.
	 * @return The current {@code Skill} object or {@code null}.
	 */
	public Skill getCastingSkill()
	{
		return castingSkill;
	}
	
	/**
	 * Retrieves the current skill number for this creature.<br>
	 * This value is used to identify specific skills in the game engine.
	 * @return The {@code int} representing the skill number.
	 */
	public int getSkillNumber()
	{
		return skillNumber;
	}
	
	/**
	 * Sets the unique identifier for the current skill.<br>
	 * This value is used to track which skill a creature is currently using.
	 * @param skillNumber The {@code int} ID of the skill.
	 */
	public void setSkillNumber(int skillNumber)
	{
		this.skillNumber = skillNumber;
	}
	
	/**
	 * Retrieves the total number of times this creature has been attacked.<br>
	 * This value is used to track combat activity.
	 * @return the current {@code int} count of attacks received.
	 */
	public int getAttackedCount()
	{
		return attackedCount;
	}
	
	/**
	 * Increases the total number of times this creature has been attacked.<br>
	 * This method updates the {@code attackedCount} variable.
	 */
	public void incrementAttackedCount()
	{
		attackedCount++;
	}
	
	/**
	 * Resets the {@code attackedCount} to {@code 0}.<br>
	 * This method is used to clear previous attack history for this creature.
	 */
	public void clearAttackedCount()
	{
		attackedCount = 0;
	}
	
	/**
	 * Checks if the creature is currently using an item.<br>
	 * It returns {@code true} if the {@code usingItem} field is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if an item is being used, {@code false} otherwise.
	 */
	public boolean isUsingItem()
	{
		return usingItem != null;
	}
	
	/**
	 * Sets the item currently being used by this creature.<br>
	 * This updates the {@code usingItem} field.
	 * @param usingItem The {@link Item} object to set.
	 */
	public void setUsingItem(Item usingItem)
	{
		this.usingItem = usingItem;
	}
	
	/**
	 * Retrieves the unique identifier of the item currently being used.<br>
	 * It checks if a {@code usingItem} exists before accessing its template.<br>
	 * Returns {@code 0} if no item is currently in use.
	 * @return The {@code int} ID of the item template or {@code 0}.
	 */
	public int getUsingItemId()
	{
		return usingItem != null ? usingItem.getItemTemplate().getTemplateId() : 0;
	}
	
	/**
	 * Retrieves the item currently being used by this creature.<br>
	 * Returns {@code null} if no item is in use.
	 * @return The {@link Item} object currently held for use.
	 */
	public Item getUsingItem()
	{
		return usingItem;
	}
	
	/**
	 * Checks if the creature is currently able to perform a movement.<br>
	 * It verifies that the creature is not in an abnormal state.<br>
	 * It also ensures the creature is spawned and can use skills while moving.
	 * @return {@code true} if the move is possible, {@code false} otherwise.
	 */
	public boolean canPerformMove()
	{
		return !(getEffectController().isAbnormalState(AbnormalState.CANT_MOVE_STATE) || !isSpawned() || !canUseSkillInMove());
	}
	
	/**
	 * Checks if the current skill can be used while moving.<br>
	 * This method validates the {@code movedCondition} of the active skill.<br>
	 * It returns {@code false} if movement is restricted by the skill template.
	 * @return {@code true} if the skill allows movement or has no restrictions, {@code false} otherwise.
	 */
	private boolean canUseSkillInMove()
	{
		if (castingSkill != null)
		{
			final SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(castingSkill.getSkillId());
			if ((st.getStartconditions() != null) && (st.getMovedCondition() != null))
			{
				if (!st.getMovedCondition().isAllow())
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the creature is currently able to perform an attack.<br>
	 * It returns {@code false} if the creature is casting, resting, in a private shop, or has a specific abnormal state.<br>
	 * Otherwise, it returns {@code true}.
	 * @return {@code true} if the creature can attack, {@code false} otherwise.
	 */
	public boolean canAttack()
	{
		return !(getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE) || isCasting() || isInState(CreatureState.RESTING) || isInState(CreatureState.PRIVATE_SHOP));
	}
	
	/**
	 * Retrieves the current state of the creature.<br>
	 * This value represents the internal status of the object.
	 * @return The current state as an {@code int}.
	 */
	public int getState()
	{
		return state;
	}
	
	/**
	 * Updates the current state of the creature.<br>
	 * This method applies a new {@code CreatureState} using a bitwise OR operation.<br>
	 * It combines the provided state with the existing states.
	 * @param state The {@code CreatureState} to apply to this creature.
	 */
	public void setState(CreatureState state)
	{
		this.state |= state.getId();
	}
	
	/**
	 * Updates the current state of the creature.<br>
	 * This method sets the {@code state} field to a new value.
	 * @param state The new integer value for the creature's state.
	 */
	public void setState(int state)
	{
		this.state = state;
	}
	
	/**
	 * Removes a specific state from the creature.<br>
	 * This method updates the internal bitmask by clearing the bits associated with {@code state}.
	 * @param state The {@link CreatureState} to be removed.
	 */
	public void unsetState(CreatureState state)
	{
		this.state &= ~state.getId();
	}
	
	/**
	 * Checks if the creature currently has a specific state.<br>
	 * It compares the current bitmask against the {@code state}.
	 * @param state The {@link CreatureState} to check for.
	 * @return {@code true} if the state is active, otherwise {@code false}.
	 */
	public boolean isInState(CreatureState state)
	{
		final int isState = this.state & state.getId();
		
		if (isState == state.getId())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current visual state of the creature.<br>
	 * This value is used to determine how the object appears in the game world.
	 * @return The {@code int} representing the current visual state.
	 */
	public int getVisualState()
	{
		return visualState;
	}
	
	/**
	 * Updates the current visual state of the creature.<br>
	 * This method uses a bitwise OR operation to combine the new state with the existing one.
	 * @param visualState The {@code CreatureVisualState} to apply.
	 */
	public void setVisualState(CreatureVisualState visualState)
	{
		this.visualState |= visualState.getId();
	}
	
	/**
	 * Removes a specific visual state from the creature.<br>
	 * This method updates the internal bitmask by clearing the bits associated with the provided {@code CreatureVisualState}.
	 * @param visualState The {@code CreatureVisualState} to remove.
	 */
	public void unsetVisualState(CreatureVisualState visualState)
	{
		this.visualState &= ~visualState.getId();
	}
	
	/**
	 * Checks if the current creature is in a specific visual state.<br>
	 * This method compares the bitmask of the current {@code visualState} with the provided state.
	 * @param visualState The {@code CreatureVisualState} to check against.
	 * @return {@code true} if the creature is in the specified state, {@code false} otherwise.
	 */
	public boolean isInVisualState(CreatureVisualState visualState)
	{
		final int isVisualState = this.visualState & visualState.getId();
		
		if (isVisualState == visualState.getId())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the current visibility state of the creature.<br>
	 * This value represents how other entities perceive this object.
	 * @return the {@code int} value representing the current {@link CreatureSeeState}.
	 */
	public int getSeeState()
	{
		return seeState;
	}
	
	/**
	 * Updates the current visibility state of the creature.<br>
	 * This method uses a bitwise OR operation to combine the new state with the existing one.
	 * @param seeState The {@code CreatureSeeState} to apply to this creature.
	 */
	public void setSeeState(CreatureSeeState seeState)
	{
		this.seeState |= seeState.getId();
	}
	
	/**
	 * Removes a specific visibility state from the current creature.<br>
	 * This method updates the internal {@code seeState} by clearing the bit associated with the provided state.
	 * @param seeState The {@link CreatureSeeState} to be removed.
	 */
	public void unsetSeeState(CreatureSeeState seeState)
	{
		this.seeState &= ~seeState.getId();
	}
	
	/**
	 * Checks if the current creature is in a specific visibility state.<br>
	 * It compares the internal {@code seeState} bitmask against the provided {@code seeState}.
	 * @param seeState The {@code CreatureSeeState} to check for.
	 * @return {@code true} if the state matches, otherwise {@code false}.
	 */
	public boolean isInSeeState(CreatureSeeState seeState)
	{
		final int isSeeState = this.seeState & seeState.getId();
		
		if (isSeeState == seeState.getId())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the {@code TransformModel} for this object.<br>
	 * This model contains information about the visual appearance and shape of the creature.
	 * @return the current {@link TransformModel} instance.
	 */
	public TransformModel getTransformModel()
	{
		return transformModel;
	}
	
	/**
	 * Sets the {@code TransformModel} for this object.<br>
	 * This updates the visual representation of the creature.
	 * @param model The new {@link TransformModel} to apply.
	 */
	public void setTransformModel(TransformModel model)
	{
		transformModel = model;
	}
	
	/**
	 * Retrieves the current list of targets that have aggressive behavior toward this creature.<br>
	 * This method returns the {@code AggroList} object managed by the entity.
	 * @return The {@link AggroList} containing all active aggro targets.
	 */
	public AggroList getAggroList()
	{
		return aggroList;
	}
	
	/**
	 * PacketBroadcasterMask
	 */
	private volatile byte packetBroadcastMask;
	
	/**
	 * Registers this object with the {@link PacketBroadcaster}.<br>
	 * It applies a specific bitmask based on the provided {@code BroadcastMode}.<br>
	 * This allows the server to send relevant packets to this entity.
	 * @param mode The {@code BroadcastMode} used to determine which packets to include.
	 */
	public void addPacketBroadcastMask(BroadcastMode mode)
	{
		packetBroadcastMask |= mode.mask();
		
		PacketBroadcaster.getInstance().add(this);
		
		// Debug
		if (log.isDebugEnabled())
		{
			log.debug("PacketBroadcaster: Packet " + mode.name() + " added to player " + getName());
		}
	}
	
	/**
	 * Removes a specific packet broadcast mask from the current object.<br>
	 * This method updates the {@code packetBroadcastMask} by clearing the bits defined in the provided {@code mode}.<br>
	 * It logs a debug message when the removal occurs.
	 * @param mode The {@code BroadcastMode} whose mask should be removed.
	 */
	public void removePacketBroadcastMask(BroadcastMode mode)
	{
		packetBroadcastMask &= ~mode.mask();
		
		// Debug
		if (log.isDebugEnabled())
		{
			log.debug("PacketBroadcaster: Packet " + mode.name() + " removed from player " + getName()); // fix
		} // ClassCastException
	}
	
	/**
	 * Retrieves the broadcast mask for network packets.<br>
	 * This value determines which clients receive specific updates.
	 * @return the {@code byte} representing the broadcast mask.
	 */
	public byte getPacketBroadcastMask()
	{
		return packetBroadcastMask;
	}
	
	/**
	 * Retrieves the {@link ObserveController} for this creature.<br>
	 * This controller handles how other entities perceive this object.
	 * @return the {@code ObserveController} instance.
	 */
	public ObserveController getObserveController()
	{
		return observeController;
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method delegates the check to the {@code isEnemyFrom} method of the provided creature.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	public boolean isEnemy(Creature creature)
	{
		return creature.isEnemyFrom(this);
	}
	
	/**
	 * Checks if the specified {@link Creature} is an enemy.<br>
	 * This method currently always returns {@code false}.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, otherwise {@code false}.
	 */
	public boolean isEnemyFrom(Creature creature)
	{
		return false;
	}
	
	/**
	 * Checks if the specified {@link Player} is considered an enemy.<br>
	 * This method currently always returns {@code false}.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is an enemy, otherwise {@code false}.
	 */
	public boolean isEnemyFrom(Player player)
	{
		return false;
	}
	
	/**
	 * Checks if the provided {@link Npc} is considered an enemy.<br>
	 * This method currently always returns {@code false}.
	 * @param npc The {@code Npc} object to check.
	 * @return {@code true} if the NPC is an enemy, otherwise {@code false}.
	 */
	public boolean isEnemyFrom(Npc npc)
	{
		return false;
	}
	
	/**
	 * Retrieves the {@link TribeClass} of this creature.<br>
	 * This method returns the specific class type assigned to the object.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	public TribeClass getTribe()
	{
		return tribe;
	}
	
	/**
	 * Sets the {@code TribeClass} for this creature.<br>
	 * This updates the internal tribe property of the object.
	 * @param tribe The {@link TribeClass} to assign.
	 */
	public void setTribe(TribeClass tribe)
	{
		this.tribe = tribe;
	}
	
	/**
	 * Retrieves the default tribe class for this object.<br>
	 * This method always returns the {@code TribeClass.GENERAL} value.
	 * @return The base {@link TribeClass} of the creature.
	 */
	public TribeClass getBaseTribe()
	{
		return TribeClass.GENERAL;
	}
	
	/**
	 * Checks if the current entity can see a specific {@link Creature}.<br>
	 * It returns {@code true} if the target is in a hidden instance state.<br>
	 * Otherwise, it compares the visual states of both entities.
	 * @param creature The {@link Creature} to check visibility for.
	 * @return {@code true} if the creature is visible, {@code false} otherwise.
	 */
	@Override
	public boolean canSee(Creature creature)
	{
		if (creature == null)
		{
			return false;
		}
		
		if ((creature.isInInstance() && creature.isInVisualState(CreatureVisualState.HIDE2)) || (creature.isInInstance() && creature.isInVisualState(CreatureVisualState.HIDE1)))
		{
			return true;
		}
		
		return creature.getVisualState() <= getSeeState();
	}
	
	/**
	 * Checks if a specific object is currently visible to this creature.<br>
	 * It looks up the {@code object} in the known list of visible objects.
	 * @param object The {@link VisibleObject} to check for visibility.
	 * @return {@code true} if the object is visible, otherwise {@code false}.
	 */
	public boolean isSeeObject(VisibleObject object)
	{
		return getKnownList().getVisibleObjects().containsKey(object.getObjectId());
	}
	
	/**
	 * Checks if the current creature can see a specific player.<br>
	 * This method verifies if the {@code player} is in the list of visible players.
	 * @param player The {@link Player} object to check for visibility.
	 * @return {@code true} if the player is visible, {@code false} otherwise.
	 */
	public boolean isSeePlayer(Player player)
	{
		return getKnownList().getVisiblePlayers().containsKey(player.getObjectId());
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.NORMAL;
	}
	
	/**
	 * Retrieves the master {@link Creature} associated with this object.<br>
	 * This method returns the current instance itself.
	 * @return the {@code Creature} object.
	 */
	public Creature getMaster()
	{
		return this;
	}
	
	/**
	 * Retrieves the {@link Creature} that is currently performing an action.<br>
	 * This method returns the current instance of the creature.
	 * @return the current {@code Creature} object.
	 */
	public Creature getActingCreature()
	{
		return this;
	}
	
	/**
	 * Checks if a specific skill is currently disabled due to cooldowns.<br>
	 * This method verifies both standard and shared cooldown timers.<br>
	 * It returns {@code true} if the skill cannot be used yet.
	 * @param template The {@link SkillTemplate} to check for availability.
	 * @return {@code true} if the skill is disabled, {@code false} otherwise.
	 */
	public boolean isSkillDisabled(SkillTemplate template)
	{
		if (skillCoolDowns == null)
		{
			return false;
		}
		
		final int cooldownId = template.getCooldownId();
		final Long coolDown = skillCoolDowns.get(cooldownId);
		if (coolDown == null)
		{
			return false;
		}
		
		if (coolDown < System.currentTimeMillis())
		{
			removeSkillCoolDown(cooldownId);
			return false;
		}
		
		/*
		 * Some shared cooldown skills have indipendent and different cooldown they must not be blocked
		 */
		if ((skillCoolDownsBase != null) && (skillCoolDownsBase.get(cooldownId) != null))
		{
			if ((template.getDuration() + (template.getCooldown() * 100) + skillCoolDownsBase.get(cooldownId)) < System.currentTimeMillis())
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Retrieves the remaining cooldown time for a specific skill.<br>
	 * It checks if the {@code cooldownId} exists in the current cooldown map.<br>
	 * If no cooldown is found, it returns {@code 0}.
	 * @param cooldownId The unique identifier of the skill cooldown to check.
	 * @return The remaining time for the cooldown as a {@code long}, or {@code 0} if not active.
	 */
	public long getSkillCoolDown(int cooldownId)
	{
		if ((skillCoolDowns == null) || !skillCoolDowns.containsKey(cooldownId))
		{
			return 0;
		}
		
		return skillCoolDowns.get(cooldownId);
	}
	
	/**
	 * Sets the cooldown time for a specific skill.<br>
	 * This method updates the {@code skillCoolDowns} map with the provided values.<br>
	 * It does nothing if the {@code cooldownId} is {@code 0}.
	 * @param cooldownId The unique identifier for the skill to update.
	 * @param time The amount of time to set as the cooldown.
	 */
	public void setSkillCoolDown(int cooldownId, long time)
	{
		if (cooldownId == 0)
		{
			return;
		}
		
		if (skillCoolDowns == null)
		{
			skillCoolDowns = new ConcurrentHashMap<>();
		}
		
		skillCoolDowns.put(cooldownId, time);
	}
	
	/**
	 * Retrieves the current cooldown times for all skills.<br>
	 * The map uses {@code Integer} as the skill ID and {@code Long} as the remaining time.
	 * @return A {@link Map} containing the skill IDs and their respective cooldown values.
	 */
	public Map<Integer, Long> getSkillCoolDowns()
	{
		return skillCoolDowns;
	}
	
	/**
	 * Removes a specific skill cooldown from the creature.<br>
	 * This method clears the entry for the given {@code cooldownId} from both active and base cooldown lists.
	 * @param cooldownId The unique identifier of the skill cooldown to remove.
	 */
	public void removeSkillCoolDown(int cooldownId)
	{
		if (skillCoolDowns == null)
		{
			return;
		}
		
		skillCoolDowns.remove(cooldownId);
		if (skillCoolDownsBase != null)
		{
			skillCoolDownsBase.remove(cooldownId);
		}
	}
	
	/**
	 * Sets the base cooldown time for a specific skill.<br>
	 * This method updates the {@code skillCoolDownsBase} map.<br>
	 * It does nothing if the {@code cooldownId} is {@code 0}.
	 * @param cooldownId The unique identifier for the skill cooldown.
	 * @param baseTime The duration of the cooldown in milliseconds.
	 */
	public void setSkillCoolDownBase(int cooldownId, long baseTime)
	{
		if (cooldownId == 0)
		{
			return;
		}
		
		if (skillCoolDownsBase == null)
		{
			skillCoolDownsBase = new ConcurrentHashMap<>();
		}
		
		skillCoolDownsBase.put(cooldownId, baseTime);
	}
	
	/**
	 * Retrieves the neutral flag for administrators.<br>
	 * This method extracts a specific bit from the {@code adminFlags} field.
	 * @return The integer value of the neutral flag.
	 */
	public int getAdminNeutral()
	{
		return adminFlags >> 4;
	}
	
	/**
	 * Updates the admin flags for this creature.<br>
	 * This method modifies the lower and upper nibbles of the {@code adminFlags} field.<br>
	 * The {@code newValue} is used to set the bits starting from the 4th position.
	 * @param newValue The new integer value to be bitwise ORed into the flags.
	 */
	public void setAdminNeutral(int newValue)
	{
		adminFlags = (byte) ((adminFlags & 0xF) | ((newValue & 0xF) << 4));
	}
	
	/**
	 * Retrieves the current enmity level for administrators.<br>
	 * This value is extracted from the {@code adminFlags} bitmask.
	 * @return The integer value of the administrator enmity.
	 */
	public int getAdminEnmity()
	{
		return adminFlags & 0xF;
	}
	
	/**
	 * Updates the admin flags for this creature.<br>
	 * This method modifies only the last 4 bits of the {@code adminFlags} field.<br>
	 * The value provided in {@code newValue} is masked to ensure it fits within those bits.
	 * @param newValue The new bitmask value to apply to the admin flags.
	 */
	public void setAdminEnmity(int newValue)
	{
		adminFlags = (byte) ((adminFlags & 0xF0) | (newValue & 0xF));
	}
	
	/**
	 * Retrieves the collision value for this object.<br>
	 * This value is obtained from the bound radius of the {@code VisibleObjectTemplate}.
	 * @return The collision value as a {@code float}.
	 */
	public float getCollision()
	{
		return getObjectTemplate().getBoundRadius().getCollision();
	}
	
	/**
	 * Checks if the NPC can be attacked by other entities.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the NPC is attackable, otherwise {@code false}.
	 */
	public boolean isAttackableNpc()
	{
		return false;
	}
	
	/**
	 * Retrieves the attack type for this item.<br>
	 * This method returns a constant value of {@code PHYSICAL}.
	 * @return the {@link ItemAttackType} of the attack.
	 */
	public ItemAttackType getAttackType()
	{
		return ItemAttackType.PHYSICAL;
	}
	
	/**
	 * Checks if the creature is currently in a flying state.<br>
	 * This includes being in the {@code FLYING} or {@code GLIDING} states.<br>
	 * It returns {@code false} if the creature is also in the {@code RESTING} state.
	 * @return {@code true} if the creature is flying, {@code false} otherwise.
	 */
	public boolean isFlying()
	{
		return (isInState(CreatureState.FLYING) && !isInState(CreatureState.RESTING)) || isInState(CreatureState.GLIDING);
	}
	
	/**
	 * Checks if the creature is currently in a flying state.<br>
	 * This returns {@code true} only if the creature is flying and not resting.
	 * @return {@code true} if the creature is flying; {@code false} otherwise.
	 */
	public boolean isInFlyingState()
	{
		return isInState(CreatureState.FLYING) && !isInState(CreatureState.RESTING);
	}
	
	/**
	 * Checks if the current creature is a player character.<br>
	 * Returns {@code 0} if it is not a player.
	 * @return A byte value representing the player status.
	 */
	public byte isPlayer()
	{
		return 0;
	}
	
	/**
	 * Checks if the current interaction is a player versus player fight.<br>
	 * It verifies that both the acting creature and the target are of type {@link Player}.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if both creatures are players, {@code false} otherwise.
	 */
	public boolean isPvpTarget(Creature creature)
	{
		return (getActingCreature() instanceof Player) && (creature.getActingCreature() instanceof Player);
	}
	
	/**
	 * Updates the zone information for the current creature.<br>
	 * This method checks the {@code MapRegion} of the current position.<br>
	 * It triggers a revalidation of zones if the region is not {@code null}.
	 */
	public void revalidateZones()
	{
		final MapRegion mapRegion = getPosition().getMapRegion();
		if (mapRegion != null)
		{
			mapRegion.revalidateZones(this);
		}
	}
	
	/**
	 * Checks if the current creature is located within a specific zone.<br>
	 * This method first verifies if the creature has been spawned.<br>
	 * It then uses the map region to determine the position relative to the {@code zoneName}.
	 * @param zoneName The name of the zone to check against.
	 * @return {@code true} if the creature is inside the specified zone, otherwise {@code false}.
	 */
	public boolean isInsideZone(ZoneName zoneName)
	{
		if (!isSpawned())
		{
			return false;
		}
		
		return getPosition().getMapRegion().isInsideZone(zoneName, this);
	}
	
	/**
	 * Checks if the current creature is located within a specific item use zone.<br>
	 * This method first verifies if the object has been spawned.<br>
	 * It then delegates the check to the map region.
	 * @param zoneName The name of the {@code ZoneName} to check against.
	 * @return {@code true} if the creature is inside the zone, otherwise {@code false}.
	 */
	public boolean isInsideItemUseZone(ZoneName zoneName)
	{
		if (!isSpawned())
		{
			return false;
		}
		
		return getPosition().getMapRegion().isInsideItemUseZone(zoneName, this);
	}
	
	/**
	 * Checks if the current creature is located within a specific weather zone.<br>
	 * This method verifies the creature's 3D position against active zones.<br>
	 * It returns {@code true} only if the zone type matches {@code WEATHER}.
	 * @param weatherZoneId The unique identifier of the weather zone to check.
	 * @return {@code true} if the creature is inside the specified weather zone, otherwise {@code false}.
	 */
	public boolean isInsideWeatherZone(int weatherZoneId)
	{
		if (getActiveRegion() == null)
		{
			return false;
		}
		
		final List<ZoneInstance> zones = getActiveRegion().getZones(this);
		for (ZoneInstance regionZone : zones)
		{
			if (regionZone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER)
			{
				if (!regionZone.getAreaTemplate().isInside3D(getPosition().getX(), getPosition().getY(), getPosition().getZ()))
				{
					continue;
				}
				
				if (DataManager.ZONE_DATA.getWeatherZoneId(regionZone.getZoneTemplate()) == weatherZoneId)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Updates the internal count for a specific {@link ZoneType}.<br>
	 * This method increments the value associated with the provided zone type.
	 * @param zoneType The {@code ZoneType} to update.
	 */
	public void setInsideZoneType(ZoneType zoneType)
	{
		final byte current = zoneTypes[zoneType.getValue()];
		zoneTypes[zoneType.getValue()] = (byte) (current + 1);
	}
	
	/**
	 * Decreases the count of a specific {@code ZoneType} by one.<br>
	 * This method updates the internal zone type array.
	 * @param zoneType The {@code ZoneType} to modify.
	 */
	public void unsetInsideZoneType(ZoneType zoneType)
	{
		final byte current = zoneTypes[zoneType.getValue()];
		zoneTypes[zoneType.getValue()] = (byte) (current - 1);
	}
	
	/**
	 * Checks if the current creature is located within a specific {@code ZoneType}.<br>
	 * This method verifies if the provided {@code zoneType} has a positive value in the internal list.
	 * @param zoneType The type of zone to check.
	 * @return {@code true} if the creature is inside the specified zone, otherwise {@code false}.
	 */
	public boolean isInsideZoneType(ZoneType zoneType)
	{
		return zoneTypes[zoneType.getValue()] > 0;
	}
	
	/**
	 * Returns the race of the creature.<br>
	 * This method always returns {@code Race.NONE}.
	 * @return The {@link Race} value for this object.
	 */
	public Race getRace()
	{
		return Race.NONE;
	}
	
	/**
	 * Retrieves the specific type of a {@link Creature}.<br>
	 * This value identifies what kind of entity the creature is.
	 * @param creature The {@code Creature} object to check.
	 * @return The integer ID representing the creature type.
	 */
	public int getType(Creature creature)
	{
		return type;
	}
	
	/**
	 * Retrieves the cooldown time for a specific skill.<br>
	 * This method fetches the value from the provided {@code SkillTemplate}.
	 * @param template The {@code SkillTemplate} containing the skill data.
	 * @return The cooldown duration as an {@code int}.
	 */
	public int getSkillCooldown(SkillTemplate template)
	{
		return template.getCooldown();
	}
	
	/**
	 * Retrieves the cooldown time for a specific item.<br>
	 * This value is fetched from the {@code ItemTemplate}.
	 * @param template The {@link ItemTemplate} to check.
	 * @return The delay time as an {@code int}.
	 */
	public int getItemCooldown(ItemTemplate template)
	{
		return template.getUseLimits().getDelayTime();
	}
	
	/**
	 * Checks if the creature was recently spawned.<br>
	 * It returns {@code true} if the time since spawning is less than 1500 milliseconds.
	 * @return {@code true} if the creature is new, {@code false} otherwise.
	 */
	public boolean isNewSpawn()
	{
		return (System.currentTimeMillis() - spawnTime) < 1500;
	}
	
	/**
	 * Retrieves the multiplier for critical effects.<br>
	 * This value determines how much a critical hit scales the damage or effect.
	 * @return the {@code int} value of the critical effect multiplier.
	 */
	public int getCriticalEffectMulti()
	{
		return CriticalEffectMulti;
	}
	
	/**
	 * Sets the multiplier for critical effects.<br>
	 * This value determines how much a critical hit scales the resulting effect.
	 * @param criticalEffectMulti The new multiplier to apply to critical effects.
	 */
	public void setCriticalEffectMulti(int criticalEffectMulti)
	{
		CriticalEffectMulti = criticalEffectMulti;
	}
	
	private int CriticalEffectMulti = 1;
	
	/**
	 * Checks if the current creature is a raid monster.<br>
	 * This method currently always returns {@code false}.
	 * @return {@code true} if the creature is a raid monster, otherwise {@code false}.
	 */
	public boolean isRaidMonster()
	{
		return false;
	}
	
	/**
	 * Checks if the current creature is a world raid monster.<br>
	 * This method evaluates the {@code TribeClass} of the creature.<br>
	 * It also checks for specific conditions like {@code isRaidMonster}.
	 * @return {@code true} if the creature belongs to a world raid tribe, {@code false} otherwise.
	 */
	public boolean isWorldRaidMonster()
	{
		return (getTribe() == TribeClass.WORLDRAID_MONSTER) || ((getTribe() == TribeClass.WORLDRAID_MONSTER_SANDWORMSUM) && isRaidMonster());
	}
}
