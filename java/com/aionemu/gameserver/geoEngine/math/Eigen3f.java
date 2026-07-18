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

/**
 * This class provides a wrapper for the {@code Eigen} library to handle 3x3 floating-point matrices.<br>
 * It is used for performing complex linear algebra operations within the geometry engine.
 */
public class Eigen3f
{
	private static final Logger logger = LoggerFactory.getLogger(Eigen3f.class);
	float[] eigenValues = new float[3];
	Vector3f[] eigenVectors = new Vector3f[3];
	static final double ONE_THIRD_DOUBLE = 1.0 / 3.0;
	static final double ROOT_THREE_DOUBLE = Math.sqrt(3.0);
	
	/**
	 * Creates a new instance of the {@code Eigen3f} class.<br>
	 * This constructor initializes the object with default values.
	 */
	public Eigen3f()
	{
	}
	
	/**
	 * Creates a new {@link Eigen3f} instance from a given matrix.<br>
	 * This constructor automatically calculates the eigenvalues and eigenvectors.<br>
	 * It uses the data provided in the {@code Matrix3f} object.
	 * @param data The {@code Matrix3f} used to compute the eigen components.
	 */
	public Eigen3f(Matrix3f data)
	{
		calculateEigen(data);
	}
	
	/**
	 * Calculates the eigenvalues and eigenvectors for a given matrix.<br>
	 * This method updates the internal state of this {@link Eigen3f} instance.<br>
	 * It uses double-precision arithmetic to ensure accuracy during computation.
	 * @param data The {@code Matrix3f} used to calculate the values.
	 */
	public void calculateEigen(Matrix3f data)
	{
		// prep work...
		eigenVectors[0] = new Vector3f();
		eigenVectors[1] = new Vector3f();
		eigenVectors[2] = new Vector3f();
		
		final Matrix3f scaledData = new Matrix3f(data);
		final float maxMagnitude = scaleMatrix(scaledData);
		
		// Compute the eigenvalues using double-precision arithmetic.
		final double roots[] = new double[3];
		computeRoots(scaledData, roots);
		eigenValues[0] = (float) roots[0];
		eigenValues[1] = (float) roots[1];
		eigenValues[2] = (float) roots[2];
		
		final float[] maxValues = new float[3];
		final Vector3f[] maxRows = new Vector3f[3];
		maxRows[0] = new Vector3f();
		maxRows[1] = new Vector3f();
		maxRows[2] = new Vector3f();
		
		for (int i = 0; i < 3; i++)
		{
			final Matrix3f tempMatrix = new Matrix3f(scaledData);
			tempMatrix.m00 -= eigenValues[i];
			tempMatrix.m11 -= eigenValues[i];
			tempMatrix.m22 -= eigenValues[i];
			final float[] val = new float[1];
			val[0] = maxValues[i];
			if (!positiveRank(tempMatrix, val, maxRows[i]))
			{
				// Rank was 0 (or very close to 0), so just return.
				// Rescale back to the original size.
				if (maxMagnitude > 1f)
				{
					for (int j = 0; j < 3; j++)
					{
						eigenValues[j] *= maxMagnitude;
					}
				}
				
				eigenVectors[0].set(Vector3f.UNIT_X);
				eigenVectors[1].set(Vector3f.UNIT_Y);
				eigenVectors[2].set(Vector3f.UNIT_Z);
				return;
			}
			
			maxValues[i] = val[0];
		}
		
		float maxCompare = maxValues[0];
		int i = 0;
		if (maxValues[1] > maxCompare)
		{
			maxCompare = maxValues[1];
			i = 1;
		}
		
		if (maxValues[2] > maxCompare)
		{
			i = 2;
		}
		
		// use the largest row to compute and order the eigen vectors.
		switch (i)
		{
			case 0:
				maxRows[0].normalizeLocal();
				computeVectors(scaledData, maxRows[0], 1, 2, 0);
				break;
			case 1:
				maxRows[1].normalizeLocal();
				computeVectors(scaledData, maxRows[1], 2, 0, 1);
				break;
			case 2:
				maxRows[2].normalizeLocal();
				computeVectors(scaledData, maxRows[2], 0, 1, 2);
				break;
		}
		
		// Rescale the values back to the original size.
		if (maxMagnitude > 1f)
		{
			for (i = 0; i < 3; i++)
			{
				eigenValues[i] *= maxMagnitude;
			}
		}
	}
	
