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
package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Manages the visual states of {@link Player} objects in the game world.<br>
 * It handles updates for animations, effects, and other visual representations.
 * @author Source
 */
public class PlayerVisualStateService
{
	/**
	 * This method updates the visibility of a {@code Player} for other players.<br>
	 * It ensures that nearby players can see the target if they are within range.<br>
	 * It also removes the target from view if it is no longer visible.
	 * @param hiden The {@code Player} object to update.
	 */
	public static void hideValidate(Player hiden)
	{
		hiden.getKnownList().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player observer)
			{
				final boolean canSee = observer.canSee(hiden);
				final boolean isSee = observer.isSeePlayer(hiden);
				
				if (canSee && !isSee)
				{
					observer.getKnownList().addVisualObject(hiden);
				}
				else if (!canSee && isSee)
				{
					observer.getKnownList().delVisualObject(hiden, false);
				}
			}
		});
	}
	
	/**
	 * Updates the visible status of players for a specific {@code Player}.<br>
	 * It checks if other players should be seen based on distance and visibility.<br>
	 * This method synchronizes the visual state with the known list.
	 * @param search The {@link Player} whose vision is being updated.
	 */
	public static void seeValidate(Player search)
	{
		search.getKnownList().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player hide)
			{
				final boolean canSee = search.canSee(hide);
				final boolean isSee = search.isSeePlayer(hide);
				
				if (canSee && !isSee)
				{
					search.getKnownList().addVisualObject(hide);
				}
				else if (!canSee && isSee)
				{
					search.getKnownList().delVisualObject(hide, false);
				}
			}
		});
	}
}
