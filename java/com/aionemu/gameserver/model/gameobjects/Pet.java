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

import com.aionemu.gameserver.controllers.PetController;
import com.aionemu.gameserver.controllers.movement.MoveController;
import com.aionemu.gameserver.controllers.movement.PetMoveController;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * Represents a pet entity within the game world.<br>
 * This class handles the data and behavior for pets owned by a {@link Player}.<br>
 * It extends {@link VisibleObject} to manage its presence in the game environment.
 * @author ATracer
 */
public class Pet extends VisibleObject
{
	private final Player master;
	private final MoveController moveController;
	private final PetTemplate petTemplate;
	
	/**
	 * Creates a new instance of a {@link Pet}.<br>
	 * This constructor initializes the pet with its template, controller, and owner.<br>
	 * It also sets up the movement logic for the pet.
	 * @param petTemplate The {@code PetTemplate} containing the base data for this pet.
	 * @param controller The {@code PetController} used to manage pet actions.
	 * @param commonData The {@code PetCommonData} containing shared object information.
	 * @param master The {@link Player} who owns this pet.
	 */
	public Pet(PetTemplate petTemplate, PetController controller, PetCommonData commonData, Player master)
	{
		super(commonData.getObjectId(), controller, null, commonData, new WorldPosition(master.getWorldId()));
		controller.setOwner(this);
		this.master = master;
		this.petTemplate = petTemplate;
		moveController = new PetMoveController();
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
	 * Retrieves the unique identifier for this pet.<br>
	 * This value is obtained from the {@link PetTemplate}.
	 * @return The {@code int} ID of the pet template.
	 */
	public int getPetId()
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
	 * Retrieves the common data associated with this {@link Pet}.<br>
	 * This method returns the underlying {@code PetCommonData} object.
	 * @return the {@code PetCommonData} for this pet.
	 */
	public PetCommonData getCommonData()
	{
		return (PetCommonData) objectTemplate;
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
	 * Retrieves the template associated with this {@link Pet}.<br>
	 * This provides access to the base data for the pet.
	 * @return the {@code PetTemplate} object.
	 */
	public PetTemplate getPetTemplate()
	{
		return petTemplate;
	}
}
