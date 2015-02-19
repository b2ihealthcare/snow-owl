/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Customized {@link AdapterImpl adapter} wrapping a bunch of SNOMED&nbsp;CT concept IDs.
 * <br>This adapter should be used for hierarchy building for {@link CDOState#NEW *NOT* persisted} SNOMED&nbsp;CT concepts.
 */
public final class ConceptParentAdapter extends AdapterImpl {

	private final Set<String> parentConceptIds;

	/**
	 * Creates a new instance of this adapter.
	 * @param parentConceptIds the parent concept.
	 */
	public ConceptParentAdapter(final Iterable<String> parentConceptIds) {
		this.parentConceptIds = Sets.newHashSet(parentConceptIds);
	}

	/**
	 * Creates a new instance of this adapter.
	 * @param parentConceptIds the parent concept.
	 */
	public ConceptParentAdapter(final String... parentConceptIds) {
		this(CompareUtils.isEmpty(parentConceptIds) ? Collections.<String>emptySet() : Sets.newHashSet(parentConceptIds));
	}
	
	/**
	 * Returns with a copy of the parent concept IDs.
	 * @return the copy of the parent concept IDs.
	 */
	public Set<String> getParentConceptIds() {
		return ImmutableSet.<String>copyOf(parentConceptIds);
	}
	
	/**
	 * Returns with the first found parent ID extracted from the {@link ConceptParentAdapter parent adapter} if any.
	 * @param object the CDO object.
	 * @return the first parent ID or a missing one.
	 */
	public static Optional<String> getFirstParentId(final CDOObject object) {
		
		Preconditions.checkNotNull(object, "Object argument cannot be null.");
		
		for (final Adapter adapter : object.eAdapters()) {
			
			if (adapter instanceof ConceptParentAdapter) {
				
				final Set<String> ids = ((ConceptParentAdapter) adapter).getParentConceptIds();
				if (!CompareUtils.isEmpty(ids)) {
					return Optional.<String>of(ids.iterator().next());
				}
				
			}
			
		}
		
		return Optional.<String>absent();
	}
	
}