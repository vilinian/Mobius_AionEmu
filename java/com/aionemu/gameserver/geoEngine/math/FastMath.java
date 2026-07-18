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
package com.aionemu.gameserver.geoEngine.math;

import java.util.Random;

/**
 * This class provides high-performance math approximations and {@code float} equivalents for standard {@code Math} functions.<br>
 * All methods and constants are provided as static members to optimize calculations within the geo engine.
 * @author Various
 */
final public class FastMath
{
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This class only contains static methods.
	 */
	private FastMath()
	{
	}
	
	/**
	 * A "close to zero" double epsilon value for use
	 */
	public static final double DBL_EPSILON = 2.220446049250313E-16d;
	/**
	 * A "close to zero" float epsilon value for use
	 */
	public static final float FLT_EPSILON = 1.1920928955078125E-7f;
	/**
	 * A "close to zero" float epsilon value for use
	 */
	public static final float ZERO_TOLERANCE = 0.0001f;
	public static final float ONE_THIRD = 1f / 3f;
	/**
	 * The value PI as a float. (180 degrees)
	 */
	public static final float PI = (float) Math.PI;
	/**
	 * The value 2PI as a float. (360 degrees)
	 */
	public static final float TWO_PI = 2.0f * PI;
	/**
	 * The value PI/2 as a float. (90 degrees)
	 */
	public static final float HALF_PI = 0.5f * PI;
	/**
	 * The value PI/4 as a float. (45 degrees)
	 */
	public static final float QUARTER_PI = 0.25f * PI;
	/**
	 * The value 1/PI as a float.
	 */
	public static final float INV_PI = 1.0f / PI;
	/**
	 * The value 1/(2PI) as a float.
	 */
	public static final float INV_TWO_PI = 1.0f / TWO_PI;
	/**
	 * A value to multiply a degree value by, to convert it to radians.
	 */
	public static final float DEG_TO_RAD = PI / 180.0f;
	/**
	 * A value to multiply a radian value by, to convert it to degrees.
	 */
	public static final float RAD_TO_DEG = 180.0f / PI;
	/**
	 * A precreated random object for random numbers.
	 */
	public static final Random rand = new Random(System.currentTimeMillis());
	
	/**
	 * Checks if a given integer is a power of two.<br>
	 * This method returns {@code true} if the number is a power of two.<br>
	 * It returns {@code false} for zero or negative numbers.
	 * @param number The integer to check.
	 * @return {@code true} if the input is a power of two, otherwise {@code false}.
	 */
	public static boolean isPowerOfTwo(int number)
	{
		return (number > 0) && ((number & (number - 1)) == 0);
	}
	
	/**
	 * Finds the smallest power of two that is greater than or equal to the input.<br>
	 * This method uses {@code log} and {@code double)} to calculate the result.
	 * @param number The integer value to check.
	 * @return The nearest power of two as an {@code int}.
	 */
	public static int nearestPowerOfTwo(int number)
	{
		return (int) Math.pow(2, Math.ceil(Math.log(number) / Math.log(2)));
	}
	
	/**
	 * Calculates a linear interpolation between two values.<br>
	 * This method finds a value based on a percentage of the distance between {@code startValue} and {@code endValue}.<br>
	 * It handles edge cases where the scale is outside the standard range.
	 * @param scale The interpolation factor, typically between 0.0 and 1.0.
	 * @param startValue The value at the beginning of the range.
	 * @param endValue The value at the end of the range.
	 * @return The interpolated {@code float} result.
	 */
	public static float interpolateLinear(float scale, float startValue, float endValue)
	{
		if ((startValue == endValue) || (scale <= 0f))
		{
			return startValue;
		}
		
		if (scale >= 1f)
		{
			return endValue;
		}
		
		return ((1f - scale) * startValue) + (scale * endValue);
	}
	
	/**
	 * Calculates a linear interpolation between two {@code Vector3f} points.<br>
	 * This method returns a new vector based on the provided scale.
	 * @param scale The interpolation factor, usually between 0.0 and 1.0.
	 * @param startValue The starting {@code Vector3f} position.
	 * @param endValue The ending {@code Vector3f} position.
	 * @return A new {@code Vector3f} representing the interpolated result.
	 */
	public static Vector3f interpolateLinear(float scale, Vector3f startValue, Vector3f endValue)
	{
		final Vector3f res = new Vector3f();
		res.x = interpolateLinear(scale, startValue.x, endValue.x);
		res.y = interpolateLinear(scale, startValue.y, endValue.y);
		res.z = interpolateLinear(scale, startValue.z, endValue.z);
		return res;
	}
	
