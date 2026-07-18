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

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.movement.SiegeWeaponMoveController;
import com.aionemu.gameserver.controllers.movement.SummonMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.SummonGameStats;
import com.aionemu.gameserver.model.stats.container.SummonLifeStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a summonable creature entity within the game world.<br>
 * This class handles the behavior, stats, and movement of summons owned by a {@link Player}.<br>
 * It extends {@link Creature} to provide specific functionality for summoned units.
 * @author ATracer
 */
public class Summon extends Creature
{
	private Player master;
	private SummonMode mode = SummonMode.GUARD;
	private final byte level;
	private int liveTime;
	private Future<?> releaseTask;
	
	/**
	 * Creates a new {@link Summon} instance and initializes its properties.<br>
	 * This constructor sets up the AI engine and movement controllers.<br>
	 * It also calculates the stats based on the provided level.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@code CreatureController} to manage this creature.
	 * @param spawnTemplate The template containing spawn information.
	 * @param objectTemplate The template defining the NPC's properties.
	 * @param level The level of the summon.
	 * @param time The initial live time for the summon.
	 */
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level, int time)
	{
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		controller.setOwner(this);
		final String ai = objectTemplate.getAi();
		AI2Engine.getInstance().setupAI(ai, this);
		moveController = ai.equals("siege_weapon") ? new SiegeWeaponMoveController(this) : new SummonMoveController(this);
		this.level = level;
		liveTime = time;
		final SummonStatsTemplate statsTemplate = DataManager.SUMMON_STATS_DATA.getSummonTemplate(objectTemplate.getTemplateId(), level);
		setGameStats(new SummonGameStats(this, statsTemplate));
		setLifeStats(new SummonLifeStats(this));
	}
	
	/**
	 * Creates a new {@link AggroList} for this creature.<br>
	 * This method initializes the list using the current object instance.
	 * @return A new {@code AggroList} associated with this creature.
	 */
	@Override
	protected AggroList createAggroList()
	{
		return new PlayerAggroList(this);
	}
	
	/**
	 * Retrieves the game statistics for this summon.<br>
	 * This method calls {@code getGameStats} and casts the result to {@code SummonGameStats}.
	 * @return The {@code SummonGameStats} object containing the summon's data.
	 */
	@Override
	public SummonGameStats getGameStats()
	{
		return (SummonGameStats) super.getGameStats();
	}
	
	/**
	 * Retrieves the owner of this {@link Minion}.<br>
	 * This returns the {@code Player} who controls the minion.
	 * @return The {@code Player} object representing the master.
	 */
	@Override
	public Player getMaster()
	{
		return master;
	}
	
	/**
	 * Sets the owner of this {@link Summon}.<br>
	 * This method assigns a {@code Player} to the master field.
	 * @param master The {@code Player} who owns this summon.
	 */
	public void setMaster(Player master)
	{
		this.master = master;
	}
	
	/**
	 * Retrieves the name of this gatherable object.<br>
	 * This method returns the {@code String} name from the associated {@link GatherableTemplate}.
	 * @return The name of the object as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return objectTemplate.getName();
	}
	
	/**
	 * Retrieves the current level of this summon.<br>
	 * This value is stored as a {@code byte}.
	 * @return The level of the summon.
	 */
	@Override
	public byte getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the {@code NpcTemplate} associated with this NPC.<br>
	 * This provides access to the base data for the NPC object.
	 * @return The {@link NpcTemplate} of the NPC.
	 */
	@Override
	public NpcTemplate getObjectTemplate()
	{
		return (NpcTemplate) super.getObjectTemplate();
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
	 * Retrieves the unique identifier for the name.<br>
	 * This value is obtained from the {@link NpcTemplate}.
	 * @return The integer ID of the name.
	 */
	public int getNameId()
	{
		return getObjectTemplate().getNameId();
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.SUMMON;
	}
	
	/**
	 * Retrieves the controller for this summon.<br>
	 * This method casts the base {@link Creature} controller to a {@code SummonController}.
	 * @return the {@code SummonController} associated with this object.
	 */
	@Override
	public SummonController getController()
	{
		return (SummonController) super.getController();
	}
	
	/**
	 * Retrieves the current {@code SummonMode} of this summon.<br>
	 * This indicates how the summon is currently behaving.
	 * @return The current {@code SummonMode}.
	 */
	public SummonMode getMode()
	{
		return mode;
	}
	
	/**
	 * Updates the current behavior mode of the {@code Summon}.<br>
	 * This method changes how the summon acts in the game world.
	 * @param mode The new {@link SummonMode} to apply.
	 */
	public void setMode(SummonMode mode)
	{
		this.mode = mode;
	}
	
	/**
	 * Determines if the specified {@code Creature} is an enemy of this summon.<br>
	 * This check relies on the owner's perspective if a {@link Player} master exists.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return master != null ? master.isEnemy(creature) : false;
	}
	
	/**
	 * Checks if the provided {@link Npc} is considered an enemy.<br>
	 * This method delegates the check to the owner's logic if a master exists.
	 * @param npc The {@code Npc} object to check.
	 * @return {@code true} if the NPC is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Npc npc)
	{
		return master != null ? master.isEnemyFrom(npc) : false;
	}
	
	/**
	 * Checks if the specified {@link Player} is considered an enemy.<br>
	 * This method delegates the check to the summon's master if one exists.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Player player)
	{
		return master != null ? master.isEnemyFrom(player) : false;
	}
	
	/**
	 * Gets the {@link TribeClass} of this summon.<br>
	 * It returns the tribe of the owner if it exists.<br>
	 * If there is no owner, it returns the tribe from the {@code objectTemplate}.
	 * @return the {@code TribeClass} of the master or the template.
	 */
	@Override
	public TribeClass getTribe()
	{
		if (master == null)
		{
			return ((NpcTemplate) objectTemplate).getTribe();
		}
		
		return master.getTribe();
	}
	
	/**
	 * Retrieves the movement controller for this summon.<br>
	 * This method casts the base {@code getMoveController} to a {@code SummonMoveController}.
	 * @return the {@code SummonMoveController} associated with this object.
	 */
	@Override
	public SummonMoveController getMoveController()
	{
		return (SummonMoveController) super.getMoveController();
	}
	
	/**
	 * Retrieves the {@link Creature} that is currently performing an action.<br>
	 * This method returns the current instance of the creature.
	 * @return the current {@code Creature} object.
	 */
	@Override
	public Creature getActingCreature()
	{
		return getMaster() == null ? this : getMaster();
	}
	
	/**
	 * Retrieves the {@code Race} of the master.<br>
	 * This method returns the race of the owner if they exist.<br>
	 * If no master is found, it returns {@code Race.NONE}.
	 * @return The {@link Race} of the master or {@code Race.NONE}.
	 */
	@Override
	public Race getRace()
	{
		return getMaster() != null ? getMaster().getRace() : Race.NONE;
	}
	
	/**
	 * Retrieves the current life duration of the summon.<br>
	 * This value represents how long the summon has been active.
	 * @return The current {@code liveTime} as an {@code int}.
	 */
	public int getLiveTime()
	{
		return liveTime;
	}
	
	/**
	 * Sets the remaining life time for this {@link Summon}.<br>
	 * This value determines how long the summon stays active.
	 * @param liveTime The new duration in seconds to set.
	 */
	public void setLiveTime(int liveTime)
	{
		this.liveTime = liveTime;
	}
	
	/**
	 * Sets the {@code releaseTask} for this summon.<br>
	 * This task handles the logic for releasing the summon.<br>
	 * The provided {@code task} can be {@code null}.
	 * @param task The {@link Future} representing the release operation.
	 */
	public void setReleaseTask(Future<?> task)
	{
		releaseTask = task;
	}
	
	/**
	 * Stops the scheduled task for releasing this summon.<br>
	 * It checks if {@code releaseTask} is not {@code null} and not finished.<br>
	 * If valid, it calls {@code cancel(true)} on the task.
	 */
	public void cancelReleaseTask()
	{
		if ((releaseTask != null) && !releaseTask.isDone())
		{
			releaseTask.cancel(true);
		}
	}
}
