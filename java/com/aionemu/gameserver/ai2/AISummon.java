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
package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.movement.SummonMoveController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * This class handles the artificial intelligence logic for {@link Summon} entities.<br>
 * It defines how summoned creatures behave and move within the game world.
 * @author xTz
 */
@AIName("summon")
public class AISummon extends AITemplate
{
	/**
	 * Retrieves the owner of this summon.<br>
	 * This method returns the {@link Summon} object associated with the AI.
	 * @return The {@code Summon} that owns this entity.
	 */
	@Override
	public Summon getOwner()
	{
		return (Summon) super.getOwner();
	}
	
	/**
	 * Retrieves the {@code NpcTemplate} for this summon.<br>
	 * This method fetches the template from the owner of the summon.
	 * @return The {@code NpcTemplate} associated with the owner.
	 */
	protected NpcTemplate getObjectTemplate()
	{
		return getOwner().getObjectTemplate();
	}
	
	/**
	 * Retrieves the {@code SpawnTemplate} associated with this summon.<br>
	 * It fetches the spawn data from the owner of the summon.
	 * @return the {@code SpawnTemplate} for the current summon.
	 */
	protected SpawnTemplate getSpawnTemplate()
	{
		return getOwner().getSpawn();
	}
	
	/**
	 * Retrieves the {@code Race} of the owner.<br>
	 * This method calls {@code getOwner} to find the associated player.<br>
	 * It then returns that player's race information.
	 * @return The {@code Race} object belonging to the summon's owner.
	 */
	protected Race getRace()
	{
		return getOwner().getRace();
	}
	
	/**
	 * Retrieves the master player of this summon.<br>
	 * This method calls {@code getOwner} to find the owner.<br>
	 * It then returns the {@code Player} associated with that owner.
	 * @return The {@code Player} who owns the summon.
	 */
	protected Player getMaster()
	{
		return getOwner().getMaster();
	}
	
	/**
	 * Retrieves the movement controller for the summon.<br>
	 * This method delegates the request to the {@link Summon} owner.
	 * @return the {@code SummonMoveController} associated with the owner.
	 */
	protected SummonMoveController getMoveController()
	{
		return getOwner().getMoveController();
	}
	
	/**
	 * Retrieves the {@link SummonController} for this summon.<br>
	 * This method gets the controller from the owner of the summon.
	 * @return the {@code SummonController} associated with the owner.
	 */
	protected SummonController getController()
	{
		return getOwner().getController();
	}
}
