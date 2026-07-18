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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * Handles the logic for applying a stagger effect to a {@link Creature}.<br>
 * This effect interrupts the target's movement and actions.<br>
 * It is used by the skill engine to simulate physical displacement or stun mechanics.
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaggerEffect")
public class StaggerEffect extends EffectTemplate
{
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE))
		{
			effect.addToEffectedController();
			effect.setIsPhysicalState(true);
			final Creature effected = effect.getEffected();
			if ((effected instanceof Player) && effected.isInState(CreatureState.GLIDING))
			{
				((Player) effected).getFlyController().endFly(true);
			}
			
			effected.getController().cancelCurrentSkill();
			effected.getEffectController().removeParalyzeEffects();
			effected.getMoveController().abortMove();
			PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_FORCED_MOVE(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
			World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
		}
	}
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.STAGGER.getId());
		effect.setAbnormal(AbnormalState.STAGGER.getId());
	}
	
	/**
	 * Calculates the logic for a {@code StaggerEffect}.<br>
	 * This method checks if the target is susceptible to stagger.<br>
	 * It cancels the target's current skill and moves them backward.
	 * @param effect The {@code Effect} object to process.
	 */
	@Override
	public void calculate(Effect effect)
	{
		if (effect.getEffected().getEffectController().hasPhysicalStateEffect() || !super.calculate(effect, StatEnum.STAGGER_RESISTANCE, SpellStatus.STAGGER))
		{
			return;
		}
		
		// Check for packets if this must be fixed someway, but for now it works good so
		effect.setSkillMoveType(SkillMoveType.STAGGER);
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		
		// Move effected 3 meters backward as on retail
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(radian) * 3);
		float y1 = (float) (Math.sin(radian) * 3);
		
		float z = effected.getZ();
		final byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		final Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1, effected.getY() + y1, effected.getZ(), false, intentions);
		x1 = closestCollision.x;
		y1 = closestCollision.y;
		z = closestCollision.z;
		effect.setTargetLoc(x1, y1, z);
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
		effect.setIsPhysicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STAGGER.getId());
	}
}
