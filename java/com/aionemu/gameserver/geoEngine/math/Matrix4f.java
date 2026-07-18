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
 * This class defines and maintains a 4x4 matrix used for translation and rotation operations.<br>
 * It provides convenience methods to create matrices from various sources.<br>
 * Note that while the internal storage is column-major, the {@code get()} and {@code set()} methods default to row-major order.
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Matrix4f implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Matrix4f.class);
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;
	public static final Matrix4f IDENTITY = new Matrix4f();
	
	/**
	 * Creates a new instance of {@link Matrix4f}.<br>
	 * This constructor initializes the matrix as an identity matrix.<br>
	 * The resulting matrix is equivalent to {@code Matrix4f.IDENTITY}.
	 */
	public Matrix4f()
	{
		loadIdentity();
	}
	
	/**
	 * Creates a new {@link Matrix4f} instance using individual component values.<br>
	 * The elements are assigned in row-major order.
	 * @param m00 The value for the first row, first column.
	 * @param m01 The value for the first row, second column.
	 * @param m02 The value for the first row, third column.
	 * @param m03 The value for the first row, fourth column.
	 * @param m10 The value for the second row, first column.
	 * @param m11 The value for the second row, second column.
	 * @param m12 The value for the second row, third column.
	 * @param m13 The value for the second row, fourth column.
	 * @param m20 The value for the third row, first column.
	 * @param m21 The value for the third row, second column.
	 * @param m22 The value for the third row, third column.
	 * @param m23 The value for the third row
	 * @param m30
	 * @param m31
	 * @param m32
	 * @param m33
	 */
	public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}
	
	/**
	 * Creates a new {@link Matrix4f} from a float array.<br>
	 * The input array is treated as row major order.<br>
	 * This constructor calls the {@code set} method internally.
	 * @param array A {@code float[]} containing 16 elements to populate the matrix.
	 */
	public Matrix4f(float[] array)
	{
		set(array, false);
	}
	
	/**
	 * Creates a new {@code Matrix4f} instance by copying the data from an existing matrix.<br>
	 * This method uses the {@code copy} method to perform the copy.
	 * @param mat The source {@code Matrix4f} to copy from.
	 */
	public Matrix4f(Matrix4f mat)
	{
		copy(mat);
	}
	
	/**
	 * Copies the values from another <code class="Matrix4f">matrix</code> into this instance.<br>
	 * If the provided <code class="Matrix4f">matrix</code> is <code class="java.lang.Object">null</code>, this method calls {@code loadIdentity}.
	 * @param matrix The source <code class="Matrix4f">matrix</code> to copy from.
	 */
	public void copy(Matrix4f matrix)
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
			m03 = matrix.m03;
			m10 = matrix.m10;
			m11 = matrix.m11;
			m12 = matrix.m12;
			m13 = matrix.m13;
			m20 = matrix.m20;
			m21 = matrix.m21;
			m22 = matrix.m22;
			m23 = matrix.m23;
			m30 = matrix.m30;
			m31 = matrix.m31;
			m32 = matrix.m32;
			m33 = matrix.m33;
		}
	}
	
	/**
	 * Copies the matrix data into a provided array.<br>
	 * This method uses row-major order for the transfer.
	 * @param matrix The {@code float[]} array to store the values in.
	 */
	public void get(float[] matrix)
	{
		get(matrix, true);
	}
	
	/**
	 * Copies the matrix elements into a provided float array.<br>
	 * The order of the copy depends on the rowMajor flag.
	 * @param matrix The destination {@code float[]} array to populate.
	 * @param rowMajor Set to {@code true} for row-major order or {@code false} for column-major order.
	 */
	public void get(float[] matrix, boolean rowMajor)
	{
		if (matrix.length != 16)
		{
			throw new IllegalArgumentException("Array must be of size 16.");
		}
		
		if (rowMajor)
		{
			matrix[0] = m00;
			matrix[1] = m01;
			matrix[2] = m02;
			matrix[3] = m03;
			matrix[4] = m10;
			matrix[5] = m11;
			matrix[6] = m12;
			matrix[7] = m13;
			matrix[8] = m20;
			matrix[9] = m21;
			matrix[10] = m22;
			matrix[11] = m23;
			matrix[12] = m30;
			matrix[13] = m31;
			matrix[14] = m32;
			matrix[15] = m33;
		}
		else
		{
			matrix[0] = m00;
			matrix[4] = m01;
			matrix[8] = m02;
			matrix[12] = m03;
			matrix[1] = m10;
			matrix[5] = m11;
			matrix[9] = m12;
			matrix[13] = m13;
			matrix[2] = m20;
			matrix[6] = m21;
			matrix[10] = m22;
			matrix[14] = m23;
			matrix[3] = m30;
			matrix[7] = m31;
			matrix[11] = m32;
			matrix[15] = m33;
		}
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
					case 3:
						return m03;
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
					case 3:
						return m13;
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
					case 3:
						return m23;
				}
			case 3:
				switch (j)
				{
					case 0:
						return m30;
					case 1:
						return m31;
					case 2:
						return m32;
					case 3:
						return m33;
				}
		}
		
		logger.warn("Invalid matrix index.");
		throw new IllegalArgumentException("Invalid indices into matrix.");
	}
	
	/**
	 * Retrieves a specific column from the matrix.<br>
	 * The result is returned as a new {@code float[]} array.
	 * @param i The index of the column to retrieve.
	 * @return A {@code float[]} containing the values of the requested column.
	 */
	public float[] getColumn(int i)
	{
		return getColumn(i, null);
	}
	
	/**
	 * Retrieves a specific column from the matrix and stores it in an array.<br>
	 * If the provided {@code store} is {@code null}, a new {@code float[]} of size 4 is created.
	 * @param i The index of the column to retrieve.
	 * @param store The array where the column values will be stored.
	 * @return The populated {@code float[]} array containing the column data.
	 */
	public float[] getColumn(int i, float[] store)
	{
		if (store == null)
		{
			store = new float[4];
		}
		
		switch (i)
		{
			case 0:
				store[0] = m00;
				store[1] = m10;
				store[2] = m20;
				store[3] = m30;
				break;
			case 1:
				store[0] = m01;
				store[1] = m11;
				store[2] = m21;
				store[3] = m31;
				break;
			case 2:
				store[0] = m02;
				store[1] = m12;
				store[2] = m22;
				store[3] = m32;
				break;
			case 3:
				store[0] = m03;
				store[1] = m13;
				store[2] = m23;
				store[3] = m33;
				break;
			default:
				logger.warn("Invalid column index.");
				throw new IllegalArgumentException("Invalid column index. " + i);
		}
		
		return store;
	}
	
	/**
	 * Sets the values of a specific column in this matrix.<br>
	 * The method updates four elements based on the provided array.<br>
	 * If the input array is {@code null}, no changes are made.
	 * @param i The index of the column to set. Must be between 0 and 3.
	 * @param column An array containing the four float values for the new column.
	 */
	public void setColumn(int i, float[] column)
	{
		if (column == null)
		{
			logger.warn("Column is null. Ignoring.");
			return;
		}
		
		switch (i)
		{
			case 0:
				m00 = column[0];
				m10 = column[1];
				m20 = column[2];
				m30 = column[3];
				break;
			case 1:
				m01 = column[0];
				m11 = column[1];
				m21 = column[2];
				m31 = column[3];
				break;
			case 2:
				m02 = column[0];
				m12 = column[1];
				m22 = column[2];
				m32 = column[3];
				break;
			case 3:
				m03 = column[0];
				m13 = column[1];
				m23 = column[2];
				m33 = column[3];
				break;
			default:
				logger.warn("Invalid column index.");
				throw new IllegalArgumentException("Invalid column index. " + i);
		}
	}
	
	/**
	 * Sets a specific element in the matrix.<br>
	 * This method updates the value at the given row and column.
	 * @param i The row index ranging from {@code 0} to {@code 3}.
	 * @param j The column index ranging from {@code 0} to {@code 3}.
	 * @param value The new {@code float} value to assign.
	 */
	public void set(int i, int j, float value)
	{
		switch (i)
		{
			case 0:
				switch (j)
				{
					case 0:
						m00 = value;
						return;
					case 1:
						m01 = value;
						return;
					case 2:
						m02 = value;
						return;
					case 3:
						m03 = value;
						return;
				}
			case 1:
				switch (j)
				{
					case 0:
						m10 = value;
						return;
					case 1:
						m11 = value;
						return;
					case 2:
						m12 = value;
						return;
					case 3:
						m13 = value;
						return;
				}
			case 2:
				switch (j)
				{
					case 0:
						m20 = value;
						return;
					case 1:
						m21 = value;
						return;
					case 2:
						m22 = value;
						return;
					case 3:
						m23 = value;
						return;
				}
			case 3:
				switch (j)
				{
					case 0:
						m30 = value;
						return;
					case 1:
						m31 = value;
						return;
					case 2:
						m32 = value;
						return;
					case 3:
						m33 = value;
						return;
				}
		}
		
		logger.warn("Invalid matrix index.");
		throw new IllegalArgumentException("Invalid indices into matrix.");
	}
	
	/**
	 * Updates the current matrix values using a 2D array.<br>
	 * The input {@code float[][]} must be of size 4x4.<br>
	 * This method assumes row major order for the input data.
	 * @param matrix A 2D array containing the new matrix values.
	 */
	public void set(float[][] matrix)
	{
		if ((matrix.length != 4) || (matrix[0].length != 4))
		{
			throw new IllegalArgumentException("Array must be of size 16.");
		}
		
		m00 = matrix[0][0];
		m01 = matrix[0][1];
		m02 = matrix[0][2];
		m03 = matrix[0][3];
		m10 = matrix[1][0];
		m11 = matrix[1][1];
		m12 = matrix[1][2];
		m13 = matrix[1][3];
		m20 = matrix[2][0];
		m21 = matrix[2][1];
		m22 = matrix[2][2];
		m23 = matrix[2][3];
		m30 = matrix[3][0];
		m31 = matrix[3][1];
		m32 = matrix[3][2];
		m33 = matrix[3][3];
	}
	
	/**
	 * Copies the values from another {@link Matrix4f} into this instance.<br>
	 * This method updates all internal components of the current matrix.
	 * @param matrix The source {@code Matrix4f} to copy from.
	 * @return The current {@code Matrix4f} instance for method chaining.
	 */
	public Matrix4f set(Matrix4f matrix)
	{
		m00 = matrix.m00;
		m01 = matrix.m01;
		m02 = matrix.m02;
		m03 = matrix.m03;
		m10 = matrix.m10;
		m11 = matrix.m11;
		m12 = matrix.m12;
		m13 = matrix.m13;
		m20 = matrix.m20;
		m21 = matrix.m21;
		m22 = matrix.m22;
		m23 = matrix.m23;
		m30 = matrix.m30;
		m31 = matrix.m31;
		m32 = matrix.m32;
		m33 = matrix.m33;
		return this;
	}
	
	/**
	 * Updates the internal matrix values from a provided array.<br>
	 * This method assumes the input array is in row-major order.
	 * @param matrix The {@code float[]} containing the new matrix data.
	 */
	public void set(float[] matrix)
	{
		set(matrix, true);
	}
	
	/**
	 * Updates the internal values of this matrix using a provided array.<br>
	 * This method supports both row-major and column-major input formats.
	 * @param matrix The {@code float[]} containing 16 elements to copy into this matrix.
	 * @param rowMajor Set to {@code true} if the input array is in row-major order, or {@code false} for column-major order.
	 */
	public void set(float[] matrix, boolean rowMajor)
	{
		if (matrix.length != 16)
		{
			throw new IllegalArgumentException("Array must be of size 16.");
		}
		
		if (rowMajor)
		{
			m00 = matrix[0];
			m01 = matrix[1];
			m02 = matrix[2];
			m03 = matrix[3];
			m10 = matrix[4];
			m11 = matrix[5];
			m12 = matrix[6];
			m13 = matrix[7];
			m20 = matrix[8];
			m21 = matrix[9];
			m22 = matrix[10];
			m23 = matrix[11];
			m30 = matrix[12];
			m31 = matrix[13];
			m32 = matrix[14];
			m33 = matrix[15];
		}
		else
		{
			m00 = matrix[0];
			m01 = matrix[4];
			m02 = matrix[8];
			m03 = matrix[12];
			m10 = matrix[1];
			m11 = matrix[5];
			m12 = matrix[9];
			m13 = matrix[13];
			m20 = matrix[2];
			m21 = matrix[6];
			m22 = matrix[10];
			m23 = matrix[14];
			m30 = matrix[3];
			m31 = matrix[7];
			m32 = matrix[11];
			m33 = matrix[15];
		}
	}
	
	/**
	 * Creates a new {@code Matrix4f} that is the transpose of this matrix.<br>
	 * The operation flips the matrix over its main diagonal.<br>
	 * This method returns a new instance and does not modify the original matrix.
	 * @return A new {@code Matrix4f} representing the transposed matrix.
	 */
	public Matrix4f transpose()
	{
		final float[] tmp = new float[16];
		get(tmp, true);
		final Matrix4f mat = new Matrix4f(tmp);
		return mat;
	}
	
	/**
	 * Transposes the current matrix in place.<br>
	 * This method swaps the rows and columns of the {@code Matrix4f}.<br>
	 * It modifies the existing object instead of creating a new one.
	 * @return The current {@code Matrix4f} instance after transposition.
	 */
	public Matrix4f transposeLocal()
	{
		float tmp = m01;
		m01 = m10;
		m10 = tmp;
		
		tmp = m02;
		m02 = m20;
		m20 = tmp;
		
		tmp = m03;
		m03 = m30;
		m30 = tmp;
		
		tmp = m12;
		m12 = m21;
		m21 = tmp;
		
		tmp = m13;
		m13 = m31;
		m31 = tmp;
		
		tmp = m23;
		m23 = m32;
		m32 = tmp;
		
		return this;
	}
	
	/**
	 * Converts the current matrix data into a {@code FloatBuffer}.<br>
	 * The buffer contains 16 elements in row-major order.
	 * @return A new {@code FloatBuffer} containing the matrix values.
	 */
	public FloatBuffer toFloatBuffer()
	{
		return toFloatBuffer(false);
	}
	
	/**
	 * Converts the matrix data into a {@code FloatBuffer}.<br>
	 * This method allows you to choose the memory layout of the resulting buffer.
	 * @param columnMajor Set to {@code true} for column-major order or {@code false} for row-major order.
	 * @return A new {@code FloatBuffer} containing the matrix elements.
	 */
	public FloatBuffer toFloatBuffer(boolean columnMajor)
	{
		final FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		fillFloatBuffer(fb, columnMajor);
		fb.rewind();
		return fb;
	}
	
	/**
	 * Fills an existing {@code FloatBuffer} with the matrix data.<br>
	 * This method uses row-major order by default.
	 * @param fb The {@code FloatBuffer} to be populated.
	 * @return The same {@code FloatBuffer} instance after it has been filled.
	 */
	public FloatBuffer fillFloatBuffer(FloatBuffer fb)
	{
		return fillFloatBuffer(fb, false);
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
			fb.put(m00).put(m10).put(m20).put(m30);
			fb.put(m01).put(m11).put(m21).put(m31);
			fb.put(m02).put(m12).put(m22).put(m32);
			fb.put(m03).put(m13).put(m23).put(m33);
		}
		else
		{
			fb.put(m00).put(m01).put(m02).put(m03);
			fb.put(m10).put(m11).put(m12).put(m13);
			fb.put(m20).put(m21).put(m22).put(m23);
			fb.put(m30).put(m31).put(m32).put(m33);
		}
		
		return fb;
	}
	
	/**
	 * Fills an external array with the values of this matrix.<br>
	 * The order of elements depends on the {@code columnMajor} flag.<br>
	 * If {@code true}, it uses column-major order.<br>
	 * If {@code false}, it uses row-major order.
	 * @param f The destination {@code float[]} array to populate.
	 * @param columnMajor A boolean indicating whether to use column-major or row-major layout.
	 */
	public void fillFloatArray(float[] f, boolean columnMajor)
	{
		if (columnMajor)
		{
			f[0] = m00;
			f[1] = m10;
			f[2] = m20;
			f[3] = m30;
			f[4] = m01;
			f[5] = m11;
			f[6] = m21;
			f[7] = m31;
			f[8] = m02;
			f[9] = m12;
			f[10] = m22;
			f[11] = m32;
			f[12] = m03;
			f[13] = m13;
			f[14] = m23;
			f[15] = m33;
		}
		else
		{
			f[0] = m00;
			f[1] = m01;
			f[2] = m02;
			f[3] = m03;
			f[4] = m10;
			f[5] = m11;
			f[6] = m12;
			f[7] = m13;
			f[8] = m20;
			f[9] = m21;
			f[10] = m22;
			f[11] = m23;
			f[12] = m30;
			f[13] = m31;
			f[14] = m32;
			f[15] = m33;
		}
	}
	
	/**
	 * Creates a new {@link Matrix4f} from the provided {@code FloatBuffer}.<br>
	 * This method assumes the buffer is in row-major order.
	 * @param fb The {@code FloatBuffer} containing the matrix data.
	 * @return A new {@link Matrix4f} instance populated with the buffer values.
	 */
	public Matrix4f readFloatBuffer(FloatBuffer fb)
	{
		return readFloatBuffer(fb, false);
	}
	
	/**
	 * Creates a new {@link Matrix4f} from the data in a {@code FloatBuffer}.<br>
	 * The method populates the matrix elements based on the specified storage order.
	 * @param fb The buffer containing the 16 float values.
	 * @param columnMajor Set to {@code true} if the buffer is in column-major order, or {@code false} for row-major order.
	 * @return This {@link Matrix4f} instance.
	 */
	public Matrix4f readFloatBuffer(FloatBuffer fb, boolean columnMajor)
	{
		if (columnMajor)
		{
			m00 = fb.get();
			m10 = fb.get();
			m20 = fb.get();
			m30 = fb.get();
			m01 = fb.get();
			m11 = fb.get();
			m21 = fb.get();
			m31 = fb.get();
			m02 = fb.get();
			m12 = fb.get();
			m22 = fb.get();
			m32 = fb.get();
			m03 = fb.get();
			m13 = fb.get();
			m23 = fb.get();
			m33 = fb.get();
		}
		else
		{
			m00 = fb.get();
			m01 = fb.get();
			m02 = fb.get();
			m03 = fb.get();
			m10 = fb.get();
			m11 = fb.get();
			m12 = fb.get();
			m13 = fb.get();
			m20 = fb.get();
			m21 = fb.get();
			m22 = fb.get();
			m23 = fb.get();
			m30 = fb.get();
			m31 = fb.get();
			m32 = fb.get();
			m33 = fb.get();
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
		m01 = m02 = m03 = 0.0f;
		m10 = m12 = m13 = 0.0f;
		m20 = m21 = m23 = 0.0f;
		m30 = m31 = m32 = 0.0f;
		m00 = m11 = m22 = m33 = 1.0f;
	}
	
	/**
	 * Sets the matrix values based on a view frustum.<br>
	 * This method calculates the projection matrix using the provided boundaries.<br>
	 * It handles both parallel and perspective projections.
	 * @param near The distance to the near clipping plane.
	 * @param far The distance to the far clipping plane.
	 * @param left The x-coordinate of the left clipping plane.
	 * @param right The x-coordinate of the right clipping plane.
	 * @param top The y-coordinate of the top clipping plane.
	 * @param bottom The y-coordinate of the bottom clipping plane.
	 * @param parallel A boolean indicating if the projection is parallel.
	 */
	public void fromFrustum(float near, float far, float left, float right, float top, float bottom, boolean parallel)
	{
		loadIdentity();
		if (parallel)
		{
			// scale
			m00 = 2.0f / (right - left);
			
			// m11 = 2.0f / (bottom - top);
			m11 = 2.0f / (top - bottom);
			m22 = -2.0f / (far - near);
			m33 = 1f;
			
			// translation
			m03 = -(right + left) / (right - left);
			
			// m31 = -(bottom + top) / (bottom - top);
			m13 = -(top + bottom) / (top - bottom);
			m23 = -(far + near) / (far - near);
		}
		else
		{
			m00 = (2.0f * near) / (right - left);
			m11 = (2.0f * near) / (top - bottom);
			m32 = -1.0f;
			m33 = -0.0f;
			
			// A
			m02 = (right + left) / (right - left);
			
			// B
			m12 = (top + bottom) / (top - bottom);
			
			// C
			m22 = -(far + near) / (far - near);
			
			// D
			m23 = -(2.0f * far * near) / (far - near);
		}
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
		zero();
		m33 = 1;
		
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
	 * Multiplies every element of this matrix by a given value.<br>
	 * This operation modifies the current instance directly.
	 * @param scalar The {@code float} value to multiply each element by.
	 */
	public void multLocal(float scalar)
	{
		m00 *= scalar;
		m01 *= scalar;
		m02 *= scalar;
		m03 *= scalar;
		m10 *= scalar;
		m11 *= scalar;
		m12 *= scalar;
		m13 *= scalar;
		m20 *= scalar;
		m21 *= scalar;
		m22 *= scalar;
		m23 *= scalar;
		m30 *= scalar;
		m31 *= scalar;
		m32 *= scalar;
		m33 *= scalar;
	}
	
	/**
	 * Multiplies this matrix by a scalar value.<br>
	 * This method returns a new {@link Matrix4f} instance.<br>
	 * The original matrix remains unchanged.
	 * @param scalar The value to multiply the matrix by.
	 * @return A new {@code Matrix4f} resulting from the multiplication.
	 */
	public Matrix4f mult(float scalar)
	{
		final Matrix4f out = new Matrix4f();
		out.set(this);
		out.multLocal(scalar);
		return out;
	}
	
	/**
	 * Multiplies this matrix by a scalar and stores the result in another matrix.<br>
	 * This method copies the current values into {@code store}.<br>
	 * It then scales those values by the provided {@code scalar}.
	 * @param scalar The value to multiply the matrix elements by.
	 * @param store The {@link Matrix4f} where the result will be saved.
	 * @return The modified {@code store} matrix.
	 */
	public Matrix4f mult(float scalar, Matrix4f store)
	{
		store.set(this);
		store.multLocal(scalar);
		return store;
	}
	
	/**
	 * Multiplies this matrix by another matrix.<br>
	 * The result is a new {@code Matrix4f} object.
	 * @param in2 The second matrix to multiply by.
	 * @return A new {@code Matrix4f} representing the product.
	 */
	public Matrix4f mult(Matrix4f in2)
	{
		return mult(in2, null);
	}
	
	/**
	 * Multiplies this matrix by another matrix.<br>
	 * The result is stored in the provided destination matrix.<br>
	 * If the destination is {@code null}, a new {@link Matrix4f} is created.
	 * @param in2 The second matrix to multiply by.
	 * @param store The matrix where the result will be saved.
	 * @return The resulting {@link Matrix4f}.
	 */
	public Matrix4f mult(Matrix4f in2, Matrix4f store)
	{
		if (store == null)
		{
			store = new Matrix4f();
		}
		
		float temp00, temp01, temp02, temp03;
		float temp10, temp11, temp12, temp13;
		float temp20, temp21, temp22, temp23;
		float temp30, temp31, temp32, temp33;
		
		temp00 = (m00 * in2.m00) + (m01 * in2.m10) + (m02 * in2.m20) + (m03 * in2.m30);
		temp01 = (m00 * in2.m01) + (m01 * in2.m11) + (m02 * in2.m21) + (m03 * in2.m31);
		temp02 = (m00 * in2.m02) + (m01 * in2.m12) + (m02 * in2.m22) + (m03 * in2.m32);
		temp03 = (m00 * in2.m03) + (m01 * in2.m13) + (m02 * in2.m23) + (m03 * in2.m33);
		
		temp10 = (m10 * in2.m00) + (m11 * in2.m10) + (m12 * in2.m20) + (m13 * in2.m30);
		temp11 = (m10 * in2.m01) + (m11 * in2.m11) + (m12 * in2.m21) + (m13 * in2.m31);
		temp12 = (m10 * in2.m02) + (m11 * in2.m12) + (m12 * in2.m22) + (m13 * in2.m32);
		temp13 = (m10 * in2.m03) + (m11 * in2.m13) + (m12 * in2.m23) + (m13 * in2.m33);
		
		temp20 = (m20 * in2.m00) + (m21 * in2.m10) + (m22 * in2.m20) + (m23 * in2.m30);
		temp21 = (m20 * in2.m01) + (m21 * in2.m11) + (m22 * in2.m21) + (m23 * in2.m31);
		temp22 = (m20 * in2.m02) + (m21 * in2.m12) + (m22 * in2.m22) + (m23 * in2.m32);
		temp23 = (m20 * in2.m03) + (m21 * in2.m13) + (m22 * in2.m23) + (m23 * in2.m33);
		
		temp30 = (m30 * in2.m00) + (m31 * in2.m10) + (m32 * in2.m20) + (m33 * in2.m30);
		temp31 = (m30 * in2.m01) + (m31 * in2.m11) + (m32 * in2.m21) + (m33 * in2.m31);
		temp32 = (m30 * in2.m02) + (m31 * in2.m12) + (m32 * in2.m22) + (m33 * in2.m32);
		temp33 = (m30 * in2.m03) + (m31 * in2.m13) + (m32 * in2.m23) + (m33 * in2.m33);
		
		store.m00 = temp00;
		store.m01 = temp01;
		store.m02 = temp02;
		store.m03 = temp03;
		store.m10 = temp10;
		store.m11 = temp11;
		store.m12 = temp12;
		store.m13 = temp13;
		store.m20 = temp20;
		store.m21 = temp21;
		store.m22 = temp22;
		store.m23 = temp23;
		store.m30 = temp30;
		store.m31 = temp31;
		store.m32 = temp32;
		store.m33 = temp33;
		
		return store;
	}
	
	/**
	 * Multiplies this matrix by another matrix.<br>
	 * This method performs a local multiplication where the current matrix is the second operand.<br>
	 * It returns a new {@link Matrix4f} instance representing the result.
	 * @param in2 The matrix to multiply with.
	 * @return A new {@code Matrix4f} containing the product of {@code in2} and this matrix.
	 */
	public Matrix4f multLocal(Matrix4f in2)
	{
		return mult(in2, this);
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
	 * Multiplies this matrix by a {@code Vector3f} and stores the result.<br>
	 * The calculation applies the transformation to the provided vector.<br>
	 * If the {@code store} parameter is {@code null}, a new {@code Vector3f} is created.
	 * @param vec The input vector to be transformed.
	 * @param store The destination vector where the result will be saved.
	 * @return The resulting {@code Vector3f} after multiplication.
	 */
	public Vector3f mult(Vector3f vec, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = (m00 * vx) + (m01 * vy) + (m02 * vz) + m03;
		store.y = (m10 * vx) + (m11 * vy) + (m12 * vz) + m13;
		store.z = (m20 * vx) + (m21 * vy) + (m22 * vz) + m23;
		
		return store;
	}
	
	/**
	 * Multiplies a {@code Vector3f} by this matrix.<br>
	 * The result is stored in the provided {@code Vector3f} object.<br>
	 * If the {@code store} parameter is {@code null}, a new instance is created.
	 * @param vec The input vector to multiply.
	 * @param store The vector where the result will be saved.
	 * @return The resulting {@code Vector3f}.
	 */
	public Vector3f multNormal(Vector3f vec, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = (m00 * vx) + (m01 * vy) + (m02 * vz);
		store.y = (m10 * vx) + (m11 * vy) + (m12 * vz);
		store.z = (m20 * vx) + (m21 * vy) + (m22 * vz);
		
		return store;
	}
	
	/**
	 * Multiplies the current matrix by a {@code Vector3f} and stores the result.<br>
	 * This method performs a transformation using the first three columns of this matrix.<br>
	 * If the provided {@code store} is {@code null}, a new {@code Vector3f} is created.
	 * @param vec The input vector to be transformed.
	 * @param store The destination vector where the result will be saved.
	 * @return The resulting {@code Vector3f} after multiplication.
	 */
	public Vector3f multNormalAcross(Vector3f vec, Vector3f store)
	{
		if (store == null)
		{
			store = new Vector3f();
		}
		
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = (m00 * vx) + (m10 * vy) + (m20 * vz);
		store.y = (m01 * vx) + (m11 * vy) + (m21 * vz);
		store.z = (m02 * vx) + (m12 * vy) + (m22 * vz);
		
		return store;
	}
	
	/**
	 * Multiplies a {@code Vector3f} by this matrix.<br>
	 * The result is stored in the provided {@code store} vector.<br>
	 * The fourth component of the result is returned as a float.
	 * @param vec The input {@code Vector3f} to multiply.
	 * @param store The {@code Vector3f} where the resulting x, y, and z coordinates will be saved.
	 * @return The w component of the resulting vector.
	 */
	public float multProj(Vector3f vec, Vector3f store)
	{
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = (m00 * vx) + (m01 * vy) + (m02 * vz) + m03;
		store.y = (m10 * vx) + (m11 * vy) + (m12 * vz) + m13;
		store.z = (m20 * vx) + (m21 * vy) + (m22 * vz) + m23;
		return (m30 * vx) + (m31 * vy) + (m32 * vz) + m33;
	}
	
	/**
	 * Multiplies the current matrix by a vector.<br>
	 * The result is stored in the provided {@code Vector3f} object.<br>
	 * If the {@code store} parameter is {@code null}, a new {@code Vector3f} is created.
	 * @param vec The input vector to multiply.
	 * @param store The vector where the result will be saved.
	 * @return The resulting {@code Vector3f} or {@code null} if the input vector is {@code null}.
	 */
	public Vector3f multAcross(Vector3f vec, Vector3f store)
	{
		if (null == vec)
		{
			logger.info("Source vector is null, null result returned.");
			return null;
		}
		
		if (store == null)
		{
			store = new Vector3f();
		}
		
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = (m00 * vx) + (m10 * vy) + (m20 * vz) + (m30 * 1);
		store.y = (m01 * vx) + (m11 * vy) + (m21 * vz) + (m31 * 1);
		store.z = (m02 * vx) + (m12 * vy) + (m22 * vz) + (m32 * 1);
		
		return store;
	}
	
	/**
	 * Multiplies this matrix by a 4D vector.<br>
	 * The calculation modifies the input array directly.
	 * @param vec4f A {@code float[]} array of length 4 representing the vector.
	 * @return The modified {@code float[]} array or {@code null} if the input is invalid.
	 */
	public float[] mult(float[] vec4f)
	{
		if ((null == vec4f) || (vec4f.length != 4))
		{
			logger.warn("invalid array given, must be nonnull and length 4");
			return null;
		}
		
		final float x = vec4f[0], y = vec4f[1], z = vec4f[2], w = vec4f[3];
		
		vec4f[0] = (m00 * x) + (m01 * y) + (m02 * z) + (m03 * w);
		vec4f[1] = (m10 * x) + (m11 * y) + (m12 * z) + (m13 * w);
		vec4f[2] = (m20 * x) + (m21 * y) + (m22 * z) + (m23 * w);
		vec4f[3] = (m30 * x) + (m31 * y) + (m32 * z) + (m33 * w);
		
		return vec4f;
	}
	
	/**
	 * Multiplies this matrix by a 4D vector.<br>
	 * The calculation is performed across the rows of the matrix.<br>
	 * This method modifies the input array in place.
	 * @param vec4f A {@code float[]} array containing four elements.
	 * @return The modified {@code float[]} array or {@code null} if the input is invalid.
	 */
	public float[] multAcross(float[] vec4f)
	{
		if ((null == vec4f) || (vec4f.length != 4))
		{
			logger.warn("invalid array given, must be nonnull and length 4");
			return null;
		}
		
		final float x = vec4f[0], y = vec4f[1], z = vec4f[2], w = vec4f[3];
		
		vec4f[0] = (m00 * x) + (m10 * y) + (m20 * z) + (m30 * w);
		vec4f[1] = (m01 * x) + (m11 * y) + (m21 * z) + (m31 * w);
		vec4f[2] = (m02 * x) + (m12 * y) + (m22 * z) + (m32 * w);
		vec4f[3] = (m03 * x) + (m13 * y) + (m23 * z) + (m33 * w);
		
		return vec4f;
	}
	
	/**
	 * Calculates the inverse of this matrix.<br>
	 * This method returns a new {@link Matrix4f} instance.<br>
	 * The resulting matrix is the multiplicative inverse of the current one.
	 * @return A new {@code Matrix4f} representing the inverted matrix.
	 */
	public Matrix4f invert()
	{
		return invert(null);
	}
	
	/**
	 * Calculates the inverse of this matrix.<br>
	 * The result is stored in the provided {@code Matrix4f} object.<br>
	 * If the input matrix is not invertible, an {@code ArithmeticException} is thrown.
	 * @param store The {@code Matrix4f} where the result will be stored. If {@code null}, a new instance is created.
	 * @return The resulting inverted {@code Matrix4f}.
	 */
	public Matrix4f invert(Matrix4f store)
	{
		if (store == null)
		{
			store = new Matrix4f();
		}
		
		final float fA0 = (m00 * m11) - (m01 * m10);
		final float fA1 = (m00 * m12) - (m02 * m10);
		final float fA2 = (m00 * m13) - (m03 * m10);
		final float fA3 = (m01 * m12) - (m02 * m11);
		final float fA4 = (m01 * m13) - (m03 * m11);
		final float fA5 = (m02 * m13) - (m03 * m12);
		final float fB0 = (m20 * m31) - (m21 * m30);
		final float fB1 = (m20 * m32) - (m22 * m30);
		final float fB2 = (m20 * m33) - (m23 * m30);
		final float fB3 = (m21 * m32) - (m22 * m31);
		final float fB4 = (m21 * m33) - (m23 * m31);
		final float fB5 = (m22 * m33) - (m23 * m32);
		final float fDet = ((((fA0 * fB5) - (fA1 * fB4)) + (fA2 * fB3) + (fA3 * fB2)) - (fA4 * fB1)) + (fA5 * fB0);
		
		if (FastMath.abs(fDet) <= 0f)
		{
			throw new ArithmeticException("This matrix cannot be inverted");
		}
		
		store.m00 = ((+m11 * fB5) - (m12 * fB4)) + (m13 * fB3);
		store.m10 = ((-m10 * fB5) + (m12 * fB2)) - (m13 * fB1);
		store.m20 = ((+m10 * fB4) - (m11 * fB2)) + (m13 * fB0);
		store.m30 = ((-m10 * fB3) + (m11 * fB1)) - (m12 * fB0);
		store.m01 = ((-m01 * fB5) + (m02 * fB4)) - (m03 * fB3);
		store.m11 = ((+m00 * fB5) - (m02 * fB2)) + (m03 * fB1);
		store.m21 = ((-m00 * fB4) + (m01 * fB2)) - (m03 * fB0);
		store.m31 = ((+m00 * fB3) - (m01 * fB1)) + (m02 * fB0);
		store.m02 = ((+m31 * fA5) - (m32 * fA4)) + (m33 * fA3);
		store.m12 = ((-m30 * fA5) + (m32 * fA2)) - (m33 * fA1);
		store.m22 = ((+m30 * fA4) - (m31 * fA2)) + (m33 * fA0);
		store.m32 = ((-m30 * fA3) + (m31 * fA1)) - (m32 * fA0);
		store.m03 = ((-m21 * fA5) + (m22 * fA4)) - (m23 * fA3);
		store.m13 = ((+m20 * fA5) - (m22 * fA2)) + (m23 * fA1);
		store.m23 = ((-m20 * fA4) + (m21 * fA2)) - (m23 * fA0);
		store.m33 = ((+m20 * fA3) - (m21 * fA1)) + (m22 * fA0);
		
		final float fInvDet = 1.0f / fDet;
		store.multLocal(fInvDet);
		
		return store;
	}
	
	/**
	 * Inverts the current matrix and stores the result in this instance.<br>
	 * This method modifies the internal values of the {@code Matrix4f}.<br>
	 * If the matrix is not invertible, it returns a zero matrix.
	 * @return The current {@code Matrix4f} instance after inversion.
	 */
	public Matrix4f invertLocal()
	{
		final float fA0 = (m00 * m11) - (m01 * m10);
		final float fA1 = (m00 * m12) - (m02 * m10);
		final float fA2 = (m00 * m13) - (m03 * m10);
		final float fA3 = (m01 * m12) - (m02 * m11);
		final float fA4 = (m01 * m13) - (m03 * m11);
		final float fA5 = (m02 * m13) - (m03 * m12);
		final float fB0 = (m20 * m31) - (m21 * m30);
		final float fB1 = (m20 * m32) - (m22 * m30);
		final float fB2 = (m20 * m33) - (m23 * m30);
		final float fB3 = (m21 * m32) - (m22 * m31);
		final float fB4 = (m21 * m33) - (m23 * m31);
		final float fB5 = (m22 * m33) - (m23 * m32);
		final float fDet = ((((fA0 * fB5) - (fA1 * fB4)) + (fA2 * fB3) + (fA3 * fB2)) - (fA4 * fB1)) + (fA5 * fB0);
		
		if (FastMath.abs(fDet) <= 0f)
		{
			return zero();
		}
		
		final float f00 = ((+m11 * fB5) - (m12 * fB4)) + (m13 * fB3);
		final float f10 = ((-m10 * fB5) + (m12 * fB2)) - (m13 * fB1);
		final float f20 = ((+m10 * fB4) - (m11 * fB2)) + (m13 * fB0);
		final float f30 = ((-m10 * fB3) + (m11 * fB1)) - (m12 * fB0);
		final float f01 = ((-m01 * fB5) + (m02 * fB4)) - (m03 * fB3);
		final float f11 = ((+m00 * fB5) - (m02 * fB2)) + (m03 * fB1);
		final float f21 = ((-m00 * fB4) + (m01 * fB2)) - (m03 * fB0);
		final float f31 = ((+m00 * fB3) - (m01 * fB1)) + (m02 * fB0);
		final float f02 = ((+m31 * fA5) - (m32 * fA4)) + (m33 * fA3);
		final float f12 = ((-m30 * fA5) + (m32 * fA2)) - (m33 * fA1);
		final float f22 = ((+m30 * fA4) - (m31 * fA2)) + (m33 * fA0);
		final float f32 = ((-m30 * fA3) + (m31 * fA1)) - (m32 * fA0);
		final float f03 = ((-m21 * fA5) + (m22 * fA4)) - (m23 * fA3);
		final float f13 = ((+m20 * fA5) - (m22 * fA2)) + (m23 * fA1);
		final float f23 = ((-m20 * fA4) + (m21 * fA2)) - (m23 * fA0);
		final float f33 = ((+m20 * fA3) - (m21 * fA1)) + (m22 * fA0);
		
		m00 = f00;
		m01 = f01;
		m02 = f02;
		m03 = f03;
		m10 = f10;
		m11 = f11;
		m12 = f12;
		m13 = f13;
		m20 = f20;
		m21 = f21;
		m22 = f22;
		m23 = f23;
		m30 = f30;
		m31 = f31;
		m32 = f32;
		m33 = f33;
		
		final float fInvDet = 1.0f / fDet;
		multLocal(fInvDet);
		
		return this;
	}
	
	/**
	 * Calculates the adjoint of this matrix.<br>
	 * This method returns a new {@link Matrix4f} instance.<br>
	 * It is used for calculating the inverse of a transformation matrix.
	 * @return A new {@code Matrix4f} representing the adjoint.
	 */
	public Matrix4f adjoint()
	{
		return adjoint(null);
	}
	
	/**
	 * Calculates the adjoint of this matrix.<br>
	 * The result is stored in the provided {@code Matrix4f} object.<br>
	 * If the input is {@code null}, a new instance is created.
	 * @param store The {@code Matrix4f} to store the result in.
	 * @return The resulting {@code Matrix4f} containing the adjoint.
	 */
	public Matrix4f adjoint(Matrix4f store)
	{
		if (store == null)
		{
			store = new Matrix4f();
		}
		
		final float fA0 = (m00 * m11) - (m01 * m10);
		final float fA1 = (m00 * m12) - (m02 * m10);
		final float fA2 = (m00 * m13) - (m03 * m10);
		final float fA3 = (m01 * m12) - (m02 * m11);
		final float fA4 = (m01 * m13) - (m03 * m11);
		final float fA5 = (m02 * m13) - (m03 * m12);
		final float fB0 = (m20 * m31) - (m21 * m30);
		final float fB1 = (m20 * m32) - (m22 * m30);
		final float fB2 = (m20 * m33) - (m23 * m30);
		final float fB3 = (m21 * m32) - (m22 * m31);
		final float fB4 = (m21 * m33) - (m23 * m31);
		final float fB5 = (m22 * m33) - (m23 * m32);
		
		store.m00 = ((+m11 * fB5) - (m12 * fB4)) + (m13 * fB3);
		store.m10 = ((-m10 * fB5) + (m12 * fB2)) - (m13 * fB1);
		store.m20 = ((+m10 * fB4) - (m11 * fB2)) + (m13 * fB0);
		store.m30 = ((-m10 * fB3) + (m11 * fB1)) - (m12 * fB0);
		store.m01 = ((-m01 * fB5) + (m02 * fB4)) - (m03 * fB3);
		store.m11 = ((+m00 * fB5) - (m02 * fB2)) + (m03 * fB1);
		store.m21 = ((-m00 * fB4) + (m01 * fB2)) - (m03 * fB0);
		store.m31 = ((+m00 * fB3) - (m01 * fB1)) + (m02 * fB0);
		store.m02 = ((+m31 * fA5) - (m32 * fA4)) + (m33 * fA3);
		store.m12 = ((-m30 * fA5) + (m32 * fA2)) - (m33 * fA1);
		store.m22 = ((+m30 * fA4) - (m31 * fA2)) + (m33 * fA0);
		store.m32 = ((-m30 * fA3) + (m31 * fA1)) - (m32 * fA0);
		store.m03 = ((-m21 * fA5) + (m22 * fA4)) - (m23 * fA3);
		store.m13 = ((+m20 * fA5) - (m22 * fA2)) + (m23 * fA1);
		store.m23 = ((-m20 * fA4) + (m21 * fA2)) - (m23 * fA0);
		store.m33 = ((+m20 * fA3) - (m21 * fA1)) + (m22 * fA0);
		
		return store;
	}
	
	/**
	 * Calculates the determinant of the current 4x4 matrix.<br>
	 * This value is used to determine scaling and orientation properties.
	 * @return The calculated determinant as a {@code float}.
	 */
	public float determinant()
	{
		final float fA0 = (m00 * m11) - (m01 * m10);
		final float fA1 = (m00 * m12) - (m02 * m10);
		final float fA2 = (m00 * m13) - (m03 * m10);
		final float fA3 = (m01 * m12) - (m02 * m11);
		final float fA4 = (m01 * m13) - (m03 * m11);
		final float fA5 = (m02 * m13) - (m03 * m12);
		final float fB0 = (m20 * m31) - (m21 * m30);
		final float fB1 = (m20 * m32) - (m22 * m30);
		final float fB2 = (m20 * m33) - (m23 * m30);
		final float fB3 = (m21 * m32) - (m22 * m31);
		final float fB4 = (m21 * m33) - (m23 * m31);
		final float fB5 = (m22 * m33) - (m23 * m32);
		final float fDet = ((((fA0 * fB5) - (fA1 * fB4)) + (fA2 * fB3) + (fA3 * fB2)) - (fA4 * fB1)) + (fA5 * fB0);
		return fDet;
	}
	
	/**
	 * Creates a new <code class="Matrix4f"> where all elements are set to <code class="float">0.0f</code>.<br>
	 * This method resets the current matrix instance.
	 * @return the current <code class="Matrix4f"> instance
	 */
	public Matrix4f zero()
	{
		m00 = m01 = m02 = m03 = 0.0f;
		m10 = m11 = m12 = m13 = 0.0f;
		m20 = m21 = m22 = m23 = 0.0f;
		m30 = m31 = m32 = m33 = 0.0f;
		return this;
	}
	
	/**
	 * Adds this matrix to another matrix.<br>
	 * This method returns a new {@code Matrix4f} instance containing the sum of the two matrices.
	 * @param mat The matrix to add to this one.
	 * @return A new {@code Matrix4f} representing the sum.
	 */
	public Matrix4f add(Matrix4f mat)
	{
		final Matrix4f result = new Matrix4f();
		result.m00 = m00 + mat.m00;
		result.m01 = m01 + mat.m01;
		result.m02 = m02 + mat.m02;
		result.m03 = m03 + mat.m03;
		result.m10 = m10 + mat.m10;
		result.m11 = m11 + mat.m11;
		result.m12 = m12 + mat.m12;
		result.m13 = m13 + mat.m13;
		result.m20 = m20 + mat.m20;
		result.m21 = m21 + mat.m21;
		result.m22 = m22 + mat.m22;
		result.m23 = m23 + mat.m23;
		result.m30 = m30 + mat.m30;
		result.m31 = m31 + mat.m31;
		result.m32 = m32 + mat.m32;
		result.m33 = m33 + mat.m33;
		return result;
	}
	
	/**
	 * Adds the values of another matrix to this one.<br>
	 * This operation modifies the current instance by summing each corresponding element.
	 * @param mat The {@code Matrix4f} to add to the current matrix.
	 */
	public void addLocal(Matrix4f mat)
	{
		m00 += mat.m00;
		m01 += mat.m01;
		m02 += mat.m02;
		m03 += mat.m03;
		m10 += mat.m10;
		m11 += mat.m11;
		m12 += mat.m12;
		m13 += mat.m13;
		m20 += mat.m20;
		m21 += mat.m21;
		m22 += mat.m22;
		m23 += mat.m23;
		m30 += mat.m30;
		m31 += mat.m31;
		m32 += mat.m32;
		m33 += mat.m33;
	}
	
	/**
	 * Converts the translation components of this matrix into a vector.<br>
	 * It extracts the values from the third column of the {@code Matrix4f}.
	 * @return A new {@link Vector3f} containing the x, y, and z translation values.
	 */
	public Vector3f toTranslationVector()
	{
		return new Vector3f(m03, m13, m23);
	}
	
	/**
	 * Updates the provided {@code Vector3f} with the translation values from this matrix.<br>
	 * The translation components are taken from the third column of the matrix.<br>
	 * This method modifies the input object directly.
	 * @param vector The {@code Vector3f} to be updated.
	 */
	public void toTranslationVector(Vector3f vector)
	{
		vector.set(m03, m13, m23);
	}
	
	/**
	 * Converts the current {@code Matrix4f} into a 3x3 rotation matrix.<br>
	 * This method extracts only the rotation components from the 4x4 matrix.<br>
	 * It ignores any translation data stored in the fourth row or column.
	 * @return A new {@link Matrix3f} object containing the rotation data.
	 */
	public Matrix3f toRotationMatrix()
	{
		return new Matrix3f(m00, m01, m02, m10, m11, m12, m20, m21, m22);
	}
	
	/**
	 * Converts the rotation part of this matrix into a 3x3 matrix.<br>
	 * This method copies the values from the current {@code Matrix4f} to the provided {@code Matrix3f}.<br>
	 * It only extracts the upper-left 3x3 portion of the data.
	 * @param mat The destination {@code Matrix3f} object to store the rotation values.
	 */
	public void toRotationMatrix(Matrix3f mat)
	{
		mat.m00 = m00;
		mat.m01 = m01;
		mat.m02 = m02;
		mat.m10 = m10;
		mat.m11 = m11;
		mat.m12 = m12;
		mat.m20 = m20;
		mat.m21 = m21;
		mat.m22 = m22;
		
	}
	
	/**
	 * Updates the rotation components of this matrix.<br>
	 * This method copies values from the provided {@code Matrix3f} object.<br>
	 * It only affects the 3x3 rotation part of the matrix.
	 * @param mat The source {@code Matrix3f} to copy from.
	 */
	public void setRotationMatrix(Matrix3f mat)
	{
		m00 = mat.m00;
		m01 = mat.m01;
		m02 = mat.m02;
		m10 = mat.m10;
		m11 = mat.m11;
		m12 = mat.m12;
		m20 = mat.m20;
		m21 = mat.m21;
		m22 = mat.m22;
		
	}
	
	/**
	 * Sets the scale of the matrix along the three axes.<br>
	 * This method modifies the current values of {@code m00}, {@code m11}, and {@code m22}.
	 * @param x The scale factor for the x-axis.
	 * @param y The scale factor for the y-axis.
	 * @param z The scale factor for the z-axis.
	 */
	public void setScale(float x, float y, float z)
	{
		m00 *= x;
		m11 *= y;
		m22 *= z;
	}
	
	/**
	 * Sets the scaling factors for this matrix.<br>
	 * This method multiplies the diagonal elements by the provided <code class="Vector3f">scale</code> values.
	 * @param scale The <code class="Vector3f">scale</code> to apply.
	 */
	public void setScale(Vector3f scale)
	{
		m00 *= scale.x;
		m11 *= scale.y;
		m22 *= scale.z;
	}
	
	/**
	 * Sets the translation values of this matrix.<br>
	 * The input array must contain exactly 3 elements.
	 * @param translation A {@code float[]} containing the x, y, and z coordinates.
	 */
	public void setTranslation(float[] translation)
	{
		if (translation.length != 3)
		{
			throw new IllegalArgumentException("Translation size must be 3.");
		}
		
		m03 = translation[0];
		m13 = translation[1];
		m23 = translation[2];
	}
	
	/**
	 * Sets the translation components of this matrix.<br>
	 * This method updates the {@code m03}, {@code m13}, and {@code m23} fields.
	 * @param x The new x coordinate for translation.
	 * @param y The new y coordinate for translation.
	 * @param z The new z coordinate for translation.
	 */
	public void setTranslation(float x, float y, float z)
	{
		m03 = x;
		m13 = y;
		m23 = z;
	}
	
	/**
	 * Sets the translation components of this matrix.<br>
	 * This updates the {@code m03}, {@code m13}, and {@code m23} fields.
	 * @param translation The {@link Vector3f} containing the new x, y, and z coordinates.
	 */
	public void setTranslation(Vector3f translation)
	{
		m03 = translation.x;
		m13 = translation.y;
		m23 = translation.z;
	}
	
	/**
	 * Sets the translation values of this matrix to the negative of the provided coordinates.<br>
	 * This method updates the rightmost column of the matrix.
	 * @param translation A {@code float[]} array containing three components for x, y, and z.
	 */
	public void setInverseTranslation(float[] translation)
	{
		if (translation.length != 3)
		{
			throw new IllegalArgumentException("Translation size must be 3.");
		}
		
		m03 = -translation[0];
		m13 = -translation[1];
		m23 = -translation[2];
	}
	
	/**
	 * Rotates the current matrix based on provided angles.<br>
	 * This method updates the internal values of this {@link Matrix4f} instance.<br>
	 * It applies rotations in the order of Z, then Y, and finally X.
	 * @param angles A {@code Vector3f} containing the rotation degrees for x, y, and z axes.
	 */
	public void angleRotation(Vector3f angles)
	{
		float angle;
		float sr, sp, sy, cr, cp, cy;
		
		angle = (angles.z * FastMath.DEG_TO_RAD);
		sy = FastMath.sin(angle);
		cy = FastMath.cos(angle);
		angle = (angles.y * FastMath.DEG_TO_RAD);
		sp = FastMath.sin(angle);
		cp = FastMath.cos(angle);
		angle = (angles.x * FastMath.DEG_TO_RAD);
		sr = FastMath.sin(angle);
		cr = FastMath.cos(angle);
		
		// matrix = (Z * Y) * X
		m00 = cp * cy;
		m10 = cp * sy;
		m20 = -sp;
		m01 = (sr * sp * cy) + (cr * -sy);
		m11 = (sr * sp * sy) + (cr * cy);
		m21 = sr * cp;
		m02 = ((cr * sp * cy) + (-sr * -sy));
		m12 = ((cr * sp * sy) + (-sr * cy));
		m22 = cr * cp;
		m03 = 0.0f;
		m13 = 0.0f;
		m23 = 0.0f;
	}
	
	/**
	 * Sets the rotation of this matrix to the inverse of the provided angles.<br>
	 * The input array must contain three values representing the rotation.<br>
	 * This method updates the internal matrix elements based on these radians.
	 * @param angles A {@code float[]} containing three rotation values.
	 */
	public void setInverseRotationRadians(float[] angles)
	{
		if (angles.length != 3)
		{
			throw new IllegalArgumentException("Angles must be of size 3.");
		}
		
		final double cr = FastMath.cos(angles[0]);
		final double sr = FastMath.sin(angles[0]);
		final double cp = FastMath.cos(angles[1]);
		final double sp = FastMath.sin(angles[1]);
		final double cy = FastMath.cos(angles[2]);
		final double sy = FastMath.sin(angles[2]);
		
		m00 = (float) (cp * cy);
		m10 = (float) (cp * sy);
		m20 = (float) (-sp);
		
		final double srsp = sr * sp;
		final double crsp = cr * sp;
		
		m01 = (float) ((srsp * cy) - (cr * sy));
		m11 = (float) ((srsp * sy) + (cr * cy));
		m21 = (float) (sr * cp);
		
		m02 = (float) ((crsp * cy) + (sr * sy));
		m12 = (float) ((crsp * sy) - (sr * cy));
		m22 = (float) (cr * cp);
	}
	
	/**
	 * Sets the inverse rotation of this matrix using degrees.<br>
	 * This method converts the input values to radians before applying them.<br>
	 * It calls {@code setInverseRotationRadians} internally.
	 * @param angles A {@code float[]} array containing three rotation angles in degrees.
	 */
	public void setInverseRotationDegrees(float[] angles)
	{
		if (angles.length != 3)
		{
			throw new IllegalArgumentException("Angles must be of size 3.");
		}
		
		final float vec[] = new float[3];
		vec[0] = (angles[0] * FastMath.RAD_TO_DEG);
		vec[1] = (angles[1] * FastMath.RAD_TO_DEG);
		vec[2] = (angles[2] * FastMath.RAD_TO_DEG);
		setInverseRotationRadians(vec);
	}
	
	/**
	 * Subtracts the translation components from a vector.<br>
	 * This method modifies the input array in place.
	 * @param vec A {@code float[]} array of size 3 representing the vector.
	 */
	public void inverseTranslateVect(float[] vec)
	{
		if (vec.length != 3)
		{
			throw new IllegalArgumentException("vec must be of size 3.");
		}
		
		vec[0] = vec[0] - m03;
		vec[1] = vec[1] - m13;
		vec[2] = vec[2] - m23;
	}
	
	/**
	 * This method applies the inverse translation of the current matrix to a vector.<br>
	 * It subtracts the translation components from the {@code data} coordinates.<br>
	 * The operation modifies the input object directly.
	 * @param data The {@code Vector3f} to be translated.
	 */
	public void inverseTranslateVect(Vector3f data)
	{
		data.x -= m03;
		data.y -= m13;
		data.z -= m23;
	}
	
	/**
	 * Translates the provided {@code Vector3f} by the current matrix translation values.<br>
	 * This method modifies the input object directly.
	 * @param data The {@code Vector3f} to be translated.
	 */
	public void translateVect(Vector3f data)
	{
		data.x += m03;
		data.y += m13;
		data.z += m23;
	}
	
	/**
	 * Multiplies the given vector by the inverse of this matrix.<br>
	 * This method updates the coordinates of the {@code Vector3f} object in place.<br>
	 * It applies the transformation defined by the current matrix state.
	 * @param vec The {@code Vector3f} to be transformed.
	 */
	public void inverseRotateVect(Vector3f vec)
	{
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		
		vec.x = (vx * m00) + (vy * m10) + (vz * m20);
		vec.y = (vx * m01) + (vy * m11) + (vz * m21);
		vec.z = (vx * m02) + (vy * m12) + (vz * m22);
	}
	
	/**
	 * Rotates the provided {@code Vector3f} using this matrix.<br>
	 * This method modifies the input vector in place.
	 * @param vec The {@code Vector3f} to be rotated.
	 */
	public void rotateVect(Vector3f vec)
	{
		final float vx = vec.x, vy = vec.y, vz = vec.z;
		
		vec.x = (vx * m00) + (vy * m01) + (vz * m02);
		vec.y = (vx * m10) + (vy * m11) + (vz * m12);
		vec.z = (vx * m20) + (vy * m21) + (vz * m22);
	}
	
	/**
	 * Returns a string representation of the matrix.<br>
	 * This method formats the 4x4 values into a readable grid.
	 * @return A formatted string representing this {@code Matrix4f}.
	 */
	@Override
	public String toString()
	{
		final StringBuilder result = new StringBuilder("Matrix4f\n[\n");
		result.append(" ");
		result.append(m00);
		result.append("  ");
		result.append(m01);
		result.append("  ");
		result.append(m02);
		result.append("  ");
		result.append(m03);
		result.append(" \n");
		result.append(" ");
		result.append(m10);
		result.append("  ");
		result.append(m11);
		result.append("  ");
		result.append(m12);
		result.append("  ");
		result.append(m13);
		result.append(" \n");
		result.append(" ");
		result.append(m20);
		result.append("  ");
		result.append(m21);
		result.append("  ");
		result.append(m22);
		result.append("  ");
		result.append(m23);
		result.append(" \n");
		result.append(" ");
		result.append(m30);
		result.append("  ");
		result.append(m31);
		result.append("  ");
		result.append(m32);
		result.append("  ");
		result.append(m33);
		result.append(" \n]");
		return result.toString();
	}
	
	/**
	 * Returns a hash code value for this {@link Matrix4f} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on all sixteen matrix elements.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		int hash = 37;
		hash = (37 * hash) + Float.floatToIntBits(m00);
		hash = (37 * hash) + Float.floatToIntBits(m01);
		hash = (37 * hash) + Float.floatToIntBits(m02);
		hash = (37 * hash) + Float.floatToIntBits(m03);
		
		hash = (37 * hash) + Float.floatToIntBits(m10);
		hash = (37 * hash) + Float.floatToIntBits(m11);
		hash = (37 * hash) + Float.floatToIntBits(m12);
		hash = (37 * hash) + Float.floatToIntBits(m13);
		
		hash = (37 * hash) + Float.floatToIntBits(m20);
		hash = (37 * hash) + Float.floatToIntBits(m21);
		hash = (37 * hash) + Float.floatToIntBits(m22);
		hash = (37 * hash) + Float.floatToIntBits(m23);
		
		hash = (37 * hash) + Float.floatToIntBits(m30);
		hash = (37 * hash) + Float.floatToIntBits(m31);
		hash = (37 * hash) + Float.floatToIntBits(m32);
		hash = (37 * hash) + Float.floatToIntBits(m33);
		
		return hash;
	}
	
	/**
	 * Compares this {@link Matrix4f} object with another object for equality.<br>
	 * It checks if both objects have the same matrix values.
	 * @param o The object to compare this instance against.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Matrix4f))
		{
			return false;
		}
		
		if (this == o)
		{
			return true;
		}
		
		final Matrix4f comp = (Matrix4f) o;
		if ((Float.compare(m00, comp.m00) != 0) || (Float.compare(m01, comp.m01) != 0) || (Float.compare(m02, comp.m02) != 0) || (Float.compare(m03, comp.m03) != 0))
		{
			return false;
		}
		
		if ((Float.compare(m10, comp.m10) != 0) || (Float.compare(m11, comp.m11) != 0) || (Float.compare(m12, comp.m12) != 0) || (Float.compare(m13, comp.m13) != 0))
		{
			return false;
		}
		
		if ((Float.compare(m20, comp.m20) != 0) || (Float.compare(m21, comp.m21) != 0) || (Float.compare(m22, comp.m22) != 0) || (Float.compare(m23, comp.m23) != 0))
		{
			return false;
		}
		
		if ((Float.compare(m30, comp.m30) != 0) || (Float.compare(m31, comp.m31) != 0) || (Float.compare(m32, comp.m32) != 0) || (Float.compare(m33, comp.m33) != 0))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the runtime class of this matrix instance.<br>
	 * This is useful for identifying specific subclasses of {@link Matrix4f}.
	 * @return The {@code Class} object representing the type of this instance.
	 */
	public Class<? extends Matrix4f> getClassTag()
	{
		return this.getClass();
	}
	
	/**
	 * Checks if this matrix is an identity matrix.<br>
	 * An identity matrix has {@code 1.0} on the main diagonal and {@code 0.0} elsewhere.
	 * @return {@code true} if the matrix is an identity matrix, {@code false} otherwise.
	 */
	public boolean isIdentity()
	{
		return ((m00 == 1) && (m01 == 0) && (m02 == 0) && (m03 == 0)) && ((m10 == 0) && (m11 == 1) && (m12 == 0) && (m13 == 0)) && ((m20 == 0) && (m21 == 0) && (m22 == 1) && (m23 == 0)) && ((m30 == 0) && (m31 == 0) && (m32 == 0) && (m33 == 1));
	}
	
	/**
	 * Multiplies the current matrix by a scaling factor.<br>
	 * This method adjusts the size of the transformation along each axis.
	 * @param scale The {@code Vector3f} containing the x, y, and z scale values.
	 */
	public void scale(Vector3f scale)
	{
		m00 *= scale.getX();
		m10 *= scale.getX();
		m20 *= scale.getX();
		m30 *= scale.getX();
		m01 *= scale.getY();
		m11 *= scale.getY();
		m21 *= scale.getY();
		m31 *= scale.getY();
		m02 *= scale.getZ();
		m12 *= scale.getZ();
		m22 *= scale.getZ();
		m32 *= scale.getZ();
	}
	
	/**
	 * Multiplies the matrix elements by a scaling factor.<br>
	 * This method updates the current matrix values in place.
	 * @param scale The value used to multiply each element.
	 */
	public void scale(float scale)
	{
		m00 *= scale;
		m10 *= scale;
		m20 *= scale;
		m30 *= scale;
		m01 *= scale;
		m11 *= scale;
		m21 *= scale;
		m31 *= scale;
		m02 *= scale;
		m12 *= scale;
		m22 *= scale;
		m32 *= scale;
	}
	
	/**
	 * Checks if the provided matrix is an identity matrix.<br>
	 * It compares each element against expected values using a small epsilon.
	 * @param mat The {@code Matrix4f} to check.
	 * @return {@code true} if it is an identity matrix, {@code false} otherwise.
	 */
	static boolean equalIdentity(Matrix4f mat)
	{
		if ((Math.abs(mat.m00 - 1) > 1e-4) || (Math.abs(mat.m11 - 1) > 1e-4) || (Math.abs(mat.m22 - 1) > 1e-4) || (Math.abs(mat.m33 - 1) > 1e-4))
		{
			return false;
		}
		
		if ((Math.abs(mat.m01) > 1e-4) || (Math.abs(mat.m02) > 1e-4) || (Math.abs(mat.m03) > 1e-4) || (Math.abs(mat.m10) > 1e-4))
		{
			return false;
		}
		
		if ((Math.abs(mat.m12) > 1e-4) || (Math.abs(mat.m13) > 1e-4) || (Math.abs(mat.m20) > 1e-4) || (Math.abs(mat.m21) > 1e-4))
		{
			return false;
		}
		
		if ((Math.abs(mat.m23) > 1e-4) || (Math.abs(mat.m30) > 1e-4) || (Math.abs(mat.m31) > 1e-4) || (Math.abs(mat.m32) > 1e-4))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates a new <code class="Matrix4f"> object that is a copy of this instance.<br>
	 * This method uses the {@code clone} method to perform the copy.
	 * @return A new <code class="Matrix4f"> instance with the same values as this one.
	 */
	@Override
	public Matrix4f clone()
	{
		try
		{
			return (Matrix4f) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError(); // can not happen
		}
	}
}
