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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * This class handles the logic for observing {@link FlyRing} effects on players.<br>
 * It monitors specific game events to trigger relevant actions or state changes.
 * @author xavier, Source
 */
public class FlyRingObserver extends ActionObserver
{
	private final Player player;
	private final FlyRing ring;
	private Point3D oldPosition;
	SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(1856);
	
	/**
	 * Creates a new instance of the {@link FlyRingObserver}.<br>
	 * This constructor initializes the observer with default values.<br>
	 * It sets the {@code player}, {@code ring}, and {@code oldPosition} fields to {@code null}.
	 */
	public FlyRingObserver()
	{
		super(ObserverType.MOVE);
		player = null;
		ring = null;
		oldPosition = null;
	}
	
	/**
	 * Creates a new {@link FlyRingObserver} for a specific player and fly ring.<br>
	 * This constructor initializes the observer with the current position of the {@code player}.
	 * @param ring The {@code FlyRing} object associated with this observer.
	 * @param player The {@code Player} who is using the fly ring.
	 */
	public FlyRingObserver(FlyRing ring, Player player)
	{
		super(ObserverType.MOVE);
		this.player = player;
		this.ring = ring;
		oldPosition = new Point3D(player.getX(), player.getY(), player.getZ());
	}
	
	/**
	 * This method handles the movement logic for a Creature.
	 */
	@Override
	public void moved()
	{
		final Point3D newPosition = new Point3D(player.getX(), player.getY(), player.getZ());
		boolean passedThrough = false;
		
		if (ring.getPlane().intersect(oldPosition, newPosition))
		{
			final Point3D intersectionPoint = ring.getPlane().intersection(oldPosition, newPosition);
			if (intersectionPoint != null)
			{
				final double distance = Math.abs(ring.getPlane().getCenter().distance(intersectionPoint));
				
				if (distance < ring.getTemplate().getRadius())
				{
					passedThrough = true;
				}
			}
			else
			{
				if (MathUtil.isIn3dRange(ring, player, ring.getTemplate().getRadius()))
				{
					passedThrough = true;
				}
			}
		}
		
		if (passedThrough)
		{
			if ((ring.getTemplate().getMap() == 400010000) || isQuestactive() || isInstancetactive())
			{
				final Effect speedUp = new Effect(player, player, skillTemplate, skillTemplate.getLvl(), 0);
				speedUp.initialize();
				speedUp.addAllEffectToSucess();
				speedUp.applyEffect();
			}
			
			QuestEngine.getInstance().onPassFlyingRing(new QuestEnv(null, player, 0, 0), ring.getName());
		}
		
		oldPosition = newPosition;
	}
	
	/**
	 * Checks if the current instance is active for the flying ring.<br>
	 * It calls {@code onPassFlyingRing} on the world map instance.
	 * @return {@code true} if the instance is active, {@code false} otherwise.
	 */
	private boolean isInstancetactive()
	{
		return ring.getPosition().getWorldMapInstance().getInstanceHandler().onPassFlyingRing(player, ring.getName());
	}
	
	/**
	 * Checks if the player currently has a specific quest active.<br>
	 * It determines the {@code questId} based on whether the player is an Asmodian or Irushan.<br>
	 * The method verifies that the quest status is {@code START} and the quest variable is between 2 and 8.
	 * @return {@code true} if the specific quest requirements are met, otherwise {@code false}.
	 */
	private boolean isQuestactive()
	{
		final int questId = player.getRace() == Race.ASMODIANS ? 2042 : 1044;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if (qs == null)
		{
			return false;
		}
		
		return (qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) >= 2) && (qs.getQuestVarById(0) <= 8);
	}
}
