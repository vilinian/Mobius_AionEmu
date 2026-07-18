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
package com.aionemu.gameserver.skillengine.effect;

import java.util.concurrent.ScheduledFuture;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.PositionUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles the logic for applying a fear status effect to a {@link Creature}.<br>
 * It prevents the target from moving or performing actions for a specified duration.<br>
 * This class manages the state transitions and notifications required for the fear mechanic.
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FearEffect")
public class FearEffect extends EffectTemplate
{
	@XmlAttribute
	protected int resistchance = 100;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.getEffected().getEffectController().removeHideEffects();
		effect.addToEffectedController();
	}
	
	/**
	 * Calculates the attributes for a specific {@code Effect}.<br>
	 * This method updates the {@code effect} to include an AP boost.<br>
	 * It also links this instance as a success effect.
	 * @param effect The {@code Effect} object to be updated.
	 */
	@Override
	public void calculate(Effect effect)
	{
		super.calculate(effect, StatEnum.FEAR_RESISTANCE, null);
	}
	
	/**
	 * Starts the {@link Effect} and applies it to the target.<br>
	 * This method handles state changes like stopping movement and setting AI states.<br>
	 * It also schedules periodic tasks if fear is enabled in the configuration.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effect.setAbnormal(AbnormalState.FEAR.getId());
		effected.getEffectController().setAbnormal(AbnormalState.FEAR.getId());
		
		// PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		effected.getController().stopMoving();
		
		if (effected instanceof Npc)
		{
			((NpcAI2) effected.getAi2()).setStateIfNot(AIState.FEAR);
		}
		
		if (GeoDataConfig.FEAR_ENABLE)
		{
			final ScheduledFuture<?> fearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FearTask(effector, effected), 0, 1000);
			effect.setPeriodicTask(fearTask, position);
		}
		
		// If the resistance chance against the fear effect from damage is lower than 100, the fear can be interrupted by damage, such as with skill ID 540 (Terrible Howl).
		if (resistchance < 100)
		{
			final ActionObserver observer = new ActionObserver(ObserverType.ATTACKED)
			{
				@Override
				public void attacked(Creature creature)
				{
					if (Rnd.get(0, 100) > resistchance)
					{
						effected.getEffectController().removeEffect(effect.getSkillId());
					}
				}
			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);
		}
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.FEAR.getId());
		
		// for now we support only players
		if (GeoDataConfig.FEAR_ENABLE)
		{
			effect.getEffected().getMoveController().abortMove(); // TODO impl stopMoving?
		}
		
		if (effect.getEffected() instanceof Npc)
		{
			((NpcAI2) effect.getEffected().getAi2()).onCreatureEvent(AIEventType.ATTACK, effect.getEffector());
		}
		
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_TARGET_IMMOBILIZE(effect.getEffected()));
		
		if (resistchance < 100)
		{
			final ActionObserver observer = effect.getActionObserver(position);
			if (observer != null)
			{
				effect.getEffected().getObserveController().removeObserver(observer);
			}
		}
	}
	
	class FearTask implements Runnable
	{
		private final Creature effector;
		private final Creature effected;
		
		FearTask(Creature effector, Creature effected)
		{
			this.effector = effector;
			this.effected = effected;
		}
		
		@Override
		public void run()
		{
			if (effected.getEffectController().isUnderFear())
			{
				final float x = effected.getX();
				final float y = effected.getY();
				if (!MathUtil.isNearCoordinates(effected, effector, 40))
				{
					return;
				}
				
				final byte moveAwayHeading = PositionUtil.getMoveAwayHeading(effector, effected);
				final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(moveAwayHeading));
				final float maxDistance = effected.getGameStats().getMovementSpeedFloat();
				final float x1 = (float) (Math.cos(radian) * maxDistance);
				final float y1 = (float) (Math.sin(radian) * maxDistance);
				final byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
				final Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, x + x1, y + y1, effected.getZ(), true, intentions);
				if (effected.isFlying())
				{
					closestCollision.setZ(effected.getZ());
				}
				
				if (effected instanceof Npc)
				{
					((Npc) effected).getMoveController().resetMove();
					((Npc) effected).getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ());
				}
				else
				{
					effected.getMoveController().setNewDirection(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), moveAwayHeading);
					effected.getMoveController().startMovingToDestination();
				}
			}
		}
	}
}
