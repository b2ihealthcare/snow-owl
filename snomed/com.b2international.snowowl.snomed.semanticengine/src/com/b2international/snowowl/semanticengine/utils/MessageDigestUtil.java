/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MessageDigestUtil {

	private static final String CHARSET_NAME = "UTF-8";
	private static final String DIGEST_ALGORITHM = "SHA-1";
	
	private static MessageDigest messageDigest;
	
	private static synchronized MessageDigest getMessageDigest() {
		
		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("Couldn't get digest algorithm instance.", e);
			}
		}
		
		return messageDigest;
	}
	
	public static synchronized byte[] calculateHash(String text) {
		
		try {
			return getMessageDigest().digest(text.getBytes(CHARSET_NAME));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Couldn't convert input string to byte array.", e);
		}
	}
	
	private MessageDigestUtil() {
		// Prevent instantiation
	}
}