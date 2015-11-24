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

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;

/**
 * @since 4.5
 */
public class SnomedConverters {
	
	private SnomedConverters() {}
	
	public static SnomedConceptConverter newConceptConverter(BranchContext context) {
		return new SnomedConceptConverter(createMembershipLookupService(context));
	}
	
	public static SnomedDescriptionConverter newDescriptionConverter(BranchContext context) {
		return new SnomedDescriptionConverter(createMembershipLookupService(context));
	}
	
	public static SnomedRelationshipConverter newRelationshipConverter(BranchContext context) {
		return new SnomedRelationshipConverter(createMembershipLookupService(context));
	}
	
	public static ResourceConverter<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember, SnomedReferenceSetMembers> newMemberConverter(BranchContext context, List<String> expand) {
		return new SnomedReferenceSetMemberConverter(context, expand);
	}
	
	public static SnomedReferenceSetConverter newRefSetConverter(BranchContext context) {
		return newRefSetConverter(context, Collections.<String>emptyList());
	}
	
	public static SnomedReferenceSetConverter newRefSetConverter(BranchContext context, List<String> expansions) {
		return new SnomedReferenceSetConverter(context, expansions);
	}
	
	private static SnomedBranchRefSetMembershipLookupService createMembershipLookupService(BranchContext context) {
		return createMembershipLookupService(context.branch().branchPath());
	}

	private static SnomedBranchRefSetMembershipLookupService createMembershipLookupService(IBranchPath branchPath) {
		return new SnomedBranchRefSetMembershipLookupService(branchPath);
	}
	
	@Deprecated
	public static SnomedConceptConverter newConceptConverter(IBranchPath branchPath) {
		return new SnomedConceptConverter(createMembershipLookupService(branchPath));
	}
	
	@Deprecated
	public static SnomedRelationshipConverter newRelationshipConverter(IBranchPath branchPath) {
		return new SnomedRelationshipConverter(createMembershipLookupService(branchPath));
	}

}
