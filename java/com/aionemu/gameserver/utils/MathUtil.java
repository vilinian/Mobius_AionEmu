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

import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.skillengine.properties.AreaDirections;

/**
 * Class with basic math.<br>
 * Thanks to:
 * <li>
 * <ul>
 * http://geom-java.sourceforge.net/
 * </ul>
 * <ul>
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/DistancePoint.java
 * </ul>
 * </li> <br>
 * <br>
 * Few words about speed:
 * <p/>
 * 
 * <pre>
 * Math.hypot(dx, dy); // Extremely slow
 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); // 20 times faster than hypot
 * Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous line
 * </pre>
 * <p/>
 * We don't need squared distances for calculations, {@code sqrt} is very fast.<br>
 * In fact the difference is very small, so it can be ignored.<br>
 * Feel free to run the following test (or to find a mistake in it ^^).<br>
 * <p/>
 * 
 * <pre>
 * import java.util.Random;
 *
 * public class MathSpeedTest
 * {
 * 	
 * 	private static long time;
 * 	
 * 	private static long n = 100000000L;
 * 	
 * 	public static void main(String[] args)
 * 	{
 * 		
 * 		Random r = new Random();
 * 		
 * 		long x;
 * 		long y;
 * 		
 * 		double res = 0;
 * 		setTime();
 * 		for (int i = 0; i &lt; n; i++)
 * 		{
 * 			x = r.nextInt();
 * 			y = r.nextInt();
 * 			res = Math.sqrt(x * x + y * y);
 * 		}
 * 		printTime();
 * 		System.out.println(res);
 * 		
 * 		setTime();
 * 		for (int i = 0; i &lt; n; i++)
 * 		{
 * 			x = r.nextInt();
 * 			y = r.nextInt();
 * 			res = x * x + y * y;
 * 		}
 * 		printTime();
 * 		System.out.println(Math.sqrt(res));
 * 	}
 * 	
 * 	public static void setTime()
 * 	{
 * 		time = System.currentTimeMillis();
 * 	}
 * 	
 * 	public static void printTime()
 * 	{
 * 		System.out.println(System.currentTimeMillis() - time);
 * 	}
 * }
 *
 * </pre>
 *
 * @author Disturbing
 * @author SoulKeeper modified by Wakizashi
 * @author GiGatR00n v4.7.5.x
 */
public class MathUtil
{
	/**
	 * Calculates the distance between two 2D points.<br>
	 * This method uses the {@code Point2D} coordinates to find the length of the line connecting them.
	 * @param point1 The first {@link Point2D} position.
	 * @param point2 The second {@link Point2D} position.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistance(Point2D point1, Point2D point2)
	{
		return getDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}
	
	/**
	 * Calculates the Euclidean distance between two points in a 2D plane.<br>
	 * This method uses coordinates to find the straight-line distance.
	 * @param x1 The x-coordinate of the first point.
	 * @param y1 The y-coordinate of the first point.
	 * @param x2 The x-coordinate of the second point.
	 * @param y2 The y-coordinate of the second point.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistance(float x1, float y1, float x2, float y2)
	{
		// using long to avoid possible overflows when multiplying
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		
		// Return Math.hypot(x2 - x1, y2 - y1), as Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) is 20 times faster.
		return Math.sqrt((dx * dx) + (dy * dy)); // 10 times faster then previous line
	}
	
	/**
	 * Calculates the distance between two 3D points.<br>
	 * This method uses {@code getX}, {@code getY}, and {@code getZ} to find the length.<br>
	 * It returns {@code 0} if either input is {@code null}.
	 * @param point1 The first 3D point.
	 * @param point2 The second 3D point.
	 * @return The distance between the two points as a {@code double}.
	 */
	public static double getDistance(Point3D point1, Point3D point2)
	{
		if ((point1 == null) || (point2 == null))
		{
			return 0;
		}
		
		return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
	}
	
