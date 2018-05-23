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
package com.b2international.snowowl.datastore.index;

import java.util.Collection;
import java.util.Map;

import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.google.common.collect.Multimap;

/**
 * @since 5.0
 */
public interface IndexCommitChangeSet {

	Collection<ComponentIdentifier> getNewComponents();

	Collection<ComponentIdentifier> getChangedComponents();

	Collection<ComponentIdentifier> getDeletedComponents();

	Map<String, Object> getMappings();

	Multimap<Class<?>, String> getDeletions();

	String getDescription();
	
	boolean isEmpty();

	void apply(StagingArea staging);
	
	IndexCommitChangeSet merge(IndexCommitChangeSet indexCommitChangeSet);

}
