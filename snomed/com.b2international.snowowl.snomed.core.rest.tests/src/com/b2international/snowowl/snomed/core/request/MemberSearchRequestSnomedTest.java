/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.FluentIterable;

/**
 * @since 7.7
 */
public class MemberSearchRequestSnomedTest {

	private static final String CODESYSTEM = "SNOMEDCT/LATEST";
	
	private static final String SYNONYM = "Synonym (core metadata concept)";
	private static final String FSN = "Fully specified name (core metadata concept)";
	private static final String DEFINITION = "Definition (core metadata concept)";
	
	@Test
	public void filterByRefset() throws Exception {
		
		SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByRefSet(Concepts.REFSET_DESCRIPTION_TYPE)
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();
		
		SetMembers setMembers = CodeSystemRequests.prepareSearchMembers()
			.all()
			.filterBySet(Concepts.REFSET_DESCRIPTION_TYPE)		
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();
				
		assertThat(setMembers.getTotal()).isEqualTo(members.getTotal());
		assertThat(setMembers.stream().allMatch(m -> SNOMED_SHORT_NAME.equals(m.getReferencedComponentURI().codeSystem())));
		
		Set<String> setMemberSourceCodes = FluentIterable.from(setMembers).transform(m -> m.getReferencedComponentURI().identifier()).toSet();
		Set<String> setMemberSourceTerms = FluentIterable.from(setMembers).transform(m -> m.getReferencedComponentURI().identifier()).toSet();
		
		assertThat(setMemberSourceCodes.contains(Concepts.TEXT_DEFINITION));
		assertThat(setMemberSourceCodes.contains(Concepts.FULLY_SPECIFIED_NAME));
		assertThat(setMemberSourceCodes.contains(Concepts.SYNONYM));
		
		assertThat(setMemberSourceTerms.contains(DEFINITION));
		assertThat(setMemberSourceTerms.contains(FSN));
		assertThat(setMemberSourceTerms.contains(SYNONYM));
	}
	
}
