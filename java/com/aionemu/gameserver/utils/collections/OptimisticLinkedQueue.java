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
package com.aionemu.gameserver.utils.collections;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * A thread-safe FIFO queue that uses an optimistic lock-free algorithm.<br>
 * It is designed to reduce {@code CAS} failures during enqueue operations compared to the standard {@code ConcurrentLinkedQueue}.<br>
 * This class provides a high-performance collection for concurrent environments.
 * @param <E>
 */
public class OptimisticLinkedQueue<E> extends AbstractQueue<E> implements Serializable
{
	private static final long serialVersionUID = -3445502502831420722L;
	
	private static class Node<E>
	{
		private volatile E item;
		private volatile Node<E> next;
		private volatile Node<E> prev;
		
		Node(E x, Node<E> n)
		{
			item = x;
			next = n;
			prev = null;
		}
		
		E getItem()
		{
			return item;
		}
		
		@SuppressWarnings("unused")
		void setItem(E val)
		{
			item = val;
		}
		
		Node<E> getNext()
		{
			return next;
		}
		
		void setNext(Node<E> val)
		{
			next = val;
		}
		
		Node<E> getPrev()
		{
			return prev;
		}
		
		void setPrev(Node<E> val)
		{
			prev = val;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<OptimisticLinkedQueue, Node> tailUpdater = AtomicReferenceFieldUpdater.newUpdater(OptimisticLinkedQueue.class, Node.class, "tail");
	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<OptimisticLinkedQueue, Node> headUpdater = AtomicReferenceFieldUpdater.newUpdater(OptimisticLinkedQueue.class, Node.class, "head");
	
	/**
	 * Atomically updates the {@code tail} of the queue.<br>
	 * It uses a compare-and-set operation to ensure thread safety.
	 * @param cmp The expected current value of the tail.
	 * @param val The new value to set as the tail.
	 * @return {@code true} if the update was successful, and {@code false} otherwise.
	 */
	private boolean casTail(Node<E> cmp, Node<E> val)
	{
		return tailUpdater.compareAndSet(this, cmp, val);
	}
	
	/**
	 * Atomically updates the {@code head} of the queue.<br>
	 * It uses a compare-and-set operation to ensure thread safety.
	 * @param cmp The expected current value of the head.
	 * @param val The new value to set as the head.
	 * @return {@code true} if the update was successful, {@code false} otherwise.
	 */
	private boolean casHead(Node<E> cmp, Node<E> val)
	{
		return headUpdater.compareAndSet(this, cmp, val);
	}
	
	/**
	 * Pointer to the head node, initialized to a dummy node. The first actual node is at head.getPrev().
	 */
	private transient volatile Node<E> head = new Node<>(null, null);
	/**
	 * Pointer to last node on list
	 */
	private transient volatile Node<E> tail = head;
	
	/**
	 * Creates a new empty instance of {@link OptimisticLinkedQueue}.<br>
	 * This constructor initializes the internal structure for thread-safe operations.
	 */
	public OptimisticLinkedQueue()
	{
	}
	
	AtomicInteger count = new AtomicInteger();
	
	/**
	 * Inserts the specified element into the queue.<br>
	 * This method returns {@code true} if the operation succeeds.<br>
	 * It throws a {@code NullPointerException} if the input is {@code null}.
	 * @param e The element to add to the queue.
	 * @return {@code true} if successful, and {@code false} otherwise.
	 */
	@Override
	public boolean offer(E e)
	{
		if (e == null)
		{
			throw new NullPointerException();
		}
		
		final Node<E> n = new Node<>(e, null);
		for (;;)
		{
			final Node<E> t = tail;
			n.setNext(t);
			count.incrementAndGet();
			if (casTail(t, n))
			{
				t.setPrev(n);
				return true;
			}
		}
	}
	
	/**
	 * Retrieves and removes the head of this queue.<br>
	 * This method returns {@code null} if the queue is empty.<br>
	 * It uses a lock-free approach to ensure thread safety.
	 * @return The element at the front of the queue, or {@code null} if empty.
	 */
	@Override
	public E poll()
	{
		for (;;)
		{
			final Node<E> h = head;
			final Node<E> t = tail;
			final Node<E> first = h.getPrev();
			if (h == head)
			{
				if (h != t)
				{
					if (first == null)
					{
						fixList(t, h);
						continue;
					}
					
					final E item = first.getItem();
					if (casHead(h, first))
					{
						h.setNext(null);
						h.setPrev(null);
						count.decrementAndGet();
						return item;
					}
				}
				else
				{
					return null;
				}
			}
		}
	}
	
	/**
	 * Corrects the links between nodes in the queue.<br>
	 * This method ensures that {@code prev} pointers are updated correctly.<br>
	 * It iterates from node {@code t} until it reaches node {@code h}.
	 * @param t The starting node for the repair process.
	 * @param h The target head node to reach.
	 */
	private void fixList(Node<E> t, Node<E> h)
	{
		Node<E> curNodeNext;
		Node<E> curNode = t;
		while ((h == head) && (curNode != h))
		{
			curNodeNext = curNode.getNext();
			curNodeNext.setPrev(curNode);
			curNode = curNode.getNext();
		}
	}
	
	/**
	 * Removes all elements from this queue.<br>
	 * The {@code size()} will become 0 after this call.<br>
	 * This method does not affect other collections.
	 */
	@Override
	public void clear()
	{
		while (poll() != null)
		{
		}
	}
	
	/**
	 * Removes all elements from the queue except for the last one.<br>
	 * The last element is re-added to the end of the queue using {@code offer}.
	 * @return The number of elements that were removed from the queue.
	 */
	public int leaveTail()
	{
		E elem = null;
		E elem1 = null;
		int removed = 0;
		while ((elem = poll()) != null)
		{
			elem1 = elem;
			removed++;
		}
		
		if (elem1 != null)
		{
			removed--;
			offer(elem1);
		}
		
		return removed;
	}
	
	/**
	 * This method is not supported.<br>
	 * It will always throw an {@code UnsupportedOperationException}.
	 * @return the element at the head of this queue, which is never returned.
	 */
	@Override
	public E peek()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns an {@link Iterator} for the elements in this queue.<br>
	 * This operation is not supported and will throw an exception.
	 * @return An {@code Iterator} of type {@code E}.
	 */
	@Override
	public Iterator<E> iterator()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the current number of elements in this queue.<br>
	 * This value is retrieved from an internal counter.
	 * @return The total number of items currently stored in the collection.
	 */
	@Override
	public int size()
	{
		return count.get();
	}
}
