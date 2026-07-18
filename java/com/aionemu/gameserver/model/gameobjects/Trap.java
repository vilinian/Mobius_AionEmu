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
import com.aionemu.gameserver.model.stats.container.HomingGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.stats.container.TrapGameStats;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * Represents a trap object within the game world.<br>
 * This class handles the behavior and properties of environmental hazards that interact with players.<br>
 * It extends {@link SummonedObject} to manage its presence in the game environment.
 * @author ATracer
 */
public class Trap extends SummonedObject<Creature>
{
	/**
	 * Creates a new instance of a {@link Trap}.<br>
	 * This constructor initializes the trap with its required data.
	 * @param objId The unique identifier for the object.
	 * @param controller The {@link NpcController} that manages this entity.
	 * @param spawnTemplate The template defining where the object spawns.
	 * @param objectTemplate The template defining the base properties of the object.
	 */
	public Trap(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate)
	{
		super(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}
	
	/**
	 * Initializes the statistics containers for this object.<br>
	 * It sets up both {@link HomingGameStats} and {@link NpcLifeStats}.
	 * @param level The level of the object.
	 */
	@Override
	protected void setupStatContainers(byte level)
	{
		setGameStats(new TrapGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}
	
	/**
	 * Retrieves the level of this trap.<br>
	 * If no creator exists, it returns {@code 1}.<br>
	 * Otherwise, it returns the level of the creator.
	 * @return The level as a {@code byte}.
	 */
	@Override
	public byte getLevel()
	{
		return (getCreator() == null ? 1 : getCreator().getLevel());
	}
	
	/**
	 * Retrieves the type of the NPC object.<br>
	 * This method returns a constant value representing a standard NPC.
	 * @return the {@code NpcObjectType} of this entity.
	 */
	@Override
	public NpcObjectType getNpcObjectType()
	{
		return NpcObjectType.TRAP;
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
