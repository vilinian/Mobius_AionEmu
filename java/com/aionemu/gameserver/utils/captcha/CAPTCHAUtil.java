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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * This utility class provides methods for generating and validating {@code CAPTCHA} images.<br>
 * It helps prevent automated bots from accessing the game server by requiring human interaction.
 * @author Cura
 */
public class CAPTCHAUtil
{
	private final static int DEFAULT_WORD_LENGTH = 6;
	private final static String WORD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
	private final static int IMAGE_WIDTH = 160;
	private final static int IMAGE_HEIGHT = 80;
	private final static int TEXT_SIZE = 25;
	private final static String FONT_FAMILY_NAME = "Verdana";
	
	/**
	 * Generates a CAPTCHA image based on the provided text.<br>
	 * This method converts the generated image into a {@code ByteBuffer}.<br>
	 * It uses the internal {@code createImage} method to build the visual.
	 * @param word The text string to display in the CAPTCHA.
	 * @return A {@code ByteBuffer} containing the converted image data.
	 */
	public static ByteBuffer createCAPTCHA(String word)
	{
		ByteBuffer byteBuffer = null;
		final BufferedImage bImg = createImage(word);
		
		byteBuffer = DDSConverter.convertToDxt1NoTransparency(bImg);
		
		return byteBuffer;
	}
	
	/**
	 * Creates a {@code BufferedImage} containing the provided text.<br>
	 * This method draws the word on a black background with white letters.<br>
	 * It is used internally by {@code createCAPTCHA} to generate images.
	 * @param word The string to draw on the image.
	 * @return A {@code BufferedImage} object containing the rendered text, or {@code null} if an error occurs.
	 */
	private static BufferedImage createImage(String word)
	{
		BufferedImage bImg = null;
		
		try
		{
			// image create
			bImg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics2D g2 = bImg.createGraphics();
			
			// set backgroup color
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			
			// set font family, color, size, antialiasing
			final Font font = new Font(FONT_FAMILY_NAME, Font.BOLD, TEXT_SIZE);
			g2.setFont(font);
			g2.setColor(Color.WHITE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			// word drawing
			final char[] chars = word.toCharArray();
			final int x = 10;
			final int y = (IMAGE_HEIGHT / 2) + (TEXT_SIZE / 2);
			
			for (int i = 0; i < chars.length; i++)
			{
				final char ch = chars[i];
				g2.drawString(String.valueOf(ch), x + (font.getSize() * i), y + ((int) Math.pow(-1, i) * (TEXT_SIZE / 6)));
			}
			
			// resource dispose
			g2.dispose();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bImg = null;
		}
		
		return bImg;
	}
	
	/**
	 * Generates a random string for use in CAPTCHAs.<br>
	 * It uses the default length defined in {@code DEFAULT_WORD_LENGTH}.<br>
	 * This method calls the private {@code randomWord} helper.
	 * @return A randomly generated {@code String}.
	 */
	public static String getRandomWord()
	{
		return randomWord(DEFAULT_WORD_LENGTH);
	}
	
	/**
	 * Generates a random string of characters.<br>
	 * This method picks letters and numbers from the {@code WORD} constant.<br>
	 * It creates a string based on the specified length.
	 * @param wordLength The number of characters to generate.
	 * @return A new {@code String} containing the random characters.
	 */
	private static String randomWord(int wordLength)
	{
		final StringBuffer word = new StringBuffer();
		
		for (int i = 0; i < wordLength; i++)
		{
			final int index = Math.abs((int) (Math.random() * WORD.length()));
			final char ch = WORD.charAt(index);
			word.append(ch);
		}
		
		return word.toString();
	}
}
