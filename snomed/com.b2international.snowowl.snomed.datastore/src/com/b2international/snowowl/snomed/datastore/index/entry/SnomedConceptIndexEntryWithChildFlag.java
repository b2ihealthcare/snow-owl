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
package com.b2international.snowowl.snomed.datastore.index.entry;

import com.b2international.snowowl.core.api.IComponentWithChildFlag;

/**
 * Adds a boolean to {@link SnomedConceptIndexEntry} to indicate whether the concept has children.
 */
public class SnomedConceptIndexEntryWithChildFlag extends SnomedConceptIndexEntry implements IComponentWithChildFlag<String> {

	private static final long serialVersionUID = -3327828639334867594L;

	private final boolean hasChildren;

	public SnomedConceptIndexEntryWithChildFlag(final SnomedConceptIndexEntry conceptIndexEntry, final boolean hasChildren) {

		super(conceptIndexEntry.getId(), 
				conceptIndexEntry.getIconId(), 
				conceptIndexEntry.getScore(), 
				conceptIndexEntry.getStorageKey(),
				conceptIndexEntry.getModuleId(),
				conceptIndexEntry.isReleased(),
				conceptIndexEntry.isActive(),
				conceptIndexEntry.getEffectiveTimeAsLong(),
				conceptIndexEntry.isPrimitive(),
				conceptIndexEntry.isExhaustive());

		this.hasChildren = hasChildren;
	}

	/**
	 * @return {@code true} if the component has children, {@code false} otherwise
	 */
	public boolean hasChildren() {
		return hasChildren;
	}
}
