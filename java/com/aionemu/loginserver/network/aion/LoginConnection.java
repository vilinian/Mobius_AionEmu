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
package com.aionemu.loginserver.network.aion;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.PacketProcessor;
import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.controller.AccountTimeController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.aion.serverpackets.SM_INIT;
import com.aionemu.loginserver.network.factories.AionPacketHandlerFactory;
import com.aionemu.loginserver.network.ncrypt.CryptEngine;
import com.aionemu.loginserver.network.ncrypt.EncryptedRSAKeyPair;
import com.aionemu.loginserver.network.ncrypt.KeyGen;

/**
 * Represents a network connection between the {@code LoginServer} and an Aion client.<br>
 * It handles communication, packet processing, and encryption for authenticated sessions.
 * @author -Nemesiss-
 */
public class LoginConnection extends AConnection
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginConnection.class);
	/**
	 * PacketProcessor for executing packets.
	 */
	private final static PacketProcessor<LoginConnection> processor = new PacketProcessor<>(1, 8, 50, 3);
	/**
	 * Server Packet "to send" Queue
	 */
	private final Deque<AionServerPacket> sendMsgQueue = new ArrayDeque<>();
	/**
	 * Unique Session Id of this connection
	 */
	private final int sessionId = hashCode();
	/**
	 * Account object for this connection. if state = AUTHED_LOGIN account cant be null.
	 */
	private Account account;
	/**
	 * Crypt to encrypt/decrypt packets
	 */
	private CryptEngine cryptEngine;
	/**
	 * True if this user is connecting to GS.
	 */
	private boolean joinedGs;
	/**
	 * Scrambled key pair for RSA
	 */
	private EncryptedRSAKeyPair encryptedRSAKeyPair;
	/**
	 * Session Key for this connection.
	 */
	private SessionKey sessionKey;
	/**
	 * Current state of this connection
	 */
	private State state;
	
	/**
	 * Possible states of AionConnection
	 */
	public static enum State
	{
		/**
		 * Means that client just connects
		 */
		CONNECTED,
		/**
		 * Means that clients GameGuard is authenticated
		 */
		AUTHED_GG,
		/**
		 * Means that client is logged in.
		 */
		AUTHED_LOGIN
	}
	
	/**
	 * Creates a new {@link LoginConnection} instance.<br>
	 * This constructor initializes the connection with specific buffer sizes.
	 * @param sc The {@code SocketChannel} used for network communication.
	 * @param d The {@code Dispatcher} used to handle incoming data.
	 */
	public LoginConnection(SocketChannel sc, Dispatcher d)
	{
		super(sc, d, 8192 * 2, 8192 * 2);
	}
	
	/**
	 * Decrypts and processes incoming data from the client.<br>
	 * It validates the packet structure and checks for flooding.<br>
	 * If the packet is valid, it executes the corresponding logic.
	 * @param data The {@code ByteBuffer} containing the raw packet data.
	 * @return {@code true} if the data was processed successfully or skipped due to decryption failure; {@code false} otherwise.
	 */
	@Override
	protected boolean processData(ByteBuffer data)
	{
		if (!decrypt(data))
		{
			return false;
		}
		
		final AionClientPacket pck = AionPacketHandlerFactory.handle(data, this);
		
		/**
		 * Execute packet only if packet exist (!= null) and read was ok.
		 */
		if ((pck != null) && pck.read())
		{
			processor.executePacket(pck);
		}
		
		return true;
	}
	
	/**
	 * Writes data to the outgoing message queue.<br>
	 * This method retrieves the next packet from {@code sendMsgQueue}.<br>
	 * It assigns the provided buffer to that packet and writes it.
	 * @param data The {@code ByteBuffer} containing the information to write.
	 * @return {@code true} if a packet was successfully processed, or {@code false} if the queue is empty.
	 */
	@Override
	protected synchronized boolean writeData(ByteBuffer data)
	{
		final AionServerPacket packet = sendMsgQueue.pollFirst();
		
		if (packet == null)
		{
			return false;
		}
		
		packet.setBuf(data);
		packet.write(this);
		
		return true;
	}
	
	/**
	 * Retrieves the delay before a disconnection occurs.<br>
	 * This method returns the current timeout value in milliseconds.
	 * @return The disconnection delay as a {@code long}.
	 */
	@Override
	protected long getDisconnectionDelay()
	{
		return 0;
	}
	
	/**
	 * Handles the cleanup logic when a client disconnects.<br>
	 * Stops the {@code pingChecker}.<br>
	 * Notifies the {@code LoginServer} if an account is logged in.<br>
	 * Triggers the world leave process for the active player.
	 */
	@Override
	protected void onDisconnect()
	{
		/**
		 * Remove account only if not joined GameServer yet.
		 */
		if ((account != null) && !joinedGs)
		{
			AccountController.removeAccountOnLS(account);
			AccountTimeController.updateOnLogout(account);
		}
	}
	
	/**
	 * Handles the logic when the server is shutting down.<br>
	 * This method forces the connection to close immediately.<br>
	 * It calls {@code boolean)} with a {@code true} flag.
	 */
	@Override
	protected void onServerClose()
	{
		// TODO mb some packet should be send to client before closing?
		close( /* packet, */true);
	}
	
	/**
	 * Decrypts the data provided in the {@code ByteBuffer}.<br>
	 * It uses the internal {@code cryptEngine} to perform the decryption.<br>
	 * If the checksum is incorrect, a warning is logged.
	 * @param buf The {@code ByteBuffer} containing the encrypted data to be decrypted.
	 * @return {@code true} if decryption was successful, or {@code false} otherwise.
	 */
	private boolean decrypt(ByteBuffer buf)
	{
		final int size = buf.remaining();
		final int offset = buf.arrayOffset() + buf.position();
		final boolean ret = cryptEngine.decrypt(buf.array(), offset, size);
		
		if (!ret)
		{
			log.warn("Wrong checksum from client: " + this);
		}
		
		return ret;
	}
	
	/**
	 * Encrypts the data contained in the provided {@code ByteBuffer}.<br>
	 * This method uses the internal {@code CryptEngine} to process the buffer.
	 * @param buf The {@code ByteBuffer} containing the raw data to be encrypted.
	 * @return The new size of the encrypted data.
	 */
	public int encrypt(ByteBuffer buf)
	{
		int size = buf.limit() - 2;
		final int offset = buf.arrayOffset() + buf.position();
		
		size = cryptEngine.encrypt(buf.array(), offset, size);
		
		return size;
	}
	
	/**
	 * Sends a server packet to the connected client.<br>
	 * This method adds the {@code AionServerPacket} to the outgoing queue.<br>
	 * It ensures that the connection is active before attempting to send data.
	 * @param bp The {@code AionServerPacket} object to be sent.
	 */
	public synchronized void sendPacket(AionServerPacket bp)
	{
		/**
		 * Connection is already closed or waiting for last (close packet) to be sent
		 */
		if (isWriteDisabled())
		{
			return;
		}
		
		log.debug("sending packet: " + bp);
		sendMsgQueue.addLast(bp);
		enableWriteInterest();
	}
	
	/**
	 * Closes the connection and sends a final packet to the client.<br>
	 * This method clears the pending message queue before closing.
	 * @param closePacket The {@code AionServerPacket} to send before disconnecting.
	 * @param forced Set to {@code true} if the connection should be closed immediately.
	 */
	public synchronized void close(AionServerPacket closePacket, boolean forced)
	{
		if (isWriteDisabled())
		{
			return;
		}
		
		log.info("sending packet: " + closePacket + " and closing connection after that.");
		
		pendingClose = true;
		isForcedClosing = forced;
		sendMsgQueue.clear();
		sendMsgQueue.addLast(closePacket);
		enableWriteInterest();
	}
	
	/**
	 * Retrieves the encrypted modulus from the RSA key pair.<br>
	 * This value is used for secure communication between the server and client.
	 * @return a {@code byte[]} containing the encrypted modulus.
	 */
	public byte[] getEncryptedModulus()
	{
		return encryptedRSAKeyPair.getEncryptedModulus();
	}
	
	/**
	 * Retrieves the private RSA key for this connection.<br>
	 * This key is used for secure communication with the client.
	 * @return the {@code RSAPrivateKey} associated with this session.
	 */
	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) encryptedRSAKeyPair.getRSAKeyPair().getPrivate();
	}
	
	/**
	 * Retrieves the unique session identifier for this connection.<br>
	 * This ID is generated based on the {@code hashCode()} of the object.
	 * @return The unique {@code int} session ID.
	 */
	public int getSessionId()
	{
		return sessionId;
	}
	
	/**
	 * Retrieves the current connection status.<br>
	 * This method returns the {@code State} of the current connection.
	 * @return The current {@code State} of this connection.
	 */
	public State getState()
	{
		return state;
	}
	
	/**
	 * Updates the current connection state.<br>
	 * This method sets the {@code state} field to the provided value.
	 * @param state The new {@code State} to apply to this connection.
	 */
	public void setState(State state)
	{
		this.state = state;
	}
	
	/**
	 * Retrieves the {@link Account} associated with this connection.
	 * @return The current {@code Account} object.
	 */
	public Account getAccount()
	{
		return account;
	}
	
	/**
	 * Sets the {@link Account} associated with this connection.<br>
	 * This method updates the internal account reference.<br>
	 * The provided {@code account} parameter must not be {@code null}.
	 * @param account The {@link Account} object to set.
	 */
	public void setAccount(Account account)
	{
		this.account = account;
	}
	
	/**
	 * Retrieves the current {@code SessionKey} for this connection.<br>
	 * This key is used to secure communication between the server and the client.
	 * @return The {@code SessionKey} associated with this session.
	 */
	public SessionKey getSessionKey()
	{
		return sessionKey;
	}
	
	/**
	 * Updates the current {@code SessionKey} for this connection.<br>
	 * This key is used to secure communication between the server and client.
	 * @param sessionKey The new {@link SessionKey} to assign.
	 */
	public void setSessionKey(SessionKey sessionKey)
	{
		this.sessionKey = sessionKey;
	}
	
	/**
	 * Updates the connection state to indicate that the user has joined a Game Server.<br>
	 * This sets the {@code joinedGs} flag to {@code true}.
	 */
	public void setJoinedGs()
	{
		joinedGs = true;
	}
	
	/**
	 * Returns a string representation of the current connection.<br>
	 * It displays the {@link Account} information and the IP address.<br>
	 * If no account is logged in, it shows a default message with the IP.
	 * @return A string describing the login status and IP address.
	 */
	@Override
	public String toString()
	{
		return (account != null) ? account + " " + getIP() : "not loged " + getIP();
	}
	
	/**
	 * Returns a hash code value for this {@link LoginConnection} object.<br>
	 * This value is used to identify the object in collections like {@code HashSet}.<br>
	 * It is calculated based on the properties of the parent class.
	 * @return The integer hash code of this object.
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	/**
	 * Closes the current connection immediately.<br>
	 * This method calls {@code boolean)} with a {@code false} value for the forced parameter.
	 */
	public void closeNow()
	{
		this.close(false);
	}
	
	/**
	 * This method performs the initial setup for the connection.<br>
	 * It sends the {@code SM_KEY} packet to the client.
	 */
	@Override
	protected void initialized()
	{
		// TODO Auto-generated method stub
		state = State.CONNECTED;
		log.info("Connection attemp from: " + getIP());
		encryptedRSAKeyPair = KeyGen.getEncryptedRSAKeyPair();
		final SecretKey blowfishKey = KeyGen.generateBlowfishKey();
		
		cryptEngine = new CryptEngine();
		cryptEngine.updateKey(blowfishKey.getEncoded());
		
		/**
		 * Send Init packet
		 */
		sendPacket(new SM_INIT(this, blowfishKey));
	}
}
