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
package com.aionemu.loginserver.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class provides general helper methods for the {@code loginserver} project.<br>
 * It contains common utility functions to simplify repetitive tasks across the application.
 * @author lord_rex
 */
public class Util
{
	/**
	 * Prints a formatted section header to the console.<br>
	 * It wraps the input string in brackets if it is not empty.<br>
	 * The method pads the output with equals signs to reach a specific length.
	 * @param s The text to be displayed as a section header.
	 */
	public static void printSection(String s)
	{
		s = "-[ " + s + " ]";
		
		while (s.length() < 119)
		{
			s = "=" + s;
		}
		
		System.out.println(s);
	}
	
	/**
	 * Prints the project credits and version information to the console.<br>
	 * This method displays an ASCII art banner and reads data from {@code info.txt}.
	 */
	public static void printCredits()
	{
		System.out.println("=================================================[  ### Credits ###  ]=================================================");
		System.out.println("");
		System.out.println("                            ooo        ooooo            .o8        o8o");
		System.out.println("                            `88.       .888'           \"888        `\"'");
		System.out.println("                             888b     d'888   .ooooo.   888oooo.  oooo  oooo  oooo   .oooo.o");
		System.out.println("                             8 Y88. .P  888  d88' `88b  d88' `88b `888  `888  `888  d88(  \"8");
		System.out.println("                             8  `888'   888  888   888  888   888  888   888   888  `\"Y88b.");
		System.out.println("                             8    Y     888  888   888  888   888  888   888   888  o.  )88b");
		System.out.println("                            o8o        o888o `Y8bod8P'  `Y8bod8P' o888o  `V88V\"V8P' 8\"\"888P'");
		System.out.println("");
		System.out.println("                       .o.        o8o                        oooooooooooo");
		System.out.println("                      .888.       `\"'                        `888'     `8");
		System.out.println("                     .8\"888.     oooo   .ooooo.  ooo. .oo.    888         ooo. .oo.  .oo.   oooo  oooo");
		System.out.println("                    .8' `888.    `888  d88' `88b `888P\"Y88b   888oooo8    `888P\"Y88bP\"Y88b  `888  `888");
		System.out.println("                   .88ooo8888.    888  888   888  888   888   888    \"     888   888   888   888   888");
		System.out.println("                  .8'     `888.   888  888   888  888   888   888       o  888   888   888   888   888");
		System.out.println("                 o88o     o8888o o888o `Y8bod8P' o888o o888o o888ooooood8 o888o o888o o888o  `V88V\"V8P'");
		System.out.println("");
		System.out.println("############################### This is Mobius AionEmu - based on an AionGermany source ###############################");
		System.out.println("");
		System.out.println("");
		System.out.println("\t\t\t\t\tThanks to all who helped this project!");
		
		String version = "";
		String minor = "";
		String copyright = "";
		try (BufferedReader br = new BufferedReader(new FileReader("./config/info.txt")))
		{
			version = br.readLine();
			minor = br.readLine();
			copyright = br.readLine();
		}
		catch (IOException e)
		{
			// ponytail: info.txt optional on login; skip version lines if absent
		}
		
		System.out.println("\t\t\t\t\t\tMajor Patch: " + version);
		System.out.println("\t\t\t\t\t\tMinor Patch: " + minor);
		System.out.println("\t\t\t\t\t\tCopyright: " + copyright);
		System.out.println("");
	}
}
