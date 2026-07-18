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
package com.aionemu.gameserver.model;

/**
 * Defines the different types of animations used for teleportation effects.<br>
 * This enum is used by {@link com.aionemu.gameserver.model.TeleportAnimation} to determine which visual effect to play during a player's movement.
 * @author xTz
 */
public enum TeleportAnimation
{
	NO_ANIMATION(0, 0),
	BEAM_ANIMATION(1, 3),
	JUMP_ANIMATION(3, 10),
	JUMP_ANIMATION_2(4, 10),
	FIRE_ANIMATION(4, 0x0B), // 5.0
	JUMP_ANIMATION_3(8, 3),
	MAGE_ANIMATION(8, 10); // Mage Grp Gate
	
	private final int startAnimation;
	private final int endAnimation;
	
	/**
	 * Creates a new {@link TeleportAnimation} instance.<br>
	 * This constructor sets the animation IDs for the teleport effect.
	 * @param startAnimation The ID of the starting animation.
	 * @param endAnimation The ID of the ending animation.
	 */
	private TeleportAnimation(int startAnimation, int endAnimation)
	{
		this.startAnimation = startAnimation;
		this.endAnimation = endAnimation;
	}
	
	/**
	 * Retrieves the starting animation ID for this teleport type.<br>
	 * This value is used to determine which animation to play first.
	 * @return The {@code int} value of the starting animation.
	 */
	public int getStartAnimationId()
	{
		return startAnimation;
	}
	
	/**
	 * Retrieves the ID of the animation that plays at the end.<br>
	 * This value corresponds to the {@code endAnimation} field.
	 * @return The integer ID of the final animation.
	 */
	public int getEndAnimationId()
	{
		return endAnimation;
	}
	
	/**
	 * Checks if the teleport animation is disabled.<br>
	 * This method returns {@code true} if the start animation ID is {@code 0}.
	 * @return {@code true} if there is no animation, otherwise {@code false}.
	 */
	public boolean isNoAnimation()
	{
		return getStartAnimationId() == 0;
	}
}
