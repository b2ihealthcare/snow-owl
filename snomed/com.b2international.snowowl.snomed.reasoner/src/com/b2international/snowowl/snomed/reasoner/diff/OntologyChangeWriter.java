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
package com.b2international.snowowl.snomed.reasoner.diff;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import com.b2international.index.DocWriter;
import com.b2international.index.IndexException;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;

/**
 * @since 7.0
 * @param <T>
 */
public abstract class OntologyChangeWriter<T extends Serializable> extends OntologyChangeProcessor<T> {

	private static final int WRITES_PER_COMMIT = 10_000;

	protected final String classificationId;
	protected final DocWriter writer;
	private int writeOps;
	private boolean hasInferredChanges;

	public OntologyChangeWriter(final String classificationId, final DocWriter writer) {
		this.classificationId = classificationId;
		this.writer = writer;
	}

	@Override
	protected final void handleAddedSubject(final String conceptId, final T addedSubject) {
		hasInferredChanges = true;
		indexChange(conceptId, addedSubject, ChangeNature.INFERRED);
	}

	@Override
	protected final void handleRemovedSubject(final String conceptId, final T removedSubject) {
		indexChange(conceptId, removedSubject, ChangeNature.REDUNDANT);
	}

	public boolean hasInferredChanges() {
		return hasInferredChanges;
	}
	
	protected abstract void indexChange(final String conceptId, final T subject, final ChangeNature nature);

	protected void indexChange(final Object doc) {
		writer.put(UUID.randomUUID().toString(), doc);

		writeOps++;
		if (writeOps > WRITES_PER_COMMIT) {
			try {
				writer.commit();
			} catch (final IOException e) {
				throw new IndexException(String.format("Failed to index classification changes for ID '%s'.", classificationId), e);
			}
			writeOps = 0;
		}
	}
}
