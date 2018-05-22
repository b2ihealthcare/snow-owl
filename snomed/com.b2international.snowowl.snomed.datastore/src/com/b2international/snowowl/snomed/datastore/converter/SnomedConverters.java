/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.converter.ResourceConverter;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * @since 4.5
 */
public class SnomedConverters {
	
	private SnomedConverters() {}
	
	public static ResourceConverter<SnomedConstraintDocument, SnomedConstraint, SnomedConstraints> newConstraintConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedConstraintConverter(context, expand, locales);
	}
	
	public static ResourceConverter<SnomedConceptDocument, SnomedConcept, SnomedConcepts> newConceptConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedConceptConverter(context, expand, locales);
	}
	
	public static ResourceConverter<SnomedDescriptionIndexEntry, SnomedDescription, SnomedDescriptions> newDescriptionConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedDescriptionConverter(context, expand, locales);
	}
	
	public static ResourceConverter<SnomedRelationshipIndexEntry, SnomedRelationship, SnomedRelationships> newRelationshipConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedRelationshipConverter(context, expand, locales);
	}
	
	public static ResourceConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> newMemberConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedReferenceSetMemberConverter(context, expand, locales);
	}
	
	public static ResourceConverter<SnomedConceptDocument, SnomedReferenceSet, SnomedReferenceSets> newRefSetConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		return new SnomedReferenceSetConverter(context, expand, locales);
	}
	
}