	/**
	 * Calculates the Euclidean distance between two points in 3D space.<br>
	 * This method uses the coordinates of both points to find the straight-line distance.
	 * @param x1 The x-coordinate of the first point.
	 * @param y1 The y-coordinate of the first point.
	 * @param z1 The z-coordinate of the first point.
	 * @param x2 The x-coordinate of the second point.
	 * @param y2 The y-coordinate of the second point.
	 * @param z2 The z-coordinate of the second point.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistance(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		final float dz = z1 - z2;
		
		// We should avoid Math.pow or Math.hypot due to perfomance reasons
		return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Calculates the distance between a {@link VisibleObject} and a specific 3D point.<br>
	 * This method retrieves the coordinates from the object and compares them to the provided values.
	 * @param object The {@code VisibleObject} to measure from.
	 * @param x The X coordinate of the target point.
	 * @param y The Y coordinate of the target point.
	 * @param z The Z coordinate of the target point.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistance(VisibleObject object, float x, float y, float z)
	{
		return getDistance(object.getX(), object.getY(), object.getZ(), x, y, z);
	}
	
	/**
	 * Calculates the distance between two {@link VisibleObject} instances.<br>
	 * This method uses the 3D coordinates of both objects to find the result.
	 * @param object The first object to measure from.
	 * @param object2 The second object to measure to.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistance(VisibleObject object, VisibleObject object2)
	{
		return getDistance(object.getX(), object.getY(), object.getZ(), object2.getX(), object2.getY(), object2.getZ());
	}
	
	/**
	 * Finds the point on a line segment that is nearest to a given point.<br>
	 * This method calculates the projection of {@code p} onto the segment defined by {@code ss} and {@code se}.<br>
	 * It returns a new {@link Point2D} representing that closest location.
	 * @param ss The starting point of the line segment.
	 * @param se The ending point of the line segment.
	 * @param p The target point to check against the segment.
	 * @return The {@link Point2D} on the segment closest to {@code p}.
	 */
	public static Point2D getClosestPointOnSegment(Point ss, Point se, Point p)
	{
		return getClosestPointOnSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
	}
	
	/**
	 * Finds the point on a line segment that is nearest to a given point.<br>
	 * This method calculates the projection of {@code px, py} onto the segment defined by {@code sx1, sy1} and {@code sx2, sy2}.<br>
	 * If the projection falls outside the segment, it returns one of the endpoints.
	 * @param sx1 The x-coordinate of the start of the segment.
	 * @param sy1 The y-coordinate of the start of the segment.
	 * @param sx2 The x-coordinate of the end of the segment.
	 * @param sy2 The y-coordinate of the end of the segment.
	 * @param px The x-coordinate of the target point.
	 * @param py The y-coordinate of the target point.
	 * @return A {@link Point2D} representing the closest location on the segment.
	 */
	public static Point2D getClosestPointOnSegment(float sx1, float sy1, float sx2, float sy2, float px, float py)
	{
		final double xDelta = sx2 - sx1;
		final double yDelta = sy2 - sy1;
		
		if ((xDelta == 0) && (yDelta == 0))
		{
			throw new IllegalArgumentException("Segment start equals segment end");
		}
		
		final double u = (((px - sx1) * xDelta) + ((py - sy1) * yDelta)) / ((xDelta * xDelta) + (yDelta * yDelta));
		
		final Point2D closestPoint;
		if (u < 0)
		{
			closestPoint = new Point2D(sx1, sy1);
		}
		else if (u > 1)
		{
			closestPoint = new Point2D(sx2, sy2);
		}
		else
		{
			closestPoint = new Point2D((float) (sx1 + (u * xDelta)), (float) (sy1 + (u * yDelta)));
		}
		
		return closestPoint;
	}
	
