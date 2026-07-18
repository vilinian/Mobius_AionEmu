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
package com.aionemu.gameserver.network;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.configs.main.SecurityConfig;

/**
 * This class provides mechanisms to protect the server from packet flooding attacks.<br>
 * It monitors incoming traffic and filters out requests that exceed defined limits.<br>
 * Use this class to ensure network stability against malicious activity.
 * @author KID
 */
public class PacketFloodFilter
{
	private static PacketFloodFilter pff = new PacketFloodFilter();
	private final Logger log = LoggerFactory.getLogger(PacketFloodFilter.class);
	
	/**
	 * Provides the global instance of the {@link PacketFloodFilter}.<br>
	 * This method follows the singleton pattern.
	 * @return The single shared instance of {@code PacketFloodFilter}.
	 */
	public static PacketFloodFilter getInstance()
	{
		return pff;
	}
	
	private int[] packets;
	private final short maxClientRequest = 0x2ff;
	
	/**
	 * Initializes the {@link PacketFloodFilter} instance.<br>
	 * It checks if packet flooding protection is enabled in {@code SecurityConfig}.<br>
	 * If enabled, it loads configuration data from {@code config/administration/pff.properties}.<br>
	 * This constructor sets up the internal packet limits for the server.
	 */
	public PacketFloodFilter()
	{
		if (SecurityConfig.PFF_ENABLE)
		{
			int cnt = 0;
			packets = new int[maxClientRequest];
			try
			{
				final java.util.Properties props = PropertiesUtils.load("config/administration/pff.properties");
				for (Object key : props.keySet())
				{
					final String str = (String) key;
					packets[Integer.decode(str)] = Integer.valueOf(props.getProperty(str).trim());
					cnt++;
				}
			}
			catch (IOException e)
			{
				log.error("Can't read pff.properties", e);
			}
			
			log.info("PacketFloodFilter initialized with " + cnt + " packets.");
		}
		else
		{
			log.info("PacketFloodFilter disabled.");
		}
	}
	
	/**
	 * Retrieves the current list of filtered packets.<br>
	 * This method returns the internal {@code packets} array.
	 * @return an {@code int[]} containing the packet data.
	 */
	public int[] getPackets()
	{
		return packets;
	}
}
