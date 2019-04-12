/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.diff.concretedomain;

import com.b2international.index.Writer;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeWriter;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;

/**
 * @since 7.0
 */
public final class ConcreteDomainWriter extends OntologyChangeWriter<ConcreteDomainFragment> {

	public ConcreteDomainWriter(final String classificationId, final Writer writer) {
		super(classificationId, writer);
	}

	@Override
	public void indexChange(final String conceptId, final ConcreteDomainFragment fragment, final ChangeNature nature) {

		final ConcreteDomainChangeDocument.Builder builder = ConcreteDomainChangeDocument.builder()
			.nature(nature)
			.classificationId(classificationId)
			.memberId(fragment.getMemberId())
			.referencedComponentId(conceptId);
		
		switch (nature) {
			case NEW:
				builder.group(fragment.getGroup());
				builder.characteristicTypeId(Concepts.INFERRED_RELATIONSHIP);
				builder.released(Boolean.FALSE);
				break;
				
			case UPDATED:
				builder.serializedValue(fragment.getSerializedValue());
				builder.released(fragment.isReleased());
				break;
				
			case REDUNDANT:
				builder.released(fragment.isReleased());
				break;
				
			default:
				throw new IllegalStateException(String.format("Unexpected CD member change '%s' found with UUID '%s'.", 
						nature, 
						fragment.getMemberId()));
		}
		
		indexChange(builder.build());
	}
}
