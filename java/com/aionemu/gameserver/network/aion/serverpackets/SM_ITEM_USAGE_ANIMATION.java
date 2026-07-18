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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.World;

/**
 * This packet is sent to the client to trigger a specific animation when an item is used.<br>
 * It allows the game server to synchronize visual effects with player actions.
 * @author ATracer
 * @rework FrozenKiller
 */
public class SM_ITEM_USAGE_ANIMATION extends AionServerPacket
{
	private final int playerObjId;
	private final int targetObjId;
	private final int itemObjId;
	private final int itemId;
	private final int time;
	private final int animationsId;
	
	/**
	 * Creates a new {@link SM_ITEM_USAGE_ANIMATION} packet.<br>
	 * This packet handles the animation for using an item.<br>
	 * It sets default values for some fields during initialization.
	 * @param playerObjId The unique identifier of the player.
	 * @param itemObjId The unique identifier of the item object.
	 * @param itemId The specific ID of the item being used.
	 * @param animationsId The ID of the animation to play.
	 * @param unk An unknown parameter for backward compatibility.
	 */
	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId, int animationsId, int unk)
	{
		this.playerObjId = playerObjId;
		targetObjId = 0;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		time = 0;
		this.animationsId = 1;
	}
	
	/**
	 * Creates a new {@link SM_ITEM_USAGE_ANIMATION} packet.<br>
	 * This constructor sets default values for the animation data.<br>
	 * It is used when an item is used without specific extra parameters.
	 * @param playerObjId The unique ID of the player performing the action.
	 * @param itemObjId The unique ID of the item being used.
	 * @param itemId The internal ID of the item type.
	 */
	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId)
	{
		this.playerObjId = playerObjId;
		targetObjId = 0;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		time = 0;
		animationsId = 1;
	}
	
	/**
	 * Creates a new {@code SM_ITEM_USAGE_ANIMATION} packet.<br>
	 * This packet handles the animation for using an item on a target.
	 * @param playerObjId The unique ID of the player performing the action.
	 * @param targetObjId The unique ID of the target object.
	 * @param itemObjId The unique ID of the specific item instance used.
	 * @param itemId The base ID of the item type.
	 * @param time The timestamp or duration for the animation.
	 * @param animationsId The specific animation ID to play.
	 */
	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int targetObjId, int itemObjId, int itemId, int time, int animationsId)
	{
		this.playerObjId = playerObjId;
		this.targetObjId = targetObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.animationsId = animationsId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		if (time > 0)
		{
			final Player player = World.getInstance().findPlayer(playerObjId);
			final Item item = player.getInventory().getItemByObjId(itemObjId);
			player.setUsingItem(item);
		}
		
		writeD(playerObjId);
		writeD(targetObjId);
		writeD(itemObjId);
		writeD(itemId);
		writeD(time);
		writeH(animationsId); // AnimationsId
		writeH(1); // Always 1 (5.4)
		writeD(0); // unk
	}
}
