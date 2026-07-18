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
package com.aionemu.gameserver.services;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AITemplate;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcShoutData;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Manages the logic and broadcasting for {@link Npc} shouts within the game world.<br>
 * It handles various {@link ShoutEventType} triggers to display messages to players.
 * @author Rolandas
 */
public class NpcShoutsService
{
	private static final Logger log = LoggerFactory.getLogger(NpcShoutsService.class);
	NpcShoutData shoutsCache = DataManager.NPC_SHOUT_DATA;
	
	/**
	 * Private constructor for the {@link NpcShoutsService} class.<br>
	 * This prevents other classes from creating new instances of this service.
	 */
	private NpcShoutsService()
	{
		for (Npc npc : World.getInstance().getNpcs())
		{
			final int npcId = npc.getNpcId();
			final int worldId = npc.getSpawn().getWorldId();
			final int objectId = npc.getObjectId();
			
			if (!shoutsCache.hasAnyShout(worldId, npcId, ShoutEventType.IDLE))
			{
				continue;
			}
			
			final List<NpcShout> shouts = shoutsCache.getNpcShouts(worldId, npcId, ShoutEventType.IDLE, null, 0);
			if (shouts.size() == 0)
			{
				continue;
			}
			
			int defaultPollDelay = Rnd.get(180, 360) * 1000;
			for (NpcShout shout : shouts)
			{
				if ((shout.getPollDelay() != 0) && (shout.getPollDelay() < defaultPollDelay))
				{
					defaultPollDelay = shout.getPollDelay();
				}
			}
			
			ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				final AionObject npcObj = World.getInstance().findVisibleObject(objectId);
				if ((npcObj != null) && (npcObj instanceof Npc))
				{
					final Npc npc2 = (Npc) npcObj;
					
					// check if AI overrides
					if (!npc2.getAi2().poll(AIQuestion.CAN_SHOUT))
					{
						return;
					}
					
					final int randomShout = Rnd.get(shouts.size());
					final NpcShout shout = shouts.get(randomShout);
					if ((shout.getPattern() != null) && !((AITemplate) npc2.getAi2()).onPatternShout(ShoutEventType.IDLE, shout.getPattern(), 0))
					{
						return;
					}
					
					final Iterator<Player> iter = npc2.getKnownList().getKnownPlayers().values().iterator();
					while (iter.hasNext())
					{
						final Player kObj = iter.next();
						if (kObj.getLifeStats().isAlreadyDead())
						{
							return;
						}
						
						shout(npc2, kObj, shout, 0);
					}
				}
			}, 0, defaultPollDelay);
		}
	}
	
	/**
	 * Triggers a shout from an {@link Npc} towards a {@link Creature}.<br>
	 * This method handles multiple shouts by either playing them in a sequence or picking one at random.
	 * @param owner The {@link Npc} who is performing the action.
	 * @param target The {@link Creature} being addressed.
	 * @param shouts A list of {@link NpcShout} objects to be played.
	 * @param delaySeconds The time in seconds to wait before starting the shout.
	 * @param isSequence If {@code true}, plays all shouts in order; if {@code false}, picks one randomly.
	 */
	public void shout(Npc owner, Creature target, List<NpcShout> shouts, int delaySeconds, boolean isSequence)
	{
		if ((owner == null) || (shouts == null))
		{
			return;
		}
		
		if (shouts.size() > 1)
		{
			if (isSequence)
			{
				int nextDelay = 5;
				for (NpcShout shout : shouts)
				{
					if (delaySeconds == -1)
					{
						shout(owner, target, shout, nextDelay);
						nextDelay += 5;
					}
					else
					{
						shout(owner, target, shout, delaySeconds);
						delaySeconds = -1;
					}
				}
			}
			else
			{
				final int randomShout = Rnd.get(shouts.size());
				shout(owner, target, shouts.get(randomShout), delaySeconds);
			}
		}
		else if (shouts.size() == 1)
		{
			shout(owner, target, shouts.get(0), delaySeconds);
		}
	}
	
	/**
	 * Makes an {@link Npc} perform a shout towards a specific {@link Creature}.<br>
	 * This method processes dynamic parameters like usernames or items based on the target.<br>
	 * It then calls the {@code shout} method on the owner object.
	 * @param owner The {@link Npc} that will perform the action.
	 * @param target The {@link Creature} being addressed by the shout.
	 * @param shout The {@link NpcShout} template containing the message data.
	 * @param delaySeconds The time in seconds to wait before performing the shout.
	 */
	public void shout(Npc owner, Creature target, NpcShout shout, int delaySeconds)
	{
		if ((owner == null) || (shout == null))
		{
			return;
		}
		
		Object param = shout.getParam();
		
		if (target instanceof Player)
		{
			final Player player = (Player) target;
			if ("username".equals(param))
			{
				param = player.getName();
			}
			else if ("userclass".equals(param))
			{
				param = ((240000 + player.getCommonData().getPlayerClass().getClassId()) * 2) + 1;
			}
			else if ("usernation".equals(param))
			{
				log.warn("Shout with param 'usernation' is not supported");
				return;
			}
			else if ("usergender".equals(param))
			{
				param = ((902012 + player.getCommonData().getGender().getGenderId()) * 2) + 1;
			}
			else if ("mainslotitem".equals(param))
			{
				final Item weapon = player.getEquipment().getMainHandWeapon();
				if (weapon == null)
				{
					return;
				}
				
				param = weapon.getItemTemplate().getNameId();
			}
			else if ("quest".equals(shout.getPattern()))
			{
				delaySeconds = 0;
			}
		}
		
		if ("target".equals(param) && (target != null))
		{
			param = target.getObjectTemplate().getName();
		}
		
		owner.shout(shout, target, param, delaySeconds);
	}
	
	/**
	 * Sends a specific message to an {@link Npc}.<br>
	 * This method handles the formatting and delivery of the text.
	 * @param npc The {@code Npc} receiving the message.
	 * @param msg The unique identifier for the message content.
	 * @param Obj The object ID associated with the message.
	 * @param color The color code for the message text.
	 * @param delay The time in milliseconds to wait before sending.
	 */
	public void sendMsg(Npc npc, int msg, int Obj, int color, int delay)
	{
		sendMsg(npc, null, msg, Obj, false, color, delay);
	}
	
	/**
	 * Sends a message from an {@link Npc} to other players.<br>
	 * This method handles the formatting and delivery of the text.
	 * @param npc The {@code Npc} object that is sending the message.
	 * @param msg The unique identifier for the message content.
	 * @param Obj The ID of the object associated with the message.
	 * @param isShout Set to {@code true} if the message should be displayed as a shout.
	 * @param color The color code for the message text.
	 * @param delay The time in milliseconds to wait before sending.
	 */
	public void sendMsg(Npc npc, int msg, int Obj, boolean isShout, int color, int delay)
	{
		sendMsg(npc, null, msg, Obj, isShout, color, delay);
	}
	
	/**
	 * Sends a system message to a specific {@link Npc}.<br>
	 * This method handles the delivery of text with a specified time gap.
	 * @param npc The {@code Npc} object that will receive or trigger the message.
	 * @param msg The unique identifier for the message content.
	 * @param delay The amount of time to wait before sending the message in milliseconds.
	 */
	public void sendMsg(Npc npc, int msg, int delay)
	{
		sendMsg(npc, null, msg, 0, false, 25, delay);
	}
	
	/**
	 * Sends a system message to a specific {@link Npc}.<br>
	 * This method uses default values for colors and delays.
	 * @param npc The {@code Npc} object that will receive the message.
	 * @param msg The unique identifier for the message to be sent.
	 */
	public void sendMsg(Npc npc, int msg)
	{
		sendMsg(npc, null, msg, 0, false, 25, 0);
	}
	
	/**
	 * Sends a message to all players in a specific world map.<br>
	 * This method handles the formatting and delivery of the text.
	 * @param instance The {@link WorldMapInstance} where the message will be sent.
	 * @param msg The unique identifier for the message content.
	 * @param Obj The object ID associated with the message.
	 * @param isShout Determines if the message should be treated as a shout.
	 * @param color The color code to apply to the text.
	 * @param delay The time in milliseconds to wait before sending.
	 */
	public void sendMsg(WorldMapInstance instance, int msg, int Obj, boolean isShout, int color, int delay)
	{
		sendMsg(null, instance, msg, Obj, isShout, color, delay);
	}
	
	/**
	 * Sends a system message to all players in a specific map.<br>
	 * This method uses the {@code WorldMapInstance} to identify the target area.<br>
	 * It applies a specified delay before the message is displayed.
	 * @param instance The {@code WorldMapInstance} where the message will be sent.
	 * @param msg The unique identifier for the message content.
	 * @param delay The time in milliseconds to wait before sending.
	 */
	public void sendMsg(WorldMapInstance instance, int msg, int delay)
	{
		sendMsg(null, instance, msg, 0, false, 25, delay);
	}
	
	/**
	 * Sends a system message to players in a specific area.<br>
	 * This method schedules the message delivery after a set delay.<br>
	 * It checks if the {@code npc} is active before sending to its known list.<br>
	 * If the {@code npc} is null, it sends the message to all players in the {@code instance}.
	 * @param npc The {@link Npc} entity that triggers the message.
	 * @param instance The {@link WorldMapInstance} where the message should be sent if no NPC is provided.
	 * @param msg The unique identifier for the message content.
	 * @param Obj An object identifier associated with the message.
	 * @param isShout A boolean indicating if the message should be displayed as a shout.
	 * @param color The color code for the message text.
	 * @param delay The time in milliseconds to wait before sending the message.
	 */
	public void sendMsg(Npc npc, WorldMapInstance instance, int msg, int Obj, boolean isShout, int color, int delay)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if ((npc != null) && npc.isSpawned())
			{
				npc.getKnownList().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color)));
			}
			else if (instance != null)
			{
				instance.doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color, new Object[0])));
			}
		}, delay);
	}
	
	/**
	 * Sends a message to all players on a specific world map.<br>
	 * This method handles the broadcast of system or NPC messages.
	 * @param map The {@code WorldMap} where the message will be sent.
	 * @param msg The unique identifier for the message content.
	 * @param Obj The object ID associated with the message.
	 * @param isShout A boolean indicating if the message should be treated as a shout.
	 * @param color The color code for the text display.
	 * @param delay The time in milliseconds to wait before sending.
	 * @param unk An unknown parameter used for internal processing.
	 */
	public void sendMsg(WorldMap map, int msg, int Obj, boolean isShout, int color, int delay, int unk)
	{
		sendMsg(null, map, msg, Obj, isShout, color, delay, unk);
	}
	
	/**
	 * Sends a system message to all players on a specific world map.<br>
	 * This method handles the delivery of messages with a specified delay.
	 * @param map The {@link WorldMap} where the message will be sent.
	 * @param msg The unique identifier for the message content.
	 * @param delay The time in milliseconds to wait before sending.
	 * @param unk An unknown parameter used for internal processing.
	 */
	public void sendMsg(WorldMap map, int msg, int delay, int unk)
	{
		sendMsg(null, map, msg, 0, false, 25, delay, unk);
	}
	
	/**
	 * Sends a system message to players based on an {@code Npc} or a {@code WorldMap}.<br>
	 * The message is sent after a specified delay.
	 * @param npc The {@code Npc} used to determine the target player list.
	 * @param map The {@code WorldMap} used if the {@code npc} is null.
	 * @param msg The message content identifier.
	 * @param Obj The object ID associated with the message.
	 * @param isShout Determines if the message should be displayed as a shout.
	 * @param color The color code for the message text.
	 * @param delay The time in milliseconds to wait before sending.
	 * @param unk An unknown parameter used for internal purposes.
	 */
	public void sendMsg(Npc npc, WorldMap map, int msg, int Obj, boolean isShout, int color, int delay, int unk)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if ((npc != null) && npc.isSpawned())
			{
				npc.getKnownList().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color, new Object[0])));
			}
			else if (map != null)
			{
				World.getInstance().doOnAllPlayers(player ->
				{
					if (player.getWorldId() == map.getMapId().intValue())
					{
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isShout, msg, Obj, color, new Object[0]));
					}
				});
			}
		}, delay);
	}
	
	/**
	 * Retrieves the singleton instance of the {@link NpcShoutsService}.<br>
	 * Use this method to access the service from anywhere in the code.
	 * @return The active {@code NpcShoutsService} instance.
	 */
	public static NpcShoutsService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcShoutsService instance = new NpcShoutsService();
	}
}
