/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 7.0
 */
@Terminology(
	id = SnomedTerminologyComponentConstants.TERMINOLOGY_ID,
	repositoryId = SnomedDatastoreActivator.REPOSITORY_UUID,
	name = SnomedTerminologyComponentConstants.SNOMED_NAME,
	icon = "icons/terminology_icon.png",
	supportsEffectiveTime = true,
	primaryComponentId = SnomedTerminologyComponentConstants.CONCEPT,
	terminologyComponents = {
		SnomedConcept.class,
		SnomedDescription.class,
		SnomedRelationship.class,
		SnomedReferenceSet.class,
		SnomedReferenceSetMember.class,
		SnomedConstraint.class
	}
)
public final class SnomedPlugin {
	
	
	
}
