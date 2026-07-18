package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * Handles the client request to search for a specific legion.<br>
 * This packet allows players to query information about legions within the game world.
 * @author CoolyT
 */
public class CM_LEGION_SEARCH extends AionClientPacket
{
	private int type;
	private String legionName;
	
	/**
	 * This constructor initializes a new {@link CM_LEGION_SEARCH} packet.<br>
	 * It sets the required network properties for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the sender.
	 * @param restStates A variable number of additional states associated with the connection.
	 */
	public CM_LEGION_SEARCH(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		type = readC();
		legionName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		LegionService.getInstance().handleLegionSearch(player, type, legionName);
	}
}
