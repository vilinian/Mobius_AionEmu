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
package com.aionemu.commons.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods to retrieve and log various system information.<br>
 * It helps in gathering environment details for debugging or logging purposes.
 * @author lord_rex This class is for get/log system informations.
 */
public class AEInfos
{
	private static final Logger log = LoggerFactory.getLogger(AEInfos.class);
	
	/**
	 * Retrieves current memory statistics from the {@code Runtime}.<br>
	 * This method calculates various memory metrics like used and allocated space.<br>
	 * It returns a formatted array of strings for display purposes.
	 * @return A {@code String[]} containing the formatted memory information.
	 */
	public static String[] getMemoryInfo()
	{
		final double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is the upper limit the jvm can use
		final double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory the size of the current allocation pool
		final double nonAllocated = max - allocated; // non allocated memory till jvm limit
		final double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory the unused memory in the allocation pool
		final double used = allocated - cached; // really used memory
		final double useable = max - used; // allocated, but non-used and non-allocated memory
		final DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		final DecimalFormat df2 = new DecimalFormat(" # 'KB'");
		return new String[]
		{
			//
			"+----", //
			"| Global Memory Informations at " + getRealTime().toString() + ":", //
			"|    |", //
			"| Allowed Memory:" + df2.format(max), //
			"|    |= Allocated Memory:" + df2.format(allocated) + df.format((allocated / max) * 100), //
			"|    |= Non-Allocated Memory:" + df2.format(nonAllocated) + df.format((nonAllocated / max) * 100), //
			"| Allocated Memory:" + df2.format(allocated), //
			"|    |= Used Memory:" + df2.format(used) + df.format((used / max) * 100), //
			"|    |= Unused (cached) Memory:" + df2.format(cached) + df.format((cached / max) * 100), //
			"| Useable Memory:" + df2.format(useable) + df.format((useable / max) * 100), //
			"+----" //
		};
	}
	
	/**
	 * Retrieves basic information about the current CPU.<br>
	 * This method uses {@code Runtime.getRuntime()} and {@code System.getenv()}.<br>
	 * It returns a list of strings containing processor details.
	 * @return An array of {@code String} objects containing CPU data.
	 */
	public static String[] getCPUInfo()
	{
		return new String[]
		{
			//
			"Available CPU(s): " + Runtime.getRuntime().availableProcessors(), //
			"Processor(s) Identifier: " + System.getenv("PROCESSOR_IDENTIFIER"), //
			"..................................................", //
			".................................................." //
		};
	}
	
	/**
	 * Retrieves basic information about the current operating system.<br>
	 * This method uses {@code System.getProperty} to fetch details.
	 * @return a {@code String[]} containing the OS name, version, and architecture.
	 */
	public static String[] getOSInfo()
	{
		return new String[]
		{
			//
			"OS: " + System.getProperty("os.name") + " Build: " + System.getProperty("os.version"), //
			"OS Arch: " + System.getProperty("os.arch"), //
			"..................................................", //
			".................................................." //
		};
	}
	
	/**
	 * Retrieves information about the current Java Runtime Environment.<br>
	 * This method collects details like the runtime name and version.
	 * @return a {@code String[]} containing the formatted JRE information.
	 */
	public static String[] getJREInfo()
	{
		return new String[]
		{
			//
			"Java Platform Information", //
			"Java Runtime  Name: " + System.getProperty("java.runtime.name"), //
			"Java Version: " + System.getProperty("java.version"), //
			"Java Class Version: " + System.getProperty("java.class.version"), //
			"..................................................", //
			".................................................." //
		};
	}
	
	/**
	 * Retrieves basic information about the current Java Virtual Machine.<br>
	 * This method collects properties like the JVM name, version, and vendor.
	 * @return a {@code String[]} containing various JVM details.
	 */
	public static String[] getJVMInfo()
	{
		return new String[]
		{
			//
			"Virtual Machine Information (JVM)", //
			"JVM Name: " + System.getProperty("java.vm.name"), //
			"JVM installation directory: " + System.getProperty("java.home"), //
			"JVM version: " + System.getProperty("java.vm.version"), //
			"JVM Vendor: " + System.getProperty("java.vm.vendor"), //
			"JVM Info: " + System.getProperty("java.vm.info"), //
			"..................................................", //
			".................................................." //
		};
	}
	
	/**
	 * Gets the current system time.<br>
	 * Returns the time formatted as {@code H:mm:ss}.
	 * @return A {@code String} representing the current time.
	 */
	public static String getRealTime()
	{
		final SimpleDateFormat String = new SimpleDateFormat("H:mm:ss");
		return String.format(new Date());
	}
	
	/**
	 * Prints the current memory information to the logs.<br>
	 * This method calls {@code getMemoryInfo} and iterates through the results.<br>
	 * Each piece of information is logged as a new entry.
	 */
	public static void printMemoryInfo()
	{
		for (String line : getMemoryInfo())
		{
			log.info(line);
		}
	}
	
	/**
	 * Prints the current CPU information to the logs.<br>
	 * This method calls {@code getCPUInfo} and iterates through the results.<br>
	 * Each piece of information is logged as a new entry.
	 */
	public static void printCPUInfo()
	{
		for (String line : getCPUInfo())
		{
			log.info(line);
		}
	}
	
	/**
	 * Prints the operating system information to the log.<br>
	 * This method calls {@code getOSInfo} and iterates through the results.<br>
	 * Each piece of information is logged as a new entry.
	 */
	public static void printOSInfo()
	{
		for (String line : getOSInfo())
		{
			log.info(line);
		}
	}
	
	/**
	 * Prints the Java Runtime Environment information to the logs.<br>
	 * This method calls {@code getJREInfo} and iterates through the results.<br>
	 * Each piece of information is logged as an info message.
	 */
	public static void printJREInfo()
	{
		for (String line : getJREInfo())
		{
			log.info(line);
		}
	}
	
	/**
	 * Prints the current {@code JVM} information to the logs.<br>
	 * This method calls {@code getJVMInfo} and iterates through the results.<br>
	 * Each piece of information is logged as an {@code info} level message.
	 */
	public static void printJVMInfo()
	{
		for (String line : getJVMInfo())
		{
			log.info(line);
		}
	}
	
	/**
	 * Prints the current system time to the log.<br>
	 * This method calls {@code getRealTime} and logs the result.
	 */
	public static void printRealTime()
	{
		log.info(getRealTime().toString());
	}
	
	/**
	 * Prints all system information to the console.<br>
	 * This method calls {@code printOSInfo}, {@code printCPUInfo}, {@code printJREInfo}, {@code printJVMInfo}, and {@code printMemoryInfo}.
	 */
	public static void printAllInfos()
	{
		printOSInfo();
		printCPUInfo();
		printJREInfo();
		printJVMInfo();
		printMemoryInfo();
	}
}