	/**
	 * Calculates the shortest distance between a point and a line segment.<br>
	 * This method uses the {@code Point} coordinates to find the nearest distance.
	 * @param ss The starting point of the segment.
	 * @param se The ending point of the segment.
	 * @param p The point to measure from.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistanceToSegment(Point ss, Point se, Point p)
	{
		return getDistanceToSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
	}
	
	/**
	 * Calculates the shortest distance between a point and a line segment.<br>
	 * This method finds the nearest point on the segment to the given coordinates.<br>
	 * It then returns the Euclidean distance between that point and {@code px}, {@code py}.
	 * @param sx1 The x-coordinate of the start of the segment.
	 * @param sy1 The y-coordinate of the start of the segment.
	 * @param sx2 The x-coordinate of the end of the segment.
	 * @param sy2 The y-coordinate of the end of the segment.
	 * @param px The x-coordinate of the point.
	 * @param py The y-coordinate of the point.
	 * @return The distance as a {@code double}.
	 */
	public static double getDistanceToSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
	{
		final Point2D closestPoint = getClosestPointOnSegment(sx1, sy1, sx2, sy2, px, py);
		return getDistance(closestPoint.getX(), closestPoint.getY(), px, py);
	}
	
	/**
	 * Checks if {@code object2} is within a specific distance of {@code object1}.<br>
	 * This method returns {@code false} if the objects are in different worlds or instances.<br>
	 * It uses squared distance for better performance.
	 * @param object1 The first {@link VisibleObject} to check.
	 * @param object2 The second {@link VisibleObject} to check.
	 * @param range The maximum distance allowed between the two objects.
	 * @return {@code true} if the distance is less than {@code range}, otherwise {@code false}.
	 */
	public static boolean isInRange(VisibleObject object1, VisibleObject object2, float range)
	{
		if ((object1.getWorldId() != object2.getWorldId()) || (object1.getInstanceId() != object2.getInstanceId()))
		{
			return false;
		}
		
		final float dx = (object2.getX() - object1.getX());
		final float dy = (object2.getY() - object1.getY());
		return ((dx * dx) + (dy * dy)) < (range * range);
	}
	
	/**
	 * Checks if {@code object1} is within a specific 3D distance of {@code object2}.<br>
	 * This method first verifies that both objects are in the same world and instance.<br>
	 * It returns {@code true} if the distance is less than the provided {@code range}.
	 * @param object1 The first object to check.
	 * @param object2 The second object to compare against.
	 * @param range The maximum allowed distance between the two objects.
	 * @return {@code true} if the objects are in range and in the same instance; {@code false} otherwise.
	 */
	public static boolean isIn3dRange(VisibleObject object1, VisibleObject object2, float range)
	{
		if ((object1.getWorldId() != object2.getWorldId()) || (object1.getInstanceId() != object2.getInstanceId()))
		{
			return false;
		}
		
		final float dx = (object2.getX() - object1.getX());
		final float dy = (object2.getY() - object1.getY());
		final float dz = (object2.getZ() - object1.getZ());
		return ((dx * dx) + (dy * dy) + (dz * dz)) < (range * range);
	}
	
	/**
	 * Checks if the distance between two objects is within a specific 3D range.<br>
	 * It first verifies that both objects are in the same world and instance.<br>
	 * The calculation uses squared distances for better performance.
	 * @param object1 The first {@link VisibleObject} to check.
	 * @param object2 The second {@link VisibleObject} to check.
	 * @param minRange The minimum allowed distance.
	 * @param maxRange The maximum allowed distance.
	 * @return {@code true} if the distance is between {@code minRange} and {@code maxRange}, otherwise {@code false}.
	 */
	public static boolean isIn3dRangeLimited(VisibleObject object1, VisibleObject object2, float minRange, float maxRange)
	{
		if ((object1.getWorldId() != object2.getWorldId()) || (object1.getInstanceId() != object2.getInstanceId()))
		{
			return false;
		}
		
		final float dx = (object2.getX() - object1.getX());
		final float dy = (object2.getY() - object1.getY());
		final float dz = (object2.getZ() - object1.getZ());
		return (((dx * dx) + (dy * dy) + (dz * dz)) > (minRange * minRange)) && (((dx * dx) + (dy * dy) + (dz * dz)) < (maxRange * maxRange));
	}
	
