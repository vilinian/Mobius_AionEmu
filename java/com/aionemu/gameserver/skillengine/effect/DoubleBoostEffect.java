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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * This class handles the logic for applying a double boost effect to a player.<br>
 * It modifies movement attributes and updates the {@link Player} status accordingly.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoubleBoostEffect")
public class DoubleBoostEffect extends EffectTemplate
{
	@XmlAttribute(name = "distance")
	private float distance;
	
	@XmlAttribute(name = "direction")
	private float direction;
	
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		final Player effector = (Player) effect.getEffector();
		PacketSendUtility.sendPacket(effector, new SM_TARGET_UPDATE(effector));
		final Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
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
		effect.addSucessEffect(this);
		effect.setDashStatus(DashStatus.DASH);
		final Player effector = (Player) effect.getEffector();
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		final float x1 = (float) (Math.cos((Math.PI * direction) + radian) * distance);
		final float y1 = (float) (Math.sin((Math.PI * direction) + radian) * distance);
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new SM_TRANSFORM(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId(), effector.getTransformedSkillId()));
		final byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		final Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1, effector.getY() + y1, effector.getZ(), false, intentions);
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
	}
}
