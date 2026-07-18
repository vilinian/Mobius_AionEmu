package com.aionemu.gameserver.model.gameobjects;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Defines the possible actions that a {@link Minion} can perform.<br>
 * This enum is used to manage behaviors for various types of minions in the game world.
 * @author ATracer
 */
public enum MinionAction
{
	
	ADOPT(0),
	DISMISS(1),
	RENAME(2),
	LOCK(3),
	SUMMON(4),
	UNSUMMON(5),
	GROWTH(6),
	EVOLVE(7),
	COMBINE(8),
	FUNCTION_SETTING(9),
	FUNCTION(10),
	ENERGY_RECHARGE(11),
	AUTO_FUNCTION(12),
	UNK(13),
	BUFFING(14),
	UNKNOWN(255);
	
	private static TIntObjectHashMap<MinionAction> minionActions;
	
	static
	{
		minionActions = new TIntObjectHashMap<>();
		for (MinionAction action : values())
		{
			minionActions.put(action.getActionId(), action);
		}
	}
	
	private final int actionId;
	
	/**
	 * This is a private constructor for the {@link MinionAction} enum.<br>
	 * It assigns a unique identifier to each action type.
	 * @param actionId The integer ID associated with this specific minion action.
	 */
	private MinionAction(int actionId)
	{
		this.actionId = actionId;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link MinionAction}.<br>
	 * This ID is used to map actions between the client and server.
	 * @return The integer value of the {@code actionId}.
	 */
	public int getActionId()
	{
		return actionId;
	}
	
	/**
	 * Retrieves a {@link MinionAction} based on its unique ID.<br>
	 * This method looks up the value in the internal map.<br>
	 * It returns {@code MinionAction.UNKNOWN} if the ID is not found.
	 * @param actionId The integer ID of the action to find.
	 * @return The corresponding {@link MinionAction} or {@code UNKNOWN}.
	 */
	public static MinionAction getActionById(int actionId)
	{
		final MinionAction action = minionActions.get(actionId);
		return action != null ? action : UNKNOWN;
	}
}