	/**
	 * Scales the input matrix to ensure its values are within a normalized range.<br>
	 * It finds the largest absolute value in the {@code Matrix3f}.<br>
	 * If that value is greater than {@code 1.0f}, it multiplies the matrix by the inverse of that value.
	 * @param mat The {@code Matrix3f} to be scaled.
	 * @return The maximum absolute value found in the matrix before scaling.
	 */
	private float scaleMatrix(Matrix3f mat)
	{
		float max = FastMath.abs(mat.m00);
		float abs = FastMath.abs(mat.m01);
		
		if (abs > max)
		{
			max = abs;
		}
		
		abs = FastMath.abs(mat.m02);
		if (abs > max)
		{
			max = abs;
		}
		
		abs = FastMath.abs(mat.m11);
		if (abs > max)
		{
			max = abs;
		}
		
		abs = FastMath.abs(mat.m12);
		if (abs > max)
		{
			max = abs;
		}
		
		abs = FastMath.abs(mat.m22);
		if (abs > max)
		{
			max = abs;
		}
		
		if (max > 1f)
		{
			final float fInvMax = 1f / max;
			mat.multLocal(fInvMax);
		}
		
		return max;
	}
	
	/**
	 * Calculates and stores the eigenvectors for a given {@code Matrix3f}.<br>
	 * This method uses the provided vector and indices to compute the basis.<br>
	 * It updates the internal {@code eigenVectors} array based on these calculations.
	 * @param mat The input matrix used for calculation.
	 * @param vect A reference vector used as a starting point for the basis.
	 * @param index1 The index in the result array to store the first computed vector.
	 * @param index2 The index in the result array to store the second computed vector.
	 * @param index3 The index in the result array to store the third computed vector.
	 */
	private void computeVectors(Matrix3f mat, Vector3f vect, int index1, int index2, int index3)
	{
		final Vector3f vectorU = new Vector3f(), vectorV = new Vector3f();
		Vector3f.generateComplementBasis(vectorU, vectorV, vect);
		
		final Vector3f tempVect = mat.mult(vectorU);
		float p00 = eigenValues[index3] - vectorU.dot(tempVect);
		float p01 = vectorV.dot(tempVect);
		float p11 = eigenValues[index3] - vectorV.dot(mat.mult(vectorV));
		float invLength;
		float max = FastMath.abs(p00);
		int row = 0;
		float fAbs = FastMath.abs(p01);
		if (fAbs > max)
		{
			max = fAbs;
		}
		
		fAbs = FastMath.abs(p11);
		if (fAbs > max)
		{
			max = fAbs;
			row = 1;
		}
		
		if (max >= FastMath.ZERO_TOLERANCE)
		{
			if (row == 0)
			{
				invLength = FastMath.invSqrt((p00 * p00) + (p01 * p01));
				p00 *= invLength;
				p01 *= invLength;
				vectorU.mult(p01, eigenVectors[index3]).addLocal(vectorV.mult(p00));
			}
			else
			{
				invLength = FastMath.invSqrt((p11 * p11) + (p01 * p01));
				p11 *= invLength;
				p01 *= invLength;
				vectorU.mult(p11, eigenVectors[index3]).addLocal(vectorV.mult(p01));
			}
		}
		else
		{
			if (row == 0)
			{
				eigenVectors[index3] = vectorV;
			}
			else
			{
				eigenVectors[index3] = vectorU;
			}
		}
		
		final Vector3f vectorS = vect.cross(eigenVectors[index3]);
		mat.mult(vect, tempVect);
		p00 = eigenValues[index1] - vect.dot(tempVect);
		p01 = vectorS.dot(tempVect);
		p11 = eigenValues[index1] - vectorS.dot(mat.mult(vectorS));
		max = FastMath.abs(p00);
		row = 0;
		fAbs = FastMath.abs(p01);
		if (fAbs > max)
		{
			max = fAbs;
		}
		
		fAbs = FastMath.abs(p11);
		if (fAbs > max)
		{
			max = fAbs;
			row = 1;
		}
		
		if (max >= FastMath.ZERO_TOLERANCE)
		{
			if (row == 0)
			{
				invLength = FastMath.invSqrt((p00 * p00) + (p01 * p01));
				p00 *= invLength;
				p01 *= invLength;
				eigenVectors[index1] = vect.mult(p01).add(vectorS.mult(p00));
			}
			else
			{
				invLength = FastMath.invSqrt((p11 * p11) + (p01 * p01));
				p11 *= invLength;
				p01 *= invLength;
				eigenVectors[index1] = vect.mult(p11).add(vectorS.mult(p01));
			}
		}
		else
		{
			if (row == 0)
			{
				eigenVectors[index1].set(vectorS);
			}
			else
			{
				eigenVectors[index1].set(vect);
			}
		}
		
		eigenVectors[index3].cross(eigenVectors[index1], eigenVectors[index2]);
	}
	
