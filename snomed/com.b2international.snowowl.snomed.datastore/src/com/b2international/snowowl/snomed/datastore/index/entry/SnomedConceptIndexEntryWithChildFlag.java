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

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Adds a boolean to {@link SnomedConceptDocument} to indicate whether the concept has children.
 */
public class SnomedConceptIndexEntryWithChildFlag extends SnomedConceptDocument implements IComponentWithChildFlag<String> {

	private static final long serialVersionUID = -3327828639334867594L;

	private final boolean hasChildren;

	public SnomedConceptIndexEntryWithChildFlag(final SnomedConceptDocument conceptIndexEntry, final boolean hasChildren) {

		super(conceptIndexEntry.getId(), 
				conceptIndexEntry.getLabel(),
				conceptIndexEntry.getIconId(), 
				conceptIndexEntry.getModuleId(),
				conceptIndexEntry.isReleased(),
				conceptIndexEntry.isActive(),
				conceptIndexEntry.getEffectiveTime(),
				conceptIndexEntry.getNamespace(),
				conceptIndexEntry.isPrimitive(),
				conceptIndexEntry.isExhaustive(),
				null, 
				CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, 
				CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, 
				CDOUtils.NO_STORAGE_KEY, 
				false);

		this.hasChildren = hasChildren;
	}

	/**
	 * @return {@code true} if the component has children, {@code false} otherwise
	 */
	public boolean hasChildren() {
		return hasChildren;
	}
	
	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("hasChildren", hasChildren);
	}
	
}
