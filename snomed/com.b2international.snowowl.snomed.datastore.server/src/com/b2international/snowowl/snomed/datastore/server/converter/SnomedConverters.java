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
package com.b2international.snowowl.snomed.datastore.server.converter;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;

/**
 * @since 4.5
 */
public class SnomedConverters {
	
	private SnomedConverters() {}
	
	public static ResourceConverter<SnomedConceptIndexEntry, ISnomedConcept, SnomedConcepts> newConceptConverter(BranchContext context, List<String> expand, List<ExtendedLocale> locales) {
		return new SnomedConceptConverter(context, expand, locales, createMembershipLookupService(context));
	}
	
	public static ResourceConverter<SnomedDescriptionIndexEntry, ISnomedDescription, SnomedDescriptions> newDescriptionConverter(BranchContext context, List<String> expand, List<ExtendedLocale> locales) {
		return new SnomedDescriptionConverter(context, expand, locales, createMembershipLookupService(context));
	}
	
	public static ResourceConverter<SnomedRelationshipIndexEntry, ISnomedRelationship, SnomedRelationships> newRelationshipConverter(BranchContext context, List<String> expand, List<ExtendedLocale> locales) {
		return new SnomedRelationshipConverter(context, expand, locales, createMembershipLookupService(context));
	}
	
	public static ResourceConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> newMemberConverter(BranchContext context, List<String> expand, List<ExtendedLocale> locales) {
		return new SnomedReferenceSetMemberConverter(context, expand, locales, createMembershipLookupService(context));
	}
	
	public static ResourceConverter<SnomedRefSetIndexEntry, SnomedReferenceSet, SnomedReferenceSets> newRefSetConverter(BranchContext context, List<String> expand, List<ExtendedLocale> locales) {
		return new SnomedReferenceSetConverter(context, expand, locales, createMembershipLookupService(context));
	}
	
	private static SnomedBranchRefSetMembershipLookupService createMembershipLookupService(BranchContext context) {
		return createMembershipLookupService(context.branch().branchPath());
	}

	private static SnomedBranchRefSetMembershipLookupService createMembershipLookupService(IBranchPath branchPath) {
		return new SnomedBranchRefSetMembershipLookupService(branchPath);
	}
}
