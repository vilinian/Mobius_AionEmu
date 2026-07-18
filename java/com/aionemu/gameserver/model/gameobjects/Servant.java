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
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents a servant entity within the game world.<br>
 * This class handles the behavior and properties of creatures summoned by players.<br>
 * It extends {@link SummonedObject} to manage its lifecycle as a non-player creature.
 * @author ATracer
 */
public class Servant extends SummonedObject<Creature>
{
	private NpcObjectType objectType;
	
	/**
	 * Creates a new instance of a {@link Servant}.<br>
	 * This constructor initializes the servant with its required data.
	 * @param objId The unique identifier for this object.
	 * @param controller The {@link NpcController} that manages the servant's logic.
	 * @param spawnTemplate The template defining where the servant can appear.
	 * @param objectTemplate The base template containing the servant's stats and properties.
	 * @param level The level of the servant.
	 */
	public Servant(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level)
	{
		super(objId, controller, spawnTemplate, objectTemplate, level);
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
		return (getCreator() != null) && getCreator().isEnemyFrom(player);
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return objectType;
	}
	
	/**
	 * Sets the {@code NpcObjectType} for this servant.<br>
	 * This updates the category of the NPC object.
	 * @param objectType The new {@link NpcObjectType} to assign.
	 */
	public void setNpcObjectType(NpcObjectType objectType)
	{
		this.objectType = objectType;
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
}
