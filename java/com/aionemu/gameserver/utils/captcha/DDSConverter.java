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
package com.aionemu.gameserver.utils.captcha;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This utility class handles the conversion of {@code BufferedImage} objects into {@code DDS} image formats.<br>
 * It is primarily used to process and save captcha images for the game server.
 * @author Cura
 */
public class DDSConverter
{
	private static final int DDSD_CAPS = 0x0001;
	private static final int DDSD_HEIGHT = 0x0002;
	private static final int DDSD_WIDTH = 0x0004;
	private static final int DDSD_PIXELFORMAT = 0x1000;
	private static final int DDSD_MIPMAPCOUNT = 0x20000;
	private static final int DDSD_LINEARSIZE = 0x80000;
	private static final int DDPF_FOURCC = 0x0004;
	private static final int DDSCAPS_TEXTURE = 0x1000;
	
	protected static class Color
	{
		private int r, g, b;
		
		public Color()
		{
			r = g = b = 0;
		}
		
		public Color(int r, int g, int b)
		{
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			
			if ((o == null) || (getClass() != o.getClass()))
			{
				return false;
			}
			
			final Color color = (Color) o;
			
			// noinspection RedundantIfStatement
			if ((b != color.b) || (g != color.g) || (r != color.r))
			{
				return false;
			}
			
			return true;
		}
		
		@Override
		public int hashCode()
		{
			int result;
			result = r;
			result = (29 * result) + g;
			result = (29 * result) + b;
			return result;
		}
	}
	
	/**
	 * Converts a {@link BufferedImage} into a DXT1 compressed format.<br>
	 * This method does not support transparency.<br>
	 * It returns a {@code ByteBuffer} containing the compressed data.
	 * @param image The source image to convert.
	 * @return A {@code ByteBuffer} of the DXT1 data, or {@code null} if the input is {@code null}.
	 */
	public static ByteBuffer convertToDxt1NoTransparency(BufferedImage image)
	{
		if (image == null)
		{
			return null;
		}
		
		final int[] pixels = new int[16];
		final int bufferSize = 128 + ((image.getWidth() * image.getHeight()) / 2);
		final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buildHeaderDxt1(buffer, image.getWidth(), image.getHeight());
		
		final int numTilesWide = image.getWidth() / 4;
		final int numTilesHigh = image.getHeight() / 4;
		for (int i = 0; i < numTilesHigh; i++)
		{
			for (int j = 0; j < numTilesWide; j++)
			{
				final java.awt.image.BufferedImage originalTile = image.getSubimage(j * 4, i * 4, 4, 4);
				originalTile.getRGB(0, 0, 4, 4, pixels, 0, 4);
				final Color[] colors = getColors888(pixels);
				
				for (int k = 0; k < pixels.length; k++)
				{
					pixels[k] = getPixel565(colors[k]);
					colors[k] = getColor565(pixels[k]);
				}
				
				final int[] extremaIndices = determineExtremeColors(colors);
				if (pixels[extremaIndices[0]] < pixels[extremaIndices[1]])
				{
					final int t = extremaIndices[0];
					extremaIndices[0] = extremaIndices[1];
					extremaIndices[1] = t;
				}
				
				buffer.putShort((short) pixels[extremaIndices[0]]);
				buffer.putShort((short) pixels[extremaIndices[1]]);
				
				final long bitmask = computeBitMask(colors, extremaIndices);
				buffer.putInt((int) bitmask);
			}
		}
		
		return buffer;
	}
	
	/**
	 * Constructs the DXT1 header into a {@code ByteBuffer}.<br>
	 * This method populates the buffer with specific DDS format data.<br>
	 * It uses the provided {@code width} and {@code height} to set dimensions.
	 * @param buffer The {@code ByteBuffer} where the header will be written.
	 * @param width The width of the image in pixels.
	 * @param height The height of the image in pixels.
	 */
	protected static void buildHeaderDxt1(ByteBuffer buffer, int width, int height)
	{
		buffer.rewind();
		buffer.put((byte) 'D');
		buffer.put((byte) 'D');
		buffer.put((byte) 'S');
		buffer.put((byte) ' ');
		buffer.putInt(124);
		final int flag = DDSD_CAPS | DDSD_HEIGHT | DDSD_WIDTH | DDSD_PIXELFORMAT | DDSD_MIPMAPCOUNT | DDSD_LINEARSIZE;
		buffer.putInt(flag);
		buffer.putInt(height);
		buffer.putInt(width);
		buffer.putInt((width * height) / 2);
		buffer.putInt(0); // depth
		buffer.putInt(0); // mipmap count
		buffer.position(buffer.position() + 44); // 11 unused double-words
		buffer.putInt(32); // pixel format size
		buffer.putInt(DDPF_FOURCC);
		buffer.put((byte) 'D');
		buffer.put((byte) 'X');
		buffer.put((byte) 'T');
		buffer.put((byte) '1');
		buffer.putInt(0); // bits per pixel for RGB (non-compressed) formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // rgb bit masks for RGB formats
		buffer.putInt(0); // alpha mask for RGB formats
		buffer.putInt(DDSCAPS_TEXTURE);
		buffer.putInt(0); // ddsCaps2
		buffer.position(buffer.position() + 12); // 3 unused double-words
	}
	
