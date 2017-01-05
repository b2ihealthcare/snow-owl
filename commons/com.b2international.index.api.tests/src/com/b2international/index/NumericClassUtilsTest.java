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
package com.b2international.index;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static com.b2international.index.util.NumericClassUtils.*;

import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteList;
import com.b2international.collections.bytes.ByteSet;
import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.floats.FloatList;
import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntList;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;

/**
 * @since 5.4
 */
public class NumericClassUtilsTest {

	static class Fields {
		List<String> stringList;
		Set<String> stringSet;
		Collection<String> stringCollection;
		
		FloatList floatList;
		FloatCollection floatCollection;
		
		LongSet longSet;
		LongList longList;
		LongCollection longCollection;
		
		IntSet intSet;
		IntList intList;
		IntCollection intCollection;
		
		ByteSet byteSet;
		ByteList byteList;
		ByteCollection byteCollection;
		
		String stringScalar;
		BigDecimal bigDecimalScalar;
		
		float floatScalar;
		Float floatWrapper;
		
		long longScalar;
		Long longWrapper;
		
		int intScalar;
		Integer intWrapper;
		
		short shortScalar;
		Short shortWrapper;
	}

	private Field getField(String name) throws NoSuchFieldException {
		return Fields.class.getDeclaredField(name);
	}
	
	private Class<?> getType(String name) throws NoSuchFieldException {
		return getField(name).getType();
	}

	@Test
	public void testUnwrapCollectionType() throws Exception {
		assertEquals(String.class, unwrapCollectionType(getField("stringList")));
		assertEquals(String.class, unwrapCollectionType(getField("stringSet")));
		assertEquals(String.class, unwrapCollectionType(getField("stringCollection")));
		
		assertEquals(float.class, unwrapCollectionType(getField("floatList")));
		assertEquals(float.class, unwrapCollectionType(getField("floatCollection")));
		
		assertEquals(long.class, unwrapCollectionType(getField("longList")));
		assertEquals(long.class, unwrapCollectionType(getField("longSet")));
		assertEquals(long.class, unwrapCollectionType(getField("longCollection")));
		
		assertEquals(int.class, unwrapCollectionType(getField("intList")));
		assertEquals(int.class, unwrapCollectionType(getField("intSet")));
		assertEquals(int.class, unwrapCollectionType(getField("intCollection")));
		
		assertEquals(byte.class, unwrapCollectionType(getField("byteList")));
		assertEquals(byte.class, unwrapCollectionType(getField("byteSet")));
		assertEquals(byte.class, unwrapCollectionType(getField("byteCollection")));
		
		assertEquals(String.class, unwrapCollectionType(getField("stringScalar")));
		assertEquals(BigDecimal.class, unwrapCollectionType(getField("bigDecimalScalar")));
		assertEquals(float.class, unwrapCollectionType(getField("floatScalar")));
		assertEquals(Float.class, unwrapCollectionType(getField("floatWrapper")));
		assertEquals(long.class, unwrapCollectionType(getField("longScalar")));
		assertEquals(Long.class, unwrapCollectionType(getField("longWrapper")));
		assertEquals(int.class, unwrapCollectionType(getField("intScalar")));
		assertEquals(Integer.class, unwrapCollectionType(getField("intWrapper")));
		assertEquals(short.class, unwrapCollectionType(getField("shortScalar")));
		assertEquals(Short.class, unwrapCollectionType(getField("shortWrapper")));
	}

	@Test
	public void testIsCollection() throws Exception {
		assertEquals(true, isCollection(getField("stringList")));
		assertEquals(true, isCollection(getField("stringSet")));
		assertEquals(true, isCollection(getField("stringCollection")));
		
		assertEquals(true, isCollection(getField("floatList")));
		assertEquals(true, isCollection(getField("floatCollection")));

		assertEquals(true, isCollection(getField("longList")));
		assertEquals(true, isCollection(getField("longSet")));
		assertEquals(true, isCollection(getField("longCollection")));
		
		assertEquals(true, isCollection(getField("intList")));
		assertEquals(true, isCollection(getField("intSet")));
		assertEquals(true, isCollection(getField("intCollection")));
		
		assertEquals(true, isCollection(getField("byteList")));
		assertEquals(true, isCollection(getField("byteSet")));
		assertEquals(true, isCollection(getField("byteCollection")));
		
		assertEquals(false, isCollection(getField("stringScalar")));
		assertEquals(false, isCollection(getField("bigDecimalScalar")));
		assertEquals(false, isCollection(getField("floatScalar")));
		assertEquals(false, isCollection(getField("floatWrapper")));
		assertEquals(false, isCollection(getField("longScalar")));
		assertEquals(false, isCollection(getField("longWrapper")));
		assertEquals(false, isCollection(getField("intScalar")));
		assertEquals(false, isCollection(getField("intWrapper")));
		assertEquals(false, isCollection(getField("shortScalar")));
		assertEquals(false, isCollection(getField("shortWrapper")));
	}
	
	@Test
	public void testIsBigDecimal() throws Exception {
		assertEquals(true, isBigDecimal(getType("bigDecimalScalar")));
		assertEquals(false, isBigDecimal(getType("stringScalar")));
	}
	
	@Test
	public void testIsFloat() throws Exception {
		assertEquals(true, isFloat(getType("floatScalar")));
		assertEquals(true, isFloat(getType("floatWrapper")));
		assertEquals(false, isFloat(getType("stringScalar")));
	}
	
	@Test
	public void testIsLong() throws Exception {
		assertEquals(true, isLong(getType("longScalar")));
		assertEquals(true, isLong(getType("longWrapper")));
		assertEquals(false, isLong(getType("stringScalar")));
	}
	
	@Test
	public void testIsInt() throws Exception {
		assertEquals(true, isInt(getType("intScalar")));
		assertEquals(true, isInt(getType("intWrapper")));
		assertEquals(false, isInt(getType("stringScalar")));
	}
	
	@Test
	public void testIsShort() throws Exception {
		assertEquals(true, isShort(getType("shortScalar")));
		assertEquals(true, isShort(getType("shortWrapper")));
		assertEquals(false, isShort(getType("stringScalar")));
	}
}
