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
package com.b2international.snowowl.snomed.reasoner.index;

import java.util.Map;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.BulkDelete;
import com.b2international.index.BulkUpdate;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.0
 */
public final class ClassificationRepository implements Index {

	private final Index index;

	public ClassificationRepository(final Index index) {
		this.index = index;
	}

	@Override
	public IndexAdmin admin() {
		return index.admin();
	}

	@Override
	public String name() {
		return index.name();
	}

	@Override
	public <T> T read(final IndexRead<T> read) {
		return index.read(read);
	}

	@Override
	public <T> T write(final IndexWrite<T> write) {
		return index.write(write);
	}

	public void save(final String key, final Object document) {
		write(writer -> {
			writer.put(key, document);
			writer.commit();
			return null;
		});
	}

	public void saveAll(final Map<String, Object> documentsByKey) {
		write(writer -> {
			writer.putAll(documentsByKey);
			writer.commit();
			return null;
		});		
	}

	public <T> void remove(final Class<T> clazz, final String key) {
		write(writer -> {
			writer.remove(clazz, key);
			writer.commit();
			return null;
		});
	}

	public void delete(final String classificationId) {
		write(writer -> {
			final ClassificationTaskDocument document = writer.searcher().get(ClassificationTaskDocument.class, classificationId);

			if (document == null) {
				throw new NotFoundException("Classification task", classificationId);
			}

			writer.remove(ClassificationTaskDocument.class, classificationId);
			writer.bulkDelete(new BulkDelete<>(EquivalentConceptSetDocument.class, EquivalentConceptSetDocument.Expressions.classificationId(classificationId)));
			writer.bulkDelete(new BulkDelete<>(ConcreteDomainChangeDocument.class, ConcreteDomainChangeDocument.Expressions.classificationId(classificationId)));
			writer.bulkDelete(new BulkDelete<>(RelationshipChangeDocument.class, RelationshipChangeDocument.Expressions.classificationId(classificationId)));

			writer.commit();
			return null;
		});
	}

	public void beginClassification(final String classificationId, final long headTimestamp) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.BEGIN_CLASSIFICATION, 
					ImmutableMap.of("status", ClassificationStatus.RUNNING,
							"headTimestamp", headTimestamp)));
			writer.commit();
			return null;
		});
	}

	public void endClassification(final String classificationId, final ClassificationStatus status) {
		index.write(writer -> {
			writer.bulkUpdate(new BulkUpdate<>(ClassificationTaskDocument.class, 
					ClassificationTaskDocument.Expressions.id(classificationId), 
					ClassificationTaskDocument.Fields.ID, 
					ClassificationTaskDocument.BEGIN_CLASSIFICATION, 
					ImmutableMap.of("status", status,
							"completionDate", System.currentTimeMillis())));
			writer.commit();
			return null;
		});
	}
}