	/**
	 * Checks if the matrix has a positive rank based on its largest entry.<br>
	 * It identifies the row with the maximum magnitude and stores it.<br>
	 * This is used to help construct eigenvectors.
	 * @param matrix The {@code Matrix3f} to analyze.
	 * @param maxMagnitudeStore A {@code float[]} array where the highest absolute value will be stored.
	 * @param maxRowStore A {@code Vector3f} object that will store the row containing the maximum magnitude.
	 * @return {@code true} if the maximum magnitude is greater than or equal to {@code FastMath.ZERO_TOLERANCE}, otherwise {@code false}.
	 */
	private boolean positiveRank(Matrix3f matrix, float[] maxMagnitudeStore, Vector3f maxRowStore)
	{
		// Locate the maximum-magnitude entry of the matrix.
		maxMagnitudeStore[0] = -1f;
		int iRow, iCol, iMaxRow = -1;
		for (iRow = 0; iRow < 3; iRow++)
		{
			for (iCol = iRow; iCol < 3; iCol++)
			{
				final float fAbs = FastMath.abs(matrix.get(iRow, iCol));
				if (fAbs > maxMagnitudeStore[0])
				{
					maxMagnitudeStore[0] = fAbs;
					iMaxRow = iRow;
				}
			}
		}
		
		// Return the row containing the maximum to be used for eigenvector construction.
		maxRowStore.set(matrix.getRow(iMaxRow));
		
		return maxMagnitudeStore[0] >= FastMath.ZERO_TOLERANCE;
	}
	