	/**
	 * Checks if a point is within a specific 3D distance from another point.<br>
	 * This method compares the squared distance between two points against the squared {@code range}.<br>
	 * It returns {@code true} if the second point is closer than the specified {@code range}.
	 * @param obj1X The X coordinate of the first object.
	 * @param obj1Y The Y coordinate of the first object.
	 * @param obj1Z The Z coordinate of the first object.
	 * @param obj2X The X coordinate of the second object.
	 * @param obj2Y The Y coordinate of the second object.
	 * @param obj2Z The Z coordinate of the second object.
	 * @param range The maximum distance allowed between the two points.
	 * @return {@code true} if the objects are within the specified range, otherwise {@code false}.
	 */
	public static boolean isIn3dRange(float obj1X, float obj1Y, float obj1Z, float obj2X, float obj2Y, float obj2Z, float range)
	{
		final float dx = (obj2X - obj1X);
		final float dy = (obj2Y - obj1Y);
		final float dz = (obj2Z - obj1Z);
		return ((dx * dx) + (dy * dy) + (dz * dz)) < (range * range);
	}
	
	/**
	 * Checks if a {@linkVisibleObject} is located inside a 3D sphere.<br>
	 * It calculates the distance from the object to the center point.<br>
	 * The result is {@code true} if the distance is less than the radius.
	 * @param obj The {@linkVisibleObject} to check.
	 * @param centerX The X coordinate of the sphere center.
	 * @param centerY The Y coordinate of the sphere center.
	 * @param centerZ The Z coordinate of the sphere center.
	 * @param radius The radius of the sphere.
	 * @return {@code true} if the object is inside the sphere, otherwise {@code false}.
	 */
	public static boolean isInSphere(VisibleObject obj, float centerX, float centerY, float centerZ, float radius)
	{
		final float dx = (obj.getX() - centerX);
		final float dy = (obj.getY() - centerY);
		final float dz = (obj.getZ() - centerZ);
		return ((dx * dx) + (dy * dy) + (dz * dz)) < (radius * radius);
	}
	
	/**
	 * Calculates the angle between two points in degrees.<br>
	 * The result is normalized to a range between {@code 0.0} and {@code 360.0}.
	 * @param obj1X The x-coordinate of the first point.
	 * @param obj1Y The y-coordinate of the first point.
	 * @param obj2X The x-coordinate of the second point.
	 * @param obj2Y The y-coordinate of the second point.
	 * @return The angle in degrees from the first point to the second point.
	 */
	public static float calculateAngleFrom(float obj1X, float obj1Y, float obj2X, float obj2Y)
	{
		float angleTarget = (float) Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		
		return angleTarget;
	}
	
	/**
	 * Calculates the angle between two {@link VisibleObject} instances.<br>
	 * This method uses the X and Y coordinates of both objects to find the direction.
	 * @param obj1 The starting object for the calculation.
	 * @param obj2 The target object for the calculation.
	 * @return The calculated angle as a {@code float}.
	 */
	public static float calculateAngleFrom(VisibleObject obj1, VisibleObject obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}
	
	/**
	 * Converts a raw heading value into degrees.<br>
	 * This method multiplies the {@code clientHeading} by {@code 3}.
	 * @param clientHeading The raw heading value from the client.
	 * @return The calculated degree as a {@code float}.
	 */
	public static float convertHeadingToDegree(byte clientHeading)
	{
		final float degree = clientHeading * 3;
		return degree;
	}
	
	/**
	 * Converts an angle in degrees to a heading value.<br>
	 * This method divides the {@code float} input by {@code 3}.<br>
	 * The result is cast to a {@code byte}.
	 * @param angle The angle in degrees to convert.
	 * @return The resulting heading as a {@code byte}.
	 */
	public static byte convertDegreeToHeading(float angle)
	{
		return (byte) (angle / 3);
	}
	