	/**
	 * Calculates a smooth curve between points using the Catmull-Rom spline algorithm.<br>
	 * This method is useful for creating fluid motion or paths in 3D space.
	 * @param u The interpolation factor, typically ranging from {@code 0.0} to {@code 1.0}.
	 * @param T The tension or control parameter of the spline.
	 * @param p0 The first control point before the segment.
	 * @param p1 The starting point of the current segment.
	 * @param p2 The ending point of the current segment.
	 * @param p3 The second control point after the segment.
	 * @return The interpolated {@code float} value at position {@code u}.
	 */
	public static float interpolateCatmullRom(float u, float T, float p0, float p1, float p2, float p3)
	{
		double c1, c2, c3, c4;
		c1 = p1;
		c2 = (-1.0 * T * p0) + (T * p2);
		c3 = (2 * T * p0) + ((T - 3) * p1) + ((3 - (2 * T)) * p2) + (-T * p3);
		c4 = (-T * p0) + ((2 - T) * p1) + ((T - 2) * p2) + (T * p3);
		
		return (float) ((((((c4 * u) + c3) * u) + c2) * u) + c1);
	}
	
	/**
	 * Calculates a smooth curve between points using Catmull-Rom interpolation.<br>
	 * This method returns a {@code Vector3f} based on four control points.<br>
	 * It is useful for creating smooth paths in 3D space.
	 * @param u The interpolation factor, typically between {@code 0.0f} and {@code 1.0f}.
	 * @param T A tension or time parameter used to adjust the curve shape.
	 * @param p0 The first control point.
	 * @param p1 The second control point.
	 * @param p2 The third control point.
	 * @param p3 The fourth control point.
	 * @return The interpolated {@code Vector3f} position.
	 */
	public static Vector3f interpolateCatmullRom(float u, float T, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3)
	{
		final Vector3f res = new Vector3f();
		res.x = interpolateCatmullRom(u, T, p0.x, p1.x, p2.x, p3.x);
		res.y = interpolateCatmullRom(u, T, p0.y, p1.y, p2.y, p3.y);
		res.z = interpolateCatmullRom(u, T, p0.z, p1.z, p2.z, p3.z);
		return res;
	}
	
	/**
	 * Calculates the arccosine of a given value.<br>
	 * This method returns the angle in radians.<br>
	 * It uses {@code acos} for values between -1.0 and 1.0.
	 * @param fValue The input value to calculate.
	 * @return The arccosine of {@code fValue} as a {@code float}.
	 */
	public static float acos(float fValue)
	{
		if (-1.0f < fValue)
		{
			if (fValue < 1.0f)
			{
				return (float) Math.acos(fValue);
			}
			
			return 0.0f;
		}
		
		return PI;
	}
	
	/**
	 * Calculates the arcsine of a given value.<br>
	 * This method returns the angle in radians.<br>
	 * It uses {@code asin} for values between -1.0 and 1.0.
	 * @param fValue The input value to calculate the arcsine for.
	 * @return The result of the arcsine calculation as a {@code float}.
	 */
	public static float asin(float fValue)
	{
		if (-1.0f < fValue)
		{
			if (fValue < 1.0f)
			{
				return (float) Math.asin(fValue);
			}
			
			return HALF_PI;
		}
		
		return -HALF_PI;
	}
	
	/**
	 * Calculates the arctangent of a given value.<br>
	 * This method returns the result as a {@code float}.<br>
	 * It provides a fast approximation using {@code atan}.
	 * @param fValue The input value to calculate the arctangent for.
	 * @return The arctangent of the provided value.
	 */
	public static float atan(float fValue)
	{
		return (float) Math.atan(fValue);
	}
	
