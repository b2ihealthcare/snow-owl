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
package com.b2international.snowowl.datastore.server.snomed.merge;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.datastore.cdo.IMergeConflictRule;
import com.b2international.snowowl.datastore.cdo.IMergeConflictRuleProvider;
import com.b2international.snowowl.datastore.server.snomed.merge.rules.SnomedInvalidRelationshipMergeConflictRule;
import com.b2international.snowowl.datastore.server.snomed.merge.rules.SnomedLanguageRefsetMembersMergeConflictRule;
import com.b2international.snowowl.datastore.server.snomed.merge.rules.SnomedRefsetMemberReferencingDetachedComponentRule;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 4.7
 */
public class SnomedMergeConflictRuleProvider implements IMergeConflictRuleProvider {

	private List<IMergeConflictRule> rules = newArrayList();

	public SnomedMergeConflictRuleProvider() {
		rules.add(new SnomedRefsetMemberReferencingDetachedComponentRule());
		rules.add(new SnomedLanguageRefsetMembersMergeConflictRule());
		rules.add(new SnomedInvalidRelationshipMergeConflictRule());
	}
	
	@Override
	public String getRepositoryUUID() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	@Override
	public Collection<IMergeConflictRule> getRules() {
		return rules;
	}
	
}
