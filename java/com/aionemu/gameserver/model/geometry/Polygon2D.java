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
package com.aionemu.gameserver.model.geometry;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Represents a two-dimensional polygon using {@code float} coordinates.<br>
 * This class provides geometric data for shapes within the game world.
 */
public class Polygon2D implements Shape, Cloneable, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The total number of points. The value of <code>npoints</code> represents the number of valid points in this <code>Polygon</code>.
	 */
	public int npoints;
	/**
	 * The array of <i>x</i> coordinates. The value of {@code npoints} is equal to the number of points in this <code>Polygon2D</code>.
	 */
	public float[] xpoints;
	/**
	 * The array of <i>x</i> coordinates. The value of {@code npoints} is equal to the number of points in this <code>Polygon2D</code>.
	 */
	public float[] ypoints;
	/**
	 * Bounds of the Polygon2D.
	 * @see #getBounds()
	 */
	protected Rectangle2D bounds;
	private GeneralPath path;
	private GeneralPath closedPath;
	
	/**
	 * Creates a new instance of {@link Polygon2D}.<br>
	 * This constructor initializes the coordinate arrays with a default size of 4.
	 */
	public Polygon2D()
	{
		xpoints = new float[4];
		ypoints = new float[4];
	}
	
	/**
	 * Creates a new {@link Polygon2D} based on the provided rectangle.<br>
	 * This method initializes the polygon with four points from the {@code Rectangle2D}.<br>
	 * It throws an exception if the input is {@code null}.
	 * @param rec The {@code Rectangle2D} used to define the shape.
	 */
	public Polygon2D(Rectangle2D rec)
	{
		if (rec == null)
		{
			throw new IndexOutOfBoundsException("null Rectangle");
		}
		
		npoints = 4;
		xpoints = new float[4];
		ypoints = new float[4];
		xpoints[0] = (float) rec.getMinX();
		ypoints[0] = (float) rec.getMinY();
		xpoints[1] = (float) rec.getMaxX();
		ypoints[1] = (float) rec.getMinY();
		xpoints[2] = (float) rec.getMaxX();
		ypoints[2] = (float) rec.getMaxY();
		xpoints[3] = (float) rec.getMinX();
		ypoints[3] = (float) rec.getMaxY();
		calculatePath();
	}
	
	/**
	 * Creates a new {@link Polygon2D} instance from an existing {@code Polygon}.<br>
	 * This method copies all points from the provided {@code pol} object.<br>
	 * It will throw an {@code IndexOutOfBoundsException} if {@code pol} is {@code null}.
	 * @param pol The source {@code Polygon} to copy data from.
	 */
	public Polygon2D(Polygon pol)
	{
		if (pol == null)
		{
			throw new IndexOutOfBoundsException("null Polygon");
		}
		
		npoints = pol.npoints;
		xpoints = new float[pol.npoints];
		ypoints = new float[pol.npoints];
		for (int i = 0; i < pol.npoints; i++)
		{
			xpoints[i] = pol.xpoints[i];
			ypoints[i] = pol.ypoints[i];
		}
		
		calculatePath();
	}
	
	/**
	 * Creates a new {@link Polygon2D} using arrays of coordinates.<br>
	 * This constructor copies the first {@code npoints} from the provided arrays.<br>
	 * It will throw an {@code IndexOutOfBoundsException} if the arrays are too small.
	 * @param xpoints The array containing the x-coordinates.
	 * @param ypoints The array containing the y-coordinates.
	 * @param npoints The number of points to include in the polygon.
	 */
	public Polygon2D(float[] xpoints, float[] ypoints, int npoints)
	{
		if ((npoints > xpoints.length) || (npoints > ypoints.length))
		{
			throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
		}
		
		this.npoints = npoints;
		this.xpoints = new float[npoints];
		this.ypoints = new float[npoints];
		System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
		System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
		calculatePath();
	}
	
	/**
	 * Creates a new {@link Polygon2D} using integer coordinates.<br>
	 * This constructor copies the provided values into internal float arrays.<br>
	 * It validates that the number of points does not exceed the array lengths.
	 * @param xpoints An array containing the x-coordinates of the vertices.
	 * @param ypoints An array containing the y-coordinates of the vertices.
	 * @param npoints The total number of valid points to include in the polygon.
	 */
	public Polygon2D(int[] xpoints, int[] ypoints, int npoints)
	{
		if ((npoints > xpoints.length) || (npoints > ypoints.length))
		{
			throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
		}
		
		this.npoints = npoints;
		this.xpoints = new float[npoints];
		this.ypoints = new float[npoints];
		for (int i = 0; i < npoints; i++)
		{
			this.xpoints[i] = xpoints[i];
			this.ypoints[i] = ypoints[i];
		}
		
		calculatePath();
	}
	
	/**
	 * Resets all fields to their default values.<br>
	 * This method clears the current state of the {@link Polygon2D} instance.
	 */
	public void reset()
	{
		npoints = 0;
		bounds = null;
		path = new GeneralPath();
		closedPath = null;
	}
	
	/**
	 * Creates and returns a copy of this {@link Polygon2D} object.<br>
	 * This method creates a new instance and copies all points from the current object.
	 * @return A new {@code Object} that is a copy of this instance.
	 */
	@Override
	public Object clone()
	{
		final Polygon2D pol = new Polygon2D();
		for (int i = 0; i < npoints; i++)
		{
			pol.addPoint(xpoints[i], ypoints[i]);
		}
		
		return pol;
	}
	
	/**
	 * This method initializes the internal {@code path} object.<br>
	 * It builds a line sequence using all available coordinates in {@code xpoints} and {@code ypoints}.<br>
	 * The method also updates the {@code bounds} field based on the new path.<br>
	 * Finally, it sets the {@code closedPath} to {@code null}.
	 */
	private void calculatePath()
	{
		path = new GeneralPath();
		path.moveTo(xpoints[0], ypoints[0]);
		for (int i = 1; i < npoints; i++)
		{
			path.lineTo(xpoints[i], ypoints[i]);
		}
		
		bounds = path.getBounds2D();
		closedPath = null;
	}
	
	/**
	 * Updates the internal path and bounding box of the polygon.<br>
	 * This method adds a new point to the {@code path} object.<br>
	 * It also recalculates the {@code bounds} based on the new coordinates.
	 * @param x The x-coordinate of the new point.
	 * @param y The y-coordinate of the new point.
	 */
	private void updatePath(float x, float y)
	{
		closedPath = null;
		if (path == null)
		{
			path = new GeneralPath(Path2D.WIND_EVEN_ODD);
			path.moveTo(x, y);
			bounds = new Rectangle2D.Float(x, y, 0, 0);
		}
		else
		{
			path.lineTo(x, y);
			float _xmax = (float) bounds.getMaxX();
			float _ymax = (float) bounds.getMaxY();
			float _xmin = (float) bounds.getMinX();
			float _ymin = (float) bounds.getMinY();
			if (x < _xmin)
			{
				_xmin = x;
			}
			else if (x > _xmax)
			{
				_xmax = x;
			}
			
			if (y < _ymin)
			{
				_ymin = y;
			}
			else if (y > _ymax)
			{
				_ymax = y;
			}
			
			bounds = new Rectangle2D.Float(_xmin, _ymin, _xmax - _xmin, _ymax - _ymin);
		}
	}
	
	/*
	 * get the associated {@code Polyline2D}.
	 */
	/**
	 * Converts the current polygon into a {@code Polyline2D} object.<br>
	 * This method uses the existing coordinates to create a new polyline.
	 * @return A new {@code Polyline2D} instance based on this polygon.
	 */
	public Polyline2D getPolyline2D()
	{
		final Polyline2D pol = new Polyline2D(xpoints, ypoints, npoints);
		
		pol.addPoint(xpoints[0], ypoints[0]);
		
		return pol;
	}
	
	/**
	 * Retrieves the polygon representation of this object.<br>
	 * This method converts the float coordinates into integer coordinates.<br>
	 * It returns a new {@link Polygon} instance.
	 * @return A new {@code Polygon} object containing the converted points.
	 */
	public Polygon getPolygon()
	{
		final int[] _xpoints = new int[npoints];
		final int[] _ypoints = new int[npoints];
		for (int i = 0; i < npoints; i++)
		{
			_xpoints[i] = (int) xpoints[i]; // todo maybe rounding is better ?
			_ypoints[i] = (int) ypoints[i];
		}
		
		return new Polygon(_xpoints, _ypoints, npoints);
	}
	
	/**
	 * Adds a new point to the polygon.<br>
	 * This method updates the internal coordinates and path.
	 * @param p The {@code Point2D} object to add.
	 */
	public void addPoint(Point2D p)
	{
		addPoint((float) p.getX(), (float) p.getY());
	}
	
	/**
	 * Adds a new point to the polygon.<br>
	 * This method updates the {@code npoints} count and internal paths.
	 * @param x The x-coordinate of the new point.
	 * @param y The y-coordinate of the new point.
	 */
	public void addPoint(float x, float y)
	{
		if (npoints == xpoints.length)
		{
			float[] tmp;
			
			tmp = new float[npoints * 2];
			System.arraycopy(xpoints, 0, tmp, 0, npoints);
			xpoints = tmp;
			
			tmp = new float[npoints * 2];
			System.arraycopy(ypoints, 0, tmp, 0, npoints);
			ypoints = tmp;
		}
		
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		npoints++;
		updatePath(x, y);
	}
	
	/**
	 * Checks if a specific point is inside this polygon.<br>
	 * This method calls {@code double)} using the coordinates of the provided {@link Point}.
	 * @param p The {@link Point} to check.
	 * @return {@code true} if the point is inside the polygon, {@code false} otherwise.
	 */
	public boolean contains(Point p)
	{
		return contains(p.x, p.y);
	}
	
	/**
	 * Checks if a specific point is inside the polygon.<br>
	 * This method calls {@code double)} using casted coordinates.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @return {@code true} if the point is inside the polygon, otherwise {@code false}.
	 */
	public boolean contains(int x, int y)
	{
		return contains((double) x, (double) y);
	}
	
	/**
	 * Retrieves the bounding box of this {@link Polygon2D}.<br>
	 * This returns a {@code Rectangle2D} object.
	 * @return The {@code Rectangle2D} representing the bounds.
	 */
	@Override
	public Rectangle2D getBounds2D()
	{
		return bounds;
	}
	
	/**
	 * Retrieves the bounding box of this polygon.<br>
	 * This method returns a {@code Rectangle} object.<br>
	 * It returns {@code null} if no bounds are defined.
	 * @return The {@code Rectangle} representing the bounds, or {@code null}.
	 */
	@Override
	public Rectangle getBounds()
	{
		if (bounds == null)
		{
			return null;
		}
		
		return bounds.getBounds();
	}
	
	/**
	 * Checks if the point at coordinates {@code x} and {@code y} is inside this polygon.<br>
	 * It first verifies that the shape has enough points and falls within the bounding box.<br>
	 * Then it uses the internal path to determine the result.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return {@code true} if the point is inside the polygon, otherwise {@code false}.
	 */
	@Override
	public boolean contains(double x, double y)
	{
		if ((npoints <= 2) || !bounds.contains(x, y))
		{
			return false;
		}
		
		updateComputingPath();
		
		return closedPath.contains(x, y);
	}
	
	/**
	 * Updates the {@code closedPath} internal variable.<br>
	 * This method ensures that a closed version of the path exists if there is at least one point.<br>
	 * It clones the existing {@code path} and calls {@code closePath()} on it.
	 */
	private void updateComputingPath()
	{
		if (npoints >= 1)
		{
			if (closedPath == null)
			{
				closedPath = (GeneralPath) path.clone();
				closedPath.closePath();
			}
		}
	}
	
	/**
	 * Checks if a specific point is inside this polygon.<br>
	 * This method uses the coordinates of the provided {@link Point2D}.
	 * @param p The {@link Point2D} to check.
	 * @return {@code true} if the point is inside, {@code false} otherwise.
	 */
	@Override
	public boolean contains(Point2D p)
	{
		return contains(p.getX(), p.getY());
	}
	
	/**
	 * Checks if this polygon overlaps with a rectangle.<br>
	 * The rectangle is defined by its top-left corner and dimensions.
	 * @param x The x-coordinate of the rectangle's top-left corner.
	 * @param y The y-coordinate of the rectangle's top-left corner.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @return {@code true} if the shapes intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		if ((npoints <= 0) || !bounds.intersects(x, y, w, h))
		{
			return false;
		}
		
		updateComputingPath();
		return closedPath.intersects(x, y, w, h);
	}
	
	/**
	 * Checks if this polygon overlaps with a given rectangle.<br>
	 * This method uses the {@code double, double, double)} logic.
	 * @param r The {@code Rectangle2D} to check against.
	 * @return {@code true} if the shapes intersect, {@code false} otherwise.
	 */
	@Override
	public boolean intersects(Rectangle2D r)
	{
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Checks if a rectangle is contained within this polygon.<br>
	 * The rectangle is defined by its top-left corner and dimensions.<br>
	 * This method currently always returns {@code false}.
	 * @param x The x-coordinate of the rectangle's top-left corner.
	 * @param y The y-coordinate of the rectangle's top-left corner.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		if ((npoints <= 0) || !bounds.intersects(x, y, w, h))
		{
			return false;
		}
		
		updateComputingPath();
		return closedPath.contains(x, y, w, h);
	}
	
	/**
	 * Checks if this polygon contains the specified rectangle.<br>
	 * This method checks if the area of {@code r} is within the boundaries of this shape.
	 * @param r The {@link Rectangle2D} to check.
	 * @return {@code true} if the rectangle is contained, {@code false} otherwise.
	 */
	@Override
	public boolean contains(Rectangle2D r)
	{
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Returns a {@link PathIterator} for the current shape.<br>
	 * This method applies the provided {@code AffineTransform} to the path.<br>
	 * It returns {@code null} if no path exists.
	 * @param at The {@code AffineTransform} to apply to the path.
	 * @return A {@link PathIterator} representing the transformed shape, or {@code null}.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at)
	{
		updateComputingPath();
		if (closedPath == null)
		{
			return null;
		}
		
		return closedPath.getPathIterator(at);
	}
	
	/**
	 * Returns a {@link PathIterator} for the current shape.<br>
	 * This method applies an {@code AffineTransform} to the path.<br>
	 * It uses the internal {@code GeneralPath} to generate the iterator.
	 * @param at The {@code AffineTransform} to apply to the path.
	 * @param flatness The flatness value used for curve approximation.
	 * @return A new {@link PathIterator} representing the transformed shape.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		return getPathIterator(at);
	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at <a href="http://www.apache.org/licenses/LICENSE-2.0</p> <p>Unless" title="http://www.apache.org/licenses/LICENSE-2.0</p> <p>Unless">http://www.apache.org/licenses/LICENSE-2.0</p> <p>Unless</p></a> required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * This class has the same behavior than {@link Polygon2D}, except that the figure is not closed.
 * @version $Id: Polyline2D.java 594018 2007-11-12 04:17:41Z cam $
 */
