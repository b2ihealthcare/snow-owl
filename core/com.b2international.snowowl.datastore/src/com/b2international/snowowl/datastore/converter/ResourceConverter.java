/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.converter;

import java.util.Collection;

import com.b2international.snowowl.core.domain.CollectionResource;

/**
 * @since 4.5
 */
public interface ResourceConverter<T, R, CR extends CollectionResource<R>> {

	/**
	 * Convert a single internal index based entity to a resource based representation.
	 * 
	 * @param component
	 * @return
	 */
	R convert(T component);

	/**
	 * Convert multiple internal index based entities to resource based representations.
	 * 
	 * @param components
	 * @param scrollId
	 * @param searchAfter
	 * @param limit
	 * @param total
	 * @return
	 */
	CR convert(Collection<T> components, String scrollId, String searchAfter, int limit, int total);
}
