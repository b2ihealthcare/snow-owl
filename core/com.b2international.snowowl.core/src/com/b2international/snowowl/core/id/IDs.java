/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.elasticsearch.common.UUIDs;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * Class to use to generate decentralized random UUIDs.
 * 
 * @since 7.3
 */
public class IDs {

	/**
	 * Generates a time-based UUID (similar to Flake IDs), which is preferred when generating an ID to be indexed into a Lucene index as primary key.
	 * 
	 * @return
	 * @see UUIDs#base64UUID()
	 */
	public static final String base64UUID() {
		return UUIDs.base64UUID();
	}
	
	/**
	 * @return a Base64 encoded version of a Version 4.0 compatible UUID as defined here: http://www.ietf.org/rfc/rfc4122.txt, using a private SecureRandom instance
	 * @see UUIDs#randomBase64UUID()
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

}
