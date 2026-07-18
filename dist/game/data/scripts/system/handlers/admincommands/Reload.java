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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.EventData;
import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.dataholders.StaticData;
import com.aionemu.gameserver.dataholders.XMLQuests;
import com.aionemu.gameserver.dataholders.loadingutils.XmlValidationHandler;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.events.EventService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;

/**
 * Handles the admin command to reload game data files.<br>
 * This class refreshes various {@link StaticData} types such as skills and quests without restarting the server.
 * @author MrPoke, reworked by Voidstar
 */
public class Reload extends AdminCommand
{
	private static final Logger log = LoggerFactory.getLogger(Reload.class);
	private static final String SYNTAX = "syntax //reload <quest | skill | npc | items | portal | commands | drop | gameshop | events | config>";
	
	/**
	 * Initializes the {@code Reload} command handler.<br>
	 * This constructor sets up the command name as {@code reload}.
	 */
	public Reload()
	{
		super("reload");
	}
	
	/**
	 * Reloads various game data components based on the provided parameter.<br>
	 * It supports reloading quests, skills, npcs, items, portals, commands, configs, drops, events, and instances.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element determines which data to reload.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length != 1))
		{
			PacketSendUtility.sendMessage(admin, SYNTAX);
			return;
		}
		
		if (params[0].equals("quest"))
		{
			final File xml = new File("./data/static_data/quest_data/quest_data.xml");
			final File dir = new File("./data/static_data/quest_script_data");
			try
			{
				QuestEngine.getInstance().shutdown();
				final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				final Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				final QuestsData newQuestData = (QuestsData) un.unmarshal(xml);
				final QuestsData questsData = DataManager.QUEST_DATA;
				questsData.setQuestsData(newQuestData.getQuestsData());
				final XMLQuests questScriptsData = DataManager.XML_QUESTS;
				questScriptsData.getQuest().clear();
				for (File file : listFiles(dir, true))
				{
					final XMLQuests data = ((XMLQuests) un.unmarshal(file));
					if (data != null)
					{
						if (data.getQuest() != null)
						{
							questScriptsData.getQuest().addAll(data.getQuest());
						}
					}
				}
				
				QuestEngine.getInstance().reload(null);
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(admin, "Quest reload failed!");
				log.error("quest reload fail", e);
			}
			finally
			{
				PacketSendUtility.sendMessage(admin, "Quest reload Success!");
			}
		}
		else if (params[0].equals("skill"))
		{
			final File dir = new File("./data/static_data/skills");
			try
			{
				final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				final Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				final List<SkillTemplate> newTemplates = new ArrayList<>();
				for (File file : listFiles(dir, true))
				{
					final SkillData data = (SkillData) un.unmarshal(file);
					if (data != null)
					{
						newTemplates.addAll(data.getSkillTemplates());
					}
				}
				
				DataManager.SKILL_DATA.setSkillTemplates(newTemplates);
				DataManager.SKILL_DATA.initializeCooldownGroups();
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(admin, "Skill reload failed!");
				log.error("Skill reload failed!", e);
			}
			finally
			{
				PacketSendUtility.sendMessage(admin, "Skill reload Success!");
			}
		}
		else if (params[0].equals("npc"))
		{
			DataManager.NPC_DATA.reload(admin);
		}
		else if (params[0].equals("items"))
		{
			DataManager.ITEM_DATA.reload(admin);
		}
		else if (params[0].equals("portal"))
		{
			new File("./data/static_data/portals");
			try
			{
				final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				final Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				
				// Create a new list of portal templates.
				// for (File file : listFiles(dir, true)) {
				// PortalData data = (PortalData) un.unmarshal(file);
				// if (data != null && data.getPortals() != null)
				// newTemplates.addAll(data.getPortals());
				// }
				// DataManager.PORTAL_DATA.setPortals(newTemplates);
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(admin, "Portal reload failed!");
				log.error("Portal reload failed!", e);
			}
			finally
			{
				PacketSendUtility.sendMessage(admin, "Portal reload Success!");
			}
		}
		else if (params[0].equals("commands"))
		{
			ChatProcessor.getInstance().reload();
			PacketSendUtility.sendMessage(admin, "Admin commands successfully reloaded!");
		}
		else if (params[0].equals("config"))
		{
			Config.reload();
			PacketSendUtility.sendMessage(admin, "Configs successfully reloaded!");
		}
		else if (params[0].equals("drop"))
		{
			DataManager.NPC_DATA.getNpcData().forEachValue(object ->
			{
				object.setNpcDrop(null);
				return true;
			});
			final File dir = new File("./data/static_data/drops");
			try
			{
				final JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				final Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				final List<NpcDrop> npcDrop = new ArrayList<>();
				for (File file : listFiles(dir, true))
				{
					final NpcDropData data = (NpcDropData) un.unmarshal(file);
					if ((data != null) && (data.getNpcDrop() != null))
					{
						npcDrop.addAll(data.getNpcDrop());
					}
				}
				
				DataManager.NPC_DROP_DATA.setNpcDrop(npcDrop);
				DropRegistrationService.getInstance().init();
			}
			catch (Exception e)
			{
				throw new Error("Drop reload failed!", e);
			}
			finally
			{
				PacketSendUtility.sendMessage(admin, "NpcDrops successfully reloaded!");
			}
		}
		
		else if (params[0].equals("events"))
		{
			final File eventXml = new File("./data/static_data/events_config/events_config.xml");
			EventData data = null;
			try
			{
				final JAXBContext jc = JAXBContext.newInstance(EventData.class);
				final Unmarshaller un = jc.createUnmarshaller();
				un.setEventHandler(new XmlValidationHandler());
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				data = (EventData) un.unmarshal(eventXml);
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(admin, "Event reload failed! Keeping the last version ...");
				log.error("Event reload failed!", e);
				return;
			}
			
			if (data != null)
			{
				EventService.getInstance().stop();
				String text = data.getActiveText();
				if ((text == null) || (text.trim().length() == 0))
				{
					text = "NONE";
				}
				
				DataManager.EVENT_DATA.setAllEvents(data.getAllEvents(), data.getActiveText());
				PacketSendUtility.sendMessage(admin, "Active events: " + text);
				EventService.getInstance().start();
			}
		}
		else if (params[0].equals("instance"))
		{
			try
			{
				InstanceEngine.getInstance().reload();
				PacketSendUtility.sendMessage(admin, "Instances reloaded Success");
			}
			catch (Exception e)
			{
				PacketSendUtility.sendMessage(admin, "Failed to reload instances");
				log.error("Instance reload failed!", e);
			}
		}
		else
		{
			PacketSendUtility.sendMessage(admin, SYNTAX);
		}
		
	}
	
	/**
	 * Creates a {@link Schema} object from a file path.<br>
	 * This method uses the {@code XMLConstants.W3C_XML_SCHEMA_NS_URI} factory.<br>
	 * It throws an {@code Error} if the schema cannot be loaded.
	 * @param xml_schema The file path to the XML schema.
	 * @return The generated {@link Schema} object.
	 */
	private Schema getSchema(String xml_schema)
	{
		Schema schema = null;
		final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		try
		{
			schema = sf.newSchema(new File(xml_schema));
		}
		catch (SAXException saxe)
		{
			throw new Error("Error while getting schema", saxe);
		}
		
		return schema;
	}
	
	/**
	 * Retrieves a list of XML files from a specific directory.<br>
	 * It filters out hidden files and those starting with {@code new}.<br>
	 * The search can be performed recursively based on the provided flag.
	 * @param root The base {@code File} directory to start searching from.
	 * @param recursive A boolean indicating if the method should search subdirectories.
	 * @return A {@code Collection} of {@code File} objects matching the criteria.
	 * @throws IOException If an error occurs while walking the directory tree.
	 */
	private Collection<File> listFiles(File root, boolean recursive) throws IOException
	{
		try (Stream<Path> stream = Files.walk(root.toPath(), recursive ? Integer.MAX_VALUE : 1))
		{
			return stream.filter(Files::isRegularFile).filter(p ->
			{
				final String n = p.getFileName().toString();
				return n.endsWith(".xml") && !n.startsWith("new") && !p.toFile().isHidden();
			}).map(Path::toFile).collect(Collectors.toList());
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
		PacketSendUtility.sendMessage(player, SYNTAX);
	}
}
