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
package com.aionemu.commons.utils;

import java.util.Random;

/**
 * This class provides a Java implementation of the MT19937 (Mersenne Twister) pseudo-random number generator algorithm.<br>
 * It serves as a drop-in replacement for {@code Random} with a much longer period and larger seed support.<br>
 * Note that this class is not cryptographically secure and should not be used for security-sensitive operations.
 * @author David Beaumont, Copyright 2005
 *         <p/>
 *         A Java implementation of the MT19937 (Mersenne Twister) pseudo random number generator algorithm based upon the original C code by Makoto Matsumoto and Takuji Nishimura (see <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html"> http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html</a> for more information.
 *         <p/>
 *         As a subclass of java.util.Random this class provides a single canonical method next() for generating bits in the pseudo random number sequence. Anyone using this class should invoke the public inherited methods (nextInt(), nextFloat etc.) to obtain values as normal. This class should provide a drop-in replacement for the standard implementation of java.util.Random with the additional advantage of having a far longer period and the ability to use a far larger seed value.
 *         <p/>
 *         This is <b>not</b> a cryptographically strong source of randomness and should <b>not</b> be used for cryptographic systems or in any other situation where true random numbers are required.
 *         <p/>
 *         <!-- Creative Commons License --> <a href="http://creativecommons.org/licenses/LGPL/2.1/"><img alt= "CC-GNU LGPL" border="0" src= "http://creativecommons.org/images/public/cc-LGPL-a.png" /></a><br />
 *         This software is licensed under the <a href="http://creativecommons.org/licenses/LGPL/2.1/">CC-GNU LGPL</a>. <!-- /Creative Commons License --> <!-- <rdf:RDF xmlns="http://web.resource.org/cc/" xmlns:dc= "http://purl.org/dc/elements/1.1/" xmlns:rdf= "http://www.w3.org/1999/02/22-rdf-syntax-ns#"> <Work rdf:about=""> <license rdf:resource="http://creativecommons.org/licenses/LGPL/2.1/" /> <dc:type rdf:resource="http://purl.org/dc/dcmitype/Software" /> </Work> <License rdf:about="http://creativecommons.org/licenses/LGPL/2.1/"> <permits rdf:resource="http://web.resource.org/cc/Reproduction" /> <permits rdf:resource="http://web.resource.org/cc/Distribution" /> <requires rdf:resource="http://web.resource.org/cc/Notice" /> <permits rdf:resource="http://web.resource.org/cc/DerivativeWorks" /> <requires rdf:resource="http://web.resource.org/cc/ShareAlike" /> <requires rdf:resource="http://web.resource.org/cc/SourceCode" /> </License> </rdf:RDF> -->
 */
public class MTRandom extends Random
{
	/**
	 * Auto-generated serial version UID. Note that MTRandom does NOT support serialisation of its internal state and it may even be necessary to implement read/write methods to re-seed it properly. This is only here to make Eclipse shut up about it being missing.
	 */
	private static final long serialVersionUID = -515082678588212038L;
	
	// Constants used in the original C implementation
	private final static int UPPER_MASK = 0x80000000;
	private final static int LOWER_MASK = 0x7fffffff;
	private final static int N = 624;
	private final static int M = 397;
	private final static int[] MAGIC =
	{
		0x0,
		0x9908b0df
	};
	private final static int MAGIC_FACTOR1 = 1812433253;
	private final static int MAGIC_FACTOR2 = 1664525;
	private final static int MAGIC_FACTOR3 = 1566083941;
	private final static int MAGIC_MASK1 = 0x9d2c5680;
	private final static int MAGIC_MASK2 = 0xefc60000;
	private final static int MAGIC_SEED = 19650218;
	private final static long DEFAULT_SEED = 5489L;
	
	// Internal state
	private transient int[] mt;
	private transient int mti;
	private transient boolean compat = false;
	
