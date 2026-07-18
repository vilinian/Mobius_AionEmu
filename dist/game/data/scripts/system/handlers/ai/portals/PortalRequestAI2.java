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
package system.handlers.ai.portals;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the logic for processing portal requests initiated by players.<br>
 * This class manages how {@link Player} objects interact with teleportation systems.<br>
 * It extends {@link PortalAI2} to provide specific behavior for these requests.
 * @author xTz
 */
@AIName("portal_request")
public class PortalRequestAI2 extends PortalAI2
{
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		if (teleportTemplate != null)
		{
			final TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
			if (loc != null)
			{
				final TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
				final RequestResponseHandler portal = new RequestResponseHandler(player)
				{
					
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						TeleportService2.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.JUMP_ANIMATION);
					}
					
					@Override
					public void denyRequest(Creature requester, Player responder)
					{
						// Nothing Happens
					}
				};
				final long transportationPrice = PricesService.getPriceForService(loc.getPrice(), player.getRace());
				if (player.getResponseRequester().putRequest(160013, portal))
				{
					PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(160013, getObjectId(), 0, new DescriptionId((locationTemplate.getNameId() * 2) + 1), transportationPrice));
				}
			}
		}
	}
}
