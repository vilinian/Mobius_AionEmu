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
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class handles the observation logic for the {@code GaleCyclone} skill.<br>
 * It monitors specific game events to trigger appropriate effects or behaviors.
 * @author xTz
 */
public abstract class GaleCycloneObserver extends ActionObserver
{
	private final Player player;
	private final Creature creature;
	private double oldRange;
	
	/**
	 * Creates a new instance of {@link GaleCycloneObserver}.<br>
	 * This constructor initializes the observer with specific entities.<br>
	 * It also calculates the initial distance between the two objects.
	 * @param player The {@code Player} object involved in the action.
	 * @param creature The {@code Creature} object involved in the action.
	 */
	public GaleCycloneObserver(Player player, Creature creature)
	{
		super(ObserverType.MOVE);
		this.player = player;
		this.creature = creature;
		oldRange = MathUtil.getDistance(player, creature);
	}
	
	/**
	 * This method handles the movement logic for a Creature.
	 */
	@Override
	public void moved()
	{
		final double newRange = MathUtil.getDistance(player, creature);
		if ((creature == null) || creature.getLifeStats().isAlreadyDead())
		{
			if (player != null)
			{
				player.getObserveController().removeObserver(this);
			}
			return;
		}
		
		if ((oldRange > 12) && (newRange <= 12))
		{
			onMove();
		}
		
		oldRange = newRange;
	}
	
	public abstract void onMove();
}
