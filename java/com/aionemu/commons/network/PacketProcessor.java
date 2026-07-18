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
package com.aionemu.commons.network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.packet.BaseClientPacket;

/**
 * This class handles the execution of network packets for a specific connection.<br>
 * It ensures that packets are processed in the order they were received.<br>
 * It enforces a rule where only one packet per client is executed at any given time.
 * @author -Nemesiss-
 * @param <T> AConnection - owner of client packets.
 */
public class PacketProcessor<T extends AConnection>
{
	/**
	 * Logger for PacketProcessor
	 */
	private static final Logger log = LoggerFactory.getLogger(PacketProcessor.class.getName());
	
	/**
	 * When one working thread should be created.
	 */
	private final int threadSpawnThreshold;
	
	/**
	 * When one working thread should be killed.
	 */
	private final int threadKillThreshold;
	
	/**
	 * Lock for synchronization.
	 */
	private final Lock lock = new ReentrantLock();
	
	/**
	 * Not Empty condition.
	 */
	private final Condition notEmpty = lock.newCondition();
	
	/**
	 * Queue of packet that will be executed in correct order.
	 */
	private final List<BaseClientPacket<T>> packets = new LinkedList<>();
	
	/**
	 * Working threads.
	 */
	private final List<Thread> threads = new ArrayList<>();
	
	/**
	 * minimum number of working Threads
	 */
	private final int minThreads;
	
	/**
	 * maximum number of working Threads
	 */
	private final int maxThreads;
	
	/**
	 * Executor that will be used to execute packets
	 */
	private final Executor executor;
	
	private static class DummyExecutor implements Executor
	{
		@Override
		public void execute(Runnable command)
		{
			command.run();
		}
	}
	
	/**
	 * Initializes a new {@link PacketProcessor} with specific threading rules.<br>
	 * This constructor sets the limits for the internal thread pool.<br>
	 * It uses a default executor for processing packets.
	 * @param minThreads The minimum number of threads to keep alive.
	 * @param maxThreads The maximum number of threads allowed.
	 * @param threadSpawnThreshold The queue size that triggers creating a new thread.
	 * @param threadKillThreshold The queue size that triggers removing a thread.
	 */
	public PacketProcessor(int minThreads, int maxThreads, int threadSpawnThreshold, int threadKillThreshold)
	{
		this(minThreads, maxThreads, threadSpawnThreshold, threadKillThreshold, new DummyExecutor());
	}
	
	/**
	 * Initializes a new {@link PacketProcessor} with specific thread pool settings.<br>
	 * This constructor sets the limits for worker threads and starts the initial pool.<br>
	 * It also configures the thresholds for spawning and killing threads dynamically.
	 * @param minThreads The minimum number of threads to keep alive in the pool.
	 * @param maxThreads The maximum number of threads allowed in the pool.
	 * @param threadSpawnThreshold The queue size at which a new thread should be created.
	 * @param threadKillThreshold The queue size at which an idle thread should be removed.
	 * @param executor The {@code Executor} used to run the packet processing tasks.
	 */
	public PacketProcessor(int minThreads, int maxThreads, int threadSpawnThreshold, int threadKillThreshold, Executor executor)
	{
		if (!(minThreads > 0))
		{
			throw new IllegalArgumentException("Min Threads must be positive");
		}
		if (!(maxThreads >= minThreads))
		{
			throw new IllegalArgumentException("Max Threads must be >= Min Threads");
		}
		if (!(threadSpawnThreshold > 0))
		{
			throw new IllegalArgumentException("Thread Spawn Threshold must be positive");
		}
		if (!(threadKillThreshold > 0))
		{
			throw new IllegalArgumentException("Thread Kill Threshold must be positive");
		}
		
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
		this.threadSpawnThreshold = threadSpawnThreshold;
		this.threadKillThreshold = threadKillThreshold;
		this.executor = executor;
		
		if (minThreads != maxThreads)
		{
			startCheckerThread();
		}
		
		for (int i = 0; i < minThreads; i++)
		{
			newThread();
		}
	}
	
	/**
	 * Starts a new background thread to monitor the packet queue.<br>
	 * This thread runs the {@code CheckerTask} logic.<br>
	 * It is used to manage the lifecycle of worker threads.
	 */
	private void startCheckerThread()
	{
		new Thread(new CheckerTask(), "PacketProcessor:Checker").start();
	}
	
