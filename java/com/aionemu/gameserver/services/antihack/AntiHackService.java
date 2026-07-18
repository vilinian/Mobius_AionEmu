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
package com.aionemu.gameserver.services.antihack;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.movement.MovementMask;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAMEGUARD;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

/**
 * This service provides core logic to detect and prevent cheating behaviors in the game.<br>
 * It monitors player actions such as movement and speed to ensure fair gameplay.<br>
 * It interacts with {@link Player} objects to enforce security rules defined in {@link SecurityConfig}.
 * @author Source
 */
public class AntiHackService
{
	/**
	 * Validates if a player's movement is legal based on speed and position.<br>
	 * This method checks for abnormal actions, speed hacks, and teleportation.<br>
	 * It updates the player's previous position if the move is valid.
	 * @param player The {@link Player} object to check.
	 * @param x The target X coordinate.
	 * @param y The target Y coordinate.
	 * @param z The target Z coordinate.
	 * @param speed The movement speed of the player.
	 * @param type The movement type mask.
	 * @return {@code true} if the move is allowed, {@code false} otherwise.
	 */
	public static boolean canMove(Player player, float x, float y, float z, float speed, byte type)
	{
		if (player.getPrevPos() == null)
		{
			return true;
		}
		
		final AionServerPacket forcedMove = new SM_FORCED_MOVE(player, player.getObjectId(), x, y, z);
		final AionServerPacket normalMove = new SM_MOVE(player);
		
		if (player.getAccessLevel() > 3)
		{
			return true;
		}
		
		if (SecurityConfig.ABNORMAL)
		{
			if (!player.canPerformMove() && !player.getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE) && ((type & MovementMask.GLIDE) != MovementMask.GLIDE))
			{
				if (player.abnormalHackCounter > SecurityConfig.ABNORMAL_COUNTER)
				{
					punish(player, x, y, type, forcedMove, "Detected illegal Action (Anti-Abnormal Hack)");
					return false;
				}
				
				player.abnormalHackCounter++;
			}
			else
			{
				player.abnormalHackCounter = 0;
			}
		}
		
		if (SecurityConfig.SPEEDHACK)
		{
			if (type != 0)
			{
				if ((type == -64) || (type == -128))
				{
					final PlayerMoveController m = player.getMoveController();
					final double vector2D = MathUtil.getDistance(x, y, m.getTargetX2(), m.getTargetY2());
					
					if (vector2D != 0)
					{
						if ((type == -64) && (vector2D > 5) && (vector2D > (speed + 0.001)))
						{
							player.speedHackCounter++;
						}
						else if ((vector2D > 37.5) && (vector2D > ((1.5 * speed * speed) + 0.001)))
						{
							player.speedHackCounter++;
						}
						else if (player.speedHackCounter > 0)
						{
							player.speedHackCounter--;
						}
						
						if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER)
						{
							return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)" + " SHC:" + player.speedHackCounter + " S:" + speed + " V:" + (Math.rint(1000.0 * vector2D) / 1000.0) + " type:" + type);
						}
					}
				}
				else if (((type & MovementMask.MOUSE) == MovementMask.MOUSE) && ((type & MovementMask.GLIDE) != MovementMask.GLIDE))
				{
					final double vector = MathUtil.getDistance(x, y, player.getPrevPos().getX(), player.getPrevPos().getY());
					final long timeDiff = System.currentTimeMillis() - player.prevPosUT;
					
					if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE)
					{
						boolean isMoveToTarget = false;
						if ((player.getTarget() != null) && (player.getTarget() != player))
						{
							final PlayerMoveController m = player.getMoveController();
							final double distDiff = MathUtil.getDistance(Math.round(player.getTarget().getX()), Math.round(player.getTarget().getY()), Math.round(m.getTargetX2()), Math.round(m.getTargetY2()));
							isMoveToTarget = distDiff <= 5;
						}
						
						if ((timeDiff > 1000) && (player.speedHackCounter > 0))
						{
							player.speedHackCounter--;
						}
						
						if (vector > (timeDiff * (speed + 0.85) * 0.001))
						{
							player.speedHackCounter++;
						}
						else if (isMoveToTarget && (player.speedHackCounter > 0))
						{
							player.speedHackCounter--;
						}
					}
					else if (vector > (timeDiff * (speed + 0.25) * 0.001))
					{
						player.speedHackCounter++;
					}
					else if (player.speedHackCounter > 0)
					{
						player.speedHackCounter--;
					}
					
