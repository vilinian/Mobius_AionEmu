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
package com.aionemu.gameserver.model.utils3d;

/**
 * This class provides a representation of a 3D matrix for spatial transformations.<br>
 * It is used to handle operations like rotation, scaling, and translation in the game world.
 * @author M@xx
 */
public class Matrix3D
{
	public static final double[][] IDENTITY = new double[][]
	{
		{
			1,
			0,
			0
		},
		{
			0,
			1,
			0
		},
		{
			0,
			0,
			1
		}
	};
	private final double[][] data;
	
	/**
	 * Creates a new instance of {@link Matrix3D}.<br>
	 * This initializes a 3x3 matrix with all values set to {@code 0.0}.
	 */
	public Matrix3D()
	{
		data = new double[3][3];
	}
	
	/**
	 * Creates a new {@link Matrix3D} instance from a 2D array.<br>
	 * The input array must have exactly 3 rows and 3 columns.<br>
	 * This method throws a {@code RuntimeException} if the dimensions are incorrect.
	 * @param data A {@code double[][]} representing the matrix values.
	 */
	public Matrix3D(double[][] data)
	{
		this();
		if (data.length != 3)
		{
			throw new RuntimeException("Invalid matrix dimensions");
		}
		
		for (int i = 0; i < 3; i++)
		{
			if (data[i].length != 3)
			{
				throw new RuntimeException("Invalid matrix dimensions");
			}
			
			System.arraycopy(data[i], 0, this.data[i], 0, 3);
		}
	}
	
	/**
	 * Creates a new {@link Matrix3D} with one column replaced.<br>
	 * The original matrix remains unchanged.
	 * @param i The index of the column to replace. Must be between 0 and 3.
	 * @param newColumn An array of 3 doubles representing the new values.
	 * @return A new {@link Matrix3D} instance containing the updated data.
	 */
	public Matrix3D replaceColumn(int i, double[] newColumn)
	{
		if ((i > 3) || (i < 0))
		{
			throw new RuntimeException("Invalid column index " + i);
		}
		
		if (newColumn.length > 3)
		{
			throw new RuntimeException("Invalid column dimension");
		}
		
		final Matrix3D B = new Matrix3D(data);
		for (int j = 0; j < 3; j++)
		{
			B.data[j][i] = newColumn[j];
		}
		
		return B;
	}
	
	/**
	 * Multiplies this matrix by another {@link Matrix3D}.<br>
	 * This performs a standard 3x3 matrix multiplication.<br>
	 * The result is stored in a new {@code Matrix3D} object.
	 * @param B The other {@code Matrix3D} to multiply by.
	 * @return A new {@code Matrix3D} representing the product of the two matrices.
	 */
	public Matrix3D multiply(Matrix3D B)
	{
		final Matrix3D C = new Matrix3D();
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				for (int k = 0; k < 3; k++)
				{
					C.data[i][j] += (data[i][k] * B.data[k][j]);
				}
			}
		}
		
		return C;
	}
	
	/**
	 * Multiplies every element in this matrix by a scalar value.<br>
	 * This method returns a new {@link Matrix3D} instance.
	 * @param b The scalar value to multiply by.
	 * @return A new {@code Matrix3D} containing the scaled values.
	 */
	public Matrix3D multiply(double b)
	{
		final Matrix3D C = new Matrix3D();
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				C.data[i][j] = b * data[i][j];
			}
		}
		
		return C;
	}
	
	/**
	 * Calculates the determinant of the current {@code Matrix3D}.<br>
	 * This value is used to determine if a matrix is invertible.
	 * @return The calculated determinant as a {@code double}.
	 */
	public double determinant()
	{
		final double aei = data[0][0] * data[1][1] * data[2][2];
		final double bfg = data[0][1] * data[1][2] * data[2][0];
		final double cdh = data[0][2] * data[1][0] * data[2][1];
		final double afh = data[0][0] * data[1][2] * data[2][1];
		final double bdi = data[0][1] * data[1][0] * data[2][2];
		final double ceg = data[0][2] * data[1][1] * data[2][0];
		return ((aei + bfg + cdh) - afh - bdi - ceg);
	}
	
	/**
	 * Calculates the inverse of the current {@code Matrix3D}.<br>
	 * This method uses the {@code adjugate} and {@code determinant} methods.<br>
	 * It throws a {@code RuntimeException} if the matrix is not invertible.
	 * @return A new {@code Matrix3D} representing the inverse.
	 */
	public Matrix3D inverse()
	{
		if (Math.abs(determinant()) <= Double.MIN_VALUE)
		{
			throw new RuntimeException("Matrix not inversible");
		}
		
		return adjugate().multiply(1 / determinant());
	}
	
	/**
	 * Calculates the adjugate matrix of this {@link Matrix3D}.<br>
	 * The adjugate is the transpose of the cofactor matrix.<br>
	 * It can be used to find the inverse of a matrix.
	 * @return A new {@code Matrix3D} object representing the adjugate matrix.
	 */
	public Matrix3D adjugate()
	{
		final Matrix3D adj = new Matrix3D();
		adj.data[0][0] = (data[1][1] * data[2][2]) - (data[1][2] * data[2][1]);
		adj.data[0][1] = -((data[0][1] * data[2][2]) - (data[0][2] * data[2][1]));
		adj.data[0][2] = (data[0][1] * data[1][2]) - (data[0][2] * data[1][1]);
		adj.data[1][0] = -((data[1][0] * data[2][2]) - (data[1][2] * data[2][0]));
		adj.data[1][1] = (data[0][0] * data[2][2]) - (data[0][2] * data[2][0]);
		adj.data[1][2] = -((data[0][0] * data[1][2]) - (data[0][2] * data[1][0]));
		adj.data[2][0] = (data[1][0] * data[2][1]) - (data[1][1] * data[2][0]);
		adj.data[2][1] = -((data[0][0] * data[2][1]) - (data[0][1] * data[2][0]));
		adj.data[2][2] = (data[0][0] * data[1][1]) - (data[0][1] * data[1][0]);
		return adj;
	}
	
	/**
	 * Multiplies this matrix by a 3D vector.<br>
	 * The input array must have a length of {@code 3}.
	 * @param v The {@code double[]} vector to multiply.
	 * @return A new {@code double[]} containing the result.
	 */
	public double[] multiply(double[] v)
	{
		if (v.length != 3)
		{
			throw new RuntimeException("Vector dimensions invalid");
		}
		
		final double[] result = new double[]
		{
			(data[0][0] * v[0]) + (data[0][1] * v[1]) + (data[0][2] * v[2]),
			(data[1][0] * v[0]) + (data[1][1] * v[1]) + (data[1][2] * v[2]),
			(data[2][0] * v[0]) + (data[2][1] * v[1]) + (data[2][2] * v[2])
		};
		
		return result;
	}
	
	/**
	 * Returns a string representation of the {@code Matrix3D}.<br>
	 * This method formats the internal {@code data} array into a readable grid.<br>
	 * Each value is formatted to four decimal places.
	 * @return A formatted string showing the 3x3 matrix values.
	 */
	@Override
	public String toString()
	{
		String s = "";
		for (int i = 0; i < 3; i++)
		{
			s += "[ ";
			for (int j = 0; j < 3; j++)
			{
				s += String.format("%+4.4f ", data[i][j]);
			}
			
			s += "]\n";
		}
		
		return s;
	}
}
