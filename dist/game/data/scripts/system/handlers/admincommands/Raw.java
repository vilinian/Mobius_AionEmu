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
package system.handlers.admincommands;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This class allows administrators to send a network packet in its raw format.<br>
 * It provides a way to manually inject custom data into the game stream.
 * @author Luno
 * @author Aquanox
 */
public class Raw extends AdminCommand
{
	private static final File ROOT = new File("data/packets/");
	private static final Logger logger = LoggerFactory.getLogger(Raw.class);
	
	/**
	 * Creates a new instance of the {@link Raw} command.<br>
	 * This constructor initializes the admin command with the name {@code raw}.
	 */
	public Raw()
	{
		super("raw");
	}
	
	/**
	 * Executes a command to send a raw packet from a file.<br>
	 * It reads the content of a text file and sends it as an {@code SM_CUSTOM_PACKET}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element must be the filename without the extension.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length != 1)
		{
			PacketSendUtility.sendMessage(admin, "Usage: //raw [name]");
			return;
		}
		
		final File file = new File(ROOT, params[0] + ".txt");
		
		if (!file.exists() || !file.canRead())
		{
			PacketSendUtility.sendMessage(admin, "Wrong file selected.");
			return;
		}
		
		try
		{
			final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			
			SM_CUSTOM_PACKET packet = null;
			PacketSendUtility.sendMessage(admin, "lines " + lines.size());
			boolean init = false;
			for (int r = 0; r < lines.size(); r++)
			{
				final String row = lines.get(r);
				final String[] tokens = row.substring(0, 48).trim().split(" ");
				final int len = tokens.length;
				
				for (int i = 0; i < len; i++)
				{
					if (!init)
					{
						if (i == 1)
						{
							packet = new SM_CUSTOM_PACKET(Integer.decode("0x" + tokens[i] + tokens[i - 1]));
							init = true;
						}
					}
					else if (((r > 0) || (i > 4)) && (packet != null))
					{
						packet.addElement(PacketElementType.C, "0x" + tokens[i]);
					}
				}
			}
			
			if (packet != null)
			{
				PacketSendUtility.sendMessage(admin, "Packet send..");
				PacketSendUtility.sendPacket(admin, packet);
			}
		}
		catch (Exception e)
		{
			PacketSendUtility.sendMessage(admin, "An error has occurred.");
			logger.warn("IO Error.", e);
		}
	}
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "Usage: //raw [name]");
	}
}