	/**
	 * Calculates the arc tangent of a given coordinate.<br>
	 * This method returns the angle in radians between the positive x-axis and the point {@code (fX, fY)}.<br>
	 * It is a fast float version of {@code double)}.
	 * @param fY The y-coordinate of the point.
	 * @param fX The x-coordinate of the point.
	 * @return The angle in radians.
	 */
	public static float atan2(float fY, float fX)
	{
		return (float) Math.atan2(fY, fX);
	}
	
	/**
	 * Returns the smallest double value that is greater than or equal to {@code fValue}.<br>
	 * This method rounds the input up to the nearest integer.<br>
	 * It uses {@code ceil} internally.
	 * @param fValue The float value to round up.
	 * @return The rounded float result.
	 */
	public static float ceil(float fValue)
	{
		return (float) Math.ceil(fValue);
	}
	
	/**
	 * This method normalizes an angle into a smaller range.<br>
	 * It reduces the input {@code radians} to be between {@code -PI/2} and {@code PI/2}.<br>
	 * Use this when you need a simplified direction for calculations.
	 * @param radians The angle in radians to be reduced.
	 * @return The reduced angle as a {@code float}.
	 */
	public static float reduceSinAngle(float radians)
	{
		radians %= TWO_PI; // put us in -2PI to +2PI space
		if (Math.abs(radians) > PI)
		{
			// put us in -PI to +PI space
			radians = radians - (TWO_PI);
		}
		
		if (Math.abs(radians) > HALF_PI)
		{// put us in -PI/2 to +PI/2 space
			radians = PI - radians;
		}
		
		return radians;
	}
	
	/**
	 * Calculates the sine of an angle.<br>
	 * This method uses a fast approximation for certain values.<br>
	 * It first calls {@code reduceSinAngle} to normalize the input.
	 * @param fValue The angle in radians to calculate the sine for.
	 * @return The calculated sine value as a {@code float}.
	 */
	public static float sin2(float fValue)
	{
		fValue = reduceSinAngle(fValue); // limits angle to between -PI/2 and +PI/2
		if (Math.abs(fValue) <= (Math.PI / 4))
		{
			return (float) Math.sin(fValue);
		}
		
		return (float) Math.cos((Math.PI / 2) - fValue);
	}
	
	/**
	 * Calculates the cosine of an angle using a fast approximation.<br>
	 * This method uses {@code sin2} for its calculation.
	 * @param fValue The input value in radians.
	 * @return The approximated cosine value as a {@code float}.
	 */
	public static float cos2(float fValue)
	{
		return sin2(fValue + HALF_PI);
	}
	
	/**
	 * Calculates the cosine of a given angle.<br>
	 * This method returns a {@code float} value.<br>
	 * It uses the standard {@code cos} function internally.
	 * @param v The angle in radians to calculate the cosine for.
	 * @return The cosine of the provided angle.
	 */
	public static float cos(float v)
	{
		return (float) Math.cos(v);
	}
	
	/**
	 * Calculates the sine of a given angle.<br>
	 * This method returns a {@code float} value.<br>
	 * It uses the standard {@code sin} function internally.
	 * @param v The angle in radians to calculate the sine for.
	 * @return The sine of the provided angle as a {@code float}.
	 */
	public static float sin(float v)
	{
		return (float) Math.sin(v);
	}
	
	/**
	 * Calculates the exponential value of a number.<br>
	 * This method returns the result as a {@code float}.<br>
	 * It uses the standard {@code exp} function internally.
	 * @param fValue The input value to calculate.
	 * @return The result of the exponential calculation.
	 */
	public static float exp(float fValue)
	{
		return (float) Math.exp(fValue);
	}
	
	/**
	 * Returns the absolute value of a {@code float}.<br>
	 * This method removes the negative sign if it exists.
	 * @param fValue The input value to process.
	 * @return The non-negative magnitude of {@code fValue}.
	 */
	public static float abs(float fValue)
	{
		if (fValue < 0)
		{
			return -fValue;
		}
		
		return fValue;
	}
	
	/**
	 * Returns the largest integer value less than or equal to the given number.<br>
	 * This method rounds the input down toward negative infinity.<br>
	 * It uses {@code floor} internally.
	 * @param fValue The float value to be rounded down.
	 * @return The floored result as a {@code float}.
	 */
	public static float floor(float fValue)
	{
		return (float) Math.floor(fValue);
	}
	
