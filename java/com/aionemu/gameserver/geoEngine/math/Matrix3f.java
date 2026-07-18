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

import java.nio.FloatBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.geoEngine.utils.BufferUtils;

/**
 * This class represents a 3x3 matrix using {@code float} values.<br>
 * It provides methods to get and set internal data as well as perform common matrix operations. You can also use convenience methods to generate a new {@link Matrix3f} from specific values.
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Matrix3f implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Matrix3f.class);
	protected float m00, m01, m02;
	protected float m10, m11, m12;
	protected float m20, m21, m22;
	
	/**
	 * Creates a new instance of {@link Matrix3f}.<br>
	 * The matrix is initialized as an identity matrix.
	 */
	public Matrix3f()
	{
		loadIdentity();
	}
	
	/**
	 * Creates a new {@link Matrix3f} instance with specific values.<br>
	 * The values are assigned to the matrix elements in row-major order.
	 * @param m00 The value for row 0, column 0.
	 * @param m01 The value for row 0, column 1.
	 * @param m02 The value for row 0, column 2.
	 * @param m10 The value for row 1, column 0.
	 * @param m11 The value for row 1, column 1.
	 * @param m12 The value for row 1, column 2.
	 * @param m20 The value for row 2, column 0.
	 * @param m21 The value for row 2, column 1.
	 * @param m22 The value for row 2, column 2.
	 */
	public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	}
	
	/**
	 * Creates a new {@code Matrix3f} instance by copying the data from an existing matrix.<br>
	 * This method uses the {@code set} method to copy values.
	 * @param mat The source {@code Matrix3f} to copy from.
	 */
	public Matrix3f(Matrix3f mat)
	{
		set(mat);
	}
	
	/**
	 * Converts all matrix elements to their absolute values.<br>
	 * This method uses {@code FastMath.abs} for each component.
	 */
	public void absoluteLocal()
	{
		m00 = FastMath.abs(m00);
		m01 = FastMath.abs(m01);
		m02 = FastMath.abs(m02);
		m10 = FastMath.abs(m10);
		m11 = FastMath.abs(m11);
		m12 = FastMath.abs(m12);
		m20 = FastMath.abs(m20);
		m21 = FastMath.abs(m21);
		m22 = FastMath.abs(m22);
	}
	
	/**
	 * Copies the values from another {@code Matrix3f} into this instance.<br>
	 * If the provided {@code matrix} is {@code null}, this method calls {@code loadIdentity}.
	 * @param matrix The source matrix to copy from.
	 * @return This {@code Matrix3f} instance for method chaining.
	 */
	public Matrix3f set(Matrix3f matrix)
	{
		if (null == matrix)
		{
			loadIdentity();
		}
		else
		{
			m00 = matrix.m00;
			m01 = matrix.m01;
			m02 = matrix.m02;
			m10 = matrix.m10;
			m11 = matrix.m11;
			m12 = matrix.m12;
			m20 = matrix.m20;
			m21 = matrix.m21;
			m22 = matrix.m22;
		}
		
		return this;
	}
	
	/**
	 * Retrieves the value at a specific position in the matrix.<br>
	 * The position is defined by row and column indices.
	 * @param i The row index of the element.
	 * @param j The column index of the element.
	 * @return The {@code float} value located at the specified coordinates.
	 */
	public float get(int i, int j)
	{
		switch (i)
		{
			case 0:
				switch (j)
				{
					case 0:
						return m00;
					case 1:
						return m01;
					case 2:
						return m02;
				}
			case 1:
				switch (j)
				{
					case 0:
						return m10;
					case 1:
						return m11;
					case 2:
						return m12;
				}
			case 2:
				switch (j)
				{
					case 0:
						return m20;
					case 1:
						return m21;
					case 2:
						return m22;
				}
		}
		
		logger.warn("Invalid matrix index.");
		throw new IllegalArgumentException("Invalid indices into matrix.");
	}
	
	/**
	 * Copies the matrix values into a provided array.<br>
	 * The method supports both row-major and column-major ordering.<br>
	 * It handles arrays of length 9 or 16.
	 * @param data The {@code float[]} array to store the matrix values in.
	 * @param rowMajor Set to {@code true} for row-major order, or {@code false} for column-major order.
	 */
	public void get(float[] data, boolean rowMajor)
	{
		if (data.length == 9)
		{
			if (rowMajor)
			{
				data[0] = m00;
				data[1] = m01;
				data[2] = m02;
				data[3] = m10;
				data[4] = m11;
				data[5] = m12;
				data[6] = m20;
				data[7] = m21;
				data[8] = m22;
			}
			else
			{
				data[0] = m00;
				data[1] = m10;
				data[2] = m20;
				data[3] = m01;
				data[4] = m11;
				data[5] = m21;
				data[6] = m02;
				data[7] = m12;
				data[8] = m22;
			}
		}
		else if (data.length == 16)
		{
			if (rowMajor)
			{
				data[0] = m00;
				data[1] = m01;
				data[2] = m02;
				data[4] = m10;
				data[5] = m11;
				data[6] = m12;
				data[8] = m20;
				data[9] = m21;
				data[10] = m22;
			}
			else
			{
				data[0] = m00;
				data[1] = m10;
				data[2] = m20;
				data[4] = m01;
				data[5] = m11;
				data[6] = m21;
				data[8] = m02;
				data[9] = m12;
				data[10] = m22;
			}
		}
		else
		{
			throw new IndexOutOfBoundsException("Array size must be 9 or 16 in Matrix3f.get().");
		}
	}
	
	/**
	 * Retrieves a specific column from the matrix.<br>
	 * This method returns the data as a {@link Vector3f}.
	 * @param i The index of the column to retrieve.
	 * @return A new {@code Vector3f} representing the requested column.
	 */
	public Vector3f getColumn(int i)
	{
		return getColumn(i, null);
	}
	
	/**
	 * Retrieves a specific column from the matrix.<br>
	 * The values are copied into the provided {@code Vector3f} object.<br>
	 * If the provided object is {@code null}, a new instance is created.
	 * @param i The index of the column to retrieve.
	 * @param store The {@code Vector3f} where the result will be stored.
	 * @return The populated {@code Vector3f} containing the column data.
	 */
	public Vector3f getColumn(int i, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		switch (i)
		{
			case 0:
				store.x = m00;
				store.y = m10;
				store.z = m20;
				break;
			case 1:
				store.x = m01;
				store.y = m11;
				store.z = m21;
				break;
			case 2:
				store.x = m02;
				store.y = m12;
				store.z = m22;
				break;
			default:
				logger.warn("Invalid column index.");
				throw new IllegalArgumentException("Invalid column index. " + i);
		}
		
		return store;
	}
	
	/**
	 * Retrieves a specific row from the matrix as a {@code Vector3f}.<br>
	 * This method returns a new object containing the values of the requested row.
	 * @param i The index of the row to retrieve.
	 * @return A {@link Vector3f} representing the row at index {@code i}.
	 */
	public Vector3f getRow(int i)
	{
		return getRow(i, null);
	}
	
	/**
	 * Retrieves a specific row from the matrix and stores it in a {@code Vector3f}.<br>
	 * If the provided {@code store} is {@code null}, a new {@code Vector3f} will be created.
	 * @param i The index of the row to retrieve.
	 * @param store The {@code Vector3f} object where the result will be stored.
	 * @return The populated {@code Vector3f} containing the row data.
	 */
	public Vector3f getRow(int i, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		switch (i)
		{
			case 0:
				store.x = m00;
				store.y = m01;
				store.z = m02;
				break;
			case 1:
				store.x = m10;
				store.y = m11;
				store.z = m12;
				break;
			case 2:
				store.x = m20;
				store.y = m21;
				store.z = m22;
				break;
			default:
				logger.warn("Invalid row index.");
				throw new IllegalArgumentException("Invalid row index. " + i);
		}
		
		return store;
	}
	
	/**
	 * Converts the current matrix data into a {@code FloatBuffer}.<br>
	 * The buffer contains 9 elements in row-major order.
	 * @return A new {@code FloatBuffer} containing the matrix values.
	 */
	public FloatBuffer toFloatBuffer()
	{
		final FloatBuffer fb = BufferUtils.createFloatBuffer(9);
		
		fb.put(m00).put(m01).put(m02);
		fb.put(m10).put(m11).put(m12);
		fb.put(m20).put(m21).put(m22);
		fb.rewind();
		return fb;
	}
	
	/**
	 * Fills a {@code FloatBuffer} with the matrix data.<br>
	 * The order of elements depends on the {@code columnMajor} flag.
	 * @param fb The buffer to be populated.
	 * @param columnMajor Set to {@code true} for column-major order, or {@code false} for row-major order.
	 * @return The same {@code FloatBuffer} instance after it has been filled.
	 */
	public FloatBuffer fillFloatBuffer(FloatBuffer fb, boolean columnMajor)
	{
		if (columnMajor)
		{
			fb.put(m00).put(m10).put(m20);
			fb.put(m01).put(m11).put(m21);
			fb.put(m02).put(m12).put(m22);
		}
		else
		{
			fb.put(m00).put(m01).put(m02);
			fb.put(m10).put(m11).put(m12);
			fb.put(m20).put(m21).put(m22);
		}
		
		return fb;
	}
	
	/**
	 * Sets a specific column in the matrix using the values from a {@code Vector3f}.<br>
	 * This method updates the internal data of the {@link Matrix3f} object.
	 * @param i The index of the column to set. Must be 0, 1, or 2.
	 * @param column The {@code Vector3f} containing the new values for the column.
	 * @return This {@code Matrix3f} instance for method chaining.
	 */
	public Matrix3f setColumn(int i, Vector3f column)
	{
		if (column == null)
		{
			logger.warn("Column is null. Ignoring.");
			return this;
		}
		
		switch (i)
		{
			case 0:
				m00 = column.x;
				m10 = column.y;
				m20 = column.z;
				break;
			case 1:
				m01 = column.x;
				m11 = column.y;
				m21 = column.z;
				break;
			case 2:
				m02 = column.x;
				m12 = column.y;
				m22 = column.z;
				break;
			default:
				logger.warn("Invalid column index.");
				throw new IllegalArgumentException("Invalid column index. " + i);
		}
		
		return this;
	}
	
	/**
	 * Sets a specific row in the matrix using the values from a {@code Vector3f}.<br>
	 * This method updates the internal components of the matrix at the given index.<br>
	 * If the provided row is {@code null}, no changes are made to the matrix.
	 * @param i The index of the row to update. Must be 0, 1, or 2.
	 * @param row The {@code Vector3f} containing the new values for the row.
	 * @return This {@link Matrix3f} instance for method chaining.
	 */
	public Matrix3f setRow(int i, Vector3f row)
	{
		if (row == null)
		{
			logger.warn("Row is null. Ignoring.");
			return this;
		}
		
		switch (i)
		{
			case 0:
				m00 = row.x;
				m01 = row.y;
				m02 = row.z;
				break;
			case 1:
				m10 = row.x;
				m11 = row.y;
				m12 = row.z;
				break;
			case 2:
				m20 = row.x;
				m21 = row.y;
				m22 = row.z;
				break;
			default:
				logger.warn("Invalid row index.");
				throw new IllegalArgumentException("Invalid row index. " + i);
		}
		
		return this;
	}
	
	/**
	 * Updates a specific element in the matrix.<br>
	 * The value is placed at the row and column specified by the indices.
	 * @param i The row index of the element.
	 * @param j The column index of the element.
	 * @param value The new float value to assign.
	 * @return The current {@code Matrix3f} instance.
	 */
	public Matrix3f set(int i, int j, float value)
	{
		switch (i)
		{
			case 0:
				switch (j)
				{
					case 0:
						m00 = value;
						return this;
					case 1:
						m01 = value;
						return this;
					case 2:
						m02 = value;
						return this;
				}
			case 1:
				switch (j)
				{
					case 0:
						m10 = value;
						return this;
					case 1:
						m11 = value;
						return this;
					case 2:
						m12 = value;
						return this;
				}
			case 2:
				switch (j)
				{
					case 0:
						m20 = value;
						return this;
					case 1:
						m21 = value;
						return this;
					case 2:
						m22 = value;
						return this;
				}
		}
		
		logger.warn("Invalid matrix index.");
		throw new IllegalArgumentException("Invalid indices into matrix.");
	}
	
	/**
	 * Updates the internal values of this <code class="Matrix3f"> with data from a 2D array.<br>
	 * The input {@code float[][]} must have a size of 3x3.
	 * @param matrix A 2D array containing the new matrix values.
	 * @return This <code class="Matrix3f"> instance for method chaining.
	 */
	public Matrix3f set(float[][] matrix)
	{
		if ((matrix.length != 3) || (matrix[0].length != 3))
		{
			throw new IllegalArgumentException("Array must be of size 9.");
		}
		
		m00 = matrix[0][0];
		m01 = matrix[0][1];
		m02 = matrix[0][2];
		m10 = matrix[1][0];
		m11 = matrix[1][1];
		m12 = matrix[1][2];
		m20 = matrix[2][0];
		m21 = matrix[2][1];
		m22 = matrix[2][2];
		
		return this;
	}
	
	/**
	 * Sets the matrix values based on three provided axes.<br>
	 * This method populates the columns of the {@code Matrix3f} with the given vectors.
	 * @param uAxis The first column vector.
	 * @param vAxis The second column vector.
	 * @param wAxis The third column vector.
	 */
	public void fromAxes(Vector3f uAxis, Vector3f vAxis, Vector3f wAxis)
	{
		m00 = uAxis.x;
		m10 = uAxis.y;
		m20 = uAxis.z;
		
		m01 = vAxis.x;
		m11 = vAxis.y;
		m21 = vAxis.z;
		
		m02 = wAxis.x;
		m12 = wAxis.y;
		m22 = wAxis.z;
	}
	
	/**
	 * Updates the internal values of this matrix using an array.<br>
	 * This method treats the input as a column-major array.
	 * @param matrix The array of {@code float} values to copy into the matrix.
	 * @return The current {@link Matrix3f} instance for method chaining.
	 */
	public Matrix3f set(float[] matrix)
	{
		return set(matrix, true);
	}
	
	/**
	 * Updates the internal values of this <code class="Matrix3f"> with data from an array.<br>
	 * It supports both row-major and column-major ordering.
	 * @param matrix The array containing 9 float values to populate the matrix.
	 * @param rowMajor Set to {@code true} if the input array is in row-major order, or {@code false} for column-major order.
	 * @return This <code class="Matrix3f"> instance for method chaining.
	 */
	public Matrix3f set(float[] matrix, boolean rowMajor)
	{
		if (matrix.length != 9)
		{
			throw new IllegalArgumentException("Array must be of size 9.");
		}
		
		if (rowMajor)
		{
			m00 = matrix[0];
			m01 = matrix[1];
			m02 = matrix[2];
			m10 = matrix[3];
			m11 = matrix[4];
			m12 = matrix[5];
			m20 = matrix[6];
			m21 = matrix[7];
			m22 = matrix[8];
		}
		else
		{
			m00 = matrix[0];
			m01 = matrix[3];
			m02 = matrix[6];
			m10 = matrix[1];
			m11 = matrix[4];
			m12 = matrix[7];
			m20 = matrix[2];
			m21 = matrix[5];
			m22 = matrix[8];
		}
		
		return this;
	}
	
	/**
	 * Resets the matrix to its identity state.<br>
	 * All off-diagonal elements are set to {@code 0}.<br>
	 * The diagonal elements are set to {@code 1}.
	 */
	public void loadIdentity()
	{
		m01 = m02 = m10 = m12 = m20 = m21 = 0;
		m00 = m11 = m22 = 1;
	}
	
	/**
	 * Checks if this matrix is an identity matrix.<br>
	 * An identity matrix has {@code 1.0} on the main diagonal and {@code 0.0} elsewhere.
	 * @return {@code true} if the matrix is an identity matrix, {@code false} otherwise.
	 */
	public boolean isIdentity()
	{
		return ((m00 == 1) && (m01 == 0) && (m02 == 0)) && ((m10 == 0) && (m11 == 1) && (m12 == 0)) && ((m20 == 0) && (m21 == 0) && (m22 == 1));
	}
	
	/**
	 * Creates a rotation matrix from an angle and an axis.<br>
	 * This method normalizes the {@code axis} before calculation.<br>
	 * It updates the current matrix values internally.
	 * @param angle The rotation angle in radians.
	 * @param axis The vector defining the axis of rotation.
	 */
	public void fromAngleAxis(float angle, Vector3f axis)
	{
		final Vector3f normAxis = axis.normalize();
		fromAngleNormalAxis(angle, normAxis);
	}
	
	/**
	 * Sets the matrix values based on a rotation.<br>
	 * This method uses an angle and a unit axis to create a rotation matrix.
	 * @param angle The rotation angle in radians.
	 * @param axis The {@code Vector3f} representing the axis of rotation.
	 */
	public void fromAngleNormalAxis(float angle, Vector3f axis)
	{
		final float fCos = FastMath.cos(angle);
		final float fSin = FastMath.sin(angle);
		final float fOneMinusCos = ((float) 1.0) - fCos;
		final float fX2 = axis.x * axis.x;
		final float fY2 = axis.y * axis.y;
		final float fZ2 = axis.z * axis.z;
		final float fXYM = axis.x * axis.y * fOneMinusCos;
		final float fXZM = axis.x * axis.z * fOneMinusCos;
		final float fYZM = axis.y * axis.z * fOneMinusCos;
		final float fXSin = axis.x * fSin;
		final float fYSin = axis.y * fSin;
		final float fZSin = axis.z * fSin;
		
		m00 = (fX2 * fOneMinusCos) + fCos;
		m01 = fXYM - fZSin;
		m02 = fXZM + fYSin;
		m10 = fXYM + fZSin;
		m11 = (fY2 * fOneMinusCos) + fCos;
		m12 = fYZM - fXSin;
		m20 = fXZM - fYSin;
		m21 = fYZM + fXSin;
		m22 = (fZ2 * fOneMinusCos) + fCos;
	}
	
	/**
	 * Multiplies this matrix by another <code class="Matrix3f">.<br>
	 * This method performs a standard matrix multiplication.<br>
	 * The result is returned as a new <code class="Matrix3f"> object.
	 * @param mat The other <code class="Matrix3f"> to multiply with.
	 * @return A new <code class="Matrix3f"> representing the product of the two matrices.
	 */
	public Matrix3f mult(Matrix3f mat)
	{
		return mult(mat, null);
	}
	
	/**
	 * Multiplies this matrix by another matrix.<br>
	 * The result is stored in the provided destination matrix.<br>
	 * If the destination matrix is {@code null}, a new {@link Matrix3f} is created.
	 * @param mat The matrix to multiply with.
	 * @param product The matrix where the result will be stored.
	 * @return The resulting {@link Matrix3f} object.
	 */
	public Matrix3f mult(Matrix3f mat, Matrix3f product)
	{
		float temp00, temp01, temp02;
		float temp10, temp11, temp12;
		float temp20, temp21, temp22;
		
		if (product == null)
		{
			product = new Matrix3f();
		}
		
		temp00 = (m00 * mat.m00) + (m01 * mat.m10) + (m02 * mat.m20);
		temp01 = (m00 * mat.m01) + (m01 * mat.m11) + (m02 * mat.m21);
		temp02 = (m00 * mat.m02) + (m01 * mat.m12) + (m02 * mat.m22);
		temp10 = (m10 * mat.m00) + (m11 * mat.m10) + (m12 * mat.m20);
		temp11 = (m10 * mat.m01) + (m11 * mat.m11) + (m12 * mat.m21);
		temp12 = (m10 * mat.m02) + (m11 * mat.m12) + (m12 * mat.m22);
		temp20 = (m20 * mat.m00) + (m21 * mat.m10) + (m22 * mat.m20);
		temp21 = (m20 * mat.m01) + (m21 * mat.m11) + (m22 * mat.m21);
		temp22 = (m20 * mat.m02) + (m21 * mat.m12) + (m22 * mat.m22);
		
		product.m00 = temp00;
		product.m01 = temp01;
		product.m02 = temp02;
		product.m10 = temp10;
		product.m11 = temp11;
		product.m12 = temp12;
		product.m20 = temp20;
		product.m21 = temp21;
		product.m22 = temp22;
		
		return product;
	}
	
	/**
	 * Multiplies this matrix by a {@code Vector3f}.<br>
	 * This method performs a standard matrix-vector multiplication.<br>
	 * It returns the resulting vector as a new object.
	 * @param vec The {@code Vector3f} to multiply.
	 * @return A new {@code Vector3f} representing the result of the multiplication.
	 */
	public Vector3f mult(Vector3f vec)
	{
		return mult(vec, null);
	}
	
	/**
	 * Multiplies this matrix by a {@code Vector3f}.<br>
	 * The result is stored in the provided {@code Vector3f} object.<br>
	 * If the provided product vector is {@code null}, a new one is created.
	 * @param vec The input vector to multiply.
	 * @param product The vector where the result will be stored.
	 * @return The resulting {@code Vector3f} object.
	 */
	public Vector3f mult(Vector3f vec, Vector3f product)
	{
		if (null == product)
		{
			product = new Vector3f();
		}
		
		final float x = vec.x;
		final float y = vec.y;
		final float z = vec.z;
		
		product.x = (m00 * x) + (m01 * y) + (m02 * z);
		product.y = (m10 * x) + (m11 * y) + (m12 * z);
		product.z = (m20 * x) + (m21 * y) + (m22 * z);
		return product;
	}
	
	/**
	 * Multiplies the current matrix by a local scaling factor.<br>
	 * This method modifies the internal values of the {@code Matrix3f}.
	 * @param scale The value to multiply each element by.
	 * @return The current instance of {@code Matrix3f} for method chaining.
	 */
	public Matrix3f multLocal(float scale)
	{
		m00 *= scale;
		m01 *= scale;
		m02 *= scale;
		m10 *= scale;
		m11 *= scale;
		m12 *= scale;
		m20 *= scale;
		m21 *= scale;
		m22 *= scale;
		return this;
	}
	
	/**
	 * Multiplies the current matrix by a {@code Vector3f}.<br>
	 * This method modifies the input vector in place.
	 * @param vec The {@code Vector3f} to be multiplied.
	 * @return The modified {@code Vector3f} or {@code null} if the input is {@code null}.
	 */
	public Vector3f multLocal(Vector3f vec)
	{
		if (vec == null)
		{
			return null;
		}
		
		final float x = vec.x;
		final float y = vec.y;
		vec.x = (m00 * x) + (m01 * y) + (m02 * vec.z);
		vec.y = (m10 * x) + (m11 * y) + (m12 * vec.z);
		vec.z = (m20 * x) + (m21 * y) + (m22 * vec.z);
		return vec;
	}
	
	/**
	 * Multiplies the current matrix by another matrix.<br>
	 * This method performs a local multiplication where the provided matrix is multiplied by {@code this}.<br>
	 * It returns a new {@link Matrix3f} containing the result.
	 * @param mat The matrix to multiply with the current instance.
	 * @return A new {@link Matrix3f} representing the product of the two matrices.
	 */
	public Matrix3f multLocal(Matrix3f mat)
	{
		return mult(mat, this);
	}
	
	/**
	 * Transposes the matrix elements in place.<br>
	 * This method swaps the rows and columns of the current {@code Matrix3f}.<br>
	 * It modifies the internal data of the object directly.
	 * @return The current {@code Matrix3f} instance for method chaining.
	 */
	public Matrix3f transposeLocal()
	{
		// float[] tmp = new float[9];
		// get(tmp, false);
		// set(tmp, true);
		
		float tmp = m01;
		m01 = m10;
		m10 = tmp;
		
		tmp = m02;
		m02 = m20;
		m20 = tmp;
		
		tmp = m12;
		m12 = m21;
		m21 = tmp;
		
		return this;
	}
	
	/**
	 * Calculates the inverse of the current matrix.<br>
	 * This method returns a new {@link Matrix3f} object.<br>
	 * If the matrix is singular, it may return {@code null}.
	 * @return The inverted {@link Matrix3f} or {@code null} if no inverse exists.
	 */
	public Matrix3f invert()
	{
		return invert(null);
	}
	
	/**
	 * Calculates the inverse of this matrix.<br>
	 * If the determinant is near zero, it returns a zero matrix.
	 * @param store The {@code Matrix3f} object to store the result in. If {@code null}, a new instance is created.
	 * @return The resulting inverted {@code Matrix3f}.
	 */
	public Matrix3f invert(Matrix3f store)
	{
		if (store == null)
		{
			store = new Matrix3f();
		}
		
		final float det = determinant();
		if (FastMath.abs(det) <= FastMath.FLT_EPSILON)
		{
			return store.zero();
		}
		
		store.m00 = (m11 * m22) - (m12 * m21);
		store.m01 = (m02 * m21) - (m01 * m22);
		store.m02 = (m01 * m12) - (m02 * m11);
		store.m10 = (m12 * m20) - (m10 * m22);
		store.m11 = (m00 * m22) - (m02 * m20);
		store.m12 = (m02 * m10) - (m00 * m12);
		store.m20 = (m10 * m21) - (m11 * m20);
		store.m21 = (m01 * m20) - (m00 * m21);
		store.m22 = (m00 * m11) - (m01 * m10);
		
		store.multLocal(1f / det);
		return store;
	}
	
	/**
	 * Calculates the inverse of the current matrix.<br>
	 * This method modifies the internal values of the {@code Matrix3f} object.<br>
	 * If the determinant is near zero, it returns a zero matrix.
	 * @return The current {@code Matrix3f} instance after inversion.
	 */
	public Matrix3f invertLocal()
	{
		final float det = determinant();
		if (FastMath.abs(det) <= FastMath.FLT_EPSILON)
		{
			return zero();
		}
		
		final float f00 = (m11 * m22) - (m12 * m21);
		final float f01 = (m02 * m21) - (m01 * m22);
		final float f02 = (m01 * m12) - (m02 * m11);
		final float f10 = (m12 * m20) - (m10 * m22);
		final float f11 = (m00 * m22) - (m02 * m20);
		final float f12 = (m02 * m10) - (m00 * m12);
		final float f20 = (m10 * m21) - (m11 * m20);
		final float f21 = (m01 * m20) - (m00 * m21);
		final float f22 = (m00 * m11) - (m01 * m10);
		
		m00 = f00;
		m01 = f01;
		m02 = f02;
		m10 = f10;
		m11 = f11;
		m12 = f12;
		m20 = f20;
		m21 = f21;
		m22 = f22;
		
		multLocal(1f / det);
		return this;
	}
	
	/**
	 * Calculates the adjoint of this matrix.<br>
	 * The result is returned as a new {@link Matrix3f} object.
	 * @return A new {@link Matrix3f} representing the adjoint matrix.
	 */
	public Matrix3f adjoint()
	{
		return adjoint(null);
	}
	
	/**
	 * Calculates the adjoint of this matrix.<br>
	 * The result is stored in the provided {@code Matrix3f} object.<br>
	 * If the input is {@code null}, a new instance is created.
	 * @param store The {@code Matrix3f} to store the result in.
	 * @return The resulting {@code Matrix3f} object.
	 */
	public Matrix3f adjoint(Matrix3f store)
	{
		if (store == null)
		{
			store = new Matrix3f();
		}
		
		store.m00 = (m11 * m22) - (m12 * m21);
		store.m01 = (m02 * m21) - (m01 * m22);
		store.m02 = (m01 * m12) - (m02 * m11);
		store.m10 = (m12 * m20) - (m10 * m22);
		store.m11 = (m00 * m22) - (m02 * m20);
		store.m12 = (m02 * m10) - (m00 * m12);
		store.m20 = (m10 * m21) - (m11 * m20);
		store.m21 = (m01 * m20) - (m00 * m21);
		store.m22 = (m00 * m11) - (m01 * m10);
		
		return store;
	}
	
	/**
	 * Calculates the determinant of the current 3x3 matrix.<br>
	 * This value is used to determine scaling and orientation properties.
	 * @return The calculated determinant as a {@code float}.
	 */
	public float determinant()
	{
		final float fCo00 = (m11 * m22) - (m12 * m21);
		final float fCo10 = (m12 * m20) - (m10 * m22);
		final float fCo20 = (m10 * m21) - (m11 * m20);
		final float fDet = (m00 * fCo00) + (m01 * fCo10) + (m02 * fCo20);
		return fDet;
	}
	
	/**
	 * Creates a new {@link Matrix3f} where all elements are set to {@code 0.0f}.<br>
	 * This method resets the current matrix to a zero state.
	 * @return the current {@link Matrix3f} instance.
	 */
	public Matrix3f zero()
	{
		m00 = m01 = m02 = m10 = m11 = m12 = m20 = m21 = m22 = 0.0f;
		return this;
	}
	
	/**
	 * Creates a new {@code Matrix3f} that is the transpose of this matrix.<br>
	 * The rows and columns of the original matrix are swapped.<br>
	 * This method calls {@code transposeLocal} internally.
	 * @return A new {@code Matrix3f} instance representing the transposed matrix.
	 */
	public Matrix3f transpose()
	{
		return transposeLocal();
	}
	
	/**
	 * Creates a new {@code Matrix3f} instance that is the transpose of this matrix.<br>
	 * The original matrix remains unchanged.
	 * @return A new {@code Matrix3f} object containing the transposed values.
	 */
	public Matrix3f transposeNew()
	{
		final Matrix3f ret = new Matrix3f(m00, m10, m20, m01, m11, m21, m02, m12, m22);
		return ret;
	}
	
	/**
	 * Returns a string representation of the matrix.<br>
	 * This method formats the internal values into a 3x3 grid layout.
	 * @return A formatted string representing this {@code Matrix3f}.
	 */
	@Override
	public String toString()
	{
		final StringBuffer result = new StringBuffer("Matrix3f\n[\n");
		result.append(" ");
		result.append(m00);
		result.append("  ");
		result.append(m01);
		result.append("  ");
		result.append(m02);
		result.append(" \n");
		result.append(" ");
		result.append(m10);
		result.append("  ");
		result.append(m11);
		result.append("  ");
		result.append(m12);
		result.append(" \n");
		result.append(" ");
		result.append(m20);
		result.append("  ");
		result.append(m21);
		result.append("  ");
		result.append(m22);
		result.append(" \n]");
		return result.toString();
	}
	
	/**
	 * Returns a hash code value for this {@link Matrix3f} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on all nine matrix components.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int hash = 37;
		hash = (37 * hash) + Float.floatToIntBits(m00);
		hash = (37 * hash) + Float.floatToIntBits(m01);
		hash = (37 * hash) + Float.floatToIntBits(m02);
		
		hash = (37 * hash) + Float.floatToIntBits(m10);
		hash = (37 * hash) + Float.floatToIntBits(m11);
		hash = (37 * hash) + Float.floatToIntBits(m12);
		
		hash = (37 * hash) + Float.floatToIntBits(m20);
		hash = (37 * hash) + Float.floatToIntBits(m21);
		hash = (37 * hash) + Float.floatToIntBits(m22);
		
		return hash;
	}
	
	/**
	 * Compares this {@link Matrix3f} object with another object for equality.<br>
	 * It checks if both objects have the same matrix values.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Matrix3f))
		{
			return false;
		}
		
		if (this == o)
		{
			return true;
		}
		
		final Matrix3f comp = (Matrix3f) o;
		if ((Float.compare(m00, comp.m00) != 0) || (Float.compare(m01, comp.m01) != 0) || (Float.compare(m02, comp.m02) != 0) || (Float.compare(m10, comp.m10) != 0))
		{
			return false;
		}
		
		if ((Float.compare(m11, comp.m11) != 0) || (Float.compare(m12, comp.m12) != 0) || (Float.compare(m20, comp.m20) != 0) || (Float.compare(m21, comp.m21) != 0))
		{
			return false;
		}
		
		if (Float.compare(m22, comp.m22) != 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the runtime class of the current instance.<br>
	 * This method helps identify the specific subclass of {@link Matrix3f}.
	 * @return The {@code Class} object representing the type of this instance.
	 */
	public Class<? extends Matrix3f> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Creates a rotation matrix from two vectors.<br>
	 * This method calculates the transformation required to align {@code start} with {@code end}.<br>
	 * It handles cases where the vectors are nearly parallel by finding an orthogonal basis.
	 * @param start The starting vector for the rotation.
	 * @param end The target vector to rotate towards.
	 */
	public void fromStartEndVectors(Vector3f start, Vector3f end)
	{
		final Vector3f v = new Vector3f();
		float e, h, f;
		
		start.cross(end, v);
		e = start.dot(end);
		f = (e < 0) ? -e : e;
		
		// if "from" and "to" vectors are nearly parallel
		if (f > (1.0f - FastMath.ZERO_TOLERANCE))
		{
			final Vector3f u = new Vector3f();
			final Vector3f x = new Vector3f();
			float c1, c2, c3; /* coefficients for later use */
			
			int i, j;
			
			x.x = (start.x > 0.0) ? start.x : -start.x;
			x.y = (start.y > 0.0) ? start.y : -start.y;
			x.z = (start.z > 0.0) ? start.z : -start.z;
			
			if (x.x < x.y)
			{
				if (x.x < x.z)
				{
					x.x = 1.0f;
					x.y = x.z = 0.0f;
				}
				else
				{
					x.z = 1.0f;
					x.x = x.y = 0.0f;
				}
			}
			else
			{
				if (x.y < x.z)
				{
					x.y = 1.0f;
					x.x = x.z = 0.0f;
				}
				else
				{
					x.z = 1.0f;
					x.x = x.y = 0.0f;
				}
			}
			
			u.x = x.x - start.x;
			u.y = x.y - start.y;
			u.z = x.z - start.z;
			v.x = x.x - end.x;
			v.y = x.y - end.y;
			v.z = x.z - end.z;
			
			c1 = 2.0f / u.dot(u);
			c2 = 2.0f / v.dot(v);
			c3 = c1 * c2 * u.dot(v);
			
			for (i = 0; i < 3; i++)
			{
				for (j = 0; j < 3; j++)
				{
					final float val = ((-c1 * u.get(i) * u.get(j)) - (c2 * v.get(i) * v.get(j))) + (c3 * v.get(i) * u.get(j));
					set(i, j, val);
				}
				
				final float val = get(i, i);
				set(i, i, val + 1.0f);
			}
		}
		else
		{
			// the most common case, unless "start"="end", or "start"=-"end"
			float hvx, hvz, hvxy, hvxz, hvyz;
			h = 1.0f / (1.0f + e);
			hvx = h * v.x;
			hvz = h * v.z;
			hvxy = hvx * v.y;
			hvxz = hvx * v.z;
			hvyz = hvz * v.y;
			set(0, 0, e + (hvx * v.x));
			set(0, 1, hvxy - v.z);
			set(0, 2, hvxz + v.y);
			
			set(1, 0, hvxy + v.z);
			set(1, 1, e + (h * v.y * v.y));
			set(1, 2, hvyz - v.x);
			
			set(2, 0, hvxz - v.y);
			set(2, 1, hvyz + v.x);
			set(2, 2, e + (hvz * v.z));
		}
	}
	
	/**
	 * Multiplies the current matrix by a scaling factor.<br>
	 * This method adjusts the size of the transformation along each axis.
	 * @param scale The {@code Vector3f} containing the x, y, and z scale values.
	 */
	public void scale(Vector3f scale)
	{
		m00 *= scale.x;
		m10 *= scale.x;
		m20 *= scale.x;
		m01 *= scale.y;
		m11 *= scale.y;
		m21 *= scale.y;
		m02 *= scale.z;
		m12 *= scale.z;
		m22 *= scale.z;
	}
	
	/**
	 * Checks if the provided matrix is an identity matrix.<br>
	 * It compares the values against a small epsilon of {@code 1e-4}.
	 * @param mat The <code class="Matrix3f">Matrix3f</code> to check.
	 * @return {@code true} if the matrix is an identity matrix, otherwise {@code false}.
	 */
	static boolean equalIdentity(Matrix3f mat)
	{
		if ((Math.abs(mat.m00 - 1) > 1e-4) || (Math.abs(mat.m11 - 1) > 1e-4) || (Math.abs(mat.m22 - 1) > 1e-4) || (Math.abs(mat.m01) > 1e-4))
		{
			return false;
		}
		
		if ((Math.abs(mat.m02) > 1e-4) || (Math.abs(mat.m10) > 1e-4) || (Math.abs(mat.m12) > 1e-4) || (Math.abs(mat.m20) > 1e-4))
		{
			return false;
		}
		
		if (Math.abs(mat.m21) > 1e-4)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates a new <code class="Matrix3f"> object that is a copy of this instance.<br>
	 * This method performs a shallow copy of the matrix data.
	 * @return A new <code class="Matrix3f"> instance with the same values as the original.
	 */
	@Override
	public Matrix3f clone()
	{
		try
		{
			return (Matrix3f) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError(); // can not happen
		}
	}
	
	/**
	 * Resets the matrix to its default state.<br>
	 * This method calls {@code loadIdentity} to set all values to the identity matrix.<br>
	 * Use this to clear any previous transformations.
	 */
	public void reset()
	{
		loadIdentity();
	}
	
	/**
	 * Creates a new instance of {@link Matrix3f}.
	 * @return A new {@code Matrix3f} object.
	 */
	public static Matrix3f newInstance()
	{
		return new Matrix3f();
	}
	
	/**
	 * Recycles the provided {@code Matrix3f} instance.
	 * @param instance The {@code Matrix3f} object to be recycled.
	 */
	public static void recycle(Matrix3f instance)
	{
		// pooling removed
	}
}