class Polyline2D implements Shape, Cloneable, Serializable
{
	private static final long serialVersionUID = 8555427697285636463L;
	private static final float ASSUME_ZERO = 0.001f;
	/**
	 * The total number of points. The value of <code>npoints</code> represents the number of points in this <code>Polyline2D</code>.
	 */
	public int npoints;
	/**
	 * The array of <i>x</i> coordinates. The value of {@code npoints} is equal to the number of points in this <code>Polyline2D</code>.
	 */
	public float[] xpoints;
	/**
	 * The array of <i>x</i> coordinates. The value of {@code npoints} is equal to the number of points in this <code>Polyline2D</code>.
	 */
	public float[] ypoints;
	/**
	 * Bounds of the Polyline2D.
	 * @see #getBounds()
	 */
	protected Rectangle2D bounds;
	private GeneralPath path;
	private GeneralPath closedPath;
	
	/**
	 * Creates a new instance of {@code Polyline2D}.<br>
	 * This constructor initializes the coordinate arrays with a default size.
	 */
	public Polyline2D()
	{
		xpoints = new float[4];
		ypoints = new float[4];
	}
	
	/**
	 * Creates a new {@code Polyline2D} using arrays of coordinates.<br>
	 * This constructor copies the provided points into internal arrays.<br>
	 * It automatically adds an extra point to close the polyline path.
	 * @param xpoints The array containing the x-coordinates of the points.
	 * @param ypoints The array containing the y-coordinates of the points.
	 * @param npoints The number of valid points to include from the arrays.
	 */
	public Polyline2D(float[] xpoints, float[] ypoints, int npoints)
	{
		if ((npoints > xpoints.length) || (npoints > ypoints.length))
		{
			throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
		}
		
		this.npoints = npoints;
		this.xpoints = new float[npoints + 1]; // make space for one more to close the polyline
		this.ypoints = new float[npoints + 1]; // make space for one more to close the polyline
		System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
		System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
		calculatePath();
	}
	
