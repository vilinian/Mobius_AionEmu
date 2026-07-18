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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * Handles the client request to perform a crafting action.<br>
 * This packet is processed by the {@link CraftService} to create items based on recipes.<br>
 * It validates the player's actions and updates the game state accordingly.
 * @author Mr. Poke
 */
public class CM_CRAFT extends AionClientPacket
{
	private int unk;
	private int targetTemplateId;
	private int recipeId;
	private int targetObjId;
	private int materialsCount;
	private int craftType;
	
	/**
	 * This constructor initializes a new {@link CM_CRAFT} packet.<br>
	 * It sets the required network data for crafting actions.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary state of the connection.
	 * @param restStates Additional states associated with the packet.
	 */
	public CM_CRAFT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		unk = readC();
		targetTemplateId = readD();
		recipeId = readD();
		targetObjId = readD();
		materialsCount = readH();
		craftType = readC();
		for (int i = 0; i < materialsCount; i++)
		{
			readD(); // materialId
			readQ(); // materialCount
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		// disallow crafting in shutdown progress..
		if ((player == null) || !player.isSpawned() || player.getController().isInShutdownProgress())
		{
			return;
		}
		
		// 129 = Morph Substances
		if (unk != 129)
		{
			final VisibleObject staticObject = player.getKnownList().getKnownObjects().get(targetObjId);
			if ((staticObject == null) || !MathUtil.isIn3dRange(player, staticObject, 10) || (staticObject.getObjectTemplate().getTemplateId() != targetTemplateId))
			{
				return;
			}
		}
		
		CraftService.startCrafting(player, recipeId, targetObjId, craftType);
	}
}
