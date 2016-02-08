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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

/**
 * @since 4.6
 */
public final class SnomedRegularReferenceSetBuilder extends SnomedReferenceSetBuilder<SnomedRegularReferenceSetBuilder, SnomedRegularRefSet> {

	protected SnomedRegularReferenceSetBuilder() {
		referencedComponentType = SnomedTerminologyComponentConstants.CONCEPT;
	}
	
	/**
	 * Specifies the component type referenced by the created SNOMED CT Reference Set. It is CONCEPT by default.
	 * 
	 * @param referencedComponentType
	 * @return
	 */
	@Override
	public SnomedRegularReferenceSetBuilder setReferencedComponentType(final String referencedComponentType) {
		return super.setReferencedComponentType(referencedComponentType);
	}
	
	@Override
	protected SnomedRegularRefSet create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
	}

}
