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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This class handles administrative commands related to the status of game entities.<br>
 * It allows administrators to modify and manage {@link Creature} states within the game world.
 * @author Rolandas
 */
public class State extends AdminCommand
{
	/**
	 * Initializes a new instance of the {@code State} class.<br>
	 * This constructor sets up the default configuration for the admin command.
	 */
	public State()
	{
		super("state");
	}
	
	static final Map<Integer, CreatureState> creatureStateLookup = new HashMap<>();
	static final Map<Integer, TestState> testStateLookup = new HashMap<>();
	static
	{
		for (CreatureState s : EnumSet.allOf(CreatureState.class))
		{
			creatureStateLookup.put(s.getId(), s);
		}
		for (TestState t : EnumSet.allOf(TestState.class))
		{
			testStateLookup.put(t.id, t);
		}
	}
	
	/**
	 * Executes the command to view or modify a creature's state.<br>
	 * It allows showing the current state or setting/unsetting specific bits for a target {@code Creature}.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings containing the action (show, set, unset) and optional bit numbers.
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		final VisibleObject target = admin.getTarget();
		
		if (target == null)
		{
			PacketSendUtility.sendMessage(admin, "Select a target first!!!");
			return;
		}
		
		if ((params == null) || (params.length == 0))
		{
			PacketSendUtility.sendMessage(admin, "syntax //state <show | set | unset>");
			return;
		}
		
		if (!(target instanceof Creature))
		{
			PacketSendUtility.sendMessage(admin, "You can select only creatures!!!");
			return;
		}
		
		final Creature creature = (Creature) target;
		
		if (params[0].equals("show"))
		{
			if (params.length != 1)
			{
				PacketSendUtility.sendMessage(admin, "syntax //state show");
				return;
			}
			
			if (creature.equals(admin))
			{
				PacketSendUtility.sendMessage(admin, "Your state is : " + creature.getState() + "\n" + getStateDescription((short) admin.getState()));
			}
			else
			{
				PacketSendUtility.sendMessage(admin, "Creature state is : " + creature.getState() + "\n" + getStateDescription((short) creature.getState()));
			}
		}
		else if (params[0].equals("set") || params[0].equals("unset"))
		{
			if (params.length != 2)
			{
				PacketSendUtility.sendMessage(admin, "syntax //state set <bit number>");
				return;
			}
			
			int number;
			try
			{
				number = Integer.valueOf(params[1]);
			}
			catch (NumberFormatException e)
			{
				PacketSendUtility.sendMessage(admin, "syntax //state set <bit number>");
				return;
			}
			
			if ((number < 1) || (number > 16))
			{
				PacketSendUtility.sendMessage(admin, "syntax <bit number> should be in range 1-16");
				return;
			}
			
			short newState = 0;
			
			if (params[0].equals("set"))
			{
				newState = (short) ((creature.getState() & 0xFFFF) | (1 << (number - 1)));
			}
			else
			{
				newState = (short) ((creature.getState() & 0xFFFF) & ~(1 << (number - 1)));
			}
			
			PacketSendUtility.sendMessage(admin, "New state : " + newState);
			creature.setState(newState);
			
			if (target.equals(admin))
			{
				PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
			}
			
			admin.clearKnownlist();
			admin.updateKnownlist();
			
			PacketSendUtility.sendMessage(admin, "State changed to : " + creature.getState() + "\n" + getStateDescription((short) creature.getState()));
		}
		else
		{
			PacketSendUtility.sendMessage(admin, "syntax //state <show | set | unset>");
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
	}
	
	/**
	 * Converts a numeric state into a human-readable string.<br>
	 * This method parses the bits of the {@code state} value.<br>
	 * It returns a formatted list of all active states.
	 * @param state The short value representing the combined states.
	 * @return A string containing the descriptions of all active states.
	 */
	String getStateDescription(short state)
	{
		final StringBuilder binsb = new StringBuilder(Integer.toBinaryString(state));
		final StringBuilder bin = binsb.reverse();
		
		final StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		
		for (int i = 0; i < bin.length(); i++)
		{
			if (bin.charAt(i) == '1')
			{
				sb.append("0x");
				final int value = 1 << i;
				sb.append(Integer.toHexString(value));
				
				sb.append(" (");
				sb.append(testStateLookup.get(value).display);
				if (creatureStateLookup.containsKey(value))
				{
					sb.append('=');
					sb.append(creatureStateLookup.get(value).toString());
				}
				
				sb.append("),\n");
			}
		}
		
		if (sb.lastIndexOf(",\n") == (sb.length() - 2))
		{
			sb.setLength(sb.length() - 2);
		}
		
		sb.append("\n}");
		return sb.toString();
	}
	
	public enum TestState
	{
		BIT01(1 << 0, "bit 1"),
		BIT02(1 << 1, "bit 2"),
		BIT03(1 << 2, "bit 3"),
		BIT04(1 << 3, "bit 4"),
		BIT05(1 << 4, "bit 5"),
		BIT06(1 << 5, "bit 6"),
		BIT07(1 << 6, "bit 7"),
		BIT08(1 << 7, "bit 8"),
		BIT09(1 << 8, "bit 9"),
		BIT10(1 << 9, "bit 10"),
		BIT11(1 << 10, "bit 11"),
		BIT12(1 << 11, "bit 12"),
		BIT13(1 << 12, "bit 13"),
		BIT14(1 << 13, "bit 14"),
		BIT15(1 << 14, "bit 15"),
		BIT16(1 << 15, "bit 16");
		
		int id;
		String display;
		
		TestState(int value, String s)
		{
			id = value;
			display = s;
		}
	}
}
