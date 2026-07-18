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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.HousingConfig;
import com.aionemu.gameserver.dao.HouseScriptsDAO;
import com.aionemu.gameserver.model.house.PlayerScript;
import com.aionemu.gameserver.utils.xml.CompressUtil;

/**
 * This class manages the scripts associated with a {@link Player}.<br>
 * It handles the loading and execution of various player-related behaviors.<br>
 * Use this class to interact with specific game logic tied to player actions.
 * @author Rolandas
 */
public class PlayerScripts
{
	private static final Logger logger = LoggerFactory.getLogger(PlayerScripts.class);
	private final Map<Integer, PlayerScript> scripts;
	private final int houseObjId;
	
	/**
	 * Creates a new instance of {@code PlayerScripts}.<br>
	 * This constructor initializes the internal script map.<br>
	 * It sets the unique identifier for the house object.
	 * @param houseObjectId The unique ID of the house object.
	 */
	public PlayerScripts(int houseObjectId)
	{
		scripts = new HashMap<>(8);
		for (int index = 0; index < 8; index++)
		{
			scripts.put(index, new PlayerScript());
		}
		
		houseObjId = houseObjectId;
	}
	
	/**
	 * Retrieves all scripts associated with this player.<br>
	 * The results are returned as an unmodifiable map.
	 * @return a {@code Map} where the key is the position and the value is a {@link PlayerScript}.
	 */
	public Map<Integer, PlayerScript> getScripts()
	{
		return Collections.unmodifiableMap(scripts);
	}
	
	/**
	 * Adds a new script to the player's house at a specific position.<br>
	 * This method compresses the provided {@code scriptXML} before saving it.<br>
	 * It updates the data for the existing script at the given index.
	 * @param position The unique identifier for the script location.
	 * @param scriptXML The raw XML string content of the script.
	 * @return {@code false} if the operation fails or the input is empty.
	 */
	public boolean addScript(int position, String scriptXML)
	{
		final PlayerScript script = scripts.get(position);
		
		if (scriptXML == null)
		{
			script.setData(null, -1);
		}
		else if ("".equals(scriptXML))
		{
			script.setData(new byte[0], 0);
		}
		
		if ((scriptXML == null) || "".equals(scriptXML))
		{
			return false;
		}
		
		try
		{
			byte[] bytes = CompressUtil.Compress(scriptXML);
			final int oldLength = bytes.length;
			bytes = Arrays.copyOf(bytes, bytes.length + 8);
			for (int i = oldLength; i < bytes.length; i++)
			{
				bytes[i] = -51; // Add NC shit bytes, without which fails to load :)
			}
			
			script.setData(bytes, scriptXML.length() * 2);
		}
		catch (Exception ex)
		{
			logger.error("Script compression failed: " + ex);
			return false;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the uncompressed script string for a specific position.<br>
	 * This method finds the {@link PlayerScript} at the given index.<br>
	 * It then decompresses the stored bytes into a readable format.
	 * @param position The unique identifier for the script location.
	 * @return The uncompressed script as a {@code String}, or {@code null} if not found.
	 */
	public String getUncompressedScript(int position)
	{
		if (!scripts.containsKey(position))
		{
			return null;
		}
		
		final PlayerScript script = scripts.get(position);
		byte[] bytes = null;
		
		script.readLock();
		bytes = script.getCompressedBytes();
		script.readUnlock();
		
		if (bytes == null)
		{
			return null;
		}
		
		if (bytes.length == 0)
		{
			return "";
		}
		
		try
		{
			return CompressUtil.Decompress(bytes);
		}
		catch (Exception ex)
		{
			logger.error("Script decompression failed: " + ex);
			return null;
		}
	}
	
	/**
	 * Adds or updates a script at the specified position.<br>
	 * This method handles decompression and database synchronization.
	 * @param position The index where the script should be placed.
	 * @param compressedXML The byte array containing the compressed XML data.
	 * @param uncompressedSize The expected size of the XML after decompression.
	 * @return {@code true} if the operation succeeded, or {@code false} otherwise.
	 */
	public boolean addScript(int position, byte[] compressedXML, int uncompressedSize)
	{
		String content = null;
		int size = -1;
		
		if (compressedXML == null)
		{
			// Nothing to do
		}
		else if (compressedXML.length == 0)
		{
			content = "";
			size = 0;
		}
		else
		{
			try
			{
				content = CompressUtil.Decompress(compressedXML);
				final byte[] bytes = content.getBytes("UTF-16LE");
				if (bytes.length != uncompressedSize)
				{
					return false;
				}
				
				size = uncompressedSize;
			}
			catch (Exception ex)
			{
				return false;
			}
		}
		
		final PlayerScript script = scripts.get(position);
		script.readLock();
		final byte[] bytes = script.getCompressedBytes();
		script.readUnlock();
		script.setData(compressedXML, size);
		
		if (bytes == null)
		{
			DAOManager.getDAO(HouseScriptsDAO.class).addScript(houseObjId, position, content);
		}
		else
		{
			DAOManager.getDAO(HouseScriptsDAO.class).updateScript(houseObjId, position, content);
		}
		
		if (HousingConfig.HOUSE_SCRIPT_DEBUG)
		{
			logger.info(content);
		}
		
		return true;
	}
	
	/**
	 * Removes a script from the specific position.<br>
	 * This method updates the internal data and deletes it from the database.
	 * @param position The unique index of the script to remove.
	 * @return {@code true} if the script was successfully removed, or {@code false} if no script existed at that position.
	 */
	public boolean removeScript(int position)
	{
		final PlayerScript script = scripts.get(position);
		
		script.readLock();
		final byte[] bytes = script.getCompressedBytes();
		script.readUnlock();
		
		if (bytes == null)
		{
			return false;
		}
		
		script.setData(null, -1);
		DAOManager.getDAO(HouseScriptsDAO.class).deleteScript(houseObjId, position);
		
		return true;
	}
	
	/**
	 * Returns the total size of the scripts.<br>
	 * This value is currently fixed at {@code 8}.
	 * @return The size of the scripts as an {@code int}.
	 */
	public int getSize()
	{
		return 8;
	}
}
