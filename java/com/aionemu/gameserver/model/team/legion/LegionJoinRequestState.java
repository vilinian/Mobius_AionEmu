/**
 * 
 */
package com.aionemu.gameserver.model.team.legion;

/**
 * Represents the different states of a request to join a {@link com.aionemu.gameserver.model.team.legion.Legion}.<br>
 * This enum tracks whether a join request is pending, accepted, or rejected.
 * @author CoolyT
 */
public enum LegionJoinRequestState
{
	ACCEPTED,
	DENIED,
	NONE,
	CANCEL;
}