	/**
	 * Checks if a {@link VisibleObject} is close to specific coordinates.<br>
	 * It compares the distance between the object and the target point against an offset.<br>
	 * The calculation includes a small buffer from {@code MOVE_CHECK_OFFSET}.
	 * @param obj The object to check.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param offset The allowed distance threshold.
	 * @return {@code true} if the object is within range, otherwise {@code false}.
	 */
	public static boolean isNearCoordinates(VisibleObject obj, float x, float y, float z, int offset)
	{
		return getDistance(obj.getX(), obj.getY(), obj.getZ(), x, y, z) < (offset + NpcMoveController.MOVE_CHECK_OFFSET);
	}
	
	/**
	 * Checks if two objects are close to each other.<br>
	 * It compares the distance between {@code obj} and {@code obj2} against a specific threshold.<br>
	 * The calculation includes an internal offset from {@link NpcMoveController}.
	 * @param obj The first object to check.
	 * @param obj2 The second object to check.
	 * @param offset The distance value used for the comparison.
	 * @return {@code true} if the objects are within the range, {@code false} otherwise.
	 */
	public static boolean isNearCoordinates(VisibleObject obj, VisibleObject obj2, int offset)
	{
		return getDistance(obj.getX(), obj.getY(), obj.getZ(), obj2.getX(), obj2.getY(), obj2.getZ()) < (offset + NpcMoveController.MOVE_CHECK_OFFSET);
	}
	
	/**
	 * Checks if one {@link Creature} is within a specific distance of another.<br>
	 * This method accounts for the collision radius of both objects.<br>
	 * It returns {@code false} if the creatures are in different worlds or instances.
	 * @param object1 The first creature to check.
	 * @param object2 The second creature to check.
	 * @param range The maximum allowed distance between the two creatures.
	 * @return {@code true} if the distance is within the specified range, otherwise {@code false}.
	 */
	public static boolean isInAttackRange(Creature object1, Creature object2, float range)
	{
		if ((object1 == null) || (object2 == null) || (object1.getWorldId() != object2.getWorldId()) || (object1.getInstanceId() != object2.getInstanceId()))
		{
			return false;
		}
		
		float offset = object1.getObjectTemplate().getBoundRadius().getCollision() + object2.getObjectTemplate().getBoundRadius().getCollision();
		if (object1.getMoveController().isInMove())
		{
			offset = +3f;
		}
		
		if (object2.getMoveController().isInMove())
		{
			offset = +3f;
		}
		
		return ((getDistance(object1, object2) - offset) <= range);
	}
	
	/**
	 * Checks if a target object is located within a cylindrical attack area.<br>
	 * The cylinder starts from the first object and extends in a specific direction.<br>
	 * It considers both the horizontal reach and the vertical height of the cylinder.
	 * @param obj1 The source object that defines the start of the cylinder.
	 * @param obj2 The target object to check for collision.
	 * @param length The total length of the cylinder.
	 * @param radius The radius of the cylinder.
	 * @param directions The direction in which the cylinder extends from {@code obj1}.
	 * @return {@code true} if {@code obj2} is inside the cylinder, otherwise {@code false}.
	 */
	public static boolean isInsideAttackCylinder(VisibleObject obj1, VisibleObject obj2, int length, int radius, AreaDirections directions)
	{
		final double radian = Math.toRadians(convertHeadingToDegree(obj1.getHeading()));
		final int direction = directions == AreaDirections.FRONT ? 0 : 1;
		final float dx = (float) (Math.cos((Math.PI * direction) + radian) * length);
		final float dy = (float) (Math.sin((Math.PI * direction) + radian) * length);
		
		final float tdx = obj2.getX() - obj1.getX();
		final float tdy = obj2.getY() - obj1.getY();
		final float tdz = obj2.getZ() - obj1.getZ();
		final float lengthSqr = length * length;
		
		final float dot = (tdx * dx) + (tdy * dy);
		if ((dot < 0.0f) || (dot > lengthSqr))
		{
			return false;
		}
		
		// distance squared to the cylinder axis
		return (((tdx * tdx) + (tdy * tdy) + (tdz * tdz)) - ((dot * dot) / lengthSqr)) <= radius;
	}
	
