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
package system.handlers.ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PositionUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * This class handles the artificial intelligence for dummy NPCs that exhibit fear behavior.<br>
 * It extends {@link GeneralNpcAI2} to provide specific logic for these entities.
 * @author Steve
 * @AE implement Wasacacax
 */
@AIName("fear_dummy")
public class FearDummyAI2 extends GeneralNpcAI2
{
	/**
	 * Adjusts the amount of damage dealt by an attack.<br>
	 * This method is used to calculate final damage values.
	 * @param damage The initial damage value to be modified.
	 * @return The multiplier applied to the damage.
	 */
	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}
	
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		final float x = getOwner().getX();
		final float y = getOwner().getY();
		if (!MathUtil.isNearCoordinates(getOwner(), creature, 40))
		{
			return;
		}
		
		final byte moveAwayHeading = PositionUtil.getMoveAwayHeading(creature, getOwner());
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(moveAwayHeading));
		final float maxDistance = getOwner().getGameStats().getMovementSpeedFloat();
		final float x1 = (float) (Math.cos(radian) * maxDistance);
		final float y1 = (float) (Math.sin(radian) * maxDistance);
		final byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		final Vector3f closestCollision = GeoService.getInstance().getClosestCollision(getOwner(), x + x1, y + y1, getOwner().getZ(), true, intentions);
		if (getOwner().isFlying())
		{
			closestCollision.setZ(getOwner().getZ());
		}
		
		getOwner().getMoveController().resetMove();
		getOwner().getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ());
	}
	
}
