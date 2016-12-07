/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.util;

/**
 * Copied from apache/hbase.
 * @see https://github.com/apache/hbase/blob/master/hbase-common/src/main/java/org/apache/hadoop/hbase/util/SimplePositionedMutableByteRange.java
 */
public class SimplePositionedMutableByteRange implements PositionedByteRange {

	public static final int UNSET_HASH_VALUE = -1;

	// Note to maintainers: Do not make these final, as the intention is to
	// reuse objects of this class

	/**
	 * The array containing the bytes in this range. It will be &gt;= length.
	 */
	private byte[] bytes;

	/**
	 * The index of the first byte in this range. {@code ByteRange.get(0)} will return bytes[offset].
	 */
	private int offset;

	/**
	 * The number of bytes in the range. Offset + length must be &lt;= bytes.length
	 */
	private int length;

	/**
	 * Variable for lazy-caching the hashCode of this range. Useful for frequently used ranges, long-lived ranges, or long ranges.
	 */
	private int hash = UNSET_HASH_VALUE;

	/**
	 * The current index into the range. Like {@link java.nio.ByteBuffer} position, it points to the next value that will be read/written in the
	 * array. It provides the appearance of being 0-indexed, even though its value is calculated according to offset.
	 * <p>
	 * Position is considered transient and does not participate in {@link #equals(Object)} or {@link #hashCode()} comparisons.
	 * </p>
	 */
	private int position = 0;

	private int limit = 0;

	public SimplePositionedMutableByteRange() {
	}

	public SimplePositionedMutableByteRange(int capacity) {
		this(new byte[capacity]);
	}

	public SimplePositionedMutableByteRange(byte[] bytes) {
		set(bytes);
	}

	public SimplePositionedMutableByteRange(byte[] bytes, int offset, int length) {
		set(bytes, offset, length);
	}

	@Override
	public PositionedByteRange unset() {
		this.position = 0;
		clearHashCache();
		bytes = null;
		offset = 0;
		length = 0;
		return this;
	}

	@Override
	public PositionedByteRange set(int capacity) {
		this.position = 0;
		set(new byte[capacity]);
		this.limit = capacity;
		return this;
	}

	@Override
	public PositionedByteRange set(byte[] bytes) {
		this.position = 0;
		if (null == bytes)
			return unset();
		clearHashCache();
		this.bytes = bytes;
		this.offset = 0;
		this.length = bytes.length;
		this.limit = bytes.length;
		return this;
	}