	/**
	 * Finds the two colors that are furthest apart in the provided array.<br>
	 * This method uses {@code Color)} to calculate the distance between every pair.<br>
	 * It returns the indices of these extreme colors.
	 * @param colors An array of {@code Color} objects to analyze.
	 * @return An {@code int[]} containing the two indices of the most distant colors.
	 */
	protected static int[] determineExtremeColors(Color[] colors)
	{
		int farthest = Integer.MIN_VALUE;
		final int[] ex = new int[2];
		
		for (int i = 0; i < (colors.length - 1); i++)
		{
			for (int j = i + 1; j < colors.length; j++)
			{
				final int d = distance(colors[i], colors[j]);
				if (d > farthest)
				{
					farthest = d;
					ex[0] = i;
					ex[1] = j;
				}
			}
		}
		
		return ex;
	}
	
	/**
	 * Calculates a bitmask based on the proximity of colors to extreme points.<br>
	 * This method identifies which color point each color in the array is closest to.<br>
	 * It uses {@code Color)} to determine these relationships.
	 * @param colors An array of {@code Color} objects to be processed.
	 * @param extremaIndices An array of indices pointing to the extreme colors in the {@code colors} array.
	 * @return A {@code long} bitmask representing the closest color point for each input color.
	 */
	protected static long computeBitMask(Color[] colors, int[] extremaIndices)
	{
		final Color[] colorPoints = new Color[]
		{
			null,
			null,
			new Color(),
			new Color()
		};
		colorPoints[0] = colors[extremaIndices[0]];
		colorPoints[1] = colors[extremaIndices[1]];
		if (colorPoints[0].equals(colorPoints[1]))
		{
			return 0;
		}
		
		colorPoints[2].r = ((2 * colorPoints[0].r) + colorPoints[1].r + 1) / 3;
		colorPoints[2].g = ((2 * colorPoints[0].g) + colorPoints[1].g + 1) / 3;
		colorPoints[2].b = ((2 * colorPoints[0].b) + colorPoints[1].b + 1) / 3;
		colorPoints[3].r = (colorPoints[0].r + (2 * colorPoints[1].r) + 1) / 3;
		colorPoints[3].g = (colorPoints[0].g + (2 * colorPoints[1].g) + 1) / 3;
		colorPoints[3].b = (colorPoints[0].b + (2 * colorPoints[1].b) + 1) / 3;
		
		long bitmask = 0;
		for (int i = 0; i < colors.length; i++)
		{
			int closest = Integer.MAX_VALUE;
			int mask = 0;
			for (int j = 0; j < colorPoints.length; j++)
			{
				final int d = distance(colors[i], colorPoints[j]);
				if (d < closest)
				{
					closest = d;
					mask = j;
				}
			}
			
			bitmask |= mask << (i * 2);
		}
		
		return bitmask;
	}
	
	/**
	 * Converts a {@code Color} object into a 16-bit RGB565 pixel value.<br>
	 * This method reduces the color depth by shifting bits.
	 * @param color The {@code Color} object to convert.
	 * @return The resulting 16-bit integer in {@code RGB565} format.
	 */
	protected static int getPixel565(Color color)
	{
		final int r = color.r >> 3;
		final int g = color.g >> 2;
		final int b = color.b >> 3;
		return (r << 11) | (g << 5) | b;
	}
	
	/**
	 * Converts a {@code int} pixel value to a {@code Color} object.<br>
	 * This method extracts the red, green, and blue components from a 565 format integer.
	 * @param pixel The raw {@code int} pixel value to convert.
	 * @return A new {@code Color} object representing the extracted values.
	 */
	protected static Color getColor565(int pixel)
	{
		final Color color = new Color();
		
		color.r = (int) (((long) pixel) & 0xf800) >> 11;
		color.g = (int) (((long) pixel) & 0x07e0) >> 5;
		color.b = (int) (((long) pixel) & 0x001f);
		
		return color;
	}
	
	/**
	 * Converts an array of raw pixel values into an array of {@code Color} objects.<br>
	 * This method extracts the red, green, and blue components from each integer.
	 * @param pixels An array of integers representing the image pixels.
	 * @return A new array containing the converted {@code Color} objects.
	 */
	protected static Color[] getColors888(int[] pixels)
	{
		final Color[] colors = new Color[pixels.length];
		
		for (int i = 0; i < pixels.length; i++)
		{
			colors[i] = new Color();
			colors[i].r = (int) (((long) pixels[i]) & 0xff0000) >> 16;
			colors[i].g = (int) (((long) pixels[i]) & 0x00ff00) >> 8;
			colors[i].b = (int) (((long) pixels[i]) & 0x0000ff);
		}
		
		return colors;
	}
	
	/**
	 * Calculates the squared distance between two {@code Color} objects.<br>
	 * This method compares the red, green, and blue components of both colors.<br>
	 * It returns a value representing how different the two colors are.
	 * @param ca The first color to compare.
	 * @param cb The second color to compare.
	 * @return The squared distance between {@code ca} and {@code cb}.
	 */
	protected static int distance(Color ca, Color cb)
	{
		return ((cb.r - ca.r) * (cb.r - ca.r)) + ((cb.g - ca.g) * (cb.g - ca.g)) + ((cb.b - ca.b) * (cb.b - ca.b));
	}
}
