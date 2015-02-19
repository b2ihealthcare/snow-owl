/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

public class Base64 {

	private static final byte equalSign = (byte) '=';

	static char digits[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	/**
	 * This method decodes the byte array in base 64 encoding into a char array
	 * Base 64 encoding has to be according to the specification given by the
	 * RFC 1521 (5.2).
	 * 
	 * @param data the encoded byte array
	 * @return the decoded byte array
	 */
	public static byte[] decode(byte[] data) {
		if (data.length == 0)
			return data;
		int lastRealDataIndex = data.length - 1;
		while (data[lastRealDataIndex] == equalSign)
			lastRealDataIndex--;
		// original data digit is 8 bits long, but base64 digit is 6 bits long
		int padBytes = data.length - 1 - lastRealDataIndex;
		int byteLength = data.length * 6 / 8 - padBytes;
		byte[] result = new byte[byteLength];
		// Each 4 bytes of input (encoded) we end up with 3 bytes of output
		int dataIndex = 0;
		int resultIndex = 0;
		int allBits = 0;
		// how many result chunks we can process before getting to pad bytes
		int resultChunks = (lastRealDataIndex + 1) / 4;
		for (int i = 0; i < resultChunks; i++) {
			allBits = 0;
			// Loop 4 times gathering input bits (4 * 6 = 24)
			for (int j = 0; j < 4; j++)
				allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
			// Loop 3 times generating output bits (3 * 8 = 24)
			for (int j = resultIndex + 2; j >= resultIndex; j--) {
				result[j] = (byte) (allBits & 0xff); // Bottom 8 bits
				allBits = allBits >>> 8;
			}
			resultIndex += 3; // processed 3 result bytes
		}
		// Now we do the extra bytes in case the original (non-encoded) data
		// was not multiple of 3 bytes
		switch (padBytes) {
			case 1 :
				// 1 pad byte means 3 (4-1) extra Base64 bytes of input, 18
				// bits, of which only 16 are meaningful
				// Or: 2 bytes of result data
				allBits = 0;
				// Loop 3 times gathering input bits
				for (int j = 0; j < 3; j++)
					allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
				// NOTE - The code below ends up being equivalent to allBits =
				// allBits>>>2
				// But we code it in a non-optimized way for clarity
				// The 4th, missing 6 bits are all 0
				allBits = allBits << 6;
				// The 3rd, missing 8 bits are all 0
				allBits = allBits >>> 8;
				// Loop 2 times generating output bits
				for (int j = resultIndex + 1; j >= resultIndex; j--) {
					result[j] = (byte) (allBits & 0xff); // Bottom 8
					// bits
					allBits = allBits >>> 8;
				}
				break;
			case 2 :
				// 2 pad bytes mean 2 (4-2) extra Base64 bytes of input, 12 bits
				// of data, of which only 8 are meaningful
				// Or: 1 byte of result data
				allBits = 0;
				// Loop 2 times gathering input bits
				for (int j = 0; j < 2; j++)
					allBits = (allBits << 6) | decodeDigit(data[dataIndex++]);
				// NOTE - The code below ends up being equivalent to allBits =
				// allBits>>>4
				// But we code it in a non-optimized way for clarity
				// The 3rd and 4th, missing 6 bits are all 0
				allBits = allBits << 6;
				allBits = allBits << 6;
				// The 3rd and 4th, missing 8 bits are all 0
				allBits = allBits >>> 8;
				allBits = allBits >>> 8;
				result[resultIndex] = (byte) (allBits & 0xff); // Bottom
				// 8
				// bits
				break;
		}
		return result;
	}

	/**
	 * This method converts a Base 64 digit to its numeric value.
	 * 
	 * @param data digit (character) to convert
	 * @return value for the digit
	 */
	static int decodeDigit(byte data) {
		char charData = (char) data;
		if (charData <= 'Z' && charData >= 'A')
			return charData - 'A';
		if (charData <= 'z' && charData >= 'a')
			return charData - 'a' + 26;
		if (charData <= '9' && charData >= '0')
			return charData - '0' + 52;
		switch (charData) {
			case '+' :
				return 62;
			case '/' :
				return 63;
			default :
				throw new IllegalArgumentException("Invalid char to decode: " + data); //$NON-NLS-1$
		}
	}

	/**
	 * This method encodes the byte array into a char array in base 64 according
	 * to the specification given by the RFC 1521 (5.2).
	 * 
	 * @param data the encoded char array
	 * @return the byte array that needs to be encoded
	 */
	public static byte[] encode(byte[] data) {
		int sourceChunks = data.length / 3;
		int len = ((data.length + 2) / 3) * 4;
		byte[] result = new byte[len];
		int extraBytes = data.length - (sourceChunks * 3);
		// Each 4 bytes of input (encoded) we end up with 3 bytes of output
		int dataIndex = 0;
		int resultIndex = 0;
		int allBits = 0;
		for (int i = 0; i < sourceChunks; i++) {
			allBits = 0;
			// Loop 3 times gathering input bits (3 * 8 = 24)
			for (int j = 0; j < 3; j++)
				allBits = (allBits << 8) | (data[dataIndex++] & 0xff);
			// Loop 4 times generating output bits (4 * 6 = 24)
			for (int j = resultIndex + 3; j >= resultIndex; j--) {
				result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom
				// 6
				// bits
				allBits = allBits >>> 6;
			}
			resultIndex += 4; // processed 4 result bytes
		}
		// Now we do the extra bytes in case the original (non-encoded) data
		// is not multiple of 4 bytes
		switch (extraBytes) {
			case 1 :
				allBits = data[dataIndex++]; // actual byte
				allBits = allBits << 8; // 8 bits of zeroes
				allBits = allBits << 8; // 8 bits of zeroes
				// Loop 4 times generating output bits (4 * 6 = 24)
				for (int j = resultIndex + 3; j >= resultIndex; j--) {
					result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom
					// 6
					// bits
					allBits = allBits >>> 6;
				}
				// 2 pad tags
				result[result.length - 1] = (byte) '=';
				result[result.length - 2] = (byte) '=';
				break;
			case 2 :
				allBits = data[dataIndex++]; // actual byte
				allBits = (allBits << 8) | (data[dataIndex++] & 0xff); // actual
				// byte
				allBits = allBits << 8; // 8 bits of zeroes
				// Loop 4 times generating output bits (4 * 6 = 24)
				for (int j = resultIndex + 3; j >= resultIndex; j--) {
					result[j] = (byte) digits[(allBits & 0x3f)]; // Bottom
					// 6
					// bits
					allBits = allBits >>> 6;
				}
				// 1 pad tag
				result[result.length - 1] = (byte) '=';
				break;
		}
		return result;
	}
}
