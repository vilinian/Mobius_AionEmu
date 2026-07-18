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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a 2D vector using two {@code float} values.<br>
 * This class provides basic mathematical operations for handling coordinates in a 2D space.
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Vector2f implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Vector2f.class);
	public static final Vector2f ZERO = new Vector2f(0f, 0f);
	public static final Vector2f UNIT_XY = new Vector2f(1f, 1f);
	/**
	 * the x value of the vector.
	 */
	public float x;
	/**
	 * the y value of the vector.
	 */
	public float y;
	
	/**
	 * Creates a new {@link Vector2f} instance.<br>
	 * This constructor sets the initial coordinates for the vector.
	 * @param x The horizontal component of the vector.
	 * @param y The vertical component of the vector.
	 */
	public Vector2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new {@link Vector2f} instance.<br>
	 * The initial values for both {@code x} and {@code y} are set to {@code 0}.
	 */
	public Vector2f()
	{
		x = y = 0;
	}
	
	/**
	 * Creates a new {@code Vector2f} instance by copying the values from another vector.<br>
	 * This method copies both the {@code x} and {@code y} fields.
	 * @param vector2f The source {@link Vector2f} to copy from.
	 */
	public Vector2f(Vector2f vector2f)
	{
		x = vector2f.x;
		y = vector2f.y;
	}
	
	/**
	 * Updates the coordinates of this vector.<br>
	 * The {@code x} and {@code y} values are replaced with new ones.<br>
	 * This method returns a reference to the current object.
	 * @param x The new value for the x coordinate.
	 * @param y The new value for the y coordinate.
	 * @return The current {@link Vector2f} instance.
	 */
	public Vector2f set(float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Updates the values of this vector to match another vector.<br>
	 * This method modifies the current object's {@code x} and {@code y} fields.
	 * @param vec The {@link Vector2f} to copy values from.
	 * @return The current instance of {@code Vector2f}.
	 */
	public Vector2f set(Vector2f vec)
	{
		x = vec.x;
		y = vec.y;
		return this;
	}
	
	/**
	 * Adds another vector to this one.<br>
	 * This method returns a new {@code Vector2f} instance.<br>
	 * The original values of this object remain unchanged.
	 * @param vec The vector to add to the current coordinates.
	 * @return A new {@code Vector2f} representing the sum, or {@code null} if {@code vec} is {@code null}.
	 */
	public Vector2f add(Vector2f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		return new Vector2f(x + vec.x, y + vec.y);
	}
	
	/**
	 * Adds the components of another vector to this vector.<br>
	 * This method modifies the current instance directly.<br>
	 * It returns {@code this} for method chaining.
	 * @param vec The {@link Vector2f} to add to this one.
	 * @return The modified {@code this} instance, or {@code null} if {@code vec} is {@code null}.
	 */
	public Vector2f addLocal(Vector2f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x += vec.x;
		y += vec.y;
		return this;
	}
	
	/**
	 * Adds values to the current vector coordinates.<br>
	 * This method modifies the internal state of the object.<br>
	 * It is similar to {@code add} but updates the existing instance.
	 * @param addX The amount to add to the {@code x} coordinate.
	 * @param addY The amount to add to the {@code y} coordinate.
	 * @return The current {@code Vector2f} instance after modification.
	 */
	public Vector2f addLocal(float addX, float addY)
	{
		x += addX;
		y += addY;
		return this;
	}
	
	/**
	 * Adds the components of two vectors together.<br>
	 * The sum is stored in a provided {@code Vector2f} object.<br>
	 * If the result object is {@code null}, a new instance is created.
	 * @param vec The vector to add to this one.
	 * @param result The vector where the sum will be stored.
	 * @return The resulting {@code Vector2f} object, or {@code null} if {@code vec} is {@code null}.
	 */
	public Vector2f add(Vector2f vec, Vector2f result)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		if (result == null)
		{
			result = new Vector2f();
		}
		
		result.x = x + vec.x;
		result.y = y + vec.y;
		return result;
	}
	
	/**
	 * Calculates the dot product between this vector and another {@code Vector2f}.<br>
	 * This operation is useful for finding the projection of one vector onto another.<br>
	 * It returns 0 if the provided {@code vec} is {@code null}.
	 * @param vec The other {@code Vector2f} to multiply with.
	 * @return The resulting dot product as a {@code float}.
	 */
	public float dot(Vector2f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, 0 returned.");
			return 0;
		}
		
		return (x * vec.x) + (y * vec.y);
	}
	
	/**
	 * Calculates the cross product between this vector and a {@code Vector2f}.<br>
	 * This operation returns a {@link Vector3f} where the z-component is the determinant.
	 * @param v The 2D vector to use for the calculation.
	 * @return A new {@code Vector3f} representing the result.
	 */
	public Vector3f cross(Vector2f v)
	{
		return new Vector3f(0, 0, determinant(v));
	}
	
	/**
	 * Calculates the determinant of a 2D matrix formed by this vector and another.<br>
	 * This is useful for finding the area of a parallelogram or checking orientation.
	 * @param v The second {@code Vector2f} used to calculate the determinant.
	 * @return The resulting float value of the calculation.
	 */
	public float determinant(Vector2f v)
	{
		return (x * v.y) - (y * v.x);
	}
	
	/**
	 * This method moves the current vector toward a target position.<br>
	 * It calculates a point between the current values and {@code finalVec}.<br>
	 * The result is updated locally based on the {@code changeAmnt} ratio.
	 * @param finalVec The target {@link Vector2f} to move towards.
	 * @param changeAmnt A float value representing the interpolation amount.
	 * @return This {@code Vector2f} instance after modification.
	 */
	public Vector2f interpolate(Vector2f finalVec, float changeAmnt)
	{
		x = ((1 - changeAmnt) * x) + (changeAmnt * finalVec.x);
		y = ((1 - changeAmnt) * y) + (changeAmnt * finalVec.y);
		return this;
	}
	
	/**
	 * This method calculates a point between two vectors.<br>
	 * It uses linear interpolation to find the new position.<br>
	 * The result is stored in the current {@code Vector2f} instance.
	 * @param beginVec The starting {@code Vector2f}.
	 * @param finalVec The target {@code Vector2f}.
	 * @param changeAmnt The interpolation factor between 0 and 1.
	 * @return The current {@code Vector2f} object after modification.
	 */
	public Vector2f interpolate(Vector2f beginVec, Vector2f finalVec, float changeAmnt)
	{
		x = ((1 - changeAmnt) * beginVec.x) + (changeAmnt * finalVec.x);
		y = ((1 - changeAmnt) * beginVec.y) + (changeAmnt * finalVec.y);
		return this;
	}
	
	/**
	 * Checks if a {@link Vector2f} object is valid.<br>
	 * It ensures the vector is not {@code null}.<br>
	 * It also checks that the coordinates are not {@code NaN} or infinite.
	 * @param vector The {@code Vector2f} to validate.
	 * @return {@code true} if the vector is valid, otherwise {@code false}.
	 */
	public static boolean isValidVector(Vector2f vector)
	{
		if ((vector == null) || Float.isNaN(vector.x) || Float.isNaN(vector.y))
		{
			return false;
		}
		
		if (Float.isInfinite(vector.x) || Float.isInfinite(vector.y))
		{
			return false;
		}
		
		return true;
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
		return (x * x) + (y * y);
	}
	
	/**
	 * Calculates the squared distance between this vector and another.<br>
	 * This is faster than {@code distance} because it avoids a square root operation.
	 * @param v The target {@code Vector2f} to measure against.
	 * @return The squared distance as a {@code float}.
	 */
	public float distanceSquared(Vector2f v)
	{
		final double dx = x - v.x;
		final double dy = y - v.y;
		return (float) ((dx * dx) + (dy * dy));
	}
	
	/**
	 * Calculates the squared distance between this vector and a point.<br>
	 * This method is faster than {@code distance} because it avoids a square root operation.
	 * @param otherX The x coordinate of the target point.
	 * @param otherY The y coordinate of the target point.
	 * @return The squared distance as a {@code float}.
	 */
	public float distanceSquared(float otherX, float otherY)
	{
		final double dx = x - otherX;
		final double dy = y - otherY;
		return (float) ((dx * dx) + (dy * dy));
	}
	
	/**
	 * Calculates the distance between this vector and another vector.<br>
	 * This method uses the {@code distanceSquared} calculation.
	 * @param v The target {@link Vector2f} to measure against.
	 * @return The straight-line distance as a {@code float}.
	 */
	public float distance(Vector2f v)
	{
		return FastMath.sqrt(distanceSquared(v));
	}
	
	/**
	 * Multiplies both components of this vector by a given value.<br>
	 * This method returns a new {@code Vector2f} instance.<br>
	 * The original vector remains unchanged.
	 * @param scalar The number to multiply the vector by.
	 * @return A new {@code Vector2f} representing the result.
	 */
	public Vector2f mult(float scalar)
	{
		return new Vector2f(x * scalar, y * scalar);
	}
	
	/**
	 * Multiplies the x and y components of this vector by a given value.<br>
	 * This method modifies the current instance instead of creating a new one.
	 * @param scalar The number to multiply both coordinates by.
	 * @return The modified {@code Vector2f} instance.
	 */
	public Vector2f multLocal(float scalar)
	{
		x *= scalar;
		y *= scalar;
		return this;
	}
	
	/**
	 * Multiplies the components of this vector by another {@code Vector2f}.<br>
	 * This method modifies the current instance values.<br>
	 * It returns {@code null} if the input is {@code null}.
	 * @param vec The vector to multiply with.
	 * @return The current instance after multiplication, or {@code null}.
	 */
	public Vector2f multLocal(Vector2f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x *= vec.x;
		y *= vec.y;
		return this;
	}
	
	/**
	 * Multiplies this vector by a {@code float} value.<br>
	 * The result is stored in the provided {@code Vector2f} object.<br>
	 * If the provided object is {@code null}, a new instance is created.
	 * @param scalar The number to multiply by.
	 * @param product The vector where the result will be stored.
	 * @return The resulting {@code Vector2f} object.
	 */
	public Vector2f mult(float scalar, Vector2f product)
	{
		if (null == product)
		{
			product = new Vector2f();
		}
		
		product.x = x * scalar;
		product.y = y * scalar;
		return product;
	}
	
	/**
	 * Divides the components of this vector by a given number.<br>
	 * This method returns a new {@link Vector2f} instance.<br>
	 * It does not modify the original vector values.
	 * @param scalar The value to divide both x and y by.
	 * @return A new {@code Vector2f} representing the result of the division.
	 */
	public Vector2f divide(float scalar)
	{
		return new Vector2f(x / scalar, y / scalar);
	}
	
	/**
	 * Divides the x and y components of this vector by a given value.<br>
	 * This operation modifies the current {@code Vector2f} instance.
	 * @param scalar The value to divide both components by.
	 * @return The current {@code Vector2f} instance.
	 */
	public Vector2f divideLocal(float scalar)
	{
		x /= scalar;
		y /= scalar;
		return this;
	}
	
	/**
	 * Creates a new vector with opposite coordinates.<br>
	 * This method returns a {@code Vector2f} where both x and y values are multiplied by -1.
	 * @return A new {@code Vector2f} representing the negation of this vector.
	 */
	public Vector2f negate()
	{
		return new Vector2f(-x, -y);
	}
	
	/**
	 * Negates the current vector values.<br>
	 * This method flips the signs of both {@code x} and {@code y}.<br>
	 * It modifies the current instance instead of creating a new one.
	 * @return The current {@code Vector2f} instance.
	 */
	public Vector2f negateLocal()
	{
		x = -x;
		y = -y;
		return this;
	}
	
	/**
	 * Subtracts the given vector from this vector.<br>
	 * This method returns a new {@code Vector2f} object.<br>
	 * It does not modify the original values of this instance.
	 * @param vec The vector to subtract from this one.
	 * @return A new {@code Vector2f} representing the result of the subtraction.
	 */
	public Vector2f subtract(Vector2f vec)
	{
		return subtract(vec, null);
	}
	
	/**
	 * Subtracts the values of one vector from another.<br>
	 * The result is stored in a provided vector object.<br>
	 * If the {@code store} parameter is {@code null}, a new {@link Vector2f} is created.
	 * @param vec The vector to subtract from this instance.
	 * @param store The vector where the result will be saved.
	 * @return The resulting {@link Vector2f}.
	 */
	public Vector2f subtract(Vector2f vec, Vector2f store)
	{
		if (store == null)
		{
			store = new Vector2f();
		}
		
		store.x = x - vec.x;
		store.y = y - vec.y;
		return store;
	}
	
	/**
	 * Subtracts two values from the current vector.<br>
	 * This method returns a new {@code Vector2f} instance.<br>
	 * It does not modify the original vector.
	 * @param valX The value to subtract from the x coordinate.
	 * @param valY The value to subtract from the y coordinate.
	 * @return A new {@code Vector2f} representing the result of the subtraction.
	 */
	public Vector2f subtract(float valX, float valY)
	{
		return new Vector2f(x - valX, y - valY);
	}
	
	/**
	 * Subtracts the coordinates of another vector from this one.<br>
	 * This method modifies the current {@code Vector2f} instance directly.<br>
	 * It returns the modified object to allow for method chaining.
	 * @param vec The {@code Vector2f} to subtract from this instance.
	 * @return The current {@code Vector2f} instance, or {@code null} if the input is {@code null}.
	 */
	public Vector2f subtractLocal(Vector2f vec)
	{
		if (null == vec)
		{
			logger.warn("Provided vector is null, null returned.");
			return null;
		}
		
		x -= vec.x;
		y -= vec.y;
		return this;
	}
	
	/**
	 * Subtracts values from the current vector coordinates.<br>
	 * This method modifies the internal state of this {@code Vector2f} instance.<br>
	 * It returns the modified object to allow for method chaining.
	 * @param valX The amount to subtract from the x coordinate.
	 * @param valY The amount to subtract from the y coordinate.
	 * @return This same {@code Vector2f} instance.
	 */
	public Vector2f subtractLocal(float valX, float valY)
	{
		x -= valX;
		y -= valY;
		return this;
	}
	
	/**
	 * Scales this vector to have a length of 1.<br>
	 * This is useful for finding the direction of a vector.<br>
	 * If the length is 0, it returns a unit vector.
	 * @return The normalized {@link Vector2f} instance.
	 */
	public Vector2f normalize()
	{
		final float length = length();
		if (length != 0)
		{
			return divide(length);
		}
		
		return divide(1);
	}
	
	/**
	 * Normalizes this vector to have a unit length of 1.<br>
	 * This method modifies the current instance directly.<br>
	 * If the length is 0, it returns a default unit vector.
	 * @return The modified {@code Vector2f} instance.
	 */
	public Vector2f normalizeLocal()
	{
		final float length = length();
		if (length != 0)
		{
			return divideLocal(length);
		}
		
		return divideLocal(1);
	}
	
	/**
	 * Calculates the smallest angle between this vector and another.<br>
	 * It uses the {@code dot} method to find the cosine of the angle.<br>
	 * The result is returned in radians.
	 * @param otherVector The {@code Vector2f} to compare against.
	 * @return The smallest angle as a {@code float}.
	 */
	public float smallestAngleBetween(Vector2f otherVector)
	{
		final float dotProduct = dot(otherVector);
		final float angle = FastMath.acos(dotProduct);
		return angle;
	}
	
	/**
	 * Calculates the angle between this vector and another vector.<br>
	 * The result is returned in radians.
	 * @param otherVector The {@code Vector2f} to compare against.
	 * @return The difference in radians between the two vectors.
	 */
	public float angleBetween(Vector2f otherVector)
	{
		final float angle = FastMath.atan2(otherVector.y, otherVector.x) - FastMath.atan2(y, x);
		return angle;
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
	 * Updates the {@code x} coordinate of this vector.<br>
	 * This method modifies the current instance and returns it for chaining.
	 * @param x The new value to assign to the {@code x} field.
	 * @return The current {@link Vector2f} instance.
	 */
	public Vector2f setX(float x)
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
	 * Updates the {@code y} coordinate of this vector.<br>
	 * This method modifies the current instance.
	 * @param y The new value to assign to the {@code y} field.
	 * @return The current {@link Vector2f} instance for method chaining.
	 */
	public Vector2f setY(float y)
	{
		this.y = y;
		return this;
	}
	
	/**
	 * Calculates the angle of this {@code Vector2f}.<br>
	 * The result is returned in radians.<br>
	 * It uses the {@code y} and {@code x} components to determine the direction.
	 * @return The calculated angle as a {@code float}.
	 */
	public float getAngle()
	{
		return -FastMath.atan2(y, x);
	}
	
	/**
	 * Resets the x and y coordinates of this vector to {@code 0}.<br>
	 * This method modifies the current instance.
	 * @return The current {@link Vector2f} instance.
	 */
	public Vector2f zero()
	{
		x = y = 0;
		return this;
	}
	
	/**
	 * Returns a hash code value for this {@link Vector2f} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the {@code x} and {@code y} fields.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int hash = 37;
		hash += (37 * hash) + Float.floatToIntBits(x);
		hash += (37 * hash) + Float.floatToIntBits(y);
		return hash;
	}
	
	/**
	 * Creates a copy of this {@code Vector2f} object.<br>
	 * This method returns a new instance with the same values.
	 * @return A new {@code Vector2f} instance.
	 */
	@Override
	public Vector2f clone()
	{
		try
		{
			return (Vector2f) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError(); // can not happen
		}
	}
	
	/**
	 * Converts the current vector values into a {@code float[]} array.<br>
	 * If the input is {@code null}, a new array of size 2 is created.<br>
	 * The first element is {@code x} and the second is {@code y}.
	 * @param floats The array to populate with the vector components.
	 * @return A {@code float[]} containing the vector's coordinates.
	 */
	public float[] toArray(float[] floats)
	{
		if (floats == null)
		{
			floats = new float[2];
		}
		
		floats[0] = x;
		floats[1] = y;
		return floats;
	}
	
	/**
	 * Compares this {@link Vector2f} object with another object for equality.<br>
	 * It checks if both objects have the same x and y values.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Vector2f))
		{
			return false;
		}
		
		if (this == o)
		{
			return true;
		}
		
		final Vector2f comp = (Vector2f) o;
		if ((Float.compare(x, comp.x) != 0) || (Float.compare(y, comp.y) != 0))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns a string representation of this {@code Vector2f}.<br>
	 * The output is formatted as a coordinate pair.
	 * @return A string containing the {@code x} and {@code y} values.
	 */
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	/**
	 * Reads the x and y coordinates from an external input stream.<br>
	 * This method updates the current values of this {@code Vector2f} instance.
	 * @param in The {@link ObjectInput} used to read the data.
	 * @throws IOException If an error occurs during reading.
	 */
	public void readExternal(ObjectInput in) throws IOException
	{
		x = in.readFloat();
		y = in.readFloat();
	}
	
	/**
	 * Writes the {@code x} and {@code y} values of this vector to an external stream.<br>
	 * This method is used for serializing the object data.
	 * @param out The {@link ObjectOutput} where the data will be written.
	 * @throws IOException If an error occurs during writing.
	 */
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeFloat(x);
		out.writeFloat(y);
	}
	
	/**
	 * Retrieves the runtime class of this object.<br>
	 * This method returns a subclass of {@link Vector2f}.
	 * @return The {@code Class} object representing the type of this instance.
	 */
	public Class<? extends Vector2f> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Rotates this vector around the origin.<br>
	 * The rotation is applied directly to the current {@code x} and {@code y} values.
	 * @param angle The rotation amount in radians.
	 * @param cw Set to {@code true} for clockwise rotation, or {@code false} for counter-clockwise rotation.
	 */
	public void rotateAroundOrigin(float angle, boolean cw)
	{
		if (cw)
		{
			angle = -angle;
		}
		
		final float newX = (FastMath.cos(angle) * x) - (FastMath.sin(angle) * y);
		final float newY = (FastMath.sin(angle) * x) + (FastMath.cos(angle) * y);
		x = newX;
		y = newY;
	}
}