	/**
	 * Creates a new {@link Polygon2D} using integer coordinates.<br>
	 * This constructor copies the values from the provided arrays into internal float arrays.<br>
	 * It automatically calls {@code calculatePath()} to initialize the geometry.
	 * @param xpoints An array of integers representing the x-coordinates.
	 * @param ypoints An array of integers representing the y-coordinates.
	 * @param npoints The number of valid points to include from the arrays.
	 */
	public Polyline2D(int[] xpoints, int[] ypoints, int npoints)
	{
		if ((npoints > xpoints.length) || (npoints > ypoints.length))
		{
			throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
		}
		
		this.npoints = npoints;
		this.xpoints = new float[npoints];
		this.ypoints = new float[npoints];
		for (int i = 0; i < npoints; i++)
		{
			this.xpoints[i] = xpoints[i];
			this.ypoints[i] = ypoints[i];
		}
		
		calculatePath();
	}
	
	/**
	 * Creates a new {@code Polyline2D} from a {@code Line2D}.<br>
	 * This constructor initializes the polyline with two points.<br>
	 * It automatically calls {@code calculatePath} to update the internal path.
	 * @param line The {@code Line2D} object used to define the start and end points.
	 */
	public Polyline2D(Line2D line)
	{
		npoints = 2;
		xpoints = new float[2];
		ypoints = new float[2];
		xpoints[0] = (float) line.getX1();
		xpoints[1] = (float) line.getX2();
		ypoints[0] = (float) line.getY1();
		ypoints[1] = (float) line.getY2();
		calculatePath();
	}
	