	/**
	 * Generates a random 2D point within a circular area.<br>
	 * This method uses the provided center and radius to calculate a valid position.
	 * @param CenterX The x-coordinate of the circle's center.
	 * @param CenterY The y-coordinate of the circle's center.
	 * @param Radius The radius of the circle.
	 * @return A new {@link Point} object representing a random location inside the circle.
	 */
	public static Point get2DPointInsideCircle(float CenterX, float CenterY, int Radius)
	{
		// Choose a Random X between -1 and 1
		final double X = (Math.random() * 2) - 1;
		
		// Calculate the Maximum and Minimum values of Y with a radius of 1
		final double YMin = -Math.sqrt(1 - (X * X));
		final double YMax = Math.sqrt(1 - (X * X));
		
		// Choose a Random Y Between Them
		final double Y = (Math.random() * (YMax - YMin)) + YMin;
		
		// Incorporate your Location and Radius values in the final value
		final double finalX = (X * Radius) + CenterX;
		final double finalY = (Y * Radius) + CenterY;
		
		return new Point((int) finalX, (int) finalY);
	}
	
	/**
	 * Calculates a coordinate on the edge of a circle.<br>
	 * It uses the center point, radius, and an angle in degrees to find the position.<br>
	 * The result is returned as a {@link Point}.
	 * @param CenterX The x-coordinate of the circle's center.
	 * @param CenterY The y-coordinate of the circle's center.
	 * @param Radius The distance from the center to the edge.
	 * @param angleInDegrees The rotation in degrees.
	 * @return A {@link Point} representing the calculated coordinates.
	 */
	public static Point get2DPointOnCircleCircumference(float CenterX, float CenterY, int Radius, float angleInDegrees)
	{
		// Convert from degrees to radians via multiplication by PI/180
		final float finalX = (float) (Radius * Math.cos((angleInDegrees * Math.PI) / 180F)) + CenterX;
		final float finalY = (float) (Radius * Math.sin((angleInDegrees * Math.PI) / 180F)) + CenterY;
		
		return new Point((int) finalX, (int) finalY);
	}
	
	/**
	 * Calculates a point on the circumference of a circle.<br>
	 * This method uses the angle between {@code CenterPoint} and {@code EndPoint} to find the position.<br>
	 * The result is based on the provided {@code Radius}.
	 * @param CenterPoint The center coordinates of the circle.
	 * @param EndPoint The point used to determine the direction from the center.
	 * @param Radius The radius of the circle.
	 * @return A new {@link Point} located on the circle's edge.
	 */
	public static Point get2DPointOnCircleCircumference(Point CenterPoint, Point EndPoint, int Radius)
	{
		// Calculates the Angle between the line through those two points "StarPoint" and "EndPoint" and the X-Axis
		final double AngleinXAxis = getAngle(CenterPoint, EndPoint);
		
		// Convert from degrees to radians via multiplication by PI/180
		final float finalX = (float) (Radius * Math.cos((AngleinXAxis * Math.PI) / 180F)) + CenterPoint.x;
		final float finalY = (float) (Radius * Math.sin((AngleinXAxis * Math.PI) / 180F)) + CenterPoint.y;
		
		return new Point((int) finalX, (int) finalY);
	}
	
	/**
	 * Calculates the angle between two points.<br>
	 * This method uses {@code atan2} to find the direction.<br>
	 * The result is returned in degrees.
	 * @param P1 The starting point.
	 * @param P2 The ending point.
	 * @return The angle in degrees between {@code P1} and {@code P2}.
	 */
	public static double getAngle(Point P1, Point P2)
	{
		final float dx = P2.x - P1.x;
		final float dy = P2.y - P1.y;
		
		final double angle = (Math.atan2(dx, dy) * 180) / Math.PI;
		
		return angle;
	}
	