					if ((SecurityConfig.PUNISH > 0) && (player.speedHackCounter > (SecurityConfig.SPEEDHACK_COUNTER + 5)))
					{
						return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)" + " SHC:" + player.speedHackCounter + " SMS:" + (Math.rint(100.0 * (timeDiff * (speed + 0.25) * 0.001)) / 100.0) + " TDF:" + timeDiff + " VTD:" + (Math.rint(1000.0 * (timeDiff * (speed + 0.85) * 0.001)) / 1000.0) + " VS:" + (Math.rint(100.0 * vector) / 100.0) + " type:" + type);
					}
					else if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER)
					{
						moveBack(player, x, y, type, forcedMove);
						return false;
					}
				}
			}
			else
			{
				final double vector = MathUtil.getDistance(x, y, player.getPrevPos().getX(), player.getPrevPos().getY());
				final long timeDiff = System.currentTimeMillis() - player.prevPosUT;
				
				if ((player.prevMoveType == 0) && (vector > (timeDiff * speed * 0.00075)))
				{
					player.speedHackCounter++;
				}
				
				if ((SecurityConfig.PUNISH > 0) && (player.speedHackCounter > (SecurityConfig.SPEEDHACK_COUNTER + 5)))
				{
					return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)" + " SHC:" + player.speedHackCounter + " TD:" + (Math.rint(1000.0 * timeDiff) / 1000.0) + " VTD:" + (Math.rint(1000.0 * (timeDiff * speed * 0.00075)) / 1000.0) + " VS:" + (Math.rint(100.0 * vector) / 100.0) + " type:" + type);
				}
				else if (player.speedHackCounter > (SecurityConfig.SPEEDHACK_COUNTER + 2))
				{
					moveBack(player, x, y, type, forcedMove);
					return false;
				}
			}
			
			// Store prev. move info
			player.getPrevPos().setXYZH(x, y, z, player.getHeading());
			player.prevPosUT = System.currentTimeMillis();
			if (player.prevMoveType != type)
			{
				player.prevMoveType = type;
			}
		}
		
		if (SecurityConfig.TELEPORTATION)
		{
			final double delta = MathUtil.getDistance(x, y, player.getX(), player.getY()) / speed;
			if ((speed > 5.0) && (delta > 5.0) && ((type & MovementMask.GLIDE) != MovementMask.GLIDE))
			{
				World.getInstance().updatePosition(player, player.getX(), player.getY(), player.getZ(), player.getHeading());
				return punish(player, x, y, type, normalMove, "Detected illegal action (Teleportation)" + " S:" + speed + " D:" + (Math.rint(1000.0 * delta) / 1000.0) + " type:" + type);
			}
		}
		
		return true;
	}
	
	/**
	 * Applies a penalty to a {@link Player} based on the configured security settings.<br>
	 * This method checks the player's access level before deciding how to handle the violation.<br>
	 * It can log an audit message, move the player back, or disconnect them from the server.
	 * @param player The {@link Player} object to be punished.
	 * @param x The X coordinate of the movement.
	 * @param y The Y coordinate of the movement.
	 * @param type The movement type identifier.
	 * @param pkt The {@link AionServerPacket} received from the client.
	 * @param message The description of the violation for logging purposes.
	 * @return {@code true} if no punishment was applied, or {@code false} if a penalty occurred.
	 */
	protected static boolean punish(Player player, float x, float y, byte type, AionServerPacket pkt, String message)
	{
		if (player.getAccessLevel() > 3)
		{
			return true;
		}
		
		switch (SecurityConfig.PUNISH)
		{
			case 1:
				AuditLogger.info(player, message);
				moveBack(player, x, y, type, pkt);
				return false;
			case 2:
				AuditLogger.info(player, message);
				moveBack(player, x, y, type, pkt);
				if ((player.speedHackCounter > (SecurityConfig.SPEEDHACK_COUNTER * 3)) || (player.abnormalHackCounter > (SecurityConfig.ABNORMAL_COUNTER * 3)))
				{
					player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
				}
				
				return false;
			case 3:
				AuditLogger.info(player, message);
				player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
				return false;
			default:
				AuditLogger.info(player, message);
				return true;
		}
	}
	
	/**
	 * Moves a {@link Player} back to a previous position.<br>
	 * This method updates the player's movement state and timestamp.<br>
	 * It is used by the anti-hack system to correct illegal movements.
	 * @param player The {@link Player} object to be moved.
	 * @param x The new X coordinate for the player.
	 * @param y The new Y coordinate for the player.
	 * @param type The movement type identifier.
	 * @param pkt The {@link AionServerPacket} associated with the move.
	 */
	protected static void moveBack(Player player, float x, float y, byte type, AionServerPacket pkt)
	{
		PacketSendUtility.broadcastPacketAndReceive(player, pkt);
		player.getMoveController().updateLastMove();
		player.prevPosUT = System.currentTimeMillis();
		if (player.prevMoveType != type)
		{
			player.prevMoveType = type;
		}
	}
	
	/**
	 * Verifies the integrity of the {@code aion.bin} file.<br>
	 * This method checks if the provided {@code size} matches the expected value.<br>
	 * If the check fails and {@code INTEGRITY_CHECK} is enabled, it disconnects the player.<br>
	 * It also sends an {@link com.aionemu.gameserver.network.aion.serverpackets.SM_GAMEGUARD} packet to the client.
	 * @param size The size of the {@code aion.bin} file reported by the client.
	 * @param player The {@link Player} object representing the current user.
	 */
	public static void checkAionBin(int size, Player player)
	{
		final int legitSize = 212; // TODO NEW SIZE
		
		if (SecurityConfig.INTEGRITY_CHECK)
		{
			if (size != legitSize)
			{
				AuditLogger.info(player, "Detected modified aion.bin");
				player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
			}
		}
		
		PacketSendUtility.sendPacket(player, new SM_GAMEGUARD(size));
	}
}