	/**
	 * Resets all fields to their default values.<br>
	 * This method clears the current state of the {@link Polygon2D} instance.
	 */
	public void reset()
	{
		npoints = 0;
		bounds = null;
		path = new GeneralPath();
		closedPath = null;
	}
	
	/**
	 * Creates and returns a copy of this {@link Polygon2D} object.<br>
	 * This method creates a new {@code Polyline2D} instance populated with the same coordinates.
	 * @return A new {@code Object} representing the copied geometry, or {@code null} if cloning fails.
	 */
	@Override
	public Object clone()
	{
		final Polyline2D pol = new Polyline2D();
		for (int i = 0; i < npoints; i++)
		{
			pol.addPoint(xpoints[i], ypoints[i]);
		}
		
		return pol;
	}
	
	/**
	 * This method initializes the internal {@code path} object.<br>
	 * It builds a line sequence using all available coordinates in {@code xpoints} and {@code ypoints}.<br>
	 * The method also updates the {@code bounds} field based on the new path.<br>
	 * Finally, it sets the {@code closedPath} to {@code null}.
	 */
	private void calculatePath()
	{
		path = new GeneralPath();
		path.moveTo(xpoints[0], ypoints[0]);
		for (int i = 1; i < npoints; i++)
		{
			path.lineTo(xpoints[i], ypoints[i]);
		}
		
		bounds = path.getBounds2D();
		closedPath = null;
	}
	
