/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.google.common.collect.Multimap;

/**
 * @since 5.0
 */
public class DelegatingIndexCommitChangeSet implements IndexCommitChangeSet {

	private final IndexCommitChangeSet delegate;

	public DelegatingIndexCommitChangeSet(IndexCommitChangeSet delegate) {
		this.delegate = delegate;
	}

	@Override
	public Collection<ComponentIdentifier> getNewComponents() {
		return delegate.getNewComponents();
	}

	@Override
	public Collection<ComponentIdentifier> getChangedComponents() {
		return delegate.getChangedComponents();
	}

	@Override
	public Collection<ComponentIdentifier> getDeletedComponents() {
		return delegate.getDeletedComponents();
	}

	@Override
	public Map<String, Object> getRawMappings() {
		return delegate.getRawMappings();
	}

	@Override
	public Multimap<Class<?>, String> getRawDeletions() {
		return delegate.getRawDeletions();
	}

	@Override
	public Map<Long, Revision> getRevisionMappings() {
		return delegate.getRevisionMappings();
	}

	@Override
	public Multimap<Class<? extends Revision>, Long> getRevisionDeletions() {
		return delegate.getRevisionDeletions();
	}
	
	@Override
	public String getDescription() {
		return delegate.getDescription();
	}
	
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}
	
	@Override
	public void apply(RevisionWriter writer) throws IOException {
		delegate.apply(writer);
	}
	
	@Override
	public IndexCommitChangeSet merge(IndexCommitChangeSet indexCommitChangeSet) {
		return delegate.merge(indexCommitChangeSet);
	}

}
