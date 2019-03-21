/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import java.io.IOException;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * @since 4.3
 */
public final class RefSetMemberChangeProcessor extends ChangeSetProcessorBase {

	private final SnomedOWLExpressionConverter expressionConverter;

	public RefSetMemberChangeProcessor(SnomedOWLExpressionConverter expressionConverter) {
		super("reference set member changes");
		this.expressionConverter = expressionConverter;
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		deleteRevisions(SnomedRefSetMemberIndexEntry.class, commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER));
		
		for (SnomedRefSetMember member : commitChangeSet.getNewComponents(SnomedRefSetMember.class)) {
			final Builder doc = SnomedRefSetMemberIndexEntry.builder(member);
			convertOwlExpression(member, doc);
			indexNewRevision(member.cdoID(), doc.build());
		}
		
		for (SnomedRefSetMember member : commitChangeSet.getDirtyComponents(SnomedRefSetMember.class)) {
			final Builder doc = SnomedRefSetMemberIndexEntry.builder(member);
			convertOwlExpression(member, doc);
			indexChangedRevision(member.cdoID(), doc.build());
		}
	}

	private void convertOwlExpression(SnomedRefSetMember member, final Builder doc) {
		if (member instanceof SnomedOWLExpressionRefSetMember) {
			SnomedOWLExpressionConverterResult result = expressionConverter.toSnomedOWLRelationships(member.getReferencedComponentId(), ((SnomedOWLExpressionRefSetMember) member).getOwlExpression());
			doc
				.classAxiomRelationships(result.getClassAxiomRelationships())
				.gciAxiomRelationships(result.getGciAxiomRelationships());
				
		}
	}
	
}
