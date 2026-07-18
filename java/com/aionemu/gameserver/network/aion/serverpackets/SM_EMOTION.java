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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the transmission of emotion data between the server and the client.<br>
 * It is used to trigger specific animations or visual effects for a {@link Creature}.
 * @author SoulKeeper
 * @modified -Enomine- 4.0
 */
public class SM_EMOTION extends AionServerPacket
{
	/**
	 * Object id of emotion sender
	 */
	private final int senderObjectId;
	/**
	 * Some unknown variable
	 */
	private final EmotionType emotionType;
	/**
	 * ID of emotion
	 */
	private int emotion;
	/**
	 * Object id of emotion target
	 */
	private int targetObjectId;
	/**
	 * Temporary Speed..
	 */
	private float speed;
	private final int state;
	private int baseAttackSpeed;
	private int currentAttackSpeed;
	/**
	 * Coordinates of player
	 */
	private float x;
	private float y;
	private float z;
	private byte heading;
	
	/**
	 * Creates a new {@code SM_EMOTION} packet for a specific creature.<br>
	 * This constructor is used when the emotion ID and target object ID are both {@code 0}.
	 * @param creature The {@link Creature} that is performing the emotion.
	 * @param emotionType The type of emotion to be sent.
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType)
	{
		this(creature, emotionType, 0, 0);
	}
	
	/**
	 * Creates a new {@code SM_EMOTION} packet for a specific creature.<br>
	 * This constructor sets the sender ID and various speed statistics from the {@link Creature}.<br>
	 * It also stores the emotion details and the target object ID.
	 * @param creature The {@link Creature} performing the emotion.
	 * @param emotionType The type of emotion being performed.
	 * @param emotion The specific ID of the emotion.
	 * @param targetObjectId The ID of the target for this emotion.
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType, int emotion, int targetObjectId)
	{
		senderObjectId = creature.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.targetObjectId = targetObjectId;
		state = creature.getState();
		final Stat2 aSpeed = creature.getGameStats().getAttackSpeed();
		baseAttackSpeed = aSpeed.getBase();
		currentAttackSpeed = aSpeed.getCurrent();
		speed = creature.getGameStats().getMovementSpeedFloat();
	}
	
	/**
	 * Creates a new {@link SM_EMOTION} packet with basic information.<br>
	 * This constructor is used for simple emotion updates.
	 * @param Objid The unique identifier of the sender.
	 * @param emotionType The type of emotion to perform.
	 * @param state The current state of the emotion.
	 */
	public SM_EMOTION(int Objid, EmotionType emotionType, int state)
	{
		senderObjectId = Objid;
		this.emotionType = emotionType;
		this.state = state;
	}
	
	/**
	 * Creates a new {@code SM_EMOTION} packet for a specific player.<br>
	 * This constructor initializes the emotion data and captures the player's current movement stats.
	 * @param player The {@link Player} object who is performing the emotion.
	 * @param emotionType The category of the emotion being performed.
	 * @param emotion The specific ID of the emotion animation.
	 * @param x The X coordinate of the player's position.
	 * @param y The Y coordinate of the player's position.
	 * @param z The Z coordinate of the player's position.
	 * @param heading The current facing direction of the player.
	 * @param targetObjectId The ID of the object being targeted by the emotion.
	 */
	public SM_EMOTION(Player player, EmotionType emotionType, int emotion, float x, float y, float z, byte heading, int targetObjectId)
	{
		senderObjectId = player.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.targetObjectId = targetObjectId;
		
		state = player.getState();
		speed = player.getGameStats().getMovementSpeedFloat();
		final Stat2 aSpeed = player.getGameStats().getAttackSpeed();
		baseAttackSpeed = aSpeed.getBase();
		currentAttackSpeed = aSpeed.getCurrent();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(senderObjectId);
		writeC(emotionType.getTypeId());
		writeH(state);
		writeF(speed);
		switch (emotionType)
		{
			case LAND_FLYTELEPORT: // fly teleport (land)
			case FLY: // toggle flight mode
			case LAND: // toggle land mode
			case SELECT_TARGET: // select target
			case JUMP:
			case SIT: // sit
			case STAND: // stand
			case ATTACKMODE: // toggle attack mode TODO STATE and SPEED
			case NEUTRALMODE: // toggle normal mode
			case WALK: // toggle walk
			case RUN: // toggle run
			case OPEN_PRIVATESHOP: // private shop open
			case CLOSE_PRIVATESHOP: // private shop close
			case POWERSHARD_ON: // powershard on
			case POWERSHARD_OFF: // powershard off
			case ATTACKMODE2: // toggle attack mode
			case NEUTRALMODE2: // toggle normal mode
			case START_FEEDING:
			case END_FEEDING:
			case WINDSTREAM_START_BOOST:
			case WINDSTREAM_END_BOOST:
			case WINDSTREAM_END:
			case WINDSTREAM_EXIT:
			case OPEN_DOOR:
				writeH(9);
				writeD(0);
				break;
			case CLOSE_DOOR:
			case WINDSTREAM_STRAFE:
				break;
			case DIE: // die
			case START_LOOT: // looting start
			case END_LOOT: // looting end
			case START_QUESTLOOT: // looting start (quest)
			case END_QUESTLOOT: // looting end (quest);
				writeD(targetObjectId);
				break;
			case CHAIR_SIT: // sit (chair)
			case CHAIR_UP: // stand (chair)
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case START_FLYTELEPORT:
				// fly teleport (start)
				writeD(emotion); // teleport Id
				break;
			case WINDSTREAM:
				// entering windstream
				writeD(emotion); // teleport Id
				writeD(targetObjectId); // distance
				break;
			case RIDE:
			case RIDE_END:
				if (targetObjectId != 0)
				{
					writeD(targetObjectId); // rideId
				}
				
				writeH(0); // emotion?
				writeC(0); // type
				writeD(0x3F); // unk
				writeD(0x3F); // unk
				writeC(0x40); // unk
				break;
			case RESURRECT:
				// resurrect
				writeD(0);
				break;
			case EMOTE:
				// emote
				writeD(targetObjectId);
				writeH(emotion);
				writeC(1);
				break;
			case START_EMOTE2:
				// emote startloop
				writeH(baseAttackSpeed);
				writeH(currentAttackSpeed);
				writeC(0); // new 4.0
				break;
			case START_FLYBOOST_SPEED:
				writeH(0);
				writeD(0);
			case END_FLYBOOST_SPEED:
				break;
			default:
				if (targetObjectId != 0)
				{
					writeD(targetObjectId);
				}
		}
	}
}
