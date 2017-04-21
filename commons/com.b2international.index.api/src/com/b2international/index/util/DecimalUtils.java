/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.index.util;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.hadoop.hbase.util.Order;
import org.apache.hadoop.hbase.util.OrderedBytes;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;

import com.b2international.commons.encoding.Base64;
import com.b2international.index.IndexException;

/**
 * @since 5.0
 */
public final class DecimalUtils {

	public static final int PRECISION = 1 + 18 + 8; // according to OrderedBytes 
	
	private DecimalUtils() {
	}
	
	public static final class DecimalSerializer {
		
	}
	
	// Convert to keyword-safe representation that preserves ordering
	public static String encode(BigDecimal val) {
		final SimplePositionedMutableByteRange dst = new SimplePositionedMutableByteRange(PRECISION);
		final int writtenBytes = OrderedBytes.encodeNumeric(dst, val, Order.ASCENDING);
		
		try {
			return Base64.encodeBytes(dst.getBytes(), 0, writtenBytes, Base64.ORDERED);
		} catch (IOException e) {
			throw new IndexException("Couldn't convert ordered binary representation of BigDecimal value to Base64.", e);
		}
	}

	public static BigDecimal decode(String val) {
		final byte[] rawBytes;
		try {
			// Convert from keyword-safe representation to raw bytes
			rawBytes = Base64.decode(val, Base64.ORDERED);
		} catch (IOException e) {
			throw new IndexException("Couldn't convert Base64 representation of BigDecimal value to raw bytes.", e);
		}

		SimplePositionedMutableByteRange src = new SimplePositionedMutableByteRange(rawBytes);
		return OrderedBytes.decodeNumericAsBigDecimal(src);
	}

	public static BigDecimal decode(byte[] bytes, int offset, int length) {
		final byte[] rawBytes;
		try {
			// Convert from keyword-safe representation to raw bytes
			rawBytes = Base64.decode(bytes, offset, length, Base64.ORDERED);
		} catch (IOException e) {
			throw new IndexException("Couldn't convert Base64 representation of BigDecimal value to raw bytes.", e);
		}

		SimplePositionedMutableByteRange src = new SimplePositionedMutableByteRange(rawBytes);
		return OrderedBytes.decodeNumericAsBigDecimal(src);
	}
	
}
