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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTabItem;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.events.ArcadeUpgradeService;

/**
 * This packet handles the upgrade process for arcade items.<br>
 * It communicates with {@link ArcadeUpgradeService} to update the player's arcade progress.
 * @author Raziel
 */
public class SM_UPGRADE_ARCADE extends AionServerPacket
{
	/**
	 * Actions: 0 = Show Icon 1 = Start
	 */
	
	private final int action;
	private int showicon = 1;
	private int frenzyPoints = 0;
	private boolean success = false;
	private int level;
	private ArcadeTabItem itemList;
	private final int sessionId = 64519;
	private Player player;
	private int frenzyTime;
	private int frenzyCount;
	
	/**
	 * Creates a new {@link SM_UPGRADE_ARCADE} packet.<br>
	 * This constructor sets the action to {@code 0}.<br>
	 * It determines if an icon should be displayed based on the input.
	 * @param showicon Set to {@code true} to display the icon, or {@code false} to hide it.
	 */
	public SM_UPGRADE_ARCADE(boolean showicon)
	{
		action = 0;
		this.showicon = showicon ? 1 : 0;
	}
	
	/**
	 * Creates a new arcade upgrade packet.<br>
	 * This constructor sets the action to {@code 1}.<br>
	 * It initializes the frenzy points and count values.
	 * @param frenzyPoints The number of points used for the frenzy effect.
	 * @param frenzyCount The total count associated with the frenzy effect.
	 */
	public SM_UPGRADE_ARCADE(int frenzyPoints, int frenzyCount)
	{
		action = 1;
		this.frenzyPoints = frenzyPoints;
		this.frenzyCount = frenzyCount;
	}
	
	/**
	 * This constructor initializes the arcade upgrade packet with a specific action.<br>
	 * It sets the {@code action} value used by the server to handle the request.
	 * @param action The action type, where {@code 0} shows an icon and {@code 1} starts the process.
	 */
	public SM_UPGRADE_ARCADE(int action)
	{
		this.action = action;
	}
	
	/**
	 * This constructor initializes the arcade upgrade packet.<br>
	 * It sets the specific action, success status, and frenzy points.
	 * @param action The type of action to perform.
	 * @param success Whether the upgrade attempt was successful.
	 * @param frenzy The number of frenzy points associated with this action.
	 */
	public SM_UPGRADE_ARCADE(int action, boolean success, int frenzy)
	{
		this.action = action;
		this.success = success;
		frenzyPoints = frenzy;
	}
	
	/**
	 * This method creates a new {@code SM_UPGRADE_ARCADE} packet.<br>
	 * It initializes the packet with specific player and arcade data.
	 * @param player The {@link Player} object associated with this action.
	 * @param action The type of action to perform, such as 0 for showing an icon or 1 for starting.
	 * @param level The current upgrade level for the arcade item.
	 */
	public SM_UPGRADE_ARCADE(Player player, int action, int level)
	{
		this.action = action;
		this.level = level;
		this.player = player;
	}
	
	/**
	 * Creates a new {@code SM_UPGRADE_ARCADE} packet.<br>
	 * This method initializes the arcade upgrade data for the client.
	 * @param action The type of action to perform, such as 0 for showing an icon or 1 for starting.
	 * @param itemList The specific {@link ArcadeTabItem} being processed.
	 */
	public SM_UPGRADE_ARCADE(int action, ArcadeTabItem itemList)
	{
		this.action = action;
		this.itemList = itemList;
	}
	
	/**
	 * Creates a new {@link SM_UPGRADE_ARCADE} packet.<br>
	 * This method initializes the arcade upgrade data.<br>
	 * It sets the action, time, and count values.
	 * @param action The type of action to perform.
	 * @param frenzyTime The duration of the frenzy effect.
	 * @param frenzyCount The number of times the frenzy can be used.
	 */
	public SM_UPGRADE_ARCADE(int action, int frenzyTime, int frenzyCount)
	{
		this.action = action;
		this.frenzyTime = frenzyTime;
		this.frenzyCount = frenzyCount;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeC(action);
		
		switch (action)
		{
			case 0:// show icon
				writeD(showicon);
				break;
			case 1: // show start upgrade arcade info
				writeD(sessionId); // SessionId
				writeD(frenzyPoints); // frenzymeter
				writeD(frenzyCount);
				writeD(1);
				writeD(4);
				writeD(6);
				writeD(8);
				writeD(8); // max upgrade
				writeH(272); // icon
				writeS("success_weapon01");
				writeS("success_weapon01");
				writeS("success_weapon01");
				writeS("success_weapon02");
				writeS("success_weapon02");
				writeS("success_weapon03");
				writeS("success_weapon03");
				writeS("success_weapon04");
				break;
			case 2:
				writeC(1); // OLD D (sessionId) new c (1)
				break;
			case 3: // try result
				writeC(success ? 1 : 0); // 1 success - 0 fail
				writeD(frenzyPoints > 100 ? 100 : frenzyPoints); // frenzyPoints
				break;
			case 4: // try result
				writeD(level); // upgradeLevel
				break;
			case 5: // show fail
				writeD(level); // upgradeLevel
				writeC((level >= 6) && !player.getUpgradeArcade().isReTry() ? 1 : 0); // canResume? 1 yes - 0 no
				writeD((level >= 6) && !player.getUpgradeArcade().isReTry() ? 2 : 0); // needed Arcade Token
				writeD(0); // unk
				player.getUpgradeArcade().setReTry(false);
				player.getUpgradeArcade().setFailed(false);
				break;
			case 6: // show reward icon
				writeD(itemList.getItemId()); // templateId
				writeD(itemList.getNormalCount() > 0 ? itemList.getNormalCount() : itemList.getFrenzyCount()); // itemCount
				writeD(0); // unk
				break;
			case 7: // Frenzy !!!!
				writeD(frenzyTime); // frenzySeconds !
				writeD(frenzyCount); // frenzyCount
				break;
			case 8: // some configuration switch first option 1 displays a "You have not enough frenzycoins" window, the second changes the appearance of the frenzyBar
				writeD(1); // unk
				writeD(0); // unk
				break;
			case 10: // show reward list
				final List<ArcadeTab> tabs = ArcadeUpgradeService.getInstance().getTabs();
				for (ArcadeTab tab : tabs)
				{
					writeC(tab.getArcadeTabItems().size());
				}
				
				for (ArcadeTab arcadetab : tabs)
				{
					for (ArcadeTabItem arcadetabitem : arcadetab.getArcadeTabItems())
					{
						writeD(arcadetabitem.getItemId()); // getId()
						writeD(arcadetabitem.getNormalCount()); // getUncheckedcount()
						writeD(0);
						writeD(arcadetabitem.getFrenzyCount()); // getCheckedcount
						writeD(0);
					}
				}
				break;
			// case 11: Empty Packet for BonusReward :)
		}
	}
}
