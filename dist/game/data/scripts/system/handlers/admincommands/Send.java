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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This admin command allows for sending custom packets from the server to a client.<br>
 * It uses XML mappings located in the {@code ./data/packets} folder to determine packet data.<br>
 * The recipient is determined by the admin's target, defaulting to the admin if no valid player is selected.
 * @author Aquanox
 */
public class Send extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@link Send} class.<br>
	 * This constructor sets up the JAXB unmarshaller for packet processing.
	 */
	public Send()
	{
		super("send");
		
		// init unmrshaller once.
		try
		{
			unmarshaller = JAXBContext.newInstance(Packets.class, Packet.class, Part.class).createUnmarshaller();
		}
		catch (Exception e)
		{
			throw new GameServerError("Failed to initialize unmarshaller.", e);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Send.class);
	private static final File FOLDER = new File("./data/packets");
	private Unmarshaller unmarshaller;
	
	/**
	 * Executes the command to send custom packets to a player.<br>
	 * It loads packet data from an XML file based on the provided mapping name.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the mapping name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length != 1)
		{
			PacketSendUtility.sendMessage(admin, "Example: //send [file] ");
			return;
		}
		
		final String mappingName = params[0];
		final Player target = getTargetPlayer(admin);
		
		// logger.debug("Mapping: " + mappingName);
		// logger.debug("Target: " + target);
		final File packetsData = new File(FOLDER, mappingName + ".xml");
		
		if (!packetsData.exists())
		{
			PacketSendUtility.sendMessage(admin, "Mapping with name " + mappingName + " not found");
			return;
		}
		
		final Packets packetsTemplate;
		
		try
		{
			packetsTemplate = (Packets) unmarshaller.unmarshal(packetsData);
		}
		catch (JAXBException e)
		{
			logger.error("Unmarshalling error", e);
			return;
		}
		
		if (packetsTemplate.getPackets().isEmpty())
		{
			PacketSendUtility.sendMessage(admin, "No packets to send.");
			return;
		}
		
		send(admin, target, packetsTemplate);
	}
	
	/**
	 * Sends a collection of custom packets to a specific player.<br>
	 * This method replaces placeholders with the correct object IDs.<br>
	 * It schedules each packet for delivery based on defined delays.
	 * @param sender The {@code Player} who initiated the command.
	 * @param target The {@code Player} who will receive the packets.
	 * @param packets The collection of {@code Packets} to be sent.
	 */
	private void send(Player sender, Player target, Packets packets)
	{
		final String senderObjectId = String.valueOf(sender.getObjectId());
		final String targetObjectId = String.valueOf(target.getObjectId());
		
		long delay = 0;
		for (Packet packetTemplate : packets)
		{
			// logger.debug("Processing: " + packetTemplate);
			
			final SM_CUSTOM_PACKET packet = new SM_CUSTOM_PACKET(packetTemplate.getOpcode());
			
			for (Part part : packetTemplate.getParts())
			{
				final PacketElementType byCode = PacketElementType.getByCode(part.getType());
				
				String value = part.getValue();
				
				if (value.indexOf("${objectId}") != -1)
				{
					value = value.replace("${objectId}", targetObjectId);
				}
				
				if (value.indexOf("${senderObjectId}") != -1)
				{
					value = value.replace("${senderObjectId}", senderObjectId);
				}
				
				if (value.indexOf("${targetObjectId}") != -1)
				{
					value = value.replace("${targetObjectId}", targetObjectId);
				}
				
				if (part.getRepeatCount() == 1) // skip loop
				{
					packet.addElement(byCode, value);
				}
				else
				{
					for (int i = 0; i < part.getRepeatCount(); i++)
					{
						packet.addElement(byCode, value);
					}
				}
			}
			
			delay += packetTemplate.getDelay();
			
			ThreadPoolManager.getInstance().schedule(() -> PacketSendUtility.sendPacket(target, packet), delay);
			
			delay += packets.getDelay();
		}
	}
	
	/**
	 * Determines which {@link Player} should receive a packet.<br>
	 * It checks if the {@code admin} has a valid target.<br>
	 * If no target exists, it returns the {@code admin} instead.
	 * @param admin The player who is executing the command.
	 * @return The targeted {@link Player} or the {@code admin} if no target is found.
	 */
	private Player getTargetPlayer(Player admin)
	{
		if (admin.getTarget() instanceof Player)
		{
			return (Player) admin.getTarget();
		}
		
		return admin;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "packets")
	private static class Packets implements Iterable<Packet>
	{
		@XmlElement(name = "packet")
		private final List<Packet> packets = new ArrayList<>();
		@XmlAttribute(name = "delay")
		private final long delay = -1;
		
		public long getDelay()
		{
			return delay;
		}
		
		public List<Packet> getPackets()
		{
			return packets;
		}
		
		@SuppressWarnings("unused")
		public boolean add(Packet packet)
		{
			return packets.add(packet);
		}
		
		@Override
		public Iterator<Packet> iterator()
		{
			return packets.iterator();
		}
		
		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Packets");
			sb.append("{delay=").append(delay);
			sb.append(", packets=").append(packets);
			sb.append('}');
			return sb.toString();
		}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "packet")
	private static class Packet
	{
		@XmlElement(name = "part")
		private final Collection<Part> parts = new ArrayList<>();
		@XmlAttribute(name = "opcode")
		private final String opcode = "-1";
		@XmlAttribute(name = "delay")
		private final long delay = 0;
		
		public int getOpcode()
		{
			return Integer.decode(opcode);
		}
		
		public Collection<Part> getParts()
		{
			return parts;
		}
		
		public long getDelay()
		{
			return delay;
		}
		
		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Packet");
			sb.append("{opcode=").append(opcode);
			sb.append(", parts=").append(parts);
			sb.append('}');
			return sb.toString();
		}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "part")
	private static class Part
	{
		@XmlAttribute(name = "type", required = true)
		private final String type = null;
		@XmlAttribute(name = "value", required = true)
		private final String value = null;
		@XmlAttribute(name = "repeat", required = true)
		private final int repeatCount = 1;
		
		public char getType()
		{
			return type.charAt(0);
		}
		
		public String getValue()
		{
			return value;
		}
		
		public int getRepeatCount()
		{
			return repeatCount;
		}
		
		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Part");
			sb.append("{type='").append(type).append('\'');
			sb.append(", value='").append(value).append('\'');
			sb.append(", repeatCount=").append(repeatCount);
			sb.append('}');
			return sb.toString();
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
		// TODO Auto-generated method stub
	}
}
