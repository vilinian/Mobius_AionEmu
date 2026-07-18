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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * This packet is sent by the client when a user selects a target.<br>
 * It handles both manual selection via commands and mouse clicks on characters.<br>
 * The object ID {@code d} represents the selected entity, or {@code 0} if the target is unselected.
 * @author SoulKeeper, Sweetkr, KID
 */
public class CM_TARGET_SELECT extends AionClientPacket
{
	/**
	 * Target object id that client wants to select or 0 if wants to unselect
	 */
	private int targetObjectId;
	private int type;
	
	/**
	 * Creates a new instance of the {@link CM_TARGET_SELECT} packet.<br>
	 * This packet is sent when a player selects or unselects a target.<br>
	 * It handles actions like typing a name or clicking an object.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary {@link State} of the packet.
	 * @param restStates Additional {@link State} values for the packet.
	 */
	public CM_TARGET_SELECT(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
		type = readC();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		
		VisibleObject obj;
		if (targetObjectId == player.getObjectId())
		{
			obj = player;
		}
		else
		{
			obj = player.getKnownList().getObject(targetObjectId);
			
			if ((obj == null) && player.isInTeam())
			{
				final TeamMember<Player> member = player.getCurrentTeam().getMember(targetObjectId);
				if (member != null)
				{
					obj = member.getObject();
				}
			}
		}
		
		if (obj != null)
		{
			if (type == 1)
			{
				if (obj.getTarget() == null)
				{
					return;
				}
				
				player.setTarget(obj.getTarget());
			}
			else
			{
				player.setTarget(obj);
			}
			
			if (obj instanceof Player)
			{
				final Player target = (Player) obj;
				if ((player != obj) && !player.canSee(target))
				{
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Player name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
			}
			else if (obj instanceof Trap)
			{
				final Trap target = (Trap) obj;
				boolean isSameTeamTrap = false;
				if (target.getMaster() instanceof Player)
				{
					isSameTeamTrap = ((Player) target.getMaster()).isInSameTeam(player);
				}
				
				if ((player != obj) && !player.canSee(target) && !isSameTeamTrap)
				{
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Trap name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
				
			}
			else if (obj instanceof Creature)
			{
				final Creature target = (Creature) obj;
				if ((player != obj) && !player.canSee(target))
				{
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Npc name: " + target.getName() + " objectId: " + target.getObjectId() + " by");
				}
			}
		}
		else
		{
			player.setTarget(null);
		}
		
		sendPacket(new SM_TARGET_SELECTED(player));
		PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
	}
}
