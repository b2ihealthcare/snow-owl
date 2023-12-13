/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.commons.numeral.ArabicToRomanNumeralConverter;

/**
 */
public class ArabicToRomanNumeralConverterTest {

	@Test
	public void testSimpleConversion1() {
		assertEquals("I", ArabicToRomanNumeralConverter.convert(1));
	}
	
	@Test
	public void testSimpleConversion10() {
		assertEquals("X", ArabicToRomanNumeralConverter.convert(10));
	}
	
	@Test
	public void testSimpleConversion100() {
		assertEquals("C", ArabicToRomanNumeralConverter.convert(100));
	}
	
	@Test
	public void testSimpleConversion1000() {
		assertEquals("M", ArabicToRomanNumeralConverter.convert(1000));
	}
	
	@Test
	public void testSimpleConversion2000() {
		assertEquals("MM", ArabicToRomanNumeralConverter.convert(2000));
	}
	
	@Test
	public void testSimpleConversion3999() {
		assertEquals("MMMCMXCIX", ArabicToRomanNumeralConverter.convert(3999));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testNegative() {
		ArabicToRomanNumeralConverter.convert(-1);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testZero() {
		ArabicToRomanNumeralConverter.convert(0);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testMoreThan3999() {
		ArabicToRomanNumeralConverter.convert(4000);
	}
	
}