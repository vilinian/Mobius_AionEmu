package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * This packet handles the cancellation of a request to join a legion.<br>
 * It is sent by the client to notify the server that the player no longer wishes to join.<br>
 * The server uses this to update the state within the {@link LegionService}.
 * @author CoolyT
 */
public class CM_LEGION_JOIN_REQUEST_CANCEL extends AionClientPacket
{
	private int legionId;
	
	/**
	 * This method initializes a new {@code CM_LEGION_JOIN_REQUEST_CANCEL} packet.<br>
	 * It sets the required network states for the request.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates Variable arguments for additional connection states.
	 */
	public CM_LEGION_JOIN_REQUEST_CANCEL(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		legionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		LegionService.getInstance().handleJoinRequestCancel(player, legionId);
	}
}
