/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.revision;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 7.0
 */
public final class AddedInSourceAndTargetConflict extends Conflict {

	private final SortedSet<String> differentProperties;

	public AddedInSourceAndTargetConflict(ObjectId objectId) {
		this(objectId, Collections.emptySet());
	}
	
	public AddedInSourceAndTargetConflict(ObjectId objectId, final Set<String> differentProperties) {
		super(objectId, String.format("'%s' has been added on both source and target", objectId));
		this.differentProperties = ImmutableSortedSet.copyOf(differentProperties);
	}
	
	public SortedSet<String> getDifferentProperties() {
		return differentProperties;
	}

}
