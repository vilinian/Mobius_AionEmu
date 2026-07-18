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

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.HomingGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.stats.container.SummonedObjectGameStats;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents an entity summoned by a player or NPC in the game world.<br>
 * This class extends {@link Npc} to provide specific behaviors for summons.<br>
 * It serves as a base model for objects that are controlled by a master.
 * @author ATracer, modified Rolandas
 * @param <T>
 */
public class SummonedObject<T extends VisibleObject>extends Npc
{
	private final byte level;
	/**
	 * Creator of this SummonedObject
	 */
	private T creator;
	
	/**
	 * Creates a new instance of a {@link SummonedObject}.<br>
	 * This constructor initializes the object with its required data.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@link NpcController} that manages this object.
	 * @param spawnTemplate The template defining where the object spawns.
	 * @param objectTemplate The template defining the object's base properties.
	 * @param level The level of the summoned object.
	 */
	public SummonedObject(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level)
	{
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.level = level;
	}
	
	/**
	 * Initializes the statistics containers for this object.<br>
	 * It sets up both {@link HomingGameStats} and {@link NpcLifeStats}.
	 * @param level The level of the object.
	 */
	@Override
	protected void setupStatContainers(byte level)
	{
		setGameStats(new SummonedObjectGameStats(this));
		setLifeStats(new NpcLifeStats(this));
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
	 * Retrieves the object that created this {@code SummonedObject}.<br>
	 * This is useful for identifying the owner or master.
	 * @return The creator of this object, or {@code null} if no creator exists.
	 */
	@Override
	public T getCreator()
	{
		return creator;
	}
	
	/**
	 * Sets the owner who created this {@code SummonedObject}.<br>
	 * If the {@code creator} is a {@link Player}, it updates the player's summoned object reference.
	 * @param creator The object that created this summon.
	 */
	public void setCreator(T creator)
	{
		if (creator instanceof Player)
		{
			((Player) creator).setSummonedObj(this);
		}
		
		this.creator = creator;
	}
	
	/**
	 * Retrieves the name of the master associated with this homing object.<br>
	 * This method returns an empty string if no master is found.
	 * @return The name of the master as a {@code String}.
	 */
	@Override
	public String getMasterName()
	{
		return creator != null ? creator.getName() : "";
	}
	
	/**
	 * Retrieves the unique identifier of the creator.<br>
	 * This value identifies which entity created this object.
	 * @return the {@code int} ID of the creator.
	 */
	@Override
	public int getCreatorId()
	{
		return creator != null ? creator.getObjectId() : 0;
	}
	
	/**
	 * Gets the creature that is currently performing an action.<br>
	 * It returns the {@link Creature} creator if one exists.<br>
	 * If no creator exists, it returns {@code this} instance.
	 * @return the acting {@link Creature} or the current object.
	 */
	@Override
	public Creature getActingCreature()
	{
		if (creator instanceof Creature)
		{
			return (Creature) getCreator();
		}
		
		return this;
	}
	
	/**
	 * Retrieves the master {@link Creature} associated with this object.<br>
	 * This method returns the current instance itself.
	 * @return the {@code Creature} object.
	 */
	@Override
	public Creature getMaster()
	{
		if (creator instanceof Creature)
		{
			return (Creature) getCreator();
		}
		
		return this;
	}
	
	/**
	 * Determines the relationship type between this object and a {@link Creature}.<br>
	 * It checks if the {@code creature} is an enemy of the master.
	 * @param creature The {@code Creature} to evaluate.
	 * @return The integer ID for either {@code ATTACKABLE} or {@code SUPPORT} types.
	 */
	@Override
	public int getType(Creature creature)
	{
		return creature.isEnemy(getMaster()) ? CreatureType.ATTACKABLE.getId() : CreatureType.SUPPORT.getId();
	}
	
	/**
	 * Determines if the given {@code Creature} is an enemy of this object.<br>
	 * This check is performed by asking the master of this object.<br>
	 * If there is no master, it returns {@code false}.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return getMaster() != null ? getMaster().isEnemy(creature) : false;
	}
	
	/**
	 * Checks if the provided {@link Npc} is considered an enemy.<br>
	 * This method currently always returns {@code false}.
	 * @param npc The {@code Npc} object to check.
	 * @return {@code true} if the NPC is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Npc npc)
	{
		return getMaster() != null ? getMaster().isEnemyFrom(npc) : false;
	}
	
	/**
	 * Checks if the specified {@link Player} is considered an enemy.<br>
	 * This method currently always returns {@code false}.
	 * @param player The {@code Player} object to check.
	 * @return {@code true} if the player is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Player player)
	{
		return getMaster() != null ? getMaster().isEnemyFrom(player) : false;
	}
	
	/**
	 * Retrieves the {@link TribeClass} of this creature.<br>
	 * This method returns the specific class type assigned to the object.
	 * @return the {@code TribeClass} associated with this entity.
	 */
	@Override
	public TribeClass getTribe()
	{
		if (getMaster() == null)
		{
			return ((NpcTemplate) objectTemplate).getTribe();
		}
		
		return getMaster().getTribe();
	}
	
	/**
	 * Retrieves the {@code Race} of the object.<br>
	 * This method returns the race of the creator if it is a {@link Creature}.<br>
	 * If the creator is not a {@link Creature}, it returns the race from the parent class.
	 * @return The {@link Race} of the object.
	 */
	@Override
	public Race getRace()
	{
		if (creator instanceof Creature)
		{
			return creator != null ? ((Creature) creator).getRace() : Race.NONE;
		}
		
		return super.getRace();
	}
}
