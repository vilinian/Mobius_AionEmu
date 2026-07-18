package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.InstanceEntryCostEnum;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.InstanceEntryService;

/**
 * This packet handles the request from a client to enter a Luna instance.<br>
 * It validates the player's eligibility and processes the entry through the {@link InstanceEntryService}.
 */
public class CM_LUNA_INSTANCE_ENTRY extends AionClientPacket
{
	private int syncId;
	private InstanceEntryCostEnum type;
	
	/**
	 * Creates a new {@link CM_LUNA_INSTANCE_ENTRY} packet.<br>
	 * This constructor initializes the packet with specific network states.<br>
	 * It passes all arguments to the parent class constructor.
	 * @param opcode The unique identifier for this packet type.
	 * @param state The primary connection state.
	 * @param restStates A variable number of additional connection states.
	 */
	public CM_LUNA_INSTANCE_ENTRY(int opcode, State state, State... restStates)
	{
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl()
	{
		syncId = readD();
		readD();
		type = InstanceEntryCostEnum.getCotstId(readC());
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getConnection().getActivePlayer();
		final int worldId = DataManager.INSTANCE_COOLTIME_DATA.getSyncId(syncId);
		InstanceEntryService.getInstance().onResetInstanceEntry(player, worldId, type);
	}
}
