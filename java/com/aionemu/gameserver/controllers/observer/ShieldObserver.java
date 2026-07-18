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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.shield.Shield;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class monitors and manages {@link Shield} effects for entities.<br>
 * It handles the logic for shield interactions within the game world.
 * @author Wakizashi, Source
 */
public class ShieldObserver extends ActionObserver
{
	private final Creature creature;
	private final Shield shield;
	private final Point3D oldPosition;
	
	/**
	 * Creates a new instance of the {@code ShieldObserver}.<br>
	 * This constructor initializes the observer with default values.<br>
	 * All internal fields are set to {@code null}.
	 */
	public ShieldObserver()
	{
		super(ObserverType.MOVE);
		creature = null;
		shield = null;
		oldPosition = null;
	}
	
	/**
	 * Creates a new {@link ShieldObserver} for a specific creature and shield.<br>
	 * This constructor initializes the observer with the current position of the {@code creature}.
	 * @param shield The {@code Shield} object to be observed.
	 * @param creature The {@code Creature} that owns the shield.
	 */
	public ShieldObserver(Shield shield, Creature creature)
	{
		super(ObserverType.MOVE);
		this.creature = creature;
		this.shield = shield;
		oldPosition = new Point3D(creature.getX(), creature.getY(), creature.getZ());
	}
	
	/**
	 * This method handles the movement logic for a Creature.
	 */
	@Override
	public void moved()
	{
		boolean passedThrough = false;
		boolean isGM = false;
		
		if (SiegeService.getInstance().getFortress(shield.getId()).isUnderShield())
		{
			if (!((creature.getZ() < shield.getZ()) && (oldPosition.getZ() < shield.getZ())))
			{
				if (MathUtil.isInSphere(shield, (float) oldPosition.getX(), (float) oldPosition.getY(), (float) oldPosition.getZ(), shield.getTemplate().getRadius()) != MathUtil.isIn3dRange(shield, creature, shield.getTemplate().getRadius()))
				{
					passedThrough = true;
				}
			}
		}
		
		if (passedThrough)
		{
			if (creature instanceof Player)
			{
				PacketSendUtility.sendMessage(((Player) creature), "You passed through shield.");
				isGM = ((Player) creature).isGM();
			}
			
			if (!isGM)
			{
				if (!(creature.getLifeStats().isAlreadyDead()))
				{
					creature.getController().die();
				}
				
				if (creature instanceof Player)
				{
					((Player) creature).getFlyController().endFly(true);
				}
				
				creature.getObserveController().removeObserver(this);
			}
		}
		
		oldPosition.x = creature.getX();
		oldPosition.y = creature.getY();
		oldPosition.z = creature.getZ();
	}
}
