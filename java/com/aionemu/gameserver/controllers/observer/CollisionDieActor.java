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
package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResult;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class handles collision events that result in an actor's death.<br>
 * It processes the logic required when a {@link Creature} or {@link Player} dies due to a collision.<br>
 * It extends {@link AbstractCollisionObserver} to monitor these specific interactions.
 * @author Rolandas
 */
public class CollisionDieActor extends AbstractCollisionObserver implements IActor
{
	private boolean isEnabled = true;
	
	/**
	 * Creates a new {@link CollisionDieActor} for a specific creature.<br>
	 * This actor handles collision logic based on the provided spatial geometry.
	 * @param creature The {@code Creature} that this actor will monitor.
	 * @param geometry The {@code Spatial} area used to define the collision boundaries.
	 */
	public CollisionDieActor(Creature creature, Spatial geometry)
	{
		super(creature, geometry, CollisionIntention.MATERIAL.getId());
	}
	
	/**
	 * Updates the active status of this actor.<br>
	 * Use {@code true} to turn it on and {@code false} to turn it off.
	 * @param enable The new status for the actor.
	 */
	@Override
	public void setEnabled(boolean enable)
	{
		isEnabled = enable;
	}
	
	/**
	 * This method is called when a creature moves and triggers a collision.<br>
	 * It checks if the {@code CollisionResults} list is not empty.<br>
	 * If a GM player enters a new area, it sends a message with the geometry name.<br>
	 * Finally, it calls the {@code act} method to perform specific actions.
	 * @param collisionResults The results of the recent movement collision.
	 */
	@Override
	public void onMoved(CollisionResults collisionResults)
	{
		if (isEnabled && (collisionResults.size() != 0))
		{
			if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && (creature instanceof Player))
			{
				final Player player = (Player) creature;
				if (player.isGM())
				{
					final CollisionResult result = collisionResults.getClosestCollision();
					PacketSendUtility.sendMessage(player, "Entered " + result.getGeometry().getName());
				}
			}
			
			act();
		}
	}
	
	/**
	 * Executes the death logic for the associated creature.<br>
	 * This method checks if {@code isEnabled} is {@code true}.<br>
	 * If enabled, it calls the {@code die()} method on the creature's controller.
	 */
	@Override
	public void act()
	{
		if (isEnabled)
		{
			creature.getController().die();
		}
	}
	
	/**
	 * Stops the current action of this actor.<br>
	 * This method is currently a placeholder and performs no operations.
	 */
	@Override
	public void abort()
	{
		// Nothing to do
	}
}
