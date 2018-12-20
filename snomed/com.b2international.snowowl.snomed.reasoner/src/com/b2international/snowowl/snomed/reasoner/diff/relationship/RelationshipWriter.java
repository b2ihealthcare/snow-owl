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
package com.b2international.snowowl.snomed.reasoner.diff.relationship;

import com.b2international.index.Writer;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeWriter;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;

/**
 * @since 7.0
 */
public final class RelationshipWriter extends OntologyChangeWriter<StatementFragment> {

	private boolean hasRedundantStatedChanges;
	
	public RelationshipWriter(final String classificationId, final Writer writer) {
		super(classificationId, writer);
	}

	@Override
	protected void indexChange(final String conceptId, final StatementFragment fragment, final ChangeNature nature) {
		final RelationshipChangeDocument.Builder builder = RelationshipChangeDocument.builder()
				.nature(nature)
				.classificationId(classificationId)
				.sourceId(conceptId)
				.typeId(Long.toString(fragment.getTypeId()))
				.destinationId(Long.toString(fragment.getDestinationId()))
				.group(fragment.getGroup())
				.unionGroup(fragment.getUnionGroup());

		if (fragment.getStatementId() != -1L) {
			builder.relationshipId(Long.toString(fragment.getStatementId()));
		}
		
		if (ChangeNature.REDUNDANT.equals(nature) && fragment.hasStatedPair()) {
			hasRedundantStatedChanges = true;
		}

		indexChange(builder.build());
	}

	public boolean hasRedundantStatedChanges() {
		return hasRedundantStatedChanges;
	}
}
