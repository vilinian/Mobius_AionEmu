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
package system.handlers.world;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;

/**
 * Handles the specific world logic for the {@code Heiron} zone.<br>
 * This class manages unique behaviors and events associated with {@code WorldID} 210040000.
 */
@WorldID(210040000)
public class Heiron extends GeneralWorldHandler
{
	private int heironHunting;
	
	/**
	 * Handles the logic that occurs when an {@link Npc} dies.<br>
	 * This method identifies the player who dealt the most damage to the NPC.<br>
	 * It triggers specific messages or spawns based on the unique ID of the dead NPC.
	 * @param npc The {@link Npc} object that has died.
	 */
	@Override
	public void onDie(Npc npc)
	{
		final Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId())
		{
			case 280416: // Deputy Hanuman Faithful Subordinate
			case 280417: // Deputy Hanuman Faithful Subordinate
			case 280771: // Underling Of Agro
			case 280775: // Power Of Exedil
			case 280780: // Dedrun Fragment
			case 280788: // Medeus Drake
			case 280802: // Thirsting Bloodwing
			case 280803: // Vicious Bloodwing
			case 280804: // Cruel Vampire
			case 280814: // Geschphalt Minion
			case 653085: // Indratu Grunt
				despawnNpc(npc);
				break;
			default:
				if (npc.getTarget() instanceof Player)
				{
					heironHunting++;
					if (heironHunting == 250)
					{
						heironHunting = 0;
						switch (Rnd.get(1, 25))
						{
							case 1:
								ItemService.addItem(player, 188073315, 1); // Legendary Punishing Weapon Box
								break;
							case 2:
								ItemService.addItem(player, 188073316, 1); // Legendary Unyielding Weapon Box
								break;
							case 3:
								ItemService.addItem(player, 188073317, 1); // Legendary Strident Weapon Box
								break;
							case 4:
								ItemService.addItem(player, 188073318, 1); // Legendary Kromede's Weapon Box
								break;
							case 5:
								ItemService.addItem(player, 188073319, 1); // Legendary Weapon Box Of Wrath
								break;
							case 6:
								ItemService.addItem(player, 188073320, 1); // Legendary Weapon Box Of Conquest
								break;
							case 7:
								ItemService.addItem(player, 188073390, 1); // Legendary Kromede's Armor Box
								break;
							case 8:
								ItemService.addItem(player, 188900063, 5); // Experience Crystal
								break;
							case 9:
								ItemService.addItem(player, 188070376, 2); // Golden Box Of Minion Contracts
								break;
							case 10:
								ItemService.addItem(player, 190120147, 1); // Solar Unicorn - Superior Recovery
								break;
							case 11:
								ItemService.addItem(player, 166023100, 5); // Ancient PvE Enchantment Stone
								break;
							case 12:
								ItemService.addItem(player, 166023101, 5); // Legendary PvE Enchantment Stone
								break;
							case 13:
								ItemService.addItem(player, 166023102, 5); // Ultimate PvE Enchantment Stone
								break;
							case 14:
								ItemService.addItem(player, 186020002, 50); // Gold Ingots
								break;
							case 15:
								ItemService.addItem(player, 166401000, 1000); // Manastone Fastener
								break;
							case 16:
								ItemService.addItem(player, 188071961, 1); // Luna Wooden Box (200)
								break;
							case 17:
								ItemService.addItem(player, 188073017, 1); // Ultimate Transformation Box
								break;
							case 18:
								ItemService.addItem(player, 188070768, 1); // Ancient Daevanion Skill Box
								break;
							case 19:
								ItemService.addItem(player, 188070330, 1); // Legendary Daevanion Skill Box
								break;
							case 20:
								ItemService.addItem(player, 169610397, 1); // [Title Card] Special Daeva 180 Day Pass
								break;
							case 21:
								ItemService.addItem(player, 169020003, 50000); // Shard Bundle
								break;
							case 22:
								ItemService.addItem(player, 188070351, 10); // Stigma Enchantment Stone Bundle
								break;
							case 23:
								ItemService.addItem(player, 188071672, 10); // Lugbug's Cubicle Box
								break;
							case 24:
								ItemService.addItem(player, 190095008, 10); // Legendary Transformation Contract
								break;
							case 25:
								ItemService.addItem(player, 188071059, 1); // Shining Skill Card Bundle
								break;
						}
					}
				}
				break;
		}
	}
	
	/**
	 * This method handles the logic after a {@link Player} uses an item on an {@link Npc}.<br>
	 * It checks the {@code npcId} to trigger specific world actions.<br>
	 * Actions include teleporting or checking level requirements for secret passages.
	 * @param player The {@link Player} who used the item.
	 * @param npc The {@link Npc} that was interacted with.
	 */
	@Override
	public void handleUseItemFinish(Player player, Npc npc)
	{
		switch (npc.getNpcId())
		{
			case 730033: // Destroyed Teleport Statue
				destroyedStatue(player, 208.0000f, 2709.0000f, 141.0000f, (byte) 65);
				break;
			case 730040: // Secret Passage Of Indratu Legion
				if (player.getLevel() >= 60)
				{
					indratuLegionIn(player, 2216.0000f, 2487.0000f, 182.0000f, (byte) 56);
				}
				else
				{
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT);
				}
				break;
		}
	}
	
	/**
	 * Handles the logic for when a statue is destroyed.<br>
	 * It teleports the {@code player} to a new location.<br>
	 * This method uses {@code teleportTo}.
	 * @param player The {@code Player} who triggered the destruction.
	 * @param x The target X coordinate for teleportation.
	 * @param y The target Y coordinate for teleportation.
	 * @param z The target Z coordinate for teleportation.
	 * @param h The target horizontal angle for teleportation.
	 */
	protected void destroyedStatue(Player player, float x, float y, float z, byte h)
	{
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	
	/**
	 * Teleports a player to the Indratu Legion area.<br>
	 * This method uses {@code teleportTo} to move the character.
	 * @param player The {@code Player} object to be teleported.
	 * @param x The destination X coordinate.
	 * @param y The destination Y coordinate.
	 * @param z The destination Z coordinate.
	 * @param h The horizontal rotation value.
	 */
	protected void indratuLegionIn(Player player, float x, float y, float z, byte h)
	{
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h);
	}
	
	/**
	 * Removes an {@link Npc} from the game world.<br>
	 * This method calls the {@code onDelete()} method on the NPC controller.<br>
	 * It checks if the provided {@code npc} is not {@code null} before proceeding.
	 * @param npc The {@link Npc} object to be removed.
	 */
	protected void despawnNpc(Npc npc)
	{
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	/**
	 * Removes a list of {@link Npc} objects from the game world.<br>
	 * This method calls {@code onDelete()} on each NPC controller.
	 * @param npcs The list of {@code Npc} objects to be removed.
	 */
	@SuppressWarnings("unused")
	private void despawnNpcs(List<Npc> npcs)
	{
		for (Npc npc : npcs)
		{
			npc.getController().onDelete();
		}
	}
	
	/**
	 * Retrieves a list of {@link Npc} objects that match the given ID.<br>
	 * This method searches through all NPCs in the current world.<br>
	 * It returns an empty list if no matches are found.
	 * @param npcId The unique identifier to search for.
	 * @return A {@code List} of matching {@link Npc} objects.
	 */
	protected List<Npc> getNpcs(int npcId)
	{
		final List<Npc> npcs = new ArrayList<>();
		for (Npc npc : map.getWorld().getNpcs())
		{
			if (npc.getNpcId() == npcId)
			{
				npcs.add(npc);
			}
		}
		
		return npcs;
	}
	
	/**
	 * Sends a system message to players based on their race.<br>
	 * This method uses {@link ThreadPoolManager} to delay the delivery of the message.<br>
	 * It checks if the player's race matches the provided {@code Race} or is set to {@code Race.PC_ALL}.
	 * @param msg The unique identifier for the system message to send.
	 * @param race The specific {@code Race} that should receive the message.
	 * @param time The delay in milliseconds before sending the message.
	 */
	protected void sendMsgByRace(int msg, Race race, int time)
	{
		ThreadPoolManager.getInstance().schedule((Runnable) () -> World.getInstance().doOnAllPlayers(player ->
		{
			if (((player.getWorldId() == map.getMapId()) && player.getRace().equals(race)) || race.equals(Race.PC_ALL))
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
			}
			
		}), time);
	}
}