	/**
	 * Updates the internal path and bounding box of the polygon.<br>
	 * This method adds a new point to the {@code path} object.<br>
	 * It also recalculates the {@code bounds} based on the new coordinates.
	 * @param x The x-coordinate of the new point.
	 * @param y The y-coordinate of the new point.
	 */
	private void updatePath(float x, float y)
	{
		closedPath = null;
		if (path == null)
		{
			path = new GeneralPath(Path2D.WIND_EVEN_ODD);
			path.moveTo(x, y);
			bounds = new Rectangle2D.Float(x, y, 0, 0);
		}
		else
		{
			path.lineTo(x, y);
			float _xmax = (float) bounds.getMaxX();
			float _ymax = (float) bounds.getMaxY();
			float _xmin = (float) bounds.getMinX();
			float _ymin = (float) bounds.getMinY();
			if (x < _xmin)
			{
				_xmin = x;
			}
			else if (x > _xmax)
			{
				_xmax = x;
			}
			
			if (y < _ymin)
			{
				_ymin = y;
			}
			else if (y > _ymax)
			{
				_ymax = y;
			}
			
			bounds = new Rectangle2D.Float(_xmin, _ymin, _xmax - _xmin, _ymax - _ymin);
		}
	}
	
	/**
	 * Adds a new point to the polygon.<br>
	 * This method updates the internal coordinates and path.
	 * @param p The {@code Point2D} object to add.
	 */
	public void addPoint(Point2D p)
	{
		addPoint((float) p.getX(), (float) p.getY());
	}
	