	/**
	 * Calculates the reciprocal of the square root of a value.<br>
	 * This method is useful for fast normalization in 3D graphics.<br>
	 * It uses {@code Math.sqrt} internally to perform the calculation.
	 * @param fValue The input value to process.
	 * @return The result of {@code 1.0f / sqrt(fValue)} as a {@code float}.
	 */
	public static float invSqrt(float fValue)
	{
		return (float) (1.0f / Math.sqrt(fValue));
	}
	
	/**
	 * Calculates an approximation of the reciprocal square root.<br>
	 * This method is faster than using {@code sqrt}.<br>
	 * It uses a bit-manipulation trick followed by one Newton iteration.
	 * @param x The value to calculate the reciprocal square root for.
	 * @return The approximate result of {@code 1.0 / sqrt(x)}.
	 */
	public static float fastInvSqrt(float x)
	{
		final float xhalf = 0.5f * x;
		int i = Float.floatToIntBits(x); // get bits for floating value
		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
		x = Float.intBitsToFloat(i); // convert bits back to float
		x = x * (1.5f - (xhalf * x * x)); // Newton step, repeating increases accuracy
		return x;
	}
	
	/**
	 * Calculates the natural logarithm of a value.<br>
	 * This method returns the {@code float} result of {@code log}.
	 * @param fValue The value to calculate the logarithm for.
	 * @return The natural logarithm of {@code fValue}.
	 */
	public static float log(float fValue)
	{
		return (float) Math.log(fValue);
	}
	
	/**
	 * Calculates the logarithm of a value with a specific base.<br>
	 * This method uses {@code log} to perform the calculation.
	 * @param value The number to calculate the logarithm for.
	 * @param base The base of the logarithm.
	 * @return The result of the logarithm as a {@code float}.
	 */
	public static float log(float value, float base)
	{
		return (float) (Math.log(value) / Math.log(base));
	}
	
	/**
	 * Calculates the value of a base raised to an exponent.<br>
	 * This method returns a {@code float} result.<br>
	 * It wraps the standard {@code double)} function.
	 * @param fBase The base value.
	 * @param fExponent The exponent value.
	 * @return The result of {@code fBase` raised to the power of `fExponent}.
	 */
	public static float pow(float fBase, float fExponent)
	{
		return (float) Math.pow(fBase, fExponent);
	}
	
	/**
	 * Calculates the square of a given number.<br>
	 * This method multiplies {@code fValue} by itself.
	 * @param fValue The float value to be squared.
	 * @return The result of squaring {@code fValue}.
	 */
	public static float sqr(float fValue)
	{
		return fValue * fValue;
	}
	
	/**
	 * Calculates the square root of a given number.<br>
	 * This method uses {@code sqrt} internally.
	 * @param fValue The value to calculate the square root for.
	 * @return The square root of {@code fValue} as a {@code float}.
	 */
	public static float sqrt(float fValue)
	{
		return (float) Math.sqrt(fValue);
	}
	
	/**
	 * Calculates the tangent of an angle.<br>
	 * This method returns a {@code float} value.<br>
	 * It uses the standard {@code tan} function.
	 * @param fValue The angle in radians to calculate.
	 * @return The tangent of the provided angle.
	 */
	public static float tan(float fValue)
	{
		return (float) Math.tan(fValue);
	}
	
