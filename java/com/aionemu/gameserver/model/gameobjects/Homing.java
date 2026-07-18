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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.HomingGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents a homing projectile or entity in the game world.<br>
 * This class handles the behavior of objects that move toward a specific target.<br>
 * It extends {@link SummonedObject} to manage its lifecycle as a dynamic game object.
 * @author ATracer
 * @modified Lilith
 */
public class Homing extends SummonedObject<Creature>
{
	/**
	 * Number of performed attacks
	 */
	private int attackCount;
	private final int skillId;
	/**
	 * Skill id of this homing. 0 - usually attack, other - skills.
	 */
	private int activeSkillId;
	
	/**
	 * Creates a new instance of a {@link Homing} object.<br>
	 * This constructor initializes the homing creature with its required templates and stats.
	 * @param objId The unique identifier for this object.
	 * @param controller The {@link NpcController} that manages this entity.
	 * @param spawnTemplate The template defining where the object spawns.
	 * @param objectTemplate The base template for the object's properties.
	 * @param level The character level of the homing creature.
	 * @param skillId The specific skill ID associated with this homing.
	 */
	public Homing(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level, int skillId)
	{
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.skillId = skillId;
	}
	
	/**
	 * Initializes the statistics containers for this object.<br>
	 * It sets up both {@link HomingGameStats} and {@link NpcLifeStats}.
	 * @param level The level of the object.
	 */
	@Override
	protected void setupStatContainers(byte level)
	{
		setGameStats(new HomingGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}
	
	/**
	 * Updates the total number of attacks performed by this object.<br>
	 * This value is used to track combat activity.
	 * @param attackCount The new count of attacks to set.
	 */
	public void setAttackCount(int attackCount)
	{
		this.attackCount = attackCount;
	}
	
	/**
	 * Retrieves the total number of attacks performed.<br>
	 * This value is stored in the {@code attackCount} field.
	 * @return The current count of attacks as an {@code int}.
	 */
	public int getAttackCount()
	{
		return attackCount;
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method delegates the check to the {@code isEnemyFrom} method of the provided creature.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if the creature is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return getCreator().isEnemy(creature);
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
		return getCreator() != null ? getCreator().isEnemyFrom(player) : false;
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.HOMING;
	}
	
	/**
	 * Retrieves the name of the master associated with this homing object.<br>
	 * This method returns an empty string if no master is found.
	 * @return The name of the master as a {@code String}.
	 */
	@Override
	public String getMasterName()
	{
		return "";
	}
	
	/**
	 * Retrieves the attack type for this object.<br>
	 * The return value depends on the name of the object.<br>
	 * It defaults to {@code ItemAttackType.PHYSICAL} if no specific match is found.
	 * @return the {@link ItemAttackType} of the attack.
	 */
	@Override
	public ItemAttackType getAttackType()
	{
		if (getName().equalsIgnoreCase("fire energy"))
		{
			return ItemAttackType.MAGICAL_FIRE;
		}
		else if (getName().equalsIgnoreCase("stone energy"))
		{
			return ItemAttackType.MAGICAL_EARTH;
		}
		else if (getName().equalsIgnoreCase("water energy"))
		{
			return ItemAttackType.MAGICAL_WATER;
		}
		else if ((getName().equalsIgnoreCase("wind servant")) || (getName().equalsIgnoreCase("cyclone servant")))
		{
			return ItemAttackType.MAGICAL_WIND;
		}
		
		return ItemAttackType.PHYSICAL;
	}
	
	/**
	 * Retrieves the unique identifier for the skill associated with this AI.
	 * @return The {@code int} value of the skill ID.
	 */
	public int getSkillId()
	{
		return skillId;
	}
	
	/**
	 * Retrieves the current skill ID of this homing.<br>
	 * A value of {@code 0} usually represents a standard attack.<br>
	 * Other values represent specific skills.
	 * @return The current active skill ID as an {@code int}.
	 */
	public int getActiveSkillId()
	{
		return activeSkillId;
	}
	
	/**
	 * Sets the current active skill identifier for this homing object.<br>
	 * Use {@code 0} to represent a standard attack.<br>
	 * Other values should correspond to specific skill IDs.
	 * @param activeSkillId The unique identifier for the skill to be activated.
	 */
	public void setActiveSkillId(int activeSkillId)
	{
		this.activeSkillId = activeSkillId;
	}
}
