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
package com.aionemu.gameserver.model.instance.playerreward;

/**
 * This class represents the rewards granted to a player for completing the {@code SealedHallOfKnowledge} instance.<br>
 * It extends {@link InstancePlayerReward} to handle specific reward logic for this content.
 * @author Eloann
 */
public class SealedHallOfKnowledgePlayerReward extends InstancePlayerReward
{
	/**
	 * Creates a new {@link SealedHallOfKnowledgePlayerReward} instance.<br>
	 * This constructor passes the provided ID to the parent class.
	 * @param object The unique identifier for the reward.
	 */
	public SealedHallOfKnowledgePlayerReward(Integer object)
	{
		super(object);
	}
}