	/**
	 * Adds a new point to the polygon.<br>
	 * This method updates the {@code npoints} count and internal paths.
	 * @param x The x-coordinate of the new point.
	 * @param y The y-coordinate of the new point.
	 */
	public void addPoint(float x, float y)
	{
		if (npoints == xpoints.length)
		{
			float[] tmp;
			
			tmp = new float[npoints * 2];
			System.arraycopy(xpoints, 0, tmp, 0, npoints);
			xpoints = tmp;
			
			tmp = new float[npoints * 2];
			System.arraycopy(ypoints, 0, tmp, 0, npoints);
			ypoints = tmp;
		}
		
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		npoints++;
		updatePath(x, y);
	}
	
	/**
	 * Retrieves the bounding box of this polygon.<br>
	 * This method returns a {@code Rectangle} object.<br>
	 * It returns {@code null} if no bounds are defined.
	 * @return The {@code Rectangle} representing the bounds, or {@code null}.
	 */
	@Override
	public Rectangle getBounds()
	{
		if (bounds == null)
		{
			return null;
		}
		
		return bounds.getBounds();
	}
	
	/**
	 * Updates the {@code closedPath} internal variable.<br>
	 * This method ensures that a closed version of the path exists if there is at least one point.<br>
	 * It clones the existing {@code path} and calls {@code closePath()} on it.
	 */
	private void updateComputingPath()
	{
		if (npoints >= 1)
		{
			if (closedPath == null)
			{
				closedPath = (GeneralPath) path.clone();
				closedPath.closePath();
			}
		}
	}
	
	/**
	 * Checks if a specific point is inside this polygon.<br>
	 * This method currently always returns {@code false}.
	 * @param p The {@link Point} to check.
	 * @return {@code false} for all inputs.
	 */
	public boolean contains(Point p)
	{
		return false;
	}
	
	/**
	 * Checks if a specific point is inside the polygon.<br>
	 * This method currently always returns {@code false}.
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean contains(double x, double y)
	{
		return false;
	}
	
	/**
	 * Checks if a specific point is inside the polygon.<br>
	 * This method currently always returns {@code false}.
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @return {@code false} for all inputs.
	 */
	public boolean contains(int x, int y)
	{
		return false;
	}
	
