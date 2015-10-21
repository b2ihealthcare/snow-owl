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
package com.b2international.snowowl.snomed.datastore.server.events;

import java.util.Date;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.domain.SnomedReferenceSetImpl;
import com.google.common.base.Function;

/**
 * @since 4.5
 */
class SnomedReferenceSetConverter implements Function<SnomedRefSetIndexEntry, SnomedReferenceSet> {

	@Override
	public SnomedReferenceSet apply(SnomedRefSetIndexEntry entry) {
		final SnomedReferenceSetImpl refset = new SnomedReferenceSetImpl();
		refset.setId(entry.getId());
		refset.setEffectiveTime(new Date(entry.getEffectiveTimeAsLong()));
		refset.setActive(entry.isActive());
		refset.setReleased(entry.isReleased());
		refset.setModuleId(entry.getModuleId());
		refset.setReferencedComponent(CoreTerminologyBroker.getInstance().getComponentInformation(entry.getReferencedComponentType()).getName());
		refset.setType(entry.getType());
		return refset;
	}
}
