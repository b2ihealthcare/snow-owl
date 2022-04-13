/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.id;

import java.util.Arrays;
import java.util.UUID;

import org.elasticsearch.common.UUIDs;

import com.b2international.commons.exceptions.BadRequestException;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import com.github.f4b6a3.uuid.codec.base.BaseN;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * Class to use to generate decentralized random UUIDs.
 * 
 * @since 7.3
 */
public class IDs {

	public static final BaseN BASE64 = new BaseN("A-Za-z0-9-_");
	public static final BaseN BASE62 = new BaseN("A-Za-z0-9");
	
	/**
	 * Generates a time-based UUID (similar to Flake IDs), which is preferred when generating an ID to be indexed into a Lucene index as primary key. This methods uses Base 62 encoding of UUIDs to omit the usage of non-alpha/numeric characters entirely.
	 * 
	 * @return
	 * @see Base62Codec
	 */
	public static final String base62UUID() {
		return Base62Codec.INSTANCE.encode(UUID.randomUUID());
	}
	
	/**
	 * Generates a time-based UUID (similar to Flake IDs), which is preferred when generating an ID to be indexed into a Lucene index as primary key.
	 * 
	 * @return
	 * @see UUIDs#base64UUID()
	 * @deprecated - use {@link #base62UUID()} instead
	 */
	public static final String base64UUID() {
		return UUIDs.base64UUID();
	}
	
	/**
	 * @return a Base64 encoded version of a Version 4.0 compatible UUID as defined here: http://www.ietf.org/rfc/rfc4122.txt, using a private SecureRandom instance
	 * @see UUIDs#randomBase64UUID()
	 * @deprecated - use {@link #base62UUID()} instead
	 */
	public static final String randomBase64UUID() {
		return UUIDs.randomBase64UUID();
	}

	/**
	 * Create an SHA-1 hash digest from the given value and returns the first N characters. Similar to how Git creates a unique shortened SHA-1
	 * commit for Git commits, this can be useful for ID generation in certain scenarios.
	 * 
	 * @param value
	 * @return
	 */
	public static String shortSha1(String value, int length) {
		return sha1(value).substring(0, length);
	}
	
	/**
	 * Create an SHA-1 hash digest from the given value and returns it.
	 * 
	 * @param value
	 * @return
	 */
	public static String sha1(String value) {
		return Hashing.sha1().hashString(value, Charsets.UTF_8).toString();
	}
	
	public static void checkBase62(String value, String property) {
		checkBaseN(value, BASE62, value);
	}
	
	public static void checkBase64(String value, String property) {
		checkBaseN(value, BASE64, value);
	}
	
	public static void checkBaseN(String value, BaseN baseN, String property) {
		if (baseN.isValid(value)) {
			throw new BadRequestException("%s'%s' uses an illegal character. Allowed characters are '%s'.", property == null ? "" : property.concat(" "), Arrays.toString(BASE62.getAlphabet().array()));
		}
	}
	
}
