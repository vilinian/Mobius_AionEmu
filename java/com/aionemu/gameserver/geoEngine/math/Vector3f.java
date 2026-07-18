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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * -- Added *Local methods to cut down on object creation - JS
 */

/**
 * Represents a three-dimensional vector using three {@code float} values.<br>
 * This class can represent spatial data such as vertices or normals.<br>
 * It includes utility methods to perform common mathematical calculations.
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Vector3f implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Vector3f.class);
	public final static Vector3f ZERO = new Vector3f(0, 0, 0);
	public final static Vector3f NAN = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
	public final static Vector3f UNIT_X = new Vector3f(1, 0, 0);
	public final static Vector3f UNIT_Y = new Vector3f(0, 1, 0);
	public final static Vector3f UNIT_Z = new Vector3f(0, 0, 1);
	public final static Vector3f UNIT_XYZ = new Vector3f(1, 1, 1);
	public final static Vector3f POSITIVE_INFINITY = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	public final static Vector3f NEGATIVE_INFINITY = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
	/**
	 * the x value of the vector.
	 */
	public float x;
	/**
	 * the y value of the vector.
	 */
	public float y;
	/**
	 * the z value of the vector.
	 */
	public float z;
	
	/**
	 * Creates a new {@link Vector3f} instance.<br>
	 * All coordinates are initialized to {@code 0.0f}.
	 */
	public Vector3f()
	{
		x = y = z = 0f;
	}
	
	/**
	 * Creates a new {@link Vector3f} instance with the specified coordinates.<br>
	 * This constructor initializes the {@code x}, {@code y}, and {@code z} fields.
	 * @param x The value for the x coordinate.
	 * @param y The value for the y coordinate.
	 * @param z The value for the z coordinate.
	 */
	public Vector3f(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a new {@link Vector3f} instance as a copy of an existing vector.<br>
	 * This method copies all values from the provided object to the new instance.
	 * @param copy The {@code Vector3f} object to be copied.
	 */
	public Vector3f(Vector3f copy)
	{
		this.set(copy);
	}
	
	/**
	 * Updates the current vector values with new coordinates.<br>
	 * This method modifies the existing {@code Vector3f} object.
	 * @param x The new value for the x coordinate.
	 * @param y The new value for the y coordinate.
	 * @param z The new value for the z coordinate.
	 * @return The current instance of {@code Vector3f}.
	 */
	public Vector3f set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**
	 * Copies the values from another {@code Vector3f} into this instance.<br>
	 * This method updates the current object's coordinates to match the provided vector.
	 * @param vect The source {@code Vector3f} to copy from.
	 * @return The current {@code Vector3f} instance for method chaining.
	 */
	public Vector3f set(Vector3f vect)
	{
		x = vect.x;
		y = vect.y;
		z = vect.z;
		return this;
	}
	
	/**
	 * Adds this vector to another vector.<br>
	 * This method returns a new {@code Vector3f} instance.<br>
	 * It does not modify the original values of this object.
	 * @param vec The vector to add to the current one.
	 * @return A new {@code Vector3f} representing the sum, or {@code null} if the input is {@code null}.
	 */
	public Vector3f add(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		return new Vector3f(x + vec.x, y + vec.y, z + vec.z);
	}
	
	/**
	 * Adds the components of two vectors together.<br>
	 * The sum is stored in a provided {@code Vector3f} object to avoid new allocations.
	 * @param vec The vector to add to this one.
	 * @param result The vector where the sum will be stored.
	 * @return The same {@code result} object after modification.
	 */
	public Vector3f add(Vector3f vec, Vector3f result)
	{
		result.x = x + vec.x;
		result.y = y + vec.y;
		result.z = z + vec.z;
		return result;
	}
	
	/**
	 * Adds the components of another vector to this vector.<br>
	 * This method modifies the current object instead of creating a new one.<br>
	 * It returns {@code this} for method chaining.
	 * @param vec The {@link Vector3f} to add to the current instance.
	 * @return The current {@link Vector3f} instance, or {@code null} if the input is {@code null}.
	 */
	public Vector3f addLocal(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x += vec.x;
		y += vec.y;
		z += vec.z;
		return this;
	}
	
	/**
	 * Adds three individual values to the current vector components.<br>
	 * This method returns a new {@code Vector3f} instance.
	 * @param addX The value to add to the x component.
	 * @param addY The value to add to the y component.
	 * @param addZ The value to add to the z component.
	 * @return A new {@code Vector3f} containing the summed results.
	 */
	public Vector3f add(float addX, float addY, float addZ)
	{
		return new Vector3f(x + addX, y + addY, z + addZ);
	}
	
	/**
	 * Adds the specified coordinates to the current vector values.<br>
	 * This method modifies the existing object instead of creating a new one.
	 * @param addX The amount to add to the {@code x} component.
	 * @param addY The amount to add to the {@code y} component.
	 * @param addZ The amount to add to the {@code z} component.
	 * @return This same {@code Vector3f} instance.
	 */
	public Vector3f addLocal(float addX, float addY, float addZ)
	{
		x += addX;
		y += addY;
		z += addZ;
		return this;
	}
	
	/**
	 * Scales the current vector by a factor and adds another vector to it.<br>
	 * This method modifies the values of the current {@code Vector3f} instance.<br>
	 * It performs the calculation as {@code (current * scalar) + add}.
	 * @param scalar The value used to multiply the current coordinates.
	 * @param add The {@link Vector3f} to be added after scaling.
	 * @return The modified {@code Vector3f} instance.
	 */
	public Vector3f scaleAdd(float scalar, Vector3f add)
	{
		x = (x * scalar) + add.x;
		y = (y * scalar) + add.y;
		z = (z * scalar) + add.z;
		return this;
	}
	
	/**
	 * This method performs a scaled addition on the current vector.<br>
	 * It multiplies {@code mult} by {@code scalar} and adds the result to {@code add}.<br>
	 * The final values are stored in this instance.
	 * @param scalar The value used to multiply the {@code mult} vector.
	 * @param mult The vector that will be scaled.
	 * @param add The vector that will be added to the scaled result.
	 * @return This {@link Vector3f} instance for method chaining.
	 */
	public Vector3f scaleAdd(float scalar, Vector3f mult, Vector3f add)
	{
		x = (mult.x * scalar) + add.x;
		y = (mult.y * scalar) + add.y;
		z = (mult.z * scalar) + add.z;
		return this;
	}
	
	/**
	 * Calculates the dot product between this vector and another {@code Vector3f}.<br>
	 * This operation returns a scalar value representing the magnitude of projection.<br>
	 * If the provided vector is {@code null}, the method returns {@code 0}.
	 * @param vec The other {@code Vector3f} to multiply with.
	 * @return The resulting dot product as a {@code float}.
	 */
	public float dot(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, 0 returned.");
			return 0;
		}
		
		return (x * vec.x) + (y * vec.y) + (z * vec.z);
	}
	
	/**
	 * Calculates the cross product of this vector and another vector.<br>
	 * This operation returns a new {@link Vector3f} perpendicular to both inputs.
	 * @param v The other {@link Vector3f} to use in the calculation.
	 * @return A new {@link Vector3f} representing the cross product.
	 */
	public Vector3f cross(Vector3f v)
	{
		return cross(v, null);
	}
	
	/**
	 * Calculates the cross product of this vector and another vector.<br>
	 * The result is stored in a provided {@code Vector3f} object to avoid new allocations.
	 * @param v The vector to multiply with.
	 * @param result The vector where the calculation result will be stored.
	 * @return This same {@code Vector3f} instance for method chaining.
	 */
	public Vector3f cross(Vector3f v, Vector3f result)
	{
		return cross(v.x, v.y, v.z, result);
	}
	
	/**
	 * Calculates the cross product between this vector and a new set of coordinates.<br>
	 * The result is stored in the provided {@code Vector3f} object to avoid extra allocations.
	 * @param otherX The x component of the second vector.
	 * @param otherY The y component of the second vector.
	 * @param otherZ The z component of the second vector.
	 * @param result The {@code Vector3f} object where the result will be stored. If {@code null}, a new instance is created.
	 * @return The resulting {@code Vector3f} object.
	 */
	public Vector3f cross(float otherX, float otherY, float otherZ, Vector3f result)
	{
		if (result == null)
		{
			result = new Vector3f();
		}
		
		final float resX = ((y * otherZ) - (z * otherY));
		final float resY = ((z * otherX) - (x * otherZ));
		final float resZ = ((x * otherY) - (y * otherX));
		result.set(resX, resY, resZ);
		return result;
	}
	
	/**
	 * Calculates the cross product of this vector and another vector.<br>
	 * This method modifies the current {@code Vector3f} instance to store the result.<br>
	 * It is used to avoid creating a new object during calculations.
	 * @param v The other {@link Vector3f} to use in the calculation.
	 * @return The current {@code Vector3f} instance containing the cross product.
	 */
	public Vector3f crossLocal(Vector3f v)
	{
		return crossLocal(v.x, v.y, v.z);
	}
	
	/**
	 * Calculates the cross product of this vector and a new set of coordinates.<br>
	 * This method modifies the current {@code Vector3f} instance directly to save memory.<br>
	 * It updates the internal values based on the provided components.
	 * @param otherX The x component of the second vector.
	 * @param otherY The y component of the second vector.
	 * @param otherZ The z component of the second vector.
	 * @return The current {@code Vector3f} instance after modification.
	 */
	public Vector3f crossLocal(float otherX, float otherY, float otherZ)
	{
		final float tempx = (y * otherZ) - (z * otherY);
		final float tempy = (z * otherX) - (x * otherZ);
		z = (x * otherY) - (y * otherX);
		x = tempx;
		y = tempy;
		return this;
	}
	
	/**
	 * Projects this vector onto another vector.<br>
	 * This method calculates the component of this vector that lies along the direction of {@code other}.
	 * @param other The target vector to project onto.
	 * @return A new {@link Vector3f} representing the projection.
	 */
	public Vector3f project(Vector3f other)
	{
		final float n = dot(other); // A . B
		final float d = other.lengthSquared(); // |B|^2
		return new Vector3f(other).normalizeLocal().multLocal(n / d);
	}
	
	/**
	 * Calculates the magnitude of this {@code Vector2f}.<br>
	 * It returns the straight-line distance from the origin to the point.<br>
	 * This method uses {@code lengthSquared} internally.
	 * @return The length of the vector as a {@code float}.
	 */
	public float length()
	{
		return FastMath.sqrt(lengthSquared());
	}
	
	/**
	 * Calculates the squared length of this vector.<br>
	 * This is faster than {@code length} because it avoids a square root operation.<br>
	 * It is useful for comparing distances between vectors.
	 * @return The squared magnitude of the vector as a {@code float}.
	 */
	public float lengthSquared()
	{
		return (x * x) + (y * y) + (z * z);
	}
	
	/**
	 * Calculates the squared distance between this vector and another vector.<br>
	 * This method is faster than {@code distance} because it skips the square root operation.
	 * @param v The target {@code Vector3f} to measure against.
	 * @return The squared distance as a {@code float}.
	 */
	public float distanceSquared(Vector3f v)
	{
		final double dx = x - v.x;
		final double dy = y - v.y;
		final double dz = z - v.z;
		return (float) ((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	/**
	 * Calculates the straight-line distance between this vector and another.<br>
	 * This method uses the {@code distanceSquared} calculation followed by a square root.
	 * @param v The target {@link Vector3f} to measure the distance to.
	 * @return The distance as a {@code float}.
	 */
	public float distance(Vector3f v)
	{
		return FastMath.sqrt(distanceSquared(v));
	}
	
	/**
	 * Multiplies each component of this vector by a given value.<br>
	 * This method returns a new {@code Vector3f} instance.
	 * @param scalar The number to multiply the vector by.
	 * @return A new {@code Vector3f} representing the scaled result.
	 */
	public Vector3f mult(float scalar)
	{
		return new Vector3f(x * scalar, y * scalar, z * scalar);
	}
	
	/**
	 * Multiplies this vector by a {@code float} value.<br>
	 * The result is stored in the provided {@code Vector3f} object.<br>
	 * If the provided object is {@code null}, a new instance is created.
	 * @param scalar The number to multiply by.
	 * @param product The vector where the result will be stored.
	 * @return The resulting {@code Vector3f} object.
	 */
	public Vector3f mult(float scalar, Vector3f product)
	{
		if (null == product)
		{
			product = new Vector3f();
		}
		
		product.x = x * scalar;
		product.y = y * scalar;
		product.z = z * scalar;
		return product;
	}
	
	/**
	 * Multiplies the components of this vector by a given scalar.<br>
	 * This operation modifies the current {@code Vector3f} instance.<br>
	 * It is used to perform scaling without creating a new object.
	 * @param scalar The value to multiply each component by.
	 * @return The current {@code Vector3f} instance after modification.
	 */
	public Vector3f multLocal(float scalar)
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	
	/**
	 * Multiplies the current vector by another {@code Vector3f}.<br>
	 * This method modifies the values of the current instance in place.
	 * @param vec The {@code Vector3f} to multiply with.
	 * @return The modified {@code Vector3f} or {@code null} if the input is {@code null}.
	 */
	public Vector3f multLocal(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x *= vec.x;
		y *= vec.y;
		z *= vec.z;
		return this;
	}
	
	/**
	 * Multiplies the current vector components by the provided values.<br>
	 * This method modifies the internal state of the current {@code Vector3f} object.<br>
	 * It is used to perform local multiplication without creating a new object.
	 * @param x The multiplier for the {@code x} component.
	 * @param y The multiplier for the {@code y} component.
	 * @param z The multiplier for the {@code z} component.
	 * @return The current {@code Vector3f} instance after modification.
	 */
	public Vector3f multLocal(float x, float y, float z)
	{
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	/**
	 * Multiplies this vector by another {@code Vector3f}.<br>
	 * This method performs a component-wise multiplication.<br>
	 * It returns the resulting vector as a new object.
	 * @param vec The {@code Vector3f} to multiply.
	 * @return A new {@code Vector3f} representing the result of the multiplication, or {@code null} if the input is {@code null}.
	 */
	public Vector3f mult(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		return mult(vec, null);
	}
	
	/**
	 * Multiplies this vector by another {@code Vector3f} component-wise.<br>
	 * The result is stored in the provided {@code store} object.<br>
	 * If {@code store} is {@code null}, a new {@code Vector3f} instance is created.
	 * @param vec The input vector to multiply with.
	 * @param store The destination vector where the result will be saved.
	 * @return The resulting {@code Vector3f} after multiplication.
	 */
	public Vector3f mult(Vector3f vec, Vector3f store)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		if (store == null)
		{
			store = new Vector3f();
		}
		
		return store.set(x * vec.x, y * vec.y, z * vec.z);
	}
	
	/**
	 * Divides the components of this vector by a given value.<br>
	 * This method returns a new {@code Vector3f} instance.<br>
	 * It calculates the result by multiplying each component by the reciprocal of the input.
	 * @param scalar The value to divide the vector by.
	 * @return A new {@code Vector3f} representing the result of the division.
	 */
	public Vector3f divide(float scalar)
	{
		scalar = 1f / scalar;
		return new Vector3f(x * scalar, y * scalar, z * scalar);
	}
	
	/**
	 * Divides the components of this vector by a given scalar.<br>
	 * This method modifies the current {@code Vector3f} instance.<br>
	 * It is used to perform division without creating a new object.
	 * @param scalar The value to divide each component by.
	 * @return The current {@code Vector3f} instance.
	 */
	public Vector3f divideLocal(float scalar)
	{
		scalar = 1f / scalar;
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	
	/**
	 * Divides each component of this vector by the corresponding component of another vector.<br>
	 * This method returns a new {@code Vector3f} instance.
	 * @param scalar The {@code Vector3f} used to divide the current coordinates.
	 * @return A new {@code Vector3f} containing the result of the division.
	 */
	public Vector3f divide(Vector3f scalar)
	{
		return new Vector3f(x / scalar.x, y / scalar.y, z / scalar.z);
	}
	
	/**
	 * Divides the components of this vector by the corresponding components of another vector.<br>
	 * This operation modifies the current {@code Vector3f} instance directly.
	 * @param scalar The {@code Vector3f} used to divide the current values.
	 * @return The current {@code Vector3f} instance after division.
	 */
	public Vector3f divideLocal(Vector3f scalar)
	{
		x /= scalar.x;
		y /= scalar.y;
		z /= scalar.z;
		return this;
	}
	
	/**
	 * Creates a new {@code Vector3f} with inverted coordinates.<br>
	 * This method multiplies each component by -1.
	 * @return A new {@code Vector3f} representing the negated vector.
	 */
	public Vector3f negate()
	{
		return new Vector3f(-x, -y, -z);
	}
	
	/**
	 * Negates the current vector values.<br>
	 * This method modifies the internal coordinates of {@code x}, {@code y}, and {@code z}.<br>
	 * It performs the operation in-place to avoid creating a new object.
	 * @return The current {@code Vector3f} instance after negation.
	 */
	public Vector3f negateLocal()
	{
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	/**
	 * Subtracts one {@code Vector3f} from this vector.<br>
	 * This method returns a new {@code Vector3f} instance.<br>
	 * It does not modify the original values of this object.
	 * @param vec The vector to subtract from this one.
	 * @return A new {@code Vector3f} representing the result of the subtraction.
	 */
	public Vector3f subtract(Vector3f vec)
	{
		return new Vector3f(x - vec.x, y - vec.y, z - vec.z);
	}
	
	/**
	 * Subtracts the components of another vector from this one.<br>
	 * This method modifies the current {@code Vector3f} instance directly.<br>
	 * It returns the modified object to allow for method chaining.
	 * @param vec The {@code Vector3f} to subtract from this instance.
	 * @return The current {@code Vector3f} instance, or {@code null} if the input is {@code null}.
	 */
	public Vector3f subtractLocal(Vector3f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		return this;
	}
	
	/**
	 * Subtracts the components of one vector from this vector.<br>
	 * The calculation is stored in a provided result vector.<br>
	 * If the {@code result} parameter is {@code null}, a new {@link Vector3f} is created.
	 * @param vec The vector to subtract from this instance.
	 * @param result The vector where the result will be stored.
	 * @return The resulting {@link Vector3f}.
	 */
	public Vector3f subtract(Vector3f vec, Vector3f result)
	{
		if (result == null)
		{
			result = new Vector3f();
		}
		
		result.x = x - vec.x;
		result.y = y - vec.y;
		result.z = z - vec.z;
		return result;
	}
	
	/**
	 * Subtracts three values from the current vector components.<br>
	 * This method returns a new {@code Vector3f} instance.
	 * @param subtractX The value to subtract from the x component.
	 * @param subtractY The value to subtract from the y component.
	 * @param subtractZ The value to subtract from the z component.
	 * @return A new {@code Vector3f} representing the result of the subtraction.
	 */
	public Vector3f subtract(float subtractX, float subtractY, float subtractZ)
	{
		return new Vector3f(x - subtractX, y - subtractY, z - subtractZ);
	}
	
	/**
	 * Subtracts the given values from the current vector coordinates.<br>
	 * This method modifies the internal state of the current {@code Vector3f} object.<br>
	 * It returns the same instance to allow for method chaining.
	 * @param subtractX The value to subtract from the x coordinate.
	 * @param subtractY The value to subtract from the y coordinate.
	 * @param subtractZ The value to subtract from the z coordinate.
	 * @return This {@code Vector3f} instance.
	 */
	public Vector3f subtractLocal(float subtractX, float subtractY, float subtractZ)
	{
		x -= subtractX;
		y -= subtractY;
		z -= subtractZ;
		return this;
	}
	
	/**
	 * Scales the vector so that its magnitude becomes 1.<br>
	 * This method returns a new {@code Vector3f} instance.<br>
	 * If the vector is already unit length or zero, it returns a copy of the original.
	 * @return A new normalized {@code Vector3f}.
	 */
	public Vector3f normalize()
	{
		// float length = length();
		// if (length != 0) {
		// return divide(length);
		// }
		//
		// return divide(1);
		float length = (x * x) + (y * y) + (z * z);
		if ((length != 1f) && (length != 0f))
		{
			length = 1.0f / FastMath.sqrt(length);
			return new Vector3f(x * length, y * length, z * length);
		}
		
		return clone();
	}
	
	/**
	 * Normalizes the vector components to a unit length of 1.<br>
	 * This method modifies the current {@code Vector3f} instance directly.<br>
	 * It skips normalization if the length is already 0 or 1.
	 * @return The current {@code Vector3f} instance.
	 */
	public Vector3f normalizeLocal()
	{
		// This implementation is more optimized than the old JME normalize because this method is commonly used.
		float length = (x * x) + (y * y) + (z * z);
		if ((length != 1f) && (length != 0f))
		{
			length = 1.0f / FastMath.sqrt(length);
			x *= length;
			y *= length;
			z *= length;
		}
		
		return this;
	}
	
	/**
	 * Updates the current vector components to their maximum values relative to another vector.<br>
	 * This method modifies the internal state of this {@code Vector3f} instance.<br>
	 * It compares each coordinate with the corresponding coordinate in the provided parameter.
	 * @param other The {@code Vector3f} used to determine the maximum values.
	 */
	public void maxLocal(Vector3f other)
	{
		x = other.x > x ? other.x : x;
		y = other.y > y ? other.y : y;
		z = other.z > z ? other.z : z;
	}
	
	/**
	 * Updates the current vector components to their minimum values relative to another vector.<br>
	 * This method modifies the internal state of this {@code Vector3f} instance.<br>
	 * It compares each coordinate with the corresponding coordinate in the provided parameter.
	 * @param other The {@code Vector3f} used to determine the minimum values.
	 */
	public void minLocal(Vector3f other)
	{
		x = other.x < x ? other.x : x;
		y = other.y < y ? other.y : y;
		z = other.z < z ? other.z : z;
	}
	
	/**
	 * Resets all components of the vector to {@code 0}.<br>
	 * This method modifies the current instance.
	 * @return The current {@link Vector3f} instance.
	 */
	public Vector3f zero()
	{
		x = y = z = 0;
		return this;
	}
	
	/**
	 * Calculates the angle between this vector and another vector.<br>
	 * The result is returned in radians.
	 * @param otherVector The {@code Vector3f} to compare against.
	 * @return The angle between the two vectors as a {@code float}.
	 */
	public float angleBetween(Vector3f otherVector)
	{
		final float dotProduct = dot(otherVector);
		final float angle = FastMath.acos(dotProduct);
		return angle;
	}
	
	/**
	 * Linearly interpolates the current vector towards a target vector.<br>
	 * This method updates the values of the current {@code Vector3f} instance.
	 * @param finalVec The target {@code Vector3f} to move toward.
	 * @param changeAmnt The interpolation factor between 0 and 1.
	 * @return The updated {@code Vector3f} instance.
	 */
	public Vector3f interpolate(Vector3f finalVec, float changeAmnt)
	{
		x = ((1 - changeAmnt) * x) + (changeAmnt * finalVec.x);
		y = ((1 - changeAmnt) * y) + (changeAmnt * finalVec.y);
		z = ((1 - changeAmnt) * z) + (changeAmnt * finalVec.z);
		return this;
	}
	
	/**
	 * Calculates a point between two vectors based on a percentage.<br>
	 * This method updates the current {@code Vector3f} instance.<br>
	 * The result is moved toward {@code finalVec} by the amount specified in {@code changeAmnt}.
	 * @param beginVec The starting position vector.
	 * @param finalVec The target destination vector.
	 * @param changeAmnt The interpolation factor, typically between {@code 0.0f} and {@code 1.0f}.
	 * @return The current {@code Vector3f} instance after the calculation.
	 */
	public Vector3f interpolate(Vector3f beginVec, Vector3f finalVec, float changeAmnt)
	{
		x = ((1 - changeAmnt) * beginVec.x) + (changeAmnt * finalVec.x);
		y = ((1 - changeAmnt) * beginVec.y) + (changeAmnt * finalVec.y);
		z = ((1 - changeAmnt) * beginVec.z) + (changeAmnt * finalVec.z);
		return this;
	}
	
	/**
	 * Checks if the provided {@code Vector3f} is valid.<br>
	 * It returns {@code false} if the vector is {@code null}.<br>
	 * It also returns {@code false} if any component is {@code NaN} or infinite.
	 * @param vector The {@code Vector3f} to validate.
	 * @return {@code true} if the vector is valid, otherwise {@code false}.
	 */
	public static boolean isValidVector(Vector3f vector)
	{
		if ((vector == null) || Float.isNaN(vector.x) || Float.isNaN(vector.y) || Float.isNaN(vector.z))
		{
			return false;
		}
		
		if (Float.isInfinite(vector.x) || Float.isInfinite(vector.y) || Float.isInfinite(vector.z))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates an orthonormal basis using three vectors.<br>
	 * This method normalizes {@code w} and then generates a complementary basis.
	 * @param u The first vector of the basis.
	 * @param v The second vector of the basis.
	 * @param w The third vector of the basis.
	 */
	public static void generateOrthonormalBasis(Vector3f u, Vector3f v, Vector3f w)
	{
		w.normalizeLocal();
		generateComplementBasis(u, v, w);
	}
	
	/**
	 * This method generates a complementary basis for a given vector.<br>
	 * It modifies the {@code u} and {@code v} vectors based on the input {@code w}.<br>
	 * The calculation ensures that the resulting basis is orthonormal.
	 * @param u The first basis vector to be updated.
	 * @param v The second basis vector to be updated.
	 * @param w The reference vector used to define the orientation of the new basis.
	 */
	public static void generateComplementBasis(Vector3f u, Vector3f v, Vector3f w)
	{
		float fInvLength;
		
		if (FastMath.abs(w.x) >= FastMath.abs(w.y))
		{
			// w.x or w.z is the largest magnitude component, swap them
			fInvLength = FastMath.invSqrt((w.x * w.x) + (w.z * w.z));
			u.x = -w.z * fInvLength;
			u.y = 0.0f;
			u.z = +w.x * fInvLength;
			v.x = w.y * u.z;
			v.y = (w.z * u.x) - (w.x * u.z);
			v.z = -w.y * u.x;
		}
		else
		{
			// w.y or w.z is the largest magnitude component, swap them
			fInvLength = FastMath.invSqrt((w.y * w.y) + (w.z * w.z));
			u.x = 0.0f;
			u.y = +w.z * fInvLength;
			u.z = -w.y * fInvLength;
			v.x = (w.y * u.z) - (w.z * u.y);
			v.y = -w.x * u.z;
			v.z = w.x * u.y;
		}
	}
	
	/**
	 * Creates a new instance of {@code Vector3f}.<br>
	 * This method performs a shallow copy of the current object.<br>
	 * It returns a new object with the same values as this one.
	 * @return A new {@code Vector3f} instance.
	 */
	@Override
	public Vector3f clone()
	{
		try
		{
			return (Vector3f) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError(); // can not happen
		}
	}
	
	/**
	 * Converts the current vector values into a {@code float[]} array.<br>
	 * If the input is {@code null}, a new array of size 3 is created.<br>
	 * The elements are populated with the {@code x}, {@code y}, and {@code z} components.
	 * @param floats The array to populate with the vector components.
	 * @return A {@code float[]} containing the vector's coordinates.
	 */
	public float[] toArray(float[] floats)
	{
		if (floats == null)
		{
			floats = new float[3];
		}
		
		floats[0] = x;
		floats[1] = y;
		floats[2] = z;
		return floats;
	}
	
	/**
	 * Compares this {@link Vector3f} object with another object for equality.<br>
	 * It checks if both objects have the same x, y, and z coordinates.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Vector3f))
		{
			return false;
		}
		
		if (this == o)
		{
			return true;
		}
		
		final Vector3f comp = (Vector3f) o;
		if ((Float.compare(x, comp.x) != 0) || (Float.compare(y, comp.y) != 0) || (Float.compare(z, comp.z) != 0))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns a hash code value for this {@link Vector3f} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code x}, {@code y}, and {@code z} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int hash = 37;
		hash += (37 * hash) + Float.floatToIntBits(x);
		hash += (37 * hash) + Float.floatToIntBits(y);
		hash += (37 * hash) + Float.floatToIntBits(z);
		return hash;
	}
	
	/**
	 * Returns a string representation of this vector.<br>
	 * The output is formatted as a tuple of the {@code x}, {@code y}, and {@code z} values.
	 * @return A string containing the coordinates in parentheses.
	 */
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	/**
	 * Retrieves the X coordinate of the bookmark.<br>
	 * This value represents the horizontal position in the world.
	 * @return The {@code float} value of the X coordinate.
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Updates the {@code x} component of this vector.<br>
	 * This method modifies the current instance and returns it for chaining.
	 * @param x The new value to assign to the {@code x} coordinate.
	 * @return The current {@link Vector3f} instance.
	 */
	public Vector3f setX(float x)
	{
		this.x = x;
		return this;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Y coordinate.
	 */
	public float getY()
	{
		return y;
	}
	
	/**
	 * Sets the {@code y} component of this vector.<br>
	 * This method updates the internal value and returns the current instance.
	 * @param y The new value to assign to the {@code y} coordinate.
	 * @return The current {@link Vector3f} object.
	 */
	public Vector3f setY(float y)
	{
		this.y = y;
		return this;
	}
	
	/**
	 * Retrieves the vertical coordinate of the bookmark.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getZ()
	{
		return z;
	}
	
	/**
	 * Updates the {@code z} component of this vector.<br>
	 * This method modifies the current instance and returns it for chaining.
	 * @param z The new value to assign to the {@code z} coordinate.
	 * @return The current {@link Vector3f} instance.
	 */
	public Vector3f setZ(float z)
	{
		this.z = z;
		return this;
	}
	
	/**
	 * Retrieves a specific coordinate from the vector.<br>
	 * The value depends on the provided {@code index}.
	 * @param index The position to retrieve. Use {@code 0} for x, {@code 1} for y, or {@code 2} for z.
	 * @return The float value at the specified coordinate.
	 */
	public float get(int index)
	{
		switch (index)
		{
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
		}
		
		throw new IllegalArgumentException("index must be either 0, 1 or 2");
	}
	
	/**
	 * Updates a specific component of the vector.<br>
	 * The method modifies the internal values based on the provided index.
	 * @param index The position to update. Use {@code 0} for x, {@code 1} for y, or {@code 2} for z.
	 * @param value The new float value to assign to the component.
	 */
	public void set(int index, float value)
	{
		switch (index)
		{
			case 0:
				x = value;
				return;
			case 1:
				y = value;
				return;
			case 2:
				z = value;
				return;
		}
		
		throw new IllegalArgumentException("index must be either 0, 1 or 2");
	}
	
	/**
	 * Resets all coordinates to their default values.<br>
	 * This method sets {@code x}, {@code y}, and {@code z} to {@code 0}.<br>
	 * Use this to clear the current state of the {@link Vector3f} instance.
	 */
	public void reset()
	{
		x = y = z = 0f;
	}
	
	/**
	 * Creates a new instance of {@link Vector3f}.<br>
	 * It returns a vector with all components set to {@code 0}.
	 * @return A new {@code Vector3f} instance.
	 */
	public static Vector3f newInstance()
	{
		return new Vector3f();
	}
	
	/**
	 * Recycles the provided {@code Vector3f} instance.
	 * @param instance The {@code Vector3f} object to be recycled.
	 */
	public static void recycle(Vector3f instance)
	{
		// pooling removed
	}
}
