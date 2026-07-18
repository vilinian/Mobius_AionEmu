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
package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * Provides utility methods for handling and calculating spatial coordinates.<br>
 * This class simplifies common operations related to {@code Position} data within the game world.
 * @author ATracer
 */
public class PositionUtil
{
	private static final float MAX_ANGLE_DIFF = 90f;
	
	/**
	 * Checks if {@code object1} is positioned behind {@code object2}.<br>
	 * This method compares the heading of {@code object2} with its relative position to {@code object1}.<br>
	 * It returns {@code true} if the angle difference is within a specific threshold.
	 * @param object1 The object to check.
	 * @param object2 The target object used as a reference.
	 * @return {@code true} if {@code object1} is behind {@code object2}, otherwise {@code false}.
	 */
	public static boolean isBehindTarget(VisibleObject object1, VisibleObject object2)
	{
		final float angleObject1 = MathUtil.calculateAngleFrom(object1, object2);
		final float angleObject2 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;
		
		if (angleDiff <= (-360 + MAX_ANGLE_DIFF))
		{
			angleDiff += 360;
		}
		
		if (angleDiff >= (360 - MAX_ANGLE_DIFF))
		{
			angleDiff -= 360;
		}
		
		return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}
	
	/**
	 * Checks if {@code object1} is facing toward {@code object2}.<br>
	 * This method compares the heading of {@code object1} with its position relative to {@code object2}.<br>
	 * It returns {@code true} if the angle difference is within the allowed limit.
	 * @param object1 The object whose heading is being checked.
	 * @param object2 The target object used as a reference point.
	 * @return {@code true} if {@code object1} is in front of {@code object2}, otherwise {@code false}.
	 */
	public static boolean isInFrontOfTarget(VisibleObject object1, VisibleObject object2)
	{
		final float angleObject2 = MathUtil.calculateAngleFrom(object2, object1);
		final float angleObject1 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;
		
		if (angleDiff <= (-360 + MAX_ANGLE_DIFF))
		{
			angleDiff += 360;
		}
		
		if (angleDiff >= (360 - MAX_ANGLE_DIFF))
		{
			angleDiff -= 360;
		}
		
		return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}
	
	/**
	 * Checks if {@code object2} is positioned behind {@code object1}.<br>
	 * This method calculates the relative position based on the heading of {@link VisibleObject}.
	 * @param object1 The reference object to check against.
	 * @param object2 The target object to check.
	 * @return {@code true} if {@code object2} is behind {@code object1}, otherwise {@code false}.
	 */
	public static boolean isBehind(VisibleObject object1, VisibleObject object2)
	{
		float angle = MathUtil.convertHeadingToDegree(object1.getHeading()) + 90;
		if (angle >= 360)
		{
			angle -= 360;
		}
		
		final double radian = Math.toRadians(angle);
		final float x0 = object1.getX();
		final float y0 = object1.getY();
		final float x1 = (float) (Math.cos(radian) * 5) + x0;
		final float y1 = (float) (Math.sin(radian) * 5) + y0;
		final float xA = object2.getX();
		final float yA = object2.getY();
		final float temp = ((x1 - x0) * (yA - y0)) - ((y1 - y0) * (xA - x0));
		return temp > 0;
	}
	
	/**
	 * Calculates the relative angle between two {@link VisibleObject} instances.<br>
	 * This method helps determine the orientation of one object compared to another.
	 * @param object1 The first object used as the reference point.
	 * @param object2 The target object to measure against.
	 * @return The calculated angle difference in degrees.
	 */
	public static float getAngleToTarget(VisibleObject object1, VisibleObject object2)
	{
		float angleObject1 = MathUtil.convertHeadingToDegree(object1.getHeading()) - 180;
		if (angleObject1 < 0)
		{
			angleObject1 += 360;
		}
		
		final float angleObject2 = MathUtil.calculateAngleFrom(object1, object2);
		float angleDiff = angleObject1 - angleObject2 - 180;
		if (angleDiff < 0)
		{
			angleDiff += 360;
		}
		
		return angleDiff;
	}
	
	/**
	 * Calculates the difference between the directional bounds of two objects.<br>
	 * This method uses the angle between {@code object1} and {@code object2}.<br>
	 * It accounts for the bounding radius of each object based on their orientation.
	 * @param object1 The first {@link VisibleObject} to compare.
	 * @param object2 The second {@link VisibleObject} to compare.
	 * @param inverseTarget A boolean that determines if the target order is swapped.
	 * @return The calculated difference between the two bounds as a {@code float}.
	 */
	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2, boolean inverseTarget)
	{
		float angle = 90 - (inverseTarget ? getAngleToTarget(object2, object1) : getAngleToTarget(object1, object2));
		if (angle < 0)
		{
			angle += 360;
		}
		
		final double radians = Math.toRadians(angle);
		final float x1 = (float) (object1.getX() + (object1.getObjectTemplate().getBoundRadius().getSide() * Math.cos(radians)));
		final float y1 = (float) (object1.getY() + (object1.getObjectTemplate().getBoundRadius().getFront() * Math.sin(radians)));
		final float x2 = (float) (object2.getX() + (object2.getObjectTemplate().getBoundRadius().getSide() * Math.cos(Math.PI + radians)));
		final float y2 = (float) (object2.getY() + (object2.getObjectTemplate().getBoundRadius().getFront() * Math.sin(Math.PI + radians)));
		final float bound1 = (float) MathUtil.getDistance(object1.getX(), object1.getY(), x1, y1);
		final float bound2 = (float) MathUtil.getDistance(object2.getX(), object2.getY(), x2, y2);
		return bound1 - bound2;
	}
	
	/**
	 * Calculates the directional boundary between two objects.<br>
	 * This method determines the angle limit for {@link VisibleObject} positioning.<br>
	 * It uses the default behavior where {@code inverseTarget} is {@code false}.
	 * @param object1 The first object used as a reference point.
	 * @param object2 The second object used to calculate the direction.
	 * @return The calculated directional bound as a {@code float}.
	 */
	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2)
	{
		return getDirectionalBound(object1, object2, false);
	}
	
	/**
	 * Calculates the heading required to move away from a target.<br>
	 * It determines the angle between {@code fromObject} and {@code object}.<br>
	 * The result is converted into a standard heading format.
	 * @param fromObject The starting point for the movement.
	 * @param object The target object to move away from.
	 * @return The calculated heading as a {@code byte}.
	 */
	public static byte getMoveAwayHeading(VisibleObject fromObject, VisibleObject object)
	{
		final float angle = MathUtil.calculateAngleFrom(fromObject, object);
		final byte heading = MathUtil.convertDegreeToHeading(angle);
		return heading;
	}
}
