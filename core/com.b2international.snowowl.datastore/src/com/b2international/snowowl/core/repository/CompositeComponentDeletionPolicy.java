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
package com.b2international.snowowl.core.repository;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.function.Predicate;

import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * @since 7.4.0
 */
public class CompositeComponentDeletionPolicy implements ComponentDeletionPolicy {

	Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies;

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies = newHashMap();

		Builder() {}

		public Builder withPolicy(final Class<?> clazz, final Predicate<RevisionDocument> predicate) {
			this.deletionPolicies.put(clazz, predicate);
			return this;
		}

		public CompositeComponentDeletionPolicy build() {
			return new CompositeComponentDeletionPolicy(this.deletionPolicies);
		}

	}

	CompositeComponentDeletionPolicy(final Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies) {
		this.deletionPolicies = deletionPolicies;
	}

	@Override
	public boolean canDelete(final RevisionDocument revision) {
		return deletionPolicies.entrySet().stream()
				.filter(entry -> entry.getKey().isInstance(revision))
				.allMatch(entry -> entry.getValue().test(revision));
	}

	public Map<Class<?>, Predicate<RevisionDocument>> getDeletionPolicies() {
		return deletionPolicies;
	}

}
