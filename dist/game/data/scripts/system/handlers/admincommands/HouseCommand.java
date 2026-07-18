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
package system.handlers.admincommands;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerHouseOwnerFlags;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseStatus;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Handles administrative commands related to the {@link House} system.<br>
 * This class allows administrators to manage house ownership and properties.<br>
 * It interacts with {@link HousingService} to perform various house-related actions.
 * @author Rolandas
 */
public class HouseCommand extends AdminCommand
{
	/**
	 * Creates a new instance of the {@link HouseCommand} class.<br>
	 * This command allows administrators to manage house properties.<br>
	 * It registers the command with the name {@code house}.
	 */
	public HouseCommand()
	{
		super("house");
	}
	
	/**
	 * Executes the command to manage houses.<br>
	 * It allows administrators to acquire, revoke, or teleport to a house.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings containing the action and the target house name.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if (params.length == 0)
		{
			PacketSendUtility.sendMessage(admin, "Syntax: //house <tp | acquire | revoke>");
			return;
		}
		
		if (params[0].equals("acquire"))
		{
			if (params.length == 1)
			{
				PacketSendUtility.sendMessage(admin, "Syntax: //house acquire <name>");
				return;
			}
			
			ChangeHouseOwner(admin, params[1].toUpperCase(), true);
		}
		else if (params[0].equals("revoke"))
		{
			if (params.length == 1)
			{
				PacketSendUtility.sendMessage(admin, "Syntax: //house revoke <name>");
				return;
			}
			
			ChangeHouseOwner(admin, params[1].toUpperCase(), false);
		}
		else if (params[0].equals("tp"))
		{
			if (params.length == 1)
			{
				PacketSendUtility.sendMessage(admin, "Syntax: //house tp <name>");
				return;
			}
			
			final House house = HousingService.getInstance().getHouseByName(params[1].toUpperCase());
			if (house == null)
			{
				PacketSendUtility.sendMessage(admin, "No such house!");
				return;
			}
			
			final HouseAddress address = house.getAddress();
			TeleportService2.teleportTo(admin, address.getMapId(), address.getX(), address.getY(), address.getZ());
		}
		
	}
	
	/**
	 * Changes the ownership of a house for a target player.<br>
	 * This method handles both acquiring and revoking houses based on the provided flags.<br>
	 * It updates the database, registry, and sends necessary packets to the players.
	 * @param admin The administrator performing the action.
	 * @param houseName The name of the house to modify.
	 * @param acquire Set to {@code true} to give a house to the target, or {@code false} to remove it.
	 */
	private void ChangeHouseOwner(Player admin, String houseName, boolean acquire)
	{
		Player target = null;
		final VisibleObject creature = admin.getTarget();
		
		if (admin.getTarget() instanceof Player)
		{
			target = (Player) creature;
		}
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}
		
		if (acquire)
		{
			if (target.getHouses().size() == 2)
			{
				PacketSendUtility.sendMessage(admin, "Player can not own more than 2 houses!");
				return;
			}
			
			final House house = HousingService.getInstance().getHouseByName(houseName);
			if (house == null)
			{
				PacketSendUtility.sendMessage(admin, "No such house!");
				return;
			}
			
			if (target.getHouses().size() == 1)
			{
				final House current = target.getHouses().get(0);
				current.revokeOwner();
				if (current.getBuilding().getType() == BuildingType.PERSONAL_INS)
				{
					target.getHouses().remove(current);
					PacketSendUtility.sendMessage(admin, "Deleted studio.");
				}
				else
				{
					current.setStatus(HouseStatus.ACTIVE);
					current.setFeePaid(true);
					current.setNextPay(null);
					current.save();
					PacketSendUtility.sendMessage(admin, current.getName() + " status is now " + current.getStatus().toString());
				}
			}
			
			house.setAcquiredTime(new Timestamp(System.currentTimeMillis()));
			house.setOwnerId(target.getCommonData().getPlayerObjId());
			house.setStatus(HouseStatus.ACTIVE);
			house.setFeePaid(true);
			house.setNextPay(null); // TODO: fix it
			house.reloadHouseRegistry();
			house.save();
			target.getHouses().add(house);
			target.setHouseRegistry(house.getRegistry());
			target.setBuildingOwnerState(PlayerHouseOwnerFlags.HOUSE_OWNER.getId());
			PacketSendUtility.sendMessage(admin, "House " + house.getName() + " acquired");
			PacketSendUtility.sendPacket(target, new SM_HOUSE_OWNER_INFO(target, house));
			PacketSendUtility.sendPacket(target, new SM_HOUSE_ACQUIRE(target.getObjectId(), house.getAddress().getId(), true));
		}
		else
		{
			if (target.getHouses().size() == 0)
			{
				PacketSendUtility.sendMessage(admin, "Nothing to revoke!");
				return;
			}
			
			House revokedHouse = null;
			for (House house : target.getHouses())
			{
				if (house.getName().equals(houseName))
				{
					revokedHouse = house;
					house.revokeOwner();
				}
				else if (house.getStatus() != HouseStatus.ACTIVE)
				{
					house.setStatus(HouseStatus.ACTIVE);
					house.setSellStarted(null);
					house.save();
				}
			}
			
			if (revokedHouse == null)
			{
				PacketSendUtility.sendMessage(admin, "Target doesn't own this house!");
				return;
			}
			
			target.getHouses().remove(revokedHouse);
			House oldHouse = null;
			if (target.getHouses().size() != 0)
			{
				oldHouse = target.getHouses().get(0);
			}
			else
			{
				target.setBuildingOwnerState(PlayerHouseOwnerFlags.BUY_STUDIO_ALLOWED.getId());
			}
			
			target.setHouseRegistry(oldHouse == null ? null : oldHouse.getRegistry());
			PacketSendUtility.sendMessage(admin, "House " + revokedHouse.getName() + " revoked");
			PacketSendUtility.sendPacket(target, new SM_HOUSE_OWNER_INFO(target, oldHouse));
			PacketSendUtility.sendPacket(target, new SM_HOUSE_ACQUIRE(target.getObjectId(), revokedHouse.getAddress().getId(), false));
			revokedHouse.getController().updateAppearance();
		}
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
		PacketSendUtility.sendMessage(player, "syntax //house <tp | list | acquire | revoke>");
	}
}
