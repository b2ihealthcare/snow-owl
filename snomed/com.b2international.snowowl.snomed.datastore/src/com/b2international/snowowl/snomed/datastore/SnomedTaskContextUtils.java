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

import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.datastore.tasks.ITaskContext;
import com.google.common.collect.Sets;

/**
 * Utility class for SNOMED&nbsp;CT aware {@link ITaskContext task context}.
 *
 */
public abstract class SnomedTaskContextUtils {

	private static final Collection<String> GA_SNOMED_CONTEXT_IDS = Collections.singleton(SnomedGeneralAuthoringTaskContext.ID);
	private static final Collection<String> REFSET_SNOMED_CONTEXT_IDS = Collections.unmodifiableCollection(Sets.newHashSet(
			SnomedRefSetAuthoringTaskContext.ID, 
			SnomedMappingRefSetAuthoringTaskContext.ID));
	
	private static final Collection<String> ALL_SNOMED_CONTEXT_IDS;
	
	static {
		
		final Set<String> $ = Sets.newHashSet(REFSET_SNOMED_CONTEXT_IDS);
		$.addAll(GA_SNOMED_CONTEXT_IDS);
		ALL_SNOMED_CONTEXT_IDS = Collections.unmodifiableCollection($);
		
	}
	
	/**
	 * Returns context ID of the GA task context for SNOMED&nbsp;CT. 
	 */
	public static Iterable<String> getGeneral() {
		return singletonList(SnomedGeneralAuthoringTaskContext.ID);
	}

	/**
	 * Returns with all the SNOMED&nbsp;CT task context IDs.
	 */
	public static Iterable<String> getSnomed() {
		return ALL_SNOMED_CONTEXT_IDS;
	}

	/**
	 * Returns with reference set authoring task context IDs.
	 */
	public static Iterable<String> getRefSet() {
		return REFSET_SNOMED_CONTEXT_IDS;
	}
	
	private SnomedTaskContextUtils() {}
	
}