	/**
	 * Retrieves the bounding box of this {@link Polygon2D}.<br>
	 * This returns a {@code Rectangle2D} object.
	 * @return The {@code Rectangle2D} representing the bounds.
	 */
	@Override
	public Rectangle2D getBounds2D()
	{
		return bounds;
	}
	
	/**
	 * Checks if a specific point is inside this polygon.<br>
	 * This method currently always returns {@code false}.
	 * @param p The {@link Point2D} to check.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean contains(Point2D p)
	{
		return false;
	}
	
	/**
	 * Checks if this polygon overlaps with a rectangle.<br>
	 * The rectangle is defined by its top-left corner and dimensions.
	 * @param x The x-coordinate of the rectangle's top-left corner.
	 * @param y The y-coordinate of the rectangle's top-left corner.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @return {@code true} if the shapes intersect, otherwise {@code false}.
	 */
	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		if ((npoints <= 0) || !bounds.intersects(x, y, w, h))
		{
			return false;
		}
		
		updateComputingPath();
		return closedPath.intersects(x, y, w, h);
	}
	
	/**
	 * Checks if this polygon overlaps with a given rectangle.<br>
	 * This method uses the {@code double, double, double)} logic.
	 * @param r The {@code Rectangle2D} to check against.
	 * @return {@code true} if the shapes intersect, {@code false} otherwise.
	 */
	@Override
	public boolean intersects(Rectangle2D r)
	{
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	/**
	 * Checks if a rectangle is contained within this polygon.<br>
	 * The rectangle is defined by its top-left corner and dimensions.<br>
	 * This method currently always returns {@code false}.
	 * @param x The x-coordinate of the rectangle's top-left corner.
	 * @param y The y-coordinate of the rectangle's top-left corner.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		return false;
	}
	
	/**
	 * Checks if this polygon contains the specified rectangle.<br>
	 * This method currently always returns {@code false}.
	 * @param r The {@link Rectangle2D} to check.
	 * @return {@code false} for all inputs.
	 */
	@Override
	public boolean contains(Rectangle2D r)
	{
		return false;
	}
	
	/**
	 * Returns a {@link PathIterator} for the current shape.<br>
	 * This method applies the provided {@code AffineTransform} to the path.<br>
	 * It returns {@code null} if no path exists.
	 * @param at The {@code AffineTransform} to apply to the path.
	 * @return A {@link PathIterator} representing the transformed shape, or {@code null}.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at)
	{
		if (path == null)
		{
			return null;
		}
		
		return path.getPathIterator(at);
	}
	
	/*
	 * get the associated {@link Polygon2D}. This method take care that may be the last point can be equal to the first. In that case it must not be included in the Polygon, as polygons declare their first point only once.
	 */
	/**
	 * Creates a new {@link Polygon2D} instance based on the current coordinates.<br>
	 * This method copies all points from the existing coordinate arrays.<br>
	 * It ensures that the last point is added if it is not identical to the first point.
	 * @return A new {@code Polygon2D} object containing the same geometry.
	 */
	public Polygon2D getPolygon2D()
	{
		final Polygon2D pol = new Polygon2D();
		for (int i = 0; i < (npoints - 1); i++)
		{
			pol.addPoint(xpoints[i], ypoints[i]);
		}
		
		final Point2D.Double p0 = new Point2D.Double(xpoints[0], ypoints[0]);
		final Point2D.Double p1 = new Point2D.Double(xpoints[npoints - 1], ypoints[npoints - 1]);
		
		if (p0.distance(p1) > ASSUME_ZERO)
		{
			pol.addPoint(xpoints[npoints - 1], ypoints[npoints - 1]);
		}
		
		return pol;
	}
	
	/**
	 * Returns a {@link PathIterator} for the current shape.<br>
	 * This method applies an {@code AffineTransform} to the path.<br>
	 * It uses the internal {@code GeneralPath} to generate the iterator.
	 * @param at The {@code AffineTransform} to apply to the path.
	 * @param flatness The flatness value used for curve approximation.
	 * @return A new {@link PathIterator} representing the transformed shape.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		return path.getPathIterator(at);
	}
}
