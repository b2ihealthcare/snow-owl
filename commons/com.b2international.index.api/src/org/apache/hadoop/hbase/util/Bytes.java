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

import java.util.Comparator;

import com.google.common.base.Charsets;

/**
 * Copied from apache/hbase
 * @see https://github.com/apache/hbase/blob/master/hbase-common/src/main/java/org/apache/hadoop/hbase/util/Bytes.java
 */
public class Bytes {

	/**
	 * Size of boolean in bytes
	 */
	public static final int SIZEOF_BOOLEAN = Byte.SIZE / Byte.SIZE;

	/**
	 * Size of byte in bytes
	 */
	public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;

	/**
	 * Size of char in bytes
	 */
	public static final int SIZEOF_CHAR = Character.SIZE / Byte.SIZE;

	/**
	 * Size of double in bytes
	 */
	public static final int SIZEOF_DOUBLE = Double.SIZE / Byte.SIZE;

	/**
	 * Size of float in bytes
	 */
	public static final int SIZEOF_FLOAT = Float.SIZE / Byte.SIZE;

	/**
	 * Size of int in bytes
	 */
	public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;

	/**
	 * Size of long in bytes
	 */
	public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;

	/**
	 * Size of short in bytes
	 */
	public static final int SIZEOF_SHORT = Short.SIZE / Byte.SIZE;

	/**
	 * Pass this to TreeMaps where byte [] are keys.
	 */
	public final static ByteArrayComparator BYTES_COMPARATOR = new ByteArrayComparator();

	/**
	 * Converts a string to a UTF-8 byte array.
	 * 
	 * @param s
	 *            string
	 * @return the byte array
	 */
	public static byte[] toBytes(String s) {
		return s.getBytes(Charsets.UTF_8);
	}

	/**
	 * @since 5.4
	 */
	public static class ByteArrayComparator implements Comparator<byte[]> {
		/**
		 * Constructor
		 */
		public ByteArrayComparator() {
			super();
		}

		@Override
		public int compare(byte[] left, byte[] right) {
			return compare(left, 0, left.length, right, 0, right.length);
		}

		public int compare(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
			// Short circuit equal case
			if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
				return 0;
			}
			// Bring WritableComparator code local
			int end1 = offset1 + length1;
			int end2 = offset2 + length2;
			for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
				int a = (buffer1[i] & 0xff);
				int b = (buffer2[j] & 0xff);
				if (a != b) {
					return a - b;
				}
			}
			return length1 - length2;
		}
	}

}