	@Override
	public PositionedByteRange set(byte[] bytes, int offset, int length) {
		this.position = 0;
		if (null == bytes)
			return unset();
		clearHashCache();
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
		limit = length;
		return this;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public byte peek() {
		return bytes[offset + position];
	}

	@Override
	public PositionedByteRange setPosition(int position) {
		this.position = position;
		return this;
	}

	@Override
	public int getRemaining() {
		return length - position;
	}

	@Override
	public byte get() {
		return get(position++);
	}

	@Override
	public byte get(int index) {
		return bytes[offset + index];
	}

	@Override
	public PositionedByteRange get(byte[] dst) {
		if (0 == dst.length)
			return this;
		return this.get(dst, 0, dst.length); // be clear we're calling self, not super
	}

	@Override
	public PositionedByteRange get(byte[] dst, int offset, int length) {
		if (0 == length)
			return this;
		get(this.position, dst, offset, length);
		this.position += length;
		return this;
	}

	@Override
	public PositionedByteRange get(int index, byte[] dst, int offset, int length) {
		if (0 == length)
			return this;
		System.arraycopy(this.bytes, this.offset + index, dst, offset, length);
		return this;
	}

	@Override
	public short getShort() {
		short s = getShort(position);
		position += Bytes.SIZEOF_SHORT;
		return s;
	}

	@Override
	public int getInt() {
		int i = getInt(position);
		position += Bytes.SIZEOF_INT;
		return i;
	}

	@Override
	public long getLong() {
		long l = getLong(position);
		position += Bytes.SIZEOF_LONG;
		return l;
	}

	@Override
	public long getVLong() {
		long p = getVLong(position);
		position += getVLongSize(p);
		return p;
	}

	@Override
	public short getShort(int index) {
		int offset = this.offset + index;
		short n = 0;
		n ^= bytes[offset] & 0xFF;
		n <<= 8;
		n ^= bytes[offset + 1] & 0xFF;
		return n;
	}

	@Override
	public int getInt(int index) {
		int offset = this.offset + index;
		int n = 0;
		for (int i = offset; i < (offset + Bytes.SIZEOF_INT); i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}

	@Override
	public long getLong(int index) {
		int offset = this.offset + index;
		long l = 0;
		for (int i = offset; i < offset + Bytes.SIZEOF_LONG; i++) {
			l <<= 8;
			l ^= bytes[i] & 0xFF;
		}
		return l;
	}

	// Copied from com.google.protobuf.CodedInputStream v2.5.0 readRawVarint64
	@Override
	public long getVLong(int index) {
		int shift = 0;
		long result = 0;
		while (shift < 64) {
			final byte b = get(index++);
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				break;
			}
			shift += 7;
		}
		return result;
	}
	// end of copied from protobuf

	public static int getVLongSize(long val) {
		int rPos = 0;
		while ((val & ~0x7F) != 0) {
			val >>>= 7;
			rPos++;
		}
		return rPos + 1;
	}

	@Override
	public PositionedByteRange put(byte val) {
		put(position++, val);
		return this;
	}

	@Override
	public PositionedByteRange put(byte[] val) {
		if (0 == val.length)
			return this;
		return this.put(val, 0, val.length);
	}

	@Override
	public PositionedByteRange put(int index, byte val) {
		bytes[offset + index] = val;
		return this;
	}

	@Override
	public PositionedByteRange put(int index, byte[] val) {
		if (0 == val.length)
			return this;
		return put(index, val, 0, val.length);
	}

	@Override
	public PositionedByteRange put(byte[] val, int offset, int length) {
		if (0 == length)
			return this;
		put(position, val, offset, length);
		this.position += length;
		return this;
	}

	@Override
	public PositionedByteRange put(int index, byte[] val, int offset, int length) {
		if (0 == length)
			return this;
		System.arraycopy(val, offset, this.bytes, this.offset + index, length);
		return this;
	}

	@Override
	public PositionedByteRange putShort(short val) {
		putShort(position, val);
		position += Bytes.SIZEOF_SHORT;
		return this;
	}

	@Override
	public PositionedByteRange putInt(int val) {
		putInt(position, val);
		position += Bytes.SIZEOF_INT;
		return this;
	}

	@Override
	public PositionedByteRange putLong(long val) {
		putLong(position, val);
		position += Bytes.SIZEOF_LONG;
		return this;
	}

	@Override
	public int putVLong(long val) {
		int len = putVLong(position, val);
		position += len;
		return len;
	}

	@Override
	public PositionedByteRange putShort(int index, short val) {
		// This writing is same as BB's putShort. When byte[] is wrapped in a BB and
		// call putShort(),
		// one can get the same result.
		bytes[offset + index + 1] = (byte) val;
		val >>= 8;
		bytes[offset + index] = (byte) val;
		clearHashCache();
		return this;
	}

	@Override
	public PositionedByteRange putInt(int index, int val) {
		// This writing is same as BB's putInt. When byte[] is wrapped in a BB and
		// call getInt(), one
		// can get the same result.
		for (int i = Bytes.SIZEOF_INT - 1; i > 0; i--) {
			bytes[offset + index + i] = (byte) val;
			val >>>= 8;
		}
		bytes[offset + index] = (byte) val;
		clearHashCache();
		return this;
	}

	@Override
	public PositionedByteRange putLong(int index, long val) {
		// This writing is same as BB's putLong. When byte[] is wrapped in a BB and
		// call putLong(), one
		// can get the same result.
		for (int i = Bytes.SIZEOF_LONG - 1; i > 0; i--) {
			bytes[offset + index + i] = (byte) val;
			val >>>= 8;
		}
		bytes[offset + index] = (byte) val;
		clearHashCache();
		return this;
	}

	// Copied from com.google.protobuf.CodedOutputStream v2.5.0 writeRawVarint64
	@Override
	public int putVLong(int index, long val) {
		int rPos = 0;
		while (true) {
			if ((val & ~0x7F) == 0) {
				bytes[offset + index + rPos] = (byte) val;
				break;
			} else {
				bytes[offset + index + rPos] = (byte) ((val & 0x7F) | 0x80);
				val >>>= 7;
			}
			rPos++;
		}
		clearHashCache();
		return rPos + 1;
	}
	// end copied from protobuf

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int hashCode() {
		if (isHashCached()) {// hash is already calculated and cached
			return hash;
		}
		if (this.isEmpty()) {// return 0 for empty ByteRange
			hash = 0;
			return hash;
		}
		int off = offset;
		hash = 0;
		for (int i = 0; i < length; i++) {
			hash = 31 * hash + bytes[off++];
		}
		return hash;
	}

	protected boolean isHashCached() {
		return hash != UNSET_HASH_VALUE;
	}

	protected void clearHashCache() {
		hash = UNSET_HASH_VALUE;
	}

	@Override
	public boolean isEmpty() {
		return isEmpty(this);
	}

	/**
	 * @return true when {@code range} is of zero length, false otherwise.
	 */
	public static boolean isEmpty(PositionedByteRange range) {
		return range == null || range.getLength() == 0;
	}
	
	@Override
	public int compareTo(ByteRange other) {
		return Bytes.BYTES_COMPARATOR.compare(bytes, offset, length, other.getBytes(), other.getOffset(), other.getLength());
	}

}