	/**
	 * Calculates the eigenvalues of a symmetric matrix.<br>
	 * The results are stored in an array sorted in increasing order.
	 * @param mat The {@code Matrix3f} to process.
	 * @param rootsStore An array of size 3 to store the resulting double values.
	 */
	private void computeRoots(Matrix3f mat, double[] rootsStore)
	{
		// Convert the unique matrix entries to double precision.
		final double a = mat.m00, b = mat.m01, c = mat.m02, d = mat.m11, e = mat.m12, f = mat.m22;
		
		// The characteristic equation is x^3 - c2*x^2 + c1*x - c0 = 0, and its roots are guaranteed to be real-valued eigenvalues because the matrix is symmetric.
		final double char0 = ((a * d * f) + (2.0 * b * c * e)) - (a * e * e) - (d * c * c) - (f * b * b);
		
		final double char1 = (((((a * d) - (b * b)) + (a * f)) - (c * c)) + (d * f)) - (e * e);
		
		final double char2 = a + d + f;
		
		// Construct the parameters used to classify and solve the equation's roots in closed form.
		final double char2Div3 = char2 * ONE_THIRD_DOUBLE;
		double abcDiv3 = (char1 - (char2 * char2Div3)) * ONE_THIRD_DOUBLE;
		if (abcDiv3 > 0.0)
		{
			abcDiv3 = 0.0;
		}
		
		final double mbDiv2 = 0.5 * (char0 + (char2Div3 * ((2.0 * char2Div3 * char2Div3) - char1)));
		
		double q = (mbDiv2 * mbDiv2) + (abcDiv3 * abcDiv3 * abcDiv3);
		if (q > 0.0)
		{
			q = 0.0;
		}
		
		// Compute the eigenvalues by solving for the roots of the polynomial.
		final double magnitude = Math.sqrt(-abcDiv3);
		final double angle = Math.atan2(Math.sqrt(-q), mbDiv2) * ONE_THIRD_DOUBLE;
		final double cos = Math.cos(angle);
		final double sin = Math.sin(angle);
		final double root0 = char2Div3 + (2.0 * magnitude * cos);
		final double root1 = char2Div3 - (magnitude * (cos + (ROOT_THREE_DOUBLE * sin)));
		final double root2 = char2Div3 - (magnitude * (cos - (ROOT_THREE_DOUBLE * sin)));
		
		// Sort in increasing order.
		if (root1 >= root0)
		{
			rootsStore[0] = root0;
			rootsStore[1] = root1;
		}
		else
		{
			rootsStore[0] = root1;
			rootsStore[1] = root0;
		}
		
		if (root2 >= rootsStore[1])
		{
			rootsStore[2] = root2;
		}
		else
		{
			rootsStore[2] = rootsStore[1];
			if (root2 >= rootsStore[0])
			{
				rootsStore[1] = root2;
			}
			else
			{
				rootsStore[1] = rootsStore[0];
				rootsStore[0] = root2;
			}
		}
	}
	
	/**
	 * This is the main entry point for testing the {@link Eigen3f} class.<br>
	 * It creates a sample {@code Matrix3f} and calculates its eigenvalues and eigenvectors.<br>
	 * The results are printed to the log for verification.
	 * @param args Command line arguments passed to the application.
	 */
	public static void main(String[] args)
	{
		final Matrix3f mat = new Matrix3f(2, 1, 1, 1, 2, 1, 1, 1, 2);
		final Eigen3f eigenSystem = new Eigen3f(mat);
		
		logger.info("eigenvalues = ");
		for (int i = 0; i < 3; i++)
		{
			logger.info(eigenSystem.getEigenValue(i) + " ");
		}
		
		logger.info("eigenvectors = ");
		for (int i = 0; i < 3; i++)
		{
			final Vector3f vector = eigenSystem.getEigenVector(i);
			logger.info(vector.toString());
			mat.setColumn(i, vector);
		}
		
		logger.info(mat.toString());
		
		// The eigenvalues are 1.000000, 1.000000, and 4.000000, with corresponding eigenvectors of 0.411953, 0.704955, 0.577350, 0.404533, -0.709239, 0.577350, -0.816485, 0.004284, and 0.577350.
	}
	
	/**
	 * Retrieves a specific eigenvalue from the calculated set.<br>
	 * This method returns the value at the given index.
	 * @param i The index of the eigenvalue to retrieve.
	 * @return The {@code float} value of the requested eigenvalue.
	 */
	public float getEigenValue(int i)
	{
		return eigenValues[i];
	}
	
	/**
	 * Retrieves a specific eigenvector from the calculated set.<br>
	 * This method returns the vector at the given index.
	 * @param i The index of the eigenvector to retrieve.
	 * @return The {@code Vector3f} corresponding to the specified index.
	 */
	public Vector3f getEigenVector(int i)
	{
		return eigenVectors[i];
	}
	
	/**
	 * Retrieves the calculated eigenvalues of the matrix.<br>
	 * This method returns an array containing all three values.
	 * @return a {@code float[]} array of the eigenvalues.
	 */
	public float[] getEigenValues()
	{
		return eigenValues;
	}
	
	/**
	 * Retrieves the calculated eigenvectors.<br>
	 * These vectors are stored as an array of {@link Vector3f}.
	 * @return An array containing all three {@code Vector3f} eigenvectors.
	 */
	public Vector3f[] getEigenVectors()
	{
		return eigenVectors;
	}
}
