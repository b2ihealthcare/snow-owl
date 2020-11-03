/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import org.eclipse.xtext.serializer.ISerializer;

import com.b2international.snowowl.snomed.ql.ql.Query;

/**
 * @since 6.12
 */
public class DefaultSnomedQuerySerializer implements SnomedQuerySerializer {

	private final ISerializer qlSerializer;

	public DefaultSnomedQuerySerializer(ISerializer eclSerializer) {
		this.qlSerializer = eclSerializer;
	}
	
	@Override
	public String serialize(Query query) {
		synchronized (qlSerializer) {
			return qlSerializer.serialize(query);
		}
	}
	
}
