package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * This packet handles a request from the client to join a specific legion.<br>
 * It communicates with {@link LegionService} to process the joining logic.
 * @author CoolyT
 */
public class CM_LEGION_JOIN_REQUEST extends AionClientPacket
{
	private String joinRequestMsg;
	private int legionId;
	private int joinType;
	
	/**
	 * This constructor initializes a new {@link CM_LEGION_JOIN_REQUEST} packet.<br>
	 * It passes the network details to the parent class.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state of the sender.
	 * @param restStates Variable arguments for additional connection states.
	 */
	public CM_LEGION_JOIN_REQUEST(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		legionId = readD();
		readS(); // LegionName
		joinType = readC();
		joinRequestMsg = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		if (player == null)
		{
			return;
		}
		
		LegionService.getInstance().handleLegionJoinRequest(player, legionId, joinType, joinRequestMsg);
	}
}
