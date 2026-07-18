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
package system.handlers.playercommands;

import java.util.concurrent.TimeUnit;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Handles the {@code /unstuck} player command.<br>
 * This class allows a {@link Player} to teleport to a safe location if they are trapped in the game world.
 * @author Nemiroff Date: 11.01.2010
 * @rework Eloann Date : 12.06.2013
 */
public class cmd_unstuck extends PlayerCommand
{
	/**
	 * Registers the {@code unstuck} command.<br>
	 * This allows players to use the command to clear stuck positions.<br>
	 * It initializes the command within the {@link PlayerCommand} system.
	 */
	public cmd_unstuck()
	{
		super("unstuck");
	}
	
	/**
	 * Executes the unstuck command for a player.<br>
	 * It checks if the {@link Player} is alive and not in prison before proceeding.<br>
	 * The method freezes the player for 10 seconds then teleports them to their bind location.
	 * @param player The player who is executing the command.
	 * @param params Variable arguments provided with the command.
	 */
	@Override
	public void execute(Player player, String... params)
	{
		if (player.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendMessage(player, "You dont have execute this command. You die");
			return;
		}
		
		if (player.isInPrison())
		{
			PacketSendUtility.sendMessage(player, "You can't use the unstuck command when you are in Prison");
			return;
		}
		
		PacketSendUtility.sendMessage(player, "You are now freeze for 10 secondes before unstuck.");
		player.getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
		player.getEffectController().updatePlayerEffectIcons();
		player.getEffectController().broadCastEffects();
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, 0, 0, (int) TimeUnit.SECONDS.toMillis(10), 0));
		ThreadPoolManager.getInstance().schedule(() ->
		{
			player.getEffectController().unsetAbnormal(AbnormalState.PARALYZE.getId());
			player.getEffectController().updatePlayerEffectIcons();
			player.getEffectController().broadCastEffects();
			player.getController().cancelUseItem();
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, 0, 0, 0, 1));
			TeleportService2.moveToBindLocation(player, true);
		}, (int) TimeUnit.SECONDS.toMillis(10));
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