	/**
	 * Finds the point on the edge of a circle that is closest to a given point.<br>
	 * This method calculates the intersection between a circle and a vector.<br>
	 * It returns a new {@link Point} object.
	 * @param Center The center coordinates of the circle.
	 * @param Radius The radius of the circle.
	 * @param GivenPoint The external point used to determine direction.
	 * @return A {@link Point} representing the closest location on the circle boundary.
	 */
	public static Point get2DPointInsideCircleClosestTo(Point Center, int Radius, Point GivenPoint)
	{
		/*
		 * P is the Point, C is the Center, and R is the radius V = (P - C); Answer = C + V / |V| * R; where |V| is length of V
		 */
		final double vX = GivenPoint.x - Center.x;
		final double vY = GivenPoint.y - Center.y;
		final double magV = Math.sqrt((vX * vX) + (vY * vY));
		
		final double aX = Center.x + ((vX / magV) * Radius);
		final double aY = Center.y + ((vY / magV) * Radius);
		
		return new Point((int) aX, (int) aY);
	}
	
	/**
	 * Generates a random {@link Point} located within an annulus.<br>
	 * The annulus is defined by two concentric circles sharing the same center.<br>
	 * It ensures the point falls between {@code Radius1} and {@code Radius2}.
	 * @param Center The central coordinates of the annulus.
	 * @param Radius1 The outer radius of the annulus.
	 * @param Radius2 The inner radius of the annulus.
	 * @return A new {@link Point} object inside the specified area.
	 */
	public static Point get2DPointInsideAnnulus(Point Center, int Radius1, int Radius2)
	{
		/*
		 * Circle 1 of Radius R1 Circle 2 of Radius R2 Where R1 > R2 Generate one Random Value for the Angular Value "theta", and one for the Distance from the Origin. As the Circles are both at the same origin this would be easy
		 */
		
		// Double theta is initialized to a random value in radians between zero and two pi.
		
		// Degrees
		final double theta = 360 * Math.random();
		
		// Distance from the Origin
		final double dist = Math.sqrt((Math.random() * ((Radius1 * Radius1) - (Radius2 * Radius2))) + (Radius2 * Radius2));
		
		final double X = (dist * Math.cos(theta)) + Center.x;
		final double Y = (dist * Math.sin(theta)) + Center.y;
		
		return new Point((int) X, (int) Y);
	}
	
	/**
	 * Checks if a {@link VisibleObject} is located within an annulus (ring) shape.<br>
	 * The object must be inside the outer radius but outside the inner radius.
	 * @param obj The object to check.
	 * @param Center The center point of the annulus.
	 * @param Radius1 The inner radius of the annulus.
	 * @param Radius2 The outer radius of the annulus.
	 * @return {@code true} if the object is inside the ring, {@code false} otherwise.
	 */
	public static boolean isInAnnulus(VisibleObject obj, Point3D Center, float Radius1, float Radius2)
	{
		/**
		 * if the given object was not within smaller circle and it was on bigger circle, it means that it is on Annulus (Ring)
		 */
		if (!isInSphere(obj, Center.getX(), Center.getY(), Center.getZ(), Radius2))
		{
			if (isInSphere(obj, Center.getX(), Center.getY(), Center.getZ(), Radius1))
			{
				return true;
			}
		}
		
		return false;
	}
	
	static final BigDecimal TWO = new BigDecimal(2);
	static final double SQRT_10 = 3.162277660168379332;
	
