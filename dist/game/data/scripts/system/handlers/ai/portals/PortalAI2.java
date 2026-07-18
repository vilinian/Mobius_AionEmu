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

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.portal.PortalUse;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import system.handlers.ai.ActionItemNpcAI2;

/**
 * Handles the artificial intelligence for portal objects in the game world.<br>
 * This class manages how NPCs interact with {@link TeleporterTemplate} and {@link PortalPath} data.<br>
 * It processes teleportation logic and animations when a player uses a portal.
 * @author xTz
 */
@AIName("portal")
public class PortalAI2 extends ActionItemNpcAI2
{
	protected TeleporterTemplate teleportTemplate;
	protected PortalUse portalUse;
	
	/**
	 * Handles the logic when a player selects an option in a dialog.<br>
	 * It checks for specific items and grants rewards or skills based on the {@code dialogId}.<br>
	 * This method is triggered by the NPC's interaction system.
	 * @param player The {@link Player} who is interacting with the NPC.
	 * @param dialogId The unique identifier for the current dialog window.
	 * @param questId The ID of the quest associated with this interaction.
	 * @param extendedRewardIndex The index used to determine specific rewards.
	 * @return Always returns {@code true} to indicate the action was processed.
	 */
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		return true;
	}
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It calls {@code handleSpawned} from the parent class.<br>
	 * It also triggers the {@code setUseInSpawnedSkill()} method.
	 */
	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		teleportTemplate = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(getNpcId());
		portalUse = DataManager.PORTAL2_DATA.getPortalUse(getNpcId());
		switch (getNpcId())
		{
			case 802219: // Advance Corridor [Arcadian Fortress]
			case 802221: // Advance Corridor [Umbral Fortress]
			case 802223: // Advance Corridor [Eternum Fortress]
			case 802225: // Advance Corridor [Skyclash Fortress]
				ThreadPoolManager.getInstance().schedule(() -> startLifeTask(), 1000);
				break;
		}
	}
	
	/**
	 * Schedules a task to handle portal closure logic.<br>
	 * This method waits for {@code 600000} milliseconds before executing.<br>
	 * It removes the owner and sends a system message to all players.
	 */
	private void startLifeTask()
	{
		ThreadPoolManager.getInstance().schedule(() -> World.getInstance().doOnAllPlayers(player ->
		{
			AI2Actions.deleteOwner(PortalAI2.this);
			
			// You will be returned to the entrance you used upon closure of the Advance Corridor
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_SVS_DIRECT_PORTAL_CLOSE_COMPULSION_TELEPORT);
		}), 600000); // 10 Minutes
	}
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		AI2Actions.selectDialog(this, player, 0, -1);
		if (getTalkDelay() != 0)
		{
			super.handleDialogStart(player);
		}
		else
		{
			handleUseItemFinish(player);
		}
	}
	
	/**
	 * Handles the completion of an item usage action.<br>
	 * This method is called when a {@link Player} finishes using an item.<br>
	 * It checks if the owner is in an instance before processing.
	 * @param player The {@code Player} who finished using the item.
	 */
	@Override
	protected void handleUseItemFinish(Player player)
	{
		if (portalUse != null)
		{
			final PortalPath portalPath = portalUse.getPortalPath(player.getRace());
			if (portalPath != null)
			{
				PortalService.port(portalPath, player, getObjectId());
			}
		}
		else if (teleportTemplate != null)
		{
			final TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
			if (loc != null)
			{
				TeleportService2.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.BEAM_ANIMATION);
			}
		}
	}
}
