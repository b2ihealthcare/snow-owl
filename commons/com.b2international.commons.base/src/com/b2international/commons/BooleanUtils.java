/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons;

/**
 * Utility class for booleans
 * 
 */
public final class BooleanUtils {

	public static final String YES = "yes";
	public static final String NO = "no";
	
	/**
	 * Converts a primitive integer to boolean by assuming 0 is false and everything else (1) is true.
	 * 
	 * @param number
	 * @return <code>true</code> or <code>false</code> based on the input
	 */
	public static boolean valueOf(int number) {
		return 0 == number ? false : true;
	}

	/**
	 * Converts an {@link Integer} object to {@link Boolean} by assuming 0 is false and everything else (1) is true. If the parameter is
	 * <code>null</code> then it returns <code>null</code>.
	 * 
	 * @param value
	 * @return <code>null</code> if the parameter is <code>null</code>, <code>true</code> or <code>false</code> otherwise.
	 */
	public static Boolean valueOf(Integer value) {
		if (null == value) {
			return null;
		}
		return 0 == value.intValue() ? Boolean.FALSE : Boolean.TRUE;
	}

	/**
	 * Converts a primitive boolean to a primitive integer by assuming 0 is false and everything else (1) is true.
	 * 
	 * @param bool
	 * @return 0 if the parameter is false, 1 otherwise.
	 */
	public static int toInteger(boolean bool) {
		return bool ? 1 : 0;
	}

	/**
	 * Converts a {@link Boolean} object to an {@link Integer} by assuming 0 is false and everything else (1) is true. If the parameter is
	 * <code>null</code> then it returns <code>null</code>.
	 * 
	 * @param bool
	 * @return <code>null</code> if the parameter is <code>null</code>, 0 if false, 1 if true.
	 */
	public static Integer toInteger(Boolean bool) {
		if (null == bool) {
			return null;
		}
		return bool.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
	}
	
	/**
	 * Converts a {@link String} object to {@link Boolean} by assuming the following mappings:
	 * <p>
	 * '0' -> false, 'no' -> false, '1' -> true, 'yes' -> true
	 * </p>
	 * Equality is checked in a case insensitive manner. If the parameter is <code>null</code> or something else than ['0', '1', 'yes', 'no'] then it
	 * returns <code>null</code>.
	 * 
	 * @param value
	 * @return <code>null</code> if the parameter is <code>null</code> or the value is not in '0', '1', 'yes', 'no', <code>true</code> or
	 *         <code>false</code> otherwise.
	 */
	public static Boolean valueOf(String value) {
		
		if (null == value) {
			return null;
		}
		
		if ("1".equals(value) || YES.equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		} else if ("0".equals(value) || NO.equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		
		return null;
	}
	
	/**
	 * Converts a {@link Boolean} object to {@link String} by assuming '0' is false and '1' is true. If the parameter is
	 * <code>null</code> then it returns <code>null</code>.
	 * 
	 * @param bool
	 * @return <code>null</code> if the parameter is <code>null</code>, '0' if false, '1' if true.
	 */
	public static String toString(Boolean bool) {
		if (null == bool) {
			return null;
		}
		return toString(bool.booleanValue());
	}
	
	/**
	 * Converts a primitive boolean to {@link String} by assuming '0' is false and '1' is true.
	 * 
	 * @param bool
	 * @return '0' if the parameter is false, '1' otherwise.
	 */
	public static String toString(boolean bool) {
		return bool ? "1" : "0";
	}
	
	/**
	 * Converts a {@link Boolean} object to {@link String} by assuming 'no' is false and 'yes' is true. If the parameter is <code>null</code> then it
	 * returns <code>null</code>.
	 * 
	 * @param bool
	 * @return <code>null</code> if the parameter is <code>null</code>, 'no' if false, 'yes' if true.
	 */
	public static String toYesOrNoString(Boolean bool) {
		if (null == bool) {
			return null;
		}
		return toYesOrNoString(bool.booleanValue());
	}
	
	/**
	 * Converts a primitive boolean to {@link String} by assuming 'no' is false and 'yes' is true.
	 * 
	 * @param bool
	 * @return 'no' if the parameter is false, 'yes' otherwise.
	 */
	public static String toYesOrNoString(boolean bool) {
		return bool ? YES : NO;
	}
}