	/**
	 * Determines the sign of an integer value.<br>
	 * Returns {@code 1} if the number is positive.<br>
	 * Returns {@code -1} if the number is negative.<br>
	 * Returns {@code 0} if the number is zero.
	 * @param iValue The integer to check.
	 * @return The sign of the provided integer.
	 */
	public static int sign(int iValue)
	{
		if (iValue > 0)
		{
			return 1;
		}
		
		if (iValue < 0)
		{
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * Returns the sign of a given number.<br>
	 * This method uses {@code signum} internally.
	 * @param fValue The float value to check.
	 * @return The sign of the input as a {@code float}.
	 */
	public static float sign(float fValue)
	{
		return Math.signum(fValue);
	}
	
	/**
	 * Determines the orientation of three points.<br>
	 * This method checks if the sequence {@code p0}, {@code p1}, and {@code p2} turns counter-clockwise.<br>
	 * It returns a value based on the cross product and distance comparisons.
	 * @param p0 The starting point of the path.
	 * @param p1 The middle point of the path.
	 * @param p2 The ending point of the path.
	 * @return {@code 1} if counter-clockwise, {@code -1} if clockwise, or {@code 0} if collinear.
	 */
	public static int counterClockwise(Vector2f p0, Vector2f p1, Vector2f p2)
	{
		float dx1, dx2, dy1, dy2;
		dx1 = p1.x - p0.x;
		dy1 = p1.y - p0.y;
		dx2 = p2.x - p0.x;
		dy2 = p2.y - p0.y;
		if ((dx1 * dy2) > (dy1 * dx2))
		{
			return 1;
		}
		
		if (((dx1 * dy2) < (dy1 * dx2)) || ((dx1 * dx2) < 0) || ((dy1 * dy2) < 0))
		{
			return -1;
		}
		
		if (((dx1 * dx1) + (dy1 * dy1)) < ((dx2 * dx2) + (dy2 * dy2)))
		{
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * Checks if a point is located inside a triangle.<br>
	 * This method uses the cross product to determine the relative position of the point.<br>
	 * It returns a value based on whether the point lies within the boundaries defined by the three vertices.
	 * @param t0 The first vertex of the triangle.
	 * @param t1 The second vertex of the triangle.
	 * @param t2 The third vertex of the triangle.
	 * @param p The point to check.
	 * @return An integer representing the position of the point relative to the triangle.
	 */
	public static int pointInsideTriangle(Vector2f t0, Vector2f t1, Vector2f t2, Vector2f p)
	{
		final int val1 = counterClockwise(t0, t1, p);
		if (val1 == 0)
		{
			return 1;
		}
		
		final int val2 = counterClockwise(t1, t2, p);
		if (val2 == 0)
		{
			return 1;
		}
		
		if (val2 != val1)
		{
			return 0;
		}
		
		final int val3 = counterClockwise(t2, t0, p);
		if (val3 == 0)
		{
			return 1;
		}
		
		if (val3 != val1)
		{
			return 0;
		}
		
		return val3;
	}
	
	/**
	 * Calculates the determinant of a 4x4 matrix.<br>
	 * This method takes 16 individual components as input.<br>
	 * The result is returned as a {@code float}.
	 * @param m00 The value at row 0, column 0.
	 * @param m01 The value at row 0, column 1.
	 * @param m02 The value at row 0, column 2.
	 * @param m03 The value at row 0, column 3.
	 * @param m10 The value at row 1, column 0.
	 * @param m11 The value at row 1, column 1.
	 * @param m12 The value at row 1, column 2.
	 * @param m13 The value at row 1, column 3.
	 * @param m20 The value at row 2, column 0.
	 * @param m21 The value at row 2, column 1.
	 * @param m22 The value at row 2, column 2.
	 * @param m23 The value at row 2, column
	 * @param m30
	 * @param m31
	 * @param m32
	 * @param m33
	 * @return
	 */
	public static float determinant(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33)
	{
		final double det01 = (m20 * m31) - (m21 * m30);
		final double det02 = (m20 * m32) - (m22 * m30);
		final double det03 = (m20 * m33) - (m23 * m30);
		final double det12 = (m21 * m32) - (m22 * m31);
		final double det13 = (m21 * m33) - (m23 * m31);
		final double det23 = (m22 * m33) - (m23 * m32);
		return (float) ((((m00 * (((m11 * det23) - (m12 * det13)) + (m13 * det12))) - (m01 * (((m10 * det23) - (m12 * det03)) + (m13 * det02)))) + (m02 * (((m10 * det13) - (m11 * det03)) + (m13 * det01)))) - (m03 * (((m10 * det12) - (m11 * det02)) + (m12 * det01))));
	}
	
	/**
	 * Generates a random floating-point number.<br>
	 * This method uses the internal {@code Random} instance to produce a value.
	 * @return A random {@code float} between 0.0 and 1.0.
	 */
	public static float nextRandomFloat()
	{
		return rand.nextFloat();
	}
	
	/**
	 * Generates a random integer between two values.<br>
	 * The result includes both the {@code min} and {@code max} boundaries.
	 * @param min The lowest possible value.
	 * @param max The highest possible value.
	 * @return A random integer between {@code min} and {@code max}.
	 */
	public static int nextRandomInt(int min, int max)
	{
		return (int) (nextRandomFloat() * ((max - min) + 1)) + min;
	}
	
	/**
	 * Generates a random integer.<br>
	 * This method uses the internal {@code Random} instance.
	 * @return A random {@code int} value.
	 */
	public static int nextRandomInt()
	{
		return rand.nextInt();
	}
	
	/**
	 * Converts coordinates from spherical to Cartesian format.<br>
	 * This method updates the {@code store} object with the new values.<br>
	 * It uses {@link FastMath} for trigonometric calculations.
	 * @param sphereCoords The input coordinates in spherical format.
	 * @param store The destination {@code Vector3f} to store the result.
	 * @return The updated {@code store} object.
	 */
	public static Vector3f sphericalToCartesian(Vector3f sphereCoords, Vector3f store)
	{
		store.y = sphereCoords.x * FastMath.sin(sphereCoords.z);
		final float a = sphereCoords.x * FastMath.cos(sphereCoords.z);
		store.x = a * FastMath.cos(sphereCoords.y);
		store.z = a * FastMath.sin(sphereCoords.y);
		
		return store;
	}
	
	/**
	 * Converts a position from Cartesian coordinates to spherical coordinates.<br>
	 * This method updates the provided {@code store} object with the new values.<br>
	 * It uses {@link FastMath} for all trigonometric calculations.
	 * @param cartCoords The input coordinates in the Cartesian system.
	 * @param store The destination vector where spherical results are stored.
	 * @return The updated {@code store} vector.
	 */
	public static Vector3f cartesianToSpherical(Vector3f cartCoords, Vector3f store)
	{
		if (cartCoords.x == 0)
		{
			cartCoords.x = FastMath.FLT_EPSILON;
		}
		
		store.x = FastMath.sqrt((cartCoords.x * cartCoords.x) + (cartCoords.y * cartCoords.y) + (cartCoords.z * cartCoords.z));
		store.y = FastMath.atan(cartCoords.z / cartCoords.x);
		if (cartCoords.x < 0)
		{
			store.y += FastMath.PI;
		}
		
		store.z = FastMath.asin(cartCoords.y / store.x);
		return store;
	}
	
	/**
	 * Converts spherical coordinates to Cartesian coordinates.<br>
	 * The result is stored directly in the provided {@code store} object.<br>
	 * This method uses {@link FastMath} for trigonometric calculations.
	 * @param sphereCoords The input coordinates in spherical format.
	 * @param store The {@code Vector3f} object where the result will be saved.
	 * @return The modified {@code store} object containing the new coordinates.
	 */
	public static Vector3f sphericalToCartesianZ(Vector3f sphereCoords, Vector3f store)
	{
		store.z = sphereCoords.x * FastMath.sin(sphereCoords.z);
		final float a = sphereCoords.x * FastMath.cos(sphereCoords.z);
		store.x = a * FastMath.cos(sphereCoords.y);
		store.y = a * FastMath.sin(sphereCoords.y);
		
		return store;
	}
	
	/**
	 * Converts coordinates from a Cartesian system to a spherical coordinate system.<br>
	 * This method updates the {@code store} object with the new values.
	 * @param cartCoords The input coordinates in Cartesian format.
	 * @param store The output vector where spherical results are stored.
	 * @return The updated {@code store} vector.
	 */
	public static Vector3f cartesianZToSpherical(Vector3f cartCoords, Vector3f store)
	{
		if (cartCoords.x == 0)
		{
			cartCoords.x = FastMath.FLT_EPSILON;
		}
		
		store.x = FastMath.sqrt((cartCoords.x * cartCoords.x) + (cartCoords.y * cartCoords.y) + (cartCoords.z * cartCoords.z));
		store.z = FastMath.atan(cartCoords.z / cartCoords.x);
		if (cartCoords.x < 0)
		{
			store.z += FastMath.PI;
		}
		
		store.y = FastMath.asin(cartCoords.y / store.x);
		return store;
	}
	
	/**
	 * This method scales a value into a specific range.<br>
	 * It ensures the result stays between {@code min} and {@code max}.<br>
	 * If the input is infinite or not a number, it returns {@code 0f}.
	 * @param val The original value to normalize.
	 * @param min The minimum boundary of the range.
	 * @param max The maximum boundary of the range.
	 * @return The normalized float value.
	 */
	public static float normalize(float val, float min, float max)
	{
		if (Float.isInfinite(val) || Float.isNaN(val))
		{
			return 0f;
		}
		
		final float range = max - min;
		while (val > max)
		{
			val -= range;
		}
		while (val < min)
		{
			val += range;
		}
		
		return val;
	}
	
	/**
	 * Returns the value of {@code x} with the sign of {@code y}.<br>
	 * This method copies the sign bit from one number to another.<br>
	 * It handles positive and negative zero correctly.
	 * @param x The magnitude to be returned.
	 * @param y The number whose sign is copied.
	 * @return The result of copying the sign of {@code y} onto {@code x}.
	 */
	public static float copysign(float x, float y)
	{
		if ((y >= 0) && (x <= -0))
		{
			return -x;
		}
		else if ((y < 0) && (x >= 0))
		{
			return -x;
		}
		else
		{
			return x;
		}
	}
	
	/**
	 * Restricts a value to be within a specific range.<br>
	 * If the {@code input} is smaller than {@code min}, it returns {@code min}.<br>
	 * If the {@code input} is larger than {@code max}, it returns {@code max}.<br>
	 * Otherwise, it returns the original {@code input}.
	 * @param input The value to be clamped.
	 * @param min The minimum allowed value.
	 * @param max The maximum allowed value.
	 * @return The clamped {@code float} value.
	 */
	public static float clamp(float input, float min, float max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Limits the input value to a range between {@code 0.0f} and {@code 1.0f}.<br>
	 * This method ensures the result never goes below zero or above one.<br>
	 * It uses the {@code float, float)} method internally.
	 * @param input The value to be saturated.
	 * @return The clamped value as a {@code float}.
	 */
	public static float saturate(float input)
	{
		return clamp(input, 0f, 1f);
	}
	
	/**
	 * Converts a {@code half_t} value to a standard {@code float}.<br>
	 * This method handles special cases like zero and infinity.<br>
	 * It uses bitwise operations for the conversion.
	 * @param half The integer representation of the half-precision float.
	 * @return The converted {@code float} value.
	 */
	public static float convertHalfToFloat(int half)
	{
		switch (half)
		{
			case 0x0000:
				return 0f;
			case 0x8000:
				return -0f;
			case 0x7c00:
				return Float.POSITIVE_INFINITY;
			case 0xfc00:
				return Float.NEGATIVE_INFINITY;
			// TODO: Support for NaN?
			default:
				return Float.intBitsToFloat(((half & 0x8000) << 16) | (((half & 0x7c00) + 0x1C000) << 13) | ((half & 0x03FF) << 13));
		}
	}
	
	/**
	 * Converts a {@code float} value into its {@code short} half-precision representation.<br>
	 * This method handles special cases like infinity and zero.<br>
	 * It throws an {@code UnsupportedOperationException} if the input is {@code NaN}.
	 * @param flt The {@code float} value to convert.
	 * @return The resulting {@code short} half-precision value.
	 */
	public static short convertFloatToHalf(float flt)
	{
		if (Float.isNaN(flt))
		{
			throw new UnsupportedOperationException("NaN to half conversion not supported!");
		}
		else if (flt == Float.POSITIVE_INFINITY)
		{
			return (short) 0x7c00;
		}
		else if (flt == Float.NEGATIVE_INFINITY)
		{
			return (short) 0xfc00;
		}
		else if (flt == 0f)
		{
			return (short) 0x0000;
		}
		else if (flt == -0f)
		{
			return (short) 0x8000;
		}
		else if (flt > 65504f)
		{
			// max value supported by half float
			return 0x7bff;
		}
		else if (flt < -65504f)
		{
			return (short) (0x7bff | 0x8000);
		}
		else if ((flt > 0f) && (flt < 5.96046E-8f))
		{
			return 0x0001;
		}
		else if ((flt < 0f) && (flt > -5.96046E-8f))
		{
			return (short) 0x8001;
		}
		
		final int f = Float.floatToIntBits(flt);
		return (short) (((f >> 16) & 0x8000) | ((((f & 0x7f800000) - 0x38000000) >> 13) & 0x7c00) | ((f >> 13) & 0x03ff));
	}
}
