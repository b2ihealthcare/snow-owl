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

/**
 * Class to use to generate decentralized random UUIDs. 
 * 
 * @since 7.3
 */
public class IDs {

	/**
	 * Generates a time-based UUID (similar to Flake IDs), which is preferred when generating an ID to be indexed into a Lucene index as primary key. 
	 * @return
	 * @see UUIDs
	 */
	public static final String base64UUID() {
		return UUIDs.base64UUID();
	}
	
}
