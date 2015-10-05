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
package com.b2international.snowowl.index.diff.tests.mock;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase.DocumentBuilder;

/**
 * Abstract superclass creating an indexed document representation from an incoming concept.
 * 
 * @since 4.3
 */
public class DiffConceptMappingStrategy extends AbstractIndexMappingStrategy {

	private final DiffConcept concept;
	private final boolean relevant;
	private final long storageKey;

	public DiffConceptMappingStrategy(final DiffConcept concept, final boolean relevant) {
		this.concept = checkNotNull(concept, "concept");
		this.relevant = relevant;

		// The concept identifier will also serve as the storage key for testing purposes
		this.storageKey = Long.parseLong(concept.getId());
	}

	@Override
	public Document createDocument() {
		final DocumentBuilder builder = new DocumentBuilderBase.DocumentBuilder()
				.id(concept.getId())
				.label(concept.getLabel())
				.storageKey(storageKey);

		if (relevant) {
			builder.compareUniqueKey(storageKey);
		} else {
			builder.compareUniqueKey(CDOUtils.NO_STORAGE_KEY).compareIgnoreUniqueKey(storageKey);
		}

		return builder.build();
	}

	@Override
	protected long getStorageKey() {
		return storageKey;
	}
}