	// Temporary buffer used during setSeed(long)
	private transient int[] ibuf;
	
	/**
	 * Creates a new instance of {@link MTRandom}.<br>
	 * This constructor uses the default settings.<br>
	 * It initializes the generator without special compatibility modes.
	 */
	public MTRandom()
	{
		this(false);
	}
	
	/**
	 * Creates a new instance of {@link MTRandom}.<br>
	 * This constructor initializes the generator with a seed based on the compatibility flag.<br>
	 * If {@code compatible} is {@code true}, it uses a default seed.<br>
	 * Otherwise, it uses the current system time as the seed.
	 * @param compatible Determines whether to use a default seed or the system time.
	 */
	public MTRandom(boolean compatible)
	{
		super(0L);
		compat = compatible;
		setSeed(compat ? DEFAULT_SEED : System.currentTimeMillis());
	}
	
	/**
	 * Creates a new instance of {@link MTRandom}.<br>
	 * This constructor initializes the generator using a specific seed.<br>
	 * It calls the superclass constructor to set the initial state.
	 * @param seed The initial value used to start the random sequence.
	 */
	public MTRandom(long seed)
	{
		super(seed);
	}
	
	/**
	 * Creates a new {@link MTRandom} instance using a byte array as the seed.<br>
	 * This method initializes the generator with the provided data.
	 * @param buf The {@code byte[]} used to seed the random number generator.
	 */
	public MTRandom(byte[] buf)
	{
		super(0L);
		setSeed(buf);
	}
	
	/**
	 * Creates a new instance of {@link MTRandom}.<br>
	 * This constructor initializes the generator using an array of integers.<br>
	 * The provided array is used to set the internal seed.
	 * @param buf An array of {@code int} values used for seeding.
	 */
	public MTRandom(int[] buf)
	{
		super(0L);
		setSeed(buf);
	}
	
