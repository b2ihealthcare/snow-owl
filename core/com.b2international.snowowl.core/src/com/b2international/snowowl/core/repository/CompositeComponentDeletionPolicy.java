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
import static java.util.Collections.singletonMap;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @since 7.4.0
 */
public class CompositeComponentDeletionPolicy implements ComponentDeletionPolicy {

	private Map<Class<?>, ComponentDeletionPolicy> deletionPolicies;
	
	public static CompositeComponentDeletionPolicy of(Class<?> clazz, ComponentDeletionPolicy policy) {
		return new CompositeComponentDeletionPolicy(singletonMap(clazz, policy));
	}
	
	public static CompositeComponentDeletionPolicy of(Map<Class<?>,ComponentDeletionPolicy> policies) {
		return new CompositeComponentDeletionPolicy(policies);
	}
	
	CompositeComponentDeletionPolicy(Map<Class<?>, ComponentDeletionPolicy> policies) {
		this.deletionPolicies = policies;
	}
	
	public CompositeComponentDeletionPolicy mergeWith(final CompositeComponentDeletionPolicy other) {
		if (other != null) {
			Map<Class<?>, ComponentDeletionPolicy> mergedPolicies = newHashMap(deletionPolicies);
			other.getDeletionPolicies().forEach( (clazz, policy) -> mergedPolicies.putIfAbsent(clazz, policy));
			this.deletionPolicies = ImmutableMap.copyOf(mergedPolicies);
		}
		return this;
	}
	
	@Override
	public boolean canDelete(final RevisionDocument revision) {
		
		if (deletionPolicies.containsKey(revision.getClass())) {
			return deletionPolicies.get(revision.getClass()).canDelete(revision);
		}
		
		return deletionPolicies.entrySet().stream()
				.filter(entry -> entry.getKey().isInstance(revision))
				.allMatch(entry -> entry.getValue().canDelete(revision));
	}
	
	public Map<Class<?>, ComponentDeletionPolicy> getDeletionPolicies() {
		return deletionPolicies;
	}
	
}
