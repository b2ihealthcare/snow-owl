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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionConverter;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRelationshipConverter;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;

/**
 * @since 4.5
 */
public class SnomedConverters {
	
	private SnomedConverters() {}
	
	public static SnomedConceptConverter newConceptConverter(BranchContext context) {
		return new SnomedConceptConverter(createMemberShipLookupService(context));
	}

	public static SnomedDescriptionConverter newDescriptionConverter(TransactionContext context) {
		return new SnomedDescriptionConverter(createMemberShipLookupService(context));
	}
	
	public static SnomedRelationshipConverter newRelationshipConverter(TransactionContext context) {
		return new SnomedRelationshipConverter(createMemberShipLookupService(context));
	}
	
	private static SnomedBranchRefSetMembershipLookupService createMemberShipLookupService(BranchContext context) {
		return new SnomedBranchRefSetMembershipLookupService(context.branch().branchPath());
	}

}
