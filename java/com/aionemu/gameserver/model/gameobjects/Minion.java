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

import com.aionemu.gameserver.controllers.MinionController;
import com.aionemu.gameserver.controllers.movement.MinionMoveController;
import com.aionemu.gameserver.controllers.movement.MoveController;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a minion entity within the game world.<br>
 * This class handles the core data and behavior for non-player creatures that follow players or NPCs.<br>
 * It extends {@link VisibleObject} to provide basic spatial and visual properties.
 * @author ATracer
 */
public class Minion extends VisibleObject
{
	private final Player master;
	private final MoveController moveController;
	private final MinionTemplate minionTemplate;
	
	/**
	 * Creates a new instance of a {@link Minion}.<br>
	 * This constructor initializes the minion with its template, controller, and owner.
	 * @param minionTemplate The {@code MinionTemplate} defining the minion's base stats.
	 * @param controller The {@code MinionController} used to handle minion logic.
	 * @param commonData The {@code MinionCommonData} containing shared object information.
	 * @param master The {@link Player} who owns this minion.
	 */
	public Minion(MinionTemplate minionTemplate, MinionController controller, MinionCommonData commonData, Player master)
	{
		super(commonData.getObjectId(), controller, null, commonData, new WorldPosition(master.getWorldId()));
		controller.setOwner(this);
		this.master = master;
		this.minionTemplate = minionTemplate;
		moveController = new MinionMoveController();
	}
	
	/**
	 * Retrieves the owner of this {@link Minion}.<br>
	 * This returns the {@code Player} who controls the minion.
	 * @return The {@code Player} object representing the master.
	 */
	public Player getMaster()
	{
		return master;
	}
	
	/**
	 * Retrieves the unique identifier for this minion.<br>
	 * This value is obtained from the {@link MinionTemplate}.
	 * @return The {@code int} ID of the minion template.
	 */
	public int getMinionId()
	{
		return objectTemplate.getTemplateId();
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
	 * Retrieves the common data associated with this {@link Minion}.<br>
	 * This method returns the underlying {@code objectTemplate}.
	 * @return The {@code MinionCommonData} for this minion.
	 */
	public MinionCommonData getCommonData()
	{
		return (MinionCommonData) objectTemplate;
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
	 * Retrieves the template associated with this {@link Minion}.<br>
	 * This provides access to the base data for the minion.
	 * @return the {@code MinionTemplate} object.
	 */
	public MinionTemplate getMinionTemplate()
	{
		return minionTemplate;
	}
}
