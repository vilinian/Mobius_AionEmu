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

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;

/**
 * Handles the {@code /marry} player command.<br>
 * This class manages the logic for players to request a marriage through the {@link WeddingService}.<br>
 * It validates requirements and sends the appropriate responses to both players.
 * @author synchro2
 * @rework Eloann
 */
public class cmd_marry extends PlayerCommand
{
	/**
	 * Registers the {@code marry} command for players.<br>
	 * This method initializes the command handler.
	 */
	public cmd_marry()
	{
		super("marry");
	}
	
	/**
	 * Executes the command to initiate a marriage request between two players.<br>
	 * It validates that both players are online and not in prison.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings where the first element is the first character name and the second is the second character name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (!WeddingsConfig.WEDDINGS_ENABLE)
		{
			PacketSendUtility.sendMessage(admin, "Weddings disabled.");
			return;
		}
		
		if ((params == null) || (params.length != 2))
		{
			PacketSendUtility.sendMessage(admin, "syntax .marry <characterName1> <characterName2>");
			return;
		}
		
		final Player partner1 = World.getInstance().findPlayer(Util.convertName(params[0]));
		final Player partner2 = World.getInstance().findPlayer(Util.convertName(params[1]));
		if ((partner1 == null) || (partner2 == null))
		{
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		
		if (partner1.equals(partner2))
		{
			PacketSendUtility.sendMessage(admin, "You can't marry player on himself.");
			return;
		}
		
		if ((partner1.getWorldId() == 510010000) || (partner1.getWorldId() == 520010000) || (partner2.getWorldId() == 510010000) || (partner2.getWorldId() == 520010000))
		{
			PacketSendUtility.sendMessage(admin, "One of the players is in prison.");
			return;
		}
		
		PacketSendUtility.sendMessage(admin, "Question sended.");
		PacketSendUtility.sendMessage(partner1, "You want marry on " + partner2.getName() + "?");
		HTMLService.showHTML(partner1, HTMLCache.getInstance().getHTML("weddings.xhtml"));
		PacketSendUtility.sendMessage(partner2, "You want marry on " + partner1.getName() + "?");
		HTMLService.showHTML(partner2, HTMLCache.getInstance().getHTML("weddings.xhtml"));
		
		WeddingService.getInstance().registerOffer(partner1, partner2, admin);
	}
	
	/**
	 * This method is called when an {@code execute} command fails.<br>
	 * It sends a failure notification to the administrator.
	 * @param admin The {@code Player} who attempted the command.
	 * @param message The error message to display.
	 */
	@Override
	public void onFail(Player admin, String message)
	{
		PacketSendUtility.sendMessage(admin, "syntax .marry <characterName1> <characterName2>");
	}
}