	/**
	 * Calculates the square root of a {@code BigDecimal} value.<br>
	 * This method uses Newton's iteration to provide high precision results.<br>
	 * It throws an {@code ArithmeticException} if the input is negative.
	 * @param squarD The number to calculate the square root for.
	 * @param rootMC The {@code MathContext} defining the required precision.
	 * @return The calculated square root as a {@code BigDecimal}.
	 */
	@SuppressWarnings("unused")
	public static BigDecimal bigSqrt(BigDecimal squarD, MathContext rootMC)
	{
		// General number and precision checking
		final int sign = squarD.signum();
		if (sign == -1)
		{
			throw new ArithmeticException("\nSquare root of a negative number: " + squarD);
		}
		else if (sign == 0)
		{
			return squarD.round(rootMC);
		}
		
		final int prec = rootMC.getPrecision(); // the requested precision
		if (prec == 0)
		{
			throw new IllegalArgumentException("\nMost roots won't have infinite precision = 0");
		}
		
		// Initial precision is that of double numbers 2^63/2 ~ 4E18
		final int BITS = 62; // 63-1 an even number of number bits
		final int nInit = 16; // precision seems 16 to 18 digits
		MathContext nMC = new MathContext(18, RoundingMode.HALF_DOWN);
		
		// Iteration variables, for the square root x and the reciprocal v
		BigDecimal x = null, e = null; // initial x: x0 ~ sqrt()
		BigDecimal v = null, g = null; // initial v: v0 = 1/(2*x)
		
		// Estimate the square root with the foremost 62 bits of squarD
		BigInteger bi = squarD.unscaledValue(); // bi and scale are a tandem
		final int biLen = bi.bitLength();
		final int shift = Math.max(0, (biLen - BITS) + ((biLen % 2) == 0 ? 0 : 1)); // even shift..
		bi = bi.shiftRight(shift); // ..floors to 62 or 63 bit BigInteger
		
		double root = Math.sqrt(bi.doubleValue());
		final BigDecimal halfBack = new BigDecimal(BigInteger.ONE.shiftLeft(shift / 2));
		
		int scale = squarD.scale();
		if ((scale % 2) == 1) // add half scales of the root to odds..
		{
			root *= SQRT_10; // 5 -> 2, -5 -> -3 need half a scale more..
		}
		
		scale = (int) Math.floor(scale / 2.); // ..where 100 -> 10 shifts the scale
		
		// Initial x - use double root - multiply by halfBack to unshift - set new scale
		x = new BigDecimal(root, nMC);
		x = x.multiply(halfBack, nMC); // x0 ~ sqrt()
		if (scale != 0)
		{
			x = x.movePointLeft(scale);
		}
		
		if (prec < nInit) // for prec 15 root x0 must surely be OK
		{
			return x.round(rootMC); // return small prec roots without iterations
		}
		
		// Initial v - the reciprocal
		v = BigDecimal.ONE.divide(TWO.multiply(x), nMC); // v0 = 1/(2*x)
		
		// Collect iteration precisions beforehand
		final ArrayList<Integer> nPrecs = new ArrayList<>();
		
		assert nInit > 3 : "Never ending loop!"; // assume nInit = 16 <= prec
		
		// Let m be the exact digits precision in an earlier! loop
		for (int m = prec + 1; m > nInit; m = (m / 2) + (m > 100 ? 1 : 2))
		{
			nPrecs.add(m);
		}
		
		// The loop of "Square Root by Coupled Newton Iteration" for simpletons
		for (int i = nPrecs.size() - 1; i > -1; i--)
		{
			// Increase precision - next iteration supplies n exact digits
			nMC = new MathContext(nPrecs.get(i), ((i % 2) == 1) ? RoundingMode.HALF_UP : RoundingMode.HALF_DOWN);
			
			// Next x // e = d - x^2
			e = squarD.subtract(x.multiply(x, nMC), nMC);
			if (i != 0)
			{
				x = x.add(e.multiply(v, nMC)); // x += e*v ~ sqrt()
			}
			else
			{
				x = x.add(e.multiply(v, rootMC), rootMC); // root x is ready!
				break;
			}
			
			// Next v // g = 1 - 2*x*v
			g = BigDecimal.ONE.subtract(TWO.multiply(x).multiply(v, nMC));
			
			v = v.add(g.multiply(v, nMC)); // v += g*v ~ 1/2/sqrt()
		}
		
		return x; // return sqrt(squarD) with precision of rootMC
	}
}
