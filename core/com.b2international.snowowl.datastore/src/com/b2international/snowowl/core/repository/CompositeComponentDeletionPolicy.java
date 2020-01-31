/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.repository;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.function.Predicate;

import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * @since 7.4.0
 */
public class CompositeComponentDeletionPolicy implements ComponentDeletionPolicy {

	Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies = newHashMap();
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies = newHashMap();
		
		Builder() {}
		
		public Builder withPolicy(Class<?> clazz, Predicate<RevisionDocument> predicate) {
			this.deletionPolicies.put(clazz, predicate);
			return this;
		}
		
		public CompositeComponentDeletionPolicy build() {
			return new CompositeComponentDeletionPolicy(this.deletionPolicies);
		}
		
	}
	
	CompositeComponentDeletionPolicy(Map<Class<?>, Predicate<RevisionDocument>> deletionPolicies) {
		this.deletionPolicies = deletionPolicies;
	}
	
	@Override
	public boolean canDelete(RevisionDocument revision) {
		return deletionPolicies.entrySet().stream()
			.filter(entry -> entry.getKey().isInstance(revision))
			.allMatch(entry -> entry.getValue().test(revision));
	}
	
	public Map<Class<?>, Predicate<RevisionDocument>> getDeletionPolicies() {
		return deletionPolicies;
	}

}
