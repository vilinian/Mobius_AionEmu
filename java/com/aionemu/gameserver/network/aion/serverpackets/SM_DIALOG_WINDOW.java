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

import java.util.List;

import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.player.PlayerMailboxState;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This packet handles the display of a dialog window to the client.<br>
 * It contains the necessary data for rendering {@link DialogPage} content.<br>
 * Use this class when an {@link Npc} or other object triggers a conversation.
 * @author alexa026
 */
public class SM_DIALOG_WINDOW extends AionServerPacket
{
	private final int targetObjectId;
	private final int dialogID;
	private int questId = 0;
	
	/**
	 * Creates a new {@code SM_DIALOG_WINDOW} packet.<br>
	 * This packet is used to display a dialog window to a player.
	 * @param targetObjectId The unique identifier of the object triggering the dialog.
	 * @param dlgID The specific ID of the dialog to be displayed.
	 */
	public SM_DIALOG_WINDOW(int targetObjectId, int dlgID)
	{
		this.targetObjectId = targetObjectId;
		dialogID = dlgID;
	}
	
	/**
	 * Creates a new {@code SM_DIALOG_WINDOW} packet.<br>
	 * This packet is used to display a dialog window to a player.
	 * @param targetObjectId The unique identifier of the object triggering the dialog.
	 * @param dlgID The specific ID of the dialog to be displayed.
	 * @param questId The ID of the quest associated with this dialog.
	 */
	public SM_DIALOG_WINDOW(int targetObjectId, int dlgID, int questId)
	{
		this.targetObjectId = targetObjectId;
		dialogID = dlgID;
		this.questId = questId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		final Player player = con.getActivePlayer();
		
		writeD(targetObjectId);
		writeH(dialogID);
		writeD(questId);
		writeH(0);
		if (dialogID == DialogPage.MAIL.id())
		{
			final AionObject object = World.getInstance().findVisibleObject(targetObjectId);
			if ((object != null) && (object instanceof Npc))
			{
				final Npc znpc = (Npc) object;
				if ((znpc.getNpcId() == 798100) || (znpc.getNpcId() == 798101))
				{
					player.getMailbox().mailBoxState = PlayerMailboxState.EXPRESS;
					writeH(2);
				}
				else
				{
					player.getMailbox().mailBoxState = PlayerMailboxState.REGULAR;
				}
			}
			else
			{
				writeH(0);
			}
		}
		else if (dialogID == DialogPage.TOWN_CHALLENGE_TASK.id())
		{
			final AionObject object = World.getInstance().findVisibleObject(targetObjectId);
			if ((object != null) && (object instanceof Npc))
			{
				final Npc npc = (Npc) object;
				if ((npc.getNpcId() == 205770) || (npc.getNpcId() == 730677) || (npc.getNpcId() == 730679))
				{
					int townId = 0;
					final MapRegion region = npc.getPosition().getMapRegion();
					if (region == null)
					{
						// some npc without region !!!
					}
					else
					{
						final List<ZoneInstance> zones = region.getZones(npc);
						for (ZoneInstance zone : zones)
						{
							townId = zone.getTownId();
							if (townId > 0)
							{
								break;
							}
						}
						
						writeH(townId);
					}
				}
			}
		}
		else
		{
			writeH(0);
		}
	}
}