	// Initializes mt[N] with a simple integer seed, which is required for the Mersenne Twister algorithm but does not need to be public.
	/**
	 * Initializes the internal state of the generator using a specific value.<br>
	 * This method sets up the {@code mt} array for the Mersenne Twister algorithm.<br>
	 * It ensures that the random sequence starts from a predictable point.
	 * @param seed The initial integer value used to generate the sequence.
	 */
	private void setSeed(int seed)
	{
		// An annoying runtime check for internal data initialization is caused by java.util.Random invoking setSeed() during initialization.
		// This is unavoidable because no fields in our instance will be initialized at this point, even if the code were placed at the member variable's declaration.
		if (mt == null)
		{
			mt = new int[N];
		}
		
		// ---- Begin Mersenne Twister Algorithm ----
		mt[0] = seed;
		for (mti = 1; mti < N; mti++)
		{
			mt[mti] = ((MAGIC_FACTOR1 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30))) + mti);
		}
		
		// ---- End Mersenne Twister Algorithm ----
	}
	
	/**
	 * Sets the initial seed for the random number generator.<br>
	 * This method updates the internal state to produce a new sequence of numbers.<br>
	 * It handles both compatible and non-compatible modes automatically.
	 * @param seed The {@code long} value used to initialize the generator.
	 */
	@Override
	public synchronized void setSeed(long seed)
	{
		if (compat)
		{
			setSeed((int) seed);
		}
		else
		{
			// An annoying runtime check for internal data initialization is caused by java.util.Random invoking setSeed() during initialization.
			// This is unavoidable because no fields in our instance will be initialized at this point, even if the code were placed at the member variable's declaration.
			if (ibuf == null)
			{
				ibuf = new int[2];
			}
			
			ibuf[0] = (int) seed;
			ibuf[1] = (int) (seed >>> 32);
			setSeed(ibuf);
		}
	}
	
	/**
	 * Sets the seed for the random number generator.<br>
	 * This method uses the {@code pack} method to process the input.<br>
	 * It replaces the current internal state with a new sequence.
	 * @param buf The byte array used to initialize the seed.
	 */
	public void setSeed(byte[] buf)
	{
		setSeed(pack(buf));
	}
	
	/**
	 * Sets the seed for the random number generator using an array of integers.<br>
	 * This method updates the internal state of the {@link MTRandom} instance.<br>
	 * The provided buffer must not be empty.
	 * @param buf An array of integers used to initialize the generator.
	 */
	public synchronized void setSeed(int[] buf)
	{
		final int length = buf.length;
		if (length == 0)
		{
			throw new IllegalArgumentException("Seed buffer may not be empty");
		}
		
		// ---- Begin Mersenne Twister Algorithm ----
		int i = 1, j = 0, k = (N > length ? N : length);
		setSeed(MAGIC_SEED);
		for (; k > 0; k--)
		{
			mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * MAGIC_FACTOR2)) + buf[j] + j;
			i++;
			j++;
			if (i >= N)
			{
				mt[0] = mt[N - 1];
				i = 1;
			}
			
			if (j >= length)
			{
				j = 0;
			}
		}
		
		for (k = N - 1; k > 0; k--)
		{
			mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * MAGIC_FACTOR3)) - i;
			i++;
			if (i >= N)
			{
				mt[0] = mt[N - 1];
				i = 1;
			}
		}
		
		mt[0] = UPPER_MASK; // MSB is 1; assuring non-zero initial array
		// ---- End Mersenne Twister Algorithm ----
	}
	
	/**
	 * Generates the next sequence of pseudo-random bits.<br>
	 * This method implements the core Mersenne Twister algorithm.<br>
	 * It handles internal state updates and tempering.
	 * @param bits The number of random bits to return.
	 * @return An {@code int} containing the generated bits.
	 */
	@Override
	protected synchronized int next(int bits)
	{
		// ---- Begin Mersenne Twister Algorithm ----
		int y, kk;
		if (mti >= N)
		{
			// generate N words at one time
			
			// In the original C implementation, mti is checked here to determine if initialization has occurred; if not, it initializes this instance with DEFAULT_SEED (5489).
			// This is no longer necessary because initializing the Java instance results in initialization; use the constructor MTRandom(true) to enable backward-compatible behavior.
			
			for (kk = 0; kk < (N - M); kk++)
			{
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + M] ^ (y >>> 1) ^ MAGIC[y & 0x1];
			}
			
			for (; kk < (N - 1); kk++)
			{
				y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
				mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ MAGIC[y & 0x1];
			}
			
			y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
			mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ MAGIC[y & 0x1];
			
			mti = 0;
		}
		
		y = mt[mti++];
		
		// Tempering
		y ^= (y >>> 11);
		y ^= (y << 7) & MAGIC_MASK1;
		y ^= (y << 15) & MAGIC_MASK2;
		y ^= (y >>> 18);
		
		// ---- End Mersenne Twister Algorithm ----
		return (y >>> (32 - bits));
	}
	
	// This is a fairly obscure code section that packs a byte array into an integer array using little-endian ordering.
	
	/**
	 * Converts a {@code byte[]} into an array of integers.<br>
	 * This method packs four bytes into a single {@code int}.<br>
	 * It is used to prepare seed data for the generator.
	 * @param buf The source byte array to be packed.
	 * @return An array of {@code int} values containing the packed bytes.
	 */
	public static int[] pack(byte[] buf)
	{
		int k;
		final int blen = buf.length, ilen = ((buf.length + 3) >>> 2);
		final int[] ibuf = new int[ilen];
		for (int n = 0; n < ilen; n++)
		{
			int m = (n + 1) << 2;
			if (m > blen)
			{
				m = blen;
			}
			
			for (k = buf[--m] & 0xff; (m & 0x3) != 0; k = (k << 8) | (buf[--m] & 0xff))
			{
				ibuf[n] = k;
			}
		}
		
		return ibuf;
	}
}