	/**
	 * Checks if a new worker thread can be created.<br>
	 * It verifies that the current count is below {@code maxThreads}.<br>
	 * If successful, it starts a new {@link Thread} and adds it to the list.
	 * @return {@code true} if a new thread was successfully started, or {@code false} otherwise.
	 */
	private boolean newThread()
	{
		if (threads.size() >= maxThreads)
		{
			return false;
		}
		
		final String name = "PacketProcessor:" + threads.size();
		log.debug("Creating new PacketProcessor Thread: " + name);
		
		final Thread t = new Thread(new PacketProcessorTask(), name);
		threads.add(t);
		t.start();
		
		return true;
	}
	
	/**
	 * Stops an active worker thread.<br>
	 * This method checks if the current number of threads exceeds {@code minThreads}.<br>
	 * If it does, it removes and interrupts the last thread in the {@code threads} list.
	 */
	private void killThread()
	{
		if (threads.size() < minThreads)
		{
			final Thread t = threads.remove((threads.size() - 1));
			log.debug("Killing PacketProcessor Thread: " + t.getName());
			t.interrupt();
		}
	}
	
	/**
	 * Adds a packet to the processing queue.<br>
	 * This method ensures the packet is handled in the correct order.<br>
	 * It signals the worker threads that new data is available.
	 * @param packet The {@code BaseClientPacket<T>} to be executed.
	 */
	public void executePacket(BaseClientPacket<T> packet)
	{
		lock.lock();
		try
		{
			packets.add(packet);
			notEmpty.signal();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * Retrieves the next available packet from the queue.<br>
	 * This method waits until a packet is found that can be processed.<br>
	 * It checks if the connection associated with the packet is currently free.
	 * @return The first {@code BaseClientPacket<T>} that is ready for execution.
	 */
	private BaseClientPacket<T> getFirstAviable()
	{
		for (;;)
		{
			while (packets.isEmpty())
			{
				notEmpty.awaitUninterruptibly();
			}
			
			final ListIterator<BaseClientPacket<T>> it = packets.listIterator();
			while (it.hasNext())
			{
				final BaseClientPacket<T> packet = it.next();
				if (packet.getConnection().tryLockConnection())
				{
					it.remove();
					return packet;
				}
			}
			
			notEmpty.awaitUninterruptibly();
		}
	}
	
	/**
	 * Packet Processor Task that will execute packet with respecting rules: - 1 packet / client at one time. - execute packets in received order.
	 * @author -Nemesiss-
	 */
	private final class PacketProcessorTask implements Runnable
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			BaseClientPacket<T> packet = null;
			for (;;)
			{
				lock.lock();
				try
				{
					if (packet != null)
					{
						packet.getConnection().unlockConnection();
					}
					
					/* thread killed */
					if (Thread.interrupted())
					{
						return;
					}
					
					packet = getFirstAviable();
				}
				finally
				{
					lock.unlock();
				}
				
				executor.execute(packet);
			}
		}
	}
	
	/**
	 * Checking if PacketProcessor is busy or idle and increasing / reducing numbers of threads.
	 * @author -Nemesiss-
	 */
	private final class CheckerTask implements Runnable
	{
		/**
		 * How often CheckerTask should do check.
		 */
		private final static int sleepTime = 60 * 1000;
		/**
		 * Number of packets waiting for execution on last check.
		 */
		private int lastSize = 0;
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			/* Sleep for some time */
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				// we dont care
			}
			
			/* Number of packets waiting for execution */
			final int packetsToExecute = packets.size();
			
			if ((packetsToExecute < lastSize) && (packetsToExecute < threadKillThreshold))
			{
				// too much threads
				killThread();
			}
			else if ((packetsToExecute > lastSize) && (packetsToExecute > threadSpawnThreshold))
			{
				// too small amount of threads
				if (!newThread() && (packetsToExecute >= (threadSpawnThreshold * 3)))
				{
					log.info("Lagg detected! [" + packetsToExecute + " client packets are waiting for execution]. You should consider increasing PacketProcessor maxThreads or hardware upgrade.");
				}
			}
			
			lastSize = packetsToExecute;
		}
	}
}
