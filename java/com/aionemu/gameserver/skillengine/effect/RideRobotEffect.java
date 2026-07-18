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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.robot.RobotInfo;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIDE_ROBOT;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Handles the visual and functional effects for riding a robot.<br>
 * This class manages how the {@link Player} interacts with a {@link RobotInfo} entity.
 * @author Rolandas
 * @Reworked Kill3r
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RideRobotEffect")
public class RideRobotEffect extends EffectTemplate
{
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		final Player player = (Player) effected;
		player.setUseRobot(true);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_RIDE_ROBOT(player, getRobotInfo(player).getRobotId()));
		player.setRobotId(getRobotInfo(player).getRobotId());
		final ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP)
		{
			@Override
			public void unequip(Item item, Player owner)
			{
				if (item.getEquipmentType() == EquipType.WEAPON)
				{
					effect.endEffect();
				}
			}
		};
		player.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		super.endEffect(effect);
		final Creature effected = effect.getEffected();
		final Player player = (Player) effected;
		if (player.isUseRobot())
		{
			PacketSendUtility.broadcastPacket(player, new SM_RIDE_ROBOT(player, 0), true);
			player.setUseRobot(false);
			player.setRobotId(0);
		}
		
		final ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
		{
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
	
	/**
	 * Retrieves the robot information for a specific player.<br>
	 * This method looks up the data based on the player's main hand weapon skin.
	 * @param player The {@link Player} whose equipment is being checked.
	 * @return The {@link RobotInfo} associated with the player's current robot ID.
	 */
	public RobotInfo getRobotInfo(Player player)
	{
		final ItemTemplate template = player.getEquipment().getMainHandWeapon().getItemSkinTemplate();
		return DataManager.ROBOT_DATA.getRobotInfo(template.getRobotId());
	}
}
