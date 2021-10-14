/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithDestination;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithValue;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeWriter;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;

/**
 * @since 7.0
 */
public final class RelationshipWriter extends OntologyChangeWriter<StatementFragment> {

	public RelationshipWriter(final String classificationId, final Writer writer) {
		super(classificationId, writer);
	}

	@Override
	public void indexChange(final String conceptId, final StatementFragment fragment, final ChangeNature nature) {
		
		final RelationshipChangeDocument.Builder builder = RelationshipChangeDocument.builder()
			.nature(nature)
			.classificationId(classificationId)
			.sourceId(conceptId);
		
		if (fragment instanceof StatementFragmentWithDestination) {
			final long destinationId = ((StatementFragmentWithDestination) fragment).getDestinationId();
			builder.destinationId(Long.toString(destinationId));
		} else {
			final StatementFragmentWithValue fragmentWithValue = (StatementFragmentWithValue) fragment;
			final RelationshipValueType valueType = fragmentWithValue.getValueType();
			final String rawValue = fragmentWithValue.getRawValue();
			builder.valueType(valueType);
			builder.rawValue(rawValue);
		}
		
		switch (nature) {
			case NEW:
				builder.group(fragment.getGroup());
				builder.unionGroup(fragment.getUnionGroup());
				builder.characteristicTypeId(Concepts.INFERRED_RELATIONSHIP);
				builder.released(Boolean.FALSE);
				
				if (fragment.getStatementId() != -1L) {
					builder.relationshipId(Long.toString(fragment.getStatementId()));
				} else {
					builder.typeId(Long.toString(fragment.getTypeId()));
				}

				break;
				
			case UPDATED:
				builder.group(fragment.getGroup());
				builder.released(fragment.isReleased());
				builder.relationshipId(Long.toString(fragment.getStatementId()));
				break;
				
			case REDUNDANT:
				builder.released(fragment.isReleased());
				builder.relationshipId(Long.toString(fragment.getStatementId()));
				break;
				
			default:
				throw new IllegalStateException(String.format("Unexpected relationship change '%s' found with SCTID '%s'.", 
						nature, 
						fragment.getStatementId()));
		}
		
		indexChange(builder.build());
	}

	@Deprecated
	public boolean hasRedundantStatedChanges() {
		return false;
	}
}
