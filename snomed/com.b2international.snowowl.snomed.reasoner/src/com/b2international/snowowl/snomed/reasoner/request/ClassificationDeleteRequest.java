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
package com.b2international.snowowl.snomed.reasoner.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.BulkDelete;
import com.b2international.index.Index;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationRepository;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;
import com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;

/**
 * @since 7.0
 */
final class ClassificationDeleteRequest implements Request<RepositoryContext, Boolean> {

	@NotEmpty
	private final String classificationId;

	ClassificationDeleteRequest(final String classificationId) {
		this.classificationId = classificationId;
	}

	@Override
	public Boolean execute(final RepositoryContext context) {
		final Index rawIndex = context.service(Index.class);
		final ClassificationRepository repository = new ClassificationRepository(rawIndex);

		return repository.write(writer -> {
			final ClassificationTaskDocument document = writer.searcher().get(ClassificationTaskDocument.class, classificationId);

			if (document == null) {
				throw new NotFoundException("Classification task", classificationId);
			}

			writer.remove(ClassificationTaskDocument.class, classificationId);
			writer.bulkDelete(new BulkDelete<>(EquivalentConceptSetDocument.class, EquivalentConceptSetDocument.Expressions.classificationId(classificationId)));
			writer.bulkDelete(new BulkDelete<>(ConcreteDomainChangeDocument.class, ConcreteDomainChangeDocument.Expressions.classificationId(classificationId)));
			writer.bulkDelete(new BulkDelete<>(RelationshipChangeDocument.class, RelationshipChangeDocument.Expressions.classificationId(classificationId)));

			writer.commit();
			return Boolean.TRUE;
		});
	}
}
